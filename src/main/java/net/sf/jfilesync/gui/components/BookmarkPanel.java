/*
 * capivara - Java File Synchronization
 *
 * Created on 01-Apr-2006
 * Copyright (C) 2006 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
package net.sf.jfilesync.gui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import net.sf.jfilesync.gui.icons.TImageIconProvider;

public class BookmarkPanel extends JPanel implements MouseListener {

    private static final long serialVersionUID = 1L;
    private List<BookmarksListener> listeners = new ArrayList<BookmarksListener>();
    private BufferedImage image;
    private final int WIDTH = 20;
    private final int HEIGHT = 20;

    private ImageIcon icon = TImageIconProvider.getInstance().getImageIcon(
            TImageIconProvider.BOOKMARK_ICON);
    private boolean grayScaleMode = false;

    public BookmarkPanel() {
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        initUI();
    }

    private void initUI() {
        setBorder(BorderFactory.createEtchedBorder());
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setMaximumSize(new Dimension(WIDTH, HEIGHT));

        addMouseListener(this);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;

        Graphics2D graphic = image.createGraphics();
        graphic.clearRect(0, 0, WIDTH, HEIGHT);
        graphic.setPaint(super.getBackground());
        graphic.fillRect(0, 0, WIDTH, HEIGHT);
        graphic.drawImage(icon.getImage(), 0, 0, WIDTH, HEIGHT, this);

        if (grayScaleMode) {
            ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace
                    .getInstance(ColorSpace.CS_GRAY), null);
            colorConvert.filter(image, image);
        }
        g2D.clearRect(0, 0, WIDTH, HEIGHT);
        g2D.drawImage(image, 0, 0, this);
    }

    public void addBookmarksListener(BookmarksListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener");
        }
        listeners.add(listener);
    }

    public void fireBookmarkShowEvent(BookmarkShowEvent e) {
        Iterator<BookmarksListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().showBookmarks(e);
        }
    }

    public void mousePressed(MouseEvent e) {
        e.consume();
        fireBookmarkShowEvent(new BookmarkShowEvent(this, e));
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            addMouseListener(this);
            setGrayScale(false);
            repaint();
        } else {
            removeMouseListener(this);
            setGrayScale(true);
            repaint();
        }
    }

    private void setGrayScale(boolean grayScaleMode) {
        this.grayScaleMode = grayScaleMode;
    }

}
