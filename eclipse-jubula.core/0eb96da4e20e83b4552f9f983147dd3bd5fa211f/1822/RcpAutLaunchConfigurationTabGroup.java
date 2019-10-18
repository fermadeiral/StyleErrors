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
package org.eclipse.jubula.launch.rcp.ui;

import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.pde.ui.launcher.EclipseLauncherTabGroup;

/**
 * Tab group for "Start Eclipse RCP AUT" launch configuration.
 * 
 * @author BREDEX GmbH
 * @created 18.07.2011
 */
public class RcpAutLaunchConfigurationTabGroup extends
        EclipseLauncherTabGroup {

    /**
     * 
     * {@inheritDoc}
     */
    public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
        super.createTabs(dialog, mode);
        
        ILaunchConfigurationTab[] baseTabs = getTabs();
        
        ILaunchConfigurationTab[] extendedTabs = {
            new RcpAutLaunchConfigurationTab()
        };
        
        ILaunchConfigurationTab[] tabs = 
            new ILaunchConfigurationTab[baseTabs.length + extendedTabs.length];
        
        System.arraycopy(baseTabs, 0, tabs, 0, baseTabs.length);
        System.arraycopy(extendedTabs, 0, tabs, 
                baseTabs.length, extendedTabs.length);
        
        setTabs(tabs);
    }

}
