/**
 * Copyright (c) 2020-2023 HSLU Informatik. All rights reserved.
 * This code and any derivative work thereof must remain private.
 * Public distribution is prohibited.
 */
package ch.hslu.cobau.minij.ast.constants;

import ch.hslu.cobau.minij.ast.expression.Expression;
import ch.hslu.cobau.minij.ast.type.Type;

public abstract class Constant extends Expression {

    public abstract Type getType();

}
