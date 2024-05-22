package dev.mathops.schema.main;

import dev.mathops.persistence.EFieldRole;
import dev.mathops.persistence.EFieldType;
import dev.mathops.persistence.Field;
import dev.mathops.persistence.Table;

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
        F_NAME = new Field("name", EFieldType.STRING, EFieldRole.PARTITION_KEY,
                "The name of the parameter set.");
        F_STRING_1 = new Field("string_1", EFieldType.STRING, EFieldRole.NULLABLE,
                "The first string parameter value.");
        F_STRING_2 = new Field("string_2", EFieldType.STRING, EFieldRole.NULLABLE,
                "The first string parameter value.");
        F_STRING_3 = new Field("string_3", EFieldType.STRING, EFieldRole.NULLABLE,
                "The first string parameter value.");
        F_STRING_4 = new Field("string_4", EFieldType.STRING, EFieldRole.NULLABLE,
                "The first string parameter value.");
        F_STRING_5 = new Field("string_5", EFieldType.STRING, EFieldRole.NULLABLE,
                "The first string parameter value.");
        F_INTEGER_1 = new Field("integer_1", EFieldType.INTEGER, EFieldRole.NULLABLE,
                "The first integer parameter value.");
        F_INTEGER_2 = new Field("integer_2", EFieldType.INTEGER, EFieldRole.NULLABLE,
                "The first integer parameter value.");
        F_INTEGER_3 = new Field("integer_3", EFieldType.INTEGER, EFieldRole.NULLABLE,
                "The first integer parameter value.");
        F_INTEGER_4 = new Field("integer_4", EFieldType.INTEGER, EFieldRole.NULLABLE,
                "The first integer parameter value.");
        F_INTEGER_5 = new Field("integer_5", EFieldType.INTEGER, EFieldRole.NULLABLE,
                "The first integer parameter value.");
        F_DATE_1 = new Field("date_1", EFieldType.LOCAL_DATE, EFieldRole.NULLABLE,
                "The first date parameter value.");
        F_DATE_2 = new Field("date_2", EFieldType.LOCAL_DATE, EFieldRole.NULLABLE,
                "The second date parameter value.");
        F_TIME_1 = new Field("time_1", EFieldType.LOCAL_TIME, EFieldRole.NULLABLE,
                "The first time parameter value.");
        F_TIME_2 = new Field("time_2", EFieldType.LOCAL_TIME, EFieldRole.NULLABLE,
                "The second time parameter value.");

        INSTANCE = new ParameterTable();
    }

    /**
     * Constructs a new {@code ParameterTable}.
     */
    private ParameterTable() {

        super("main.system", "parameter", DESCR, null, F_NAME, F_STRING_1, F_STRING_2, F_STRING_3, F_STRING_4,
                F_STRING_5, F_INTEGER_1, F_INTEGER_2, F_INTEGER_3, F_INTEGER_4, F_INTEGER_5, F_DATE_1, F_DATE_2,
                F_TIME_1, F_TIME_2);
    }
}
