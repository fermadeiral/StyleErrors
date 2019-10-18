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
package org.eclipse.jubula.client.ui.rcp.validator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jubula.client.core.businessprocess.TestDataCubeBP;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedText.IValidator;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedText.ValidationState;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Text;


/**
 * Validates text against Test Data Cube names.
 *
 * @author BREDEX GmbH
 * @created Jul 20, 2010
 */
public class TestDataCubeReferenceValidator implements IValidator {

    /** 
     * The Project containing the Test Data Cubes against which
     * validation will be performed 
     */
    private IProjectPO m_project;

    /**
     * Constructor
     * 
     * @param project The Project containing the Test Data Cubes against which
     *                validation will be performed.
     */
    public TestDataCubeReferenceValidator(IProjectPO project) {
        Validate.notNull(project);
        m_project = project;
    }
    
    /**
     * {@inheritDoc}
     */
    public ValidationState validateInput(VerifyEvent e) {
        Text txt = (Text)e.widget;
        StringBuilder workValue = 
            new StringBuilder(txt.getText());
        workValue.replace(e.start, e.end, e.text);
        String newValue = workValue.toString();
        boolean mightMatch = false;
        for (IParameterInterfacePO dataCube 
                : TestDataCubeBP.getAllTestDataCubesFor(m_project)) {
            if (StringUtils.equals(e.text, dataCube.getName())) {
                return ValidationState.OK;
            }
            if (!mightMatch 
                    && StringUtils.startsWith(dataCube.getName(), newValue)) {
                mightMatch = true;
            }
        }

        if (mightMatch) {
            return ValidationState.MightMatchAccept;
        }
        
        return ValidationState.MightMatchReject;
    }

}
