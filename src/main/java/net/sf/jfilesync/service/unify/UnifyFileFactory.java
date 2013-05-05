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

import net.sf.jfilesync.engine.AbstractFileFactory;
import net.sf.jfilesync.engine.BasicFile;
import net.sf.jfilesync.engine.BasicFileTree;
import net.sf.jfilesync.engine.TFileProperties;
import net.sf.jfilesync.util.TPathControlInterface;

public class UnifyFileFactory extends AbstractFileFactory {

  private final String rootPath;
  private final TPathControlInterface pathControl;

  public UnifyFileFactory(final String rootPath,
      final TPathControlInterface pathControl) {
    if( rootPath == null ) {
      throw new IllegalArgumentException("rootPath must not be null");
    }
    if( pathControl == null ) {
      throw new IllegalArgumentException("pathControl must not be null");
    }
    this.rootPath = rootPath;
    this.pathControl = pathControl;
  }

  @Override
  public BasicFile createBasicFile(TFileProperties file) {
    return new UnifyFile(file, rootPath, pathControl);
  }

  @Override
  public BasicFileTree createBasicFileTree(BasicFile file) {
    return new UnifyFileTree((UnifyFile)file);
  }

}
