/*
 * capivara - Java File Synchronization
 *
 * Created on 06-Jul-2005
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
 * $Id: Sha1SyncAttributeComparator.java,v 1.7 2006/03/04 12:51:56 hunold Exp $
 */
package net.sf.jfilesync.sync2.syncer;

import net.sf.jfilesync.sync2.SyncDiff;
import net.sf.jfilesync.sync2.SyncFile;
import net.sf.jfilesync.sync2.diffs.Sha1SyncDiff;

public class Sha1SyncAttributeComparator 
extends HashSyncAttributeComparator {
    
  public SyncDiff createSyncDiff(SyncFile f) {
    return new Sha1SyncDiff(f);
  }
  
  public HashSyncAttributor getHashSyncAttributor() {
    return new Sha1SyncAttributor();
  }

}
