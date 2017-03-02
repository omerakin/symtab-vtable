package cs652.j.semantics;

import cs652.j.parser.JBaseListener;
import cs652.j.parser.JParser;
import org.antlr.symtab.*;
import org.antlr.v4.runtime.RuleContext;

public class DefineScopesAndSymbols extends JBaseListener {
	public Scope currentScope;
	public static final Type JINT_TYPE = new JPrimitiveType("int");
	public static final Type JFLOAT_TYPE = new JPrimitiveType("float");
	public static final Type JSTRING_TYPE = new JPrimitiveType("string");
	public static final Type JVOID_TYPE = new JPrimitiveType("void");

	public DefineScopesAndSymbols(GlobalScope globals) {
		currentScope = globals;
	}

	@Override
	public void enterFile(JParser.FileContext ctx) {
		currentScope.define((Symbol) JINT_TYPE);
		currentScope.define((Symbol) JFLOAT_TYPE);
		currentScope.define((Symbol) JSTRING_TYPE);
		currentScope.define((Symbol) JVOID_TYPE);
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
		currentScope.define(jMethod);
		currentScope = jMethod;
		JArg jArg = new JArg("this", JINT_TYPE);////////////////////////////
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
		Type type = null;
		if (ctx.jType().ID() == null) {
			if (ctx.jType().getText().trim().equals("int")){
				type = JINT_TYPE;
			} else {
				type = JFLOAT_TYPE;
			}
		} else {
			String jtype = ctx.jType().ID().getText();
			Symbol symbol = currentScope.resolve(jtype);
			if (symbol == null) {
				System.err.println("No such var: "+ jtype);
				return;
			} else {
				//
			}
		}
		JArg jArg = new JArg(id, type);
		currentScope.define(jArg);
	}

	@Override
	public void enterBlock(JParser.BlockContext ctx) {
		JMethod jMethod = new JMethod("local", RuleContext.EMPTY);
		currentScope.define(jMethod);
		//JArg jArg = new JArg("xxxxx", JINT_TYPE);
		//currentScope.define(jArg);///////////////////////////////
		currentScope = new LocalScope(currentScope);
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
		currentScope.define(jField);
	}

	@Override
	public void enterIdRef(JParser.IdRefContext ctx) {
		String id = ctx.ID().getText();
		Symbol symbol = currentScope.resolve(id);
		if ( symbol==null ) {
			System.err.println("No such var: "+ id);
		}
	}

	@Override
	public void enterThisRef(JParser.ThisRefContext ctx) {
		System.out.println("thisssss");
		// what should I do ? (for now checking in enterFieldRef method).
	}

	@Override
	public void enterFieldRef(JParser.FieldRefContext ctx) {
		String isThis = ctx.expression().getText();
		if (isThis.equals("this")) {
			String id = ctx.ID().getText();
			Symbol symbol = currentScope.getEnclosingScope().getEnclosingScope().resolve(id);
			if ( symbol==null ) {
				System.err.println("No such var: "+ id);
			}
		}
	}

	@Override
	public void enterLocalVariableDeclaration(JParser.LocalVariableDeclarationContext ctx) {
		String id = ctx.ID().getText();
		Type type;
		if (ctx.jType().getText().trim().equals("int")){
			type = JINT_TYPE;
		} else {
			type = JFLOAT_TYPE;
		}
		JArg jArg = new JArg(id, type);
		currentScope.define(jArg);
	}

	@Override
	public void enterMain(JParser.MainContext ctx) {
		JMethod jMethod = new JMethod("main", ctx.block());
		currentScope.define(jMethod);
		currentScope = jMethod;
		ctx.scope = (JMethod) currentScope;
	}

	@Override
	public void exitMain(JParser.MainContext ctx) {
		currentScope = currentScope.getEnclosingScope();
	}
}
