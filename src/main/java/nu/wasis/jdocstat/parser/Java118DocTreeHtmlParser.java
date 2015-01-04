package nu.wasis.jdocstat.parser;

import java.io.File;
import java.io.IOException;
import java.util.List;

import nu.wasis.jdocstat.cli.JDocStatConfig;
import nu.wasis.jdocstat.domain._Class;
import nu.wasis.jdocstat.domain._Method;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Java118DocTreeHtmlParser extends AbstractDocTreeHtmlParser implements JavaDocTreeHtmlParser {

    private static final Logger LOG = LogManager.getLogger();

    public Java118DocTreeHtmlParser(final JDocStatConfig config) {
        super(config);
        LOG.warn("Deprecation info not available for " + config.getJavaVersion() + ".");
    }

    @Override
    protected _Class parseSingleClassDocument(final File classHtml) throws IOException {
        final Document doc = Jsoup.parse(classHtml, UTF_8, "");
        final _Class _class = Java102DocTreeHtmlParser.createClass(classHtml);
        doc.select("a:not([href])").forEach(el -> {
            if (!el.attr("name").contains("(")) {
                return;
            }
            final String methodName = StringUtils.substringBefore(el.attr("name"), "(");
            LOG.debug("\t" + methodName);
            final List<String> argTypes = findArgTypes(el);
            _class.addMethod(new _Method(methodName, argTypes, false));
        });
        return _class;
    }

}
