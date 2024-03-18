package compiler;

import compiler.AST.*;
import compiler.exc.VoidException;
import compiler.lib.BaseASTVisitor;
import compiler.lib.TypeNode;

import java.util.*;

public class SymbolTableASTVisitor extends BaseASTVisitor<Void, VoidException> {
    static class VirtualTable extends HashMap<String, STentry> {
    }

    private final Map<String, VirtualTable> classTable = new HashMap<>();
    private final List<Map<String, STentry>> symbolTable = new ArrayList<>();

    private int nestingLevel = 0; // current nesting level
    private int decOffset = -2; // counter for offset of local declarations at current nesting level
    public int stErrors = 0;

    public SymbolTableASTVisitor() {
    }

    SymbolTableASTVisitor(boolean debug) {
        super(true, debug);
    } // enables print for debugging

    private STentry stLookup(String id) {
        int j = nestingLevel;
        STentry entry = null;
        while (j >= 0 && entry == null)
            entry = symbolTable.get(j--).get(id);
        return entry;
    }

    @Override
    public Void visitNode(ProgLetInNode node) {
        if (print) printNode(node);
        symbolTable.add(new HashMap<>());
        node.declarations.forEach(this::visit);
        visit(node.exp);
        symbolTable.remove(0);
        return null;
    }

    @Override
    public Void visitNode(ProgNode node) {
        if (print) printNode(node);
        visit(node.exp);
        return null;
    }

    @Override
    public Void visitNode(FunNode node) {
        if (print) printNode(node);

        final Map<String, STentry> currentSymbolTable = symbolTable.get(nestingLevel);
        final List<TypeNode> parametersTypes = node.parameters.stream().map(ParNode::getType).toList();
        final ArrowTypeNode arrowTypeNode = new ArrowTypeNode(parametersTypes, node.returnType);
        final STentry entry = new STentry(nestingLevel, arrowTypeNode, decOffset--);

        //inserimento di ID nella symtable
        if (currentSymbolTable.put(node.id, entry) != null) {
            System.out.println("Fun id " + node.id + " at line " + node.getLine() + " already declared");
            stErrors++;
        }
        //creare una nuova hashmap per la symTable
        nestingLevel++;
        int prevNLDecOffset = decOffset; // stores counter for offset of declarations at previous nesting level
        decOffset = -2;
        final Map<String, STentry> newSymbolTable = new HashMap<>();
        symbolTable.add(newSymbolTable);

        int parOffset = 1;
        for (ParNode par : node.parameters) {
            final STentry parEntry = new STentry(nestingLevel, par.getType(), parOffset++);
            if (newSymbolTable.put(par.id, parEntry) != null) {
                System.out.println("Par id " + par.id + " at line " + node.getLine() + " already declared");
                stErrors++;
            }
        }
        node.declarations.forEach(this::visit);
        visit(node.exp);

        //rimuovere la hashmap corrente poiche' esco dallo scope
        symbolTable.remove(nestingLevel);
        decOffset = prevNLDecOffset; // restores counter for offset of declarations at previous nesting level
        nestingLevel--;
        return null;
    }

    @Override
    public Void visitNode(VarNode node) {
        if (print) printNode(node);
        visit(node.exp);
        final Map<String, STentry> currentSymbolTable = symbolTable.get(nestingLevel);
        final STentry entry = new STentry(nestingLevel, node.getType(), decOffset--);
        //inserimento di ID nella symtable
        if (currentSymbolTable.put(node.id, entry) != null) {
            System.out.println("Var id " + node.id + " at line " + node.getLine() + " already declared");
            stErrors++;
        }
        return null;
    }

    @Override
    public Void visitNode(PrintNode node) {
        if (print) printNode(node);
        visit(node.exp);
        return null;
    }

    @Override
    public Void visitNode(IfNode node) {
        if (print) printNode(node);
        visit(node.cond);
        visit(node.thenBranch);
        visit(node.elseBranch);
        return null;
    }

    @Override
    public Void visitNode(EqualNode node) {
        if (print) printNode(node);
        visit(node.left);
        visit(node.right);
        return null;
    }

    @Override
    public Void visitNode(TimesNode node) {
        if (print) printNode(node);
        visit(node.left);
        visit(node.right);
        return null;
    }

    @Override
    public Void visitNode(PlusNode node) {
        if (print) printNode(node);
        visit(node.left);
        visit(node.right);
        return null;
    }

    @Override
    public Void visitNode(CallNode node) {
        if (print) printNode(node);
        final STentry entry = stLookup(node.id);
        if (entry == null) {
            System.out.println("Fun id " + node.id + " at line " + node.getLine() + " not declared");
            stErrors++;
        } else {
            node.entry = entry;
            node.nestingLevel = nestingLevel;
        }
        node.arguments.forEach(this::visit);
        return null;
    }

