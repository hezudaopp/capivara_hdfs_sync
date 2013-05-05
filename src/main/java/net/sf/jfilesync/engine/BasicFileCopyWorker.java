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
package net.sf.jfilesync.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.gnocchi.GWorker;
import net.sf.gnocchi.GWorkerEvent;
import net.sf.jfilesync.engine.worker.events.TSimpleWorkerDoneEvent;
import net.sf.jfilesync.plugins.GeneralPlugin;
import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;

public class BasicFileCopyWorker extends GWorker {

  private final AbstractConnectionProxy sourceCon;
  private final String sourceRootPath;
  private final String targetRootPath;
  private final BasicFileTree sourceTree;

  private final List<FileCopyListener> copyListeners = new ArrayList<FileCopyListener>();

  public BasicFileCopyWorker(final AbstractConnectionProxy sourceCon,
      final String sourceRootPath, final String targetRootPath,
      final BasicFileTree sourceTree) {
    this.sourceCon = sourceCon;
    this.sourceRootPath = sourceRootPath;
    this.targetRootPath = targetRootPath;
    this.sourceTree = sourceTree;
  }

  public void addFileCopyListener(final FileCopyListener listener) {
    if( listener != null ) {
      copyListeners.add(listener);
    }
  }

  @Override
  public GWorkerEvent construct() {
    return new TSimpleWorkerDoneEvent(this);
  }

  @Override
  public void task() throws Exception {

    final Iterator<BasicFile> fileIterator = this.sourceTree.iterator();

    final List<TFileProperties> fileCopyList = new ArrayList<TFileProperties>();
    while(fileIterator.hasNext()) {
      fileCopyList.add( fileIterator.next().getTFileProperties() );
    }

    final TFileProperties[] filesToCopy = fileCopyList
        .toArray(new TFileProperties[fileCopyList.size()]);

//    DevelopmentMode.suspend(1000);

    final FileTransfer2 transfer = new FileTransfer2(this.sourceCon,
        this.sourceRootPath, this.sourceCon, this.targetRootPath, filesToCopy);
    for (FileCopyListener listener : copyListeners) {
      transfer.addFileCopyListener(listener);
    }
    setTransferOptions(transfer);
    transfer.startCopying();

    if( transfer.wasCancelled() ) {
        cancel();
    }
    
//    DevelopmentMode.suspend(1000);

  }

  private void setTransferOptions(final FileTransfer2 transfer) {

    transfer.addOption(FileTransfer2.OPTION_PRESERVE_MTIME);

    if (this.sourceCon.getPlugin().isProvided(
        GeneralPlugin.PROVIDES_PERMISSION_HANDLING)) {
      transfer.addOption(FileTransfer2.OPTION_PRESERVE_PERMISSIONS);
    }

  }


}
