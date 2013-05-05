/*
 * capivara - Java File Synchronization
 *
 * Created on 30-May-2005
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
 * $Id: PermissionPanel.java,v 1.9 2006/08/09 22:18:39 hunold Exp $
 */
package net.sf.jfilesync.gui.components;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.jfilesync.engine.FilePermissions;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.settings.ConfigDefinitions;

public class PermissionPanel extends JPanel 
{
  
  public final static int TYPE_DIR  = 0x1;
  public final static int TYPE_FILE = 0x2;
  
  private final int panelType; 
  private boolean recursiveOption = false;
  private FilePermissions permissions;
    
  private SpecialFlagsPanel specialPanel = new SpecialFlagsPanel();
  private RWXPanel uPanel = new RWXPanel(RWXPanel.TYPE_USER);
  private RWXPanel gPanel = new RWXPanel(RWXPanel.TYPE_GROUP);
  private RWXPanel oPanel = new RWXPanel(RWXPanel.TYPE_OTHER);
  private JCheckBox recBox = new JCheckBox();
  
  public PermissionPanel(final int type, final boolean recursiveOption) {
    this.panelType = type;
    this.recursiveOption = recursiveOption;

    if (type == TYPE_DIR) {
      permissions = new FilePermissions(
          ConfigDefinitions.DEFAULT_PERMISSIONS_DIR);
    } else if (type == TYPE_FILE) {
      permissions = new FilePermissions(
          ConfigDefinitions.DEFAULT_PERMISSIONS_FILE);
    }

    initUI();
    updatePanel();
  }
  
  protected void initUI() {
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    //mainPanel.setBorder(BorderFactory.createEtchedBorder());
    mainPanel.add(uPanel);
    mainPanel.add(gPanel);
    mainPanel.add(oPanel);
    mainPanel.add(specialPanel);
    if( panelType == TYPE_DIR && recursiveOption ) {
      mainPanel.add(createRecursivePanel());
    }
    add(mainPanel);
  }
  
  protected JPanel createRecursivePanel() {
    JPanel ret = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel recLabel = new JLabel("recursive");
    ret.add(recLabel);
    ret.add(recBox);
    return ret;
  }
  
  /**
   * 
   * @return file permissions as int or -1 no
   * permissions are available
   */
  public int getPermissions() {
    int ret = readPermissions();
    // dont forget to add the type flags
    if( panelType == TYPE_DIR ) {
      ret |= FilePermissions.S_IFDIR;
    }
    else if( panelType == TYPE_FILE ) {
      ret |= FilePermissions.S_IFREG;
    }
    return ret;
  }
  
  public void setPermissions(final int p) {
    if( permissions == null ) {
      permissions = new FilePermissions(p);
    }
    else {
      permissions.setValue(p);
    }
    updatePanel();
  }
  
  public boolean recursive() {
    return recBox.isSelected();
  }
  
  protected int readPermissions() {
    int retval = 0;
    retval |= uPanel.getRXWFlags();
    retval |= gPanel.getRXWFlags();
    retval |= oPanel.getRXWFlags();
    retval |= specialPanel.getSpecialFlags();
    return retval;
  }
  
  protected void updatePanel() {
    uPanel.setRWXFlags(permissions.getValue());
    gPanel.setRWXFlags(permissions.getValue());
    oPanel.setRWXFlags(permissions.getValue());
    specialPanel.setSpecialFlags(permissions.getValue());
  }
  
  //--------------------------------------------------------------------------
  
  static class RWXPanel extends JPanel {
    
    public final static int TYPE_USER  = 0;
    public final static int TYPE_GROUP = 1;
    public final static int TYPE_OTHER = 2;
        
    private int shift = 0;
    private JLabel groupLabel = new JLabel("");
    private JCheckBox readBox    = new JCheckBox("r");
    private JCheckBox writeBox   = new JCheckBox("w");
    private JCheckBox executeBox = new JCheckBox("x");
    
    public RWXPanel(int type) {
      switch(type) {
      case TYPE_USER:
        shift = 6;
        groupLabel.setText(LanguageBundle.getInstance()
            .getMessage("label.perm.owner"));
        break;
      case TYPE_GROUP:
        shift = 3;
        groupLabel.setText(LanguageBundle.getInstance()
            .getMessage("label.perm.group"));
        break;
      case TYPE_OTHER:
        shift = 0;
        groupLabel.setText(LanguageBundle.getInstance()
            .getMessage("label.perm.others"));
        break;
      }
      
      setLayout(new FlowLayout(FlowLayout.RIGHT));
      add(groupLabel);
      add(readBox);
      add(writeBox);
      add(executeBox);
    }
        
    public void setRWXFlags(int perm) {
      boolean read = ( (perm & (1 << 2 << shift)) > 0 );
      readBox.setSelected(read);
      boolean write = ( (perm & (1 << 1 << shift)) > 0 );
      writeBox.setSelected(write);
      boolean execute = ( (perm & (1 << shift)) > 0 );
      executeBox.setSelected(execute);
    }
    
    public int getRXWFlags() {
      int ret = 0;
      if( readBox.isSelected() ) {
        ret |= ( 1 << 2 << shift ); 
      }
      if( writeBox.isSelected() ) {
        ret |= ( 1 << 1 << shift ); 
      }
      if( executeBox.isSelected() ) {
        ret |= ( 1 << shift ); 
      }
      return ret;
    }
    
  }
  
  //--------------------------------------------------------------------------

  static class SpecialFlagsPanel extends JPanel {

    /*
    S_ISUID    0004000   set UID bit
    S_ISGID    0002000   set GID bit
    S_ISVTX    0001000   sticky bit
    */
    
    private final int S_ISUID      = 0004000;
    private final int S_ISGID      = 0002000;
    private final int S_ISVTX      = 0001000;
    
    private JCheckBox uidCheck    = new JCheckBox("UID");
    private JCheckBox gidCheck    = new JCheckBox("GID");
    private JCheckBox stickyCheck = new JCheckBox("Sticky");
    
    public SpecialFlagsPanel() {
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      add(uidCheck);
      add(gidCheck);
      add(stickyCheck);
    }
        
    public int getSpecialFlags() {
      int retval = 0;
      if( uidCheck.isSelected() ) {
        retval |= S_ISUID;
      }
      if( gidCheck.isSelected() ) {
        retval |= S_ISGID;
      }
      if( stickyCheck.isSelected() ) {
        retval |= S_ISVTX;
      }
      return retval;
    }
    
    public void setSpecialFlags(int perm) {
      boolean uid = (perm & S_ISUID) == S_ISUID;
      uidCheck.setSelected(uid);
      boolean gid = (perm & S_ISGID) == S_ISGID;
      gidCheck.setSelected(gid);
      boolean sticky = (perm & S_ISVTX) == S_ISVTX;
      stickyCheck.setSelected(sticky);
    }
    
  }
}
