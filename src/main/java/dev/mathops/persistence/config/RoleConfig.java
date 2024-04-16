package dev.mathops.persistence.config;

import java.util.HashMap;
import java.util.Map;

/**
 * The configuration of a role.
 *
 * <p>
 * Roles are defined in a JSON file "roles.json" in the configuration directory.
 *
 * <pre>
 * [
 *   {
 *     "role-name": "...role name...",
 *     "schemas": [
 *       {
 *         "name": "...schema name...",
 *         "tables": [
 *           {
 *             "name": "...table name...",
 *             "allow": ["count", "query", "insert", "delete", "update"]
 *           }
 *         ]
 *       }
 *     ]
 *   },
 *   ... additional role objects ...
 * ]
 * </pre>
 */
public class RoleConfig {

    /** The role name. */
    private final String name;

    /** A map from schema name to a map from table name to the permissions for that table. */
    private final Map<String, Map<String, TablePermissions>> tablePermissions;

    /**
     * Constructs a new {@code RoleConfig}.
     *
     * @param theName the role name
     */
    RoleConfig(final String theName) {

        this.name = theName;
        this.tablePermissions = new HashMap<>(10);
    }
}
