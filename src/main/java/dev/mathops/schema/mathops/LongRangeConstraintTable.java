package dev.mathops.schema.mathops;

import dev.mathops.persistence.EFieldRole;
import dev.mathops.persistence.EFieldType;
import dev.mathops.persistence.Field;
import dev.mathops.persistence.Table;

/**
 * The "LongRangeConstraint" table specification within the "persistence" group of the "mathops" schema.
 *
 * <p>
 * Each row in this table represents a single range constraint applied to a long field in an installed table. Fields
 * and their constraints are defined via the table implementation class, but this table provides a method by which they
 * can be queried. There may or may not be a physical database able to back this table's data. If not, data is
 * synthesized from the set of installed tables when queried.
 */
public final class LongRangeConstraintTable extends Table {

    /** The table description (HTML). */
    private static final String DESCR = "<p>Each row represents a single range constraint on a single long-valued "
            + "field within a table. There cam be only one long range constraint on a field.</p>";

    /** The schema name. */
    private static final Field F_SCHEMA;

    /** The table name. */
    private static final Field F_TABLE;

    /** The field name. */
    private static final Field F_FIELD;

    /** The minimum Long value. */
    private static final Field F_MIN_VALUE;

    /** The maximum Long value. */
    private static final Field F_MAX_VALUE;

    /** The constraint description (algorithmically generated). */
    private static final Field F_DESCRIPTION;

    /** The single instance. */
    public static final LongRangeConstraintTable INSTANCE;

    static {
        F_SCHEMA = new Field("schema", EFieldType.STRING, EFieldRole.PARTITION_KEY,
                "The name of the schema that contains the table.");
        F_TABLE = new Field("table", EFieldType.STRING, EFieldRole.PARTITION_KEY,
                "The table name.");
        F_FIELD = new Field("field", EFieldType.STRING, EFieldRole.PARTITION_KEY,
                "The field name");
        F_MIN_VALUE = new Field("min_value", EFieldType.LONG, EFieldRole.NOT_NULL,
                "The minimum allowed value.");
        F_MAX_VALUE = new Field("max_value", EFieldType.LONG, EFieldRole.NOT_NULL,
                "The maximum allowed value.");
        F_DESCRIPTION = new Field("description", EFieldType.STRING, EFieldRole.NOT_NULL,
                "A description of the constraint for presentation in automatically-generated documentation.");

        INSTANCE = new LongRangeConstraintTable();
    }

    /**
     * Constructs a new {@code LongRangeConstraint}.
     */
    private LongRangeConstraintTable() {

        super("mathops", "persistence", "long_range_constraint", DESCR, null, F_SCHEMA, F_TABLE, F_FIELD, F_MIN_VALUE,
                F_MAX_VALUE, F_DESCRIPTION);
    }
}
