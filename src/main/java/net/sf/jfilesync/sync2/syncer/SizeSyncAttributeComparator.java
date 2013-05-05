/*
 * capivara - Java File Synchronization
 *
 * Created on 12 Nov 2007
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
package net.sf.jfilesync.sync2.syncer;

import java.io.IOException;

import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;
import net.sf.jfilesync.sync2.SyncAttributeComparator;
import net.sf.jfilesync.sync2.SyncDiff;
import net.sf.jfilesync.sync2.SyncFile;
import net.sf.jfilesync.sync2.diffs.SizeSyncDiff;

public class SizeSyncAttributeComparator implements SyncAttributeComparator {

  public SyncDiff[] compare(AbstractConnectionProxy con1, SyncFile file1,
      AbstractConnectionProxy con2, SyncFile file2) throws IOException {
    
    SyncDiff[] res = new SyncDiff[2];

    if( ! file1.getFile().getFileSize().equals(file2.getFile().getFileSize()) ) {
      res[0] = new SizeSyncDiff(file1);
      res[1] = new SizeSyncDiff(file2);
    }

    return res;
  }

}
