package org.narcotek.cputemp.systemui.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class for strings
 */
public final class StringUtils {

    private final static Pattern PRECISION_PATTERN = Pattern.compile("%(\\d*)T");

    /**
     * Looks for the pattern "%(\d*)T" in the given string and returns the first integer.
     *
     * @param s String to check
     * @return The number in the string or 0 if no number was found
     */
    public static int findPrecision(String s) {
        Matcher m = PRECISION_PATTERN.matcher(s);

        if (!m.find() || m.group(1).isEmpty()) {
            return 0;
        }

        return Integer.parseInt(m.group(1));
    }

}
