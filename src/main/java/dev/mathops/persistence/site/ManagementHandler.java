package dev.mathops.persistence.site;

import dev.mathops.commons.builder.HtmlBuilder;
import dev.mathops.commons.log.Log;
import dev.mathops.persistence.site.session.Login;
import dev.mathops.persistence.site.session.LoginResult;
import dev.mathops.persistence.site.session.Session;
import dev.mathops.persistence.site.session.SessionManager;
import io.undertow.server.BlockingHttpExchange;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A handler for Management requests.
 */
public final class ManagementHandler implements HttpHandler {

    /** A request method. */
    private static final HttpString GET = new HttpString("GET");

    /** A request method. */
    private static final HttpString POST = new HttpString("POST");

    /** An HTTP status code. */
    private static final int STATUS_BAD_METHOD = 405;

    /** An HTTP status code. */
    private static final int STATUS_NOT_FOUND = 404;

    /** An HTTP status code. */
    private static final int STATUS_MOVED_PERMANENTLY = 301;

    /** The number of characters of URI path used to select this handler. */
    private final int prefixLength;

    /** The directory in which to find configuration files. */
    private final File configDir;

    /** The session manager. */
    private final SessionManager sessionMgr;

    /**
     * Constructs a new {@code ManagementHandler}.
     *
     * @param thePrefixLength the number of characters of URI path used to select this handler
     * @param theConfigDir    the directory in which to find configuration files
     * @param theSessionMgr   the session manager
     */
    ManagementHandler(final int thePrefixLength, final File theConfigDir, final SessionManager theSessionMgr) {

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
            final HttpString method = exchange.getRequestMethod();

            if (GET.equals(method)) {
                handleGet(exchange);
            } else if (POST.equals(method)) {
                handlePost(exchange);
            } else {
                exchange.setStatusCode(STATUS_BAD_METHOD);
            }

            exchange.endExchange();
        }
    }

    /**
     * Handles a GET request.
     *
     * @param exchange the exchange containing the request from the client and used to send the response
     * @throws IOException if there is an error reading the request body or writing the response
     */
    private void handleGet(final HttpServerExchange exchange) throws Exception {

        final String requestPath = exchange.getRequestPath();
        final String subpath = requestPath.substring(this.prefixLength);

        Log.info("GET Path is: ", subpath);

        if ("/login.html".equals(subpath)) {
            doLoginGet(exchange);
        } else if ("/secure.html".equals(subpath)) {
            doSecureGet(exchange);
        } else {
            exchange.setStatusCode(STATUS_NOT_FOUND);
        }
    }

    /**
     * Handles a POST request.
     *
     * @param exchange the exchange containing the request from the client and used to send the response
     * @throws IOException if there is an error reading the request body or writing the response
     */
    private void handlePost(final HttpServerExchange exchange) throws Exception {

        final String requestPath = exchange.getRequestPath();
        final String subpath = requestPath.substring(this.prefixLength);

        Log.info("POST Path is: ", subpath);

        if ("/login.html".equals(subpath)) {
            doLoginPost(exchange);
        } else if ("/create_admin_login.html".equals(subpath)) {
            doCreateAdminLoginPost(exchange);
        } else {
            exchange.setStatusCode(STATUS_NOT_FOUND);
        }
    }

    /**
     * Generates the login page.
     *
     * @param exchange the exchange containing the request from the client and used to send the response
     * @throws IOException if there is an error reading the request body or writing the response
     */
    private void doLoginGet(final HttpServerExchange exchange) throws IOException {

        final HtmlBuilder htm = new HtmlBuilder(200);

        startPage(htm);
        emitHeader(htm);

        if (this.sessionMgr.isValid()) {
            emitLoginForm(htm, null);
        } else {
            htm.sDiv(null, "style='padding-left:30px;'");
            htm.sP().add("The MathOps <strong>Persistence Layer</strong> has been installed correctly.").eP();
            htm.sP().add("To get started, please configure your administrator login.").eP();
            htm.eDiv();

            htm.addln("<form action='create_admin_login.html' method='POST'>");
            htm.add("<label for='u' class='right'>Administrator Username: &nbsp;</label>",
                    "<input type='text' id='u' name='u' style='width:150pt;'/>").br().addln();
            htm.add("<label for='p1' class='right'>Enter Password: &nbsp;</label>",
                    "<input type='password' id='p1' name='p1' style='width:150pt;'/>").br().addln();
            htm.add("<label for='p2' class='right'>Confirm Password: &nbsp;</label>",
                    "<input type='password' id='p2' name='p2' style='width:150pt;'/>").br().addln();
            htm.add("<input type='submit' style='margin-left:200pt;' value='Apply'/></p>");
            htm.addln("</form>");
        }

        endPage(htm);

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
        final String htmString = htm.toString();
        exchange.getResponseSender().send(htmString);
    }

    /**
     * Processes a POST request from a form submission of the login page form.
     *
     * <p>
     * This reads the "login.json" file from the configuration directory and compares login credentials with that file
     * to see if the user is authenticated.  The format of that file is:
     *
     * <pre>
     * [
     *    ["username1", "salt1", "sha512hash1_hex", "role1"],
     *    ["username2", "salt2", "sha512hash2_hex", "role2"],
     *    ...
     * ]
     * </pre>
     *
     * @param exchange the exchange containing the request from the client and used to send the response
     * @throws IOException if there is an error reading the request body or writing the response
     */
    private void doLoginPost(final HttpServerExchange exchange) throws IOException {

        final byte[] requestBody = getRequestBody(exchange);
        final Map<String, String> params = extractParameters(requestBody);

        final String username = params.get("u");
        final String password = params.get("p");

        Log.info("Login request, u=", username, ", p=", password);

        final LoginResult result = this.sessionMgr.createSession(username, password);
        final Session session = result.getSession();

        if (session == null) {
            final HtmlBuilder htm = new HtmlBuilder(200);

            startPage(htm);
            emitHeader(htm);
            final String error = result.getError();
            emitLoginForm(htm, error);
            endPage(htm);

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
            final String htmString = htm.toString();
            exchange.getResponseSender().send(htmString);
        } else {
            final String sid = session.getId();
            exchange.setStatusCode(STATUS_MOVED_PERMANENTLY);
            exchange.getResponseHeaders().put(new HttpString("Location"), "secure.html?sid=" + sid);
        }
    }

    /**
     * Processes a POST request from a form submission on the page to configure a new login.
     *
     * <p>
     * This reads the "login.json" file from the configuration directory and compares login credentials with that file
     * to see if the user is authenticated.  The format of that file is:
     *
     * <pre>
     * [
     *    ["username1", "salt1", "sha512hash1_hex", "role1"],
     *    ["username2", "salt2", "sha512hash2_hex", "role2"],
     *    ...
     * ]
     * </pre>
     *
     * @param exchange the exchange containing the request from the client and used to send the response
     * @throws IOException if there is an error reading the request body or writing the response
     */
    private void doCreateAdminLoginPost(final HttpServerExchange exchange) throws Exception {

        final HtmlBuilder htm = new HtmlBuilder(200);

        startPage(htm);
        emitHeader(htm);

        if (this.sessionMgr.isValid()) {
            emitLoginForm(htm, null);
        } else {
            final byte[] requestBody = getRequestBody(exchange);
            final Map<String, String> params = extractParameters(requestBody);

            final String u = params.get("u");
            final String p1 = params.get("p1");
            final String p2 = params.get("p2");

            final boolean usernameBad = u == null || u.isBlank();
            final boolean p1Bad = p1 == null || p1.isBlank();
            final boolean p2Bad = p2 == null || p2.isBlank();

            if (usernameBad || p1Bad || p2Bad) {
                // Incomplete data: present form again
            } else if (p1.equals(p2)) {
                Log.info("Administrator login created");

                final List<String> roles = List.of("ADMIN");
                final Login login = this.sessionMgr.createLogin(u, p1, roles);

                // TODO: Establish a login session
            } else {
                // Password mismatch: present form again
            }
        }

        endPage(htm);

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
        final String htmString = htm.toString();
        exchange.getResponseSender().send(htmString);
    }

    /**
     * Generates the secure page.
     *
     * @param exchange the exchange containing the request from the client and used to send the response
     */
    private void doSecureGet(final HttpServerExchange exchange) {

        if (this.sessionMgr.isValid()) {
            final Map<String, Deque<String>> params = exchange.getQueryParameters();
            final Deque<String> sid = params.get("sid");

            if (sid == null) {
                exchange.setStatusCode(STATUS_MOVED_PERMANENTLY);
                exchange.getResponseHeaders().put(new HttpString("Location"), "login.html");
            } else {
                final String sessionId = sid.getFirst();
                final Session session = this.sessionMgr.getSession(sessionId);
                if (session == null) {
                    exchange.setStatusCode(STATUS_MOVED_PERMANENTLY);
                    exchange.getResponseHeaders().put(new HttpString("Location"), "login.html");
                } else{
                    generateSecurePage(exchange, session);
                }
            }
        } else {
            exchange.setStatusCode(STATUS_NOT_FOUND);
        }
    }

    /**
     * Generates the secure page.
     *
     * @param exchange the exchange containing the request from the client and used to send the response
     */
    private void generateSecurePage(final HttpServerExchange exchange, final Session session) {

        final HtmlBuilder htm = new HtmlBuilder(200);

        startPage(htm);
        emitHeader(htm);

        final Login login = session.getLogin();

        htm.sP(null, "style='padding-left:20px;'").add("Logged in as ", login.getUsername()).eP();

        endPage(htm);

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
        final String htmString = htm.toString();
        exchange.getResponseSender().send(htmString);
    }

    /**
     * Reads the request body from an HTTP exchange.
     *
     * @param exchange the exchange containing the request from the client and used to send the response
     * @return the request body
     * @throws IOException if there is an error reading the request body or writing the response
     */
    private static byte[] getRequestBody(final HttpServerExchange exchange) throws IOException {

        try (final BlockingHttpExchange blocking = exchange.startBlocking()) {
            final InputStream is = exchange.getInputStream();

            final ByteArrayOutputStream baos = new ByteArrayOutputStream(100);
            final byte[] buffer = new byte[100];
            int numRead = is.read(buffer);
            while (numRead > 0) {
                baos.write(buffer, 0, numRead);
                numRead = is.read(buffer);
            }

            return baos.toByteArray();
        }
    }

    /**
     * Extracts parameters from a request body as submitted by a form POST.  The body format should be in
     * "application/x-www-form-urlencoded" format, which is of the form "a=hello&b=world&c=123".
     *
     * @param requestBody the request body byte array
     * @return a map containing the extracted parameters
     */
    private static Map<String, String> extractParameters(final byte[] requestBody) {

        final String bodyString = new String(requestBody, StandardCharsets.UTF_8);
        final String[] parts = bodyString.split("&");

        final Map<String, String> result = new HashMap<>(parts.length);

        for (final String part : parts) {
            final int eq = part.indexOf('=');
            if (eq > 0) {
                final String key = part.substring(0, eq);
                final String value = part.substring(eq + 1);
                result.put(key, value);
            }
        }

        return result;
    }

    /**
     * Emits the start of a management site page, including the embedded stylesheet.
     *
     * @param htm the {@code HtmlBuilder} to which to append
     */
    private void startPage(final HtmlBuilder htm) {

        htm.addln("<html><head>");
        htm.addln(" <style>");
        htm.addln("  body { background-color:white; font-family:sans-serif; font-size:18pt; ",
                "font-stretch:semi-condensed; }");
        htm.addln("  h1 { color:white; margin:0; font-size:34pt; font-weight:100; font-stretch:condensed; }");
        htm.addln("  h1 strong { font-weight:700; color:#C8C372; }");
        htm.addln("  input { font-size:16pt; font-stretch:condensed; margin-bottom:6pt; }");
        htm.addln("  label.right { display:inline-block; width:200pt; text-align:right; color:#1E4D2B; font-size:16pt;}");
        htm.addln(" </style>");
        htm.addln("</head><body>");
    }

    /**
     * Emits the end of a management site page, including the embedded stylesheet.
     *
     * @param htm the {@code HtmlBuilder} to which to append
     */
    private void endPage(final HtmlBuilder htm) {

        htm.addln("</body></html>");
    }

    /**
     * Emits the page header.
     *
     * @param htm the {@code HtmlBuilder} to which to append
     */
    private void emitHeader(final HtmlBuilder htm) {

        htm.sDiv(null, "style='background-color: #1E4D2B; margin:10px; padding:10px 20px;'");
        htm.sH(1).add("MathOps <strong>Persistence Layer</strong>").eH(1);
        htm.eDiv();
    }

    /**
     * Emits the form to log in.
     *
     * @param htm the {@code HtmlBuilder} to which to append
     * @param errorMessage an error message ({@code null} if none)
     */
    private void emitLoginForm(final HtmlBuilder htm, final String errorMessage) {

        htm.sDiv(null, "style='margin-top:60px;'");
        htm.addln("<form action='login.html' method='POST'>");
        htm.add("<label for='u' class='right'>Username: &nbsp;</label>",
                "<input type='text' id='u' name='u' style='width:150pt;'/>").br().addln();
        htm.add("<label for='p' class='right'>Password: &nbsp;</label>",
                "<input type='password' id='p' name='p' style='width:150pt;'/>").br().addln();
        htm.addln("<input type='submit' style='margin-left:200pt;' value='Submit'/>");
        htm.addln("</form>");

        if (errorMessage != null) {
            htm.sP(null, "style='color:#7E5475; margin-left:200pt;'").add(errorMessage).eP();
        }
        htm.eDiv();
    }

    /**
     * Emits the form to configure the administrator username and password.
     *
     * @param htm the {@code HtmlBuilder} to which to append
     * @param errorMessage an error message ({@code null} if none)
     */
    private void emitCreateAdminForm(final HtmlBuilder htm, final String errorMessage) {

        htm.sDiv(null, "style='padding-left:30px;'");
        htm.sP().add("The MathOps <strong>Persistence Layer</strong> has been installed correctly.").eP();
        htm.sP().add("To get started, please configure your administrator login.").eP();
        htm.eDiv();

        htm.addln("<form action='create_admin_login.html' method='POST'>");
        htm.add("<label for='u' class='right'>Administrator Username: &nbsp;</label>",
                "<input type='text' id='u' name='u' style='width:150pt;'/>").br().addln();
        htm.add("<label for='p1' class='right'>Enter Password: &nbsp;</label>",
                "<input type='password' id='p1' name='p1' style='width:150pt;'/>").br().addln();
        htm.add("<label for='p2' class='right'>Confirm Password: &nbsp;</label>",
                "<input type='password' id='p2' name='p2' style='width:150pt;'/>").br().addln();
        htm.add("<input type='submit' style='margin-left:200pt;' value='Apply'/></p>");
        htm.addln("</form>");

        if (errorMessage != null) {
            htm.sP(null, "style='color:#7E5475;'").add(errorMessage).eP();
        }
    }
}
