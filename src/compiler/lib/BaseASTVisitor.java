package src.compiler.lib;

import src.compiler.AST.*;
import src.compiler.exc.IncomplException;
import src.compiler.exc.UnimplException;

import static src.compiler.lib.FOOLlib.extractNodeName;

public class BaseASTVisitor<S, E extends Exception> {

    private boolean incomplExc; // enables throwing IncomplException
    protected boolean print;    // enables printing
    protected String indent;

    protected BaseASTVisitor() {
    }

    protected BaseASTVisitor(boolean ie) {
        this.incomplExc = ie;
    }

    protected BaseASTVisitor(boolean ie, boolean p) {
        this.incomplExc = ie;
        this.print = p;
    }

    protected void printNode(Node n) {
        System.out.println(this.indent + extractNodeName(n.getClass().getName()));
    }

    protected void printNode(Node n, String s) {
        System.out.println(this.indent + extractNodeName(n.getClass().getName()) + ": " + s);
    }

    public S visit(Visitable v) throws E {
        return this.visit(v, "");                //performs unmarked visit
    }

    public S visit(Visitable v, String mark) throws E {   //when printing marks this visit with string mark
        if (v == null)
            if (this.incomplExc) throw new IncomplException();
            else
                return null;
        if (this.print) {
            String temp = this.indent;
            this.indent = (this.indent == null) ? "" : this.indent + "  ";
            this.indent += mark; //inserts mark
            try {
                S result = this.visitByAcc(v);
                return result;
            } finally {
                this.indent = temp;
            }
        } else
            return this.visitByAcc(v);
    }

    S visitByAcc(Visitable v) throws E {
        return v.accept(this);
    }

    public S visitNode(ProgLetInNode n) throws E {
        throw new UnimplException();
    }

    public S visitNode(ProgNode n) throws E {
        throw new UnimplException();
    }

    public S visitNode(FunNode n) throws E {
        throw new UnimplException();
    }

    public S visitNode(ParNode n) throws E {
        throw new UnimplException();
    }

    public S visitNode(VarNode n) throws E {
        throw new UnimplException();
    }

    public S visitNode(PrintNode n) throws E {
        throw new UnimplException();
    }

    public S visitNode(IfNode n) throws E {
        throw new UnimplException();
    }

    public S visitNode(EqualNode n) throws E {
        throw new UnimplException();
    }

    public S visitNode(TimesNode n) throws E {
        throw new UnimplException();
    }

    public S visitNode(PlusNode n) throws E {
        throw new UnimplException();
    }

    public S visitNode(CallNode n) throws E {
        throw new UnimplException();
    }

    public S visitNode(IdNode n) throws E {
        throw new UnimplException();
    }

    public S visitNode(BoolNode n) throws E {
        throw new UnimplException();
    }

    public S visitNode(IntNode n) throws E {
        throw new UnimplException();
    }

    public S visitNode(ArrowTypeNode n) throws E {
        throw new UnimplException();
    }

    public S visitNode(BoolTypeNode n) throws E {
        throw new UnimplException();
    }

    public S visitNode(IntTypeNode n) throws E {
        throw new UnimplException();
    }
}
