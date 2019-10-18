/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.utils;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.SwtToolkitConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 * @created 14.02.2005
 */
public class Languages {
    /** the BUNDLE_ID_CLIENT_API */
    private static final String BUNDLE_ID_CLIENT_API = "org.eclipse.jubula.client.api"; //$NON-NLS-1$

    /** the logger */
    private static final Logger LOG = LoggerFactory.getLogger(Languages.class);
    
    /** single instance from Languages */
    private static Languages instance = null;
    
    /** Mapping from Locale to display string */
    private Map<Locale, String> m_localeToDisplayMap;

    /** Mapping from display string to Locale */
    private Map<String, Locale> m_displayToLocaleMap;
    
    /**
     * <code>m_suppLangList</code>List with Locale objects for supported languages
     */
    private List<Locale> m_suppLangList = new ArrayList<Locale>();

    /**
     * private constructor
     */
    private Languages() {
        initMap();
    }

    /**
     * getter for Singleton
     * @return the single instance
     */
    public static Languages getInstance() {
        if (instance == null) {
            instance = createInstance();
        }
        return instance;
    }
    
    /**
     * Creates (synchronized) only ONE instance of <code>Languages</code>,
     * if <code>getInstance()</code> is called in several threads.
     * @return the single instance
     */
    private static synchronized Languages createInstance() {
        if (instance == null) {
            instance = new Languages();
        }
        return instance;
    }

    /**
     * Initializes m_map with the supported language_country_codes and their display names.
     */
    private void initMap() {
        m_localeToDisplayMap = new HashMap<Locale, String>();
        m_displayToLocaleMap = new HashMap<String, Locale>();
        for (Locale l : Locale.getAvailableLocales()) {
            if (!StringConstants.EMPTY.equals(l.getCountry())) {
                m_suppLangList.add(l);
                String displayName = l.getDisplayName(Locale.getDefault());
                m_localeToDisplayMap.put(l, displayName);
                m_displayToLocaleMap.put(displayName, l);
            }
        }   
    }
    
    /**
     * @param displayString The display string for which to find the locale.
     * @return Returns the display string for the given locale.
     */
    public Locale getLocale(String displayString) {
        
        if (!m_displayToLocaleMap.containsKey(displayString)) {
            // add the locale to both maps
            Locale locale = LocaleUtil.convertStrToLocale(displayString);
            m_localeToDisplayMap.put(locale, displayString);
            m_displayToLocaleMap.put(displayString, locale);
        }

        return m_displayToLocaleMap.get(displayString);
    }

    /**
     * @param locale The locale for which to find the display string.
     * @return Returns the display string for the given locale.
     */
    public String getDisplayString(Locale locale) {
        if (!m_localeToDisplayMap.containsKey(locale)) {
            // add the locale to both maps
            String displayString = locale.getDisplayName();
            m_localeToDisplayMap.put(locale, displayString);
            m_displayToLocaleMap.put(displayString, locale);
        }

        return m_localeToDisplayMap.get(locale);
    }

    /**
     * @return Returns the suppLang.
     */
    public List<Locale> getSuppLangList() {
        return m_suppLangList;
    }
    
    /**
     * 
     * @return the names of all available Keyboard Layouts. Keyboard Layouts
     *         are contributed by this bundle and its fragments.
     */
    public String[] getKeyboardLayouts() {
        List<String> keyboardLayouts = new ArrayList<String>();
        Enumeration<URL> layoutUrls = Platform.getBundle(BUNDLE_ID_CLIENT_API)
            .findEntries(SwtToolkitConstants.KEYBOARD_MAPPING_FILE_PREFIX,
                "*" + SwtToolkitConstants.KEYBOARD_MAPPING_FILE_POSTFIX, true); //$NON-NLS-1$
        while (layoutUrls.hasMoreElements()) {
            URL layoutUrl = layoutUrls.nextElement();
            try {
                keyboardLayouts.add(URIUtil.lastSegment(
                    URIUtil.removeFileExtension(URIUtil.toURI(layoutUrl))));
            } catch (URISyntaxException e) {
                LOG.error("Error occurred while reading Keyboard Mappings.", e); //$NON-NLS-1$
            }
        }
        
        return keyboardLayouts.toArray(new String[keyboardLayouts.size()]);
    }
}