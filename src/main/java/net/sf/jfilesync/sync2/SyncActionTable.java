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
 * $Id: SyncActionTable.java,v 1.7 2006/08/09 22:18:40 hunold Exp $
 */
package net.sf.jfilesync.sync2;

import java.awt.Dimension;

import javax.swing.JTable;

import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.sync2.SyncActionModel;
import net.sf.jfilesync.sync2.gui.SyncActionFileRenderer;
import net.sf.jfilesync.sync2.gui.SyncActionTypeRenderer;

public class SyncActionTable extends JTable {

  private static final long serialVersionUID = 4049636776307929913L;

  public SyncActionTable(SyncActionModel syncActionModel,
      	                 SyncActionTypeRenderer actionRenderer,
      	                 SyncActionFileRenderer syncFileRenderer) {
    
    super(syncActionModel);
    
    getColumnModel().getColumn(0)
    	.setHeaderValue(LanguageBundle.getInstance().getMessage("sync.table.action"));
    getColumnModel().getColumn(0).setCellRenderer(actionRenderer);
    getColumnModel().getColumn(0).setPreferredWidth(70);
    getColumnModel().getColumn(0).setMaxWidth(70);
    
    getColumnModel().getColumn(1)
		.setHeaderValue(LanguageBundle.getInstance().getMessage("sync.table.file"));    
    getColumnModel().getColumn(1).setCellRenderer(syncFileRenderer);
    getColumnModel().getColumn(1).setPreferredWidth(350);
    
    setPreferredScrollableViewportSize(new Dimension(420,350));
  }
  
}
