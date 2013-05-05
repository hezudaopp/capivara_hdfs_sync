
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
 * $Id: SyncTreeExplorer.java,v 1.18 2006/08/09 22:18:39 hunold Exp $
 */

package net.sf.jfilesync.sync2.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sf.jfilesync.engine.ConnectionDetails;
import net.sf.jfilesync.gui.components.ConnectionDetailsComponent2;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.sync2.SyncTree;
import net.sf.jfilesync.sync2.diffs.SyncDiffTree;


public class SyncTreeExplorer extends JPanel implements SyncTreeViewListener {
  
  private static final long serialVersionUID = 1L;
  private final SyncTree fileTree;
  private final SyncDiffTree diffTree;
  private SyncTreeViewer fileTreeViewer;
  private final JTextField pathBox = new JTextField(30);
  private final JPanel toolPane = new JPanel();
  private static final int TOP_PANEL_HEIGHT = 28;

  public SyncTreeExplorer(final SyncTree fileTree, final SyncDiffTree diffTree,
      final ConnectionDetails conDetails) {
    if (fileTree == null) {
      throw new IllegalArgumentException("fileTree is null");
    }
    if (diffTree == null) {
      throw new IllegalArgumentException("diffTree is null");
    }
    if (conDetails == null) {
      throw new IllegalArgumentException("conDetails is null");
    }

    this.fileTree = fileTree;
    this.diffTree = diffTree;

    fileTreeViewer = new SyncTreeViewer(this.fileTree, this.diffTree);
    fileTreeViewer.addSyncTreeViewListener(this);
    fileTreeViewer.showRootNode();

    pathBox.setEditable(false);
    pathBox.setHorizontalAlignment(JTextField.LEADING);
    JLabel lab1 = new JLabel(LanguageBundle.getInstance().getMessage(
        "component.path.label")
        + ": ");
    //Font toolFont = new Font("Helvetica", Font.BOLD, 10);
    //lab1.setFont(toolFont);
    //pathBox.setFont(toolFont);

    toolPane.setPreferredSize(new Dimension(400, TOP_PANEL_HEIGHT));
    toolPane.setLayout(new BoxLayout(toolPane, BoxLayout.X_AXIS));
    toolPane.setBorder(BorderFactory.createEtchedBorder());
    toolPane.add(lab1);
    toolPane.add(pathBox);

    ConnectionDetailsComponent2 cdc = new ConnectionDetailsComponent2(conDetails);

    JPanel topPane = new JPanel();
    topPane.setLayout(new BoxLayout(topPane, BoxLayout.Y_AXIS));

    topPane.add(cdc);
    topPane.add(toolPane);

    setLayout(new BorderLayout());
    add(topPane, BorderLayout.NORTH);
    add(fileTreeViewer, BorderLayout.CENTER);
    setPreferredSize(new Dimension(new Dimension(390, 475)));
  }

  public void syncTreeViewPathChanged(final SyncTreeViewEvent e) {
    pathBox.setText(e.getPath());
  }

  public void showOnlyDifferences(final boolean showOnlyDiff) {
    if( fileTreeViewer != null && 
        fileTreeViewer.getShowOnlyDifferences() != showOnlyDiff ) {      
      fileTreeViewer.setShowOnlyDifferences(showOnlyDiff);
      fileTreeViewer.rebuildTree();
    }
  }
  
  public void showExcludedFiles(final boolean showExcluded) {
    if( fileTreeViewer != null && 
        fileTreeViewer.getShowExcludedFiles() != showExcluded ) {
      fileTreeViewer.setShowExcludedFiles(showExcluded);
      fileTreeViewer.rebuildTree();
    }
  }

  public synchronized void addSyncDiffAttributeListener(
      final SyncDiffAttributeListener listener) {
    if (fileTreeViewer != null && listener != null) {
      fileTreeViewer.addSyncDiffAttributeListener(listener);
    }
  }
  
}
