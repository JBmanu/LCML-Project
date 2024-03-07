package compiler;

import java.util.*;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import compiler.AST.*;
import compiler.FOOLParser.*;
import compiler.lib.*;
import static compiler.lib.FOOLlib.*;

public class ASTGenerationSTVisitor extends FOOLBaseVisitor<Node> {

	String indent;
    public boolean print;
	
    ASTGenerationSTVisitor() {}    
    ASTGenerationSTVisitor(boolean debug) { print=debug; }
        
    private void printVarAndProdName(ParserRuleContext ctx) {
        String prefix="";        
    	Class<?> ctxClass=ctx.getClass(), parentClass=ctxClass.getSuperclass();
        if (!parentClass.equals(ParserRuleContext.class)) // parentClass is the var context (and not ctxClass itself)
        	prefix=lowerizeFirstChar(extractCtxName(parentClass.getName()))+": production #";
    	System.out.println(indent+prefix+lowerizeFirstChar(extractCtxName(ctxClass.getName())));                               	
    }
        
    @Override
	public Node visit(ParseTree t) {
    	if (t==null) return null;
        String temp=indent;
        indent=(indent==null)?"":indent+"  ";
        Node result = super.visit(t);
        indent=temp;
        return result; 
	}

	@Override
	public Node visitClassFunction(ClassFunctionContext c) {//come FundecContext
		if (print) printVarAndProdName(c);
		List<ParNode> parList = new ArrayList<>();
		for (int i = 1; i < c.ID().size(); i++) {
			ParNode p = new ParNode(c.ID(i).getText(), (TypeNode) visit(c.type(i)));
			p.setLine(c.ID(i).getSymbol().getLine());
			parList.add(p);
		}
		List<DecNode> decList = new ArrayList<>();
		for (DecContext dec : c.dec()) decList.add((DecNode) visit(dec));
		Node n = null;
		if (c.ID().size() > 0) { //non-incomplete ST
			n = new ClassFunctionNode(c.ID(0).getText(), (TypeNode)visit(c.type(0)), parList, decList, visit(c.exp()));
			n.setLine(c.FUN().getSymbol().getLine());
		}
		return n;
	}

	@Override
	public Node visitClass(ClassContext c) { //check
		String superID = null;
		if (print) printVarAndProdName(c);
		int index = 0;
		String classID = c.ID(index++).getText();

		boolean heredity = this.checkHeredity(c, superID, index);
		List<AttributeNode> attributes = this.findAttributeNodes(c, heredity, index);
		List<ClassFunctionNode> functions = this.foundFunctionNodes(c);

		Node n = new ClassNode(classID, superID, attributes, functions);
		n.setLine(c.ID(0).getSymbol().getLine());
		return n;
	}

	public boolean checkHeredity(ClassContext c, String superID, int index){
		if(c.EXTENDS() != null) {
			superID = c.ID(index++).getText();
			return true;
		}
		return false;
	}

	List<AttributeNode> findAttributeNodes(ClassContext c, boolean heredity, int index){
		List<AttributeNode> attributes = new ArrayList<>();
		for(; index < c.ID().size(); index++) {
			AttributeNode n = new AttributeNode(c.ID(index).getText(),
					(TypeNode) visit(c.type(heredity?index-2:index-1)));
			n.setLine(c.ID(index).getSymbol().getLine());
			attributes.add(n);
		}
		return attributes;
	}

	List<ClassFunctionNode> foundFunctionNodes(ClassContext c){
		List<ClassFunctionNode> functions = new ArrayList<>();
		for(ClassFunctionContext function : c.classFunction()) {
			functions.add((ClassFunctionNode) visit(function));
		}
		return functions;
	}

	@Override
	public Node visitNew(NewContext c) {
		if (print) printVarAndProdName(c);
		List<Node> arglist = new ArrayList<>();
		for (ExpContext arg : c.exp()) arglist.add(visit(arg));
		Node n = new NewNode(c.ID().getText(), arglist);
		n.setLine(c.ID().getSymbol().getLine());
		return n;
	}

	@Override
	public Node visitNull(NullContext c) {
		if (print) printVarAndProdName(c);
		Node n = new EmptyNode();
		n.setLine(c.NULL().getSymbol().getLine());
		return n;
	}

	@Override
	public Node visitProg(ProgContext c) {
		if (print) printVarAndProdName(c);
		return visit(c.progbody());
	}

	@Override
	public Node visitLetInProg(LetInProgContext c) {
		if (print) printVarAndProdName(c);

		List<DecNode> declist = new ArrayList<>();

		// Visita le classi
		for (ClassContext classContext : c.class_()) declist.add((DecNode) visit(classContext));

		for (DecContext dec : c.dec()) declist.add((DecNode) visit(dec));
		return new ProgLetInNode(declist, visit(c.exp()));
	}

	@Override
	public Node visitNoDecProg(NoDecProgContext c) {
		if (print) printVarAndProdName(c);
		return new ProgNode(visit(c.exp()));
	}

	@Override
	public Node visitTimes(TimesContext c) {
		if (print) printVarAndProdName(c);
		Node n = new TimesNode(visit(c.exp(0)), visit(c.exp(1)));
		n.setLine(c.TIMES().getSymbol().getLine());		// setLine added
        return n;		
	}

	@Override
	public Node visitDivision(DivisionContext c) {
		if (print) printVarAndProdName(c);
		Node n = new DivisionNode(visit(c.exp(0)), visit(c.exp(1)));
		n.setLine(c.DIVISION().getSymbol().getLine());		// setLine added
		return n;
	}

	/*@Override
	public Node visitGreater(PlusContext c) {
		if (print) printVarAndProdName(c);
		Node n = new PlusNode(visit(c.exp(0)), visit(c.exp(1)));
		n.setLine(c.PLUS().getSymbol().getLine());	
        return n;		
	}*/

