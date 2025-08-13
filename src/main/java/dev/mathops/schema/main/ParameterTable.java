package dev.mathops.schema.main;

import dev.mathops.db.table.EFieldRole;
import dev.mathops.db.table.EFieldType;
import dev.mathops.db.table.Field;
import dev.mathops.db.table.FieldDef;
import dev.mathops.db.table.Table;

/**
 * The "Parameter" table specification within the "system" schema of the "main" tablespace.
 */
public final class ParameterTable extends Table {

    /** The table description (HTML). */
    private static final String DESCR = "<p>Each row represents a named collection of parameter values that can "
                                        + "control operation of system functions like batch jobs, reports, etc.</p>";

    /** The role ID. */
    private static final Field F_NAME;

    /** The first string parameter value. */
    private static final Field F_STRING_1;

    /** The second string parameter value. */
    private static final Field F_STRING_2;

    /** The third string parameter value. */
    private static final Field F_STRING_3;

    /** The fourth string parameter value. */
    private static final Field F_STRING_4;

    /** The fifth string parameter value. */
    private static final Field F_STRING_5;

    /** The first integer parameter value. */
    private static final Field F_INTEGER_1;

    /** The second integer parameter value. */
    private static final Field F_INTEGER_2;

    /** The third integer parameter value. */
    private static final Field F_INTEGER_3;

    /** The fourth integer parameter value. */
    private static final Field F_INTEGER_4;

    /** The fifth integer parameter value. */
    private static final Field F_INTEGER_5;

    /** The first date parameter value. */
    private static final Field F_DATE_1;

    /** The second date parameter value. */
    private static final Field F_DATE_2;

    /** The first time parameter value. */
    private static final Field F_TIME_1;

    /** The second time parameter value. */
    private static final Field F_TIME_2;

    /** The single instance. */
    public static final ParameterTable INSTANCE;

    static {
        F_NAME = new Field(new FieldDef("name", EFieldType.STRING,
                "The name of the parameter set."), EFieldRole.PARTITION_KEY);
        F_STRING_1 = new Field(new FieldDef("string_1", EFieldType.STRING,
                "The first string parameter value."), EFieldRole.NULLABLE);
        F_STRING_2 = new Field(new FieldDef("string_2", EFieldType.STRING,
                "The first string parameter value."), EFieldRole.NULLABLE);
        F_STRING_3 = new Field(new FieldDef("string_3", EFieldType.STRING,
                "The first string parameter value."), EFieldRole.NULLABLE);
        F_STRING_4 = new Field(new FieldDef("string_4", EFieldType.STRING,
                "The first string parameter value."), EFieldRole.NULLABLE);
        F_STRING_5 = new Field(new FieldDef("string_5", EFieldType.STRING,
                "The first string parameter value."), EFieldRole.NULLABLE);
        F_INTEGER_1 = new Field(new FieldDef("integer_1", EFieldType.INTEGER,
                "The first integer parameter value."), EFieldRole.NULLABLE);
        F_INTEGER_2 = new Field(new FieldDef("integer_2", EFieldType.INTEGER,
                "The first integer parameter value."), EFieldRole.NULLABLE);
        F_INTEGER_3 = new Field(new FieldDef("integer_3", EFieldType.INTEGER,
                "The first integer parameter value."), EFieldRole.NULLABLE);
        F_INTEGER_4 = new Field(new FieldDef("integer_4", EFieldType.INTEGER,
                "The first integer parameter value."), EFieldRole.NULLABLE);
        F_INTEGER_5 = new Field(new FieldDef("integer_5", EFieldType.INTEGER,
                "The first integer parameter value."), EFieldRole.NULLABLE);
        F_DATE_1 = new Field(new FieldDef("date_1", EFieldType.LOCAL_DATE,
                "The first date parameter value."), EFieldRole.NULLABLE);
        F_DATE_2 = new Field(new FieldDef("date_2", EFieldType.LOCAL_DATE,
                "The second date parameter value."), EFieldRole.NULLABLE);
        F_TIME_1 = new Field(new FieldDef("time_1", EFieldType.LOCAL_TIME,
                "The first time parameter value."), EFieldRole.NULLABLE);
        F_TIME_2 = new Field(new FieldDef("time_2", EFieldType.LOCAL_TIME,
                "The second time parameter value."), EFieldRole.NULLABLE);

        INSTANCE = new ParameterTable();
    }

    /**
     * Constructs a new {@code ParameterTable}.
     */
    private ParameterTable() {

        super("main", "system", "parameter", DESCR, null, F_NAME, F_STRING_1, F_STRING_2, F_STRING_3, F_STRING_4,
                F_STRING_5, F_INTEGER_1, F_INTEGER_2, F_INTEGER_3, F_INTEGER_4, F_INTEGER_5, F_DATE_1, F_DATE_2,
                F_TIME_1, F_TIME_2);
    }
}
