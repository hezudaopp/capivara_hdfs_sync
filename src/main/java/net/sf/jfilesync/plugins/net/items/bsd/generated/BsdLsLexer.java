// $ANTLR 3.3 Nov 30, 2010 12:45:30 /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g 2011-05-04 10:48:40

package net.sf.jfilesync.plugins.net.items.bsd.generated;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class BsdLsLexer extends Lexer {
    public static final int EOF=-1;
    public static final int T__9=9;
    public static final int WS=4;
    public static final int NUMBER=5;
    public static final int NEWLINE=6;
    public static final int STRING=7;
    public static final int TIME_STAMP=8;

    // delegates
    // delegators

    public BsdLsLexer() {;} 
    public BsdLsLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public BsdLsLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "/Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g"; }

    // $ANTLR start "T__9"
    public final void mT__9() throws RecognitionException {
        try {
            int _type = T__9;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:7:6: ( 'total' )
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:7:8: 'total'
            {
            match("total"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__9"

    // $ANTLR start "TIME_STAMP"
    public final void mTIME_STAMP() throws RecognitionException {
        try {
            int _type = TIME_STAMP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            CommonToken HOUR=null;
            CommonToken MIN=null;
            CommonToken SEC=null;

            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:157:2: (HOUR= NUMBER ':' MIN= NUMBER ':' SEC= NUMBER )
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:157:5: HOUR= NUMBER ':' MIN= NUMBER ':' SEC= NUMBER
            {
            int HOURStart33 = getCharIndex();
            int HOURStartLine33 = getLine();
            int HOURStartCharPos33 = getCharPositionInLine();
            mNUMBER(); 
            HOUR = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, HOURStart33, getCharIndex()-1);
            HOUR.setLine(HOURStartLine33);
            HOUR.setCharPositionInLine(HOURStartCharPos33);
            match(':'); 
            int MINStart39 = getCharIndex();
            int MINStartLine39 = getLine();
            int MINStartCharPos39 = getCharPositionInLine();
            mNUMBER(); 
            MIN = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, MINStart39, getCharIndex()-1);
            MIN.setLine(MINStartLine39);
            MIN.setCharPositionInLine(MINStartCharPos39);
            match(':'); 
            int SECStart45 = getCharIndex();
            int SECStartLine45 = getLine();
            int SECStartCharPos45 = getCharPositionInLine();
            mNUMBER(); 
            SEC = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, SECStart45, getCharIndex()-1);
            SEC.setLine(SECStartLine45);
            SEC.setCharPositionInLine(SECStartCharPos45);

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TIME_STAMP"

    // $ANTLR start "NUMBER"
    public final void mNUMBER() throws RecognitionException {
        try {
            int _type = NUMBER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:160:8: ( ( '0' .. '9' )+ )
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:160:10: ( '0' .. '9' )+
            {
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:160:10: ( '0' .. '9' )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='0' && LA1_0<='9')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:160:11: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NUMBER"

    // $ANTLR start "NEWLINE"
    public final void mNEWLINE() throws RecognitionException {
        try {
            int _type = NEWLINE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:163:8: ( ( '\\r' )? '\\n' )
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:163:9: ( '\\r' )? '\\n'
            {
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:163:9: ( '\\r' )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='\r') ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:163:9: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }

            match('\n'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NEWLINE"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:165:5: ( ( ' ' | '\\t' )+ )
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:165:9: ( ' ' | '\\t' )+
            {
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:165:9: ( ' ' | '\\t' )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0=='\t'||LA3_0==' ') ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:
            	    {
            	    if ( input.LA(1)=='\t'||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:167:9: ( (~ ( '\"' | '\\r' | '\\n' | ' ' | '\\t' ) )+ )
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:167:11: (~ ( '\"' | '\\r' | '\\n' | ' ' | '\\t' ) )+
            {
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:167:11: (~ ( '\"' | '\\r' | '\\n' | ' ' | '\\t' ) )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>='\u0000' && LA4_0<='\b')||(LA4_0>='\u000B' && LA4_0<='\f')||(LA4_0>='\u000E' && LA4_0<='\u001F')||LA4_0=='!'||(LA4_0>='#' && LA4_0<='\uFFFF')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:167:13: ~ ( '\"' | '\\r' | '\\n' | ' ' | '\\t' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\b')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\u001F')||input.LA(1)=='!'||(input.LA(1)>='#' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING"

    public void mTokens() throws RecognitionException {
        // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:1:8: ( T__9 | TIME_STAMP | NUMBER | NEWLINE | WS | STRING )
        int alt5=6;
        alt5 = dfa5.predict(input);
        switch (alt5) {
            case 1 :
                // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:1:10: T__9
                {
                mT__9(); 

                }
                break;
            case 2 :
                // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:1:15: TIME_STAMP
                {
                mTIME_STAMP(); 

                }
                break;
            case 3 :
                // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:1:26: NUMBER
                {
                mNUMBER(); 

                }
                break;
            case 4 :
                // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:1:33: NEWLINE
                {
                mNEWLINE(); 

                }
                break;
            case 5 :
                // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:1:41: WS
                {
                mWS(); 

                }
                break;
            case 6 :
                // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:1:44: STRING
                {
                mSTRING(); 

                }
                break;

        }

    }


    protected DFA5 dfa5 = new DFA5(this);
    static final String DFA5_eotS =
        "\1\uffff\1\5\1\7\3\uffff\1\5\1\uffff\5\5\1\17\1\20\2\uffff";
    static final String DFA5_eofS =
        "\21\uffff";
    static final String DFA5_minS =
        "\1\0\1\157\1\0\3\uffff\1\164\1\uffff\1\60\1\141\1\60\1\154\1\60"+
        "\2\0\2\uffff";
    static final String DFA5_maxS =
        "\1\uffff\1\157\1\uffff\3\uffff\1\164\1\uffff\1\71\1\141\1\72\1\154"+
        "\1\71\2\uffff\2\uffff";
    static final String DFA5_acceptS =
        "\3\uffff\1\4\1\5\1\6\1\uffff\1\3\7\uffff\1\1\1\2";
    static final String DFA5_specialS =
        "\1\2\1\uffff\1\0\12\uffff\1\1\1\3\2\uffff}>";
    static final String[] DFA5_transitionS = {
            "\11\5\1\4\1\3\2\5\1\3\22\5\1\4\1\5\1\uffff\15\5\12\2\72\5\1"+
            "\1\uff8b\5",
            "\1\6",
            "\11\5\2\uffff\2\5\1\uffff\22\5\1\uffff\1\5\1\uffff\15\5\12"+
            "\2\1\10\uffc5\5",
            "",
            "",
            "",
            "\1\11",
            "",
            "\12\12",
            "\1\13",
            "\12\12\1\14",
            "\1\15",
            "\12\16",
            "\11\5\2\uffff\2\5\1\uffff\22\5\1\uffff\1\5\1\uffff\uffdd\5",
            "\11\5\2\uffff\2\5\1\uffff\22\5\1\uffff\1\5\1\uffff\15\5\12"+
            "\16\uffc6\5",
            "",
            ""
    };

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__9 | TIME_STAMP | NUMBER | NEWLINE | WS | STRING );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA5_2 = input.LA(1);

                        s = -1;
                        if ( (LA5_2==':') ) {s = 8;}

                        else if ( ((LA5_2>='0' && LA5_2<='9')) ) {s = 2;}

                        else if ( ((LA5_2>='\u0000' && LA5_2<='\b')||(LA5_2>='\u000B' && LA5_2<='\f')||(LA5_2>='\u000E' && LA5_2<='\u001F')||LA5_2=='!'||(LA5_2>='#' && LA5_2<='/')||(LA5_2>=';' && LA5_2<='\uFFFF')) ) {s = 5;}

                        else s = 7;

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA5_13 = input.LA(1);

                        s = -1;
                        if ( ((LA5_13>='\u0000' && LA5_13<='\b')||(LA5_13>='\u000B' && LA5_13<='\f')||(LA5_13>='\u000E' && LA5_13<='\u001F')||LA5_13=='!'||(LA5_13>='#' && LA5_13<='\uFFFF')) ) {s = 5;}

                        else s = 15;

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA5_0 = input.LA(1);

                        s = -1;
                        if ( (LA5_0=='t') ) {s = 1;}

                        else if ( ((LA5_0>='0' && LA5_0<='9')) ) {s = 2;}

                        else if ( (LA5_0=='\n'||LA5_0=='\r') ) {s = 3;}

                        else if ( (LA5_0=='\t'||LA5_0==' ') ) {s = 4;}

                        else if ( ((LA5_0>='\u0000' && LA5_0<='\b')||(LA5_0>='\u000B' && LA5_0<='\f')||(LA5_0>='\u000E' && LA5_0<='\u001F')||LA5_0=='!'||(LA5_0>='#' && LA5_0<='/')||(LA5_0>=':' && LA5_0<='s')||(LA5_0>='u' && LA5_0<='\uFFFF')) ) {s = 5;}

                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA5_14 = input.LA(1);

                        s = -1;
                        if ( ((LA5_14>='0' && LA5_14<='9')) ) {s = 14;}

                        else if ( ((LA5_14>='\u0000' && LA5_14<='\b')||(LA5_14>='\u000B' && LA5_14<='\f')||(LA5_14>='\u000E' && LA5_14<='\u001F')||LA5_14=='!'||(LA5_14>='#' && LA5_14<='/')||(LA5_14>=':' && LA5_14<='\uFFFF')) ) {s = 5;}

                        else s = 16;

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 5, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}