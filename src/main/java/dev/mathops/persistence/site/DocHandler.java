package dev.mathops.persistence.site;

import dev.mathops.commons.CoreConstants;
import dev.mathops.commons.builder.HtmlBuilder;
import dev.mathops.persistence.EFieldType;
import dev.mathops.persistence.Field;
import dev.mathops.persistence.Table;
import dev.mathops.persistence.constraint.AbstractFieldConstraint;
import dev.mathops.schema.AllTables;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A handler for Documentation requests.
 */
final class DocHandler {

    /** A common string. */
    private static final String A_CLASS_LIT2 = "<a class='lit2'";

    /** A common string. */
    private static final String A_CLASS_DIM2 = "<a class='dim2'";

    /** A header selector. */
    private static final String SCHEMA_OVERVIEW = "SchOverview";

    /** A header selector. */
    private static final String GEN_OVERVIEW = "GenOverview";

    /** A header selector. */
    private static final String GEN_STRUCTURES = "GenStructures";

    /** A header selector. */
    private static final String GEN_SELECTING = "GenSelecting";

    /** A header selector. */
    private static final String GEN_UPDATING = "GenUpdating";

    /** A header selector. */
    private static final String GEN_IMPL = "GenImpl";

    /** A header selector. */
    private static final String GEN_ENCODING = "GenEncoding";

    /** A header selector. */
    private static final String GEN_ENDPOINTS = "GenEndpoints";

    /** An HTTP status code. */
    private static final int STATUS_BAD_METHOD = 405;

    /** An HTTP status code. */
    private static final int STATUS_NOT_FOUND = 404;

    /** The number of characters of URI path used to select this handler. */
    private final int prefixLength;

    /** A map from schema to a map from group name to a list of tables in that group. */
    private final Map<String, Map<String, List<Table>>> tables;

    /**
     * Constructs a new {@code DocHandler}.
     *
     * @param thePrefixLength the number of characters of URI path used to select this handler
     */
    DocHandler(final int thePrefixLength) {

        this.prefixLength = thePrefixLength;

        final List<Table> allTables = AllTables.INSTANCE.tables;

        this.tables = new TreeMap<>();
        for (final Table table : allTables) {
            final String schema = table.getSchema();
            final int dotIndex = schema.indexOf('.');
            final String tablespace = dotIndex == -1 ? CoreConstants.EMPTY : schema.substring(0, dotIndex);
            final String name = dotIndex == -1 ? schema : schema.substring(dotIndex + 1);
            final Map<String, List<Table>> inner1 = this.tables.computeIfAbsent(tablespace, s -> new TreeMap<>());
            final List<Table> inner2 = inner1.computeIfAbsent(name, s -> new ArrayList<>(10));
            inner2.add(table);
        }
    }

