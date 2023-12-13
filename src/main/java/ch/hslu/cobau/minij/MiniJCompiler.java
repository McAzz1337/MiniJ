package ch.hslu.cobau.minij;

import ch.hslu.cobau.minij.ast.AstBuilder;
import ch.hslu.cobau.minij.ast.entity.Unit;
import org.antlr.v4.runtime.*;

import java.io.IOException;
import java.text.Format;
import java.util.List;

public class MiniJCompiler {

    private static class EnhancedConsoleErrorListener extends ConsoleErrorListener {
        private boolean hasErrors;

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
            hasErrors = true;
        }

        public boolean hasErrors() {
            return hasErrors;
        }
    }

    public static void main(String[] args) throws IOException {    
        // initialize lexer and parser
        CharStream charStream;
        if (args.length > 0) {
            charStream = CharStreams.fromFileName(args[0]);
        } else {
            charStream = CharStreams.fromStream(System.in);
        }

        MiniJLexer miniJLexer = new MiniJLexer(charStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(miniJLexer);
        MiniJParser miniJParser = new MiniJParser(commonTokenStream);

        EnhancedConsoleErrorListener errorListener = new EnhancedConsoleErrorListener();
        miniJParser.removeErrorListeners();
        miniJParser.addErrorListener(errorListener);

        // start parsing at outermost level (milestone 2)
        MiniJParser.UnitContext unitContext = miniJParser.unit();
        if (errorListener.hasErrors()) {

            System.exit(1);
        }

        //printUnitContext(unitContext);
        // semantic check (milestone 3)

        AstBuilder astBuilder = new AstBuilder();
        unitContext.accept(astBuilder);

        Unit u = astBuilder.getUnit();

        FirstPassVisitor v = new FirstPassVisitor();
        u.accept(v);

        SecondPassVisitor v2 = new SecondPassVisitor(v.getSymbolTable());
        u.accept(v2);

        // code generation (milestone 4)

        CodeGenerator cg = new CodeGenerator(v.getSymbolTable());
        u.accept(cg);
        System.out.println(cg.getCode());

    }

    public static void printErrorAndExit(String errorMessage) {

        System.out.println(errorMessage);
        System.exit(1);
    }
}
