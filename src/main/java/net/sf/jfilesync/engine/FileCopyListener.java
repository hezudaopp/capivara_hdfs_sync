/*
 * capivara - Java File Synchronization
 *
 * Created on 20-Aug-2005
 * Copyright (C) 2005 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: FileCopyListener.java,v 1.2 2005/09/23 21:55:46 hunold Exp $
 */
package net.sf.jfilesync.engine;

/**
 * The class that implements this interface will be notified
 * any time the FileTransfer2 starts and finishes the transfer
 * of one file.
 * 
 * @author sascha
 * @see net.sf.jfilesync.sync2.SyncPerformer2
 * @see net.sf.jfilesync.engine.FileTransfer2
 */
public interface FileCopyListener {

  public void startCopying(String filename);
  
  public void finishCopying(String filename);
  
}
