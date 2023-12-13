package ch.hslu.cobau.minij;

import ch.hslu.cobau.minij.ast.AstElement;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class Scope {

    Set<String> symbols = new HashSet<>();
    Scope parent = null;

    public Scope() {

    }


    public Scope(Scope parent) {

        this.parent = parent;
    }

    public void addSymbol(String symbol) {

        symbols.add(symbol);
    }

    public boolean hasSymbol(String symbol) {

        return symbols.contains(symbol);
    }

    public boolean hasSymbolInSelfOrParent(String symbol) {

        if (hasSymbol(symbol))
            return true;

        Scope scope = parent;
        while (scope != null) {

            if(scope.symbols.contains(symbol))
                return true;

            scope = scope.parent;
        }

        return false;
    }

    public Scope getScopeOfIdentifier(String identifier) {

        Scope scope = this;
        do {

            if (scope.symbols.contains(identifier)) {
                break;
            }

            scope = parent;
        } while(scope != null);

        return scope;
    }
    public Scope getParent() {

        return parent;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Scope s) {

            return this == s;
        }

        return false;
    }

}
