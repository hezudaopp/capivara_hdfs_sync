/*
 * capivara - Java File Synchronization
 *
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
package net.sf.jfilesync.service.unify;

import java.security.MessageDigest;
import java.util.logging.Logger;

import net.sf.gnocchi.GWorker;
import net.sf.gnocchi.GWorkerEvent;
import net.sf.jfilesync.engine.BasicFile;
import net.sf.jfilesync.engine.worker.BasicFileTreeEvent;
import net.sf.jfilesync.engine.worker.events.FileProgressWorkerEvent;
import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;
import net.sf.jfilesync.util.MessageDigestor;

public class UnifyDigestWorker extends GWorker {

  private final UnifyFileTree fileTree;
  private final AbstractConnectionProxy con;

  private final static Logger LOGGER = Logger.getLogger(UnifyDigestWorker.class
      .getName());

  public UnifyDigestWorker(AbstractConnectionProxy con, UnifyFileTree fileTree) {
    this.con = con;
    this.fileTree = fileTree;
  }

  @Override
  public GWorkerEvent construct() {
    return new BasicFileTreeEvent(this, fileTree);
  }

  @Override
  public void task() throws Exception {

    final MessageDigestor digestor = new MessageDigestor("MD5");
    final int fileNum = fileTree.getNumberOfFiles();
    int filesDone = 0;

    LOGGER.info("start digesting");

    for (BasicFile file : fileTree) {

      if (isCancelled()) {
        break;
      }

      if (file.isDirectory()) {
        continue;
      }

      if (!(file instanceof UnifyFile)) {
        continue;
      }

      final UnifyFile uFile = (UnifyFile) file;

      final int perc = filesDone*100 / fileNum;
      getObserver().updateGUI(
          new FileProgressWorkerEvent(this, uFile.getAbsolutePath(), perc));

      //LOGGER.info("digest " + uFile.getAbsolutePath());

      final MessageDigest digest = digestor.digest(con, uFile.getAbsolutePath());
      if (digest != null) {
        uFile.setMessageDigest(digest.digest());
      }

      filesDone++;

    } // end for

  }

}
