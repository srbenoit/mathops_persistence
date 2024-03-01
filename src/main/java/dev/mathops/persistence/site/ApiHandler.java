package dev.mathops.persistence.site;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A handler for API requests.
 */
public final class ApiHandler implements HttpHandler {

    /** The number of characters of URI path used to select this handler. */
    private final int prefixLength;

    /** The directory in which to find configuration files. */
    private final File configDir;

    /**
     * Constructs a new {@code ApiHandler}.
     *
     * @param thePrefixLength the number of characters of URI path used to select this handler
     * @param theConfigDir the directory in which to find configuration files
     */
    ApiHandler(final int thePrefixLength, final File theConfigDir) {

        this.prefixLength = thePrefixLength;
        this.configDir = theConfigDir;
    }

    /**
     * Handles a request.
     *
     * @param exchange the exchange containing the request from the client and used to send the response
     * @throws IOException if there is an error reading the request body or writing the response
     */
    public void handle(final HttpExchange exchange) throws IOException {

        final InputStream is = exchange.getRequestBody();
        is.read(); // .. read the request body
        final String response = "This is the response from the API handler";
        exchange.sendResponseHeaders(200, response.length());
        final OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
