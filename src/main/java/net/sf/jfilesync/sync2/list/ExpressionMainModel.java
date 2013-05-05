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

public class ExpressionMainModel {

  // new lists
  private ExpressionList includeList = new ExpressionList();
  private ExpressionList excludeList = new ExpressionList();

  // saved lists
  private NExpListList listOfLists;
  
  public ExpressionMainModel(NExpListList listOfLists) {
    this.listOfLists = listOfLists;
  }
  
  public NExpListList getNExpListList() {
    return listOfLists;
  }
  
  public NamedExpressionList getNamedExpressionList(final String name) {
    NamedExpressionList ret = null;
    if( listOfLists != null ) {
      ret = listOfLists.getList(name);
    }
    return ret;
  }
  
  public void addNamedExpressionList(final NamedExpressionList list) {
    if( list != null ) {
      listOfLists.addNamedExpressionList(list);
    }
  }
    
  public void removeNamedExpressionList(final String name) {
    if( name != null ) {
      listOfLists.deleteList(name);
    }
  }
  
  public void mergeIncludeList(final ExpressionList list) {
    if( list != null ) {
      includeList.merge(list);
    }
  }

  public void mergeExcludeList(final ExpressionList list) {
    if( list != null ) {
      excludeList.merge(list);
    }
  }
  
  public void deleteFromIncludeList(final ExpressionList list) {
    if( list != null ) {
      includeList.removeList(list);
    }
  }

  public void deleteFromExcludeList(final ExpressionList list) {
    if( list != null ) {
      excludeList.removeList(list);
    }
  }
  
  public ExpressionList getIncludeList() {
    return includeList;
  }

  public void setIncludeList(ExpressionList il) {
    includeList = il;
  }
  
  public ExpressionList getExcludeList() {
    return excludeList;
  }

  public void setExcludeList(ExpressionList el) {
    excludeList = el;
  }

}
