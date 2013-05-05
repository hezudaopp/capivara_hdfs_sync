/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003-2007 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
package net.sf.jfilesync.service.unify.action;

import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.jfilesync.gui.icons.TImageIconProvider;

public class UnifyDeleteActionRenderer implements UnifyActionRenderer {

  public JComponent render(UnifyAction action) {
    
    final JLabel lab1 = new JLabel(TImageIconProvider.getInstance().getImageIcon(
        TImageIconProvider.SYNC_ACTION_DELETE));
    final JLabel lab2 = new JLabel(action.getUnifyFile().getAbsolutePath());
    
    final JPanel pane = new JPanel(new FlowLayout(FlowLayout.LEFT));
    pane.add(lab1);
    pane.add(lab2);
    
    return pane;
  }

}
