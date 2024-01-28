package dev.mathops.persistence.impl.primary.pg;

import dev.mathops.commons.CoreConstants;
import dev.mathops.commons.builder.HtmlBuilder;
import dev.mathops.commons.builder.SimpleBuilder;
import dev.mathops.commons.log.Log;
import dev.mathops.persistence.Field;
import dev.mathops.persistence.Record;
import dev.mathops.persistence.Table;
import dev.mathops.persistence.connection.AbstractGeneralConnection;
import dev.mathops.persistence.connection.JdbcGeneralConnection;
import dev.mathops.persistence.criteria.AbstractFieldCriterion;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The implementation for the PostgreSQL database system.
 *
 * <p>
 * In the PostgreSQL implementation, a table in the "example" schema will exist in three different actual schemata:
 * "example" (the PRODUCTION table), "example_d" (the DEVELOPMENT table), and "example_t" (the TEST table).  However, if
 * the schema is prefixed by "term_", then there will be the PRODUCTION table, in the named schema, but the DEVELOPMENT
 * table will live in a general "term_d" schema (representing the active term), and the TEST table will live in a
 * general "term_t" schema.
 *
 * <p>
 * PostgreSQL table names match the {@code Table}'s name, and field names match the {@code Field}'s names.
 */
public final class PostgreSQLImpl {

    /** The prefix for a "term" table. */
    private static final String TERM_PREFIX = "term_";

    /** The development schema for "term" tables. */
    private static final String TERM_DEV_SCHEMA = "term_d";

    /** The test schema for "term" tables. */
    private static final String TERM_TEST_SCHEMA = "term_t";

    /** The single instance. */
    public static final PostgreSQLImpl INSTANCE = new PostgreSQLImpl();

    /**
     * Constructs a new {@code PostgreSQLImpl}.
     */
    private PostgreSQLImpl() {

        // No action
    }

    /**
     * Returns the number of records that would match a generalized query.
     *
     * @param connection  the connection on which to perform the query
     * @param table       the table to query
     * @param criteria    zero or more criteria to apply
     * @return the number of records that be returned by a query with the same criteria
     */
    public int count(final AbstractGeneralConnection connection, final Table table,
                     final AbstractFieldCriterion... criteria) {

        int count = 0;

        if (connection instanceof final JdbcGeneralConnection jdbc) {
            final String actualSchema = generateSchemaName(connection, table);
            final String tableName = table.getName();

            final HtmlBuilder sql = new HtmlBuilder(100);
            sql.add("SELECT COUNT(*) FROM ", actualSchema, ".", tableName);
            appendWhere(sql, criteria);
            final String sqlStr = sql.toString();

            final Connection jdbcConn = jdbc.getConnection();
            try (final Statement stmt = jdbcConn.createStatement(); final ResultSet rs = stmt.executeQuery(sqlStr)) {
                rs.next();
                count = rs.getInt(1);
            } catch (final SQLException ex) {
                Log.warning("Exception querying count from PostgreSQL database [", sql, "]", ex);
            }
        } else {
            Log.warning("Call to PostgreSQL implementation with a non-JDBC connection object.");
        }

        return count;
    }

    /**
     * Performs a generalized query.
     *
     * @param connection  the connection on which to perform the query
     * @param table       the table to query
     * @param criteria    zero or more criteria to apply
     * @return the list of records; can be empty but never {@code null}
     */
    public List<Record> query(final AbstractGeneralConnection connection, final Table table,
                              final AbstractFieldCriterion... criteria) {

        final List<Record> result = new ArrayList<>(10);

        if (connection instanceof final JdbcGeneralConnection jdbc) {
            final String actualSchema = generateSchemaName(connection, table);
            final String tableName = table.getName();

            final HtmlBuilder sql = new HtmlBuilder(100);
            sql.add("SELECT * FROM ", actualSchema, ".", tableName);
            appendWhere(sql, criteria);
            final String sqlStr = sql.toString();

            final Connection jdbcConn = jdbc.getConnection();
            try (final Statement stmt = jdbcConn.createStatement(); final ResultSet rs = stmt.executeQuery(sqlStr)) {
                while (rs.next()) {
                    final Record rec = extractRecord(table, rs);
                    result.add(rec);
                }
            } catch (final SQLException ex) {
                Log.warning("Exception querying PostgreSQL database [", sql, "]", ex);
            }
        } else {
            Log.warning("Call to PostgreSQL implementation with a non-JDBC connection object.");
        }

        return result;
    }

