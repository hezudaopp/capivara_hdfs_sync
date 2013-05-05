/*
 * capivara - Java File Synchronization
 *
 * Created on 11-Dec-2005
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
 * $Id$
 */
package net.sf.jfilesync.sync2.list;

import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sf.jfilesync.prop.LanguageBundle;

public class AddEditExpressionDialog extends JDialog {

    private static final long serialVersionUID = -7711539178678948243L;
    private JTextField expField = new JTextField(20);
    private JTextField testField = new JTextField(20);
    private JTextField statusField = new JTextField(20);
    private JButton applyButton = new JButton(LanguageBundle.getInstance()
            .getMessage("label.apply"));
    private JButton dismissButton = new JButton(LanguageBundle.getInstance()
            .getMessage("label.dismiss"));

    public final static String OPT_APPLY = "apply";
    public final static String OPT_DISMISS = "dismiss";

    public AddEditExpressionDialog(Dialog owner) {
        super(owner, true);
        setTitle(LanguageBundle.getInstance().getMessage("filter.add"));
        initUI();
    }

    public AddEditExpressionDialog(Dialog owner, Expression exp) {
        super(owner, true);
        setTitle(LanguageBundle.getInstance().getMessage("filter.edit"));
        initUI();
        expField.setText(exp.toString());
    }

    public String getExpressionString() {
        return expField.getText();
    }

    public String getTestString() {
        return testField.getText();
    }

    public void setStatusText(final String text) {
        statusField.setText(text);
    }

    public synchronized void setActionListener(ActionListener l) {
        applyButton.addActionListener(l);
        dismissButton.addActionListener(l);
    }

    private void initUI() {

        MatchKeyListener expKeyListener = new MatchKeyListener();
        expField.addKeyListener(expKeyListener);
        testField.addKeyListener(expKeyListener);

        JPanel mainPanel = (JPanel) getContentPane();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.add(createExpressionInputPanel());
        mainPanel.add(createExpressionTestPanel());
        mainPanel.add(createStatusPanel());
        mainPanel.add(createButtonPanel());
        pack();
        setLocationRelativeTo(getOwner());
    }

    private JPanel createExpressionInputPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanelName(p, LanguageBundle.getInstance().getMessage("label.regexp"));
        p.add(expField);
        return p;
    }

    private JPanel createExpressionTestPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanelName(p, LanguageBundle.getInstance().getMessage(
                "filter.test_input"));
        p.add(testField);
        return p;
    }

    private JPanel createStatusPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanelName(p, LanguageBundle.getInstance()
                .getMessage("filter.status"));

        // p.add(new JLabel(LanguageBundle.getInstance()
        // .getMessage("filter.exp_match")));

        statusField.setEditable(false);
        p.add(statusField);
        return p;
    }

    private void addPanelName(JPanel p, String name) {
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createEmptyBorder(2, 5, 2, 5), BorderFactory
                .createTitledBorder(name)));
    }

    private JPanel createButtonPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));

        applyButton.setActionCommand(OPT_APPLY);
        dismissButton.setActionCommand(OPT_DISMISS);

        p.add(applyButton);
        p.add(dismissButton);
        return p;
    }

    class MatchKeyListener implements KeyListener {

        public MatchKeyListener() {
        }

        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {
        }

        public void keyReleased(KeyEvent e) {
            testExpression(e);
        }

        private void testExpression(KeyEvent e) {
            if (!e.getSource().equals(testField)
                    && !e.getSource().equals(expField)) {
                return;
            }

            final String testStr = testField.getText();
            final String expStr = expField.getText();

            if (testStr.equals("") || expStr.equals("")) {
                return;
            }

            // validate expression
            Pattern p = null;
            try {
                p = Pattern.compile(expStr);
                Matcher matcher = p.matcher(testStr);
                boolean matches = matcher.matches();

                // System.out.println("exp:\'"+expStr+"\'" + "
                // \'"+testStr+"\'");

                if (matches) {
                    statusField.setText(LanguageBundle.getInstance()
                            .getMessage("filter.match"));

                } else {
                    statusField.setText(LanguageBundle.getInstance()
                            .getMessage("filter.no_match"));
                }
            } catch (PatternSyntaxException pe) {
                //statusField.setText(LanguageBundle.getInstance()
                //    .getMessage("filter.invalid_pattern") + " " + pe.getMessage());
                statusField.setText(pe.getMessage());
            }
        }

    }

}
