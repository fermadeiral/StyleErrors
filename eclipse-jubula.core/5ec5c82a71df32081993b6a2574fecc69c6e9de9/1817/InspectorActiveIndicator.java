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
package org.eclipse.jubula.client.inspector.ui.contribution;

import org.eclipse.jubula.client.inspector.ui.constants.IconConstants;
import org.eclipse.jubula.client.inspector.ui.i18n.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;


/**
 * This control contribution indicates that the Inspector is currently active.
 * It should obviously only be visible when the Inspector is indeed active.
 * 
 * @author BREDEX GmbH
 * @created Jul 7, 2009
 */
public class InspectorActiveIndicator extends
        WorkbenchWindowControlContribution {

    /**
     * Constructor
     */
    public InspectorActiveIndicator() {
        super();
    }

    /**
     * Constructor
     * 
     * @param id The ID for the contribution.
     */
    public InspectorActiveIndicator(String id) {
        super(id);
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected Control createControl(Composite parent) {

        Label label = new Label(parent, SWT.NONE);
        label.setImage(IconConstants.INSPECTOR);
        label.setToolTipText(Messages.InspectorIsActive);
        return label;
    }

}
