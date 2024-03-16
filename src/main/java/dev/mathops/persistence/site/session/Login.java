package dev.mathops.persistence.site.session;

import dev.mathops.commons.builder.SimpleBuilder;
import dev.mathops.commons.parser.HexEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * A single login configuration loaded from the "login.json" file in the configuration directory.  The format of that
 * file is:
 *
 * <pre>
 * [
 *    ["username1", "salt1", "sha512hash1_hex", "role1"],
 *    ["username2", "salt2", "sha512hash2_hex", "role1,role2"],
 *    ...
 * ]
 * </pre>
 * <p>
 * The last field is a comma-separated list of roles for which the user is authorized.  Entries in the list should be
 * one of the following:
 * <ul>
 *     <li>ADMIN (can manage users and passwords)</li>
 *     <li>DBA (can create, alter, and delete schema and table definitions, backup and recover)</li>
 *     <li>MODIFIER (update, insert, and delete all data)</li>
 *     <li>VIEWER (can query all data)</li>
 *     <li>(other named roles that don't collide with the above, with permissions defined in "roles.json")</li>
 * </ul>
 */
public final class Login {

    /** The ADMIN role. */
    private static final String ADMIN = "ADMIN";

    /** The ADMIN role. */
    private static final String DBA = "DBA";

    /** The ADMIN role. */
    private static final String DATA_OWNER = "DATA_OWNER";

    /** The ADMIN role. */
    private static final String DATA_VIEWER = "DATA_VIEWER";

    /** The username. */
    private final String username;

    /** The salt value (a 20-character random string). */
    private final String salt;

    /** The SHA-512 hash of concatenation of the salt and the SHA-512 hash of the password. */
    private final byte[] sha512Hash;

    /** The list of roles for which the user is authorized. */
    private final List<String> roles;

    /**
     * Constructs a new {@code Login}.
     *
     * @param theUsername   the username
     * @param theSalt       the salt value (a 20-character random string)
     * @param theSha512Hash the SHA-512 hash of concatenation of the salt and the SHA-512 hash of the password
     * @param theRoles      the list of roles for which the user is authorized
     */
    Login(final String theUsername, final String theSalt, final byte[] theSha512Hash, final List<String> theRoles) {

        this.username = theUsername;
        this.salt = theSalt;
        this.sha512Hash = theSha512Hash.clone();
        this.roles = new ArrayList<>(theRoles);
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {

        return this.username;
    }

    /**
     * Gets the salt value.
     *
     * @return the salt value
     */
    String getSalt() {

        return this.salt;
    }

    /**
     * Gets the 64-byte hash.
     *
     * @return the hash
     */
    byte[] getHash() {

        return this.sha512Hash.clone();
    }


    /**
     * Gets the hexadecimal representation of the 64-byte hash.  This will be a 128-character string.
     *
     * @return the hex
     */
    String getHashHex() {

        return HexEncoder.encodeUppercase(this.sha512Hash);
    }

    /**
     * Gets the list of roles for which the login is authorized.
     *
     * @return the list of roles
     */
    List<String> getRoles() {

        return new ArrayList<>(this.roles);
    }

    /**
     * Tests whether this login has the ADMIN role.
     *
     * @return true if the login has the ADMIN role
     */
    boolean isAdmin() {

        return this.roles.contains(ADMIN);
    }

    /**
     * Tests whether this login has the DBA role.
     *
     * @return true if the login has the DBA role
     */
    boolean isDba() {

        return this.roles.contains(DBA);
    }

    /**
     * Tests whether this login has the DATA_OWNER role.
     *
     * @return true if the login has the DATA_OWNER role
     */
    boolean isDataOwner() {

        return this.roles.contains(DATA_OWNER);
    }

    /**
     * Tests whether this login has the DATA_VIEWER role.
     *
     * @return true if the login has the DATA_VIEWER role
     */
    boolean isDataViewer() {

        return this.roles.contains(DATA_VIEWER);
    }

    /**
     * Tests whether this login has a specified role.
     *
     * @param theRole the role for which to test
     * @return true if the login has the specified role
     */
    boolean isRole(final String theRole) {

        return this.roles.contains(theRole);
    }

    /**
     * Generates a diagnostic string representation of the object.
     *
     * @return the string representation
     */
    @Override
    public String toString() {

        final String hex = HexEncoder.encodeUppercase(this.sha512Hash);

        return SimpleBuilder.concat("Login{username='", this.username, "', salt='", this.salt, "', sha512Hash=", hex,
                ", roles=", this.roles, "}");
    }
}
