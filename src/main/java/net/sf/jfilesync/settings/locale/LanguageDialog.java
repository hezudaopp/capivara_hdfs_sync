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
 * $Id: LanguageDialog.java,v 1.6 2006/08/09 22:18:40 hunold Exp $
 */
package net.sf.jfilesync.settings.locale;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import net.sf.jfilesync.gui.icons.TImageIconProvider;
import net.sf.jfilesync.prop.LanguageBundle;

public class LanguageDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 3762253058486383154L;

    private String selectedLanguage = LanguageBundle.getDefaultLanguageKey();
    private JComboBox langBox = new JComboBox();

    public LanguageDialog(JFrame parent) {

        super(parent);
        setTitle("Language selection");
        setModal(true);

        Image langSplashImage = TImageIconProvider.getInstance().getImage(
                TImageIconProvider.LANGUAGE_SPLASH);

        ImagePanel imPanel = new ImagePanel(langSplashImage);
        imPanel.setPreferredSize(new Dimension(264, 258));

        JPanel topPanel = new JPanel();
        topPanel.add(imPanel, BorderLayout.CENTER);

        final java.util.List<String> langKeys = LanguageBundle.getInstance()
                .getAvailableLanguages();
        int defLangIndex = 0;
        for (int i = 0; i < langKeys.size(); i++) {
            final String langKey = (String) langKeys.get(i);
            final String langName = (String) LanguageBundle.getInstance()
                    .getLanguageDescription(langKey);
            final String flagPath = LanguageBundle.getInstance().getFlagPath(
                    langKey);

            addLanguageToList(langKey, langName, flagPath);
            if (langKey.equals(LanguageBundle.getDefaultLanguageKey())) {
                defLangIndex = i;
            }
        }
        langBox.setSelectedIndex(defLangIndex);
        langBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                LangLabel ll = (LangLabel) e.getItem();
                selectedLanguage = ll.getLangKey();
            }
        });
        langBox.setRenderer(new LangLabelRenderer());

        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        langPanel.add(langBox);

        JButton okB = new JButton("OK");
        okB.setActionCommand("ok");
        okB.addActionListener(this);
        JPanel bPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bPanel.add(okB);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(topPanel);
        content.add(langPanel);
        content.add(bPanel);

        getContentPane().add(content);

        pack();

        Dimension screen = getToolkit().getScreenSize();
        setLocation(screen.width / 2 - getSize().width / 2, screen.height / 2
                - getSize().height / 2);
    }

    protected void addLanguageToList(final String langKey,
            final String langName, final String flagPath) {

        LangLabel ll;
        if (flagPath != null) {
            if (LanguageDialog.class.getResource(flagPath) != null) {
                Icon icon = new ImageIcon(LanguageDialog.class
                        .getResource(flagPath));
                ll = new LangLabel(langKey, langName, icon);
            } else {
                ll = new LangLabel(langKey, langName);
            }
        } else {
            ll = new LangLabel(langKey, langName);
        }

        langBox.addItem(ll);
    }

    public String getSelectedLanguage() {
        return selectedLanguage;
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("ok")) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    setVisible(false);
                }
            });
        }
    }

    static class LangLabel extends JLabel {
        private static final long serialVersionUID = 3257003246185953332L;
        private final String langKey;

        public LangLabel(final String langKey, final String langName,
                final Icon icon) {
            super(langName, icon, JLabel.LEFT);
            this.langKey = langKey;
        }

        public LangLabel(final String langKey, final String langName) {
            super(langName);
            this.langKey = langKey;
        }

        public String getLangKey() {
            return langKey;
        }
    }

    static class ImagePanel extends JPanel {
        private static final long serialVersionUID = 3257003246185953332L;

        private Image im;
        private Image offscreen;

        public ImagePanel(Image im) {
            super();
            this.im = im;
        }

        public void paint(Graphics g) {
            if (offscreen == null) {
                offscreen = createImage(getSize().width, getSize().height);
            }
            Graphics og = offscreen.getGraphics();
            og.drawImage(im, 0, 0, this);
            og.dispose();
            g.drawImage(offscreen, 0, 0, null);
        }

        public void update(Graphics g) {
            paint(g);
        }

    }

    static class LangLabelRenderer implements ListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {

            LangLabel ll = (LangLabel) value;
            ll.setOpaque(true);

            if (isSelected) {
                ll.setBackground(list.getSelectionBackground());
                ll.setForeground(list.getSelectionForeground());
            } else {
                ll.setBackground(list.getBackground());
                ll.setForeground(list.getForeground());
            }

            return ll;
        }

    }

}
