package ch.hslu.cobau.minij;

import ch.hslu.cobau.minij.ast.AstVisitor;
import ch.hslu.cobau.minij.ast.constants.*;
import ch.hslu.cobau.minij.ast.entity.Declaration;
import ch.hslu.cobau.minij.ast.entity.Function;
import ch.hslu.cobau.minij.ast.entity.Struct;
import ch.hslu.cobau.minij.ast.entity.Unit;
import ch.hslu.cobau.minij.ast.expression.*;
import ch.hslu.cobau.minij.ast.statement.*;
import ch.hslu.cobau.minij.ast.type.*;
import org.antlr.v4.codegen.model.decl.Decl;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.util.List;
import java.util.Objects;

public class CodeGenerator implements AstVisitor {

    private static final int MAX_PASS_REGISTER = 6;
    private static final String LABEL = ".LABEL";

    Code code = new Code();
    Code patch;
    SymbolTable symbolTable;
    Scope currentScope;


    boolean isMain = false;
    int rbpOffset = 0;
    int stackOffset = -8;
    int labelCount = 0;




    private static final String[] BUILT_INS = {
            "readInt",
            "readChar",
            "writeInt",
            "writeChar",
            "_exit"
    };



    private static boolean isBuiltIn(String identifier) {

        for (String s : BUILT_INS) {

            if (s.equals(identifier)) {

                return true;
            }
        }

        return false;
    }

    public CodeGenerator(SymbolTable symbolTable) {

        code.addSections();
        this.symbolTable = symbolTable;
        currentScope = symbolTable.getGlobalScope();

        for (String s : BUILT_INS) {

            code.addExternFunction(s);
        }

        code.addGlobalFunction("_start");

    }


    @Override
    public void visit(Unit program) {

        program.visitChildren(this);
    }

    @Override
    public void visit(Function function) {


        currentScope = symbolTable.getScope(function);


        if (Objects.equals(function.getIdentifier(), "main")) {

            code.addFunctionLabel("_start");
            code.addInstruction(Instruction.PUSH, Register.RBP.name).newLine();
            code.addInstruction(Instruction.MOV, Register.RBP.name, Register.RSP.name).newLine();
            isMain = true;
        }
        else if (!isBuiltIn(function.getIdentifier())){

            code.addFunctionLabel(function.getIdentifier());
            code.addInstruction(Instruction.PUSH, Register.RBP.name).newLine();
            code.addInstruction(Instruction.MOV, Register.RBP.name, Register.RSP.name).newLine();
            isMain = false;
        }
        else {

            return;
        }

        List<Declaration> declarations = symbolTable.getDeclarationsInScope(currentScope);
        List<Declaration> parameters = function.getFormalParameters();

        int sub = 0;
        int paramCount = 0;
        for (Declaration d : declarations) {

            Type type = d.getType();
            if (parameters.contains(d) && paramCount < MAX_PASS_REGISTER) {
                sub += type.size();
                paramCount++;
            }
            else if (!parameters.contains(d)) {

                sub += type.size();
            }
        }


        if (sub > 0) {

            code.addInstruction(Instruction.SUB, Register.RSP.name, Integer.toString(sub)).newLine();
        }

        function.visitChildren(this);

        rbpOffset = 0;
        stackOffset = -8;
        symbolTable.clear(currentScope);
        labelCount = 0;

        if (!isMain) {

            boolean hasReturnStatement = false;
            List<Statement> statements =  function.getStatements();
            for (Statement s : statements) {

                if (s instanceof ReturnStatement) {

                    hasReturnStatement = true;
                    break;
                }
            }

            if (!hasReturnStatement) {
                code.addInstruction(Instruction.MOV, Register.RSP.name, Register.RBP.name).newLine();
                code.addInstruction(Instruction.POP, Register.RBP.name).newLine();
                code.addInstruction(Instruction.RET).newLine();
            }
        }
    }

    @Override
    public void visit(Declaration declaration) {

        Type type = declaration.getType();
        Symbol symbol = new Symbol(declaration.getIdentifier(), declaration.getType());

        if (symbolTable.isGlobalScope(currentScope)) {

            symbolTable.add(symbol, currentScope);
            code.addData(declaration.getIdentifier(), type.dataAllocation(), type.size());
        }
        else {

            Function function = symbolTable.getFunctionOfScope(currentScope);
            List<Declaration> parameters = function.getFormalParameters();

            if (parameters.contains(declaration)) {
                int i = 0;
                for (; i < parameters.size(); i++) {

                    if (parameters.get(i).equals(declaration)) {

                        break;
                    }
                }
                if (i < MAX_PASS_REGISTER) {

                    rbpOffset += type.size();
                    symbol.setRegister(Register.RBP, rbpOffset);
                    code.addInstruction(Instruction.MOV, symbol.getRegisterAsString(true), Register.values()[i].name).newLine();
                }
                else {

                    stackOffset -= type.size();
                    symbol.setRegister(Register.RBP, stackOffset);

                }

                symbolTable.add(symbol, currentScope);
            }
        }


        declaration.visitChildren(this);
    }

