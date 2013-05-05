/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2006 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
package net.sf.jfilesync.service.unify;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import net.sf.jfilesync.gui.viewmodel.BasicFileModel;
import net.sf.jfilesync.gui.viewmodel.BasicFilePopupListener;
import net.sf.jfilesync.gui.viewmodel.BasicFileTable;
import net.sf.jfilesync.gui.viewmodel.BasicFileTableController;
import net.sf.jfilesync.gui.viewmodel.BasicFileTreeView;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.service.unify.action.UnifyOption;

public class DupExplorer extends AbstractDupDialog implements ActionListener {

  private final UnifyFileTree fileTree;
//  private final List<DupExplorerListener> listeners = new ArrayList<DupExplorerListener>();

  private final UnifyFileTreePopup  treePopupMenu = new UnifyFileTreePopup();
  private final UnifyFileTablePopup tablePopupMenu = new UnifyFileTablePopup();

  private final BasicFileTreeView treeView;
  private final BasicFileTable fileTable;

  private final JComboBox actionCombo = new JComboBox();
  //private final UnifyOptions unifyOptions = new UnifyOptions();

  private final static Logger LOGGER = Logger.getLogger(DupExplorer.class.getName());

  public DupExplorer(JFrame owner, UnifyFileTree fileTree) {
    super(owner, "DupExplorer");
    setModal(true);
    this.fileTree = fileTree;

    treeView = new BasicFileTreeView(this.fileTree, new UnifyFileTreeRenderer());
    treeView.setAbstractBasicFileTreePopup(treePopupMenu);

    final BasicFileModel fileTableModel = new BasicFileModel(fileTree.getRoot().getChildren());

    fileTable = new BasicFileTable(fileTableModel,
        new UnifyFileTableRenderer(fileTableModel.getBasicFileTableColumModel()));
    fileTable.setAbstractBasicFileTreePopup(tablePopupMenu);

    final BasicFileTableController tableController = new BasicFileTableController(fileTable);
    treeView.addBasicFileTreeSelectionListener(tableController);

    initUI();
  }

  public void addUnifyOption(UnifyOption option) {
    //unifyOptions.addUnifyOption(option);
    actionCombo.addItem(option);
  }

  protected void initUI() {

    final JSplitPane splitPane = new JSplitPane();
    splitPane.setLeftComponent(new JScrollPane(treeView));
    splitPane.setRightComponent(new JScrollPane(fileTable));
    splitPane.setPreferredSize(new Dimension(400, 400));
    splitPane.setBorder(BorderFactory.createEmptyBorder());
    splitPane.setResizeWeight(0.3);
    // set border to null, this is important
    // see http://java.sun.com/docs/books/tutorial/uiswing/\
    //     components/problems.html#nestedborders
    splitPane.setBorder(null);

    add(splitPane, BorderLayout.CENTER);
    add(createActionPanel(), BorderLayout.SOUTH);
    setPreferredSize(new Dimension(640,480));
    pack();
    setLocationRelativeTo(getOwner());
  }

  public void updateUI() {
    treeView.updateUI();
    fileTable.updateUI();
  }

  private JPanel createActionPanel() {
    final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    final JButton previewButton = new JButton(LanguageBundle.getInstance()
        .getMessage("label.preview"));
    previewButton.setActionCommand("preview");
    previewButton.addActionListener(this);

//    for(UnifyOption option : unifyOptions.getOptions()) {
//      actionCombo.addItem(option);
//    }

//    actionCombo.addItem(LanguageBundle.getInstance().getMessage(
//        "unify.option.delete"));
//    actionCombo.addItem(LanguageBundle.getInstance().getMessage(
//        "unify.option.move"));

    final JButton cancelButton = new JButton(LanguageBundle.getInstance().getMessage(
        "label.cancel"));
    cancelButton.setActionCommand("cancel");
    cancelButton.addActionListener(this);

    panel.add(previewButton);
    panel.add(actionCombo);
    panel.add(cancelButton);

    return panel;
  }

  public void addBasicFileTreePopupListener(BasicFilePopupListener listener) {
    treePopupMenu.addBasicFileTreePopupListener(listener);
  }

  public void addBasicFileTablePopupListener(BasicFilePopupListener listener) {
    tablePopupMenu.addBasicFileTreePopupListener(listener);
  }

//  public void addDupExlorerListener(DupExplorerListener listener) {
//    listeners.add(listener);
//  }
//
//  public void fireDupExplorerEvent(DupExplorerEvent e) {
//    if( e == null ) {
//      throw new IllegalArgumentException("e must not be null");
//    }
//    for (DupExplorerListener listener : listeners ) {
//      listener.performDupExplorerEvent(e);
//    }
//  }

  public void actionPerformed(ActionEvent e) {
    final String command = e.getActionCommand();

    if (command.equals("cancel")) {
      fireDupExplorerEvent(new DupExplorerEvent(this, "cancel"));
    } else if (command.equals("preview")) {
      fireDupExplorerEvent(new DupExplorerEvent(this, "preview"));
    } else {
      LOGGER.warning("unknown command: " + command);
    }
  }

  public List<UnifyFile> getSelectedFiles() {
    return fileTable.getSelectedFiles();
  }

  public UnifyOption getSelectedOption() {
    UnifyOption option = null;
    Object selectedItem = actionCombo.getSelectedItem();
    if(  selectedItem instanceof UnifyOption ) {
      option = (UnifyOption)selectedItem;
    } else {
      LOGGER.warning("BUG: selectedItem must be a UnifyOption");
    }
    return option;
  }

}
