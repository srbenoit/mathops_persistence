package dev.mathops.schema.term;

import dev.mathops.persistence.EFieldRole;
import dev.mathops.persistence.EFieldType;
import dev.mathops.persistence.Field;
import dev.mathops.persistence.Table;

/**
 * The "Term"Week table specification within the "system" schema of the "term*" tablespace.
 */
public final class TermWeekTable extends Table {

    /** The table description (HTML). */
    private static final String DESCR = "<p>Each row stores the date ranges for a single \"week\" within a term. "
            + "These ranges need not correspond exactly to calendar weeks, but are used when reporting in which week "
            + "a student's work occurred.  The weeks of a term must cover all days contained in the term, but may not "
            + "overlap (every calendar day must uniquely identify a term and a week number within that term).</p>";

    /** Examples (HTML). */
    private static final String EXAMPLES = "<p>Week 0 could represent any days from the start of the term to the first "
            + "day of the first week of class.  Then weeks 1 through 15 would be the actual weeks of the class (with "
            + "each week running from Sunday through Saturday), but week 10 could span 14 days since it covers "
            + "\"Spring Break\".  Then a week 16 could cover any remaining days from the day after the last day of "
            + "classes to the end of the term.</p>";

    /** The week number. */
    private static final Field F_WEEK_NBR;

    /** The first day of the week. */
    private static final Field F_START_DATE;

    /** The last day of the week. */
    private static final Field F_END_DATE;

    /** The single instance. */
    public static final TermWeekTable INSTANCE;

    static {
        F_WEEK_NBR = new Field("week_nbr", EFieldType.INTEGER, EFieldRole.PARTITION_KEY,
                "The week number.");
        F_START_DATE = new Field("start_date", EFieldType.LOCAL_DATE, EFieldRole.NOT_NULL,
                "The first day of the week.");
        F_END_DATE = new Field("end_date", EFieldType.LOCAL_DATE, EFieldRole.NOT_NULL,
                "The last day of the week.");

        INSTANCE = new TermWeekTable();
    }

    /**
     * Constructs a new {@code TermWeekTable}.
     */
    private TermWeekTable() {

        super("term", "system", "term_week", DESCR, EXAMPLES, F_WEEK_NBR, F_START_DATE, F_END_DATE);
    }
}
