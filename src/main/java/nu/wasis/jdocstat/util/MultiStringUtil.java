package nu.wasis.jdocstat.util;

import java.util.Arrays;

public class MultiStringUtil {

    public static boolean containsAny(final String str, final String... searchStrings) {
        return Arrays.stream(searchStrings).anyMatch(s -> str.contains(s));
    }

}
