/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TMessage.java,v 1.3 2005/08/19 21:29:02 hunold Exp $
 */

package net.sf.jfilesync.event;

public interface TMessage {

  public enum ID {
    ANY_MESSAGE              ,
    SORT_FILE_LIST_MESSAGE   ,
    CHDIR_REQUEST_MESSAGE    ,
    CHDIR_REPLY_MESSAGE      ,
    CHDIR_FAILURE_MESSAGE    ,
    CONNECT_REQUEST_MESSAGE  ,
    CONNECT_REPLY_MESSAGE    ,
    CONNECT_FAILURE_MESSAGE  ,
    CONNECTION_LOST_MESSAGE  ,
    DISCONNECT_REQUEST_MESSAGE ,
    DISCONNECT_REPLY_MESSAGE   ,
    LS_REQUEST_MESSAGE         ,
    LS_REPLY_MESSAGE           ,
    LS_FAILURE_MESSAGE         ,
    LS_CANCELLED_MESSAGE       ,
    DELETE_OK_MESSAGE          ,
    DELETE_FAILURE_MESSAGE     ,
    DELETE_CANCELLED_MESSAGE   ,
    MKDIR_REQUEST_MESSAGE      ,
    MKDIR_REPLY_MESSAGE        ,
    MKDIR_FAILURE_MESSAGE      ,
    RELOAD_DIR_MESSAGE         ,
    SETTINGS_CHANGED_MESSAGE   ,
    LANGUAGE_CHANGED_MESSAGE   ,
    DESELECT_FILES_MESSAGE     ,
    COPY_FILES_MESSAGE         ,
    DELETE_FILES_MESSAGE       ,
    CONNECTION_STATE_MESSAGE   ,
    CONTROLCENTER_CHANGE_MESSAGE ,
    ERROR_MESSAGE                ,
    EXCEPTION_MESSAGE            ,
    SYNC_IGNORE_FILE_MESSAGE     ,
    SYNC_TREE_VIEW_UPDATE_MESSAGE,
    SYNC_UNHIDE_FILE_MESSAGE     ,
    SYNC_TREE_VIEW_KEEP_FILES_MESSAGE,
    JVM_STATS_MESSAGE            ,
    SYNC_PLUGIN_FILE_MESSAGE     ,
    QUICK_CONNECT_MESSAGE        ,
    SAVE_CONFIG_MESSAGE          ;
  }

  public ID getMessageType();

}
