package dev.mathops.persistence.api;

import dev.mathops.db.table.Row;
import dev.mathops.db.table.SelectionCriteria;
import dev.mathops.db.table.Table;
import dev.mathops.db.table.UpdatedValues;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Utility methods to encode field values and append to byte streams and to consume bytes from byte streams and decode
 * as decode byte streams field values.
 *
 * <p>
 * A byte stream consists of a sequence of fields, each preceded by a type code (as defined in {@code EFieldTypeCode})
 * to indicate the field's type.  The type dictates the length and structure of field contents.
 *
 * @see EFieldTypeCode
 */
public enum FieldDecoder {
    ;

    /** Mask value for a single byte. */
    private static final int BYTE_MASK = 0xFF;

    /** Mask value for a single high-order byte. */
    private static final int HI_BYTE_MASK = 0x7F;

    /** Bits for a 1-byte shift. */
    private static final int BYTE1_SHIFT = 8;

    /** Bits for a 2-byte shift. */
    private static final int BYTE2_SHIFT = 16;

    /** Bits for a 3-byte shift. */
    private static final int BYTE3_SHIFT = 24;

    /** Bits for a 3-byte shift. */
    private static final int BYTE4_SHIFT = 32;

    /** Bits for a 3-byte shift. */
    private static final int BYTE5_SHIFT = 40;

    /** Bits for a 3-byte shift. */
    private static final int BYTE6_SHIFT = 48;

    /** Bits for a 3-byte shift. */
    private static final int BYTE7_SHIFT = 56;

    /** Minimum value for a type code that represents a "tiny int". */
    private static final int MIN_TINYINT_CODE = 0x30;

    /**
     * Attempts to extract an object from an input stream.
     *
     * @param in the input stream
     * @return the object
     * @throws IOException              if the input stream could not be read
     * @throws IllegalArgumentException if the object could not be parsed
     */
    static Object decode(final InputStream in) throws IllegalArgumentException, IOException {

        final Object result;

        final int typeCode = in.read() & BYTE_MASK;

        if (typeCode == (int) EFieldTypeCode.ASCII_1.value) {
            final int length = extractLength1(in);
            result = decodeAscii(in, length);
        } else if (typeCode == (int) EFieldTypeCode.ASCII_2.value) {
            final int length = extractLength2(in);
            result = decodeAscii(in, length);
        } else if (typeCode == (int) EFieldTypeCode.ASCII_4.value) {
            final int length = extractLength4(in);
            result = decodeAscii(in, length);
        } else if (typeCode == (int) EFieldTypeCode.STRING_1.value) {
            final int length = extractLength1(in);
            result = decodeString(in, length);
        } else if (typeCode == (int) EFieldTypeCode.STRING_2.value) {
            final int length = extractLength2(in);
            result = decodeString(in, length);
        } else if (typeCode == (int) EFieldTypeCode.STRING_4.value) {
            final int length = extractLength4(in);
            result = decodeString(in, length);
        } else if (typeCode == (int) EFieldTypeCode.BOOLEAN.value) {
            result = decodeBoolean(in);
        } else if (typeCode == (int) EFieldTypeCode.BYTE.value) {
            result = decodeByte(in);
        } else if (typeCode == (int) EFieldTypeCode.SHORT.value) {
            result = decodeShort(in);
        } else if (typeCode == (int) EFieldTypeCode.INTEGER.value) {
            result = decodeInteger(in);
        } else if (typeCode == (int) EFieldTypeCode.LONG.value) {
            result = decodeLong(in);
        } else if (typeCode == (int) EFieldTypeCode.FLOAT.value) {
            result = decodeFloat(in);
        } else if (typeCode == (int) EFieldTypeCode.DOUBLE.value) {
            result = decodeDouble(in);
        } else if (typeCode == (int) EFieldTypeCode.LOCAL_DATE.value) {
            result = decodeDate(in);
        } else if (typeCode == (int) EFieldTypeCode.LOCAL_TIME.value) {
            result = decodeTime(in);
        } else if (typeCode == (int) EFieldTypeCode.LOCAL_DATE_TIME.value) {
            result = decodeDateTime(in);
        } else if (typeCode == (int) EFieldTypeCode.BLOB_1.value) {
            final int length = extractLength1(in);
            result = decodeBlob(in, length);
        } else if (typeCode == (int) EFieldTypeCode.BLOB_2.value) {
            final int length = extractLength2(in);
            result = decodeBlob(in, length);
        } else if (typeCode == (int) EFieldTypeCode.BLOB_4.value) {
            final int length = extractLength4(in);
            result = decodeBlob(in, length);
        } else if (typeCode == (int) EFieldTypeCode.TABLE.value) {
            result = decodeTable(in);
        } else if (typeCode == (int) EFieldTypeCode.ROW.value) {
            result = decodeRow(in);
        } else if (typeCode == (int) EFieldTypeCode.SELECTION_CRITERIA.value) {
            result = decodeSelectionCriteria(in);
        } else if (typeCode == (int) EFieldTypeCode.NEW_VALUES.value) {
            result = decodeUpdatedValues(in);
        } else if (typeCode >= MIN_TINYINT_CODE) {
            result = Integer.valueOf(typeCode - MIN_TINYINT_CODE);
        } else {
            throw new IllegalArgumentException("Invalid type code");
        }

        return result;
    }

