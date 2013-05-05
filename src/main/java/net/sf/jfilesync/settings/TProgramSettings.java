/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2004 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TProgramSettings.java,v 1.18 2006/08/21 20:53:44 hunold Exp $
 */

package net.sf.jfilesync.settings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * TProgramSettings is the object instance of all <paramconf>
 * settings in the config.xml.
 */

public class TProgramSettings {

  static class ProgramOption {

        private int optionKeyID;
        private String optionKeyString;
        private int optionValueType;
        private Object val;

        public ProgramOption(final int optionKeyID,
                final String optionKeyString, final int optionValueType,
                final Object defaultVal) {
            this.optionKeyID = optionKeyID;
            this.optionKeyString = optionKeyString;
            this.optionValueType = optionValueType;
            this.val = defaultVal;
        }

        public int getKeyID() {
            return optionKeyID;
        }

        public String getKeyString() {
            return optionKeyString;
        }

        public int getType() {
            return optionValueType;
        }

        public Object getValue() {
            return val;
        }

        public void setValue(Object v) {
            val = v;
        }

    }

    private static final String XML_PARAM = "paramconf";
    private static final String XML_OPTION = "option";
    private static final String XML_OPTION_KEY = "key";
    private static final String XML_OPTION_VALUE = "value";

    public static final int OPTION_HIDDEN_FILES = 0,
                          OPTION_TABLE_STYLE       = 1,
                          OPTION_USER_LANG         = 2,
                          OPTION_CLOSE_COPY_DIALOG = 3,
                          OPTION_CONFIRM_PASSWORD  = 4,
                          OPTION_THEME             = 5,
                          OPTION_DIR_OPEN_DOUBLE_CLICK = 6,
                          OPTION_CONFIRM_EDITOR_OPEN = 7,
                          OPTION_CONFIRM_EDITOR_SIZE = 8,
                          OPTION_GURU_MODE           = 9,
                          OPTION_PRESERVE_MTIME      = 10,
                          OPTION_PERMISSIONS_DIR     = 11,
                          OPTION_PERMISSIONS_FILE    = 12,
                          OPTION_FILE_SIZE_BYTES     = 13,
                          OPTION_PRESERVE_PERM       = 14,
                          OPTION_FOLLOW_SYMLINKS_COPY= 15,
                          OPTION_FOLLOW_SYMLINKS_SYNC= 16,
                          OPTION_TAB_TITLE_LENGTH    = 17,
                          OPTION_TAB_FONT_SIZE       = 18,
                          OPTION_LOOK_AND_FEEL_CLASS  = 20,
                          OPTION_CONNECT_TO_LOCAL     = 21,
                          OPTION_USE_GNU_FOR_LOCAL    = 22,
                          OPTION_USE_CUSTOM_PATH      = 23,
                          OPTION_CUSTOM_PATH          = 24,
                          OPTION_MASTER_PASSWORD      = 25,
                          OPTION_FILE_TABLE_CASE_INSENSITIVE = 26,
                          OPTION_DO_NOT_PROMPT_FOR_EXIT = 50,
                          OPTION_EXPERT_MODE            = 51,
                          OPTION_SHOW_NOTE              = 52;

    private static final int VALUE_INT = 0,
                              VALUE_STRING = 1,
                                VALUE_BOOL = 2;

  // defined key value options optionID->option
    private final HashMap<Integer, ProgramOption> optionHash = new HashMap<Integer, ProgramOption>();

    private final Document config;

    private static Logger logger = Logger.getLogger(TProgramSettings.class
            .getName());

    public TProgramSettings(final Document config) {
        this.config = config;
        defineOptions();
        try {
            readKeyValueOptions();
        } catch (SettingsTypeException sex) {
            logger.log(Level.SEVERE, sex.getMessage());
        }
    }

