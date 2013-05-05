/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2004 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TimeSyncOption.java,v 1.15 2006/08/09 22:18:39 hunold Exp $
 */
package net.sf.jfilesync.sync2.syncer;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.sync2.SyncAction;
import net.sf.jfilesync.sync2.SyncActionCopy;
import net.sf.jfilesync.sync2.SyncDiff;
import net.sf.jfilesync.sync2.SyncOption;
import net.sf.jfilesync.sync2.diffs.SyncDiffSubDir;
import net.sf.jfilesync.sync2.diffs.SyncDiffTime;
import net.sf.jfilesync.sync2.diffs.SyncDiffTree;
import net.sf.jfilesync.sync2.diffs.SyncDiffUnique;



public class TimeSyncOption extends GenericOption {

  public enum Method { MAKE_IDENTICAL, ADD_NEWER_FILES, COPY_UNIQUE_FILES };

  private Method method = Method.MAKE_IDENTICAL;

  private static Logger LOGGER = Logger.getLogger(TimeSyncOption.class
      .getName());

  public TimeSyncOption(Method method) {
    this.method = method;
  }

  public String getDescription() {
    String desc;
    switch (method) {
    case MAKE_IDENTICAL:
      desc = LanguageBundle.getInstance().getMessage(
          "sync.option.make_identical");
      break;
    case ADD_NEWER_FILES:
      desc = LanguageBundle.getInstance().getMessage(
          "sync.option.add_newer_files");
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
    SyncChoice choice = new SyncChoice();

    if( method == Method.MAKE_IDENTICAL ) {

      if( direction == Direction.LEFT_RIGHT ) {
        actions = copy(tree, choice);
      } else if( direction == Direction.RIGHT_LEFT ) {
        actions = delete(tree);
      } else {
        LOGGER.warning("unsupported direction : " + direction + " [method "
            + method + "]");
      }

    } else if( method == Method.ADD_NEWER_FILES ) {

      if( direction == Direction.LEFT_RIGHT ) {
        choice.setOption(SyncChoice.NEWER_FILES);
        actions = copy(tree, choice);
      } else if( direction == Direction.RIGHT_LEFT ) {
        // nothing to do
      } else if( direction == Direction.BIDIRECTIONAL ) {
        choice.setOption(SyncChoice.NEWER_FILES);
        actions = copy(tree, choice);
      } else {
        LOGGER.warning("unsupported direction : " + direction + " [method "
            + method + "]");
      }

    } else if( method == Method.COPY_UNIQUE_FILES ) {

      if( direction == Direction.LEFT_RIGHT ) {
        choice.setOption(SyncChoice.UNIQUE_FILES);
        actions = copy(tree, choice);
      } else if( direction == Direction.RIGHT_LEFT ) {
        // nothing to do
      } else if( direction == Direction.BIDIRECTIONAL ) {
        choice.setOption(SyncChoice.UNIQUE_FILES);
        actions = copy(tree, choice);
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
    SyncChoice choice = new SyncChoice();

    if( method == Method.MAKE_IDENTICAL ) {

      if( direction == Direction.LEFT_RIGHT ) {
        actions = delete(tree);
      } else if( direction == Direction.RIGHT_LEFT ) {
        actions = copy(tree, choice);
      } else {
        LOGGER.warning("unsupported direction : " + direction + " [method "
            + method + "]");
      }

    } else if( method == Method.ADD_NEWER_FILES ) {

      if( direction == Direction.LEFT_RIGHT ) {
        // nothing to do
      } else if( direction == Direction.RIGHT_LEFT ) {
        choice.setOption(SyncChoice.NEWER_FILES);
        actions = copy(tree, choice);
      } else if( direction == Direction.BIDIRECTIONAL ) {
        choice.setOption(SyncChoice.NEWER_FILES);
        actions = copy(tree, choice);
      } else {
        LOGGER.warning("unsupported direction : " + direction + " [method "
            + method + "]");
      }

    } else if( method == Method.COPY_UNIQUE_FILES ) {

      if( direction == Direction.LEFT_RIGHT ) {
        // nothing to do
      } else if( direction == Direction.RIGHT_LEFT ) {
        choice.setOption(SyncChoice.UNIQUE_FILES);
        actions = copy(tree, choice);
      } else if( direction == Direction.BIDIRECTIONAL ) {
        choice.setOption(SyncChoice.UNIQUE_FILES);
        actions = copy(tree, choice);
      } else {
        LOGGER.warning("unsupported direction : " + direction + " [method "
            + method + "]");
      }

    } else {
      LOGGER.warning("unknown method : " + method);
    }

    return actions;
  }

  protected SyncAction[] copy(final SyncDiffTree tree, final SyncChoice options) {

    final List<SyncActionCopy> copyList = new ArrayList<SyncActionCopy>();

    final SyncDiff[] diffs = tree.getTopDownList();
    LOGGER.info("diffs.length : " + diffs.length);
    for (int i = 0; i < diffs.length; i++) {

      if (!(diffs[i] instanceof SyncDiffSubDir)) {

        // TODO revise this
        // not very clear even though it's practical

        if (options.isOptionEnabled(SyncChoice.NEWER_FILES)) {
          // update case
          if (diffs[i] instanceof SyncDiffTime
              && ((SyncDiffTime) diffs[i]).isNewer()) {
            copyList.add(new SyncActionCopy(diffs[i].getSyncFile()));
          } else if (diffs[i] instanceof SyncDiffUnique) {
            if (uniqueAndNotExcluded((SyncDiffUnique) diffs[i])) {
              copyList.add(new SyncActionCopy(diffs[i].getSyncFile()));
            }
          }
        } else if (options.isOptionEnabled(SyncChoice.UNIQUE_FILES)) {
          // case for unique files
          if (diffs[i] instanceof SyncDiffUnique) {
            if (uniqueAndNotExcluded((SyncDiffUnique) diffs[i])) {
              copyList.add(new SyncActionCopy(diffs[i].getSyncFile()));
            }
          }
        } else {
          // copy everything
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


  public List<Direction> getSupportedDirections() {
    final List<Direction> dirList = new ArrayList<Direction>();

    switch(method) {
    case MAKE_IDENTICAL:
      dirList.add(SyncOption.Direction.LEFT_RIGHT);
      dirList.add(SyncOption.Direction.RIGHT_LEFT);
      break;
    case ADD_NEWER_FILES:
      dirList.add(SyncOption.Direction.LEFT_RIGHT);
      dirList.add(SyncOption.Direction.RIGHT_LEFT);
      dirList.add(SyncOption.Direction.BIDIRECTIONAL);
      break;
    case COPY_UNIQUE_FILES:
      dirList.add(SyncOption.Direction.LEFT_RIGHT);
      dirList.add(SyncOption.Direction.RIGHT_LEFT);
      dirList.add(SyncOption.Direction.BIDIRECTIONAL);
      break;
    default:
      LOGGER.warning("BUG: unknown method : " + method);
    }

    return dirList;
  }

  ////////////////////////////////////////////////////////////////////////////

  static class SyncChoice {

    private int options = 0;
    public final static int NEWER_FILES  = 0x1;
    public final static int UNIQUE_FILES = 0x2;

    public SyncChoice() {}

    public boolean isOptionEnabled(int option) {
      return (options & option) > 0;
    }

    public void setOption(int option) {
      options |= option;
    }
  }

}
