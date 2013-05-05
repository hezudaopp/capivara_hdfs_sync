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
 * $Id: ConnectionDetails.java,v 1.4 2006/02/14 22:25:24 hunold Exp $
 */
package net.sf.jfilesync.engine;

public class ConnectionDetails {

  private String user;
  private String host;
  private String path;
      
  public ConnectionDetails(String user, String host, String path) {
    this.user = user;
    this.host = host;
    this.path = path;
  }
  
  public void setUser(String user) {
    this.user = user;
  }
  
  public String getUser() {
    return user;
  }
  
  public void setHost(String host) {
    this.host = host;
  }

  public String getHost() {
    return host;
  }
  
  public String getCurrentPath() {
    return path;
  }
  
  public void setCurrentPath(String path) {
    this.path = path;
  }
  
  public String toString() {
    StringBuffer buf = new StringBuffer();
    if( user != null ) {
      buf.append(user);
      buf.append("@");
    }
    if( host != null ) {
      buf.append(host);
    }
    return buf.toString();
  }
  
}
