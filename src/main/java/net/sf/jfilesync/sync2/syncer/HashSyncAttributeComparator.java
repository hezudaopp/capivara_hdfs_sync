/*
 * capivara - Java File Synchronization
 *
 * Created on 28-Jun-2005
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
 * $Id: HashSyncAttributeComparator.java,v 1.6 2006/08/02 20:25:47 hunold Exp $
 */
package net.sf.jfilesync.sync2.syncer;

import java.io.IOException;
import java.util.logging.Logger;

import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;
import net.sf.jfilesync.sync2.SyncAttributeComparator;
import net.sf.jfilesync.sync2.SyncDiff;
import net.sf.jfilesync.sync2.SyncFile;
import net.sf.jfilesync.sync2.UnknownAttributeException;

public abstract class HashSyncAttributeComparator implements
        SyncAttributeComparator {

    static Logger log = Logger.getLogger(HashSyncAttributeComparator.class
            .getPackage().getName());

    public SyncDiff[] compare(final AbstractConnectionProxy con1,
            final SyncFile file1, final AbstractConnectionProxy con2,
            final SyncFile file2) throws IOException {

        SyncDiff[] res = new SyncDiff[2];

        // in case files differ in size they will also differ
        // in their hash code -> don't compute hash
        if (!file1.getFile().getFileSize()
                .equals(file2.getFile().getFileSize())) {
            res[0] = createSyncDiff(file1);
            res[1] = createSyncDiff(file2);
        } else {
            HashSyncAttributor attributor = getHashSyncAttributor();
            HashSyncAttribute attr1 = (HashSyncAttribute) attributor
                    .getSyncAttribute(con1, file1);
            HashSyncAttribute attr2 = (HashSyncAttribute) attributor
                    .getSyncAttribute(con2, file2);

            try {
                if (!attr1.equals(attr2)) {
                    res[0] = createSyncDiff(file1);
                    res[1] = createSyncDiff(file2);
                }
            } catch (UnknownAttributeException e) {
                log.severe(e.getMessage());
            }
        }
        return res;
    }

    protected abstract SyncDiff createSyncDiff(SyncFile f);

    protected abstract HashSyncAttributor getHashSyncAttributor();

}
