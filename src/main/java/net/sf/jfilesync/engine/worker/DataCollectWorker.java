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
 * $Id: DataCollectWorker.java,v 1.16 2006/08/05 15:28:17 hunold Exp $
 */

package net.sf.jfilesync.engine.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import net.sf.gnocchi.GWorker;
import net.sf.gnocchi.GWorkerEvent;
import net.sf.jfilesync.engine.TFileData;
import net.sf.jfilesync.engine.TFileProperties;
import net.sf.jfilesync.engine.worker.events.CollectStatusMessage;
import net.sf.jfilesync.engine.worker.events.TFileListEvent;
import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;


public class DataCollectWorker extends GWorker {

  private static final int
  	JOB_COLLECT_FROM_SELECTION = 0,
  	JOB_COLLECT_FROM_PATH = 1;


  private final AbstractConnectionProxy con;
  private TFileProperties[] startData;
  private String startPath;
  private final boolean recursive;
  private final boolean followSymLinks;
  private final int job;

  private TFileData collection;
  static Logger logger = Logger.getLogger(DataCollectWorker.class.getName());

  private int filesCollected;

  public DataCollectWorker(AbstractConnectionProxy connection,
      String path,
      boolean recursive,
      boolean follow_symlinks) {
    con = connection;
    startPath = path;
    this.recursive = recursive;
    followSymLinks = follow_symlinks;
    job = JOB_COLLECT_FROM_PATH;
  }

  public DataCollectWorker(AbstractConnectionProxy connection,
      TFileProperties[] dataIn,
      boolean recursive,
      boolean follow_symlinks) {
    con = connection;
    startData = dataIn;
    this.recursive = recursive;
    followSymLinks = follow_symlinks;
    job = JOB_COLLECT_FROM_SELECTION;
  }

  public void task() throws Exception {
    collection = new TFileData();
    filesCollected = 0;

    if( job == JOB_COLLECT_FROM_SELECTION) {
      doCollectJobFromSel();
    } else {
      doCollectJobFromPath();
    }
  }

  public GWorkerEvent construct() {
    return new TFileListEvent(this, collection);
  }

  private void doCollectJobFromPath() throws IOException {
    final List<TFileProperties> dirQueue = new ArrayList<TFileProperties>();

    collectFilesInDir(startPath, dirQueue);

    if (recursive && !isCancelled()) {
      collectFilesFromQueue(dirQueue);
    }

    con.chdir(startPath);
  }

  private void doCollectJobFromSel() throws IOException {

    final List<TFileProperties> dirQueue = new ArrayList<TFileProperties>();
    startPath = con.getCurrentPath();

    for(int i=0; i<startData.length; i++) {
      if( startData[i].isLink() && ! followSymLinks ) {
        // don't follow this link
      } else {
        //logger.info("add " + startData[i].getAbsoluteFileName());
        if( recursive && startData[i].isDirectory() ) {
          dirQueue.add(startData[i]);
        }
        collection.addFileProperties(startData[i]);
        filesCollected++;
      }
    }

    if( recursive && !isCancelled() ) {
      collectFilesFromQueue(dirQueue);
    }

    con.chdir(startPath);
  }

  private void collectFilesFromQueue(final List<TFileProperties> queue) throws IOException {

    while (!queue.isEmpty() && !isCancelled()) {
      final TFileProperties nextDir = queue.remove(0);
      if (getObserver() != null) {
        getObserver().updateGUI(
            new CollectStatusMessage(this, nextDir.getAbsoluteFileName(),
                filesCollected));
      }
      collectFilesInDir(nextDir.getAbsoluteFileName(), queue);
    }
  }

  private void collectFilesInDir(String path, List<TFileProperties> dirQueue) throws IOException {
    TFileData dirData = null;

    con.chdir(path);
    dirData = con.ls();

    if (dirData == null) {
      return;
    }

    final Iterator<TFileProperties> it = dirData.getFileProperties().iterator();
    while (it.hasNext()) {
      TFileProperties curProps = it.next();
      if (curProps.isLink() && !followSymLinks) {
        // ignore link of not followSymLinks
      } else {
        collection.addFileProperties(curProps);
        filesCollected++;
        if (curProps.isDirectory()) {
          dirQueue.add(curProps);
        }
      }
    }
  }


}
