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

import java.util.Locale;

import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * @author BREDEX GmbH
 * @created 14.06.2005
 */
public final class LocaleUtil {
    /**
     * private constructor
     */
    private LocaleUtil() {
        // nothing
    }

    /**
     * @param str
     *            string to convert to a Locale
     * @return the associated locale
     */
    public static Locale convertStrToLocale(String str) {
        if (str == null) {
            return null;
        }
        String lang = null;
        String country = null;
        String variant = null;
        Locale loc = null;
        if (str.indexOf("__") > -1) { //$NON-NLS-1$
            // language, variant
            String[] s = str.split("__"); //$NON-NLS-1$
            lang = s[0];
            variant = s[1];
            loc = new Locale(lang, StringConstants.EMPTY, variant);
        } else {
            String[] s = str.split(StringConstants.UNDERSCORE);
            switch (s.length) {
                case 1:
                    lang = s[0];
                    loc = new Locale(lang);
                    break;
                case 2:
                    lang = s[0];
                    country = s[1];
                    loc = new Locale(lang, country);
                    break;
                case 3:
                    lang = s[0];
                    country = s[1];
                    variant = s[2];
                    loc = new Locale(lang, country, variant);
                    break;
                default:
                    // nothing
            }
        }
        return loc;
    }

}
