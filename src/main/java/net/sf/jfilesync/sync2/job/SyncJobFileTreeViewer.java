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

import javax.swing.JPanel;

public class SyncJobFileTreeViewer extends JPanel {
    
    private final SyncJobDataModel dataModel;
    
    public SyncJobFileTreeViewer(SyncJobDataModel dataModel) {
        this.dataModel = dataModel;
    }
    
    
    
}
