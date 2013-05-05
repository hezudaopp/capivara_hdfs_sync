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
 * $Id: SyncDiff.java,v 1.11 2006/05/03 20:32:27 hunold Exp $
 */
package net.sf.jfilesync.sync2;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public abstract class SyncDiff {

  private List<SyncDiff> childs = new ArrayList<SyncDiff>();
  private final SyncFile file;

  private final static Logger LOGGER = Logger.getLogger(SyncDiff.class
      .getName());

  public SyncDiff(final SyncFile file) {
    this.file = file;
  }

  public SyncFile getSyncFile() {
    return this.file;
  }

  public void addChild(final SyncDiff child) {
    // debugging
    final Iterator<SyncDiff> it = childs.iterator();
    while (it.hasNext()) {
      if (((SyncDiff) it.next()).getSyncFile().getFile().getAbsoluteFileName()
          .equals(child.getSyncFile().getFile().getAbsoluteFileName())) {
        LOGGER.info("CHILD EXISTS: "
            + child.getSyncFile().getFile().getAbsoluteFileName());
      }
    }

    childs.add(child);
  }

  public SyncDiff[] getChildren() {
    // collect diffs whose files are set ignored
    java.util.List<SyncDiff> diffList = new ArrayList<SyncDiff>();

    final Iterator<SyncDiff> it = childs.iterator();
    while( it.hasNext() ) {
      SyncDiff d = (SyncDiff)it.next();
      if( ! d.getSyncFile().isIgnored() ) {
        diffList.add(d);
      }
    }
    return (SyncDiff[])diffList.toArray(new SyncDiff[diffList.size()]);
  }

  public boolean hasChildren() {
    return !childs.isEmpty();
  }

  public void print(int depth) {
    for(int i=0; i<depth; i++) System.out.print("-");
    if( file != null ) {
      System.out.println(file.getFile().getFileName());
    }
    else {
      System.err.println("file is NULL!! WTF??");
    }

    final Iterator<SyncDiff> it = childs.iterator();
    while( it.hasNext() ) {
      ((SyncDiff)it.next()).print(depth+1);
    }
  }

  public abstract String getDescription();
  public abstract Color getForeground();
  public abstract Color getBackground();
  public abstract String getLongDescription();
}
