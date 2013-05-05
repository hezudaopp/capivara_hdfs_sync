/*
 * capivara - Java File Synchronization
 *
 * Created on 29-Jun-2005
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
 * $Id: HashSyncOption.java,v 1.5 2006/08/09 22:18:39 hunold Exp $
 */
package net.sf.jfilesync.sync2.syncer;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.sync2.SyncAction;
import net.sf.jfilesync.sync2.SyncActionCopy;
import net.sf.jfilesync.sync2.SyncDiff;
import net.sf.jfilesync.sync2.diffs.SyncDiffSubDir;
import net.sf.jfilesync.sync2.diffs.SyncDiffTree;
import net.sf.jfilesync.sync2.diffs.SyncDiffUnique;

public class HashSyncOption extends GenericOption {

  public enum Method { MAKE_IDENTICAL, COPY_UNIQUE_FILES };

  private final Method method;
  private final static Logger LOGGER = Logger.getLogger(HashSyncOption.class
      .getName());

  public HashSyncOption(final Method method) {
    this.method = method;
  }

  public String getDescription() {
    String desc;
    switch (method) {
    case MAKE_IDENTICAL:
      desc = LanguageBundle.getInstance().getMessage(
          "sync.option.make_identical");
      break;
    case COPY_UNIQUE_FILES:
      desc = LanguageBundle.getInstance().getMessage(
          "sync.option.copy_unique");
      break;
    default:
      desc = "BUG: unknown option";
    }
    return desc;
  }

  public SyncAction[] getLeftActionList(SyncDiffTree tree, Direction direction) {
    SyncAction[] actions = new SyncAction[0];

    if( method == Method.MAKE_IDENTICAL ) {

      if( direction == Direction.LEFT_RIGHT ) {
        actions = copy(tree, false);
      } else if( direction == Direction.RIGHT_LEFT ) {
        actions = delete(tree);
      } else {
        LOGGER.warning("unsupported direction : " + direction + " [method "
            + method + "]");
      }

    } else if( method == Method.COPY_UNIQUE_FILES ) {

      if( direction == Direction.LEFT_RIGHT ) {
        actions = copy(tree, true);
      } else if( direction == Direction.RIGHT_LEFT ) {
        // nothing to do
      } else if( direction == Direction.BIDIRECTIONAL ) {
        actions = copy(tree, true);
      } else {
        LOGGER.warning("unsupported direction : " + direction + " [method "
            + method + "]");
      }

    } else {
      LOGGER.warning("unknown method : " + method);
    }

    return actions;
  }

  public SyncAction[] getRightActionList(SyncDiffTree tree, Direction direction) {
    SyncAction[] actions = new SyncAction[0];


    if( method == Method.MAKE_IDENTICAL ) {

      if( direction == Direction.LEFT_RIGHT ) {
        actions = delete(tree);
      } else if( direction == Direction.RIGHT_LEFT ) {
        actions = copy(tree, false);
      } else {
        LOGGER.warning("unsupported direction : " + direction + " [method "
            + method + "]");
      }

    } else if( method == Method.COPY_UNIQUE_FILES ) {

      if( direction == Direction.LEFT_RIGHT ) {
        // nothing to do
      } else if( direction == Direction.RIGHT_LEFT ) {
        actions = copy(tree, true);
      } else if( direction == Direction.BIDIRECTIONAL ) {
        actions = copy(tree, true);
      } else {
        LOGGER.warning("unsupported direction : " + direction + " [method "
            + method + "]");
      }

    } else {
      LOGGER.warning("unknown method : " + method);
    }

    return actions;
  }

  public List<Direction> getSupportedDirections() {
    final List<Direction> dirList = new ArrayList<Direction>();

    switch(method) {
    case MAKE_IDENTICAL:
      dirList.add(Direction.LEFT_RIGHT);
      dirList.add(Direction.RIGHT_LEFT);
      break;
    case COPY_UNIQUE_FILES:
      dirList.add(Direction.LEFT_RIGHT);
      dirList.add(Direction.RIGHT_LEFT);
      dirList.add(Direction.BIDIRECTIONAL);
      break;
    default:
      LOGGER.warning("BUG: unknown method : " + method);
    }

    return dirList;
  }


  protected SyncAction[] copy(final SyncDiffTree tree, final boolean onlyUnique) {

    final List<SyncActionCopy> copyList = new ArrayList<SyncActionCopy>();

    SyncDiff[] diffs = tree.getTopDownList();

    LOGGER.info("diffs.length : " + diffs.length);

    for (int i = 0; i < diffs.length; i++) {

      if (!(diffs[i] instanceof SyncDiffSubDir)) {

        if (onlyUnique) {
          if (diffs[i] instanceof SyncDiffUnique) {
            if (uniqueAndNotExcluded((SyncDiffUnique) diffs[i])) {
              copyList.add(new SyncActionCopy(diffs[i].getSyncFile()));
            }
          }
        } else {

          if (diffs[i] instanceof SyncDiffUnique) {
            if (uniqueAndNotExcluded((SyncDiffUnique) diffs[i])) {
              copyList.add(new SyncActionCopy(diffs[i].getSyncFile()));
            }
          } else {
            copyList.add(new SyncActionCopy(diffs[i].getSyncFile()));
          }
        }

      }

    }

    return copyList.toArray(new SyncAction[copyList.size()]);
  }

}
