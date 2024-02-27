package dev.mathops.persistence;

/**
 * An immutable container for the validation status of a table (or similar storage structure).
 */
public final class TableValidationStatus {

    /** The validity of the table. */
    public final ETableValidity validity;

    /**
     * Constructs a new {@code TableValidationStatus}.
     *
     * @param theValidity the validity of the table
     */
    public TableValidationStatus(final ETableValidity theValidity) {

        this.validity = theValidity;
    }
}
