/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.pal.util;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contains several static helper methods that can be used to perform string manipulation algorithms.
 * 
 * @since Dec 16, 2013
 * @version $Id$
 */

public final class StringHelper {

    /**
     * The constant character string of a DASH
     */
    @SuppressWarnings("nls")
    public static final String DASH = "-";

    /**
     * The constant character string of a period (DOT)
     */
    @SuppressWarnings("nls")
    public static final String DOT = ".";

    /**
     * The constant character string of an ELIPSE (...)
     */
    @SuppressWarnings("nls")
    static final String ELLIPSES = "...";

    /**
     * The constant character string of a line feed control character
     */
    @SuppressWarnings("nls")
    public static final String LINE_FEED = "\n";

    /**
     * The constant character string of a forward slash character
     */
    @SuppressWarnings("nls")
    public static final String SLASH = "/";

    /**
     * The constant character string of a comma
     */
    @SuppressWarnings("nls")
    public static final String COMMA = ",";

    /**
     * Converts the specified string pattern to a regular expression string. If the supplied string is null or empty,
     * then a regular expression that matches all strings (.*) is returned.
     * <p>
     * The expression passed to this method should not already be a regular expression. If it contains problematic
     * meta-characters for this routine (such as period, asterisk, and plus), they will be escaped and matched literally
     * in the resulting regular expression returned.
     * </p>
     * 
     * @param value
     *            The pattern that we need to convert to a regular expression
     * @return The regular expression that is equivalent to the pattern
     */
    @SuppressWarnings("nls")
    public static String convertToRegex(String value) {
        if (value == null || value.trim().length() == 0) {
            return ".*";
        }
        boolean appendEOL = false;
        StringBuffer buffer = new StringBuffer(value.trim());

        /*
         * If there are any period characters, we need to escape them so that they are exactly matched
         */
        Pattern pattern = Pattern.compile("\\.");
        Matcher matcher = pattern.matcher(buffer);
        int position = 0;
        while (matcher.find(position)) {
            buffer.replace(matcher.start(), matcher.end(), "\\.");
            position = matcher.end() + 1;
        }

        /*
         * If there are any asterisks or pluses, which we need to interpret as wildcard characters, we need to convert
         * them into .* or .
         */
        pattern = Pattern.compile("\\*|\\+");
        matcher = pattern.matcher(buffer);
        position = 0;
        while (matcher.find(position)) {
            String metachar = buffer.substring(matcher.start(), matcher.end());
            if (metachar.equals("*")) {
                buffer.replace(matcher.start(), matcher.end(), ".*");
                position = matcher.end() + 1;
                if (matcher.end() < buffer.length() - 1) {
                    appendEOL = true;
                }
            } else if (metachar.equals("+")) {
                buffer.replace(matcher.start(), matcher.end(), ".");
                position = matcher.end();
                if (matcher.end() == buffer.length()) {
                    appendEOL = true;
                }
            }
        }

        /*
         * If the string contains a .* meta-character sequence anywhere in the middle of the string (i.e., there are
         * other characters following the .* meta-characters), OR the string ends with the .+ sequence, then we need to
         * append the "end-of-line" boundary condition to the end of the string to get predictable results.
         */
        if (appendEOL) {
            buffer.append("$");
        }
        return buffer.toString();
    }

    /**
     * Takes a string that may possibly be very long and return a string that is at most maxLength. If the string is
     * longer than maxLength, the last three characters will be the ellipses (...) to indicate that the string was
     * shortened.
     * 
     * @param possiblyLongString
     *            The source string to be trimmed
     * @param maxLength
     *            must be at least 4 (one character plus ellipses). It is the maximum length of the string desired.
     * @return possibly shortened string
     */
    public static String getShortenedString(String possiblyLongString, int maxLength) {
        if ((possiblyLongString != null) && (maxLength > ELLIPSES.length())
            && (possiblyLongString.length() > maxLength)) {
            return possiblyLongString.substring(0, maxLength - ELLIPSES.length()) + ELLIPSES;

        }
        return possiblyLongString;
    }

