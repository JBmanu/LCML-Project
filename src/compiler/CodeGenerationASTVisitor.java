package compiler;

import compiler.AST.*;
import compiler.exc.VoidException;
import compiler.lib.BaseASTVisitor;
import compiler.lib.Node;
import svm.ExecuteVM;

import java.util.ArrayList;
import java.util.List;

import static compiler.lib.FOOLlib.*;

public class CodeGenerationASTVisitor extends BaseASTVisitor<String, VoidException> {
    private final List<List<String>> dispatchTables = new ArrayList<>();

    public CodeGenerationASTVisitor() {
    }

    CodeGenerationASTVisitor(boolean debug) {
        super(false, debug);
    } //enables print for debugging

    @Override
    public String visitNode(ProgLetInNode n) {
        if (print) printNode(n);
        String declCode = null;
        for (Node dec : n.declarations) declCode = nlJoin(declCode, visit(dec));
        return nlJoin(
                "push 0",
                declCode, // generate code for declarations (allocation)
                visit(n.exp),
                "halt",
                getCode()
        );
    }

    @Override
    public String visitNode(ProgNode n) {
        if (print) printNode(n);
        return nlJoin(
                visit(n.exp),
                "halt"
        );
    }

    @Override
    public String visitNode(FunNode n) {
        if (print) printNode(n, n.id);
        String declCode = null, popDecl = null, popParl = null;
        for (Node dec : n.declarations) {
            declCode = nlJoin(declCode, visit(dec));
            popDecl = nlJoin(popDecl, "pop");
        }
        for (int i = 0; i < n.parameters.size(); i++) popParl = nlJoin(popParl, "pop");
        String funl = freshFunLabel();
        putCode(
                nlJoin(
                        funl + ":",
                        "cfp", // set $fp to $sp value
                        "lra", // load $ra value
                        declCode, // generate code for local declarations (they use the new $fp!!!)
                        visit(n.exp), // generate code for function body expression
                        "stm", // set $tm to popped value (function result)
                        popDecl, // remove local declarations from stack
                        "sra", // set $ra to popped value
                        "pop", // remove Access Link from stack
                        popParl, // remove parameters from stack
                        "sfp", // set $fp to popped value (Control Link)
                        "ltm", // load $tm value (function result)
                        "lra", // load $ra value
                        "js"  // jump to to popped address
                )
        );
        return "push " + funl;
    }

    @Override
    public String visitNode(VarNode n) {
        if (print) printNode(n, n.id);
        return visit(n.exp);
    }

    @Override
    public String visitNode(PrintNode n) {
        if (print) printNode(n);
        return nlJoin(
                visit(n.exp),
                "print"
        );
    }

    @Override
    public String visitNode(IfNode n) {
        if (print) printNode(n);
        String l1 = freshLabel();
        String l2 = freshLabel();
        return nlJoin(
                visit(n.cond),
                "push 1",
                "beq " + l1,
                visit(n.el),
                "b " + l2,
                l1 + ":",
                visit(n.th),
                l2 + ":"
        );
    }

    @Override
    public String visitNode(EqualNode n) {
        if (print) printNode(n);
        String l1 = freshLabel();
        String l2 = freshLabel();
        return nlJoin(
                visit(n.left),
                visit(n.right),
                "beq " + l1,
                "push 0",
                "b " + l2,
                l1 + ":",
                "push 1",
                l2 + ":"
        );
    }

    @Override
    public String visitNode(TimesNode n) {
        if (print) printNode(n);
        return nlJoin(
                visit(n.left),
                visit(n.right),
                "mult"
        );
    }

    @Override
    public String visitNode(PlusNode n) {
        if (print) printNode(n);
        return nlJoin(
                visit(n.left),
                visit(n.right),
                "add"
        );
    }

