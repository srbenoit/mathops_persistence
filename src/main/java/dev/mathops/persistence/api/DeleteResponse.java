package dev.mathops.persistence.api;

/**
 * An object returned by a "delete" operation.
 *
 * @param resultCode the result code
 * @param numRowsDeleted on a SUCCESS result code, the number of rows that were deleted
 * @param errorMessage on a non-SUCCESS result code, a diagnostic error message
 */
public record DeleteResponse(EResultCode resultCode, int numRowsDeleted, String errorMessage) {}
