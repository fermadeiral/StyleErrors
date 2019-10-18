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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IParamValueSetPO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITDManager;
import org.eclipse.jubula.client.core.model.ITcParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IValueCommentPO;
import org.eclipse.jubula.client.core.model.TDCell;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.utils.GuiParamValueConverter;
import org.eclipse.jubula.client.core.utils.ModelParamValueConverter;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Action;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Param;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ValueSetElement;


/**
 * This is the business process for all parameter value operations on
 * a parameter node like test steps, specification and execution test cases.
 * The BP performs all required operations that result in the removing
 * or adding of parameter references, e.g. <code>=FOO</code>. This includes
 * checks to ensure that the operations are valid.
 * 
 * {@inheritDoc}
 *
 * @author BREDEX GmbH
 * @created 22.08.2005
 */
public class TestCaseParamBP extends AbstractParamInterfaceBP<ISpecTestCasePO> {

    /** {@inheritDoc} */
    protected void updateParam(GuiParamValueConverter conv, 
        IParamNameMapper mapper, int row) {
        
        checkRemoveExternalDataFile(conv.getCurrentNode());
        if (conv.getCurrentNode() instanceof IExecTestCasePO) {
            IExecTestCasePO exTc = (IExecTestCasePO)conv.getCurrentNode();
            exTc.resolveTDReference();
        }
        INodePO parent = NodePM.getSpecTestCaseParent(
                (INodePO)conv.getCurrentNode());
        if (parent instanceof ISpecTestCasePO) {
            ISpecTestCasePO parentSpecTc = (ISpecTestCasePO)parent;
            for (String refName : conv.getParametersToAdd(parentSpecTc)) {
                addParameter(refName, 
                    conv.getDesc().getType(), parentSpecTc, mapper);
            }
        }
        writeTestDataEntry(conv, row);
    }
    
    /**
     * finds all Params where a parameter is actually used. Since this is
     * used for type checking only there is only one representative for a
     * possible group of uses stored in the set.
     * @param node node to check
     * @param paramGUID the parameter to check
     * @return a possibly empty set of usage points. 
     */
    public static Set<Param> getValuesForParameter(IParamNodePO node,
            String paramGUID) {
        Set<Param> result = new HashSet<Param>();
        if (paramGUID != null) { // can happen when a dataset is deleted
            getValuesForParameterImp(node, paramGUID, true, result);
        }
        return result;
    }
    
    /**
     * finds all Params where a parameter is actually used. Since this is
     * used for type checking only there is only one representative for a
     * possible group of uses stored in the set.
     * @param node node to check
     * @param paramGUID the parameter to check
     * @param isFirstCall true if called for the first time, false on all 
     * recursive calls
     * @param result storage for results, owned by caller
     */
    private static void getValuesForParameterImp(IParamNodePO node,
            String paramGUID, boolean isFirstCall, Set<Param> result) {
        if (node instanceof ISpecTestCasePO) {
            ISpecTestCasePO spec = (ISpecTestCasePO) node;
            boolean found = getParameterValueSet(paramGUID, result, spec);
            if (found) {
                return;
            }
            Iterator<INodePO> it = node.getNodeListIterator();
            while (it.hasNext()) {
                INodePO next = it.next();
                if (next instanceof IParamNodePO) {
                    IParamNodePO n = (IParamNodePO) next;
                    getValuesForParameterImp(n, paramGUID, 
                            false, result);
                }
            }            
        } else if (node instanceof IExecTestCasePO) {
            IExecTestCasePO execTC = (IExecTestCasePO)node;
            if (execTC.getSpecTestCase() != null) {
                if (isFirstCall) {
                    getValuesForParameterImp(execTC.getSpecTestCase(),
                            paramGUID, false, result);
                } else {
                    Set<String> subst = findSubstitutes(execTC, paramGUID);
                    for (String p : subst) {
                        getValuesForParameterImp(execTC.getSpecTestCase(), p,
                                false, result);
                    }
                }
            }
        } else if (node instanceof ICapPO) {
            ICapPO cap = (ICapPO) node;
            final CompSystem compSystem =
                    ComponentBuilder.getInstance().getCompSystem();
            for (IParamDescriptionPO pd : cap.getParameterList()) {
                try {
                    String pID = cap.getDataManager().getCell(0, pd);
                    if ((pID != null) // check for unset data
                            && pID.endsWith(paramGUID)) {
                        Component c = compSystem
                                .findComponent(cap.getComponentType());
                        Action a = c.findAction(cap.getActionName());
                        Param findParam = a.findParam(pd.getUniqueId());
                        for (Iterator<ValueSetElement> iterator =
                                findParam.getValueSet().iterator(); iterator
                                        .hasNext();) {
                            ValueSetElement param = iterator.next();
                        }
                        result.add(findParam);
                    }
                } catch (IndexOutOfBoundsException e) {
                    // Cell for the given row and column does not exist
                    // Do nothing
                }
            }
        }
    }

