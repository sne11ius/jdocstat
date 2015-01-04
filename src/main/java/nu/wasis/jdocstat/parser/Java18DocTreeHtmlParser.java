package nu.wasis.jdocstat.parser;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import nu.wasis.jdocstat.cli.JDocStatConfig;
import nu.wasis.jdocstat.domain._Class;
import nu.wasis.jdocstat.domain._Method;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Java18DocTreeHtmlParser extends Java17DocTreeHtmlParser implements JavaDocTreeHtmlParser {

    private static final Logger LOG = LogManager.getLogger();

    public Java18DocTreeHtmlParser(final JDocStatConfig config) {
        super(config);
    }

    @Override
    protected _Class parseSingleClassDocument(final File classHtml) throws IOException {
        final Document doc = Jsoup.parse(classHtml, UTF_8, "");
        final String _package = doc.select(".subTitle").text();
        final String className = FilenameUtils.removeExtension(classHtml.getName());
        final _Class _class = new _Class(className, _package, isClassDeprecated(_package + "." + className));
        doc.select("td.colLast").forEach(td -> {
            if (!td.text().contains("(")) {
                return;
            }
            final String methodName = td.select(".memberNameLink").text();
            if (methodName.toUpperCase().equals(methodName)) {
                return;
            }
            LOG.debug("\t" + methodName);
            final List<String> argTypes = findArgTypes(td);
            final boolean isMethodDeprecated = isMethodDeprecated(_class.getName(), methodName);
            _class.addMethod(new _Method(methodName, argTypes, isMethodDeprecated));
        });
        return _class;
    }

    @Override
    protected List<String> findArgTypes(final Element el) {
        final String[] args = StringUtils.substringBetween(el.text(), "(", ")").split(", ");
        final List<String> argTypes = Arrays.stream(args).map(s -> s.split(" ")[0]).collect(Collectors.toList());
        LOG.debug("\tArg types: " + argTypes);
        return argTypes;
    }

}
