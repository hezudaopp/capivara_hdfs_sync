/*
 * capivara - Java File Synchronization
 *
 * Created on 11-Dec-2005
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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import net.sf.jfilesync.gui.icons.TImageIconProvider;
import net.sf.jfilesync.prop.LanguageBundle;

public class ExpressionListEditPanel extends JPanel {

  private ExpressionListComponent listComp = null;
  private final JDialog owner; 
  private ButtonPanel buttonPanel = new ButtonPanel();
  
  public final static String ACTION_ADD   = "add";
  public final static String ACTION_EDIT  = "edit";
  public final static String ACTION_DEL   = "del";
  public final static String ACTION_CLEAR = "clear";
  
  public ExpressionListEditPanel(JDialog owner) {
    this.owner = owner;
    listComp = new ExpressionListComponent();
    initUI();
  }
  
  public synchronized void addActionListener(ActionListener l) {
    if( l != null ) {
      buttonPanel.addActionListener(l);
    }
  }
  
  public void setEnabled(final boolean enabled) {
    buttonPanel.setEnabled(enabled);
  }
  
  public JDialog getOwner() {
    return owner;
  }
    
  public void setExpressionList(ExpressionList list) {
    listComp.setExpressionList(list);
  }
  
  public Expression getSelectedExpression() {
    return listComp.getSelectedExpression();
  }
  
  public void setPreferredSizeOfList(final Dimension d) {
    listComp.setPreferredSizeOfList(d);
  }
  
  private void initUI() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    add(listComp);
    add(buttonPanel);
  }
 
  class ButtonPanel extends JPanel {
    private JButton addButton = new JButton();
    private JButton deleteButton = new JButton();
    private JButton clearButton = new JButton();
    private JButton editButton = new JButton();
    
    public ButtonPanel() {
      initUI();
    }
    
    private void initUI() {
      setLayout(new FlowLayout(FlowLayout.CENTER));

      addButton.setActionCommand(ACTION_ADD);
      addButton.setToolTipText(LanguageBundle.getInstance()
          .getMessage("filter.add"));
      addButton.setIcon(TImageIconProvider.getInstance()
        .getImageIcon(TImageIconProvider.ADD_ICON, 16, 16));

      deleteButton.setToolTipText(LanguageBundle.getInstance()
          .getMessage("filter.delete"));
      deleteButton.setActionCommand(ACTION_DEL);
      deleteButton.setIcon(TImageIconProvider.getInstance()
          .getImageIcon(TImageIconProvider.DEL_ICON, 16, 16));

      clearButton.setToolTipText(LanguageBundle.getInstance()
          .getMessage("filter.clear_list"));
      clearButton.setActionCommand(ACTION_CLEAR);
      clearButton.setIcon(TImageIconProvider.getInstance()
          .getImageIcon(TImageIconProvider.SHREDDER_ICON, 16, 16));

      editButton.setToolTipText(LanguageBundle.getInstance()
          .getMessage("filter.edit"));
      editButton.setActionCommand(ACTION_EDIT);
      editButton.setIcon(TImageIconProvider.getInstance()
          .getImageIcon(TImageIconProvider.EDIT_ICON, 16, 16));

      add(addButton);
      add(editButton);
      add(deleteButton);
      add(clearButton);
    }
    
    public void addActionListener(ActionListener l) {
      addButton.addActionListener(l);
      editButton.addActionListener(l);
      deleteButton.addActionListener(l);
      clearButton.addActionListener(l);
    }
    
    /*
    public void setEnabled(boolean enabled) {
      addButton.setEnabled(enabled);
      deleteButton.setEnabled(enabled);
    }
    */
  }
  
}
