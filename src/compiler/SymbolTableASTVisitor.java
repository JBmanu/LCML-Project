package compiler;

import java.util.*;
import compiler.AST.*;
import compiler.exc.*;
import compiler.lib.*;

public class SymbolTableASTVisitor extends BaseASTVisitor<Void,VoidException> {

	private final Map<String, VirtualTable> classTable = new HashMap<>();

	private List<Map<String, STentry>> symTable = new ArrayList<>();
	private int nestingLevel=0; // current nesting level
	private int decOffset=-2; // counter for offset of local declarations at current nesting level 
	int stErrors=0;

	SymbolTableASTVisitor() {}
	SymbolTableASTVisitor(boolean debug) {super(debug);} // enables print for debugging

	private STentry stLookup(String id) {
		int j = nestingLevel;
		STentry entry = null;
		while (j >= 0 && entry == null) 
			entry = symTable.get(j--).get(id);	
		return entry;
	}

	@Override
	public Void visitNode(ProgLetInNode n) {
		if (print) printNode(n);
		Map<String, STentry> hm = new HashMap<>();
		symTable.add(hm);
	    for (Node dec : n.declist) visit(dec);
		visit(n.exp);
		symTable.remove(0);
		return null;
	}

	@Override
	public Void visitNode(ProgNode n) {
		if (print) printNode(n);
		visit(n.exp);
		return null;
	}
	
	@Override
	public Void visitNode(FunNode n) {
		if (print) printNode(n);
		Map<String, STentry> hm = symTable.get(nestingLevel);
		List<TypeNode> parTypes = new ArrayList<>();  
		for (ParNode par : n.parlist) parTypes.add(par.getType()); 
		STentry entry = new STentry(nestingLevel, new ArrowTypeNode(parTypes,n.retType),decOffset--);
		//inserimento di ID nella symtable
		if (hm.put(n.id, entry) != null) {
			System.out.println("Fun id " + n.id + " at line "+ n.getLine() +" already declared");
			stErrors++;
		} 
		//creare una nuova hashmap per la symTable
		nestingLevel++;
		Map<String, STentry> hmn = new HashMap<>();
		symTable.add(hmn);
		int prevNLDecOffset=decOffset; // stores counter for offset of declarations at previous nesting level 
		decOffset=-2;
		
		int parOffset=1;
		for (ParNode par : n.parlist)
			if (hmn.put(par.id, new STentry(nestingLevel,par.getType(),parOffset++)) != null) {
				System.out.println("Par id " + par.id + " at line "+ n.getLine() +" already declared");
				stErrors++;
			}
		for (Node dec : n.declist) visit(dec);
		visit(n.exp);
		//rimuovere la hashmap corrente poiche' esco dallo scope               
		symTable.remove(nestingLevel--);
		decOffset=prevNLDecOffset; // restores counter for offset of declarations at previous nesting level 
		return null;
	}
	
	@Override
	public Void visitNode(VarNode n) {
		if (print) printNode(n);
		visit(n.exp);
		Map<String, STentry> hm = symTable.get(nestingLevel);
		STentry entry = new STentry(nestingLevel,n.getType(),decOffset--);
		//inserimento di ID nella symtable
		if (hm.put(n.id, entry) != null) {
			System.out.println("Var id " + n.id + " at line "+ n.getLine() +" already declared");
			stErrors++;
		}
		return null;
	}

	@Override
	public Void visitNode(PrintNode n) {
		if (print) printNode(n);
		visit(n.exp);
		return null;
	}

	@Override
	public Void visitNode(IfNode n) {
		if (print) printNode(n);
		visit(n.cond);
		visit(n.th);
		visit(n.el);
		return null;
	}
	
