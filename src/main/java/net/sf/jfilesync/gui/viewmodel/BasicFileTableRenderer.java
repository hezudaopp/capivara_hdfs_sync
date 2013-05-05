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

import java.awt.Color;
import java.awt.Component;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import net.sf.jfilesync.engine.BasicFile;
import net.sf.jfilesync.gui.viewmodel.BasicFileTableColumModel.COLUMN_ID;
import net.sf.jfilesync.settings.TStyleChooser;

public class BasicFileTableRenderer extends DefaultTableCellRenderer {

  private static final long serialVersionUID = 1L;

  private final static Logger LOGGER = Logger
      .getLogger(BasicFileTreeRenderer.class.getName());

  private final BasicFileTableColumModel columnModel;
  private final BasicFilePresenter presenter = new BasicFilePresenter();

  public BasicFileTableRenderer(BasicFileTableColumModel columnModel) {
    this.columnModel = columnModel;
  }

  public BasicFileTableColumModel getBasicFileTableColumModel() {
    return columnModel;
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {

    if( !(value instanceof BasicFile) ) {
      LOGGER.severe("value must be of type BasicFile");
      throw new IllegalArgumentException("value must be of type BasicFile");
    }

    Component renderedComponent = null;

    if( columnModel.getColumnIdx(COLUMN_ID.COLUMN_NAME) == column ) {
      renderedComponent = getRenderedFileName((BasicFile)value, isSelected);
    } else {
      renderedComponent = getRenderedFileAttribute((BasicFile)value, isSelected, column);
    }

    return renderedComponent;
  }

  public Component getRenderedFileName(BasicFile file, boolean isSelected) {
    final JLabel label = new JLabel(file.getFileName());
    label.setOpaque(true);
    if( file.isDirectory() ) {
      label.setIcon(TStyleChooser.getStyle().getFolderImageIcon());
    }
    if( isSelected ) {
      label.setBackground(Color.DARK_GRAY);
      label.setForeground(Color.WHITE);
    }
    return label;
  }

  public Component getRenderedFileAttribute(BasicFile file, boolean isSelected, int column) {
    String attr = "unkown";
    if( columnModel.getColumnIdx(COLUMN_ID.COLUMN_SIZE) == column ) {
      attr = presenter.getSize(file);
    } else if( columnModel.getColumnIdx(COLUMN_ID.COLUMN_TIME) == column ) {
      attr = presenter.getModTime(file);
    }
    final JLabel label = new JLabel(attr);
    label.setOpaque(true);
    label.setFocusable(false);
    if( isSelected ) {
      label.setBackground(Color.DARK_GRAY);
      label.setForeground(Color.WHITE);
    }
    return label;
  }
}