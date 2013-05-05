/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003-2007 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id$
 */
package net.sf.jfilesync.gui.viewmodel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import net.sf.jfilesync.prop.LanguageBundle;

public class BasicFileTableColumModel {

  public enum COLUMN_ID {
    COLUMN_NAME, COLUMN_SIZE, COLUMN_TIME
  };

  private final static HashMap<COLUMN_ID, Integer> name2idx = new HashMap<COLUMN_ID, Integer>();
  static {
    name2idx.put(COLUMN_ID.COLUMN_NAME, 0);
    name2idx.put(COLUMN_ID.COLUMN_SIZE, 1);
    name2idx.put(COLUMN_ID.COLUMN_TIME, 2);
  }

  private final static HashMap<COLUMN_ID, Integer> name2width = new HashMap<COLUMN_ID, Integer>();
  static {
    name2width.put(COLUMN_ID.COLUMN_NAME, 150);
    name2width.put(COLUMN_ID.COLUMN_SIZE, 20);
    name2width.put(COLUMN_ID.COLUMN_TIME, 50);
  }


  public BasicFileTableColumModel() {

  }

  public int getColumnCount() {
    return name2idx.keySet().size();
  }

  public int getColumnIdx(final COLUMN_ID columnId) {
    return name2idx.get(columnId);
  }

  public String getColumnName(final int columnIdx) {
    String colName = "";
    if (columnIdx == name2idx.get(COLUMN_ID.COLUMN_NAME)) {
      colName = LanguageBundle.getInstance().getMessage(
          "component.filename.label");
    } else if (columnIdx == name2idx.get(COLUMN_ID.COLUMN_SIZE)) {
      colName = LanguageBundle.getInstance().getMessage(
          "component.filesize.label");
    } else if (columnIdx == name2idx.get(COLUMN_ID.COLUMN_TIME)) {
      colName = LanguageBundle.getInstance().getMessage(
          "component.filemod.label");
    }
    return colName;
  }

  private COLUMN_ID getColumnId(int columnIdx) {
    COLUMN_ID id = null;

    Set<Entry<COLUMN_ID,Integer>> entrySet = name2idx.entrySet();
    Iterator<Entry<COLUMN_ID,Integer>> it = entrySet.iterator();
    while( it.hasNext() ) {
      Entry<COLUMN_ID,Integer> entry = it.next();
      if( entry.getValue() == columnIdx ) {
        id = entry.getKey();
        break;
      }
    }
    return id;
  }

  public int getColumnWidth(int columnIdx) {
    int width = 20;
    final COLUMN_ID columnID = getColumnId(columnIdx);
    if( columnID != null ) {
      width = name2width.get(columnID);
    }
    return width;
  }

}
