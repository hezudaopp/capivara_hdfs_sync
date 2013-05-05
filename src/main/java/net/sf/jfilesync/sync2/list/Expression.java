/*
 * capivara - Java File Synchronization
 *
 * Created on 28-Oct-2005
 * Copyright (C) 2005 Sascha Hunold <hunoldinho@users.sourceforge.net>
 *
<license>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
</license>
 *
 * $Id: Expression.java,v 1.9 2006/03/06 11:08:51 hunold Exp $
 */
package net.sf.jfilesync.sync2.list;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Expression
implements Cloneable {

  private final String exp;

  private final static Logger LOGGER = Logger.getLogger(Expression.class
      .getName());

  public Expression(String exp) {
    this.exp = exp;
  }

  public String getExpressionString() {
    return exp;
  }

  public boolean matches(String path) {
    boolean ret = false;
    try {
      Pattern pattern = Pattern.compile(exp);
      Matcher matcher = pattern.matcher(path);
      ret = matcher.matches();
    }
    catch(PatternSyntaxException e) {
      LOGGER.warning(e.getMessage());
    }
    return ret;
  }

  public boolean equals(Object o) {
    boolean ret = false;
    if( o instanceof Expression ) {
      ret = exp.equals( ((Expression)o).toString() );
    }
    return ret;
  }

  public String toString() {
    return exp;
  }

  public Object clone() {
    Expression copy = new Expression(new String(exp));
    return copy;
  }

  /*
  private static String decodeExpression(final String e) {
    StringBuffer decoded = new StringBuffer();
    for(int i=0; i<e.length(); i++) {
      if( e.charAt(i) == '\\') {
        if( i < e.length()-1 ) {
          decoded.append('\\');
          decoded.append(e.charAt(i+1));
          i++;
          continue;
        }
        else {
          decoded.append(e.charAt(i));
        }
      }
      if( e.charAt(i) == '*' ) {
        decoded.append(".*");
      }
      else if( e.charAt(i) == '.' ) {
        decoded.append("\\.");
      }
      else {
        decoded.append(e.charAt(i));
      }
    }
    return decoded.toString();
  }
  */

  public static void validate(final String pattern)
      throws PatternSyntaxException {
    Pattern.compile(pattern);
  }

}