    @Override
    public void visit(Struct record) {

        record.visitChildren(this);
    }

    @Override
    public void visit(IfStatement ifStatement) {


        Expression condition = ifStatement.getExpression();
        if (!(condition instanceof VariableAccess) &&
                !(condition instanceof FalseConstant) &&
                !(condition instanceof TrueConstant)) {

            condition.accept(this);
        }

        if (condition instanceof BinaryExpression b) {

            if (b.getLeft() instanceof BinaryExpression l && b.getRight() instanceof BinaryExpression r) {

                Register result = Register.getFreeRegister();
                l.accept(this);
                code.addInstruction(Instruction.CMP, result.name, "1").newLine();

                if (b.getBinaryOperator() == BinaryOperator.AND) {

                    code.addInstruction(Instruction.JNE, LABEL + labelCount);
                }
                else if (b.getBinaryOperator() == BinaryOperator.OR) {

                    code.addInstruction(Instruction.JE, LABEL + labelCount);
                }

                r.accept(this);
                code.addInstruction(Instruction.CMP, result.name, "1").newLine();
                code.addInstruction(Instruction.JMP, LABEL + labelCount).newLine();
            }

            Block elseBlock = ifStatement.getElseBlock();

            code.addInstruction(b.getJumpInstructionForIf(), LABEL + labelCount).newLine();
            ifStatement.visitOnlyStatements(this);
            if (elseBlock != null) code.addInstruction(Instruction.JMP, LABEL + (labelCount + 1)).newLine();
            code.addLabel(LABEL + labelCount++);

            if (elseBlock != null) {

                elseBlock.accept(this);
                code.addLabel(LABEL + labelCount++);
            }

            return;
        }
        else if (condition instanceof VariableAccess v) {

            Register dst = Register.getFreeRegister();
            code.addInstruction(Instruction.MOV, dst.name).comma();
            v.accept(this);
            code.newLine().addInstruction(Instruction.CMP, "QWORD " + dst.name, "1").newLine();
            code.addInstruction(Instruction.JNE, LABEL + labelCount).newLine();
        }
        else if (condition instanceof FalseConstant) {

            Block elseBlock = ifStatement.getElseBlock();

            if (elseBlock != null) {

                ifStatement.getElseBlock().accept(this);
            }
            return;
        }
        else if (condition instanceof TrueConstant) {

            ifStatement.visitOnlyStatements(this);
            return;
        }


        ifStatement.visitChildrenWithoutExpression(this);


        code.addLabel(LABEL + labelCount++);


    }

    @Override
    public void visit(WhileStatement whileStatement) {

        Expression condition = whileStatement.getExpression();

        if (!(condition instanceof VariableAccess) &&
                !(condition instanceof FalseConstant) &&
                !(condition instanceof TrueConstant) &&
                !(condition instanceof BinaryExpression)) {

            condition.accept(this);
        }


        String loopLabel = LABEL + labelCount++;
        String conditionLabel = LABEL + labelCount++;

        if (condition instanceof FalseConstant) {

            return;
        }
        else if (condition instanceof TrueConstant) {

            code.addLabel(loopLabel);
            whileStatement.visitChildren(this);
            code.addInstruction(Instruction.JMP, loopLabel).newLine();
            return;
        }
        else if (condition instanceof VariableAccess v) {

            code.addInstruction(Instruction.JMP, conditionLabel).newLine();
            code.addLabel(loopLabel);
            whileStatement.visitChildrenWithoutExpression(this);

            Register dst = Register.getFreeRegister();
            code.addLabel(conditionLabel);
            code.addInstruction(Instruction.MOV, dst.name).comma();
            v.accept(this);
            code.newLine().addInstruction(Instruction.CMP, "QWORD " + dst.name, "1").newLine();
            code.addInstruction(Instruction.JE, loopLabel).newLine();
        }
        else if (condition instanceof BinaryExpression b) {

            code.addInstruction(Instruction.JMP, conditionLabel).newLine();
            code.addLabel(loopLabel);

            whileStatement.visitChildrenWithoutExpression(this);

            code.addLabel(conditionLabel);
            b.accept(this);

            Instruction jump = b.getJumpForWhile();
            code.addInstruction(jump,  loopLabel).newLine();
        }



    }

