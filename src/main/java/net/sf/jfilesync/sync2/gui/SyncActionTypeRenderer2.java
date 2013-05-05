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

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import net.sf.jfilesync.sync2.SyncAction;


public class SyncActionTypeRenderer2 extends DefaultTableCellRenderer {

  private static final long serialVersionUID = 3760559780632408881L;
  
  public final static int LEFT_ACTION_RENDERER = 0;
  public final static int RIGHT_ACTION_RENDERER = 1;
  
  private final int side;
  
  public SyncActionTypeRenderer2(int side) {
    this.side = side;
  }
  
  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {

    Component comp;

    if (value instanceof SyncAction) {
      SyncAction action = (SyncAction) value;
      if( side == LEFT_ACTION_RENDERER ) {
        comp = action.renderLeftAction();
      } else {
        comp = action.renderRightAction();
      }
    } else {
      comp = new JLabel("");
    }
    
    return comp;
  }
  
  
}
