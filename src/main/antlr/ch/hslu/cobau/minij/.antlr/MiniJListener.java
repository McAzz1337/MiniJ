// Generated from c:/HSLU/COBAU/CompilerFw/src/main/antlr/ch/hslu/cobau/minij/MiniJ.g4 by ANTLR 4.13.1

package ch.hslu.cobau.minij;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MiniJParser}.
 */
public interface MiniJListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MiniJParser#unit}.
	 * @param ctx the parse tree
	 */
	void enterUnit(MiniJParser.UnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#unit}.
	 * @param ctx the parse tree
	 */
	void exitUnit(MiniJParser.UnitContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#member}.
	 * @param ctx the parse tree
	 */
	void enterMember(MiniJParser.MemberContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#member}.
	 * @param ctx the parse tree
	 */
	void exitMember(MiniJParser.MemberContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#record}.
	 * @param ctx the parse tree
	 */
	void enterRecord(MiniJParser.RecordContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#record}.
	 * @param ctx the parse tree
	 */
	void exitRecord(MiniJParser.RecordContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(MiniJParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(MiniJParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(MiniJParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(MiniJParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#declarations}.
	 * @param ctx the parse tree
	 */
	void enterDeclarations(MiniJParser.DeclarationsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#declarations}.
	 * @param ctx the parse tree
	 */
	void exitDeclarations(MiniJParser.DeclarationsContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#functionBody}.
	 * @param ctx the parse tree
	 */
	void enterFunctionBody(MiniJParser.FunctionBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#functionBody}.
	 * @param ctx the parse tree
	 */
	void exitFunctionBody(MiniJParser.FunctionBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(MiniJParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(MiniJParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#declarationStatement}.
	 * @param ctx the parse tree
	 */
	void enterDeclarationStatement(MiniJParser.DeclarationStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#declarationStatement}.
	 * @param ctx the parse tree
	 */
	void exitDeclarationStatement(MiniJParser.DeclarationStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(MiniJParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(MiniJParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(MiniJParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(MiniJParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#callStatement}.
	 * @param ctx the parse tree
	 */
	void enterCallStatement(MiniJParser.CallStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#callStatement}.
	 * @param ctx the parse tree
	 */
	void exitCallStatement(MiniJParser.CallStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatement(MiniJParser.WhileStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatement(MiniJParser.WhileStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(MiniJParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(MiniJParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#elseClause}.
	 * @param ctx the parse tree
	 */
	void enterElseClause(MiniJParser.ElseClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#elseClause}.
	 * @param ctx the parse tree
	 */
	void exitElseClause(MiniJParser.ElseClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(MiniJParser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(MiniJParser.ReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(MiniJParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(MiniJParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#call}.
	 * @param ctx the parse tree
	 */
	void enterCall(MiniJParser.CallContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#call}.
	 * @param ctx the parse tree
	 */
	void exitCall(MiniJParser.CallContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpression(MiniJParser.UnaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpression(MiniJParser.UnaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#trueConstant}.
	 * @param ctx the parse tree
	 */
	void enterTrueConstant(MiniJParser.TrueConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#trueConstant}.
	 * @param ctx the parse tree
	 */
	void exitTrueConstant(MiniJParser.TrueConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#falseConstant}.
	 * @param ctx the parse tree
	 */
	void enterFalseConstant(MiniJParser.FalseConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#falseConstant}.
	 * @param ctx the parse tree
	 */
	void exitFalseConstant(MiniJParser.FalseConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#integerConstant}.
	 * @param ctx the parse tree
	 */
	void enterIntegerConstant(MiniJParser.IntegerConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#integerConstant}.
	 * @param ctx the parse tree
	 */
	void exitIntegerConstant(MiniJParser.IntegerConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#stringConstant}.
	 * @param ctx the parse tree
	 */
	void enterStringConstant(MiniJParser.StringConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#stringConstant}.
	 * @param ctx the parse tree
	 */
	void exitStringConstant(MiniJParser.StringConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#memoryAccess}.
	 * @param ctx the parse tree
	 */
	void enterMemoryAccess(MiniJParser.MemoryAccessContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#memoryAccess}.
	 * @param ctx the parse tree
	 */
	void exitMemoryAccess(MiniJParser.MemoryAccessContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(MiniJParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(MiniJParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(MiniJParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(MiniJParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#basicType}.
	 * @param ctx the parse tree
	 */
	void enterBasicType(MiniJParser.BasicTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#basicType}.
	 * @param ctx the parse tree
	 */
	void exitBasicType(MiniJParser.BasicTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#integerType}.
	 * @param ctx the parse tree
	 */
	void enterIntegerType(MiniJParser.IntegerTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#integerType}.
	 * @param ctx the parse tree
	 */
	void exitIntegerType(MiniJParser.IntegerTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#stringType}.
	 * @param ctx the parse tree
	 */
	void enterStringType(MiniJParser.StringTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#stringType}.
	 * @param ctx the parse tree
	 */
	void exitStringType(MiniJParser.StringTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#booleanType}.
	 * @param ctx the parse tree
	 */
	void enterBooleanType(MiniJParser.BooleanTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#booleanType}.
	 * @param ctx the parse tree
	 */
	void exitBooleanType(MiniJParser.BooleanTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#recordType}.
	 * @param ctx the parse tree
	 */
	void enterRecordType(MiniJParser.RecordTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#recordType}.
	 * @param ctx the parse tree
	 */
	void exitRecordType(MiniJParser.RecordTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniJParser#identifier}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(MiniJParser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniJParser#identifier}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(MiniJParser.IdentifierContext ctx);
}