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
 * $Id: Locales.java,v 1.6 2006/09/04 16:01:18 hunold Exp $
 */
package net.sf.jfilesync.prop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jfilesync.settings.ConfigFileHandler;

public class Locales {

  private final static String JAR_RESOURCE_NAME = "/net/sf/jfilesync/prop/bundles";
  private final static Pattern p = Pattern
      .compile("MessageBundle_(.*)\\.properties");
  private final static String LOCALE_DIR_NAME = "locales";

  private static HashMap<String, String> knownLocales = new HashMap<String, String>();
  private static HashMap<String, String> flagMap = new HashMap<String, String>();
  private static final String flagPath = "/net/sf/jfilesync/settings/locale/flags";

  private final HashMap<String, String> localeMap = new HashMap<String, String>();
  private final HashMap<String, Boolean> extLocaleMap = new HashMap<String, Boolean>();

  private final static Logger LOGGER = Logger.getLogger(Locales.class.getName());

  private static Locales locales;

  static {
    knownLocales.put("en", "English");
    knownLocales.put("de", "Deutsch");
    knownLocales.put("pt_BR", "Portugu\u00eas [Brasil]");
    knownLocales.put("zh_CN", "Chinese simplified");
    knownLocales.put("fr_FR", "French");
    knownLocales.put("ja_JP", "Japanese");
    knownLocales.put("pl_PL", "Polish");
    knownLocales.put("zh_TW", "Chinese traditional");
    knownLocales.put("it_IT", "Italian");
    knownLocales.put("es_AR", "Spanish [Argentina]");
    knownLocales.put("es_ES", "Spanish [Spain]");
    knownLocales.put("nl_NL", "Dutch");

    flagMap.put("en", flagPath + "/uk.png");
    flagMap.put("de", flagPath + "/de.png");
    flagMap.put("pt_BR", flagPath + "/br.png");
    flagMap.put("zh_CN", flagPath + "/cn.png");
    flagMap.put("fr_FR", flagPath + "/fr.png");
    flagMap.put("ja_JP", flagPath + "/jp.png");
    flagMap.put("pl_PL", flagPath + "/pl.png");
    flagMap.put("zh_TW", flagPath + "/tw.png");
    flagMap.put("it_IT", flagPath + "/it.png");
    flagMap.put("es_AR", flagPath + "/ar.png");
    flagMap.put("es_ES", flagPath + "/es.png");
    flagMap.put("nl_NL", flagPath + "/nl.png");
  }

  private Locales() {

    Iterator<String> localeKeyIt = knownLocales.keySet().iterator();
    while (localeKeyIt.hasNext()) {
      final String localeKey = (String) localeKeyIt.next();
      localeMap.put(localeKey, JAR_RESOURCE_NAME + "/" + "MessageBundle_"
          + localeKey + ".properties");
      LOGGER.info("put : " + localeMap.get(localeKey));
    }

  }

  public static synchronized Locales getInstance() {
    if (locales == null) {
      locales = new Locales();
      locales.load();
    }
    return locales;
  }

  public void load() {
    loadFromDir();
  }

  protected void loadFromDir() {

    final String configDir = ConfigFileHandler.getInstance().getConfigDirLocation();
    final String localeDir = configDir + "/" + LOCALE_DIR_NAME;

    File localeDirFile = new File(localeDir);
    if (!localeDirFile.exists()) {
      LOGGER.info("locale dir does not exist : " + localeDir);
      return;
    }

    String[] contents = localeDirFile.list();
    for (int i = 0; contents != null && i < contents.length; i++) {
      Matcher m = p.matcher(contents[i]);
      if (m.matches()) {
        String localeName = m.group(1);
        LOGGER.info("found locale : " + localeName);
        localeMap.put(localeName, localeDir + "/" + "MessageBundle_"
            + localeName + ".properties");
        extLocaleMap.put(localeName, Boolean.TRUE);
      }
    }

  }

  public ResourceBundle loadBundle(final String localeKey) {
    ResourceBundle bundle;

    if (localeMap.containsKey(localeKey)) {
      String localeLocation = (String) localeMap.get(localeKey);
      LOGGER.info("locale location : " + localeLocation);

      try {

        InputStream localeStream;
        if (extLocaleMap.containsKey(localeKey)) {
          LOGGER.info("trying to load locale from " + localeLocation);
          localeStream = new FileInputStream(localeLocation);
        } else {
          localeStream = Locales.class.getResourceAsStream(localeLocation);
        }

        if (localeStream == null) {
          LOGGER.warning("Cannot find property file " + localeLocation);
          bundle = getDefaultResourceBundle();
        } else {
          PropertyResourceBundle propertyBundle = new PropertyResourceBundle(
              localeStream);
          bundle = new FileResourceBundle(getDefaultResourceBundle(),
              propertyBundle);
        }

      } catch (IOException e) {
        LOGGER.warning("Cannot load property file " + localeLocation);
        bundle = getDefaultResourceBundle();
      }

    } else {
      LOGGER.warning("locale " + localeKey + " not found. Using default.");
      bundle = getDefaultResourceBundle();
    }

    return bundle;
  }

  private ResourceBundle getDefaultResourceBundle() {
    ResourceBundle bundle = ResourceBundle.getBundle(
        "net.sf.jfilesync.prop.bundles.MessageBundle", new Locale(
            LanguageBundle.getDefaultLanguageKey()));
    return bundle;
  }

  public static void main(String[] args) {
    Locales.getInstance();
  }

  public List<String> getAvailableLanguages() {
    final List<String> languageKeyList = new ArrayList<String>();

    for(String langKey : localeMap.keySet()) {
      languageKeyList.add(langKey);
    }
    Collections.sort(languageKeyList);

    return languageKeyList;
  }

  public String getDescription(final String key) {
    String descr = key;
    if (knownLocales.containsKey(key)) {
      descr = (String) knownLocales.get(key);
    }
    return descr;
  }

  public String getFlagPath(final String langKey) {
    String flagPath = null;
    if (flagMap.containsKey(langKey)) {
      flagPath = (String) flagMap.get(langKey);
    }
    return flagPath;
  }

}
