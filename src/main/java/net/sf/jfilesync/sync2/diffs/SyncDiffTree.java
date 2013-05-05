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
 * $Id: SyncDiffTree.java,v 1.11 2006/06/11 21:00:12 hunold Exp $
 */
package net.sf.jfilesync.sync2.diffs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jfilesync.sync2.SyncDiff;
import net.sf.jfilesync.sync2.SyncDiffCollection;
import net.sf.jfilesync.sync2.SyncFile;

public class SyncDiffTree {

  private SyncDiff root;

  private final Map<SyncFile, SyncDiff> fileMap = new HashMap<SyncFile, SyncDiff>();

  // private final static Logger LOGGER = Logger.getLogger(SyncDiffTree.class
  // .getName());

  private SyncDiffCollection collection;

  public SyncDiffTree(final SyncDiff root) {
    this.root = root;
    if (root == null) {
      throw new IllegalArgumentException("root of SyncDiffTree is null");
    }
    fileMap.put(root.getSyncFile(), root);
  }

  private SyncDiffTree() {
  }

  public static SyncDiffTree getDummyTree() {
    return new SyncDiffTree();
  }

  public SyncDiffCollection getSyncDiffCollection() {
    if (collection == null) {
      collection = new SyncDiffCollection();
      collectDiffs(root, collection);
    }
    return collection;
  }

  protected void collectDiffs(final SyncDiff node, SyncDiffCollection col) {
    if (node != null && !node.getSyncFile().isIgnored()) {
      col.addSyncDiff(node);
      final SyncDiff[] diffAr = node.getChildren();
      for (int i = 0; diffAr != null && i < diffAr.length; i++) {
        collectDiffs(diffAr[i], col);
      }
    }
  }

  public SyncDiff[] getTopDownList() {
    final List<SyncDiff> topDownList = new ArrayList<SyncDiff>();
    topDown(root, topDownList);
    return topDownList.toArray(new SyncDiff[topDownList.size()]);
  }

  private void topDown(final SyncDiff node, final List<SyncDiff> nodeList) {
    if (node == null) {
      return;
    }
    if (node != root) {
      // we dont want to return the root
      // because the root is the root of the sync operation
      nodeList.add(node);
    }
    if (node.hasChildren()) {
      final SyncDiff[] childs = node.getChildren();
      for (int i = 0; i < childs.length; i++) {
        topDown(childs[i], nodeList);
      }
    }
  }

  public SyncDiff[] getBottomUpList() {
    final List<SyncDiff> buList = new ArrayList<SyncDiff>();
    bottomUp(root, buList);
    return (SyncDiff[]) buList.toArray(new SyncDiff[buList.size()]);
  }

  private void bottomUp(final SyncDiff node, final List<SyncDiff> l) {
    if (node == null) {
      return;
    }
    if (node.hasChildren()) {
      final SyncDiff[] childs = node.getChildren();
      for (int i = 0; i < childs.length; i++) {
        bottomUp(childs[i], l);
      }
    }
    if (node != root) {
      // we dont want to return the root
      // because the root is the root of the sync operation
      l.add(node);
    }
  }

  public SyncDiff getRoot() {
    return root;
  }

  public void print() {
    if (root != null) {
      root.print(0);
    }
  }

}
