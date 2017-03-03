package cs652.j.semantics;

import cs652.j.parser.JBaseListener;
import cs652.j.parser.JParser;
import org.antlr.symtab.*;
import org.antlr.v4.runtime.RuleContext;

public class DefineScopesAndSymbols extends JBaseListener {
	public Scope currentScope;
	public ComputeTypes computeTypes;

	public DefineScopesAndSymbols(GlobalScope globals) {
		currentScope = globals;
	}

	@Override
	public void enterFile(JParser.FileContext ctx) {
		currentScope.define((Symbol) computeTypes.JINT_TYPE);
		currentScope.define((Symbol) computeTypes.JFLOAT_TYPE);
		currentScope.define((Symbol) computeTypes.JSTRING_TYPE);
		currentScope.define((Symbol) computeTypes.JVOID_TYPE);
		ctx.scope = (GlobalScope) currentScope;
	}

	@Override
	public void exitFile(JParser.FileContext ctx) {
		currentScope = currentScope.getEnclosingScope();
	}

	@Override
	public void enterClassDeclaration(JParser.ClassDeclarationContext ctx) {
		String id = ctx.ID(0).getText();
		JClass jClass = new JClass(id, ctx.classBody());
		currentScope.define(jClass);
		currentScope = jClass;
		ctx.scope = (JClass) currentScope;
	}

	@Override
	public void exitClassDeclaration(JParser.ClassDeclarationContext ctx) {
		currentScope = currentScope.getEnclosingScope();
	}

	@Override
	public void enterMethodDeclaration(JParser.MethodDeclarationContext ctx) {
		String id = ctx.ID().getText();
		JMethod jMethod = new JMethod(id, ctx.methodBody());
		if (ctx.jType() != null) {
			jMethod.setType((Type) currentScope.resolve(ctx.jType().getText()));
		}
		currentScope.define(jMethod);
		currentScope = jMethod;
		JArg jArg = new JArg("this");
		jArg.setType((Type) currentScope.getEnclosingScope());
		currentScope.define(jArg);
		ctx.scope = (JMethod) currentScope;
	}

	@Override
	public void exitMethodDeclaration(JParser.MethodDeclarationContext ctx) {
		currentScope = currentScope.getEnclosingScope();
	}

	@Override
	public void enterFormalParameter(JParser.FormalParameterContext ctx) {
		String id = ctx.ID().getText();
		JArg jArg = new JArg(id);
		jArg.setType((Type) currentScope.resolve(ctx.jType().getText()));
		currentScope.define(jArg);
	}

	@Override
	public void enterBlock(JParser.BlockContext ctx) {
		LocalScope localScope = new LocalScope(currentScope);
		currentScope.nest(localScope);
		currentScope = localScope;
		ctx.scope = (LocalScope) currentScope;
	}

	@Override
	public void exitBlock(JParser.BlockContext ctx) {
		currentScope = currentScope.getEnclosingScope();
	}

	@Override
	public void enterFieldDeclaration(JParser.FieldDeclarationContext ctx) {
		String id = ctx.ID().getText();
		JField jField = new JField(id);
		jField.setType((Type) currentScope.resolve(ctx.jType().getText()));
		currentScope.define(jField);
	}

	@Override
	public void enterLocalVariableDeclaration(JParser.LocalVariableDeclarationContext ctx) {
		String id = ctx.ID().getText();
		JArg jArg = new JArg(id);
		jArg.setType((Type) currentScope.resolve(ctx.jType().getText()));
		currentScope.define(jArg);
	}

	@Override
	public void enterMain(JParser.MainContext ctx) {
		JMethod jMethod = new JMethod("main", ctx.block());
		jMethod.setType(computeTypes.JVOID_TYPE);
		currentScope.define(jMethod);
		currentScope = jMethod;
		ctx.scope = (JMethod) currentScope;
	}

	@Override
	public void exitMain(JParser.MainContext ctx) {
		currentScope = currentScope.getEnclosingScope();
	}
}
