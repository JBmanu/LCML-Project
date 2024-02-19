package src.compiler;

import src.compiler.AST.BoolTypeNode;
import src.compiler.AST.IntTypeNode;
import src.compiler.lib.TypeNode;

public class TypeRels {

    // valuta se il tipo "a" e' <= al tipo "b", dove "a" e "b" sono tipi di base: IntTypeNode o BoolTypeNode
    public static boolean isSubtype(TypeNode a, TypeNode b) {
        return a.getClass().equals(b.getClass()) || ((a instanceof BoolTypeNode) && (b instanceof IntTypeNode));
    }

}
