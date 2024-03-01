package dev.mathops.persistence.site;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.mathops.commons.builder.HtmlBuilder;
import dev.mathops.commons.log.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * A handler for Management requests.
 */
public final class ManagementHandler implements HttpHandler {

    /** An HTTP status code. */
    private static final int STATUS_OK = 200;

    /** An HTTP status code. */
    private static final int STATUS_BAD_METHOD = 405;

    /** An HTTP status code. */
    private static final int STATUS_NOT_FOUND = 404;

    /** The number of characters of URI path used to select this handler. */
    private final int prefixLength;

    /** The directory in which to find configuration files. */
    private final File configDir;

    /**
     * Constructs a new {@code ManagementHandler}.
     *
     * @param thePrefixLength the number of characters of URI path used to select this handler
     * @param theConfigDir the directory in which to find configuration files
     */
    ManagementHandler(final int thePrefixLength, final File theConfigDir) {

        this.prefixLength = thePrefixLength;
        this.configDir = theConfigDir;

        // TODO: Load in the configured authentication properties (lists of {username, salt, hash, role}).
    }

    /**
     * Handles a request.
     *
     * @param exchange the exchange containing the request from the client and used to send the response
     * @throws IOException if there is an error reading the request body or writing the response
     */
    public void handle(final HttpExchange exchange) throws IOException {

        final String method = exchange.getRequestMethod();

        try {
            if ("GET".equalsIgnoreCase(method)) {
                handleGet(exchange);
            } else if ("POST".equalsIgnoreCase(method)) {
                handlePost(exchange);
            } else {
                exchange.sendResponseHeaders(STATUS_BAD_METHOD, 0L);
            }
        } catch (final IOException e1) {
            Log.warning(e1);
            throw e1;
        } catch (final Exception e2) {
            Log.warning(e2);
        }
    }

    /**
     * Handles a GET request.
     *
     * @param exchange the exchange containing the request from the client and used to send the response
     * @throws IOException if there is an error reading the request body or writing the response
     */
    private void handleGet(final HttpExchange exchange) throws IOException {

        final URI uri = exchange.getRequestURI();
        final String path = uri.getPath().substring(this.prefixLength);

        Log.info("GET Path is: ", path);

        if ("/login.html".equals(path)) {
            doLoginPage(exchange);
        } else {
            exchange.sendResponseHeaders(STATUS_NOT_FOUND, 0L);
        }
    }

    /**
     * Generates the login page.
     *
     * @param exchange the exchange containing the request from the client and used to send the response
     * @throws IOException if there is an error reading the request body or writing the response
     */
    private void doLoginPage(final HttpExchange exchange) throws IOException {

        final HtmlBuilder htm = new HtmlBuilder(200);

        htm.addln("<html><head></head><body>");

        htm.addln("<form action='login.html' method='POST'>");
        htm.addln("<p><label for='u'>Username:</label> <input type='text' id='u' name='u'/></p>");
        htm.addln("<p><label for='p'>Password:</label> <input type='password' id='p' name='p'/></p>");
        htm.addln("<p><input type='submit'/></p>");
        htm.addln("</form>");

        htm.addln("</body></html>");

        sendResponse(htm, exchange);
    }

    /**
     * Handles a POST request.
     *
     * @param exchange the exchange containing the request from the client and used to send the response
     * @throws IOException if there is an error reading the request body or writing the response
     */
    private void handlePost(final HttpExchange exchange) throws IOException {

        final URI uri = exchange.getRequestURI();
        final String path = uri.getPath();

        Log.info("POST Path is: ", path);

        final InputStream is = exchange.getRequestBody();
        is.read(); // .. read the request body
        final String response = "This is the POST response from the Management handler";
        exchange.sendResponseHeaders(STATUS_OK, response.length());
        final OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    /**
     * Sends a response to the client.
     *
     * @param htm the {@code HtmlBuilder} containing the response
     * @param exchange the exchange containing the request from the client and used to send the response
     * @throws IOException if there is an error reading the request body or writing the response
     */
    private void sendResponse(final HtmlBuilder htm, final HttpExchange exchange) throws IOException {

        final String response = htm.toString();
        final long len = (long) response.length();
        exchange.sendResponseHeaders(STATUS_OK, len);

        final OutputStream os = exchange.getResponseBody();
        final byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        os.write(responseBytes);
        os.close();
    }
}
