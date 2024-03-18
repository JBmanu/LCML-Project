package compiler;

import compiler.AST.*;
import compiler.exc.VoidException;
import compiler.lib.BaseASTVisitor;
import compiler.lib.DecNode;
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
    private static final String SUB = "sub";
    private static final String DIV = "div";
    private static final String BRANCH_LESS_EQUAL = "bleq ";
    private static final String LOAD_HEAP_POINTER = "lhp";
    private static final String STORE_WORD = "sw";
    private static final String STORE_HP = "shp";
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
    public String visitNode(VarNode node) {
        if (print) printNode(node, node.id);
        return visit(node.exp);
    }

    @Override
    public String visitNode(PrintNode node) {
        if (print) printNode(node);
        return nlJoin(
                visit(node.exp),
                PRINT
        );
    }

    @Override
    public String visitNode(IfNode node) {
        if (print) printNode(node);
        String thenLabel = freshLabel();
        String endLabel = freshLabel();
        return nlJoin(
                visit(node.cond),
                PUSH + "1",
                BRANCH_EQUAL + thenLabel,
                visit(node.elseBranch),
                BRANCH + endLabel,
                thenLabel + ":",
                visit(node.thenBranch),
                endLabel + ":"
        );
    }

    @Override
    public String visitNode(EqualNode node) {
        if (print) printNode(node);
        String trueLabel = freshLabel();
        String endLabel = freshLabel();
        return nlJoin(
                visit(node.left),
                visit(node.right),
                BRANCH_EQUAL + trueLabel,
                PUSH + 0,
                BRANCH + endLabel,
                trueLabel + ":",
                PUSH + 1,
                endLabel + ":"
        );
    }

    @Override
    public String visitNode(TimesNode node) {
        if (print) printNode(node);
        return nlJoin(
                visit(node.left),
                visit(node.right),
                MULT
        );
    }

    @Override
    public String visitNode(PlusNode node) {
        if (print) printNode(node);
        return nlJoin(
                visit(node.left),
                visit(node.right),
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
    public String visitNode(IdNode node) {
        if (print) printNode(node, node.id);
        String getARCode = null;
        for (int i = 0; i < node.nestingLevel - node.entry.nl; i++)
            getARCode = nlJoin(getARCode, LOAD_WORD);
        return nlJoin(
                LOAD_FP, getARCode, // retrieve address of frame containing "id" declaration
                // by following the static chain (of Access Links)
                PUSH + node.entry.offset,
                ADD, // compute address of "id" declaration
                LOAD_WORD // load value of "id" variable
        );
    }

    @Override
    public String visitNode(BoolNode node) {
        if (print) printNode(node, String.valueOf(node.value));
        return PUSH + (node.value ? 1 : 0); // push 1 if true, 0 if false
    }

    @Override
    public String visitNode(IntNode node) {
        if (print) printNode(node, node.value.toString());
        return PUSH + node.value;
    }

    @Override
    public String visitNode(MinusNode node) {
        if (print) printNode(node);
        return nlJoin(
                visit(node.left),
                visit(node.right),
                SUB
        );
    }

    @Override
    public String visitNode(DivNode node) {
        if (print) printNode(node);
        return nlJoin(
                visit(node.left),
                visit(node.right),
                DIV
        );
    }

    @Override
    public String visitNode(LessEqualNode node) {
        if (print) printNode(node);
        String trueLabel = freshLabel();
        String endLabel = freshLabel();
        return nlJoin(
                visit(node.left),
                visit(node.right),
                BRANCH_LESS_EQUAL + trueLabel,
                PUSH + 0,
                BRANCH + endLabel,
                trueLabel + ":",
                PUSH + "1",
                endLabel + ":"
        );
    }

    @Override
    public String visitNode(GreaterEqualNode node) {
        if (print) printNode(node);
        String trueLabel = freshLabel();
        String endLabel = freshLabel();
        return nlJoin(
                visit(node.right),
                visit(node.left),
                BRANCH_LESS_EQUAL + trueLabel,
                PUSH + 0,
                BRANCH + endLabel,
                trueLabel + ":",
                PUSH + 1,
                endLabel + ":"
        );
    }

    @Override
    public String visitNode(OrNode node) {
        if (print) printNode(node);
        String trueLabel = freshLabel();
        String endLabel = freshLabel();
        return nlJoin(
                visit(node.left),
                PUSH + 1,
                BRANCH_EQUAL + trueLabel,
                visit(node.right),
                PUSH + 1,
                BRANCH_EQUAL + trueLabel,
                PUSH + 0,
                BRANCH + endLabel,
                trueLabel + ":",
                PUSH + 1,
                endLabel + ":"
        );
    }

    @Override
    public String visitNode(AndNode node) {
        if (print) printNode(node);
        String falseLabel = freshLabel();
        String endLabel = freshLabel();
        return nlJoin(
                visit(node.left),
                PUSH + 0,
                BRANCH_EQUAL + falseLabel,
                visit(node.right),
                PUSH + 0,
                BRANCH_EQUAL + falseLabel,
                PUSH + 1,
                BRANCH + endLabel,
                falseLabel + ":",
                PUSH + 0,
                endLabel + ":"
        );
    }

    @Override
    public String visitNode(NotNode node) {
        if (print) printNode(node);
        String itWasFalseLabel = freshLabel();
        String endLabel = freshLabel();
        return nlJoin(
                visit(node.exp),
                PUSH + 0,
                BRANCH_EQUAL + itWasFalseLabel,
                PUSH + 0,
                BRANCH + endLabel,
                itWasFalseLabel + ":",
                PUSH + 1,
                endLabel + ":"
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
            visit(methodEntry);

            final boolean isOverriding = methodEntry.offset < dispatchTable.size();
            if (isOverriding) {
                dispatchTable.set(methodEntry.offset, methodEntry.label);
            } else {
                dispatchTable.add(methodEntry.label);
            }
        }

        String dispatchTableHeapCode = "";
        for (final String label : dispatchTable) {
            dispatchTableHeapCode = nlJoin(
                    dispatchTableHeapCode,
                    // Store method label in heap
                    PUSH + label,       // push method label
                    LOAD_HEAP_POINTER,  // push heap pointer
                    STORE_WORD,       // store method label in heap
                    // Increment heap pointer
                    LOAD_HEAP_POINTER,  // push heap pointer
                    PUSH + 1,           // push 1
                    ADD,                // heap pointer + 1
                    STORE_HP            // store heap pointer
            );
        }

        return nlJoin(
                LOAD_HEAP_POINTER,
                dispatchTableHeapCode
        );
    }

    @Override
    public String visitNode(MethodNode node) {
        if (print) printNode(node);

        String declarationsCode = null;
        String popDeclarationsCode = null;
        String popParametersCode = null;

        for (final DecNode declaration : node.declarations) {
            declarationsCode = nlJoin(declarationsCode, visit(declaration));
            popDeclarationsCode = nlJoin(popDeclarationsCode, POP);
        }

        for (final ParNode parameter : node.parameters) {
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
    public String visitNode(NewNode node) {
        if (print) printNode(node, node.classId);

        String argumentsCode = "";
        String moveArgumentsOnHeapCode = "";

        for (final Node argument : node.args) {
            argumentsCode = nlJoin(argumentsCode, visit(argument));
        }

        for (final Node argument : node.args) {
            moveArgumentsOnHeapCode = nlJoin(
                    moveArgumentsOnHeapCode,
                    LOAD_HEAP_POINTER,
                    STORE_WORD,
                    LOAD_HEAP_POINTER,
                    PUSH + 1,
                    ADD,
                    STORE_HP
            );
        }

        return nlJoin(
                argumentsCode,
                moveArgumentsOnHeapCode,
                PUSH + (ExecuteVM.MEMSIZE + node.entry.offset),
                LOAD_WORD,
                LOAD_HEAP_POINTER,
                STORE_WORD,
                LOAD_HEAP_POINTER,
                LOAD_HEAP_POINTER,
                PUSH + 1,
                ADD,
                STORE_HP
        );
    }

    @Override
    public String visitNode(EmptyNode n) {
        if (print) printNode(n);
        return PUSH + "-1";
    }

    @Override
    public String visitNode(ClassCallNode node) {
        if (print) printNode(node, node.objectId);

        String argumentsCode = null;
        String getARCode = null;

        for (int i = node.args.size() - 1; i >= 0; i--) {
            argumentsCode = nlJoin(argumentsCode, visit(node.args.get(i)));
        }

        for (int i = 0; i < node.nestingLevel - node.entry.nl; i++) {
            getARCode = nlJoin(getARCode, LOAD_WORD);
        }

        return nlJoin(
                LOAD_FP, // load Control Link (pointer to frame of function "id" caller)
                argumentsCode, // generate code for argument expressions in reversed order
                LOAD_FP, getARCode, // retrieve address of frame containing "id" declaration
                // by following the static chain (of Access Links)
                PUSH + node.entry.offset,
                ADD, // compute address of "id" declaration
                LOAD_WORD, // load address of "id" function
                STORE_TM, // set $tm to popped value (with the aim of duplicating top of stack)
                LOAD_TM, // load Access Link (pointer to frame of function "id" declaration)
                LOAD_TM, // duplicate top of stack
                LOAD_WORD, // load address of dispatch table
                PUSH + node.methodEntry.offset,
                ADD,
                LOAD_WORD, // load address of method
                JUMP_SUBROUTINE  // jump to popped address (saving address of subsequent instruction in $ra)
        );
    }

}