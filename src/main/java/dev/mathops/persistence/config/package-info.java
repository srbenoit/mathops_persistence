/**
 * Classes to manage the configuration of the data layer.
 *
 * <p>
 * When clients call most data layer functions (directly or through the API), they pass a schema ID and a context ID
 * (both integers).  The implementation then maps this to a physical database table and performs the operation.
 *
 * <p>
 * Clients can query for all schemas and all contexts available, and can then choose the appropriate one based on its
 * needs.  It can also query the tables each schema provided to ensure all required tables are available as part of
 * application startup.
 */
package dev.mathops.persistence.config;
