package dev.mathops.persistence.api;

import dev.mathops.db.table.Row;
import dev.mathops.db.table.SelectionCriteria;
import dev.mathops.db.table.UpdatedValues;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Utility methods to encode field values and append to byte streams.
 *
 * <p>
 * A byte stream consists of a sequence of fields, each preceded by a type code (as defined in {@code EFieldTypeCode})
 * to indicate the field's type.  The type dictates the length and structure of field contents.
 *
 * @see EFieldTypeCode
 */
public enum FieldEncoder {
    ;

    /** Mask value for a single byte. */
    private static final int BYTE_MASK = 0xFF;

    /** Mask value for two bytes (a "short"). */
    private static final int SHORT_MASK = 0xFFFF;

    /** Mask value for a single byte. */
    private static final long BYTE_MASK_LONG = 0xFFL;

    /** Mask value for two bytes (a "short"). */
    private static final long SHORT_MASK_LONG = 0xFFFFL;

    /** Mask value for four bytes (an "integer"). */
    private static final long INT_MASK_LONG = 0xFFFFFFFFL;

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

    /** Maximum value that can be represented as a "Tinyint". */
    private static final int MAX_TINYINT = 0xCF;

    /** Maximum ASCII value. */
    private static final int MAX_ASCII = 0x7E;

    /** Maximum length that can be encoded in one byte. */
    private static final int MAX_1BYTE_LEN = 256;

    /** Maximum length that can be encoded in two bytes. */
    private static final int MAX_2BYTE_LEN = 65536;

    /** Maximum length that can be encoded in two bytes. */
    private static final int MAX_YEAR = 4194303;

    /** Maximum length that can be encoded in two bytes. */
    private static final int MIN_YEAR = -4194304;

    /** Minimum value for a type code that represents a "tiny int". */
    private static final int MIN_TINYINT_CODE = 0x30;

    /**
     * Encodes a {@code String} to an output stream.
     *
     * @param toEncode the value to encode
     * @param out      the output stream
     * @throws IOException              if the output stream could not be written
     * @throws IllegalArgumentException if the object could not be parsed
     */
    static void encode(final String toEncode, final OutputStream out) throws IllegalArgumentException, IOException {

        if (toEncode == null) {
            throw new IllegalArgumentException("Value to encode may not be null");
        }

        final int len = toEncode.length();

        boolean isAscii = true;
        for (int i = 0; i < len; ++i) {
            final int value = (int) toEncode.charAt(i);
            if (value > MAX_ASCII) {
                isAscii = false;
                break;
            }
        }

        if (isAscii) {
            if (len < MAX_1BYTE_LEN) {
                out.write((int) EFieldTypeCode.ASCII_1.value);
                out.write(len);
            } else if (len < MAX_2BYTE_LEN) {
                out.write((int) EFieldTypeCode.ASCII_2.value);
                out.write(len >> BYTE1_SHIFT);
                out.write(len & BYTE_MASK);
            } else {
                out.write((int) EFieldTypeCode.ASCII_4.value);
                out.write((len >> BYTE3_SHIFT) & HI_BYTE_MASK);
                out.write((len >> BYTE2_SHIFT) & BYTE_MASK);
                out.write((len >> BYTE1_SHIFT) & BYTE_MASK);
                out.write(len & BYTE_MASK);
            }

            for (int i = 0; i < len; ++i) {
                final int value = (int) toEncode.charAt(i);
                out.write(value);
            }
        } else {
            if (len < MAX_1BYTE_LEN) {
                out.write((int) EFieldTypeCode.STRING_1.value);
                out.write(len);
            } else if (len < MAX_2BYTE_LEN) {
                out.write((int) EFieldTypeCode.STRING_2.value);
                out.write(len >> BYTE1_SHIFT);
                out.write(len & BYTE_MASK);
            } else {
                out.write((int) EFieldTypeCode.STRING_4.value);
                out.write((len >> BYTE3_SHIFT) & HI_BYTE_MASK);
                out.write((len >> BYTE2_SHIFT) & BYTE_MASK);
                out.write((len >> BYTE1_SHIFT) & BYTE_MASK);
                out.write(len & BYTE_MASK);
            }

            for (int i = 0; i < len; ++i) {
                final int value = (int) toEncode.charAt(i);
                out.write(value >> BYTE1_SHIFT);
                out.write(value & BYTE_MASK);
            }
        }
    }

