/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Raik Nagel <raik.nagel@uni-bayreuth.de>
 * changed 2005, Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TConnectionData.java,v 1.14 2006/08/05 21:35:37 hunold Exp $
 */

package net.sf.jfilesync.plugins.net;

/**
 *  All needed connection data and/or temporary data.
 */

import java.util.logging.Logger;

import javax.swing.JFrame;

import net.sf.jfilesync.gui.TConnectDialog;
import net.sf.jfilesync.sync2.projects.ISyncProjectNode;
import net.sf.jfilesync.sync2.projects.ISyncProjectSavable2;
import net.sf.jfilesync.sync2.projects.nodes.ConnectionDataSaveNode;
import net.sf.jfilesync.util.TMiscTool;


public class TConnectionData implements ISyncProjectSavable2 {

  // in the first time, there are no data
  private boolean validData ;

  private String user;
  private String host;
  private String password;
  private int port = -1;
  private ConnectionPlugin plugin;

  private TConnectDialog myDialog ;
  private JFrame parent;
  private String encoding;

  private boolean caseInsensitive;
  private boolean keepAlive;

  private final static Logger LOGGER = Logger.getLogger(TConnectionData.class
      .getName());


  public TConnectionData(JFrame parent) {
    this(parent, "localhost", -1,
        TMiscTool.getUserName(), "",
        ConnectionPluginManager.LOCAL_PLUGIN);
    validData = false ; // no valid data available
  }

  public TConnectionData(JFrame parent, String host, int port, String user,
      String passwd, int protocol) {
    this.parent = parent;
    this.user = user;
    this.host = host;
    this.port = port;
    this.password = passwd;
    setProtocol(protocol);

    encoding = null;
    caseInsensitive = false;

    validData = true; // all data available ?!?
  }

// --------------------------------------------------------------------------


  public String getHost() {
    return host;
  }

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }

  public JFrame getParentComponent() {
    return parent;
  }

  public boolean hasValidData() {
    return validData;
  }

  public ConnectionPlugin getPlugin() {
    return plugin;
  }

  public void setProtocol(int protocol) {
        this.plugin = ConnectionPluginManager
                .getConnectionModelInstance(protocol);
  }

  public String getEncoding() {
    return encoding;
  }

  public void setEncoding(final String encoding) {
    this.encoding = encoding;
  }

  public boolean isCaseInsensitive() {
    return caseInsensitive;
  }

  public boolean isKeepAliveSet() {
      return keepAlive;
  }

  public void setCaseInsensitive(final boolean caseInsensitive) {
    this.caseInsensitive = caseInsensitive;
  }

  public int getPort() {
    return port;
  }

  // A little method for get/update/refresh the connection data via a
  // gui dialog. The parameter >always< indicates the manner of user
  // interaction. If it true - the input dialog is shown, always. Otherwise
  // (if it false) the input dialog is shown, if no connection data are
  // available.
  // This code is global synchronized because only one thread can manipulate
  // the data.
  public synchronized boolean promptForConnectionData(boolean always) {
    boolean back = false; // default : data update cancelled

    if( !validData || always ) {

      myDialog = new TConnectDialog(parent);

      myDialog.setHost(host);

      // check if user name exits
      if( user == null || user.length() == 0) {
        user = TMiscTool.getUserName();
      }
      myDialog.setUser(user);

      if( password != null && ! password.equals("") ) {
        myDialog.setPasswd(password);
      }

      myDialog.setPlugin(plugin);
      myDialog.setPort(port);

      if( validData ) {
        LOGGER.info("GIVE PASSWD FIELD FOCUS");
        myDialog.setFocusElement(0);
      }

      int userClicked = myDialog.showDialog();
      if ( userClicked == TConnectDialog.CD_RESULT_OKAY ) {
        // dialog contains valid data
        host     = myDialog.getHost();
        user     = myDialog.getUser();
        password = myDialog.getPasswd();
        plugin   = myDialog.getPlugin();
        port     = myDialog.getPort();
        if( myDialog.hasUserEncoding() ) {
          encoding = myDialog.getUserEncoding();
        }

        caseInsensitive = myDialog.isCaseInsensitive();
        keepAlive = myDialog.isKeepAliveSet();

        validData = true; // hopefully
        back = true; // successful data update
      }
    }
    else {
      back = true;  // successful data without dialog
    }

     return back;
  }

  public ISyncProjectNode save() {

    final ConnectionDataSaveNode node = new ConnectionDataSaveNode(getHost(),
        getUser(), getPort(), getPlugin().getConnectionID());

    node.setCaseInsensitive(isCaseInsensitive());
    node.setEncoding(getEncoding());

    return node;
  }

}
