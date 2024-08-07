package dev.mathops.schema;

import dev.mathops.persistence.Field;
import dev.mathops.persistence.SelectionCriteria;
import dev.mathops.persistence.Table;
import dev.mathops.persistence.criteria.AbstractFieldCriterion;
import dev.mathops.persistence.criteria.ENumericMatchType;
import dev.mathops.persistence.criteria.IntegerFieldCriterion;
import dev.mathops.schema.main.TermTable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests for the {@code Builder} class.
 */
final class TestTermTable {

    /** The term table instance. */
    private static TermTable instance;

    /**
     * A test case.
     */
    @BeforeAll
    static void init() {

        instance = dev.mathops.schema.main.TermTable.INSTANCE;
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("mkTermIdQuery")
    void test001() {

        final SelectionCriteria criteria = instance.mkTermIdQuery(202410);

        final Table criteriaTable = criteria.getTable();
        assertSame(instance, criteriaTable, "Expected table to match singleton instance");

        final int numCriteria = criteria.getNumCriteria();
        assertEquals(1, numCriteria, "Expected number of criteria to be 1");

        final AbstractFieldCriterion criterion = criteria.getCriterion(0);
        assertInstanceOf(IntegerFieldCriterion.class, criterion, "Expected criterion to be IntegerFieldCriterion");

        if (criterion instanceof final IntegerFieldCriterion intCriterion) {
            final Field termIdField = instance.getField(0);
            final Field field = intCriterion.getField();
            assertSame(termIdField, field, "Expected match field to be term ID field");

            final ENumericMatchType matchType = intCriterion.getMatchType();
            assertEquals(ENumericMatchType.EXACT_IN, matchType, "Expected match type to be EXACT_IN");

            final Integer numMatchInt = intCriterion.getNumIntegers();
            assertEquals(1, numMatchInt, "Expected there to be one match integer");

            final Integer matchInt = intCriterion.getInteger(0);
            assertEquals(202410, matchInt.intValue() , "Expected match integer to ne 202410");
        }
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("mkActiveIndexQuery")
    void test002() {

        final SelectionCriteria criteria = instance.mkActiveIndexQuery(-3);

        final Table criteriaTable = criteria.getTable();
        assertSame(instance, criteriaTable, "Expected table to match singleton instance");

        final int numCriteria = criteria.getNumCriteria();
        assertEquals(1, numCriteria, "Expected number of criteria to be 1");

        final AbstractFieldCriterion criterion = criteria.getCriterion(0);
        assertInstanceOf(IntegerFieldCriterion.class, criterion, "Expected criterion to be IntegerFieldCriterion");

        if (criterion instanceof final IntegerFieldCriterion intCriterion) {
            final Field termIdField = instance.getField(0);
            final Field field = intCriterion.getField();
            assertSame(termIdField, field, "Expected match field to be term ID field");

            final ENumericMatchType matchType = intCriterion.getMatchType();
            assertEquals(ENumericMatchType.EXACT_IN, matchType, "Expected match type to be EXACT_IN");

            final Integer numMatchInt = intCriterion.getNumIntegers();
            assertEquals(1, numMatchInt, "Expected there to be one match integer");

            final Integer matchInt = intCriterion.getInteger(0);
            assertEquals(-3, matchInt.intValue() , "Expected match integer to be -3");
        }
    }
    /**
     * Creates a {@code SelectionCriteria} object that matches on a specified active index.
     *
     * @param theActiveIndex the active index for which to query
     * @return the selection criteria
     */
    public SelectionCriteria mkActiveIndexQuery(final int theActiveIndex) {

        final Integer activeIndexObj = Integer.valueOf(theActiveIndex);

        return new SelectionCriteria(this, new IntegerFieldCriterion(TermTable.F_ACTIVE_INDEX,
                ENumericMatchType.EXACT_IN, activeIndexObj));
    }


}
