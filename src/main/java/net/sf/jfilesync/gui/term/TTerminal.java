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
 * $Id: TTerminal.java,v 1.3 2006/04/02 14:47:42 hunold Exp $
 */

package net.sf.jfilesync.gui.term;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class TTerminal extends JTextArea implements KeyListener {
  private static final long serialVersionUID = 4121132541429559856L;

  private static int rows = 25, cols = 80;

  private PipedInputStream userInStream;

  private PipedOutputStream userOutStream;

  private PipedInputStream sessionInStream;

  private PipedOutputStream sessionOutStream;

  private TerminalObserver termobsv;

  private boolean consume;

  private Document doc;

  public TTerminal() {
    super(rows, cols);
    userInStream = new PipedInputStream();
    userOutStream = new PipedOutputStream();
    sessionInStream = new PipedInputStream();
    sessionOutStream = new PipedOutputStream();

    this.setLineWrap(true);
    this.addKeyListener(this);

    doc = this.getDocument();

    try {
      userInStream.connect(userOutStream);
      sessionInStream.connect(sessionOutStream);
    } catch (IOException ioex) {
      ioex.printStackTrace();
    }
  }

  public InputStream getInputStream() {
    return userInStream;
  }

  public OutputStream getOutputStream() {
    return sessionOutStream;
  }

  public void keyPressed(KeyEvent e) {
  }

  public void keyReleased(KeyEvent e) {
  }

  public void keyTyped(KeyEvent e) {
    if (!consume)
      return;

    //Caret caret = this.getCaret();
    char c = e.getKeyChar();

    switch (e.getKeyCode()) {
    case KeyEvent.VK_BACK_SPACE:
      break;
    default:
      removeLast();
    }

    try {
      userOutStream.write((int) c);
    } catch (IOException ioex) {
      ioex.printStackTrace();
    }
  }

  public void removeLast() {
    try {
      doc.remove(doc.getLength() - 1, 1);
    } catch (BadLocationException blex) {
      blex.printStackTrace();
    }
  }

  public void startConsuming() {
    consume = true;
    termobsv = new TerminalObserver(sessionInStream, this);
    termobsv.start();
  }

  public void stopConsuming() {
    termobsv.shutdown();
  }

  class TerminalObserver extends Thread {
    private InputStream instream;

    private TTerminal term;

    private boolean alive = true;

    public TerminalObserver(InputStream instream, TTerminal term) {
      this.instream = instream;
      this.term = term;
    }

    public synchronized void shutdown() {
      alive = false;
    }

    public void run() {
      int in;
      while (alive) {
        try {
          while ((in = instream.read()) != -1) {
            term.append("" + (char) in);
          }
        } catch (IOException ioex) {
          ioex.printStackTrace();
        }
        try {
          Thread.sleep(50);
        } catch (InterruptedException iex) {
          iex.printStackTrace();
        }
      }
    }
  }

}
