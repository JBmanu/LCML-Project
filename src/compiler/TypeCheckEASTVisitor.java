package compiler;

import compiler.AST.*;
import compiler.exc.*;
import compiler.lib.*;
import static compiler.TypeRels.*;

//visitNode(n) fa il type checking di un Node n e ritorna:
//- per una espressione, il suo tipo (oggetto BoolTypeNode o IntTypeNode)
//- per una dichiarazione, "null"; controlla la correttezza interna della dichiarazione
//(- per un tipo: "null"; controlla che il tipo non sia incompleto) 
//
//visitSTentry(s) ritorna, per una STentry s, il tipo contenuto al suo interno
public class TypeCheckEASTVisitor extends BaseEASTVisitor<TypeNode,TypeException> {

	TypeCheckEASTVisitor() { super(true); } // enables incomplete tree exceptions 
	TypeCheckEASTVisitor(boolean debug) { super(true,debug); } // enables print for debugging

	//checks that a type object is visitable (not incomplete) 
	private TypeNode ckvisit(TypeNode t) throws TypeException {
		visit(t);
		return t;
	}

	@Override
	public TypeNode visitNode(ProgLetInNode n) throws TypeException {
		if (print) printNode(n);
		for (Node dec : n.declist)
			try {
				visit(dec);
			} catch (IncomplException e) { 
			} catch (TypeException e) {
				System.out.println("Type checking error in a declaration: " + e.text);
			}
		return visit(n.exp);
	}

	@Override
	public TypeNode visitNode(ProgNode n) throws TypeException {
		if (print) printNode(n);
		return visit(n.exp);
	}

	@Override
	public TypeNode visitNode(FunNode n) throws TypeException {
		if (print) printNode(n,n.id);
		for (Node dec : n.declist)
			try {
				visit(dec);
			} catch (IncomplException e) { 
			} catch (TypeException e) {
				System.out.println("Type checking error in a declaration: " + e.text);
			}
		if ( !isSubtype(visit(n.exp),ckvisit(n.retType)) ) 
			throw new TypeException("Wrong return type for function " + n.id,n.getLine());
		return null;
	}

	@Override
	public TypeNode visitNode(VarNode n) throws TypeException {
		if (print) printNode(n,n.id);
		if ( !isSubtype(visit(n.exp),ckvisit(n.getType())) )
			throw new TypeException("Incompatible value for variable " + n.id,n.getLine());
		return null;
	}

	@Override
	public TypeNode visitNode(PrintNode n) throws TypeException {
		if (print) printNode(n);
		return visit(n.exp);
	}

	@Override
	public TypeNode visitNode(IfNode n) throws TypeException {
		if (print) printNode(n);
		if ( !(isSubtype(visit(n.cond), new BoolTypeNode())) )
			throw new TypeException("Non boolean condition in if",n.getLine());
		TypeNode t = visit(n.th);
		TypeNode e = visit(n.el);
		if (isSubtype(t, e)) return e;
		if (isSubtype(e, t)) return t;
		throw new TypeException("Incompatible types in then-else branches",n.getLine());
	}

	@Override
	public TypeNode visitNode(EqualNode n) throws TypeException {
		if (print) printNode(n);
		TypeNode l = visit(n.left);
		TypeNode r = visit(n.right);
		if ( !(isSubtype(l, r) || isSubtype(r, l)) )
			throw new TypeException("Incompatible types in equal",n.getLine());
		return new BoolTypeNode();
	}

	@Override
	public TypeNode visitNode(MinorEqualNode n) throws TypeException {
		if (print) printNode(n);
		TypeNode l = visit(n.left);
		TypeNode r = visit(n.right);
		if ( !(isSubtype(l, r) || isSubtype(r, l)) )
			throw new TypeException("Incompatible types in equal",n.getLine());
		return new BoolTypeNode();
	}

	@Override
	public TypeNode visitNode(GreaterEqualNode n) throws TypeException {
		if (print) printNode(n);
		TypeNode l = visit(n.left);
		TypeNode r = visit(n.right);
		if ( !(isSubtype(l, r) || isSubtype(r, l)) )
			throw new TypeException("Incompatible types in equal",n.getLine());
		return new BoolTypeNode();
	}

	@Override
	public TypeNode visitNode(TimesNode n) throws TypeException {
		if (print) printNode(n);
		if ( !(isSubtype(visit(n.left), new IntTypeNode())
				&& isSubtype(visit(n.right), new IntTypeNode())) )
			throw new TypeException("Non integers in multiplication",n.getLine());
		return new IntTypeNode();
	}

