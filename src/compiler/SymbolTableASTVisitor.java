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
	public Void visitNode(ClassNode node) {
		if (print) printNode(node);
		this.attributesId = new HashSet<>();
		this.checkNestingLevel0(node);

		Map<String, STentry> level0HashMap = symTable.get(nestingLevel);
		node.classType = this.defineClassType(node);

		STentry entry = new STentry(nestingLevel, node.classType, decOffset--);
		Map<String, STentry> virtualTable = this.addVirtualTable(node, level0HashMap, entry);
		int prevNLDecOffset = this.modifyOffsetForVisit(node);

		this.manageAddedAttribute(node, virtualTable);
		this.manageAddedFunctions(node, virtualTable);

		decOffset=prevNLDecOffset; // restores counter for offset of declarations at previous nesting level
		nestingLevel--;
		symTable.remove(virtualTable);

		return null;
	}

	public void checkNestingLevel0(ClassNode node){
		if(nestingLevel!=0){
			System.out.println("Class id " + node.classID + " at line "+ node.getLine() +" declared at nesting level != 0");
			stErrors++;
		}
	}

	public ClassTypeNode defineClassType(ClassNode node){
		ClassTypeNode classType = new ClassTypeNode(new ArrayList<>(), new ArrayList<>());

		if (node.superID != null){//superID esiste
			if (classTable.get(node.superID)!= null) {

				node.superEntry=symTable.get(0).get(node.superID);

				classType.attributes.addAll(((ClassTypeNode)symTable.get(0).get(node.superID).type).attributes);
				classType.functions.addAll(((ClassTypeNode)symTable.get(0).get(node.superID).type).functions);
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
		if (node.superID != null) {//se superID esiste aggiungo alla virtualTable i campi e i metodi ereditati
			virtualTable.putAll(classTable.get(node.superID));
		}

		classTable.put(node.classID, virtualTable);//inserisce nome classe e virtual table relativa (per riferimento)
		symTable.add(virtualTable);//aggiungo nel NL 1 la virtual table

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

	public void manageAddedAttribute(ClassNode node, Map<String, STentry> virtualTable){
		for (int i=0; i< node.attributes.size();i++){
			AttributeNode attribute = node.attributes.get(i);
			if(this.attributesId.add(attribute.id)){
				//se il campo non è stato ancora dichiarato
				if(virtualTable.containsKey(attribute.id)){
					//overriding
					STentry currentFieldEntry = virtualTable.get(attribute.id);
					if(!(currentFieldEntry.type instanceof FunctionTypeNode)){
						//overriding ok
						virtualTable.put(attribute.id, new STentry(nestingLevel,attribute.getType(),currentFieldEntry.offset));
						//mantieni offset del campo che stai overridando
						attribute.offset=currentFieldEntry.offset;
						node.classType.attributes.set((-attribute.offset -1),attribute.getType());
					}else{
						//overriding not ok
						System.out.println("cannot override a method with the field "+ node.classID+"."+attribute.id );
						stErrors++;
					}
				}else{
					//not overriding
					virtualTable.put(attribute.id, new STentry(this.nestingLevel,attribute.getType(),this.decOffset));
					attribute.offset=this.decOffset;
					decOffset--;
					node.classType.attributes.add(attribute.getType());
				}
			}else{
				// caso due dichiarazioni di campo con lo stesso nome
				System.out.println("Field id " + attribute.id + " at line " + node.getLine() + " already declared");
				stErrors++;
			}
		}
	}

	public void manageAddedFunctions(ClassNode node, Map<String, STentry> virtualTable){
		this.attributesId = new HashSet<>();
		decOffset=0;
		if (node.superID != null){
			decOffset = (((ClassTypeNode)symTable.get(0).get(node.superID).type).functions).size();
		}

		int lastOffset = decOffset;
		for (int i =0;i<node.attributes.size();i++){
			FunctionNode function =  node.functions.get(i);
			if(!(attributesId.add(function.id))){
				System.out.println("Method id "+function.id + " at line " + node.getLine() +" already declared" );
				stErrors++;
			} else {
				visit(function);
				if (lastOffset != decOffset) {
					lastOffset = decOffset;
					node.classType.functions.add(((FunctionTypeNode) virtualTable.get(function.id).type).fun);
				} else {
					STentry currentMethodEntry = virtualTable.get(function.id);
					if (currentMethodEntry.type instanceof FunctionTypeNode) {
						node.classType.functions.set(function.offset, ((FunctionTypeNode) virtualTable.get(function.id).type).fun);
					}
				}
			}
		}

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
		for (ParNode par : n.parlist)
			if (hmn.put(par.id, new STentry(nestingLevel,par.getType(),parOffset++)) != null) {
				System.out.println("Par id " + par.id + " at line "+ n.getLine() +" already declared");
				stErrors++;
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
