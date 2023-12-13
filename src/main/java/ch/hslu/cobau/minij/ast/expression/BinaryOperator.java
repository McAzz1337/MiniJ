/**
 * Copyright (c) 2020-2023 HSLU Informatik. All rights reserved.
 * This code and any derivative work thereof must remain private.
 * Public distribution is prohibited.
 */
package ch.hslu.cobau.minij.ast.expression;

import ch.hslu.cobau.minij.Code;
import ch.hslu.cobau.minij.ast.type.*;


public enum BinaryOperator {
    PLUS,
    MINUS,
    TIMES,
    DIV,
    MOD,
    EQUAL,
    UNEQUAL,
    LESSER,
    LESSER_EQ,
    GREATER,
    GREATER_EQ,
    AND,
    OR;

    public static Type getResultingType(Type left, Type right, BinaryOperator operator) {

        switch (operator) {
            case PLUS: {
                if (left instanceof IntegerType && right instanceof IntegerType)  {
                    return new IntegerType();
                }
                else if (left instanceof StringType && right instanceof StringType) {
                    return new StringType();
                }
                break;
            }
            case MINUS:
            case TIMES:
            case DIV:
            case MOD:
            case AND:
            case OR:
                if ((left instanceof BooleanType && right instanceof BooleanType)) {
                    return new BooleanType();
                }
                else if (left instanceof IntegerType && right instanceof IntegerType) {
                    return new IntegerType();
                }
                break;
            case EQUAL:
            case UNEQUAL:
            case LESSER:
            case LESSER_EQ:
            case GREATER:
            case GREATER_EQ:
                return new BooleanType();

        }

        return null;
    }

    public static boolean areCompatibleWithOperator(Type left, Type right, BinaryOperator operator) {

        switch (operator) {
            case PLUS:
                if ((left instanceof IntegerType && right instanceof IntegerType) ||
                        (left instanceof StringType && right instanceof StringType)) {
                    return true;
                }
                break;
            case MINUS:
            case TIMES:
            case DIV:
            case MOD:
                if (left instanceof IntegerType && right instanceof IntegerType) {
                    return true;
                }
                break;
            case EQUAL:
            case UNEQUAL:
                if (left instanceof RecordType || right instanceof RecordType ||
                    left instanceof ArrayType || right instanceof ArrayType) {
                    return false;
                } else {
                    return true;
                }
            case LESSER:
            case LESSER_EQ:
            case GREATER:
            case GREATER_EQ:
                if (left instanceof BooleanType || right instanceof BooleanType ||
                        left instanceof RecordType || right instanceof RecordType ||
                        left instanceof ArrayType || right instanceof ArrayType ||
                        !left.equals(right)) {
                    return false;
                }
            case AND:
            case OR:
                if (left instanceof RecordType || right instanceof RecordType ||
                        left instanceof ArrayType || right instanceof ArrayType ||
                        !left.equals(right)) {
                    return false;
                }
                else {
                    return true;
                }
        }

        return false;
    }


    public boolean isComparison() {

        return this == EQUAL || this == UNEQUAL || this == LESSER || this == LESSER_EQ ||
                this == GREATER ||this == GREATER_EQ;
    }
}
