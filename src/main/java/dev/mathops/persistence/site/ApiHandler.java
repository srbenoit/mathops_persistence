package dev.mathops.persistence.site;

import dev.mathops.commons.log.Log;
import dev.mathops.persistence.site.session.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;

/**
 * A handler for API requests.  Every request must provide a valid session ID that resulted from a SCRAM-SHA-256
 * authentication process, or it will fail with an HTTP "Unauthorized" (401) response.  These session IDs are
 * short-lived and are associated with a particular client address.
 */
final class ApiHandler {

    /** The number of characters of URI path used to select this handler. */
    private final int prefixLength;

    /** The directory in which to find configuration files. */
    private final File configDir;

    /** The session manager. */
    private final SessionManager sessionMgr;

    /**
     * Constructs a new {@code ApiHandler}.
     *
     * @param thePrefixLength the number of characters of URI path used to select this handler
     * @param theConfigDir    the directory in which to find configuration files
     * @param theSessionMgr   the session manager
     */
    ApiHandler(final int thePrefixLength, final File theConfigDir, final SessionManager theSessionMgr) {

        this.prefixLength = thePrefixLength;
        this.configDir = theConfigDir;
        this.sessionMgr = theSessionMgr;
    }

    /**
     * Handles an HTTP exchange.
     *
     * @param reqPath the request path (including the prefix that selected this handler)
     * @param req     the HTTP servlet request
     * @param resp    the HTTP servlet response
     * @throws IOException if there is an error reading the request body or writing the response
     */
    void handleRequest(final String reqPath, final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {

        final String method = req.getMethod();

        if ("GET".equalsIgnoreCase(method)) {
            final String path = reqPath.substring(this.prefixLength);

            Log.info("GET Path is: ", path);

            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else if ("POST".equalsIgnoreCase(method)) {
            final String path = reqPath.substring(this.prefixLength);

            Log.info("POST Path is: ", path);

            if ("authorization".equals(path)) {
                ScramSha256.processPostAuthorization(req, resp);
            }

            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }
}
