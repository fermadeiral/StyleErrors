/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.teststyle.gui.decoration;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author marcell
 * @created Nov 4, 2010
 */
public class GuiNodeDecoratorHandler extends DecoratorHandler implements
        ILightweightLabelDecorator {

    /** ID of this decorator */
    private static final String ID = 
        "org.eclipse.jubula.client.teststyle.tsGuiNodeDecorator"; //$NON-NLS-1$

    /**
     * Checks in the ProblemCont if the INodePO contains Elements which must be
     * decorated for violating a Checkstyle rule.
     * 
     * {@inheritDoc}
     */
    public void decorate(Object element, IDecoration decoration) {
        super.decorate(element, decoration);
    }

    /**
     * 
     */
    public static void refresh() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                PlatformUI.getWorkbench().getDecoratorManager().update(ID);
            }
        });
    }
}
