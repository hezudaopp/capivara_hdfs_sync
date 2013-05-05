/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TPathControlFactory.java,v 1.4 2006/06/11 21:00:12 hunold Exp $
 */

package net.sf.jfilesync.util;

import java.io.File;

public class TPathControlFactory {

  public TPathControlFactory() {
  }

  public static TPathControlInterface getPathControlInstance(final String system) {
    TPathControlInterface ret = new TPathUnix();
    if (system.equals("windows")) {
      ret = createWindowsPathControlInstance();
    }
    return ret;
  }

  public static TPathControlInterface getPathControlInstance(final int osID) {
    switch (osID) {
    case TMiscTool.OS_LINUX:
    case TMiscTool.OS_UNIX:
      return new TPathUnix();
    case TMiscTool.OS_WINDOWS:
      return createWindowsPathControlInstance();
    default:
      return new TPathUnix();
    }
  }
  
  /**
   * 
   * @return Path control object for local system 
   */
  public static TPathControlInterface getLocalPathControlInstance() {
    return getPathControlInstance(TMiscTool.getOSId());
  }
  
  protected static TPathWindows createWindowsPathControlInstance() {
    final File[] rootFiles = File.listRoots();
    final String[] roots = new String[rootFiles.length];
    for (int i = 0; i < roots.length; i++)
      roots[i] = rootFiles[i].getAbsolutePath();
    final String fileSep = System.getProperty("file.separator");
    
    return new TPathWindows(roots, fileSep);
  }

}
