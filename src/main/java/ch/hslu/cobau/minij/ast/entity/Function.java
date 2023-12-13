/**
 * Copyright (c) 2020-2023 HSLU Informatik. All rights reserved.
 * This code and any derivative work thereof must remain private.
 * Public distribution is prohibited.
 */
package ch.hslu.cobau.minij.ast.entity;

import ch.hslu.cobau.minij.SymbolTable;
import ch.hslu.cobau.minij.ast.AstVisitor;
import ch.hslu.cobau.minij.ast.expression.Expression;
import ch.hslu.cobau.minij.ast.statement.Block;
import ch.hslu.cobau.minij.ast.statement.Statement;
import ch.hslu.cobau.minij.ast.type.ArrayType;
import ch.hslu.cobau.minij.ast.type.Type;

import java.util.List;
import java.util.Objects;

public class Function extends Block {
    private final String identifier;
	private final Type returnType;
    private final List<Declaration> formalParameters;

    public Function(String identifier, Type returnType, List<Declaration> formalParameters, List<Statement> statements) {
        super(statements);
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(returnType);
        Objects.requireNonNull(formalParameters);

        this.identifier = identifier;
        this.returnType = returnType;
        this.formalParameters = formalParameters;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<Declaration> getFormalParameters() {
        return formalParameters;
    }

    public void accept(AstVisitor astVisitor) {
        astVisitor.visit(this);
    }

    @Override
    public void visitChildren(AstVisitor astVisitor) {
        formalParameters.forEach(parameter -> parameter.accept(astVisitor));
        super.visitChildren(astVisitor); // statements
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Function f) {

            if (formalParameters.size() != f.formalParameters.size())
                return false;
            if (!returnType.equals(f.returnType))
                return false;

            for (int i = 0; i < formalParameters.size(); i++) {

                Declaration d1 = formalParameters.get(i);
                Declaration d2 = f.formalParameters.get(i);

                if (!d1.equals(d2)) {
                    return false;
                }
            }
        }

        return true;
    }


    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(returnType.toString() + " " + identifier + "(");
        formalParameters.forEach(p -> s.append(p.toString() + ", "));

        if (formalParameters.size() > 1) {
            return s.toString().substring(0, s.length() - 2) + ")";
        }

        return s.toString() + ")";
    }

    public boolean matchesParameters(List<Expression> parameters, SymbolTable symbolTable) {

        if (parameters.size() != formalParameters.size())
            return false;

        for (int i = 0; i < parameters.size(); i++) {

            Type arg = formalParameters.get(i).getType();
            Expression.typeToLookFor = arg;
            Type param = Expression.evaluateType(parameters.get(i), symbolTable);
            if (!arg.equals(param))
                return false;
        }

        return true;
    }
}
