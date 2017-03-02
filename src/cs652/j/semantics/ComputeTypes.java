package cs652.j.semantics;

import cs652.j.parser.JBaseListener;
import cs652.j.parser.JParser;
import org.antlr.symtab.*;

public class ComputeTypes extends JBaseListener {
	protected StringBuilder buf = new StringBuilder();
	protected Scope currentScope;

	public static final Type JINT_TYPE = new JPrimitiveType("int");
	public static final Type JFLOAT_TYPE = new JPrimitiveType("float");
	public static final Type JSTRING_TYPE = new JPrimitiveType("string");
	public static final Type JVOID_TYPE = new JPrimitiveType("void");

	public ComputeTypes(GlobalScope globals) {
		this.currentScope = globals;
	}

	// ...

	// S U P P O R T


	@Override
	public void enterFormalParameter(JParser.FormalParameterContext ctx) {
		String id = ctx.ID().getText();
		Type type = null;
		if (ctx.jType().ID() == null) {
			type = new JPrimitiveType(ctx.jType().getText());
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
	public void enterLocalVariableDeclaration(JParser.LocalVariableDeclarationContext ctx) {
		String id = ctx.ID().getText();
		String type = ctx.jType().getText();
		//JVar jVar = new JVar(id); ///////////////////////////
		buf.append( id + " is " + type + System.lineSeparator());
	}

	@Override
	public void enterCtorCall(JParser.CtorCallContext ctx) {
		String id = ctx.ID().getText();
		buf.append( ctx.getText() + " is " + id + System.lineSeparator());
	}

	@Override
	public void enterIdRef(JParser.IdRefContext ctx) {
		String id = ctx.ID().getText();
		Symbol symbol = currentScope.resolve(id);
		if (symbol instanceof JArg) {
			buf.append( id + " is " + ((JArg) symbol).getType() + System.lineSeparator());
		}
	}

	public String getRefOutput() {
		return buf.toString();
	}
}

