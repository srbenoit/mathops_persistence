package dev.mathops.persistence;

import dev.mathops.commons.builder.SimpleBuilder;

/**
 * An immutable definition of a schema.  Every table exists within one schema, and the tuple of the schema name and
 * table name must be unique.  That is, schema names must be unique across the system, and table names must be unique
 * within a schema.
 *
 * <p>
 * The physical implementation of a schema may depend on the database engine used.  For a typical RDBMS, this concept
 * maps directly to the SQL schema concept.
 */
public class Schema implements Comparable<Schema> {

    /** The schema name. */
    private final String name;

    /** The HTML table description (shown in auto-generated documentation). */
    private final String description;

    /**
     * Constructs a new {@code Schema}.
     *
     * @param theName        the schema name
     * @param theDescription the table description (shown in auto-generated documentation)
     * @throws IllegalArgumentException if the schema name is {@code null} or blank or is not a valid identifier
     */
    public Schema(final String theName, final String theDescription) throws IllegalArgumentException {

        if (theName == null || theName.isBlank()) {
            throw new IllegalArgumentException("Schema name may not be null or blank");
        }
        if (theDescription == null || theDescription.isBlank()) {
            throw new IllegalArgumentException("Table description may not be null or blank");
        }

        if (NameUtils.isInvalidName(theName)) {
            throw new IllegalArgumentException("Schema name is not a valid identifier");
        }

        this.name = theName;
        this.description = theDescription;
    }

    /**
     * Gets the schema name.
     *
     * @return the schema name
     */
    public final String getName() {

        return this.name;
    }

    /**
     * Gets the table description.
     *
     * @return the table description
     */
    public final String getDescription() {

        return this.description;
    }

    /**
     * Generates a hash code for the object.
     *
     * @return the hash code
     */
    @Override
    public final int hashCode() {

        return this.name.hashCode();
    }

    /**
     * Tests whether this object is equal to another.  To be equal, the other object must be a {@code Table} with the
     * same schema name, table group name, table name, and collection of fields.
     *
     * @return the hash code
     */
    @Override
    public final boolean equals(final Object obj) {

        final boolean equal;

        if (obj == this) {
            equal = true;
        } else if (obj instanceof final Schema objSchema) {
            final String objName = objSchema.getName();

            equal = this.name.equals(objName);
        } else {
            equal = false;
        }

        return equal;
    }

    /**
     * Compares this object with the specified object for order. Returns a negative integer, zero, or a positive integer
     * as this object is less than, equal to, or greater than the specified object.  Sort order is based on schema name
     * first, then table group name, then table name.
     *
     * @param o the object to be compared
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
     *         the specified object
     */
    @Override
    public final int compareTo(final Schema o) {

        final String oName = o.getName();

        return  this.name.compareTo(oName);
    }

    /**
     * Generates a diagnostic string representation of the object.
     *
     * @return the string representation
     */
    @Override
    public final String toString() {

        return SimpleBuilder.concat("Schema{name='", this.name, "'}");
    }
}