    /**
     * Extracts a 1-byte unsigned length.
     *
     * @param in the input stream
     * @return the length
     * @throws IOException if the input stream could not be read
     */
    private static int extractLength1(final InputStream in) throws IOException {

        final int v = in.read();

        return v & BYTE_MASK;
    }

    /**
     * Extracts a 2-byte unsigned length.
     *
     * @param in the input stream
     * @return the length
     * @throws IOException if the input stream could not be read
     */
    private static int extractLength2(final InputStream in) throws IOException {

        final int v1 = in.read();
        final int v2 = in.read();

        return ((v1 & BYTE_MASK) << BYTE1_SHIFT)
                + (v2 & BYTE_MASK);
    }

    /**
     * Extracts a 4-byte (31-bit unsigned) length.
     *
     * @param in the input stream
     * @return the length
     * @throws IOException if the input stream could not be read
     */
    private static int extractLength4(final InputStream in) throws IOException {

        final int v1 = in.read();
        final int v2 = in.read();
        final int v3 = in.read();
        final int v4 = in.read();

        return ((v1 & HI_BYTE_MASK) << BYTE3_SHIFT)
                + ((v2 & BYTE_MASK) << BYTE2_SHIFT)
                + ((v3 & BYTE_MASK) << BYTE1_SHIFT)
                + (v4 & BYTE_MASK);
    }

    /**
     * Extracts a 4-byte signed integer.
     *
     * @param in the input stream
     * @return the decoded integer
     * @throws IOException if the input stream could not be read
     */
    private static int extractInt(final InputStream in) throws IOException {

        final int v1 = in.read();
        final int v2 = in.read();
        final int v3 = in.read();
        final int v4 = in.read();

        return (v1 << BYTE3_SHIFT)
                + ((v2 & BYTE_MASK) << BYTE2_SHIFT)
                + ((v3 & BYTE_MASK) << BYTE1_SHIFT)
                + (v4 & BYTE_MASK);
    }


    /**
     * Decodes an 64-bit signed integer
     *
     * @param in the input stream
     * @return the decoded integer
     * @throws IOException if the input stream could not be read
     */
    private static long extractLong(final InputStream in) throws IOException {

        final int v1 = in.read();
        final int v2 = in.read();
        final int v3 = in.read();
        final int v4 = in.read();
        final int v5 = in.read();
        final int v6 = in.read();
        final int v7 = in.read();
        final int v8 = in.read();

        return  ((long) v1 << BYTE7_SHIFT)
                + ((long) (v2 & HI_BYTE_MASK) << BYTE6_SHIFT)
                + ((long) (v3 & HI_BYTE_MASK) << BYTE5_SHIFT)
                + ((long) (v4 & HI_BYTE_MASK) << BYTE4_SHIFT)
                + ((long) (v5 & HI_BYTE_MASK) << BYTE3_SHIFT)
                + ((long) (v6 & BYTE_MASK) << BYTE2_SHIFT)
                + ((long) (v7 & BYTE_MASK) << BYTE1_SHIFT)
                + (long) (v8 & BYTE_MASK);
    }


