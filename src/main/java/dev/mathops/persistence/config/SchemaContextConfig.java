package dev.mathops.persistence.config;

/**
 * The configuration of a context within a schema.
 */
public final class SchemaContextConfig {

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

    /**
     * Gets the context name.
     *
     * @return the context name
     */
    public String getName() {

        return this.name;
    }
}
