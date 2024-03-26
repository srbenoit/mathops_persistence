package dev.mathops.persistence.api;

/**
 * An object returned by a "count" operation.
 *
 * @param resultCode the result code
 * @param numRows on a SUCCESS result code, the number of rows in the requested table
 * @param errorMessage on a non-SUCCESS result code, a diagnostic error message
 */
public record CountResponse(EResultCode resultCode, int numRows, String errorMessage) {}
