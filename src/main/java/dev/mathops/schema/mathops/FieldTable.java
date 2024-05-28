package dev.mathops.schema.mathops;

import dev.mathops.persistence.EFieldRole;
import dev.mathops.persistence.EFieldType;
import dev.mathops.persistence.Field;
import dev.mathops.persistence.Table;

/**
 * The "Field" table specification within the "persistence" group of the "mathops" schema.
 *
 * <p>
 * Each row in this table represents a single field specification within an installed table.  Fields are defined via
 * the table implementation class, but this table provides a method by which they can be queried.  There may or may not
 * be a physical database able to back this table's data.  If not, data is synthesized from the set of installed tables
 * when queried.
 */
public final class FieldTable extends Table {

    /** The table description (HTML). */
    private static final String DESCR = "<p>Each row represents a single field within a table.  The field name must be "
            + "unique across all fields in a table.</p>";

    /** The schema name. */
    private static final Field F_SCHEMA;

    /** The table name. */
    private static final Field F_TABLE;

    /** The field name. */
    private static final Field F_FIELD;

    /** The field data type. */
    private static final Field F_TYPE;

    /** The field's role in the table. */
    private static final Field F_ROLE;

    /** The field description. */
    private static final Field F_DESCRIPTION;

    /** The single instance. */
    public static final FieldTable INSTANCE;

    static {
        F_SCHEMA = new Field("schema", EFieldType.STRING, EFieldRole.PARTITION_KEY,
                "The name of the schema that contains the table.");
        F_TABLE = new Field("table", EFieldType.STRING, EFieldRole.PARTITION_KEY,
                "The table name.");
        F_FIELD = new Field("field", EFieldType.STRING, EFieldRole.PARTITION_KEY,
                "The field name");
        F_TYPE = new Field("type", EFieldType.STRING, EFieldRole.NOT_NULL,
                "The field's data type.");
        F_ROLE = new Field("role", EFieldType.STRING, EFieldRole.NOT_NULL,
                "The field's role in the table.");
        F_DESCRIPTION = new Field("description", EFieldType.STRING, EFieldRole.NOT_NULL,
                "A description of the field for presentation in automatically-generated documentation.");

        INSTANCE = new FieldTable();
    }

    /**
     * Constructs a new {@code FieldTable}.
     */
    private FieldTable() {

        super("mathops", "persistence", "field", DESCR, null, F_SCHEMA, F_TABLE, F_FIELD, F_TYPE, F_ROLE,
                F_DESCRIPTION);
    }
}
