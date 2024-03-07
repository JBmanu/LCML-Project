package compiler;

import compiler.AST.*;
import compiler.lib.*;
import compiler.exc.*;
import compiler.lib.Node;

import java.util.ArrayList;
import java.util.List;

import static compiler.lib.FOOLlib.*;
import static svm.ExecuteVM.MEMSIZE;

public class CodeGenerationASTVisitor extends BaseASTVisitor<String, VoidException> {
  private final List<List<String>> dispatchTables = new ArrayList<>();

  CodeGenerationASTVisitor() {}
  CodeGenerationASTVisitor(boolean debug) {super(false,debug);} //enables print for debugging

	@Override
	public String visitNode(ProgLetInNode n) {
		if (print) printNode(n);
		String declCode = null;
		for (Node dec : n.declist) declCode=nlJoin(declCode,visit(dec));
		return nlJoin(
			"push 0",	
			declCode, // generate code for declarations (allocation)			
			visit(n.exp),
			"halt",
			getCode()
		);
	}

	@Override
	public String visitNode(ProgNode n) {
		if (print) printNode(n);
		return nlJoin(
			visit(n.exp),
			"halt"
		);
	}


	@Override
	public String visitNode(ClassNode n) {
		if (print) printNode(n,n.classID);
		List<String> distpatchTable = new ArrayList<>();

		if(n.superID != null){ //inheritance
			//copy of the dispatchTable of the superclass
			distpatchTable.addAll(dispatchTables.get(-n.superEntry.offset-2));
		}

		for (int i = 0; i < n.functions.size(); i++) {
			n.functions.get(i).label = "class" + dispatchTables.size() + "method" + i;
			if (n.functions.get(i).offset >= distpatchTable.size()) { //non c'è overriding
				distpatchTable.add(n.functions.get(i).label);
			} else {
				//overriding: substitution of the method label
				distpatchTable.set(n.functions.get(i).offset, n.functions.get(i).label);
			}

			visit(n.functions.get(i)); //on the top of the stack there is the address of the method
		}
		String dispatchTableCode = null;
		for (String label : distpatchTable){
			dispatchTableCode = nlJoin(
					dispatchTableCode,
					"push " + label, //push on stack label(address)
					"lhp", //push on stack heap pointer
					"sw", //put in memory[heap pointer] the label

					//hp increment (point to next free position, hp=hp+1)
					"lhp",
					"push 1",
					"add",
					"shp"
			);
		}
		//aggiungo le dispatchtable della classe a dispatchtables
		dispatchTables.add(distpatchTable);
		// put the distpatch pointer on the stack
		return nlJoin(
				"lhp",
				dispatchTableCode
		);
	}

	public String visitNode(ClassFunctionNode n){
		if (print) printNode(n, n.id);

		String declCode = null, popDecl = null, popParl = null;

		for (Node dec : n.declist) {
			declCode = nlJoin(declCode, visit(dec));//string with all declaration
			popDecl = nlJoin(popDecl, "pop");//same number of pop as declaration
		}

		for (int i = 0; i < n.parlist.size(); i++) {
			popParl = nlJoin(popParl, "pop");//same number of pop as parameters
		}

		putCode(
				nlJoin(
						n.label+":",
						"cfp", // set $fp to $sp value
						"lra", // load $ra value

						"/* local declaration code */",
						declCode, // generate code for local declarations

						"/* method body */",
						visit(n.exp), // generate code for function body expression
						"stm", // set $tm to popped value (function result)

						"/* removing local declaration */",
						popDecl, // remove local declarations from stack
						"sra", // set $ra to popped value
						"pop", // remove Access Link from stack

						"/* removing parameters */",
						popParl, // remove parameters from stack
						"sfp", // set $fp to popped value (Control Link)
						"ltm", // load $tm value (function result)
						"lra", // load $ra value
						"js"  // jump to popped address
				)
		);
		return null;
	}


	@Override
	public String visitNode(EmptyNode n) {
		if (print) printNode(n);
		return "push -1";
	}

	@Override
	public String visitNode(ClassCallNode n) {
		if (print) printNode(n);
		String argCode = "/* ClassCallNode */", getAR = null;

		for (int i = n.arglist.size() - 1;i >= 0;i--) {
			//string with arguments in reversed order
			argCode = nlJoin(argCode, visit(n.arglist.get(i)));
		}
		for (int i = 0;i < (n.nl - n.classEntry.nl);i++) {
			getAR=nlJoin(getAR,"lw");//access link, punta all'AR del chiamante
		}

		return nlJoin(
				"lfp", // load Control Link (pointer to frame of function "id" caller)
				argCode, // generate code for argument expressions in reversed order
				"lfp", getAR, // retrieve address of frame containing "id" declaration
				// by following the static chain (of Access Links)
				"push "+n.classEntry.offset,
				"add", // compute address of "id" declaration
				"lw", // load value of "id" variable

				"stm", // set $tm to popped value (with the aim of duplicating top of stack)
				"ltm", // load Access Link (pointer to frame of function "id" declaration)
				"ltm", // duplicate top of stack
				"lw", // load value of "id" variable
				"push "+n.methodEntry.offset,
				"add", // compute address of "id" declaration
				"lw", // load address of "id" function

				"js"  // jump to popped address (saving address of subsequent instruction in $ra)
		);
	}

