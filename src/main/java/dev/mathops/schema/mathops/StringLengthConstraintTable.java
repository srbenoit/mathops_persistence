package dev.mathops.schema.mathops;

import dev.mathops.db.table.EFieldRole;
import dev.mathops.db.table.EFieldType;
import dev.mathops.db.table.Field;
import dev.mathops.db.table.FieldDef;
import dev.mathops.db.table.Table;

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
                                        + "field within a table. There cam be only one string length constraint on a " +
                                        "field.</p>";

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
        F_SCHEMA = new Field(new FieldDef("schema", EFieldType.STRING,
                "The name of the schema that contains the table."), EFieldRole.PARTITION_KEY);
        F_TABLE = new Field(new FieldDef("table", EFieldType.STRING,
                "The table name."), EFieldRole.PARTITION_KEY);
        F_FIELD = new Field(new FieldDef("field", EFieldType.STRING,
                "The field name"), EFieldRole.PARTITION_KEY);
        F_MIN_LENGTH = new Field(new FieldDef("min_length", EFieldType.INTEGER,
                "The minimum allowed string length, in UTF-16 code units."), EFieldRole.NOT_NULL);
        F_MAX_LENGTH = new Field(new FieldDef("max_length", EFieldType.INTEGER,
                "The maximum allowed string length, in UTF-16 code units."), EFieldRole.NOT_NULL);
        F_DESCRIPTION = new Field(new FieldDef("description", EFieldType.STRING,
                "A description of the constraint for presentation in automatically-generated documentation."),
                EFieldRole.NOT_NULL);

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
