package dev.mathops.persistence;

/**
 *A utility class for testing whether a string is a valid "name" for use as a schema, table, or field identifier.
 */
public enum NameUtils {
    ;

    /** Valid characters to start a table name. */
    private static final String VALID_NAME_START = "abcdefghijklmnopqrstuvwxyz";

    /** Valid characters in table names. */
    private static final String VALID_NAME_CHARS = "abcdefghijklmnopqrstuvwxyz_0123456789";

    /**
     * Tests whether a name conforms to requirements.  Names must begin with a lowercase letter (a-z), and contain only
     * lowercase letters, decimal digits, or the underscore character.
     *
     * @param theName the name to test
     * @return {@code true} if the name is valid; {@code false} if not
     */
    static boolean isInvalidName(final CharSequence theName) {

        final int len = theName.length();
        boolean valid = len > 0;

        if (valid) {
            final char ch1 = theName.charAt(0);
            if (VALID_NAME_START.indexOf((int) ch1) == -1) {
                valid = false;
            }

            for (int i = 1; valid && i < len; ++i) {
                final char ch2 = theName.charAt(i);
                if (VALID_NAME_CHARS.indexOf((int) ch2) == -1) {
                    valid = false;
                }
            }
        }

        return !valid;
    }
}
