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
package org.eclipse.jubula.client.core.businessprocess;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.model.TDCell;
import org.eclipse.jubula.client.core.utils.ModelParamValueConverter;
import org.eclipse.jubula.client.core.utils.RefToken;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.TestDataConstants;


/**
 * A utility class to support tree operations
 * 
 * @author BREDEX GmbH
 * @created 09.09.2005
 */
public class TreeOpsBP {

    /**
     * Hidden default constructor
     */
    private TreeOpsBP() {
        super();
    }

    /**
     * Extracts a given List of nodes from a node to a new TestCase and
     *       inserts the new created/extracted TestCase into the owner node as an ExecTestCase.
     * The new Test Case is not put into the child node list of the SpecObjContPO!
     * @param newTcName The name of the new SpecTestCase
     * @param ownerNode the edited node from which to extract
     * @param modNodes the node to be extracted
     * @param s the database session
     * @param mapper mapper to resolve param names
     * @return an ExecTestCasePO, the location of use of the extracted TestCase
     * @throws TreeOpFailedException if the operation failed
     */
    public static IExecTestCasePO extractTestCase(String newTcName,
        INodePO ownerNode, List<INodePO> modNodes, EntityManager s, 
        ParamNameBPDecorator mapper) {

        final boolean isOwnerSpecTestCase = 
            ownerNode instanceof ISpecTestCasePO;
        INodePO oldParent = modNodes.get(0).getParentNode();
        ISpecTestCasePO newTc = NodeMaker.createSpecTestCasePO(newTcName);
        newTc.setParentProjectId(oldParent.getParentProjectId());
        s.persist(newTc); // to get an id for newTc
        
        int pos = -1;
        Map<String, String> oldToNewParamGuids = new HashMap<String, String>();
        for (INodePO moveNode : modNodes) {
            if (isOwnerSpecTestCase && moveNode instanceof IParamNodePO) {
                addParamsToParent(newTc, (IParamNodePO)moveNode, mapper,
                        (ISpecTestCasePO)ownerNode, oldToNewParamGuids);
            }
            pos = oldParent.indexOf(moveNode);
            oldParent.removeNode(moveNode);
            newTc.addNode(moveNode);
        }
        IExecTestCasePO newExec = NodeMaker.createExecTestCasePO(newTc);
        newExec.setSpecTestCase(newTc);
        if (isOwnerSpecTestCase) {
            propagateParams(newExec, (IParamNodePO)ownerNode);
        }
        propagateCompNames(modNodes, newExec);
        oldParent.addNode(pos, newExec);
        s.persist(newExec);
        ownerNode.addTrackedChange("modified", true); //$NON-NLS-1$
        return newExec;
    }
    
    /**
     * Adds all parameter references of any language of <code>child</code> to the
     * <code>parent</code> by adding new parameter descriptions to the
     * <code>parent</code>. As the result of this, the parent
     * will contain all references of any language of the child.
     * 
     * @param parent
     *            The parent node
     * @param child
     *            The child node
     * @param mapper mapper to resolve param names
     * @param ownerNode the edited node from which to extract
     * @param oldToNewUuids mapping between old and new paramter GUIDs
     */
    private static void addParamsToParent(
            ISpecTestCasePO parent, IParamNodePO child, 
            IParamNameMapper mapper, ISpecTestCasePO ownerNode, 
            Map<String, String> oldToNewUuids) {
        
        TDCell cell = null;
        for (Iterator<TDCell> it = child.getParamReferencesIterator(); 
                it.hasNext();) {
            cell = it.next();
            String guid = child.getDataManager().getUniqueIds().get(
                cell.getCol());
            IParamDescriptionPO childDesc = 
                child.getParameterForUniqueId(guid);
            // The childDesc can be null if the parameter has been removed
            // in another session and not yet updated in the current
            // editor session.
            if (childDesc != null) {
                ModelParamValueConverter conv = 
                    new ModelParamValueConverter(
                        cell.getTestData(), child, childDesc);
                List<RefToken> refTokens = conv.getRefTokens();
                for (RefToken refToken : refTokens) {
                    String uiString = RefToken.extractCore(
                            refToken.getGuiString());
                    IParamDescriptionPO parentParamDescr = parent
                            .addParameter(childDesc.getType(), uiString,
                                    false, mapper);
                    // get old GUID from owner node
                    List<IParamDescriptionPO> ownerDescs = 
                        ownerNode.getParameterList();
                    String oldUuid = StringConstants.EMPTY;
                    for (IParamDescriptionPO ownerDesc : ownerDescs) {
                        if (ownerDesc.getName().equals(uiString)) {
                            oldUuid = ownerDesc.getUniqueId();
                            break;
                        }
                    }
                    if (parentParamDescr != null) {
                        String newUuid = parentParamDescr.getUniqueId();
                        oldToNewUuids.put(oldUuid, newUuid);
                    }
                    
                }
                // update test data of child with UUID for reference
                conv.replaceUuidsInReferences(oldToNewUuids);
                cell.setTestData(conv.getModelString());
            }
        }
    }
    
    
    
    
    /**
     * @param modNodes the extracted nodes.
     * @param newExec the new ExecTestCasePO.
     */
    private static void propagateCompNames(List<INodePO> modNodes, 
        IExecTestCasePO newExec) {
        
        for (INodePO modNode : modNodes) {
            if (modNode instanceof IExecTestCasePO) {
                final IExecTestCasePO execTc = (IExecTestCasePO)modNode;
                for (ICompNamesPairPO pair : execTc.getCompNamesPairs()) {
                    if (pair.isPropagated()) {
                        // use secondName twice here! The new newExec only 
                        // delegates the second name.
                        final String secondName = pair.getSecondName();
                        final ICompNamesPairPO newPairPO = PoMaker
                            .createCompNamesPairPO(secondName, secondName,
                                pair.getType());
                        newPairPO.setPropagated(true);
                        newExec.addCompNamesPair(newPairPO);
                    }
                }
            }
        }
    }

    /**
     * 
     * Propagates the parameters of the given IExecTestCasePO.
     * @param execTc the IExecTestCasePO
     * @param ownerNode the edited node from which to extract
     */
    private static void propagateParams(IExecTestCasePO execTc, 
        IParamNodePO ownerNode) {
        
        execTc.resolveTDReference();
        final List<IParamDescriptionPO> parameterList = execTc
            .getParameterList();
        final List<IParamDescriptionPO> ownerParamList = ownerNode
            .getParameterList();
        for (IParamDescriptionPO descr : parameterList) {
            StringBuilder builder = new StringBuilder();
            final String paramName = descr.getName();
            for (IParamDescriptionPO ownerDesc : ownerParamList) {
                if (ownerDesc.getName().equals(paramName)) {
                    builder.append(ownerDesc.getUniqueId());
                    break;
                }
            }            
            String value = 
                TestDataConstants.REFERENCE_CHAR_DEFAULT + builder.toString();
            execTc.getDataManager().updateCell(
                    value, 0, descr.getUniqueId());
        }
        
    }

    /**
     * Checks if the given selected node exsists in the given owner node
     * (comparing with equals()).
     * 
     * @param ownerNode
     *            the owner node to search in.
     * @param selectecNode
     *            the node to check
     * @return the selected node if the given SpecTestCase contains it, null
     *         otherwise.
     */
    private static INodePO findNode(INodePO ownerNode,
        INodePO selectecNode) {
        Iterator childIt = ownerNode.getNodeListIterator();
        while (childIt.hasNext()) {
            INodePO child = (INodePO)childIt.next();
            if (child.getId().equals(selectecNode.getId())) {
                return child;
            }
        }
        return null;
    }

}
