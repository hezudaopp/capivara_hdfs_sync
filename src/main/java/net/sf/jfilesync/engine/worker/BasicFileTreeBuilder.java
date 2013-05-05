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
package net.sf.jfilesync.engine.worker;

import java.util.ArrayList;
import java.util.List;

import net.sf.gnocchi.GWorker;
import net.sf.gnocchi.GWorkerEvent;
import net.sf.gnocchi.GWorkerObserver;
import net.sf.jfilesync.engine.AbstractFileFactory;
import net.sf.jfilesync.engine.BasicFile;
import net.sf.jfilesync.engine.TFileData;
import net.sf.jfilesync.engine.TFileProperties;
import net.sf.jfilesync.engine.worker.events.CollectStatusMessage;
import net.sf.jfilesync.engine.worker.events.TFileListEvent;
import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;

public class BasicFileTreeBuilder extends GWorker {

  private final AbstractFileFactory factory;
  private final AbstractConnectionProxy con;
  private final String rootPath;
  private BasicFile rootDummy;

  private final GWorkerObserver observer = new GWorkerObserver();

//  private static Logger LOGGER = Logger.getLogger(BasicFileTreeBuilder.class
//      .getName());

  public BasicFileTreeBuilder(AbstractConnectionProxy con, String path,
      AbstractFileFactory factory) {
    this.con = con;
    this.rootPath = path;
    this.factory = factory;
    this.rootDummy = factory.createDummyRoot(rootPath);
  }

  @Override
  public GWorkerEvent construct() {
    return new BasicFileTreeEvent(this, factory.createBasicFileTree(rootDummy));
  }

  @Override
  public void task() throws Exception {

    final List<BasicFile> dirQueue = new ArrayList<BasicFile>();
    dirQueue.add(rootDummy);

    int filesCollected = 0;

    while (!dirQueue.isEmpty() && !isCancelled()) {

      final BasicFile curDir = dirQueue.remove(0);
      final String path = curDir.getAbsolutePath();

      getObserver().updateGUI(
          new CollectStatusMessage(this, path, filesCollected));

      GWorker worker = new DataCollectWorker(con, path, false, false);
      worker.addObserver(observer);
      observer.executeAndWait(worker);

      int state = worker.getWorkerState();
      if (state == GWorker.STATE_DONE) {
        final TFileListEvent res = (TFileListEvent) worker.construct();
        TFileData data = res.getFileData();

        filesCollected += data.getNumberofFiles();

        for (int i = 0; i < data.getNumberofFiles(); i++) {

          final TFileProperties file = data.getFileProperties(i);
          final BasicFile absFile = factory.createBasicFile(file);
          curDir.addChild(absFile);

          if (file.isDirectory()) {
            dirQueue.add(0, absFile);
          }
        }
      } else {
        // error in sub-worker
        throw worker.getException();
      }
    }

  }

}
