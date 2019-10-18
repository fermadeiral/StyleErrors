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
package org.eclipse.jubula.client.ui.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * The JBText widget is meant as a standard replacement for the SWT Text class.
 * JBText acts as an anchor for other derived classes. Its main purpose is to
 * supply a consistent look and feel througout the application. If needed,
 * shared properties (fonts, colors or layouts for example) may be set 
 * at one location (the JBText() constructor). No additional function is 
 * implemented in this class.
 *
 * @author BREDEX GmbH
 * @created 02.03.2006
 */
public abstract class JBText extends Text {
    /**
     * @param parent parent
     * @param style style
     */
    public JBText(Composite parent, int style) {
        super(parent, style);
    }   

    /** {@inheritDoc} */
    protected void checkSubclass() {
        // do nothing, therefor allowing subclassing
    }
}
