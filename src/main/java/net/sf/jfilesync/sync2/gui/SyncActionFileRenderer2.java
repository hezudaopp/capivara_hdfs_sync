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
 * $Id$
 */
package net.sf.jfilesync.sync2.gui;

import java.awt.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import net.sf.jfilesync.sync2.SyncAction;

public class SyncActionFileRenderer2 extends DefaultTableCellRenderer {

  private static final long serialVersionUID = 3690193261230764080L;
  
  public SyncActionFileRenderer2() {
  }
  
  public Component getTableCellRendererComponent(JTable table,
      Object value, boolean isSelected, boolean hasFocus,
      int row, int column) {
    
    JLabel label = null;

    if (value instanceof SyncAction) {
      SyncAction action = (SyncAction) value;
      String fileName = action.getSyncFile().getRelativePath();
      label = new JLabel(fileName);
      action.renderFileName(label);
    } else {
      label = new JLabel("");
    }
    
    return label;
  }
  
}
