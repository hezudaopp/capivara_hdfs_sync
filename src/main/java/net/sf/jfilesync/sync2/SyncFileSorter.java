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
 * $Id: SyncFileSorter.java,v 1.4 2005/08/19 21:29:02 hunold Exp $
 */

package net.sf.jfilesync.sync2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sf.jfilesync.sync2.syncer.ISyncFileSorter;

public class SyncFileSorter implements ISyncFileSorter {

  private boolean dirsFirst;
  private boolean caseInsensitive;

  public SyncFileSorter() {
  }

  public SyncFile[] sortByName(SyncFile[] files) {
    return sortNames(files);
  }

  public void enableDirectoriesFirst(boolean enabled) {
    this.dirsFirst = enabled;
  }

  public void enableCaseInsensitive(boolean enabled) {
    this.caseInsensitive = enabled;
  }
  
//  public SyncFile[] sortByNameDirectoriesFirst(SyncFile[] files) {
//    dirsFirst = true;
//    return sortNames(files);
//  }

  private SyncFile[] sortNames(final SyncFile[] files) {

    SyncFile[] sortedFiles = files;

    final List<SyncFile> fileList = new ArrayList<SyncFile>();
    for (int i = 0; i < sortedFiles.length; i++) {
      fileList.add(sortedFiles[i]);
    }

    Collections.sort(fileList, new TSyncNameComparator());

    sortedFiles = fileList.toArray(files);

    return sortedFiles;
  }

  class TSyncNameComparator implements Comparator<SyncFile> {

    public int compare(final SyncFile file1, final SyncFile file2) {
      int cmp = 0;
      
      if (dirsFirst) {
        if (file1.getFile().isDirectory() ^ file2.getFile().isDirectory()) {
          if (file1.getFile().isDirectory()) {
            cmp = -1;
          } else {
            cmp =  1;
          }
        }
      }
      
      if (cmp == 0) {
        // if both have same type we gotta do some name comparation

        String fileName1 = file1.getFile().getFileName();
        String fileName2 = file2.getFile().getFileName();

        if (caseInsensitive) {
          fileName1 = fileName1.toLowerCase();
          fileName2 = fileName2.toLowerCase();
        }

        cmp = fileName1.compareTo(fileName2);
      }
      
      return cmp;
    }

  }

}
