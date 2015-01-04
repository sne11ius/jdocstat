package nu.wasis.jdocstat.util;

public class MultiStringUtil {

    public static boolean containsAny(final String str, final String... searchStrings) {
        for (final String string : searchStrings) {
            if (str.contains(string)) {
                return true;
            }
        }
        return false;
    }

}
