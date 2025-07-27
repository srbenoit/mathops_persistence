package dev.mathops.schema.mathops;

import dev.mathops.db.table.EFieldRole;
import dev.mathops.db.table.EFieldType;
import dev.mathops.db.table.Field;
import dev.mathops.db.table.Table;

/**
 * The "Table" table specification within the "persistence" group of the "mathops" schema.
 *
 * <p>
 * Each row in this table represents an installed table specification.  Tables are defined via their implementation
 * class, but this table provides a method by which they can be queried.  There may or may not be a physical database
 * table to back this table's data.  If not, data is synthesized from the set of installed tables when queried.
 */
public final class TableTable extends Table {

    /** The table description (HTML). */
    private static final String DESCR = "<p>Each row stores the configuration of a single table.  The combination of "
            + "schema name and table name must be unique across all installed tables.  Table group name does not "
            + "take part in this primary key function, but is used to group tables within a schema for display.</p>";

    /** The schema name. */
    private static final Field F_SCHEMA;

    /** The table group name. */
    private static final Field F_GROUP;

    /** The table name. */
    private static final Field F_TABLE;

    /** The table description. */
    private static final Field F_DESCRIPTION;

    /** Optional examples of usages of this table. */
    private static final Field F_EXAMPLES;

    /** The single instance. */
    public static final TableTable INSTANCE;

    static {
        F_SCHEMA = new Field("schema", EFieldType.STRING, EFieldRole.PARTITION_KEY,
                "The name of the schema that contains the table.");
        F_GROUP = new Field("group", EFieldType.STRING, EFieldRole.NOT_NULL,
                "The table group name.");
        F_TABLE = new Field("table", EFieldType.STRING, EFieldRole.PARTITION_KEY,
                "The table name.");
        F_DESCRIPTION = new Field("description", EFieldType.STRING, EFieldRole.NOT_NULL,
                "A description of the table for presentation in automatically-generated documentation.");
        F_EXAMPLES = new Field("examples", EFieldType.STRING, EFieldRole.NULLABLE,
                "Optional examples of how this table is used.");

        INSTANCE = new TableTable();
    }

    /**
     * Constructs a new {@code TableTable}.
     */
    private TableTable() {

        super("mathops", "persistence", "table", DESCR, null, F_SCHEMA, F_GROUP, F_TABLE, F_DESCRIPTION, F_EXAMPLES);
    }
}
