package compiler;

import compiler.AST.*;
import compiler.exc.VoidException;
import compiler.lib.BaseASTVisitor;
import compiler.lib.Node;
import svm.ExecuteVM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static compiler.lib.FOOLlib.*;

public class CodeGenerationASTVisitor extends BaseASTVisitor<String, VoidException> {
    private static final String PUSH = "push ";
    private static final String HALT = "halt";
    private static final String POP = "pop";
    private static final String COPY_FP = "cfp";
    private static final String LOAD_RA = "lra";
    private static final String STORE_TM = "stm";
    private static final String STORE_RA = "sra";
    private static final String STORE_FP = "sfp";
    private static final String LOAD_TM = "ltm";
    private static final String JUMP_SUBROUTINE = "js";
    private static final String PRINT = "print";
    private static final String BRANCH_EQUAL = "beq ";
    private static final String BRANCH = "b ";
    private static final String MULT = "mult";
    private static final String ADD = "add";
    private static final String LOAD_WORD = "lw";
    private static final String LOAD_FP = "lfp";
    private final List<List<String>> dispatchTables = new ArrayList<>();

    public CodeGenerationASTVisitor() {
    }

    CodeGenerationASTVisitor(boolean debug) {
        super(false, debug);
    } //enables print for debugging

    @Override
    public String visitNode(ProgLetInNode node) {
        if (print) printNode(node);
        String declarationsCode = null;
        for (Node dec : node.declarations)
            declarationsCode = nlJoin(declarationsCode, visit(dec));
        return nlJoin(
                PUSH + 0,
                declarationsCode, // generate code for declarations (allocation)
                visit(node.exp),
                HALT,
                getCode()
        );
    }

    @Override
    public String visitNode(ProgNode node) {
        if (print) printNode(node);
        return nlJoin(
                visit(node.exp),
                HALT
        );
    }

