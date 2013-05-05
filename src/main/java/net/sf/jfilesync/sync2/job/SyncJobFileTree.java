/*
 * capivara - Java File Synchronization
 *
 * Created on Jul 10, 2010
 * Copyright (C) 2010 Sascha Hunold <hunoldinho@users.sourceforge.net>
 *
 <license/>
 *
 */
package net.sf.jfilesync.sync2.job;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.sf.jfilesync.engine.BasicFileTree;

public class SyncJobFileTree extends BasicFileTree implements TreeModel { 

    public SyncJobFileTree(SyncJobFile root) {
        super(root);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Object getChild(Object parent, int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getChildCount(Object parent) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isLeaf(Object node) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        // TODO Auto-generated method stub
        
    }

}
