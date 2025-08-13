package dev.mathops.schema.mathops;

import dev.mathops.db.table.EFieldRole;
import dev.mathops.db.table.EFieldType;
import dev.mathops.db.table.Field;
import dev.mathops.db.table.FieldDef;
import dev.mathops.db.table.Table;

/**
 * The "Field" table specification within the "persistence" group of the "mathops" schema.
 *
 * <p>
 * Each row in this table represents a single field specification within an installed table.  Fields are defined via the
 * table implementation class, but this table provides a method by which they can be queried.  There may or may not be a
 * physical database able to back this table's data.  If not, data is synthesized from the set of installed tables when
 * queried.
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
        F_SCHEMA = new Field(new FieldDef("schema", EFieldType.STRING,
                "The name of the schema that contains the table."), EFieldRole.PARTITION_KEY);
        F_TABLE = new Field(new FieldDef("table", EFieldType.STRING,
                "The table name."), EFieldRole.PARTITION_KEY);
        F_FIELD = new Field(new FieldDef("field", EFieldType.STRING,
                "The field name"), EFieldRole.PARTITION_KEY);
        F_TYPE = new Field(new FieldDef("type", EFieldType.STRING,
                "The field's data type."), EFieldRole.NOT_NULL);
        F_ROLE = new Field(new FieldDef("role", EFieldType.STRING,
                "The field's role in the table."), EFieldRole.NOT_NULL);
        F_DESCRIPTION = new Field(new FieldDef("description", EFieldType.STRING,
                "A description of the field for presentation in automatically-generated documentation."),
                EFieldRole.NOT_NULL);

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
