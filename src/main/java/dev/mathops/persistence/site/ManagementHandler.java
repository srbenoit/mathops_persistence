package dev.mathops.persistence.site;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A handler for Management requests.
 */
public final class ManagementHandler implements HttpHandler {

    /**
     * Constructs a new {@code ManagementHandler}.  This determines a file path (a default or a path specified in
     * command-line arguments) from which to load configuration data, then loads that data to configure the handler.
     *
     * @param configDir the directory in which to find configuration files
     */
    ManagementHandler(final File configDir) {

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
        final String response = "This is the response from the Management handler";
        exchange.sendResponseHeaders(200, response.length());
        final OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
