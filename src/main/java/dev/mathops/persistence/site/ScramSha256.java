package dev.mathops.persistence.site;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Code to process SCRAM-SHA-256 authentication.
 */
public enum ScramSha256 {
    ;

    /**
     * Processes a POST request to the "authorization" path, which handles SCRAM-SHA-256 authorization.
     *
     * <p>
     * This implements the SCRAM-SHA-256 protocol (without channel binding), documented in RFC5802 and RFC7677, and its
     * implementation over HTTP, documented in RFC7804.  The "Authentication-Info" header used to provide the client
     * with a session ID at the end of the process is documented in RFC7615.
     *
     * <p>
     * The client sends a "client-first-message": {@code n,n=[username],r=[client-nonce]}
     * <ul>
     * <li>{@code [client-nonce]} is a sequence of (at least 20) random ASCII letters or digits</li>
     * </ul>
     *
     * <p>
     * The server replies with a "server-first-message": {@code r=[client-nonce][server-nonce],s=[salt],i=[iterations]}
     * <ul>
     * <li>{@code [client-nonce]} is that sent by the client</li>
     * <li>{@code [server-nonce]} is a sequence of (at least 20) random ASCII letters or digits</li>
     * <li>{@code [salt]} is a base-64 representation of the salt associated with the given username</li>
     * <li>{@code [iterations]} is an iteration count (at least 4096)</li>
     * </ul>
     *
     * <p>
     * The client sends a "client-final-message": {@code c=,r=[client-nonce][server-nonce],p=[proof]}
     * <ul>
     * <li>{@code [client-nonce][server-nonce]} is that sent by the server</li>
     * <li>{@code [proof]} is the client's proof</li>
     * </ul>
     *
     * <p>
     * The server replies with a "server-final-message": {@code v=[verifier]}
     * <ul>
     * <li>{@code [verifier]} is the base-64 encoded server signature</li>
     * </ul>
     *
     * @param req  the HTTP servlet request
     * @param resp the HTTP servlet response
     * @throws IOException if there is an error reading the request body or writing the response
     */
    static void processPostAuthorization(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {

        final byte[] body = ServiceSite.getRequestBody(req);
        final String replyBody;

        if (body.length > 5) {
            if ((int) body[0] == (int) 'n') {
                // If the body starts with "n,,n=", this is a "client-first-message"
                if (((int) body[1] == (int) ',') && ((int) body[2] == (int) ',') && ((int) body[3] == (int) 'n')
                    && ((int) body[4] == (int) '=')) {

                    //TODO:
                } else {
                    replyBody = "e=other-error";
                }
            } else if (body[0] == 'c') {
                // If the body starts with "c=,r=", this is a "client-final-message"
                if (((int) body[1] == (int) '=') && ((int) body[2] == (int) ',') && ((int) body[3] == (int) 'r')
                    && ((int) body[4] == (int) '=')) {

                    //TODO:
                } else {
                    replyBody = "e=other-error";
                }
            } else {
                replyBody = "e=other-error";
            }
        } else {
            replyBody = "e=other-error";
        }

        ServiceSite.sendReply(req, resp, "text/plain", replyBody);
    }
}
