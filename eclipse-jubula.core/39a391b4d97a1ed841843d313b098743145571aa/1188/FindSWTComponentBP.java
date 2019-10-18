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
package org.eclipse.jubula.rc.swt.components;

import org.eclipse.jubula.rc.common.components.FindComponentBP;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.swt.driver.EventThreadQueuerSwtImpl;
import org.eclipse.jubula.tools.internal.constants.SwtToolkitConstants;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * @author BREDEX GmbH
 * @created 19.04.2006
 */
public class FindSWTComponentBP extends FindComponentBP {

    /**
     * Searches for the component in the AUT with the given
     * <code>componentIdentifier</code>.
     * 
     * @param componentIdentifier
     *            the identifier created in object mapping mode
     * @param autHierarchy
     *            the current AUT hierarchy
     * @throws IllegalArgumentException
     *             if the given identifier is null or <br>
     *             the hierarchy is not valid: empty or containing null elements
     * @return the technical component
     */
    protected Object findComponent(final IComponentIdentifier 
        componentIdentifier, final SwtAUTHierarchy autHierarchy) 
        throws IllegalArgumentException {
        
        EventThreadQueuerSwtImpl etQueuer = new EventThreadQueuerSwtImpl();
        return etQueuer.invokeAndWait(this.getClass().getName()
                + ".findComponent", new IRunnable<Object>() { //$NON-NLS-1$
                    public Object run() throws StepExecutionException {
                        return findComponentImpl(componentIdentifier,
                                autHierarchy);
                    }
                });
    }
    
    /**
     * 
     * @param componentIdentifier the identifier created in object mapping mode
     * @param autHierarchy the current AUT hierarchy
     * @return a FindComponentResult which contains the technical component
     * @see FindComponentBP#findComponent
     */
    private Object findComponentImpl (final IComponentIdentifier 
        componentIdentifier, final SwtAUTHierarchy autHierarchy) {
        return super.findComponent(componentIdentifier, autHierarchy);
    }

    /**
     * {@inheritDoc}
     */
    protected String getCompName(Object currentComponent) {
        return getComponentName((Widget)currentComponent);
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isAvailable(Object currComp) {
        if (currComp instanceof Control) {
            return !((Control)currComp).isDisposed() 
                && ((Control)currComp).isVisible();
        }
        return !((Widget)currComp).isDisposed();
    }
    
    /**
     * @param w the widget to get the component name for
     * @return the components name (if set) or null if not found
     */
    public static String getComponentName(Widget w) {
        String compName = null;
        Object o = w.getData(SwtToolkitConstants.WIDGET_NAME);
        if (o == null) {
            o = w.getData(SwtToolkitConstants.WIDGET_NAME_FALLBACK);
        }
        if (o == null) {
            o = w.getData(SwtToolkitConstants.RCP_NAME);
        }
        if (o != null) {
            compName = o.toString();
        }
        return compName;
    }
}