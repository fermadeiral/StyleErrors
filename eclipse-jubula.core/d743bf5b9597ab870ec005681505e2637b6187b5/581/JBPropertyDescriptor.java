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
package org.eclipse.jubula.client.ui.rcp.controllers.propertydescriptors;

import org.eclipse.jubula.client.ui.controllers.propertysources.IPropertyController;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.client.ui.widgets.JBText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.PropertyDescriptor;


/**
 * @author BREDEX GmbH
 * @created 27.01.2005
 */
public class JBPropertyDescriptor extends PropertyDescriptor {
    
    /**
     * Creates a new property descriptor with the given id and display name
     * @param id The associated property controller
     * @param displayName the name to display for the property
     */
    public JBPropertyDescriptor(IPropertyController id, String displayName) {
        super(id, displayName);
    }
    
    /**
     * @param parent the parent.
     * @return a new always disables Text widget.
     */
    public Control createPropertyWidget(Composite parent) {
        Control control = new DisabledTextField(parent, 0);
        return control;
    }

    /**
     * An always disabled Text field.
     * @author BREDEX GmbH
     * @created 02.02.2005
     */
    public static class DisabledTextField extends JBText {
        
        /**
         * Constructor
         * @param parent the parent
         * @param style the style
         * {@inheritDoc}
         */
        public DisabledTextField(Composite parent, int style) {
            super(parent, style);
            setDisabled();
            super.setForeground(LayoutUtil.GRAY_COLOR);
        }
        
        /**
         * {@inheritDoc}
         */
        public void setForeground(Color color) {
            // nothing
        }

        /**
         * Sets this TextField disabled with enabled-background.
         */
        private void setDisabled() {
            Color background = super.getBackground();
            super.setEditable(false);
            setBackground(background);
        }
        
        /**
         * Overrides Text.setEnabled().
         * Does nothing. 
         * This TextField is always disabled!
         * @param bool a boolean flag.
         */
        public void setEnabled(boolean bool) {
            // do nothing. This TextField is always disabled!
        } 
        
        /**
         * {@inheritDoc}
         */
        public void setEditable(boolean editable) {
            // do nothing. This TextField is never editable!
        }
    
        /**
         * Necessary to subclass!
         * {@inheritDoc}
         */
        protected void checkSubclass() {
            // do nothing    
        }
    }
}