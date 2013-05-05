/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TFileCompareStatus.java,v 1.2 2005/08/19 21:29:01 hunold Exp $
 */

package net.sf.jfilesync.engine;

public class TFileCompareStatus
{
  public static final int
      TIME_NEWER           =  1,
      TIME_OLDER           =  2,
      FILE_ORPHAN          =  3,
      SIZE_LESS            =  4,
      SIZE_GREATER         =  5;

  private boolean differ = false;
  private TFileProperties file;

  private static final int
      TIME_STAT      = 0,
      SIZE_STAT      = 1,
      ORPHAN_STAT    = 2;

  private int[] status = {0, 0, 0};

  public TFileCompareStatus(TFileProperties file) {
    this.file = file;
  }

  public void addDifference(int property) {
    int stat = -1;
    switch(property) {
      case TIME_NEWER:
      case TIME_OLDER:
        stat = TIME_STAT;
        break;
      case FILE_ORPHAN:
        stat = ORPHAN_STAT;
        break;
      case SIZE_LESS:
      case SIZE_GREATER:
        stat = SIZE_STAT;
        break;
    }
    if( stat != -1 ) {
      status[stat] = property;
      differ = true;
    }
  }

  public TFileProperties getFile() {
    return file;
  }

  public boolean isEqual() {
    return ! differ;
  }

  public int getTimeStat() {
    return status[TIME_STAT];
  }

  public int getSizeStat() {
    return status[SIZE_STAT];
  }

  public boolean isOrphan() {
    return (status[ORPHAN_STAT] == FILE_ORPHAN);
  }

  public void setOrphan() {
    addDifference(FILE_ORPHAN);
  }

  public boolean isNewer() {
    return (status[TIME_STAT] == TIME_NEWER);
  }

  public boolean hasLessSize() {
    return (status[SIZE_STAT] == SIZE_LESS);
  }

}
