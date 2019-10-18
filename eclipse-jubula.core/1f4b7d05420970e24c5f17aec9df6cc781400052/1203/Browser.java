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
package org.eclipse.jubula.toolkit.html;

/** @author BREDEX GmbH */
public enum Browser {
    /** InternetExplorer */
    InternetExplorer,
    /** Firefox < 47 */
    Firefox,
    /** Firefox >= 47 
     * @since 3.3 **/
    FirefoxOver47,
    /** Safari */
    Safari,
    /** Chrome */
    Chrome;

    /**
     * @return is it Firefox?
     * @since 3.3
     */
    public boolean isFirefox() {
        return this.equals(Firefox) || this.equals(FirefoxOver47);
    }

}