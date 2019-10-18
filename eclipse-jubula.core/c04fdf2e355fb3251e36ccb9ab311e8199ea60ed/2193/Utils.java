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
package org.eclipse.jubula.client.ui.preferences.utils;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.JBException;


/**
 * @author BREDEX GmbH
 * @created Sep 2, 2008
 */
public class Utils {
    
    /**
     * Constructor
     */
    private Utils() {
        // do nothing
    }
    
    
    /**
     * decode String from the preference store to a SortedSet
     * @param store string read from preference store
     * @param delimiter delimiter to separate string part from index 0 to delimiter
     * @return decoded Set of Strings
     * @throws JBException in case of problem with preference store
     */
    public static SortedSet<String> decodeStringToSet(String store,
            String delimiter) 
        throws JBException {
        SortedSet<String> decodedSet = new TreeSet<String>();
        String storage = store;
        while (storage.length() > 0) {
            String valueName = decodeString(storage, delimiter);
            storage = storage.substring(storage.indexOf(delimiter) + 1);
            decodedSet.add(valueName);
        }
        
        return decodedSet;
    }
    
    /**
     * @param encodedString decode a base64 encoded string
     * @param delimiter delimiter to separate string part from index 0 to delimiter
     * @return decoded string part
     * @throws JBException in case of not base64 decoded string
     */
    public static String decodeString(String encodedString, String delimiter) 
        throws JBException {
        String decodedString = StringConstants.EMPTY;
        checkPreferences(encodedString.substring(0, 
            encodedString.indexOf(delimiter)));
        decodedString = new String(Base64.decodeBase64(
            encodedString.substring(0, 
                encodedString.indexOf(delimiter)).getBytes()));
        return decodedString;
    }
    
    
    /**
     * Checks correctness of the stored preferences.
     * @param pref The readed, base64-coded preference.
     * @throws JBException if the prefrence is not base64-coded.
     */
    public static void checkPreferences(String pref) throws JBException {
        if (!Base64.isBase64(pref.getBytes())) {
            throw new JBException(StringConstants.EMPTY, new Integer(0));
        }
    }
    
    /**
     * @param stringArray The readed, base64-coded preference.
     * @param delimiter delimiter to seperate values
     * @return encoded SortedSet
     */
    public static String encodeStringArray(String[] stringArray,
            String delimiter) {
        SortedSet<String> stringSet =
            new TreeSet<String>(Arrays.asList(stringArray));
        String storage = StringConstants.EMPTY;
        for (String value : stringSet) {
            byte[] valueArray = value.getBytes();
            String valueEncoded = new String(Base64.encodeBase64(valueArray));
            storage = storage + valueEncoded + delimiter;
        }
        return storage;
    }

}