    @Override
    public String visitNode(CallNode n) {
        if (print) printNode(n, n.id);
        String loadARAddress = "";
        if (n.entry.type instanceof MethodTypeNode) {
            loadARAddress = "lw";
        }
        String argCode = null, getAR = null;
        for (int i = n.arguments.size() - 1; i >= 0; i--)
            argCode = nlJoin(argCode, visit(n.arguments.get(i)));
        for (int i = 0; i < n.nl - n.entry.nl; i++)
            getAR = nlJoin(getAR, "lw");
        return nlJoin(
                "lfp", // load Control Link (pointer to frame of function "id" caller)
                argCode, // generate code for argument expressions in reversed order
                "lfp", getAR, // retrieve address of frame containing "id" declaration
                // by following the static chain (of Access Links)
                "stm", // set $tm to popped value (with the aim of duplicating top of stack)
                "ltm", // load Access Link (pointer to frame of function "id" declaration)
                "ltm", // duplicate top of stack
                loadARAddress,
                "push " + n.entry.offset, "add", // compute address of "id" declaration
                "lw", // load address of "id" function
                "js"  // jump to popped address (saving address of subsequent instruction in $ra)
        );
    }

    @Override
    public String visitNode(IdNode n) {
        if (print) printNode(n, n.id);
        String getAR = null;
        for (int i = 0; i < n.nl - n.entry.nl; i++) getAR = nlJoin(getAR, "lw");
        return nlJoin(
                "lfp", getAR, // retrieve address of frame containing "id" declaration
                // by following the static chain (of Access Links)
                "push " + n.entry.offset, "add", // compute address of "id" declaration
                "lw" // load value of "id" variable
        );
    }

    @Override
    public String visitNode(BoolNode n) {
        if (print) printNode(n, n.val.toString());
        return "push " + (n.val ? 1 : 0);
    }

    @Override
    public String visitNode(IntNode n) {
        if (print) printNode(n, n.val.toString());
        return "push " + n.val;
    }

    @Override
    public String visitNode(MinusNode n) {
        if (print) printNode(n);
        return nlJoin(
                visit(n.left),
                visit(n.right),
                "sub"
        );
    }

    @Override
    public String visitNode(DivNode n) {
        if (print) printNode(n);
        return nlJoin(
                visit(n.left),
                visit(n.right),
                "div"
        );
    }

    @Override
    public String visitNode(LessEqualNode n) {
        if (print) printNode(n);
        String l1 = freshLabel();
        String l2 = freshLabel();
        return nlJoin(
                visit(n.left),
                visit(n.right),
                "bleq " + l1,
                "push 0",
                "b " + l2,
                l1 + ":",
                "push 1",
                l2 + ":"
        );
    }

    @Override
    public String visitNode(GreaterEqualNode n) {
        if (print) printNode(n);
        String l1 = freshLabel();
        String l2 = freshLabel();
        return nlJoin(
                visit(n.right),
                visit(n.left),
                "bleq " + l1,
                "push 0",
                "b " + l2,
                l1 + ":",
                "push 1",
                l2 + ":"
        );
    }

    @Override
    public String visitNode(OrNode n) {
        if (print) printNode(n);
        String l1 = freshLabel();
        String l2 = freshLabel();
        return nlJoin(
                visit(n.left),
                "push 1",
                "beq " + l1,
                visit(n.right),
                "push 1",
                "beq " + l1,
                "push 0",
                "b " + l2,
                l1 + ":",
                "push 1",
                l2 + ":"
        );
    }

    @Override
    public String visitNode(AndNode n) {
        if (print) printNode(n);
        String l1 = freshLabel();
        String l2 = freshLabel();
        return nlJoin(
                visit(n.left),
                "push 0",
                "beq " + l1,
                visit(n.right),
                "push 0",
                "beq " + l1,
                "push 1",
                "b " + l2,
                l1 + ":",
                "push 0",
                l2 + ":"
        );
    }

    @Override
    public String visitNode(NotNode n) {
        if (print) printNode(n);
        String l1 = freshLabel();
        String l2 = freshLabel();
        return nlJoin(
                visit(n.exp),
                "push 0",
                "beq " + l1,
                "push 0",
                "b " + l2,
                l1 + ":",
                "push 1",
                l2 + ":"
        );
    }

