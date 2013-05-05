/*
 * capivara - Java File Synchronization
 *
 * Created on 25-Mar-2005
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
 * $Id: KeyMapPropertiesReader.java,v 1.5 2006/08/05 21:35:37 hunold Exp $
 */
package net.sf.jfilesync.gui.keymap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class KeyMapPropertiesReader {

  public static Properties readKeyProperties(final InputStream instream)
      throws IOException {
    Properties keyprops = new Properties();
    return readKeyMap(instream, keyprops);
  }

  public static Properties readKeyProperties(final InputStream instream,
      final Properties defaults) throws IOException {
    Properties keyprops = new Properties(defaults);
    return readKeyMap(instream, keyprops);
  }

  protected static Properties readKeyMap(final InputStream instream,
      final Properties props) throws IOException {
    props.load(instream);
    return props;
  }
  
}
