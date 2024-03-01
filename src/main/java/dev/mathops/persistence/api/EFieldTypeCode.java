package dev.mathops.persistence.api;

/**
 * The set of defined field type codes.
 */
public enum EFieldTypeCode {

    /** An ASCII string whose length can be described by an unsigned 1-byte value. */
    ASCII_1((byte) 0x01),

    /** An ASCII string whose length can be described by an unsigned 2-byte value. */
    ASCII_2((byte) 0x02),

    /** An ASCII string whose length can be described by a signed 4-byte value. */
    ASCII_4((byte) 0x03),

    /** A Unicode string whose length can be described by an unsigned 1-byte value. */
    STRING_1((byte) 0x04),

    /** A Unicode string whose length can be described by an unsigned 2-byte value. */
    STRING_2((byte) 0x05),

    /** A Unicode string whose length can be described by a signed 4-byte value. */
    STRING_4((byte) 0x06),

    /** A Boolean value, represented as a byte with value 0x00 (FALSE) or 0x01 (TRUE). */
    BOOLEAN((byte) 0x07),

    /** An 8-bit signed integer or enumerated value. */
    BYTE((byte) 0x08),

    /** A 16-bit signed integer or enumerated value. */
    SHORT((byte) 0x09),

    /** A 32-bit signed integer or enumerated value. */
    INTEGER((byte) 0x0A),

    /** A 64-bit signed long integer value. */
    LONG((byte) 0x0B),

    /** A 32-bit floating point value in IEEE 754 format. */
    FLOAT((byte) 0x0C),

    /** A 64-bit floating point value in IEEE 754 format. */
    DOUBLE((byte) 0x0D),

    /**
     * A local date, encoded as a 32-bit integer whose low-order 5 bits are the unsigned day of the month (1-31), next
     * highest 4 bits  are the unsigned month (1 to 12), and remaining high-order 23 bits are the signed year.
     */
    LOCAL_DATE((byte) 0x0E),

    /**
     * A local time, encoded as a 32-bit integer whose low-order 10 bits are the unsigned millisecond (0 to 999), next
     * highest 6 bits are the unsigned second (0 to 59), next highest 6 bits are the unsigned minute (0 to 59), and
     * next highest 5 bits are the unsigned hour (0 to 23).  The remaining 5 bits should be zero.
     */
    LOCAL_TIME((byte) 0x0F),

    /** A local date and local time, encoded as a local date (as above) followed by a local time (as above). */
    LOCAL_DATE_TIME((byte) 0x10),

    /** A raw byte array whose length can be described by an unsigned 1-byte value. */
    BLOB_1((byte) 0x11),

    /** A raw byte array whose length can be described by an unsigned 2-byte value. */
    BLOB_2((byte) 0x12),

    /** A raw byte array whose length can be described by a signed 4-byte value. */
    BLOB_4((byte) 0x13),

    /** A table definition object. */
    TABLE((byte) 0x20),

    /** A row object. */
    ROW((byte) 0x21),

    /** A selection criteria object. */
    SELECTION_CRITERIA((byte) 0x22),

    /** A new values  object. */
    NEW_VALUES((byte) 0x23),

    // NOTE: Type codes 0x30 through 0xFF represent a "Tinyint", and encode an integer value from 0x00 through 0xCF
    // directly (the integer value is the type code minus 0x30).

    ;

    /** The byte value. */
    final byte value;

    /**
     * Constructs a new {@code EFieldTypeCode}
     *
     * @param theValue the value
     */
    EFieldTypeCode(final byte theValue) {

        this.value = theValue;
    }
}