    /**
     * Decodes an ASCII string.
     *
     * @param in  the input stream
     * @param len the length
     * @return the decoded string
     * @throws IOException if the input stream could not be read
     */
    private static String decodeAscii(final InputStream in, final int len) throws IOException {

        final StringBuilder builder = new StringBuilder(len);

        for (int i = 0; i < len; ++i) {
            final char ch = (char) (in.read() & BYTE_MASK);
            builder.append(ch);
        }

        return builder.toString();
    }

    /**
     * Decodes a Unicode string.
     *
     * @param in  the input stream
     * @param len the length
     * @return the decoded string
     * @throws IOException if the input stream could not be read
     */
    private static String decodeString(final InputStream in, final int len) throws IOException {

        final StringBuilder builder = new StringBuilder(len);

        for (int i = 0; i < len; ++i) {
            final int v1 = in.read();
            final int v2 = in.read();

            final char ch = (char) (((v1 & BYTE_MASK) << BYTE1_SHIFT) + (v2 & BYTE_MASK));
            builder.append(ch);

        }

        return builder.toString();
    }

    /**
     * Decodes a Boolean.
     *
     * @param in the input stream
     * @return the decoded Boolean value
     * @throws IllegalArgumentException if an invalid value was detected
     * @throws IOException              if the input stream could not be read
     */
    private static Boolean decodeBoolean(final InputStream in) throws IllegalArgumentException, IOException {

        final int v1 = in.read();
        final Boolean result;

        if (v1 == 0) {
            result = Boolean.FALSE;
        } else if (v1 == 1) {
            result = Boolean.TRUE;
        } else {
            throw new IllegalArgumentException("Invalid Boolean value.");
        }

        return result;
    }

    /**
     * Decodes an 8-bit signed integer.
     *
     * @param in the input stream
     * @return the decoded integer
     * @throws IOException if the input stream could not be read
     */
    private static Integer decodeByte(final InputStream in) throws IOException {

        final int v1 = in.read();

        return Integer.valueOf(v1);
    }

    /**
     * Decodes an 16-bit signed integer.
     *
     * @param in the input stream
     * @return the decoded integer
     * @throws IOException if the input stream could not be read
     */
    private static Integer decodeShort(final InputStream in) throws IOException {

        final int v1 = in.read();
        final int v2 = in.read();

        final int combined = (v1 << BYTE1_SHIFT) + (v2 & BYTE_MASK);

        return Integer.valueOf(combined);
    }

    /**
     * Decodes an 32-bit signed integer.
     *
     * @param in the input stream
     * @return the decoded integer
     * @throws IOException if the input stream could not be read
     */
    private static Integer decodeInteger(final InputStream in) throws IOException {

        final int combined = extractInt(in);

        return Integer.valueOf(combined);
    }

    /**
     * Decodes an 64-bit signed integer.
     *
     * @param in the input stream
     * @return the decoded long integer
     * @throws IOException if the input stream could not be read
     */
    private static Long decodeLong(final InputStream in) throws IOException {

        final long combined = extractLong(in);

        return Long.valueOf(combined);
    }

    /**
     * Decodes an 32-bit signed floating point ":"float" number.
     *
     * @param in the input stream
     * @return the decoded float
     * @throws IOException if the input stream could not be read
     */
    private static Float decodeFloat(final InputStream in) throws IOException {

        final int combined = extractLength4(in);

        final float f = Float.intBitsToFloat(combined);

        return Float.valueOf(f);
    }

