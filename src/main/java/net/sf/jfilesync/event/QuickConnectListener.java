/*
 * capivara - Java File Synchronization
 *
 * Created on Feb 28, 2006
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
package net.sf.jfilesync.event;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import net.sf.jfilesync.gui.QuickConnectPopupMenu;

/**
 * 
 * QuickConnectListener gets called when user clicks
 * on connect button in TControlCenter.
 * It shows a list of all save favourites to quickly connect
 * to one of them.
 * 
 * @author sascha
 */
public class QuickConnectListener implements MouseListener {

  private final int controlCenterID;
  
  public QuickConnectListener(int ccID) {
    this.controlCenterID = ccID;
  }
  
  public void mousePressed(MouseEvent e) {
    handleEvent(e);
  }

  public void mouseReleased(MouseEvent e) {
    handleEvent(e);
  }

  private void handleEvent(MouseEvent e) {
    if( e.isPopupTrigger() ) {
      QuickConnectPopupMenu menu = new QuickConnectPopupMenu(controlCenterID);
      menu.show(e.getComponent(), e.getX(), e.getY());
    }
  }

  public void mouseClicked(MouseEvent e) {
  }
  
  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

}
