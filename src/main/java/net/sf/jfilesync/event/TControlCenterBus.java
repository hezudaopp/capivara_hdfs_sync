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
 * $Id: TControlCenterBus.java,v 1.2 2005/08/19 21:29:02 hunold Exp $
 */

package net.sf.jfilesync.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

public class TControlCenterBus {

    private final List<ControlCenterBusListener> listeners = new ArrayList<ControlCenterBusListener>();

    public TControlCenterBus() {

    }

    public synchronized void addControlCenterBusListener(
            ControlCenterBusListener l) {
        listeners.add(l);
    }

    public synchronized void removeControlCenterBusListener(
            ControlCenterBusListener l) {
        listeners.remove(l);
    }

    public synchronized void fireControlCenterBusEvent(ControlCenterEvent e) {
        final Iterator<ControlCenterBusListener> lit = listeners.iterator();
        while (lit.hasNext()) {
            SwingUtilities.invokeLater(new DispatchRunner(lit.next(), e));
        }
    }

    class DispatchRunner implements Runnable {
        private ControlCenterBusListener listener;
        private ControlCenterEvent event;

        public DispatchRunner(ControlCenterBusListener l, ControlCenterEvent e) {
            listener = l;
            event = e;
        }

        public void run() {
            listener.busMessageReceived(event);
        }
    }
}
