/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2005 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
package net.sf.jfilesync.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.sf.jfilesync.MainWin;
import net.sf.jfilesync.engine.CopyProgressListener;
import net.sf.jfilesync.gui.swing.ComponentVisibleRunner;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.settings.SettingsTypeException;
import net.sf.jfilesync.settings.TProgramSettings;
import net.sf.jfilesync.util.StatisticsFormatter;

public class FileCopyProgressMonitor extends JDialog implements
        CopyProgressListener, ItemListener {

    private static final long serialVersionUID = 1L;

    private final static Logger LOGGER = Logger
            .getLogger(FileCopyProgressMonitor.class.getName());

    // GUI elements
    private JProgressBar fileProgress;
    private JProgressBar overallProgress;
    private JTextField fileField;
    private JCheckBox closeAfterWorkCheckBox;
    private JLabel avgStatsLabel, avgStatsField;
    private JLabel timeToFinishLabel, timeToFinishField;
    private JLabel timeElapsedLabel, timeElapsedField;
    private JButton cancelButton = new JButton(LanguageBundle.getInstance()
            .getMessage("label.cancel"));
    private JButton closeButton;

    private final String avgRateStr = LanguageBundle.getInstance().getMessage(
            "dialog.transfer.average_transfer_rate.label");
    private final String estTimeStr = LanguageBundle.getInstance().getMessage(
            "dialog.transfer.time_to_finish.label");
    private final String elapsedTimeStr = LanguageBundle.getInstance()
            .getMessage("dialog.transfer.time_elapsed.label");

    private final static int MIN = 0, MAX = 100;
    private final static int COMP_WIDTH = 400;

    private Color labelBG = UIManager.getColor("ColorChooser.background");

    public FileCopyProgressMonitor(JFrame owner) {
        super(owner, LanguageBundle.getInstance().getMessage(
                "window.dialog.filetransfer.label"), true);

        initUI();

        pack();
        setLocationRelativeTo(owner);
    }

    public void setActionListener(ActionListener listener) {
        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener(listener);
    }

    private void initUI() {

        fileField = new JTextField(20);
        fileField.setText(""); // current file
        // fileField.setPreferredSize(new Dimension(COMP_WIDTH,20));
        fileField.setHorizontalAlignment(JTextField.LEFT);
        fileField.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
        fileField.setEditable(false);
        fileField.setBackground(labelBG);

        fileProgress = new JProgressBar(MIN, MAX);
        fileProgress.setPreferredSize(new Dimension(COMP_WIDTH, 20));
        fileProgress.setBorder(BorderFactory.createEtchedBorder());
        fileProgress.setStringPainted(true);

        overallProgress = new JProgressBar(MIN, MAX);
        overallProgress.setPreferredSize(new Dimension(COMP_WIDTH, 20));
        overallProgress.setBorder(BorderFactory.createEtchedBorder());
        overallProgress.setStringPainted(true);

        avgStatsLabel = new JLabel(avgRateStr + ":");
        avgStatsField = new JLabel("");

        timeToFinishLabel = new JLabel(estTimeStr + ":");
        timeToFinishField = new JLabel("");

        timeElapsedLabel = new JLabel(elapsedTimeStr + ":");
        timeElapsedField = new JLabel("");

        JLabel compDescrLabel = new JLabel(LanguageBundle.getInstance()
                .getMessage("dialog.transfer.overall_stats.label"));

        boolean close = false;
        try {
            close = MainWin.config.getProgramSettings().getBooleanOption(
                    TProgramSettings.OPTION_CLOSE_COPY_DIALOG);
        } catch (SettingsTypeException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }

        closeAfterWorkCheckBox = new JCheckBox(LanguageBundle.getInstance()
                .getMessage("dialog.transfer.autoclose.label"), close);
        closeAfterWorkCheckBox.setMnemonic(KeyEvent.VK_C);
        closeAfterWorkCheckBox.addItemListener(this);
        JPanel checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        checkBoxPanel.add(closeAfterWorkCheckBox);

        JPanel toppane = new JPanel();
        toppane.setLayout(new BoxLayout(toppane, BoxLayout.Y_AXIS));
        toppane.add(fileField);
        toppane.add(fileProgress);
        toppane.add(makeLabelPanel(compDescrLabel, null));
        toppane.add(overallProgress);
        toppane.add(makeLabelPanel(avgStatsLabel, avgStatsField));
        toppane.add(makeLabelPanel(timeToFinishLabel, timeToFinishField));
        toppane.add(makeLabelPanel(timeElapsedLabel, timeElapsedField));
        toppane.add(new JSeparator());
        toppane.add(checkBoxPanel);
        // toppane.setPreferredSize(new Dimension(COMP_WIDTH,180));
        toppane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonpane = new JPanel();
        buttonpane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        closeButton = new JButton(LanguageBundle.getInstance().getMessage(
                "label.ok"));
        closeButton.setEnabled(false);
        buttonpane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonpane.add(closeButton);

        buttonpane.add(cancelButton);

        getContentPane().add(toppane, BorderLayout.CENTER);
        getContentPane().add(buttonpane, BorderLayout.SOUTH);

        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new ComponentVisibleRunner(
                        FileCopyProgressMonitor.this, false, true));
            }
        });

        /*
         * this is just in case that the FileTransfer2 dies unexpectedly, so we
         * can close the dialog
         */
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String cmd = e.getActionCommand();
                if (cmd.equals("cancel")) {
                    FileCopyProgressMonitor.this.setVisible(false);
                }
            }
        });

    }

    private JPanel makeLabelPanel(JLabel label, JLabel field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(label);
        if (field != null) {
            panel.add(field);
        }
        return panel;
    }

    public void setFinishedState() {
        closeButton.setEnabled(true);
        cancelButton.setEnabled(false);
        closeButton.requestFocus();
    }

    public boolean closeAfterCompletion() {
        return closeAfterWorkCheckBox.isSelected();
    }

    public void itemStateChanged(ItemEvent e) {
        Object src = e.getItemSelectable();
        if (src == closeAfterWorkCheckBox) {
            try {
                MainWin.config.getProgramSettings().setProgramOption(
                        TProgramSettings.OPTION_CLOSE_COPY_DIALOG,
                        Boolean.valueOf(closeAfterWorkCheckBox.isSelected())
                                .toString());
            } catch (SettingsTypeException ste) {
                LOGGER.log(Level.WARNING, ste.getMessage());
            }
        }
    }

    public void setCurrentFileName(final String fname) {
        fileField.setText(fname);
    }

    public void setCurrentFileProgress(final int progress) {
        fileProgress.setValue(progress);
    }

    public void setOverallProgress(final int progress) {
        overallProgress.setValue(progress);
    }

    public void setAverageTransferRate(final float rate) {
        if (rate > 0.0f) {
            String rateStr = StatisticsFormatter.formatTransferRate(rate);
            avgStatsField.setText(rateStr);
        }
    }

    public void setSecondsElapsed(final long secs) {
        String timeStr = StatisticsFormatter.formatSeconds(secs);
        timeElapsedField.setText(timeStr);
    }

    public void setSecondsEstimated(final long secs) {
        if (secs > 0) {
            String timeStr = StatisticsFormatter.formatSeconds(secs);
            timeToFinishField.setText(timeStr);
        }
    }

    public void setExtendedOverallProgress(int filesDone, int fileNum,
            int progress) {
        final StringBuffer ovStrB = new StringBuffer();
        ovStrB.append(Integer.toString(filesDone));
        ovStrB.append("/");
        ovStrB.append(Integer.toString(fileNum));
        ovStrB.append(" ( ");
        ovStrB.append(Integer.toString(progress));
        ovStrB.append("% )");
        overallProgress.setValue(progress);
        overallProgress.setString(ovStrB.toString());
    }

    public void shutdown() {
        SwingUtilities
                .invokeLater(new ComponentVisibleRunner(this, false, true));
    }

    public void startup() {
        SwingUtilities.invokeLater(new ComponentVisibleRunner(this, true));
    }

}
