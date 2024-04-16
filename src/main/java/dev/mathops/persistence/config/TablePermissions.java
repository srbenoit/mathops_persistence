package dev.mathops.persistence.config;

/**
 * A set of permissions for a single table that can be configured for a role.
 *
 * @param canCount  true if users with this permission can execute "count" on the table
 * @param canQuery  true if users with this permission can execute "query" on the table
 * @param canInsert true if users with this permission can execute "insert" on the table, or "multi-insert" that
 *                  includes the table
 * @param canDelete true if users with this permission can execute "delete" on the table, or "delete-multi" that
 *                  includes the table
 * @param canUpdate true if users with this permission can execute "update" on the table, or "update-multi" that
 *                  includes the table
 */
public record TablePermissions(boolean canCount, boolean canQuery, boolean canInsert, boolean canDelete,
                               boolean canUpdate) {
}
