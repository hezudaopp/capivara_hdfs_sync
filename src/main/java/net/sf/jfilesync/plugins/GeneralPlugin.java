/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Raik Nagel <raik.nagel@uni-bayreuth.de>
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
 * $Id: GeneralPlugin.java,v 1.6 2006/08/06 20:49:28 hunold Exp $
 */

package net.sf.jfilesync.plugins;

public interface GeneralPlugin {
  
  // feature constants
  int PROVIDES_FILETRANSFER       = 1,
      PROVIDES_SHELL               = 2,
      PROVIDES_RSYNC               = 3,
      PROVIDES_PERMISSION_HANDLING = 4,
      PROVIDES_SYMLINK_HANDLING    = 5;

  /**
   * plug-in must provide information if a feature is provided or not
   */ 
  public boolean isProvided(int feature);

  /**
   * @return returns name of plug-in
   */
  public String getName();  

  /**
   * 
   * @return returns a more detailed description of the plug-in
   */
  public String getDescription();

  // public String getVersionString();

  public int getMajorVersion();

  public int getMinorVersion();
  
  public String getLicense();

}
