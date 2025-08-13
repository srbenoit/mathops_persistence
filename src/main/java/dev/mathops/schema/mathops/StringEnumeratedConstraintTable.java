package dev.mathops.schema.mathops;

import dev.mathops.db.table.EFieldRole;
import dev.mathops.db.table.EFieldType;
import dev.mathops.db.table.Field;
import dev.mathops.db.table.FieldDef;
import dev.mathops.db.table.Table;

/**
 * The "StringEnumeratedConstraint" table specification within the "persistence" group of the "mathops" schema.
 *
 * <p>
 * Each row in this table represents a single allowed enumerated value for a string field in an installed table. The
 * existence of at least one such constraint indicates field values may only take on an allowed set of enumerated string
 * values, which are given by the set of all such constraint rows. Fields and their constraints are defined via the
 * table implementation class, but this table provides a method by which they can be queried. There may or may not be a
 * physical database able to back this table's data. If not, data is synthesized from the set of installed tables when
 * queried.
 */
public final class StringEnumeratedConstraintTable extends Table {

    /** The table description (HTML). */
    private static final String DESCR = "<p>Each row represents a single allowed enumerated value of a single "
                                        + "string-valued field within a table. There can be many such constraints on " +
                                        "a field. The existence of "
                                        + "at least one such constraint indicates field values may only take on an " +
                                        "allowed set of enumerated "
                                        + "string values, which are given by the set of all such constraint rows</p>";

    /** The schema name. */
    private static final Field F_SCHEMA;

    /** The table name. */
    private static final Field F_TABLE;

    /** The field name. */
    private static final Field F_FIELD;

    /** The allowed string value. */
    private static final Field F_ALLOWED_VALUE;

    /** The constraint description (algorithmically generated). */
    private static final Field F_DESCRIPTION;

    /** The single instance. */
    public static final StringEnumeratedConstraintTable INSTANCE;

    static {
        F_SCHEMA = new Field(new FieldDef("schema", EFieldType.STRING,
                "The name of the schema that contains the table."), EFieldRole.PARTITION_KEY);
        F_TABLE = new Field(new FieldDef("table", EFieldType.STRING,
                "The table name."), EFieldRole.PARTITION_KEY);
        F_FIELD = new Field(new FieldDef("field", EFieldType.STRING,
                "The field name"), EFieldRole.NOT_NULL);
        F_ALLOWED_VALUE = new Field(new FieldDef("allowed_value", EFieldType.STRING,
                "The allowed value."), EFieldRole.NOT_NULL);
        F_DESCRIPTION = new Field(new FieldDef("description", EFieldType.STRING,
                "A description of the constraint for presentation in automatically-generated documentation."),
                EFieldRole.NOT_NULL);

        INSTANCE = new StringEnumeratedConstraintTable();
    }

    /**
     * Constructs a new {@code StringEnumeratedConstraintTable}.
     */
    private StringEnumeratedConstraintTable() {

        super("mathops", "persistence", "string_enumerated_constraint", DESCR, null, F_SCHEMA, F_TABLE, F_FIELD,
                F_ALLOWED_VALUE, F_DESCRIPTION);
    }
}