    /**
     * Encodes a {@code Boolean} to an output stream.
     *
     * @param toEncode the value to encode
     * @param out      the output stream
     * @throws IOException              if the output stream could not be written
     * @throws IllegalArgumentException if the object could not be parsed
     */
    static void encode(final Boolean toEncode, final OutputStream out) throws IllegalArgumentException, IOException {

        if (toEncode == null) {
            throw new IllegalArgumentException("Value to encode may not be null");
        }

        out.write((int) EFieldTypeCode.BOOLEAN.value);
        final boolean value = toEncode.booleanValue();
        out.write(value ? 1 : 0);
    }

    /**
     * Encodes an integer value to an output stream.
     *
     * @param toEncode the value to encode
     * @param out      the output stream
     * @throws IOException              if the output stream could not be written
     * @throws IllegalArgumentException if the object could not be parsed
     */
    static void encode(final int toEncode, final OutputStream out) throws IOException {

        if (toEncode >= 0 && toEncode <= MAX_TINYINT) {
            final int fieldCode = MIN_TINYINT_CODE + toEncode;
            out.write(fieldCode);
        } else if ((toEncode & BYTE_MASK) == toEncode) {
            out.write((int) EFieldTypeCode.BYTE.value);
            out.write(toEncode);
        } else if ((toEncode & SHORT_MASK) == toEncode) {
            out.write((int) EFieldTypeCode.SHORT.value);
            out.write(toEncode >> BYTE1_SHIFT);
            out.write(toEncode & BYTE_MASK);
        } else {
            out.write((int) EFieldTypeCode.INTEGER.value);
            out.write((toEncode >> BYTE3_SHIFT) & HI_BYTE_MASK);
            out.write((toEncode >> BYTE2_SHIFT) & BYTE_MASK);
            out.write((toEncode >> BYTE1_SHIFT) & BYTE_MASK);
            out.write(toEncode & BYTE_MASK);
        }
    }

    /**
     * Encodes a long integer value to an output stream.
     *
     * @param toEncode the value to encode
     * @param out      the output stream
     * @throws IOException              if the output stream could not be written
     * @throws IllegalArgumentException if the object could not be parsed
     */
    static void encode(final long toEncode, final OutputStream out) throws IOException {

        if (toEncode >= 0L && toEncode <= (long) MAX_TINYINT) {
            final int fieldCode = MIN_TINYINT_CODE + (int) toEncode;
            out.write(fieldCode);
        } else if ((toEncode & BYTE_MASK_LONG) == toEncode) {
            final int intValue = (int) toEncode;
            out.write((int) EFieldTypeCode.BYTE.value);
            out.write(intValue);
        } else if ((toEncode & SHORT_MASK_LONG) == toEncode) {
            final int intValue = (int) toEncode;
            out.write((int) EFieldTypeCode.SHORT.value);
            out.write(intValue >> BYTE1_SHIFT);
            out.write(intValue & BYTE_MASK);
        } else if ((toEncode & INT_MASK_LONG) == toEncode) {
            final int intValue = (int) toEncode;
            out.write((int) EFieldTypeCode.INTEGER.value);
            out.write((intValue >> BYTE3_SHIFT) & HI_BYTE_MASK);
            out.write((intValue >> BYTE2_SHIFT) & BYTE_MASK);
            out.write((intValue >> BYTE1_SHIFT) & BYTE_MASK);
            out.write(intValue & BYTE_MASK);
        } else {
            out.write((int) EFieldTypeCode.LONG.value);
            out.write((int) (toEncode >> BYTE7_SHIFT) & HI_BYTE_MASK);
            out.write((int) (toEncode >> BYTE6_SHIFT) & BYTE_MASK);
            out.write((int) (toEncode >> BYTE5_SHIFT) & BYTE_MASK);
            out.write((int) (toEncode >> BYTE4_SHIFT) & BYTE_MASK);
            out.write((int) (toEncode >> BYTE3_SHIFT) & BYTE_MASK);
            out.write((int) (toEncode >> BYTE2_SHIFT) & BYTE_MASK);
            out.write((int) (toEncode >> BYTE1_SHIFT) & BYTE_MASK);
            out.write((int) toEncode & BYTE_MASK);
        }
    }

