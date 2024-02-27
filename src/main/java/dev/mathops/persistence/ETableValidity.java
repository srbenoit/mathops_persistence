package dev.mathops.persistence;

/**
 * Validities that a table can report.
 */
public enum ETableValidity {

    /** Valid, no changes were needed. */
    VALID,

    /** Valid after changes were made. */
    VALID_AFTER_CHANGES,

    /** Present but not valid. */
    PRESENT_AND_INVALID,

    /** Not present. */
    NOT_PRESENT,
}
