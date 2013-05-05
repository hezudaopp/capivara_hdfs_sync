/*
 * capivara - Java File Synchronization
 *
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
 * $Id: SyncColorSet.java,v 1.7 2006/02/05 22:46:15 hunold Exp $
 */
package net.sf.jfilesync.sync2.gui;

import java.awt.Color;
import java.util.HashMap;

public class SyncColorSet {

  public final static int
      COLOR_ACTION_DELETE    = 0,
      COLOR_ACTION_COPY      = 1,
      COLOR_DIFF_SUBDIR      = 10,
      COLOR_DIFF_TIME_NEWER  = 11,
      COLOR_DIFF_TIME_OLDER  = 12,
      COLOR_DIFF_TYPE        = 20,
      COLOR_DIFF_UNIQUE      = 21,
      COLOR_EXCLUDED         = 30;

  private final static HashMap<Integer, Color> colorMap = new HashMap<Integer, Color>();

  static {
    addColor(COLOR_ACTION_DELETE, Color.RED);
    addColor(COLOR_ACTION_COPY, 0x3300CC);
    addColor(COLOR_DIFF_SUBDIR, 0x1131f8);
    addColor(COLOR_DIFF_TIME_NEWER, 0xbe1300);
    addColor(COLOR_DIFF_TIME_OLDER, 0xff5c4a);
    addColor(COLOR_DIFF_TYPE, Color.MAGENTA);
    addColor(COLOR_DIFF_UNIQUE, 0xff0000);
    addColor(COLOR_EXCLUDED, Color.GRAY);
  }

  public static Color getColor(int colorID) {
    if( colorMap.containsKey(new Integer(colorID)) ) {
      return (Color)colorMap.get(new Integer(colorID));
    }
    else {
      return Color.black;
    }
  }

  protected static void addColor(int key, Color col) {
    Integer ik = new Integer(key);
    colorMap.put(ik, col);
  }

  protected static void addColor(int key, int rgb) {
    addColor(key, new Color(rgb));
  }

}
