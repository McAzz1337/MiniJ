package ch.hslu.cobau.minij;

import ch.hslu.cobau.minij.ast.AstElement;
import ch.hslu.cobau.minij.ast.entity.Declaration;
import ch.hslu.cobau.minij.ast.entity.Function;
import ch.hslu.cobau.minij.ast.entity.Struct;
import ch.hslu.cobau.minij.ast.expression.*;
import ch.hslu.cobau.minij.ast.statement.AssignmentStatement;
import ch.hslu.cobau.minij.ast.statement.CallStatement;
import ch.hslu.cobau.minij.ast.statement.DeclarationStatement;
import ch.hslu.cobau.minij.ast.statement.ReturnStatement;
import ch.hslu.cobau.minij.ast.type.RecordType;
import com.ibm.icu.text.UnicodeSet;
import org.antlr.v4.codegen.model.decl.Decl;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class SymbolTable {

    public static boolean symbolTrace = false;

    private Scope globalScope =  new Scope();
    private Map<Declaration, Scope> declarations = new HashMap<>();
    private Map<Function, Scope> functions = new HashMap<>();
    private Map<Struct, Scope> structs = new HashMap<>();
    private Map<FieldAccess, Scope> fieldAccesses = new HashMap<>();
    private Map<VariableAccess, Scope> variableAccesses = new HashMap<>();
    private Map<ArrayAccess, Scope> arrayAccesses = new HashMap<>();
    private Map<CallExpression, Scope> callExpressions = new HashMap<>();
    private Map<BinaryExpression, Scope> binaryExpressions = new HashMap<>();
    private Map<UnaryExpression, Scope> unaryExpressions = new HashMap<>();
    private Map<CallStatement, Scope> callStatements = new HashMap<>();
    private Map<DeclarationStatement, Scope> declarationStatements = new HashMap<>();
    private Map<AssignmentStatement, Scope> assignmentStatements = new HashMap<>();
    private Map<ReturnStatement, Scope> returnStatements = new HashMap<>();

    private Map<Symbol, Scope> symbols = new HashMap<>();


    public SymbolTable() {

    }


    public Scope getScope(Declaration d) {

        return declarations.get(d);
    }

    public Declaration getDeclaration(String identifier, Scope scope) {

        Declaration[] decls = new Declaration[declarations.size()];
        declarations.keySet().toArray(decls);
        Scope[] scopes = new Scope[declarations.size()];
        declarations.values().toArray(scopes);
        for (int i = 0; i < declarations.size(); i++) {

            Declaration decl = decls[i];
            Scope s = scopes[i];
            if (Objects.equals(decl.getIdentifier(), identifier)) {

                return decl;
            }
        }

        return null;
    }

    public Scope getScope(Function f) {

        return functions.get(f);
    }

    public Scope getScope(Struct s) {

        return structs.get(s);
    }


    public Scope getScope(VariableAccess v) {

        return variableAccesses.get(v);
    }

    public Struct getStruct(String identifier) {

        Set<Struct> set = structs.keySet();
        for (Struct s : set) {
            if (s.getIdentifier().equals(identifier))
                return s;
        }

        return null;
    }

    public Declaration getDeclaration(String identifier) {

        Set<Declaration> set = declarations.keySet();

        for (Declaration d : set) {
            if (d.getIdentifier().equals(identifier))
                return d;
        }

        return null;
    }

    public List<Declaration> getDeclarationsInScope(Scope scope) {

        Scope[] scopes = new Scope[declarations.size()];
        declarations.values().toArray(scopes);
        Declaration[] ds = new Declaration[declarations.size()];
        declarations.keySet().toArray(ds);
        List<Declaration> declarations = new ArrayList<>();
        for (int i = 0; i < this.declarations.size(); i++) {

            if (scopes[i] == scope) {

                declarations.add(ds[i]);
            }
        }

        return declarations;
    }


    public void add(Symbol s, Scope sc) {

        if (symbolTrace) {
            if (symbols.containsKey(s) && symbols.get(s).equals(sc)) {
                System.out.println("Dupe of " + s.getIdentifier() + " in " + sc);
            }
            System.out.println("added " + s.getIdentifier() + " with scope " + sc);
        }
        if (sc == null)
            try {
                throw new Exception();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        symbols.put(s, sc);
    }

    public void add(Declaration d, Scope s) {

        declarations.put(d, s);
    }


    public void add(Function f, Scope s) {

        functions.put(f, s);
    }

    public void add(Struct st, Scope s) {

        structs.put(st, s);
    }


    public void add(FieldAccess f, Scope s) {

        fieldAccesses.put(f, s);
    }

    public void add(VariableAccess v, Scope s) {

        variableAccesses.put(v, s);
    }

    public void add(ArrayAccess a, Scope s) {

        arrayAccesses.put(a, s);
    }

    public void add(CallExpression c, Scope s) {


        callExpressions.put(c, s);
    }

    public void add(BinaryExpression b, Scope s) {

        binaryExpressions.put(b, s);
    }

    public void add(UnaryExpression u, Scope s) {

        unaryExpressions.put(u, s);
    }

    public void add(CallStatement c, Scope s) {

        callStatements.put(c, s);
    }

    public void add(DeclarationStatement d, Scope s) {

        declarationStatements.put(d, s);
    }

    public void add(AssignmentStatement a, Scope s) {

        assignmentStatements.put(a, s);
    }

    public void add(ReturnStatement r, Scope s) {

        returnStatements.put(r, s);
    }


    public Scope getScope(FieldAccess f) {

        return fieldAccesses.get(f);
    }

    public Scope getScope(ReturnStatement r) {

        return returnStatements.get(r);
    }

    public Scope getScope(CallExpression c) {

        return callExpressions.get(c);
    }


    public Scope getGlobalScope() {

        return globalScope;
    }

    public Scope addScope(Scope parent) {

        Scope scope = new Scope(parent);
        return scope;
    }

    public Scope addScope(Scope parent, Function f) {

        Scope scope = new Scope(parent);
        scope.addSymbol(f.getIdentifier());

        return scope;
    }

    public Scope addScope(Scope parent, Struct s) {

        Scope scope = new Scope(parent);
        scope.addSymbol(s.getIdentifier());

        return scope;
    }

    public Function getFunctionOfScope(Scope scope) {

        Scope[] scopes = new Scope[functions.values().size()];
        Function[] fs = new Function[functions.keySet().size()];
        functions.values().toArray(scopes);
        functions.keySet().toArray(fs);
        for (int i = 0; i < scopes.length; i++) {
            if (scopes[i].equals(scope)) {

                return fs[i];
            }
        }

        return null;
    }

    public List<Function> getAllFunctionsInScope(Scope scope) {

        List<Function> fs = new ArrayList<>();
        functions.forEach(new BiConsumer<Function, Scope>() {
            @Override
            public void accept(Function function, Scope s) {

                if (scope.equals(s)) {

                    fs.add(function);
                }
            }
        });

        return fs;
    }

    public List<Function> getAllFunctionsByName(String name) {

        List<Function> fs = new ArrayList<>();
        functions.forEach(new BiConsumer<Function, Scope>() {
            @Override
            public void accept(Function function, Scope scope) {

                if (Objects.equals(function.getIdentifier(), name)) {

                    fs.add(function);
                }
            }
        });

        return fs;
    }


    public DeclarationStatement getDeclarationStatement(Declaration d) {

        DeclarationStatement[] dsa = new DeclarationStatement[declarationStatements.size()];
        declarationStatements.keySet().toArray(dsa);

        for (DeclarationStatement ds : dsa) {

            if (ds.getDeclaration() == d) {

                return ds;
            }
        }

        return null;
    }

    public Symbol getSymbol(String identifier, Scope scope) {

        if (symbolTrace)
            System.out.println("looking for " + identifier + " in scope "  + scope);

        for (Symbol s : symbols.keySet()) {



            if (Objects.equals(s.getIdentifier(), identifier)) {

                if (symbolTrace)
                    System.out.println("found " + identifier + " in " + s);

                return s;
            }
        }

        if (scope.getParent() != null) {

            return getSymbol(identifier, scope.getParent());
        }

        return null;
    }

    public void clear(Scope scope) {


        var it = symbols.keySet().removeIf(new Predicate<Symbol>() {
            @Override
            public boolean test(Symbol symbol) {

                Scope currentScope = symbols.get(symbol);

                while (currentScope != scope.getParent() && currentScope != null) {

                    if (currentScope == scope) {

                        return true;
                    }

                    currentScope = currentScope.getParent();
                }

                return false;
            }
        });
    }

    public boolean isGlobalScope(Scope scope) {

        return globalScope.equals(scope);
    }

}
