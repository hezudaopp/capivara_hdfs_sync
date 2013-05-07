/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Sascha Hunold <hunoldinho@users.sourceforge.net>
 * changed by: Raik Nagel <raik.nagel@uni-bayreuth.de>
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
 * $Id: TControlCenterFactory.java,v 1.5 2006/03/05 10:26:21 hunold Exp $
 */

package net.sf.jfilesync.gui;

/**
 * This class helps us to manage ControlCenters.
 * It will keep track of the number of ControlCenters used.
 *
 * @author Sascha Hunold
 */

import javax.swing.JFrame;

import net.sf.jfilesync.event.TControlCenterBus;
import net.sf.jfilesync.plugins.net.ConnectionPluginManager;
import net.sf.jfilesync.plugins.net.TConnectionData;
import net.sf.jfilesync.util.TMiscTool;

public class TControlCenterFactory
{
  private static TControlCenterFactory factory = null;
  private static int ccNumber = 0;
  private JFrame parent;
  private static TControlCenterBus bus = new TControlCenterBus();

  private TControlCenterFactory(JFrame parent) {
    this.parent = parent;
  }

  public static TControlCenterFactory getInstance(JFrame parent) {
    if (factory == null) {
      factory = new TControlCenterFactory(parent);
    }
    return factory;
  }

  public TControlCenter getNextControlCenter() {
    TControlCenter cc = new TControlCenter(parent, ccNumber++);
    bus.addControlCenterBusListener(cc);

    /* Jawinton */
    if (ccNumber == 1)
    {
//      TOutStreamCapture.setMainArea(back.getLoggingArea() );
//      TOutStreamCapture.start(true);
    	// automatically connect to local file system.
    	TConnectionData conData = new TConnectionData(parent, "localhost", -1,
    	        TMiscTool.getUserName(), "",
    	        ConnectionPluginManager.LOCAL_PLUGIN);
    	cc.connect(conData);
    }
    /* Jawinton */
    
    return cc;
  }

  public static TControlCenterBus getControlCenterBus() {
    return bus;
  }
  
}
