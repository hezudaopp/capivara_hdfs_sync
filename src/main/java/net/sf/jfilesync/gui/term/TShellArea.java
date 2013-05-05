/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2004 Raik Nagel <raik.nagel@uni-bayreuth.de>
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

package net.sf.jfilesync.gui.term;


import javax.swing.* ;
import java.awt.* ;

public class TShellArea extends JPanel
{

  private static final long serialVersionUID = 4049914974325322293L;

  private static int
      rows = 25,
      cols = 80;

  private JTextArea viewArea ;
  private JTextArea inputLine ;

  public TShellArea()
  {
    super() ;
    viewArea = new JTextArea(rows, cols);
    viewArea.append("view");
    viewArea.setEditable(false);

    inputLine = new JTextArea(2, cols);
    inputLine.setForeground(Color.black);
//    inputLine.setBackground(Color.red);
    inputLine.setEditable(true);
    inputLine.append("input>");

    this.setLayout( new FlowLayout() );
    add(viewArea);
    add(inputLine);

  }
}
