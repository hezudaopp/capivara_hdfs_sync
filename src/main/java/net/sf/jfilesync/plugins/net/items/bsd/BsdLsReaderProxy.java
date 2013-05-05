/*
 * capivara - Java File Synchronization
 *
 * Created on Sep 8, 2010
 * Copyright (C) 2006 Sascha Hunold <hunoldinho@users.sourceforge.net>
 *
 <license/>
 *
 * $Id$
 */
package net.sf.jfilesync.plugins.net.items.bsd;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jfilesync.engine.TFileData;
import net.sf.jfilesync.engine.TFileProperties;
import net.sf.jfilesync.plugins.net.items.bsd.generated.BsdLsLexer;
import net.sf.jfilesync.plugins.net.items.bsd.generated.BsdLsParser;
import net.sf.jfilesync.util.TPathControlInterface;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;


public class BsdLsReaderProxy {

  private final TPathControlInterface pci;
  private final static Logger LOG = Logger.getLogger(BsdLsReaderProxy.class.getName());

  public BsdLsReaderProxy(TPathControlInterface pci) {
    this.pci = pci;
  }
      
  public TFileData ls(String path, String lsOutput) throws IOException {
    final TFileData fileData = new TFileData();
    try {
      BsdLsLexer lexer = new BsdLsLexer(new ANTLRStringStream(lsOutput));
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      BsdLsParser parser = new BsdLsParser(tokens);
      for(TFileProperties file : parser.lsoutput(path, pci)) {
        if( ! file.getFileName().equals(".") && ! file.getFileName().equals("..") ) {
          if (file.getFileName().startsWith(".")) {
            file.setHiddenFlag(true);
          }
          if( LOG.isLoggable(Level.INFO) ) {
            LOG.info("add file: " + file.getAbsoluteFileName());
          }
          fileData.addFileProperties(file);
        }
      }
    } catch (RecognitionException e) {
      throw new IOException(e);
    }
    return fileData;
  }

}
