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
 * $Id: KeyStrokeModel.java,v 1.6 2006/08/05 21:35:37 hunold Exp $
 */
package net.sf.jfilesync.gui.keymap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import net.sf.jfilesync.MainWin;

public class KeyStrokeModel {

  private HashMap<String, List<KeyStroke>> keyStrokeHash = new HashMap<String, List<KeyStroke>>();

  public final static String TEXT_VIEW = "viewer";

  private final static Logger LOGGER = Logger.getLogger(KeyStrokeModel.class
      .getName());

  public KeyStrokeModel() {
  }

  public void addKeyStrokeBinding(final String shortcut,
      final KeyStroke keystroke) {

    if (keyStrokeHash.containsKey(shortcut)) {
      List<KeyStroke> ksList = keyStrokeHash.get(shortcut);
      ksList.add(keystroke);
    } else {
      List<KeyStroke> ksList = new ArrayList<KeyStroke>();
      ksList.add(keystroke);
      keyStrokeHash.put(shortcut, ksList);
    }
  }

  /**
   * @param shortcut
   * @return returns keystroke for shortcut or null if shortcut unknown
   */
  public List<KeyStroke> getKeyStrokeList(final String shortcut) {
    if (keyStrokeHash.containsKey(shortcut)) {
      return keyStrokeHash.get(shortcut);
    } else {
      return null;
    }
  }

  public static void registerAction(final String shortcut,
      final JComponent component, final int condition,
      final AbstractAction action) {

    java.util.List<KeyStroke> ksList = MainWin.keyMap
        .getKeyStrokeList(shortcut);

    if (ksList != null) {
      for (final KeyStroke ks : ksList) {
        component.getInputMap(condition).put(ks, shortcut);
        component.getActionMap().put(shortcut, action);
      }
    } else {
      LOGGER.severe("Shortcut : " + shortcut + " is unkown");
    }
  }

  public static boolean isShortcut(final KeyStroke stroke, final String shortcut) {
    boolean ret = false;

    java.util.List<KeyStroke> ksList = MainWin.keyMap
        .getKeyStrokeList(shortcut);

    if (ksList != null) {
      for (final KeyStroke ks : ksList) {
        if (ks.equals(stroke)) {
          ret = true;
          break;
        }
      }
    }

    return ret;
  }

}
