/*
 * capivara - Java File Synchronization
 *
 * Created on 28-Oct-2005
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
 * $Id: ExpressionList.java,v 1.7 2006/02/04 22:42:30 hunold Exp $
 */
package net.sf.jfilesync.sync2.list;

import java.util.ArrayList;
import java.util.List;

import net.sf.jfilesync.sync2.projects.ISyncProjectNode;
import net.sf.jfilesync.sync2.projects.ISyncProjectSavable2;
import net.sf.jfilesync.sync2.projects.nodes.SyncExpressionListNode;

public class ExpressionList implements Cloneable, ISyncProjectSavable2 {

  private List<Expression> list = new ArrayList<Expression>();

//  private final static Logger LOGGER = Logger.getLogger(ExpressionList.class
//      .getName());

  public ExpressionList() {

  }

  public void addExpression(final Expression exp) {
    if( ! list.contains(exp) ) {
      list.add(exp);
    }
  }

  public void merge(final ExpressionList elist) {
    if( elist != null ) {
      for(int i=0; i<elist.size(); i++) {
        Expression exp = elist.get(i);
        if( ! list.contains(exp) ) {
          list.add(exp);
        }
      }
    }
  }

  public void deleteExpression(final Expression exp) {
    list.remove(exp);
  }

  public boolean containsExpression(final Expression exp) {
    return list.contains(exp);
  }

  public void removeList(final ExpressionList elist) {
    if( elist != null ) {
      for(int i=0; i<elist.size(); i++) {
        Expression exp = elist.get(i);
        if( list.contains(exp) ) {
          deleteExpression(exp);
        }
      }
    }
  }

  public void update(final Expression toUpdate,
      final Expression newExp) {
    for(int i=0; i<list.size(); i++) {
      if( list.get(i) == toUpdate ) {
        list.set(i, newExp);
        break;
      }
    }
  }

  public void clear() {
    list.clear();
  }

  public Expression get(final int idx) {
    return list.get(idx);
  }

  public int size() {
    return list.size();
  }

  public void print() {
    if( list != null ) {
      for(int i=0; i<list.size(); i++) {
        Expression exp = list.get(i);
        System.out.println(exp.toString());
      }
    }
  }

  public Object clone() {
    ExpressionList copyList = new ExpressionList();
    for(int i=0; i<list.size(); i++) {
      copyList.addExpression((Expression)(list.get(i)).clone());
    }
    return copyList;
  }

  public boolean equals(Object o) {
    boolean ret = true;

    if( o instanceof ExpressionList ) {
      ExpressionList el = (ExpressionList)o;
      if( el.size() == size() ) {
        for(int i=0; i<size(); i++) {
          if( ! get(i).equals( el.get(i) ) ) {
            ret = false;
            break;
          }
        }
      }
      else {
        ret = false;
      }
    }
    else {
      ret = false;
    }

    return ret;
  }

  public ISyncProjectNode save() {
    SyncExpressionListNode node = new SyncExpressionListNode(list);
    return node;
  }

}
