package dev.mathops.persistence.site.session;

import dev.mathops.commons.builder.HtmlBuilder;
import dev.mathops.commons.file.FileLoader;
import dev.mathops.commons.log.Log;
import dev.mathops.commons.parser.HexEncoder;
import dev.mathops.commons.parser.ParsingException;
import dev.mathops.commons.parser.json.JSONParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;

/**
 * A session manager.
 *
 * <p>
 * This class loads the "login.json" file with the set of defined logins, and can test whether the system has a valid
 * configuration (at least one ADMIN login defined), or if it needs to be configured.
 *
 * <p>
 * It can process login requests to establish new sessions, manage session lifecycles, and test the validity and granted
 * roles of a session ID.
 */
public final class SessionManager {

    /** Characters allowed in a salt value (do not include double-quotes to simplify JSON generation). */
    private static final String SALT_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_~";

    /** The minimum allowed length of a salt value (6 bits per character). */
    static final int MIN_SALT_LEN = 20;

    /** The length of a session ID (6 bits per character). */
    static final int SESSION_ID_LEN = 24;

    /** The filename where logins are stored. */
    private static final String FILENAME = "login.json";

    /** The filename of the most recent backup of login data. */
    private static final String FILENAME_BAK1 = "login_bak1.json";

    /** The filename of the second most recent backup of login data. */
    private static final String FILENAME_BAK2 = "login_bak2.json";

    /** The filename of the third most recent backup of login data. */
    private static final String FILENAME_BAK3 = "login_bak3.json";

    /** The expected length of an SHA-512 hash in bytes. */
    private static final int EXPECT_HASH_LEN = 64;

    /** The configuration directory. */
    private final File configDir;

    /** A secure random number generator. */
    private final SecureRandom rnd;

    /** A SHA-512 digest (access to this member is synchronized on the member). */
    private final MessageDigest digest;

    /** The active sessions (access to this member is synchronized on the member). */
    private final Map<String, Session> sessions;

    /** The loaded logins (access to this member is synchronized on the member). */
    private final SequencedMap<String, Login> logins;

    /** Flag indicating configuration is valid (there is at least one ADMIN login defined). */
    private boolean valid;

    /**
     * Constructs a new {@code SessionManager}.
     *
     * @param theConfigDir the configuration directory in which to find the "login.json" file
     */
    public SessionManager(final File theConfigDir) throws NoSuchAlgorithmException {

        this.configDir = theConfigDir;
        this.valid = false;

        this.rnd = SecureRandom.getInstanceStrong();
        this.digest = MessageDigest.getInstance("SHA-512");
        this.sessions = new HashMap<>(20);

        this.logins = load();
    }

    /**
     * Tests whether the configuration is valid.
     *
     * @return true if valid (there is at least one ADMIN login configured)
     */
    public boolean isValid() {

        return this.valid;
    }

    /**
     * Attempts to load the "login.json" file from a specified configuration directory.
     *
     * @return a sequenced map from string username to the corresponding {@code Login} object (empty if the file could
     *         not be found, could not be parsed, or had no records)
     */
    private SequencedMap<String, Login> load() {

        final SequencedMap<String, Login> result = new LinkedHashMap<>(10);

        final File file = new File(this.configDir, FILENAME);
        if (file.exists()) {
            final String json = FileLoader.loadFileAsString(file, false);
            if (json == null) {
                Log.warning("'", FILENAME, "' could not be read.");
            } else {
                try {
                    final Object parsedJson = JSONParser.parseJSON(json);
                    if (parsedJson instanceof final Object[] array) {
                        processParsedJson(array, result);
                        final int count = result.size();
                        final String countStr = Integer.toString(count);
                        Log.info("Loaded ", countStr, " login configurations from '", FILENAME, "'.");

                        for (final Login login : result.sequencedValues()) {
                            if (login.isAdmin()) {
                                Log.info("Login system configuration is valid.");
                                this.valid = true;
                                break;
                            }
                        }
                    } else {
                        Log.warning("'", FILENAME, "' had no top-level array of login objects.");
                    }
                } catch (final ParsingException ex) {
                    Log.warning("Failed to parse '", FILENAME, "'.", ex);
                }
            }
        } else {
            Log.warning("'", FILENAME, "' was not found.");
        }

        return result;
    }

