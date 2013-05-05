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
 * $Id: TNameCellRenderer.java,v 1.6 2005/12/30 21:59:06 hunold Exp $
 */

package net.sf.jfilesync.gui.viewmodel;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import net.sf.jfilesync.engine.*;
import net.sf.jfilesync.settings.*;


/*
 * - class is responsible for rendering the file names
 *   in a JTable
 * - it uses JLabels as its Component
 */

public class TNameCellRenderer extends DefaultTableCellRenderer {

  private static final long serialVersionUID = -9188426841433592523L;
  private TStyleInterface style;

  public TNameCellRenderer(TStyleInterface style) {
    this.style = style;
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {

    JLabel fileLabel = null;

    boolean syncSelected = false;

    if (row == 0 && column == 0) {
      // this is for ".."
      fileLabel = new JLabel((String) value);
    } else if( value == null ) {
      // this is a hack as it happens on Mac OS X that I see value=null for some reason
      fileLabel = new JLabel("NULL");
    } else {
      
      final TFileProperties fileProperties = (TFileProperties) value;
      String fname = fileProperties.getFileName();

      if (fileProperties.isLink()) {
        fname = ConfigDefinitions.LINK_PREFIX + fname;
      }

      if (fileProperties.isDirectory()) {
        fileLabel = new JLabel(fname, style.getFolderImageIcon(), JLabel.LEFT);
      } else {
        fileLabel = new JLabel(fname);
      }

      syncSelected = fileProperties.isSelectedForSync();
    }
    
//    System.out.println("label set up for : " + fileLabel.getText());

//    boolean enable = (table == null || table.isEnabled());
//    fileLabel.setEnabled(enable);

    setLabelProperties(fileLabel, row);

    if (isSelected || table.isRowSelected(row) ) {
      fileLabel.setForeground(style.getTableSelectionForegroundColor());
      fileLabel.setBackground(style.getTableSelectionBackGroundColor());
    } 
//    else if (hasFocus) {
////      fileLabel
////          .setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
//        fileLabel.setBorder(DefaultTableCellRenderer.noFocusBorder);
//    } else {
//        fileLabel.setBorder(DefaultTableCellRenderer.noFocusBorder);
//    }

    if( syncSelected ) {
        fileLabel.setForeground(Color.RED);
    }

    return fileLabel;
  }

  private void setLabelProperties(JLabel label, int row) {
    label.setOpaque(true);
    label.setFont(style.getTableFont());
    if (row % 2 == 0) {
      label.setBackground(style.getTableFileNameEven());
    } else {
      label.setBackground(style.getTableFileNameOdd());
    }
  }

}
