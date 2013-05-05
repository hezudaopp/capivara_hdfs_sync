// $ANTLR 3.3 Nov 30, 2010 12:45:30 /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g 2011-05-04 10:48:40


package net.sf.jfilesync.plugins.net.items.bsd.generated;

import java.math.BigInteger;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;

import net.sf.jfilesync.engine.TFileProperties;
import net.sf.jfilesync.engine.TFileAttributes;
import net.sf.jfilesync.plugins.net.items.GnuPermissionParser;
import net.sf.jfilesync.util.TPathControlInterface;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class BsdLsParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "WS", "NUMBER", "NEWLINE", "STRING", "TIME_STAMP", "'total'"
    };
    public static final int EOF=-1;
    public static final int T__9=9;
    public static final int WS=4;
    public static final int NUMBER=5;
    public static final int NEWLINE=6;
    public static final int STRING=7;
    public static final int TIME_STAMP=8;

    // delegates
    // delegators


        public BsdLsParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public BsdLsParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return BsdLsParser.tokenNames; }
    public String getGrammarFileName() { return "/Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g"; }


    private String cwd;
    private TPathControlInterface pci;


    protected static class lsoutput_scope {
        List<TFileProperties> fileList;
        SimpleDateFormat df;
        GnuPermissionParser permParser;
    }
    protected Stack lsoutput_stack = new Stack();


    // $ANTLR start "lsoutput"
    // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:38:1: lsoutput[ String cwd, TPathControlInterface pci ] returns [ List<TFileProperties> l ] : fileinfo ;
    public final List<TFileProperties> lsoutput(String cwd, TPathControlInterface pci) throws RecognitionException {
        lsoutput_stack.push(new lsoutput_scope());
        List<TFileProperties> l = null;

         
        	this.cwd = cwd;
        	this.pci = pci;

        ((lsoutput_scope)lsoutput_stack.peek()).fileList = new ArrayList<TFileProperties>(); 
        ((lsoutput_scope)lsoutput_stack.peek()).df = new SimpleDateFormat("MMM d HH:mm:ss yyyy");
        ((lsoutput_scope)lsoutput_stack.peek()).permParser = new GnuPermissionParser();


        try {
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:58:3: ( fileinfo )
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:58:5: fileinfo
            {
            pushFollow(FOLLOW_fileinfo_in_lsoutput61);
            fileinfo();

            state._fsp--;

             l = ((lsoutput_scope)lsoutput_stack.peek()).fileList; 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            lsoutput_stack.pop();
        }
        return l;
    }
    // $ANTLR end "lsoutput"


    // $ANTLR start "fileinfo"
    // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:63:1: fileinfo : summary ( file )+ ;
    public final void fileinfo() throws RecognitionException {
        try {
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:63:10: ( summary ( file )+ )
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:63:12: summary ( file )+
            {
            pushFollow(FOLLOW_summary_in_fileinfo79);
            summary();

            state._fsp--;

            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:63:20: ( file )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==STRING) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:63:20: file
            	    {
            	    pushFollow(FOLLOW_file_in_fileinfo81);
            	    file();

            	    state._fsp--;


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

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "fileinfo"


    // $ANTLR start "summary"
    // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:66:1: summary : 'total' WS NUMBER NEWLINE ;
    public final void summary() throws RecognitionException {
        try {
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:66:9: ( 'total' WS NUMBER NEWLINE )
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:66:11: 'total' WS NUMBER NEWLINE
            {
            match(input,9,FOLLOW_9_in_summary93); 
            match(input,WS,FOLLOW_WS_in_summary95); 
            match(input,NUMBER,FOLLOW_NUMBER_in_summary97); 
            match(input,NEWLINE,FOLLOW_NEWLINE_in_summary99); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "summary"


    // $ANTLR start "file"
    // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:69:1: file : PERM= permissions WS nlinks WS owner WS group WS SIZE= bytes WS MTIME= mtime WS FNAME= file_name NEWLINE ;
    public final void file() throws RecognitionException {
        BsdLsParser.permissions_return PERM = null;

        String SIZE = null;

        long MTIME = 0;

        BsdLsParser.file_name_return FNAME = null;


        try {
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:70:2: (PERM= permissions WS nlinks WS owner WS group WS SIZE= bytes WS MTIME= mtime WS FNAME= file_name NEWLINE )
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:71:2: PERM= permissions WS nlinks WS owner WS group WS SIZE= bytes WS MTIME= mtime WS FNAME= file_name NEWLINE
            {
            pushFollow(FOLLOW_permissions_in_file116);
            PERM=permissions();

            state._fsp--;

            match(input,WS,FOLLOW_WS_in_file118); 
            pushFollow(FOLLOW_nlinks_in_file120);
            nlinks();

            state._fsp--;

            match(input,WS,FOLLOW_WS_in_file122); 
            pushFollow(FOLLOW_owner_in_file124);
            owner();

            state._fsp--;

            match(input,WS,FOLLOW_WS_in_file126); 
            pushFollow(FOLLOW_group_in_file128);
            group();

            state._fsp--;

            match(input,WS,FOLLOW_WS_in_file130); 
            pushFollow(FOLLOW_bytes_in_file134);
            SIZE=bytes();

            state._fsp--;

            match(input,WS,FOLLOW_WS_in_file136); 
            pushFollow(FOLLOW_mtime_in_file140);
            MTIME=mtime();

            state._fsp--;

            match(input,WS,FOLLOW_WS_in_file142); 
            pushFollow(FOLLOW_file_name_in_file146);
            FNAME=file_name();

            state._fsp--;

            match(input,NEWLINE,FOLLOW_NEWLINE_in_file148); 
             
            		TFileProperties fp = new TFileProperties();
            		
            		String filen = (FNAME!=null?input.toString(FNAME.start,FNAME.stop):null);
            		
            		// file name could still contain link target such as this
            		// name -> link
            		// need to remove this link
            		int idx = 0;
            		if( (idx = filen.indexOf("->")) != -1 ) {
            			filen = filen.substring(0, idx);
            			filen = filen.trim();
            		}
            		
            		fp.setFileName(filen);
            		
            		String absPath = pci.appendDirectory(this.cwd, filen);
            		fp.setAbsoluteFileName(absPath);
            		
            		fp.setFileSize(new BigInteger(SIZE));
            		fp.setFileModTime(MTIME);
            		fp.setAttributes((PERM!=null?PERM.attrib:null));
            		
            		if( (PERM!=null?input.toString(PERM.start,PERM.stop):null).charAt(0) == 'd' ) {
            			fp.setDirectoryFlag(true);
            		}
            		
            		if( (PERM!=null?input.toString(PERM.start,PERM.stop):null).charAt(0) == 'l' ) {
            	    	fp.setLinkFlag(true);
            		}
            		
            		((lsoutput_scope)lsoutput_stack.peek()).fileList.add(fp);
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "file"

    public static class permissions_return extends ParserRuleReturnScope {
        public TFileAttributes attrib;
    };

    // $ANTLR start "permissions"
    // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:107:1: permissions returns [ TFileAttributes attrib ] : str= STRING ;
    public final BsdLsParser.permissions_return permissions() throws RecognitionException {
        BsdLsParser.permissions_return retval = new BsdLsParser.permissions_return();
        retval.start = input.LT(1);

        Token str=null;

        try {
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:109:2: (str= STRING )
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:109:4: str= STRING
            {
            str=(Token)match(input,STRING,FOLLOW_STRING_in_permissions177); 
            		
            	  retval.attrib = new TFileAttributes();
            	  int perm = ((lsoutput_scope)lsoutput_stack.peek()).permParser.parserPermissionString((str!=null?str.getText():null));
            	  retval.attrib.setPermissions(perm);
            	

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "permissions"

    public static class file_name_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "file_name"
    // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:117:1: file_name : fname ( WS fname )* ;
    public final BsdLsParser.file_name_return file_name() throws RecognitionException {
        BsdLsParser.file_name_return retval = new BsdLsParser.file_name_return();
        retval.start = input.LT(1);

        try {
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:118:2: ( fname ( WS fname )* )
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:118:4: fname ( WS fname )*
            {
            pushFollow(FOLLOW_fname_in_file_name191);
            fname();

            state._fsp--;

            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:118:10: ( WS fname )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==WS) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:118:11: WS fname
            	    {
            	    match(input,WS,FOLLOW_WS_in_file_name194); 
            	    pushFollow(FOLLOW_fname_in_file_name196);
            	    fname();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "file_name"


    // $ANTLR start "nlinks"
    // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:121:1: nlinks : NUMBER ;
    public final void nlinks() throws RecognitionException {
        try {
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:121:8: ( NUMBER )
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:121:10: NUMBER
            {
            match(input,NUMBER,FOLLOW_NUMBER_in_nlinks209); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "nlinks"


    // $ANTLR start "owner"
    // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:124:1: owner : STRING ;
    public final void owner() throws RecognitionException {
        try {
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:124:9: ( STRING )
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:124:11: STRING
            {
            match(input,STRING,FOLLOW_STRING_in_owner221); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "owner"


    // $ANTLR start "group"
    // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:127:1: group : STRING ;
    public final void group() throws RecognitionException {
        try {
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:127:7: ( STRING )
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:127:9: STRING
            {
            match(input,STRING,FOLLOW_STRING_in_group232); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "group"


    // $ANTLR start "bytes"
    // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:130:1: bytes returns [ String nbytes ] : NUM= ( NUMBER | STRING ) ( WS MAJOR= NUMBER )? ;
    public final String bytes() throws RecognitionException {
        String nbytes = null;

        Token NUM=null;
        Token MAJOR=null;

        try {
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:132:5: (NUM= ( NUMBER | STRING ) ( WS MAJOR= NUMBER )? )
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:133:5: NUM= ( NUMBER | STRING ) ( WS MAJOR= NUMBER )?
            {
            NUM=(Token)input.LT(1);
            if ( input.LA(1)==NUMBER||input.LA(1)==STRING ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:133:27: ( WS MAJOR= NUMBER )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==WS) ) {
                int LA3_1 = input.LA(2);

                if ( (LA3_1==NUMBER) ) {
                    alt3=1;
                }
            }
            switch (alt3) {
                case 1 :
                    // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:133:28: WS MAJOR= NUMBER
                    {
                    match(input,WS,FOLLOW_WS_in_bytes268); 
                    MAJOR=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_bytes272); 

                    }
                    break;

            }

             
            		nbytes = (NUM!=null?NUM.getText():null);
            		if( nbytes.endsWith(",") ) {
            			nbytes = "0";
            		} 
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return nbytes;
    }
    // $ANTLR end "bytes"


    // $ANTLR start "mtime"
    // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:142:1: mtime returns [ long time ] : MONTH= STRING WS DAY= NUMBER WS TIME= TIME_STAMP WS YEAR= NUMBER ;
    public final long mtime() throws RecognitionException {
        long time = 0;

        Token MONTH=null;
        Token DAY=null;
        Token TIME=null;
        Token YEAR=null;

        try {
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:144:2: (MONTH= STRING WS DAY= NUMBER WS TIME= TIME_STAMP WS YEAR= NUMBER )
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:144:5: MONTH= STRING WS DAY= NUMBER WS TIME= TIME_STAMP WS YEAR= NUMBER
            {
            MONTH=(Token)match(input,STRING,FOLLOW_STRING_in_mtime301); 
            match(input,WS,FOLLOW_WS_in_mtime303); 
            DAY=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_mtime307); 
            match(input,WS,FOLLOW_WS_in_mtime309); 
            TIME=(Token)match(input,TIME_STAMP,FOLLOW_TIME_STAMP_in_mtime313); 
            match(input,WS,FOLLOW_WS_in_mtime315); 
            YEAR=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_mtime319); 

            	  String tString = (MONTH!=null?MONTH.getText():null) + " " + (DAY!=null?DAY.getText():null) + " " + (TIME!=null?TIME.getText():null) + " " + (YEAR!=null?YEAR.getText():null);
            	  Date d = ((lsoutput_scope)lsoutput_stack.peek()).df.parse(tString, new ParsePosition(0));
                  time = d.getTime();
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return time;
    }
    // $ANTLR end "mtime"


    // $ANTLR start "fname"
    // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:152:1: fname : ( STRING | NUMBER )+ ;
    public final void fname() throws RecognitionException {
        try {
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:153:2: ( ( STRING | NUMBER )+ )
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:153:4: ( STRING | NUMBER )+
            {
            // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:153:4: ( STRING | NUMBER )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==NUMBER||LA4_0==STRING) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /Users/sascha/programming/gpl/capivara-git/src/net/sf/jfilesync/plugins/net/items/bsd/BsdLs.g:
            	    {
            	    if ( input.LA(1)==NUMBER||input.LA(1)==STRING ) {
            	        input.consume();
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


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

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "fname"

    // Delegated rules


 

    public static final BitSet FOLLOW_fileinfo_in_lsoutput61 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_summary_in_fileinfo79 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_file_in_fileinfo81 = new BitSet(new long[]{0x0000000000000082L});
    public static final BitSet FOLLOW_9_in_summary93 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_WS_in_summary95 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NUMBER_in_summary97 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_NEWLINE_in_summary99 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_permissions_in_file116 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_WS_in_file118 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_nlinks_in_file120 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_WS_in_file122 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_owner_in_file124 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_WS_in_file126 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_group_in_file128 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_WS_in_file130 = new BitSet(new long[]{0x00000000000000A0L});
    public static final BitSet FOLLOW_bytes_in_file134 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_WS_in_file136 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_mtime_in_file140 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_WS_in_file142 = new BitSet(new long[]{0x00000000000000A0L});
    public static final BitSet FOLLOW_file_name_in_file146 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_NEWLINE_in_file148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_permissions177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fname_in_file_name191 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_WS_in_file_name194 = new BitSet(new long[]{0x00000000000000A0L});
    public static final BitSet FOLLOW_fname_in_file_name196 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_NUMBER_in_nlinks209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_owner221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_group232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_bytes259 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_WS_in_bytes268 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NUMBER_in_bytes272 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_mtime301 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_WS_in_mtime303 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NUMBER_in_mtime307 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_WS_in_mtime309 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_TIME_STAMP_in_mtime313 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_WS_in_mtime315 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NUMBER_in_mtime319 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_fname336 = new BitSet(new long[]{0x00000000000000A2L});

}