    @Override
    public void visit(ReturnStatement returnStatement) {



        if (isMain) {
            String exitCode = Expression.evaluateAsString(returnStatement.getExpression(), symbolTable);
            code.addInstruction(Instruction.MOV, "QWORD " + Register.RDI.name, exitCode).newLine();
            code.addInstruction(Instruction.CALL, "_exit").newLine();
        }
        else {

            code.addInstruction(Instruction.MOV,
                                    Register.RAX.name,
                                    Expression.evaluateAsString(returnStatement.getExpression(), symbolTable)).newLine();

            code.addInstruction(Instruction.MOV, Register.RSP.name, Register.RBP.name).newLine();
            code.addInstruction(Instruction.POP, Register.RBP.name).newLine();

            code.addInstruction(Instruction.RET).newLine();
        }
    }

    @Override
    public void visit(AssignmentStatement assignment) {


        Expression left = assignment.getLeft();
        Symbol dst = null;
        if (left instanceof VariableAccess v) {

            dst = symbolTable.getSymbol(symbolTable.getDeclaration(v.getIdentifier()).getIdentifier(), currentScope);
        }


        Expression right = assignment.getRight();

        if (right instanceof Constant c) {

            code.addInstruction(Instruction.MOV, dst.getRegisterAsString(true)).comma();
            c.accept(this);
            code.newLine();
        }
        else if (right instanceof VariableAccess v) {

            code.addInstruction(Instruction.MOV, dst.getRegisterAsString(true)).comma();
            v.accept(this);
            code.newLine();
        }
        else if (right instanceof BinaryExpression b) {

            BinaryOperator operator = b.getBinaryOperator();

            if (operator == BinaryOperator.PLUS || operator == BinaryOperator.MINUS) {

                Register result = Register.getFreeRegister(Register.RAX);
                code.addInstruction(Instruction.MOV, result.name, dst.getRegisterAsString(true)).newLine();
                b.accept(this);
                code.addInstruction(Instruction.MOV, dst.getRegisterAsString(true), result.name).newLine();
            }
            else if (operator == BinaryOperator.TIMES) {

                right.accept(this);
                code.addInstruction(Instruction.MOV, dst.getRegisterAsString(true), Register.RAX.name).newLine();
            }
            else if (operator == BinaryOperator.DIV) {

                if (b.getRight() instanceof BinaryExpression bin) {

                    right.accept(this);
                    Register dividend = Register.getFreeRegister(Register.RBX);
                    code.addInstruction(Instruction.MOV, dividend.name, Register.RAX.name).newLine();
                    code.addInstruction(Instruction.MOV, Register.RAX.name).comma();
                    b.getLeft().accept(this);
                    code.newLine();
                    code.addInstruction(Instruction.XOR, Register.RDX.name, Register.RDX.name).newLine();
                    code.addInstruction(Instruction.DIV, dividend.name).newLine();
                    code.addInstruction(Instruction.MOV, dst.getRegisterAsString(true), Register.RAX.name).newLine();
                }
                else {

                    right.accept(this);
                    code.addInstruction(Instruction.MOV, dst.getRegisterAsString(true), Register.RAX.name).newLine();
                }
            }
            else if (operator == BinaryOperator.MOD) {

                code.comment("MODULO").newLine();
            }
        }
        else if (right instanceof CallExpression c) {

            c.accept(this);
            code.addInstruction(Instruction.MOV, dst.getRegisterAsString(true), Register.RAX.name).newLine();
        }

    }

    @Override
    public void visit(DeclarationStatement declarationStatement) {

        Declaration declaration = declarationStatement.getDeclaration();
        Type type = declaration.getType();

        if (symbolTable.isGlobalScope(currentScope)) {


            code.addData(declarationStatement.getDeclaration().getIdentifier(), type.dataAllocation(), type.size());
        }
        else {


            Symbol symbol = symbolTable.getSymbol(declaration.getIdentifier(), currentScope);
            if (symbol == null) {

                symbol = new Symbol(declaration.getIdentifier(), type);
                rbpOffset += type.size();
                symbol.setRegister(Register.RBP, rbpOffset);
                symbolTable.add(symbol, currentScope);
            }
            code.addInstruction(Instruction.MOV, symbol.getRegisterAsString(true), "0").newLine();
        }

        declarationStatement.visitChildren(this);
    }

