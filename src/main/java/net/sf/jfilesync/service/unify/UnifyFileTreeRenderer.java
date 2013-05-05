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

import javax.swing.JLabel;

import net.sf.jfilesync.engine.BasicFile;
import net.sf.jfilesync.gui.viewmodel.BasicFileTreeRenderer;

public class UnifyFileTreeRenderer extends BasicFileTreeRenderer {

  private final UnifyFileComponentRenderer componentRenderer = new UnifyFileComponentRenderer();

  public UnifyFileTreeRenderer() {

  }

  @Override
  public void doRenderJLabel(BasicFile file, JLabel label, boolean selected) {
    if (!(file instanceof UnifyFile)) {
      throw new IllegalArgumentException("file must be of type UnifyFile");
    }

    final UnifyFile uf = (UnifyFile) file;
    componentRenderer.renderUnifyFile(label, uf);
  }

}