    /**
     * Performs a generalized deletion.
     *
     * @param connection  the connection on which to perform the deletion
     * @param table       the table from which to delete
     * @param criteria    zero or more criteria to apply
     * @return the number of records deleted
     */
    public int delete(final AbstractGeneralConnection connection, final Table table,
                      final AbstractFieldCriterion... criteria) {

        int numDeleted = 0;

        if (connection instanceof final JdbcGeneralConnection jdbc) {
            final String actualSchema = generateSchemaName(connection, table);
            final String tableName = table.getName();

            final HtmlBuilder sql = new HtmlBuilder(100);
            sql.add("DELETE FROM ", actualSchema, ".", tableName);
            appendWhere(sql, criteria);
            final String sqlStr = sql.toString();

            final Connection jdbcConn = jdbc.getConnection();
            try (final Statement stmt = jdbcConn.createStatement()) {
                numDeleted = stmt.executeUpdate(sqlStr);
            } catch (final SQLException ex) {
                Log.warning("Exception deleting from PostgreSQL database [", sql, "]", ex);
            }
        } else {
            Log.warning("Call to PostgreSQL implementation with a non-JDBC connection object.");
        }

        return numDeleted;
    }

    /**
     * Performs a generalized insertion.
     *
     * @param connection  the connection on which to perform the insertion
     * @param table       the table into which to insert
     * @param toInsert the record to insert
     * @return the number of records inserted (1 on success, 0 on failure)
     */
    public int insert(final AbstractGeneralConnection connection, final Table table, final Record toInsert) {

        int numInserted = 0;

        if (connection instanceof final JdbcGeneralConnection jdbc) {
            final String actualSchema = generateSchemaName(connection, table);
            final String tableName = table.getName();
            final int numFields = table.getNumFields();

            final HtmlBuilder sql = new HtmlBuilder(100);
            sql.add("INSERT INTO ", actualSchema, ".", tableName, " (");
            final Field field0 = table.getField(0);
            final String field0Name = field0.getName();
            sql.add(field0Name);
            for (int i = 1; i < numFields; ++i) {
                final Field field = table.getField(i);
                final String fieldName = field.getName();
                sql.add(CoreConstants.COMMA, fieldName);
            }
            sql.add(") VALUES (");
            appendFieldValue(sql, 0, toInsert);
            for (int i = 1; i < numFields; ++i) {
                sql.add(CoreConstants.COMMA_CHAR);
                appendFieldValue(sql, i, toInsert);
            }
            sql.add(")");

            final String sqlStr = sql.toString();

            final Connection jdbcConn = jdbc.getConnection();
            try (final Statement stmt = jdbcConn.createStatement()) {
                numInserted = stmt.executeUpdate(sqlStr);
            } catch (final SQLException ex) {
                Log.warning("Exception inserting into PostgreSQL database [", sql, "]", ex);
            }
        } else {
            Log.warning("Call to PostgreSQL implementation with a non-JDBC connection object.");
        }

        return numInserted;
    }

    /**
     * Appends a field value in a format acceptable to PostgreSQL to a {@code HtmlBuilder}.
     *
     * @param sql    the {@code HtmlBuilder} to which to append
     * @param index  the field index
     * @param record the record with the field value
     */
    private static void appendFieldValue(final HtmlBuilder sql, final int index, final Record record) {

        final Object value = record.getFieldValue(index);

        if (value == null) {
            sql.add("NULL");
        } else if (value instanceof final String stringValue) {

        } else if (value instanceof final Boolean booleanValue) {

        } else if (value instanceof final Byte byteValue) {

        } else if (value instanceof final Integer integerValue) {

        } else if (value instanceof final Long longValue) {

        } else if (value instanceof final Float floatValue) {

        } else if (value instanceof final Double doubleValue) {

        } else if (value instanceof final Blob blobValue) {

        } else if (value instanceof final LocalDate localDateValue) {

        } else if (value instanceof final LocalTime localTimeValue) {

        } else if (value instanceof final LocalDateTime localDateTimeValue) {

        } else {
            Log.warning("Unsupported field value type: ", value.getClass().getName());
            sql.add("NULL");
        }
    }


