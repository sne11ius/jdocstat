package nu.wasis.jdocstat.parser;

import java.io.File;
import java.io.IOException;

import nu.wasis.jdocstat.cli.JDocStatConfig;
import nu.wasis.jdocstat.domain.ApiDescriptor;
import nu.wasis.jdocstat.domain._Class;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

abstract class AbstractDocTreeHtmlParser implements JavaDocTreeHtmlParser {

    private static final Logger LOG = LogManager.getLogger();

    private static final String UTF_8 = "UTF-8";

    private final JDocStatConfig config;

    protected AbstractDocTreeHtmlParser(final JDocStatConfig config) {
        this.config = config;
    }

    @Override
    public ApiDescriptor parseHtml() throws IOException {
        final Document doc = Jsoup.parse(getConfig().getTreeFile(), UTF_8, "");
        final ApiDescriptor descriptor = new ApiDescriptor(getConfig().getJavaVersion());
        doc.select(getTreeDocClassSelector()).forEach(el -> {
            final File classHtmlFile = extractClassHtmlFilename(el);
            descriptor.addClass(parseSingleClassDocument(classHtmlFile));
        });
        return descriptor;
    }

    protected abstract _Class parseSingleClassDocument(File classHtmlFile);

    protected abstract File extractClassHtmlFilename(Element el);

    protected abstract String getTreeDocClassSelector();

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
