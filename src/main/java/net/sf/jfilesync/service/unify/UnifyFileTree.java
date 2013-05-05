/*
 * capivara - Java File Synchronization
 *
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
package net.sf.jfilesync.service.unify;

import java.util.ArrayList;
import java.util.List;

import net.sf.jfilesync.engine.BasicFile;
import net.sf.jfilesync.engine.BasicFileTree;

public class UnifyFileTree extends BasicFileTree {

  public UnifyFileTree(UnifyFile root) {
    super(root);
    root.setKeepIt(true);
  }

  public List<UnifyFile> getDuplicateList() {
    final List<UnifyFile> dupList = new ArrayList<UnifyFile>();
    collectDuplicates(dupList, (UnifyFile)getRoot());
    return dupList;
  }

  private void collectDuplicates(final List<UnifyFile> dupList,
      final UnifyFile file) {
    if( file.isDirectory() ) {
      for( BasicFile child : file.getChildren() ) {
        collectDuplicates(dupList, (UnifyFile)child);
      }
    } else {
      if( file.getKeepIt() == false ) {
        dupList.add(file);
      }
    }
  }

}
