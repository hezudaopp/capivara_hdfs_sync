/*
 * capivara - Java File Synchronization
 *
 * Created on 25-Jul-2005
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
 * $Id: FileTransfer2.java,v 1.42 2006/08/23 22:20:52 hunold Exp $
 */
package net.sf.jfilesync.engine;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.sf.jfilesync.gui.FileCopyProgressMonitor;
import net.sf.jfilesync.gui.dialog.ConfirmOverrideDialog;
import net.sf.jfilesync.gui.dialog.LinkExistsDialog;
import net.sf.jfilesync.gui.swing.ComponentVisibleRunner;
import net.sf.jfilesync.io.CapivaraFileInputStream;
import net.sf.jfilesync.io.CapivaraFileOutputStream;
import net.sf.jfilesync.io.CopyStatusStream;
import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.util.TPathControlInterface;

public class FileTransfer2 implements Runnable, ActionListener {

  private final Frame owner;
  private final TFileProperties[] files;
  private final AbstractConnectionProxy source, target;
  private final String sourceRoot, targetRoot;
  private boolean cancelled;
  private boolean checkForIncompleteFile = false;
  private Thread copyThread;
  private boolean transferDone = false;
  private CopyProgressListener monitor = null;
  private final java.util.List<CopyProgressListener> progressListeners = new ArrayList<CopyProgressListener>();
  private final java.util.List<FileCopyListener> fileCopyListeners = new ArrayList<FileCopyListener>();

  private boolean ignoreErrors = false;

  public final static int OPTION_PRESERVE_MTIME = 0x1;
  public final static int OPTION_PRESERVE_PERMISSIONS = 0x2;
  // public final static int OPTION_FOLLOW_SYMLINKS = 0x4;

  private int options = 0;

  public final static int MODE_OVERRIDE_UNKNOWN = 0;
  public final static int MODE_OVERRIDE_ALL = 1;
  public final static int MODE_OVERRIDE_SKIP_ALL = 2;

  private ConfirmOverrideDialog cod;
  private int mode_override = MODE_OVERRIDE_UNKNOWN;
  private boolean override = false;

  private boolean custom_target_permissions = false;
  private int target_dir_permissions = -1;
  private int target_file_permissions = -1;

  private final static Logger LOGGER = Logger.getLogger(FileTransfer2.class.getName());

  // for mtime of dirs
  private final HashMap<String, TFileProperties> dirHash = new HashMap<String, TFileProperties>();
  private final List<String> dirList = new ArrayList<String>();

  // when on target a link exists and the user chooses to skip
  // to copy this directory, store it here
  private final List<String> dirSkipList = new ArrayList<String>();

  private final LanguageBundle lang = LanguageBundle.getInstance();

  public FileTransfer2(Frame owner, AbstractConnectionProxy sourceCon, String sourceRoot,
      AbstractConnectionProxy targetCon, String targetRoot, TFileProperties[] files) {
    this.owner = owner;
    this.source = sourceCon;
    this.target = targetCon;
    this.sourceRoot = sourceRoot;
    this.targetRoot = targetRoot;
    this.files = files;
  }

  public FileTransfer2(AbstractConnectionProxy sourceCon, String sourceRoot, AbstractConnectionProxy targetCon,
      String targetRoot, TFileProperties[] files) {
    this(null, sourceCon, sourceRoot, targetCon, targetRoot, files);
  }

  public synchronized void startCopying() {

    transferDone = false;

    cancelled = false;
    copyThread = new Thread(this);
    copyThread.start();

    if (monitor != null) {
      monitor.startup();
    }

    while (!transferDone) {
      try {
        wait();
      } catch (InterruptedException e) {
      }
    }

  }

  public void addOption(int option) {
    options |= option;
  }

  public boolean isOptionEnabled(int option) {
    return ((options & option) > 0);
  }

  public void setOverrideMode(int mode) {
    mode_override = mode;
  }

  public int getOverrideMode() {
    return mode_override;
  }

  public void setTargetPermissions(final int dirPermissions, final int filePermissions) {
    if (dirPermissions != -1 && filePermissions != -1) {
      custom_target_permissions = true;
      target_dir_permissions = dirPermissions;
      target_file_permissions = filePermissions;
    }
  }

