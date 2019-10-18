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
package org.eclipse.jubula.toolkit.api.gen.internal.utils;

import java.util.Calendar;
import java.util.TimeZone;

/** @author BREDEX GmbH */
public class GenerateUtil {
    /** Constructor */
    private GenerateUtil() {
        // hide
    }
    
    /**
     * @return a string representing the current time following the ISO 8601
     *         standard
     */
    public static String getISO8601Timestamp() {
        return javax.xml.bind.DatatypeConverter.printDateTime(Calendar
                .getInstance(TimeZone.getTimeZone("UTC"))); //$NON-NLS-1$
    }
}
