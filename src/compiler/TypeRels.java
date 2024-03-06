package compiler;

import compiler.AST.*;
import compiler.lib.*;

import java.util.HashMap;
import java.util.Map;

public class TypeRels {
	static Map<String,String> superType = new HashMap<>();
	// valuta se il tipo "a" e' <= al tipo "b", dove "a" e "b" sono tipi di base: IntTypeNode o BoolTypeNode
	public static boolean isSubtype(TypeNode a, TypeNode b) {
		if(a instanceof RefTypeNode && b instanceof RefTypeNode){
			//subtyping tra classi
			String aID=((RefTypeNode)a).classID;
			String bID=((RefTypeNode)b).classID;

			if (superType.get(aID) == null) {
				//caso tipi di classi senza ereditarietà
				return aID.equals(bID);
			} else {
				//caso tipi di classi con ereditarietà
				if (aID.equals((bID))) {
					return true;
				}
				while (!(aID).equals(bID)) {
					aID = superType.get(aID);
					if (aID == null) {
						return false;
					}
				}
			}
			return true;
		}else if(a instanceof ArrowTypeNode && b instanceof ArrowTypeNode){
			// caso arrowtypenode
			ArrowTypeNode arrowTypea = (ArrowTypeNode) a;
			ArrowTypeNode arrowTypeb = (ArrowTypeNode) b;

			//se il return type è corretto (uguale o sottotipo) e gli arrowtype hanno lo stesso numero di parametri
			if (isSubtype(arrowTypea.ret, arrowTypeb.ret) && (arrowTypea.parlist.size() == arrowTypeb.parlist.size())) {

				//controllo tutti i parametri
				for (int i = 0; i < arrowTypea.parlist.size(); i++) {
					if (!(isSubtype(arrowTypeb.parlist.get(i),arrowTypea.parlist.get(i)))) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		} else{
			//caso tipi semplici
			return a.getClass().equals(b.getClass()) ||
					((a instanceof BoolTypeNode) && (b instanceof IntTypeNode)) ||
					((a instanceof EmptyTypeNode) && (b instanceof RefTypeNode));
		}
	}

	public static TypeNode lowestCommonAncestor(TypeNode a, TypeNode b){
		//if one of them is an emptyTypenode returns the other one
		if(a instanceof EmptyTypeNode || b instanceof EmptyTypeNode ||
				a instanceof RefTypeNode || b instanceof RefTypeNode){
			if(a instanceof EmptyTypeNode){
				return b;
			} else if(b instanceof EmptyTypeNode){
				return a;
			} else{ //else it rises the chain of the supertypes looking for a class which is a b supertype
				RefTypeNode aType = (RefTypeNode) a;
				RefTypeNode bType = (RefTypeNode) b;

				while(aType.classID != null) {
					if(isSubtype(bType, aType)) {
						return aType;
					}
					aType = new RefTypeNode(superType.get(aType.classID));
				}

				return null;


			}

		} else if (a instanceof IntTypeNode || b instanceof IntTypeNode){
			//if one of them in an IntTypeNode returns IntTypeNode, else it returns BoolTypeNode
			return new IntTypeNode();
		} else if(a instanceof BoolTypeNode || b instanceof BoolTypeNode){
			return new BoolTypeNode();
		}
		//failure
		return null;
	}


}
