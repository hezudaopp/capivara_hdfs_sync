/*
 * capivara - Java File Synchronization
 *
 * Created on 25-Dec-2005
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import net.sf.jfilesync.prop.LanguageBundle;

public class ExpressionMainController 
implements ActionListener {

  private ExpressionMainModel model;
  private ExpressionMainPanel view;
  
  static Logger log = Logger.getLogger(ExpressionMainController.class
      .getPackage().getName());

  public ExpressionMainController(final ExpressionMainModel model,
      final ExpressionMainPanel view) {
    this.model = model;
    this.view = view;
    
    view.addActionListener(this);
  }
  
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if( cmd.equals(ExpressionMainPanel.ACTION_ADD_INCL) ) {
      String listName = view.getSelectedNamedList();
      if( listName != null ) {
        NamedExpressionList nl = model.getNamedExpressionList(listName);
        model.mergeIncludeList(nl.getList());
        view.setIncludeList(model.getIncludeList());
      }
    }
    else if( cmd.equals(ExpressionMainPanel.ACTION_ADD_EXCL) ) {
      String listName = view.getSelectedNamedList();
      if( listName != null ) {
        NamedExpressionList nl = model.getNamedExpressionList(listName);
        model.mergeExcludeList(nl.getList());
        view.setExcludeList(model.getExcludeList());
      }
    }
    else if( cmd.equals(ExpressionMainPanel.ACTION_SAVE_INCL) ) {
      ExpressionList list = (ExpressionList)model.getIncludeList().clone();
      saveList(list);
    }
    else if( cmd.equals(ExpressionMainPanel.ACTION_SAVE_EXCL) ) {
      ExpressionList list = (ExpressionList)model.getExcludeList().clone();
      saveList(list);
    }
    else if( cmd.equals(NamedExpressionListComponent.ACTION_DELETE) ) {
      String listName = view.getSelectedNamedList();
      if( listName != null ) {
        deleteList(listName);
      }
    }
    
  }

  
  protected void saveList(final ExpressionList list) {
    if( list != null && list.size() > 0 ) {
      
      boolean running = true;
      boolean cancelled = false;
      String listName = "";
      
      do {
        listName = JOptionPane.showInputDialog(view, LanguageBundle
            .getInstance().getMessage("filter.enter_listname"),
            LanguageBundle.getInstance()
            .getMessage("filter.enter_listname"), JOptionPane.QUESTION_MESSAGE);
        
        if( listName == null ) {
          running = false;
          cancelled = true;
        }
        else if( listName.equals("") ) {
          continue;
        }
        else {
          boolean exists = model.getNExpListList().containsNEList(listName);
          if( ! exists ) {
            running = false;
          }
          else {
            JOptionPane.showMessageDialog(view, 
                LanguageBundle.getInstance()
                .getMessage("filter.listname_exists"));
          }
        }
      }
      while( running );
      
      if( ! cancelled ) {
        list.print();
        NamedExpressionList nl = new NamedExpressionList(listName, list);
        model.addNamedExpressionList(nl);
        view.updateSavedExpressions();
        saveToFile(nl);        
      }
      
    }
  }
  
  protected void deleteList(final String listName) {
    int opt = JOptionPane.showConfirmDialog(view, 
        LanguageBundle.getInstance().getMessage("filter.delete_list")
        + " " + listName,
        LanguageBundle.getInstance().getMessage("label.delete"), 
        JOptionPane.YES_NO_OPTION);
    if( opt == JOptionPane.YES_OPTION ) {
      model.removeNamedExpressionList(listName);
      view.updateSavedExpressions();
      
      final NExpListList allLists = model.getNExpListList();
      allLists.print();
      rewriteFile(allLists);
    }
  }
  
  private void saveToFile(final NamedExpressionList list) {
    NamedListConfigHandler handler = NamedListConfigHandler.getInstance();
    try {
      handler.saveNamedList(list);
    }
    catch(IOException e) {
      JOptionPane.showMessageDialog(view, 
          LanguageBundle.getInstance().getMessage("filter.save_error")
          + e.getMessage(),
          LanguageBundle.getInstance().getMessage("error.io.name"),
          JOptionPane.ERROR_MESSAGE);
    }
  }
  
  private void rewriteFile(final NExpListList allLists) {
    NamedListConfigHandler handler = NamedListConfigHandler.getInstance();
    try {
      handler.saveLists(allLists);
    }
    catch(IOException e) {
      JOptionPane.showMessageDialog(view, 
          LanguageBundle.getInstance().getMessage("filter.save_error")
          + e.getMessage(),
          LanguageBundle.getInstance().getMessage("error.io.name"),
          JOptionPane.ERROR_MESSAGE);
    }
  }
  
}
