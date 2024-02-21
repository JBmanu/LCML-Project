package test;

import compiler.*;
import compiler.exc.IncomplException;
import compiler.exc.TypeException;
import compiler.lib.FOOLlib;
import compiler.lib.Node;
import compiler.lib.TypeNode;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;
import svm.ExecuteVM;
import svm.SVMLexer;
import svm.SVMParser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OperatorsTest {
    private static final String ROOT_OPERATORS_TEST_FILES = "res/test/operators/";

    // FOOL
    private FOOLLexer lexer;
    private CommonTokenStream tokens;
    private FOOLParser parser;
    private ParseTree st;
    private final ASTGenerationSTVisitor visitor = new ASTGenerationSTVisitor();
    private final SymbolTableASTVisitor symtableVisitor = new SymbolTableASTVisitor();
    private final TypeCheckEASTVisitor typeCheckVisitor = new TypeCheckEASTVisitor();

    // ASM
    private SVMLexer lexerASM;
    private CommonTokenStream tokensASM;
    private SVMParser parserASM;


    private CharStream getCharStreams(String fileName) {
        try {
            return CharStreams.fromFileName(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void generateST(FOOLParser parser, FOOLLexer lexer) {
        System.out.println("Generating ST via lexer and parser.");
        this.st = parser.prog();
        System.out.println("You had " + lexer.lexicalErrors + " lexical errors and " +
                parser.getNumberOfSyntaxErrors() + " syntax errors.\n");
    }

    private Node generateASTAndGetRoot(FOOLParser parser, FOOLLexer lexer) {
        System.out.println("Generating AST.");
        Node ast = this.visitor.visit(this.st);
        System.out.println();
        return ast;
    }

    private void enrichASTSymbolTable(Node ast) {
        System.out.println("Enriching AST via symbol table.");
        this.symtableVisitor.visit(ast);
        System.out.println("You had " + this.symtableVisitor.stErrors + " symbol table errors.\n");

        System.out.println("Visualizing Enriched AST.");
        new PrintEASTVisitor().visit(ast);
        System.out.println();
    }

    private void checkingTypes(Node ast) {
        System.out.println("Checking Types.");
        try {
            TypeNode mainType = this.typeCheckVisitor.visit(ast);
            System.out.print("Type of main program expression is: ");
            new PrintEASTVisitor().visit(mainType);
        } catch (IncomplException e) {
            System.out.println("Could not determine main program expression type due to errors detected before type checking.");
        } catch (TypeException e) {
            System.out.println("Type checking error in main program expression: " + e.text);
        }
        System.out.println("You had " + FOOLlib.typeErrors + " type checking errors.\n");
    }

    private int getFrontEndErrors() {
        int frontEndErrors = this.lexer.lexicalErrors + this.parser.getNumberOfSyntaxErrors() + this.symtableVisitor.stErrors + FOOLlib.typeErrors;
        System.out.println("You had a total of " + frontEndErrors + " front-end errors.\n");
        return frontEndErrors;
    }

    private void generateCode(Node ast, String fileName) {
        System.out.println("Generating code.");
        String code = new CodeGenerationASTVisitor().visit(ast);
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName + ".asm"));
            out.write(code);
            out.close();
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void assemblingGeneratedCode(String fileName) {
        System.out.println("Assembling generated code.");
        this.lexerASM = new SVMLexer(this.getCharStreams(fileName + ".asm"));
        this.tokensASM = new CommonTokenStream(this.lexerASM);
        this.parserASM = new SVMParser(this.tokensASM);

        this.parserASM.assembly();
    }

    private int getErrorLexerAsm() {
        System.out.println("You had: " + this.lexerASM.lexicalErrors + " lexical errors and " + this.parserASM.getNumberOfSyntaxErrors() + " syntax errors.\n");
        return this.lexerASM.lexicalErrors + this.parserASM.getNumberOfSyntaxErrors();
    }

    private void runningSVM() {
        System.out.println("Running generated code via Stack Virtual Machine.");
        ExecuteVM vm = new ExecuteVM(this.parserASM.code);
        vm.cpu();
    }

    private void buildASTAndSVMAndCheckErrors(String fileName) {
        this.lexer = new FOOLLexer(this.getCharStreams(fileName));
        this.tokens = new CommonTokenStream(this.lexer);
        this.parser = new FOOLParser(this.tokens);

        this.generateST(this.parser, this.lexer);

        Node ast = this.generateASTAndGetRoot(this.parser, this.lexer);
        this.enrichASTSymbolTable(ast);
        this.checkingTypes(ast);

        int frontEndErrors = this.getFrontEndErrors();
        assertEquals(0, frontEndErrors);

        this.generateCode(ast, fileName);
        this.assemblingGeneratedCode(fileName);
        assertEquals(0, this.getErrorLexerAsm());
    }

    @Test
    public void testOperators() {
        String fileName = ROOT_OPERATORS_TEST_FILES + "simple.fool";
        this.buildASTAndSVMAndCheckErrors(fileName);
        this.runningSVM();
    }


}