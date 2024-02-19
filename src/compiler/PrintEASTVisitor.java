package src.compiler;

import src.compiler.AST.*;
import src.compiler.exc.VoidException;
import src.compiler.lib.BaseEASTVisitor;
import src.compiler.lib.Node;

public class PrintEASTVisitor extends BaseEASTVisitor<Void, VoidException> {

    PrintEASTVisitor() {
        super(false, true);
    }

    @Override
    public Void visitNode(ProgLetInNode n) {
        this.printNode(n);
        for (Node dec : n.declist) this.visit(dec);
        this.visit(n.exp);
        return null;
    }

    @Override
    public Void visitNode(ProgNode n) {
        this.printNode(n);
        this.visit(n.exp);
        return null;
    }

    @Override
    public Void visitNode(FunNode n) {
        this.printNode(n, n.id);
        this.visit(n.retType);
        for (ParNode par : n.parlist) this.visit(par);
        for (Node dec : n.declist) this.visit(dec);
        this.visit(n.exp);
        return null;
    }

    @Override
    public Void visitNode(ParNode n) {
        this.printNode(n, n.id);
        this.visit(n.getType());
        return null;
    }

    @Override
    public Void visitNode(VarNode n) {
        this.printNode(n, n.id);
        this.visit(n.getType());
        this.visit(n.exp);
        return null;
    }

    @Override
    public Void visitNode(PrintNode n) {
        this.printNode(n);
        this.visit(n.exp);
        return null;
    }

    @Override
    public Void visitNode(IfNode n) {
        this.printNode(n);
        this.visit(n.cond);
        this.visit(n.th);
        this.visit(n.el);
        return null;
    }

    @Override
    public Void visitNode(EqualNode n) {
        this.printNode(n);
        this.visit(n.left);
        this.visit(n.right);
        return null;
    }

    @Override
    public Void visitNode(TimesNode n) {
        this.printNode(n);
        this.visit(n.left);
        this.visit(n.right);
        return null;
    }

    @Override
    public Void visitNode(PlusNode n) {
        this.printNode(n);
        this.visit(n.left);
        this.visit(n.right);
        return null;
    }

    @Override
    public Void visitNode(CallNode n) {
        this.printNode(n, n.id + " at nestinglevel " + n.nl);
        this.visit(n.entry);
        for (Node arg : n.arglist) this.visit(arg);
        return null;
    }

    @Override
    public Void visitNode(IdNode n) {
        this.printNode(n, n.id + " at nestinglevel " + n.nl);
        this.visit(n.entry);
        return null;
    }

    @Override
    public Void visitNode(BoolNode n) {
        this.printNode(n, n.val.toString());
        return null;
    }

    @Override
    public Void visitNode(IntNode n) {
        this.printNode(n, n.val.toString());
        return null;
    }

    @Override
    public Void visitNode(ArrowTypeNode n) {
        this.printNode(n);
        for (Node par : n.parlist) this.visit(par);
        this.visit(n.ret, "->"); //marks return type
        return null;
    }

    @Override
    public Void visitNode(BoolTypeNode n) {
        this.printNode(n);
        return null;
    }

    @Override
    public Void visitNode(IntTypeNode n) {
        this.printNode(n);
        return null;
    }

    @Override
    public Void visitSTentry(STentry entry) {
        this.printSTentry("nestlev " + entry.nl);
        this.printSTentry("type");
        this.visit(entry.type);
        this.printSTentry("offset " + entry.offset);
        return null;
    }

}
