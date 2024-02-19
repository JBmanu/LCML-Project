package src.compiler;

import gen.compiler.FOOLBaseVisitor;
import gen.compiler.FOOLParser.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import src.compiler.AST.*;
import src.compiler.lib.DecNode;
import src.compiler.lib.Node;
import src.compiler.lib.TypeNode;

import java.util.ArrayList;
import java.util.List;

import static src.compiler.lib.FOOLlib.extractCtxName;
import static src.compiler.lib.FOOLlib.lowerizeFirstChar;

public class ASTGenerationSTVisitor extends FOOLBaseVisitor<Node> {

    String indent;
    public boolean print;

    ASTGenerationSTVisitor() {
    }

    ASTGenerationSTVisitor(boolean debug) {
        this.print = debug;
    }

    private void printVarAndProdName(ParserRuleContext ctx) {
        String prefix = "";
        Class<?> ctxClass = ctx.getClass(), parentClass = ctxClass.getSuperclass();
        if (!parentClass.equals(ParserRuleContext.class)) // parentClass is the var context (and not ctxClass itself)
            prefix = lowerizeFirstChar(extractCtxName(parentClass.getName())) + ": production #";
        System.out.println(this.indent + prefix + lowerizeFirstChar(extractCtxName(ctxClass.getName())));
    }

    @Override
    public Node visit(ParseTree t) {
        if (t == null) return null;
        String temp = this.indent;
        this.indent = (this.indent == null) ? "" : this.indent + "  ";
        Node result = super.visit(t);
        this.indent = temp;
        return result;
    }

    @Override
    public Node visitProg(ProgContext c) {
        if (this.print) this.printVarAndProdName(c);
        return this.visit(c.progbody());
    }

    @Override
    public Node visitLetInProg(LetInProgContext c) {
        if (this.print) this.printVarAndProdName(c);
        List<DecNode> declist = new ArrayList<>();
        for (DecContext dec : c.dec()) declist.add((DecNode) this.visit(dec));
        return new ProgLetInNode(declist, this.visit(c.exp()));
    }

    @Override
    public Node visitNoDecProg(NoDecProgContext c) {
        if (this.print) this.printVarAndProdName(c);
        return new ProgNode(this.visit(c.exp()));
    }

    @Override
    public Node visitTimes(TimesContext c) {
        if (this.print) this.printVarAndProdName(c);
        Node n = new TimesNode(this.visit(c.exp(0)), this.visit(c.exp(1)));
        n.setLine(c.TIMES().getSymbol().getLine());        // setLine added
        return n;
    }

    @Override
    public Node visitPlus(PlusContext c) {
        if (this.print) this.printVarAndProdName(c);
        Node n = new PlusNode(this.visit(c.exp(0)), this.visit(c.exp(1)));
        n.setLine(c.PLUS().getSymbol().getLine());
        return n;
    }

    @Override
    public Node visitEq(EqContext c) {
        if (this.print) this.printVarAndProdName(c);
        Node n = new EqualNode(this.visit(c.exp(0)), this.visit(c.exp(1)));
        n.setLine(c.EQ().getSymbol().getLine());
        return n;
    }

    @Override
    public Node visitVardec(VardecContext c) {
        if (this.print) this.printVarAndProdName(c);
        Node n = null;
        if (c.ID() != null) { //non-incomplete ST
            n = new VarNode(c.ID().getText(), (TypeNode) this.visit(c.type()), this.visit(c.exp()));
            n.setLine(c.VAR().getSymbol().getLine());
        }
        return n;
    }

    @Override
    public Node visitFundec(FundecContext c) {
        if (this.print) this.printVarAndProdName(c);
        List<ParNode> parList = new ArrayList<>();
        for (int i = 1; i < c.ID().size(); i++) {
            ParNode p = new ParNode(c.ID(i).getText(), (TypeNode) this.visit(c.type(i)));
            p.setLine(c.ID(i).getSymbol().getLine());
            parList.add(p);
        }
        List<DecNode> decList = new ArrayList<>();
        for (DecContext dec : c.dec()) decList.add((DecNode) this.visit(dec));
        Node n = null;
        if (c.ID().size() > 0) { //non-incomplete ST
            n = new FunNode(c.ID(0).getText(), (TypeNode) this.visit(c.type(0)), parList, decList, this.visit(c.exp()));
            n.setLine(c.FUN().getSymbol().getLine());
        }
        return n;
    }

    @Override
    public Node visitIntType(IntTypeContext c) {
        if (this.print) this.printVarAndProdName(c);
        return new IntTypeNode();
    }

    @Override
    public Node visitBoolType(BoolTypeContext c) {
        if (this.print) this.printVarAndProdName(c);
        return new BoolTypeNode();
    }

    @Override
    public Node visitInteger(IntegerContext c) {
        if (this.print) this.printVarAndProdName(c);
        int v = Integer.parseInt(c.NUM().getText());
        return new IntNode(c.MINUS() == null ? v : -v);
    }

    @Override
    public Node visitTrue(TrueContext c) {
        if (this.print) this.printVarAndProdName(c);
        return new BoolNode(true);
    }

    @Override
    public Node visitFalse(FalseContext c) {
        if (this.print) this.printVarAndProdName(c);
        return new BoolNode(false);
    }

    @Override
    public Node visitIf(IfContext c) {
        if (this.print) this.printVarAndProdName(c);
        Node ifNode = this.visit(c.exp(0));
        Node thenNode = this.visit(c.exp(1));
        Node elseNode = this.visit(c.exp(2));
        Node n = new IfNode(ifNode, thenNode, elseNode);
        n.setLine(c.IF().getSymbol().getLine());
        return n;
    }

    @Override
    public Node visitPrint(PrintContext c) {
        if (this.print) this.printVarAndProdName(c);
        return new PrintNode(this.visit(c.exp()));
    }

    @Override
    public Node visitPars(ParsContext c) {
        if (this.print) this.printVarAndProdName(c);
        return this.visit(c.exp());
    }

    @Override
    public Node visitId(IdContext c) {
        if (this.print) this.printVarAndProdName(c);
        Node n = new IdNode(c.ID().getText());
        n.setLine(c.ID().getSymbol().getLine());
        return n;
    }

    @Override
    public Node visitCall(CallContext c) {
        if (this.print) this.printVarAndProdName(c);
        List<Node> arglist = new ArrayList<>();
        for (ExpContext arg : c.exp()) arglist.add(this.visit(arg));
        Node n = new CallNode(c.ID().getText(), arglist);
        n.setLine(c.ID().getSymbol().getLine());
        return n;
    }
}
