package dev.mathops.schema.main;

import dev.mathops.db.table.EFieldRole;
import dev.mathops.db.table.EFieldType;
import dev.mathops.db.table.Field;
import dev.mathops.db.table.FieldDef;
import dev.mathops.db.table.Table;
import dev.mathops.db.table.constraint.StringLengthConstraint;

/**
 * The "LocalLogin" table specification within the "system" schema of the "main" tablespace.
 */
public final class LocalLoginTable extends Table {

    /** The table description (HTML). */
    private static final String DESCR = "<p>Each row represents a set of login credentials with which a user can log "
                                        + "into the system.  This is separate from any institutional centralized " +
                                        "login system, and can be used "
                                        + "for system administrators to access the system.</p>"
                                        + "<p>This can also be used to provide a way for someone not affiliated with " +
                                        "the institution to log "
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
        F_LOGIN = new Field(new FieldDef("login", EFieldType.STRING,
                "A unique login name (users may be able to log in using their email address as well).",
                new StringLengthConstraint("login_length", 6, Integer.MAX_VALUE)), EFieldRole.PARTITION_KEY);
        F_USER_ID = new Field(new FieldDef("user_id", EFieldType.STRING, "The user ID."),
                EFieldRole.NOT_NULL);
        F_USER_TYPE = new Field(new FieldDef("user_type", EFieldType.STRING,
                "The type of user, such as administrator, director, proctor, tutor, student, etc."),
                EFieldRole.NOT_NULL);
        F_EMAIL = new Field(new FieldDef("email", EFieldType.STRING,
                "The user's email address – must be unique within this table."),
                EFieldRole.NOT_NULL);
        F_SALT = new Field(new FieldDef("salt", EFieldType.STRING,
                "A salt value for the SCRAM-SHA-256 algorithm, as a 32-character string."),
                EFieldRole.NOT_NULL);
        F_STORED_KEY = new Field(new FieldDef("stored_key", EFieldType.STRING,
                "A SCRAM-SHA-256 stored key, in hexadecimal, 64 characters in length."),
                EFieldRole.NOT_NULL);
        F_SERVER_KEY = new Field(new FieldDef("server_key", EFieldType.STRING,
                "A SCRAM-SHA-256 server key, in hexadecimal, 64 characters in length."),
                EFieldRole.NOT_NULL);
        F_WHEN_CREATED = new Field(new FieldDef("when_created", EFieldType.LOCAL_DATE_TIME,
                "The date/time the login was created."),
                EFieldRole.NOT_NULL);
        F_WHEN_EXPIRES = new Field(new FieldDef("when_expires", EFieldType.LOCAL_DATE_TIME,
                "An optional expiration date/time."),
                EFieldRole.NULLABLE);
        F_WHEN_LAST_LOGGED_IN = new Field(new FieldDef("when_last_logged_in", EFieldType.LOCAL_DATE_TIME,
                "The date/time the user last successfully logged in."),
                EFieldRole.NULLABLE);
        F_WHEN_LAST_FAILED = new Field(new FieldDef("when_last_failed", EFieldType.LOCAL_DATE_TIME,
                "The date/time the user last failed to log in."),
                EFieldRole.NULLABLE);
        F_NBR_FAILED_LOGINS = new Field(new FieldDef("nbr_failed_logins", EFieldType.INTEGER,
                "The number of failed logins since the last successful login."),
                EFieldRole.NOT_NULL);
        F_FORCE_PWD_CHANGE = new Field(new FieldDef("force_pwd_change", EFieldType.BOOLEAN,
                "A flag indicating the user should be forced to change their password on their next login."),
                EFieldRole.NOT_NULL);

        INSTANCE = new LocalLoginTable();
    }

    /**
     * Constructs a new {@code LocalLoginTable}.
     */
    private LocalLoginTable() {

        super("main", "system", "local_login", DESCR, null, F_LOGIN, F_USER_ID, F_USER_TYPE, F_EMAIL, F_SALT,
                F_STORED_KEY,
                F_SERVER_KEY, F_WHEN_CREATED, F_WHEN_EXPIRES, F_WHEN_LAST_LOGGED_IN, F_WHEN_LAST_FAILED,
                F_NBR_FAILED_LOGINS, F_FORCE_PWD_CHANGE);
    }
}
