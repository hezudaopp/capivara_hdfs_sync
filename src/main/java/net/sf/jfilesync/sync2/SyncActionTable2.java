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

import java.awt.Dimension;

import javax.swing.JTable;

import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.sync2.gui.SyncActionFileRenderer2;
import net.sf.jfilesync.sync2.gui.SyncActionTypeRenderer2;

public class SyncActionTable2 extends JTable {

  private static final long serialVersionUID = 4049636776307929913L;

  public SyncActionTable2(SyncActionModel2 syncActionModel) {

    super(syncActionModel);
        
    setRowHeight(20);
    
    getColumnModel().getColumn(0).setHeaderValue(
        LanguageBundle.getInstance().getMessage("sync.table.file"));
    getColumnModel().getColumn(0).setCellRenderer(new SyncActionFileRenderer2());
    getColumnModel().getColumn(0).setPreferredWidth(350);

    getColumnModel().getColumn(1).setHeaderValue(
        LanguageBundle.getInstance().getMessage("sync.table.action"));
    getColumnModel().getColumn(1).setCellRenderer(
        new SyncActionTypeRenderer2(
            SyncActionTypeRenderer2.LEFT_ACTION_RENDERER));
    getColumnModel().getColumn(1).setPreferredWidth(70);
    getColumnModel().getColumn(1).setMaxWidth(70);

    getColumnModel().getColumn(2).setHeaderValue(
        LanguageBundle.getInstance().getMessage("sync.table.action"));
    getColumnModel().getColumn(2).setCellRenderer(
        new SyncActionTypeRenderer2(
            SyncActionTypeRenderer2.RIGHT_ACTION_RENDERER));
    getColumnModel().getColumn(2).setPreferredWidth(70);
    getColumnModel().getColumn(2).setMaxWidth(70);

    getColumnModel().getColumn(3).setHeaderValue(
        LanguageBundle.getInstance().getMessage("sync.table.file"));
    getColumnModel().getColumn(3).setCellRenderer(new SyncActionFileRenderer2());
    getColumnModel().getColumn(3).setPreferredWidth(350);

    getTableHeader().setReorderingAllowed(false);

    setPreferredScrollableViewportSize(new Dimension(840, 350));
  }
  
}
