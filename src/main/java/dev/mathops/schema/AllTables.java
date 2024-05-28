package dev.mathops.schema;

import dev.mathops.commons.builder.SimpleBuilder;
import dev.mathops.persistence.Table;
import dev.mathops.schema.main.HoldTypeTable;
import dev.mathops.schema.main.LocalLoginTable;
import dev.mathops.schema.main.ParameterTable;
import dev.mathops.schema.main.RolePermissionTable;
import dev.mathops.schema.main.RoleTable;
import dev.mathops.schema.main.SchoolTable;
import dev.mathops.schema.main.TermTable;
import dev.mathops.schema.main.ZipCodeTable;
import dev.mathops.schema.mathops.FieldTable;
import dev.mathops.schema.mathops.TableTable;
import dev.mathops.schema.term.TermWeekTable;

import java.util.ArrayList;
import java.util.List;

/**
 * A container object that holds all table definitions.
 */
public final class AllTables {

    /** The singleton collection of all tables. */
    public static final AllTables INSTANCE = new AllTables();

    /** The tables in the schema. */
    public final List<Table> tables;

    /**
     * Constructs a new {@code AllTables}.
     */
    private AllTables() {

        this.tables = new ArrayList<>(10);

        // mathops.persistence schema
        this.tables.add(TableTable.INSTANCE);
        this.tables.add(FieldTable.INSTANCE);

        // main.system schema
        this.tables.add(TermTable.INSTANCE);
        this.tables.add(RoleTable.INSTANCE);
        this.tables.add(RolePermissionTable.INSTANCE);
        this.tables.add(LocalLoginTable.INSTANCE);
        this.tables.add(ParameterTable.INSTANCE);
        this.tables.add(HoldTypeTable.INSTANCE);
        this.tables.add(SchoolTable.INSTANCE);
        this.tables.add(ZipCodeTable.INSTANCE);

        // term.system schema
        this.tables.add(TermWeekTable.INSTANCE);
    }

    /**
     * Generates a diagnostic string representation of the object.
     *
     * @return the string representation
     */
    @Override
    public String toString() {

        return SimpleBuilder.concat("PrimarySchema{tables=", this.tables, "}");
    }
}
