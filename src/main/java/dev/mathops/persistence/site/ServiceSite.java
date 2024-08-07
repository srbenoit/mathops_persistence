package dev.mathops.persistence.site;

import dev.mathops.commons.CoreConstants;
import dev.mathops.commons.installation.Installation;
import dev.mathops.commons.log.Log;
import dev.mathops.commons.log.LogBase;
import dev.mathops.persistence.site.session.SessionManager;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * A servlet the provides the data access API as well as a website with documentation and administrative functions.
 *
 * <p>
 * Administration is only allowed from a fixed set of IP addresses and logins, as configured in a file on the server.
 */
public final class ServiceSite extends HttpServlet {

    /** The name of the servlet. */
    private static final String TITLE = "MathOps Persistence Layer servlet";

    /** The prefix to select the API handler. */
    private static final String API_PREFIX = "/api";

    /** The prefix to select the MGT handler. */
    private static final String DOC_PREFIX = "/doc";

    /** The prefix to select the MGT handler. */
    private static final String MGT_PREFIX = "/mgt";

    /** The servlet configuration. */
    private ServletConfig servletConfig = null;

    /** The servlet context. */
    private ServletContext servletContext = null;

    /** The installation. */
    private Installation installation = null;

    /** Installation property with the path to the configuration directory. */
    private static final String CONFIG_DIR_PROPERTY = "public-dir";

    /** The default configuration directory, used when none specified. */
    private static final String DEFAULT_CONFIG_DIR = "/opt/mathops";

    /** The configuration directory. */
    private File configDir = null;

    /** The set of authorized administrators. */
    private Administrators administrators = null;

    /** The session manager. */
    private SessionManager sessionMgr = null;

    /**
     * Constructs a new {@code ServiceSite}.
     */
    private ServiceSite() {

        super();
    }

    /**
     * Initializes the servlet.
     *
     * @param config the servlet context in which the servlet is being initialized
     * @throws ServletException if the servlet could not be initialized
     */
    @Override
    public void init(final ServletConfig config) throws ServletException {

        this.servletConfig = config;
        this.servletContext = config.getServletContext();

        this.installation = (Installation) this.servletContext.getAttribute("Installation");

        final String serverInfo = this.servletContext.getServerInfo();
        Log.info(TITLE, " initializing: ", serverInfo);

        this.configDir = this.installation.extractFileProperty(CONFIG_DIR_PROPERTY, new File(DEFAULT_CONFIG_DIR));
        this.administrators = new Administrators(this.configDir);

        try {
            this.sessionMgr = new SessionManager(this.configDir);
        } catch (final NoSuchAlgorithmException ex) {
            throw new ServletException(ex);
        }

        Log.info(TITLE, " initialized");
    }

    /**
     * Gets the servlet configuration.
     *
     * @return the servlet configuration
     */
    @Override
    public ServletConfig getServletConfig() {

        return this.servletConfig;
    }

    /**
     * Gets the servlet context under which this servlet was initialized.
     *
     * @return the servlet context
     */
    @Override
    public ServletContext getServletContext() {

        return this.servletContext;
    }

    /**
     * Gets the servlet information string.
     *
     * @return the information string
     */
    @Override
    public String getServletInfo() {

        return TITLE;
    }

    /**
     * Called when the servlet container unloads the servlet.
     * <p>
     * This method reduces the reference count on the installation - when that count reaches zero, the installation's
     * JMX server is allowed to stop
     */
    @Override
    public void destroy() {

        Log.info(TITLE, " terminated");
    }

    /**
     * Processes a request. The first part of the request path (between the first and second '/') is used to determine
     * the site, then if the site is valid, the request is dispatched to the site processor.
     *
     * @param req  the request
     * @param resp the response
     * @throws IOException if there is an error writing the response
     */
    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

        req.setCharacterEncoding("UTF-8");

        final String remote = req.getRemoteAddr();

