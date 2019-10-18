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
package org.eclipse.jubula.client.ui;

import java.util.Map;

import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * Base class for plug-ins that integrate with the Eclipse platform UI.
 * 
 * @author BREDEX GmbH
 * @created 06.07.2004
 */
public class Plugin extends AbstractUIPlugin {
    /** single instance of plugin */
    private static Plugin plugin;
    
    /** 
     * {@link IWindowListener} to show an error dialog if there were errors
     * during {@link ComponentBuilder#getCompSystem()} in the startup
     */
    private class ShowCompSysErrosListener implements IWindowListener {
        
        /**{@inheritDoc}
         */
        public void windowOpened(IWorkbenchWindow window) {
            /** Show a error message for Errors during initialization of the CompSystem */
            Map<String, Exception> exceptionList = ComponentBuilder
                    .getInstance().getInitExceptions();
            for (Map.Entry<String, Exception> entry 
                    : exceptionList.entrySet()) {
                ErrorHandlingUtil.createMessageDialog(
                        MessageIDs.E_TOOLKIT_COMPSYS_ERROR,
                        new Object[]{entry.getKey()},
                        ErrorHandlingUtil.getStackTrace(entry.getValue()))
                        .create();
            }
            /** remove the listener because this should be a one time dialog */
            PlatformUI.getWorkbench().removeWindowListener(this);
        }

        /** {@inheritDoc} */
        public void windowDeactivated(IWorkbenchWindow window) {
            // nothing
        }

        /** {@inheritDoc} */
        public void windowClosed(IWorkbenchWindow window) {
            // nothing
        }

        /** {@inheritDoc} */
        public void windowActivated(IWorkbenchWindow window) {
            // nothing
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        PlatformUI.getWorkbench().addWindowListener(
                new ShowCompSysErrosListener());
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        plugin = null;
    }

    /**
     * @return instance of plugin
     */
    public static Plugin getDefault() {
        return plugin;
    }
}