    /**
     * 
     * @param paramGUID the parameter to check
     * @param result result storage for results, owned by caller
     * @param spec the {@link ISpecTestCasePO}
     * @return if there has a result been found <code>true</code>
     */
    private static boolean getParameterValueSet(String paramGUID,
            Set<Param> result, ISpecTestCasePO spec) {
        boolean found = false;
        for (Iterator<IParamDescriptionPO> iterator =
                spec.getParameterListIter(); iterator.hasNext();) {
            IParamDescriptionPO param = iterator.next();
            if (param.getUniqueId().equals(paramGUID)) {
                if (param instanceof ITcParamDescriptionPO) {
                    IParamValueSetPO valueSet =
                            ((ITcParamDescriptionPO) param).getValueSet();
                    if ((valueSet != null)
                            && valueSet.getValues().size() > 0) {
                        Param param2 = new Param(param.getName(),
                                param.getType(),
                                valueSet.getValues().stream()
                                        .collect(Collectors.toMap(
                                                IValueCommentPO::getValue,
                                                IValueCommentPO::getComment)));
                        result.add(param2);
                        param2.setOptional(false);
                        found = true;
                    }
                }
            }
        }
        return found;
    }





    /**
     * @param node ExecTC from which the data is chosen
     * @param paramGUID check in the data set for usage of this GUID
     * @return a possibly empty Set of parameter GUIDs which are the parameters 
     * which are substituted by paramGUID
     */
    private static Set<String> findSubstitutes(IExecTestCasePO node,
            String paramGUID) {
        Set<String> result = new HashSet<String>();

        for (IParamDescriptionPO paramDesc : node.getParameterList()) {
            for (int rowNum = 0; rowNum < node.getDataManager()
                    .getDataSetCount(); rowNum++) {
                try {
                    String value  = node.getDataManager().getCell(rowNum,
                            paramDesc);
                    if ((value != null) && value.endsWith(paramGUID)) {
                        result.add(paramDesc.getUniqueId());
                    }
                } catch (IndexOutOfBoundsException ioob) {
                    // this is a legal state: see ticket #3354
                }
            }
        }
        
        return result;
    }


    /**
     * @param refName name for new parameter
     * @param type of parameter to create
     * @param node which get the new parameter
     * @param mapper to resolve param names
     */
    public void addParameter(String refName, String type, 
        ISpecTestCasePO node, IParamNameMapper mapper) {
        node.addParameter(type, refName, mapper);
    }
    
    /**
     * @param desc of parameter to remove
     * @param specTc with parameter to remove
     */
    public void removeParameter(IParamDescriptionPO desc, 
            ISpecTestCasePO specTc) {
        specTc.removeParameter(desc.getUniqueId());
        removeReferences(desc, specTc.getAllNodeIter());
    }

    /**
     * @param desc desc of removed parameter of specTc
     * @param childrenIt the Iterator of the children which References are to 
     * remove.
     */
    private void removeReferences(IParamDescriptionPO desc,
            Iterator<? extends INodePO> childrenIt) {
        while (childrenIt.hasNext()) {
            INodePO node = childrenIt.next();
            if (node instanceof IParamNodePO) {
                final IParamNodePO child = (IParamNodePO)node;
                final ITDManager mgr = child.getDataManager();
                final Iterator<TDCell> refIt =
                    child.getParamReferencesIterator();
                while (refIt.hasNext()) {
                    final TDCell cell = refIt.next();
                    final String guid = mgr.getUniqueIds().get(cell.getCol());
                    final IParamDescriptionPO childDesc =
                        child.getParameterForUniqueId(guid);
                    final ModelParamValueConverter conv =
                        new ModelParamValueConverter(cell.getTestData(), child,
                                childDesc);
                    if (conv.containsReferences()) {
                        final boolean isModified = conv.removeReference(
                                desc.getUniqueId());
                        if (isModified) {
                            cell.setTestData(conv.getModelString());
                        }
                    }
                }
            }
        }
    }

