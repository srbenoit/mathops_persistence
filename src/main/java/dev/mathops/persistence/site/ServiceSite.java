package dev.mathops.persistence.site;

import dev.mathops.persistence.site.session.SessionManager;
import io.undertow.Handlers;
import io.undertow.Undertow;
import dev.mathops.commons.log.Log;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.RequestBufferingHandler;
import io.undertow.server.handlers.ResponseCodeHandler;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * A stand-alone HTTP web server that provides the service API as well as a management interface.
 */
public final class ServiceSite {

    /** The host on which to listen. */
    private static final String HOST = "localhost";

    /** The keystore filename. */
    private static final String KEYSTORE = "tlskeystore.jks";

    /** The keystore password. */
    private static final char[] STOREPASS = "a23095u".toCharArray();

    /** The alias of the self-signed certificate. */
    private static final String SELF_SIGNED_ALIAS = "selfsigned";

    /** The prefix to select the API handler. */
    private static final String API_PREFIX = "/api";

    /** The prefix to select the MGT handler. */
    private static final String MGT_PREFIX = "/mgt";

    /** The configuration directory. */
    private final File configDir;

    /** The SSL/TLS context. */
    private final SSLContext context;

    /** The session manager. */
    private final SessionManager sessionMgr;

    /**
     * Constructs a new {@code ServiceSite}.
     *
     * @param theConfigDir the configuration directory
     * @throws NoSuchAlgorithmException if either the strong random number generator or the SHA-512 message digest could
     *                                  not be created
     * @throws KeyStoreException if the self-signed TLS certificate did not exist and could not be created
     */
    private ServiceSite(final File theConfigDir) throws NoSuchAlgorithmException, KeyStoreException {

        this.configDir = theConfigDir;
        this.sessionMgr = new SessionManager(theConfigDir);
        this.context = getSSLContext();
    }

    /**
     * Creates the SSL context for HTTPS connections.  This uses a certificate in the configuration directory.  If no
     * such certificate exists, a self-signed certificate is created and used.
     *
     * <p>
     * The self-signed certificate can be created using the following commands with "keytool":
     *
     * <pre>
     * keytool -genkey -keyalg RSA -alias selfsigned -keystore tlskeystore.jks -storepass a23095u -validity 36000
     *         -keysize 4096
     * Enter the distinguished name. Provide a single dot (.) to leave a sub-component empty or press ENTER to use the
     * default value in braces.
     * What is your first and last name?
     *   [Unknown]:  localhost
     * What is the name of your organizational unit?
     *   [Unknown]:  Persistence
     * What is the name of your organization?
     *   [Unknown]:  MathOps
     * What is the name of your City or Locality?
     *   [Unknown]:  Fort Collins
     * What is the name of your State or Province?
     *   [Unknown]:  Colorado
     * What is the two-letter country code for this unit?
     *   [Unknown]:  US
     * Is CN=localhost, OU=Persistence, O=MathOps, L=Fort Collins, ST=Colorado, C=US correct?
     *   [no]:  yes
     * Generating 4,096 bit RSA key pair and self-signed certificate (SHA384withRSA) with a validity of 36,000 days
     *         for: CN=localhost, OU=Persistence, O=MathOps, L=Fort Collins, ST=Colorado, C=US
     * </pre>
     *
     * @return the SSL context
     * @throws NoSuchAlgorithmException if a trust manager could not be created
     * @throws KeyStoreException if the self-signed TLS certificate did not exist and could not be created
     */
    private SSLContext getSSLContext() throws NoSuchAlgorithmException, KeyStoreException {

        SSLContext context = null;

        final File storeFile = new File(this.configDir, KEYSTORE);
        KeyStore store = null;

        if (storeFile.exists()) {
            try {
                final KeyStore loadedStore = KeyStore.getInstance(storeFile, STOREPASS);

                if (loadedStore.getCertificate(SELF_SIGNED_ALIAS) == null) {
                    Log.warning("'", KEYSTORE, "' does not contain a self-signed certificate");
                } else {
                    store = loadedStore;
                }
            } catch (final IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException ex) {
                Log.warning("Unable to load '", KEYSTORE, "'.", ex);
            }
        }

        if (store == null) {
            throw new KeyStoreException("Failed to load self-signed TLS certificate.");
        }

        final String keyAlg = KeyManagerFactory.getDefaultAlgorithm();
        final KeyManagerFactory kmf = KeyManagerFactory.getInstance(keyAlg);
        try {
            kmf.init(store, STOREPASS);
        } catch (final UnrecoverableKeyException ex) {
            throw new KeyStoreException("Failed to read private key in self-signed TLS certificate.", ex);
        }
        final KeyManager[] keyManagers = kmf.getKeyManagers();

        final String trustAlg = TrustManagerFactory.getDefaultAlgorithm();
        final TrustManagerFactory tmf = TrustManagerFactory.getInstance(trustAlg);
        tmf.init(store);
        final TrustManager[] trustManagers = tmf.getTrustManagers();

        final SecureRandom rnd = SecureRandom.getInstanceStrong();

        try {
            context = SSLContext.getInstance("TLSv1.2");
            context.init(keyManagers, trustManagers, rnd);
        } catch (final KeyManagementException ex) {
            throw new KeyStoreException("Failed to create SSL context.", ex);
        }

        return context;
    }

