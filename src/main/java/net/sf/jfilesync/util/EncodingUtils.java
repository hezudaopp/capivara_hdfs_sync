/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2006 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: EncodingUtils.java,v 1.4 2006/08/29 19:58:19 hunold Exp $
 */
package net.sf.jfilesync.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

public class EncodingUtils {

//  private static final String[] defaultEncodings = {
//    "US-ASCII",
//    "ISO-8859-1",
//    "UTF-8",
//    "UTF-16BE",
//    "UTF-16LE",
//    "UTF-16"
//  };

  public static String[] getDefaultEncodings() {
//    final String[] encodings = new String[defaultEncodings.length];
//    System.arraycopy(defaultEncodings, 0, encodings, 0, defaultEncodings.length);
//    return encodings;
    final List<String> encodingNames = new ArrayList<String>();
    SortedMap<String, Charset> map = Charset.availableCharsets();
    for( String charsetName : map.keySet() ) {
      encodingNames.add(charsetName);
    }
    return encodingNames.toArray(new String[encodingNames.size()]);
  }

  public static String getJVMEnconding() {
    return System.getProperty("file.encoding");
  }

  public static String decodeToHostEncoding(String text, String sourceEncoding,
      String targetEncoding) throws UnsupportedEncodingException {
    String ret = new String(text.getBytes(targetEncoding), sourceEncoding);
    return ret;
  }

  public static String encodeToTargetEncoding(String text, String sourceEncoding,
      String targetEncoding) throws UnsupportedEncodingException {
    String ret = new String(text.getBytes(sourceEncoding), targetEncoding);
    return ret;
  }

}