	@Override
	public String visitNode(NewNode n) {
		if (print) printNode(n, n.classID);
		String argCode = "/* newNode*/";
		//visit and push on the stack all the arguments
		for (int i = 0; i < n.arglist.size(); i++) {
			argCode = nlJoin(argCode, visit(n.arglist.get(i)));
		}
		//load of the arguments on heap
		for (int i = 0; i < n.arglist.size(); i++){
			argCode = nlJoin(argCode,
					"lhp", //load heap pointer
					"sw", //load the argument on the heap
					//increment of heap pointer
					"lhp",
					"push 1",
					"add",
					"shp" //store heap pointer
			);
		}

		return nlJoin(
				argCode,
				//get from MEMSIZE + offset (ID class) the dispatch pointer
				"push " + MEMSIZE,
				"push " + n.classEntry.offset,
				"add",
				"lw",
				//load distpatch pointer on heap address
				"lhp",
				"sw",
				//load on stack the hp address (object pointer)
				"lhp",
				//imcrement of heap pointer
				"lhp",
				"push 1",
				"add",
				"shp"
		);
	}


	@Override
	public String visitNode(FunNode n) {
		if (print) printNode(n,n.id);
		String declCode = null, popDecl = null, popParl = null;
		for (Node dec : n.declist) {
			declCode = nlJoin(declCode,visit(dec));
			popDecl = nlJoin(popDecl,"pop");
		}
		for (int i=0;i<n.parlist.size();i++) popParl = nlJoin(popParl,"pop");
		String funl = freshFunLabel();
		putCode(
			nlJoin(
				funl+":",
				"cfp", // set $fp to $sp value
				"lra", // load $ra value
				declCode, // generate code for local declarations (they use the new $fp!!!)
				visit(n.exp), // generate code for function body expression
				"stm", // set $tm to popped value (function result)
				popDecl, // remove local declarations from stack
				"sra", // set $ra to popped value
				"pop", // remove Access Link from stack
				popParl, // remove parameters from stack
				"sfp", // set $fp to popped value (Control Link)
				"ltm", // load $tm value (function result)
				"lra", // load $ra value
				"js"  // jump to to popped address
			)
		);
		return "push "+funl;		
	}

	@Override
	public String visitNode(VarNode n) {
		if (print) printNode(n,n.id);
		return visit(n.exp);
	}

	@Override
	public String visitNode(PrintNode n) {
		if (print) printNode(n);
		return nlJoin(
			visit(n.exp),
			"print"
		);
	}

	@Override
	public String visitNode(IfNode n) {
		if (print) printNode(n);
	 	String l1 = freshLabel();
	 	String l2 = freshLabel();		
		return nlJoin(
			visit(n.cond),
			"push 1",
			"beq "+l1,
			visit(n.el),
			"b "+l2,
			l1+":",
			visit(n.th),
			l2+":"
		);
	}

	@Override
	public String visitNode(EqualNode n) {
		if (print) printNode(n);
	 	String l1 = freshLabel();
	 	String l2 = freshLabel();
		return nlJoin(
			visit(n.left),
			visit(n.right),
			"beq "+l1,
			"push 0",
			"b "+l2,
			l1+":",
			"push 1",
			l2+":"
		);
	}

	@Override
	public String visitNode(MinorEqualNode n) {
		if (print) printNode(n);
		String l1 = freshLabel();
		String l2 = freshLabel();
		return nlJoin(
				visit(n.left),
				visit(n.right),
				"bleq "+l1,
				"push 0",
				"b "+l2,
				l1+":",
				"push 1",
				l2+":"
		);
	}

	@Override
	public String visitNode(GreaterEqualNode n) {
		if (print) printNode(n);
		final String l1 = freshLabel();
		final String l2 = freshLabel();
		return nlJoin(
				visit(n.left),        // generate code for left expression
				visit(n.right),              // generate code for right expression
				"push " + 1,                // push 1
				"sub",                      // subtract 1 from right value
				"bleq " + l1, 		// if left value is not less or equal than right value, jump to false label
				"push " + 1,                // push 1 (the result)
				"b " + l2,            // jump to end label
				l1 + ":",           // false label
				"push " + 0,                // push 0 (the result)
				l2 + ":"              // end label
		);
	}

