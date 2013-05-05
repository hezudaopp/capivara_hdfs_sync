/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2004 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TPathTextField.java,v 1.4 2005/08/19 21:29:01 hunold Exp $
 */
package net.sf.jfilesync.gui.components;

import java.awt.*;
import javax.swing.JTextField;

/**
 * @author sascha
 * 
 * just a test class
 * this class may be used to customize strings on JLabels
 * for TextFields it may seem useless
 * 
 */
public class TPathTextField extends JTextField {
  private static final long serialVersionUID = 1L;
  private int max_length = 15;
  private FontMetrics fm;
  
  public TPathTextField(int length) {
    super();
    this.max_length = length;
    this.setColumns(max_length);
  }

  public TPathTextField(int length, Font font) {
    super();
    this.max_length = length;
    this.setColumns(max_length);
    this.setFont(font);
  }
  
  public void setText(String text) {
    if( this.isVisible() ) {
      int max = compute_max_length(text);
      super.setText(text.substring(
          Math.max(text.length()-max,0),
          text.length()));
    }
    else {
      super.setText(text);
    }
  }

  private int compute_max_length(String text) {
    fm = getGraphics().getFontMetrics();
    int max_width = this.getSize().width;
    String t = text;
    while( fm.stringWidth(t) > max_width ) {
      t = t.substring(0,t.length()-2);
    }
    return t.length();
  }
}
