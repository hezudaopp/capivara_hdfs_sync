/*
 * capivara - Java File Synchronization
 *
 * Created on 07-Dec-2005
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
 */
package net.sf.jfilesync.sync2.list;

import java.util.ArrayList;
import java.util.List;

public class NExpListList {

  private List<NamedExpressionList> listOfLists = new ArrayList<NamedExpressionList>();

  public NExpListList() {

  }

  public void addNamedExpressionList(final NamedExpressionList nl) {
    if (nl != null) {
      listOfLists.add(nl);
    }
  }

  public void changeList(final String name, final ExpressionList list) {
    NamedExpressionList nl = getList(name);
    if (nl != null) {
      nl.setExpressionList(list);
    }
  }

  public boolean containsNEList(String name) {
    boolean ret = false;
    for (NamedExpressionList nl : listOfLists) {
      if (nl.getName().equals(name)) {
        ret = true;
        break;
      }
    }
    return ret;
  }

  public void deleteList(final String name) {
    for (NamedExpressionList nl : listOfLists) {
      if (nl.getName().equals(name)) {
        listOfLists.remove(nl);
        break;
      }
    }
  }

  public int size() {
    return listOfLists.size();
  }

  public NamedExpressionList getElementAt(int i) {
    NamedExpressionList ret = null;
    if (i >= 0 && i < listOfLists.size()) {
      ret = (NamedExpressionList) listOfLists.get(i);
    }
    return ret;
  }

  public NamedExpressionList getList(final String name) {
    NamedExpressionList ret = null;
    if (name != null) {
      for (NamedExpressionList nl : listOfLists) {
        if (nl.getName().equals(name)) {
          ret = nl;
          break;
        }
      }
    }
    return ret;
  }

  public void print() {
    for (NamedExpressionList nl : listOfLists) {
      nl.print();
    }
  }

}
