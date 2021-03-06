package nu.wasis.jdocstat.parser;

import java.io.File;
import java.io.IOException;
import java.util.List;

import nu.wasis.jdocstat.cli.JDocStatConfig;
import nu.wasis.jdocstat.domain._Class;
import nu.wasis.jdocstat.domain._Method;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Java12DocTreeHtmlParser extends AbstractDeprecationSupportingDocTreeHtmlParser implements JavaDocTreeHtmlParser {

    private static final Logger LOG = LogManager.getLogger();

    public Java12DocTreeHtmlParser(final JDocStatConfig config) {
        super(config);
    }

    @Override
    protected _Class parseSingleClassDocument(final File classHtml) throws IOException {
        final Document doc = Jsoup.parse(classHtml, UTF_8, "");
        final String className = FilenameUtils.removeExtension(classHtml.getName());
        final String packageName = StringUtils.substringBefore(doc.select("h2 + pre b").text(), "." + className);
        final _Class _class = new _Class(className, packageName, isClassDeprecated(packageName + "." + className));
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

}
