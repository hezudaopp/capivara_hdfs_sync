/*
 * capivara - Java File Synchronization
 *
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
 * $Id: SyncFile.java,v 1.14 2006/04/02 14:47:42 hunold Exp $
 */
package net.sf.jfilesync.sync2;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import net.sf.jfilesync.engine.TFileProperties;

public class SyncFile {

  private final TFileProperties file;
  private Hashtable<Integer, SyncAttribute> syncHash = new Hashtable<Integer, SyncAttribute>();
  private final String relativePath;
  //private boolean unique   = false;
  private boolean ignored  = false;
  private boolean excluded = false;
  private List<SyncFile> children = new ArrayList<SyncFile>();

  private final static Logger LOGGER = Logger.getLogger(SyncFile.class
      .getPackage().getName());

  public SyncFile(TFileProperties file, String relativePath) {
    this.file = file;
    this.relativePath = relativePath;
  }

  public TFileProperties getFile() {
    return file;
  }

  public void addSyncAttribute(SyncAttribute attr) {
    syncHash.put(new Integer(attr.getType()), attr);
  }

  public SyncAttribute[] getSyncAttributes() {
    final int size = syncHash.size();
    final SyncAttribute[] attrs = new SyncAttribute[size];
    final Enumeration<SyncAttribute> en = syncHash.elements();
    for (int i = 0; en.hasMoreElements(); i++) {
      attrs[i] = en.nextElement();
    }
    return attrs;
  }

  public SyncAttribute getSyncAttribute(int attributeID) {
    SyncAttribute attr = null;
    Integer methodKey = new Integer(attributeID);
    if( syncHash.containsKey(methodKey) ) {
      return (SyncAttribute)syncHash.get(methodKey);
    }
    return attr;
  }

  public void setExcluded(boolean b) {
    excluded = b;
  }

  public boolean isExcluded() {
    return excluded;
  }

  public String getRelativePath() {
    return relativePath;
  }

  public void addChild(SyncFile child) {
    children.add(child);
  }

  public SyncFile[] getChildren() {
    final java.util.List<SyncFile> l = new ArrayList<SyncFile>();
    SyncFile[] childs = getAllChildren();
    for(int i=0; i<childs.length; i++) {
      if( childs[i] != null && !childs[i].isIgnored() ) {
        l.add(childs[i]);
      }
    }
    return (SyncFile[])l.toArray(new SyncFile[l.size()]);
  }

  public SyncFile[] getAllChildren() {
    return (SyncFile[])children.toArray(new SyncFile[children.size()]);
  }
  
  public void setIgnored(final boolean ignored) {
    LOGGER.info("set ignored : " + getFile().getFileName() + " : " + ignored);
    this.ignored = ignored;
  }

  /**
   * returns true if users has set this file to be ignored in sync process 
   * 
   * @param ignored
   */

  public boolean isIgnored() {
    return ignored;
  }

  public SyncFile findChild(final String absoluteFileName) {
    return getChild(this, absoluteFileName);
  }

  protected SyncFile getChild(SyncFile f, String absoluteFileName) {
    SyncFile child = null;
    if (f.getFile().getAbsoluteFileName().equals(absoluteFileName)) {
      child = f;
    } else {
      if (f.getFile().isDirectory()) {
        SyncFile[] childs = f.getAllChildren();
        for (int i = 0; i < childs.length && child == null; i++) {
          child = getChild(childs[i], absoluteFileName);
        }
      }
    }
    return child;
  }

  public boolean containsExcludedChild() {
    boolean ret = false;
    ret = containsExcluded(this);
    return ret;
  }

  private boolean containsExcluded(final SyncFile f) {
    boolean ret = false;

    SyncFile[] childs = f.getChildren();
    for (int i = 0; i < childs.length; i++) {
      SyncFile child = childs[i];
      if (child.isExcluded()) {
        ret = true;
        break;
      } else {
        boolean inChild = containsExcluded(child);
        if (inChild) {
          ret = true;
          break;
        }
      }
    }

    return ret;
  }

}
