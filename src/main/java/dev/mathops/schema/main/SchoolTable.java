package dev.mathops.schema.main;

import dev.mathops.db.table.EFieldRole;
import dev.mathops.db.table.EFieldType;
import dev.mathops.db.table.SelectionCriteria;
import dev.mathops.db.table.Table;
import dev.mathops.db.table.constraint.IntegerRangeConstraint;
import dev.mathops.db.table.constraint.StringLengthConstraint;
import dev.mathops.db.table.Field;
import dev.mathops.db.table.criteria.ENumericMatchType;
import dev.mathops.db.table.criteria.IntegerFieldCriterion;

/**
 * The "School" table specification within the "system" schema of the "main" tablespace.
 */
public final class SchoolTable extends Table {

    /** The table description (HTML). */
    private static final String DESCR = "<p>Each row stores information on a single K-12 school from which a "
            + "student graduated.  These are used to generate reports for K-12 schools on the performance or "
            + "readiness of their graduates.</p>";

    /** The school's CEEB code, unique among all K-12 schools. */
    private static final Field F_CEEB_CODE;

    /** The school name. */
    private static final Field F_NAME;

    /** The school's mailing address, suitable for use on an envelope addressed to the school. */
    private static final Field F_MAILING_ADDR;

    /** The city of the school's mailing address, suitable for use on an envelope addressed to the school. */
    private static final Field F_CITY;

    /**
     * The state (two-letter code) of the school's mailing address, suitable for use on an envelope addressed to the
     * school.
     */
    private static final Field F_STATE;

    /**
     * The zip code (5-digit or 5+4) of the school's mailing address, suitable for use on an envelope addressed to the
     * school.
     */
    private static final Field F_ZIP_CODE;

    /** The single instance. */
    public static final SchoolTable INSTANCE;

    static {
        F_CEEB_CODE = new Field("ceeb_code", EFieldType.INTEGER, EFieldRole.PARTITION_KEY,
                "The CEEB code of the high school.",
                new IntegerRangeConstraint("ceeb_code_range", 0, 999999));
        F_NAME = new Field("name", EFieldType.STRING, EFieldRole.NOT_NULL,
                "The school name.");
        F_MAILING_ADDR = new Field("mailing_addr", EFieldType.STRING, EFieldRole.NOT_NULL,
                "The school's mailing address, suitable for use on an envelope addressed to the school.",
                new StringLengthConstraint("mailing_addr_length", 1, 32));
        F_CITY = new Field("city", EFieldType.STRING, EFieldRole.NOT_NULL,
                "The city of the school's mailing address, suitable for use on an envelope addressed to the school.",
                new StringLengthConstraint("city_length", 1, 32));
        F_STATE = new Field("state", EFieldType.STRING, EFieldRole.NOT_NULL,
                "The state (two-letter code) of the school's mailing address, suitable for use on an envelope "
                        + "addressed to the school.",
                new StringLengthConstraint("state_length", 2, 2));
        F_ZIP_CODE = new Field("zip_code", EFieldType.STRING, EFieldRole.NOT_NULL,
                "The zip code (5-digit or 5+4) of the school's mailing address, suitable for use on an envelope "
                        + "addressed to the school.",
                new StringLengthConstraint("zip_code_length", 5, 10));

        INSTANCE = new SchoolTable();
    }

    /**
     * Constructs a new {@code SchoolTable}.
     */
    private SchoolTable() {

        super("main", "system", "school", DESCR, null, F_CEEB_CODE, F_NAME, F_MAILING_ADDR, F_CITY, F_STATE, F_ZIP_CODE);
    }

    /**
     * Creates a {@code SelectionCriteria} object that matches on a specified CEEB code.
     *
     * @param theCeebCode the CEEB code for which to query
     * @return the selection criteria
     */
    public SelectionCriteria mkCeebCodeQuery(final int theCeebCode) {

        final Integer ceebCodeObj = Integer.valueOf(theCeebCode);

        return new SelectionCriteria(this, new IntegerFieldCriterion(F_CEEB_CODE, ENumericMatchType.EXACT_IN,
                ceebCodeObj));
    }
}
