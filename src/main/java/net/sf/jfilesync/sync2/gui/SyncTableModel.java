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
 * $Id: SyncTableModel.java,v 1.6 2006/08/09 22:18:39 hunold Exp $
 */

package net.sf.jfilesync.sync2.gui;

import javax.swing.table.*;

import net.sf.jfilesync.gui.viewmodel.*;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.sync2.*;
import net.sf.jfilesync.sync2.syncer.ISyncFileSorter;


public class SyncTableModel extends AbstractTableModel {

  private static final long serialVersionUID = 1L;

  private SyncFile[] files;

  private TFileDataPresenter presenter;
  private final ISyncFileSorter sorter;

  public SyncTableModel(SyncFile[] files) {
    // make directories to appear first
    SyncFileSorter tableModelSorter = new SyncFileSorter();
    tableModelSorter.enableDirectoriesFirst(true);
    sorter = tableModelSorter;
    
    this.files = sort(files);
    presenter = new TFileDataPresenter();
  }

  public int getRowCount() {
    return files.length;
  }

  public int getColumnCount() {
    return 3;
  }

  public void setTableData(SyncFile[] files) {
    this.files = sort(files);
  }

  private SyncFile[] sort(SyncFile[] sfiles) {
    return sorter.sortByName(sfiles);
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    switch (columnIndex) {
    case 0:
      // name
      return files[rowIndex];
    case 1:
      // size
      return presenter.getSize(files[rowIndex].getFile());
    case 2:
      // mod time
      return presenter.getModTime(files[rowIndex].getFile());
    default:
      return "NULL";
    }
  }

  public String getColumnName(int column) {
    switch (column) {

    case 0:
      return LanguageBundle.getInstance()
          .getMessage("component.filename.label");
    case 1:
      return LanguageBundle.getInstance()
          .getMessage("component.filesize.label");
    case 2:
      return LanguageBundle.getInstance().getMessage("component.filemod.label");
    default:
      return "";
    }
  }

}
