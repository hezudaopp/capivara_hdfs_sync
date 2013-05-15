/*
 * capivara - Java File Synchronization
 *
 * Created on 07-Mar-2006
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
 */
package net.sf.jfilesync.gui.components;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.sf.jfilesync.engine.worker.events.CollectStatusMessage;
import net.sf.jfilesync.prop.LanguageBundle;

public class CollectFilesPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final MessagePanel msgPanel = new MessagePanel();

    public CollectFilesPanel() {
        initUI();
    }

    private void initUI() {
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        setLayout(layout);
        add(msgPanel, gbc);
    }

    public void setMessage(CollectStatusMessage csm) {
        Runnable r = new UpdateMsgPanelRunner(msgPanel, csm);
        SwingUtilities.invokeLater(r);
    }

    class UpdateMsgPanelRunner implements Runnable {
        private final CollectStatusMessage csm;
        private final MessagePanel mpanel;

        public UpdateMsgPanelRunner(MessagePanel mpanel,
                CollectStatusMessage csm) {
            this.mpanel = mpanel;
            this.csm = csm;
        }

        public void run() {
            mpanel.setCurrentDirectory(csm.getCurrentDirectory());
            mpanel.setNumberOfFiles(csm.getCollectedFileNum());
        }
    }

    static class MessagePanel extends JPanel {
        private static final long serialVersionUID = 3976741363100431412L;

        private final JLabel dirLabel = new JLabel(LanguageBundle.getInstance()
                .getMessage("dialog.collect.curdir"));
        private final DisplayFileNamePanel dirField = new DisplayFileNamePanel();

        private final JLabel filesLabel = new JLabel(LanguageBundle
                .getInstance().getMessage("dialog.collect.numfiles"));
        private final JTextField filesField = new JTextField();

        public MessagePanel() {

            GridBagLayout layout = new GridBagLayout();
            GridBagConstraints gbc = new GridBagConstraints();
            setLayout(layout);

            // Jawinton If I set new font, chinese characters don't display well.
//            final Font origF = dirLabel.getFont();
//            final Font newF = new Font(origF.getFontName(), origF.getStyle(), 11);
//            dirLabel.setFont(newF);
//            filesLabel.setFont(newF);

            JPanel dirPanel = new JPanel();
            dirPanel.setLayout(new BoxLayout(dirPanel, BoxLayout.Y_AXIS));
            dirPanel.add(dirLabel);
            dirPanel.add(dirField);
            dirLabel.setAlignmentX(LEFT_ALIGNMENT);
            dirField.setAlignmentX(LEFT_ALIGNMENT);

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add(dirPanel, gbc);

            filesField.setHorizontalAlignment(JTextField.LEFT);
            filesField.setEditable(false);

            JPanel filesPanel = new JPanel();
            filesPanel.setLayout(new BoxLayout(filesPanel, BoxLayout.Y_AXIS));
            filesPanel.add(filesLabel);
            filesPanel.add(filesField);
            filesLabel.setAlignmentX(LEFT_ALIGNMENT);
            filesField.setAlignmentX(LEFT_ALIGNMENT);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add(filesPanel, gbc);
            
            //setBorder(BorderFactory.createEtchedBorder());
        }

        public void setNumberOfFiles(int num) {
            filesField.setText(Integer.toString(num));
        }

        public void setCurrentDirectory(final String dir) {
            if (dir != null) {
                dirField.setFileName(dir);
            }
        }
    }

}