  public void setMonitor(final CopyProgressListener monitor) {
    this.monitor = monitor;
    if (monitor instanceof FileCopyProgressMonitor) {
      ((FileCopyProgressMonitor) monitor).setActionListener(this);
    }
    addCopyProgressListener(monitor);
  }

  public void run() {

    String targetName = null;
    TFileProperties sourceFile = null;

    if (files == null || files.length == 0) {
      endOfTransfer();
    }

    long filesKb = 0L;
    for (int i = 0; i < files.length; i++) {
      if (!files[i].isDirectory()) {
        filesKb += files[i].getFileSize().longValue() / 1024L;
      }
    }
    if (!source.isLocalConnection() && !target.isLocalConnection()) {
      filesKb *= 2;
    }

    // try to enter source and target dir first
    try {
      source.chdir(sourceRoot);
      target.chdir(targetRoot);
    } catch (IOException ex) {
      LOGGER.severe("Cannot change directory");
      TErrorHandling.reportError(TErrorHandling.ERROR_IO_GENERAL, "Cannot change directory");
      cancelled = true;
      endOfTransfer();
      return;
    }

    LOGGER.fine("starting transfer...");

    FileCopyStatistics stats = new FileCopyStatistics(files.length, filesKb);
    stats.start();

    FileProgressObserver progressThread = new FileProgressObserver(stats);
    progressThread.start();

    transferDone = false;

    for (int i = 0; i < files.length && !cancelled; i++) {

      sourceFile = files[i];
      if (monitor != null) {
        monitor.setCurrentFileName(sourceFile.getFileName());
      }

      fireStartCopying(files[i].getAbsoluteFileName());

      final TFileProperties file = sourceFile;
      // final String nextFilePath = file.getAbsoluteFileName();

      final String plainPath = getPlainPath(file);
      targetName = getTargetName(plainPath);
      // LOGGER.info("target name " + targetName);

      if (isInSkipList(files[i].getAbsoluteFileName())) {
        // do nothing but update stats
        long fsize = files[i].getFileSize().longValue() / 1024L;
        stats.finishFile(fsize);
        updateStatistics(stats);
      } else if (file.isDirectory()) {

        if (!target.exists(targetName)) {
          makeDirectory(source, target, targetName, files[i]);
        } else {

          boolean isLink = false;
          boolean isFile = false;

          isLink = handleIfTargetIsLink(source, target, targetName, files[i]);
          if (!isLink && !cancelled) {
            isFile = handleIfTargetIsFile(target, targetName, files[i]);
            if (!isFile && !cancelled) {
              final boolean override = getOverrideFlag(files[i], targetName);
              if (override) {
                makeDirectory(source, target, targetName, files[i]);
              } else {
                dirSkipList.add(files[i].getAbsoluteFileName());
              }
            }
          }

        }

      } // end dir
      else { // start files

        if (target.exists(targetName)) {
          try {
            if (target.isLink(targetName)) {
              int linkChoice = showLinkExistsDialog(owner, targetName);
              switch (linkChoice) {
              case LinkExistsDialog.OPTION_CANCEL:
                cancelled = true;
                override = false;
                break;
              case LinkExistsDialog.OPTION_DELETE:
                deleteLink(target, targetName);
                override = true;
                break;
              case LinkExistsDialog.OPTION_SKIP:
                override = false;
                break;
              }
            } else {
              override = getOverrideFlag(sourceFile, targetName);
            }
          } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage() + ", " + targetName, e);

            boolean cont = showContinueDialog(owner, lang.getMessage("error.io.islink") + ": " + targetName,
                e.getMessage(), lang.getMessage("button.label.skip_file") + "?", lang.getMessage("label.ioerror"));
            if (!cont) {
              cancelled = true;
            }
          }
        } else {
          override = true;
        }

        /*
         * now check if we got enough permissions to write the target
         * 
         * if target exists check permissions of the file if not check
         * permissions of parent directory TODO permission check
         */

        if (override) {
          try {
            copyFile(source, target, sourceFile, targetName, progressThread);
          } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IOException in copyFile: " + e.getMessage(), e);

            boolean cont = false;

            if (!source.isConnected()) {
              JOptionPane.showMessageDialog(owner, "Connection lost");
            } else if (!target.isConnected()) {
              JOptionPane.showMessageDialog(owner, "Connection lost");
            } else {
              cont = showContinueDialog(owner,
                  lang.getMessage("label.ioerror") + ": " + sourceFile.getAbsoluteFileName(), e.getMessage(), null,
                  lang.getMessage("label.ioerror"));
            }

            if (!cont) {
              cancelled = true;
              checkForIncompleteFile = true;
            }
          }
        }