    /**
     * Generates the actual PostgreSQL schema name that corresponds to a given schema name and a connection context.
     *
     * @param connection the connection on which to perform the query
     * @param table      the table to query
     * @return the actual schema name
     */
    private static String generateSchemaName(final AbstractGeneralConnection connection, final Table table) {

        // Generate the actual schema name
        final String schema = table.getSchema();

        return switch (connection.getContext()) {
            case PRODUCTION -> schema;
            case DEVELOPMENT -> schema.startsWith(TERM_PREFIX) ? TERM_DEV_SCHEMA : SimpleBuilder.concat(schema, "_d");
            case TESTING -> schema.startsWith(TERM_PREFIX) ? TERM_TEST_SCHEMA : SimpleBuilder.concat(schema, "_t");
        };
    }

    /**
     * Constructs the "Where" portion of a query, update, or delete statement.
     *
     * @param sql         the {@code HtmlBuilder} to which to append
     * @param criteria    zero or more criteria to apply
     */
    private static void appendWhere(final HtmlBuilder sql, final AbstractFieldCriterion... criteria) {

        if (criteria != null && criteria.length > 0) {
            boolean first = true;
            for (final AbstractFieldCriterion criterion : criteria) {
                sql.add(first ? " WHERE " : " AND ");
                first = false;
                appendWhereClause(sql, criterion);
            }
        }
    }

    /**
     * Appends a where clause like "index = 4" or "name is not null" from a criterion.
     *
     * @param sql       the {@code HtmlBuilder} to which to append
     * @param criterion the criterion
     */
    private static void appendWhereClause(final HtmlBuilder sql, final AbstractFieldCriterion criterion) {

        final Field field = criterion.getField();
        final String fieldName = field.getName();

        // TODO:
    }

    /**
     * Appends a where clause like "index = 4" or "name is not null" from a constraint.
     *
     * @param table the {@code Table} from which to get the record's field names and types
     * @param rs    the {@code ResultSet} from which to retrieve field values
     */
    private static Record extractRecord(final Table table, final ResultSet rs) throws SQLException {

        final int numFields = table.getNumFields();
        final Object[] fieldValues = new Object[numFields];

        for (int i = 0; i < numFields; ++i) {
            final Field field = table.getField(i);
            final String name = field.getName();

            fieldValues[i] = switch (field.getType()) {
                case STRING -> rs.getString(name);
                case BOOLEAN -> {
                    final boolean value = rs.getBoolean(name);
                    yield rs.wasNull() ? null : Boolean.valueOf(value);
                }
                case BYTE -> {
                    final byte value = rs.getByte(name);
                    yield rs.wasNull() ? null : Byte.valueOf(value);
                }
                case INTEGER -> {
                    final int value = rs.getInt(name);
                    yield rs.wasNull() ? null : Integer.valueOf(value);
                }
                case LONG -> {
                    final long value = rs.getLong(name);
                    yield rs.wasNull() ? null : Long.valueOf(value);
                }
                case FLOAT -> {
                    final float value = rs.getFloat(name);
                    yield rs.wasNull() ? null : Float.valueOf(value);
                }
                case DOUBLE -> {
                    final double value = rs.getDouble(name);
                    yield rs.wasNull() ? null : Double.valueOf(value);
                }
                case BLOB -> rs.getBlob(name);
                case LOCAL_DATE -> {
                    final Date value = rs.getDate(name);
                    yield value == null ? null : value.toLocalDate();
                }
                case LOCAL_TIME -> {
                    final Time value = rs.getTime(name);
                    yield value == null ? null : value.toLocalTime();
                }
                case LOCAL_DATE_TIME -> {
                    final Timestamp value = rs.getTimestamp(name);
                    yield value == null ? null : value.toLocalDateTime();
                }
            };
        }

        try {
            return new Record(table, fieldValues);
        } catch (final IllegalArgumentException ex) {
            final String tableName = table.getName();
            final String msg = SimpleBuilder.concat("Failed to extract a record from the '", tableName, "' table");
            throw new SQLException(msg, ex);
        }
    }
}
