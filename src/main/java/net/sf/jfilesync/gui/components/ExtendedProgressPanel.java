/*
 * capivara - Java File Synchronization
 *
 * Created on 23-Jun-2005
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
 * $Id: ExtendedProgressPanel.java,v 1.4 2006/04/02 14:47:42 hunold Exp $
 */
package net.sf.jfilesync.gui.components;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ExtendedProgressPanel extends JPanel {

  private final static int min = 0;
  private final static int max = 100;
  private JProgressBar bar = new JProgressBar(min, max);
  private final int steps;
  
  public ExtendedProgressPanel(final int steps) {
    super(true);
    this.steps = steps;
    setLayout(new GridLayout(1,1));
    setBorder(BorderFactory.createEmptyBorder(2,5,2,5));
    add(bar);
  }
  
  public void setStep(final int step) {
    int progress = (int)((double)step*100.0/(double)steps);
    String msg = step + "/" + steps;
    setProgressBar(progress);
    setMessage(msg);
  }

  protected void setProgressBar(final int pos) {
    int p = pos;
    if( p < min ) {
      p = min;
    }
    else if( p > max ) {
      p = max;
    }
    bar.setValue(p);
  }
  
  protected void setMessage(final String msg) {
    bar.setString(msg);
  }
  
}
