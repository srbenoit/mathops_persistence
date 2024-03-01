package dev.mathops.persistence.site;

import com.sun.net.httpserver.HttpServer;
import dev.mathops.commons.log.Log;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * A stand-alone HTTP web server that provides the service API as well as a management interface.
 */
public final class ServiceSite {

    /** The prefix to select the API handler. */
    private static final String API_PREFIX = "/api";

    /** The prefix to select the MGT handler. */
    private static final String MGT_PREFIX = "/mgt";

    /**
     * Constructs a new {@code ServiceSite}.
     */
    private ServiceSite() {

        // No action
    }

    /**
     * Determines the configuration file directory, either from command-line arguments or as a default directory below
     * the user's home directory.
     *
     * @param args command-line arguments
     * @return the configuration directory if that directory could successfully be determined (this will be guaranteed
     * to be a directory that exists) or {@code null} if the directory could not be determined, in which case an error
     * message will have been printed using {@code Log.fine}
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
                            break;
                        }
                    } else {
                        Log.fine("ERROR: \"--configdir\" argument must be followed by a directory path.");
                        break;
                    }
                } else {
                    Log.fine("ERROR: multiple \"--configdir\" arguments are not allowed.");
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
            Log.info("Using configuration directory: ", configDir);
            try {
                final HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

                final int apiPrefixLen = API_PREFIX.length();
                server.createContext(API_PREFIX, new ApiHandler(apiPrefixLen, configDir));

                final int mgtPrefixLen = MGT_PREFIX.length();
                server.createContext(MGT_PREFIX, new ManagementHandler(mgtPrefixLen, configDir));

                server.setExecutor(null); // creates a default executor
                server.start();
            } catch (final IOException ex) {
                Log.warning(ex);
            }
        }
    }
}
