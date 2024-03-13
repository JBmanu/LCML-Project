package compiler;

import compiler.AST.*;
import compiler.exc.VoidException;
import compiler.lib.BaseASTVisitor;
import compiler.lib.TypeNode;

import java.util.*;

public class SymbolTableASTVisitor extends BaseASTVisitor<Void, VoidException> {
    static class VirtualTable extends HashMap<String, STentry> {
    }

    private List<Map<String, STentry>> symbolTable = new ArrayList<>();
    private final Map<String, VirtualTable> classTable = new HashMap<>();

    private int nestingLevel = 0; // current nesting level
    private int decOffset = -2; // counter for offset of local declarations at current nesting level
    public int stErrors = 0;

    public SymbolTableASTVisitor() {
    }

    SymbolTableASTVisitor(boolean debug) {
        super(debug);
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
        Map<String, STentry> hm = new HashMap<>();
        symbolTable.add(hm);
        node.declist.forEach(this::visit);
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
        if (!Objects.isNull(currentSymbolTable.put(node.id, entry))) {
            System.out.println("Fun id " + node.id + " at line " + node.getLine() + " already declared");
            stErrors++;
        }
        //creare una nuova hashmap per la symTable
        nestingLevel++;
        final Map<String, STentry> newSymbolTable = new HashMap<>();
        symbolTable.add(newSymbolTable);
        int prevNLDecOffset = decOffset; // stores counter for offset of declarations at previous nesting level
        decOffset = -2;

        int parOffset = 1;
        for (ParNode par : node.parameters) {
            if (newSymbolTable.put(par.id, new STentry(nestingLevel, par.getType(), parOffset++)) != null) {
                System.out.println("Par id " + par.id + " at line " + node.getLine() + " already declared");
                stErrors++;
            }
        }
        node.declarations.forEach(this::visit);
        visit(node.exp);

        //rimuovere la hashmap corrente poiche' esco dallo scope
        symbolTable.remove(nestingLevel--);
        decOffset = prevNLDecOffset; // restores counter for offset of declarations at previous nesting level
        return null;
    }

    @Override
    public Void visitNode(VarNode node) {
        if (print) printNode(node);
        visit(node.exp);
        final Map<String, STentry> currentSymbolTable = symbolTable.get(nestingLevel);
        final STentry entry = new STentry(nestingLevel, node.getType(), decOffset--);
        //inserimento di ID nella symtable
        if (!Objects.isNull(currentSymbolTable.put(node.id, entry))) {
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
        visit(node.th);
        visit(node.el);
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

        if (Objects.isNull(entry)) {
            System.out.println("Fun id " + node.id + " at line " + node.getLine() + " not declared");
            stErrors++;
        } else {
            node.entry = entry;
            node.nl = nestingLevel;
        }
        node.arguments.forEach(this::visit);
        return null;
    }

    @Override
    public Void visitNode(IdNode node) {
        if (print) printNode(node);
        STentry entry = stLookup(node.id);
        if (Objects.isNull(entry)) {
            System.out.println("Var or Par id " + node.id + " at line " + node.getLine() + " not declared");
            stErrors++;
        } else {
            node.entry = entry;
            node.nl = nestingLevel;
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

//    @Override
//    public Void visitNode(ClassNode node) {
//        if (print) printNode(node);
//
//    }

}
