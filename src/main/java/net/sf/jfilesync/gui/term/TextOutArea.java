/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Raik Nagel <raik.nagel@uni-bayreuth.de>
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
 * $Id: TextOutArea.java,v 1.3 2005/08/19 21:29:02 hunold Exp $
 */


package net.sf.jfilesync.gui.term;


import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.* ;
import javax.swing.text.* ;

public class TextOutArea extends JTextPane
{
  private static final long serialVersionUID = 3835155068965433905L;
  private Document doc ;

  static Logger logger = Logger.getLogger(TextOutArea.class.getName());
  
  public TextOutArea()
  {
    doc = this.getDocument() ;
    this.setEditable(false);
  }

  public void append(String s1)
  {
    try
    {
      doc.insertString(doc.getLength(), s1, null);
    }
    catch (BadLocationException e) {
      logger.log(Level.WARNING, e.getMessage());
    }

  }
}