    protected void defineOptions() {

        addProgramOption(new ProgramOption(OPTION_HIDDEN_FILES, "hiddenfiles",
                VALUE_BOOL, new Boolean(true)));

        addProgramOption(new ProgramOption(OPTION_TABLE_STYLE, "tablestyle",
                VALUE_INT, new Integer(ConfigDefinitions.DEFAULT_STYLE)));

        addProgramOption(new ProgramOption(OPTION_CLOSE_COPY_DIALOG,
                "autoclosecopydialog", VALUE_BOOL, new Boolean(false)));

        addProgramOption(new ProgramOption(OPTION_CONFIRM_PASSWORD,
                "autoconfirmpasswd", VALUE_BOOL, new Boolean(false)));

        addProgramOption(new ProgramOption(OPTION_DIR_OPEN_DOUBLE_CLICK,
                "doubleclick", VALUE_BOOL, new Boolean(true)));

//        addProgramOption(new ProgramOption(OPTION_USER_LANG, "language",
//                VALUE_STRING, LanguageBundle.getDefaultLanguageKey()));

        addProgramOption(new ProgramOption(OPTION_USER_LANG, "language",
                VALUE_STRING, "undefined"));

        addProgramOption(new ProgramOption(OPTION_THEME, "theme", VALUE_STRING,
                null));

        addProgramOption(new ProgramOption(OPTION_CONFIRM_EDITOR_OPEN,
                "confirm_editor_open", VALUE_BOOL, new Boolean(true)));

        addProgramOption(new ProgramOption(OPTION_CONFIRM_EDITOR_SIZE,
                "confirm_editor_size", VALUE_INT, new Integer(
                        ConfigDefinitions.EDITOR_MAX_FILE_SIZE_KB)));

        addProgramOption(new ProgramOption(OPTION_GURU_MODE, "guru_mode",
                VALUE_BOOL, new Boolean(false)));

        addProgramOption(new ProgramOption(OPTION_PRESERVE_MTIME,
                "preserve_mtime", VALUE_BOOL, new Boolean(false)));

        addProgramOption(new ProgramOption(OPTION_PERMISSIONS_DIR,
                "permissions_dir", VALUE_INT, new Integer(
                        ConfigDefinitions.DEFAULT_PERMISSIONS_DIR)));

        addProgramOption(new ProgramOption(OPTION_PERMISSIONS_FILE,
                "permissions_file", VALUE_INT, new Integer(
                        ConfigDefinitions.DEFAULT_PERMISSIONS_FILE)));

        addProgramOption(new ProgramOption(OPTION_FILE_SIZE_BYTES,
                "file_size_bytes", VALUE_BOOL, new Boolean(false)));

        addProgramOption(new ProgramOption(OPTION_PRESERVE_PERM,
                "preserve_perm", VALUE_BOOL, new Boolean(false)));

        addProgramOption(new ProgramOption(OPTION_FOLLOW_SYMLINKS_COPY,
                "follow_symlinks_copy", VALUE_BOOL, new Boolean(false)));

        addProgramOption(new ProgramOption(OPTION_FOLLOW_SYMLINKS_SYNC,
                "follow_symlinks_copy", VALUE_BOOL, new Boolean(false)));

        addProgramOption(new ProgramOption(OPTION_DO_NOT_PROMPT_FOR_EXIT,
                "prompt_for_exit", VALUE_BOOL, new Boolean(false))); // false
                                                                        // means
                                                                        // show
                                                                        // prompt

        addProgramOption(new ProgramOption(OPTION_EXPERT_MODE, "expert_mode",
                VALUE_BOOL, new Boolean(false)));

        addProgramOption(new ProgramOption(OPTION_LOOK_AND_FEEL_CLASS,
                "look_and_feel_class", VALUE_STRING, UIManager
                        .getSystemLookAndFeelClassName()));

        addProgramOption(new ProgramOption(OPTION_TAB_TITLE_LENGTH,
                "tab_title_length", VALUE_INT, new Integer(
                        ConfigDefinitions.TAB_TITLE_LENGTH)));

        addProgramOption(new ProgramOption(OPTION_TAB_FONT_SIZE,
                "tab_font_size", VALUE_INT, new Integer(
                        ConfigDefinitions.TAB_FONT_SIZE)));

        addProgramOption(new ProgramOption(OPTION_CONNECT_TO_LOCAL,
                "connect_to_local", VALUE_BOOL, new Boolean(false)));

        addProgramOption(new ProgramOption(OPTION_USE_GNU_FOR_LOCAL,
                "use_gnu_for_local", VALUE_BOOL, new Boolean(false)));

        addProgramOption(new ProgramOption(OPTION_USE_CUSTOM_PATH,
                "use_custom_path", VALUE_BOOL, new Boolean(false)));

        addProgramOption(new ProgramOption(OPTION_CUSTOM_PATH, "custom_path",
                VALUE_STRING, null));

        addProgramOption(new ProgramOption(OPTION_MASTER_PASSWORD,
                "master_password", VALUE_STRING, null));
        
        addProgramOption(new ProgramOption(OPTION_FILE_TABLE_CASE_INSENSITIVE,
                "file_name_sort_case_insensitive", VALUE_BOOL, new Boolean(
                        false)));

        addProgramOption(new ProgramOption(OPTION_SHOW_NOTE,
            "show_note_dialog", VALUE_BOOL, new Boolean(
                    true)));

    }

    protected void addProgramOption(ProgramOption opt) {
        optionHash.put(new Integer(opt.getKeyID()), opt);
    }

    protected void readKeyValueOptions() throws SettingsTypeException {
        Iterator<Integer> it = optionHash.keySet().iterator();
        while (it.hasNext()) {
            int keyID = ((Integer) it.next()).intValue();

            ProgramOption option = findOption(keyID);
            if (option != null) {

                Element optionElem = getOptionElement(config,
                        option.optionKeyString);

                if (optionElem != null) {
                    readOption(option, optionElem);
                }
            }
        }
    }

    protected ProgramOption findOption(int keyID) {
        ProgramOption retval = null;

        Integer key = new Integer(keyID);

        if (optionHash.containsKey(key)) {
            retval = (ProgramOption) optionHash.get(key);
        }

        return retval;
    }

