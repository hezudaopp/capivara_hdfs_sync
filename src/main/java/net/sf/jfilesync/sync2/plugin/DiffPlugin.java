/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2008 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
package net.sf.jfilesync.sync2.plugin;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sf.jfilesync.gui.TextViewDialog;
import net.sf.jfilesync.gui.swing.ComponentVisibleRunner;
import net.sf.jfilesync.sync2.SyncFile;
import bmsi.util.Diff;
import bmsi.util.DiffPrint;

public class DiffPlugin implements SyncPlugin {

  private final SyncPluginProvider provider;
  
  public DiffPlugin(SyncPluginProvider provider) {
    if( provider == null ) {
      throw new IllegalArgumentException("provider is null");
    }
    this.provider = provider;
  }
  
  public void compare(int conId1, SyncFile file1, int conId2, SyncFile file2)
      throws SyncPluginException {

    try{  
      final List<String> lines1 = provider.readLines(conId1, file1);
      final List<String> lines2 = provider.readLines(conId2, file2);
      
      final String[] lines1Ar = (String[])lines1.toArray(new String[lines1.size()]);
      final String[] lines2Ar = (String[])lines2.toArray(new String[lines2.size()]);
      
      Diff a = new Diff(lines1Ar, lines2Ar);
      DiffPrint.Base dp = new DiffPrint.ContextPrint(lines1Ar, lines2Ar);
      
      StringWriter sw = new StringWriter();
  
      dp.setOutput(sw);
  
      //dp.print_header(file1.getRelativePath(), file1.getRelativePath());
  
      dp.print_script(a.diff_2(false));
      
      final TextViewDialog tf = new TextViewDialog(null, sw.getBuffer()
          .toString(), "Diff");
      SwingUtilities.invokeLater(new ComponentVisibleRunner(tf, true));
      // tf.setVisible(true);
      
    } catch(IOException e) {
      throw new SyncPluginException(e);
    }
    
  }

  public String getDescription() {
    return "diff plugin";
  }

  public String getLicense() {
    return "GPL";
  }

  public int getMajorVersion() {
    return 0;
  }

  public int getMinorVersion() {
    return 1;
  }

  public String getName() {
    return "diff plugin";
  }

  public boolean isProvided(int feature) {
    // we won't need this
    return false;
  }

}
