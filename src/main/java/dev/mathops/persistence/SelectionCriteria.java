package dev.mathops.persistence;

import dev.mathops.commons.builder.SimpleBuilder;
import dev.mathops.persistence.criteria.AbstractFieldCriterion;

import java.util.Arrays;

/**
 * An immutable definition of query criteria to select rows from a table.
 *
 * <p>
 * When performing queries, updates, deletes, and counts, applications can provide selection criteria to determine which
 * rows from a table are considered.  This is provided to the database layer in the form of an instance of this class.
 *
 * <p>
 * This class stores a reference to the table being queried, as well as an array of zero or more field criterion
 * objects. If no field criterion objects are provided, every row in the table is to be considered.  If one or more
 * criterion objects are provided, only rows that satisfy ALL supplied criteria will be considered.
 *
 * <p>
 * Field criterion objects are concrete subclasses of the {@link AbstractFieldCriterion} class, and are either one of a
 * pre-defined set of criteria object types, or a custom criteria provided by the application.  Every implementation of
 * the database layer for a particular database engine or product will have to interpret these selection criteria
 * objects and convert them into, for example, SQL "where" clauses, or other forms of conditions as appropriate to the
 * product.
 */
public final class SelectionCriteria {

    /** An empty criterion array. */
    private static final AbstractFieldCriterion[] ZERO_LEN_CRITERIA = new AbstractFieldCriterion[0];

    /** The table for which this query criteria applies. */
    private final Table table;

    /** A collection of criteria that fields may match (only rows matching all criteria are matched). */
    private final AbstractFieldCriterion[] criteria;

    /**
     * Constructs a new {@code SelectionCriteria}.
     *
     * @param theTable the table
     * @param theCriteria the ordered list of field criteria
     */
    public SelectionCriteria(final Table theTable, final AbstractFieldCriterion... theCriteria) {

        if (theTable == null) {
            throw new IllegalArgumentException("Table may not be null");
        }

        if (theCriteria != null) {
            for (final AbstractFieldCriterion test : theCriteria) {
                if (test == null) {
                    throw new IllegalArgumentException("Field criteria array may not contain null values");
                }
            }
        }

        this.table = theTable;
        this.criteria = theCriteria == null ? ZERO_LEN_CRITERIA : theCriteria.clone();
    }

    /**
     * Gets the table for which this set of criteria applies.
     *
     * @return the table
     */
    public Table getTable() {

        return this.table;
    }

    /**
     * Gets the number of field criteria.
     *
     * @return the number of field criteria
     */
    public int getNumCriteria() {

        return this.criteria.length;
    }

    /**
     * Gets a specific field criterion.
     *
     * @param index the zero-based index of the field
     * @return the field criterion
     */
    public AbstractFieldCriterion getCriterion(final int index) {

        return this.criteria[index];
    }

    /**
     * Generates a diagnostic string representation of the object.
     *
     * @return the string representation
     */
    @Override
    public String toString() {

        final String criteriaString = Arrays.toString(this.criteria);

        return SimpleBuilder.concat("SelectionCriteria{table=", this.table, ", criteria=", criteriaString, "}");
    }
}
