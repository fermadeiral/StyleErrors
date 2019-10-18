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
package org.eclipse.jubula.tools.internal.utils;

import java.awt.AWTError;
import java.awt.Toolkit;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jubula.tools.internal.constants.EnvConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * Utility class for handling environment variables.
 *
 * @author BREDEX GmbH
 * @created 04.06.2008
 */
public class EnvironmentUtils {
    /**
     * <code>AWT_MULTI_CLICK_INTERVAL_PROPERTY_NAME</code>
     */
    private static final String AWT_MULTI_CLICK_INTERVAL_PROPERTY_NAME = "awt.multiClickInterval"; //$NON-NLS-1$

    /** Key for Java's OS Name property. */
    private static final String OS_NAME_KEY = "os.name"; //$NON-NLS-1$

    /** OS Name property. */
    private static final String OS_NAME = System.getProperty(OS_NAME_KEY);

    /** OS Name property in lower case */
    private static final String OS_NAME_LOWER_CASE = OS_NAME.toLowerCase();

    /** Constant for Dos os name */
    private static final String OS_NAME_DOS = "dos"; //$NON-NLS-1$

    /** Constant for Solaris os name */
    private static final String OS_NAME_SOLARIS = "sunos"; //$NON-NLS-1$

    /** Substring for matching Windows. */
    private static final String OS_NAME_WIN = "windows"; //$NON-NLS-1$

    /** Substring for matching MacOS. */
    private static final String OS_NAME_MAC = "mac"; //$NON-NLS-1$

    /** Substring for matching Linux. */
    private static final String OS_NAME_LIN = "lin"; //$NON-NLS-1$

    /**
     * Private constructor
     */
    private EnvironmentUtils() {
        // private constructor for utility class
    }

    /**
     * checks the host operating system
     * @return true if the host OS is Microsoft Windows
     */
    public static boolean isWindowsOS() {
        return OS_NAME_LOWER_CASE.indexOf(OS_NAME_WIN) != -1;
    }

    /**
     * @return <code>true</code> if the current OS is DOS. Otherwise, 
     *         <code>false</code>
     */
    public static boolean isDosOS() {
        return OS_NAME_LOWER_CASE.indexOf(OS_NAME_DOS) != -1;
    }

    /**
     * @return <code>true</code> if the current OS is Win 9x. Otherwise, 
     *         <code>false</code>
     */
    public static boolean isWin9xOS() {
        return isWindowsOS()
                && (OS_NAME_LOWER_CASE.indexOf("95") != -1 //$NON-NLS-1$
                || OS_NAME_LOWER_CASE.indexOf("98") != -1 //$NON-NLS-1$
                || OS_NAME_LOWER_CASE.indexOf("ME") != -1); //$NON-NLS-1$
    }
    
    /**
     * @return <code>true</code> if the current OS is Solaris. Otherwise, 
     *         <code>false</code>
     */
    public static boolean isSolarisOS() {
        return OS_NAME_LOWER_CASE.indexOf(OS_NAME_SOLARIS) != -1;
    }
    
    /**
     * @return <code>true</code> if the current OS is Mac OS. Otherwise, 
     *         <code>false</code>
     */
    public static boolean isMacOS() {
        return OS_NAME_LOWER_CASE.indexOf(OS_NAME_MAC) != -1;
    }

    /**
     * @return <code>true</code> if the current OS is Linux. Otherwise, 
     *         <code>false</code>
     */
    public static boolean isLinuxOS() {
        return OS_NAME_LOWER_CASE.indexOf(OS_NAME_LIN) != -1;
    }

    /**
     * Gets the environment settings of the current process.
     * 
     * @return a <code>Properties</code> object with the
     *         defined environments variables and corresponding values.
     */
    public static Properties getProcessEnvironment() {
        final Properties env = new Properties();
        env.putAll(System.getenv());
        return env;
    }

    /**
     * Converts a <code>String</code> array in a <code>Properties</code>.
     * The Strings have to have the form key&lt;<code>separator</code>&gt;value.
     * For example: If the <code>separator</code> is <code>=</code>, the 
     * Strings would need to have the form key=value.
     * 
     * @param strArray
     *            the <code>String</code> array.
     * @param separator
     *            the string that separates keys and values.
     * @return a <code>Properties</code> object.
     */
    public static Properties strArrayToProp(
            String[] strArray, String separator) {
        
        Properties prop = new Properties();
        String key = null;
        String value = null;
        String tmpStr = null;
        for (int i = 0; i < strArray.length; i++) {
            tmpStr = strArray[i];
            int index = tmpStr.indexOf(separator); 
            if (index > -1) {   
                key = tmpStr.substring(0, index);
                value = tmpStr.substring(index + 1, tmpStr.length());
                prop.put(key, value);
            }
        }
        return prop;
    }