    /**
     * Determines that a provided string is not null and not empty (length = 0 after trimming)
     * 
     * @param theString
     *            The string to be tested
     * @return true if the string IS NOT null and is NOT empty
     */
    public static boolean isNotNullNotEmpty(String theString) {
        return ((theString != null) && (!theString.trim().isEmpty()));
    }

    /**
     * Determines that a provided string IS null or an empty string (length = 0 after trimming)
     * 
     * @param theString
     *            The string to be tested
     * @return true if the string IS null OR is empty
     */
    public static boolean isNullOrEmpty(String theString) {
        return ((theString == null) || (theString.trim().isEmpty()));
    }

    /**
     * Returns an indication if the first string is equal to the second string, allowing for either or both strings to
     * be null.
     * 
     * @param a
     *            The first string to be compared
     * @param b
     *            The second string to be compared
     * @return True if both strings are null, or both strings are non-null AND they are equal. False otherwise.
     */
    public static boolean equals(String a, String b) {
        return equals(a, b, false);
    }

    /**
     * Returns an indication if the first string is equal to the second string, allowing for either or both strings to
     * be null, and ignoring case.
     * 
     * @param a
     *            The first string to be compared
     * @param b
     *            The second string to be compared
     * @return True if both strings are null, or both strings are non-null AND they are equal (without regard to case).
     *         False otherwise.
     */
    public static boolean equalsIgnoreCase(String a, String b) {
        return equals(a, b, true);
    }

    /**
     * Compares two strings (allowing either or both to be null), and allowing for optional case sensitive or
     * insensitive comparison.
     * 
     * @param a
     *            The first string to be compared
     * @param b
     *            The second string to be compared
     * @param caseInsensitive
     *            True if the comparison is to be case in-sensitive.
     * @return True if both strings are null, or both strings are non-null and they are equal
     */
    private static boolean equals(String a, String b, boolean caseInsensitive) {
        if (a == null && b == null) {
            return true;
        }
        if (a != null && b != null) {
            if (caseInsensitive) {
                return a.equalsIgnoreCase(b);
            }
            return a.equals(b);
        }

        return false;
    }

