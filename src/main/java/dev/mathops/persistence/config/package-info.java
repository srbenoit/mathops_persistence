/**
 * Classes to manage the configuration of the data layer.
 *
 * <p>
 * One or more <b>schemas</b> are defined as configuration objects, each with a unique name.  Within each schema, a
 * number of <b>contexts</b> are defined, each with a unique name.  Every context must support all the tables defined in
 * the schema.  Every schema also defines a collection of <b>tables</b>, each with a list of <b>fields</b>.
 *
 * <p>
 * One or more <b>implementations</b> are configured here (they must exist as code in the layer, but the configuration
 * here sets parameters used by the implementation, and gives it a name to allow it to be referenced.  The configuration
 * of an implementation includes the fully-qualified name of its implementation class.
 *
 * <p>
 * One or more <b>roles</b> can be defined, along with the set of <b>permissions</b> assigned to that role.  One or more
 * <b>users</b> can then be defined and assigned to any number of roles.  Authentication properties can be configured
 * for each user.  User permissions can extend to administration of the configuration objects defined in this package.
 *
 * <p>
 * One or more named data configurations can then be constructed.  Each data configuration selects one implementation
 * object for every defined schema and context.  When users are authenticated to establish a connection to the data
 * layer, they request a specific data configuration by name.
 */
package dev.mathops.persistence.config;
