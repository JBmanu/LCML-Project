package compiler;

import java.io.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import compiler.lib.*;
import compiler.exc.*;
import svm.*;

public class Test {
	static FOOLParser parser;
	static FOOLLexer lexer;
	static ParseTree st;
	static Node ast;
	static SymbolTableASTVisitor symtableVisitor;
	static SVMLexer lexerASM;
	static SVMParser parserASM;
	static final String FILENAME = "res/prova.fool";

    public static void main(String[] args) throws Exception {

		setFoolToCompile();
		generateLexerAndCheckErrors();
		generateAST();
		viewAST();
		checkTypes();
		ifErrorsFoundedExit();

		generateCode();
		assembleASM();
    	debug();
		executeVMAndCompute();
    }

	static void setFoolToCompile() throws IOException {
		CharStream chars = CharStreams.fromFileName(FILENAME);
		FOOLLexer lexer = new FOOLLexer(chars);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		FOOLParser parser = new FOOLParser(tokens);
	}

	static void generateLexerAndCheckErrors(){
		System.out.println("Generating ST via lexer and parser.");
		st = parser.prog();
		System.out.println("You had "+lexer.lexicalErrors+" lexical errors and "+
				parser.getNumberOfSyntaxErrors()+" syntax errors.\n");
	}

	static void generateAST(){
		System.out.println("Generating AST.");
		ASTGenerationSTVisitor visitor = new ASTGenerationSTVisitor(); // use true to visualize the ST
		ast = visitor.visit(st);
		System.out.println("");

		System.out.println("Enriching AST via symbol table.");
		symtableVisitor = new SymbolTableASTVisitor();
		symtableVisitor.visit(ast);
		System.out.println("You had "+symtableVisitor.stErrors+" symbol table errors.\n");
	}

	static void viewAST(){
		System.out.println("Visualizing Enriched AST.");
		new PrintEASTVisitor().visit(ast);
		System.out.println("");
	}

	static void checkTypes(){
		System.out.println("Checking Types.");
		try {
			TypeCheckEASTVisitor typeCheckVisitor = new TypeCheckEASTVisitor();
			TypeNode mainType = typeCheckVisitor.visit(ast);
			System.out.print("Type of main program expression is: ");
			new PrintEASTVisitor().visit(mainType);
		} catch (IncomplException e) {
			System.out.println("Could not determine main program expression type due to errors detected before type checking.");
		} catch (TypeException e) {
			System.out.println("Type checking error in main program expression: "+e.text);
		}
		System.out.println("You had "+FOOLlib.typeErrors+" type checking errors.\n");
	}

	static void ifErrorsFoundedExit(){
		int frontEndErrors = lexer.lexicalErrors+parser.getNumberOfSyntaxErrors()+symtableVisitor.stErrors+FOOLlib.typeErrors;
		System.out.println("You had a total of "+frontEndErrors+" front-end errors.\n");

		if ( frontEndErrors > 0) System.exit(1);
	}

	static void generateCode() throws IOException {
		System.out.println("Generating code.");
		String code = new CodeGenerationASTVisitor().visit(ast);
		BufferedWriter out = new BufferedWriter(new FileWriter(FILENAME+".asm"));
		out.write(code);
		out.close();
		System.out.println("");
	}

	static void assembleASM() throws IOException {
		System.out.println("Assembling generated code.");
		CharStream charsASM = CharStreams.fromFileName(FILENAME+".asm");
		lexerASM = new SVMLexer(charsASM);
		CommonTokenStream tokensASM = new CommonTokenStream(lexerASM);
		parserASM = new SVMParser(tokensASM);

		parserASM.assembly();
	}

	static void debug(){
		System.out.println("You had: "+lexerASM.lexicalErrors+" lexical errors and "+parserASM.getNumberOfSyntaxErrors()+" syntax errors.\n");
		if (lexerASM.lexicalErrors+parserASM.getNumberOfSyntaxErrors()>0) System.exit(1);
	}

	static void executeVMAndCompute(){
		System.out.println("Running generated code via Stack Virtual Machine.");
		ExecuteVM vm = new ExecuteVM(parserASM.code);
		vm.cpu();
	}
}

