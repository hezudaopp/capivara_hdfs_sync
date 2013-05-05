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

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.*;


public class ExpressionListComponent extends JPanel {

  private ExpressionList elist = new ExpressionList();
  private JList dlist = new JList();
  private JScrollPane listScroller = new JScrollPane(dlist);

  static Logger log = Logger.getLogger(ExpressionListComponent.class.getPackage().getName());

  public ExpressionListComponent() {
    initUI();
  }

  public Expression getSelectedExpression() {
    Expression ret = null;
    ret = (Expression)dlist.getSelectedValue();
    return ret;
  }

  public void setExpressionList(final ExpressionList l) {
    if( l != null ) {
      elist = l;
      fillList();
    }
  }

  public void clearExpressionList() {
    elist = null;
    fillList();
  }

  private void fillList() {
    final List<Expression> objectList = new ArrayList<Expression>();
    if( elist != null ) {
      for(int i=0; i < elist.size(); i++) {
        objectList.add(elist.get(i));
      }
    }
    Object[] objectArray = objectList.toArray(new Object[objectList.size()]);
    dlist.setListData(objectArray);
  }

  private void initUI() {
    add(listScroller);
  }

  public void setPreferredSizeOfList(final Dimension d) {
    if( d != null && listScroller != null ) {
      listScroller.setPreferredSize(d);
    }
  }


  class ExpressionListCellRenderer implements ListCellRenderer {
    public Component getListCellRendererComponent(JList list,
        Object value, int index, boolean isSelected,
        boolean cellHasFocus) {
      JTextField field = new JTextField();
      if( value instanceof Expression ) {
        field.setText( ((Expression)value).toString() );
      }
      return field;
    }
  }


}
