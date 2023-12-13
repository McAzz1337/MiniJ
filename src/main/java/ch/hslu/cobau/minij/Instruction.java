package ch.hslu.cobau.minij;

import ch.hslu.cobau.minij.ast.expression.BinaryOperator;

public enum Instruction {

    ADD("\tadd\t"),
    SUB("\tsub\t"),
    MUL("\timul\t"),
    DIV("\tidiv\t"),
    MOD("\tidiv\t"),
    MOV("\tmov\t"),
    CMP("\tcmp\t"),
    JMP("\tjmp\t"),
    JE("\tje\t"),
    JNE("\tjne\t"),
    JL("\tjl\t"),
    JLE("\tjle\t"),
    JG("\tjg\t"),
    JGE("\tJGE\t"),
    PUSH("\tpush\t"),
    POP("\tpop\t"),
    CALL("\tcall\t"),
    RET("\tret\t"),
    XOR("\txor\t");

    public final String mnemonic;

    private Instruction(String mnemonic) {

        this.mnemonic = mnemonic;
    }

    public static String asMnemonic(BinaryOperator operator) {

        switch (operator) {
            case PLUS: return ADD.mnemonic;
            case MINUS: return SUB.mnemonic;
            case TIMES: return MUL.mnemonic;
            case DIV: return DIV.mnemonic;
            case MOD: return MOD.mnemonic;
        }


        return "Operator " + operator.toString() + " has no mnemonic";
    }

    public static Instruction binaryOperatorAsInstruction(BinaryOperator operator) {

        switch (operator) {
            case PLUS: return ADD;
            case MINUS: return SUB;
            case TIMES: return MUL;
            case DIV: return DIV;
            case MOD: return MOD;
        }

        return null;
    }


}
