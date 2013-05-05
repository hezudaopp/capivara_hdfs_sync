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
 * $Id: SyncDiffCollection.java,v 1.7 2006/04/02 14:47:42 hunold Exp $
 */
package net.sf.jfilesync.sync2;

import java.util.HashMap;

public class SyncDiffCollection {

  private HashMap<SyncFile, SyncDiff> file2diff = new HashMap<SyncFile, SyncDiff>();

  public SyncDiffCollection() {
  }

  public void addSyncDiff(final SyncDiff diff) {
    file2diff.put(diff.getSyncFile(), diff);
  }

  public void removeSyncDiff(final SyncDiff diff) {
    if (diff != null) {
      if (file2diff.containsKey(diff.getSyncFile())) {
        file2diff.remove(diff.getSyncFile());
      }
    }
  }

  public boolean contains(final SyncFile file) {
    return file2diff.containsKey(file);
  }

  public SyncDiff getSyncDiff(final SyncFile file) {
    return (SyncDiff) file2diff.get(file);
  }

}
