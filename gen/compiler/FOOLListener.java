// Generated from /Users/manuelbuizo/Desktop/uni/LCMC/LCML-Project/src/compiler/FOOL.g4 by ANTLR 4.13.1
package compiler;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link FOOLParser}.
 */
public interface FOOLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link FOOLParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(FOOLParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link FOOLParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(FOOLParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code letInProg}
	 * labeled alternative in {@link FOOLParser#progbody}.
	 * @param ctx the parse tree
	 */
	void enterLetInProg(FOOLParser.LetInProgContext ctx);
	/**
	 * Exit a parse tree produced by the {@code letInProg}
	 * labeled alternative in {@link FOOLParser#progbody}.
	 * @param ctx the parse tree
	 */
	void exitLetInProg(FOOLParser.LetInProgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code noDecProg}
	 * labeled alternative in {@link FOOLParser#progbody}.
	 * @param ctx the parse tree
	 */
	void enterNoDecProg(FOOLParser.NoDecProgContext ctx);
	/**
	 * Exit a parse tree produced by the {@code noDecProg}
	 * labeled alternative in {@link FOOLParser#progbody}.
	 * @param ctx the parse tree
	 */
	void exitNoDecProg(FOOLParser.NoDecProgContext ctx);
	/**
	 * Enter a parse tree produced by {@link FOOLParser#cldec}.
	 * @param ctx the parse tree
	 */
	void enterCldec(FOOLParser.CldecContext ctx);
	/**
	 * Exit a parse tree produced by {@link FOOLParser#cldec}.
	 * @param ctx the parse tree
	 */
	void exitCldec(FOOLParser.CldecContext ctx);
	/**
	 * Enter a parse tree produced by {@link FOOLParser#methdec}.
	 * @param ctx the parse tree
	 */
	void enterMethdec(FOOLParser.MethdecContext ctx);
	/**
	 * Exit a parse tree produced by {@link FOOLParser#methdec}.
	 * @param ctx the parse tree
	 */
	void exitMethdec(FOOLParser.MethdecContext ctx);
	/**
	 * Enter a parse tree produced by the {@code vardec}
	 * labeled alternative in {@link FOOLParser#dec}.
	 * @param ctx the parse tree
	 */
	void enterVardec(FOOLParser.VardecContext ctx);
	/**
	 * Exit a parse tree produced by the {@code vardec}
	 * labeled alternative in {@link FOOLParser#dec}.
	 * @param ctx the parse tree
	 */
	void exitVardec(FOOLParser.VardecContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fundec}
	 * labeled alternative in {@link FOOLParser#dec}.
	 * @param ctx the parse tree
	 */
	void enterFundec(FOOLParser.FundecContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fundec}
	 * labeled alternative in {@link FOOLParser#dec}.
	 * @param ctx the parse tree
	 */
	void exitFundec(FOOLParser.FundecContext ctx);
	/**
	 * Enter a parse tree produced by the {@code new}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterNew(FOOLParser.NewContext ctx);
	/**
	 * Exit a parse tree produced by the {@code new}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitNew(FOOLParser.NewContext ctx);
	/**
	 * Enter a parse tree produced by the {@code comp}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterComp(FOOLParser.CompContext ctx);
	/**
	 * Exit a parse tree produced by the {@code comp}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitComp(FOOLParser.CompContext ctx);
	/**
	 * Enter a parse tree produced by the {@code pars}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterPars(FOOLParser.ParsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code pars}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitPars(FOOLParser.ParsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code times_Div}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterTimes_Div(FOOLParser.Times_DivContext ctx);
	/**
	 * Exit a parse tree produced by the {@code times_Div}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitTimes_Div(FOOLParser.Times_DivContext ctx);
	/**
	 * Enter a parse tree produced by the {@code false}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterFalse(FOOLParser.FalseContext ctx);
	/**
	 * Exit a parse tree produced by the {@code false}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitFalse(FOOLParser.FalseContext ctx);
	/**
	 * Enter a parse tree produced by the {@code and_Or}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterAnd_Or(FOOLParser.And_OrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code and_Or}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitAnd_Or(FOOLParser.And_OrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code plus_Minus}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterPlus_Minus(FOOLParser.Plus_MinusContext ctx);
	/**
	 * Exit a parse tree produced by the {@code plus_Minus}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitPlus_Minus(FOOLParser.Plus_MinusContext ctx);
	/**
	 * Enter a parse tree produced by the {@code integer}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterInteger(FOOLParser.IntegerContext ctx);
	/**
	 * Exit a parse tree produced by the {@code integer}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitInteger(FOOLParser.IntegerContext ctx);
	/**
	 * Enter a parse tree produced by the {@code call}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterCall(FOOLParser.CallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code call}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitCall(FOOLParser.CallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code not}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterNot(FOOLParser.NotContext ctx);
	/**
	 * Exit a parse tree produced by the {@code not}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitNot(FOOLParser.NotContext ctx);
	/**
	 * Enter a parse tree produced by the {@code print}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterPrint(FOOLParser.PrintContext ctx);
	/**
	 * Exit a parse tree produced by the {@code print}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitPrint(FOOLParser.PrintContext ctx);
	/**
	 * Enter a parse tree produced by the {@code null}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterNull(FOOLParser.NullContext ctx);
	/**
	 * Exit a parse tree produced by the {@code null}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitNull(FOOLParser.NullContext ctx);
	/**
	 * Enter a parse tree produced by the {@code true}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterTrue(FOOLParser.TrueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code true}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitTrue(FOOLParser.TrueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code id}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterId(FOOLParser.IdContext ctx);
	/**
	 * Exit a parse tree produced by the {@code id}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitId(FOOLParser.IdContext ctx);
	/**
	 * Enter a parse tree produced by the {@code dotCall}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterDotCall(FOOLParser.DotCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code dotCall}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitDotCall(FOOLParser.DotCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code if}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterIf(FOOLParser.IfContext ctx);
	/**
	 * Exit a parse tree produced by the {@code if}
	 * labeled alternative in {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitIf(FOOLParser.IfContext ctx);
	/**
	 * Enter a parse tree produced by the {@code intType}
	 * labeled alternative in {@link FOOLParser#type}.
	 * @param ctx the parse tree
	 */
	void enterIntType(FOOLParser.IntTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code intType}
	 * labeled alternative in {@link FOOLParser#type}.
	 * @param ctx the parse tree
	 */
	void exitIntType(FOOLParser.IntTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code boolType}
	 * labeled alternative in {@link FOOLParser#type}.
	 * @param ctx the parse tree
	 */
	void enterBoolType(FOOLParser.BoolTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code boolType}
	 * labeled alternative in {@link FOOLParser#type}.
	 * @param ctx the parse tree
	 */
	void exitBoolType(FOOLParser.BoolTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code idType}
	 * labeled alternative in {@link FOOLParser#type}.
	 * @param ctx the parse tree
	 */
	void enterIdType(FOOLParser.IdTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code idType}
	 * labeled alternative in {@link FOOLParser#type}.
	 * @param ctx the parse tree
	 */
	void exitIdType(FOOLParser.IdTypeContext ctx);
}