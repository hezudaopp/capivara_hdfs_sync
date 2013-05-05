/*
 * capivara - Java File Synchronization
 *
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
 * $Id: SyncTree.java,v 1.10 2005/08/19 21:29:02 hunold Exp $
 */
package net.sf.jfilesync.sync2;

import net.sf.jfilesync.util.TPathControlInterface;

public class SyncTree {

    private final SyncFile root;
    private final TPathControlInterface pathControl;
    private final String rootPath;

    // private final static Logger LOGGER = Logger.getLogger(SyncTree.class
    // .getPackage().getName());

    public SyncTree(SyncFile root, String rootPath,
            TPathControlInterface pathControl) {
        this.root = root;
        this.rootPath = rootPath;
        this.pathControl = pathControl;
    }

    public TPathControlInterface getPathControl() {
        return pathControl;
    }

    public SyncFile getRoot() {
        return root;
    }

    public String getRootPath() {
        return rootPath;
    }

    public boolean isRoot(SyncFile node) {
        return (root == node);
    }

    public String getPathRelativeToRoot(SyncFile file) {
        return pathControl.netPlainpath(rootPath, file.getFile()
                .getAbsoluteFileName());
    }

    public int getNumberOfDirectories() {
        int s = 0;
        if (root != null) {
            s = 1;
            s += getNumberOfDirChildren(root);
        }
        return s;
    }

    private int getNumberOfDirChildren(final SyncFile f) {
        int ccount = 0;
        if (f.getFile().isDirectory()) {
            ccount = 1;
            SyncFile[] childs = f.getChildren();
            for (int i = 0; i < childs.length; i++) {
                ccount += getNumberOfDirChildren(childs[i]);
            }
        }
        return ccount;
    }

    public int getNumberOfFiles() {
        int fileNum = 0;
        if (root != null) {
            fileNum = getNumberOfFiles(root);
        }
        return fileNum;
    }

    private int getNumberOfFiles(final SyncFile f) {
        int childNum = 0;
        if (f.getFile().isDirectory()) {
            SyncFile[] childs = f.getChildren();
            for (int i = 0; i < childs.length; i++) {
                if (childs[i].getFile().isDirectory()) {
                    childNum += getNumberOfFiles(childs[i]);
                } else {
                    childNum++;
                }
            }
        } else {
            childNum = 1;
        }
        return childNum;
    }

    /**
     *
     * @param absoluteFileName
     * @return returns a reference to this sync file or null if it does not
     *         exist
     */

    public SyncFile findSyncFile(final String absoluteFileName) {
        SyncFile file = null;
        file = root.findChild(absoluteFileName);
        return file;
    }

    public void setIgnoredFile(final String fname) {
        SyncFile child = root.findChild(fname);
        if (child != null) {
            child.setIgnored(true);
        }
    }

    public void unhide() {
        if (root != null) {
            unhideNode(root);
        }
    }

    public void showOnly(final String[] fnlist) {
        if (fnlist == null) {
            return;
        }
        if (root != null) {
            hideIfNotInList(root, fnlist);
        }
    }

    protected void hideIfNotInList(final SyncFile node, final String[] fnlist) {
        if (node == null || fnlist == null) {
            return;
        }
        boolean inList = isNodeInList(node, fnlist);
        if (!inList) {
            // if its a dir and a prefix of some file in fnlist
            // -> do not ignore
            if (node.getFile().isDirectory()) {
                boolean isPrefix = isPrefixOfListElem(node, fnlist);
                if (!isPrefix) {
                    node.setIgnored(true);
                } else {
                    // continue with subdirs
                    SyncFile[] childs = node.getAllChildren();
                    for (int i = 0; i < childs.length; i++) {
                        hideIfNotInList(childs[i], fnlist);
                    }
                }
            } else {
                node.setIgnored(true);
            }
        }
    }

    protected boolean isPrefixOfListElem(final SyncFile node,
            final String[] fnlist) {
        boolean ret = false;
        String pathName = node.getFile().getAbsoluteFileName();
        for (int i = 0; i < fnlist.length; i++) {
            if (fnlist[i].startsWith(pathName)) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    protected boolean isNodeInList(final SyncFile node, String[] fnlist) {
        boolean ret = false;
        String pathName = node.getFile().getAbsoluteFileName();
        for (int i = 0; i < fnlist.length; i++) {
            if (pathName.equals(fnlist[i])) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    protected void unhideNode(SyncFile node) {
        if (node.isIgnored()) {
            node.setIgnored(false);
        }
        if (node.getFile().isDirectory()) {
            SyncFile[] childs = node.getAllChildren();
            if (childs != null) {
                for (int i = 0; i < childs.length; i++) {
                    unhideNode(childs[i]);
                }
            }
        }
    }

    public SyncFileIgnoreList getIgnoreList() {
        SyncFileIgnoreList il = new SyncFileIgnoreList();
        collectIgnoredNodes(root, il);
        return il;
    }

    protected void collectIgnoredNodes(SyncFile node, SyncFileIgnoreList il) {
        if (node.isIgnored()) {
            il.add(node);
        }
        if (node.getFile().isDirectory()) {
            SyncFile[] c = node.getAllChildren();
            for (int i = 0; i < c.length; i++) {
                collectIgnoredNodes(c[i], il);
            }
        }
    }

}
