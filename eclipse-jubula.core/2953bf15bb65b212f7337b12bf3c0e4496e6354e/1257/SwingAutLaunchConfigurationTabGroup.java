/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.launch.java.ui;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaMainTab;
import org.eclipse.jubula.launch.ui.tab.AutLaunchConfigurationTab;

/**
 * Tab group for "Start Java / Swing AUT" launch configuration.
 * 
 * @author BREDEX GmbH
 * @created 13.07.2011
 */
public class SwingAutLaunchConfigurationTabGroup extends
        AbstractLaunchConfigurationTabGroup {

    /**
     * 
     * {@inheritDoc}
     */
    public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
        ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
            new JavaMainTab(),
            new JavaArgumentsTab(),
            new JavaJRETab(),
            new JavaClasspathTab(),
            new SourceLookupTab(),
            new EnvironmentTab(),
            new CommonTab(),
            new AutLaunchConfigurationTab()
        };
        
        setTabs(tabs);
    }

}
