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
 * $Id: SyncFileNameRenderer.java,v 1.11 2006/08/09 22:18:39 hunold Exp $
 */

package net.sf.jfilesync.sync2.gui;

import javax.swing.JComponent;
import javax.swing.JLabel;

import net.sf.jfilesync.gui.viewmodel.FileNameRenderer;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.settings.TStyleChooser;
import net.sf.jfilesync.settings.TStyleInterface;
import net.sf.jfilesync.sync2.SyncDiff;
import net.sf.jfilesync.sync2.SyncDiffCollection;
import net.sf.jfilesync.sync2.SyncFile;
import net.sf.jfilesync.sync2.diffs.SyncDiffTree;

public class SyncFileNameRenderer implements FileNameRenderer {

  private static final long serialVersionUID = 7575976372029618495L;

  private TStyleInterface style = TStyleChooser.getStyle();
  private final SyncDiffTree diffTree;
  private final SyncDiffCollection diffCol;

  //  private final static Logger LOGGER = Logger
  //      .getLogger(SyncFileNameRenderer.class.getName());

  public SyncFileNameRenderer(final SyncDiffTree syncDiffTree) {
    this.diffTree = syncDiffTree;
    diffCol = diffTree.getSyncDiffCollection();
  }

  public JComponent getRenderedComponent(Object value) {
    JLabel label;
    SyncFile file = (SyncFile) value;

    if (file.getFile().isDirectory()) {
      label = new JLabel(file.getFile().getFileName(), style
          .getFolderImageIcon(), JLabel.LEFT);
    } else {
      label = new JLabel(file.getFile().getFileName());
    }

    if (diffCol.contains(file)) {
      SyncDiff diff = diffCol.getSyncDiff(file);
      label.setForeground(diff.getForeground());
      label.setBackground(diff.getBackground());
      label.setToolTipText(diff.getDescription());
    } else if (file.isExcluded()) {
      label.setForeground(SyncColorSet.getColor(SyncColorSet.COLOR_EXCLUDED));
      label.setToolTipText(LanguageBundle.getInstance().getMessage(
          "file.diff.excluded"));
    }

    return label;
  }

}
