package compiler;

import compiler.AST.*;
import compiler.lib.*;
import compiler.exc.*;
import compiler.lib.Node;
import svm.ExecuteVM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static compiler.lib.FOOLlib.*;

public class CodeGenerationASTVisitor extends BaseASTVisitor<String, VoidException> {

	private List<List<String>> dispatchTables = new ArrayList<>();

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
			"beq " + l1,
			"push 0",
			"b " + l2,
			l1 + ":",
			"push 1",
			l2 + ":"
		);
	}

	@Override
	public String visitNode(LessEqualNode n) {
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
		String l1 = freshLabel();
		String l2 = freshLabel();
		String l3 = freshLabel();
		return nlJoin(
				visit(n.left),
				visit(n.right),
				"bleq "+l1,
				"push 1",
				"b "+l3,
				l1+":",
				visit(n.left),
				visit(n.right),
				"beq "+l2,
				"push 0",
				"b "+l3,
				l2+":",
				"push 1",
				l3+":"
		);
	}

	@Override
	public String visitNode(AndNode n) {
		if (print) printNode(n);
		String l1 = freshLabel();
		String l2 = freshLabel();
		String l3 = freshLabel();
		return nlJoin(
				visit(n.left),
				"beq "+l1,
				"push 0",
				"b "+l3,
				l1+":",
				visit(n.right),
				"beq "+l2,
				"push 0",
				"b "+l3,
				l2+":",
				"push 1",
				l3+":"
		);
	}

	@Override
	public String visitNode(OrNode n) {
		if (print) printNode(n);
		String l1 = freshLabel();
		String l2 = freshLabel();
		return nlJoin(
				visit(n.left),
				"beq "+l1,
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
	public String visitNode(NotNode n) {
		if (print) printNode(n);
		String l1 = freshLabel();
		String l2 = freshLabel();
		return nlJoin(
				visit(n.exp),
				"beq "+l1,
				"push 1",
				"b "+l2,
				l1+":",
				"push 0",
				l2+" :"
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
	public String visitNode(DivNode n) {
		if (print) printNode(n);
		return nlJoin(
				visit(n.left),
				visit(n.right),
				"div"
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

	public String visitNode(final MethodNode n) {
		if (print) printNode(n);

		String declarationsCode = "";
		for (final DecNode declaration : n.declist) {
			declarationsCode = nlJoin(
					declarationsCode,
					visit(declaration)
			);
		}

		String popDeclarationsCode = "";
		for (final DecNode declaration : n.declist) {
			popDeclarationsCode = nlJoin(
					popDeclarationsCode,
					"pop"
			);
		}

		String popParametersCode = "";
		for (final ParNode parameter : n.parlist) {
			popParametersCode = nlJoin(
					popParametersCode,
					"pop"
			);
		}

		final String methodLabel = freshFunLabel();

		n.label = methodLabel; // set the label of the method

		// Generate code for the method body
		putCode(
				nlJoin(
						methodLabel + ":",   // method label

						// Set up the stack frame with FP, RA, and declarations
						"cfp",                    // copy $sp to $fp, the new frame pointer
						"lra",                    // push return address
						declarationsCode,           // generate code for declarations

						// Generate code for the body and store the result in $tm
						visit(n.exp),            // generate code for the expression
						"stm",                   // set $tm to popped value (function result)

						// Frame cleanup
						popDeclarationsCode,        // pop declarations
						"sra",                   // pop return address to $ra (for return)
						"pop",                        // pop $fp
						popParametersCode,          // pop parameters
						"sfp",                   // pop $fp (restore old frame pointer)

						// Return
						"ltm",                    // push function result
						"lra",                    // push return address
						"js"                  // jump to return address
				)
		);

		return null;
	}


	@Override
	public String visitNode(final NewNode n) {
		if (print) printNode(n);

		String argumentsCode = "";
		for (final Node argument : n.arglist) {
			argumentsCode = nlJoin(
					argumentsCode,
					visit(argument)
			);
		}

		String moveArgumentsOnHeapCode = "";
		for (final Node argument : n.arglist) {
			moveArgumentsOnHeapCode = nlJoin(
					moveArgumentsOnHeapCode,

					// Store argument on the heap
					"lhp",    // push $hp on the stack
					"sw",           // store argument on the heap

					// Update $hp = $hp + 1
					"lhp",    // push $hp on the stack
					"push " + 1,             // push 1 on the stack
					"add",                  // add 1 to $hp
					"shp"           // store $hp
			);
		}

		return nlJoin(

				// Set up arguments on the stack and move them on the heap
				argumentsCode,      // generate arguments
				moveArgumentsOnHeapCode,  // move arguments on the heap

				// Load the address of the dispatch table in the heap
				"push "+ (ExecuteVM.MEMSIZE + n.entry.offset), // push class address on the stack
				"lw",          // load dispatch table address
				"lhp",  // push $hp on the stack
				"sw",         // store dispatch table address on the heap

				// Put the result on the stack (object address)
				"lhp",  // push $hp on the stack (object address)

				// Update $hp = $hp + 1
				"lhp",  // push $hp on the stack
				"push " + 1,           // push 1 on the stack
				"add ",                // add 1 to $hp
				"shp "            // store $hp
		);

	}


	@Override
	public String visitNode(EmptyTypeNode n) {
		if (print) printNode(n, "Null");
		return nlJoin(
				"push -1"
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
			"push "+n.entry.offset,
			"add", // compute address of "id" declaration
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

	@Override
	public String visitNode(final ClassNode n) {
		if (print) printNode(n);

		final List<String> dispatchTable = new ArrayList<>();
		dispatchTables.add(dispatchTable);

		final boolean isSubclass = n.superEntry != null;

		if (isSubclass) {
			final List<String> superDispatchTable = dispatchTables.get(-n.superEntry.offset - 2);
			dispatchTable.addAll(superDispatchTable);
		}

		for (final MethodNode methodEntry : n.methodList) {
			visit(methodEntry);

			final boolean isOverriding = methodEntry.offset < dispatchTable.size();
			if (isOverriding) {
				dispatchTable.set(methodEntry.offset, methodEntry.label);
			} else {
				dispatchTable.add(methodEntry.label);
			}
		}

		String dispatchTableHeapCode = "";
		for (final String label : dispatchTable) {
			dispatchTableHeapCode = nlJoin(
					dispatchTableHeapCode,

					// Store method label in heap
					"push " + label,       // push method label
					"lhp",  // push heap pointer
					"sw",         // store method label in heap

					// Increment heap pointer
					"lhp",  // push heap pointer
					"push " + 1,           // push 1
					"add",                // heap pointer + 1
					"shp"           // store heap pointer

			);
		}

		return nlJoin(
				"lhp",      // push heap pointer, the address of the dispatch table
				dispatchTableHeapCode   // generated code for creating the dispatch table in the heap
		);

	}

	@Override
	public String visitNode(final EmptyNode n) {
		if (print) printNode(n);
		return "push " + "-1";
	}

	@Override
	public String visitNode(final ClassCallNode n) {
		if (print) printNode(n);

		String argumentsCode = "";
		for (int i = n.argList.size() - 1; i >= 0; i--) {
			argumentsCode = nlJoin(
					argumentsCode,
					visit(n.argList.get(i))
			);
		}

		String getARCode = "";
		for (int i = 0; i < n.nestingLevel - n.entry.nl; i++) {
			getARCode = nlJoin(
					getARCode,
					"lw"
			);
		}

		return nlJoin(

				// Set up the stack frame
				"lfp",     // push $fp on the stack
				argumentsCode,      // generate arguments

				// Get the address of the object
				"lfp", getARCode,         // get AR
				"push "+ n.entry.offset,   // push class offset on the stack
				"add",                        // add class offset to $ar
				"lw",                  // load object address


				// Duplicate class address
				"stm",     // set $tm to popped value (class address)
				"ltm",      // push class address on the stack
				"ltm",      // duplicate class address

				// Get the address of the method
				"lw",    // load dispatch table address
				"push " + n.methodEntry.offset, // push method offset on the stack
				"add",          // add method offset to dispatch table address
				"lw",    // load method address

				// Call the method
				"js"
		);

	}


}