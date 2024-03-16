package dev.mathops.persistence.site.session;

/**
 * The result of a login operation, which either results in a session or an error.
 */
public class LoginResult {

    /** The session. */
    private final Session session;

    /** The error message. */
    private final String error;

    /**
     * Constructs a new {@code LoginResult} with a valid session.
     *
     * @param theSession the session
     */
    LoginResult(final Session theSession) {

        this.session = theSession;
        this.error = null;
    }

    /**
     * Constructs a new {@code LoginResult} with an error message.
     *
     * @param theError the error message
     */
    LoginResult(final String theError) {

        this.session = null;
        this.error = theError;
    }

    /**
     * Gets the session.
     *
     * @return the session ({@code null} if the result was an error)
     */
    public Session getSession() {

        return this.session;
    }

    /**
     * Gets the error message.
     *
     * @return the error message ({@code null} if the result was a valid session)
     */
    public String getError() {

        return this.error;
    }
}
