/*
 * capivara - Java File Synchronization
 *
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
 * $Id: FileResourceBundle.java,v 1.2 2006/09/06 09:04:02 hunold Exp $
 */
package net.sf.jfilesync.prop;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;

public class FileResourceBundle extends ResourceBundle {

  private final ResourceBundle parent;
  private final ResourceBundle child;

  private final static Logger LOGGER = Logger
      .getLogger(FileResourceBundle.class.getName());

  public FileResourceBundle(ResourceBundle parent, ResourceBundle child) {
    if (parent == null) {
      throw new IllegalArgumentException("parent resource bundle is null");
    }
    if (child == null) {
      throw new IllegalArgumentException("parent resource bundle is null");
    }
    this.parent = parent;
    this.child = child;
  }

  public Enumeration<String> getKeys() {
    Set<String> keySet = new HashSet<String>();
    Enumeration<String> parentKeyEnum = parent.getKeys();
    while (parentKeyEnum.hasMoreElements()) {
      keySet.add(parentKeyEnum.nextElement());
    }
    Enumeration<String> childKeyEnum = child.getKeys();
    while (childKeyEnum.hasMoreElements()) {
      keySet.add(childKeyEnum.nextElement());
    }
    return new FileResourceEnumeration(keySet.iterator());
  }

  protected Object handleGetObject(final String key) {
    Object value = null;
    try {
      value = child.getString(key);
    } catch (NullPointerException e) {
      LOGGER.warning(e.getMessage());
      value = "key unknown";
    } catch (MissingResourceException e) {
      value = parent.getObject(key);
    }
    return value;
  }

}
