/*
 * capivara - Java File Synchronization
 *
 * Created on 10-Jun-2005
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
 * $Id: ProgressPanel.java,v 1.4 2005/08/19 21:29:01 hunold Exp $
 */
package net.sf.jfilesync.gui.components;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressPanel extends JPanel {

  private final static int min = 0;
  private final static int max = 100;
  private JProgressBar bar = new JProgressBar(min, max);
  
  public ProgressPanel() {
    super(true);
    
    setLayout(new GridLayout(1,1));
    setBorder(BorderFactory.createEmptyBorder(2,5,2,5));

    add(bar);
  }
  
  public void setProgress(final int progress) {
    int p = progress;
    if( progress < min ) {
      p = min;
    }
    if( progress > max ) {
      p = max;
    }
    bar.setValue(p);
  }
  
}
