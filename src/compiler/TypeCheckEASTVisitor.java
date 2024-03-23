package compiler;

import compiler.AST.*;
import compiler.exc.IncomplException;
import compiler.exc.TypeException;
import compiler.lib.BaseEASTVisitor;
import compiler.lib.DecNode;
import compiler.lib.Node;
import compiler.lib.TypeNode;

import static compiler.TypeRels.*;

public class TypeCheckEASTVisitor extends BaseEASTVisitor<TypeNode, TypeException> {

	public TypeCheckEASTVisitor(boolean incompleteExc, boolean debug) {
		super(incompleteExc, debug);
	}

	public TypeCheckEASTVisitor(boolean debug) {
		this(true, debug);
	}

	public TypeCheckEASTVisitor() {
		this(true, false);
	}

	private TypeNode ckvisit(TypeNode t) throws TypeException {
		visit(t);
		return t;
	}

	@Override
	public TypeNode visitSTentry(STentry entry) throws TypeException {
		if (print) printSTentry("type");
		return ckvisit(entry.type);
	}

	@Override
	public TypeNode visitNode(ProgLetInNode node) throws TypeException {
		if (print) printNode(node);
		for (DecNode declaration : node.declist) {
			try {
				visit(declaration);
			} catch (IncomplException ignored) {
			} catch (TypeException e) {
				System.out.println("Type checking error in a declaration: " + e.text);
			}
		}
		return visit(node.exp);
	}

	@Override
	public TypeNode visitNode(ProgNode node) throws TypeException {
		if (print) printNode(node);
		return visit(node.exp);
	}

	@Override
	public TypeNode visitNode(FunNode node) throws TypeException {
		if (print) printNode(node, node.id);
		for (Node declaration : node.declist) {
			try {
				visit(declaration);
			} catch (IncomplException ignored) {
			} catch (TypeException e) {
				System.out.println("Type checking error in a declaration: " + e.text);
			}
		}
		if (!isSubtype(visit(node.exp), ckvisit(node.retType))) {
			throw new TypeException("Wrong return type for function " + node.id, node.getLine());
		}
		return null;
	}

	@Override
	public TypeNode visitNode(VarNode node) throws TypeException {
		if (print) printNode(node, node.id);
		if (!isSubtype(visit(node.exp), ckvisit(node.getType()))) {
			throw new TypeException("Incompatible value for variable " + node.id, node.getLine());
		}
		return null;
	}

	@Override
	public TypeNode visitNode(IfNode node) throws TypeException {
		if (print) printNode(node);
		if (!(isSubtype(visit(node.cond), new BoolTypeNode()))) {
			throw new TypeException("Non boolean condition in if", node.getLine());
		}

		TypeNode thenBranch = visit(node.th);
		TypeNode elseBranch = visit(node.el);

		TypeNode returnType = lowestCommonAncestor(thenBranch, elseBranch);
		if (returnType == null) {
			throw new TypeException("Incompatible types in then-else branches", node.getLine());
		}

		return returnType;
	}

	@Override
	public TypeNode visitNode(NotNode node) throws TypeException {
		if (print) printNode(node);
		if (!(isSubtype(visit(node.exp), new BoolTypeNode()))) {
			throw new TypeException("Non boolean in not", node.getLine());
		}
		return new BoolTypeNode();
	}

	@Override
	public TypeNode visitNode(OrNode node) throws TypeException {
		if (print) printNode(node);
		if (!(isSubtype(visit(node.left), new BoolTypeNode())
				&& isSubtype(visit(node.right), new BoolTypeNode()))) {
			throw new TypeException("Non booleans in or", node.getLine());
		}
		return new BoolTypeNode();
	}

	@Override
	public TypeNode visitNode(AndNode node) throws TypeException {
		if (print) printNode(node);
		if (!(isSubtype(visit(node.left), new BoolTypeNode())
				&& isSubtype(visit(node.right), new BoolTypeNode()))) {
			throw new TypeException("Non booleans in and", node.getLine());
		}
		return new BoolTypeNode();
	}

	@Override
	public TypeNode visitNode(EqualNode node) throws TypeException {
		if (print) printNode(node);
		TypeNode left = visit(node.left);
		TypeNode right = visit(node.right);
		if (!(isSubtype(left, right) || isSubtype(right, left))) {
			throw new TypeException("Incompatible types in equal", node.getLine());
		}
		return new BoolTypeNode();
	}

	@Override
	public TypeNode visitNode(LessEqualNode node) throws TypeException {
		if (print) printNode(node);
		if (!(isSubtype(visit(node.left), new IntTypeNode())
				&& isSubtype(visit(node.right), new IntTypeNode()))) {
			throw new TypeException("Non integers in Lte", node.getLine());
		}
		return new BoolTypeNode();
	}

