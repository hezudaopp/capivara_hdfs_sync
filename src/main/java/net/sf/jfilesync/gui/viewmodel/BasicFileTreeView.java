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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sf.jfilesync.engine.BasicFile;
import net.sf.jfilesync.engine.BasicFileTree;

public class BasicFileTreeView extends JTree implements TreeSelectionListener {

  private static final long serialVersionUID = 1L;

  private final List<BasicFileTreeSelectionListener> selectionListeners = new ArrayList<BasicFileTreeSelectionListener>();

  private final BasicFileTreeMouseListener treeMouseListener = new BasicFileTreeMouseListener(null);

  public BasicFileTreeView(final BasicFileTree fileTree, final BasicFileTreeRenderer renderer) {
    DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(fileTree
        .getRoot());
    buildTree(rootNode, fileTree.getRoot());
    setModel(new DefaultTreeModel(rootNode));
    setCellRenderer(renderer);
    addTreeSelectionListener(this);
    addMouseListener(treeMouseListener);
  }

  protected void buildTree(final DefaultMutableTreeNode node, final BasicFile file) {
    final Iterator<BasicFile> it = file.getChildren().iterator();
    while( it.hasNext() ) {
      final BasicFile uf = it.next();
      if( uf.isDirectory() ) {
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(uf);
        node.add(childNode);
        buildTree(childNode, uf);
      }
    }
  }

  public void setAbstractBasicFileTreePopup(AbstractBasicFilePopup popup) {
    treeMouseListener.setAbstractBasicFileTreePopup(popup);
  }

  public void addBasicFileTreeSelectionListener(final BasicFileTreeSelectionListener listener) {
    if( listener == null ) {
      throw new IllegalArgumentException("listener must not be null");
    }
    selectionListeners.add(listener);
  }

  public void fireBasicFileTreeSelectionEvent(BasicFileTreeSelectionEvent event) {
    Iterator<BasicFileTreeSelectionListener> it = selectionListeners.iterator();
    while(it.hasNext()) {
      it.next().valueSelected(event);
    }
  }

  public void valueChanged(TreeSelectionEvent e) {
    final DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();
    if (node == null) {
      return;
    }
    final BasicFile selectedDir = (BasicFile) node.getUserObject();
    fireBasicFileTreeSelectionEvent(new BasicFileTreeSelectionEvent(this, selectedDir));
  }

  public BasicFile getSelectedBasicFile() {
    BasicFile selectedFile = null;
    final DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();
    if( node != null ) {
      selectedFile = (BasicFile) node.getUserObject();
    }
    return selectedFile;
  }

  class BasicFileTreeMouseListener extends MouseAdapter {

    private AbstractBasicFilePopup popup;

    public BasicFileTreeMouseListener(AbstractBasicFilePopup popup) {
      this.popup = popup;
    }

    public void setAbstractBasicFileTreePopup(AbstractBasicFilePopup popup) {
      this.popup = popup;
    }

    public void mousePressed(MouseEvent e) {

      int row = BasicFileTreeView.this.getRowForLocation(e.getX(),e.getY());
      BasicFileTreeView.this.setSelectionRow(row);

      popup.setBasicFileContext(BasicFileTreeView.this.getSelectedBasicFile());

      if (e.isPopupTrigger()) {
        showPopup(e);
      }
    }

    public void mouseReleased(MouseEvent e) {
      if (e.isPopupTrigger()) {
        showPopup(e);
      }
    }

    private void showPopup(MouseEvent e) {
      if (popup != null) {
        popup.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  }

}
