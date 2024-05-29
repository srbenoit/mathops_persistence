package dev.mathops.schema.mathops;

import dev.mathops.persistence.EFieldRole;
import dev.mathops.persistence.EFieldType;
import dev.mathops.persistence.Field;
import dev.mathops.persistence.Table;

/**
 * The "StringLengthConstraint" table specification within the "persistence" group of the "mathops" schema.
 *
 * <p>
 * Each row in this table represents a single length constraint applied to a string field in an installed table. Fields
 * and their constraints are defined via the table implementation class, but this table provides a method by which they
 * can be queried. There may or may not be a physical database able to back this table's data. If not, data is
 * synthesized from the set of installed tables when queried.
 */
public final class StringLengthConstraintTable extends Table {

    /** The table description (HTML). */
    private static final String DESCR = "<p>Each row represents a single length constraint on a single string-valued "
            + "field within a table. There cam be only one string length constraint on a field.</p>";

    /** The schema name. */
    private static final Field F_SCHEMA;

    /** The table name. */
    private static final Field F_TABLE;

    /** The field name. */
    private static final Field F_FIELD;

    /** The minimum string length. */
    private static final Field F_MIN_LENGTH;

    /** The maximum string length. */
    private static final Field F_MAX_LENGTH;

    /** The constraint description (algorithmically generated). */
    private static final Field F_DESCRIPTION;

    /** The single instance. */
    public static final StringLengthConstraintTable INSTANCE;

    static {
        F_SCHEMA = new Field("schema", EFieldType.STRING, EFieldRole.PARTITION_KEY,
                "The name of the schema that contains the table.");
        F_TABLE = new Field("table", EFieldType.STRING, EFieldRole.PARTITION_KEY,
                "The table name.");
        F_FIELD = new Field("field", EFieldType.STRING, EFieldRole.PARTITION_KEY,
                "The field name");
        F_MIN_LENGTH = new Field("min_length", EFieldType.INTEGER, EFieldRole.NOT_NULL,
                "The minimum allowed string length, in UTF-16 code units.");
        F_MAX_LENGTH = new Field("max_length", EFieldType.INTEGER, EFieldRole.NOT_NULL,
                "The maximum allowed string length, in UTF-16 code units.");
        F_DESCRIPTION = new Field("description", EFieldType.STRING, EFieldRole.NOT_NULL,
                "A description of the constraint for presentation in automatically-generated documentation.");

        INSTANCE = new StringLengthConstraintTable();
    }

    /**
     * Constructs a new {@code StringLengthConstraintTable}.
     */
    private StringLengthConstraintTable() {

        super("mathops", "persistence", "string_length_constraint", DESCR, null, F_SCHEMA, F_TABLE, F_FIELD,
                F_MIN_LENGTH, F_MAX_LENGTH, F_DESCRIPTION);
    }
}
