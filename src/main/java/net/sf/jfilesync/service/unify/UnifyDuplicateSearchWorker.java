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

import java.util.List;
import java.util.logging.Logger;

import net.sf.gnocchi.GWorker;
import net.sf.gnocchi.GWorkerEvent;
import net.sf.jfilesync.engine.BasicFile;
import net.sf.jfilesync.engine.worker.BasicFileTreeEvent;
import net.sf.jfilesync.engine.worker.events.FileProgressWorkerEvent;

public class UnifyDuplicateSearchWorker extends GWorker {

  private final UnifyFileTree fileTree;

  private final static Logger LOGGER = Logger
      .getLogger(UnifyDuplicateSearchWorker.class.getName());

  public UnifyDuplicateSearchWorker(UnifyFileTree fileTree) {
    this.fileTree = fileTree;
  }

  @Override
  public GWorkerEvent construct() {
    return new BasicFileTreeEvent(this, fileTree);
  }

  @Override
  public void task() throws Exception {

    final int fileNum = fileTree.getNumberOfFiles();
    int filesDone = 0;
    final List<BasicFile> fileList = fileTree.getFilesInTree();

    for (BasicFile file : fileList) {

      if (isCancelled()) {
        break;
      }

      if (file.isDirectory()) {
        continue;
      }

      final int perc = filesDone * 100 / fileNum;

      getObserver().updateGUI(
          new FileProgressWorkerEvent(this, file.getAbsolutePath(), perc));

      boolean othersMarkedToKeep = false;
      final UnifyFile uf1 = (UnifyFile) file;

      for (BasicFile file2 : fileList) {
        if (isCancelled()) {
          break;
        }
        if (file == file2) {
          continue;
        }
        if (file2.isDirectory()) {
          continue;
        }

        final UnifyFile uf2 = (UnifyFile) file2;

        //if (uf1.hasSameDigest(uf2.getMessageDigest())) {
        if (uf1.isDuplicateOf(uf2)) {
          LOGGER.info(uf1.getAbsolutePath() + " same hash as "
              + uf2.getAbsolutePath());
          uf1.addDuplicate(uf2);
          if (uf2.getKeepIt()) {
            othersMarkedToKeep = true;
          }
        }
      }

      if (!othersMarkedToKeep) {
        uf1.setKeepIt(true);
      }

      filesDone++;
    }

  }

}
