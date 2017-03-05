
import cs652.j.parser.JLexer;
import cs652.j.parser.JParser;
import cs652.j.semantics.ComputeTypes;
import cs652.j.semantics.DefineScopesAndSymbols;
import org.antlr.symtab.GlobalScope;
import org.antlr.symtab.Utils;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

/**
 * Created by omerakin on 3/1/17.
 */
public class Test {

    public static void main(String[] args) throws IOException {
        ANTLRFileStream antlrFileStream = new ANTLRFileStream("test.falala");
        JLexer jLexer = new JLexer(antlrFileStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(jLexer);
        JParser jParser = new JParser(commonTokenStream);
        ParseTree parseTree = jParser.file();
        //System.out.println(parseTree.toStringTree(jParser));

        GlobalScope globals = new GlobalScope(null);
        ParseTreeWalker walker = new ParseTreeWalker();
        DefineScopesAndSymbols defSymbols = new DefineScopesAndSymbols(globals);
        walker.walk(defSymbols, parseTree);

        //System.out.println( Utils.toString(globals));

        ComputeTypes computeTypes = new ComputeTypes(globals);
        walker = new ParseTreeWalker();
        walker.walk(computeTypes, parseTree);

        System.out.println(System.lineSeparator() + computeTypes.getRefOutput());
    }

}
