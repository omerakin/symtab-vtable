package cs652.j.semantics;

import cs652.j.parser.JBaseListener;
import cs652.j.parser.JParser;
import org.antlr.symtab.*;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.HashMap;

public class ComputeTypes extends JBaseListener {
	protected StringBuilder buf = new StringBuilder();
	protected Scope currentScope, globalScope;
	protected HashMap<String, String> extendClasses;

	public static final Type JINT_TYPE = new JPrimitiveType("int");
	public static final Type JFLOAT_TYPE = new JPrimitiveType("float");
	public static final Type JSTRING_TYPE = new JPrimitiveType("string");
	public static final Type JVOID_TYPE = new JPrimitiveType("void");

	public ComputeTypes(GlobalScope globals) {
		this.currentScope = globals;
		globalScope = globals;
		extendClasses = new HashMap<>();
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
	public void enterFieldRef(JParser.FieldRefContext ctx) {
		String s2 = ctx.getText();
		String[] string =ctx.getText().trim().split("\\.");
		for (int i=0; i<string.length-1; i++) {
			String s = string[i];
			Symbol symbol;
			if (i == 0) symbol = currentScope.resolve(s); else symbol = globalScope.resolve(ctx.type.getName());
			if (symbol instanceof JArg || symbol instanceof JClass) {
				String type;
				if (symbol instanceof JArg) type = ((JArg) symbol).getType().getName(); else type = symbol.getName();
				String ddd = string[i+1].replace("()","");
				Symbol symbol1 = ((Scope) globalScope.getSymbol(type)).getSymbol(string[i+1].replace("()",""));
				while (symbol1==null && !extendClasses.isEmpty() && extendClasses.get(type)!=null) {
					String extendType = extendClasses.get(type);
					symbol1 =  ((Scope) globalScope.getSymbol(extendType)).getSymbol(string[i+1].replace("()",""));
					type =  extendType;
				}
				if (symbol1 instanceof JField) {
					ctx.type = ((JField) symbol1).getType();
				} else if (symbol1 instanceof JMethod) {
					ctx.type = ((JMethod) symbol1).getType();
				}
			}
		}
	}

	@Override
	public void exitFieldRef(JParser.FieldRefContext ctx) {
		buf.append(ctx.getText() + " is " + ctx.type.getName() + System.lineSeparator());
	}

	@Override
	public void enterMethodCall(JParser.MethodCallContext ctx) {
		Symbol symbol = currentScope.resolve(ctx.ID().getText());
		if (symbol instanceof JMethod) {
			ctx.type = ((JMethod) symbol).getType();
		} else {
			ParserRuleContext parserRuleContext = ctx.getParent();
			while (!parserRuleContext.getClass().getSimpleName().contains("ClassDeclarationContext")) {
				parserRuleContext = parserRuleContext.getParent();
			}
			String type = parserRuleContext.getChild(1).getText();
			while (symbol==null && !extendClasses.isEmpty() && extendClasses.get(type)!=null) {
				String extendType = extendClasses.get(type);
				symbol =  ((Scope) globalScope.getSymbol(extendType)).getSymbol(ctx.ID().getText());
				type =  extendType;
			}
			if (symbol instanceof JMethod) {
				ctx.type = ((JMethod) symbol).getType();
			}
		}
	}

	@Override
	public void exitMethodCall(JParser.MethodCallContext ctx) {
		buf.append(ctx.getText() + " is " + ctx.type.getName() + System.lineSeparator());
	}

	@Override
	public void enterQMethodCall(JParser.QMethodCallContext ctx) {
		String s = ctx.getText();
		Symbol symbol = currentScope.resolve(ctx.expression().getText());
		if (symbol instanceof JArg) {
			String type = ((JArg) symbol).getType().getName();
			Symbol symbol1 = ((Scope) globalScope.getSymbol(type)).getSymbol(ctx.ID().getText());
			while (symbol1==null && !extendClasses.isEmpty()) {
				String extendType = extendClasses.get(type);
				symbol1 =  ((Scope) globalScope.getSymbol(extendType)).getSymbol(ctx.ID().getText());
				type =  extendType;
			}
			if (symbol1 instanceof JMethod) {
				ctx.type = ((JMethod) symbol1).getType();
			}
		} else {
			ctx.type = JINT_TYPE;
		}
	}

	@Override
	public void exitQMethodCall(JParser.QMethodCallContext ctx) {
		buf.append(ctx.getText() + " is " + ctx.type.getName() + System.lineSeparator());
	}

	@Override
	public void enterIdRef(JParser.IdRefContext ctx) {
		Symbol symbol = currentScope.resolve(ctx.ID().getText());
		if (symbol != null) {
			if (symbol instanceof JArg) { ctx.type = ((JArg) symbol).getType(); }
			else if (symbol instanceof JField) { ctx.type = ((JField) symbol).getType(); }
		} else {
			symbol = currentScope.resolve("this");
			if (symbol instanceof JArg) {
				String type = ((JArg) symbol).getType().getName();
				Symbol symbol2 = null;
				while (symbol2==null && !extendClasses.isEmpty()) {
					String extendType = extendClasses.get(type);
					symbol2 =  ((Scope) globalScope.getSymbol(extendType)).getSymbol(ctx.ID().getText());
					type =  extendType;
				}
				if (symbol2 instanceof JField) {
					ctx.type = ((JField) symbol2).getType();
				}
			}
		}
	}

	@Override
	public void exitIdRef(JParser.IdRefContext ctx) {
		buf.append(ctx.ID().getText() + " is " + ctx.type.getName() + System.lineSeparator());
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
	public void enterCtorCall(JParser.CtorCallContext ctx) {
		ctx.type = (Type) currentScope.resolve(ctx.ID().getText());
	}

	@Override
	public void exitCtorCall(JParser.CtorCallContext ctx) {
		buf.append(ctx.getText() + " is " + ctx.type.getName() + System.lineSeparator());
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
		if (ctx.getText().contains("extends")) { extendClasses.put(ctx.ID(0).getText().trim(),ctx.ID(1).getText().trim()); }
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

