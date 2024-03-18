package compiler;

import java.util.*;
import compiler.AST.*;
import compiler.exc.*;
import compiler.lib.*;

public class SymbolTableASTVisitor extends BaseASTVisitor<Void,VoidException> {
	
	private List<Map<String, STentry>> symTable = new ArrayList<>();
	private Map<String, Map<String,STentry>> classTable = new HashMap<>();
	private int nestingLevel=0; // current nesting level
	private int decOffset=-2; // counter for offset of local declarations at current nesting level 
	int stErrors=0;
	private HashSet<String> attributesId;

	SymbolTableASTVisitor() {}
	SymbolTableASTVisitor(boolean debug) {super(debug);}

	private STentry stLookup(String id) {
		int j = nestingLevel;
		STentry entry = null;
		while (j >= 0 && entry == null)// && entry == null a che serve?
			entry = symTable.get(j--).get(id);
		System.out.println("j: " + j + " entry: " + entry);
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
	public Void visitNode(ClassNode node) {
		System.out.println("classTable:" + classTable.toString());
		System.out.println("node.superID: " + node.superID);

		if (print) printNode(node);
		this.attributesId = new HashSet<>();
		this.checkNestingLevel0(node);

		Map<String, STentry> level0HashMap = this.symTable.get(this.nestingLevel);
		ClassTypeNode classType = new ClassTypeNode(new ArrayList<>(), new ArrayList<>());
		node.classType = this.defineClassType(node, classType);

		STentry entry = new STentry(this.nestingLevel, classType, this.decOffset--);
		Map<String, STentry> virtualTable = this.addVirtualTable(node, level0HashMap, entry);
		int prevNLDecOffset = this.modifyOffsetForVisit(node);

		this.manageAddedAttribute(node, classType, virtualTable);
		this.manageAddedFunctions(node, classType, virtualTable);

		decOffset=prevNLDecOffset;
		nestingLevel--;
		symTable.remove(virtualTable);

		return null;
	}

	public void checkNestingLevel0(ClassNode node){
		if(this.nestingLevel !=0){
			System.out.println("Class id " + node.classID + " at line "+ node.getLine() +" declared at nesting level != 0");
            this.stErrors++;
		}
	}

	public ClassTypeNode defineClassType(ClassNode node, ClassTypeNode classType){
		if (node.superID != null){//superID esiste
			System.out.println("classTable:" + classTable.toString());
			if (classTable.get(node.superID)!= null) {

				node.superEntry=symTable.get(0).get(node.superID);

				classType.functions.addAll(((ClassTypeNode)symTable.get(0).get(node.superID).type).functions);
				classType.attributes.addAll(((ClassTypeNode)symTable.get(0).get(node.superID).type).attributes);
			} else {
				System.out.println("SuperClass id " + node.superID + " at line "+ node.getLine() +" is not declared");
				stErrors++;
			}
		}
		return classType;
	}

	public Map<String, STentry> addVirtualTable(ClassNode node, Map<String, STentry> level0HashMap, STentry entry){
		//inserimento di ID nella symtable
		if (level0HashMap.put(node.classID, entry) != null) {
			System.out.println("Class id " + node.classID + " at line "+ node.getLine() +" already declared");
			stErrors++;
		}

		Map<String, STentry> virtualTable = new HashMap<>();//level 1
		if (node.superID != null) {
			virtualTable.putAll(classTable.get(node.superID));
		}

		System.out.println("added " + node.classID + " in the class table: " + classTable.toString());
		classTable.put(node.classID, virtualTable);
		symTable.add(virtualTable);

		return virtualTable;
	}

	public int modifyOffsetForVisit(ClassNode node){
		int prevNLDecOffset=decOffset;
		decOffset = -1;
		if (node.superID != null){
			decOffset = -(((ClassTypeNode)symTable.get(0).get(node.superID).type).attributes).size() -1;
		}

		nestingLevel++;
		return prevNLDecOffset;
	}

	public void manageAddedAttribute(ClassNode node, ClassTypeNode classType, Map<String, STentry> virtualTable){
		for (int i=0; i< node.attributes.size();i++){
			AttributeNode attribute = node.attributes.get(i);
			System.out.println("attribute: " + attribute);
			if(this.attributesId.add(attribute.id)){
				if(virtualTable.containsKey(attribute.id)){
					STentry currentFieldEntry = virtualTable.get(attribute.id);
					if(!(currentFieldEntry.type instanceof FunctionTypeNode)){
						virtualTable.put(attribute.id, new STentry(nestingLevel,attribute.getType(),currentFieldEntry.offset));
						attribute.offset=currentFieldEntry.offset;
						classType.attributes.set((-attribute.offset -1),attribute.getType());
					}else{
						//overriding not ok
						System.out.println("cannot override a method with the field "+ node.classID+"."+attribute.id );
						stErrors++;
					}
				}else{
					virtualTable.put(attribute.id, new STentry(this.nestingLevel,attribute.getType(),this.decOffset));
					attribute.offset=this.decOffset;
					decOffset--;
					classType.attributes.add(attribute.getType());
				}
			}else{
				// caso due dichiarazioni di campo con lo stesso nome
				System.out.println("Field id " + attribute.id + " at line " + node.getLine() + " already declared");
				stErrors++;
			}
		}
	}

	public void manageAddedFunctions(ClassNode node, ClassTypeNode classType, Map<String, STentry> virtualTable){
		this.attributesId = new HashSet<>();
		decOffset=0;
		if (node.superID != null){
			decOffset = (((ClassTypeNode)symTable.get(0).get(node.superID).type).functions).size();
		}

		int lastOffset = decOffset;
		for (int i =0;i<node.functions.size();i++){
			ClassFunctionNode function = node.functions.get(i);
			if(!(attributesId.add(function.id))){
				System.out.println("Method id "+function.id + " at line " + node.getLine() +" already declared" );
				stErrors++;
			} else {
				visit(function);
				if (lastOffset != decOffset) {
					lastOffset = decOffset;
					classType.functions.add(((FunctionTypeNode) virtualTable.get(function.id).type).fun);
				} else {
					STentry currentMethodEntry = virtualTable.get(function.id);
					if (currentMethodEntry.type instanceof FunctionTypeNode) {
						classType.functions.set(function.offset, ((FunctionTypeNode) virtualTable.get(function.id).type).fun);
					}
				}
			}
		}
	}

	@Override
	public Void visitNode(ClassFunctionNode n) {
		if (print) printNode(n);

		//creazione methodType
		List<TypeNode> parTypes = new ArrayList<>();
		for (ParNode par : n.parlist) parTypes.add(par.getType());

		FunctionTypeNode methodTypeNode = new FunctionTypeNode(
				new ArrowTypeNode(parTypes, n.retType));


		Map<String,STentry> virtualTable=symTable.get(1);

		if (!virtualTable.containsKey(n.id)) {
			//virtual table non contiene il metodo (not overriding)
			virtualTable.put(n.id, new STentry(nestingLevel, methodTypeNode, decOffset));
			n.offset = decOffset;
			decOffset++;
			//classType.allMethods.add(((MethodTypeNode) methodTypeNode).fun);
		} else {
			//override
			STentry currentMethodEntry = virtualTable.get(n.id);
			if (currentMethodEntry.type instanceof FunctionTypeNode) {
				//overriding ok
				virtualTable.put(n.id, new STentry(nestingLevel, methodTypeNode, currentMethodEntry.offset));
				n.offset = currentMethodEntry.offset;
			} else {
				//overriding not ok
				System.out.println("cannot override a field with the method " + n.id);
				stErrors++;
			}
		}



		//gestione parametri
		int prevNLOffset = decOffset;
		decOffset = 1;

		Map<String,STentry> nestedHashMap = new HashMap<>();
		nestingLevel++; //level 2
		symTable.add(nestedHashMap); //added level 2 currently empty

		//insert parameter STentry and check if parameter is already declared
		for (ParNode par: n.parlist) {
			if (nestedHashMap.put(par.id, new STentry(nestingLevel, par.getType(), decOffset++)) != null) {
				System.out.println("Par id " + par.id + " at line " + par.getLine() + " already declared");
				stErrors++;
			}
		}

		//visit declaration
		for (DecNode dec : n.declist) {
			visit(dec);
		}

		//visit body
		visit(n.exp);

		decOffset = prevNLOffset;
		nestingLevel--;
		symTable.remove(nestedHashMap);

		return null;
	}


	@Override
	public Void visitNode(NewNode n){
		if (print) printNode(n);

		STentry classEntry = stLookup(n.classID);//cerco la STentry della classe

		if (classEntry == null) {
			System.out.println("Class " + n.classID + " at line "+ n.getLine() + " not declared");
			stErrors++;
		} else {
			n.classEntry = classEntry;
		}

		for (Node arg : n.arglist) {
			visit(arg);
		}

		return null;
	}
	@Override
	public Void visitNode(ClassCallNode n) {
		if (print) printNode(n);
		System.out.println("classTable:" + classTable.toString());

		STentry objectEntry = this.stLookup(n.objectID);//cerco la STentry della classe

		if (objectEntry == null) {
			System.out.println("Class " + n.objectID + " at line "+ n.getLine() + " not declared");
			stErrors++;
		} else {//se objectEntry esiste
			n.classEntry = objectEntry;
			n.nl = nestingLevel;

			String classID = ((RefTypeNode) objectEntry.type).classID;
			STentry methodEntry = classTable.get(classID).get(n.methodID);
			System.out.println("classID:" + classID);

			if (methodEntry == null) {
				System.out.println("Method " + n.methodID + " at line " + n.getLine() + " not declared in class " + n.objectID);
				stErrors++;
			}
			else {//se il metodo esiste
				n.methodEntry = methodEntry;
				n.nl = nestingLevel;
			}

			for (Node arg : n.arglist) {
				visit(arg);
			}
		}
		return null;
	}



	@Override
	public Void visitNode(ProgNode n) {
		if (print) printNode(n);
		visit(n.exp);
		return null;
	}

	public Void visitNode(FunNode n) {
		if (print) printNode(n);
		Map<String, STentry> hm = symTable.get(nestingLevel);
		List<TypeNode> parTypes = new ArrayList<>();
		for (ParNode par : n.parlist) parTypes.add(par.getType());
		STentry entry = new STentry(nestingLevel, new ArrowTypeNode(parTypes,n.retType),decOffset--);
		if (hm.put(n.id, entry) != null) {
			System.out.println("Fun id " + n.id + " at line "+ n.getLine() +" already declared");
			stErrors++;
		}
		nestingLevel++;
		Map<String, STentry> hmn = new HashMap<>();
		symTable.add(hmn);
		int prevNLDecOffset=decOffset;
		decOffset=-2;

		int parOffset=1;
		for (ParNode par : n.parlist) {
			if (hmn.put(par.id, new STentry(nestingLevel, par.getType(), parOffset++)) != null) {
				System.out.println("Par id " + par.id + " at line " + n.getLine() + " already declared");
				stErrors++;
			}
		}
		for (Node dec : n.declist) visit(dec);
		visit(n.exp);
		symTable.remove(nestingLevel--);
		decOffset=prevNLDecOffset;
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
	public Void visitNode(EmptyNode n) {
		if (print) printNode(n, "null");
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
	public Void visitNode(MinorEqualNode n) {
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
	public Void visitNode(TimesNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(DivisionNode n) {
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


	/**
	 * Visit a NotNode.
	 * Visit the expression.
	 *
	 * @param node the NotNode to visit
	 * @return null
	 */
	@Override
	public Void visitNode(final NotNode node) {
		if (print) printNode(node);
		visit(node.exp);
		return null;
	}

	/**
	 * Visit a OrNode.
	 * Visit the left and right expression.
	 *
	 * @param node the OrNode to visit
	 * @return null
	 */
	@Override
	public Void visitNode(final OrNode node) {
		if (print) printNode(node);
		visit(node.left);
		visit(node.right);
		return null;
	}

	/**
	 * Visit a AndNode.
	 * Visit the left and right expression.
	 *
	 * @param node the AndNode to visit
	 * @return null
	 */
	@Override
	public Void visitNode(final AndNode node) {
		if (print) printNode(node);
		visit(node.left);
		visit(node.right);
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
}
