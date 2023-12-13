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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SecondPassVisitor  implements AstVisitor {

    private SymbolTable symbolTable;
    private Scope currentScope;

    public SecondPassVisitor(SymbolTable symbolTable) {

        this.symbolTable = symbolTable;
    }

    private void removeBuiltInFunctions(Unit program) {

        List<Function> functions = program.getFunctions();

        Iterator<Function> it = functions.iterator();

        for (;it.hasNext();) {

            String identifier = it.next().getIdentifier();
            if (identifier == "writeInt" ||
                    identifier == "writeChar" ||
                    identifier == "readInt" ||
                    identifier == "readChar") {

                it.remove();
            }
        }


    }

    @Override
    public void visit(Unit program) {

        currentScope = symbolTable.getGlobalScope();
        program.visitChildren(this);

        removeBuiltInFunctions(program);
    }

    @Override
    public void visit(Function function) {


        function.visitChildren(this);
    }

    @Override
    public void visit(Declaration declaration) {

        if (declaration.getType() instanceof RecordType t) {

            if (!symbolTable.getGlobalScope().hasSymbol(t.getIdentifier())) {

                MiniJCompiler.printErrorAndExit("Undefined record " + t.getIdentifier());
            }
        }

        declaration.visitChildren(this);
    }

    @Override
    public void visit(Struct record) {

        currentScope.addSymbol(record.getIdentifier());
        currentScope = symbolTable.addScope(currentScope, record);
        record.visitChildren(this);
        currentScope = currentScope.getParent();
    }

    @Override
    public void visit(IfStatement ifStatement) {

        if (!Expression.evaluateType(ifStatement.getExpression(), symbolTable).equals(new BooleanType())) {

            MiniJCompiler.printErrorAndExit("Boolean expression expected in if statement");
        }

        ifStatement.visitChildren(this);
    }

    @Override
    public void visit(WhileStatement whileStatement) {

        if (!Expression.evaluateType(whileStatement.getExpression(), symbolTable).equals(new BooleanType())) {

            MiniJCompiler.printErrorAndExit("Boolean expression expected in while statement");
        }

        whileStatement.visitChildren(this);
    }

    @Override
    public void visit(ReturnStatement returnStatement) {

        Function function = symbolTable.getFunctionOfScope(symbolTable.getScope(returnStatement));
        Expression expression = returnStatement.getExpression();



        Type returnType = function.getReturnType();

        boolean correctType = Expression.evaluateType(expression, symbolTable).equals(returnType);

        if (!correctType) {

            MiniJCompiler.printErrorAndExit("Returned value does not match return type of function: " + function.getIdentifier());
        }
        returnStatement.visitChildren(this);
    }

    @Override
    public void visit(AssignmentStatement assignment) {

        Type left = Expression.evaluateType(assignment.getLeft(), symbolTable);
        Expression.typeToLookFor = left;
        Type right = Expression.evaluateType(assignment.getRight(), symbolTable);


        if (!left.equals(right)) {

            MiniJCompiler.printErrorAndExit("Type mismatch in assignment: " +
                    left.toString() + " = " + right.toString());
        }

        assignment.visitChildren(this);
    }

    @Override
    public void visit(DeclarationStatement declarationStatement) {

        declarationStatement.visitChildren(this);
    }

    @Override
    public void visit(CallStatement callStatement) {


        CallExpression call = callStatement.getCallExpression();
        List<Expression> params = call.getParameters();

        List<Function> functions =  symbolTable.getAllFunctionsByName(call.getIdentifier());


        boolean found = false;
        for (Function f : functions) {

            if (!call.getIdentifier().equals(f.getIdentifier()))
                continue;
            if (!f.matchesParameters(params, symbolTable))
                continue;

            List<Declaration> args = f.getFormalParameters();
            if (args.size() != params.size())
                continue;

            for (int i = 0; i < params.size(); i++) {

                Expression param = params.get(i);
                Declaration arg = args.get(i);
                boolean correctType = arg.getType().equals(Expression.evaluateType(param, symbolTable));

                if (!correctType) {

                    MiniJCompiler.printErrorAndExit("Type mismatch in function call " + f.toString());
                }

            }
            found = true;
        }

        if (!found) {

            MiniJCompiler.printErrorAndExit("Unresolved call to function: " + call.getIdentifier());
        }

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


        binaryExpression.visitChildren(this);
    }

    @Override
    public void visit(CallExpression callExpression) {

        callExpression.visitChildren(this);
    }

    @Override
    public void visit(VariableAccess variable) {


        Scope scope = symbolTable.getScope(variable);
        if (scope == null || !scope.hasSymbolInSelfOrParent(variable.getIdentifier())) {

            MiniJCompiler.printErrorAndExit("Accessing undefined variable: " + variable.getIdentifier());
        }

        variable.visitChildren(this);
    }

    @Override
    public void visit(ArrayAccess arrayAccess) {

        arrayAccess.visitChildren(this);
    }

    @Override
    public void visit(FieldAccess fieldAccess) {

        String identifier = fieldAccess.getField();
        VariableAccess variable = (VariableAccess) fieldAccess.getBase();
        Declaration d = symbolTable.getDeclaration(variable.getIdentifier());

        Struct s = symbolTable.getStruct(((RecordType) d.getType()).getIdentifier());

        List<Declaration> fields = s.getDeclarations();
        boolean found = false;
        for (Declaration dec : fields) {

            if (dec.getIdentifier().equals(identifier)) {

                found = true;
                break;
            }
        }

        if (!found) {

            MiniJCompiler.printErrorAndExit("No field " + identifier + " on record " + s.getIdentifier());
        }

        fieldAccess.visitChildren(this);
    }

    @Override
    public void visit(FalseConstant falseConstant) {

        falseConstant.visitChildren(this);
    }

    @Override
    public void visit(IntegerConstant integerConstant) {

        integerConstant.visitChildren(this);
    }

    @Override
    public void visit(StringConstant stringConstant) {

        stringConstant.visitChildren(this);
    }

    @Override
    public void visit(TrueConstant trueConstant) {

        trueConstant.visitChildren(this);
    }
}
