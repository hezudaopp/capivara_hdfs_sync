/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Raik Nagel <raik.nagel@uni-bayreuth.de>
 * changed by: Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TMiscTool.java,v 1.6 2006/08/11 23:02:35 hunold Exp $
 */

package net.sf.jfilesync.util;

/**
 * a collection of generic, more or less useful routines
 */

import java.net.InetAddress;

import javax.swing.UIManager;

public class TMiscTool {
  
  public final static int
      OS_UNKNOWN = 1,
      OS_WINDOWS = 10,
      OS_LINUX   = 11,
      OS_SUNOS   = 12,
      OS_UNIX    = 13;

  /**
   * returns the type of Operating System
   */
  public final static int getOSId() {
    int osID = OS_UNKNOWN;
    
    final String osName = getOSName();
    if (osName.equals("SunOS")) {
      osID = OS_SUNOS;
    } else if (osName.equals("Linux")) {
      osID = OS_LINUX;
    } else if (osName.startsWith("Windows")) {
      osID = OS_WINDOWS;
    }

    return osID;
  }

  public final static String  getOSName() {
    return System.getProperty("os.name");
  }

/* -------------------------------------------------------------------------- */
  public final static String getUserName() {
    String defaultBack = "anonymous";
    
    defaultBack = System.getProperties()
    .getProperty("user.name", defaultBack);
    return defaultBack;
  }

/* -------------------------------------------------------------------------- */
  public final static String getLocalHostName() {
    String localhost = "localhost";
    InetAddress ia = null;
    try {
      ia = InetAddress.getLocalHost();
      if( ia != null ) {
        localhost = ia.getHostName();
      }
    } catch ( Exception e) {};
        
    return localhost;
  }

/* -------------------------------------------------------------------------- */
  public final static String getLocalHostIP() {
    InetAddress ia = null;
    try {
      ia = InetAddress.getLocalHost();
    } catch ( Exception e) {};

    if (ia != null)
       return ia.getHostAddress();

    return "localhost"; // ? or 0.0.0.0 or loopback
  }
  
  public final static boolean isGtkLookAndFeel() {
    return UIManager.getLookAndFeel().getClass().getName().equals(
        "com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
  }

}
