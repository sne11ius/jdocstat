package nu.wasis.jdocstat.parser;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import nu.wasis.jdocstat.cli.JDocStatConfig;
import nu.wasis.jdocstat.domain.ApiDescriptor;
import nu.wasis.jdocstat.domain._Class;
import nu.wasis.jdocstat.domain._Method;
import nu.wasis.jdocstat.util.MultiStringUtil;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Java102DocTreeHtmlParser extends AbstractDocTreeHtmlParser implements JavaDocTreeHtmlParser {

    private static final Logger LOG = LogManager.getLogger();

    private static final String UTF_8 = "UTF-8";

    public Java102DocTreeHtmlParser(final JDocStatConfig config) {
        super(config);
    }

    @Override
    public ApiDescriptor parseHtml() throws IOException {
        LOG.warn("Deprecation info not available for " + getConfig().getJavaVersion() + ".");
        LOG.debug("Parsing " + getConfig().getTreeFile());
        final Document doc = Jsoup.parse(getConfig().getTreeFile(), UTF_8, "");
        final ApiDescriptor descriptor = new ApiDescriptor(getConfig().getJavaVersion());
        doc.select("li a").forEach(a -> {
            String href = a.attr("href");
            href = StringUtils.removePattern(href, "#.*");
            LOG.debug(a.text() + " -> " + makeAbsolute(href));
            try {
                descriptor.addClass(parseSingleClassDocument(makeAbsolute(href)));
            } catch (final Exception e) {
                LOG.error("Cannot parse file " + makeAbsolute(href));
                throw new RuntimeException(e);
            }
        });
        return descriptor;
    }

    private _Class parseSingleClassDocument(final File classHtml) throws IOException {
        final Document doc = Jsoup.parse(classHtml, UTF_8, "");
        final String fullName = FilenameUtils.removeExtension(classHtml.getName());
        final String _package = StringUtils.substringBeforeLast(fullName, ".");
        final String className = StringUtils.substringAfterLast(fullName, ".");
        final _Class _class = new _Class(className, _package, false);
        doc.select("h3 a:not([href]), a code").forEach(h3 -> {
            if (h3.html().contains("<dl>")) {
                return;
            }
            if (h3.html().contains("throws")) {
                return;
            }
            if (!h3.html().contains("(")) {
                return;
            }
            String methodName = "";
            final String text = h3.text();
            if (MultiStringUtil.containsAny(text, "public", "protected", "private")) {
                methodName = StringUtils.substringBetween(text, " ", "(");
            } else {
                methodName = StringUtils.substringBetween(text, "", "(");
            }
            if (methodName.equals(methodName.toUpperCase())) {
                return;
            }
            if (methodName.contains(" ")) {
                final String[] methodNameParts = methodName.split(" ");
                methodName = methodNameParts[methodNameParts.length - 1];
            }
            LOG.debug("\t" + methodName);
            final List<String> argTypes = findArgTypes(h3);
            _class.addMethod(new _Method(methodName, argTypes, false));
        });
        return _class;
    }

    private List<String> findArgTypes(final Element h3) {
        String signature = h3.text().contains("(") ? h3.text() : h3.parent().nextElementSibling().text();
        LOG.debug("\tSignature: " + signature);
        while (signature.endsWith(",") || signature.endsWith("(")) {
            signature += " " + h3.parent().nextElementSibling().text();
        }
        final String[] args = StringUtils.substringBetween(signature, "(", ")").split(", ");
        final List<String> argTypes = Arrays.stream(args).map(s -> s.split(" ")[0]).collect(Collectors.toList());
        LOG.debug("\tArg types: " + argTypes);
        return argTypes;
    }
}
