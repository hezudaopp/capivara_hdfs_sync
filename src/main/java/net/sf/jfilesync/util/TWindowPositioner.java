/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TWindowPositioner.java,v 1.2 2005/08/19 21:29:02 hunold Exp $
 */

package net.sf.jfilesync.util;

/**
 * Class provides methods to put any window (dialog, frame)
 * on the screen at specified position (e.g. centered)
 */

import java.awt.*;

public class TWindowPositioner {
  public TWindowPositioner() {
  }

  public static Point getCenteredWindowPoint(Window win) {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension winSize = win.getSize();
    Point p = new Point(0, 0);
    p.x = (screenSize.width - winSize.width)/2;
    p.y = (screenSize.height - winSize.height)/2;
    return p;
  }
  
  public static Point center(Component parent, Component me) {
    Dimension parSize   = parent.getSize();
    Dimension childSize = me.getSize();
    Point parStart      = parent.getLocationOnScreen();   
    
    Point p = new Point(0, 0);
    p.x = parStart.x + (parSize.width - childSize.width)/2;
    p.y = parStart.y + (parSize.height - childSize.height)/2;
    return p;
  }
  

}
