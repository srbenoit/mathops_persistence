package dev.mathops.schema;

import dev.mathops.persistence.EFieldRole;
import dev.mathops.persistence.EFieldType;
import dev.mathops.persistence.Field;
import dev.mathops.persistence.SelectionCriteria;
import dev.mathops.persistence.Table;
import dev.mathops.persistence.constraint.IntegerRangeConstraint;
import dev.mathops.persistence.criteria.ENumericMatchType;
import dev.mathops.persistence.criteria.IntegerFieldCriterion;

/**
 * The "Term" table specification within the "system" schema of the "main" tablespace.
 */
public final class TermTable extends Table {

    /** The table description (HTML). */
    private static final String DESCR = "<p>Each row stores the top-level configuration of a term.</p>"
            + "<p>Conceptually a sequence of terms is a non-overlapping sequence of date ranges with no gaps, where "
            + "one term begins the day after the prior term ended.  Exactly one term is 'active' (the term in which "
            + "the current date lies), and every term has an active index (0 for the active term, +1 for the next, "
            + "-1 for the prior, and so forth).</p>"
            + "<p>Each row in this table should map to a corresponding <code>termYYYYMM</code> tablespace, withe the "
            + "associated schemata.  When a new row is created, the corresponding tablespace (and its tables) should "
            + "be created.  When a row is deleted, the corresponding tablespace (and its tables) should be archived "
            + "to files then deleted.  Applications can query for the active term, then use that to select the "
            + "appropriate term tablespace for queries of term-specific data.</p>"
            + "<p>At the boundary between terms (midnight at the end of a term's last day), the <code>active "
            + "index</code> of all term rows should be incremented.</p>";

    /** Examples (HTML). */
    private static final String EXAMPLES = "<p>Under a typical \"fall, spring, summer\" academic year, if the current "
            + "year is 2023, and the fall term is active, there would be a \"Fall 2023\" term row with active index 0, "
            + "a \"Spring 2024\" term row with active index 1, a \"Summer, 2024\" term with active index 2, a "
            + "\"Summer 2023\" term with active index -1, and so forth.</p>";

    /**
     * Term IDs, which are six-digit integers of the form "202330" (Spring 2023), "202360" (Summer 2023), or
     * "202390" (Fall, 2023).
     */
    private static final Field F_TERM;

    /**
     * The first day of the term.  The start/end date ranges of terms should form a collection of non-overlapping
     * intervals that include all days; there should be no days that are not part of a term.
     */
    private static final Field F_START_DATE;

    /** The last day of the term. */
    private static final Field F_END_DATE;

    /**
     * The academic year, which is a four-digit integer of the form "2324", which would indicate the 2023/2024 academic
     * year, which includes Fall 2023, Spring 2024, and Summer 2024.
     */
    private static final Field F_ACADEMIC_YEAR;

    /**
     * The active index.  0 for the currently active term; +1 for the next term, +2 for the term after the +1 term,
     * -1 for the prior term, -2 for the term prior to the prior term, etc.  At each transition between terms, all
     * records are updated to increment their active index.
     */
    private static final Field F_ACTIVE_INDEX;

    /** The last day students may drop the course. */
    private static final Field F_DROP_DEADLINE;

    /** The last day students can withdraw from the course. */
    private static final Field F_WITHDRAW_DEADLINE;

    /** The single instance. */
    public static final TermTable INSTANCE;

    static {
        F_TERM = new Field("term", EFieldType.INTEGER, EFieldRole.PARTITION_KEY,
                "The term ID - in the form YYYYNN, where NN is 30 for Spring, 60 for Summer, 90 for Fall.",
                new IntegerRangeConstraint("term_id_range", 100000, 999999));
        F_START_DATE = new Field("start_date", EFieldType.LOCAL_DATE, EFieldRole.NOT_NULL,
                "The first day of the term (not the first day of classes).");
        F_END_DATE = new Field("end_date", EFieldType.LOCAL_DATE, EFieldRole.NOT_NULL,
                "The last day of the term (not the last day of classes).");
        F_ACADEMIC_YEAR = new Field("academic_year", EFieldType.INTEGER, EFieldRole.NOT_NULL,
                "The academic year, such as '2324' to indicate the 2023-2024 academic year.",
                new IntegerRangeConstraint("academic_year_range", 1000, 9999));
        F_ACTIVE_INDEX = new Field("active_index", EFieldType.LOCAL_DATE, EFieldRole.NOT_NULL,
                "The active index (0 for the current active term, +1 for the next term, -1 for the prior term, etc.).");
        F_DROP_DEADLINE = new Field("drop_deadline", EFieldType.LOCAL_DATE, EFieldRole.NOT_NULL,
                "The deadline date to drop courses this term.");
        F_WITHDRAW_DEADLINE = new Field("withdraw_deadline", EFieldType.LOCAL_DATE,
                EFieldRole.NOT_NULL, "The deadline date to withdraw from courses this term.");

        INSTANCE = new TermTable();
    }

    /**
     * Constructs a new {@code TermTable}.
     */
    private TermTable() {

        super("main.system", "term", DESCR, EXAMPLES, F_TERM, F_START_DATE, F_END_DATE, F_ACADEMIC_YEAR, F_ACTIVE_INDEX,
                F_DROP_DEADLINE, F_WITHDRAW_DEADLINE);
    }

    /**
     * Creates a {@code SelectionCriteria} object that matches on a specified term ID.
     *
     * @param theTermId the term ID for which to query
     * @return the selection criteria
     */
    public SelectionCriteria mkTermIdQuery(final int theTermId) {

        final Integer activeIndexObj = Integer.valueOf(theTermId);

        return new SelectionCriteria(this, new IntegerFieldCriterion(F_TERM, ENumericMatchType.EXACT_IN,
                activeIndexObj));
    }

    /**
     * Creates a {@code SelectionCriteria} object that matches on a specified active index.
     *
     * @param theActiveIndex the active index for which to query
     * @return the selection criteria
     */
    public SelectionCriteria mkActiveIndexQuery(final int theActiveIndex) {

        final Integer activeIndexObj = Integer.valueOf(theActiveIndex);

        return new SelectionCriteria(this, new IntegerFieldCriterion(F_ACTIVE_INDEX, ENumericMatchType.EXACT_IN,
                activeIndexObj));
    }
}
