package dev.mathops.schema;

import dev.mathops.persistence.EFieldRole;
import dev.mathops.persistence.EFieldType;
import dev.mathops.persistence.Field;
import dev.mathops.persistence.SelectionCriteria;
import dev.mathops.persistence.Table;
import dev.mathops.persistence.constraint.StringEnumeratedConstraint;
import dev.mathops.persistence.constraint.StringLengthConstraint;
import dev.mathops.persistence.criteria.ENumericMatchType;
import dev.mathops.persistence.criteria.IntegerFieldCriterion;

/**
 * The "LocalLogin" table specification within the "system" schema of the "main" tablespace.
 */
public final class LocalLoginTable extends Table {

    /** The table description (HTML). */
    private static final String DESCR = "<p>Each row represents a set of login credentials with which a user can log "
            + "into the system.  This is separate from any institutional centralized login system, and can be used "
            + "for system administrators to access the system.</p>"
            + "<p>This can also be used to provide a way for someone not affiliated with the institution to log "
            + "in.</p>";

    /** The unique login name. */
    private static final Field F_LOGIN;

    /** The user ID. */
    private static final Field F_USER_ID;

    /** The type of user, such as administrator, director, proctor, tutor, student, etc. */
    private static final Field F_USER_TYPE;

    /** The user's email address – must be unique within this table. */
    private static final Field F_EMAIL;

    /** A salt value for the SCRAM-SHA-256 algorithm, as a 32-character string. */
    private static final Field F_SALT;

    /** A SCRAM-SHA-256 stored key, in hexadecimal, 64 characters in length. */
    private static final Field F_STORED_KEY;

    /** A SCRAM-SHA-256 server key, in hexadecimal, 64 characters in length. */
    private static final Field F_SERVER_KEY;

    /** The date/time the login was created. */
    private static final Field F_WHEN_CREATED;

    /** An optional expiration date/time. */
    private static final Field F_WHEN_EXPIRES;

    /** The date/time the user last successfully logged in. */
    private static final Field F_WHEN_LAST_LOGGED_IN;

    /** The date/time the user last failed to log in. */
    private static final Field F_WHEN_LAST_FAILED;

    /** The number of failed logins since the last successful login. */
    private static final Field F_NBR_FAILED_LOGINS;

    /** A flag indicating the user should be forced to change their password on their next login. */
    private static final Field F_FORCE_PWD_CHANGE;

    /** The single instance. */
    public static final LocalLoginTable INSTANCE;

    static {
        F_LOGIN = new Field("login", EFieldType.STRING, EFieldRole.PARTITION_KEY,
                "A unique login name (users may be able to log in using their email address as well).",
                new StringLengthConstraint("login_length", 6, Integer.MAX_VALUE));
        F_USER_ID = new Field("user_id", EFieldType.STRING, EFieldRole.NOT_NULL,
                "The user ID.");
        F_USER_TYPE = new Field("user_type", EFieldType.STRING, EFieldRole.NOT_NULL,
                "The type of user, such as administrator, director, proctor, tutor, student, etc.");
        F_EMAIL = new Field("email", EFieldType.STRING, EFieldRole.NOT_NULL,
                "The user's email address – must be unique within this table.");
        F_SALT = new Field("salt", EFieldType.STRING, EFieldRole.NOT_NULL,
                "A salt value for the SCRAM-SHA-256 algorithm, as a 32-character string.");
        F_STORED_KEY = new Field("stored_key", EFieldType.STRING, EFieldRole.NOT_NULL,
                "A SCRAM-SHA-256 stored key, in hexadecimal, 64 characters in length.");
        F_SERVER_KEY = new Field("server_key", EFieldType.STRING, EFieldRole.NOT_NULL,
                "A SCRAM-SHA-256 server key, in hexadecimal, 64 characters in length.");
        F_WHEN_CREATED = new Field("when_created", EFieldType.LOCAL_DATE_TIME, EFieldRole.NOT_NULL,
                "The date/time the login was created.");
        F_WHEN_EXPIRES = new Field("when_expires", EFieldType.LOCAL_DATE_TIME, EFieldRole.NULLABLE,
                "An optional expiration date/time.");
        F_WHEN_LAST_LOGGED_IN = new Field("when_last_logged_in", EFieldType.LOCAL_DATE_TIME, EFieldRole.NULLABLE,
                "The date/time the user last successfully logged in.");
        F_WHEN_LAST_FAILED = new Field("when_last_failed", EFieldType.LOCAL_DATE_TIME, EFieldRole.NULLABLE,
                "The date/time the user last failed to log in.");
        F_NBR_FAILED_LOGINS = new Field("nbr_failed_logins", EFieldType.INTEGER, EFieldRole.NOT_NULL,
                "The number of failed logins since the last successful login.");
        F_FORCE_PWD_CHANGE = new Field("force_pwd_change", EFieldType.BOOLEAN, EFieldRole.NOT_NULL,
                "A flag indicating the user should be forced to change their password on their next login.");

        INSTANCE = new LocalLoginTable();
    }

    /**
     * Constructs a new {@code LocalLoginTable}.
     */
    private LocalLoginTable() {

        super("main.system", "local_login", DESCR, null, F_LOGIN, F_USER_ID, F_USER_TYPE, F_EMAIL, F_SALT, F_STORED_KEY,
                F_SERVER_KEY, F_WHEN_CREATED, F_WHEN_EXPIRES, F_WHEN_LAST_LOGGED_IN, F_WHEN_LAST_FAILED,
                F_NBR_FAILED_LOGINS, F_FORCE_PWD_CHANGE);
    }
}