        long fsize = files[i].getFileSize().longValue() / 1024L;
        stats.finishFile(fsize);
        updateStatistics(stats);

      } // end is file

      setFileProgress(100);
      setRelativeProgress(i + 1, files.length, stats.getRelativeOverallProgress());

      fireFinishCopying(files[i].getAbsoluteFileName());
    } // end for

    if (!cancelled) {
      // set mtime/permissions for saved directories
      setDirectoryFlags(target);
    } else {
      // thread was canceled
      // check if an incomplete file exist
      if (checkForIncompleteFile) {
        checkIncompleteFile(sourceFile, targetName);
      }
    }

    progressThread.shutdown();

    if (monitor != null) {
      monitor.setFinishedState();
      boolean closeMonitor = monitor.closeAfterCompletion();
      if (closeMonitor) {
        monitor.shutdown();
        // SwingUtilities.invokeLater(new ComponentVisibleRunner(monitor,
        // false));
      }
    }

    endOfTransfer();
  }

  private void endOfTransfer() {
    setRelativeProgress(files.length, files.length, 100);
    synchronized (this) {
      transferDone = true;
      notifyAll();
    }
  }

  private boolean getOverrideFlag(final TFileProperties sourceFile, final String targetName) {
    boolean flag = false;

    switch (mode_override) {
    case MODE_OVERRIDE_ALL:
      flag = true;
      break;
    case MODE_OVERRIDE_SKIP_ALL:
      break;
    case MODE_OVERRIDE_UNKNOWN:
      // ask again
      cod = new ConfirmOverrideDialog(owner, sourceFile.getFileName(), false);

      try {
        SwingUtilities.invokeAndWait(new Runnable() {
          public void run() {
            cod.setVisible(true);
          }
        });
      } catch (InterruptedException e) {
        LOGGER.severe(e.getMessage());
      } catch (InvocationTargetException e) {
        LOGGER.severe(e.getMessage());
      }

      int choice = cod.getUserChoice();
      switch (choice) {
      case ConfirmOverrideDialog.OPTION_OVERRIDE:
        mode_override = MODE_OVERRIDE_UNKNOWN;
        flag = true;
        break;
      case ConfirmOverrideDialog.OPTION_OVERRIDE_ALL:
        mode_override = MODE_OVERRIDE_ALL;
        flag = true;
        break;
      case ConfirmOverrideDialog.OPTION_SKIP:
        mode_override = MODE_OVERRIDE_UNKNOWN;
        break;
      case ConfirmOverrideDialog.OPTION_SKIP_ALL:
        mode_override = MODE_OVERRIDE_SKIP_ALL;
        break;
      case ConfirmOverrideDialog.OPTION_CANCEL:
        cancelled = true;
        break;
      }
      break;
    }

    return flag;
  }

  private void setDirectoryFlags(final AbstractConnectionProxy targetCon) {

    while (!dirList.isEmpty()) {

      // have to start from the end
      // that's not really nice
      // but until I change it to a tree structure
      // that's the only way
      String dirName = (String) dirList.remove(dirList.size() - 1);
      TFileProperties dirFile = (TFileProperties) dirHash.get(dirName);

      if (isOptionEnabled(OPTION_PRESERVE_MTIME)) {
        try {
          setModificationTime(targetCon, dirName, dirFile.getFileModTime());
          // targetCon.setModificationTime(dirName, dirFile.getFileModTime());
        } catch (IOException e) {
          LOGGER.warning(e.getMessage());
        }
      }

      if (isOptionEnabled(OPTION_PRESERVE_PERMISSIONS)) {
        int perm = dirFile.getAttributes().getPermissions();
        try {
          targetCon.setPermissions(dirName, perm);
        } catch (IOException e) {
          LOGGER.warning("cannot set permissions for " + dirName);
        }
      } else if (custom_target_permissions) {
        try {
          targetCon.setPermissions(dirName, target_dir_permissions);
        } catch (IOException e) {
          LOGGER.warning("cannot set permissions for " + dirName);
        }
      }

      // preserve hidden flag if possible
      if (dirFile.isHidden()) {
        try {
          // supported by Windows plug-in
          targetCon.setHidden(dirName);
        } catch (IOException e) {
          LOGGER.warning("cannot set hidden flag for " + dirName);
        }
      }

    }

  }

  private void setModificationTime(final AbstractConnectionProxy con, final String fileName, final long modtime)
      throws IOException {

    if (modtime > 0L) {
      con.setModificationTime(fileName, modtime);
    } else {

      try {
        SwingUtilities.invokeAndWait(new Runnable() {
          public void run() {
            JOptionPane.showMessageDialog(owner, "Suspicious modification time for file " + fileName);
          }
        });
      } catch (InterruptedException e) {
        LOGGER.severe(e.getMessage());
      } catch (InvocationTargetException e) {
        LOGGER.severe(e.getMessage());
      }

    }

  }

  private int showLinkExistsDialog(final Frame owner, final String linkName) {
    int choice = LinkExistsDialog.OPTION_CANCEL;
    LinkExistsDialog dialog = new LinkExistsDialog(owner, linkName);
    try {
      SwingUtilities.invokeAndWait(new ComponentVisibleRunner(dialog, true));
    } catch (InterruptedException e) {
      LOGGER.severe(e.getMessage());
    } catch (InvocationTargetException e) {
      LOGGER.severe(e.getMessage());
    }
    choice = dialog.getChosenOption();
    return choice;
  }

  private void copyFile(final AbstractConnectionProxy sourceCon, final AbstractConnectionProxy targetCon,
      final TFileProperties sourceFile, final String targetName, final FileProgressObserver observer)
      throws IOException {

    if (!sourceCon.isLocalConnection() && !targetCon.isLocalConnection()) {
      // remote to remote transfer
      File tmpFile = File.createTempFile("current", null);

      observer.setCopySteps(2);

      getFile(sourceCon, targetCon, sourceFile.getAbsoluteFileName(), sourceFile.getFileSize().longValue(),
          tmpFile.getAbsolutePath(), observer);

      putFile(targetCon, sourceCon, tmpFile.getAbsolutePath(), tmpFile.length(), targetName, observer);

      tmpFile.delete();

    } else {

      observer.setCopySteps(1);

      if (sourceCon.isLocalConnection()) {
        putFile(targetCon, sourceCon, sourceFile.getAbsoluteFileName(), sourceFile.getFileSize().longValue(),
            targetName, observer);
      } else {
        getFile(sourceCon, targetCon, sourceFile.getAbsoluteFileName(), sourceFile.getFileSize().longValue(),
            targetName, observer);
      }

    }

    if (!cancelled) {

      if (isOptionEnabled(OPTION_PRESERVE_MTIME)) {
        LOGGER.info("Set modification time");
        try {
          setModificationTime(targetCon, targetName, sourceFile.getFileModTime());
        } catch (IOException e) {
          LOGGER.warning("IO error in set modtime. Error: " + e.getMessage());
        }
      }

      if (isOptionEnabled(OPTION_PRESERVE_PERMISSIONS)) {

        int perm = sourceFile.getAttributes().getPermissions();
        try {
          targetCon.setPermissions(targetName, perm);
        } catch (IOException e) {
          LOGGER.warning("cannot set permissions for " + targetName);
        }

      } else if (custom_target_permissions) {
        try {
          targetCon.setPermissions(targetName, target_file_permissions);
        } catch (IOException e) {
          LOGGER.warning("cannot set permissions for " + targetName);
        }

      }

      // preserve hidden flag on dest file
      if (sourceFile.isHidden()) {
        targetCon.setHidden(targetName);
      }

    } // end if( ! cancelled )

  }

  private void putFile(final AbstractConnectionProxy targetCon, final AbstractConnectionProxy sourceCon,
      final String sourceName, final long sourceFileSize, final String targetName, final FileProgressObserver observer)
      throws IOException {
    CapivaraFileInputStream instream = sourceCon.getFileInputStream(sourceName);
    try {
      observer.startObservingStream(instream, sourceFileSize);
      targetCon.put(instream, targetName);
    } finally {
      observer.stopObservingChannel();
      instream.close();
    }
  }

  private void getFile(final AbstractConnectionProxy targetCon, final AbstractConnectionProxy sourceCon,
      final String sourceName, long sourceSize, final String targetName, final FileProgressObserver observer)
      throws IOException {
    CapivaraFileOutputStream outstream = sourceCon.getFileOutputStream(targetName);
    String remoteFileName = targetCon.getEncodedFileName(sourceName);
    try {
      observer.startObservingStream(outstream, sourceSize);
      targetCon.get(remoteFileName, outstream);
    } finally {
      observer.stopObservingChannel();
      outstream.close();
    }
  }

  private String getPlainPath(final TFileProperties file) {
    String plainPath = null;
    if (source.isLocalConnection()) {
      plainPath = source.getPathControlInstance().plainpath(sourceRoot, file.getAbsoluteFileName());
      if (!target.isLocalConnection()) {
        plainPath = source.getPathControlInstance().getNetPath(plainPath);
      }
    } else {
      plainPath = source.getPathControlInstance().netPlainpath(sourceRoot, file.getAbsoluteFileName());
    }
    return plainPath;
  }

  private String getTargetName(final String path) {
    String tName = null;

    if (target.isLocalConnection()) {
      tName = target.getPathControlInstance().appendDirectory(targetRoot, path);
    } else {
      tName = target.getPathControlInstance().netAppendDirectory(targetRoot, path);
    }

    return tName;
  }

  private void deleteLink(final AbstractConnectionProxy con, final String path) {
    try {
      con.remove(path);
    } catch (IOException e) {
      boolean cont = showContinueDialog(owner, lang.getMessage("error.io.delete_link") + ": " + path, e.getMessage(),
          lang.getMessage("warn.continue.harm_target"), lang.getMessage("label.ioerror"));
      if (!cont) {
        cancelled = true;
      }
    }
  }

  private void makeDirectory(final AbstractConnectionProxy sourceCon, final AbstractConnectionProxy targetCon,
      final String path, final TFileProperties file) {
    try {

      if (!target.exists(path)) {
        LOGGER.fine("mkdir path : " + path);
        target.mkdirs(path);
      }

      /*
       * mtime/permissions for directories workaround because I dont have the
       * files in a tree structure store each dir and set the mod time at the
       * end
       */

      if (isOptionEnabled(OPTION_PRESERVE_MTIME) || isOptionEnabled(OPTION_PRESERVE_PERMISSIONS)
          || custom_target_permissions) {
        dirHash.put(path, file);
        dirList.add(path);
      }

    } catch (IOException e) {
      LOGGER.severe(e.getMessage());
      boolean cont = showContinueDialog(owner, lang.getMessage("error.io.mkdir") + path, e.getMessage(),
          lang.getMessage("quest.cont_and_skip_dir"), lang.getMessage("label.ioerror"));
      if (!cont) {
        cancelled = true;
      } else {
        dirSkipList.add(file.getAbsoluteFileName());
      }
    }
  }

  private boolean handleIfTargetIsLink(final AbstractConnectionProxy sourceCon,
      final AbstractConnectionProxy targetCon, final String targetName, final TFileProperties sourceFile) {
    boolean ret = false;
    try {
      if (targetCon.isLink(targetName)) {
        ret = true;
        int linkChoice = showLinkExistsDialog(owner, targetName);
        switch (linkChoice) {
        case LinkExistsDialog.OPTION_CANCEL:
          cancelled = true;
          break;
        case LinkExistsDialog.OPTION_DELETE:
          deleteLink(targetCon, targetName);
          makeDirectory(sourceCon, targetCon, targetName, sourceFile);
          break;
        case LinkExistsDialog.OPTION_SKIP:
          LOGGER.info("skip directory : " + sourceFile.getAbsoluteFileName());
          dirSkipList.add(sourceFile.getAbsoluteFileName());
          break;
        }
      }
    } catch (IOException e) {
      LOGGER.severe(e.getMessage() + ", " + targetName);
      boolean cont = showContinueDialog(owner, lang.getMessage("error.io.islink") + ": " + targetName, e.getMessage(),
          lang.getMessage("warn.continue.harm_target"), lang.getMessage("label.ioerror"));
      if (!cont) {
        cancelled = true;
      }
    }

    return ret;
  }

  private boolean handleIfTargetIsFile(final AbstractConnectionProxy con, final String targetName,
      final TFileProperties sourceFile) {
    boolean ret = false;

    try {
      if (con.isFile(targetName)) {
        ret = true;

        final String[] options = new String[] { lang.getMessage("button.label.skip_dir"),
            lang.getMessage("button.label.delete_target"), lang.getMessage("label.cancel") };

        int n = JOptionPane.showOptionDialog(owner, lang.getMessage("msg.file_exists") + ": " + targetName,
            lang.getMessage("msg.file_exists"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
            options[0]);

        if (n == 2) {
          cancelled = true;
        } else if (n == 1) {
          try {
            con.remove(targetName);
          } catch (IOException e) {
            dirSkipList.add(sourceFile.getAbsoluteFileName());

            final String[] opts = { lang.getMessage("button.label.skip_dir"), lang.getMessage("label.cancel") };
            JOptionPane.showOptionDialog(
                owner,
                lang.getMessage("error.io.delete_file") + " " + targetName + "\n"
                    + lang.getMessage("label.system_error") + ": " + e.getMessage() + "\n"
                    + lang.getMessage("button.label.skip_dir") + " " + sourceFile.getAbsoluteFileName() + "?",
                lang.getMessage("label.continue"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                opts, opts[0]);
          }
        } else {
          dirSkipList.add(sourceFile.getAbsoluteFileName());
        }
      }
    } catch (IOException e) {
      LOGGER.severe(e.getMessage() + ", " + targetName);
      boolean cont = showContinueDialog(owner, lang.getMessage("error.io.isfile") + ": " + targetName, e.getMessage(),
          lang.getMessage("warn.continue.harm_target"), lang.getMessage("label.ioerror"));
      if (!cont) {
        cancelled = true;
      }
    }

    return ret;
  }

  private boolean isInSkipList(final String fileName) {
    boolean ret = false;
    for (final String dir : dirSkipList) {
      if (fileName.indexOf(dir) == 0) {
        ret = true;
        break;
      }
    }
    return ret;
  }

  public synchronized void cancelTransfer() {
    cancelled = true;
    if (copyThread != null) {
      checkForIncompleteFile = true;
      try {
        if (source != null) {
          source.abort();
        }
        if (target != null) {
          target.abort();
        }
      } catch (IOException e) {
        LOGGER.warning(e.getMessage());
      }
    }
  }

  public synchronized boolean wasCancelled() {
    return cancelled;
  }

  private void checkIncompleteFile(final TFileProperties sourceFile, final String targetFile) {

    if (!target.isConnected()) {
      TErrorHandling.reportError(TErrorHandling.ERROR_IO_GENERAL, lang.getMessage("error.connection.lost"));
    } else {

      try {

        TPathControlInterface pc = target.getPathControlInstance();
        final String targetPath = pc.getPathLevelUp(targetFile);

        LOGGER.info("check dir " + targetPath);
        TFileData tdata = target.ls(targetPath);

        TFileProperties tfile = null;
        for (int i = 0; tdata != null && i < tdata.getNumberofFiles(); i++) {
          if (tdata.getFileProperties(i).getAbsoluteFileName().equals(targetFile)) {
            tfile = tdata.getFileProperties(i);
            break;
          }
        }

        if (tfile == null) {
          // last file has not already been created on target
          return;
        }

        if (!tfile.getFileSize().equals(sourceFile.getFileSize())) {
          int res = JOptionPane.showConfirmDialog(owner,
              lang.getMessage("copy.confirm.delete_incomplete") + targetFile, lang.getMessage("label.delete"),
              JOptionPane.YES_NO_OPTION);

          if (res == JOptionPane.YES_OPTION) {
            target.remove(targetFile);
          }
        }

      } catch (IOException e) {
        TErrorHandling.reportError(TErrorHandling.ERROR_IO_GENERAL, e.getMessage());
      }

    } // end else

  } // end checkIncompleteFile

  private boolean showContinueDialog(final Frame owner, final String errorMsg, final String exceptionMsg,
      final String question, final String title) {
    boolean ret = false;

    if (ignoreErrors) {
      return true;
    }

    String[] options = new String[] { lang.getMessage("label.continue"),
        lang.getMessage("copy.option.continue_always"), lang.getMessage("label.cancel") };

    String msg = lang.getMessage("label.error") + ": " + errorMsg + "\n" + lang.getMessage("label.system_error") + ": "
        + exceptionMsg;
    if (question != null) {
      msg += "\n" + question;
    }

    int n = JOptionPane.showOptionDialog(owner, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
        null, options, options[0]);
    if (n == JOptionPane.CLOSED_OPTION) {
      ret = false;
    } else if (n != 2) {
      // user didn't click cancel
      // let's continue
      ret = true;
      if (n == 1) {
        ignoreErrors = true;
      }
    }
    return ret;
  }

  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if (cmd.equals("cancel")) {
      cancelTransfer();
    }
  }

  public synchronized void addCopyProgressListener(CopyProgressListener listener) {
    progressListeners.add(listener);
  }

  public synchronized void addFileCopyListener(FileCopyListener listener) {
    fileCopyListeners.add(listener);
  }

  private void fireStartCopying(final String filename) {
    for (FileCopyListener l : fileCopyListeners) {
      l.startCopying(filename);
    }
  }

  private void fireFinishCopying(final String filename) {
    for (FileCopyListener l : fileCopyListeners) {
      l.finishCopying(filename);
    }
  }

  /*
   * private void setOverallProgress(final int progress) { Iterator it =
   * progressListeners.iterator(); while( it.hasNext() ) {
   * ((CopyProgressListener)it.next()).setOverallProgress(progress); } }
   */

  private void setFileProgress(final int progress) {
    for (final CopyProgressListener l : progressListeners) {
      l.setCurrentFileProgress(progress);
    }
  }

  private void setRelativeProgress(final int filePos, final int fileNum, final int progress) {
    for (final CopyProgressListener l : progressListeners) {
      l.setExtendedOverallProgress(filePos, fileNum, progress);
    }
  }

  private void updateStatistics(final FileCopyStatistics stats) {
    for (final CopyProgressListener l : progressListeners) {
      l.setAverageTransferRate(stats.getAverageTransferRate());
      l.setSecondsElapsed(stats.getSecondsElapsed());
      l.setSecondsEstimated(stats.getEstimatedSecondsToComplete());
    }
  }

  // ---------------------------------------------------------------------------

  class FileProgressObserver extends Thread {
    private boolean working;
    private boolean observe;
    private CopyStatusStream stream;
    private long fileSize;
    private final FileCopyStatistics statistics;
    private int copySteps = 1, currentStep;
    private long byteCache;

    FileProgressObserver(final FileCopyStatistics statistics) {
      this.statistics = statistics;
      working = true;
      observe = false;
    }

    public void run() {
      synchronized (this) {
        while (working) {
          while (!observe && working) {
            try {
              wait();
            } catch (InterruptedException e) {
            }
          }
          if (observe && working) {
            try {
              if (stream == null) {
                observe = false;
              } else if (fileSize > 0 && copySteps > 0) {
                final long bytesDone = byteCache + stream.getBytesDone();
                final int progress = (int) ((bytesDone * 100L) / (copySteps * fileSize));
                final long kbDone = bytesDone / 1024L;
                statistics.updateFile(kbDone);

                setFileProgress(progress);
                updateStatistics(statistics);
              }
            } catch (ClosedChannelException e) {
              observe = false;
            } catch (IOException e) {
              // quietly consume IO exception thrown by position()
              // log.warning(e.getMessage());
            }
            try {
              wait(100);
            } catch (InterruptedException e) {
            }
          }
        }
      }
    }

    public synchronized void startObservingStream(final CopyStatusStream stream, final long fileSize) {
      this.stream = stream;
      this.fileSize = fileSize;
      currentStep++;
      observe = true;
      notify();
    }

    public synchronized void stopObservingChannel() {
      if (currentStep < copySteps) {
        byteCache = this.fileSize;
      } else {
        byteCache = 0;
        currentStep = 0;
      }
      stream = null;
      observe = false;
      notify();
    }

    public synchronized void shutdown() {
      observe = false;
      working = false;
      notify();
    }

    public synchronized void setCopySteps(int steps) {
      copySteps = steps;
    }

    // private void setFileProgress(final int progress) {
    // for(final CopyProgressListener l : progressListeners) {
    // l.setCurrentFileProgress(progress);
    // }
    // }
    //
    // private void updateStatistics() {
    // for(final CopyProgressListener l : progressListeners) {
    // l.setAverageTransferRate(statistics.getAverageTransferRate());
    // l.setSecondsElapsed(statistics.getSecondsElapsed());
    // l.setSecondsEstimated(statistics.getEstimatedSecondsToComplete());
    // }
    // }

  }

  // ---------------------------------------------------------------------------

  static class FileCopyStatistics {

    // private final int fileNum;
    private final long sizeOfFilesInKb;

    private float avgRate;
    private long startTimeStamp;
    private long kbDone;
    private long secsToComplete = -1;

    public FileCopyStatistics(final int fileNum, final long sizeOfFilesInKb) {
      // this.fileNum = fileNum;
      this.sizeOfFilesInKb = sizeOfFilesInKb;
    }

    public synchronized void start() {
      avgRate = 0.0f;
      startTimeStamp = System.currentTimeMillis();
      kbDone = 0L;
    }

    public synchronized void updateFile(final long transferredKb) {
      long tmpKb = kbDone + transferredKb;
      updateStats(tmpKb);
    }

    public synchronized void finishFile(final long fileSizeKb) {
      kbDone += fileSizeKb;
      updateStats(kbDone);
    }

    private void updateStats(final long kbDone) {
      long millisElapsed = getMillisElapsed();
      float tmpAvgRate = computeAverageRate(kbDone, millisElapsed);
      setAverageTransferRate(tmpAvgRate);
      long tmpSecsToGo = computeSecondsToComplete(tmpAvgRate, kbDone);
      setEstimatedSecondsToComplete(tmpSecsToGo);
    }

    private float computeAverageRate(long kb, long millisElapsed) {
      float averageRate = -1.0f;
      if (millisElapsed > 0) {
        averageRate = (float) ((double) (kb * 1000L) / (double) millisElapsed);
      }
      return averageRate;
    }

    private long computeSecondsToComplete(final float rate, final long kb) {
      long secsToGo = -1;

      // to make the time not jumping up and down because
      // of decimal digits -> cut them off
      float nrate = ((int) rate);
      if (nrate == 0.0f) {
        nrate = rate;
      }

      if (nrate > 0.0f) {
        secsToGo = (long) ((double) (sizeOfFilesInKb - kb) / (double) nrate);
      }
      return secsToGo;
    }

    private long getMillisElapsed() {
      return (System.currentTimeMillis() - startTimeStamp);
    }

    private void setAverageTransferRate(float val) {
      avgRate = val;
    }

    public float getAverageTransferRate() {
      return avgRate;
    }

    private void setEstimatedSecondsToComplete(long val) {
      secsToComplete = val;
    }

    public long getEstimatedSecondsToComplete() {
      return secsToComplete;
    }

    public long getSecondsElapsed() {
      long secs = (System.currentTimeMillis() - startTimeStamp) / 1000L;
      return secs;
    }

    public int getRelativeOverallProgress() {
      int ret = 100;
      if (sizeOfFilesInKb > 0) {
        ret = (int) (kbDone * 100L / sizeOfFilesInKb);
      }
      return ret;
    }

  }

}