	@Override
	public String visitNode(TimesNode n) {
		if (print) printNode(n);
		return nlJoin(
			visit(n.left),
			visit(n.right),
			"mult"
		);	
	}

	@Override
	public String visitNode(PlusNode n) {
		if (print) printNode(n);
		return nlJoin(
			visit(n.left),
			visit(n.right),
			"add"				
		);
	}

	@Override
	public String visitNode(MinusNode n) {
		if (print) printNode(n);
		return nlJoin(
				visit(n.left),
				visit(n.right),
				"sub"
		);
	}

	@Override
	public String visitNode(DivisionNode n) {
		if (print) printNode(n);
		return nlJoin(
				visit(n.left),
				visit(n.right),
				"div"
		);
	}

	public String visitNode(final NotNode node) {
		if (print) printNode(node);
		final String itWasFalseLabel = freshLabel();
		final String endLabel = freshLabel();
		return nlJoin(
				visit(node.exp),          // generate code for expression
				"push " + 0,                       // push 0
				"beq " + itWasFalseLabel, // if value is 0, jump to itWasFalseLabel
				"push " + 0,                       // push 0 (the result)
				"b " + endLabel,              // jump to end label
				itWasFalseLabel + ":",          // itWasFalseLabel
				"push " + 1,                       // push 1 (the result)
				endLabel + ":"                  // end label
		);
	}

	/**
	 * Generate code for the OrNode node.
	 *
	 * @param node the OrNode node
	 * @return the code generated for the OrNode node
	 */
	@Override
	public String visitNode(final OrNode node) {
		if (print) printNode(node);
		final String trueLabel = freshLabel();
		final String endLabel = freshLabel();
		return nlJoin(
				visit(node.left),    // generate code for left expression
				"push " + 1,                   // push 1
				"beq " + trueLabel,   // if value is 1, jump to true label
				visit(node.right),          // generate code for right expression
				"push " + 1,                   // push 1
				"beq " + trueLabel,   // if value is 1, jump to true label
				"push " + 0,                   // push 0 (the result)
				"b " + endLabel,          // jump to end label
				trueLabel + ":",            // true label
				"push " + 1,                   // push 1 (the result)
				endLabel + ":"              // end label
		);
	}

	/**
	 * Generate code for the AndNode node.
	 *
	 * @param node the AndNode node
	 * @return the code generated for the AndNode node
	 */
	@Override
	public String visitNode(final AndNode node) {
		if (print) printNode(node);
		final String falseLabel = freshLabel();
		final String endLabel = freshLabel();
		return nlJoin(
				visit(node.left),    // generate code for left expression
				"push " + 0,                   // push 0
				"beq " + falseLabel,  // if value is 0, jump to false label
				visit(node.right),          // generate code for right expression
				"push " + 0,                   // push 0
				"beq " + falseLabel,  // if value is 0, jump to false label
				"push " + 1,                   // push 1 (the result)
				"b " + endLabel,          // jump to end label
				falseLabel + ":",           // false label
				"push " + 0,                   // push 0 (the result)
				endLabel + ":"              // end label
		);
	}

	@Override
	public String visitNode(CallNode n) {
		if (print) printNode(n,n.id);
		String argCode = null, getAR = null;
		for (int i=n.arglist.size()-1;i>=0;i--) argCode=nlJoin(argCode,visit(n.arglist.get(i)));
		for (int i = 0;i<n.nl-n.entry.nl;i++) getAR=nlJoin(getAR,"lw");
		return nlJoin(
			"lfp", // load Control Link (pointer to frame of function "id" caller)
			argCode, // generate code for argument expressions in reversed order
			"lfp", getAR, // retrieve address of frame containing "id" declaration
                          // by following the static chain (of Access Links)
            "stm", // set $tm to popped value (with the aim of duplicating top of stack)
            "ltm", // load Access Link (pointer to frame of function "id" declaration)
            "ltm", // duplicate top of stack
            "push "+n.entry.offset, "add", // compute address of "id" declaration
			"lw", // load address of "id" function
            "js"  // jump to popped address (saving address of subsequent instruction in $ra)
		);
	}

	@Override
	public String visitNode(IdNode n) {
		if (print) printNode(n,n.id);
		String getAR = null;
		for (int i = 0;i<n.nl-n.entry.nl;i++) getAR=nlJoin(getAR,"lw");
		return nlJoin(
			"lfp", getAR, // retrieve address of frame containing "id" declaration
			              // by following the static chain (of Access Links)
			"push "+n.entry.offset, "add", // compute address of "id" declaration
			"lw" // load value of "id" variable
		);
	}

	@Override
	public String visitNode(BoolNode n) {
		if (print) printNode(n,n.val.toString());
		return "push "+(n.val?1:0);
	}

	@Override
	public String visitNode(IntNode n) {
		if (print) printNode(n,n.val.toString());
		return "push "+n.val;
	}
}