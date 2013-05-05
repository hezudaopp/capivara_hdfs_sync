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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.PatternSyntaxException;

import javax.swing.JOptionPane;

import net.sf.jfilesync.prop.LanguageBundle;

public class ExpressionListController 
implements ActionListener {

  private ExpressionList list;
  private ExpressionListEditPanel listGui;
  private AddEditExpressionDialog dialog;
  
  private final static int STATE_ADD  = 0;
  private final static int STATE_EDIT = 1;
  private int editState = STATE_ADD;
  
  public ExpressionListController(ExpressionList list,
      ExpressionListEditPanel listGui) {
    this.list = list;
    this.listGui = listGui;
    listGui.addActionListener(this);
    listGui.setEnabled(true);
  }
  
  public void setExpressionList(ExpressionList list) {
    this.list = list;
    listGui.setExpressionList(list);
  }
  
  public void setEditEnabled(final boolean enabled) {
    listGui.setEnabled(enabled);
  }
      
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if( cmd.equals(ExpressionListEditPanel.ACTION_ADD) ) {
      editState = STATE_ADD;
      addExpression();
    }
    else if( cmd.equals(ExpressionListEditPanel.ACTION_DEL) ) {
      deleteExpression();
    }
    else if( cmd.equals(ExpressionListEditPanel.ACTION_CLEAR ) ) {
      list.clear();
      listGui.setExpressionList(list);
    }
    else if( cmd.equals(ExpressionListEditPanel.ACTION_EDIT) ) {
      editState = STATE_EDIT;
      editExpression();
    }
    else if( cmd.equals(AddEditExpressionDialog.OPT_APPLY) ) {
      applyExpression();
    }
    else if( cmd.equals(AddEditExpressionDialog.OPT_DISMISS) ) {
      dismiss();
    }
  }
  
  private void addExpression() {
    dialog = new AddEditExpressionDialog(listGui.getOwner());
    dialog.setActionListener(this);
    dialog.setVisible(true);
  }
  
  private void editExpression() {
    Expression exp = listGui.getSelectedExpression();
    if( exp != null ) {
      dialog = new AddEditExpressionDialog(listGui.getOwner(), exp);
      dialog.setActionListener(this);
      dialog.setVisible(true);
    }
  }
  
  private void deleteExpression() {
    Expression exp = listGui.getSelectedExpression();
    if( exp != null ) {
      list.deleteExpression(exp);
      listGui.setExpressionList(list);
    }
  }
      
  /*
  private void testExpression() {
    String exp = dialog.getExpressionString();
    String testVal = dialog.getTestString();
    
    if( exp == null || exp.equals("") ) {
      JOptionPane.showMessageDialog(dialog, 
          LanguageBundle.getInstance()
          .getMessage("filter.exp_empty") );
      return;
    }
    if( testVal == null || testVal.equals("") ) {
      JOptionPane.showMessageDialog(dialog, 
          LanguageBundle.getInstance()
          .getMessage("filter.test_empty"));
      return;
    }
    
    try {
      Expression.validate(exp);
      Expression testExp = new Expression(exp);
      boolean matches = testExp.matches(testVal);
//      Pattern pattern = Pattern.compile(exp);
//      Matcher matcher = pattern.matcher(testVal);
//      if( matcher.matches() ) {
      if( matches ) {
        dialog.setStatusText(LanguageBundle.getInstance()
            .getMessage("filter.match"));
      }
      else {
        dialog.setStatusText(LanguageBundle.getInstance()
            .getMessage("filter.no_match"));
      }
    }
    catch(PatternSyntaxException e) {
      JOptionPane.showMessageDialog(dialog, e.getMessage());
      return;
    }
  }
  */
  
  private void applyExpression() {
    String exp = dialog.getExpressionString();
    if( exp == null || exp.equals("") ) {
      JOptionPane.showMessageDialog(dialog, 
          LanguageBundle.getInstance()
          .getMessage("filter.exp_empty"));
      return;
    }
    else {
      try {
        Expression.validate(exp);        
//        Pattern pattern = Pattern.compile(exp);
        dialog.setVisible(false);
        
        if( editState == STATE_ADD ) {
          list.addExpression(new Expression(exp));
          listGui.setExpressionList(list);
        }
        else if( editState == STATE_EDIT ) {
          Expression updated = listGui.getSelectedExpression();
          if( updated != null ) {
            list.update(updated, new Expression(exp));
            listGui.setExpressionList(list);
          }
        }
      }
      catch(PatternSyntaxException e) {
        JOptionPane.showMessageDialog(dialog, 
            LanguageBundle.getInstance()
            .getMessage("filter.invalid_pattern")         
            + e.getMessage());
      }
    }
  }
  
  private void dismiss() {
    dialog.setVisible(false);
  }
    
  
}
