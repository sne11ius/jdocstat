package nu.wasis.jdocstat.parser;

import java.io.IOException;

import nu.wasis.jdocstat.domain.ApiDescriptor;

public interface JavaDocTreeHtmlParser {

    ApiDescriptor parseHtml() throws IOException;

}
