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
 * $Id: SyncDiffType.java,v 1.6 2006/08/09 22:18:40 hunold Exp $
 */
package net.sf.jfilesync.sync2.diffs;

import java.awt.Color;


import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.sync2.SyncDiff;
import net.sf.jfilesync.sync2.SyncFile;
import net.sf.jfilesync.sync2.gui.SyncColorSet;

/**
 * 
 * @author sascha
 *
 * is triggered when 2 files have the same name but
 * one is a normal file and the other a directory
 */

public class SyncDiffType extends SyncDiff {

  public SyncDiffType(final SyncFile file) {
    super(file);
  }
  
  public Color getForeground() {
    return SyncColorSet.getColor(SyncColorSet.COLOR_DIFF_TYPE);
  }

  public Color getBackground() {
    return Color.WHITE;
  }
  
  public String getDescription() {
    return LanguageBundle.getInstance().getMessage("file.diff.type");
  }
  
  public String getLongDescription() {
    return getDescription();
  }

}
