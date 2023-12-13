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
import java.util.List;


public class FirstPassVisitor implements AstVisitor {

    private SymbolTable symbolTable = new SymbolTable();
    private Scope currentScope;
    public FirstPassVisitor() {

    }

    private Function makeFunction(Type ret,
                              String identifier,
                              Type[] argTypes,
                              String[] argName,
                              List<Statement> statements) {

        List<Declaration> declarations = new ArrayList<>();
        for (int i = 0; i < argTypes.length; i++) {
            declarations.add(new Declaration(argName[i], argTypes[i], false));
        }

        return new Function(identifier, ret, declarations, statements);
    }
    private void insertBuiltinFunctions(Unit program) {

        List<Function> functions = program.getFunctions();

        functions.add(makeFunction(
                new VoidType(),
                "writeInt",
                new Type[] {new IntegerType()},
                new String[]{"x"},
                new ArrayList<Statement>()));

        functions.add(makeFunction(
                new VoidType(),
                "writeChar",
                new Type[] {new IntegerType()},
                new String[]{"x"},
                new ArrayList<>()
        ));


        Statement ret = new ReturnStatement(new IntegerConstant(1337));
        List<Statement> statements = new ArrayList<>();
        statements.add(ret);
        functions.add(makeFunction(
                new IntegerType(),
                "readInt",
                new Type[]{},
                new String[]{},
                statements
        ));


        ret = new ReturnStatement(new StringConstant("1337"));
        statements = new ArrayList<>();
        statements.add(ret);
        functions.add(makeFunction(
                new StringType(),
                "readChar",
                new Type[]{},
                new String[]{},
                statements
        ));

        ret = new ReturnStatement(new IntegerConstant(1337));
        statements = new ArrayList<>();
        statements.add(ret);
        functions.add(makeFunction(
                new IntegerType(),
                "readChar",
                new Type[]{},
                new String[]{},
                statements
        ));


    }

    @Override
    public void visit(Unit program) {

        currentScope = symbolTable.getGlobalScope();
        insertBuiltinFunctions(program);
        program.visitChildren(this);
    }

    @Override
    public void visit(Function function) {


        if (function.getIdentifier().equals("main")) {
            if (!function.getReturnType().equals(new IntegerType())) {

                MiniJCompiler.printErrorAndExit("Main function must have a return type of int");
            }
            else if (function.getFormalParameters().size() != 0) {

                MiniJCompiler.printErrorAndExit("Main function cannot take any arguments");
            }
        }
        else if (!function.getReturnType().equals(new VoidType())) {
            List<Statement> statements = function.getStatements();
            boolean hasReturnStatement = false;
            OUTER: for (Statement s : statements) {

                if (s instanceof ReturnStatement) {

                    hasReturnStatement = true;
                    break;
                }
                else if (s instanceof IfStatement i) {

                    for (Statement st : i.getStatements()) {
                        if (st instanceof ReturnStatement) {

                            hasReturnStatement = true;
                            break OUTER;
                        }
                    }
                    Block elseBlock = i.getElseBlock();
                    for (Statement st : elseBlock.getStatements()) {
                        if (st instanceof ReturnStatement) {

                            hasReturnStatement = true;
                            break OUTER;
                        }
                    }
                }
            }

            if (!hasReturnStatement) {

                MiniJCompiler.printErrorAndExit("Missing return statement in function " + function.getIdentifier());
            }
        }

        List<Function> functions = symbolTable.getAllFunctionsByName(function.getIdentifier());
        for (Function f : functions) {

            if (function.equals(f)) {

                MiniJCompiler.printErrorAndExit("Redefinition of function: " + function.toString() + "\n" + f.toString());

            }
        }



        currentScope.addSymbol(function.getIdentifier());
        currentScope = symbolTable.addScope(currentScope, function);
        symbolTable.add(function, currentScope);
        function.visitChildren(this);


        currentScope = currentScope.getParent();
    }

    @Override
    public void visit(Declaration declaration) {

        if (currentScope.hasSymbol(declaration.getIdentifier())) {

            MiniJCompiler.printErrorAndExit("Redefinition of variable: " + declaration.getIdentifier());
        }
        currentScope.addSymbol(declaration.getIdentifier());
        symbolTable.add(declaration, currentScope);
        declaration.visitChildren(this);
    }

    @Override
    public void visit(Struct record) {

        if (currentScope.hasSymbol(record.getIdentifier())) {

            MiniJCompiler.printErrorAndExit("Redefinition of record: " + record.getIdentifier());
        }

        currentScope.addSymbol(record.getIdentifier());
        currentScope = symbolTable.addScope(currentScope, record);
        symbolTable.add(record, currentScope);

        record.visitChildren(this);
        currentScope = currentScope.getParent();
    }

    @Override
    public void visit(IfStatement ifStatement) {

        ifStatement.visitChildren(this);
    }

    @Override
    public void visit(WhileStatement whileStatement) {

        whileStatement.visitChildren(this);
    }

    @Override
    public void visit(ReturnStatement returnStatement) {

        symbolTable.add(returnStatement, currentScope);
        returnStatement.visitChildren(this);
    }

    @Override
    public void visit(AssignmentStatement assignment) {


        symbolTable.add(assignment, currentScope);
        assignment.visitChildren(this);
    }

    @Override
    public void visit(DeclarationStatement declarationStatement) {

        symbolTable.add(declarationStatement, currentScope);
        declarationStatement.visitChildren(this);
    }

    @Override
    public void visit(CallStatement callStatement) {

        symbolTable.add(callStatement, currentScope);
        callStatement.visitChildren(this);
    }

    @Override
    public void visit(Block block) {

        block.visitChildren(this);
    }

    @Override
    public void visit(UnaryExpression unaryExpression) {

        symbolTable.add(unaryExpression, currentScope);
        unaryExpression.visitChildren(this);
    }

    @Override
    public void visit(BinaryExpression binaryExpression) {



        symbolTable.add(binaryExpression, currentScope);
        binaryExpression.visitChildren(this);
    }

    @Override
    public void visit(CallExpression callExpression) {

        symbolTable.add(callExpression, currentScope);
        callExpression.visitChildren(this);
    }

    @Override
    public void visit(VariableAccess variable) {

        if (!currentScope.hasSymbolInSelfOrParent(variable.getIdentifier())) {

            MiniJCompiler.printErrorAndExit("Accessing undefined variable: " + variable.getIdentifier());
        }
        else if (symbolTable.getDeclaration(variable.getIdentifier()) == null) {

            MiniJCompiler.printErrorAndExit("Accessing undefined variable: " + variable.getIdentifier());
        }

        symbolTable.add(variable, currentScope);
        variable.visitChildren(this);
    }

    @Override
    public void visit(ArrayAccess arrayAccess) {

        symbolTable.add(arrayAccess, currentScope);
        arrayAccess.visitChildren(this);
    }

    @Override
    public void visit(FieldAccess fieldAccess) {

        symbolTable.add(fieldAccess, currentScope);
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

    public SymbolTable getSymbolTable() {

        return symbolTable;
    }
}
