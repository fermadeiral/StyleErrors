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
package org.eclipse.jubula.client.ui.rcp.wizards.search.refactor;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.wizards.refactor.pages.ChooseTestCasePage;
import org.eclipse.jubula.client.ui.rcp.wizards.search.refactor.pages.ComponentNameMappingWizardPage;
import org.eclipse.jubula.client.ui.rcp.wizards.search.refactor.pages.FillParameterValuesWizardPage;
import org.eclipse.jubula.client.ui.rcp.wizards.search.refactor.pages.ParameterNamesMatchingWizardPage;
import org.eclipse.jubula.client.ui.rcp.wizards.search.refactor.pages.ReplaceExecTestCaseData;
import org.eclipse.ui.PlatformUI;

/**
 * This wizard is used for replacing Test Cases from items in a search result
 * 
 * @author BREDEX GmbH
 * 
 */
public class SearchReplaceTCRWizard extends Wizard {
    /** ID for the "Choose" page */
    private static final String CHOOSE_PAGE_ID = "ReplaceTCRWizard.ChoosePageId"; //$NON-NLS-1$
    /** ID for the "Component Mapping" page */
    private static final String COMPONENT_MAPPING_PAGE_ID = "ReplaceTCRWizard.ComponentMappingPageId"; //$NON-NLS-1$
    /** ID for the "Parameter Matching" page */
    private static final String PARAMETER_MATCHING_PAGE_ID = "ReplaceTCRWizard.ParameterMatchingPageId"; //$NON-NLS-1$
    /** ID for the Parameter Filling page */
    private static final String PARAMETER_FILLING_PAGE_ID = "ReplaceTCRWizard.ParameterFillingPageId"; //$NON-NLS-1$

    /**
     * <code>m_setOfExecsToReplace</code>
     */
    private final ReplaceExecTestCaseData m_replaceExecTestCaseData;

    /**
     * Map for matching guid's for component names 
     */
    private Map<String, String> m_matchedCompNameGuidMap;
    
    /**
     * <code>m_choosePage</code>
     */
    private ChooseTestCasePage m_choosePage;

    /**
     * Component Names matching page ID
     */
    private ComponentNameMappingWizardPage m_componentNamesPage;

    /**
     * Constructor for the wizard page
     * 
     * @param execsToReplace
     *            set of ExecTC in which the SpecTC and other information should
     *            be changed
     */
    public SearchReplaceTCRWizard(Set<IExecTestCasePO> execsToReplace) {
        m_replaceExecTestCaseData = new ReplaceExecTestCaseData(execsToReplace);
        setWindowTitle(Messages.ReplaceTCRWizardTitle);
    }

    /** {@inheritDoc} */
    public boolean performFinish() {
        // This is needed if Finish was pressed on the first page
        m_matchedCompNameGuidMap = m_componentNamesPage.getCompMatching();
        try {
            PlatformUI.getWorkbench().getProgressService()
                .run(true, false, new ReplaceTestCaseTransaction(
                    m_replaceExecTestCaseData, m_matchedCompNameGuidMap));
        } catch (InvocationTargetException e) {
            // Already handled;
        } catch (InterruptedException e) {
            // Already handled
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void addPages() {
        super.addPages();
        Set<INodePO> specSet = new HashSet<INodePO>();
        for (IExecTestCasePO exec : m_replaceExecTestCaseData
                .getOldExecTestCases()) {
            if (ISpecTestCasePO.class.isAssignableFrom(exec.getParentNode()
                    .getClass())) {
                specSet.add(exec.getParentNode());
            }
        }    
        m_choosePage = new ChooseTestCasePage(specSet, CHOOSE_PAGE_ID);
        m_choosePage.setDescription(
                Messages.ReplaceTCRWizard_choosePage_multi_description);
        m_choosePage.setContextHelpId(ContextHelpIds
                .SEARCH_REFACTOR_REPLACE_EXECTC_WIZARD);
        m_componentNamesPage = new ComponentNameMappingWizardPage(
                COMPONENT_MAPPING_PAGE_ID,
                m_replaceExecTestCaseData.getOldExecTestCases());
        m_componentNamesPage.setDescription(Messages
                .ReplaceTCRWizard_matchComponentNames_multi_description);
        addPage(m_choosePage);
        addPage(m_componentNamesPage);
        addPage(new ParameterNamesMatchingWizardPage(
                PARAMETER_MATCHING_PAGE_ID, m_replaceExecTestCaseData));
        addPage(new FillParameterValuesWizardPage(
                PARAMETER_FILLING_PAGE_ID, m_replaceExecTestCaseData));
    }

    /**
     * {@inheritDoc}
     */
    public IWizardPage getNextPage(IWizardPage page) {
        if (page instanceof ChooseTestCasePage) {
            m_replaceExecTestCaseData.setNewSpecTestCase(
                    m_choosePage.getChoosenTestCase());
            // FIXME RB: wizard pages should update data it self
            m_componentNamesPage.setNewSpec(
                    m_replaceExecTestCaseData.getNewSpecTestCase());
        }
        IWizardPage nextPage = super.getNextPage(page);
        return nextPage;
    }

}
