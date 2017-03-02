package cs652.j.semantics;

import org.antlr.symtab.Type;
import org.antlr.symtab.VariableSymbol;

/**
 * Created by omerakin on 2/28/17.
 */
public class JVar extends VariableSymbol {
    public JVar(String name, Type type) {
        super(name);
        setType(type);
    }
}