	@Override
	public TypeNode visitNode(DivisionNode n) throws TypeException {
		if (print) printNode(n);
		if ( !(isSubtype(visit(n.left), new IntTypeNode())
				&& isSubtype(visit(n.right), new IntTypeNode())) )
			throw new TypeException("Non integers in division",n.getLine());
		if(visit(n.right).getLine()==0){
			throw new TypeException("Non divisible by zero",n.getLine());
		}
		return new IntTypeNode();
	}

	@Override
	public TypeNode visitNode(PlusNode n) throws TypeException {
		if (print) printNode(n);
		if ( !(isSubtype(visit(n.left), new IntTypeNode())
				&& isSubtype(visit(n.right), new IntTypeNode())) )
			throw new TypeException("Non integers in sum",n.getLine());
		return new IntTypeNode();
	}

	@Override
	public TypeNode visitNode(MinusNode n) throws TypeException {
		if (print) printNode(n);
		if ( !(isSubtype(visit(n.left), new IntTypeNode())
				&& isSubtype(visit(n.right), new IntTypeNode())) )
			throw new TypeException("Non integers in sub",n.getLine());
		return new IntTypeNode();
	}

	/**
	 * Visit a NotNode node and check its type.
	 * Visit the expression and check that its type is boolean.
	 * Return boolean.
	 *
	 * @param node the NotNode node to visit
	 * @return boolean
	 * @throws TypeException if the type of the expression is not boolean
	 */
	@Override
	public TypeNode visitNode(final NotNode node) throws TypeException {
		if (print) printNode(node);
		if (!(isSubtype(visit(node.exp), new BoolTypeNode()))) {
			throw new TypeException("Non boolean in not", node.getLine());
		}
		return new BoolTypeNode();
	}

	/**
	 * Visit a OrNode node and check its type.
	 * Visit the left and right expressions and check that their types are boolean.
	 * Return boolean.
	 *
	 * @param node the OrNode node to visit
	 * @return boolean
	 * @throws TypeException if the types of the expressions are not boolean
	 */
	@Override
	public TypeNode visitNode(final OrNode node) throws TypeException {
		if (print) printNode(node);
		if (!(isSubtype(visit(node.left), new BoolTypeNode())
				&& isSubtype(visit(node.right), new BoolTypeNode()))) {
			throw new TypeException("Non booleans in or", node.getLine());
		}
		return new BoolTypeNode();
	}

	/**
	 * Visit a AndNode node and check its type.
	 * Visit the left and right expressions and check that their types are boolean.
	 * Return boolean.
	 *
	 * @param node the AndNode node to visit
	 * @return boolean
	 * @throws TypeException if the types of the expressions are not boolean
	 */
	@Override
	public TypeNode visitNode(final AndNode node) throws TypeException {
		if (print) printNode(node);
		if (!(isSubtype(visit(node.left), new BoolTypeNode())
				&& isSubtype(visit(node.right), new BoolTypeNode()))) {
			throw new TypeException("Non booleans in and", node.getLine());
		}
		return new BoolTypeNode();
	}

	@Override
	public TypeNode visitNode(CallNode n) throws TypeException {
		if (print) printNode(n,n.id);
		TypeNode t = visit(n.entry); 
		if ( !(t instanceof ArrowTypeNode) )
			throw new TypeException("Invocation of a non-function "+n.id,n.getLine());
		ArrowTypeNode at = (ArrowTypeNode) t;
		if ( !(at.parlist.size() == n.arglist.size()) )
			throw new TypeException("Wrong number of parameters in the invocation of "+n.id,n.getLine());
		for (int i = 0; i < n.arglist.size(); i++)
			if ( !(isSubtype(visit(n.arglist.get(i)),at.parlist.get(i))) )
				throw new TypeException("Wrong type for "+(i+1)+"-th parameter in the invocation of "+n.id,n.getLine());
		return at.ret;
	}

	@Override
	public TypeNode visitNode(IdNode n) throws TypeException {
		if (print) printNode(n,n.id);
		TypeNode t = visit(n.entry); 
		if (t instanceof ArrowTypeNode)
			throw new TypeException("Wrong usage of function identifier " + n.id,n.getLine());
		return t;
	}

	@Override
	public TypeNode visitNode(BoolNode n) {
		if (print) printNode(n,n.val.toString());
		return new BoolTypeNode();
	}

	@Override
	public TypeNode visitNode(IntNode n) {
		if (print) printNode(n,n.val.toString());
		return new IntTypeNode();
	}

// gestione tipi incompleti	(se lo sono lancia eccezione)
	
	@Override
	public TypeNode visitNode(ArrowTypeNode n) throws TypeException {
		if (print) printNode(n);
		for (Node par: n.parlist) visit(par);
		visit(n.ret,"->"); //marks return type
		return null;
	}

