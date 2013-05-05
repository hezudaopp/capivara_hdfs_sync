/*
 * capivara - Java File Synchronization
 *
 * Created on 03-Mar-2006
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
package net.sf.jfilesync.sync2.syncer;

import java.util.logging.Logger;

import net.sf.jfilesync.sync2.ISyncDataValidator;
import net.sf.jfilesync.sync2.SyncDataValidator;
import net.sf.jfilesync.sync2.SyncFileSorter;
import net.sf.jfilesync.sync2.list.Expression;
import net.sf.jfilesync.sync2.list.ExpressionList;
import net.sf.jfilesync.sync2.projects.ISyncProjectNode;
import net.sf.jfilesync.sync2.projects.ISyncProjectSavable2;
import net.sf.jfilesync.sync2.projects.nodes.SyncSettingsNode;

public class SyncSettings implements ISyncProjectSavable2 {

  private boolean leftFatMode = false;
  private boolean rightFatMode = false;
  private boolean caseSensitive = true;

  private ExpressionList includeList;
  private ExpressionList excludeList;

  private static int lastSyncID = 0;
  private final int syncID;

  private int modifyWindow = 0;

  private final static Logger LOGGER = Logger.getLogger(SyncSettings.class
      .getPackage().getName());

  public SyncSettings() {
    this.syncID = lastSyncID++;
  }

  public int getSyncID() {
    return syncID;
  }

  public void setLeftFatModeEnabled(final boolean enabled) {
    this.leftFatMode = enabled;
  }

  public boolean getLeftFatModeEnabled() {
    return leftFatMode;
  }

  public void setRightFatModeEnabled(final boolean enabled) {
    this.rightFatMode = enabled;
  }

  public boolean getRightFatModeEnabled() {
    return rightFatMode;
  }

  public boolean isCaseSensitive() {
    return caseSensitive;
  }

  public void setCaseSensitive(final boolean sensitive) {
    this.caseSensitive = sensitive;
  }

  public AbstractSyncPathComparator getAbstractSyncPathComparator() {
    if( isCaseSensitive() ) {
      return new CommonSyncPathComparator();
    } else {
      return new CaseInsensitiveSyncPathComparator();
    }
  }

  public ISyncFileSorter getISyncFileSorter() {
    SyncFileSorter sorter = new SyncFileSorter();
    sorter.enableCaseInsensitive(!isCaseSensitive());
    return sorter;
  }

  public ISyncDataValidator getISyncDataValidator() {

    SyncDataValidator validator = new SyncDataValidator();
    validator.enableCaseInsensitive( !isCaseSensitive() );

    return validator;
  }


  public ExpressionList getExcludeList() {
    return excludeList;
  }

  public void setExcludeList(ExpressionList excludeList) {
    this.excludeList = excludeList;
    for(int i=0; i<excludeList.size(); i++) {
      Expression exp = excludeList.get(i);
      LOGGER.fine("exclude exp : " + exp.getExpressionString());
    }
  }

  public ExpressionList getIncludeList() {
    return includeList;
  }

  public void setIncludeList(ExpressionList includeList) {
    this.includeList = includeList;
  }

  public ISyncProjectNode save() {
    SyncSettingsNode node = new SyncSettingsNode();

    node.setIsFatLeft(leftFatMode);
    node.setIsFatRight(rightFatMode);
    node.setIsCaseSensitive(caseSensitive);
    node.setIncludeList(includeList);
    node.setExcludeList(excludeList);

    return node;
  }

  /**
   * @return the modifyWindow
   */
  public int getModifyWindow() {
    return modifyWindow;
  }

  /**
   * @param modifyWindow the modifyWindow to set
   */
  public void setModifyWindow(int modifyWindow) {
    this.modifyWindow = modifyWindow;
  }


}
