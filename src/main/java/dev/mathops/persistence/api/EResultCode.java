package dev.mathops.persistence.api;

import dev.mathops.commons.builder.SimpleBuilder;

/**
 * Possible result codes from database API operations.
 */
public enum EResultCode {

    /** Success. */
    SUCCESS(0),

    /** Generic failure (this should be replaced by more useful codes). */
    FAILURE(-1);

    /** The code. */
    public final int code;

    /**
     * Constructs a new {@code EResultCode}.
     *
     * @param theCode the code
     */
    EResultCode(final int theCode) {

        this.code = theCode;
    }

    /**
     * Generates a diagnostic string representation of the object.
     *
     * @return the string representation
     */
    @Override
    public String toString() {

        final String codeString = Integer.toString(this.code);

        return SimpleBuilder.concat("EResultCode{code=", codeString,"}");
    }
}
