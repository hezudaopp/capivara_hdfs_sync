/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2004 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TableFileNameRenderer.java,v 1.5 2005/08/19 21:29:02 hunold Exp $
 */
package net.sf.jfilesync.gui.viewmodel;

import java.awt.Color;
import java.awt.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import net.sf.jfilesync.settings.TStyleChooser;
import net.sf.jfilesync.settings.TStyleInterface;

/**
 *  Renderer for SyncTreeViewer
 */

public class TableFileNameRenderer extends DefaultTableCellRenderer {

  private static final long serialVersionUID = 2859248707724932012L;
  private TStyleInterface style = TStyleChooser.getStyle();
  private FileNameRenderer renderer;

  public TableFileNameRenderer(FileNameRenderer renderer) {
    this.renderer = renderer;
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {

    JComponent fileLabel = renderer.getRenderedComponent(value);
    boolean enable = (table == null || table.isEnabled());
    fileLabel.setEnabled(enable);

    fileLabel.setOpaque(true);

    fileLabel.setFont(style.getTableFont());

    Color bg = (row % 2 == 0) ? style.getTableFileNameEven() : style
        .getTableFileNameOdd();
    fileLabel.setBackground(bg);

    if (isSelected) {
      fileLabel.setForeground(style.getTableSelectionForegroundColor());
      fileLabel.setBackground(style.getTableSelectionBackGroundColor());
    } else if (hasFocus) {
      setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
      if (table.isCellEditable(row, column)) {
        setForeground(UIManager.getColor("Table.focusCellForeground"));
        setBackground(UIManager.getColor("Table.focusCellBackground"));
      }
    } else {
      setBorder(DefaultTableCellRenderer.noFocusBorder);
    }

    return fileLabel;
  }

}
