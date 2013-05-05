/*
 * capivara - Java File Synchronization
 *
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
 * $Id: KeyStrokeConfigLoader.java,v 1.2 2006/08/29 19:58:19 hunold Exp $
 */
package net.sf.jfilesync.gui;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.jfilesync.gui.keymap.KeyStrokeModel;

public class KeyStrokeConfigLoader {

    private final InputStream keyStrokeDefInstream;
    private final InputStream keyStrokeNameInstream;

    private KeyStrokeConfig keyStrokeConfig = new KeyStrokeConfig();
    private KeyStrokeModel keyStrokeModel = new KeyStrokeModel();

    private final static Logger LOGGER = Logger
            .getLogger(KeyStrokeConfigLoader.class.getName());

    public KeyStrokeConfigLoader(final InputStream keyStrokeDefInputStream,
            final InputStream keyStrokeActionInstream) {

        if (keyStrokeDefInputStream == null) {
            throw new IllegalArgumentException(
                    "keyStrokeDefInputStream is null");
        }

        if (keyStrokeActionInstream == null) {
            throw new IllegalArgumentException(
                    "keyStrokeActionInstream is null");
        }

        this.keyStrokeDefInstream = keyStrokeDefInputStream;
        this.keyStrokeNameInstream = keyStrokeActionInstream;

    }

    public void load() throws IOException {

        final Properties ksProps = new Properties();
        final Properties nameProps = new Properties();

        ksProps.load(keyStrokeDefInstream);
        nameProps.load(keyStrokeNameInstream);

        final Enumeration<Object> keyEnum = ksProps.keys();

        while (keyEnum.hasMoreElements()) {

            String propKey = (String) keyEnum.nextElement();
            String keyStrokeDefinition = ksProps.getProperty(propKey);

            String shortcutName = getShortcutName(propKey);
            KeyStroke keyStroke = parseKeyStroke(keyStrokeDefinition);

            if (keyStroke != null) {
                LOGGER.info("Add KeyStroke : " + shortcutName + " , "
                        + keyStroke);
                keyStrokeModel.addKeyStrokeBinding(shortcutName, keyStroke);

                // check if this key is also included in the action name
                // properties, if so then add it to the config
                if (nameProps.containsKey(propKey)) {
                    final String localeKey = nameProps.getProperty(propKey);
                    LOGGER.info("add name : " + localeKey);
                    keyStrokeConfig.addKeyStrokeConfig(localeKey, keyStroke);
                }
            }

        }

    }

    public KeyStrokeConfig getKeyStrokeConfig() {
        return keyStrokeConfig;
    }

    public KeyStrokeModel getKeyStrokeModel() {
        return keyStrokeModel;
    }

    /*
     * This method was originally written by Slava Pestov. Copyright (C) 1999,
     * 2003 Slava Pestov
     *
     * changed to my own needs sahu
     */

    protected static KeyStroke parseKeyStroke(String def) {

        if (def == null)
            return null;

        int modifiers = 0;
        int index = def.indexOf('+');

        if (index != -1) {
            for (int i = 0; i < index; i++) {
                switch (Character.toUpperCase(def.charAt(i))) {
                case 'A':
                    modifiers |= KeyEvent.ALT_MASK;
                    break;
                case 'C':
                    modifiers |= KeyEvent.CTRL_MASK;
                    break;
                case 'M':
                    modifiers |= KeyEvent.META_MASK;
                    break;
                case 'S':
                    modifiers |= KeyEvent.SHIFT_MASK;
                    break;
                }
            }
        }

        String key = def.substring(index + 1);
        if (key.length() == 1) {
            char ch = key.charAt(0);
            if (modifiers == 0) {
                return KeyStroke.getKeyStroke(ch);
            } else {
                return KeyStroke.getKeyStroke(Character.toUpperCase(ch),
                        modifiers);
            }
        } else if (key.length() == 0) {
            LOGGER.log(Level.SEVERE, "Invalid key stroke: " + def);
            return null;
        } else {
            int ch;

            try {
                ch = KeyEvent.class.getField("VK_".concat(key)).getInt(null);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Invalid key stroke: " + def);
                return null;
            }

            return KeyStroke.getKeyStroke(ch, modifiers);
        }

    } // end parseKeyStroke

    protected static String getShortcutName(String shortcut) {
        String retval = shortcut;
        int idx = retval.indexOf(".shortcut");
        if (idx != -1 && idx > 0) {
            retval = retval.substring(0, idx);
        } else {
            idx = retval.indexOf(".shortcut2");
            if (idx != -1 && idx > 0) {
                retval = retval.substring(0, idx);
            }
        }
        return retval;
    }

}
