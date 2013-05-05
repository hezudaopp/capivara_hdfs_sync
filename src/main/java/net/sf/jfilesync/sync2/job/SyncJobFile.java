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

import net.sf.jfilesync.engine.BasicFile;
import net.sf.jfilesync.engine.TFileProperties;
import net.sf.jfilesync.util.TPathControlInterface;

public class SyncJobFile extends BasicFile {

    public SyncJobFile(TFileProperties file, String rootPath,
            TPathControlInterface pathControl) {
        super(file, rootPath, pathControl);
        // TODO Auto-generated constructor stub
    }

    @Override
    public BasicFile clone() {
        // TODO Auto-generated method stub
        return null;
    }

}
