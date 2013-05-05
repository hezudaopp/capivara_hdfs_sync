/*
 * capivara - Java File Synchronization
 *
 * Created on 30-Oct-2005
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
 * $Id$
 */
package net.sf.jfilesync.sync2.list;

import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import net.sf.jfilesync.GlobalDataStore;
import net.sf.jfilesync.prop.LanguageBundle;

public class ExpressionListDialog extends JDialog 
implements ActionListener {

  private ExpressionMainModel model;
  private ExpressionMainPanel view;
  
  private static final String ACTION_APPLY   = "apply";
  private static final String ACTION_DISMISS = "dismiss";
  
  public static final int OPTION_APPLY   = 0;
  public static final int OPTION_DISMISS = 1;
  private int userOpt = OPTION_DISMISS;
  
  public ExpressionListDialog(Dialog owner, NExpListList list) {
    super(owner, LanguageBundle.getInstance().getMessage("filters.title"),
        true);
    model = new ExpressionMainModel(list);
    initModel();
    initUI();
  }

  public ExpressionListDialog(Frame owner, NExpListList list) {
    super(owner, LanguageBundle.getInstance().getMessage("filters.title"),
        true);
    model = new ExpressionMainModel(list);
    initModel();
    initUI();
  }

  private void initModel() {
    ExpressionList globalIL = GlobalDataStore.getInstance().getIncludeList();
    if (globalIL != null) {
      model.setIncludeList(globalIL);
    }

    ExpressionList globalEL = GlobalDataStore.getInstance().getExcludeList();
    if (globalEL != null) {
      model.setExcludeList(globalEL);
    }
  }

  private void initUI() {

    view = new ExpressionMainPanel(this, model);
    new ExpressionMainController(model, view);

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

    JButton applyButton = new JButton(LanguageBundle.getInstance()
        .getMessage("label.apply"));
    applyButton.setActionCommand(ACTION_APPLY);
    applyButton.addActionListener(this);

    JButton dismissButton = new JButton(LanguageBundle.getInstance()
        .getMessage("label.dontapply"));
    dismissButton.setActionCommand(ACTION_DISMISS);
    dismissButton.addActionListener(this);

    buttonPanel.add(applyButton);
    buttonPanel.add(dismissButton);

    mainPanel.add(view);
    mainPanel.add(buttonPanel);

    setContentPane(mainPanel);
    pack();
    setLocationRelativeTo(getParent());
  }

  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();

    if (cmd.equals(ACTION_APPLY)) {
      userOpt = OPTION_APPLY;
      storeExpressionLists();
      setVisible(false);
    } else if (cmd.equals(ACTION_DISMISS)) {
      userOpt = OPTION_DISMISS;
      setVisible(false);
    }
  }

  public int getUserOption() {
    return userOpt;
  }

  private void storeExpressionLists() {
    ExpressionList il = model.getIncludeList();
    if (il != null) {
      GlobalDataStore.getInstance().setIncludeList(il);
    }
    ExpressionList el = model.getExcludeList();
    if (el != null) {
      GlobalDataStore.getInstance().setExcludeList(el);
    }
  }

  public ExpressionList getIncludeExpressionList() {
    return model.getIncludeList();
  }

  public ExpressionList getExcludeExpressionList() {
    return model.getExcludeList();
  }

  //  private void saveLists() {
  //    int opt = JOptionPane.showConfirmDialog(getOwner(),
  //        LanguageBundle.getInstance().getMessage("filter.save_list"), 
  //        LanguageBundle.getInstance().getMessage("label.save"), 
  //        JOptionPane.YES_NO_OPTION);
  //    
  //    if( opt == JOptionPane.YES_OPTION ) {
  //      NExpListList lists = model.getNExpListList();
  //      NamedListConfigHandler handler = NamedListConfigHandler.getInstance();
  //      try {
  //        handler.saveLists(lists);
  //      }
  //      catch(IOException e) {
  //        TErrorHandling.reportError(TErrorHandling.ERROR_IO_GENERAL,
  //            e.getMessage());
  //      }
  //    }
  //  }

}
