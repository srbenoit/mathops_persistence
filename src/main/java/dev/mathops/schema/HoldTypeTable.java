package dev.mathops.schema;

import dev.mathops.persistence.EFieldRole;
import dev.mathops.persistence.EFieldType;
import dev.mathops.persistence.Field;
import dev.mathops.persistence.Table;
import dev.mathops.persistence.constraint.StringEnumeratedConstraint;
import dev.mathops.persistence.constraint.StringLengthConstraint;

/**
 * The "HoldType" table specification within the "system" schema of the "main" tablespace.
 */
public final class HoldTypeTable extends Table {

    /** The table description (HTML). */
    private static final String DESCR = "<p>Each row represents a type of administrative hold that can be applied "
            + "to a user's account.</p>";

    /** Examples (HTML). */
    private static final String EXAMPLES = "<p>A student's who has borrowed a resource but not returned it by its "
            + "due date may receive a hold that prevents further course work until the resource is returned.  "
            + "Holds are generally applied by nightly automated processes, so removing a hold is a temporary fix "
            + "unless the underlying cause of the hold is addressed.</p>";

    /** The hold ID. */
    private static final Field F_HOLD_ID;

    /** The hold severity, which controls the actions this hold prevents. */
    private static final Field F_SEVERITY;

    /** A category for the hold, such as 'registration', 'resource', or 'disciplinary'. */
    private static final Field F_HOLD_CATEGORY;

    /** The required clearance level of a logged-in user in order to add a hold. */
    private static final Field F_ADD_PERM_LVL;

    /** The required clearance level of a logged-in user in order to remove a hold. */
    private static final Field F_REMOVE_PERM_LVL;

    /** The required clearance level of a logged-in user in order to suspend (temporarily remove) a hold. */
    private static final Field F_SUSPEND_PERM_LVL;

    /**
     * The zip code (5-digit or 5+4) of the school's mailing address, suitable for use on an envelope addressed to the
     * school.
     */
    private static final Field F_TIMES_TO_DISPLAY;

    /** The single instance. */
    public static final HoldTypeTable INSTANCE;

    static {
        F_HOLD_ID = new Field("hold_id", EFieldType.STRING, EFieldRole.PARTITION_KEY,
                "A unique ID for each hold, 2 characters in length.",
                new StringLengthConstraint("hold_id_length", 2, 2));
        F_SEVERITY = new Field("severity", EFieldType.STRING, EFieldRole.NOT_NULL,
                "The hold severity, which controls the actions this hold prevents.",
                new StringLengthConstraint("severity_length", 1, 1),
                new StringEnumeratedConstraint("severity_value", "F", "N"));
        F_HOLD_CATEGORY = new Field("hold_category", EFieldType.STRING, EFieldRole.NOT_NULL,
                "A category for the hold, such as 'registration', 'resource', or 'disciplinary'.",
                new StringLengthConstraint("hold_type_length", 1, 32));
        F_ADD_PERM_LVL = new Field("add_perm_lvl", EFieldType.INTEGER, EFieldRole.NOT_NULL,
                "The required permission level in the 'HOLD' activity a logged in user must have in order to add a "
                        + "hold of this type.");
        F_REMOVE_PERM_LVL = new Field("remove_perm_lvl", EFieldType.INTEGER, EFieldRole.NOT_NULL,
                "The required permission level in the 'HOLD' activity a logged in user must have in order to remove "
                        + "a hold of this type.");
        F_SUSPEND_PERM_LVL = new Field("suspend_perm_lvl", EFieldType.INTEGER, EFieldRole.NOT_NULL,
                "The required permission level in the 'HOLD' activity a logged in user must have in order to "
                        + "suspend (temporarily remove) a hold of this type.");
        F_TIMES_TO_DISPLAY = new Field("times_to_display", EFieldType.INTEGER, EFieldRole.NULLABLE,
                "The number of times to display a hold of this type to a student before automatically removing it "
                        + "(null if the hold is never automatically removed).");

        INSTANCE = new HoldTypeTable();
    }

    /**
     * Constructs a new {@code HoldTypeTable}.
     */
    private HoldTypeTable() {

        super("main.system", "hold_type", DESCR, EXAMPLES, F_HOLD_ID, F_SEVERITY, F_HOLD_CATEGORY, F_ADD_PERM_LVL,
                F_REMOVE_PERM_LVL, F_SUSPEND_PERM_LVL, F_TIMES_TO_DISPLAY);
    }
}