    /**
     * This method is used to mangle a name.
     * <p>
     * This method will first remove all unacceptable characters from the name and translate all characters to lower
     * case. This is done to eliminate any potentially troublesome characters. If the resulting string is empty, then a
     * random string of characters for the minimum desired length is returned. If the string is too short to meet the
     * minimum length requirement, it is padded with random characters.
     * </p>
     * <p>
     * Once the string has been scrubbed and possibly padded, it may be truncated (if longer than the maximum value) and
     * the result is returned. To make the string as unique as possible, the algorithm removes excess letters from the
     * center of the string, concatenating the first nad last parts of the name together. The assumption is that users
     * tend to start the names of multiple things in similar ways, and get more descriptive as the name progresses. If
     * for example, several objects were named "A test Object", "A test Object1", and "A test Object2", shortening the
     * name only from the left does not generate a unique name.
     * </p>
     * 
     * @param name
     *            The name to be mangled
     * @param minLen
     *            minimum number of characters for the name
     * @param maxLen
     *            maximum number of characters for the name
     * @return The mangled name, or an empty string if the value is null or an empty string.
     */
    @SuppressWarnings("nls")
    public static String mangleName(String name, int minLen, int maxLen) {
        StringBuffer buffer = new StringBuffer(name == null ? "" : name);
        Pattern pattern = Pattern.compile("[^a-z0-9]+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(buffer);
        int position = 0;
        while (matcher.find(position)) {
            buffer.delete(matcher.start(), matcher.end());
            position = matcher.start();
        }

        if (buffer.length() < minLen) {
            for (int i = buffer.length(); i <= minLen; i++) {
                buffer.append("A");
            }
        }

        /*
         * Remove out of the center of the name to preserve start and end and result in a string of max len
         */
        if (buffer.length() > maxLen) {
            int excess = buffer.length() - maxLen;
            int left = maxLen / 2;

            buffer.delete(left, excess + left);
        }

        return buffer.toString().toLowerCase();
    }

    /**
     * This method is used to normalize a string value.
     * <p>
     * This method will ensure that the string value is trimmed of all leading and trailing whitespace if not null. If
     * it is null or an empty string, then it will return null.
     * </p>
     * 
     * @param value
     *            The value to be normalized
     * @return The normalized (no leading or trailing whitespace) value, or null if the string was null or an empty
     *         string (or all whitespace). This method will never return an empty string.
     */
    public static String normalizeString(String value) {
        if (value != null) {
            String temp = value.trim();
            if (temp.length() > 0) {
                return temp;
            }
        }
        return null;
    }

    /**
     * This method is used to strip all carriage returns and line feed characters from a string
     * 
     * @param value
     *            The source string to remove CR+LF, LF+CR, CR, or LF control sequences from
     * @return The original value less all carriage returns and line feeds
     */
    @SuppressWarnings("nls")
    public static String stripCRLF(String value) {

        if (value == null) {
            return null;
        }
        String[] tokens = value.split("\r\n|\n\r|\r|\n");
        StringBuffer buffer = new StringBuffer();
        for (String token : tokens) {
            buffer.append(token.trim());
        }
        return buffer.toString();
    }

    /**
     * Converts UNIX-style line endings to DOS-style. Replaces LF with CR+LF as long as the LF does not already exist
     * paired with a CR.
     * 
     * @param content
     *            The content to be converted
     * @return The converted content.
     */
    @SuppressWarnings("nls")
    public static String toDOSLines(String content) {
        if (content == null) {
            return null;
        }

        StringBuffer buffer = new StringBuffer(content);
        Pattern pattern = Pattern.compile("^(\n)[^\r]|[^\r](\n)[^\r]|[^\r](\n)$");
        Matcher matcher = pattern.matcher(buffer);
        int position = 0;
        while (matcher.find(position)) {
            int index = matcher.start(1);
            if (index == -1) {
                index = matcher.start(2);
            }
            if (index == -1) {
                index = matcher.start(3);
            }

            buffer.replace(index, index + 1, "\r\n");
            position = index + 1;
        }

        return buffer.toString();
    }

    /**
     * This method will convert a string contents to use the UNIX-style line endings. That is, all occurrences of CR
     * (Carriage Return) and LF (Line Feed) are reduced to just use LF.
     * 
     * @param content
     *            The buffer to be processed
     * @return The converted contents
     */
    @SuppressWarnings("nls")
    public static String toUnixLines(String content) {
        if (content == null) {
            return null;
        }

        StringBuffer buffer = new StringBuffer(content);
        Pattern pattern = Pattern.compile("\r\n|\n\r");
        Matcher matcher = pattern.matcher(buffer);
        int position = 0;
        while (matcher.find(position)) {
            buffer.replace(matcher.start(), matcher.end(), "\n");
            position = matcher.start();
        }

        return buffer.toString();
    }

    /**
     * This method is used to translate characters in the input sequence that match the characters in the match list to
     * the corresponding character in the replacement list. If the replacement list is shorter than the match list, then
     * the character from the replacement list is taken as the modulo of the match character position and the length of
     * the replacement list.
     * 
     * @param sequence
     *            The input sequence to be processed
     * @param match
     *            The list of matching characters to be searched
     * @param replacement
     *            The list of replacement characters, positional coincident with the match list. If shorter than the
     *            match list, then the position "wraps" around on the replacement list.
     * @return The translated string contents.
     */
    public static Object translate(String sequence, String match, String replacement) {

        if (sequence == null) {
            return sequence;
        }

        StringBuffer buffer = new StringBuffer(sequence);

        for (int index = 0; index < buffer.length(); index++) {
            char ch = buffer.charAt(index);

            int position = match.indexOf(ch);
            if (position == -1) {
                continue;
            }

            if (position >= replacement.length()) {
                position %= replacement.length();
            }
            buffer.setCharAt(index, replacement.charAt(position));
        }

        return buffer.toString();
    }

    /**
     * Ensures that the name provided is a valid identifier. This means that no spaces are allowed as well as special
     * characters. This method translates all spaces and illegal characters to underscores (_).
     * 
     * @param name
     *            The name to be checked and converted to an identifier if needed
     * @return The valid identifier from the name
     */
    public static String validIdentifier(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }

        StringBuffer buffer = new StringBuffer(name);
        for (int index = 0; index < buffer.length(); index++) {
            char ch = buffer.charAt(index);

            if ((index == 0 && !Character.isJavaIdentifierStart(ch)) || (!Character.isJavaIdentifierPart(ch))) {
                buffer.setCharAt(index, '_');
            }
        }
        return buffer.toString();
    }

