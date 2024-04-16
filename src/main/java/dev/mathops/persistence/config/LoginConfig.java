package dev.mathops.persistence.config;

import java.util.HashSet;
import java.util.Set;

/**
 * The configuration of a user login.
 *
 * <p>
 * Implementations are defined in a JSON file "logins.json" in the configuration directory.
 *
 * <pre>
 * [
 *   {
 *     "login": "...login...",
 *     "roles": [ "...role name...", "...role name..." ]
 *   },
 *   ... additional login objects ...
 * ]
 * </pre>
 */
public class LoginConfig {

    /** The login name. */
    private final String name;

    /** The set of roles to which the login is assigned. */
    private final Set<RoleConfig> roles;

    /**
     * Constructs a new {@code LoginConfig}.
     *
     * @param theName the role name
     */
    LoginConfig(final String theName) {

        this.name = theName;
        this.roles = new HashSet<>(10);
    }
}
