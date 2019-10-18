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
package org.eclipse.jubula.examples.aut.adder.rcp;

import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 * 
 * @author BREDEX GmbH
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
    /** The instance of this application action bar advisor */
    private static ApplicationActionBarAdvisor instance;

    /**
     * Standard RCP
     * @param configurer Standard RCP
     */
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
        instance = this;
    }

    /**
     * @return The instance of this class, or null.
     */
    public static ApplicationActionBarAdvisor getInstance() {
        return instance;
    }

    /**
     * @param text The text to show in the status line of the application.
     */
    public void setStatusText(String text) {
        getActionBarConfigurer().getStatusLineManager().setMessage(text);
    }
}