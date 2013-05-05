/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2004 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TAbstractStandardDialog.java,v 1.4 2006/08/09 22:18:40 hunold Exp $
 */

package net.sf.jfilesync.gui.dialog;


import java.awt.Component;

import net.sf.gnocchi.GWorkerEvent;
import net.sf.jfilesync.prop.LanguageBundle;

public abstract class TAbstractStandardDialog extends AbstractWorkerDialog 
{

  private static final long serialVersionUID = 2406122983598061180L;

  public final static int 
	ACTION_COLLECT = 0,
	ACTION_COMPARE = 1,
	ACTION_DELETE  = 2;
  
  protected int type = ACTION_COLLECT;
  protected Component parent;
    
  public TAbstractStandardDialog(Component parent, int type) {
    this.parent = parent;
    this.type = type;
  }

  protected String getMsgByType() 
  {
    switch(type) {
    case ACTION_COLLECT:
      return LanguageBundle.getInstance().getMessage("message.collect_data");
    case ACTION_COMPARE:
      return LanguageBundle.getInstance().getMessage("message.compare_data");
    case ACTION_DELETE:
      return LanguageBundle.getInstance().getMessage("message.delete_data");
    default:
      return "please wait...";    	
    }
  }
  
  protected String trimMessage(String msg, int maxlength) {
    String back = msg;
    if( back.length() > maxlength )
      back = back.substring(back.length()-maxlength, back.length());
    return back;
  }
  
  public abstract void displayWorkerData(GWorkerEvent e);
  public abstract void enableGUIElement(boolean enable);
  
}
