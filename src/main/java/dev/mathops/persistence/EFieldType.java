package dev.mathops.persistence;

import dev.mathops.commons.builder.SimpleBuilder;
import dev.mathops.commons.log.Log;

import java.sql.Blob;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

/**
 * Supported types of database field.
 */
public enum EFieldType {

    /** Java String. */
    STRING(String.class),

    /** Java Boolean. */
    BOOLEAN(Boolean.class),

    /** Java Byte. */
    BYTE(Byte.class),

    /** Java Integer. */
    INTEGER(Integer.class),

    /** Java Long. */
    LONG(Long.class),

    /** Java Float. */
    FLOAT(Float.class),

    /** Java Double. */
    DOUBLE(Double.class),

    /** Java byte array. */
    BLOB(Blob.class),

    /** Java LocalDate. */
    LOCAL_DATE(LocalDate.class),

    /** Java LocalTime. */
    LOCAL_TIME(LocalTime.class),

    /** Java LocalDateTime. */
    LOCAL_DATE_TIME(LocalDateTime.class);

    /** The type class. */
    public final Class<?> cls;

    /**
     * Constructs a new {@code EFieldType}.
     *
     * @param theCls the value class
     */
    EFieldType(final Class<?> theCls) {

        this.cls = theCls;
    }

    /**
     * Generates a diagnostic string representation of the object.
     *
     * @return the string representation
     */
    @Override
    public String toString() {

        final String clsName = this.cls.getName();

        return SimpleBuilder.concat("EFieldType{cls=", clsName, "}");
    }

    /**
     * Returns the field type with a specified name.
     * @param name the name (case-insensitive)
     * @return the field type
     */
    public static EFieldType forName(final String name) {

        EFieldType result = null;

        try {
            final String upper = name.toUpperCase(Locale.ROOT);
            result = EFieldType.valueOf(upper);
        } catch (final IllegalArgumentException ex) {
            Log.warning("Attempt to parse '", name, "' as field type.");
        }

        return result;
    }
}
