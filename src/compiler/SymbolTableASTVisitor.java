package src.compiler;

import src.compiler.AST.*;
import src.compiler.exc.VoidException;
import src.compiler.lib.BaseASTVisitor;
import src.compiler.lib.Node;
import src.compiler.lib.TypeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTableASTVisitor extends BaseASTVisitor<Void, VoidException> {

    private final List<Map<String, STentry>> symTable = new ArrayList<>();
    private int nestingLevel = 0; // current nesting level
    private int decOffset = -2; // counter for offset of local declarations at current nesting level
    int stErrors = 0;

    SymbolTableASTVisitor() {
    }

    SymbolTableASTVisitor(boolean debug) {
        super(debug);
    } // enables print for debugging

    private STentry stLookup(String id) {
        int j = this.nestingLevel;
        STentry entry = null;
        while (j >= 0 && entry == null)
            entry = this.symTable.get(j--).get(id);
        return entry;
    }

    @Override
    public Void visitNode(ProgLetInNode n) {
        if (this.print) this.printNode(n);
        Map<String, STentry> hm = new HashMap<>();
        this.symTable.add(hm);
        for (Node dec : n.declist) this.visit(dec);
        this.visit(n.exp);
        this.symTable.remove(0);
        return null;
    }

    @Override
    public Void visitNode(ProgNode n) {
        if (this.print) this.printNode(n);
        this.visit(n.exp);
        return null;
    }

    @Override
    public Void visitNode(FunNode n) {
        if (this.print) this.printNode(n);
        Map<String, STentry> hm = this.symTable.get(this.nestingLevel);
        List<TypeNode> parTypes = new ArrayList<>();
        for (ParNode par : n.parlist) parTypes.add(par.getType());
        STentry entry = new STentry(this.nestingLevel, new ArrowTypeNode(parTypes, n.retType), this.decOffset--);
        //inserimento di ID nella symtable
        if (hm.put(n.id, entry) != null) {
            System.out.println("Fun id " + n.id + " at line " + n.getLine() + " already declared");
            this.stErrors++;
        }
        //creare una nuova hashmap per la symTable
        this.nestingLevel++;
        Map<String, STentry> hmn = new HashMap<>();
        this.symTable.add(hmn);
        int prevNLDecOffset = this.decOffset; // stores counter for offset of declarations at previous nesting level
        this.decOffset = -2;

        int parOffset = 1;
        for (ParNode par : n.parlist)
            if (hmn.put(par.id, new STentry(this.nestingLevel, par.getType(), parOffset++)) != null) {
                System.out.println("Par id " + par.id + " at line " + n.getLine() + " already declared");
                this.stErrors++;
            }
        for (Node dec : n.declist) this.visit(dec);
        this.visit(n.exp);
        //rimuovere la hashmap corrente poiche' esco dallo scope
        this.symTable.remove(this.nestingLevel--);
        this.decOffset = prevNLDecOffset; // restores counter for offset of declarations at previous nesting level
        return null;
    }

    @Override
    public Void visitNode(VarNode n) {
        if (this.print) this.printNode(n);
        this.visit(n.exp);
        Map<String, STentry> hm = this.symTable.get(this.nestingLevel);
        STentry entry = new STentry(this.nestingLevel, n.getType(), this.decOffset--);
        //inserimento di ID nella symtable
        if (hm.put(n.id, entry) != null) {
            System.out.println("Var id " + n.id + " at line " + n.getLine() + " already declared");
            this.stErrors++;
        }
        return null;
    }

    @Override
    public Void visitNode(PrintNode n) {
        if (this.print) this.printNode(n);
        this.visit(n.exp);
        return null;
    }

    @Override
    public Void visitNode(IfNode n) {
        if (this.print) this.printNode(n);
        this.visit(n.cond);
        this.visit(n.th);
        this.visit(n.el);
        return null;
    }

    @Override
    public Void visitNode(EqualNode n) {
        if (this.print) this.printNode(n);
        this.visit(n.left);
        this.visit(n.right);
        return null;
    }

    @Override
    public Void visitNode(TimesNode n) {
        if (this.print) this.printNode(n);
        this.visit(n.left);
        this.visit(n.right);
        return null;
    }

    @Override
    public Void visitNode(PlusNode n) {
        if (this.print) this.printNode(n);
        this.visit(n.left);
        this.visit(n.right);
        return null;
    }

    @Override
    public Void visitNode(CallNode n) {
        if (this.print) this.printNode(n);
        STentry entry = this.stLookup(n.id);
        if (entry == null) {
            System.out.println("Fun id " + n.id + " at line " + n.getLine() + " not declared");
            this.stErrors++;
        } else {
            n.entry = entry;
            n.nl = this.nestingLevel;
        }
        for (Node arg : n.arglist) this.visit(arg);
        return null;
    }

    @Override
    public Void visitNode(IdNode n) {
        if (this.print) this.printNode(n);
        STentry entry = this.stLookup(n.id);
        if (entry == null) {
            System.out.println("Var or Par id " + n.id + " at line " + n.getLine() + " not declared");
            this.stErrors++;
        } else {
            n.entry = entry;
            n.nl = this.nestingLevel;
        }
        return null;
    }

    @Override
    public Void visitNode(BoolNode n) {
        if (this.print) this.printNode(n, n.val.toString());
        return null;
    }

    @Override
    public Void visitNode(IntNode n) {
        if (this.print) this.printNode(n, n.val.toString());
        return null;
    }
}
