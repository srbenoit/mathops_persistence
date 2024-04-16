package dev.mathops.persistence.config;

/**
 * The configuration of a context within a schema.
 */
public class SchemaContextConfig {

    /** The context name (unique within the schema). */
    private final String name;

    /**
     * Constructs a new {@code SchemaContextConfig}.
     *
     * @param theName the context name
     */
    SchemaContextConfig(final String theName) {

        this.name = theName;
    }
}
