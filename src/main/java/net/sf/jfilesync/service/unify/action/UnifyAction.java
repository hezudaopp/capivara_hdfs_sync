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
 * $Id$
 */
package net.sf.jfilesync.service.unify.action;

import java.awt.Component;

import net.sf.jfilesync.service.unify.UnifyFile;

public abstract class UnifyAction {

  private final UnifyFile file;

  public UnifyAction(final UnifyFile file) {
    if( file == null ) {
      throw new IllegalArgumentException("file must not be null");
    }
    this.file = file;
  }

  public UnifyFile getUnifyFile() {
    return file;
  }

//  public abstract Component getActionComponent();


}
