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
	public void enterAssignStat(JParser.AssignStatContext ctx) {
		//buf.append(ctx.expression(0).getText() + " is " + ctx.expression(1).getText());
	}

	@Override
	public void enterLocalVariableDeclaration(JParser.LocalVariableDeclarationContext ctx) {
		String id = ctx.ID().getText();
		Symbol symbol = null;


		System.out.println( Utils.toString(currentScope));


		for (Symbol s: currentScope.getAllSymbols()) {
			//System.out.println(s.getName());
		}



		//System.out.println("buraaaa" + symbol.getName());
		if (symbol instanceof JField)
		buf.append( id + " is " + ((JField) symbol).getType() + System.lineSeparator());
	}

	@Override
	public void enterIdRef(JParser.IdRefContext ctx) {
		 //buf.append(ctx.ID().getText());


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