    @Override
    public String visitNode(ClassNode node) {
        if (print) printNode(node, node.classId);
        final List<String> dispatchTable = new ArrayList<>();
        dispatchTables.add(dispatchTable);

        final boolean isSubclass = node.superEntry != null;
        if (isSubclass) {
            final List<String> superDispatchTable = dispatchTables.get(-node.superEntry.offset - 2);
            dispatchTable.addAll(superDispatchTable);
        }

        for (final MethodNode methodEntry : node.methods) {
            final boolean isOverriding = methodEntry.offset < dispatchTable.size();
            if (isOverriding) {
                dispatchTable.set(methodEntry.offset, methodEntry.label);
            } else {
                dispatchTable.add(methodEntry.label);
            }
            visit(methodEntry);
        }

        String dispatchTableHeapCode = "";
        for (final String label : dispatchTable) {
            dispatchTableHeapCode = nlJoin(
                    dispatchTableHeapCode,
                    // Store method label in heap
                    "push" + label,       // push method label
                    "lhp",  // push heap pointer
                    "sw",       // store method label in heap
                    // Increment heap pointer
                    "lhp",  // push heap pointer
                    "push 1",           // push 1
                    "add",                // heap pointer + 1
                    "shp"            // store heap pointer

            );
        }

        return nlJoin(
                "lhp",
                dispatchTableHeapCode
        );
    }

    @Override
    public String visitNode(MethodNode node) {
        if (print) printNode(node, node.id);

        String declarationsCode = null, popDeclarationsCode = null, popParametersCode = null;
        for (Node dec : node.declarations) {
            declarationsCode = nlJoin(declarationsCode, visit(dec));
            popDeclarationsCode = nlJoin(popDeclarationsCode, "pop");
        }
        for (int i = 0; i < node.parameters.size(); i++) {
            popParametersCode = nlJoin(popParametersCode, "pop");
        }

        String methodLabel = freshFunLabel();
        node.label = methodLabel;

        putCode(
                nlJoin(
                        methodLabel + ":",
                        "cfp", // set $fp to $sp value
                        "lra", // load $ra value
                        declarationsCode, // generate code for local declarations (they use the new $fp!!!)
                        visit(node.exp), // generate code for function body expression
                        "stm", // set $tm to popped value (function result)
                        popDeclarationsCode, // remove local declarations from stack
                        "sra", // set $ra to popped value
                        "pop", // remove Access Link from stack
                        popParametersCode, // remove parameters from stack
                        "sfp", // set $fp to popped value (Control Link)
                        "ltm", // load $tm value (function result)
                        "lra", // load $ra value
                        "js"  // jump to to popped address
                )
        );
        return null;
    }

    @Override
    public String visitNode(NewNode n) {
        if (print) printNode(n, n.classId);

        String argumentsCode = null, moveArgumentsOnHeapCode = null;
        for (int i = 0; i < n.args.size(); i++) {
            argumentsCode = nlJoin(argumentsCode, visit(n.args.get(i)));
        }

        for (int i = 0; i < n.args.size(); i++) {
            moveArgumentsOnHeapCode = nlJoin(
                    moveArgumentsOnHeapCode,
                    "lhp",
                    "sw",
                    "lhp",
                    "push 1",
                    "add",
                    "shp"
            );
        }

        return nlJoin(
                argumentsCode,
                moveArgumentsOnHeapCode,
                "push " + (ExecuteVM.MEMSIZE + n.entry.offset),
                "lw",
                "lhp",
                "sw",
                "lhp",
                "lhp",
                "push 1",
                "add",
                "shp"
        );
    }

    @Override
    public String visitNode(EmptyNode n) {
        if (print) printNode(n);
        return "push -1";
    }

    @Override
    public String visitNode(ClassCallNode n) {
        if (print) printNode(n, n.objectId);

        String argumentsCode = null, getARCode = null;

        for (int i = n.args.size() - 1; i >= 0; i--) {
            argumentsCode = nlJoin(argumentsCode, visit(n.args.get(i)));
        }

        for (int i = 0; i < n.nestingLevel - n.entry.nl; i++) {
            getARCode = nlJoin(getARCode, "lw");
        }

        return nlJoin(
                "lfp", // load Control Link (pointer to frame of function "id" caller)
                argumentsCode, // generate code for argument expressions in reversed order
                "lfp", getARCode, // retrieve address of frame containing "id" declaration
                // by following the static chain (of Access Links)
                "push " + n.entry.offset,
                "add", // compute address of "id" declaration
                "lw", // load address of "id" function
                "stm", // set $tm to popped value (with the aim of duplicating top of stack)
                "ltm", // load Access Link (pointer to frame of function "id" declaration)
                "ltm", // duplicate top of stack
                "lw", // load address of dispatch table
                "push " + n.methodEntry.offset,
                "add",
                "lw", // load address of method
                "js"  // jump to popped address (saving address of subsequent instruction in $ra)
        );
    }

}