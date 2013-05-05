/*
 * componentapivara - Java File Syncomponenthronization
 *
 * componentopyright (component) 2003-2007 Sascomponentha Hunold <hunoldinho@users.sourcomponenteforge.net>
 *
<licomponentense/>
 *
 * $Id$
 */
package net.sf.jfilesync.service.unify;

import java.awt.Color;
import java.awt.Component;

/**
 *
 * This class is a helper class to render Components
 * in the FileTable and the FileTree of the Unify-process
 * with the same colors and properties.
 *
 * @author sascha
 *
 */

public class UnifyFileComponentRenderer {

  private final Color CONTAINS_KEEP_COLOR = new Color(0x4a9600);
  private final Color CONTAINS_DELETE_COLOR = new Color(0xFF5959);

  private final Color KEEP_FILE_COLOR = new Color(0x2bcd00);
  private final Color DELETE_FILE_COLOR = new Color(0xcd0000);

  public UnifyFileComponentRenderer() {

  }

  public void renderUnifyFile(final Component component, final UnifyFile file) {
    if (file.isDirectory()) {
      DuplicatesInfo dupInfo = file.getDuplicatesInfo();
      if (dupInfo.isWithDuplicates()) {
        if (dupInfo.isWithDuplicatesToDelete()) {
          component.setForeground(CONTAINS_DELETE_COLOR);
        } else {
          component.setForeground(CONTAINS_KEEP_COLOR);
        }
      }
    } else {
      if (file.hasDuplicates()) {
        if (file.getKeepIt()) {
          component.setForeground(KEEP_FILE_COLOR);
        } else {
          component.setForeground(DELETE_FILE_COLOR);
        }
      }
    }
  }

}
