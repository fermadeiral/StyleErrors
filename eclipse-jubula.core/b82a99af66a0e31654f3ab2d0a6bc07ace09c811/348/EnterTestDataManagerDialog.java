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
package org.eclipse.jubula.client.ui.rcp.dialogs;

import java.util.Set;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jubula.client.ui.dialogs.AbstractValidatedDialog;
import org.eclipse.jubula.client.ui.rcp.databinding.validators.TestDataManagerNameValidator;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;



/**
 * @author BREDEX GmbH
 * @created Jun 29, 2010
 */
public abstract class EnterTestDataManagerDialog 
    extends AbstractValidatedDialog {
    /** observable (bindable) value for component name */
    private WritableValue m_name;
    
    /** initial value for name, null if none */
    private String m_initialName;
    
    /** the already used cube names */
    private Set<String> m_alreadyUsedNames;
    
    /**
     * Constructor
     * 
     * @param parentShell
     *            the parent SWT shell
     * @param initialName if set used to initialize the name field
     * @param usedNames a set of already used names
     */
    public EnterTestDataManagerDialog(Shell parentShell, String initialName, 
            Set<String> usedNames) {
        super(parentShell);
        m_initialName = initialName;
        m_alreadyUsedNames = usedNames;
    }
    
    /**
     * Constructor
     * 
     * @param parentShell
     *            the parent SWT shell
     * @param usedNames a set of already used names
     */
    public EnterTestDataManagerDialog(Shell parentShell, 
            Set<String> usedNames) {
        this(parentShell, null, usedNames);
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        Composite area = new Composite(parent, SWT.BORDER);
        area.setLayoutData(gridData);
        area.setLayout(new GridLayout(2, false));

        Text testDataCubeNameField = createTestDataManagerName(area);
        gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        LayoutUtil.addToolTipAndMaxWidth(gridData, testDataCubeNameField);
        testDataCubeNameField.setLayoutData(gridData);
        LayoutUtil.setMaxChar(testDataCubeNameField);
        
        IObservableValue nameFieldText = 
            WidgetProperties.text(SWT.Modify).observe(testDataCubeNameField);
        m_name = WritableValue.withValueType(String.class);
        
        getValidationContext().bindValue(
                nameFieldText,
                m_name,
                new UpdateValueStrategy().setAfterGetValidator(
                        new TestDataManagerNameValidator(m_initialName,
                                m_alreadyUsedNames)),
                new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
        
        if (m_initialName != null) {
            setName(m_initialName);
        }
        testDataCubeNameField.selectAll();

        return area;
    }

    /**
     * Creates the label and text field for the test data manager name.
     * 
     * @param area The parent for the created widgets.
     * @return the created text field.
     */
    private Text createTestDataManagerName(Composite area) {
        new Label(area, SWT.NONE).setText(
                Messages.EnterTestDataCubeDialogTestDataCubeNameLabel);
        Text testDataCubeNameField = 
            new Text(area, SWT.SINGLE | SWT.BORDER);
        return testDataCubeNameField;
    }

    /**
     * This method must be called from the GUI thread.
     * 
     * @return the name of the logical component name being created.
     */
    public String getName() {
        return (String)m_name.getValue();
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        m_name.setValue(name);
    }
    
}