	@Override
	public Void visitNode(EqualNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(LessEqualNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(GreaterEqualNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(AndNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(OrNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(NotNode n) {
		if (print) printNode(n);
		visit(n.exp);
		return null;
	}
	
	@Override
	public Void visitNode(TimesNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(DivNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}
	
	@Override
	public Void visitNode(PlusNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(MinusNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(CallNode n) {
		if (print) printNode(n);
		STentry entry = stLookup(n.id);
		if (entry == null) {
			System.out.println("Fun id " + n.id + " at line "+ n.getLine() + " not declared");
			stErrors++;
		} else {
			n.entry = entry;
			n.nl = nestingLevel;
		}
		for (Node arg : n.arglist) visit(arg);
		return null;
	}

	public Void visitNode(final MethodNode n) {
		if (print) printNode(n);
		final Map<String, STentry> currentTable = symTable.get(nestingLevel);
		final List<TypeNode> params = n.parlist.stream()
				.map(ParNode::getType)
				.toList();
		final boolean isOverriding = currentTable.containsKey(n.id);
		final TypeNode methodType = new MethodTypeNode(new ArrowTypeNode(params, n.ret));
		STentry entry = new STentry(nestingLevel, methodType, decOffset++);
		if (isOverriding) {
			final var overriddenMethodEntry = currentTable.get(n.id);
			final boolean isOverridingAMethod = overriddenMethodEntry != null && overriddenMethodEntry.type instanceof MethodTypeNode;
			if (isOverridingAMethod) {
				entry = new STentry(nestingLevel, methodType, overriddenMethodEntry.offset);
				decOffset--;
			} else {
				System.out.println("Cannot override a class attribute with a method: " + n.id);
				stErrors++;
			}
		}

		n.offset = entry.offset;
		currentTable.put(n.id, entry);

		// Create a new table for the method.
		nestingLevel++;
		final Map<String, STentry> methodTable = new HashMap<>();
		symTable.add(methodTable);

		// Set the declaration offset
		int prevDecOffset = decOffset;
		decOffset = -2;
		int parameterOffset = 1;

		for (final ParNode parameter : n.parlist) {
			final STentry parameterEntry = new STentry(nestingLevel, parameter.getType(), parameterOffset++);
			if (methodTable.put(parameter.id, parameterEntry) != null) {
				System.out.println("Par id " + parameter.id + " at line " + n.getLine() + " already declared");
				stErrors++;
			}
		}
		n.declist.forEach(this::visit);
		visit(n.exp);

		// Remove the current nesting level symbolTable.
		symTable.remove(nestingLevel--);
		decOffset = prevDecOffset;
		return null;
	}


	@Override
	public Void visitNode(NewNode n) {
		if (print) printNode(n);
		STentry entry = stLookup(n.id);
		if (entry == null) {
			System.out.println("Class id " + n.id + " at line "+ n.getLine() + " not found");
			stErrors++;
		} else {
			n.entry = entry;
		}
		for (Node arg : n.arglist) visit(arg);
		return null;
	}

	@Override
	public Void visitNode(EmptyTypeNode n) {
		if (print) printNode(n);
		return null;
	}

	@Override
	public Void visitNode(IdNode n) {
		if (print) printNode(n);
		STentry entry = stLookup(n.id);
		if (entry == null) {
			System.out.println("Var or Par id " + n.id + " at line "+ n.getLine() + " not declared");
			stErrors++;
		} else {
			n.entry = entry;
			n.nl = nestingLevel;
		}
		return null;
	}

	@Override
	public Void visitNode(BoolNode n) {
		if (print) printNode(n, n.val.toString());
		return null;
	}

	@Override
	public Void visitNode(IntNode n) {
		if (print) printNode(n, n.val.toString());
		return null;
	}

	@Override
	public Void visitNode(final ClassNode n) {
		if (print) printNode(n);

		ClassTypeNode tempClassTypeNode = new ClassTypeNode();
		boolean isSubClass = n.superId.isPresent();
		String superId = isSubClass ? n.superId.get() : null;

		if (isSubClass) {
			if (classTable.containsKey(superId)) {
				final STentry superSTEntry = symTable.get(0).get(superId);
				final ClassTypeNode superTypeNode = (ClassTypeNode) superSTEntry.type;
				tempClassTypeNode = new ClassTypeNode(superTypeNode);
				n.superEntry = superSTEntry;
			} else {
				System.out.println("Class " + superId + " at line " + n.getLine() + " not declared");
				stErrors++;
			}
		}

		final ClassTypeNode classTypeNode = tempClassTypeNode;
		n.setType(classTypeNode);

		// Add the class id to the global scope table checking for duplicates
		final STentry entry = new STentry(0, classTypeNode, decOffset--);
		final Map<String, STentry> globalScopeTable = symTable.get(0);
		if (globalScopeTable.put(n.id, entry) != null) {
			System.out.println("Class id " + n.id + " at line " + n.getLine() + " already declared");
			stErrors++;
		}

		// Add the class to the class table
		final Set<String> visitedClassNames = new HashSet<>();
		final VirtualTable virtualTable = new VirtualTable();
		if (isSubClass) {
			final VirtualTable superClassVirtualTable = classTable.get(superId);
			virtualTable.putAll(superClassVirtualTable);
		}
		classTable.put(n.id, virtualTable);

		symTable.add(virtualTable);
		// Setting the field offset
		nestingLevel++;
		int fieldOffset = -1;
		if (isSubClass) {
			final ClassTypeNode superTypeNode = (ClassTypeNode) symTable.get(0).get(superId).type;
			fieldOffset = -superTypeNode.fieldList.size() - 1;
		}

		/*
		 * Handle field declaration.
		 */
		for (final FieldNode field : n.fieldList) {
			if (visitedClassNames.contains(field.id)) {
				System.out.println(
						"Field with id " + field.id + " on line " + field.getLine() + " was already declared"
				);
				stErrors++;
			} else {
				visitedClassNames.add(field.id);
			}
			visit(field);

			STentry fieldEntry = new STentry(nestingLevel, field.getType(), fieldOffset--);
			final boolean isFieldOverridden = isSubClass && virtualTable.containsKey(field.id);
			if (isFieldOverridden) {
				final STentry overriddenFieldEntry = virtualTable.get(field.id);
				final boolean isOverridingAMethod = overriddenFieldEntry.type instanceof MethodTypeNode;
				if (isOverridingAMethod) {
					System.out.println("Cannot override method " + field.id + " with a field");
					stErrors++;
				} else {
					fieldEntry = new STentry(nestingLevel, field.getType(), overriddenFieldEntry.offset);
					classTypeNode.fieldList.set(-fieldEntry.offset - 1, fieldEntry.type);
				}
			} else {
				classTypeNode.fieldList.add(-fieldEntry.offset - 1, fieldEntry.type);
			}

			// Add the field to the virtual table
			virtualTable.put(field.id, fieldEntry);
			field.offset = fieldEntry.offset;
		}

		// Setting the method offset
		int prevDecOffset = decOffset;
		decOffset = 0;
		if (isSubClass) {
			final ClassTypeNode superTypeNode = (ClassTypeNode) symTable.get(0).get(superId).type;
			decOffset = superTypeNode.methodList.size();
		}

		for (final MethodNode method : n.methodList) {
			if (visitedClassNames.contains(method.id)) {
				System.out.println(
						"Method with id " + method.id + " on line " + method.getLine() + " was already declared"
				);
				stErrors++;
			} else {
				visitedClassNames.add(method.id);
			}
			visit(method);
			final MethodTypeNode methodTypeNode = (MethodTypeNode) symTable.get(nestingLevel).get(method.id).type;
			classTypeNode.methodList.add(
					method.offset,
					methodTypeNode.arrowType
			);
		}

		// Remove the class from the symbol table
		symTable.remove(nestingLevel--);
		decOffset = prevDecOffset;
		return null;
	}

	@Override
	public Void visitNode(final FieldNode n) {
		if (print) printNode(n);
		return null;
	}

	@Override
	public Void visitNode(final EmptyNode n) {
		if (print) printNode(n);
		return null;
	}

	@Override
	public Void visitNode(final ClassCallNode node) {
		if (print) printNode(node);
		final STentry entry = stLookup(node.objectId);
		if (entry == null) {
			System.out.println("Object id " + node.objectId + " was not declared");
			stErrors++;
		} else if (entry.type instanceof final RefTypeNode refTypeNode) {
			node.entry = entry;
			node.nestingLevel = nestingLevel;
			final VirtualTable virtualTable = classTable.get(refTypeNode.id);
			if (virtualTable.containsKey(node.methodId)) {
				node.methodEntry = virtualTable.get(node.methodId);
			} else {
				System.out.println(
						"Object id " + node.objectId + " at line " + node.getLine() + " has no method " + node.methodId
				);
				stErrors++;
			}
		} else {
			System.out.println("Object id " + node.objectId + " at line " + node.getLine() + " is not a RefType");
			stErrors++;
		}
		node.argList.forEach(this::visit);
		return null;
	}

	@Override
	public Void visitNode(final ClassTypeNode n) {
		if (print) printNode(n);
		return null;
	}

	@Override
	public Void visitNode(final MethodTypeNode n) {
		if (print) printNode(n);
		return null;
	}

	@Override
	public Void visitNode(final RefTypeNode node) {
		if (print) printNode(node);
		if (!this.classTable.containsKey(node.id)) {
			System.out.println("Class with id: " + node.id + " on line: " + node.getLine() + " was not declared");
			stErrors++;
		}
		return null;
	}

	static class VirtualTable extends HashMap<String, STentry> {
	}
}
