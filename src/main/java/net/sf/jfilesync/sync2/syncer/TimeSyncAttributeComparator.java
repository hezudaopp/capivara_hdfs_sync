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
 * $Id: TimeSyncAttributeComparator.java,v 1.9 2006/08/02 20:25:47 hunold Exp $
 */
package net.sf.jfilesync.sync2.syncer;

import java.io.IOException;
import java.util.logging.Logger;

import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;
import net.sf.jfilesync.sync2.SyncAttributeComparator;
import net.sf.jfilesync.sync2.SyncDiff;
import net.sf.jfilesync.sync2.SyncFile;
import net.sf.jfilesync.sync2.diffs.SyncDiffTime;

public class TimeSyncAttributeComparator
implements SyncAttributeComparator {

  private int modifyWindow = 0;

  private final static Logger LOGGER = Logger
      .getLogger(TimeSyncAttributeComparator.class.getName());

  public SyncDiff[] compare(AbstractConnectionProxy con1, SyncFile file1,
      AbstractConnectionProxy con2, SyncFile file2) throws IOException {

    SyncDiff[] res = new SyncDiff[2];

    TimeSyncAttributor attributor = new TimeSyncAttributor();
    TimeSyncAttribute attr1 = (TimeSyncAttribute) attributor.getSyncAttribute(
        con1, file1);
    TimeSyncAttribute attr2 = (TimeSyncAttribute) attributor.getSyncAttribute(
        con2, file2);

    final long t1 = attr1.getTimeStamp();
    final long t2 = attr2.getTimeStamp();

    LOGGER.fine("modifyWindow : " + modifyWindow);
    if( Math.abs(t1-t2) <= modifyWindow*1000 ) {
      // do nothing since files are in window range
    } else if (t1 < t2) {
      // file2 newer
      res[0] = new SyncDiffTime(file1, false);
      res[1] = new SyncDiffTime(file2, true);
    } else if (t2 < t1) {
      res[0] = new SyncDiffTime(file1, true);
      res[1] = new SyncDiffTime(file2, false);
    }

    return res;
  }

  public void setModifyWindow(int modifyWindow) {
    if( modifyWindow > 0 ) {
      this.modifyWindow = modifyWindow;
    }
  }

}