	@Override
	public TypeNode visitNode(BoolTypeNode n) {
		if (print) printNode(n);
		return null;
	}

	@Override
	public TypeNode visitNode(IntTypeNode n) {
		if (print) printNode(n);
		return null;
	}

	public TypeNode visitNode(ClassNode n) throws TypeException{
		if (print) printNode(n);
		TypeRels.superType.put(n.classID,n.superID);

		// confronta suo tipo ClassTypeNode in campo "type"
		//con quello del genitore in campo "superEntry" per
		//controllare che eventuali overriding siano corretti
		//• scorre tipi in array allFields/allMethods del genitore e
		//controlla che il tipo alla stessa posizione nel proprio array
		//allFields/allMethods sia sottotipo

		if (n.superID !=null ) {

			ClassTypeNode parentCT = ((ClassTypeNode) n.superEntry.type);

			//fields
			for (int i = 0; i < n.attributes.size(); i++) {

				//offset del campo corrente (in modo che sia positivo)
				int offset = -n.attributes.get(i).offset - 1;

				//se l'offset ottenuto è di un campo ereditato e il fieldNode è stato inizializzato correttamente
				if(offset <  parentCT.attributes.size() && offset >= 0) {
					if (!isSubtype(n.classType.attributes.get(offset), parentCT.attributes.get(offset))) {
						throw new TypeException("Wrong type for " + (i + 1) + "-th field in the inheritance of " + n.classID, n.getLine());
					}
				}
			}

			//methods
			for (int i = 0; i < n.functions.size(); i++) {//per ogni metodo scritto nella classe (non ereditato)
				int offset = n.functions.get(i).offset;//offset del metodo corrente

				if(offset < parentCT.functions.size() && offset >= 0) {
					if (!isSubtype(n.classType.functions.get(offset), parentCT.functions.get(offset))) {
						throw new TypeException("Wrong type for " + (i + 1) + "-th method in the inheritance of " + n.classID, n.getLine());
					}
				}
			}

		}

		for (Node method : n.functions) {
			try {
				visit(method);
			} catch (IncomplException e) {
			} catch (TypeException e) {
				System.out.println("Type checking error in a declaration: " + e.text);
			}
		}

		return null;
	}

	@Override
	public TypeNode visitNode(EmptyNode n) throws TypeException {
		if (print) printNode(n);
		return new EmptyTypeNode();
	}

	@Override
	public TypeNode visitNode(EmptyTypeNode n) throws TypeException {
		if (print) printNode(n);
		return null;
	}

	@Override
	public TypeNode visitNode(NewNode n) throws TypeException {
		if (print) printNode(n, n.classID);
		TypeNode t =visit(n.classEntry);
		if ( t instanceof ClassTypeNode) { //controllo che sia una classe
			if (!(((ClassTypeNode) t).attributes.size() == n.arglist.size()))
				throw new TypeException("Wrong number of parameters in the new class invocation of " + n.classID, n.getLine());
			for (int i = 0; i < n.arglist.size(); i++) {
				if (!(isSubtype(visit(n.arglist.get(i)), ((ClassTypeNode) t).attributes.get(i))))
					throw new TypeException("Wrong type for " + (i + 1) + "-th parameter in the new class invocation of " + n.classID, n.getLine());
			}
			return new RefTypeNode(n.classID);
		} else {
			throw new TypeException("Invocation of a non-class ",n.getLine());
		}
	}

	@Override
	public TypeNode visitNode(ClassCallNode n) throws TypeException {
		String classCall = n.objectID +"."+n.methodID;
		if (print) printNode(n, classCall);
		TypeNode t = visit(n.methodEntry);//tipo del metodo
		if ( t instanceof  ClassFunctionTypeNode) {//controllo che sia un metodo
			ArrowTypeNode at = ((ClassFunctionTypeNode) t).fun;//se è un metodo estraggo arrowtype
			if ( !(at.parlist.size() == n.arglist.size()) )
				throw new TypeException("Wrong number of parameters in the class invocation of "+classCall,n.getLine());
			for (int i = 0; i < n.arglist.size(); i++)
				if ( !(isSubtype(visit(n.arglist.get(i)),at.parlist.get(i))) )
					throw new TypeException("Wrong type for "+(i+1)+"-th parameter in the class invocation of "+classCall,n.getLine());
			return at.ret;
		} else {//se non è un metodo
			throw new TypeException("Invocation of a non-method ",n.getLine());
		}
	}

	@Override
	public TypeNode visitSTentry(STentry entry) throws TypeException {
		if (print) printSTentry("type");
		return ckvisit(entry.type); 
	}

}