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
package org.eclipse.jubula.client.ui.rcp.wizards.refactor.param;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;

/**
 * The content provider for the parameter names used by the TreeViewer.
 * @author BREDEX GmbH
 */
public class ParameterDescriptionContentProvider
        implements ITreeContentProvider {

    /**
     * The parameter names passed to this content provider in the method
     * {@link #getElements(Object)} by
     * {@link org.eclipse.jface.viewers.TreeViewer#setInput(Object)}.
     */
    private ExistingAndNewParameterData m_paramData;

    /**
     * @return The array of parameter descriptions to show in the tree directly
     *         under the root node.
     * {@inheritDoc}
     */
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof ExistingAndNewParameterData) {
            m_paramData = (ExistingAndNewParameterData) inputElement;
            return m_paramData.getAllParamDescriptions();
        }
        return null;
    }

    /**
     * @return True, if the element is a parameter description, otherwise false.
     * {@inheritDoc}
     */
    public boolean hasChildren(Object element) {
        return element instanceof IParamDescriptionPO;
    }

    /**
     * @return The array of execution Test Cases corresponding to the given
     *         parameter description, if the element is a parameter description.
     *         Otherwise returns false.
     * {@inheritDoc}
     */
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof IParamDescriptionPO) {
            if (m_paramData != null) {
                IParamDescriptionPO paramDesc =
                        (IParamDescriptionPO) parentElement;
                return m_paramData.getTestCasesOfParamDescription(paramDesc);
            }
        }
        return null;
    }

    /**
     * @return null, because, the execution Test Cases do not know about their parents.
     * {@inheritDoc}
     */
    public Object getParent(Object element) {
        return null;
    }

    /**
     * Do nothing.
     * {@inheritDoc}
     */
    public void dispose() {
        // do nothing
    }

    /**
     * Do nothing.
     * {@inheritDoc}
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // do nothing
    }

}
