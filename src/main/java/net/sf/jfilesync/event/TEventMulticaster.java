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
 * $Id: TEventMulticaster.java,v 1.6 2006/03/05 10:26:21 hunold Exp $
 */

package net.sf.jfilesync.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;


public class TEventMulticaster extends Thread {

  private static final Logger LOGGER = Logger.getLogger(TEventMulticaster.class
      .getPackage().getName());

  class TEventQueue {
    private int maxCapacity = Integer.MAX_VALUE;
    private List<TEvent> queue = new ArrayList<TEvent>();

    public TEventQueue() {
    }

    public TEventQueue(int maxCapacity) {
      this.maxCapacity = maxCapacity;
    }

    public synchronized void push(TEvent message) {
      queue.add(message);
    }

    public synchronized TEvent pop() {
      TEvent e = null;
      if (queue.size() > 0) {
        e = queue.remove(0);
      }
      return e;
    }

    public synchronized boolean isEmpty() {
      return queue.isEmpty();
    }

    public synchronized boolean full() {
      return (queue.size() >= maxCapacity);
    }
  }


  private static TEventMulticaster instance = null;
  private final HashMap<TMessage.ID, List<TEventListener>> listenerHt = new HashMap<TMessage.ID, List<TEventListener>>();
  private TEventQueue eventQueue;
  private boolean running = true;
  private int openEvents;

  private TEventMulticaster() {
    
    for(TMessage.ID mid : TMessage.ID.values()) {
      listenerHt.put(mid, new ArrayList<TEventListener>());
    }    
    eventQueue = new TEventQueue();
    openEvents = 0;
    start();
  }

  public synchronized void shutdown() {
    running = false;
    interrupt();
  }

  /*
   * Singleton
   */
  public synchronized static TEventMulticaster getInstance() {
    if (instance == null) {
      instance = new TEventMulticaster();
    }
    return instance;
  }

  public final synchronized void addTEventListener(
      final TEventListener listener, final TMessage.ID mid) {

    final List<TEventListener> listenerQueue = listenerHt.get(mid);
    if (listenerQueue == null) {
      LOGGER.warning("unsupported type of event : " + mid);
    } else {
      if (!listenerQueue.contains(listener)) {
        listenerQueue.add(listener);
      }
    }
  }

  public final synchronized void removeTEventListener(
      final TEventListener listener) {
    for (final List<TEventListener> eventList : listenerHt.values()) {
      if( eventList != null && eventList.contains(listener) ) {
        eventList.remove(listener);
      }
    }
  }

  public final synchronized void removeTEventListener(
      final TEventListener listener, final TMessage.ID mid) {
    final List<TEventListener> queue = listenerHt.get(mid);
    if (queue != null && queue.contains(listener)) {
      queue.remove(listener);
    }
  }

  public final synchronized void fireTEvent(Object origin, int ccId, TMessage message) {
    eventQueue.push(new TEvent(origin, ccId, message));
    openEvents++;
    notify();
  }

  public void run() {
    LOGGER.info("start multicaster");
    outer: while (running) {
      synchronized(this) {
        while (openEvents == 0) {
          try {
            wait();
          } catch (InterruptedException ex) {
            if (!running) {
              break outer;
            }
          }
        }
        final TEvent event = eventQueue.pop();
        final TMessage.ID mid = event.getMessage().getMessageType();
        final List<TEventListener> listeners = listenerHt.get(mid);
        if( listeners != null ) {
          // maybe someone adds a listener meanwhile
          // better work on a copy
          final List<TEventListener> listCopy = new ArrayList<TEventListener>(
              listeners);
          
          for( final TEventListener l : listCopy ) {
            if( running == false) {
              break;
            }
            SwingUtilities.invokeLater(new DispatchRunner(l, event));
          }
          openEvents--;
        }
      }
    }
    LOGGER.info("shutdown multicaster");
  }

  class DispatchRunner implements Runnable {

    private TEventListener listener;
    private TEvent event;

    public DispatchRunner(TEventListener listener, TEvent event) {
      this.listener = listener;
      this.event = event;
    }

    public void run() {
      listener.processEvent(event);
    }
  }

}
