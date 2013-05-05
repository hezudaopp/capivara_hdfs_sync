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
 * $Id: SyncTreeTable.java,v 1.8 2006/06/09 20:58:45 hunold Exp $
 */

package net.sf.jfilesync.sync2.gui;

import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.table.*;

import net.sf.jfilesync.gui.viewmodel.*;
import net.sf.jfilesync.settings.*;

public class SyncTreeTable extends JTable {
  private static final long serialVersionUID = 487097202386139489L;

  static Logger log = Logger.getLogger(SyncTreeTable.class.getPackage()
      .getName());

  public SyncTreeTable(SyncTableModel syncDataModel,
      DefaultTableCellRenderer nameCellRenderer) {
    super(syncDataModel);

    getTableHeader().setReorderingAllowed(false);

    // tooltips off -> faster rendering
    // ok now with tooltips
    // ToolTipManager.sharedInstance().unregisterComponent(this);

    setRowSelectionAllowed(true);
    setColumnSelectionAllowed(false);
    setCellSelectionEnabled(true);

    int tableCols = getColumnModel().getColumnCount();
    TableColumn col = getColumnModel().getColumn(0);
    col.setCellRenderer(nameCellRenderer);
    for (int i = 1; i < tableCols; i++) {
      getColumnModel().getColumn(i).setCellRenderer(
          new FileAttributeCellRenderer(TStyleChooser.getStyle()));
    }

  }

}