    @Override
    public void visit(CallStatement callStatement) {



        callStatement.visitChildren(this);
    }

    @Override
    public void visit(Block block) {

        block.visitChildren(this);
    }

    @Override
    public void visit(UnaryExpression unaryExpression) {


        unaryExpression.visitChildren(this);
    }

    @Override
    public void visit(BinaryExpression binaryExpression) {

        BinaryOperator op = binaryExpression.getBinaryOperator();
        Expression left = binaryExpression.getLeft();
        Expression right = binaryExpression.getRight();

        if (op.isComparison()) {

            Register dst = null;


            if (left instanceof Constant && right instanceof Constant) {

                dst = Register.getFreeRegister();
                code.addInstruction(Instruction.MOV,  dst.name).comma();
                left.accept(this);
                code.newLine();
            }
            else if (left instanceof VariableAccess l && right instanceof VariableAccess r) {

                dst = Register.getFreeRegister();
                code.addInstruction(Instruction.MOV, dst.name).comma();
                l.accept(this);
                code.newLine();
            }
            else if (right instanceof Constant) {

                dst = Register.getFreeRegister();
                code.addInstruction(Instruction.MOV, dst.name).comma();
                left.accept(this);
                code.newLine();
            }
            else if (left instanceof Constant) {

                Expression t = left;
                left = right;
                right = t;
            }

            code.addInstruction(Instruction.CMP);
            if (dst != null) {

                code.add(dst.name);
            }
            else {

                left.accept(this);
            }
            code.comma();
            right.accept(this);
            code.newLine();

        }
        else {

            BinaryOperator operator = binaryExpression.getBinaryOperator();
            Instruction instruction = Instruction.binaryOperatorAsInstruction(operator);

            if (left instanceof BinaryExpression l && right instanceof BinaryExpression r) {

                l.accept(this);
                r.accept(this);
            }
            else if (left instanceof BinaryExpression b) {

                if (operator == BinaryOperator.PLUS || operator == BinaryOperator.MINUS) {


                    Register dst = null;

                    if (b.getBinaryOperator() == BinaryOperator.TIMES || b.getBinaryOperator() == BinaryOperator.DIV) {

                        dst = Register.RAX;
                        left.accept(this);
                        code.addInstruction(instruction, Register.RAX.name).comma();
                        right.accept(this);
                        code.newLine();
                    }
                    else {

                        dst = Register.getFreeRegister(Register.RAX);
                        dst.inUse = true;
                        code.addInstruction(Instruction.MOV, dst.name).comma();
                        right.accept(this);
                        code.newLine();
                        left.accept(this);
                        code.addInstruction(instruction, dst.name, Register.getFreeRegister(Register.RAX).name).newLine();
                        dst.inUse = false;
                    }

                }
                else if (operator == BinaryOperator.TIMES) {


                    left.accept(this);
                    code.addInstruction(Instruction.MUL);
                    right.accept(this);
                    code.newLine();
                }
                else if (operator == BinaryOperator.DIV) {

                    right.accept(this);
                    left.accept(this);
                }

            }
            else if (right instanceof BinaryExpression b) {

                b.accept(this);
            }
            else {

                if (operator == BinaryOperator.PLUS || operator == BinaryOperator.MINUS) {

                    Register dst = Register.getFreeRegister(Register.RAX);
                    dst.inUse = true;

                    code.addInstruction(Instruction.MOV, dst.name).comma();
                    left.accept(this);
                    code.newLine();

                    code.addInstruction(instruction, dst.name).comma();
                    right.accept(this);
                    code.newLine();

                    dst.inUse = false;
                }
                else if (operator == BinaryOperator.TIMES) {

                    Register dst = Register.RAX;
                    Register rhs = Register.getFreeRegister();
                    code.addInstruction(Instruction.MOV, dst.name).comma();
                    left.accept(this);
                    code.newLine().addInstruction(Instruction.MOV, rhs.name).comma();
                    right.accept(this);
                    code.newLine().addInstruction(Instruction.MUL, rhs.name);
                    code.newLine();
                }
                else if (operator == BinaryOperator.DIV) {

                    boolean raxSwap = Register.RAX.inUse;
                    boolean rdxSwap = Register.RDX.inUse;
                    Register raxReg = null;
                    Register rdxReg = null;

                    if (raxSwap) {

                        raxReg = Register.getFreeRegister();
                        code.addInstruction(Instruction.MOV, raxReg.name, Register.RAX.name).newLine();
                    }
                    if (rdxSwap) {

                        rdxReg = Register.getFreeRegister();
                        code.addInstruction(Instruction.MOV, rdxReg.name, Register.RDX.name).newLine();
                    }

                    Register rhs = Register.getFreeRegister();
                    code.addInstruction(Instruction.MOV, Register.RAX.name).comma();
                    left.accept(this);
                    code.newLine().addInstruction(Instruction.MOV, rhs.name).comma();
                    right.accept(this);
                    code.newLine().addInstruction(Instruction.XOR, Register.RDX.name, Register.RDX.name).newLine();
                    code.addInstruction(Instruction.DIV, rhs.name).newLine();

                    if (raxSwap) {

                        code.addInstruction(Instruction.MOV).comma();
                        left.accept(this);
                        code.comma().add(Register.RAX.name).newLine();
                        code.addInstruction(Instruction.MOV, Register.RAX.name, raxReg.name);
                    }

                    if (rdxSwap) {

                        code.addInstruction(Instruction.MOV, Register.RDX.name, rdxReg.name);
                    }
                }
                else if (operator == BinaryOperator.MOD) {

                    boolean raxSwap = Register.RAX.inUse;
                    boolean rdxSwap = Register.RDX.inUse;
                    Register raxReg = null;
                    Register rdxReg = null;

                    if (raxSwap) {

                        raxReg = Register.getFreeRegister();
                        code.addInstruction(Instruction.MOV, raxReg.name, Register.RAX.name).newLine();
                    }
                    if (rdxSwap) {

                        rdxReg = Register.getFreeRegister();
                        code.addInstruction(Instruction.MOV, rdxReg.name, Register.RDX.name).newLine();
                    }

                    Register rhs = Register.getFreeRegister();
                    code.addInstruction(Instruction.MOV, Register.RAX.name).comma();
                    left.accept(this);
                    code.newLine().addInstruction(Instruction.MOV, rhs.name).comma();
                    right.accept(this);
                    code.newLine().addInstruction(Instruction.XOR, Register.RDX.name, Register.RDX.name).newLine();
                    code.addInstruction(Instruction.DIV, rhs.name).newLine();

                    if (raxSwap) {

                        code.addInstruction(Instruction.MOV, Register.RAX.name, raxReg.name);
                    }

                    if (rdxSwap) {

                        code.addInstruction(Instruction.MOV);
                        left.accept(this);
                        code.comma().add(Register.RDX.name).newLine();
                        code.addInstruction(Instruction.MOV, Register.RDX.name, rdxReg.name);
                    }
                }
            }
        }
       
    }

