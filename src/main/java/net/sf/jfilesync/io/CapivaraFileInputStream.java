/*
 * capivara - Java File Synchronization
 *
 * Created on 16 Mar 2008
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
 */
package net.sf.jfilesync.io;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CapivaraFileInputStream extends FileInputStream implements CopyStatusStream {

  private long bytesRead = 0;

  public CapivaraFileInputStream(File file) throws FileNotFoundException {
    super(file);
  }

  public CapivaraFileInputStream(FileDescriptor fdObj) {
    super(fdObj);
  }

  public CapivaraFileInputStream(String name) throws FileNotFoundException {
    super(name);
  }

  @Override
  public int read() throws IOException {
    int read = super.read();
    if (read > 0) {
      bytesRead += read;
    }
    return read;
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    int read = super.read(b, off, len);
    if (read > 0) {
      bytesRead += read;
    }
    return read;
  }

  @Override
  public int read(byte[] b) throws IOException {
    int read = super.read(b);
    if (read > 0) {
      bytesRead += read;
    }
    return read;
  }

  public long getBytesDone() throws IOException {
    return bytesRead;
  }

}
