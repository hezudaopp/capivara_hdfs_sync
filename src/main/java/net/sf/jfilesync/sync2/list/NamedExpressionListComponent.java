/*
 * capivara - Java File Synchronization
 *
 * Created on 29-Nov-2005
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

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

import net.sf.jfilesync.gui.icons.TImageIconProvider;
import net.sf.jfilesync.prop.LanguageBundle;


public class NamedExpressionListComponent extends JPanel {

  private JList guiList = new JList();
  private JScrollPane listScroller = new JScrollPane(guiList);
  private DefaultListModel listModel = new DefaultListModel();
   
  private JButton deleteListButton = new JButton();
  public final static String ACTION_SAVE   = "save";
  public final static String ACTION_DELETE = "delete_list";

  private ExpressionListComponent showListPanel;
    
  public NamedExpressionListComponent() {
    guiList.setModel(listModel);
    guiList.setCellRenderer(new DefaultListCellRenderer());
    guiList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
    initUI();
  }
  
  public void addActionListener(ActionListener l) {
    deleteListButton.addActionListener(l);
  }
  
  public void setListSelectionListener(ListSelectionListener l) {
    guiList.addListSelectionListener(l);
  }
  
  public synchronized void addDeleteListListener(ActionListener l) {
    deleteListButton.addActionListener(l);
  }
  
  public synchronized void setNExpListList(NExpListList list) {
    if( list != null ) {
      listModel.clear();
      for(int i=0; i<list.size(); i++) {
        NamedExpressionList le = list.getElementAt(i);
        listModel.addElement(le.getName());
      }
    }
  }
    
  public String getSelectedExpressionList() {
    String back = null;
    if( guiList.getSelectedIndex() != -1 ) {
      back = (String)guiList.getSelectedValue();
    }
    return back;
  }
    
  public void showExpressionList(ExpressionList list) {
    if( list != null ) {
      showListPanel.setExpressionList(list);
    }
  }  
  
  public void clearList() {
    showListPanel.clearExpressionList();
  }
  
  private void initUI() {
    
    deleteListButton.setActionCommand(ACTION_DELETE);
    deleteListButton.setText(LanguageBundle.getInstance()
        .getMessage("filter.delete_list"));
    deleteListButton.setIcon(TImageIconProvider.getInstance()
        .getImageIcon(TImageIconProvider.DEL_ICON, 16, 16));
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.add(deleteListButton);
    buttonPanel.setBorder(null);
    
    listScroller.setBorder(BorderFactory.createTitledBorder(
        LanguageBundle.getInstance()
        .getMessage("filter.list_names") ));
    
    showListPanel = new ExpressionListComponent();
    showListPanel.setBorder(BorderFactory.createTitledBorder(
        LanguageBundle.getInstance()
        .getMessage("filter.list_contents") ));
    
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    
    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
    centerPanel.add(listScroller);
    centerPanel.add(buttonPanel);
    centerPanel.add(showListPanel);
    
    add(centerPanel);
    
    setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(),
        LanguageBundle.getInstance()
        .getMessage("filter.title.saved_filters") ));
  }
      
}
