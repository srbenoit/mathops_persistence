package dev.mathops.persistence.api;

import dev.mathops.db.table.Row;
import dev.mathops.db.table.SelectionCriteria;
import dev.mathops.db.table.UpdatedValues;

import java.io.ByteArrayOutputStream;
import java.nio.channels.SocketChannel;
import java.util.List;

/**
 * A client that can connect to a persistence server through its API and perform queries, counts, updates, inserts,
 * and deletes.
 */
public final class PersistenceClient {

    /** A shared stream in which to assemble requests (all operations synchronize on this object). */
    private final ByteArrayOutputStream buffer;

    /** The socket channel to the server, if the client is connected. */
    private SocketChannel channel;

    /** The 16-byte authorization token, if client is connected. */
    private byte[] token;

    /**
     * Constructs a new {@code PersistenceClient}.
     */
    public PersistenceClient() {

        this.buffer = new ByteArrayOutputStream(500);

        // TODO: Store server address/port
        // TODO: Store credentials
    }

    /**
     * Connects to the API server using provided credentials.  This establishes a TLS connection to the server, obtains
     * an authorization token, and then can use that on future API calls.  The TLS connection is maintained, but may
     * close if unused for some period of time, and is reopened if needed when an API call is to be made.
     *
     * @return the response, with an error message if not successful
     */
    public ConnectResponse connect() {

        // TODO: Create a SSLSocketFactory that can use the self-signed certificate from the server.

        // TODO: Connect to the server over TLS

        // TODO: Send SCRAM-SHA-256 "client_first" message, receive the "server_first" message, generate and send the
        // TODO: "client_final" message, receive the "server_final" message, and store the authorization token

        return null;
    }

    /**
     * Disconnects from the server.  This attempts to tell the server that the authorization token is no longer needed
     * (which invalidates the token), then closes the TLS connection and discards the token.  The client may call
     * {@code connect} again if needed to re-connect.
     */
    public void disconnect() {

        // TODO: If TLS connection is closed, attempt to re-open

        // TODO: If connected to server, send a message to invalidate the authorization token

        this.token = null;
    }

    /**
     * Performs a count operation to return the number of rows in a table that match selection criteria (or all rows
     * if no selection criteria is provided).
     *
     * @param schemaId the schema ID
     * @param contextId the context ID
     * @param tableId the table ID
     * @param criteria optional selection criteria
     * @return the response, with the number of rows found (on SUCCESS), or an error message otherwise
     */
    public CountResponse doCount(final int schemaId, final int contextId, final int tableId,
                                 final SelectionCriteria criteria) {

        // TODO:

        return new CountResponse(EResultCode.FAILURE, 0, "Not yet implemented");
    }

    /**
     * Performs a query operation to return the rows in a table that match selection criteria (or all rows if no
     * selection criteria is provided).
     *
     * @param schemaId the schema ID
     * @param contextId the context ID
     * @param tableId the table ID
     * @param criteria optional selection criteria
     * @return the response, with the rows found (on SUCCESS), or an error message otherwise
     */
    public QueryResponse doQuery(final int schemaId, final int contextId, final int tableId,
                                 final SelectionCriteria criteria) {

        // TODO:

        return new QueryResponse(EResultCode.FAILURE, null, "Not yet implemented");
    }

    /**
     * Performs an insert operation to atomically insert some list of rows into a specified table.
     *
     * @param schemaId the schema ID
     * @param contextId the context ID
     * @param tableId the table ID
     * @param rowsToInsert a list of rows to be inserted
     * @return the response, with the rows found (on SUCCESS), or an error message otherwise
     */
    public InsertResponse doInsert(final int schemaId, final int contextId, final int tableId,
                                   final List<Row> rowsToInsert) {

        // TODO:

        return new InsertResponse(EResultCode.FAILURE, 0, "Not yet implemented");
    }

    /**
     * Performs a multi-table insert operation to atomically insert some list of rows into more than one specified
     * table.  The length of all argument arrays must be the same, and represents the number of tables into which
     * rows are being inserted.  The same table may not be specified more than once.
     *
     * @param schemaId an array of schema IDs
     * @param contextId an array of the context IDs
     * @param tableId an array of the table IDs
     * @param rowsToInsert an array of lists of rows to be inserted into each table
     * @return the response, with the rows found (on SUCCESS), or an error message otherwise
     */
    public InsertResponse doMultiInsert(final int[] schemaId, final int[] contextId, final int[] tableId,
                                        final List<Row>[] rowsToInsert) {

        // TODO:

        return new InsertResponse(EResultCode.FAILURE, 0, "Not yet implemented");
    }

    /**
     * Performs a delete operation to delete the rows in a table that match selection criteria (or all rows if no
     * selection criteria is provided).
     *
     * @param schemaId the schema ID
     * @param contextId the context ID
     * @param tableId the table ID
     * @param criteria optional selection criteria
     * @return the response, with the rows deleted (on SUCCESS), or an error message otherwise
     */
    public DeleteResponse doDelete(final int schemaId, final int contextId, final int tableId,
                                   final SelectionCriteria criteria) {

        // TODO:

        return new DeleteResponse(EResultCode.FAILURE, 0, "Not yet implemented");
    }

    /**
     * Performs an update operation to update the rows in a table that match selection criteria (or all rows if no
     * selection criteria is provided) with specified new values.
     *
     * @param schemaId the schema ID
     * @param contextId the context ID
     * @param tableId the table ID
     * @param criteria optional selection criteria
     * @param updatedValues the values to apply to matching rows
     * @return the response, with the rows updated (on SUCCESS), or an error message otherwise
     */
    public UpdateResponse doUpdate(final int schemaId, final int contextId, final int tableId,
                                   final SelectionCriteria criteria, final UpdatedValues updatedValues) {

        // TODO:

        return new UpdateResponse(EResultCode.FAILURE, 0, "Not yet implemented");
    }

    /**
     * Generates a diagnostic string representation of the object.
     *
     * @return the string representation
     */
    @Override
    public String toString() {

        return "PersistenceClient{}";
    }
}
