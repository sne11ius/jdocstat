package nu.wasis.jdocstat.parser;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import nu.wasis.jdocstat.cli.JDocStatConfig;
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

    public Java102DocTreeHtmlParser(final JDocStatConfig config) {
        super(config);
        LOG.warn("Deprecation info not available for " + getConfig().getJavaVersion() + ".");
    }

    @Override
    protected _Class parseSingleClassDocument(final File classHtml) throws IOException {
        final Document doc = Jsoup.parse(classHtml, UTF_8, "");
        final String fullName = FilenameUtils.removeExtension(classHtml.getName());
        final String _package = StringUtils.substringBeforeLast(fullName, ".");
        final String className = StringUtils.substringAfterLast(fullName, ".");
        final _Class _class = new _Class(className, _package, false);
        doc.select("h3 a:not([href]), a code").forEach(el -> {
            if (el.html().contains("<dl>")) {
                return;
            }
            if (el.html().contains("throws")) {
                return;
            }
            if (!el.html().contains("(")) {
                return;
            }
            String methodName = "";
            final String text = el.text();
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
            final List<String> argTypes = findArgTypes(el);
            _class.addMethod(new _Method(methodName, argTypes, false));
        });
        return _class;
    }

    @Override
    protected List<String> findArgTypes(final Element el) {
        String signature = el.text().contains("(") ? el.text() : el.parent().nextElementSibling().text();
        LOG.debug("\tSignature: " + signature);
        while (signature.endsWith(",") || signature.endsWith("(")) {
            signature += " " + el.parent().nextElementSibling().text();
        }
        final String[] args = StringUtils.substringBetween(signature, "(", ")").split(", ");
        final List<String> argTypes = Arrays.stream(args).map(s -> s.split(" ")[0]).collect(Collectors.toList());
        LOG.debug("\tArg types: " + argTypes);
        return argTypes;
    }

}
