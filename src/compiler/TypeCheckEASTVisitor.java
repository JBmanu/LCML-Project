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
public class TypeCheckEASTVisitor extends BaseEASTVisitor<TypeNode, TypeException> {

    public TypeCheckEASTVisitor() {
        super(true);
    } // enables incomplete tree exceptions

    TypeCheckEASTVisitor(boolean debug) {
        super(true, debug);
    } // enables print for debugging

    //checks that a type object is visitable (not incomplete)
    private TypeNode ckvisit(TypeNode t) throws TypeException {
        visit(t);
        return t;
    }

    @Override
    public TypeNode visitNode(ProgLetInNode node) throws TypeException {
        if (print) printNode(node);
        for (Node dec : node.declist)
            try {
                visit(dec);
            } catch (IncomplException e) {
            } catch (TypeException e) {
                System.out.println("Type checking error in a declaration: " + e.text);
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
        for (Node dec : node.declarations)
            try {
                visit(dec);
            } catch (IncomplException e) {
            } catch (TypeException e) {
                System.out.println("Type checking error in a declaration: " + e.text);
            }
        if (!isSubtype(visit(node.exp), ckvisit(node.returnType)))
            throw new TypeException("Wrong return type for function " + node.id, node.getLine());
        return null;
    }

    @Override
    public TypeNode visitNode(VarNode node) throws TypeException {
        if (print) printNode(node, node.id);
        if (!isSubtype(visit(node.exp), ckvisit(node.getType())))
            throw new TypeException("Incompatible value for variable " + node.id, node.getLine());
        return null;
    }

    @Override
    public TypeNode visitNode(PrintNode node) throws TypeException {
        if (print) printNode(node);
        return visit(node.exp);
    }

    @Override
    public TypeNode visitNode(IfNode node) throws TypeException {
        if (print) printNode(node);
        if (!(isSubtype(visit(node.cond), new BoolTypeNode())))
            throw new TypeException("Non boolean condition in if", node.getLine());
        TypeNode t = visit(node.th);
        TypeNode e = visit(node.el);
        if (isSubtype(t, e)) return e;
        if (isSubtype(e, t)) return t;
        throw new TypeException("Incompatible types in then-else branches", node.getLine());
    }

    @Override
    public TypeNode visitNode(EqualNode node) throws TypeException {
        if (print) printNode(node);
        TypeNode l = visit(node.left);
        TypeNode r = visit(node.right);
        if (!(isSubtype(l, r) || isSubtype(r, l)))
            throw new TypeException("Incompatible types in equal", node.getLine());
        return new BoolTypeNode();
    }

    @Override
    public TypeNode visitNode(TimesNode node) throws TypeException {
        if (print) printNode(node);
        if (!(isSubtype(visit(node.left), new IntTypeNode())
                && isSubtype(visit(node.right), new IntTypeNode())))
            throw new TypeException("Non integers in multiplication", node.getLine());
        return new IntTypeNode();
    }

    @Override
    public TypeNode visitNode(PlusNode node) throws TypeException {
        if (print) printNode(node);
        if (!(isSubtype(visit(node.left), new IntTypeNode())
                && isSubtype(visit(node.right), new IntTypeNode())))
            throw new TypeException("Non integers in sum", node.getLine());
        return new IntTypeNode();
    }

    @Override
    public TypeNode visitNode(CallNode node) throws TypeException {
        if (print) printNode(node, node.id);
        TypeNode t = visit(node.entry);
        if (!(t instanceof ArrowTypeNode))
            throw new TypeException("Invocation of a non-function " + node.id, node.getLine());
        ArrowTypeNode at = (ArrowTypeNode) t;
        if (!(at.parameters.size() == node.arglist.size()))
            throw new TypeException("Wrong number of parameters in the invocation of " + node.id, node.getLine());
        for (int i = 0; i < node.arglist.size(); i++)
            if (!(isSubtype(visit(node.arglist.get(i)), at.parameters.get(i))))
                throw new TypeException("Wrong type for " + (i + 1) + "-th parameter in the invocation of " + node.id, node.getLine());
        return at.returnType;
    }

    @Override
    public TypeNode visitNode(IdNode node) throws TypeException {
        if (print) printNode(node, node.id);
        TypeNode t = visit(node.entry);
        if (t instanceof ArrowTypeNode)
            throw new TypeException("Wrong usage of function identifier " + node.id, node.getLine());
        return t;
    }

    @Override
    public TypeNode visitNode(BoolNode node) {
        if (print) printNode(node, node.val.toString());
        return new BoolTypeNode();
    }

    @Override
    public TypeNode visitNode(IntNode node) {
        if (print) printNode(node, node.val.toString());
        return new IntTypeNode();
    }

// gestione tipi incompleti	(se lo sono lancia eccezione)

    @Override
    public TypeNode visitNode(ArrowTypeNode node) throws TypeException {
        if (print) printNode(node);
        for (Node par : node.parameters) visit(par);
        visit(node.returnType, "->"); //marks return type
        return null;
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

// STentry (ritorna campo type)

    @Override
    public TypeNode visitSTentry(STentry entry) throws TypeException {
        if (print) printSTentry("type");
        return ckvisit(entry.type);
    }

    // new nodes
    @Override
    public TypeNode visitNode(MinusNode node) throws TypeException {
        if (print) printNode(node);
        if (!(isSubtype(visit(node.left), new IntTypeNode())
                && isSubtype(visit(node.right), new IntTypeNode())))
            throw new TypeException("Non integers in sum", node.getLine());
        return new IntTypeNode();
    }

    @Override
    public TypeNode visitNode(DivNode node) throws TypeException {
        if (print) printNode(node);
        if (!(isSubtype(visit(node.left), new IntTypeNode())
                && isSubtype(visit(node.right), new IntTypeNode())))
            throw new TypeException("Non integers in div", node.getLine());
        return new IntTypeNode();
    }

    @Override
    public TypeNode visitNode(GreaterEqualNode node) throws TypeException {
        if (print) printNode(node);
        TypeNode l = visit(node.left);
        TypeNode r = visit(node.right);
        if (!(isSubtype(visit(node.left), new IntTypeNode())
                && isSubtype(visit(node.right), new IntTypeNode())))
            throw new TypeException("Incompatible types in greaterEqual", node.getLine());
        return new BoolTypeNode();
    }

    @Override
    public TypeNode visitNode(LessEqualNode node) throws TypeException {
        if (print) printNode(node);
        TypeNode l = visit(node.left);
        TypeNode r = visit(node.right);
        if (!(isSubtype(visit(node.left), new IntTypeNode())
                && isSubtype(visit(node.right), new IntTypeNode())))
            throw new TypeException("Incompatible types in lessEqual", node.getLine());
        return new BoolTypeNode();
    }

    @Override
    public TypeNode visitNode(OrNode node) throws TypeException {
        if (print) printNode(node);
        if (!(isSubtype(visit(node.left), new BoolTypeNode())
                && isSubtype(visit(node.right), new BoolTypeNode())))
            throw new TypeException("Non booleans in or", node.getLine());
        return new BoolTypeNode();
    }

    @Override
    public TypeNode visitNode(AndNode node) throws TypeException {
        if (print) printNode(node);
        if (!(isSubtype(visit(node.left), new BoolTypeNode())
                && isSubtype(visit(node.right), new BoolTypeNode())))
            throw new TypeException("Non booleans in and", node.getLine());
        return new BoolTypeNode();
    }

    @Override
    public TypeNode visitNode(NotNode node) throws TypeException {
        if (print) printNode(node);
        if (!(isSubtype(visit(node.exp), new BoolTypeNode())))
            throw new TypeException("Non boolean in not", node.getLine());
        return new BoolTypeNode();
    }

    // OO Nodes
    @Override
    public TypeNode visitNode(ClassNode node) throws TypeException {
        if (this.print) printNode(node, node.classId);
        final boolean isSubClass = node.superId.isPresent();
        final String parent = isSubClass ? node.superId.get() : null;

        if (isSubClass) superType.put(node.classId, parent);

        for (final MethodNode method : node.methods) {
            try {
                visit(method);
            } catch (TypeException e) {
                System.out.println("Type checking error in a class declaration: " + e.text);
            }
        }

        if (!isSubClass || node.superEntry == null) return null;

        final ClassTypeNode classType = (ClassTypeNode) node.getType();
        final ClassTypeNode superClassType = (ClassTypeNode) node.superEntry.type;

        for (final FieldNode field : node.fields) {
            int position = -field.offset - 1;
            final boolean isOverriding = position < superClassType.fields.size();
            if (isOverriding && !isSubtype(classType.fields.get(position), superClassType.fields.get(position))) {
                throw new TypeException("Wrong type for field " + field.id, field.getLine());
            }
        }

        for (final MethodNode method : node.methods) {
            int position = method.offset;
            final boolean isOverriding = position < superClassType.fields.size();
            if (isOverriding && !isSubtype(classType.methods.get(position), superClassType.methods.get(position))) {
                throw new TypeException("Wrong type for method " + method.id, method.getLine());
            }
        }

        return null;
    }

    @Override
    public TypeNode visitNode(final MethodNode node) throws TypeException {
        if (print) printNode(node, node.id);

        for (final DecNode declaration : node.declarations) {
            try {
                visit(declaration);
            } catch (TypeException e) {
                System.out.println("Type checking error in a method declaration: " + e.text);
            }
        }
        // visit expression and check if it is a subtype of the return type
        if (!isSubtype(visit(node.exp), ckvisit(node.returnType))) {
            throw new TypeException("Wrong return type for method " + node.id, node.getLine());
        }

        return null;
    }

    @Override
    public TypeNode visitNode(final EmptyNode node) {
        if (print) printNode(node);
        return new EmptyTypeNode();
    }

    @Override
    public TypeNode visitNode(final ClassCallNode node) throws TypeException {
        if (print) printNode(node, node.objectId);

        TypeNode type = visit(node.methodEntry);

        // visit method, if it is a method type, get the functional type
        if (type instanceof MethodTypeNode methodTypeNode) {
            type = methodTypeNode.functionalType;
        }

        // if it is not an arrow type, throw an exception
        if (!(type instanceof ArrowTypeNode arrowTypeNode)) {
            throw new TypeException("Invocation of a non-function " + node.methodId, node.getLine());
        }

        // check if the number of parameters is correct
        if (arrowTypeNode.parameters.size() != node.args.size()) {
            throw new TypeException("Wrong number of parameters in the invocation of method " + node.methodId, node.getLine());
        }

        // check if the types of the parameters are correct
        for (int i = 0; i < node.args.size(); i++) {
            if (!(isSubtype(visit(node.args.get(i)), arrowTypeNode.parameters.get(i)))) {
                throw new TypeException("Wrong type for " + (i + 1) + "-th parameter in the invocation of method " + node.methodId, node.getLine());
            }
        }

        return arrowTypeNode.returnType;
    }

    @Override
    public TypeNode visitNode(final NewNode node) throws TypeException {
        if (print) printNode(node, node.classId);
        final TypeNode typeNode = visit(node.entry);

        if (!(typeNode instanceof ClassTypeNode classTypeNode)) {
            throw new TypeException("Invocation of a non-constructor " + node.classId, node.getLine());
        }

        if (classTypeNode.fields.size() != node.args.size()) {
            throw new TypeException("Wrong number of parameters in the invocation of constructor " + node.classId, node.getLine());
        }
        // check if the types of the parameters are correct
        for (int i = 0; i < node.args.size(); i++) {
            if (!(isSubtype(visit(node.args.get(i)), classTypeNode.fields.get(i)))) {
                throw new TypeException("Wrong type for " + (i + 1) + "-th parameter in the invocation of constructor " + node.classId, node.getLine());
            }
        }
        return new RefTypeNode(node.classId);
    }

    // OO Type Nodes
    @Override
    public TypeNode visitNode(final ClassTypeNode node) throws TypeException {
        if (print) printNode(node);
        // Visit all fields and methods
        for (final TypeNode field : node.fields) visit(field);
        for (final ArrowTypeNode method : node.methods) visit(method);
        return null;
    }

    @Override
    public TypeNode visitNode(final MethodTypeNode node) throws TypeException {
        if (print) printNode(node);
        // Visit all parameters and the return type
        for (final TypeNode parameter : node.functionalType.parameters) visit(parameter);
        visit(node.functionalType.returnType, "->");
        return null;
    }

    @Override
    public TypeNode visitNode(final RefTypeNode node) {
        if (print) printNode(node);
        return null;
    }

    @Override
    public TypeNode visitNode(final EmptyTypeNode node) {
        if (print) printNode(node);
        return null;
    }
}