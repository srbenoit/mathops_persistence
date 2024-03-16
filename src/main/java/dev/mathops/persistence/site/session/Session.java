package dev.mathops.persistence.site.session;

/**
 * A login session.
 */
public final class Session {

    /** The session ID. */
    private final String id;

    /** The login (this defines the roles granted to the user). */
    private final Login login;

    /**
     * A logged-in session.
     *
     * @param theLogin the login (this defines the roles granted to the user)
     */
    Session(final String theId, final Login theLogin) {

        this.id = theId;
        this.login = theLogin;
    }

    /**
     * Gets the session ID.
     *
     * @return the session ID
     */
    public String getId() {

        return this.id;
    }

    /**
     * Gets the login.
     *
     * @return the login
     */
    public Login getLogin() {

        return this.login;
    }
}