    @Override
    public void visit(CallExpression callExpression) {

        List<Expression> params = callExpression.getParameters();

        for (int i = params.size() - 1; i >= 0; i--) {

            Expression parameter = params.get(i);
            if (parameter instanceof BinaryExpression b) {

                Register result = Register.getFreeRegister(Register.RAX);
                b.accept(this);
                code.addInstruction(Instruction.MOV, Register.RDI.name, result.name).newLine();
            }
            else if (parameter instanceof CallExpression c) {


                c.accept(this);
                code.addInstruction(Instruction.MOV, Register.values()[i].name, Register.RAX.name).newLine();
            }
            else {
                if (i < MAX_PASS_REGISTER) {

                    code.addInstruction(Instruction.MOV, Register.values()[i].name).comma();
                } else {

                    code.addInstruction(Instruction.PUSH);
                    stackOffset -= Expression.evaluateType(parameter, symbolTable).size();
                }

                parameter.accept(this);
                code.newLine();
            }
        }


        code.addInstruction(Instruction.CALL, callExpression.getIdentifier()).newLine();
    }

    @Override
    public void visit(VariableAccess variable) {

        Symbol symbol = symbolTable.getSymbol(variable.getIdentifier(), symbolTable.getScope(variable));
        code.add(symbol.getRegisterAsString(true));
    }

    @Override
    public void visit(ArrayAccess arrayAccess) {


    }

    @Override
    public void visit(FieldAccess fieldAccess) {


    }

    @Override
    public void visit(FalseConstant falseConstant) {

        code.add("0");
    }

    @Override
    public void visit(IntegerConstant integerConstant) {


        code.add(Long.toString(integerConstant.getValue()));
    }

    @Override
    public void visit(StringConstant stringConstant) {


    }

    @Override
    public void visit(TrueConstant trueConstant) {

        code.add("1");
    }

    public String getCode() {

        return code.toString();
    }
}
