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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Java118DocTreeHtmlParser implements JavaDocTreeHtmlParser {

    private static final Logger LOG = LogManager.getLogger();

    private static final String UTF_8 = "UTF-8";

    private final JDocStatConfig config;

    public Java118DocTreeHtmlParser(final JDocStatConfig config) {
        this.config = config;
    }

    @Override
    public ApiDescriptor parseHtml() throws IOException {
        LOG.warn("Deprecation info not available for " + config.getJavaVersion() + ".");
        LOG.debug("Parsing " + config.getTreeFile());
        final Document doc = Jsoup.parse(config.getTreeFile(), UTF_8, "");
        final ApiDescriptor descriptor = new ApiDescriptor(config.getJavaVersion());
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
        doc.select("a:not([href])").forEach(a -> {
            if (!a.attr("name").contains("(")) {
                return;
            }
            final String methodName = StringUtils.substringBefore(a.attr("name"), "(");
            LOG.debug("\t" + methodName);
            final List<String> argTypes = findArgTypes(a);
            _class.addMethod(new _Method(methodName, argTypes, false));
        });
        return _class;
    }

    private List<String> findArgTypes(final Element a) {
        final String[] args = StringUtils.substringBetween(a.attr("name"), "(", ")").split(", ");
        final List<String> argTypes = Arrays.stream(args).map(s -> s.split(" ")[0]).collect(Collectors.toList());
        LOG.debug("\tArg types: " + argTypes);
        return argTypes;
    }

    private File getBaseDirectory() {
        return config.getTreeFile().getParentFile();
    }

    private File makeAbsolute(final String url) {
        return new File(getBaseDirectory(), url);
    }

}
