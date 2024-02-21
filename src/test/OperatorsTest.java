package test;

import org.junit.jupiter.api.Test;

public class OperatorsTest {
    // 0=FALSE
    // 1=TRUE

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

    // testing lessEquals 4 <= 2
    @Test
    public void testLessEqualsOperator() {
        String fileName = ROOT_LE_TEST_FILES + "lessEquals.fool";

        ToolsForTest tools = new ToolsForTest();
        tools.createFOOLFile(fileName, "let var x:bool = 4 <= 2; in print(x);");
        tools.buildASTAndSVMAndCheckErrors(fileName, false);
        tools.runningSVM();
    }

    // testing lessEquals 4 <= 5
    @Test
    public void testLessEqualsOperator2() {
        String fileName = ROOT_LE_TEST_FILES + "lessEquals2.fool";

        ToolsForTest tools = new ToolsForTest();
        tools.createFOOLFile(fileName, "let var x:bool = 4 <= 5; in print(x);");
        tools.buildASTAndSVMAndCheckErrors(fileName, false);
        tools.runningSVM();
    }

    // testing greaterEquals 4 <= 4
    @Test
    public void testGreaterEqualsOperator() {
        String fileName = ROOT_LE_TEST_FILES + "lessEquals3.fool";

        ToolsForTest tools = new ToolsForTest();
        tools.createFOOLFile(fileName, "let var x:bool = 4 <= 4; in print(x);");
        tools.buildASTAndSVMAndCheckErrors(fileName, false);
        tools.runningSVM();
    }

    // testing greaterEquals 4 >= 5
    @Test
    public void testGreaterEqualsOperator2() {
        String fileName = ROOT_GE_TEST_FILES + "greaterEquals.fool";

        ToolsForTest tools = new ToolsForTest();
        tools.createFOOLFile(fileName, "let var x:bool = 4 >= 5; in print(x);");
        tools.buildASTAndSVMAndCheckErrors(fileName, false);
        tools.runningSVM();
    }

    // testing greaterEquals 4 >= 4
    @Test
    public void testGreaterEqualsOperator3() {
        String fileName = ROOT_GE_TEST_FILES + "greaterEquals2.fool";

        ToolsForTest tools = new ToolsForTest();
        tools.createFOOLFile(fileName, "let var x:bool = 4 >= 4; in print(x);");
        tools.buildASTAndSVMAndCheckErrors(fileName, false);
        tools.runningSVM();
    }

    // testing greaterEquals 5 >= 4
    @Test
    public void testGreaterEqualsOperator4() {
        String fileName = ROOT_GE_TEST_FILES + "greaterEquals3.fool";

        ToolsForTest tools = new ToolsForTest();
        tools.createFOOLFile(fileName, "let var x:bool = 5 >= 4; in print(x);");
        tools.buildASTAndSVMAndCheckErrors(fileName, false);
        tools.runningSVM();
    }

    // testing or var x:bool = false||false
    @Test
    public void testOrOperator() {
        String fileName = ROOT_OR_TEST_FILES + "or.fool";

        ToolsForTest tools = new ToolsForTest();
        tools.createFOOLFile(fileName, "let var x:bool = false||false; in print(x);");
        tools.buildASTAndSVMAndCheckErrors(fileName, false);
        tools.runningSVM();
    }

    // testing or var x:bool = false||true
    @Test
    public void testOrOperator2() {
        String fileName = ROOT_OR_TEST_FILES + "or2.fool";

        ToolsForTest tools = new ToolsForTest();
        tools.createFOOLFile(fileName, "let var x:bool = false||true; in print(x);");
        tools.buildASTAndSVMAndCheckErrors(fileName, false);
        tools.runningSVM();
    }

    // testing or var x:bool = true||false
    @Test
    public void testOrOperator3() {
        String fileName = ROOT_OR_TEST_FILES + "or3.fool";

        ToolsForTest tools = new ToolsForTest();
        tools.createFOOLFile(fileName, "let var x:bool = true||false; in print(x);");
        tools.buildASTAndSVMAndCheckErrors(fileName, false);
        tools.runningSVM();
    }

    // testing or var x:bool = true||true
    @Test
    public void testOrOperator4() {
        String fileName = ROOT_OR_TEST_FILES + "or4.fool";

        ToolsForTest tools = new ToolsForTest();
        tools.createFOOLFile(fileName, "let var x:bool = true||true; in print(x);");
        tools.buildASTAndSVMAndCheckErrors(fileName, false);
        tools.runningSVM();
    }

    // testing and var x:bool = false&&false
    @Test
    public void testAndOperator() {
        String fileName = ROOT_AND_TEST_FILES + "and.fool";

        ToolsForTest tools = new ToolsForTest();
        tools.createFOOLFile(fileName, "let var x:bool = false&&false; in print(x);");
        tools.buildASTAndSVMAndCheckErrors(fileName, false);
        tools.runningSVM();
    }

    // testing and var x:bool = false&&true
    @Test
    public void testAndOperator2() {
        String fileName = ROOT_AND_TEST_FILES + "and2.fool";

        ToolsForTest tools = new ToolsForTest();
        tools.createFOOLFile(fileName, "let var x:bool = false&&true; in print(x);");
        tools.buildASTAndSVMAndCheckErrors(fileName, false);
        tools.runningSVM();
    }

    // testing and var x:bool = true&&false
    @Test
    public void testAndOperator3() {
        String fileName = ROOT_AND_TEST_FILES + "and3.fool";

        ToolsForTest tools = new ToolsForTest();
        tools.createFOOLFile(fileName, "let var x:bool = true&&false; in print(x);");
        tools.buildASTAndSVMAndCheckErrors(fileName, false);
        tools.runningSVM();
    }

    // testing and var x:bool = true&&true
    @Test
    public void testAndOperator4() {
        String fileName = ROOT_AND_TEST_FILES + "and4.fool";

        ToolsForTest tools = new ToolsForTest();
        tools.createFOOLFile(fileName, "let var x:bool = true&&true; in print(x);");
        tools.buildASTAndSVMAndCheckErrors(fileName, false);
        tools.runningSVM();
    }

    // testing not var x:bool = !true
    @Test
    public void testNotOperator() {
        String fileName = ROOT_NOT_TEST_FILES + "not.fool";

        ToolsForTest tools = new ToolsForTest();
        tools.createFOOLFile(fileName, "let var x:bool = !true; in print(x);");
        tools.buildASTAndSVMAndCheckErrors(fileName, false);
        tools.runningSVM();
    }

    // testing not var x:bool = !false
    @Test
    public void testNotOperator2() {
        String fileName = ROOT_NOT_TEST_FILES + "not2.fool";

        ToolsForTest tools = new ToolsForTest();
        tools.createFOOLFile(fileName, "let var x:bool = !false; in print(x);");
        tools.buildASTAndSVMAndCheckErrors(fileName, false);
        tools.runningSVM();
    }


}