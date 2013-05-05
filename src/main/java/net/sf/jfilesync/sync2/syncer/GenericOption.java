/*
 * capivara - Java File Synchronization
 *
 * Created on 30-Dec-2005
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
 * $Id$
 */
package net.sf.jfilesync.sync2.syncer;

import java.util.ArrayList;
import java.util.List;

import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.sync2.SyncAction;
import net.sf.jfilesync.sync2.SyncActionDelete;
import net.sf.jfilesync.sync2.SyncDiff;
import net.sf.jfilesync.sync2.SyncFile;
import net.sf.jfilesync.sync2.SyncOption;
import net.sf.jfilesync.sync2.diffs.SyncDiffTree;
import net.sf.jfilesync.sync2.diffs.SyncDiffUnique;


/**
 * Just a class that implements methods that can be shared
 * between the different syncers (time, hash).
 * For example the delete method is shared. So, no redundancy.
 *
 * @author sascha
 *
 */

public abstract class GenericOption implements SyncOption {

  protected SyncAction[] delete(final SyncDiffTree tree) {
    final List<SyncActionDelete> delList = new ArrayList<SyncActionDelete>();
    final SyncDiff[] diffs = tree.getBottomUpList();
    for (int i = 0; i < diffs.length; i++) {
      if (diffs[i] instanceof SyncDiffUnique) {
        SyncFile file = diffs[i].getSyncFile();

        boolean delete = false;

        if (!file.isExcluded()) {
          if (file.getFile().isDirectory()) {
            // check if any file in this dir is excluded
            // if yes, don't delete file
            if (!file.containsExcludedChild()) {
              delete = true;
            }
          } else {
            delete = true;
          }
        }

        if (delete) {
          delList.add(new SyncActionDelete(file));
        }

      }
    }
    return delList.toArray(new SyncAction[delList.size()]);
  }

  protected boolean uniqueAndNotExcluded(final SyncDiffUnique uDiff) {
    boolean ret = false;

    final SyncFile file = uDiff.getSyncFile();
    if( file.isExcluded() ) {
      if( isChildIncluded(file) ) {
        ret = true;
      }
    }
    else {
      ret = true;
    }

    return ret;
  }

  private boolean isChildIncluded(final SyncFile file) {
    boolean ret = false;

    if( file.getFile().isDirectory() ) {
      SyncFile[] childs = file.getChildren();
      for(int i=0; i<childs.length && ret == false; i++) {
        if( ! childs[i].isExcluded() ) {
          ret = true;
          break;
        }
        else {
          ret = isChildIncluded(childs[i]);
        }
      }
    }

    return ret;
  }

  public String getDirectionDescription(Direction direction) {
    String descr = "BUG: unknown description";

    switch(direction) {
    case LEFT_RIGHT:
      descr = LanguageBundle.getInstance().getMessage(
          "sync.option.direction.left_right");
      break;
    case RIGHT_LEFT:
      descr = LanguageBundle.getInstance().getMessage(
      "sync.option.direction.right_left");
      break;
    case BIDIRECTIONAL:
      descr = LanguageBundle.getInstance().getMessage(
      "sync.option.direction.bidirectional");
      break;
    }

    return descr;
  }

}
