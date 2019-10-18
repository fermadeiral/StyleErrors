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
package org.eclipse.jubula.examples.aut.dvdtool.resources;

import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

/**
 * Accessor class for internationalizable messages and other resources, like images.
 *
 * @author BREDEX GmbH
 * @created 11.04.2005
 */
public class Resources {
    /** the application icon */
    public static final String APP_ICON = "icon.png"; //$NON-NLS-1$
    /** the icon for a category with dvds */
    public static final String CAT_ICON = "category.png"; //$NON-NLS-1$
    /** the icon for a category with no dvds */
    public static final String EMPTY_CAT_ICON = "empty_category.png"; //$NON-NLS-1$
    /** the directory to the resources */
    private static final String RESOURCES_DIR = "resources"; //$NON-NLS-1$
    /** the bundle name */
    private static final String BUNDLE_NAME = "org.eclipse.jubula.examples.aut.dvdtool.resources.messages"; //$NON-NLS-1$

    /** the resource bundle */
    private static ResourceBundle resourceBundle;
    
    /**
     * static initialisation
     */
    static { 
        load();
    }

    /**
     * private constructor, use static methods
     */
    private Resources() {
        // empty
    }

    /**
     * @return the name of the welcome screen
     */
    public static String getWelcomeScreenName() {
        return Resources.getString("dvdtool.html"); //$NON-NLS-1$
    }

    /**
     * private method loading the resource bundle
     */
    private static void load() {
        // unset the current resource bundle 
        resourceBundle = null;
        try {
            resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
        } catch (MissingResourceException mre) {
            mre.printStackTrace();
        }
    }
    
    /**
     * Returns the string for the given <code>key</code>. If the resource
     * bundle could not initialized, or <code>key</code> was not found, the
     * key enclosed with '!' is returned.
     * 
     * @param key
     *            the key to search for
     * @return the assigned string
     */
    public static String getString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (NullPointerException npe) {
            return '!' + key + '!';
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
    
    /**
     * changes the language to <code>locale</code>
     * @param locale the new locale, use constants from java.util.Locale
     */
    public static void setLanguage(Locale locale) {
        Locale.setDefault(locale);
        
        // reload the resource bundle
        load();
    }
    
    /**
     * creates a url to <code>name</code> with file: protocol
     * @param name name of a file, use the defined constants/getter here 
     * @return a new String containing the url
     */
    public static String getFileUrl(String name) {
        URL url = getURLResource("/" + RESOURCES_DIR + "/" + name); //$NON-NLS-1$ //$NON-NLS-2$
        if (url != null) {
            return url.toExternalForm();
        }
        
        return null;
    }
    
    /**
     * returns the image icon with <code>name</code>
     * @param name the name of the, use the defined constants/getter here
     * @return the image icon
     */
    public static ImageIcon getImageIcon(String name) {
        URL url = getURLResource("/" + RESOURCES_DIR + "/" + name); //$NON-NLS-1$ //$NON-NLS-2$
        if (url != null) {
            return new ImageIcon(url);
        } 
        
        return new ImageIcon();
    }
    
    /**
     * return an URL for resource <code>filename</code> if found by the classloader
     * @param filename filename of the resource
     * @return an URL or null
     */
    private static URL getURLResource(String filename) {
        return Resources.class.getResource(filename);
    }

}
