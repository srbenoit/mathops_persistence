package dev.mathops.persistence.config;

import java.util.HashMap;
import java.util.Map;

/**
 * A data configuration, which maps an implementation to every defined schema and context.
 *
 * <p>
 * Data configurations are defined in a JSON file "data_configurations.json" in the configuration directory.
 *
 * <pre>
 * [
 *   {
 *     "configuration-name": "...configuration name...",
 *     "schemas": [
 *       {
 *         "schema-name": "...schema name...",
 *         "contexts": [
 *           {
 *             "context-name": "...context name...",
 *             "implementation-name": "...implementation name...",
 *           },
 *           ... additional context objects ...
 *         ]
 *       },
 *       ... additional schema objects ...
 *     ]
 *   },
 *   ... additional configuration objects ...
 * ]
 * </pre>
 */
public class DataConfiguration {

    /** The data configuration name. */
    private final String name;

    /** A map from schema name to a map from context name to the implementation. */
    private final Map<String, Map<String, ImplementationConfig>> implementations;

    /**
     * Constructs a new {@code DataConfiguration}.
     *
     * @param theName the implementation name
     */
    DataConfiguration(final String theName) {

        this.name = theName;
        this.implementations = new HashMap<>(10);
    }
}
