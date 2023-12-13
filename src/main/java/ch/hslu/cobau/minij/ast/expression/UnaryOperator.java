/**
 * Copyright (c) 2020-2023 HSLU Informatik. All rights reserved.
 * This code and any derivative work thereof must remain private.
 * Public distribution is prohibited.
 */
package ch.hslu.cobau.minij.ast.expression;

import ch.hslu.cobau.minij.ast.type.BooleanType;
import ch.hslu.cobau.minij.ast.type.IntegerType;
import ch.hslu.cobau.minij.ast.type.Type;

public enum UnaryOperator {
    MINUS,
    NOT,
    PRE_INCREMENT,
    PRE_DECREMENT,
    POST_INCREMENT,
    POST_DECREMENT;

    public static boolean isCompatible(UnaryOperator operator, Type type) {

        switch (operator) {
            case MINUS:
            case PRE_DECREMENT:
            case PRE_INCREMENT:
            case POST_DECREMENT:
            case POST_INCREMENT:
                if (type instanceof IntegerType) {
                    return true;
                }
                break;
            case NOT:
                if (type instanceof BooleanType) {
                    return true;
                }
                break;
        }

        return false;
    }
}
