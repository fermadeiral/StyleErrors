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
package org.eclipse.jubula.client.ui.rcp.sourceprovider;

import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISourceProvider;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.services.ISourceProviderService;

/**
 * Base class for all source providers in Jubula. Ensures that listeners are
 * notified on the UI thread, provided the corresponding gd..() methods are used
 * rather than their counterparts in the parent class.
 * 
 * @author BREDEX GmbH
 * @created Apr 14, 2010
 */
public abstract class AbstractJBSourceProvider extends AbstractSourceProvider {

    /**
     * Notifies all listeners that a single source has changed.
     * 
     * The notification takes place in the UI thread (via asyncExec, if the
     * current thread is not the UI thread).
     * 
     * @see AbstractSourceProvider#fireSourceChagned(int, java.lang.String,
     *      java.lang.Object)
     * @param sourcePriority
     *            The source priority that has changed.
     * @param sourceName
     *            The name of the source that has changed; must not be
     *            <code>null</code>.
     * @param sourceValue
     *            The new value for the source; may be <code>null</code>.
     */
    protected final void gdFireSourceChanged(final int sourcePriority,
            final String sourceName, final Object sourceValue) {

        Display display = PlatformUI.getWorkbench().getDisplay();
        if (display.getThread() == Thread.currentThread()) {
            fireSourceChanged(sourcePriority, sourceName, sourceValue);
        } else {
            display.asyncExec(new Runnable() {
                @SuppressWarnings("synthetic-access")
                public void run() {
                    fireSourceChanged(sourcePriority, sourceName, sourceValue);
                }
            });
        }

    }

    /**
     * Notifies all listeners that multiple sources have changed.
     * 
     * The notification takes place in the UI thread (via asyncExec, if the
     * current thread is not the UI thread).
     * 
     * @see AbstractSourceProvider#fireSourceChagned(int, java.util.Map)
     * @param sourcePriority
     *            The source priority that has changed.
     * @param sourceValuesByName
     *            The map of source names (<code>String</code>) to source values
     *            (<code>Object</code>) that have changed; must not be
     *            <code>null</code>. The names must not be <code>null</code>,
     *            but the values may be <code>null</code>.
     */
    protected final void gdFireSourceChanged(final int sourcePriority,
            final Map sourceValuesByName) {

        Display display = PlatformUI.getWorkbench().getDisplay();
        if (display.getThread() == Thread.currentThread()) {
            fireSourceChanged(sourcePriority, sourceValuesByName);
        } else {
            display.asyncExec(new Runnable() {
                @SuppressWarnings("synthetic-access")
                public void run() {
                    fireSourceChanged(sourcePriority, sourceValuesByName);
                }
            });
        }
    }

    /**
     * @param event
     *            the ExecutionEvent if available
     * @param sourceProviderID
     *            the id of the source provider to retrieve
     * @return the source provider instance or <code>null</code> if not found
     */
    public static ISourceProvider getSourceProviderInstance(
            ExecutionEvent event, String sourceProviderID) {
        IWorkbenchWindow ww;
        if (event == null) {
            ww = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        } else {
            ww = HandlerUtil.getActiveWorkbenchWindow(event);
        }
        ISourceProviderService s = ww.getService(ISourceProviderService.class);
        if (s != null) {
            return s.getSourceProvider(sourceProviderID);
        }
        return null;
    }
}
