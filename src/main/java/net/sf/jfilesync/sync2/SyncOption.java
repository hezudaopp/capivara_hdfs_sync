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
 * $Id: SyncOption.java,v 1.6 2005/08/19 21:29:02 hunold Exp $
 */
package net.sf.jfilesync.sync2;

import java.util.List;

import net.sf.jfilesync.sync2.diffs.SyncDiffTree;

public interface SyncOption {

  public enum Direction { NOOP, LEFT_RIGHT, RIGHT_LEFT, BIDIRECTIONAL };
//  public int DIRECTION_LEFT_RIGHT   = 0;
//  public int DIRECTION_RIGHT_LEFT   = 1;
//  public int DIRECTION_BIDRECTIONAL = 2;

  public String getDescription();

  public SyncAction[] getLeftActionList(SyncDiffTree tree, Direction direction);
  public SyncAction[] getRightActionList(SyncDiffTree tree, Direction direction);

  public List<Direction> getSupportedDirections();

  public String getDirectionDescription(Direction direction);
}
