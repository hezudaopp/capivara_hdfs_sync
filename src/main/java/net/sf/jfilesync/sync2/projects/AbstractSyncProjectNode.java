/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003-2007 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
package net.sf.jfilesync.sync2.projects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AbstractSyncProjectNode implements ISyncProjectNode {

  private final String objectId;
  private final HashMap<String, ISyncProjectNode> childMap = new HashMap<String, ISyncProjectNode>();
  private final HashMap<String, String> valueMap = new HashMap<String, String>();
  private final HashMap<String, List<String>> listMap = new HashMap<String, List<String>>();

  public AbstractSyncProjectNode(String objectId) {
    this.objectId = objectId;
  }

  public String getId() {
    return objectId;
  }

  public void storeValue(String key, String value) {
    valueMap.put(key, value);
  }

  public String getValue(final String key) {
    return valueMap.get(key);
  }

  public List<String> getValueKeys() {
    return new ArrayList<String>(valueMap.keySet());
  }


  public void storeValueList(String key, List<String> valueList) {
    listMap.put(key, valueList);
  }

  public List<String> getValueList(String key) {
    return listMap.get(key);
  }

  public List<String> getValueListKeys() {
    return new ArrayList<String>(listMap.keySet());
  }


  public void addChild(ISyncProjectNode node) {
    childMap.put(node.getId(), node);
  }

  public ISyncProjectNode getChild(final String id) {
    return childMap.get(id);
  }

  public List<String> getChildKeys() {
    return new ArrayList<String>(childMap.keySet());
  }


  public abstract ISyncProjectSavable2 load();

}
