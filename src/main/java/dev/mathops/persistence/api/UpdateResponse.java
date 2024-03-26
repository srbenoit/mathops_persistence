package dev.mathops.persistence.api;

/**
 * An object returned by an "update" operation.
 *
 * @param resultCode the result code
 * @param numRowsUpdated on a SUCCESS result code, the number of rows that were updated
 * @param errorMessage on a non-SUCCESS result code, a diagnostic error message
 */
public record UpdateResponse(EResultCode resultCode, int numRowsUpdated, String errorMessage) {}
