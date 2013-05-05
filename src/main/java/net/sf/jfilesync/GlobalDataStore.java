/*
 * capivara - Java File Synchronization
 *
 * Created on Feb 24, 2006
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
package net.sf.jfilesync;

import java.util.HashMap;

import net.sf.jfilesync.sync2.list.ExpressionList;

public class GlobalDataStore {

  private static GlobalDataStore store;

  private ExpressionList includeList;
  private ExpressionList excludeList;

  private HashMap<String, Object> valueStore = new HashMap<String, Object>();

  private GlobalDataStore() {

  }

  public static GlobalDataStore getInstance() {
    if( store == null ) {
      store = new GlobalDataStore();
    }
    return store;
  }

  public ExpressionList getExcludeList() {
    return excludeList;
  }

  public void setExcludeList(ExpressionList excludeList) {
    this.excludeList = excludeList;
  }

  public ExpressionList getIncludeList() {
    return includeList;
  }

  public void setIncludeList(ExpressionList includeList) {
    this.includeList = includeList;
  }

  public void storeValue(String id, Object value) {
    valueStore.put(id, value);
  }

  public Object getValue(String id) {
    return valueStore.get(id);
  }

}