        try {
            final String requestHost = getHost(req);
            final String requestPath = getPath(req);
            LogBase.setHostPath(requestHost, requestPath, remote);

            try {
                if (req.isSecure()) {
                    serviceSecure(requestPath, req, resp);
                } else {
                    final String reqScheme = req.getScheme();
                    if ("http".equals(reqScheme)) {
                        serviceInsecure(requestPath, req, resp);
                    } else {
                        Log.warning("Invalid scheme: ", reqScheme);
                        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    }
                }
            } finally {
                LogBase.setHostPath(null, null, null);
            }
        } catch (final IOException ex) {
            // Make sure unexpected exceptions get logged rather than silently failing
            Log.severe(ex);
            throw ex;
        }
    }

    /**
     * Gets the host from a request, removing any trailing "dev" or "test" from the leading component (so
     * "foodev.bar.baz" and "footest.bar.baz" each return "foo.bar.baz" as the host).
     *
     * @param req the servlet request
     * @return the host
     */
    public static String getHost(final ServletRequest req) {

        final String server = req.getServerName();
        final String host;

        final int firstDot = server.indexOf('.');
        if (firstDot < 4) {
            host = server;
        } else {
            final String name = server.substring(0, firstDot);
            if (name.endsWith("dev")) {
                host = name.substring(0, name.length() - 3) + server.substring(firstDot);
            } else if (name.endsWith("test")) {
                host = name.substring(0, name.length() - 4) + server.substring(firstDot);
            } else {
                host = server;
            }
        }

        return host;
    }

    /**
     * Gets the servlet path from a request, converting a {@code null} value to the empty string. This returns the whole
     * path (the servlet path + the path info part).
     * <p>
     * Handlers should be registered on either the empty string or a path with a leading "/" and no trailing "/". The
     * reason for this behavior is that we want a handler to receive the same relative paths when registered on "" as it
     * would get if registered on "/foo".
     *
     * <pre>
     * http://www.example.com      --> ""     --> "" + ""
     * http://www.example.com/     --> "/"    --> "" + "/"
     * http://www.example.com/a    --> "/a"   --> "" + "/a"
     * http://www.example.com/a/   --> "/a/"  --> "" + "/a/"
     * http://www.example.com/a/b  --> "/a/b" --> "" + "/a/b"
     *
     * http://www.example.com/foo      --> "/foo"     --> "/foo" + ""
     * http://www.example.com/foo/     --> "/foo/"    --> "/foo" + "/"
     * http://www.example.com/foo/a    --> "/foo/a"   --> "/foo" + "/a"
     * http://www.example.com/foo/a/   --> "/foo/a/"  --> "/foo" + "/a/"
     * http://www.example.com/foo/a/b  --> "/foo/a/b" --> "/foo" + "/a/b"
     * </pre>
     *
     * @param req the servlet request
     * @return the path
     */
    public static String getPath(final HttpServletRequest req) {

        final String sPath = req.getServletPath();
        final String iPath = req.getPathInfo();

        final String path = sPath == null ? iPath : iPath == null ? sPath : sPath + iPath;

        return path == null ? CoreConstants.EMPTY : path;
    }

    /**
     * Processes a request when it is known the connection was secured. The first part of the request path is used to
     * determine whether the request is for a public file, or if not, to determine the mid-controller to which to
     * forward the request.
     *
     * @param requestPath the request path
     * @param req         the HTTP servlet request
     * @param resp        the HTTP servlet response
     * @throws IOException if there is an error writing the response
     */
    private void serviceSecure(final String requestPath, final HttpServletRequest req,
                               final HttpServletResponse resp) throws IOException {

        Log.info("Servicing secure request: " + requestPath);

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    /**
     * Processes a request when it is known the connection is not secure. The first part of the request path is used to
     * determine whether the request is for a public file, or if not, to determine the mid-controller to which to
     * forward the request.
     *
     * @param requestPath the request path
     * @param req         the HTTP servlet request
     * @param resp        the HTTP servlet response
     * @throws IOException if there is an error writing the response
     */
    private void serviceInsecure(final String requestPath, final HttpServletRequest req,
                                 final HttpServletResponse resp) throws IOException {

        Log.info("Servicing insecure request: " + requestPath);

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
