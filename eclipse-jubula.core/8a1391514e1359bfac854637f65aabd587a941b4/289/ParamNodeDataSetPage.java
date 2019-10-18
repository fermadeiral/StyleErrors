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
package org.eclipse.jubula.client.ui.rcp.views.dataset;

import java.util.List;

import org.eclipse.jubula.client.core.businessprocess.TestCaseParamBP;
import org.eclipse.jubula.client.core.businessprocess.compcheck.CompletenessGuard;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.editors.NodeEditorInput;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;


/**
 * @author BREDEX GmbH
 * @created Jul 12, 2010
 */
public class ParamNodeDataSetPage extends AbstractDataSetPage {

    /** Constructor */
    public ParamNodeDataSetPage() {
        super(new TestCaseParamBP());
    }

    /**
     * @param paramObj
     *            IParamNodePO
     * @return the param node if the param interface obj is instanceof
     *         IParamNodePO; otherwise throws exception
     */
    private IParamNodePO getParamNodePO(IParameterInterfacePO paramObj) {
        if (paramObj instanceof IParamNodePO) {
            IParamNodePO paramNode = ((IParamNodePO)paramObj);
            return paramNode;
        }
        Assert.notReached();
        return null;
    }
    
    /** {@inheritDoc} */
    protected boolean isNodeValid(IParameterInterfacePO paramObj) {
        return paramObj instanceof IParamNodePO
                && getParamNodePO(paramObj).isValid();
    }

    /**
     *
     * {@inheritDoc} 
     */    
    protected void setIsEntrySetComplete(IParameterInterfacePO paramNode) {
        IParamNodePO node = getParamNodePO(paramNode);
        CompletenessGuard.setCompletenessTestData(node, 
                node.isTestDataComplete());
    }

    /** {@inheritDoc} */
    protected boolean isEditorOpenOrIsPageTestDataCube(
            IParameterInterfacePO paramObj) {
        if (paramObj != null) {
            Object inputNode = paramObj;
            if (paramObj instanceof ICapPO 
                    || paramObj instanceof IExecTestCasePO) {
                inputNode = getParamNodePO(paramObj).getSpecAncestor();
            }
            List<IEditorReference> editors = Plugin.getAllEditors();
            for (IEditorReference reference : editors) {
                try {
                    if (reference.getEditorInput() instanceof NodeEditorInput) {
                        INodePO editorInputNode = 
                            ((NodeEditorInput)reference.getEditorInput())
                            .getNode();
                        if (editorInputNode != null 
                                && editorInputNode.equals(inputNode)) {
                            return true;
                        }
                    }
                } catch (PartInitException e) {
                    // should not happpen. If it happens, it does not matter here.
                }
            }
        }
        return false;
    }

}
