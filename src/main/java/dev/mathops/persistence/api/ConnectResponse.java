package dev.mathops.persistence.api;

/**
 * An object returned by a "connect" operation.  If connection is successful, the authorization token is stored in
 * the client, but is not accessible to the caller.
 *
 * @param resultCode the result code
 * @param errorMessage on a non-SUCCESS result code, a diagnostic error message
 */
public record ConnectResponse(EResultCode resultCode, String errorMessage) {}
