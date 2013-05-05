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

import java.util.List;

public interface ISyncProjectNode {

  public String getId();

  public void addChild(ISyncProjectNode node);

  public ISyncProjectNode getChild(String nodeId);

  public List<String> getChildKeys();

  public void storeValue(final String key, final String value);

  public String getValue(final String key);

  public List<String> getValueKeys();

  public void storeValueList(final String key, final List<String> valueList);

  public List<String> getValueList(final String key);

  public List<String> getValueListKeys();

  public ISyncProjectSavable2 load();

}
