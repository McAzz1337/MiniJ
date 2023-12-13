/**
 * Copyright (c) 2020-2023 HSLU Informatik. All rights reserved.
 * This code and any derivative work thereof must remain private.
 * Public distribution is prohibited.
 */
package ch.hslu.cobau.minij.ast.expression;

import ch.hslu.cobau.minij.*;
import ch.hslu.cobau.minij.ast.AstElement;
import ch.hslu.cobau.minij.ast.constants.*;
import ch.hslu.cobau.minij.ast.entity.Declaration;
import ch.hslu.cobau.minij.ast.entity.Function;
import ch.hslu.cobau.minij.ast.entity.Struct;
import ch.hslu.cobau.minij.ast.type.*;

import java.math.BigInteger;
import java.util.List;

import ch.hslu.cobau.minij.Instruction;
import ch.hslu.cobau.minij.Register;

public abstract class Expression extends AstElement {

    public static Type typeToLookFor;
    public static Type evaluateType(Expression expression, SymbolTable symbolTable) {

        Type type = null;
        if (expression == null) {
            type = new VoidType();
        }
        else if (expression instanceof IntegerConstant i) {

            BigInteger integer = new BigInteger(String.valueOf(i.getValue()));
            if (integer.compareTo(new BigInteger(String.valueOf(Long.MAX_VALUE))) > 0) {

                MiniJCompiler.printErrorAndExit("Integer constant provided larger than allowed maximum: " + integer.toString());
            }
            else if (integer.compareTo(new BigInteger(String.valueOf(Long.MIN_VALUE))) < 0) {

                MiniJCompiler.printErrorAndExit("Integer constant provided smaller than allowed minimum: " + integer.toString());
            }
            type = new IntegerType();
        }
        else if (expression instanceof StringConstant) {
            type = new StringType();
        }
        else if (expression instanceof TrueConstant || expression instanceof FalseConstant) {
            type = new BooleanType();
        }
        else if (expression instanceof VariableAccess variable) {
            Declaration declaration = symbolTable.getDeclaration(variable.getIdentifier());
            if (declaration == null)  {

                MiniJCompiler.printErrorAndExit("Accessing undefined variable: " + variable.getIdentifier());
            }
            type = declaration.getType();
        }
        else if (expression instanceof ArrayAccess array) {
            VariableAccess variable = (VariableAccess) array.getBase();
            Declaration declaration = symbolTable.getDeclaration(variable.getIdentifier());
            if (declaration == null) {

                MiniJCompiler.printErrorAndExit("Accessing undefined array: " + variable.getIdentifier());
            }
            type = ((ArrayType) declaration.getType()).getType();
        }
        else if (expression instanceof FieldAccess field) {
            VariableAccess variable = (VariableAccess) field.getBase();
            Declaration declaration = symbolTable.getDeclaration(variable.getIdentifier());
            if (declaration == null)  {

                MiniJCompiler.printErrorAndExit("Trying to access field of undefined variable: " +
                        variable.getIdentifier() + "." + field.getField());
            }
            if (!(declaration.getType() instanceof RecordType)) {

                MiniJCompiler.printErrorAndExit("Trying to access field on a primitive type: " +
                        declaration.getIdentifier() +
                        "." +
                        field.getField());
            }
            Struct struct = symbolTable.getStruct(((RecordType) declaration.getType()).getIdentifier());
            declaration = struct.getDeclarationByIdentifier(field.getField());
            if (declaration == null) {

                MiniJCompiler.printErrorAndExit("Trying to access undefined field: " +
                        struct.getIdentifier() + "::" + field.getField());
            }
            type = declaration.getType();
        }
        else if (expression instanceof BinaryExpression bin) {
            Type left = evaluateType(bin.getLeft(), symbolTable);
            Type right = evaluateType(bin.getRight(), symbolTable);
            if (!left.equals(right)) {
                MiniJCompiler.printErrorAndExit("Type mismatch in binary expression: " +
                        bin.getLeft().toString() + " " +
                        bin.getBinaryOperator().toString() + " " +
                        bin.getRight().toString());
            }

            BinaryOperator operator = bin.getBinaryOperator();
            if (!BinaryOperator.areCompatibleWithOperator(left, right, operator)) {

                MiniJCompiler.printErrorAndExit("Incompatible types for binary operator: " +
                        left.toString() + " " +
                        operator.toString() + " " +
                        right.toString());
            }
            if (bin.getLeft() instanceof ArrayAccess) left = new ArrayType(left);
            if (bin.getRight() instanceof ArrayAccess) right = new ArrayType(right);
            type = BinaryOperator.getResultingType(left, right, operator);
        }
        else if (expression instanceof UnaryExpression un) {
            type = evaluateType(un.getExpression(), symbolTable);
            UnaryOperator operator = un.getUnaryOperator();
            if (!UnaryOperator.isCompatible(operator, type)) {

                MiniJCompiler.printErrorAndExit("Incompatible type for unary operator: " +
                        operator.toString() + " " + type);
            }

        }
        else if (expression instanceof Constant c) {
            type = c.getType();
        }
        else if (expression instanceof CallExpression call) {
            Scope scope = symbolTable.getScope(call);
            List<Function> functions = symbolTable.getAllFunctionsByName(call.getIdentifier());
            if (functions.size() == 0) {

                MiniJCompiler.printErrorAndExit("Call to undefined function " + call.getIdentifier());
            }

            boolean found = false;
            for (Function f : functions) {

                if (!f.matchesParameters(call.getParameters(), symbolTable))
                    continue;
                if (!f.getReturnType().equals(typeToLookFor))
                    continue;

                found = true;
                type = f.getReturnType();
                break;
            }
            if (!found) {

                MiniJCompiler.printErrorAndExit("Call to undefined function: " + call.getIdentifier());
            }
        }

        return type;
    }

