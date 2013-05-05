/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2009 Sascha Hunold <hunoldinho@users.sourceforge.net>
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

package net.sf.jfilesync.settings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class BuildInfoPropertiesReader {

    private static Logger LOG = Logger
            .getLogger(BuildInfoPropertiesReader.class.getName());

    private final static String generalPropFile = "/net/sf/jfilesync/capivara.properties";
    private final static String buildPropFile   = "/net/sf/jfilesync/prop/bundles/build.properties";

    public static void readBuildVersionData() {

        InputStream instream = BuildInfoPropertiesReader.class
                .getResourceAsStream(buildPropFile);

        if (instream == null) {
            LOG.warning("properties file " + buildPropFile + " does not exist");
        } else {
            try {
                Properties properties = new Properties();
                properties.load(instream);
                setProperty("BUILD_DATE", properties.getProperty("builddate"),
                        "build.date");
            } catch (IOException e) {
                LOG.warning("error while reading " + buildPropFile + ": "
                        + e.getMessage());
            }
        }
    }

    public static void readCapivaraProperties() {
        InputStream instream = BuildInfoPropertiesReader.class
                .getResourceAsStream(generalPropFile);

        if (instream == null) {
            LOG.warning("properties file " + generalPropFile
                    + " does not exist");
        } else {
            try {
                                
                Properties properties = new Properties();
                properties.load(instream);

                setProperty("PROGRAM_NAME", properties
                        .getProperty("program.title"), "program.title");

                setProperty("PROGRAM_VERSION", properties
                        .getProperty("program.version"), "program.version");

                setProperty("COPYRIGHT_YEARS", properties
                        .getProperty("program.years"), "program.years");

                
            } catch (IOException e) {
                LOG.warning("error while reading " + generalPropFile + ": "
                        + e.getMessage());
            }
        }
    }

    private static void setProperty(String key, String value, String desc) {

        try {
            if (value != null) {
                ConfigDefinitions.class.getDeclaredField(key).set(null, value);
            } else {
                LOG.warning("no value found for key : " + desc);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

}
