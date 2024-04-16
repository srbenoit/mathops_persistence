package dev.mathops.persistence.config;

import dev.mathops.persistence.IImplementation;

/**
 * The configuration of an implementation.
 *
 * <p>
 * Implementations are defined in a JSON file "implementations.json" in the configuration directory.
 *
 * <pre>
 * [
 *   {
 *     "implementation-name": "...implementation name...",
 *     "schemas": [
 *       {
 *         "name": "...schema name...",
 *         "implementation-class": "...class name..."
 *       },
 *       ... additional schemas ...
 *     ]
 *   },
 *   ... additional role objects ...
 * ]
 * </pre>
 */
public class ImplementationConfig {

    /** The implementation name. */
    private final String name;

    /** The implementation object. */
    private final IImplementation implementation;

    /**
     * Constructs a new {@code ImplementationConfig}.
     *
     * @param theName           the implementation name
     * @param theImplementation the implementation object
     */
    ImplementationConfig(final String theName, final IImplementation theImplementation) {

        this.name = theName;
        this.implementation = theImplementation;
    }
}
