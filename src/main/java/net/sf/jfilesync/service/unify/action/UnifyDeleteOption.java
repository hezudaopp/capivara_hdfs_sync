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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.jfilesync.engine.BasicFile;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.service.unify.UnifyFile;
import net.sf.jfilesync.service.unify.UnifyFileTree;

public class UnifyDeleteOption extends UnifyOption {

  private UnifyActionRenderer renderer = new UnifyDeleteActionRenderer();

  public UnifyDeleteOption(UnifyFileTree fileTree) {
    super(fileTree);
  }

  @Override
  public String getID() {
    return "delete";
  }

  @Override
  public String getName() {
    return LanguageBundle.getInstance().getMessage("unify.option.delete");
  }

  @Override
  public UnifyActions getUnifyActions() {
    final UnifyActions actions = new UnifyActions();
    generateActions(actions);
    return actions;
  }

  private void generateActions(UnifyActions actions) {
    final List<UnifyFile> duplicateList = getUnifyFileTree().getDuplicateList();
    for (UnifyFile duplicate : duplicateList) {
      actions.addUnifyAction(new UnifyDeleteAction(duplicate));
    }
  }

  public List<BasicFile> getFilesToDelete() {
    final List<BasicFile> fileList = new ArrayList<BasicFile>();
    for (UnifyFile duplicate : getUnifyFileTree().getDuplicateList()) {
      fileList.add(duplicate);
    }
    return fileList;
  }

  @Override
  public HashMap<String, String> getDescriptions() {
    HashMap<String, String> map = new HashMap<String, String>();
    return map;
  }

  @Override
  public UnifyActionRenderer getUnifyActionRenderer() {
    return renderer;
  }


}