    /**
     * Stores a set of configured logins to the "login.json" file.  The existing file is backed up if present.
     */
    void store() {

        final HtmlBuilder htm = new HtmlBuilder(1000);
        final int numLogins = this.logins.size();
        int onRow = 0;

        htm.addln("[");
        for (final Login login : this.logins.sequencedValues()) {
            ++onRow;

            final String username = login.getUsername();
            final String salt = login.getSalt();
            final String hashHex = login.getHashHex();
            final List<String> roles = login.getRoles();
            final int numRoles = roles.size();

            htm.add("  [\"", username, "\", \"", salt, "\", \"", hashHex, "\", \"", roles.get(0));
            for (int i = 1; i < numRoles; ++i) {
                htm.add(", ", roles.get(i));
            }
            if (onRow == numLogins) {
                htm.addln("\"]");
            } else {
                htm.addln("\"],");
            }
        }
        htm.addln("]");

        final String fileContents = htm.toString();
        final File file = new File(this.configDir, FILENAME);

        if (file.exists()) {
            final File bak1 = new File(this.configDir, FILENAME_BAK1);
            if (bak1.exists()) {
                final File bak2 = new File(this.configDir, FILENAME_BAK2);
                if (bak2.exists()) {
                    final File bak3 = new File(this.configDir, FILENAME_BAK3);
                    if (bak3.exists()) {
                        if (!bak3.delete()) {
                            Log.warning("Failed to delete '", FILENAME_BAK3, "'.");
                        }
                    }
                    if (!bak2.renameTo(bak3)) {
                        Log.warning("Failed to move '", FILENAME_BAK2, "' to '", FILENAME_BAK3, "'.");
                    }
                }
                if (!bak1.renameTo(bak2)) {
                    Log.warning("Failed to move '", FILENAME_BAK1, "' to '", FILENAME_BAK2, "'.");
                }
            }
            if (!file.renameTo(bak1)) {
                Log.warning("Failed to move '", FILENAME, "' to '", FILENAME_BAK1, "'.");
            }
        }

        try (final FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(fileContents);
        } catch (final IOException ex) {
            Log.warning("Failed to write login data to '", FILENAME, "'.", ex);
        }
    }

    /**
     * Processes the top-level array from the JSON file.
     *
     * @param array  the array of objects, each of which should be an array of length 4
     * @param result the map to which to add loaded logins
     */
    private static void processParsedJson(final Object[] array, final Map<? super String, ? super Login> result) {

        for (final Object entry : array) {
            if (entry instanceof final Object[] inner) {
                if (inner.length == 4 && inner[0] instanceof final String username
                        && inner[1] instanceof final String salt
                        && inner[2] instanceof final String hash
                        && inner[3] instanceof final String roles) {

                    if (salt.length() < MIN_SALT_LEN) {
                        Log.warning("Salt value too short in '", FILENAME, "' entry.");
                    } else {
                        try {
                            final byte[] decodedHash = HexEncoder.decode(hash);
                            if (decodedHash.length == EXPECT_HASH_LEN) {
                                final String[] roleArray = roles.split(",");
                                final List<String> roleList = Arrays.asList(roleArray);

                                final Login login = new Login(username, salt, decodedHash, roleList);
                                result.put(username, login);
                            } else {
                                Log.warning("Decode hex hash was not length 64 in '", FILENAME, "' entry.");
                            }
                        } catch (final IllegalArgumentException ex) {
                            Log.warning("Could not decode hex hash in '", FILENAME, "' entry.");
                        }
                    }
                } else {
                    Log.warning("'", FILENAME, "' had array entry in top-level array that is not four strings.");
                }
            } else {
                Log.warning("'", FILENAME, "' had entry in top-level array that is not an array.");
            }
        }
    }

