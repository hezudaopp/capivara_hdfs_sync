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
 * $Id: LanguageBundle.java,v 1.3 2006/08/29 19:58:19 hunold Exp $
 */
package net.sf.jfilesync.prop;

import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class LanguageBundle {

    private final static String DEFAULT_LANG = "en";
    private static String currentLang = DEFAULT_LANG;
    private static LanguageBundle instance;
    private ResourceBundle bundle;

    private static final Logger LOGGER = Logger.getLogger(LanguageBundle.class
            .getName());

    private LanguageBundle() {
        bundle = Locales.getInstance().loadBundle(currentLang);
    }

    public static String getDefaultLanguageKey() {
        return DEFAULT_LANG;
    }

    public static LanguageBundle getInstance() {
        if (instance == null) {
            instance = new LanguageBundle();
        }
        return instance;
    }

    public static void setLanguage(final String langKey) {
        currentLang = langKey;
        instance = null;
    }

    public static String getLanguage() {
        return currentLang;
    }

    public String getMessage(final String key) {
        String msg = "missing";
        try {
            msg = bundle.getString(key);
        } catch (MissingResourceException mrex) {
            LOGGER.warning("locale key " + key + " missing for lang "
                    + currentLang);
        }
        return msg;
    }

    public List<String> getAvailableLanguages() {
        return Locales.getInstance().getAvailableLanguages();
    }

    public String getLanguageDescription(final String key) {
        return Locales.getInstance().getDescription(key);
    }

    public String getFlagPath(String langKey) {
        return Locales.getInstance().getFlagPath(langKey);
    }

}
