package dev.mathops.persistence;

import java.io.Serial;

/**
 * An exception performing a persistence operation.
 */
public class PersistenceException extends Exception  {

    /** Version for serialization. */
    @Serial
    private static final long serialVersionUID = 6325632612880128499L;

    /**
     * Constructs a {@code PersistenceException} object with no message.
     */
    public PersistenceException() {

        super();
    }

    /**
     *  Constructs a {@code PersistenceException} object with a given {@code message}.
     *
     * @param message a description of the exception
     */
    public PersistenceException(final String message) {

        super(message);
    }

    /**
     *  Constructs a {@code PersistenceException} object with a given {@code cause}.
     *
     * @param cause the underlying reason for this {@code PersistenceException}
     */
    public PersistenceException(final Throwable cause) {

        super(cause);
    }

    /**
     * Constructs a {@code PersistenceException} object with a given {@code message} and  {@code cause}.
     *
     * @param message a description of the exception
     * @param cause the underlying reason for this {@code PersistenceException}
     */
    public PersistenceException(final String message, final Throwable cause) {

        super(message,cause);
    }
}