    @Override
    public Void visitNode(IdNode node) {
        if (print) printNode(node);
        STentry entry = stLookup(node.id);
        if (entry == null) {
            System.out.println("Var or Par id " + node.id + " at line " + node.getLine() + " not declared");
            stErrors++;
        } else {
            node.entry = entry;
            node.nestingLevel = nestingLevel;
        }
        return null;
    }

    @Override
    public Void visitNode(BoolNode node) {
        if (print) printNode(node, node.val.toString());
        return null;
    }

    @Override
    public Void visitNode(IntNode node) {
        if (print) printNode(node, node.val.toString());
        return null;
    }

    // new Nodes
    @Override
    public Void visitNode(MinusNode node) {
        if (print) printNode(node);
        visit(node.left);
        visit(node.right);
        return null;
    }

    @Override
    public Void visitNode(DivNode node) {
        if (print) printNode(node);
        visit(node.left);
        visit(node.right);
        return null;
    }

    @Override
    public Void visitNode(LessEqualNode node) {
        if (print) printNode(node);
        visit(node.left);
        visit(node.right);
        return null;
    }

    @Override
    public Void visitNode(GreaterEqualNode node) {
        if (print) printNode(node);
        visit(node.left);
        visit(node.right);
        return null;
    }

    @Override
    public Void visitNode(OrNode node) {
        if (print) printNode(node);
        visit(node.left);
        visit(node.right);
        return null;
    }

    @Override
    public Void visitNode(AndNode node) {
        if (print) printNode(node);
        visit(node.left);
        visit(node.right);
        return null;
    }

    @Override
    public Void visitNode(NotNode node) {
        if (print) printNode(node);
        visit(node.exp);
        return null;
    }

    // OBJECT-ORIENTED EXTENSION
    @Override
    public Void visitNode(final ClassNode node) {
        if (print) printNode(node);

        ClassTypeNode tempClassTypeNode = new ClassTypeNode();
        final boolean isSubClass = node.superId.isPresent();
        final String superId = isSubClass ? node.superId.get() : null;

        if (isSubClass) {
            // Check if the super class is declared
            if (classTable.containsKey(superId)) {
                final STentry superSTEntry = symbolTable.get(0).get(superId);
                final ClassTypeNode superTypeNode = (ClassTypeNode) superSTEntry.type;
                tempClassTypeNode = new ClassTypeNode(superTypeNode);
                node.superEntry = superSTEntry;
            } else {
                System.out.println("Class " + superId + " at line " + node.getLine() + " not declared");
                stErrors++;
            }
        }

        final ClassTypeNode classTypeNode = tempClassTypeNode;
        node.setType(classTypeNode);

        // Add the class id to the global scope table checking for duplicates
        final STentry entry = new STentry(0, classTypeNode, decOffset--);
        final Map<String, STentry> globalScopeTable = symbolTable.get(0);
        if (globalScopeTable.put(node.classId, entry) != null) {
            System.out.println("Class id " + node.classId + " at line " + node.getLine() + " already declared");
            stErrors++;
        }

        // Add the class to the class table
        final Set<String> visitedClassNames = new HashSet<>();
        final VirtualTable virtualTable = new VirtualTable();
        if (isSubClass) {
            final VirtualTable superClassVirtualTable = classTable.get(superId);
            virtualTable.putAll(superClassVirtualTable);
        }
        classTable.put(node.classId, virtualTable);

        symbolTable.add(virtualTable);
        // Setting the field offset
        nestingLevel++;
        int fieldOffset = -1;
        if (isSubClass) {
            final ClassTypeNode superTypeNode = (ClassTypeNode) symbolTable.get(0).get(superId).type;
            fieldOffset = -superTypeNode.fields.size() - 1;
        }

        /*
         * Handle field declaration.
         */
        for (final FieldNode field : node.fields) {
            if (visitedClassNames.contains(field.id)) {
                System.out.println(
                        "Field with id " + field.id + " on line " + field.getLine() + " was already declared"
                );
                stErrors++;
            } else {
                visitedClassNames.add(field.id);
            }
            visit(field);

            STentry fieldEntry = new STentry(nestingLevel, field.getType(), fieldOffset--);
            final boolean isFieldOverridden = isSubClass && virtualTable.containsKey(field.id);
            if (isFieldOverridden) {
                final STentry overriddenFieldEntry = virtualTable.get(field.id);
                final boolean isOverridingAMethod = overriddenFieldEntry.type instanceof MethodTypeNode;
                if (isOverridingAMethod) {
                    System.out.println("Cannot override method " + field.id + " with a field");
                    stErrors++;
                } else {
                    fieldEntry = new STentry(nestingLevel, field.getType(), overriddenFieldEntry.offset);
                    classTypeNode.fields.set(-fieldEntry.offset - 1, fieldEntry.type);
                }
            } else {
                classTypeNode.fields.add(-fieldEntry.offset - 1, fieldEntry.type);
            }

            // Add the field to the virtual table
            virtualTable.put(field.id, fieldEntry);
            field.offset = fieldEntry.offset;
        }

        // Setting the method offset
        int prevDecOffset = decOffset;
        decOffset = 0;
        if (isSubClass) {
            final ClassTypeNode superTypeNode = (ClassTypeNode) symbolTable.get(0).get(superId).type;
            decOffset = superTypeNode.methods.size();
        }

        for (final MethodNode method : node.methods) {
            if (visitedClassNames.contains(method.id)) {
                System.out.println(
                        "Method with id " + method.id + " on line " + method.getLine() + " was already declared"
                );
                stErrors++;
            } else {
                visitedClassNames.add(method.id);
            }
            visit(method);
            final MethodTypeNode methodTypeNode = (MethodTypeNode) symbolTable.get(nestingLevel).get(method.id).type;
            classTypeNode.methods.add(method.offset, methodTypeNode);
        }

        // Remove the class from the symbol table
        symbolTable.remove(nestingLevel--);
        decOffset = prevDecOffset;
        return null;
    }

