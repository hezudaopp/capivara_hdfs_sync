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
 */
package net.sf.jfilesync.engine.worker;

import java.awt.Frame;
import java.util.logging.Logger;

import net.sf.gnocchi.GWorker;
import net.sf.gnocchi.GWorkerEvent;
import net.sf.jfilesync.engine.BasicFileTree;
import net.sf.jfilesync.gui.dialog.ConfirmOverrideDialog;
import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;

/**
 * This class is supposed to replace FileTransfer2 one day.
 *
 * @author sascha
 *
 */

public class BasicFileTreeCopyWorker extends GWorker {

  private final Frame owner;
  private final AbstractConnectionProxy sourceCon;
  private final AbstractConnectionProxy targetCon;
  private final String sourceRoot;
  private final String targetRoot;
  private final BasicFileTree fileTree;

//  private boolean checkForIncompleteFile = false;

  public final static int OPTION_PRESERVE_MTIME       = 0x1;
  public final static int OPTION_PRESERVE_PERMISSIONS = 0x2;

  public enum OVERRIDE_MODE {
    CONFIRM, OVERRIDE_ALL, SKIP_ALL
  };

  private ConfirmOverrideDialog cod;
  private OVERRIDE_MODE mode_override = OVERRIDE_MODE.CONFIRM;
  private boolean override = false;

  private final static Logger LOGGER = Logger
      .getLogger(BasicFileTreeCopyWorker.class.getName());

  public BasicFileTreeCopyWorker(Frame owner, AbstractConnectionProxy sourceCon,
      String sourceRoot, AbstractConnectionProxy targetCon, String targetRoot,
      BasicFileTree fileTree) {
    this.owner = owner;
    this.sourceCon = sourceCon;
    this.sourceRoot = sourceRoot;
    this.targetCon = targetCon;
    this.targetRoot = targetRoot;
    this.fileTree = fileTree;
  }

  @Override
  public GWorkerEvent construct() {
    // do I need an event???
    return null;
  }

  @Override
  public void task() throws Exception {




  }


}

