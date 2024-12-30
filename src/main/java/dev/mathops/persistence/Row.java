package dev.mathops.persistence;

import dev.mathops.persistence.constraint.AbstractFieldConstraint;
import dev.mathops.text.builder.SimpleBuilder;

import java.util.Arrays;

/**
 * An immutable generalized row object, which references a {@link Table} object (with field definitions), and which
 * stores an immutable array of field values.
 *
 * <p>
 * A row stores a reference to the table to which it belongs so field values can be interpreted at runtime.  Each field
 * value in a row has either the Java object type that corresponds to the type of the field, or is {@code null}.
 *
 * <p>
 * Field values are guaranteed to satisfy the constraints of the corresponding field definition.  When rows are queried
 * from the underlying database, they are validated against these constraints, and invalid rows are discarded, and the
 * event is logged.
 */
public final class Row {

    /** The table to which this row belongs. */
    private final Table table;

    /** Field values, where {@code null} values indicate fields whose field value in the database was NULL. */
    private final Object[] fieldValues;

    /**
     * Constructs a new {@code Row}.  The constructor ensures that the constructed object is valid with respect to
     * the fields define din the table.
     *
     * @param theTable the table to which this row belongs
     * @param theFieldValues the field values, indexed as in {@code theTable}, where trailing null values may be
     *                       omitted (if no values are permitted, the result is a row with all null field values)
     * @throws IllegalArgumentException if the table name is null, there are more field values provided than the
     * table defines, or a field value does not satisfy a field role or constraint
     */
    public Row(final Table theTable, final Object... theFieldValues) throws IllegalArgumentException {

        if (theTable == null) {
            throw new IllegalArgumentException("Table may not be null");
        }
        final int numDefined = theTable.getNumFields();

        this.table = theTable;

        this.fieldValues = new Object[numDefined];
        if (theFieldValues != null) {
            final int numProvided = theFieldValues.length;
            if (numProvided > numDefined) {
                throw new IllegalArgumentException("More field values provided than fields defined in the table.");
            }
            System.arraycopy(theFieldValues, 0, this.fieldValues, 0, numProvided);
        }

        // Verify that field values are compatible with roles and satisfy all constraints
        for (int i = 0; i < numDefined; ++i) {
            final Field field = theTable.getField(i);
            final Object value = this.fieldValues[i];

            if (value == null) {
                if (field.getRole() != EFieldRole.NULLABLE) {
                    final String fieldName = field.getName();
                    throw new IllegalArgumentException("Value of '" + fieldName
                            + "' field was null but this field does not allow nulls");
                }
            } else {
                final int numConstraints = field.getNumConstraints();
                for (int j = 0; j < numConstraints; ++j) {
                    final AbstractFieldConstraint<?> constraint = field.getConstraint(j);
                    if (!constraint.isValidValue(value)) {
                        final String fieldName = field.getName();
                        throw new IllegalArgumentException("Value of '" + fieldName
                                + "' field does not satisfy field constraints");
                    }
                }
            }
        }
    }

    /**
     * Gets a particular field value.
     *
     * @param index the field index
     * @return the value (could be
     */
    public Object getFieldValue(final int index) {

        return this.fieldValues[index];
    }

    /**
     * Generates a diagnostic string representation of the object.
     *
     * @return the string representation
     */
    @Override
    public String toString() {

        final String fieldValuesStr = Arrays.toString(this.fieldValues);

        return SimpleBuilder.concat("Row{table=", this.table, ", fieldValues=", fieldValuesStr, "}");
    }
}
