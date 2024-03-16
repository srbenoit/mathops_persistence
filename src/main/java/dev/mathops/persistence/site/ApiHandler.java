package dev.mathops.persistence.site;

import dev.mathops.persistence.site.session.SessionManager;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.io.File;

/**
 * A handler for API requests.
 */
public final class ApiHandler implements HttpHandler {

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
     * @param exchange the exchange
     */
    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {

        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
        } else {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("Hello API: " + exchange.getRequestPath());
        }
    }
}