    /**
     * Change the usage of the given old parameter to the new parameter GUID
     * in all child execution Test Cases of the given specification Test Case.
     * @param specTc The specification Test Case for changing the usage at.
     * {@inheritDoc}
     */
    public void changeUsageParameter(ISpecTestCasePO specTc,
            IParamDescriptionPO desc, String newGuid,
            ParamNameBPDecorator mapper) {
        changeUsageReferences(
                specTc.getAllNodeIter(), desc, newGuid, mapper);
    }

    /**
     * @param childrenIt the Iterator of the children which References are to
     * remove.
     * @param desc The old parameter description, which have to be changed.
     * @param newGuid The new GUID to change the reference to.
     * @param mapper The parameter name mapping used here to persist test data.
     */
    private void changeUsageReferences(Iterator<?> childrenIt,
            IParamDescriptionPO desc, String newGuid,
            ParamNameBPDecorator mapper) {
        while (childrenIt.hasNext()) {
            final IParamNodePO child = (IParamNodePO)childrenIt.next();
            final ITDManager mgr = child.getDataManager();
            final Iterator<TDCell> refIt =
                child.getParamReferencesIterator();
            while (refIt.hasNext()) {
                final TDCell cell = refIt.next();
                final String guid = mgr.getUniqueIds().get(cell.getCol());
                final IParamDescriptionPO childDesc =
                    child.getParameterForUniqueId(guid);
                final String testData = cell.getTestData();
                final ModelParamValueConverter conv =
                    new ModelParamValueConverter(testData, child,
                            childDesc);
                if (conv.containsReferences()) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put(desc.getUniqueId(), newGuid);
                    if (conv.replaceUuidsInReferences(map)) {
                        cell.setTestData(conv.getModelString());
                    }
                }
            }
        }
    }

    /**
     * Gets a List of IExecTestCasePO with unused TestData of the given 
     * ISpecTestCasePO.
     * @param nodePO An INodePO.
     * @return a List of IExecTestCasePO with unused TestData of the given 
     * ISpecTestCasePO or an empty List.
     */
    public static final List<IExecTestCasePO> getExecTcWithUnusedTestData(
            INodePO nodePO) {
        
        List<IExecTestCasePO> unusedTDExecList = 
            new LinkedList<IExecTestCasePO>();
        for (Iterator<?> nodeIt = nodePO.getNodeListIterator(); 
            nodeIt.hasNext();) {

            final INodePO childNode = (INodePO)nodeIt.next();
            if (childNode instanceof IExecTestCasePO) {
                final IExecTestCasePO execTc = (IExecTestCasePO)childNode;
                if (execTc.getDataManager() != null 
                        && execTc.checkHasUnusedTestData()) {
                    
                    unusedTDExecList.add(execTc);
                }
            }
        }
        return unusedTDExecList;
    }
    
    
    
    /**
     * Removes the External-Data-File of the given IParamNodePO if it has no
     * parameter. Otherwise it does nothing
     * @param paramnode the IParamNodePO to check
     * @return true if the External-Data-File has been removed, false otherwise.
     */
    private boolean checkRemoveExternalDataFile(
            IParameterInterfacePO paramnode) {
        if (paramnode.getParameterList().isEmpty()) {
            paramnode.setDataFile(null);
            return true;
        }
        return false;
    }
    
    /**
     * Sets the Interface (Parameters) of the given isInterfaceLocked to the
     * given locked status.
     * @param specTC an ISpecTestCasePO.
     * @param locked true if locked, false otherwise.
     */
    public static final void setInterfaceLocked(ISpecTestCasePO specTC, 
            boolean locked) {
        
        specTC.setInterfaceLocked(locked);
    }
    
    /**
     * Returns true, if the given IParamNodePO allowes References as data values.
     * @param paramNode an IParamNodePO
     * @return true if References are allowed, false otherwise.
     */
    public static final boolean isReferenceValueAllowed(
            IParameterInterfacePO paramNode) {
        
        if (paramNode instanceof ISpecTestCasePO) {
            return false;
        }
        if (paramNode instanceof IExecTestCasePO 
                || paramNode instanceof ICapPO) {
            
            // FIXME zeb assuming that the preceding instanceof checks imply
            //           that the object is an INodePO
            final INodePO parentNode = NodePM.getSpecTestCaseParent(
                    ((INodePO)paramNode));
            if (parentNode instanceof ISpecTestCasePO) {
                return !((ISpecTestCasePO)parentNode).isInterfaceLocked();
            }
        }
        return true;
    }

}
