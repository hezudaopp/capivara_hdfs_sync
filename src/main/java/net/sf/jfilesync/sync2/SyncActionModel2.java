/*
 * capivara - Java File Synchronization
 *
 * Created on 04-May-2006
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
package net.sf.jfilesync.sync2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class SyncActionModel2 extends AbstractTableModel {

  private static final long serialVersionUID = 3906652994321003060L;
  private final MergedActionList mergedActions;

  public SyncActionModel2(final SyncAction[] leftActions,
      final SyncAction[] rightActions) {
    mergedActions = new MergedActionList(leftActions, rightActions);
  }

  public int getRowCount() {
    return mergedActions.getRowCount();
  }

  public int getColumnCount() {
    return 4;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    //return actionList[rowIndex];
//    System.out.println("value at " + rowIndex + "," + columnIndex + " "
//        + mergedActions.getValueAt(rowIndex, columnIndex));
    return mergedActions.getValueAt(rowIndex, columnIndex);
  }

  static class MergedActionList {

    private final List<SyncAction> actionList = new ArrayList<SyncAction>();
    private final Integer LEFT_ACTION = new Integer(0);
    private final Integer RIGHT_ACTION = new Integer(1);
    private final HashMap<SyncAction, Integer> actionMap = new HashMap<SyncAction, Integer>();

    public MergedActionList(final SyncAction[] leftActions,
        final SyncAction[] rightActions) {

      for (int i = 0; i < leftActions.length; i++) {
        actionList.add(leftActions[i]);
        actionMap.put(leftActions[i], LEFT_ACTION);
      }
      for (int i = 0; i < rightActions.length; i++) {
        actionList.add(rightActions[i]);
        actionMap.put(rightActions[i], RIGHT_ACTION);
      }

      Collections.sort(actionList, new SyncActionFileNameComparator());
    }

    public SyncAction getValueAt(int rowIdx, int colIdx) {

      // colIdx 0 and 1 for leftActions
      // colIdx 2 and 3 for rightActions

      if (rowIdx >= actionList.size() && colIdx >= 4) {
        return null;
      }

      final SyncAction action = actionList.get(rowIdx);
      SyncAction returnVal = null;
      if (colIdx < 2) {
        // must be a leftAction
        // otherwise return null
        if (actionMap.get(action).equals(LEFT_ACTION)) {
          returnVal = action;
        }
      } else {
        // vice versa
        if (actionMap.get(action).equals(RIGHT_ACTION)) {
          returnVal = action;
        }
      }

      return returnVal;
    }

    public int getRowCount() {
      return actionList.size();
    }

  }

  static class SyncActionFileNameComparator implements Comparator<SyncAction> {

    public int compare(final SyncAction a1, final SyncAction a2) {
      return a1.getSyncFile().getRelativePath().compareTo(
          a2.getSyncFile().getRelativePath());
    }

  }

}