    public static String evaluateAsString(Expression expr, SymbolTable symbolTable) {

        return evaluateAsString(expr, symbolTable, null);
    }

    public static String evaluateAsString(Expression expr, SymbolTable symbolTable, Register dst) {

        if (expr instanceof TrueConstant t) {

            if (dst != null) {

                return Instruction.MOV.mnemonic + dst.name + ", 1";
            }

            return "1";
        }
        else if (expr instanceof FalseConstant f) {

            if (dst != null) {

                return Instruction.MOV.mnemonic + dst.name + ", 0";
            }

            return "0";
        }
        else if (expr instanceof IntegerConstant i) {

            if (dst != null) {

                return Instruction.MOV.mnemonic + dst.name + ", " + Long.toString(i.getValue());
            }

            return Long.toString(i.getValue());
        }
        else if (expr instanceof StringConstant s) {

            if (dst != null) {

                return Instruction.MOV.mnemonic + dst.name + ", " + s.getValue();
            }

            return s.getValue();
        }
        else if (expr instanceof ArrayAccess a) {

            VariableAccess v = (VariableAccess) a.getBase();
            Declaration d = symbolTable.getDeclaration(v.getIdentifier());

        }
        else if (expr instanceof BinaryExpression b) {

            String left = evaluateAsString(b.getLeft(), symbolTable);
            String right = evaluateAsString(b.getRight(), symbolTable, dst);

            String mnemonic = Instruction.asMnemonic(b.getBinaryOperator());

            if (dst != null) {

                String s = right + "\n" + mnemonic + dst.name + ", " + left;
                dst = left.contains("rbp") ? Register.RBP : dst;
                return s;
            }

            return mnemonic + left + ", " + right;
        }
        else if (expr instanceof CallExpression c) {

            return "call\t" + c.getIdentifier();
        }
        else if (expr instanceof FieldAccess f) {


            return "FieldAccess not Implemented";
        }
        else if (expr instanceof UnaryExpression u) {

            return "UnaryExpression not Implemented";
        }
        else if (expr instanceof VariableAccess v) {

            Declaration d = symbolTable.getDeclaration(v.getIdentifier());
            Scope scope = symbolTable.getScope(d);

            Symbol symbol = symbolTable.getSymbol(d.getIdentifier(), scope);

            if (dst == null) {

                return symbol.getRegisterAsString(true);
            }
            else {

                return Instruction.MOV.mnemonic + dst.name + ", " + symbol.getRegisterAsString(true);
            }
        }
        else if (expr == null) {

            return "0";
        }
    
    
        return "EXPR INSTANCE OF NOTHING";
    }

    public static String asCondition(Expression condition) {

        if (condition instanceof BinaryExpression b) {

            String mnemonic = Instruction.asMnemonic(b.getBinaryOperator());

        }

        return "Expression.asCondition() not implemented";
    }

}