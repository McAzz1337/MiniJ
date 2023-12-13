/**
 * Copyright (c) 2020-2023 HSLU Informatik. All rights reserved.
 * This code and any derivative work thereof must remain private.
 * Public distribution is prohibited.
 */
package ch.hslu.cobau.minij.ast.type;

import ch.hslu.cobau.minij.SymbolTable;
import ch.hslu.cobau.minij.ast.entity.Declaration;
import ch.hslu.cobau.minij.ast.entity.Struct;

import java.util.List;
import java.util.Objects;

public class RecordType extends Type {
    private final String identifier;

    public RecordType(String identifier) {
        Objects.requireNonNull(identifier);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        RecordType that = (RecordType) other;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public String toString() {
        return "record " + identifier;
    }

    public int size(SymbolTable symbolTable) {


        int size = 0;
        Struct s = symbolTable.getStruct(identifier);

        List<Declaration> declarations = s.getDeclarations();
        for (Declaration d : declarations) {

            Type t = d.getType();

            if (t instanceof RecordType r) {

                size += r.size(symbolTable);
            }
            else {
                size += t.size();
            }
        }

        return size;
    }

    @Override
    public String dataAllocation() {

        return "Allocation type for RecordType not implemented";
    }
}
