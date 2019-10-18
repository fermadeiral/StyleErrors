/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.wizards.search.refactor.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jubula.client.core.businessprocess.AbstractParamInterfaceBP;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.controllers.propertydescriptors.ParamTextPropertyDescriptor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.labelprovider.GeneralLabelProvider;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedParamText;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedParamTextContentAssisted;
import org.eclipse.jubula.client.ui.rcp.widgets.ParamProposalProvider;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ParamValueSet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Page for entering values to unmatched parameters
 * @author BREDEX GmbH
 */
public class FillParameterValuesWizardPage extends AbstractMatchSelectionPage
        implements ModifyListener {

    /** The data for replacing execution Test Cases. */
    private final ReplaceExecTestCaseData m_data;
    
    /** The text input fields for the values */
    private Map<IParamDescriptionPO, CheckedParamText> m_valueTexts =
            new HashMap<>();

    /** The labels for the values */
    private Map<IParamDescriptionPO, Label> m_labels = new HashMap<>();
    
    /** Initialization flag */
    private boolean m_needInit = true;
    
    /**
     * @param pageName
     *            The name of the page.
     * @param replaceExecTestCasesData The data for replacing execution Test Cases.
     */
    public FillParameterValuesWizardPage(String pageName,
            ReplaceExecTestCaseData replaceExecTestCasesData) {
        super(pageName, Messages.ReplaceTCRWizard_fillParameters_title,
                null, ContextHelpIds.SEARCH_REFACTOR_REPLACE_EXECTC_WIZARD);
        m_data = replaceExecTestCasesData;
        setDescription(Messages
                .ReplaceTCRWizard_fillParameters_multi_description);
    }
    
    /**
     * Creates the selection table with new unmatched parameter names in the left column and text fields in the right
     */
    @Override
    protected void createSelectionTable(Composite parent) {
        if (m_needInit) {
            // first call, so we create the header for the table
            createHeadLabel(parent, Messages
                    .ReplaceTCRWizard_fillParameters_newParameter);
            createHeadLabel(parent, Messages
                    .ReplaceTCRWizard_fillParameters_value);
            m_needInit = false;
        }
        // we remove no longer relevant (e.g by changing Test Case or remapping Parameters) text fields
        List<IParamDescriptionPO> paramDescList = m_data
                .getNewSpecTestCase().getParameterList();
        Map<IParamDescriptionPO, IParamDescriptionPO> newOld =
                m_data.getNewOldParamMap();
        for (Iterator<IParamDescriptionPO> it = m_valueTexts.keySet().
                iterator(); it.hasNext(); ) {
            IParamDescriptionPO desc = it.next();
            if (!newOld.containsKey(desc) || newOld.get(desc) != null) {
                // Param Desc is not in the current Test Case or it is mapped to an old Param
                m_valueTexts.get(desc).removeModifyListener(this);
                m_valueTexts.get(desc).dispose();
                it.remove();
                m_labels.get(desc).dispose();
                m_labels.remove(desc);
            }
        }
        
        for (IParamDescriptionPO paramDesc: newOld.keySet()) {
            if (newOld.get(paramDesc) != null
                    || m_labels.containsKey(paramDesc)) {
                continue;
            }
            Label label = new Label(parent, SWT.NONE);
            label.setText(GeneralLabelProvider.getTextWithType(paramDesc));
            m_labels.put(paramDesc, label);
            ParamValueSet valSet = ParamTextPropertyDescriptor.getValuesSet(
                    m_data.getNewSpecTestCase(), paramDesc.getUniqueId());
            String[] values = ParamTextPropertyDescriptor.getValues(valSet);
            CheckedParamTextContentAssisted fieldEditor =
                new CheckedParamTextContentAssisted(parent,
                    SWT.NONE, null, paramDesc,
                    AbstractParamInterfaceBP.createParamValueValidator(
                    paramDesc.getType(), false, values),
                            new ParamProposalProvider(
                                    ParamTextPropertyDescriptor
                                            .getValuesWithComment(valSet),
                                    null, paramDesc));
            fieldEditor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            fieldEditor.addModifyListener(this);
            m_valueTexts.put(paramDesc, fieldEditor);
        }
        m_data.setUnmatchedValuesMap(m_valueTexts);
        updateAdditionalInformation();
    }

    /** {@inheritDoc} */
    public boolean isPageComplete() {
        return isCurrentPage();
    }
    
    /** Updates the additional information */
    private void updateAdditionalInformation() {
        List<String> messages = new ArrayList<>();
        boolean problem = false;
        for (CheckedParamText text : m_valueTexts.values()) {
            if (!text.isValid()) {
                problem = true;
            }
        }
        if (problem) {
            messages.add(Messages.ReplaceTCRWizard_fillParameters_invalidData);
        }
        setAdditionalInformation(messages);
    }

    /** {@inheritDoc} */
    public void modifyText(ModifyEvent e) {
        m_data.setUnmatchedValuesMap(m_valueTexts);
        updateAdditionalInformation();
    }
    
}
