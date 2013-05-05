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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CapivaraFileOutputStream extends FileOutputStream implements CopyStatusStream {

  private long bytesWritten = 0;

  public CapivaraFileOutputStream(File file, boolean append) throws FileNotFoundException {
    super(file, append);
  }

  public CapivaraFileOutputStream(File file) throws FileNotFoundException {
    super(file);
  }

  public CapivaraFileOutputStream(FileDescriptor fdObj) {
    super(fdObj);
  }

  public CapivaraFileOutputStream(String name, boolean append) throws FileNotFoundException {
    super(name, append);
  }

  public CapivaraFileOutputStream(String name) throws FileNotFoundException {
    super(name);
  }

  public long getBytesDone() throws IOException {
    return bytesWritten;
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    bytesWritten += len;
    super.write(b, off, len);
  }

  @Override
  public void write(byte[] b) throws IOException {
    if (b != null) {
      bytesWritten += b.length;
    }
    super.write(b);
  }

  @Override
  public void write(int b) throws IOException {
    bytesWritten++;
    super.write(b);
  }

}

