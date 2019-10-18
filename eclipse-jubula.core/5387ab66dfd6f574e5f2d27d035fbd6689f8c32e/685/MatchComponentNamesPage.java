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
package org.eclipse.jubula.client.ui.rcp.wizards.refactor.pages;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jubula.client.core.businessprocess.CalcTypes;
import org.eclipse.jubula.client.core.businessprocess.CompNamesBP;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.jubula.client.ui.rcp.editors.TestSuiteEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.labelprovider.GeneralLabelProvider;
import org.eclipse.jubula.client.ui.rcp.widgets.ComponentNamesTableComposite;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

/**
 * @author Markus Tiede
 * @created Jul 25, 2011
 */
public class MatchComponentNamesPage extends WizardPage {
    /**
     * <code>m_editor</code> the currently active editor
     */
    private final AbstractJBEditor m_editor;
    /**
     * <code>cntc</code> the new execs component names table composite
     */
    private ComponentNamesTableComposite m_cntc;
    /**
     * <code>m_execTCList</code>
     */
    private final List<INodePO> m_execTCList;
    
    /**
     * <code>m_parents</code> mapping
     */
    private Map<ICompNamesPairPO, IExecTestCasePO> m_parents = 
        new HashMap<ICompNamesPairPO, IExecTestCasePO>();
    
    /**
     * @author Markus Tiede
     * @created Aug 5, 2011
     */
    public class MatchCompNamesPageTreeContentProvider implements
            ITreeContentProvider {
        /**
         * <code>m_cnBP</code>
         */
        private CompNamesBP m_cnBP = new CompNamesBP();
        
        /** {@inheritDoc} */
        public void dispose() {
            m_parents.clear();
        }

        /** {@inheritDoc} */
        public void inputChanged(Viewer viewer, Object oldInput, 
            Object newInput) {
            m_parents.clear();
        }

        /** {@inheritDoc} */
        public Object[] getElements(Object inputElement) {
            return ((List)inputElement).toArray();
        }

        /** {@inheritDoc} */
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof IExecTestCasePO) {
                IExecTestCasePO execTC = (IExecTestCasePO)parentElement;
                Collection<ICompNamesPairPO> compPairs = 
                    m_cnBP.getAllCompNamesPairs(execTC);
                if (compPairs.size() > 0) {
                    for (ICompNamesPairPO pair : compPairs) {
                        m_parents.put(pair, execTC);
                    }
                    return compPairs.toArray();
                }
                return new String[] { Messages.NoComponentNames };
            }
            return ArrayUtils.EMPTY_OBJECT_ARRAY;
        }

        /** {@inheritDoc} */
        public Object getParent(Object element) {
            return null;
        }

        /** {@inheritDoc} */
        public boolean hasChildren(Object element) {
            return getChildren(element).length > 0;
        }
    }
    
    /**
     * @author Markus Tiede
     * @created Aug 5, 2011
     */
    public class MatchCompNamesPageTreeLabelProvider 
        extends GeneralLabelProvider {
        /** {@inheritDoc} */
        public String getText(Object element) {
            if (element instanceof ICompNamesPairPO) {
                ICompNamesPairPO pair = (ICompNamesPairPO)element;
                EditSupport supp = m_editor.getEditorHelper().getEditSupport();
                IWritableComponentNameCache cache = m_editor.getCompNameCache();
                StringBuilder sb = new StringBuilder(cache.getNameByGuid(
                        pair.getSecondName()));
                if (StringUtils.isEmpty(pair.getType())) {
                    CalcTypes.recalculateCompNamePairs(cache,
                            (INodePO) supp.getWorkVersion());
                }
                sb.append(StringConstants.SPACE)
                        .append(StringConstants.LEFT_BRACKET)
                        .append(CompSystemI18n.getString(pair.getType()))
                        .append(StringConstants.RIGHT_BRACKET);
                return sb.toString();
            }
            return super.getText(element);
        }
        
        /** {@inheritDoc} */
        public Image getImage(Object element) {
            if (element instanceof ICompNamesPairPO) {
                if (((ICompNamesPairPO)element).isPropagated()) {
                    return IconConstants.PROPAGATED_LOGICAL_NAME_IMAGE;
                }
                return IconConstants.LOGICAL_NAME_IMAGE;
            }
            return super.getImage(element);
        }
    }

    /**
     * @param pageName
     *            the page name
     * @param editor
     *            the current editor
     * @param execTCList
     *            the node list to extract the component interface for
     */
    public MatchComponentNamesPage(String pageName, AbstractJBEditor editor, 
        List<INodePO> execTCList) {
        super(pageName, Messages.ReplaceTCRWizard_matchComponentNames_title,
                null);
        m_editor = editor;
        m_execTCList = execTCList;
    }

    /** {@inheritDoc} */
    public void createControl(Composite parent) {
        SashForm sash = new SashForm(parent, SWT.HORIZONTAL);
        sash.setLayout(new FillLayout(SWT.VERTICAL | SWT.HORIZONTAL));
        
        Composite leftSashContent = new Composite(sash, SWT.NONE);
        leftSashContent.setLayout(GridLayoutFactory.fillDefaults().create());
        new Label(leftSashContent, SWT.NONE)
           .setText(Messages.ReplaceTCRWizard_matchComponentNames_oldInterface);
        TreeViewer tv = new TreeViewer(leftSashContent);
        tv.setContentProvider(new MatchCompNamesPageTreeContentProvider());
        tv.setLabelProvider(new MatchCompNamesPageTreeLabelProvider());
        tv.setInput(m_execTCList);
        tv.getTree().setLayoutData(
                GridDataFactory.fillDefaults().grab(true, true).create());
        tv.expandAll();

        Composite rightSashContent = new Composite(sash, SWT.NONE);
        rightSashContent.setLayout(GridLayoutFactory.fillDefaults().create());
        new Label(rightSashContent, SWT.NONE)
           .setText(Messages.ReplaceTCRWizard_matchComponentNames_newInterface);
        m_cntc = new ComponentNamesTableComposite(rightSashContent, SWT.NONE);
        m_cntc.setSelectedExecNodeOwner(m_editor);
        if (m_editor instanceof TestSuiteEditor) {
            m_cntc.controlPropagation(false);
        }
        sash.setWeights(new int[] { 1, 2 });
        setControl(sash);
    }

    /**
     * @param replacement the replacing exec node
     */
    public void setSelectedExecNode(IExecTestCasePO replacement) {
        m_cntc.setSelectedExecNode(replacement);
    }

    /** {@inheritDoc} */
    public void performHelp() {
        PlatformUI.getWorkbench().getHelpSystem().displayHelp(
            ContextHelpIds.REFACTOR_REPLACE_MATCH_COMP_NAMES_WIZARD_PAGE);
    }
}