	@Override
	public TypeNode visitNode(GreaterEqualNode node) throws TypeException {
		if (print) printNode(node);
		if (!(isSubtype(visit(node.left), new IntTypeNode())
				&& isSubtype(visit(node.right), new IntTypeNode()))) {
			throw new TypeException("Non integers in Gte", node.getLine());
		}
		return new BoolTypeNode();
	}

	@Override
	public TypeNode visitNode(TimesNode node) throws TypeException {
		if (print) printNode(node);
		if (!(isSubtype(visit(node.left), new IntTypeNode())
				&& isSubtype(visit(node.right), new IntTypeNode()))) {
			throw new TypeException("Non integers in multiplication", node.getLine());
		}
		return new IntTypeNode();
	}

	@Override
	public TypeNode visitNode(DivNode node) throws TypeException {
		if (print) printNode(node);
		if (!(isSubtype(visit(node.left), new IntTypeNode())
				&& isSubtype(visit(node.right), new IntTypeNode()))) {
			throw new TypeException("Non integers in Div", node.getLine());
		}
		return new IntTypeNode();
	}

	@Override
	public TypeNode visitNode(PlusNode node) throws TypeException {
		if (print) printNode(node);
		if (!(isSubtype(visit(node.left), new IntTypeNode())
				&& isSubtype(visit(node.right), new IntTypeNode()))) {
			throw new TypeException("Non integers in sum", node.getLine());
		}
		return new IntTypeNode();
	}

	@Override
	public TypeNode visitNode(MinusNode node) throws TypeException {
		if (print) printNode(node);
		if (!(isSubtype(visit(node.left), new IntTypeNode())
				&& isSubtype(visit(node.right), new IntTypeNode()))) {
			throw new TypeException("Non integers in minus", node.getLine());
		}
		return new IntTypeNode();
	}

	@Override
	public TypeNode visitNode(IdNode node) throws TypeException {
		if (print) printNode(node, node.id);
		TypeNode typeNode = visit(node.entry);
		if (typeNode instanceof ArrowTypeNode) {
			throw new TypeException("Wrong usage of function identifier " + node.id, node.getLine());
		}
		return typeNode;
	}

	@Override
	public TypeNode visitNode(BoolNode node) {
		if (print) printNode(node, String.valueOf(node.val));
		return new BoolTypeNode();
	}

	@Override
	public TypeNode visitNode(IntNode node) {
		if (print) printNode(node, node.val.toString());
		return new IntTypeNode();
	}

	@Override
	public TypeNode visitNode(BoolTypeNode node) {
		if (print) printNode(node);
		return null;
	}

	@Override
	public TypeNode visitNode(IntTypeNode node) {
		if (print) printNode(node);
		return null;
	}

	@Override
	public TypeNode visitNode(ArrowTypeNode node) throws TypeException {
		if (print) printNode(node);
		for (TypeNode parameter : node.parlist) {
			visit(parameter);
		}
		visit(node.ret, "->"); //marks return type
		return null;
	}

	@Override
	public TypeNode visitNode(PrintNode node) throws TypeException {
		if (print) printNode(node);
		return visit(node.exp);
	}

	@Override
	public TypeNode visitNode(CallNode node) throws TypeException {
		if (print) printNode(node, node.id);
		TypeNode typeNode = visit(node.entry);

		if (typeNode instanceof MethodTypeNode methodTypeNode) {
			typeNode = methodTypeNode.arrowType;
		}

		if (!(typeNode instanceof ArrowTypeNode arrowTypeNode)) {
			throw new TypeException("Invocation of a non-function " + node.id, node.getLine());
		}

		if (!(arrowTypeNode.parlist.size() == node.arglist.size())) {
			throw new TypeException("Wrong number of parameters in the invocation of " + node.id, node.getLine());
		}

		for (int i = 0; i < node.arglist.size(); i++) {
			if (!(isSubtype(visit(node.arglist.get(i)), arrowTypeNode.parlist.get(i)))) {
				throw new TypeException("Wrong type for " + (i + 1) + "-th parameter in the invocation of " + node.id, node.getLine());
			}
		}
		return arrowTypeNode.ret;
	}

