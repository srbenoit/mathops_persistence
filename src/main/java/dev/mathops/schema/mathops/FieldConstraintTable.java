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
public final class FieldConstraintTable extends Table {

    /** The table description (HTML). */
    private static final String DESCR = "<p>Each row represents a single constraint on a single field within a table. "
            + "The constraint type must be unique across all constraints on a field, and the data type of the "
            + "constraint must match that of the field.</p>";

    /** The schema name. */
    private static final Field F_SCHEMA;

    /** The table name. */
    private static final Field F_TABLE;

    /** The field name. */
    private static final Field F_FIELD;

    /** The constraint type. */
    private static final Field F_TYPE;

    /** xxx. */
    private static final Field F_XXX;

    /** The constraint description (algorithmically generated). */
    private static final Field F_DESCRIPTION;

    /** The single instance. */
    public static final FieldConstraintTable INSTANCE;

    static {
        F_SCHEMA = new Field("schema", EFieldType.STRING, EFieldRole.PARTITION_KEY,
                "The name of the schema that contains the table.");
        F_TABLE = new Field("table", EFieldType.STRING, EFieldRole.PARTITION_KEY,
                "The table name.");
        F_FIELD = new Field("field", EFieldType.STRING, EFieldRole.PARTITION_KEY,
                "The field name");
        F_TYPE = new Field("type", EFieldType.STRING, EFieldRole.PARTITION_KEY,
                "The constraint type.");



        // TODO:

        F_XXX = new Field("role", EFieldType.STRING, EFieldRole.NOT_NULL,
                "The field's role in the table.");



        F_DESCRIPTION = new Field("description", EFieldType.STRING, EFieldRole.NOT_NULL,
                "A description of the constraint for presentation in automatically-generated documentation.");

        INSTANCE = new FieldConstraintTable();
    }

    /**
     * Constructs a new {@code FieldConstraintTable}.
     */
    private FieldConstraintTable() {

        super("mathops", "persistence", "constraint", DESCR, null, F_SCHEMA, F_TABLE, F_FIELD, F_TYPE, F_XXX,
                F_DESCRIPTION);
    }
}
