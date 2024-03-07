package compiler;

import java.util.*;
import compiler.lib.*;

public class AST {

	public static class RefTypeNode extends TypeNode {//ID

		final String classID;

		public RefTypeNode(String classID) {
			this.classID = classID;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}

	}

	public static class EmptyNode extends Node {//null
		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class EmptyTypeNode extends TypeNode {// tipo di null
		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}

	}

	public static class ProgLetInNode extends Node {
		final List<DecNode> declist;
		final Node exp;
		ProgLetInNode(List<DecNode> d, Node e) {
			declist = Collections.unmodifiableList(d); 
			exp = e;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class ClassNode extends DecNode {

		final String classID; //name
		final String superID;  // ID classe padre
		final List<AttributeNode> attributes;
		final List<ClassFunctionNode> functions;
		public ClassTypeNode classType;
		STentry superEntry;

		public ClassNode(final String id, final String superID, List<AttributeNode> attributes, final List<ClassFunctionNode> functions) {
			this.classID = id;
			this.superID = superID;
			this.attributes = Collections.unmodifiableList(attributes);
			this.functions = Collections.unmodifiableList(functions);
		}
		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class ClassTypeNode extends TypeNode {

		List<TypeNode> attributes;
		List<ArrowTypeNode> functions;

		public ClassTypeNode(List<TypeNode> attributes, List<ArrowTypeNode> functions) {
			this.attributes = attributes;
			this.functions = functions;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}

	}


	public static class NewNode extends Node {//new ID()

		final String classID;
		final List<Node> arglist;
		STentry classEntry;

		public NewNode(String classID, List<Node> arglist) {
			this.classID = classID;
			this.arglist = arglist;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}

	}

	public static class AttributeNode extends DecNode {
		final String id;
		public int offset = 0;
		AttributeNode(String i, TypeNode t) {id = i; type = t;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class ClassFunctionNode extends DecNode {
		final String id;
		final TypeNode retType;
		final List<ParNode> parlist;
		final List<DecNode> declist;
		final Node exp;
		public int offset = -1;//default

		public String label;

		ClassFunctionNode(String i, TypeNode rt, List<ParNode> pl, List<DecNode> dl, Node e) {
			id=i;
			retType=rt;
			parlist=Collections.unmodifiableList(pl);
			declist=Collections.unmodifiableList(dl);
			exp=e;
		}

		public void setType(TypeNode type) {
			this.type = type;
		}
		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class FunctionTypeNode extends TypeNode {

		final ArrowTypeNode fun;

		public FunctionTypeNode(ArrowTypeNode fun) {
			this.fun = fun;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class ProgNode extends Node {
		final Node exp;
		ProgNode(Node e) {exp = e;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}
	
	public static class FunNode extends DecNode {
		final String id;
		final TypeNode retType;
		final List<ParNode> parlist;
		final List<DecNode> declist; 
		final Node exp;
		FunNode(String i, TypeNode rt, List<ParNode> pl, List<DecNode> dl, Node e) {
	    	id=i; 
	    	retType=rt; 
	    	parlist=Collections.unmodifiableList(pl); 
	    	declist=Collections.unmodifiableList(dl); 
	    	exp=e;
	    }
		
		//void setType(TypeNode t) {type = t;}
		
		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class ParNode extends DecNode {
		final String id;
		ParNode(String i, TypeNode t) {id = i; type = t;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}
	
	public static class VarNode extends DecNode {
		final String id;
		final Node exp;
		VarNode(String i, TypeNode t, Node v) {id = i; type = t; exp = v;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}
		
	public static class PrintNode extends Node {
		final Node exp;
		PrintNode(Node e) {exp = e;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}
	
	public static class IfNode extends Node {
		final Node cond;
		final Node th;
		final Node el;
		IfNode(Node c, Node t, Node e) {cond = c; th = t; el = e;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}
	
	public static class EqualNode extends Node {
		final Node left;
		final Node right;
		EqualNode(Node l, Node r) {left = l; right = r;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class MinorEqualNode extends Node {
		final Node left;
		final Node right;
		MinorEqualNode(Node l, Node r) {left = l; right = r;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class GreaterEqualNode extends Node {
		final Node left;
		final Node right;
		GreaterEqualNode(Node l, Node r) {left = l; right = r;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}
	
	public static class TimesNode extends Node {
		final Node left;
		final Node right;
		TimesNode(Node l, Node r) {left = l; right = r;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class DivisionNode extends Node {
		final Node left;
		final Node right;
		DivisionNode(Node l, Node r) {left = l; right = r;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}
	
	public static class PlusNode extends Node {
		final Node left;
		final Node right;
		PlusNode(Node l, Node r) {left = l; right = r;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class MinusNode extends Node {
		final Node left;
		final Node right;
		MinusNode(Node l, Node r) {left = l; right = r;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class NotNode extends Node {
		final Node exp;

		NotNode(final Node exp) {
			this.exp = exp;
		}

		@Override
		public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
			return visitor.visitNode(this);
		}

	}

	/**
	 * The node for the or expression.
	 * It contains the left and right expression.
	 */
	public static class OrNode extends Node {
		final Node left;
		final Node right;

		OrNode(final Node left, final Node right) {
			this.left = left;
			this.right = right;
		}

		@Override
		public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
			return visitor.visitNode(this);
		}

	}

	/**
	 * The node for the and expression.
	 * It contains the left and right expression.
	 */
	public static class AndNode extends Node {
		final Node left;
		final Node right;

		AndNode(final Node left, final Node right) {
			this.left = left;
			this.right = right;
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
			id = i; 
			arglist = Collections.unmodifiableList(p);
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}
	
	public static class IdNode extends Node {
		final String id;
		STentry entry;
		int nl;
		IdNode(String i) {id = i;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}
	
	public static class BoolNode extends Node {
		final Boolean val;
		BoolNode(boolean n) {val = n;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}
	
	public static class IntNode extends Node {
		final Integer val;
		IntNode(Integer n) {val = n;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}
	
	public static class ArrowTypeNode extends TypeNode {
		final List<TypeNode> parlist;
		final TypeNode ret;
		ArrowTypeNode(List<TypeNode> p, TypeNode r) {
			parlist = Collections.unmodifiableList(p); 
			ret = r;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}
	
	public static class BoolTypeNode extends TypeNode {

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class IntTypeNode extends TypeNode {

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class ClassCallNode extends Node {//classID.methodID()

		final String objectID;
		final String methodID;
		final List<Node> arglist;
		int nl;
		STentry classEntry; //class entry
		STentry methodEntry; //method entry

		public ClassCallNode(String objectID, String methodID, List<Node> arglist) {
			this.objectID = objectID;
			this.methodID = methodID;
			this.arglist = arglist;
		}


		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}
}