/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003-2012 Sascha Hunold <hunoldinho@users.sourceforge.net>
 *
 <license/>
 *
 */
package net.sf.jfilesync.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.sf.jfilesync.MainWin;
import net.sf.jfilesync.gui.swing.ComponentVisibleRunner;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.settings.SettingsTypeException;
import net.sf.jfilesync.settings.TProgramSettings;

public class InfoDialog extends JDialog {

  private final static Logger LOGGER = Logger.getLogger(InfoDialog.class.getName());

  private final JEditorPane editorPane = new JEditorPane();
  private final JButton closeButton = new JButton(LanguageBundle.getInstance()
      .getMessage("label.close"));
  private final JCheckBox checkBox = new JCheckBox("Show note during startup", true);
  
  public InfoDialog(JFrame parent) throws IOException {
    super(parent);
    
    final StringBuffer contentBuf = new StringBuffer();

    final BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(
        "/net/sf/jfilesync/note.properties")));
    
    do {
      String line = br.readLine();
      if( line == null ) {
        break;
      } else {
        contentBuf.append(line);
      }
    } while( true );
    
    editorPane.setContentType("text/html");
    editorPane.setText(contentBuf.toString());
    editorPane.setPreferredSize(new Dimension(500,400));
    editorPane.setEditable(false);
    editorPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    
//    editorPane.addHyperlinkListener(new HyperlinkListener() {
//      @Override
//      public void hyperlinkUpdate(HyperlinkEvent e) {
//        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
//          try {
//            Desktop.getDesktop().browse(e.getURL().toURI());
//          } catch (IOException e1) {
//            e1.printStackTrace();
//          } catch (URISyntaxException e1) {
//            e1.printStackTrace();
//          }
//        }
//      }
//    });
    
    closeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        
        try {
          if (checkBox.isSelected() == false) {
            MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_SHOW_NOTE,
                new Boolean(checkBox.isSelected()).toString());
          }
        } catch (SettingsTypeException e) {
          LOGGER.warning(e.getMessage());
        }        
        
        SwingUtilities.invokeLater(new ComponentVisibleRunner(InfoDialog.this, false));
      }
    });
    
    add(editorPane, BorderLayout.CENTER);
    
    final JPanel chkBoxPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
    chkBoxPane.add(checkBox);

    final JPanel buttonPane = new JPanel(new FlowLayout());
    buttonPane.add(closeButton);
    
    final JPanel southPanel = new JPanel(new BorderLayout());
    southPanel.add(chkBoxPane, BorderLayout.NORTH);
    southPanel.add(buttonPane, BorderLayout.SOUTH);
    
    add(southPanel, BorderLayout.SOUTH);
    pack();
    setLocationRelativeTo(parent);
  }
  
  
  
}
