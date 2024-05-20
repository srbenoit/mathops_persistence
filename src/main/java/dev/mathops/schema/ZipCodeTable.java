package dev.mathops.schema;

import dev.mathops.persistence.EFieldRole;
import dev.mathops.persistence.EFieldType;
import dev.mathops.persistence.Field;
import dev.mathops.persistence.Table;
import dev.mathops.persistence.constraint.StringLengthConstraint;
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
        F_ZIP_CODE = new Field("zip_code", EFieldType.STRING, EFieldRole.NOT_NULL,
                "The zip code (5-digit or 5+4).",
                new StringLengthConstraint("zip_code_length", 5, 10));
        F_CITY = new Field("city", EFieldType.STRING, EFieldRole.NOT_NULL,
                "The city.",
                new StringLengthConstraint("city_length", 1, 32));
        F_STATE = new Field("state", EFieldType.STRING, EFieldRole.NOT_NULL,
                "The state.",
                new StringLengthConstraint("state_length", 2, 2));

        INSTANCE = new ZipCodeTable();
    }

    /**
     * Constructs a new {@code TermTable}.
     */
    private ZipCodeTable() {

        super("main.system", "zip_code", DESCR, null, F_ZIP_CODE, F_CITY, F_STATE);
    }
}
