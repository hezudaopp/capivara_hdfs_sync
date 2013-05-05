/*
 * capivara - Java File Synchronization
 *
 * Created on 29 Oct 2006
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
package net.sf.jfilesync.sync2.projects;

import java.util.logging.Logger;

import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;
import net.sf.jfilesync.sync2.projects.nodes.SyncProjectNode;
import net.sf.jfilesync.sync2.syncer.SyncSettings;

public class SyncProjectSettings implements ISyncProjectSavable2 {

  private final SyncSettings settings;
  private final AbstractConnectionProxy con1;
  private final AbstractConnectionProxy con2;
  private final String rootPath1;
  private final String rootPath2;

  private final static Logger LOGGER = Logger
      .getLogger(SyncProjectSettings.class.getPackage().getName());

  public SyncProjectSettings(final SyncSettings settings,
      AbstractConnectionProxy con1, String rootPath1,
      AbstractConnectionProxy con2, String rootPath2) {

    this.settings = settings;
    this.con1 = con1;
    this.rootPath1 = rootPath1;
    this.con2 = con2;
    this.rootPath2 = rootPath2;
  }


  public ISyncProjectNode save() {
    final SyncProjectNode saveNode = new SyncProjectNode(settings, con1,
        rootPath1, con2, rootPath2);
    return saveNode;
  }


  public AbstractConnectionProxy getCon1() {
    return con1;
  }


  public AbstractConnectionProxy getCon2() {
    return con2;
  }


  public String getRootPath1() {
    return rootPath1;
  }


  public String getRootPath2() {
    return rootPath2;
  }


  public SyncSettings getSettings() {
    return settings;
  }

}
