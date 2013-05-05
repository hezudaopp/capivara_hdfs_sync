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
 * $Id: SyncActionCopy.java,v 1.8 2006/08/09 22:18:40 hunold Exp $
 */
package net.sf.jfilesync.sync2;

import javax.swing.JComponent;
import javax.swing.JLabel;

import net.sf.jfilesync.gui.icons.TImageIconProvider;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.sync2.gui.SyncColorSet;

public class SyncActionCopy extends SyncAction {

  public SyncActionCopy(SyncFile file) {
    super(file);
  }
  
  public String getActionName() {
    return LanguageBundle.getInstance().getMessage("sync.action.names.copy");
  }

  public void renderFileName(JLabel label) {
    label.setForeground(
        SyncColorSet.getColor(SyncColorSet.COLOR_ACTION_COPY));
  }

  public String getLeftActionName() {
    return "->";
  }

  public String getRightActionName() {
    return "<-";
  }

  public JComponent renderLeftAction() {
    return new JLabel(TImageIconProvider.getInstance().getImageIcon(
        TImageIconProvider.SYNC_ACTION_COPY_RIGHT));
  }

  public JComponent renderRightAction() {
    return new JLabel(TImageIconProvider.getInstance().getImageIcon(
        TImageIconProvider.SYNC_ACTION_COPY_LEFT));
  }


}
