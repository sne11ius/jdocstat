package nu.wasis.jdocstat.parser;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import nu.wasis.jdocstat.cli.JDocStatConfig;
import nu.wasis.jdocstat.domain.ApiDescriptor;
import nu.wasis.jdocstat.domain._Class;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

abstract class AbstractDocTreeHtmlParser implements JavaDocTreeHtmlParser {

    private static final Logger LOG = LogManager.getLogger();

    protected static final String UTF_8 = "UTF-8";

    private final JDocStatConfig config;

    protected AbstractDocTreeHtmlParser(final JDocStatConfig config) {
        this.config = config;
    }

    @Override
    public ApiDescriptor parseHtml() throws IOException {
        beforeParse();
        LOG.debug("Parsing " + getConfig().getTreeFile());
        final Document treeDoc = Jsoup.parse(getConfig().getTreeFile(), UTF_8, "");
        final ApiDescriptor descriptor = new ApiDescriptor(getConfig().getJavaVersion());
        treeDoc.select(getTreeDocClassSelector()).forEach(el -> {
            final File classHtmlFile = extractClassHtmlFile(el);
            try {
                descriptor.addClass(parseSingleClassDocument(classHtmlFile));
            } catch (final IOException e) {
                LOG.error("Unable to parse file `" + classHtmlFile + "'.", e);
            }
        });
        return descriptor;
    }

    protected String getTreeDocClassSelector() {
        return "li a";
    }

    protected File extractClassHtmlFile(final Element el) {
        String href = el.attr("href");
        href = StringUtils.removePattern(href, "#.*");
        return makeAbsolute(href);
    }

    protected abstract _Class parseSingleClassDocument(File classHtmlFile) throws IOException;

    protected void beforeParse() {
        // This intentionally blank.
    };

    protected List<String> findArgTypes(final Element el) {
        final String[] args = StringUtils.substringBetween(el.attr("name"), "(", ")").split(", ");
        final List<String> argTypes = Arrays.stream(args).map(s -> s.split(" ")[0]).collect(Collectors.toList());
        LOG.debug("\tArg types: " + argTypes);
        return argTypes;
    }

    protected File getBaseDirectory() {
        return config.getTreeFile().getParentFile();
    }

    protected File makeAbsolute(final String url) {
        return new File(getBaseDirectory(), url);
    }

    protected JDocStatConfig getConfig() {
        return config;
    }

}
