// Generated from C:/Users/Federico/IdeaProjects/LCML-Project/src/compiler/FOOL.g4 by ANTLR 4.13.1
package compiler;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class FOOLLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		PLUS=1, MINUS=2, TIMES=3, DIVISION=4, LPAR=5, RPAR=6, CLPAR=7, CRPAR=8, 
		SEMIC=9, COLON=10, COMMA=11, EQ=12, MINOREQ=13, GREATEREQ=14, ASS=15, 
		TRUE=16, FALSE=17, IF=18, THEN=19, ELSE=20, PRINT=21, LET=22, IN=23, VAR=24, 
		FUN=25, INT=26, BOOL=27, NUM=28, ID=29, WHITESP=30, COMMENT=31, ERR=32;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"PLUS", "MINUS", "TIMES", "DIVISION", "LPAR", "RPAR", "CLPAR", "CRPAR", 
			"SEMIC", "COLON", "COMMA", "EQ", "MINOREQ", "GREATEREQ", "ASS", "TRUE", 
			"FALSE", "IF", "THEN", "ELSE", "PRINT", "LET", "IN", "VAR", "FUN", "INT", 
			"BOOL", "NUM", "ID", "WHITESP", "COMMENT", "ERR"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'+'", "'-'", "'*'", "'/'", "'('", "')'", "'{'", "'}'", "';'", 
			"':'", "','", "'=='", "'<='", "'>='", "'='", "'true'", "'false'", "'if'", 
			"'then'", "'else'", "'print'", "'let'", "'in'", "'var'", "'fun'", "'int'", 
			"'bool'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "PLUS", "MINUS", "TIMES", "DIVISION", "LPAR", "RPAR", "CLPAR", 
			"CRPAR", "SEMIC", "COLON", "COMMA", "EQ", "MINOREQ", "GREATEREQ", "ASS", 
			"TRUE", "FALSE", "IF", "THEN", "ELSE", "PRINT", "LET", "IN", "VAR", "FUN", 
			"INT", "BOOL", "NUM", "ID", "WHITESP", "COMMENT", "ERR"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public int lexicalErrors=0;


	public FOOLLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "FOOL.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 31:
			ERR_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void ERR_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:
			 System.out.println("Invalid char "+getText()+" at line "+getLine()); lexicalErrors++; 
			break;
		}
	}

	public static final String _serializedATN =
		"\u0004\u0000 \u00c3\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b"+
		"\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002"+
		"\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002"+
		"\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002"+
		"\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002"+
		"\u0018\u0007\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002"+
		"\u001b\u0007\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002"+
		"\u001e\u0007\u001e\u0002\u001f\u0007\u001f\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001"+
		"\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001"+
		"\u0007\u0001\u0007\u0001\b\u0001\b\u0001\t\u0001\t\u0001\n\u0001\n\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r"+
		"\u0001\r\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001"+
		"\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001"+
		"\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001"+
		"\u001a\u0001\u001a\u0001\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0005"+
		"\u001b\u009c\b\u001b\n\u001b\f\u001b\u009f\t\u001b\u0003\u001b\u00a1\b"+
		"\u001b\u0001\u001c\u0001\u001c\u0005\u001c\u00a5\b\u001c\n\u001c\f\u001c"+
		"\u00a8\t\u001c\u0001\u001d\u0004\u001d\u00ab\b\u001d\u000b\u001d\f\u001d"+
		"\u00ac\u0001\u001d\u0001\u001d\u0001\u001e\u0001\u001e\u0001\u001e\u0001"+
		"\u001e\u0005\u001e\u00b5\b\u001e\n\u001e\f\u001e\u00b8\t\u001e\u0001\u001e"+
		"\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001f\u0001\u001f"+
		"\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u00b6\u0000 \u0001\u0001\u0003"+
		"\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011"+
		"\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e\u001d\u000f\u001f\u0010"+
		"!\u0011#\u0012%\u0013\'\u0014)\u0015+\u0016-\u0017/\u00181\u00193\u001a"+
		"5\u001b7\u001c9\u001d;\u001e=\u001f? \u0001\u0000\u0003\u0002\u0000AZ"+
		"az\u0003\u000009AZaz\u0003\u0000\t\n\r\r  \u00c7\u0000\u0001\u0001\u0000"+
		"\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000"+
		"\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000"+
		"\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000"+
		"\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000"+
		"\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000"+
		"\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000"+
		"\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000\u0000"+
		"\u0000\u001f\u0001\u0000\u0000\u0000\u0000!\u0001\u0000\u0000\u0000\u0000"+
		"#\u0001\u0000\u0000\u0000\u0000%\u0001\u0000\u0000\u0000\u0000\'\u0001"+
		"\u0000\u0000\u0000\u0000)\u0001\u0000\u0000\u0000\u0000+\u0001\u0000\u0000"+
		"\u0000\u0000-\u0001\u0000\u0000\u0000\u0000/\u0001\u0000\u0000\u0000\u0000"+
		"1\u0001\u0000\u0000\u0000\u00003\u0001\u0000\u0000\u0000\u00005\u0001"+
		"\u0000\u0000\u0000\u00007\u0001\u0000\u0000\u0000\u00009\u0001\u0000\u0000"+
		"\u0000\u0000;\u0001\u0000\u0000\u0000\u0000=\u0001\u0000\u0000\u0000\u0000"+
		"?\u0001\u0000\u0000\u0000\u0001A\u0001\u0000\u0000\u0000\u0003C\u0001"+
		"\u0000\u0000\u0000\u0005E\u0001\u0000\u0000\u0000\u0007G\u0001\u0000\u0000"+
		"\u0000\tI\u0001\u0000\u0000\u0000\u000bK\u0001\u0000\u0000\u0000\rM\u0001"+
		"\u0000\u0000\u0000\u000fO\u0001\u0000\u0000\u0000\u0011Q\u0001\u0000\u0000"+
		"\u0000\u0013S\u0001\u0000\u0000\u0000\u0015U\u0001\u0000\u0000\u0000\u0017"+
		"W\u0001\u0000\u0000\u0000\u0019Z\u0001\u0000\u0000\u0000\u001b]\u0001"+
		"\u0000\u0000\u0000\u001d`\u0001\u0000\u0000\u0000\u001fb\u0001\u0000\u0000"+
		"\u0000!g\u0001\u0000\u0000\u0000#m\u0001\u0000\u0000\u0000%p\u0001\u0000"+
		"\u0000\u0000\'u\u0001\u0000\u0000\u0000)z\u0001\u0000\u0000\u0000+\u0080"+
		"\u0001\u0000\u0000\u0000-\u0084\u0001\u0000\u0000\u0000/\u0087\u0001\u0000"+
		"\u0000\u00001\u008b\u0001\u0000\u0000\u00003\u008f\u0001\u0000\u0000\u0000"+
		"5\u0093\u0001\u0000\u0000\u00007\u00a0\u0001\u0000\u0000\u00009\u00a2"+
		"\u0001\u0000\u0000\u0000;\u00aa\u0001\u0000\u0000\u0000=\u00b0\u0001\u0000"+
		"\u0000\u0000?\u00be\u0001\u0000\u0000\u0000AB\u0005+\u0000\u0000B\u0002"+
		"\u0001\u0000\u0000\u0000CD\u0005-\u0000\u0000D\u0004\u0001\u0000\u0000"+
		"\u0000EF\u0005*\u0000\u0000F\u0006\u0001\u0000\u0000\u0000GH\u0005/\u0000"+
		"\u0000H\b\u0001\u0000\u0000\u0000IJ\u0005(\u0000\u0000J\n\u0001\u0000"+
		"\u0000\u0000KL\u0005)\u0000\u0000L\f\u0001\u0000\u0000\u0000MN\u0005{"+
		"\u0000\u0000N\u000e\u0001\u0000\u0000\u0000OP\u0005}\u0000\u0000P\u0010"+
		"\u0001\u0000\u0000\u0000QR\u0005;\u0000\u0000R\u0012\u0001\u0000\u0000"+
		"\u0000ST\u0005:\u0000\u0000T\u0014\u0001\u0000\u0000\u0000UV\u0005,\u0000"+
		"\u0000V\u0016\u0001\u0000\u0000\u0000WX\u0005=\u0000\u0000XY\u0005=\u0000"+
		"\u0000Y\u0018\u0001\u0000\u0000\u0000Z[\u0005<\u0000\u0000[\\\u0005=\u0000"+
		"\u0000\\\u001a\u0001\u0000\u0000\u0000]^\u0005>\u0000\u0000^_\u0005=\u0000"+
		"\u0000_\u001c\u0001\u0000\u0000\u0000`a\u0005=\u0000\u0000a\u001e\u0001"+
		"\u0000\u0000\u0000bc\u0005t\u0000\u0000cd\u0005r\u0000\u0000de\u0005u"+
		"\u0000\u0000ef\u0005e\u0000\u0000f \u0001\u0000\u0000\u0000gh\u0005f\u0000"+
		"\u0000hi\u0005a\u0000\u0000ij\u0005l\u0000\u0000jk\u0005s\u0000\u0000"+
		"kl\u0005e\u0000\u0000l\"\u0001\u0000\u0000\u0000mn\u0005i\u0000\u0000"+
		"no\u0005f\u0000\u0000o$\u0001\u0000\u0000\u0000pq\u0005t\u0000\u0000q"+
		"r\u0005h\u0000\u0000rs\u0005e\u0000\u0000st\u0005n\u0000\u0000t&\u0001"+
		"\u0000\u0000\u0000uv\u0005e\u0000\u0000vw\u0005l\u0000\u0000wx\u0005s"+
		"\u0000\u0000xy\u0005e\u0000\u0000y(\u0001\u0000\u0000\u0000z{\u0005p\u0000"+
		"\u0000{|\u0005r\u0000\u0000|}\u0005i\u0000\u0000}~\u0005n\u0000\u0000"+
		"~\u007f\u0005t\u0000\u0000\u007f*\u0001\u0000\u0000\u0000\u0080\u0081"+
		"\u0005l\u0000\u0000\u0081\u0082\u0005e\u0000\u0000\u0082\u0083\u0005t"+
		"\u0000\u0000\u0083,\u0001\u0000\u0000\u0000\u0084\u0085\u0005i\u0000\u0000"+
		"\u0085\u0086\u0005n\u0000\u0000\u0086.\u0001\u0000\u0000\u0000\u0087\u0088"+
		"\u0005v\u0000\u0000\u0088\u0089\u0005a\u0000\u0000\u0089\u008a\u0005r"+
		"\u0000\u0000\u008a0\u0001\u0000\u0000\u0000\u008b\u008c\u0005f\u0000\u0000"+
		"\u008c\u008d\u0005u\u0000\u0000\u008d\u008e\u0005n\u0000\u0000\u008e2"+
		"\u0001\u0000\u0000\u0000\u008f\u0090\u0005i\u0000\u0000\u0090\u0091\u0005"+
		"n\u0000\u0000\u0091\u0092\u0005t\u0000\u0000\u00924\u0001\u0000\u0000"+
		"\u0000\u0093\u0094\u0005b\u0000\u0000\u0094\u0095\u0005o\u0000\u0000\u0095"+
		"\u0096\u0005o\u0000\u0000\u0096\u0097\u0005l\u0000\u0000\u00976\u0001"+
		"\u0000\u0000\u0000\u0098\u00a1\u00050\u0000\u0000\u0099\u009d\u000219"+
		"\u0000\u009a\u009c\u000209\u0000\u009b\u009a\u0001\u0000\u0000\u0000\u009c"+
		"\u009f\u0001\u0000\u0000\u0000\u009d\u009b\u0001\u0000\u0000\u0000\u009d"+
		"\u009e\u0001\u0000\u0000\u0000\u009e\u00a1\u0001\u0000\u0000\u0000\u009f"+
		"\u009d\u0001\u0000\u0000\u0000\u00a0\u0098\u0001\u0000\u0000\u0000\u00a0"+
		"\u0099\u0001\u0000\u0000\u0000\u00a18\u0001\u0000\u0000\u0000\u00a2\u00a6"+
		"\u0007\u0000\u0000\u0000\u00a3\u00a5\u0007\u0001\u0000\u0000\u00a4\u00a3"+
		"\u0001\u0000\u0000\u0000\u00a5\u00a8\u0001\u0000\u0000\u0000\u00a6\u00a4"+
		"\u0001\u0000\u0000\u0000\u00a6\u00a7\u0001\u0000\u0000\u0000\u00a7:\u0001"+
		"\u0000\u0000\u0000\u00a8\u00a6\u0001\u0000\u0000\u0000\u00a9\u00ab\u0007"+
		"\u0002\u0000\u0000\u00aa\u00a9\u0001\u0000\u0000\u0000\u00ab\u00ac\u0001"+
		"\u0000\u0000\u0000\u00ac\u00aa\u0001\u0000\u0000\u0000\u00ac\u00ad\u0001"+
		"\u0000\u0000\u0000\u00ad\u00ae\u0001\u0000\u0000\u0000\u00ae\u00af\u0006"+
		"\u001d\u0000\u0000\u00af<\u0001\u0000\u0000\u0000\u00b0\u00b1\u0005/\u0000"+
		"\u0000\u00b1\u00b2\u0005*\u0000\u0000\u00b2\u00b6\u0001\u0000\u0000\u0000"+
		"\u00b3\u00b5\t\u0000\u0000\u0000\u00b4\u00b3\u0001\u0000\u0000\u0000\u00b5"+
		"\u00b8\u0001\u0000\u0000\u0000\u00b6\u00b7\u0001\u0000\u0000\u0000\u00b6"+
		"\u00b4\u0001\u0000\u0000\u0000\u00b7\u00b9\u0001\u0000\u0000\u0000\u00b8"+
		"\u00b6\u0001\u0000\u0000\u0000\u00b9\u00ba\u0005*\u0000\u0000\u00ba\u00bb"+
		"\u0005/\u0000\u0000\u00bb\u00bc\u0001\u0000\u0000\u0000\u00bc\u00bd\u0006"+
		"\u001e\u0000\u0000\u00bd>\u0001\u0000\u0000\u0000\u00be\u00bf\t\u0000"+
		"\u0000\u0000\u00bf\u00c0\u0006\u001f\u0001\u0000\u00c0\u00c1\u0001\u0000"+
		"\u0000\u0000\u00c1\u00c2\u0006\u001f\u0000\u0000\u00c2@\u0001\u0000\u0000"+
		"\u0000\u0006\u0000\u009d\u00a0\u00a6\u00ac\u00b6\u0002\u0000\u0001\u0000"+
		"\u0001\u001f\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}