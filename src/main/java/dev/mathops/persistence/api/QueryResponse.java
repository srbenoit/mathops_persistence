package dev.mathops.persistence.api;

import dev.mathops.db.table.Row;

import java.util.List;

/**
 * An object returned by a "query" operation.
 *
 * @param resultCode the result code
 * @param rows on a SUCCESS result code, the rows in the requested table
 * @param errorMessage on a non-SUCCESS result code, a diagnostic error message
 */
public record QueryResponse(EResultCode resultCode, List<Row> rows, String errorMessage) {}