	@Override
	public TypeNode visitNode(ClassNode node) throws TypeException {
		if (print) printNode(node, node.id);
		boolean isSubClass = node.superId.isPresent();
		String superId = isSubClass ? node.superId.get() : null;

		// if class has a super class, add it as super type in TypeRels Map
		if (isSubClass) {
			superType.put(node.id, superId);
		}

		// visit all methods
		for (MethodNode method : node.methodList) {
			try {
				visit(method);
			} catch (TypeException e) {
				System.out.println("Type checking error in a class declaration: " + e.text);
			}
		}

		if (!isSubClass || node.superEntry == null) {
			return null;
		}

		ClassTypeNode classType = (ClassTypeNode) node.getType();
		ClassTypeNode parentClassType = (ClassTypeNode) node.superEntry.type;

		// check if all fields and methods of the class are the correct subtypes and with the correct position
		for (FieldNode field : node.fieldList) {
			int position = -field.offset - 1;
			boolean isOverriding = position < parentClassType.fieldList.size();
			if (isOverriding && !isSubtype(classType.fieldList.get(position), parentClassType.fieldList.get(position))) {
				throw new TypeException("Wrong type for field " + field.id, field.getLine());
			}
		}

		for (MethodNode method : node.methodList) {
			int position = method.offset;
			boolean isOverriding = position < parentClassType.fieldList.size();
			if (isOverriding && !isSubtype(classType.methodList.get(position), parentClassType.methodList.get(position))) {
				throw new TypeException("Wrong type for method " + method.id, method.getLine());
			}
		}

		return null;
	}

	@Override
	public TypeNode visitNode(MethodNode node) throws TypeException {
		if (print) printNode(node, node.id);

		for (DecNode declaration : node.declist) {
			try {
				visit(declaration);
			} catch (TypeException e) {
				System.out.println("Type checking error in a method declaration: " + e.text);
			}
		}
		// visit expression and check if it is a subtype of the return type
		if (!isSubtype(visit(node.exp), ckvisit(node.ret))) {
			throw new TypeException("Wrong return type for method " + node.id, node.getLine());
		}

		return null;
	}

	@Override
	public TypeNode visitNode(EmptyNode node) {
		if (print) printNode(node);
		return new EmptyTypeNode();
	}

	@Override
	public TypeNode visitNode(ClassCallNode node) throws TypeException {
		if (print) printNode(node, node.objectId);

		TypeNode type = visit(node.methodEntry);

		// visit method, if it is a method type, get the functional type
		if (type instanceof MethodTypeNode methodTypeNode) {
			type = methodTypeNode.arrowType;
		}

		// if it is not an arrow type, throw an exception
		if (!(type instanceof ArrowTypeNode arrowTypeNode)) {
			throw new TypeException("Invocation of a non-function " + node.methodId, node.getLine());
		}

		// check if the number of parameters is correct
		if (arrowTypeNode.parlist.size() != node.argList.size()) {
			throw new TypeException("Wrong number of parameters in the invocation of method " + node.methodId, node.getLine());
		}

		// check if the types of the parameters are correct
		for (int i = 0; i < node.argList.size(); i++) {
			if (!(isSubtype(visit(node.argList.get(i)), arrowTypeNode.parlist.get(i)))) {
				throw new TypeException("Wrong type for " + (i + 1) + "-th parameter in the invocation of method " + node.methodId, node.getLine());
			}
		}

		return arrowTypeNode.ret;
	}

	@Override
	public TypeNode visitNode(NewNode node) throws TypeException {
		if (print) printNode(node, node.id);
		TypeNode typeNode = visit(node.entry);

		if (!(typeNode instanceof ClassTypeNode classTypeNode)) {
			throw new TypeException("Invocation of a non-constructor " + node.id, node.getLine());
		}

		if (classTypeNode.fieldList.size() != node.arglist.size()) {
			throw new TypeException("Wrong number of parameters in the invocation of constructor " + node.id, node.getLine());
		}
		// check if the types of the parameters are correct
		for (int i = 0; i < node.arglist.size(); i++) {
			if (!(isSubtype(visit(node.arglist.get(i)), classTypeNode.fieldList.get(i)))) {
				throw new TypeException("Wrong type for " + (i + 1) + "-th parameter in the invocation of constructor " + node.id, node.getLine());
			}
		}
		return new RefTypeNode(node.id);
	}

	@Override
	public TypeNode visitNode(ClassTypeNode node) throws TypeException {
		if (print) printNode(node);
		// Visit all fields and methods
		for (TypeNode field : node.fieldList) visit(field);
		for (ArrowTypeNode method : node.methodList) visit(method);
		return null;
	}

	@Override
	public TypeNode visitNode(MethodTypeNode node) throws TypeException {
		if (print) printNode(node);
		// Visit all parameters and the return type
		for (TypeNode parameter : node.arrowType.parlist) visit(parameter);
		visit(node.arrowType.ret, "->");
		return null;
	}

	@Override
	public TypeNode visitNode(RefTypeNode node) {
		if (print) printNode(node);
		return null;
	}

	@Override
	public TypeNode visitNode(EmptyTypeNode node) {
		if (print) printNode(node);
		return null;
	}

}