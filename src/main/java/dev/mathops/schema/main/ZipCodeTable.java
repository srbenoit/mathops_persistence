package dev.mathops.schema.main;

import dev.mathops.db.table.EFieldRole;
import dev.mathops.db.table.EFieldType;
import dev.mathops.db.table.Field;
import dev.mathops.db.table.FieldDef;
import dev.mathops.db.table.Table;
import dev.mathops.db.table.constraint.StringLengthConstraint;

/**
 * The "ZipCode" table specification within the "system" schema of the "main" tablespace.
 */
public final class ZipCodeTable extends Table {

    /** The table description (HTML). */
    private static final String DESCR = "<p>A zip code with the associated city and state.</p>";

    /** The zip code (5-digit or 5+4). */
    private static final Field F_ZIP_CODE;

    /** The city. */
    private static final Field F_CITY;

    /** The state (two-letter code). */
    private static final Field F_STATE;

    /** The single instance. */
    public static final ZipCodeTable INSTANCE;

    static {
        F_ZIP_CODE = new Field(new FieldDef("zip_code", EFieldType.STRING,
                "The zip code (5-digit or 5+4).",
                new StringLengthConstraint("zip_code_length", 5, 10)), EFieldRole.NOT_NULL);
        F_CITY = new Field(new FieldDef("city", EFieldType.STRING,
                "The city.",
                new StringLengthConstraint("city_length", 1, 32)), EFieldRole.NOT_NULL);
        F_STATE = new Field(new FieldDef("state", EFieldType.STRING,
                "The state.",
                new StringLengthConstraint("state_length", 2, 2)), EFieldRole.NOT_NULL);

        INSTANCE = new ZipCodeTable();
    }

    /**
     * Constructs a new {@code TermTable}.
     */
    private ZipCodeTable() {

        super("main", "system", "zip_code", DESCR, null, F_ZIP_CODE, F_CITY, F_STATE);
    }
}
