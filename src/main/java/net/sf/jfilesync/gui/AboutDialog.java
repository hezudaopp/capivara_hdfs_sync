/*
 * AboutDialog.java - About jEdit dialog box
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2001, 2002 Slava Pestov
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
 * taken from the jEdit sources
 * changed by: Raik Nagel <raik.nagel@uni-bayreuth.de>
 * changed 2004 by Sascha Hunold <hunoldinho@users.sourceforge.net>
 *
 * $Id: AboutDialog.java,v 1.7 2006/08/29 19:58:19 hunold Exp $
 */

package net.sf.jfilesync.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import net.sf.jfilesync.gui.dialog.TLicenseDialog;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.settings.ConfigDefinitions;

public class AboutDialog extends JDialog implements ActionListener {
    
    private static final long serialVersionUID = 1L;
    private final JButton close = new JButton(LanguageBundle.getInstance()
            .getMessage("label.close"));
    private final JButton license = new JButton(LanguageBundle.getInstance()
            .getMessage("label.license"));

    private final AboutPanel aboutP = new AboutPanel();
    
    //private final Color textColor = new Color(96, 96, 96);
    private final Color textColor = Color.WHITE;
    
    
//    private final static Logger LOG = Logger.getLogger(AboutDialog.class
//            .getName());
    
    public AboutDialog(JFrame parent) {
        super(parent, /*LanguageBundle.getInstance().getMessage(
                "dialog.about.title")
                + */" VISG云同步" /*+ ConfigDefinitions.PROGRAM_NAME*/, true);  //改

        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(content);

        content.add(BorderLayout.CENTER, aboutP);


        close.addActionListener(this);
        close.setActionCommand("close");
        close.setFocusable(false);

        license.addActionListener(this);
        license.setActionCommand("license");
        license.setFocusable(false);

        getRootPane().setDefaultButton(close);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonPanel.add(close);
       // buttonPanel.add(license);
        content.add(BorderLayout.SOUTH, buttonPanel);

        pack();
                
        setResizable(false);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("close")) {
            setVisible(false);
            dispose();
        } else if (cmd.equals("license")) {
            showLicense();
        }
    }

    private void showLicense() {
        new TLicenseDialog(this).setVisible(true);
    }

    class AboutPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final Image image = new ImageIcon(getClass().getResource(
                "/net/sf/jfilesync/gui/icons/splash_2.jpg")).getImage();
        private final List<String> text = new ArrayList<String>();
        int scrollPosition;
        private AnimationThread thread;
        int maxWidth;
        FontMetrics fm;
        private final String versionStr = "version " + ConfigDefinitions.PROGRAM_VERSION;
        private final String buildStr = ConfigDefinitions.BUILD_DATE;

        // animated text positions
        public int TOP = 35; // offset from top - hide
        public int BOTTOM = 70; // show // Jawinton, 55 to 70

        AboutPanel() {

            setFont(UIManager.getFont("Label.font"));
            fm = getFontMetrics(getFont());

            setBorder(new MatteBorder(1, 1, 1, 1, Color.gray));
            
            //text.add(ConfigDefinitions.PROGRAM_NAME);
            loadAboutText();
            scrollPosition = -250;
            
            setPreferredSize(new Dimension(image.getWidth(this), image
                    .getHeight(this)));
            
            thread = new AnimationThread();
        }

        protected void loadAboutText() {

            try {
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(
                                getClass()
                                        .getResourceAsStream(
                                                "/net/sf/jfilesync/prop/bundles/about.properties")));

                while (input.ready()) {
                    String line = input.readLine();
                    maxWidth = Math.max(maxWidth, fm.stringWidth(line) + 10);
                    text.add(line);
                }
                input.close();
                
            } catch (Exception e) {
                String line = LanguageBundle.getInstance().getMessage(
                        "dialog.about.no_infos");
                maxWidth = Math.max(maxWidth, fm.stringWidth(line) + 10);
                text.add(line);
            }

        }

        public void paintComponent(Graphics g) {
            
            g.setColor(AboutDialog.this.textColor);
            
            g.drawImage(image, 0, 0, this);

            fm = g.getFontMetrics();

            //g.drawString(versionStr,
              //      (getWidth() - fm.stringWidth(versionStr)) / 2,
                //    getHeight() - 30);	// Jawinton, 14 to 30

            //g.drawString(buildStr, (getWidth() - fm.stringWidth(buildStr)) / 2,
              //      getHeight() - 15);	// Jawinton, 3 to 15 

            g = g.create((getWidth() - maxWidth) / 2, TOP, maxWidth,
                    getHeight() - TOP - BOTTOM);

            int height = fm.getHeight();
            int firstLine = scrollPosition / height;

            int firstLineOffset = height - scrollPosition % height;
            int lines = (getHeight() - TOP - BOTTOM) / height;

            int y = firstLineOffset;

            for (int i = 0; i <= lines; i++) {
                if (i + firstLine >= 0 && i + firstLine < text.size()) {
                    String line = (String) text.get(i + firstLine);
                    g
                            .drawString(line,
                                    (maxWidth - fm.stringWidth(line)) / 2, y);
                }
                y += fm.getHeight();
            }
        }

        public void addNotify() {
            super.addNotify();
            thread.start();
        }

        public void removeNotify() {
            super.removeNotify();
            thread.kill();
        }

        class AnimationThread extends Thread {
            private boolean running = true;

            AnimationThread() {
                super("About box animation thread");
                setPriority(Thread.MIN_PRIORITY);
            }

            public void kill() {
                running = false;
            }

            public void run() {
                FontMetrics fm = getFontMetrics(getFont());
                int max = (text.size() * fm.getHeight());

                while (running) {
                    scrollPosition += 2;

                    if (scrollPosition > max)
                        scrollPosition = -250;

                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                    }

                    repaint(getWidth() / 2 - maxWidth, TOP, maxWidth * 2,
                            getHeight() - TOP - BOTTOM);
                }
            }
        }

    }
}
