/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TStringTool.java,v 1.4 2005/08/19 21:29:02 hunold Exp $
 */

package net.sf.jfilesync.util;

import java.text.NumberFormat;

/**
 * @author sascha
 *
 */
public class TStringTool {

  private static NumberFormat format = NumberFormat.getInstance();

  /**
   * This method formats a double value according to the specification
   * of a string length. It can be used to get fixed length strings of
   * doubles.
   *
   * @author SaHu
   * @param d double value
   * @param fractionDigits number of fraction digits
   * @param length fixed length of output string
   * @return formatted string
   */

  public static String formatDouble(double d, int fractionDigits, int length) {
    format.setMaximumFractionDigits(fractionDigits);
    format.setMinimumFractionDigits(fractionDigits);
    String output = format.format(d);
    
    if( output.length() < length ) {
      String empty = "";
      for(int i=0; i<length-output.length(); i++) {
        empty += " ";
      }
      output = empty.concat(output);
    }
    return output;
  }

  public static String formatDouble(double d, int fractionDigits) {
    format.setMaximumFractionDigits(fractionDigits);
    format.setMinimumFractionDigits(fractionDigits);
    return format.format(d);
  }


}
