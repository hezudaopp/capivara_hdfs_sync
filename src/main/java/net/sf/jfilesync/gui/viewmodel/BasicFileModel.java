/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2007 Sascha Hunold <hunoldinho@users.sourceforge.net>
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

import java.util.List;

import javax.swing.table.DefaultTableModel;

import net.sf.jfilesync.engine.BasicFile;

public class BasicFileModel extends DefaultTableModel {
  private static final long serialVersionUID = 1L;

  private List<BasicFile> files;

//  private final BasicFilePresenter presenter = new BasicFilePresenter();
  private final BasicFileSorter sorter = new BasicFileSorter();
  private final BasicFileTableColumModel columnModel;

  public BasicFileModel(final List<BasicFile> files) {
    columnModel = new BasicFileTableColumModel();
    setTableData(files);
  }

  public BasicFileModel(final List<BasicFile> files, BasicFileTableColumModel columnModel) {
    this.columnModel = columnModel;
    setTableData(files);
  }

  public BasicFileTableColumModel getBasicFileTableColumModel() {
    return columnModel;
  }

  public int getRowCount() {
    int rowCount = 0;
    if( files == null ) {
      rowCount = 0;
    } else {
      rowCount = files.size();
    }
    return rowCount;
  }

  public int getColumnCount() {
    return columnModel.getColumnCount();
  }

  private void setTableData(final List<BasicFile> files) {
    if( files == null ) {
      throw new IllegalArgumentException("files must not be null");
    }
    this.files = files;
    sorter.sortByName(files);
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    return files.get(rowIndex);
  }

  public String getColumnName(int column) {
    return columnModel.getColumnName(column);
  }

  public boolean isCellEditable(int row, int column) {
    return false;
  }

}