    /**
     * Encodes a 32-bit floating point value to an output stream.
     *
     * @param toEncode the value to encode
     * @param out      the output stream
     * @throws IOException              if the output stream could not be written
     * @throws IllegalArgumentException if the object could not be parsed
     */
    static void encode(final float toEncode, final OutputStream out) throws IOException {

        final int bits = Float.floatToIntBits(toEncode);

        out.write((int) EFieldTypeCode.FLOAT.value);
        out.write((bits >> BYTE3_SHIFT) & HI_BYTE_MASK);
        out.write((bits >> BYTE2_SHIFT) & BYTE_MASK);
        out.write((bits >> BYTE1_SHIFT) & BYTE_MASK);
        out.write(bits & BYTE_MASK);
    }

    /**
     * Encodes a 64-bit floating point value to an output stream.
     *
     * @param toEncode the value to encode
     * @param out      the output stream
     * @throws IOException              if the output stream could not be written
     * @throws IllegalArgumentException if the object could not be parsed
     */
    static void encode(final double toEncode, final OutputStream out) throws IOException {

        final long bits = Double.doubleToLongBits(toEncode);

        out.write((int) EFieldTypeCode.FLOAT.value);
        out.write((int) (bits >> BYTE7_SHIFT) & HI_BYTE_MASK);
        out.write((int) (bits >> BYTE6_SHIFT) & BYTE_MASK);
        out.write((int) (bits >> BYTE5_SHIFT) & BYTE_MASK);
        out.write((int) (bits >> BYTE4_SHIFT) & BYTE_MASK);
        out.write((int) (bits >> BYTE3_SHIFT) & BYTE_MASK);
        out.write((int) (bits >> BYTE2_SHIFT) & BYTE_MASK);
        out.write((int) (bits >> BYTE1_SHIFT) & BYTE_MASK);
        out.write((int) bits & BYTE_MASK);
    }

    /**
     * Encodes a {@code LocalDate} value to an output stream.
     *
     * @param toEncode the value to encode
     * @param out      the output stream
     * @throws IOException              if the output stream could not be written
     * @throws IllegalArgumentException if the object could not be parsed
     */
    static void encode(final LocalDate toEncode, final OutputStream out) throws IllegalArgumentException, IOException {

        if (toEncode == null) {
            throw new IllegalArgumentException("Value to encode may not be null");
        }

        final int year = toEncode.getYear();

        if (year > MAX_YEAR || year < MIN_YEAR) {
            throw new IllegalArgumentException("LocalDate year outside range that can be represented.");
        }

        final int day = toEncode.getDayOfMonth();
        final int month = toEncode.getMonthValue();

        out.write((int) EFieldTypeCode.LOCAL_DATE.value);
        final int dateValue = (year << 9) | (month << 5) | day;
        out.write((dateValue >> BYTE3_SHIFT) & HI_BYTE_MASK);
        out.write((dateValue >> BYTE2_SHIFT) & BYTE_MASK);
        out.write((dateValue >> BYTE1_SHIFT) & BYTE_MASK);
        out.write(dateValue & BYTE_MASK);
    }

    /**
     * Encodes a {@code LocalTime} value to an output stream.
     *
     * @param toEncode the value to encode
     * @param out      the output stream
     * @throws IOException              if the output stream could not be written
     * @throws IllegalArgumentException if the object could not be parsed
     */
    static void encode(final LocalTime toEncode, final OutputStream out) throws IllegalArgumentException, IOException {

        if (toEncode == null) {
            throw new IllegalArgumentException("Value to encode may not be null");
        }

        final int hour = toEncode.getHour();
        final int min = toEncode.getMinute();
        final int sec = toEncode.getSecond();
        final int ms = toEncode.getNano() / 1000000;

        out.write((int) EFieldTypeCode.LOCAL_TIME.value);

        final int timeValue = (hour << 22) | (min << 16) | (sec << 10) | ms;
        out.write((timeValue >> BYTE3_SHIFT) & HI_BYTE_MASK);
        out.write((timeValue >> BYTE2_SHIFT) & BYTE_MASK);
        out.write((timeValue >> BYTE1_SHIFT) & BYTE_MASK);
        out.write(timeValue & BYTE_MASK);
    }

