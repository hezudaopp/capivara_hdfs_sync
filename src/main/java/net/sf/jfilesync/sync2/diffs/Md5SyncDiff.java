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
 * $Id: Md5SyncDiff.java,v 1.4 2006/05/03 20:32:27 hunold Exp $
 */
package net.sf.jfilesync.sync2.diffs;

import java.awt.Color;

import net.sf.jfilesync.sync2.SyncDiff;
import net.sf.jfilesync.sync2.SyncFile;

public class Md5SyncDiff extends SyncDiff {

    public Md5SyncDiff(final SyncFile file) {
        super(file);
    }

    public String getDescription() {
        return "MD5";
    }

    public Color getForeground() {
        return Color.BLUE;
    }

    public Color getBackground() {
        return Color.WHITE;
    }

    public String getLongDescription() {
        return getDescription();
    }

}
