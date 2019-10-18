/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.tester.util;

/**
 * Because JavaFX uses doubles on various places and the RC-Architecture expects
 * integers, we have to round a lot. This class should be used, to easily change
 * the rounding at a central place.
 *
 * @author BREDEX GmbH
 * @created 28.1.2014
 */
public class Rounding {

    /**
     * Private Constructor
     */
    private Rounding() {
        // Private Constructor
    }

    /**
     * Using (int)Math.round(x)
     *
     * @param d
     *            the double value
     * @return the rounded int value
     */
    public static int round(double d) {
        return (int) Math.round(d);
    }

}
