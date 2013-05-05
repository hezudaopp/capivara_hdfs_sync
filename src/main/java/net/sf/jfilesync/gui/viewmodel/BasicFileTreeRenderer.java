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

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.sf.jfilesync.engine.BasicFile;
import net.sf.jfilesync.settings.TStyleChooser;

public abstract class BasicFileTreeRenderer extends DefaultTreeCellRenderer {

  private final ImageIcon folderIcon = TStyleChooser.getStyle().getFolderImageIcon();

  public BasicFileTreeRenderer() {
  }

  public Component getTreeCellRendererComponent(JTree tree, Object value,
      boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

    final BasicFile file = (BasicFile) ((DefaultMutableTreeNode) value)
        .getUserObject();

    JLabel fileLabel = null;

    final String fileName = file.getFileName();

    if (file.isDirectory()) {
      if (folderIcon != null) {
        fileLabel = new JLabel(fileName, folderIcon, JLabel.LEFT);
      } else {
        fileLabel = new JLabel("+" + fileName);
      }
    } else {
      fileLabel = new JLabel(fileName);
    }

    fileLabel.setOpaque(true);

    if (sel) {
      fileLabel.setBackground(Color.BLUE);
      fileLabel.setForeground(Color.WHITE);
    } else {
      fileLabel.setBackground(Color.WHITE);
      fileLabel.setForeground(Color.BLACK);
    }

    doRenderJLabel(file, fileLabel, selected);

    return fileLabel;
  }

  public abstract void doRenderJLabel(final BasicFile file, final JLabel label,
      final boolean selected);

}
