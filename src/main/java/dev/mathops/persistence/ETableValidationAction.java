package dev.mathops.persistence;

/**
 * Enumeration of table validation actions.
 */
public enum ETableValidationAction {

    /** Checks the status of tables or structures and reports results without attempting to make changes. */
    CHECK_ONLY,

    /**
     * Attempts to build any missing tables or structures, but does not try to change existing structures if they are
     * present but not valid.
     */
    BUILD_IF_MISSING,

    /**
     * Attempts to build any missing tables or structures, or to repair/correct any structures that are present but not
     * correct.  This could potentially, in the event of an error, delete existing data, so it should be done with
     * care.
     */
    BUILD_OR_REPAIR_IF_INVALID;
}
