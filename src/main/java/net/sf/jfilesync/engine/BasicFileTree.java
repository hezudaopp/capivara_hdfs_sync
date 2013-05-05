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
 */
package net.sf.jfilesync.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class BasicFileTree implements Iterable<BasicFile> {

  private final BasicFile root;

  private final static Logger LOGGER = Logger.getLogger(BasicFileTree.class
      .getName());

  /*
   * Take care!
   * The BasicFile root is just a dummy object.
   * Don't use it for operation. Hide it from the user!
   */

  public BasicFileTree(final BasicFile root) {
    this.root = root;
  }

  public BasicFile getRoot() {
    return root;
  }

  /**
   *
   * @return number of regular files
   */
  public int getNumberOfFiles() {
    return countFilesInDir(root);
  }

  // count regular files (not dirs)
  protected int countFilesInDir(BasicFile dir) {
    int fileNum = 0;
    for (BasicFile file : dir.getChildren()) {
      if( file.isDirectory() ) {
        fileNum += countFilesInDir(file);
      } else {
        fileNum++;
      }
    }
    return fileNum;
  }

  /**
   *
   * @return number of all BasicFiles in tree (without root)
   */

  public int getNumberOfNodes() {
    return countNumberOfNodes(this.root);
  }

  // count regular files (not dirs)
  protected int countNumberOfNodes(BasicFile dir) {
    int fileNum = 0;
    for (BasicFile file : dir.getChildren()) {
      fileNum++;
      if( file.isDirectory() ) {
        fileNum += countFilesInDir(file);
      }
    }
    return fileNum;
  }

  public Iterator<BasicFile> iterator() {
    return new BasicFileIterator(root);
  }

  public void print() {
    Iterator<BasicFile> it = iterator();
    while(it.hasNext()) {
      it.next().print();
    }
  }

  public List<BasicFile> getFilesInTree() {
    final List<BasicFile> fileList = new ArrayList<BasicFile>();
    for(BasicFile file : this) {
      fileList.add(file);
    }
    return fileList;
  }


  class BasicFileIterator implements Iterator<BasicFile> {

    /*
     * That is not the best implementation.
     * It uses far too much memory.
     */

    private BasicFile root;
    private List<BasicFile> files = new ArrayList<BasicFile>();
    private int listPos = -1;

    public BasicFileIterator(BasicFile root) {
      this.root = root;
      traverseTree(root);
      LOGGER.info("elems in file tree: " + files.size());
    }

    protected void traverseTree(BasicFile node) {
      if( node != BasicFileIterator.this.root ) {
//        System.out.println("add at " + files.size() + " " + node.getAbsolutePath());
        files.add(node);
      }
      for (BasicFile child : node.getChildren()) {
        traverseTree(child);
      }
    }

    public boolean hasNext() {
      return ( listPos+1 < files.size() );
    }

    public BasicFile next() {
      listPos++;
      return files.get(listPos);
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }

  }

}
