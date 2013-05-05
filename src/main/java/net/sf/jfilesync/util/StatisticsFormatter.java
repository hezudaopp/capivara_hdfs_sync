/*
 * capivara - Java File Synchronization
 *
 * Created on 14-Aug-2005
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
 * $Id: StatisticsFormatter.java,v 1.6 2005/12/01 19:40:20 hunold Exp $
 */
package net.sf.jfilesync.util;

public class StatisticsFormatter {

  
  public static String formatTransferRate(final float rate) {
    String rateString = TStringTool.formatDouble(rate, 2);
    rateString += " kB/s";
    return rateString;
  }
  
  public static String formatSeconds(final long seconds) {    
    StringBuffer res = new StringBuffer();
    long remain = seconds;
    
    long hours = remain / 3600L;
    if( hours > 0 ) {
      res.append(Long.toString(hours) + ":");
      remain %= 3600L;
    }
    
    long mins = remain / 60L;
    String minString  = Long.toString(mins) + ":";
    if( mins < 10 && hours > 0 ) {
        minString = "0" + minString;  
    }
    res.append(minString);
    remain %= 60L;
    
    long secs = remain;
    String secsString = Long.toString(secs);
    if( secs < 10 ) {
      secsString = "0" + secsString;
    }    
    res.append(secsString);
    
    return res.toString();
  }
  
}
