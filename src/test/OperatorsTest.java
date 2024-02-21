package test;

import org.junit.jupiter.api.Test;

public class OperatorsTest {
    private static final String ROOT_MINUS_TEST_FILES = "res/test/operators/minus/";
    private static final String ROOT_DIV_TEST_FILES = "res/test/operators/div/";
    private static final String ROOT_GE_TEST_FILES = "res/test/operators/greaterEquals/";
    private static final String ROOT_LE_TEST_FILES = "res/test/operators/lessEquals/";
    private static final String ROOT_NOT_TEST_FILES = "res/test/operators/not/";
    private static final String ROOT_AND_TEST_FILES = "res/test/operators/and/";
    private static final String ROOT_OR_TEST_FILES = "res/test/operators/or/";

    @Test
    public void testMinusOperator() {
        String fileName = ROOT_MINUS_TEST_FILES + "minus.fool";

        ToolsForTest tools = new ToolsForTest();
        tools.createFOOLFile(fileName, "let var x:int = 5 - 3; in print(x);");
        tools.buildASTAndSVMAndCheckErrors(fileName, false);
        tools.runningSVM();
    }

    // testing 10 --2
    @Test
    public void testDoubleMinusOperator() {
        String fileName = ROOT_MINUS_TEST_FILES + "doubleMinus.fool";

        ToolsForTest tools = new ToolsForTest();
        tools.createFOOLFile(fileName, "let var x:int = 10 - -2; in print(x);");
        tools.buildASTAndSVMAndCheckErrors(fileName, false);
        tools.runningSVM();
    }

    // testing 10 / 5
    @Test
    public void testDivOperator() {
        String fileName = ROOT_DIV_TEST_FILES + "div.fool";

        ToolsForTest tools = new ToolsForTest();
        tools.createFOOLFile(fileName, "let var x:int = 10 / 5; in print(x);");
        tools.buildASTAndSVMAndCheckErrors(fileName, false);
        tools.runningSVM();
    }

    // testing 10 / 3
    @Test
    public void testDivOperator2() {
        String fileName = ROOT_DIV_TEST_FILES + "div2.fool";

        ToolsForTest tools = new ToolsForTest();
        tools.createFOOLFile(fileName, "let var x:int = 10 / 3; in print(x);");
        tools.buildASTAndSVMAndCheckErrors(fileName, false);
        tools.runningSVM();
    }

}