    /**
     * Runs the service site using a specified configuration directory.
     */
    private void run() {

        Log.info("Using configuration directory: ", this.configDir);

        final int mgtPrefixLen = MGT_PREFIX.length();
        final HttpHandler mgtHandler = new ManagementHandler(mgtPrefixLen, this.configDir, this.sessionMgr);

        final int apiPrefixLen = API_PREFIX.length();
        final HttpHandler apiHandler = new ApiHandler(apiPrefixLen, this.configDir, this.sessionMgr);

        final PathHandler pathHandler = Handlers.path(new ResponseCodeHandler(404));
        pathHandler.addPrefixPath(MGT_PREFIX, mgtHandler);
        pathHandler.addPrefixPath(API_PREFIX, apiHandler);

        final HttpHandler h = new RequestBufferingHandler(pathHandler, 10);

        final Undertow server1 = Undertow.builder().addHttpListener(8000, HOST).setHandler(h).build();
        final Undertow server2 = Undertow.builder().addHttpsListener(8443, HOST, this.context).setHandler(h).build();

        server2.start();
        server1.start();
    }

    /**
     * Determines the configuration file directory, either from command-line arguments or as a default directory below
     * the user's home directory.
     *
     * @param args command-line arguments
     * @return the configuration directory if that directory could successfully be determined (this will be guaranteed
     *         to be a directory that exists) or {@code null} if the directory could not be determined, in which case an
     *         error message will have been printed using {@code Log.fine}
     */
    private static File determineConfigDir(final String... args) {

        File configDir = null;
        boolean ok = true;

        final int numArgs = args == null ? 0 : args.length;
        for (int i = 0; i < numArgs; ++i) {
            if ("--configdir".equals(args[i])) {
                if (configDir == null) {
                    if (i + 1 < numArgs) {
                        final String configPath = args[i + 1];
                        final File dir = new File(configPath);

                        if (dir.exists() && dir.isDirectory()) {
                            configDir = dir;
                        } else {
                            Log.fine("ERROR: Configuration directory \"", configPath, "\" is not a valid directory.");
                            ok = false;
                            break;
                        }
                    } else {
                        Log.fine("ERROR: \"--configdir\" argument must be followed by a directory path.");
                        ok = false;
                        break;
                    }
                } else {
                    Log.fine("ERROR: multiple \"--configdir\" arguments are not allowed.");
                    ok = false;
                    break;
                }
            }
        }

        if (ok && configDir == null) {
            final String userHome = System.getProperty("user.home");

            final File dir = new File(userHome);

            if (dir.exists() && dir.isDirectory()) {
                configDir = new File(dir, "mathops_persistence");
                if (!(configDir.exists() || configDir.mkdir())) {
                    final String path = configDir.getAbsolutePath();
                    Log.fine("ERROR: User home directory \"", path, "\" is not a valid directory.");
                }
            } else {
                Log.fine("ERROR: User home directory \"", userHome, "\" is not a valid directory.");
            }
        }

        return configDir;
    }

    /**
     * Main method to run the server.
     *
     * @param args command-line arguments
     */
    public static void main(final String... args) {

        final File configDir = determineConfigDir(args);

        if (configDir != null) {
            try {
                new ServiceSite(configDir).run();
            } catch (final NoSuchAlgorithmException ex) {
                Log.severe("Error while creating SecureRandom or MessageDigest.", ex);
            } catch (final KeyStoreException ex) {
                Log.severe("Error while generating self-signed certificate.", ex);
            }
        }
    }
}
