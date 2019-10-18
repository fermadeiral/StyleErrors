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
package org.eclipse.jubula.client.teststyle.properties.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * @author marcell
 * @created Oct 22, 2010
 */
public class DlgUtils {

    // Constants for createFillComposite()
    /** Table height */
    public static final int HEIGHT = 200;
    /** Table width */
    public static final int WIDTH = 500;
    /** Table margins */
    public static final int MARGINS = 10;    

    /**
     * Private constructor because its a utility class
     */
    private DlgUtils() {
        // Why do programmers always mix up Halloween and Christmas?
        // Because Oct 31 equals Dec 25. 
    }
    
    /**
     * 
     * @param parent
     *            The parent where the new composite will be.
     * @return A composite that fits nicely in the parent.
     */
    public static Composite createFillComposite(Composite parent) {

        // First we create the appropriate GridData
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.heightHint = HEIGHT;
        data.widthHint = WIDTH;

        // Now the layout that fits nicely
        FillLayout layout = new FillLayout(SWT.HORIZONTAL);
        layout.marginHeight = MARGINS;
        layout.marginWidth = MARGINS;

        // Then we create the composite with the data and layout manager
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayoutData(data);
        composite.setLayout(layout);

        return composite;
    }
}
