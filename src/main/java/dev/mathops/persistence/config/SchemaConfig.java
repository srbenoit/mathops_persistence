package dev.mathops.persistence.config;

import dev.mathops.db.table.Table;

import java.util.HashMap;
import java.util.Map;

/**
 * The configuration of a schema.
 *
 * <p>
 * Schemas are defined in a JSON file "schemas.json" in the configuration directory.
 *
 * <pre>
 * [
 *   {
 *     "schema-name": "...schema name...",
 *     "contexts": [
 *       {
 *         "context-name": "...context name...",
 *       }
 *     ],
 *     "tables": [
 *       {
 *         "table-name": "...table name...",
 *         "fields": [
 *           {
 *             "field-name": "...field name...",
 *             "type": "...field type name...",
 *             "role": "...field role name...",
 *             "description": "...description...",
 *             "constraints": [
 *               {"type": "byte-range", "min": 1, "max": 10},
 *               {"type": "double-range", "allow": "...EFloatingPointAllow.name...", "min": 1.0, "max": 10.0},
 *               {"type": "float-range", "allow": "...EFloatingPointAllow.name...", "min": 1.0, "max": 10.0},
 *               {"type": "integer-range", "min": 1, "max": 10},
 *               {"type": "long-range", "min": 1, "max": 10},
 *               {"type": "string-enum", "values": ["A", "B", "C"]},
 *               {"type": "string-length", "min": 1, "max": 10},
 *             ]
 *           }
 *         ]
 *       }
 *     ]
 *   },
 *   ... additional schema objects ...
 * ]
 * </pre>
 */
public final class SchemaConfig {

    /** The schema name. */
    private final String name;

    /** A map from context name to the schema context configuration. */
    private final Map<String, SchemaContextConfig> contexts;

    /** A map from table name to the tables supported by the schema. */
    private final Map<String, Table> tables;

    /**
     * Constructs a new {@code SchemaConfig}.
     *
     * @param theName the schema name
     */
    SchemaConfig(final String theName) {

        this.name = theName;

        this.contexts = new HashMap<>(10);
        this.tables = new HashMap<>(100);
    }

    /**
     * Gets the schema name.
     *
     * @return the schema name
     */
    public String getName() {

        return this.name;
    }

    /**
     * Gets the number of contexts this schema defines.
     *
     * @return the number of contexts
     */
    int getNumContexts() {

        return this.contexts.size();
    }

    /**
     * Adds a schema context configuration.
     *
     * @param theContext the context configuration
     */
    void addContext(final SchemaContextConfig theContext) {

        final String contextName = theContext.getName();
        this.contexts.put(contextName, theContext);
    }

    /**
     * Gets the number of tables this schema defines.
     *
     * @return the number of tables
     */
    int getNumTables() {

        return this.tables.size();
    }

    /**
     * Adds a schema table configuration.
     *
     * @param theTable the table configuration
     */
    void addTable(final Table theTable) {

        final String tableName = theTable.getName();
        this.tables.put(tableName, theTable);
    }
}
