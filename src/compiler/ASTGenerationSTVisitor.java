package compiler;

import java.util.*;
import java.util.stream.Collectors;

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
	public Node visitProg(ProgContext c) {
		if (print) printVarAndProdName(c);
		return visit(c.progbody());
	}

	@Override
	public Node visitLetInProg(LetInProgContext c) {
		if (print) printVarAndProdName(c);
		List<DecNode> classDec = c.cldec().stream()
				.map(x -> (DecNode) visit(x))
				.collect(Collectors.toList());
		List<DecNode> dec = c.dec().stream()
				.map(x -> (DecNode) visit(x))
				.collect(Collectors.toList());
		List<DecNode> allDeclarations = new ArrayList<>();
		allDeclarations.addAll(classDec);
		allDeclarations.addAll(dec);
		return new ProgLetInNode(allDeclarations, visit(c.exp()));
	}

	@Override
	public Node visitNoDecProg(NoDecProgContext c) {
		if (print) printVarAndProdName(c);
		return new ProgNode(visit(c.exp()));
	}

	@Override
	public Node visitTimesDiv(TimesDivContext c) {
		if (print) printVarAndProdName(c);
		if(c.TIMES()!=null){
			Node n = new TimesNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.TIMES().getSymbol().getLine());
			return n;
		} else {
			Node n = new DivNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.DIV().getSymbol().getLine());
			return n;
		}
	}

	@Override
	public Node visitPlusMinus(PlusMinusContext c) {
		if (print) printVarAndProdName(c);
		if(c.PLUS()!=null){
			Node n = new PlusNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.PLUS().getSymbol().getLine());
			return n;
		} else {
			Node n = new MinusNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.MINUS().getSymbol().getLine());
			return n;
		}

	}

	@Override
	public Node visitComp(CompContext c) {
		if (print) printVarAndProdName(c);
		if(c.EQ()!=null){
			Node n = new EqualNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.EQ().getSymbol().getLine());
			return n;
		} else if (c.LE()!=null){
			Node n = new LessEqualNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.LE().getSymbol().getLine());
			return n;
		} else {
			Node n = new GreaterEqualNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.GE().getSymbol().getLine());
			return n;
		}

	}

	@Override
	public Node visitAndOr(AndOrContext c) {
		if (print) printVarAndProdName(c);
		if(c.AND()!=null){
			Node n = new AndNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.AND().getSymbol().getLine());
			return n;
		} else {
			Node n = new OrNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.OR().getSymbol().getLine());
			return n;
		}
	}

	@Override
	public Node visitNot(NotContext c) {
		if (print) printVarAndProdName(c);
		Node n = new NotNode(visit(c.exp()));
		n.setLine(c.NOT().getSymbol().getLine());
		return n;
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
		Node n = null;
		if (c.ID().size() != 0) {
			final List<ParNode> parametersList = new ArrayList<>();
			// Skip the first ID, which is the function name
			for (int i = 1; i < c.ID().size(); i++) {
				final ParNode p = new ParNode(c.ID(i).getText(), (TypeNode) visit(c.type(i)));
				p.setLine(c.ID(i).getSymbol().getLine());
				parametersList.add(p);
			}

			final List<DecNode> declarationsList = c.dec().stream()
					.map(x -> (DecNode) visit(x))
					.collect(Collectors.toList());

			final String id = c.ID(0).getText();
			final TypeNode type = (TypeNode) visit(c.type(0));
			n = new FunNode(id, type, parametersList, declarationsList, visit(c.exp()));
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
		List<Node> arglist = c.exp().stream()
				.map(this::visit)
				.collect(Collectors.toList());
		for (ExpContext arg : c.exp()) arglist.add(visit(arg));
		Node n = new CallNode(c.ID().getText(), arglist);
		n.setLine(c.ID().getSymbol().getLine());
		return n;
	}

	@Override
	public Node visitCldec(final CldecContext c) {
		if (print) printVarAndProdName(c);
		Node n = null;
		if (c.ID().size() != 0){
			final String classId = c.ID(0).getText();
			final Optional<String> superId = c.EXTENDS() == null ?
					Optional.empty() : Optional.of(c.ID(1).getText());
			final int idPadding = superId.isPresent() ? 2 : 1;

			final List<FieldNode> fields = new ArrayList<>();
			for (int i = idPadding; i < c.ID().size(); i++) {
				final String id = c.ID(i).getText();
				final TypeNode type = (TypeNode) visit(c.type(i - idPadding));
				final FieldNode f = new FieldNode(id, type);
				f.setLine(c.ID(i).getSymbol().getLine());
				fields.add(f);
			}
			final List<MethodNode> methods = c.methdec().stream()
					.map(x -> (MethodNode) visit(x))
					.collect(Collectors.toList());
			n = new ClassNode(classId, superId, fields, methods);
			n.setLine(c.ID(0).getSymbol().getLine());
		}
		return n;
	}

	@Override
	public Node visitMethdec(MethdecContext c) {
		if (print) printVarAndProdName(c);
		Node n = null;
		if (c.ID().size() != 0){
			String methodId = c.ID(0).getText();
			TypeNode returnType = (TypeNode) visit(c.type(0));

			int idPadding = 1;
			List<ParNode> params = new ArrayList<>();
			for (int i = idPadding; i < c.ID().size(); i++) {
				String id = c.ID(i).getText();
				TypeNode type = (TypeNode) visit(c.type(i));
				ParNode p = new ParNode(id, type);
				p.setLine(c.ID(i).getSymbol().getLine());
				params.add(p);
			}

			List<DecNode> declarations = c.dec().stream()
					.map(x -> (DecNode) visit(x))
					.collect(Collectors.toList());
			Node exp = visit(c.exp());
			n = new MethodNode(methodId, returnType, params, declarations, exp);
			n.setLine(c.ID(0).getSymbol().getLine());
		}
		return n;
	}

	@Override
	public Node visitNull(final NullContext context) {
		if (print) printVarAndProdName(context);
		return new EmptyNode();
	}


	@Override
	public Node visitDotCall(final DotCallContext c) {
		if (print) printVarAndProdName(c);
		Node n = null;
		if (c.ID().size() == 2) {
			String objectId = c.ID(0).getText();
			String methodId = c.ID(1).getText();
			List<Node> args = c.exp().stream()
					.map(this::visit)
					.collect(Collectors.toList());

			n = new ClassCallNode(objectId, methodId, args);
			n.setLine(c.ID(0).getSymbol().getLine());
		}
		return n;
	}

	@Override
	public Node visitNew(NewContext c) {
		if (print) printVarAndProdName(c);
		Node n = null;
		if (c.ID() != null) {
			String classId = c.ID().getText();
			List<Node> args = c.exp().stream()
					.map(this::visit)
					.toList();

			n = new NewNode(classId, args);
			n.setLine(c.ID().getSymbol().getLine());
		}
		return n;
	}

	@Override
	public Node visitIdType(IdTypeContext c) {
		if (print) printVarAndProdName(c);
		String id = c.ID().getText();
		Node n = new RefTypeNode(id);
		n.setLine(c.ID().getSymbol().getLine());
		return n;
	}
}
