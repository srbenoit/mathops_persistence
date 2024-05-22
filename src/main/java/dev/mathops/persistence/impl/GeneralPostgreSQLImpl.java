package dev.mathops.persistence.impl;

import dev.mathops.commons.CoreConstants;
import dev.mathops.commons.log.Log;
import dev.mathops.persistence.ETableValidationAction;
import dev.mathops.persistence.IImplementation;
import dev.mathops.persistence.PersistenceException;
import dev.mathops.persistence.Row;
import dev.mathops.persistence.Table;
import dev.mathops.persistence.TableValidationStatus;
import dev.mathops.persistence.UpdatedValues;
import dev.mathops.persistence.criteria.AbstractFieldCriterion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * A generalized implementation class based on the PostgreSQL database engine.  This configuration assumes that a
 * PostgreSQL cluster contains a single database, and that database will contain multiple schemas.
 *
 * <p>
 * Schema names will be the concatenation of the context name (typically "prod", "dev", "test", or similar, but always
 * consisting of only ASCII letters), an underscore, and then the data layer schema name (for example, "main", or
 * "term202410").  Examples of complete PostgreSQL schema names might include "prod_main" or "dev_term202410".
 *
 * <p>
 * An instance of this class is instantiated for each PostgreSQL database (having the generalized table structure) that
 * will provide one or more schemas or contexts through the persistence layer.  Each instance maintains its own JDBC
 * connection, using its own connection credentials.
 *
 * <p>
 * This class can connect to a PostgreSQL database using JDBC to perform operations.  Assuming the connection is made
 * with credentials that allow for table creation, this class can create missing tables as needed.
 */
public final class GeneralPostgreSQLImpl implements IImplementation {

    /** The host to which to connect. */
    private String host = "localhost";

    /** The TCP port to which to connect. */
    private int port = 5432;

    /** The database to which to connect. */
    private String database = CoreConstants.EMPTY;

    /** The username with which to connect. */
    private String username = CoreConstants.EMPTY;

    /** The password with which to connect. */
    private String password = CoreConstants.EMPTY;

    /** The JDBC connection. */
    private Connection conn;

    // TODO: Store a list of the contexts provided.

    // TODO: For each context, store a list of schemas provided.

    /**
     * Constructs a new {@code GeneralPostgreSQLImpl}.
     */
    public GeneralPostgreSQLImpl() {

        // No action
    }

    /**
     * Creates the connection to the database.
     *
     * @throws SQLException if the connection cannot be created
     */
    public void connect() throws SQLException {

        final StringBuilder url = new StringBuilder(40);

        url.append("jdbc:postgresql://");
        url.append(this.host);
        if (this.port != 5432) {
            url.append(':');
            url.append(this.port);
        }
        url.append('/');
        url.append(this.database);
        final String urlString = url.toString();

        final Properties props = new Properties();
        props.setProperty("user", this.username);
        props.setProperty("password", this.password);

        this.conn = DriverManager.getConnection(urlString, props);
    }

    /**
     * Closes the connection if it is open.
     */
    public void disconnect() {

        if (this.conn != null) {
            try {
                if (!this.conn.isClosed()) {
                    this.conn.close();
                }
            } catch (final SQLException ex) {
                Log.warning("Exception while closing JDBC connection", ex);
            } finally {
                this.conn = null;
            }
        }
    }

    @Override
    public TableValidationStatus validateTable(final int context, final Table table,
                                               final ETableValidationAction action) throws PersistenceException {
        return null;
    }

    @Override
    public int count(final int context, final Table table,
                     final AbstractFieldCriterion... criteria) throws PersistenceException {
        return 0;
    }

    @Override
    public List<Row> query(final int context, final Table table,
                           final AbstractFieldCriterion... criteria) throws PersistenceException {
        return List.of();
    }

    @Override
    public int delete(final int context, final Table table,
                      final AbstractFieldCriterion... criteria) throws PersistenceException {
        return 0;
    }

    @Override
    public int update(final int context, final Table table, final UpdatedValues newValues,
                      final AbstractFieldCriterion... criteria) throws PersistenceException {
        return 0;
    }

    @Override
    public int insert(final int context, final Table table, final Row... toInsert) throws PersistenceException {
        return 0;
    }
}
