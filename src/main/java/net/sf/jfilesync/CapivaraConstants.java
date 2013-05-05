/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2007 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
package net.sf.jfilesync;

import java.util.HashMap;

public class CapivaraConstants {

    public enum LICENSE {
        GPLv2, GPLv3, Apache20, BSD
    };

    private static HashMap<LICENSE, String> licenseNameMap = new HashMap<LICENSE, String>();

    static {
        licenseNameMap.put(LICENSE.GPLv2, "GPL v2");
        licenseNameMap.put(LICENSE.GPLv3, "GPL v3");
        licenseNameMap.put(LICENSE.Apache20, "Apache license 2.0");
        licenseNameMap.put(LICENSE.BSD, "BSD-style license");
    }

    public static String getLicenseName(final LICENSE license) {
        String licenseName = "unknown";
        if (licenseNameMap.containsKey(license)) {
            licenseName = licenseNameMap.get(license);
        }
        return licenseName;
    }

}
