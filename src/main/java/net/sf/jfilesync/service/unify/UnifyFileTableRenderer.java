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
package net.sf.jfilesync.service.unify;

import java.awt.Component;
import java.awt.PopupMenu;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;
import javax.swing.ToolTipManager;

import net.sf.jfilesync.engine.BasicFile;
import net.sf.jfilesync.gui.viewmodel.BasicFileTableColumModel;
import net.sf.jfilesync.gui.viewmodel.BasicFileTableRenderer;

public class UnifyFileTableRenderer extends BasicFileTableRenderer {

  private final UnifyFileComponentRenderer componentRenderer = new UnifyFileComponentRenderer();

  public UnifyFileTableRenderer(BasicFileTableColumModel columnModel) {
    super(columnModel);
  }

  @Override
  public Component getRenderedFileName(BasicFile file, boolean isSelected) {
    final Component c = super.getRenderedFileName(file, isSelected);

    if( ! (file instanceof UnifyFile) ){
      throw new IllegalArgumentException("file of wrong type");
    }

    if( c instanceof JLabel ) {
      final JLabel cLabel = (JLabel)c;
      final UnifyFile uf = (UnifyFile)file;
      if( uf.hasDuplicates() ) {
        final StringBuffer dupBuffer = new StringBuffer();
        dupBuffer.append("<html>");
        for(UnifyFile dupFile : uf.getDuplicates()) {
          dupBuffer.append("<tt>");
          dupBuffer.append(dupFile.getRelativePathToRoot());
          dupBuffer.append("</tt>");
          dupBuffer.append("<br />");
        }
        dupBuffer.append("</html>");
        cLabel.setToolTipText(dupBuffer.toString());
      }
    }

    componentRenderer.renderUnifyFile(c, (UnifyFile)file);

    return c;
  }



}
