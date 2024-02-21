package test;

import org.junit.jupiter.api.Test;

public class OperatorsTest {
    private static final String ROOT_OPERATORS_TEST_FILES = "res/test/operators/minus/";

    @Test
    public void testMinusOperator() {
        String fileName = ROOT_OPERATORS_TEST_FILES + "minus.fool";

        ToolsForTest tools = new ToolsForTest();
        tools.createFOOLFile(fileName, "let var x:int = 5 - 3; in print(x);");
        tools.buildASTAndSVMAndCheckErrors(fileName, false);
        tools.runningSVM();
    }

    // testing 10 --2
    @Test
    public void testDoubleMinusOperator() {
        String fileName = ROOT_OPERATORS_TEST_FILES + "doubleMinus.fool";

        ToolsForTest tools = new ToolsForTest();
        tools.createFOOLFile(fileName, "let var x:int = 10 - -2; in print(x);");
        tools.buildASTAndSVMAndCheckErrors(fileName, false);
        tools.runningSVM();
    }


}