/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2004  Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TGetFileListWorker.java,v 1.6 2006/08/02 20:25:46 hunold Exp $
 */

package net.sf.jfilesync.engine.worker;

import net.sf.gnocchi.GWorker;
import net.sf.gnocchi.GWorkerEvent;
import net.sf.jfilesync.engine.TFileData;
import net.sf.jfilesync.engine.worker.events.*;
import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;


public class TGetFileListWorker extends GWorker {

  private final AbstractConnectionProxy con;
  private final String path;
  private TFileData data = null;
    
  public TGetFileListWorker(final AbstractConnectionProxy con, final String path) {
    this.con = con;
    this.path = path;
  }

  public void task() throws Exception {
    data = con.ls(path);
  }

  public GWorkerEvent construct() {
    return new TFileListEvent(this, data);
  }

}
