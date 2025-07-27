package dev.mathops.schema.main;

import dev.mathops.db.table.EFieldRole;
import dev.mathops.db.table.EFieldType;
import dev.mathops.db.table.Field;
import dev.mathops.db.table.Table;

/**
 * The "Role" table specification within the "system" schema of the "main" tablespace.
 */
public final class RoleTable extends Table {

    /** The table description (HTML). */
    private static final String DESCR = "<p>Each row represents a role to which users may be assigned.  Permissions "
            + "are defined for roles, then users are granted roles under which they can work.  To give unique "
            + "permissions to a single user, a new role can be defined and granted to only that user.</p>";

    /** The role ID. */
    private static final Field F_ROLE_ID;

    /** The role name. */
    private static final Field F_ROLE_NAME;

    /** The single instance. */
    public static final RoleTable INSTANCE;

    static {
        F_ROLE_ID = new Field("role_id", EFieldType.STRING, EFieldRole.PARTITION_KEY,
                "The role ID.");
        F_ROLE_NAME = new Field("role_name", EFieldType.STRING, EFieldRole.NOT_NULL,
                "The role name.");

        INSTANCE = new RoleTable();
    }

    /**
     * Constructs a new {@code RolePermissionTable}.
     */
    private RoleTable() {

        super("main", "system", "role", DESCR, null, F_ROLE_ID, F_ROLE_NAME);
    }
}
