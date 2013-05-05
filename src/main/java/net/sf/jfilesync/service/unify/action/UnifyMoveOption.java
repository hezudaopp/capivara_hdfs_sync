/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003-2007 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id$
 */
package net.sf.jfilesync.service.unify.action;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.jfilesync.engine.BasicFile;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.service.unify.UnifyFile;
import net.sf.jfilesync.service.unify.UnifyFileTree;

public class UnifyMoveOption extends UnifyOption {

  private final static Logger LOGGER = Logger.getLogger(UnifyMoveOption.class
      .getName());

  private String targetPath;
  private UnifyActionRenderer renderer = new UnifyMoveActionRenderer();

  public UnifyMoveOption(final UnifyFileTree fileTree) {
    super(fileTree);
  }

  @Override
  public String getID() {
    return "move";
  }

  @Override
  public String getName() {
    return LanguageBundle.getInstance().getMessage("unify.option.move");
  }

  @Override
  public UnifyActions getUnifyActions() {
    final UnifyActions actions = new UnifyActions();
    if(targetPath != null) {
      generateActions(actions, this.targetPath);
    }
    return actions;
  }

  private void generateActions(UnifyActions actions, String targetPath) {
    final List<UnifyFile> duplicateList = getUnifyFileTree().getDuplicateList();
    for (UnifyFile duplicate : duplicateList) {
      actions.addUnifyAction(new UnifyMoveAction(duplicate, targetPath));
    }
  }

  public String getTargetPath() {
    return targetPath;
  }

  public void setTargetPath(String targetPath) {
    this.targetPath = targetPath;
  }

  public String getSourcePath() {
    return getUnifyFileTree().getRoot().getAbsolutePath();
  }

  public UnifyFileTree buildSourceCopyBasicFileTree() {
    UnifyFileTree tree = null;

    if( this.targetPath != null ) {
      LOGGER.info("generate target file tree");
//      final Iterator<BasicFile> it = getUnifyFileTree().iterator();
      final UnifyFile sourceRoot = (UnifyFile)getUnifyFileTree().getRoot();
      final UnifyFile targetRoot = (UnifyFile)sourceRoot.clone();
      final String sourceRootPath = getUnifyFileTree().getRoot().getAbsolutePath();
//      changeRootPath(targetRoot, sourceRootPath, getTargetPath());
      tree = new UnifyFileTree(targetRoot);
      addDuplicatedFiles(sourceRoot, targetRoot, sourceRootPath, getTargetPath());
    }
    return tree;
  }


  private boolean addDuplicatedFiles(UnifyFile sourceFile, UnifyFile targetFile,
      String sourceRootPath, String targetRootPath) {

    boolean hasDuplicates = false;

    for (BasicFile f : sourceFile.getChildren()) {
//      LOGGER.info("check child " + f.getAbsolutePath());
      UnifyFile uF = (UnifyFile) f;

      if( uF.isDirectory() ) {
        final UnifyFile newTargetChild = uF.clone();
        boolean hasDirDuplicates = addDuplicatedFiles(uF, newTargetChild,
            sourceRootPath, targetRootPath);
        if( hasDirDuplicates ) {
          hasDuplicates = true;
//          changeRootPath(newTargetChild, sourceRootPath, targetRootPath);
          targetFile.addChild(newTargetChild);
        }
      } else if (uF.getKeepIt() == false) {
        hasDuplicates = true;
        final UnifyFile newTargetChild = uF.clone();
//        changeRootPath(newTargetChild, sourceRootPath, targetRootPath);
        targetFile.addChild(newTargetChild);
      }
    }

    return hasDuplicates;
  }


  @Override
  public HashMap<String, String> getDescriptions() {
    final HashMap<String, String> map = new HashMap<String, String>();
    map.put(LanguageBundle.getInstance().getMessage("unify.option.label"),
        LanguageBundle.getInstance().getMessage("unify.option.move"));
    map.put(LanguageBundle.getInstance().getMessage("unify.move.source"),
        getSourcePath());
    map.put(LanguageBundle.getInstance().getMessage("unify.move.target"),
        getTargetPath());
    return map;
  }

  @Override
  public UnifyActionRenderer getUnifyActionRenderer() {
    return renderer;
  }

//  private void changeRootPath(UnifyFile newTargetChild, String sourceRootPath,
//      String targetRootPath) {
//    newTargetChild.setRootPath(sourceRootPath, targetRootPath);
//  }

}
