package dev.mathops.schema.mathops;

import dev.mathops.db.table.EFieldRole;
import dev.mathops.db.table.EFieldType;
import dev.mathops.db.table.Field;
import dev.mathops.db.table.FieldDef;
import dev.mathops.db.table.Table;

/**
 * The "Schema" table specification within the "persistence" group of the "mathops" schema.
 *
 * <p>
 * Each row in this table represents a schema.  Tables are defined via their implementation class, but this table
 * provides a method by which they can be queried.  There may or may not be a physical database table to back this
 * table's data.  If not, data is synthesized from the set of installed tables when queried.
 */
public final class SchemaTable extends Table {

    /** The table description (HTML). */
    private static final String DESCR = "<p>Each row stores the configuration of a single schema.</p>";

    /** The schema name. */
    private static final Field F_SCHEMA;

    /** The table description. */
    private static final Field F_DESCRIPTION;

    /** The single instance. */
    public static final SchemaTable INSTANCE;

    static {
        F_SCHEMA = new Field(new FieldDef("schema", EFieldType.STRING,
                "The name of the schema."), EFieldRole.PARTITION_KEY);
        F_DESCRIPTION = new Field(new FieldDef("description", EFieldType.STRING,
                "A description of the schema for presentation in automatically-generated documentation."),
                EFieldRole.NOT_NULL);

        INSTANCE = new SchemaTable();
    }

    /**
     * Constructs a new {@code TableTable}.
     */
    private SchemaTable() {

        super("mathops", "persistence", "schema", DESCR, null, F_SCHEMA, F_DESCRIPTION);
    }
}
