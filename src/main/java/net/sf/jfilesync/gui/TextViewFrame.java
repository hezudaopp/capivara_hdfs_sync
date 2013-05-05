/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2006 
 * Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TextViewFrame.java,v 1.2 2006/08/22 21:29:09 hunold Exp $
 */


package net.sf.jfilesync.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.sf.jfilesync.gui.icons.TImageIconProvider;
import net.sf.jfilesync.gui.keymap.KeyStrokeModel;
import net.sf.jfilesync.util.TWindowPositioner;

public class TextViewFrame extends JFrame implements ActionListener {
  
  private static final long serialVersionUID = 3257001064442901559L;
  private static int instanceCounter = 0 ;

  private JButton closeButton = new JButton("Close");;
  private JTextArea textarea ;
  
//  private final static Logger LOGGER = Logger.getLogger(TextViewFrame.class
//      .getName());

  public TextViewFrame(final String content) {
    this(content, null);
  }
  
  public TextViewFrame(final String content, final String title) {
    if (content == null) {
      throw new IllegalArgumentException("content must not be null");
    }
    initFrame(content, title);
    registerKeyEvents();
  }
    
  private void initFrame(final String content, final String title) {
        
    textarea = new JTextArea(content);
    textarea.setCaretPosition(0);
    //textarea.setFont(new Font("SansSerif", Font.PLAIN, 12));
    textarea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    textarea.setEditable(false);

    final JScrollPane mainPanel = new JScrollPane(textarea);
    mainPanel.setMaximumSize(new Dimension(500, 400));
    mainPanel.setPreferredSize(new Dimension(500, 400));

    getContentPane().add(mainPanel, BorderLayout.CENTER);

    closeButton.setActionCommand("close");
    closeButton.addActionListener(this);

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        closingEvent();
      }
    });

    if( title == null ) {
      instanceCounter++;
      setTitle("view <" + instanceCounter + ">");
    } else {
      setTitle(title);
    }

    final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.add(closeButton);

    getContentPane().add(buttonPanel, BorderLayout.SOUTH);

    setIconImage(TImageIconProvider.getInstance().getImage(
        TImageIconProvider.FRAME_ICON));

    pack();
    setLocation(TWindowPositioner.getCenteredWindowPoint(this));
  }

  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if (cmd.equals("close")) {
//      SwingUtilities.invokeLater(new Runnable() {
//        public void run() {
          closingEvent();
//        }
//      });
    }
  }

  private void closingEvent() {
    instanceCounter--;
    setVisible(false);
    dispose();
  }


  private void registerKeyEvents() {
    KeyStrokeModel.registerAction(KeyStrokeModel.TEXT_VIEW, closeButton,
        JComponent.WHEN_IN_FOCUSED_WINDOW, new AbstractAction() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
  }

}