    /**
     * Sets new process properties by a given set of properties
     * @param oldProp the process properties to be changed.
     * @param newProp the process properties to be set.
     * @return the changed process properties.
     */
    public static Properties setEnvironment(
            Properties oldProp, Properties newProp) {
        
        Enumeration enum1 = newProp.keys();
        while (enum1.hasMoreElements()) {
            String key = enum1.nextElement().toString();
            String value = newProp.getProperty(key);
            oldProp.setProperty(key, value);
        }
        return oldProp;
    }
    
    /**
     * Converts a <code>Properties</code> into a <code>String</code> array.
     * Each String in the returned array is of the form 
     * key&lt;<code>separator</code>&gt;value.
     * For example: If the <code>separator</code> is <code>=</code>, 
     * the Strings will have the form key=value.
     * @param prop the <code>Properties</code> to be converted.
     * @param separator the string that separates 
     * @return a <code>String</code> array of <code>Properties</code>.
     */
    public static String[] propToStrArray(Properties prop, String separator) {
        Enumeration enumVal = prop.elements();
        Enumeration enumKey = prop.keys();
        final int keyCount = prop.size();
        String[] strArray = new String[keyCount];

        int i = 0;
        while (enumKey.hasMoreElements()) {
            strArray[i++] = enumKey.nextElement().toString();
        }
        i = 0;
        while (enumVal.hasMoreElements()) {
            strArray[i] = strArray[i] 
                + separator
                + enumVal.nextElement().toString();
            i++;
        }
        return strArray;
    }
    
    /**
     * Converts a <code>String</code> to an array of Strings.
     * @param str the String to be converted.
     * @param separators the separators which are used to split up the String.
     * @return an array of String.
     */
    public static String[] strToStrArray(String str, String separators) {
        StringTokenizer tokenizer = 
            new StringTokenizer(str, separators); 
        int count = tokenizer.countTokens();
        String[] strArray = new String[count];
        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            strArray[i++] = tokenizer.nextToken().trim();
        }
        return strArray;
    }

    /**
     * @see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4908395
     * @return the platform specific double click speed in milli secs or -1 if
     *         not resolvable
     */
    public static int getPlatformDoubleClickSpeed() {
        int doubleClickSpeed = -1;
        try {
            Toolkit tk = Toolkit.getDefaultToolkit();
            if (tk != null) {
                Object value = tk.getDesktopProperty(
                        AWT_MULTI_CLICK_INTERVAL_PROPERTY_NAME);
                doubleClickSpeed = Integer.valueOf(
                        String.valueOf(value)).intValue();
            }
        } catch (NumberFormatException e) {
            // ignore
        } catch (AWTError e) {
            // ignore
        }
        return doubleClickSpeed;
    }
    
    /**
     * @return the defined AUT Agent environment port no or <code>-1</code> of
     *         not found
     */
    public static int getAUTAgentEnvironmentPortNo() {
        int port = -1;
        String portStr = getProcessEnvironment().getProperty(
                EnvConstants.AUT_AGENT_PORT);
        if ((portStr != null)
                && (!portStr.trim().equals(StringConstants.EMPTY))) {
            try {
                port = Integer.valueOf(portStr).intValue();
            } catch (NumberFormatException nfe) {
                // is ok here - do nothing
            }
        }
        return port;
    }
    
    /**
     * @param key
     *            the key to retrieve the property for
     * @return if set either the process environment property or if not set the
     *         system environment property. Returns <code>null</code> if non of
     *         both is set
     */
    public static String getProcessOrSystemProperty(final String key) {
        String value = getProcessEnvironment().getProperty(key);
        if (value == null) {
            value = System.getProperty(key);
        }
        return value;
    }

    /**
     * Concatenates two given string values to a property string, i.e. "property=value"
     * @param property the property
     * @param value the value for the property
     * @return the concatenated string
     */
    public static String toPropertyString(String property, String value) {
        return property + StringConstants.EQUALS_SIGN + value;
    }

    /**
     * Returns null if -key is not present in the String
     *         empty String if it is present, but not followed by a value
     *         the value otherwise
     * @param args the arguments
     * @param key the key
     * @return the value
     */
    public static String getArgValue(String[] args, String key) {
        int pos = ArrayUtils.indexOf(args, StringConstants.MINUS + key);
        if (pos < 0 || pos >= args.length) {
            return null;
        }
        if (pos == args.length - 1 || args[pos + 1] == null
                || args[pos + 1].startsWith(StringConstants.MINUS)) {
            return StringConstants.EMPTY;
        }
        return args[pos + 1];
    }
}