    /**
     * Encodes a {@code LocalDateTime} value to an output stream.
     *
     * @param toEncode the value to encode
     * @param out      the output stream
     * @throws IOException              if the output stream could not be written
     * @throws IllegalArgumentException if the object could not be parsed
     */
    static void encode(final LocalDateTime toEncode, final OutputStream out) throws IllegalArgumentException,
            IOException {

        if (toEncode == null) {
            throw new IllegalArgumentException("Value to encode may not be null");
        }

        final int year = toEncode.getYear();

        if (year > MAX_YEAR || year < MIN_YEAR) {
            throw new IllegalArgumentException("LocalDate year outside range that can be represented.");
        }

        final int day = toEncode.getDayOfMonth();
        final int month = toEncode.getMonthValue();

        out.write((int) EFieldTypeCode.LOCAL_DATE_TIME.value);

        final int dateValue = (year << 9) | (month << 5) | day;
        out.write((dateValue >> BYTE3_SHIFT) & HI_BYTE_MASK);
        out.write((dateValue >> BYTE2_SHIFT) & BYTE_MASK);
        out.write((dateValue >> BYTE1_SHIFT) & BYTE_MASK);
        out.write(dateValue & BYTE_MASK);

        final int hour = toEncode.getHour();
        final int min = toEncode.getMinute();
        final int sec = toEncode.getSecond();
        final int ms = toEncode.getNano() / 1000000;

        final int timeValue = (hour << 22) | (min << 16) | (sec << 10) | ms;
        out.write((timeValue >> BYTE3_SHIFT) & HI_BYTE_MASK);
        out.write((timeValue >> BYTE2_SHIFT) & BYTE_MASK);
        out.write((timeValue >> BYTE1_SHIFT) & BYTE_MASK);
        out.write(timeValue & BYTE_MASK);
    }

    /**
     * Encodes a byte array (a "BLOB") to an output stream.
     *
     * @param toEncode the value to encode
     * @param out      the output stream
     * @throws IOException              if the output stream could not be written
     * @throws IllegalArgumentException if the object could not be parsed
     */
    static void encode(final byte[] toEncode, final OutputStream out) throws IllegalArgumentException, IOException {

        if (toEncode == null) {
            throw new IllegalArgumentException("Value to encode may not be null");
        }

        final int len = toEncode.length;

        if (len < MAX_1BYTE_LEN) {
            out.write((int) EFieldTypeCode.BLOB_1.value);
            out.write(len);
        } else if (len < MAX_2BYTE_LEN) {
            out.write((int) EFieldTypeCode.BLOB_2.value);
            out.write(len >> BYTE1_SHIFT);
            out.write(len & BYTE_MASK);
        } else {
            out.write((int) EFieldTypeCode.BLOB_4.value);
            out.write((len >> BYTE3_SHIFT) & HI_BYTE_MASK);
            out.write((len >> BYTE2_SHIFT) & BYTE_MASK);
            out.write((len >> BYTE1_SHIFT) & BYTE_MASK);
            out.write(len & BYTE_MASK);
        }

        out.write(toEncode);
    }

    /**
     * Encodes a {@code SelectionCriteria} value to an output stream.
     *
     * @param toEncode the value to encode
     * @param out      the output stream
     * @throws IOException              if the output stream could not be written
     * @throws IllegalArgumentException if the object could not be parsed
     */
    static void encode(final SelectionCriteria toEncode, final OutputStream out) throws IllegalArgumentException,
            IOException {

        if (toEncode == null) {
            throw new IllegalArgumentException("Value to encode may not be null");
        }

        // TODO:
    }

    /**
     * Encodes a {@code Row} value to an output stream.
     *
     * @param toEncode the value to encode
     * @param out      the output stream
     * @throws IOException              if the output stream could not be written
     * @throws IllegalArgumentException if the object could not be parsed
     */
    static void encode(final Row toEncode, final OutputStream out) throws IllegalArgumentException, IOException {

        if (toEncode == null) {
            throw new IllegalArgumentException("Value to encode may not be null");
        }

        // TODO:
    }

    /**
     * Encodes an {@code UpdatedValues} value to an output stream.
     *
     * @param toEncode the value to encode
     * @param out      the output stream
     * @throws IOException              if the output stream could not be written
     * @throws IllegalArgumentException if the object could not be parsed
     */
    static void encode(final UpdatedValues toEncode, final OutputStream out) throws IllegalArgumentException,
            IOException {

        if (toEncode == null) {
            throw new IllegalArgumentException("Value to encode may not be null");
        }

        // TODO:
    }
}
