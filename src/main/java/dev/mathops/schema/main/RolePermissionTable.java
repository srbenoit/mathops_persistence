package dev.mathops.schema.main;

import dev.mathops.db.table.EFieldRole;
import dev.mathops.db.table.EFieldType;
import dev.mathops.db.table.FieldDef;
import dev.mathops.db.table.Table;
import dev.mathops.db.table.constraint.StringEnumeratedConstraint;
import dev.mathops.db.table.Field;

/**
 * The "RolePermission" table specification within the "system" schema of the "main" tablespace.
 */
public final class RolePermissionTable extends Table {

    /** The table description (HTML). */
    private static final String DESCR = "<p>Each row represents a permission level for a single activity granted to "
            + "a single role.  Greater integer values represent greater permissions levels; a role with permission "
            + "level 3 can perform all activities available to roles with lower permission levels.  Users may be "
            + "allowed to act as one or more roles, and when acting as a role, have that role's permissions.</p>";

    /** The role ID. */
    private static final Field F_ROLE_ID;

    /** The activity to which this permission level applies . */
    private static final Field F_ACTIVITY;

    /** The permission level granted to the role. */
    private static final Field F_PERM_LVL;

    /** The single instance. */
    public static final RolePermissionTable INSTANCE;

    static {
        F_ROLE_ID = new Field(new FieldDef("role_id", EFieldType.STRING,
                "The role ID."), EFieldRole.PARTITION_KEY);
        F_ACTIVITY = new Field(new FieldDef("activity", EFieldType.STRING,
                "The activity to which this permission level applies.",
                new StringEnumeratedConstraint("activity_value", "HOLD")), EFieldRole.NOT_NULL);
        F_PERM_LVL = new Field(new FieldDef("perm_lvl", EFieldType.INTEGER,
                "The permission level granted to the role."), EFieldRole.NOT_NULL);

        INSTANCE = new RolePermissionTable();
    }

    /**
     * Constructs a new {@code RolePermissionTable}.
     */
    private RolePermissionTable() {

        super("main", "system", "role_permission", DESCR, null, F_ROLE_ID, F_ACTIVITY, F_PERM_LVL);
    }
}
