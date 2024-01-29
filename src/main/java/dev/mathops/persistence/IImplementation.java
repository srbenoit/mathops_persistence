package dev.mathops.persistence;

import dev.mathops.persistence.criteria.AbstractFieldCriterion;

import java.util.Collection;
import java.util.List;

/**
 * The interface that implementations must implement.
 */
public interface IImplementation {

    /** An empty row array used to convert collections to arrays. */
    Row[] EMPTY_ROW_ARRAY = new Row[0];

    /** An empty field criterion array used to convert collections to arrays. */
    AbstractFieldCriterion[] EMPTY_CRITERION_ARRAY = new AbstractFieldCriterion[0];

    /**
     * Returns the number of rows that match a set of field criteria.
     *
     * @param table       the table to query
     * @param criteria    zero or more criteria to apply
     * @return the number of rows that be returned by a query with the same criteria
     * @throws PersistenceException if there was an error performing the count
     */
    int count(Table table, AbstractFieldCriterion... criteria) throws PersistenceException;

    /**
     * Returns the number of rows that match a set of field criteria.
     *
     * @param table       the table to query
     * @param criteria    zero or more criteria to apply
     * @return the number of rows that be returned by a query with the same criteria
     * @throws PersistenceException if there was an error performing the count
     */
    default int countC(final Table table, final Collection<AbstractFieldCriterion> criteria)
            throws PersistenceException {

        final AbstractFieldCriterion[] array = criteria.toArray(EMPTY_CRITERION_ARRAY);
        return count(table, array);
    }

    /**
     * Performs a generalized query.
     *
     * @param table       the table to query
     * @param criteria    zero or more criteria to apply
     * @return the list of rows; can be empty but never {@code null}
     * @throws PersistenceException if there was an error performing the query
     */
    List<Row> query(Table table, AbstractFieldCriterion... criteria) throws PersistenceException;

    /**
     * Performs a generalized query.
     *
     * @param table       the table to query
     * @param criteria    zero or more criteria to apply
     * @return the list of rows; can be empty but never {@code null}
     * @throws PersistenceException if there was an error performing the query
     */
    default List<Row> queryC(final Table table, final Collection<AbstractFieldCriterion> criteria)
            throws PersistenceException {

        final AbstractFieldCriterion[] array = criteria.toArray(EMPTY_CRITERION_ARRAY);
        return query(table, array);
    }

    /**
     * Performs a generalized deletion.
     *
     * @param table       the table from which to delete
     * @param criteria    zero or more criteria to select the rows to be deleted
     * @return the number of rows deleted
     * @throws PersistenceException if there was an error performing the deletion
     */
    int delete(final Table table, final AbstractFieldCriterion... criteria) throws PersistenceException;

    /**
     * Performs a generalized deletion.
     *
     * @param table       the table whose rows to delete
     * @param criteria    zero or more criteria to select the rows to be deleted
     * @return the number of rows deleted
     * @throws PersistenceException if there was an error performing the deletion
     */
    default int deleteC(final Table table, final Collection<AbstractFieldCriterion> criteria)
            throws PersistenceException {

        final AbstractFieldCriterion[] array = criteria.toArray(EMPTY_CRITERION_ARRAY);
        return delete(table, array);
    }

    /**
     * Performs a generalized update.
     *
     * @param table       the table whose rows to update
     * @param newValues   the new values to assign in matching rows
     * @param criteria    zero or more criteria to select the rows to be updated
     * @return the number of rows updated
     * @throws PersistenceException if there was an error performing the update
     */
    int update(Table table, UpdatedValues newValues, AbstractFieldCriterion... criteria) throws PersistenceException;

    /**
     * Performs a generalized update.
     *
     * @param table       the table whose rows to update
     * @param newValues   the new values to assign in matching rows
     * @param criteria    zero or more criteria to select the rows to be updated
     * @return the number of rows updated
     * @throws PersistenceException if there was an error performing the insert
     */
    default int updateC(final Table table, final UpdatedValues newValues,
                       final Collection<AbstractFieldCriterion> criteria) throws PersistenceException {

        final AbstractFieldCriterion[] array = criteria.toArray(EMPTY_CRITERION_ARRAY);
        return update(table, newValues, array);
    }

    /**
     * Performs a generalized insertion.
     *
     * @param table      the table into which to insert
     * @param toInsert   the rows to insert
     * @return the number of rows inserted (1 on success, 0 on failure)
     * @throws PersistenceException if there was an error performing the insert
     */
    int insert(Table table, Row... toInsert) throws PersistenceException;

    /**
     * Performs a generalized insertion.
     *
     * @param table      the table into which to insert
     * @param toInsert   the rows to insert
     * @return the number of rows inserted (1 on success, 0 on failure)
     * @throws PersistenceException if there was an error performing the insert
     */
    default int insertC(final Table table, final Collection<Row> toInsert) throws PersistenceException {

        final Row[] array = toInsert.toArray(EMPTY_ROW_ARRAY);
        return insert(table, array);
    }
}
