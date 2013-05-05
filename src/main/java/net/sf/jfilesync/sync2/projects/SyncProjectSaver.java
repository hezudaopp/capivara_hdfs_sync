/*
 * capivara - Java File Synchronization
 *
 * Created on 29 Oct 2006
 * Copyright (C) 2006 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id$
 */
package net.sf.jfilesync.sync2.projects;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import net.sf.jfilesync.MainWin;
import net.sf.jfilesync.settings.ConfigDefinitions;
import net.sf.jfilesync.settings.ConfigFileHandler;
import net.sf.jfilesync.sync2.projects.save.xml.XmlSyncProjectSaver;



public class SyncProjectSaver {

  private final SyncProjectSettings projectSettings;

  public SyncProjectSaver(final SyncProjectSettings projectSettings) {
    if( projectSettings == null ) {
      throw new IllegalArgumentException("projectSettings null");
    }
    this.projectSettings = projectSettings;
  }

  public void saveProject() throws IOException {

    final ISyncProjectNode projectRoot = projectSettings.save();
        
    final String projectFileName = ConfigFileHandler.getInstance()
        .getSyncProjectFileLocation("testFile.xml");
    final String projectFileDir = ConfigFileHandler.getInstance()
        .getSyncProjectLocation();
    final File f = new File(projectFileDir);
    if(  ! f.exists() ) {
      f.mkdir();
    }

    XmlSyncProjectSaver xmlSaver = new XmlSyncProjectSaver();
    try {
      xmlSaver.save(projectRoot, projectFileName);
    } catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (TransformerFactoryConfigurationError e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (TransformerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