    @Override
    public String visitNode(FunNode node) {
        if (print) printNode(node, node.id);
        String declarationsCode = null;
        String popDeclarationsCode = null;
        String popParametersCode = null;

        for (Node declaration : node.declarations) {
            declarationsCode = nlJoin(declarationsCode, visit(declaration));
            popDeclarationsCode = nlJoin(popDeclarationsCode, POP);
        }

        for (final ParNode parameter : node.parameters) {
            popParametersCode = nlJoin(popParametersCode, POP);
        }

        final String funLabel = freshFunLabel();
        putCode(
                nlJoin(
                        funLabel + ":",
                        COPY_FP, // set $fp to $sp value
                        LOAD_RA, // load $ra value
                        declarationsCode, // generate code for local declarations (they use the new $fp!!!)
                        visit(node.exp), // generate code for function body expression
                        STORE_TM, // set $tm to popped value (function result)
                        popDeclarationsCode, // remove local declarations from stack
                        STORE_RA, // set $ra to popped value
                        POP, // remove Access Link from stack
                        popParametersCode, // remove parameters from stack
                        STORE_FP, // set $fp to popped value (Control Link)
                        LOAD_TM, // load $tm value (function result)
                        LOAD_RA, // load $ra value
                        JUMP_SUBROUTINE  // jump to to popped address
                )
        );
        return PUSH + funLabel;
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
                PRINT
        );
    }

    @Override
    public String visitNode(IfNode n) {
        if (print) printNode(n);
        String thenLabel = freshLabel();
        String endLabel = freshLabel();
        return nlJoin(
                visit(n.cond),
                PUSH + "1",
                BRANCH_EQUAL + thenLabel,
                visit(n.elseBranch),
                BRANCH + endLabel,
                thenLabel + ":",
                visit(n.thenBranch),
                endLabel + ":"
        );
    }

    @Override
    public String visitNode(EqualNode n) {
        if (print) printNode(n);
        String trueLabel = freshLabel();
        String endLabel = freshLabel();
        return nlJoin(
                visit(n.left),
                visit(n.right),
                BRANCH_EQUAL + trueLabel,
                PUSH + 0,
                BRANCH + endLabel,
                trueLabel + ":",
                PUSH + 1,
                endLabel + ":"
        );
    }

    @Override
    public String visitNode(TimesNode n) {
        if (print) printNode(n);
        return nlJoin(
                visit(n.left),
                visit(n.right),
                MULT
        );
    }

    @Override
    public String visitNode(PlusNode n) {
        if (print) printNode(n);
        return nlJoin(
                visit(n.left),
                visit(n.right),
                ADD
        );
    }

    @Override
    public String visitNode(CallNode node) {
        if (print) printNode(node, node.id);

        final String loadARAddress = node.entry.type instanceof MethodTypeNode ? LOAD_WORD : "";

        String argumentsCode = null;
        String getARCode = null;

        final List<Node> reversedArgumentsCode = new ArrayList<>(node.arguments);
        Collections.reverse(reversedArgumentsCode);
        for (final Node argument : reversedArgumentsCode) {
            argumentsCode = nlJoin(argumentsCode, visit(argument));
        }
        for (int i = 0; i < node.nestingLevel - node.entry.nl; i++) {
            getARCode = nlJoin(getARCode, LOAD_WORD);
        }

        return nlJoin(
                LOAD_FP, // load Control Link (pointer to frame of function "id" caller)
                argumentsCode, // generate code for argument expressions in reversed order
                LOAD_FP,
                getARCode, // retrieve address of frame containing "id" declaration
                // by following the static chain (of Access Links)
                STORE_TM, // set $tm to popped value (with the aim of duplicating top of stack)
                LOAD_TM, // load Access Link (pointer to frame of function "id" declaration)
                LOAD_TM, // duplicate top of stack
                loadARAddress,
                PUSH + node.entry.offset,
                ADD, // compute address of "id" declaration
                LOAD_WORD, // load address of "id" function
                JUMP_SUBROUTINE  // jump to popped address (saving address of subsequent instruction in $ra)
        );
    }

    @Override
    public String visitNode(IdNode n) {
        if (print) printNode(n, n.id);
        String getARCode = null;
        for (int i = 0; i < n.nestingLevel - n.entry.nl; i++)
            getARCode = nlJoin(getARCode, LOAD_WORD);
        return nlJoin(
                LOAD_FP, getARCode, // retrieve address of frame containing "id" declaration
                // by following the static chain (of Access Links)
                PUSH + n.entry.offset, ADD, // compute address of "id" declaration
                LOAD_WORD // load value of "id" variable
        );
    }

    @Override
    public String visitNode(BoolNode n) {
        if (print) printNode(n, n.val.toString());
        return PUSH + (n.val ? 1 : 0);
    }

    @Override
    public String visitNode(IntNode n) {
        if (print) printNode(n, n.val.toString());
        return PUSH + n.val;
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
                PUSH + "0",
                BRANCH + l2,
                l1 + ":",
                PUSH + "1",
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
                PUSH + "0",
                BRANCH + l2,
                l1 + ":",
                PUSH + "1",
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
                PUSH + "1",
                BRANCH_EQUAL + l1,
                visit(n.right),
                PUSH + "1",
                BRANCH_EQUAL + l1,
                PUSH + "0",
                BRANCH + l2,
                l1 + ":",
                PUSH + "1",
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
                PUSH + "0",
                BRANCH_EQUAL + l1,
                visit(n.right),
                PUSH + "0",
                BRANCH_EQUAL + l1,
                PUSH + "1",
                BRANCH + l2,
                l1 + ":",
                PUSH + "0",
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
                PUSH + "0",
                BRANCH_EQUAL + l1,
                PUSH + "0",
                BRANCH + l2,
                l1 + ":",
                PUSH + "1",
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
                    PUSH + label,       // push method label
                    "lhp",  // push heap pointer
                    "sw",       // store method label in heap
                    // Increment heap pointer
                    "lhp",  // push heap pointer
                    PUSH + "1",           // push 1
                    ADD,                // heap pointer + 1
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
            popDeclarationsCode = nlJoin(popDeclarationsCode, POP);
        }
        for (int i = 0; i < node.parameters.size(); i++) {
            popParametersCode = nlJoin(popParametersCode, POP);
        }

        String methodLabel = freshFunLabel();
        node.label = methodLabel;

        putCode(
                nlJoin(
                        methodLabel + ":",
                        COPY_FP, // set $fp to $sp value
                        LOAD_RA, // load $ra value
                        declarationsCode, // generate code for local declarations (they use the new $fp!!!)
                        visit(node.exp), // generate code for function body expression
                        STORE_TM, // set $tm to popped value (function result)
                        popDeclarationsCode, // remove local declarations from stack
                        STORE_RA, // set $ra to popped value
                        POP, // remove Access Link from stack
                        popParametersCode, // remove parameters from stack
                        STORE_FP, // set $fp to popped value (Control Link)
                        LOAD_TM, // load $tm value (function result)
                        LOAD_RA, // load $ra value
                        JUMP_SUBROUTINE  // jump to to popped address
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
                    PUSH + "1",
                    ADD,
                    "shp"
            );
        }

        return nlJoin(
                argumentsCode,
                moveArgumentsOnHeapCode,
                PUSH + (ExecuteVM.MEMSIZE + n.entry.offset),
                LOAD_WORD,
                "lhp",
                "sw",
                "lhp",
                "lhp",
                PUSH + "1",
                ADD,
                "shp"
        );
    }

    @Override
    public String visitNode(EmptyNode n) {
        if (print) printNode(n);
        return PUSH + "-1";
    }

    @Override
    public String visitNode(ClassCallNode n) {
        if (print) printNode(n, n.objectId);

        String argumentsCode = null, getARCode = null;

        for (int i = n.args.size() - 1; i >= 0; i--) {
            argumentsCode = nlJoin(argumentsCode, visit(n.args.get(i)));
        }

        for (int i = 0; i < n.nestingLevel - n.entry.nl; i++) {
            getARCode = nlJoin(getARCode, LOAD_WORD);
        }

        return nlJoin(
                LOAD_FP, // load Control Link (pointer to frame of function "id" caller)
                argumentsCode, // generate code for argument expressions in reversed order
                LOAD_FP, getARCode, // retrieve address of frame containing "id" declaration
                // by following the static chain (of Access Links)
                PUSH + n.entry.offset,
                ADD, // compute address of "id" declaration
                LOAD_WORD, // load address of "id" function
                STORE_TM, // set $tm to popped value (with the aim of duplicating top of stack)
                LOAD_TM, // load Access Link (pointer to frame of function "id" declaration)
                LOAD_TM, // duplicate top of stack
                LOAD_WORD, // load address of dispatch table
                PUSH + n.methodEntry.offset,
                ADD,
                LOAD_WORD, // load address of method
                JUMP_SUBROUTINE  // jump to popped address (saving address of subsequent instruction in $ra)
        );
    }

}