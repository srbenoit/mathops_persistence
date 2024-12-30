package dev.mathops.persistence;

import dev.mathops.text.builder.SimpleBuilder;

import java.util.Arrays;

/**
 * An immutable definition of a database table, which stores any number of rows.  The table definition specifies an
 * ordered list of typed fields with possible constraints.  A single-instance concrete subclass of this class should
 * exist for each defined table, and that class can have utility methods to create criteria or updated value objects for
 * common operations on that table's data.
 *
 * <p>
 * A table exists within a schema (a named collection of tables, typically implemented in a single database product or
 * installation).  The table object stores the unique name of its schema, its unique table name, and a list of the
 * fields that rows in that table may contain.
 *
 * <p>
 * Table objects are suitable for use as map keys, and implement {@code Comparable&lt;Table&gt;} so they can be used in
 * contexts that require a well-defined order (such as keys in a {@code TreeMap}).  Order is based on schema name and
 * then table name, using {@code String}'s default ordering for each.
 *
 * <p>
 * Typically, concrete subclasses of the {@code Table} class will define their fields, with all required constraints, as
 * class-static instances of {@code Field} with names that are upper-case underscore-separated versions of the field's
 * name.  The field's name itself is often required by specific implementations to map to an actual field name in a
 * database product, so it should limit itself to lowercase ASCII letters, digits, and underscores.  For example, a
 * field with the name "user_id" would typically be defined by a class-static instance of Field named "USER_ID".
 *
 * <p>
 * A subclass of {@code Table} should provide static utility methods to retrieve field values from rows with their
 * proper type.  These methods should take a single {@link Row} argument, and should have a method name that is the
 * CamelCase rendition of the field name, plus "Of".
 *
 * <p>
 * For example, if a table defines a String field named "last_name", it should provide a static method with this
 * signature:
 *
 * <pre>
 *   	static String lastNameOf(Row row)
 * </pre>
 * <p>
 * This method should test that the row's owning table matches the single instance of the subclass of {@code Table} that
 * owns the field, but it does not need to test the data type of the value, since it will have been validated on
 * construction.
 */
public class Table implements Comparable<Table> {

    /** The schema name. */
    private final String schema;

    /** The table group name. */
    private final String group;

    /** The table name. */
    private final String name;

    /** The HTML table description (shown in auto-generated documentation). */
    private final String description;

    /** An optional HTML description with some examples or use-cases of rows in the table. */
    private final String examples;

    /** The ordered list of fields. */
    private final Field[] fields;

    /**
     * Constructs a new {@code Table}.
     *
     * @param theSchema      the schema name
     * @param theGroup       the table group name
     * @param theName        the table name
     * @param theDescription the table description (shown in auto-generated documentation)
     * @param theExamples    optional HTML description with some examples or use-cases of rows in the table
     * @param theFields      the ordered list of fields
     * @throws IllegalArgumentException if the schema, group, or table name is {@code null} or blank or is not a valid
     * identifier, if there are no fields provides, or if an entry in the fields array is {@code null}
     */
    public Table(final String theSchema, final String theGroup, final String theName, final String theDescription,
                 final String theExamples, final Field... theFields) throws IllegalArgumentException {

        if (theSchema == null || theSchema.isBlank()) {
            throw new IllegalArgumentException("Schema name may not be null or blank");
        }
        if (theGroup == null || theGroup.isBlank()) {
            throw new IllegalArgumentException("Table group name may not be null or blank");
        }
        if (theName == null || theName.isBlank()) {
            throw new IllegalArgumentException("Table name may not be null or blank");
        }
        if (theDescription == null || theDescription.isBlank()) {
            throw new IllegalArgumentException("Table description may not be null or blank");
        }

        if (NameUtils.isInvalidName(theSchema)) {
            throw new IllegalArgumentException("Schema name is not a valid identifier");
        }
        if (NameUtils.isInvalidName(theGroup)) {
            throw new IllegalArgumentException("Table group name is not a valid identifier");
        }
        if (NameUtils.isInvalidName(theName)) {
            throw new IllegalArgumentException("Table name is not a valid identifier");
        }

        if (theFields == null || theFields.length == 0) {
            throw new IllegalArgumentException("Field list may not be null or empty");
        }
        for (final Field test : theFields) {
            if (test == null) {
                throw new IllegalArgumentException("Field list may not contain null values");
            }
        }

        this.schema = theSchema;
        this.group = theGroup;
        this.name = theName;
        this.description = theDescription;
        this.examples = theExamples;
        this.fields = theFields.clone();
    }

    /**
     * Gets the schema name.
     *
     * @return the schema name
     */
    public final String getSchema() {

        return this.schema;
    }

    /**
     * Gets the table group name.
     *
     * @return the table group name
     */
    public final String getGroup() {

        return this.group;
    }

    /**
     * Gets the table name.
     *
     * @return the table name
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
     * Gets the optional examples.
     *
     * @return the examples (null if none)
     */
    public final String getExamples() {

        return this.examples;
    }

    /**
     * Gets the number of fields.
     *
     * @return the number of fields
     */
    public final int getNumFields() {

        return this.fields.length;
    }

    /**
     * Gets a specific field definition.
     *
     * @param index the zero-based index of the field
     * @return the field definition
     */
    public final Field getField(final int index) {

        return this.fields[index];
    }

    /**
     * Gets the array of field definitions.
     *
     * @return the field array
     */
    private Field[] getFields() {

        return this.fields;
    }

    /**
     * Generates a hash code for the object.
     *
     * @return the hash code
     */
    @Override
    public final int hashCode() {

        return this.schema.hashCode() + this.group.hashCode() + this.name.hashCode() + Arrays.hashCode(this.fields);
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
        } else if (obj instanceof final Table objTable) {
            final String objSchema = objTable.getSchema();
            final String objGroup = objTable.getGroup();
            final String objName = objTable.getName();
            final Field[] objFields = objTable.getFields();

            equal = this.schema.equals(objSchema) && this.group.equals(objGroup)  && this.name.equals(objName)
                    && Arrays.equals(this.fields, objFields);
        } else {
            equal = false;
        }

        return equal;
    }

    /**
     * Compares this object with the specified object for order. Returns a negative integer, zero, or a positive integer
     * as this object is less than, equal to, or greater than the specified object.  Sort order is based on schema
     * name first, then table group name, then table name.
     *
     * @param o the object to be compared
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
     *         the specified object
     */
    @Override
    public final int compareTo(final Table o) {

        final String oSchema = o.getSchema();
        int result = this.schema.compareTo(oSchema);

        if (result == 0) {
            final String oGroup = o.getGroup();
            result = this.group.compareTo(oGroup);

            if (result == 0) {
                final String oName = o.getName();
                result = this.name.compareTo(oName);
            }
        }

        return result;
    }

    /**
     * Generates a diagnostic string representation of the object.
     *
     * @return the string representation
     */
    @Override
    public final String toString() {

        final String fieldsStr = Arrays.toString(this.fields);

        return SimpleBuilder.concat("Table{schema='", this.schema, "',group='", this.group, "',name='", this.name,
                "',fields=", fieldsStr, "}");
    }
}
