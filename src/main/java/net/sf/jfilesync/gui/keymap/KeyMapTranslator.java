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
 * $Id: KeyMapTranslator.java,v 1.7 2006/08/05 21:35:37 hunold Exp $
 */
package net.sf.jfilesync.gui.keymap;

import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.jfilesync.gui.TControlCenter;

/**
 *
 * @author sascha
 * @deprecated replaced by KeyStrokeConfigLoader
 */

public class KeyMapTranslator {

  private final static Logger LOGGER = Logger.getLogger(TControlCenter.class
      .getName());

  public static KeyStrokeModel translateKeyMap(final Properties keyprops) {
    KeyStrokeModel keymodel = new KeyStrokeModel();

    Enumeration keyEnum = keyprops.keys();
    while( keyEnum.hasMoreElements() ) {

      String propKey = (String)keyEnum.nextElement();
      String keyStrokeDefinition = keyprops.getProperty(propKey);

      String shortcutName = getShortcutName(propKey);
      KeyStroke keyStroke = parseKeyStroke(keyStrokeDefinition);

      if( keyStroke != null ) {
        LOGGER.info("Add KeyStroke : " + shortcutName + " , " + keyStroke);

        keymodel.addKeyStrokeBinding(shortcutName, keyStroke);
      }
    }

    return keymodel;
  }

  /*
   * This method was originally written by Slava Pestov.
   * Copyright (C) 1999, 2003 Slava Pestov
   *
   * changed to my own needs
   *   sahu
   */

  protected static KeyStroke parseKeyStroke(String def) {

    if (def == null)
      return null;

    int modifiers = 0;
    int index = def.indexOf('+');

    if (index != -1) {
      for (int i = 0; i < index; i++) {
        switch (Character.toUpperCase(def.charAt(i))) {
        case 'A':
          modifiers |= KeyEvent.ALT_MASK;
          break;
        case 'C':
          modifiers |= KeyEvent.CTRL_MASK;
          break;
        case 'M':
          modifiers |= KeyEvent.META_MASK;
          break;
        case 'S':
          modifiers |= KeyEvent.SHIFT_MASK;
          break;
        }
      }
    }

    String key = def.substring(index + 1);
    if (key.length() == 1) {
      char ch = key.charAt(0);
      if (modifiers == 0) {
        return KeyStroke.getKeyStroke(ch);
      } else {
        return KeyStroke.getKeyStroke(Character.toUpperCase(ch), modifiers);
      }
    } else if (key.length() == 0) {
      LOGGER.log(Level.SEVERE, "Invalid key stroke: " + def);
      return null;
    } else {
      int ch;

      try {
        ch = KeyEvent.class.getField("VK_".concat(key)).getInt(null);
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Invalid key stroke: " + def);
        return null;
      }

      return KeyStroke.getKeyStroke(ch, modifiers);
    }

  } // end parseKeyStroke

  public static String getShortcutName(String shortcut) {
    String retval = shortcut;
    int idx = retval.indexOf(".shortcut");
    if (idx != -1 && idx > 0) {
      retval = retval.substring(0, idx);
    } else {
      idx = retval.indexOf(".shortcut2");
      if (idx != -1 && idx > 0) {
        retval = retval.substring(0, idx);
      }
    }
    return retval;
  }

} // end KeyMapTranslator
