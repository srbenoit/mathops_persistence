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
     * Attempts to ensure all required database tables or filesystem or other structures that are needed to store a
     * table's data are present and in a workable state.
     *
     * @param context the context
     * @param table  the table to validate
     * @param action the action to take
     * @return the status of the table after the operation
     * @throws PersistenceException if there was an error validating the table
     */
    TableValidationStatus validateTable(int context, Table table, ETableValidationAction action)
            throws PersistenceException;

    /**
     * Returns the number of rows that match a set of field criteria.
     *
     * @param context the context
     * @param table       the table to query
     * @param criteria    zero or more criteria to apply
     * @return the number of rows that be returned by a query with the same criteria
     * @throws PersistenceException if there was an error performing the count
     */
    int count(int context, Table table, AbstractFieldCriterion... criteria) throws PersistenceException;

    /**
     * Returns the number of rows that match a set of field criteria.
     *
     * @param context the context
     * @param table       the table to query
     * @param criteria    zero or more criteria to apply
     * @return the number of rows that be returned by a query with the same criteria
     * @throws PersistenceException if there was an error performing the count
     */
    default int countC(final int context, final Table table, final Collection<AbstractFieldCriterion> criteria)
            throws PersistenceException {

        final AbstractFieldCriterion[] array = criteria.toArray(EMPTY_CRITERION_ARRAY);
        return count(context, table, array);
    }

    /**
     * Performs a generalized query.
     *
     * @param context the context
     * @param table       the table to query
     * @param criteria    zero or more criteria to apply
     * @return the list of rows; can be empty but never {@code null}
     * @throws PersistenceException if there was an error performing the query
     */
    List<Row> query(int context, Table table, AbstractFieldCriterion... criteria) throws PersistenceException;

    /**
     * Performs a generalized query.
     *
     * @param context the context
     * @param table       the table to query
     * @param criteria    zero or more criteria to apply
     * @return the list of rows; can be empty but never {@code null}
     * @throws PersistenceException if there was an error performing the query
     */
    default List<Row> queryC(final int context, final Table table, final Collection<AbstractFieldCriterion> criteria)
            throws PersistenceException {

        final AbstractFieldCriterion[] array = criteria.toArray(EMPTY_CRITERION_ARRAY);
        return query(context, table, array);
    }

    /**
     * Performs a generalized deletion.
     *
     * @param context the context
     * @param table       the table from which to delete
     * @param criteria    zero or more criteria to select the rows to be deleted
     * @return the number of rows deleted
     * @throws PersistenceException if there was an error performing the deletion
     */
    int delete(int context, final Table table, final AbstractFieldCriterion... criteria) throws PersistenceException;

    /**
     * Performs a generalized deletion.
     *
     * @param context the context
     * @param table       the table whose rows to delete
     * @param criteria    zero or more criteria to select the rows to be deleted
     * @return the number of rows deleted
     * @throws PersistenceException if there was an error performing the deletion
     */
    default int deleteC(final int context, final Table table, final Collection<AbstractFieldCriterion> criteria)
            throws PersistenceException {

        final AbstractFieldCriterion[] array = criteria.toArray(EMPTY_CRITERION_ARRAY);
        return delete(context, table, array);
    }

    /**
     * Performs a generalized update.
     *
     * @param context the context
     * @param table       the table whose rows to update
     * @param newValues   the new values to assign in matching rows
     * @param criteria    zero or more criteria to select the rows to be updated
     * @return the number of rows updated
     * @throws PersistenceException if there was an error performing the update
     */
    int update(int context, Table table, UpdatedValues newValues, AbstractFieldCriterion... criteria)
            throws PersistenceException;

    /**
     * Performs a generalized update.
     *
     * @param context the context
     * @param table       the table whose rows to update
     * @param newValues   the new values to assign in matching rows
     * @param criteria    zero or more criteria to select the rows to be updated
     * @return the number of rows updated
     * @throws PersistenceException if there was an error performing the insert
     */
    default int updateC(final int context, final Table table, final UpdatedValues newValues,
                       final Collection<AbstractFieldCriterion> criteria) throws PersistenceException {

        final AbstractFieldCriterion[] array = criteria.toArray(EMPTY_CRITERION_ARRAY);
        return update(context, table, newValues, array);
    }

    /**
     * Performs a generalized insertion.
     *
     * @param context the context
     * @param table      the table into which to insert
     * @param toInsert   the rows to insert
     * @return the number of rows inserted (1 on success, 0 on failure)
     * @throws PersistenceException if there was an error performing the insert
     */
    int insert(int context, Table table, Row... toInsert) throws PersistenceException;

    /**
     * Performs a generalized insertion.
     *
     * @param context the context
     * @param table      the table into which to insert
     * @param toInsert   the rows to insert
     * @return the number of rows inserted (1 on success, 0 on failure)
     * @throws PersistenceException if there was an error performing the insert
     */
    default int insertC(final int context, final Table table, final Collection<Row> toInsert)
            throws PersistenceException {

        final Row[] array = toInsert.toArray(EMPTY_ROW_ARRAY);
        return insert(context, table, array);
    }
}