    /**
     * Creates a new login and adds it to the list of available logins.
     *
     * @param username the username
     * @param password the password
     * @param roles    the list of roles to grant to the new login
     */
    public Login createLogin(final String username, final String password, final List<String> roles) {

        final String salt = randomString(MIN_SALT_LEN);
        final byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
        final int numSaltBytes = saltBytes.length;

        final byte[] pwdBytes = password.getBytes(StandardCharsets.UTF_8);

        final byte[] hash;

        // MessageDigest is not thread-safe, so synchronize access to that object.

        synchronized (this.digest) {
            final byte[] digestedPwdBytes = this.digest.digest(pwdBytes);
            final int numDigestBytes = digestedPwdBytes.length;
            final int total = numSaltBytes + numDigestBytes;

            final byte[] toHash = new byte[total];
            System.arraycopy(saltBytes, 0, toHash, 0, numSaltBytes);
            System.arraycopy(digestedPwdBytes, 0, toHash, numSaltBytes, numDigestBytes);

            hash = this.digest.digest(toHash);
        }

        final Login newLogin = new Login(username, salt, hash, roles);

        this.logins.put(username, newLogin);
        store();

        if (newLogin.isAdmin()) {
            this.valid = true;
        }

        return newLogin;
    }

    /**
     * Gets the login session with a specified session ID.
     *
     * @param sessionId the session ID
     * @return the login session;' {@code null} if none has the specified session ID
     */
    public Session getSession(final String sessionId) {

        return this.sessions.get(sessionId);
    }

    /**
     * Attempts to create a session with a username and password.  This tests whether  the username and password matches
     * a configured login record, and if so, creates and returns a new login session with that login record.
     *
     * @param username the username
     * @param password the password
     * @return the result
     */
    public LoginResult createSession(final String username, final String password) {

        LoginResult result;

        final Login login = this.logins.get(username);

        if (login == null) {
            result = new LoginResult("Invalid login.");
        } else {
            final String salt = login.getSalt();
            final byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
            final int numSaltBytes = saltBytes.length;

            final byte[] pwdBytes = password.getBytes(StandardCharsets.UTF_8);

            final byte[] hash;

            // MessageDigest is not thread-safe, so synchronize access to that object.

            synchronized (this.digest) {
                final byte[] digestedPwdBytes = this.digest.digest(pwdBytes);
                final int numDigestBytes = digestedPwdBytes.length;
                final int total = numSaltBytes + numDigestBytes;

                final byte[] toHash = new byte[total];
                System.arraycopy(saltBytes, 0, toHash, 0, numSaltBytes);
                System.arraycopy(digestedPwdBytes, 0, toHash, numSaltBytes, numDigestBytes);

                hash = this.digest.digest(toHash);
            }

            final byte[] expectHash = login.getHash();
            if (Arrays.equals(hash, expectHash)) {
                String sessionId = randomString(SESSION_ID_LEN);
                final Session sess;

                synchronized (this.sessions) {
                    while (this.sessions.containsKey(sessionId)) {
                        sessionId = randomString(SESSION_ID_LEN);
                    }
                    sess = new Session(sessionId, login);
                    this.sessions.put(sessionId, sess);
                }
                result = new LoginResult(sess);
            } else {
                result = new LoginResult("Invalid login.");
            }
        }

        return result;
    }

    /**
     * Generates a random string using the character set allowed for salt values.
     *
     * @param len the length of the string to generate
     * @return the generated string
     */
    private String randomString(final int len) {

        final StringBuilder builder = new StringBuilder(len);

        final int numChars = SALT_CHARS.length();
        for (int i = 0; i < len; ++i) {
            final int index = this.rnd.nextInt(numChars);
            final char ch = SALT_CHARS.charAt(index);
            builder.append(ch);
        }

        return builder.toString();
    }
}