	@Override
	public Node visitEq(EqContext c) {
		if (print) printVarAndProdName(c);
		Node n = new EqualNode(visit(c.exp(0)), visit(c.exp(1)));
		n.setLine(c.EQ().getSymbol().getLine());
        return n;		
	}

	@Override
	public Node visitMinoreq(MinoreqContext c) {
		if (print) printVarAndProdName(c);
		Node n = new MinorEqualNode(visit(c.exp(0)), visit(c.exp(1)));
		n.setLine(c.MINOREQ().getSymbol().getLine());
		return n;
	}

	@Override
	public Node visitGreatereq(GreatereqContext c) {
		Node n = new GreaterEqualNode(visit(c.exp(0)), visit(c.exp(1)));
		n.setLine(c.GREATEREQ().getSymbol().getLine());
		return n;
	}

	@Override
	public Node visitMinus(MinusContext c) {
		if (print) printVarAndProdName(c);
		Node n = new MinusNode(visit(c.exp(0)), visit(c.exp(1)));
		n.setLine(c.MINUS().getSymbol().getLine());
		return n;
	}

	@Override
	public Node visitNot(final NotContext c) {
		if (print) printVarAndProdName(c);
		final Node node = new NotNode(visit(c.exp()));
		node.setLine(c.NOT().getSymbol().getLine());
		return node;
	}

	@Override
	public Node visitAnd(final AndContext c) {
		final Node node = new AndNode(visit(c.exp(0)), visit(c.exp(1)));
		node.setLine(c.AND().getSymbol().getLine());
		return node;
	}

	@Override
	public Node visitOr(final OrContext c) {
		final Node node = new OrNode(visit(c.exp(0)), visit(c.exp(1)));
		node.setLine(c.OR().getSymbol().getLine());
		return node;
	}

	@Override
	public Node visitVardec(VardecContext c) {
		if (print) printVarAndProdName(c);
		Node n = null;
		if (c.ID()!=null) { //non-incomplete ST
			n = new VarNode(c.ID().getText(), (TypeNode) visit(c.type()), visit(c.exp()));
			n.setLine(c.VAR().getSymbol().getLine());
		}
        return n;
	}

	@Override
	public Node visitFundec(FundecContext c) {
		if (print) printVarAndProdName(c);
		List<ParNode> parList = new ArrayList<>();
		for (int i = 1; i < c.ID().size(); i++) { 
			ParNode p = new ParNode(c.ID(i).getText(),(TypeNode) visit(c.type(i)));
			p.setLine(c.ID(i).getSymbol().getLine());
			parList.add(p);
		}
		List<DecNode> decList = new ArrayList<>();
		for (DecContext dec : c.dec()) decList.add((DecNode) visit(dec));
		Node n = null;
		if (c.ID().size()>0) { //non-incomplete ST
			n = new FunNode(c.ID(0).getText(),(TypeNode)visit(c.type(0)),parList,decList,visit(c.exp()));
			n.setLine(c.FUN().getSymbol().getLine());
		}
        return n;
	}

	@Override
	public Node visitIntType(IntTypeContext c) {
		if (print) printVarAndProdName(c);
		return new IntTypeNode();
	}

	@Override
	public Node visitBoolType(BoolTypeContext c) {
		if (print) printVarAndProdName(c);
		return new BoolTypeNode();
	}

	@Override
	public Node visitIdType(IdTypeContext c) {
		if (print) printVarAndProdName(c);
		return new RefTypeNode(c.ID().getText());
	}

	@Override
	public Node visitInteger(IntegerContext c) {
		if (print) printVarAndProdName(c);
		int v = Integer.parseInt(c.NUM().getText());
		return new IntNode(c.MINUS()==null?v:-v);
	}

	@Override
	public Node visitTrue(TrueContext c) {
		if (print) printVarAndProdName(c);
		return new BoolNode(true);
	}

	@Override
	public Node visitFalse(FalseContext c) {
		if (print) printVarAndProdName(c);
		return new BoolNode(false);
	}

	@Override
	public Node visitIf(IfContext c) {
		if (print) printVarAndProdName(c);
		Node ifNode = visit(c.exp(0));
		Node thenNode = visit(c.exp(1));
		Node elseNode = visit(c.exp(2));
		Node n = new IfNode(ifNode, thenNode, elseNode);
		n.setLine(c.IF().getSymbol().getLine());			
        return n;		
	}

	@Override
	public Node visitPrint(PrintContext c) {
		if (print) printVarAndProdName(c);
		return new PrintNode(visit(c.exp()));
	}

	@Override
	public Node visitPars(ParsContext c) {
		if (print) printVarAndProdName(c);
		return visit(c.exp());
	}

	@Override
	public Node visitId(IdContext c) {
		if (print) printVarAndProdName(c);
		Node n = new IdNode(c.ID().getText());
		n.setLine(c.ID().getSymbol().getLine());
		return n;
	}

	@Override
	public Node visitCall(CallContext c) {
		if (print) printVarAndProdName(c);		
		List<Node> arglist = new ArrayList<>();
		for (ExpContext arg : c.exp()) arglist.add(visit(arg));
		Node n = new CallNode(c.ID().getText(), arglist);
		n.setLine(c.ID().getSymbol().getLine());
		return n;
	}

	@Override
	public Node visitDotCall(DotCallContext c) {
		if (print) printVarAndProdName(c);
		List<Node> arglist = new ArrayList<>();
		for (ExpContext arg : c.exp()) arglist.add(visit(arg));
		Node n = new ClassCallNode(c.ID(0).getText(),
				c.ID(1).getText(), arglist);
		n.setLine(c.ID(0).getSymbol().getLine());
		return n;
	}
}