    /**
     * This method verifies that the provided string only contains characters from the legal set, and replaces any
     * character not in the legal set with the specified replacement character.
     * 
     * @param sequence
     *            The sequence to be verified
     * @param legal
     *            The set of all legal characters
     * @param replacement
     *            The replacement character if a character is not in the legal set
     * @return The verified *and possibly updated) string
     */
    public static String verify(String sequence, String legal, char replacement) {
        if (sequence == null) {
            return sequence;
        }

        StringBuffer buffer = new StringBuffer(sequence);
        for (int index = 0; index < buffer.length(); index++) {
            char ch = buffer.charAt(index);
            if (legal.indexOf(ch) == -1) {
                buffer.setCharAt(index, replacement);
            }
        }
        return buffer.toString();
    }

    /**
     * Private constructor to prevent instantiation of this class - All methods are static!
     */
    private StringHelper() {

    }

    /**
     * @param list
     *            The list of elements
     * @return The list of elements formatted as a comma-delimited list
     */
    @SuppressWarnings("nls")
    public static String asList(List<String> list) {
        StringBuffer buffer = new StringBuffer();
        if (list != null) {
            if (list.size() == 1) {
                buffer.append(list.get(0));
            } else {
                for (String element : list) {
                    buffer.append(element);
                    buffer.append(", ");
                }

                if (buffer.length() > 2) {
                    buffer.delete(buffer.length() - 2, buffer.length());
                }
            }
        }
        return buffer.toString();
    }

    /**
     * @param map
     *            A map of strings
     * @return A map expressed as a comma-delimited list of name=value tuples
     */
    @SuppressWarnings("nls")
    public static String asList(Map<String, String> map) {
        StringBuffer buffer = new StringBuffer();
        if (map != null) {
            Set<String> keys = map.keySet();
            for (String key : keys) {
                buffer.append(String.format("%s=%s, ", key, map.get(key)));
            }

            if (buffer.length() > 2) {
                buffer.delete(buffer.length() - 2, buffer.length());
            }
        }
        return buffer.toString();
    }

    /**
     * This method attempts to identify the data type being passed to it and convert that data to an appropriate wrapper
     * object (Integer, Double, Boolean, Date, or String).
     * 
     * @param input
     *            The input to be recognized and converted
     * @return The output wrapper object that contains the value
     */
    @SuppressWarnings("nls")
    public static Object resolveToType(String input) {
        String intRegex = "^(\\-)?[0-9]+$";
        String doubleRegex = "^(\\-)?[0-9\\.]+$";
        String boolRegex = "(^(?i)((true)|(false))$)";

        // Check for null
        if (input == null) {
            return null;
        }

        // Check int first
        if (input.matches(intRegex)) {
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException nfe) {
                // Should not happen
                return null;
            }
        }

        // Check double (int + decimal point)
        if (input.matches(doubleRegex)) {
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        // Check boolean
        if (input.matches(boolRegex)) {
            return Boolean.parseBoolean(input);
        }

        // Try to parse a date
        Date date = Time.utcParse(input);
        if (date != null) {
            return date;
        }

        // No special type, return string
        return input;
    }

    /**
     * This method takes a variable argument list of string values and returns a single string with the values expressed
     * as a comma-delimited list.
     * 
     * @param values
     *            The values to be converted into a comma-separated list
     * @return The comma-separated list, or an empty string if there are no values
     */
    public static String asList(String... values) {
        return asList(Arrays.asList(values));
    }
}
