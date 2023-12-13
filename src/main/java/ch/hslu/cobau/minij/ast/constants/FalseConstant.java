/**
 * Copyright (c) 2020-2023 HSLU Informatik. All rights reserved.
 * This code and any derivative work thereof must remain private.
 * Public distribution is prohibited.
 */
package ch.hslu.cobau.minij.ast.constants;

import ch.hslu.cobau.minij.ast.AstVisitor;
import ch.hslu.cobau.minij.ast.type.BooleanType;
import ch.hslu.cobau.minij.ast.type.Type;

public class FalseConstant extends Constant {

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visit(this);
    }


    @Override
    public Type getType() {
        return new BooleanType();
    }

}
