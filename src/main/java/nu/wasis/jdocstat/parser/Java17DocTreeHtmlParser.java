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

public class Java17DocTreeHtmlParser implements JavaDocTreeHtmlParser {

    private static final Logger LOG = LogManager.getLogger();

    private static final String UTF_8 = "UTF-8";

    private final JDocStatConfig config;

    private Document deprecatedDoc;

    public Java17DocTreeHtmlParser(final JDocStatConfig config) {
        this.config = config;
    }

    @Override
    public ApiDescriptor parseHtml() throws IOException {
        LOG.debug("Parsing " + config.getTreeFile());
        deprecatedDoc = readDeprecatedDoc(config.getTreeFile());
        final Document doc = Jsoup.parse(config.getTreeFile(), UTF_8, "");
        final ApiDescriptor descriptor = new ApiDescriptor(config.getJavaVersion());
        doc.select(".contentContainer li a").forEach(a -> {
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

    private Document readDeprecatedDoc(final File treeFile) throws IOException {
        final File deprecatedFile = new File(treeFile.getParentFile(), "deprecated-list.html");
        return Jsoup.parse(deprecatedFile, UTF_8, "");
    }

    private _Class parseSingleClassDocument(final File classHtml) throws IOException {
        final String className = FilenameUtils.removeExtension(classHtml.getName());
        final Document doc = Jsoup.parse(classHtml, UTF_8, "");
        final String _package = doc.select(".subTitle").text();
        final _Class _class = new _Class(className, _package, isClassDeprecated(_package + "." + className));
        doc.select("a:not([href])").forEach(a -> {
            if (!a.attr("name").contains("(")) {
                return;
            }
            final String methodName = StringUtils.substringBefore(a.attr("name"), "(");
            LOG.debug("\t" + methodName);
            final List<String> argTypes = findArgTypes(a);
            final boolean isMethodDeprecated = isMethodDeprecated(_class.getName(), methodName);
            _class.addMethod(new _Method(methodName, argTypes, isMethodDeprecated));
        });
        return _class;
    }

    private boolean isClassDeprecated(final String className) {
        return deprecatedDoc.select("td a").stream().anyMatch(a -> a.text().endsWith(className));
    }

    private boolean isMethodDeprecated(final String className, final String methodName) {
        return deprecatedDoc.select("td a").stream().anyMatch(a -> a.text().startsWith(className + "." + methodName + "("));
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
