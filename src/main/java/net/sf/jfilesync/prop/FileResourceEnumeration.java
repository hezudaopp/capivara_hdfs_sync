/*
 * capivara - Java File Synchronization
 *
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
 * $Id: FileResourceEnumeration.java,v 1.1 2006/09/04 16:01:18 hunold Exp $
 */
package net.sf.jfilesync.prop;

import java.util.Enumeration;
import java.util.Iterator;

public class FileResourceEnumeration implements Enumeration<String> {

	private final Iterator<String> keyIt;
	
	public FileResourceEnumeration(Iterator<String> keyIt) {
		this.keyIt = keyIt;
	}
	
	public boolean hasMoreElements() {
		return keyIt.hasNext();
	}

	public String nextElement() {
		return keyIt.next();
	}

}
