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
package org.eclipse.jubula.client.ui.rcp.widgets;

import java.io.File;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author BREDEX GmbH
 * @created 26.09.2007
 */
public class CheckedDirnameText extends CheckedText {

    /**
     * @author BREDEX GmbH
     * @created 26.09.2007
     */
    private static class DirnameValidator implements IValidator {

        /**
         * {@inheritDoc}
         */
        public ValidationState validateInput(VerifyEvent e) {
            Text txt = (Text)e.widget;
            
            final String oldValue = txt.getText();
            StringBuilder workValue = new StringBuilder(oldValue);
            workValue.replace(e.start, e.end, e.text);
            String newValue = workValue.toString();

            if (!txt.isEnabled()) {
                return ValidationState.EmptyAccept;
            }

            File f = new File(newValue);
            if (!f.isAbsolute()) {
                return ValidationState.MightMatchAccept;
            }
            if (f.isDirectory() && f.canRead()) {
                return ValidationState.OK;
            }
            
            return ValidationState.MightMatchAccept;
        }

    }

    /**
     * Checks if the entered text is a valid directory name on the
     * current system.
     * @param parent composite
     * @param style SWT style
     */
    public CheckedDirnameText(Composite parent, int style) {
        super(parent, style, new DirnameValidator());        
    }

}
