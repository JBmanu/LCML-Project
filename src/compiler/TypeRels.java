package compiler;

import compiler.AST.*;
import compiler.lib.TypeNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class TypeRels {

	public static Map<String, String> superType = new HashMap<>();

	public static TypeNode lowestCommonAncestor(TypeNode first, TypeNode second) {
		if (isSubtype(first, second)) return second;
		if (isSubtype(second, first)) return first;

		if (!(first instanceof RefTypeNode firstRefTypeNode)) return null;

		return superTypes(firstRefTypeNode.id)
				.map(RefTypeNode::new)
				.filter(typeOfSuperA -> isSubtype(second, typeOfSuperA))
				.findFirst()
				.orElse(null);
	}

	private static Stream<String> superTypes(String type) {
		return Stream.iterate(type, Objects::nonNull, superType::get);
	}

	public static boolean isSubtype(TypeNode first, TypeNode second) {
		return isBoolAndInt(first, second)
				|| isEmptyTypeAndRefType(first, second)
				|| isSubclass(first, second)
				|| isMethodOverride(first, second);
	}

	public static boolean isSupertype(TypeNode first, TypeNode second) {
		return isSubtype(second, first);
	}

	private static boolean isMethodOverride(TypeNode first, TypeNode second) {
		if (!(first instanceof ArrowTypeNode firstArrowTypeNode) ||
				!(second instanceof ArrowTypeNode secondArrowTypeNode)) {
			return false;
		}

		// Covariance of return type
		if (!isSubtype(firstArrowTypeNode.ret, secondArrowTypeNode.ret)) {
			return false;
		}

		// Contravariance of parameters
		for (int i = 0; i < firstArrowTypeNode.parlist.size(); i++) {
			if (!isSupertype(firstArrowTypeNode.parlist.get(i), secondArrowTypeNode.parlist.get(i))) {
				return false;
			}
		}

		return true;
	}

	private static boolean isSubclass(TypeNode first, TypeNode second) {

		if (!(first instanceof RefTypeNode firstRefTypeNode)
				|| !(second instanceof RefTypeNode secondRefTypeNode)) {
			return false;
		}

		return superTypes(firstRefTypeNode.id)
				.anyMatch(secondRefTypeNode.id::equals);

	}

	private static boolean isEmptyTypeAndRefType(TypeNode first, TypeNode second) {
		return ((first instanceof EmptyTypeNode) && (second instanceof RefTypeNode));
	}

	private static boolean isBoolAndInt(TypeNode first, TypeNode second) {
		return ((first instanceof BoolTypeNode) && (second instanceof IntTypeNode | second instanceof BoolTypeNode))
				|| ((first instanceof IntTypeNode) && (second instanceof IntTypeNode));
	}
}