    /**
     * Handles an HTTP exchange.
     *
     * @param reqPath the request path (including the prefix that selected this handler)
     * @param req     the HTTP servlet request
     * @param resp    the HTTP servlet response
     * @throws IOException if there is an error writing the response
     */
    void handleRequest(final String reqPath, final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {

        final String method = req.getMethod();

        if ("GET".equalsIgnoreCase(method)) {
            final String path = reqPath.substring(this.prefixLength);

//                Log.info("GET Path is: ", path);

            final HtmlBuilder htm = new HtmlBuilder(1000);
            boolean found = true;

            startPage(htm);
            emitHeader(htm);
            htm.sDiv(null, "style='padding:20px;'");

            if (path.isEmpty() || "/".equals(path) || "/index.html".equals(path)) {
                doGeneralIndex(htm);
            } else if ("/general1.html".equals(path)) {
                doGeneralDatabase(htm);
            } else if ("/general2.html".equals(path)) {
                doGeneralStructures(htm);
            } else if ("/general3.html".equals(path)) {
                doGeneralSelectionCriteria(htm);
            } else if ("/general4.html".equals(path)) {
                doGeneralUpdatedValues(htm);
            } else if ("/general5.html".equals(path)) {
                doGeneralImplementations(htm);
            } else if ("/general6.html".equals(path)) {
                doGeneralApiEncoding(htm);
            } else if ("/general7.html".equals(path)) {
                doGeneralApiEndpoints(htm);
            } else if ("/schemas.html".equals(path)) {
                doSchemas(htm);
            } else if ("/schema.html".equals(path)) {

                final Map<String, String[]> params = req.getParameterMap();
                final String[] schemaList = params.get("schema");
                if (schemaList == null || schemaList.length == 0) {
                    doSchemas(htm);
                } else {
                    final String schema = schemaList[0];
                    final String[] tableList = params.get("table");
                    if (tableList == null || tableList.length == 0) {
                        doSchema(htm, schema, null);
                    } else {
                        final String table = tableList[0];
                        doSchema(htm, schema, table);
                    }
                }
            } else {
                found = false;
            }

            if (found) {
                htm.eDiv();
                endPage(htm);

                final String htmString = htm.toString();
                final byte[] bytes = htmString.getBytes(StandardCharsets.UTF_8);
                ServiceSite.sendReply(req, resp, "text/html", bytes);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    /**
     * Emits the start of a management site page, including the embedded stylesheet.
     *
     * @param htm the {@code HtmlBuilder} to which to append
     */
    private static void startPage(final HtmlBuilder htm) {

        htm.addln("<html><head>");
        htm.addln(" <style>");
        htm.addln("  body {background-color:white;font-family:sans-serif;font-size:11pt;margin:0;}");
        htm.addln("  h1 {color:white;margin:0;font-size:25pt;font-weight:100;font-stretch:condensed;margin:0;}");
        htm.addln("  h1 strong {font-weight:700;color:#C8C372;}");
        htm.addln("  h2 {color:#1E4D2B;margin:0;font-size:20pt;font-weight:200;font-stretch:condensed;}");
        htm.addln("  h3 {color:#000;margin:0;font-size:16pt;font-weight:400;padding-top:8pt;font-stretch:condensed;}");
        htm.addln("  h3 strong {font-weight:700;}");
        htm.addln("  h4 {color:#105456;margin:0;font-size:13pt;font-weight:700;padding-top:8pt;font-stretch:normal;}");
        htm.addln("  h5 {color:#105456;margin:0;font-size:12pt;font-weight:700;padding-top:8pt;font-stretch:normal;}");
        htm.addln("  article h5 {padding-top:0;}");
        htm.addln("  a {font-family:sans-serif;font-size:11pt;}");
        htm.addln("  .flexbox {display:flex;align-content:flex-start;}");
        htm.addln("  nav {float:left;border-right:1px solid gray;border-left:1px solid gray;background-color:#ccc;",
                "padding:10px;margin-top:10px;}");
        htm.addln("  article {float:left;padding:0 20px 0 20px;margin-top:10px;width:auto;order:1;flex-grow:1;}");
        htm.addln("  code {color:#0070C0;font-weight:700;}");
        htm.addln("  th {text-align:left;background-color:#eee;}");
        htm.addln("  td {vertical-align:top; padding:5px;}");
        htm.addln("  .indent {margin-left:20px;}");
        htm.addln("  .redhead {color:red;font-style:italic;margin-top:4px;margin-bottom:2px;}");
        htm.addln("  p.innerhead {color:#006144;font-weight:700;margin-top:12px;margin-bottom:2px;}");
        htm.addln("  .red {color:#B00;font-weight:700;}");
        htm.addln("  .constraint {color:#D9782D;}");
        htm.addln("  .thin {margin-top:3px;margin-bottom:3px;}");
        htm.addln("  .vgap {min-height:15px;}");
        htm.addln("  .hgap {display:inline-block;min-width:30px;}");
        htm.addln(" </style>");
        htm.addln("</head><body>");
    }

    /**
     * Emits the end of a management site page, including the embedded stylesheet.
     *
     * @param htm the {@code HtmlBuilder} to which to append
     */
    private static void endPage(final HtmlBuilder htm) {

        htm.addln("</body></html>");
    }

    /**
     * Emits the page header.
     *
     * @param htm the {@code HtmlBuilder} to which to append
     */
    private static void emitHeader(final HtmlBuilder htm) {

        htm.sDiv(null, "style='background-color: #1E4D2B; margin:0; padding:6px 20px 8px 20px;'");
        htm.sH(1).add("MathOps <strong>Persistence Layer</strong>").eH(1);
        htm.eDiv();
    }

    /**
     * The main General index page.
     *
     * @param htm the {@code HtmlBuilder} to which to append
     */
    private void doGeneralIndex(final HtmlBuilder htm) {

        htm.sDiv(null,"style='margin:10px 30px;max-width:800px;'");
        htm.sH(2).add("System Documentation").eH(2);
        htm.div("vgap");

        htm.sDiv(null, "style='border-top:1px solid gray;border-bottom:1px solid gray;'");
        htm.sP().add("""
                This component of the MathOps system is designed to operate as a service on a database and/or file
                servers, or as a front-end to a cluster of such servers.  It’s purpose is to present a secure and
                flexible interface to the underlying data that insulates applications from implementation details
                and allows changes in underlying storage architectures and products without changes to application
                code or logic.""").eP();

        htm.sP().add("""
                The software is Java-based, and runs in a servlet engine such as Tomcat, Jetty, Glassfish, or Wildfly,
                typically behind a front-end such as Apache HTTPD or Nginx that handles the HTTP protocol and TLS
                encryption.""").eP();

        htm.sP(null, " style='margin-bottom:3px;'")
                .add("An typical installation of the Persistence Layer on a single server might include:").eP();

        htm.addln("<ul style='margin-top:0;'>");
        htm.addln("  <li>a database product such as PostgreSQL,</li>");
        htm.addln("  <li>a web server such as Apache HTTPD,</li>");
        htm.addln("  <li>a servlet container such as Tomcat, and</li>");
        htm.addln("  <li>the MathOps persistence layer installed as a servlet.</li>");
        htm.addln("</ul>");
        htm.eDiv(); // top and bottom line border

        htm.div("vgap");

        htm.sDiv(null, "style='margin:0;background:#eee;border:1px solid gray;padding:0 20px 15px 20px;'");

        htm.sH(3).add("Documentation Topics").eH(3);
        htm.sDiv("indent");
        
        htm.sH(4).add("Generalized Database System").eH(4);
        htm.sDiv("indent");
        htm.sP("thin").add("<a href='/doc/general1.html'>Overview</a>").eP();
        htm.sP("thin").add("<a href='/doc/general2.html'>Structures</a>").eP();
        htm.sP("thin").add("<a href='/doc/general3.html'>Selecting</a>").eP();
        htm.sP("thin").add("<a href='/doc/general4.html'>Updating</a>").eP();
        htm.sP("thin").add("<a href='/doc/general5.html'>Implementations</a>").eP();
        htm.sP("thin").add("<a href='/doc/general6.html'>API Encoding</a>").eP();
        htm.sP("thin").add("<a href='/doc/general7.html'>API Endpoints</a>").eP();
        htm.eDiv(); // indent

        htm.sH(4).add("Schemas").eH(4);
        htm.sDiv("indent");
        htm.sP("thin").add("<a href='/doc/schema0.html'>Overview of Schemas</a>").eP();
        htm.sP("thin").add("<a href='/doc/schema1.html'>MathOps Schema</a>").eP();
        htm.sP("thin").add("<a href='/doc/schema2.html'>Main Schema</a>").eP();
        htm.sP("thin").add("<a href='/doc/schema3.html'>External Data Schema</a>").eP();
        htm.sP("thin").add("<a href='/doc/schema4.html'>Analytics Schema</a>").eP();
        htm.sP("thin").add("<a href='/doc/schema5.html'>Term Schema</a>").eP();
        htm.eDiv(); // indent

        htm.sH(4).add("Installation and Operations").eH(4);
        htm.sDiv("indent");
        htm.sP("thin").add("<a href='/doc/installation.html'>Installation</a>").eP();
        htm.sP("thin").add("<a href='/doc/configuration.html'>Configuration</a>").eP();
        htm.sP("thin").add("<a href='/doc/operations.html'>Operations</a>").eP();
        htm.eDiv(); // indent
        
        htm.eDiv(); // indent
        htm.eDiv(); // box with sections
        htm.eDiv(); // margins, max-width
    }

    /**
     * The main General information (index) page.
     *
     * @param htm the {@code HtmlBuilder} to which to append
     */
    private void doGeneralDatabase(final HtmlBuilder htm) {

        htm.sH(2).add("MathOps.Dev <strong>Generalized Database</strong> Overview").eH(2);

        htm.sP().add("MathOps is a Java-based system for course delivery and operation.").eP();

        htm.sP().add("""
                The system provides a generalized interface for data access that can use a variety of back-end products
                or persistence strategies, presented to applications as a <b>generalized persistence layer</b> with a
                flexible <b>API</b>.""").eP();

        htm.sP().add("This is based on a set of generalized classes that represent:").eP();
        htm.addln("<ul>");
        htm.addln("<li>Logical <b><i>tables</i></b>, organized into <b><i>schemas</i></b>, that define ",
                "<b><i>fields</i></b> and store <b><i>rows</i></b>.  Each schema may provide multiple ",
                "<b><i>contexts</i></b> (production, development, test, etc.)</li>");
        htm.addln("<li>Generalized <b><i>query criteria</i></b> that can be used for queries or to select rows in a ",
                "table for updates, deletes, or counting,</li>");
        htm.addln("<li>Generalized <b><i>updated data</i></b> objects that can store new field values for ",
                "updates.</li>");
        htm.addln("<li>A generalization of a <b><i>connection</i></b> to a server product, and support for ",
                "configuration of multiple products, databases, user logins etc., with pluggable implementations for ",
                "particular back-ends if needed to interface with legacy data.</li>");
        htm.addln("</ul>");

        htm.sP().add("The core classes are provided within the <code>dev.mathops.persistence</code> package.").eP();

        htm.sP().add("It is worth noting some features that are NOT provided by this system:").eP();

        htm.addln("<ul>");
        htm.addln("<li>Foreign keys and referential integrity between tables.</li>");
        htm.addln("<li>Joins, or queries that access multiple tables.</li>");
        htm.addln("<li>Transactions.  Every operation that changes data is committed at the time it completes or ",
                "rolled back if it fails.</li>");
        htm.addln("</ul>");
    }

    /**
     * The main information page describing tables, schemas, fields, and rows.
     *
     * @param htm the {@code HtmlBuilder} to which to append
     */
    private void doGeneralStructures(final HtmlBuilder htm) {

        doGeneralNav(htm, GEN_STRUCTURES);

        htm.sH(4).add("Tables, Schemas, Fields, and Rows").eH(4);

        htm.sP().add("A <b><i>table</i></b> is represented by an instance of a concrete subclass of the ",
                "<code>dev.mathops.persistence.Table</code> class.  Such subclasses should be immutable (thread-safe) ",
                "and are typically singletons with private constructor and static instance.").eP();
        htm.sP().add("A <b><i>table</i></b> exists within a <b><i>schema</i></b> (a named collection of tables, ",
                "typically implemented in a single database product or installation).  The table stores the unique ",
                "name of its schema, its unique table name, and a list of the <b><i>fields</i></b> that rows in that ",
                "table may contain.").eP();
        htm.sP().add("Each <b><i>field</i></b> defined within a table is represented by an instance of the (final) ",
                "<code>dev.mathops.persistence.Field</code> class, which is an immutable (thread-safe) object that ",
                "stores the field name (unique within the table), the field's data type (see below), the field's role ",
                "in the table (see below), and zero or more constraints that values for the field must obey.  ",
                "<code>Field</code> objects are suitable for use as map keys, and implement ",
                "<code>Comparable&lt;Field&gt;</code> so they can be used in contexts that require a well-defined ",
                "order (such as keys in a <code>TreeMap</code>).").eP();
        htm.sP().add("A <b><i>constraint</i></b> on a field of type <code>T</code> is an instance of one of a set of ",
                "pre-defined concrete subclasses of ",
                "<code>dev.mathops.persistence.constraint.AbstractFieldConstraint&lt;T&gt;</code>, or possibly a ",
                "custom subclass provided by an application.").eP();
        htm.sP().add("The data types supported by fields, along with the corresponding Java object type and the ",
                "pre-defined types of constraint that a field may define for each type are listed below:").eP();
        htm.addln("<ul>");
        htm.addln("<li>String (<code>java.lang.String</code>)</li>");
        htm.addln("  <ul>");
        htm.addln("  <li><code>StringEnumeratedConstraint</code>, which defines a fixed list of allowed ",
                "<code>String</code> values.</li>");
        htm.addln("  <li><code>StringLengthConstraint</code>, which defines a minimum and maximum allowed ",
                "length.</li>");
        htm.addln("  </ul>");
        htm.addln("<li>Boolean (<code>java.lang.Boolean</code>)</li>");
        htm.addln("<li>Byte (<code>java.lang.Byte</code>)</li>");
        htm.addln("  <ul>");
        htm.addln("  <li><code>ByteRangeConstraint</code>, which defines a minimum and maximum allowed byte ",
                "value.</li>");
        htm.addln("  </ul>");
        htm.addln("<li>Integer (<code>java.lang.Integer</code>)</li>");
        htm.addln("  <ul>");
        htm.addln("  <li><code>IntegerRangeConstraint</code>, which defines a minimum and maximum allowed int ",
                "value.</li>");
        htm.addln("  </ul>");
        htm.addln("<li>Long (<code>java.lang.Long</code>)</li>");
        htm.addln("  <ul>");
        htm.addln("  <li><code>LongRangeConstraint</code>, which defines a minimum and maximum allowed long ",
                "value.</li>");
        htm.addln("  </ul>");
        htm.addln("<li>Float (<code>java.lang.Float</code>)</li>");
        htm.addln("  <ul>");
        htm.addln("  <li><code>FloatRangeConstraint</code>, which defines a minimum and maximum allowed float ",
                "value and specifies whether NaN or infinite values are allowed.</li>");
        htm.addln("  </ul>");
        htm.addln("<li>Double (<code>java.lang.Double</code>)</li>");
        htm.addln("  <ul>");
        htm.addln("  <li><code>DoubleRangeConstraint</code>, which defines a minimum and maximum allowed double ",
                "value and specifies whether NaN or infinite values are allowed.</li>");
        htm.addln("  </ul>");
        htm.addln("<li>Blob (<code>java.sql.Blob</code>)</li>");
        htm.addln("<li>LocalDate (<code>java.time.LocalDate</code>)</li>");
        htm.addln("<li>LocalTime (<code>java.time.LocalTime</code>)</li>");
        htm.addln("<li>LocalDateTime (<code>java.time.LocalDateTime</code>)</li>");
        htm.addln("</ul>");

        htm.sP().add("The roles within their containing table that a field may be assigned include:").eP();
        htm.addln("<ul>");
        htm.addln("<li>Partition Key</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>The field participates in the primary key (the tuple of all fields that participate in the ",
                "primary key together must have a unique value for each row in the table)</li>");
        htm.addln("  <li>The field can be used to partition data across multiple servers.  Fields used as partition ",
                "keys should be chosen so that the majority of queries will select only rows with the same value for ",
                "the partition key.</li>");
        htm.addln("  </ul>");
        htm.addln("<li>Clustering Key</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>The field participates in the primary key.</li>");
        htm.addln("  <li>The field can be used to cluster data within a single partition for faster selection of ",
                "data by queries.</li>");
        htm.addln("  </ul>");
        htm.addln("<li>Not-null</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>The field does NOT participate in the primary key.</li>");
        htm.addln("  <li>The field may not have a NULL value &ndash; it must have a specified value in each row. ",
                "Note that an empty string is not considered a NULL value.</li>");
        htm.addln("  </ul>");
        htm.addln("<li>Nullable</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>The field does NOT participate in the primary key.</li>");
        htm.addln("  <li>The field may have any value, including NULL.</li>");
        htm.addln("  </ul>");
        htm.addln("</ul>");

        htm.sP().add("Typically, concrete subclasses of the <code>Table</code> class will define their fields, with ",
                "all required constraints, as class-static instances of <code>Field</code> with names that are ",
                "upper-case underscore-separated versions of the field's name.  The field's name itself is often ",
                "required to map to an actual field name in a database product, so it should limit itself to ",
                "lowercase ASCII letters and digits and underscores.  For example, a field with the name \"user_id\" ",
                "would typically be defined by a class-static instance of <code>Field</code> named \"USER_ID\".").eP();

        htm.sP().add("A <b><i>row</i></b> is represented by an immutable instance of the ",
                "<code>dev.mathops.persistence.Row</code> class, which contains an immutable array of values for all ",
                "fields defined in a table.  A row carries a reference to the table to which it belongs so its fields ",
                "can be interpreted at runtime.  Each field value in a row has either the Java object type that ",
                "corresponds to its field, or is null.  Field values are guaranteed to satisfy the constraints of the ",
                "corresponding field definition.").eP();

        htm.sP().add("A concrete subclass of <code>Table</code> should provide static utility methods to retrieve ",
                "field values from rows with their proper type.  These methods should take a single <code>Row</code> ",
                "argument, and should have a method name that is the CamelCase rendition of the field name, plus ",
                "\"Of\". For example, if a table defines a String field named \"last_name\", it should provide a ",
                "static method with this signature:").eP();
        htm.sP().add("<code>static String lastNameOf(Row row)</code>").eP();

        htm.sP().add("This method should test that the row's owning table matches the single instance of ",
                "<code>Table</code> that owns the field, but it does not need to test the data type of the field ",
                "value, since it will have been validated on construction.").eP();
    }

    /**
     * The main information page describing how rows within tables are selected.
     *
     * @param htm the {@code HtmlBuilder} to which to append
     */
    private void doGeneralSelectionCriteria(final HtmlBuilder htm) {

        doGeneralNav(htm, GEN_SELECTING);

        htm.sH(4).add("Selection Criteria").eH(4);

        htm.sP().add("When performing queries, updates, deletes, and counts, applications can provide selection ",
                "criteria to determine which rows from a table are considered.  This is provided to the database ",
                "layer in the form of an immutable instance of the final ",
                "<code>dev.mathops.persistence.SelectionCriteria</code> class.").eP();

        htm.sP().add("This class stores a reference to the table being queried, as well as an array of zero or more ",
                "field criterion objects.").eP();

        htm.sP().add("If no field criterion objects are provided, every row in the table is to be considered.  If one ",
                "or more criterion objects are provided, only rows that satisfy ALL supplied criteria will be ",
                "considered.").eP();

        htm.sP().add("Field criterion objects are concrete subclasses of the ",
                "<code>dev.mathops.persistence.criteria.AbstractFieldCriterion</code> class, and are either one of a ",
                "pre-defined set of criteria, or a custom criteria provided by the application.  Every implementation ",
                "of the database layer for a particular database engine or product will have to interpret these ",
                "selection criteria objects and convert them into, for example, SQL \"where\" clauses, or other ",
                "forms of criteria as appropriate to the product.").eP();

        htm.sP().add("The set of pre-defined field criteria for each allowed field type are:");

        htm.addln("<ul>");
        htm.addln("<li>String</li>");
        htm.addln("  <ul>");
        htm.addln("  <li><code>StringFieldCriterion</code>, which specifies a match type with associated data:").br();
        htm.addln("      IS_NULL").br();
        htm.addln("      IS_NOT_NULL").br();
        htm.addln("      EXACT_IN (with a list of matching <code>String</code> values)").br();
        htm.addln("      EXACT_NOT_IN (with a list of excluded <code>String</code> values)").br();
        htm.addln("      CASE_INSENSITIVE_IN (with a list of matching <code>String</code> values)").br();
        htm.addln("      CASE_INSENSITIVE_NOT_IN (with a list of excluded <code>String</code> values)").br();
        htm.addln("      EXACT_STARTS_WITH (with a list of matching <code>String</code> prefixes)").br();
        htm.addln("      EXACT_NOT_STARTS_WITH (with a list of excluded <code>String</code> prefixes)").br();
        htm.addln("      CASE_INSENSITIVE_STARTS_WITH (with a list of matching <code>String</code> prefixes)").br();
        htm.addln("      CASE_INSENSITIVE_NOT_STARTS_WITH (with a list of excluded <code>String</code> prefixes)</li>");
        htm.addln("  </ul>");

        htm.addln("<li>Boolean</li>");
        htm.addln("  <ul>");
        htm.addln("  <li><code>BooleanFieldCriterion</code>, which specifies a match type:").br();
        htm.addln("      IS_NULL").br();
        htm.addln("      IS_NOT_NULL").br();
        htm.addln("      IS_TRUE").br();
        htm.addln("      IS_FALSE</li>");
        htm.addln("  </ul>");

        htm.addln("<li>Byte</li>");
        htm.addln("  <ul>");
        htm.addln("  <li><code>ByteFieldCriterion</code>, which specifies a match type with associated data:").br();
        htm.addln("      IS_NULL").br();
        htm.addln("      IS_NOT_NULL").br();
        htm.addln("      EXACT_IN (with a list of matching <code>Byte</code> values)").br();
        htm.addln("      EXACT_NOT_IN (with a list of excluded <code>Byte</code> values)").br();
        htm.addln("      GREATER_THAN (with a single <code>Byte</code> value)").br();
        htm.addln("      GREATER_THAN_OR_EQUAL (with a single <code>Byte</code> value)").br();
        htm.addln("      LESS_THAN (with a single <code>Byte</code> value)").br();
        htm.addln("      LESS_THAN_OR_EQUAL (with a single <code>Byte</code> value)").br();
        htm.addln("      BETWEEN_EXCLUDE_BOUNDS (with an ordered pair of <code>Byte</code> values)").br();
        htm.addln("      BETWEEN_EXCLUDE_LOWER_BOUND (with an ordered pair of <code>Byte</code> values)").br();
        htm.addln("      BETWEEN_EXCLUDE_UPPER_BOUND (with an ordered pair of <code>Byte</code> values)").br();
        htm.addln("      BETWEEN_INCLUDE_BOUNDS (with an ordered pair of <code>Byte</code> values)</li>");
        htm.addln("  </ul>");

        htm.addln("<li>Integer</li>");
        htm.addln("  <ul>");
        htm.addln("  <li><code>IntegerFieldCriterion</code>, which specifies a match type with associated data (as ",
                "the match types for <code>Byte</code>, but with <code>Integer</code> values rather than ",
                "<code>Byte</code> values)</li>");
        htm.addln("  </ul>");

        htm.addln("<li>Long</li>");
        htm.addln("  <ul>");
        htm.addln("  <li><code>LongFieldCriterion</code>, which specifies a match type with associated data (as the ",
                "match types for <code>Byte</code>, but with <code>Long</code> values rather than ",
                "<code>Byte</code> values)</li>");
        htm.addln("  </ul>");

        htm.addln("<li>Float</li>");
        htm.addln("  <ul>");
        htm.addln("  <li><code>FloatFieldCriterion</code>, which specifies a match type with associated data (as the ",
                "match types for <code>Byte</code>, but with <code>Float</code> values rather than ",
                "<code>Byte</code> values)</li>");
        htm.addln("  </ul>");

        htm.addln("<li>Double</li>");
        htm.addln("  <ul>");
        htm.addln("  <li><code>DoubleFieldCriterion</code>, which specifies a match type with associated data (as the ",
                "match types for <code>Byte</code>, but with <code>Double</code> values rather than ",
                "<code>Byte</code> values)</li>");
        htm.addln("  </ul>");

        htm.addln("<li>LocalDate</li>");
        htm.addln("  <ul>");
        htm.addln("  <li><code>LocalDateFieldCriterion</code>, which specifies a match type with associated ",
                "data:").br();
        htm.addln("      IS_NULL").br();
        htm.addln("      IS_NOT_NULL").br();
        htm.addln("      EXACT_IN (with a list of matching <code>LocalDate</code> values)").br();
        htm.addln("      EXACT_NOT_IN (with a list of excluded <code>LocalDate</code> values)").br();
        htm.addln("      GREATER_THAN (with a single <code>LocalDate</code> value)").br();
        htm.addln("      GREATER_THAN_OR_EQUAL (with a single <code>LocalDate</code> value)").br();
        htm.addln("      LESS_THAN (with a single <code>LocalDate</code> value)").br();
        htm.addln("      LESS_THAN_OR_EQUAL (with a single <code>LocalDate</code> value)").br();
        htm.addln("      BETWEEN_EXCLUDE_BOUNDS (with an ordered pair of <code>LocalDate</code> values)").br();
        htm.addln("      BETWEEN_EXCLUDE_LOWER_BOUND (with an ordered pair of <code>LocalDate</code> values)").br();
        htm.addln("      BETWEEN_EXCLUDE_UPPER_BOUND (with an ordered pair of <code>LocalDate</code> values)").br();
        htm.addln("      BETWEEN_INCLUDE_BOUNDS (with an ordered pair of <code>LocalDate</code> values)</li>");
        htm.addln("  </ul>");

        htm.addln("<li>LocalTime</li>");
        htm.addln("  <ul>");
        htm.addln("  <li><code>LocalTimeFieldCriterion</code>, which specifies a match type with associated data (as ",
                "the match types for <code>LocalDate</code>, but with <code>LocalTime</code> values rather than ",
                "<code>LocalDate</code> values)</li>");
        htm.addln("  </ul>");

        htm.addln("<li>LocalDateTime</li>");
        htm.addln("  <ul>");
        htm.addln("  <li><code>LocalDateTimeFieldCriterion</code>, which specifies a match type with associated data ",
                "(as the match types for <code>LocalDate</code>, but with <code>LocalTime</code> values rather than ",
                "<code>LocalDateTime</code> values)</li>");
        htm.addln("  </ul>");

        htm.addln("</ul>");
    }

    /**
     * The main information page describing how updates are applied.
     *
     * @param htm the {@code HtmlBuilder} to which to append
     */
    private void doGeneralUpdatedValues(final HtmlBuilder htm) {

        doGeneralNav(htm, GEN_UPDATING);

        htm.sH(4).add("Updated Values").eH(4);

        htm.sP().add("When performing updates, applications need to supply new values for a subset of the fields in ",
                "a table, but the <code>Row</code> class is unsuitable for this task since every row must have values ",
                "that match its constraints for every field.").eP();

        htm.sP().add("Therefore, an immutable (final) <code>dev.mathops.persistence.UpdatedValues</code> class is ",
                "provided as a container for new values for an update operation.").eP();

        htm.sP().add("This class contains a reference to the table being updated, and a list of new field values, ",
                "some of which may be null to indicate the corresponding field is not to be updated. A special ",
                "<code>dev.mathops.persistence.NullValue</code> object is provided to allow applications to specify ",
                "that a field's value is to be set to NULL.").eP();
    }

    /**
     * The main information page describing implementations for specific databases.
     *
     * @param htm the {@code HtmlBuilder} to which to append
     */
    private void doGeneralImplementations(final HtmlBuilder htm) {

        doGeneralNav(htm, GEN_IMPL);

        htm.sH(4).add("Implementations").eH(4);

        htm.sP().add("For a given back-end implementation (a database with a set of tables that will get mapped to ",
                "the database objects defined here), there will be an implementation class that can dispatch requests ",
                "to either generalized code to handle tables defined in standard ways, or to custom code that ",
                "translates requests into legacy or database-specific operations.");

        htm.sP().add("This layer will connect directly to the database engine, and will be used when an API call is ",
                "processed.  Client code is free to use the implementation layer directly rather than use the API to ",
                "avoid the overhead.").eP();

        htm.sH(5).add("Data Configuration").eH(5);

        htm.sP().add("A system may have many implementations active at once.  A <b><i>data configuration</i></b> ",
                "objects selects implementations and routes operations to the correct implementation.").eP();

        htm.sP().add("This object selects a single implementation for every defined <b><i>schema</i></b> and ",
                "<b><i>context</i></b>. When a client makes a call that references a schema and context, the ",
                "corresponding implementation is used.").eP();

        htm.sP().add("The data layer itself stores the set of data configurations available.  These are guaranteed to ",
                "support the complete set of schemas and contexts defined within the data layer in order to be ",
                "advertised as available.  Configurations may be named, and client code can use those names to look ",
                "up and use the configurations (subject to permissions granted and the authorization token used in ",
                "the request).").eP();

        htm.sH(5).add("Implementation Object").eH(5);

        htm.sP().add("Every implementation has a single object that provides the implementation interface.  This ",
                "object implements the <code>dev.mathops.persistence.IImplementation</code> interface.  It may ",
                "internally process all requests directly (when the underlying database has a very regular ",
                "structure), or it may delegate requests to objects that implement custom code appropriate to the ",
                "underlying database.").eP();
    }

    /**
     * The main information page describing encoding of values and structures within the API.
     *
     * @param htm the {@code HtmlBuilder} to which to append
     */
    private void doGeneralApiEncoding(final HtmlBuilder htm) {

        doGeneralNav(htm, GEN_ENCODING);

        htm.sH(4).add("Service API: Encoding").eH(4);

        htm.sP().add("The database layer provides a service-oriented REST API that supports secure queries and ",
                "management.  The API is delivered through the HTTP protocol over TLS connections.").eP();

        htm.sP().add("API requests include an an authorization token that encodes the user's permissions.  This token ",
                "is generated as part of an authentication process, which can take place through the API, or through ",
                "a web-based front-end to the service.").eP();

        htm.sP().add("The API uses a binary format (which may be compressed) rather than XML or JSON, for efficiency. ",
                "Requests can use a transfer encoding of \"chunked\" or \"gzip\" to send data to endpoints, and ",
                "responses will use one of these two encodings when data is returned.").eP();

        htm.sH(5).add("Data Encodings").eH(5);

        htm.sP().add("<strong>Authorization Token Encoding</strong>").eP();

        htm.sP().add("A 128-bit binary value.  The high-order 64 bits is a random ID generated by the authentication ",
                "service.  The low-order 64 bits has, as its high-order 32-bits, the role of the authenticating user, ",
                "and in its low-order 32-bits, the effective role ID for the transaction.  Users may execute ",
                "transactions with any role for which their primary role grants access.  This can be used for testing ",
                "role permissions, or for executing operations in under a \"safer\" role with fewer unnecessary ",
                "permissions. This also allows an \"aggregate\" role to be defined that grants permissions to a ",
                "number of other roles without having to grant the aggregate role a superset of permissions of the ",
                "other roles.").eP();

        htm.sP().add("This token is transmitted ONLY over TLS-secured connections.").eP();

        htm.sP().add("<strong>Context Encoding</strong>").eP();
        htm.addln("<ul>");
        htm.addln("<li>The context ID as an Integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("<li>The context name as a String (ASCII1, ASCII2, String1, String2)</li>");
        htm.addln("</ul>");

        htm.sP().add("<strong>Schema Encoding</strong>").eP();
        htm.addln("<ul>");
        htm.addln("<li>The schema ID as an Integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("<li>The schema name as a a String (ASCII1, ASCII2, String1, String2)</li>");
        htm.addln("<li>The number of contexts (<code>N</code>) under which this schema may be accessed.</li>");
        htm.addln("<li><code>N</code> repetitions of:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>The context ID as an Integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("  </ul>");
        htm.addln("</ul>");

        htm.sP().add("<strong>Table Encoding</strong>  (used when a list of tables and field definitions is to be ",
                "returned)").eP();
        htm.addln("<ul>");
        htm.addln("<li>The owning schema ID as an Integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("<li>The table ID as an Integer  (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("<li>The table name as a a String (ASCII1, ASCII2, String1, String2)</li>");
        htm.addln("<li>The number of fields (<code>N</code>) as an Integer (Tinyint, Byte, or Short)</li>");
        htm.addln("<li><code>N</code> repetitions of:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>An enumerated value (Tinyint) indicating the field type</li>");
        htm.addln("  <li>An enumerated value (Tinyint) indicating the field's role</li>");
        htm.addln("  <li>The number (<code>M</code>) of constraints associated with the field, as an integer " +
                  "(Tinyint, Byte, or Short)</li>");
        htm.addln("  <li><code>M</code> repetitions of constraint definitions:</li>");
        htm.addln("    <ul>");
        htm.addln("    <li>If type is STRING_ENUMERATED, the number (<code>P</code>) of enumerated values as an ",
                "Integer (Tinyint, Byte, or Short) followed by <code>P</code> Strings (ASCII1, ASCII2, ASCII4, ",
                "String1, String2, or String4)</li>");
        htm.addln("    <li>If type is STRING_LENGTH, the minimum length as an Integer (Tinyint, Byte, Short, or ",
                "Integer) followed by the maximum length as an integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("    <li>If type is BYTE_RANGE, the minimum value as a Byte followed by the maximum value as a ",
                "Byte.</li>");
        htm.addln("    <li>If type is INTEGER_RANGE, the minimum value as an integer (Tinyint, Byte, Short, or ",
                "Integer) followed by the maximum value as an integer (Tinyint, Byte, Short, or Integer).</li>");
        htm.addln("    <li>If type is LONG_RANGE, the minimum value as a long integer (Tinyint, Byte, Short, or ",
                "Integer, or Long) followed by the maximum value as an integer (Tinyint, Byte, Short, Integer, or " +
                "Long).</li>");
        htm.addln("    <li>If type is FLOAT_RANGE, the minimum value as a Float followed by the maximum value as a ",
                "Float.</li>");
        htm.addln("    <li>If type is DOUBLE_RANGE, the minimum value as a Double followed by the maximum value as a ",
                "Double.</li>");
        htm.addln("    </ul>");
        htm.addln("  </ul>");
        htm.addln("</ul>");

        htm.sP().add("<strong>Binary Encodings</strong>").eP();
        htm.sP().add("All numeric fields are encoded in big-endian byte ordering (most significant byte first).").eP();
        htm.sTable();
        htm.sTr().sTd().add("Type:").eTd().sTd().add("An 8-bit type field that precedes any field.").br();
        htm.sTable();
        htm.sTr().sTd().add("0x01 = ASCII1	     ").eTd().sTd().add("0x02 = ASCII2	 ").eTd().sTd()
                .add("0x03 = ASCII4").eTd().eTr();
        htm.sTr().sTd().add("0x04 = String1	     ").eTd().sTd().add("0x05 = String2	 ").eTd().sTd()
                .add("0x06 = String4").eTd().eTr();
        htm.sTr().sTd().add("0x07 = Boolean	     ").eTd().sTd().add("0x08 = Byte	 ").eTd().sTd()
                .add("0x09 = Short").eTd().eTr();
        htm.sTr().sTd().add("0x0A = Integer	     ").eTd().sTd().add("0x0B = Long	 ").eTd().sTd()
                .add("0x0C = Float").eTd().eTr();
        htm.sTr().sTd().add("0x0D = Double	     ").eTd().sTd().add("0x0E = LocalDate").eTd().sTd()
                .add("0x0F = LocalTime").eTd().eTr();
        htm.sTr().sTd().add("0x10 = LocalDateTime").eTd().sTd().add("0x11 = BLOB1	 ").eTd().sTd()
                .add("0x12 = BLOB2").eTd().eTr();
        htm.sTr().sTd().add("0x13 = BLOB4	     ").eTd().sTd(null, "colspan='2'")
                .add("0x30-0xFF = Tinyint (unsigned integer from 0x00 to 0xCF)").eTd().eTr();
        htm.eTable();
        htm.eTd().eTr();
        htm.sTr().sTd().add("ASCII1:").eTd().sTd().add("An 8-bit unsigned length (<code>N</code>) followed by ",
                "(<code>N</code>) ASCII characters").eTd().eTr();
        htm.sTr().sTd().add("ASCII2:").eTd().sTd().add("A 16-bit unsigned length (<code>N</code>) followed by ",
                "(<code>N</code>) ASCII characters").eTd().eTr();
        htm.sTr().sTd().add("ASCII4:").eTd().sTd().add("A 31-bit unsigned length (<code>N</code>) followed by ",
                "(<code>N</code>) ASCII characters").eTd().eTr();
        htm.sTr().sTd().add("String1:").eTd().sTd().add("An 8-bit unsigned length (<code>N</code>) followed by ",
                "(<code>N</code>) UTF-16 code points").eTd().eTr();
        htm.sTr().sTd().add("String2:").eTd().sTd().add("A 16-bit unsigned length (<code>N</code>) followed by ",
                "(<code>N</code>) UTF-16 code points").eTd().eTr();
        htm.sTr().sTd().add("String4:").eTd().sTd().add("A 31-bit unsigned length (<code>N</code>) followed by ",
                "(<code>N</code>) UTF-16 code points").eTd().eTr();
        htm.sTr().sTd().add("Boolean:").eTd().sTd().add("An 8-bit value 0x01 or 0x00").eTd().eTr();
        htm.sTr().sTd().add("Byte:").eTd().sTd().add("An 8-bit signed integer or enumerated value").eTd().eTr();
        htm.sTr().sTd().add("Short:").eTd().sTd().add("A 16-bit signed integer or enumerated value").eTd().eTr();
        htm.sTr().sTd().add("Integer:").eTd().sTd().add("A 32-bit signed integer or enumerated value").eTd().eTr();
        htm.sTr().sTd().add("Long:").eTd().sTd().add("A 64-bit signed integer").eTd().eTr();
        htm.sTr().sTd().add("Float:").eTd().sTd().add("A 32-bit floating-point number").eTd().eTr();
        htm.sTr().sTd().add("Double:").eTd().sTd().add("A 64-bit floating point number").eTd().eTr();
        htm.sTr().sTd().add("LocalDate:").eTd().sTd().add("A 32-bit number whose low-order 5 bits are the unsigned ",
                "day of the month (1-31), next highest 4 bits are the unsigned month (1 to 12), and remaining ",
                "high-order 23 bits are the signed year.   Dates whose year falls outside this range cannot be ",
                "transmitted.").eTd().eTr();
        htm.sTr().sTd().add("LocalTime:").eTd().sTd().add("A 32-bit number whose low-order 10 bits are the unsigned ",
                "millisecond (0 to 999), next highest 6 bits are the unsigned second (0 to 59), next highest 6 bits ",
                "are the unsigned minute (0 to 59), and next highest 5 bits are the unsigned hour (0 to 23).  The ",
                "remaining 5 bits should be zero.").eTd().eTr();
        htm.sTr().sTd().add("LocalDateTime:").eTd().sTd().add("A 32-bit LocalDate (as above) followed by a 32-bit ",
                "LocalTime (as above).").eTd().eTr();
        htm.sTr().sTd().add("BLOB1:").eTd().sTd().add("An 8-bit unsigned length (<code>N</code>) followed by ",
                "(<code>N</code>) bytes").eTd().eTr();
        htm.sTr().sTd().add("BLOB2:").eTd().sTd().add("A 16-bit unsigned length (<code>N</code>) followed by ",
                "(<code>N</code>) bytes").eTd().eTr();
        htm.sTr().sTd().add("BLOB4:").eTd().sTd().add("A 32-bit signed length (<code>N</code>) followed by ",
                "(<code>N</code>) bytes").eTd().eTr();
        htm.eTable();

        htm.sP().add("<strong>Selection Criteria Encoding</strong> (NOTE: table name is already defined when this ",
                "object is encoded)").eP();
        htm.addln("<ul>");
        htm.addln("<li>An enumerated type (Tinyint, Byte, Short, or Integer) to indicate the block is Selection ",
                "Criteria</li>");
        htm.addln("<li>An integer (Tinyint, Byte, Short, or Integer) encoding the number (<code>N</code>) of field ",
                "criteria that follows.</li>");
        htm.add("<li><code>N</code> repetitions of:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>An enumerated value (Tinyint, Byte, Short, or Integer) indicating the constraint type</li>");
        htm.addln("  <li>The field name String (ASCII1, ASCII2, ASCII4, String1, String2, or String4)</li>");
        htm.addln("  <li>An enumerated value (Tinyint, Byte, Short, or Integer) indicating match type</li>");
        htm.addln("  <li>If type is STRING:</li>");
        htm.addln("    <ul>");
        htm.addln("      <li>An integer (<code>M</code>) with the number of match strings</li>");
        htm.addln("      <li><code>M</code> repetitions of a String (ASCII1, ASCII2, ASCII4, String1, String2, or ",
                "String4)</li>");
        htm.addln("    </ul>");
        htm.addln("  <li>If type is BOOLEAN, no additional fields</li>");
        htm.addln("  <li>If type is BYTE:</li>");
        htm.addln("    <ul>");
        htm.addln("    <li>An integer (<code>M</code>) with the number of match bytes").br();
        htm.addln("    <li><code>M</code> repetitions of a Byte</li>");
        htm.addln("    </ul>");
        htm.addln("  <li>If type is INTEGER:</li>");
        htm.addln("    <ul>");
        htm.addln("    <li>An integer (M) with the number of match integers").br();
        htm.addln("    <li><code>M</code> repetitions of an Integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("    </ul>");
        htm.addln("  <li>If type is LONG:</li>");
        htm.addln("    <ul>");
        htm.addln("    <li>An integer (M) with the number of match integers").br();
        htm.addln("    <li><code>M</code> repetitions of an Integer (Tinyint, Byte, Short, Integer, or Long)</li>");
        htm.addln("    </ul>");
        htm.addln("  <li>If type is FLOAT:</li>");
        htm.addln("    <ul>");
        htm.addln("    <li>An integer (<code>M</code>) with the number of match floats</li>");
        htm.addln("    <li><code>M</code> repetitions of an Float</li>");
        htm.addln("    </ul>");
        htm.addln("  <li>If type is DOUBLE:</li>");
        htm.addln("    <ul>");
        htm.addln("    <li>An integer (<code>M</code>) with the number of match doubles</li>");
        htm.addln("    <li><code>M</code> repetitions of Double</li>");
        htm.addln("    </ul>");
        htm.addln("  <li>If type is LOCAL_DATE:</li>");
        htm.addln("    <ul>");
        htm.addln("    <li>An integer (<code>M</code>) with the number of match local dates</li>");
        htm.addln("    <li><code>M</code> repetitions of LocalDate</li>");
        htm.addln("    </ul>");
        htm.addln("  <li>If type is LOCAL_TIME:</li>");
        htm.addln("    <ul>");
        htm.addln("    <li>An integer (<code>M</code>) with the number of match local times</li>");
        htm.addln("    <li><code>M</code> repetitions of LocalTime</li>");
        htm.addln("    </ul>");
        htm.addln("  <li>If type is LOCAL_DATE_TIME:</li>");
        htm.addln("    <ul>");
        htm.addln("    <li>An integer (<code>M</code>) with the number of match local date/times</li>");
        htm.addln("    <li><code>M</code> repetitions of LocalDateTime</li>");
        htm.addln("    </ul>");
        htm.addln("  </ul>");
        htm.addln("</ul>");

        htm.sP().add("<strong>Row Encoding</strong> (NOTE: schema and table name are already defined when this object",
                " is encoded, fields with null values are excluded)").eP();
        htm.addln("<ul>");
        htm.addln("<li>An enumerated type (Tinyint, Byte, Short, or Integer) to indicate the block is a Row</li>");
        htm.addln("<li>An integer (Tinyint, Byte, Short, or Integer) encoding the number (<code>N</code>) of field ",
                "values that follow.</li>");
        htm.addln("<li><code>N</code> repetitions of:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>The integer field index  (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("  <li>An enumerated value (Tinyint, Byte, Short, or Integer) indicating the field type</li>");
        htm.addln("  <li>If type is STRING, a String (ASCII1, ASCII2, ASCII4, String1, String2, or String4)</li>");
        htm.addln("  <li>If type is BOOLEAN, a Boolean</li>");
        htm.addln("  <li>If type is BYTE, a Byte</li>");
        htm.addln("  <li>If type is INTEGER, an Integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("  <li>If type is LONG, a long integer (Tinyint, Byte, Short, Integer, or Long)</li>");
        htm.addln("  <li>If type is FLOAT, a Float</li>");
        htm.addln("  <li>If type is DOUBLE, a Double</li>");
        htm.addln("  <li>If type is LOCAL_DATE, a LocalDate</li>");
        htm.addln("  <li>If type is LOCAL_TIME, a LocalTime</li>");
        htm.addln("  <li>If type is LOCAL_DATE_TIME, a LocalDateTime</li>");
        htm.addln("  </ul>");
        htm.addln("</ul>");

        htm.sP().add("<strong>Updated Values Encoding</strong> (NOTE: schema and table name are already defined when ",
                "this object is encoded)").eP();
        htm.addln("<ul>");
        htm.addln("<li>An enumerated type (Tinyint, Byte, Short, or Integer) to indicate the block is Updated ",
                "Values</li>");
        htm.addln("<li>An integer (Tinyint, Byte, Short, or Integer) encoding the number (<code>N</code>) of field ",
                "values that follow.</li>");
        htm.addln("<li><code>N</code> repetitions of:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>The integer field index  (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("  <li>An enumerated value (Tinyint, Byte, Short, or Integer) indicating the field type</li>");
        htm.addln("  <li>If type is NULL, no further data is needed</li>");
        htm.addln("  <li>If type is STRING, a String (ASCII1, ASCII2, ASCII4, String1, String2, or String4)</li>");
        htm.addln("  <li>If type is BOOLEAN, a Boolean</li>");
        htm.addln("  <li>If type is BYTE, a Byte</li>");
        htm.addln("  <li>If type is INTEGER, an Integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("  <li>If type is LONG, a long integer (Tinyint, Byte, Short, Integer, or Long)</li>");
        htm.addln("  <li>If type is FLOAT, a Float</li>");
        htm.addln("  <li>If type is DOUBLE, a Double</li>");
        htm.addln("  <li>If type is LOCAL_DATE, a LocalDate</li>");
        htm.addln("  <li>If type is LOCAL_TIME, a LocalTime</li>");
        htm.addln("  <li>If type is LOCAL_DATE_TIME, a LocalDateTime</li>");
        htm.addln("  </ul>");
        htm.addln("</ul>");
    }

    /**
     * The main information page describing the API endpoints.
     *
     * @param htm the {@code HtmlBuilder} to which to append
     */
    private void doGeneralApiEndpoints(final HtmlBuilder htm) {

        doGeneralNav(htm, GEN_ENDPOINTS);

        htm.sH(4).add("Service API: Endpoints").eH(4);

        htm.sP().add("In the API endpoints documented below, the endpoint name is the portion of the HTTP request ",
                "path that follows that part that identifies the host and service.  Request parameters are provided ",
                "in the request body in a binary format.").eP();

        htm.sP("thin").add("<code>GET all_contexts</code>").eP();
        htm.sDiv("indent");
        htm.sP("thin").add("Retrieves the set of defined contexts.").eP();
        htm.sP("redhead").add("Request body:").eP();
        htm.addln("<ul class='thin'>");
        htm.addln("<li>16-byte authorization token</li>");
        htm.addln("</ul>");
        htm.sP("redhead").add("Response body:").eP();
        htm.addln("<ul class='thin'>");
        htm.addln("<li>Enumerated result code {SUCCESS | FAILURE} (Tinyint, Byte or Short)</li>");
        htm.addln("<li>On SUCCESS:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>Integer number of contexts (N) (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("  <li>N repetitions of:</li>");
        htm.addln("    <ul>");
        htm.addln("    <li>Context Encoding (see above)</li>");
        htm.addln("    </ul>");
        htm.addln("  </ul>");
        htm.addln("<li>On FAILURE:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>Enumerated failure code (Tinyint, Byte, or Short)</li>");
        htm.addln("  <li>Error message (ASCII1, ASCII2, ASCII4, String1, String2, or String4)</li>");
        htm.addln("  </ul>");
        htm.addln("</ul>");
        htm.eDiv();
        htm.div("vgap");

        htm.sP("thin").add("<code>GET all_schemas</code>").eP();
        htm.sDiv("indent");
        htm.sP("thin").add("Retrieves the set of defined schemas.").eP();
        htm.sP("redhead").add("Request body:").eP();
        htm.addln("<ul class='thin'>");
        htm.addln("<li>16-byte authorization token</li>");
        htm.addln("</ul>");
        htm.sP("redhead").add("Response body:").eP();
        htm.addln("<ul class='thin'>");
        htm.addln("<li>Enumerated result code {SUCCESS | FAILURE} (Tinyint, Byte or Short)</li>");
        htm.addln("<li>On SUCCESS:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>Integer number of schemas (N) (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("  <li>N repetitions of:</li>");
        htm.addln("    <ul>");
        htm.addln("    <li>Schema Encoding (see above)</li>");
        htm.addln("    </ul>");
        htm.addln("  </ul>");
        htm.addln("<li>On FAILURE:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>Enumerated failure code (Tinyint, Byte, or Short)</li>");
        htm.addln("  <li>Error message (ASCII1, ASCII2, ASCII4, String1, String2, or String4)</li>");
        htm.addln("  </ul>");
        htm.addln("</ul>");
        htm.eDiv();
        htm.div("vgap");

        htm.sP("thin").add("<code>GET all_tables</code>").eP();
        htm.sDiv("indent");
        htm.sP("thin").add("Retrieves the set of defined tables and their fields and constraints.  If a schema ID is " +
                           "provided, only the tables in that schema are returned.").eP();
        htm.sP("redhead").add("Request body:").eP();
        htm.addln("<ul class='thin'>");
        htm.addln("<li>16-byte authorization token</li>");
        htm.addln("<li>Flags (Short) – 0x01 = Include row count</li>");
        htm.addln("<li>[OPTIONAL] Schema ID (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("</ul>");
        htm.sP("redhead").add("Response body:").eP();
        htm.addln("<ul class='thin'>");
        htm.addln("<li>Enumerated result code {SUCCESS | FAILURE} (Tinyint, Byte or Short)</li>");
        htm.addln("<li>Flags (Short) – copied from request</li>");
        htm.addln("<li>On SUCCESS:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>Integer number of tables (N) (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("  <li>N repetitions of:</li>");
        htm.addln("    <ul>");
        htm.addln("    <li>Table Encoding (see above)</li>");
        htm.addln("    <li>If Flags indicates row count included:</li>");
        htm.addln("      <ul>");
        htm.addln("      <li>Integer number of rows in the table (Tinyint, Byte, Short, Integer, or Long)</li>");
        htm.addln("      </ul>");
        htm.addln("    </ul>");
        htm.addln("  </ul>");
        htm.addln("<li>On FAILURE:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>Enumerated failure code (Tinyint, Byte, or Short)</li>");
        htm.addln("  <li>Error message (ASCII1, ASCII2, ASCII4, String1, String2, or String4)</li>");
        htm.addln("  </ul>");
        htm.addln("</ul>");
        htm.eDiv();
        htm.div("vgap");

        htm.sP("thin").add("<code>GET table</code>").eP();
        htm.sDiv("indent");
        htm.sP("thin").add("Retrieves the fields and constraints of a single table.").eP();
        htm.sP("redhead").add("Request body:").eP();
        htm.addln("<ul class='thin'>");
        htm.addln("<li>16-byte authorization token</li>");
        htm.addln("<li>Flags (Short) – 0x01 = Include row count</li>");
        htm.addln("<li>The schema ID as an integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("<li>The table ID as an integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("</ul>");
        htm.sP("redhead").add("Response body:").eP();
        htm.addln("<ul class='thin'>");
        htm.addln("<li>Enumerated result code {SUCCESS | FAILURE} (Tinyint, Byte or Short)</li>");
        htm.addln("<li>Flags (Short) – copied from request</li>");
        htm.addln("<li>On SUCCESS:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>Table Encoding (see above)</li>");
        htm.addln("  <li>If Flags indicates row count included:</li>");
        htm.addln("  <li>Integer number of rows (Tinyint, Byte, Short, Integer, or Long)</li>");
        htm.addln("  </ul>");
        htm.addln("<li>On FAILURE:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>Enumerated failure code (Tinyint, Byte, or Short)</li>");
        htm.addln("  <li>Error message (ASCII1, ASCII2, ASCII4, String1, String2, or String4)</li>");
        htm.addln("  </ul>");
        htm.addln("</ul>");
        htm.eDiv();
        htm.div("vgap");

        htm.sP("thin").add("<code>GET count</code>").eP();
        htm.sDiv("indent");
        htm.sP("thin").add("Retrieves the number of rows in the requested table that match criteria.").eP();
        htm.sP("redhead").add("Request body:").eP();
        htm.addln("<ul class='thin'>");
        htm.addln("<li>16-byte authorization token</li>");
        htm.addln("<li>The schema ID as an integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("<li>The context ID as an integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("<li>The table ID as an integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("<li>[OPTIONAL] Selection Criteria (see above – if omitted, all rows match)</li>");
        htm.addln("</ul>");
        htm.sP("redhead").add("Response body:").eP();
        htm.addln("<ul class='thin'>");
        htm.addln("<li>Enumerated result code {SUCCESS | FAILURE} (Tinyint, Byte or Short)</li>");
        htm.addln("<li>On SUCCESS:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>Integer number of rows (Tinyint, Byte, Short, Integer, or Long)</li>");
        htm.addln("  </ul>");
        htm.addln("<li>On FAILURE:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>Enumerated failure code (Tinyint, Byte, or Short)</li>");
        htm.addln("  <li>Error message (ASCII1, ASCII2, ASCII4, String1, String2, or String4)</li>");
        htm.addln("  </ul>");
        htm.addln("</ul>");
        htm.eDiv();
        htm.div("vgap");

        htm.sP("thin").add("<code>GET query</code>").eP();
        htm.sDiv("indent");
        htm.sP("thin").add("Retrieves all rows in a specified table that match a set of match criteria.").eP();
        htm.sP("redhead").add("Request body:").eP();
        htm.addln("<ul class='thin'>");
        htm.addln("<li>16-byte authorization token</li>");
        htm.addln("<li>The schema ID as an integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("<li>The context ID as an integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("<li>The table ID as an integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("<li>[OPTIONAL] Selection Criteria (see above – if omitted, all rows match)</li>");
        htm.addln("</ul>");
        htm.sP("redhead").add("Response body:").eP();
        htm.addln("<ul class='thin'>");
        htm.addln("<li>Enumerated result code {SUCCESS | FAILURE} (Tinyint, Byte or Short)</li>");
        htm.addln("<li>On SUCCESS:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>Integer number of rows (N) (Tinyint, Byte, Short, Integer, or Long)</li>");
        htm.addln("  <li>N repetitions of Row  Encoding (see above)</li>");
        htm.addln("  </ul>");
        htm.addln("<li>On FAILURE:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>Enumerated failure code (Tinyint, Byte, or Short)</li>");
        htm.addln("  <li>Error message (ASCII1, ASCII2, ASCII4, String1, String2, or String4)</li>");
        htm.addln("  </ul>");
        htm.addln("</ul>");
        htm.eDiv();
        htm.div("vgap");

        htm.sP("thin").add("<code>POST insert</code>").eP();
        htm.sDiv("indent");
        htm.sP("thin").add("Inserts one or more rows into a specified table.  All rows will be inserted if " +
                           "successful; none will be inserted on failure.").eP();
        htm.sP("redhead").add("Request body:").eP();
        htm.addln("<ul class='thin'>");
        htm.addln("<li>16-byte authorization token</li>");
        htm.addln("<li>The schema ID as an integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("<li>The context ID as an integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("<li>The table ID as an integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("<li>Integer number of rows (<code>N</code>) (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("<li><code>N</code> repetitions of Row Encoding (see above)</li>");
        htm.addln("</ul>");
        htm.sP("redhead").add("Response body:").eP();
        htm.addln("<ul class='thin'>");
        htm.addln("<li>Enumerated result code {SUCCESS | FAILURE} (Tinyint, Byte or Short)</li>");
        htm.addln("<li>On SUCCESS:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>Integer number of rows inserted (Tinyint, Byte, Short or, Integer)</li>");
        htm.addln("  </ul>");
        htm.addln("<li>On FAILURE:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>Enumerated failure code (Tinyint, Byte, or Short)</li>");
        htm.addln("  <li>Error message (ASCII1, ASCII2, ASCII4, String1, String2, or String4)</li>");
        htm.addln("  </ul>");
        htm.addln("</ul>");
        htm.eDiv();
        htm.div("vgap");

        htm.sP("thin").add("<code>POST insert_multi</code>").eP();
        htm.sDiv("indent");
        htm.sP("thin").add("Inserts one or more rows into each of a set of specified tables.  All rows will be " +
                           "inserted if successful; none will be inserted on failure.").eP();
        htm.sP("redhead").add("Request body:").eP();
        htm.addln("<ul class='thin'>");
        htm.addln("<li>16-byte authorization token</li>");
        htm.addln("<li>Integer number of tables (<code>M</code>) (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("<li><code>M</code> repetitions of:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>The schema ID as an integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("  <li>The context ID as an integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("  <li>The table ID as an integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("  <li>Integer number of rows (<code>N</code>) (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("  <li><code>N</code> repetitions of Row Encoding (see above)</li>");
        htm.addln("  </ul>");
        htm.addln("</ul>");
        htm.sP("redhead").add("Response body:").eP();
        htm.addln("<ul class='thin'>");
        htm.addln("<li>Enumerated result code {SUCCESS | FAILURE} (Tinyint, Byte or Short)</li>");
        htm.addln("<li>On SUCCESS:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>Integer number of rows inserted (Tinyint, Byte, Short, Integer)</li>");
        htm.addln("  </ul>");
        htm.addln("<li>On FAILURE:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>Enumerated failure code (Tinyint, Byte, or Short)</li>");
        htm.addln("  <li>Error message (ASCII1, ASCII2, ASCII4, String1, String2, or String4</li>");
        htm.addln("  </ul>");
        htm.addln("</ul>");
        htm.eDiv();
        htm.div("vgap");

        htm.sP("thin").add("<code>POST delete</code>").eP();
        htm.sDiv("indent");
        htm.sP("thin").add("Deletes all rows matching a set of selection criteria.").eP();
        htm.sP("redhead").add("Request body:").eP();
        htm.addln("<ul class='thin'>");
        htm.addln("<li>16-byte authorization token</li>");
        htm.addln("<li>The schema ID as an integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("<li>The context ID as an integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("<li>The table ID as an integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("<li>[OPTIONAL] Selection Criteria Encoding (see above – if omitted, all records match)</li>");
        htm.addln("</ul>");
        htm.sP("redhead").add("Response body:").eP();
        htm.addln("<ul class='thin'>");
        htm.addln("<li>Enumerated result code {SUCCESS | FAILURE} (Tinyint, Byte or Short)</li>");
        htm.addln("<li>On SUCCESS:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>Integer number of rows deleted (Tinyint, Byte, Short, Integer, or Long)</li>");
        htm.addln("  </ul>");
        htm.addln("<li>On FAILURE:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>Enumerated failure code (Tinyint, Byte, or Short)</li>");
        htm.addln("  <li>Error message (ASCII1, ASCII2, ASCII4, String1, String2, or String4)</li>");
        htm.addln("  </ul>");
        htm.addln("</ul>");
        htm.eDiv();
        htm.div("vgap");

        htm.sP("thin").add("<code>POST update</code>").eP();
        htm.sDiv("indent");
        htm.sP("thin").add("Updates all rows matching a set of selection criteria to new values.").eP();
        htm.sP("redhead").add("Request body:").eP();
        htm.addln("<ul class='thin'>");
        htm.addln("<li>16-byte authorization token</li>");
        htm.addln("<li>The schema ID as an integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("<li>The context ID as an integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("<li>The table ID as an integer (Tinyint, Byte, Short, or Integer)</li>");
        htm.addln("<li>[OPTIONAL] Selection Criteria Encoding (see above – if omitted, all records match)</li>");
        htm.addln("<li>Updated Values Encoding (see above)</li>");
        htm.addln("</ul>");
        htm.sP("redhead").add("Response body:").eP();
        htm.addln("<ul class='thin'>");
        htm.addln("<li>Enumerated result code {SUCCESS | FAILURE} (Tinyint, Byte or Short)</li>");
        htm.addln("<li>On SUCCESS:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>Integer number of rows updated (Tinyint, Byte, Short, Integer, or Long)</li>");
        htm.addln("  </ul>");
        htm.addln("<li>On FAILURE:</li>");
        htm.addln("  <ul>");
        htm.addln("  <li>Enumerated failure code (Tinyint, Byte, or Short)</li>");
        htm.addln("  <li>Error message (ASCII1, ASCII2, ASCII4, String1, String2, or String4)</li>");
        htm.addln("  </ul>");
        htm.addln("</ul>");
        htm.eDiv();
        htm.div("vgap");

//        Question: Is a "delete_multi" needed that deletes from multiple tables, where all will be deleted on success,
//        or none are deleted on failure?
//        Question: Is an "update_multi" needed that updates multiple tables, where all will be updated on success, or
//        none are updated on failure?

        htm.sP("thin").add("<code>POST authorization</code>").eP();
        htm.sDiv("indent");
        htm.sP("thin").add("Authentication uses the SCRAM-SHA-256 protocol as defined in RFC 5802 and RFC 7677 over ",
                "a TLS-secured connection.").eP();
        htm.sP("thin").add("The client begins by sending a client_first message, to which the server responds with a ",
                "server_first message. The client then sends a client_final message, to which the server responds ",
                "with a server_final message.").eP();
        htm.eDiv();
    }

    /**
     * The page describing general schema organization.
     *
     * @param htm the {@code HtmlBuilder} to which to append
     */
    private void doSchemas(final HtmlBuilder htm) {

        doSchemaNav(htm, SCHEMA_OVERVIEW);

        htm.sP().add("The <strong>MathOps Persistence Layer</strong> is generalized and can manage any collection of ",
                "tables and schemas, but includes an implementation of all schemas and tables needed by the broader ",
                "MathOps system.").eP();

        htm.sP().add("At a high level, data is partitioned into schemas and tables (each with a unique name), ",
                "where each table exists within a schema, and the combination of schema name and table name ",
                "uniquely select a physical database table.  Since schemas may have a large number of tables, ",
                "tables within a schema may be organized into groups, each with a unique group name, but all ",
                "tables in a schema must have unique names regardless of their group membership.").eP();

        htm.sP().add("Some database products are case-sensitive while others are not, so names of schemas, tables, ",
                "and table groups should use only lowercase letters, digits, and underscore characters. Every name ",
                "must start with a letter.").eP().hr();

        htm.sP().add("The MathOps system includes a wide variety of data spanning many different patterns of usage ",
                "and generation.").eP();
        htm.addln("<ul>");
        htm.addln("<li>Configuration data such as course, section, term, and policy definitions (low-volume, ",
                "frequent reads)</li>");
        htm.addln("<li>Dynamic student data like registrations, course status, profiles (high-volume, frequent ",
                "reads/writes)</li>");
        htm.addln("<li>Records of activities like logs or messages (high-volume, frequent writes)</li>");
        htm.addln("<li>Analytics data (high-volume, frequent reads)</li>");
        htm.addln("</ul>");

        htm.sP().add("Low-volume data that is read frequently is best served by an in-memory replicated cache that ",
                "can be populated on startup from a \"source of truth\".  A programmatic interface to alter this ",
                "data would update the \"source of truth\" and update the in-memory cache at the same time.  ",
                "The in-memory cache can be replicated across multiple nodes in a high-availability cluster.").eP();

        htm.sP().add("High-volume data could be better served by an RDBMS like <b>PostgreSQL</b> or a partitioned ",
                "database such as <b>Cassandra</b>, since queries in this system tend to rely on a few small key ",
                "columns, and joins or database-level referential integrity constraints are not needed.").eP();

        htm.sP().add("The MathOps system stores data in the context of \"terms\", or semesters.  For example, Fall, ",
                "Spring, and Summer semesters of each academic year.   There is exactly one term \"active\" at any ",
                "moment in time.  Each term represents a contiguous span of days.  Terms do not overlap, and there ",
                "are no gaps between terms. Old term configuration data is retained and reference.  New term data can ",
                "be prepared ahead of time, to become active when the current date crosses the boundary into a new ",
                "term.").eP();

        htm.sP().add("Data for the current term will be accessed frequently (with many updates), data for older terms ",
                "will be accessed infrequently (and without updates), and data for upcoming terms will be created or ",
                "updated infrequently as new terms approach.  Therefore, we do not want to store data for all terms ",
                "in all tables and force scanning of large amounts of non-applicable data for each query within the ",
                "active term.  To address this, we define a separate schema for every term.").eP();

        htm.sP().add("The schemas that make up the system include:").eP();
        htm.addln("<ul>");
        htm.addln("<li><code>mathops</code>: the system schema used by the MathOps system and the persistence layer ",
                "to store its own configuration.</li>");
        htm.addln("<li><code>main</code>: data that does not vary by term, such as student data.  There are actually ",
                "three schemas defined with identical table structures: <code>main</code> for production data, ",
                "<code>main_d</code> for a development schema where changes can be tested, and <code>main_t</code> ",
                "for low-level testing.</li>");
        htm.addln("<li><code>extern</code>: data that is replicated from \"external\" systems like a University ",
                "registrar's database. There are two schemas defined with identical structure: <code>extern</code> ",
                "stores production data, and <code>extern_t</code> supports low-level testing.</li>");
        htm.addln("<li><code>analyt</code>: analytics data that is generated or updated when analytics are run. ",
                "There are two schemas defined with identical structure: <code>analyt</code> for production data, ",
                "and <code>analyt_t</code> for low-level testing.</li>");
        htm.addln("<li><code>termYYYYMM</code>: one schema for each term, where <code>YYYY</code> is a 4-digit ",
                "year and <code>MM</code> is a unique 2-digit code for the term within the year, where ",
                "<code>30</code> indicates a Spring term, <code>60</code> a Summer term, and <code>90</code> a Fall ",
                "term.  Each of these schemas has identical table structure.  An additional <code>term_t</code> ",
                "schema is defined with the same table structure for low-level testing.</li>");
        htm.addln("</ul>");

        htm.sP().add("Since some database products (like Cassandra) perform best when the numbers of tables to search ",
                "through is small (say, under 100), each table group within a schema could exist within its own ",
                "cluster.").eP();
    }

    /**
     * The page showing a single tablespace.
     *
     * @param htm    the {@code HtmlBuilder} to which to append
     * @param schema the tablespace name
     * @param table  the table name (null to show the first table in the space)
     */
    private void doSchema(final HtmlBuilder htm, final String schema, final String table) {

        doSchemaNav(htm, schema);

        final boolean isMathops = "mathops".equals(schema);

        if (isMathops) {
            htm.sP().add("The <code>mathops</code> schema stores configuration data for the MathOps system itself, ",
                    "including the Persistence Layer. Tables in this schema are updated by administrative tools.").eP();

            htm.sP().add("The <code>persistence</code> table group stores configuration data for the MathOps " +
                         "Persistence ",
                    "Layer. It includes:").eP();
            htm.addln("<ul>");
            htm.addln("<li>A list of all defined schemas, including the <code>mathops</code> schema.</li>");
            htm.addln("<li>A list of all defined table groups.</li>");
            htm.addln("<li>A list of all defined tables, each assigned to one table group.</li>");
            htm.addln("<li>A list of physical tables in each schema, and the associated table type.</li>");
            htm.addln("<li>A list of data profiles that select a schema for each defined table.</li>");
            htm.addln("<li>Users and their permissions for administration of the persistence layer.</li>");
            htm.addln("</ul>");
        }

        htm.sH(4).add("Schema:&nbsp;<code>", schema, "</code>").eH(4);

        final Map<String, List<Table>> map = this.tables.get(schema);

        Table found = null;
        String foundGroup = null;
        if (map != null) {
            htm.sDiv("flexbox");
            htm.addln("<nav>");

            for (final Map.Entry<String, List<Table>> entry : map.entrySet()) {
                final String group = entry.getKey();
                htm.addln("Table Group:&nbsp;<span class='red'>", group, "</span>").br();

                final List<Table> tablesInSchema = entry.getValue();
                for (final Table tableInSchema : tablesInSchema) {
                    final String tableName = tableInSchema.getName();
                    boolean hit = false;
                    if (table == null) {
                        if (found == null) {
                            found = tableInSchema;
                            foundGroup = group;
                            hit = true;
                        }
                    } else if (table.equals(tableName)) {
                        found = tableInSchema;
                        foundGroup = group;
                        hit = true;
                    }

                    emitBullet(htm, hit);
                    htm.add("<a href='/doc/schema.html?schema=", schema, "&table=", tableName, "'><code>",
                            tableName, "</code></a>").br();
                }
            }
            htm.addln("</nav>");

            if (found != null && foundGroup != null) {
                final String tableName = found.getName();

                htm.addln("<article>");
                htm.sH(5).add("Table Group: ").sSpan("red").add(foundGroup).eSpan().div("hgap")
                        .add("Table: <code>", tableName, "</code>").eH(5);

                final String desc = found.getDescription();
                if (desc != null) {
                    htm.sP("innerhead").add("Description:").eP();
                    htm.sDiv("indent");
                    htm.add(desc);
                    htm.eDiv();
                }

                final String examples = found.getExamples();
                if (examples != null) {
                    htm.sP("innerhead").add("Examples:").eP();
                    htm.sDiv("indent");
                    htm.add(examples);
                    htm.eDiv();
                }

                htm.sTable();
                htm.sTr().sTh().add("Field").eTh().sTh().add("Type").eTh().sTh().add("Description").eTh().eTr();
                final int numFields = found.getNumFields();
                for (int i = 0; i < numFields; ++i) {
                    final Field field = found.getField(i);

                    final String name = field.getName();
                    final EFieldType type = field.getType();
                    final String typeName = type.name();
                    final String descr = field.getDescription();
                    htm.sTr().sTd().add("<code>", name, "</code>").eTd()
                            .sTd().add(typeName).eTd()
                            .sTd().add(descr);

                    final int numConstraints = field.getNumConstraints();
                    for (int j = 0; j < numConstraints; ++j) {
                        final AbstractFieldConstraint<?> constraint = field.getConstraint(j);
                        final String constraintDesc = constraint.getDescription();
                        htm.br().sSpan("constraint").add("Constraint: ", constraintDesc).eSpan();
                    }

                    htm.eTd().eTr();
                }
                htm.eTable();

                htm.addln("</article>");
                htm.eDiv();
            }
        }
    }

    /**
     * Generates the navigation buttons in the "Generalized Database" sub-menu.
     *
     * @param htm   the {@code HtmlBuilder} to which to append
     * @param which the header selector
     */
    private void doGeneralNav(final HtmlBuilder htm, final String which) {

        htm.sP().add("<a class='lit1' href='/doc/index.html'>Generalized Database</a>",
                "<a class='dim1' href='/doc/schemas.html'>Schemas</a>").eP();

        htm.sP();
        final boolean isOverview = GEN_OVERVIEW.equals(which);
        htm.add(isOverview ? A_CLASS_LIT2 : A_CLASS_DIM2);
        htm.add(" href='/doc/index.html'>Overview</a>");

        final boolean isStructures = GEN_STRUCTURES.equals(which);
        htm.add(isStructures ? A_CLASS_LIT2 : A_CLASS_DIM2);
        htm.add(" href='/doc/general2.html'>Structures</a>");

        final boolean isSelecting = GEN_SELECTING.equals(which);
        htm.add(isSelecting ? A_CLASS_LIT2 : A_CLASS_DIM2);
        htm.add(" href='/doc/general3.html'>Selecting</a>");

        final boolean isUpdating = GEN_UPDATING.equals(which);
        htm.add(isUpdating ? A_CLASS_LIT2 : A_CLASS_DIM2);
        htm.add(" href='/doc/general4.html'>Updating</a>");

        final boolean isImpl = GEN_IMPL.equals(which);
        htm.add(isImpl ? A_CLASS_LIT2 : A_CLASS_DIM2);
        htm.add(" href='/doc/general5.html'>Implementations</a>");

        final boolean isEncoding = GEN_ENCODING.equals(which);
        htm.add(isEncoding ? A_CLASS_LIT2 : A_CLASS_DIM2);
        htm.add(" href='/doc/general6.html'>API Encoding</a>");

        final boolean isEndpoints = GEN_ENDPOINTS.equals(which);
        htm.add(isEndpoints ? A_CLASS_LIT2 : A_CLASS_DIM2);
        htm.add(" href='/doc/general7.html'>API Endpoints</a>");

        htm.eP().hr();
    }

    /**
     * Generates the navigation buttons in the "Schemas" sub-menu.
     *
     * @param htm   the {@code HtmlBuilder} to which to append
     * @param which the header selector
     */
    private void doSchemaNav(final HtmlBuilder htm, final String which) {

        htm.sP().add("<a class='dim1' href='/doc/index.html'>Generalized Database</a>",
                "<a class='lit1' href='/doc/schemas.html'>Schemas</a>").eP();

        htm.sP();
        final boolean isOverview = SCHEMA_OVERVIEW.equals(which);
        htm.add(isOverview ? A_CLASS_LIT2 : A_CLASS_DIM2);
        htm.add(" href='/doc/schemas.html'>Overview</a>");

        for (final String schema : this.tables.keySet()) {
            final boolean match = schema.equals(which);
            htm.add(match ? A_CLASS_LIT2 : A_CLASS_DIM2);
            htm.add(" href='/doc/schema.html?schema=", schema, "'>Schema: <b>", schema, "</b></a>");
        }
        htm.eP().hr();
    }

    /**
     * Emits a bullet character, either visible or hidden, to indicate a selected item in a list.
     *
     * @param htm     the {@code HtmlBuilder} to which to append
     * @param visible true to make the bullet visible.
     */
    private static void emitBullet(final HtmlBuilder htm, final boolean visible) {

        htm.add(visible ? "&bullet;&nbsp;" : "<span style='color:rgba(0,0,0,0%)'>&bullet;</span>&nbsp;");
    }
}
