package compiler;

import compiler.lib.BaseASTVisitor;
import compiler.lib.DecNode;
import compiler.lib.Node;
import compiler.lib.TypeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AST {

    public static class ProgLetInNode extends Node {
        final List<DecNode> declist;
        final Node exp;

        ProgLetInNode(List<DecNode> d, Node e) {
            this.declist = Collections.unmodifiableList(d);
            this.exp = e;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class ProgNode extends Node {
        final Node exp;

        ProgNode(Node e) {
            this.exp = e;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class FunNode extends DecNode {
        final String id;
        final TypeNode retType;
        final List<ParNode> parlist;
        final List<DecNode> declist;
        final Node exp;

        FunNode(String i, TypeNode rt, List<ParNode> pl, List<DecNode> dl, Node e) {
            this.id = i;
            this.retType = rt;
            this.parlist = Collections.unmodifiableList(pl);
            this.declist = Collections.unmodifiableList(dl);
            this.exp = e;
        }

        //void setType(TypeNode t) {type = t;}

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class ParNode extends DecNode {
        final String id;

        ParNode(String i, TypeNode t) {
            this.id = i;
            this.type = t;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class VarNode extends DecNode {
        final String id;
        final Node exp;

        VarNode(String i, TypeNode t, Node v) {
            this.id = i;
            this.type = t;
            this.exp = v;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class PrintNode extends Node {
        final Node exp;

        PrintNode(Node e) {
            this.exp = e;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class IfNode extends Node {
        final Node cond;
        final Node th;
        final Node el;

        IfNode(Node c, Node t, Node e) {
            this.cond = c;
            this.th = t;
            this.el = e;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class EqualNode extends Node {
        final Node left;
        final Node right;

        EqualNode(Node l, Node r) {
            this.left = l;
            this.right = r;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class TimesNode extends Node {
        final Node left;
        final Node right;

        TimesNode(Node l, Node r) {
            this.left = l;
            this.right = r;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class PlusNode extends Node {
        final Node left;
        final Node right;

        PlusNode(Node l, Node r) {
            this.left = l;
            this.right = r;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class CallNode extends Node {
        final String id;
        final List<Node> arglist;
        STentry entry;
        int nl;

        CallNode(String i, List<Node> p) {
            this.id = i;
            this.arglist = Collections.unmodifiableList(p);
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class IdNode extends Node {
        final String id;
        STentry entry;
        int nl;

        IdNode(String i) {
            this.id = i;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class BoolNode extends Node {
        final Boolean val;

        BoolNode(boolean n) {
            this.val = n;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class IntNode extends Node {
        final Integer val;

        IntNode(Integer n) {
            this.val = n;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    // New node -, /, <=, >=, or(||), and(&&), not(!)
    public static class MinusNode extends Node {
        final Node left;
        final Node right;

        MinusNode(Node l, Node r) {
            this.left = l;
            this.right = r;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class DivNode extends Node {
        final Node left;
        final Node right;

        DivNode(Node l, Node r) {
            this.left = l;
            this.right = r;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class LessEqualNode extends Node {
        final Node left;
        final Node right;

        LessEqualNode(Node l, Node r) {
            this.left = l;
            this.right = r;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class GreaterEqualNode extends Node {
        final Node left;
        final Node right;

        GreaterEqualNode(Node l, Node r) {
            this.left = l;
            this.right = r;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class OrNode extends Node {
        final Node left;
        final Node right;

        OrNode(Node l, Node r) {
            this.left = l;
            this.right = r;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class AndNode extends Node {
        final Node left;
        final Node right;

        AndNode(Node l, Node r) {
            this.left = l;
            this.right = r;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class NotNode extends Node {
        final Node exp;

        NotNode(Node e) {
            this.exp = e;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    // OO nodes
    public static class ClassNode extends DecNode {
        final String classId;
        final List<FieldNode> declist;
        final List<FunNode> metlist;

        final Optional<String> parentId;
        STentry parentEntry;

        ClassNode(String classId, final Optional<String> parentId, List<FieldNode> dl, List<FunNode> ml) {
            this.classId = classId;
            this.parentId = parentId;
            this.declist = Collections.unmodifiableList(dl);
            this.metlist = Collections.unmodifiableList(ml);
        }

        public void setTypeNode(TypeNode type) {
            this.type = type;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class FieldNode extends ParNode {
        int offset;

        FieldNode(String i, TypeNode t) {
            super(i, t);
            this.offset = -1;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class MethodNode extends FunNode {
        int offset;

        MethodNode(String i, TypeNode rt, List<ParNode> pl, List<DecNode> dl, Node e) {
            super(i, rt, pl, dl, e);
            this.offset = -1;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class ClassCallNode extends CallNode {
        final String objectId;
        STentry objectEntry;
        int nestingLevel;

        ClassCallNode(final String objId, final String methodId, final List<Node> args) {
            super(methodId, args);
            this.objectId = objId;
            this.nestingLevel = 0;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class NewNode extends Node {
        final String id;
        final List<Node> args;
        STentry entry;

        NewNode(String i, final List<Node> args) {
            this.id = i;
            this.args = Collections.unmodifiableList(args);
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class EmptyNode extends Node {
        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }


    // Type nodes
    public static class ArrowTypeNode extends TypeNode {
        final List<TypeNode> parlist;
        final TypeNode ret;

        ArrowTypeNode(List<TypeNode> p, TypeNode r) {
            this.parlist = Collections.unmodifiableList(p);
            this.ret = r;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class BoolTypeNode extends TypeNode {
        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class IntTypeNode extends TypeNode {
        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    // type nodes for OO
    public static class ClassTypeNode extends TypeNode {
        final List<TypeNode> fields;
        final List<MethodTypeNode> methods;

        ClassTypeNode(final List<TypeNode> f, final List<MethodTypeNode> m) {
            this.fields = Collections.unmodifiableList(f);
            this.methods = Collections.unmodifiableList(m);
        }

        ClassTypeNode(final ClassTypeNode parent) {
            this(parent.fields, parent.methods);
        }

        ClassTypeNode() {
            this(new ArrayList<>(), new ArrayList<>());
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class MethodTypeNode extends ArrowTypeNode {
        MethodTypeNode(List<TypeNode> p, TypeNode r) {
            super(p, r);
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class RefTypeNode extends TypeNode {
        final String typeId;

        RefTypeNode(String id) {
            this.typeId = id;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class EmptyTypeNode extends TypeNode {
        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

}