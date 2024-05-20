package dev.mathops.schema;

import dev.mathops.commons.builder.SimpleBuilder;
import dev.mathops.persistence.Table;

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

        this.tables.add(TermTable.INSTANCE);
        this.tables.add(SchoolTable.INSTANCE);
        this.tables.add(HoldTypeTable.INSTANCE);
        this.tables.add(LocalLoginTable.INSTANCE);
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
