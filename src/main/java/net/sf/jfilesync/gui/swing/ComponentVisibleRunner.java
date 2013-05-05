/*
 * capivara - Java File Synchronization
 *
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
 */
package net.sf.jfilesync.gui.swing;

import java.awt.Component;
import java.awt.Window;

public class ComponentVisibleRunner implements Runnable {

    private final boolean visible;
    private final boolean dispose;
    private final Component c;

    public ComponentVisibleRunner(Component c, boolean visible) {
        this(c, visible, false);
    }

    public ComponentVisibleRunner(Component c, boolean visible, boolean dispose) {
        this.c = c;
        this.visible = visible;
        this.dispose = dispose;
    }

    public void run() {
        c.setVisible(visible);
        if (dispose) {
            if (c instanceof Window) {
                ((Window) c).dispose();
            }
        }
    }

}
