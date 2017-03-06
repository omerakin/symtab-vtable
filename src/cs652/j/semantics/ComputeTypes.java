package cs652.j.semantics;

import cs652.j.parser.JBaseListener;
import cs652.j.parser.JParser;
import org.antlr.symtab.*;
import org.antlr.v4.runtime.ParserRuleContext;
import sun.jvm.hotspot.types.Field;

import java.util.HashMap;

public class ComputeTypes extends JBaseListener {
	protected StringBuilder buf = new StringBuilder();
	protected Scope currentScope, globalScope;

	public static final Type JINT_TYPE = new JPrimitiveType("int");
	public static final Type JFLOAT_TYPE = new JPrimitiveType("float");
	public static final Type JSTRING_TYPE = new JPrimitiveType("string");
	public static final Type JVOID_TYPE = new JPrimitiveType("void");

	public ComputeTypes(GlobalScope globals) {
		this.currentScope = globals;
		globalScope = globals;
	}

    @Override
    public void exitFieldRef(JParser.FieldRefContext ctx) {
        Type type = ctx.expression().type;
        Symbol symbol = ((JClass) type).resolve(ctx.ID().getText());
        ctx.type = ((JField) symbol).getType();

        buf.append(ctx.getText() + " is " + ctx.type.getName() + System.lineSeparator());
    }

    @Override
    public void exitQMethodCall(JParser.QMethodCallContext ctx) {
        Type type = ctx.expression().type;
        Symbol symbol = ((JClass) type).resolve(ctx.ID().getText());
        ctx.type = ((JMethod) symbol).getType();

        buf.append(ctx.getText() + " is " + ctx.type.getName() + System.lineSeparator());
    }

    @Override
    public void enterCtorCall(JParser.CtorCallContext ctx) {
        ctx.type = (Type) currentScope.resolve(ctx.ID().getText());
    }

    @Override
    public void exitCtorCall(JParser.CtorCallContext ctx) {
        buf.append(ctx.getText() + " is " + ctx.type.getName() + System.lineSeparator());
    }

    @Override
    public void enterMethodCall(JParser.MethodCallContext ctx) {
        Symbol symbol = currentScope.resolve(ctx.ID().getText());
        if (symbol instanceof JMethod) {
            ctx.type = ((JMethod) symbol).getType();
        }
    }

    @Override
    public void exitMethodCall(JParser.MethodCallContext ctx) {
        buf.append(ctx.getText() + " is " + ctx.type.getName() + System.lineSeparator());
    }

    @Override
    public void enterThisRef(JParser.ThisRefContext ctx) {
        Symbol symbol = currentScope.resolve(ctx.getText());
        if (symbol instanceof JArg) { ctx.type = ((JArg) symbol).getType(); }
    }

    @Override
    public void exitThisRef(JParser.ThisRefContext ctx) {
        buf.append(ctx.getText() + " is " + ctx.type.getName() + System.lineSeparator());
    }

    @Override
	public void enterLiteralRef(JParser.LiteralRefContext ctx) {
		if (ctx.getText().contains(".")) { ctx.type = JFLOAT_TYPE;} else { ctx.type = JINT_TYPE; }
	}

	@Override
	public void exitLiteralRef(JParser.LiteralRefContext ctx) {
		buf.append(ctx.getText() + " is " + ctx.type.getName() + System.lineSeparator());
	}

	@Override
	public void enterIdRef(JParser.IdRefContext ctx) {
		Symbol symbol = currentScope.resolve(ctx.ID().getText());
		if (symbol instanceof JArg) {
			ctx.type = ((JArg) symbol).getType();
		}
		else if (symbol instanceof JField) {
			ctx.type = ((JField) symbol).getType();
		}
	}

	@Override
	public void exitIdRef(JParser.IdRefContext ctx) {
		buf.append(ctx.ID().getText() + " is " + ctx.type.getName() + System.lineSeparator());
	}

	@Override
	public void enterFile(JParser.FileContext ctx) {
		currentScope = ctx.scope;
	}

	@Override
	public void exitFile(JParser.FileContext ctx) {
		currentScope = currentScope.getEnclosingScope();
	}

	@Override
	public void enterClassDeclaration(JParser.ClassDeclarationContext ctx) {
		currentScope = ctx.scope;
	}

	@Override
	public void exitClassDeclaration(JParser.ClassDeclarationContext ctx) {
		currentScope = currentScope.getEnclosingScope();
	}

	@Override
	public void enterMethodDeclaration(JParser.MethodDeclarationContext ctx) {
		currentScope = ctx.scope;
	}

	@Override
	public void exitMethodDeclaration(JParser.MethodDeclarationContext ctx) {
		currentScope = currentScope.getEnclosingScope();
	}

	@Override
	public void enterBlock(JParser.BlockContext ctx) {
		currentScope = ctx.scope;
	}

	@Override
	public void exitBlock(JParser.BlockContext ctx) {
		currentScope = currentScope.getEnclosingScope();
	}

	public String getRefOutput() {
		return buf.toString();
	}
}

