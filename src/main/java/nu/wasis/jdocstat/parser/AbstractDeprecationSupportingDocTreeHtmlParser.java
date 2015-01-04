package nu.wasis.jdocstat.parser;

import java.io.File;
import java.io.IOException;

import nu.wasis.jdocstat.cli.JDocStatConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public abstract class AbstractDeprecationSupportingDocTreeHtmlParser extends AbstractDocTreeHtmlParser implements JavaDocTreeHtmlParser {

    private static final Logger LOG = LogManager.getLogger();

    public AbstractDeprecationSupportingDocTreeHtmlParser(final JDocStatConfig config) {
        super(config);
    }

    private Document deprecatedDoc;

    @Override
    protected void beforeParse() {
        try {
            deprecatedDoc = readDeprecatedDoc(getConfig().getTreeFile());
        } catch (final IOException e) {
            LOG.error(e);
            throw new RuntimeException(e);
        }
    };

    private Document readDeprecatedDoc(final File treeFile) throws IOException {
        final File deprecatedFile = new File(treeFile.getParentFile(), "deprecated-list.html");
        return Jsoup.parse(deprecatedFile, UTF_8, "");
    }

    protected boolean isClassDeprecated(final String className) {
        return deprecatedDoc.select("td a").stream().anyMatch(a -> a.text().endsWith(className));
    }

    protected boolean isMethodDeprecated(final String className, final String methodName) {
        return deprecatedDoc.select("td a").stream().anyMatch(a -> a.text().startsWith(className + "." + methodName + "("));
    }

}
