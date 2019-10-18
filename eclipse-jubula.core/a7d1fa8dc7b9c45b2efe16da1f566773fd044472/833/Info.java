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
package org.eclipse.jubula.tools.internal.utils.generator;

/**
 * Defines any kind of info class for Component, Action and Param.
 * 
 * @author BREDEX GmbH
 * @created 23.09.2005
 */
public abstract class Info {
    /**
     * The localized name of the Component, Action or Param.
     */
    private String m_i18nName;
    /**
     * @param i18nName
     *            The localized name of the Component, Action or Param.
     */
    protected Info(String i18nName) {
        m_i18nName = i18nName;
    }
    /**
     * @return Returns the localized name of the Component, Action or Param..
     */
    public String getI18nName() {
        return m_i18nName;
    }
}
