package src.compiler.lib;

import src.compiler.STentry;
import src.compiler.exc.UnimplException;

public class BaseEASTVisitor<S, E extends Exception> extends BaseASTVisitor<S, E> {

    protected BaseEASTVisitor() {
    }

    protected BaseEASTVisitor(boolean ie) {
        super(ie);
    }

    protected BaseEASTVisitor(boolean ie, boolean p) {
        super(ie, p);
    }

    protected void printSTentry(String s) {
        System.out.println(this.indent + "STentry: " + s);
    }

    public S visitSTentry(STentry s) throws E {
        throw new UnimplException();
    }
}
