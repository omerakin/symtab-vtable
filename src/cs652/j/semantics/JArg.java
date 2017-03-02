package cs652.j.semantics;

import org.antlr.symtab.ParameterSymbol;
import org.antlr.symtab.Type;

/**
 * Created by omerakin on 2/28/17.
 */
public class JArg extends ParameterSymbol {
    public JArg(String name, Type type) {
        super(name);
        setType(type);
    }
}
