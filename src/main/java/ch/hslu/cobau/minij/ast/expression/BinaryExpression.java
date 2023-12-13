/**
 * Copyright (c) 2020-2023 HSLU Informatik. All rights reserved.
 * This code and any derivative work thereof must remain private.
 * Public distribution is prohibited.
 */
package ch.hslu.cobau.minij.ast.expression;

import ch.hslu.cobau.minij.Code;
import ch.hslu.cobau.minij.Register;
import ch.hslu.cobau.minij.SymbolTable;
import ch.hslu.cobau.minij.ast.AstVisitor;

import java.util.Objects;
import java.util.Stack;

import ch.hslu.cobau.minij.Instruction;
import ch.hslu.cobau.minij.ast.constants.Constant;

public class BinaryExpression extends Expression  {
    private final Expression left;
    private final Expression right;
    private final BinaryOperator binaryOperator;

    public BinaryExpression(Expression left, Expression right, BinaryOperator binaryOperator) {
        Objects.requireNonNull(left);
        Objects.requireNonNull(right);
        Objects.requireNonNull(binaryOperator);

        this.left = left;
        this.right = right;
        this.binaryOperator = binaryOperator;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    public BinaryOperator getBinaryOperator() {
        return binaryOperator;
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visit(this);
    }

    @Override
    public void visitChildren(AstVisitor astVisitor) {
        left.accept(astVisitor);
        right.accept(astVisitor);
    }

    public Instruction getJumpInstructionForIf() {

        switch (binaryOperator) {
                case EQUAL: return Instruction.JNE;
                case UNEQUAL: return  Instruction.JE;
                case LESSER: return Instruction.JGE;
                case LESSER_EQ: return Instruction.JG;
                case GREATER: return Instruction.JLE;
                case GREATER_EQ: return Instruction.JL;
        }

        return null;
    }

    public Instruction getJumpForWhile() {

        switch (binaryOperator) {
            case EQUAL: return Instruction.JE;
            case UNEQUAL: return  Instruction.JNE;
            case LESSER: return Instruction.JL;
            case LESSER_EQ: return Instruction.JLE;
            case GREATER: return Instruction.JG;
            case GREATER_EQ: return Instruction.JGE;
        }

        return null;
    }



    public Register evaluate(Code code, SymbolTable symbolTable) {


        return null;
    }


}
