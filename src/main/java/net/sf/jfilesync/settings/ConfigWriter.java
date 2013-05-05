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
 * $Id: ConfigWriter.java,v 1.3 2005/09/20 21:30:41 hunold Exp $
 */

package net.sf.jfilesync.settings;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;

import java.io.*;

/**
 * transforming DOM 2 XML
 *
 * @author Sascha Hunold
 */

public class ConfigWriter {

  public ConfigWriter() {
  }

  public void writeConfig(final TConfig config, final String configFileName) {
    try {
      final Transformer transformer = TransformerFactory.newInstance()
          .newTransformer();

      final DOMSource source = new DOMSource(config.getDOMDocument());
      final File configFile = new File(configFileName);
      final StreamResult result = new StreamResult(configFile);

      transformer.setOutputProperty(OutputKeys.METHOD, "xml");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.transform(source, result);
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
      ex.printStackTrace();
    }
  }

}