    /**
     * Decodes an 64-bit signed floating point "double" number.
     *
     * @param in the input stream
     * @return the decoded double
     * @throws IOException if the input stream could not be read
     */
    private static Double decodeDouble(final InputStream in) throws IOException {

        final long combined = extractLong(in);

        final double d = Double.longBitsToDouble(combined);

        return Double.valueOf(d);
    }

    /**
     * Decodes a local date value.
     *
     * <p>
     * A date is encoded as a 32-bit number whose low-order 5 bits are the unsigned day of the month (1-31), next
     * highest 4 bits  are the unsigned month (1 to 12), and remaining high-order 23 bits are the signed year.
     *
     * @param in the input stream
     * @return the decoded local date
     * @throws IOException if the input stream could not be read
     */
    private static LocalDate decodeDate(final InputStream in) throws IOException {

        final int combined = extractLength4(in);

        final int day = combined & 0x1F;
        final int month = (combined >> 5) & 0x0F;

        if (day == 0 || month == 0 || month > 12) {
            throw new IllegalArgumentException("Invalid date");
        }

        final int year = combined >> 9;

        return LocalDate.of(year, month, day);
    }

    /**
     * Decodes a local time value.
     *
     * <p>
     * A time is encoded as a 32-bit number whose low-order 10 bits are the unsigned millisecond (0 to 999), next
     * highest 6 bits are the unsigned second (0 to 59), next highest 6 bits are the unsigned minute (0 to 59), and next
     * highest 5 bits are the unsigned hour (0 to 23).  The remaining 5 bits should be zero.
     *
     * @param in the input stream
     * @return the decoded local time
     * @throws IOException if the input stream could not be read
     */
    private static LocalTime decodeTime(final InputStream in) throws IOException {

        final int combined = extractLength4(in);

        final int ms = combined & 0x03FF;
        final int sec = (combined >> 10) & 0x003F;
        final int min = (combined >> 16) & 0x003F;
        final int hr = combined >> 22;

        if (ms > 999 || sec > 59 || min > 59 || hr < 0 || hr > 23) {
            throw new IllegalArgumentException("Invalid time");
        }

        return LocalTime.of(hr, min, sec, ms * 1000000);
    }

    /**
     * Decodes a local date/time value.
     *
     * @param in the input stream
     * @return the decoded local date/time
     * @throws IOException if the input stream could not be read
     */
    private static LocalDateTime decodeDateTime(final InputStream in) throws IOException {

        final LocalDate date = decodeDate(in);
        final LocalTime time = decodeTime(in);

        return LocalDateTime.of(date, time);
    }

    /**
     * Decodes a BLOB.
     *
     * @param in  the input stream
     * @param len the length
     * @return the decoded BLOB as a byte array
     * @throws IOException if the input stream could not be read
     */
    private static byte[] decodeBlob(final InputStream in, final int len) throws IOException {

        final byte[] data = new byte[len];

        for (int i = 0; i < len; ++i) {
            data[i] = (byte) (in.read() & BYTE_MASK);
        }

        return data;
    }

    /**
     * Decodes a table definition.
     *
     * @param in the input stream
     * @return the decoded table definition
     * @throws IOException if the input stream could not be read
     */
    private static Table decodeTable(final InputStream in) throws IOException {

        // TODO:
        return null;
    }

    /**
     * Decodes a row.
     *
     * @param in the input stream
     * @return the decoded row
     * @throws IOException if the input stream could not be read
     */
    private static Row decodeRow(final InputStream in) throws IOException {

        // TODO:
        return null;
    }

    /**
     * Decodes a set of selection criteria.
     *
     * @param in the input stream
     * @return the decoded selection criteria
     * @throws IOException if the input stream could not be read
     */
    private static SelectionCriteria decodeSelectionCriteria(final InputStream in) throws IOException {

        // TODO:
        return null;
    }

    /**
     * Decodes a set of updated values.
     *
     * @param in the input stream
     * @return the decoded updated values
     * @throws IOException if the input stream could not be read
     */
    private static UpdatedValues decodeUpdatedValues(final InputStream in) throws IOException {

        // TODO:
        return null;
    }
}
