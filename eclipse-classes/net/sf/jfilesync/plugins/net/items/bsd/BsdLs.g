/*
 * parse BSD ls command for Capivara
 * ls -lac
 */


grammar BsdLs;

@header {

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

}

@lexer::header 
{
package net.sf.jfilesync.plugins.net.items.bsd.generated;
}

@members {
private String cwd;
private TPathControlInterface pci;
}


lsoutput  [ String cwd, TPathControlInterface pci ]

returns [ List<TFileProperties> l ]

scope { 
List<TFileProperties> fileList;
SimpleDateFormat df;
GnuPermissionParser permParser;
}

@init { 
	this.cwd = cwd;
	this.pci = pci;

$lsoutput::fileList = new ArrayList<TFileProperties>(); 
$lsoutput::df = new SimpleDateFormat("MMM d HH:mm:ss yyyy");
$lsoutput::permParser = new GnuPermissionParser();

}
  
  : fileinfo
  
  { $l = $lsoutput::fileList; }
  ;

fileinfo : summary file+
	;
	
summary	: 'total' WS NUMBER NEWLINE
	;
	
file	 
	:
	PERM=permissions WS nlinks WS owner WS group WS SIZE=bytes WS MTIME=mtime WS FNAME=file_name NEWLINE
	{ 
		TFileProperties fp = new TFileProperties();
		
		String filen = $FNAME.text;
		
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
		
		fp.setFileSize(new BigInteger($SIZE.nbytes));
		fp.setFileModTime($MTIME.time);
		fp.setAttributes($PERM.attrib);
		
		if( $PERM.text.charAt(0) == 'd' ) {
			fp.setDirectoryFlag(true);
		}
		
		if( $PERM.text.charAt(0) == 'l' ) {
	    	fp.setLinkFlag(true);
		}
		
		$lsoutput::fileList.add(fp);
	}
	;
	
permissions
        returns [ TFileAttributes attrib ]
	: str=STRING
	{		
	  $attrib = new TFileAttributes();
	  int perm = $lsoutput::permParser.parserPermissionString($str.text);
	  $attrib.setPermissions(perm);
	}
	;

file_name
	: fname (WS fname)*
	;
	
nlinks	: NUMBER
	;

owner   : STRING
	;
	
group	: STRING
	;
	
bytes	
	returns [ String nbytes ]
    :
    NUM=(NUMBER | STRING) (WS MAJOR=NUMBER)?
	{ 
		$nbytes = $NUM.text;
		if( $nbytes.endsWith(",") ) {
			$nbytes = "0";
		} 
	}
	;
				
mtime	
	returns [ long time ]
	:  MONTH=STRING WS DAY=NUMBER WS TIME=TIME_STAMP WS YEAR=NUMBER
	{
	  String tString = $MONTH.text + " " + $DAY.text + " " + $TIME.text + " " + $YEAR.text;
	  Date d = $lsoutput::df.parse(tString, new ParsePosition(0));
      $time = d.getTime();
	}
	;
			
fname
	:	( STRING | NUMBER )+
	;
					
TIME_STAMP	
	:  HOUR=NUMBER ':' MIN=NUMBER ':' SEC=NUMBER
	; 

NUMBER	: ('0'..'9')+
	;

NEWLINE:'\r'? '\n'; // {$channel=HIDDEN;} ;

WS  :   (' '|'\t')+; // {skip();} ;

STRING  : ( ~('"'|'\r'|'\n'|' '|'\t') )+ 
        ;

//CHAR	: ( 'a'..'z' | 'A'..'Z' );

        