    protected Element getOptionElement(final Document config,
            final String optionKey) {

        Element retval = null;

        NodeList params = config.getElementsByTagName(XML_PARAM);
        NodeList paramChilds = params.item(0).getChildNodes();

        for (int i = 0; i < paramChilds.getLength(); i++) {
            if (paramChilds.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element curElem = (Element) paramChilds.item(i);
                if (curElem.getTagName().equals(XML_OPTION)) {
                    Element keyElem = (Element) curElem.getElementsByTagName(
                            XML_OPTION_KEY).item(0);
                    if (keyElem.getChildNodes().getLength() > 0) {
                        String keyString = keyElem.getChildNodes().item(0)
                                .getNodeValue();
                        if (optionKey.equals(keyString)) {
                            retval = curElem;
                            break;
                        }
                    }
                }
            }
        }

        return retval;
    }

    protected void readOption(final ProgramOption option,
            final Element optionElem) throws SettingsTypeException {

        Element valElem = (Element) optionElem.getElementsByTagName(
                XML_OPTION_VALUE).item(0);

        if (valElem.getChildNodes().getLength() > 0) {
            String valueString = valElem.getChildNodes().item(0).getNodeValue();
            castAndSetValue(option, valueString);
        }
    }

    protected void castAndSetValue(final ProgramOption option,
            final String valueString) throws SettingsTypeException {

        switch (option.getType()) {
        case VALUE_BOOL:
            Boolean v = new Boolean(valueString);
            option.setValue(v);
            break;
        case VALUE_INT:
            try {
                Integer i = new Integer(valueString);
                option.setValue(i);
            } catch (NumberFormatException nex) {
                throw new SettingsTypeException(nex.getMessage());
            }
            break;
        case VALUE_STRING:
            option.setValue(valueString);
            break;
        default:
            throw new SettingsTypeException("unkown type : " + option.getType());
        }
    }

    public void setProgramOption(int keyID, String value)
            throws SettingsTypeException {

        Integer key = new Integer(keyID);
        if (optionHash.containsKey(key)) {

            ProgramOption option = (ProgramOption) optionHash.get(key);
            castAndSetValue(option, value);

            // and update XML data
            Element elem = getOptionElement(config, option.getKeyString());
            if (elem == null) {
                // insert value
                Element paramElem = (Element) config.getElementsByTagName(
                        XML_PARAM).item(0);
                Element newOption = config.createElement(XML_OPTION);
                Element newOptionKey = config.createElement(XML_OPTION_KEY);
                Element newOptionVal = config.createElement(XML_OPTION_VALUE);
                newOptionKey.appendChild(config.createTextNode(option
                        .getKeyString()));
                newOptionVal.appendChild(config.createTextNode(value));
                newOption.appendChild(newOptionKey);
                newOption.appendChild(newOptionVal);
                paramElem.appendChild(newOption);
            } else {
                // update value
                NodeList valList = elem.getElementsByTagName(XML_OPTION_VALUE);
                if (valList.getLength() == 0) {
                    // add val
                    Element newOptionVal = config
                            .createElement(XML_OPTION_VALUE);
                    newOptionVal.appendChild(config.createTextNode(value));
                    elem.appendChild(newOptionVal);
                } else {
                    // update val
                    Element valElem = (Element) valList.item(0);
                    if (valElem.getChildNodes().getLength() > 0) {
                        valElem.getChildNodes().item(0).setNodeValue(value);
                    } else {
                        valElem.appendChild(config.createTextNode(value));
                    }
                }
            }
        }
    }

    public int getIntegerOption(int keyID) throws SettingsTypeException {

        int retval = -1;

        ProgramOption opt = findOption(keyID);
        if (opt == null) {
            throw new SettingsTypeException("unkown optionID : " + keyID);
        }

        if (opt.getType() != VALUE_INT) {
            throw new SettingsTypeException("optionID " + keyID
                    + " has wrong type");
        }

        Integer i = (Integer) opt.getValue();
        retval = i.intValue();

        return retval;
    }

    public boolean getBooleanOption(int keyID) throws SettingsTypeException {

        boolean retval = false;

        ProgramOption opt = findOption(keyID);
        if (opt == null) {
            throw new SettingsTypeException("unkown optionID : " + keyID);
        }

        if (opt.getType() != VALUE_BOOL) {
            throw new SettingsTypeException("optionID " + keyID
                    + " has wrong type");
        }

        Boolean b = (Boolean) opt.getValue();
        retval = b.booleanValue();

        return retval;
    }

    public String getStringOption(int keyID) throws SettingsTypeException {

        String retval = null;

        ProgramOption opt = findOption(keyID);
        if (opt == null) {
            throw new SettingsTypeException("unkown optionID : " + keyID);
        }

        if (opt.getType() != VALUE_STRING) {
            throw new SettingsTypeException("optionID " + keyID
                    + " has wrong type");
        }

        retval = (String) opt.getValue();

        return retval;
    }

}
