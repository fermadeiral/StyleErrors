/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.widgets;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author BREDEX GmbH
 * @created 19.08.2013
 */
public class CheckedURLText extends CheckedText {
    /**
     * Implementation of the URL validator
     */
    private static class URLValidator implements IValidator {
        /** {@inheritDoc} */
        public ValidationState validateInput(VerifyEvent e) {
            ValidationState val;
            Text txt = (Text)e.widget;
            
            try {
                new URL(txt.getText());
                val = ValidationState.OK;
            } catch (MalformedURLException exception) {
                val = ValidationState.MightMatchAccept;
            }
            
            return val;
        }
    }

    /**
     * Warns if no text is supplied
     * @param parent SWT
     * @param style SWT 
     */
    public CheckedURLText(Composite parent, int style) {
        super(parent, style, new URLValidator());
    }
}