    @Override
    public Void visitNode(final FieldNode node) {
        if (print) printNode(node);
        return null;
    }

    @Override
    public Void visitNode(final EmptyNode node) {
        if (print) printNode(node);
        return null;
    }

    @Override
    public Void visitNode(final MethodNode node) {
        if (print) printNode(node);
        final Map<String, STentry> currentTable = symbolTable.get(nestingLevel);
        final List<TypeNode> params = node.parameters.stream().map(ParNode::getType).toList();
        final boolean isOverriding = currentTable.containsKey(node.id);
        final TypeNode methodType = new MethodTypeNode(params, node.returnType);
        STentry entry = new STentry(nestingLevel, methodType, decOffset++);

        if (isOverriding) {
            final var overriddenMethodEntry = currentTable.get(node.id);
            final boolean isOverridingAMethod = overriddenMethodEntry != null && overriddenMethodEntry.type instanceof MethodTypeNode;
            if (isOverridingAMethod) {
                entry = new STentry(nestingLevel, methodType, overriddenMethodEntry.offset);
                decOffset--;
            } else {
                System.out.println("Cannot override a class attribute with a method: " + node.id);
                stErrors++;
            }
        }

        node.offset = entry.offset;
        currentTable.put(node.id, entry);

        // Create a new table for the method.
        nestingLevel++;
        final Map<String, STentry> methodTable = new HashMap<>();
        symbolTable.add(methodTable);

        // Set the declaration offset
        int prevDecOffset = decOffset;
        decOffset = -2;
        int parameterOffset = 1;

        for (final ParNode parameter : node.parameters) {
            final STentry parameterEntry = new STentry(nestingLevel, parameter.getType(), parameterOffset++);
            if (methodTable.put(parameter.id, parameterEntry) != null) {
                System.out.println("Par id " + parameter.id + " at line " + node.getLine() + " already declared");
                stErrors++;
            }
        }
        node.declarations.forEach(this::visit);
        visit(node.exp);

        // Remove the current nesting level symbolTable.
        symbolTable.remove(nestingLevel--);
        decOffset = prevDecOffset;
        return null;
    }

    // OPERATION NODES
    @Override
    public Void visitNode(final ClassCallNode node) {
        if (print) printNode(node);
        final STentry entry = stLookup(node.objectId);

        if (entry == null) {
            System.out.println("Object id " + node.objectId + " was not declared");
            stErrors++;
        } else if (entry.type instanceof final RefTypeNode refTypeNode) {
            node.entry = entry;
            node.nestingLevel = nestingLevel;
            final VirtualTable virtualTable = classTable.get(refTypeNode.typeId);
            if (virtualTable.containsKey(node.methodId)) {
                node.methodEntry = virtualTable.get(node.methodId);
            } else {
                System.out.println("Object id " + node.objectId + " at line " + node.getLine() + " has no method " + node.methodId);
                stErrors++;
            }
        } else {
            System.out.println("Object id " + node.objectId + " at line " + node.getLine() + " is not a RefType");
            stErrors++;
        }

        node.args.forEach(this::visit);
        return null;
    }

    @Override
    public Void visitNode(final NewNode node) {
        if (print) printNode(node);
        if (!classTable.containsKey(node.classId)) {
            System.out.println("Class id " + node.classId + " was not declared");
            stErrors++;
        }

        node.entry = symbolTable.getFirst().get(node.classId);
        node.args.forEach(this::visit);
        return null;
    }

    // OOP Type Nodes
    @Override
    public Void visitNode(final ClassTypeNode node) {
        if (print) printNode(node);
        return null;
    }

    @Override
    public Void visitNode(final MethodTypeNode node) {
        if (print) printNode(node);
        return null;
    }

    @Override
    public Void visitNode(final RefTypeNode node) {
        if (print) printNode(node);
        if (!this.classTable.containsKey(node.typeId)) {
            System.out.println("Class with id: " + node.typeId + " on line: " + node.getLine() + " was not declared");
            stErrors++;
        }
        return null;
    }

    @Override
    public Void visitNode(EmptyTypeNode node) {
        if (print) printNode(node);
        return null;
    }

}
