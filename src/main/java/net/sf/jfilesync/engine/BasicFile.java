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
 */
package net.sf.jfilesync.engine;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import net.sf.jfilesync.util.TPathControlInterface;

public abstract class BasicFile {

    private final TFileProperties file;
    private final String rootPath;
    private final TPathControlInterface pathControl;
    private final List<BasicFile> children = new ArrayList<BasicFile>();

    public BasicFile(final TFileProperties file, final String rootPath,
            final TPathControlInterface pathControl) {
        this.file = file;
        this.rootPath = rootPath;
        this.pathControl = pathControl;
    }

    protected TFileProperties getTFileProperties() {
        return file;
    }

    protected String getRootPath() {
        return rootPath;
    }

//    public void setRootPath(String sourceRootPath, String targetRootPath) {
//        final String absoluteFileName = getAbsolutePath();
//        final String newAbsoluteFileName = absoluteFileName.replace(
//                sourceRootPath, targetRootPath);
//        file.setAbsoluteFileName(newAbsoluteFileName);
//    }

    protected TPathControlInterface getPathControl() {
        return pathControl;
    }

    public String getAbsolutePath() {
        return file.getAbsoluteFileName();
    }

    public String getFileName() {
        return file.getFileName();
    }

    public String getRelativePathToRoot() {
        return getPathControl().netPlainpath(getRootPath(), getAbsolutePath());
    }

    public void addChild(final BasicFile absFile) {
        children.add(absFile);
    }

    public List<BasicFile> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return (children.size() > 0);
    }

    public boolean isDirectory() {
        return file.isDirectory();
    }

    public final BigInteger getFileSize() {
        return file.getFileSize();
    }

    public final long getFileModTime() {
        return file.getFileModTime();
    }

    public boolean hasChild(final BasicFile file) {
        boolean isChild = false;

        if (isDirectory()) {
            for (BasicFile child : getChildren()) {
                if (child.isDirectory()) {
                    isChild |= child.hasChild(file);
                } else {
                    if (file == child) {
                        isChild = true;
                    }
                }
                if (isChild) {
                    break;
                }
            }
        }

        return isChild;
    }

    public abstract BasicFile clone();

    public void print() {
        System.out.println("BF : " + file.getAbsoluteFileName());
    }

}
