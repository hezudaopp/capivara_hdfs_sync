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
 * $Id: ConfigReader.java,v 1.4 2006/01/01 22:38:19 hunold Exp $
 */

package net.sf.jfilesync.settings;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ConfigReader {

    private Document config;
    private DocumentBuilderFactory factory;

//    private final static Logger LOGGER = Logger.getLogger(ConfigReader.class
//            .getPackage().getName());

    public ConfigReader() {
        factory = DocumentBuilderFactory.newInstance();
        // factory.setValidating(true);
    }

    public void readConfig(final String configDir) throws IOException {
        try {
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final File f = new File(configDir);
            config = builder.parse(f);
        } catch (SAXException e) {
            throw new IOException(e.getMessage());
        } catch (ParserConfigurationException e) {
            throw new IOException(e.getMessage());
        }
    }

    public TConfig getConfig() {
        return new TConfig(config);
    }

}
