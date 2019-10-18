/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.examples.extension.rcp.aut;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

/**
 * @author BREDEX GmbH
 */
public class ExtensionView extends ViewPart {
    
    /** constant for naming the SWT components */
    private static final String WIDGET_NAME = "TEST_COMP_NAME"; //$NON-NLS-1$
    
    /** {@inheritDoc} */
    public void createPartControl(Composite parent) {
        GridLayout shellLayout = new GridLayout ();
        shellLayout.marginWidth = 100;
        parent.setLayout(shellLayout);
        
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setData(WIDGET_NAME, "AUTExtension.Composite"); //$NON-NLS-1$
        GridLayout compositeLayout = new GridLayout (1, false);
        composite.setLayout (compositeLayout);
        GridData data = new GridData ();
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = false;
        data.grabExcessVerticalSpace = false;
        composite.setLayoutData (data);
        
        Group bundleNameGroup = new Group(composite, SWT.NONE);
        bundleNameGroup.setText("Bundle name"); //$NON-NLS-1$
        bundleNameGroup.setData(WIDGET_NAME, "AUTExtension.NameGroup"); //$NON-NLS-1$
        Label bundleName = new Label(bundleNameGroup, SWT.NONE);
        bundleName.setText(Activator.getActivator().getBundle()
                .getSymbolicName());
        bundleName.setLocation(20, 20);
        bundleName.pack();
        
        Group bundleVersionGroup = new Group(composite, SWT.NONE);
        bundleVersionGroup.setText("Bundle version"); //$NON-NLS-1$
        bundleNameGroup.setData(WIDGET_NAME, "AUTExtension.VersionGroup"); //$NON-NLS-1$
        Label bundleVersion = new Label(bundleVersionGroup, SWT.NONE);
        bundleVersion.setText(Activator.getActivator().getBundle()
                .getVersion().toString());
        bundleVersion.setLocation(20, 20);
        bundleVersion.pack();
        
    }

    @Override
    public void setFocus() {
        // Nothing
    }
}