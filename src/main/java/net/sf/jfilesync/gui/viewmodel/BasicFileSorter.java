/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2007 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
package net.sf.jfilesync.gui.viewmodel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sf.jfilesync.engine.BasicFile;

public class BasicFileSorter {

  private boolean dirsFirst = true;

  public BasicFileSorter() {
  }

  public BasicFileSorter(final boolean dirsFirst) {
    this.dirsFirst = dirsFirst;
  }

  public void sortByName(List<BasicFile> files) {
    Collections.sort(files, new BasicFileNameComparator());
  }

  class BasicFileNameComparator implements Comparator<BasicFile> {

    public int compare(final BasicFile file1, final BasicFile file2) {
      if (dirsFirst) {
        if (file1.isDirectory() ^ file2.isDirectory()) {
          if (file1.isDirectory()) {
            return -1;
          } else {
            return 1;
          }
        }
      }

      return file1.getFileName().compareTo(
          file2.getFileName());
    }

  }

  public boolean isDirsFirst() {
    return dirsFirst;
  }

  public void setDirsFirst(boolean dirsFirst) {
    this.dirsFirst = dirsFirst;
  }

}
