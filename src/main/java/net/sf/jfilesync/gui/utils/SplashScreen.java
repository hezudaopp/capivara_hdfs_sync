/*
 * SplashScreen.java - Splash screen
 * Copyright (C) 1998, 1999, 2000, 2001 Slava Pestov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 *
 * taken from the jEdit sources
 * changed by: Raik Nagel <raik.nagel@uni-bayreuth.de>
 * changed by: Sascha Hunold <hunoldinho@users.sourceforge.net>
 */

package net.sf.jfilesync.gui.utils;

import java.awt.*;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sf.jfilesync.settings.ConfigDefinitions;

/**
 * The splash screen displayed on startup.
 * 
 * This file only uses AWT APIs so that it can be displayed as soon as possible
 * after Capivara is launched.
 */

public class SplashScreen extends Canvas {

    private static final long serialVersionUID = 3545513989262947378L;
    private FontMetrics fmB;
    private FontMetrics fmL;
    private final JFrame win = new JFrame();
    private Image image;
    private Image offscreenImg;
    private Graphics offscreenGfx;
    private int progress;
    private static final int PROGRESS_HEIGHT = 20;

    private String versionStr;
    private String buildStr;
    private String actionStr;
    private Font bigFont;
    private Font littleFont;

    public SplashScreen() {
        versionStr = "Version " + ConfigDefinitions.PROGRAM_VERSION;
        buildStr = ConfigDefinitions.BUILD_DATE;
        actionStr = "init";

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        setBackground(Color.white);

        bigFont = new Font("Dialog", Font.BOLD, 11);
        littleFont = new Font("Dialog", Font.PLAIN, 10);
        setFont(bigFont);
        fmB = getFontMetrics(bigFont);
        fmL = getFontMetrics(littleFont);

        image = getToolkit().getImage(
                getClass().getResource(
                        "/net/sf/jfilesync/gui/icons/"
                                + ConfigDefinitions.SPLASH_IMAGE));
        MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(image, 0);

        try {
            tracker.waitForAll();
        } catch (Exception e) {
        }

        Dimension screen = getToolkit().getScreenSize();
        Dimension size = new Dimension(image.getWidth(this) + 2, image
                .getHeight(this)
                + 2 + PROGRESS_HEIGHT);
        win.setSize(size);

        win.setLayout(new BorderLayout());
        win.add(BorderLayout.CENTER, this);

        win.setLocation((screen.width - size.width) / 2,
                (screen.height - size.height) / 2);
        
        win.setUndecorated(true);
    }

    public void showSplash() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                win.setVisible(true);
            }
        });
    }

    public void hideSplash() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                win.setVisible(false);
            }
        });
    }

    public void disposeSplash() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                win.dispose();
            }
        });
    }

    public synchronized void advance() {
        progress++;
        repaint();
    }

    public synchronized void advance(String infoStr) {
        progress++;
        actionStr = infoStr;
        repaint();
    }


    public synchronized void paint(Graphics g) {

        if (offscreenImg == null) {
            offscreenImg = createImage(getSize().width, getSize().height);
            offscreenGfx = offscreenImg.getGraphics();
            offscreenGfx.setFont(getFont());
        }

        offscreenGfx.setColor(Color.GRAY);
        offscreenGfx.drawRect(0, 0, getSize().width - 1, getSize().height - 1);

        offscreenGfx.drawImage(image, 1, 1, this);

        int x1 = (getWidth() - fmB.stringWidth(versionStr)) / 2;
        int x2 = (getWidth() - fmB.stringWidth(buildStr)) / 2;

        // TODO: This should not be hardcoded
        offscreenGfx.setColor(new Color(168, 173, 189));
        offscreenGfx.fillRect(1, image.getHeight(this) + 1,
                ((win.getWidth() - 2) * progress) / 6, PROGRESS_HEIGHT);

        offscreenGfx.setColor(Color.BLACK);
        offscreenGfx.setFont(littleFont);
        offscreenGfx.drawString(actionStr, (getWidth() - fmL
                .stringWidth(actionStr)) / 2, image.getHeight(this)
                + PROGRESS_HEIGHT / 2 + 5);

        offscreenGfx.setFont(bigFont);
        // Version String
        // white shadow
        offscreenGfx.drawString(versionStr, x1, image.getHeight(this) - 110);
        offscreenGfx.drawString(buildStr, x2, image.getHeight(this) - 100);

        g.drawImage(offscreenImg, 0, 0, this);

        notifyAll();
    }

    public JFrame getSplashFrame() {
        return win;
    }

}
