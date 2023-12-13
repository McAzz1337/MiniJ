/**
 * Copyright (c) 2020-2023 HSLU Informatik. All rights reserved.
 * This code and any derivative work thereof must remain private.
 * Public distribution is prohibited.
 */
package ch.hslu.cobau.minij.ast.type;

public class Type {

    public int size() {

        return -1;
    }

    public String dataAllocation() {

        return "Base type Type has no allocation size";
    }

    public static boolean isPrimitive(Type type) {

        return type instanceof IntegerType ||
                type instanceof BooleanType;
    }
}
