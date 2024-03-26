package dev.mathops.persistence.api;

/**
 * An object returned by an "insert" operation.
 *
 * @param resultCode the result code
 * @param numRowsInserted on a SUCCESS result code, the number of rows that were inserted
 * @param errorMessage on a non-SUCCESS result code, a diagnostic error message
 */
public record InsertResponse(EResultCode resultCode, int numRowsInserted, String errorMessage) {}
