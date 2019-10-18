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
package org.eclipse.jubula.examples.aut.adder.rcp.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * @author BREDEX GmbH
 */
public class DefaultPerspective implements IPerspectiveFactory {
    /** {@inheritDoc} */
    public void createInitialLayout(IPageLayout layout) {
        layout.setEditorAreaVisible(false);
        layout.addView("org.eclipse.jubula.examples.aut.adder.rcp.view", //$NON-NLS-1$
            IPageLayout.LEFT, 0.5f, layout.getEditorArea());
    }
}