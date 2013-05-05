/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Raik Nagel <raik.nagel@uni-bayreuth.de>
 * changed by: Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TOutStreamCapture.java,v 1.3 2005/08/19 21:29:01 hunold Exp $
 */

package net.sf.jfilesync.gui;

import java.io.*;

import net.sf.jfilesync.gui.term.*;

public class TOutStreamCapture
    extends PrintStream
{
  private static PrintStream oldStdOut;
  private static PrintStream oldStdErr;

  private static TOutStreamCapture stdErrCapture;
  private static TOutStreamCapture stdOutCapture;

  private static boolean active; // deflection active ?
  private static boolean writeStdOut = false; // use old output too

  private static TextOutArea jta; // new output area

  private TOutStreamCapture(PrintStream ps)
  {
    super(ps);
  }

  public static void setMainArea(TextOutArea jt)
  {
    jta = jt;
  }

  /**
   * Starts the capturing
   */
  public static synchronized void start(boolean writeStdoutFlag)
  {
    try
    {
      if (!active && stdOutCapture == null && stdErrCapture == null)
      {
        // Save old settings.
        oldStdOut = System.out;
        oldStdErr = System.err;

        // make new output objects
        stdOutCapture = new TOutStreamCapture(System.out);
        stdErrCapture = new TOutStreamCapture(System.err);

        // deflect the output
        System.setOut(stdOutCapture);
        System.setErr(stdErrCapture);

        active = true;
        writeStdOut = writeStdoutFlag;
      }
    }
    catch (Exception e)
    {
      e.printStackTrace(oldStdErr);
      stop();
    }
  }

  /**
   * Restores the original settings.
   */
  public static synchronized void stop()
  {
    if (oldStdOut != null)
    {
      System.setOut(oldStdOut);
      oldStdOut = null;
      if (stdOutCapture != null)
      {
        stdOutCapture.close();
        stdOutCapture = null;
      }
    }
    if (oldStdErr != null)
    {
      System.setErr(oldStdErr);
      oldStdErr = null;
      if (stdErrCapture != null)
      {
        stdErrCapture.close();
        stdErrCapture = null;
      }
    }
    active = false;
  }

  public static TOutStreamCapture getStdOutCapture()
  {
    return stdOutCapture;
  }

  public static TOutStreamCapture getStdErrCapture()
  {
    return stdErrCapture;
  }

  public static boolean isActive()
  {
    return active;
  }

  public static void setActive(boolean active, boolean writeStdoutFlag)
  {
    if (active)
    {
      start(writeStdoutFlag);
    }
    else
    {
      stop();
    }
  }

  // PrintStream override.
  public void write(int b)
  {
    try
    {
      // here goes whatever custom code you need for capturing
      // Let's write to the standard stream, too
      if (writeStdOut)
      {
        super.write(b);
      }
    }
    catch (Exception e)
    {
      // Oops something's wrong
      super.write(b);
      e.printStackTrace(oldStdErr);
      setError();
    }

    jta.append( (new Integer(b)).toString());
  }

  // PrintStream override.
  public void write(byte buf[], int off, int len)
  {
    try
    {
      // here goes whatever custom code you need for capturing
      // Let's write to the standard stream, too
      if (writeStdOut)
      {
        super.write(buf, off, len);
      }
    }
    catch (Exception e)
    {
      // Oops something's wrong
      super.write(buf, off, len);
      e.printStackTrace(oldStdErr);
      setError();
    }

    jta.append( (new String(buf, off, len)));
  }
}
