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
package org.eclipse.jubula.client.ui.rcp.wizards.search.refactor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jubula.client.core.businessprocess.CompNamesBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IDataSetPO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITDManager;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.TransactionSupport.ITransaction;
import org.eclipse.jubula.client.ui.rcp.actions.TransactionWrapper;
import org.eclipse.jubula.client.ui.rcp.wizards.search.refactor.pages.ReplaceExecTestCaseData;

/**
 * The actual operation executing the replacement
 * @author BREDEX GmbH
 *
 */
public class ReplaceTestCaseTransaction implements IRunnableWithProgress,
        ITransaction {

    /** The replacement data */
    private ReplaceExecTestCaseData m_data;
    
    /** The monitor */
    private IProgressMonitor m_monitor;
    
    /** The new CN Guid => old CN Guid map */
    private Map<String, String> m_guidMap;
    
    /** The new exec TCs */
    private List<IExecTestCasePO> m_newExecs = new ArrayList<>();

    /**
     * @param data the data
     * @param guidMap the guidMap
     */
    public ReplaceTestCaseTransaction(ReplaceExecTestCaseData data,
            Map<String, String> guidMap) {
        m_data = data;
        m_guidMap = guidMap;
    }
    
    /** {@inheritDoc} */
    public Collection<? extends IPersistentObject> getToLock() {
        List<IPersistentObject> toLock = new ArrayList<>();
        toLock.addAll(m_data.getOldExecTestCases());
        Set<INodePO> par = new HashSet<>();
        for (IExecTestCasePO exec : m_data.getOldExecTestCases()) {
            par.add(exec.getSpecAncestor());
        }
        toLock.addAll(par);
        toLock.add(m_data.getNewSpecTestCase());
        return toLock;
    }

    /** {@inheritDoc} */
    public Collection<? extends IPersistentObject> getToRefresh() {
        Set<IPersistentObject> toRefresh = new HashSet<>();
        for (IExecTestCasePO exec : m_newExecs) {
            toRefresh.add(exec.getSpecAncestor());
        }
        return toRefresh;
    }
    
    /** {@inheritDoc} */
    public Collection<? extends IPersistentObject> getToMerge() {
        return m_newExecs;
    }

    /** {@inheritDoc} */
    public void run(EntityManager sess) throws Exception {
        EntityManager main = GeneralStorage.getInstance().getMasterSession();
        for (IExecTestCasePO exec: m_data.getOldExecTestCases()) {
            INodePO parent = exec.getParentNode();
            int index = parent.indexOf(exec);
            parent = sess.find(parent.getClass(), parent.getId());
            main.detach(exec);
            exec = sess.find(exec.getClass(), exec.getId());
            parent.removeNode(exec);
            IExecTestCasePO newExec;
            if (exec instanceof IEventExecTestCasePO) {
                ISpecTestCasePO spec = (ISpecTestCasePO) parent;
                IEventExecTestCasePO newEventExec = NodeMaker.
                    createEventExecTestCasePO(m_data.getNewSpecTestCase(),
                        parent);
                IEventExecTestCasePO oldEventExec = 
                        (IEventExecTestCasePO) exec;
                newEventExec.setEventType(oldEventExec.getEventType());
                newEventExec.setReentryProp(oldEventExec.getReentryProp());
                newEventExec.setMaxRetries(oldEventExec.getMaxRetries());
                newExec = newEventExec;
                spec.addEventTestCase(newEventExec);
            } else {
                newExec = NodeMaker.createExecTestCasePO(
                        m_data.getNewSpecTestCase());
                parent.addNode(index, newExec);
            }
            // 1. copy primitive members
            copyPrimitiveMembers(exec, newExec);
            // 2. add data manager with new columns
            addParametersFromOldToNewTC(exec, newExec);
            // 3. copy referenced data cube
            if (exec.getReferencedDataCube() != null
                    && !exec.getReferencedDataCube().equals(exec
                            .getSpecTestCase().getReferencedDataCube())) {
                newExec.setReferencedDataCube(exec.getReferencedDataCube());
            }
            // 4. add pairs of component names
            addNewCompNamePairs(exec, newExec);
            sess.remove(exec);
            m_newExecs.add(newExec);
            m_monitor.worked(1);
        }
    }
    
    /**
     * Copy primitive members from old to new execution Test Case.
     * @param oldExec The old execution Test Case.
     * @param newExec The new execution Test Case.
     */
    private void copyPrimitiveMembers(IExecTestCasePO oldExec,
            IExecTestCasePO newExec) {
        newExec.setActive(oldExec.isActive());
        newExec.setComment(oldExec.getComment());
        newExec.setDataFile(oldExec.getDataFile());
        newExec.setGenerated(oldExec.isGenerated());
        // copy execution test case name
        if (oldExec.getSpecTestCase().getName() != oldExec.getName()) {
            newExec.setName(oldExec.getName());
        }
        newExec.setParentProjectId(oldExec.getParentProjectId());
        newExec.setToolkitLevel(oldExec.getToolkitLevel());
        newExec.setHasReferencedTD(
                oldExec.getDataManager().equals(
                oldExec.getSpecTestCase().getDataManager()));
    }

    /**
     * Add the parameters to the new execution Test Case by using the
     * map between new to old parameter descriptions.
     * @param oldExec The old execution Test Case.
     * @param newExec The new execution Test Case.
     */
    private void addParametersFromOldToNewTC(
            IExecTestCasePO oldExec,
            IExecTestCasePO newExec) {
        if (oldExec.getHasReferencedTD()
                || oldExec.getReferencedDataCube() != null) {
            // test data referenced to specification Test Case
            // or a data cube exists
            return; // add no local test data
        }
        // get the parameter map
        Map<IParamDescriptionPO, IParamDescriptionPO> newOldParamMap =
                m_data.getNewOldParamMap();
        // add the new parameter description IDs to data manager
        ITDManager tdManager = newExec.getDataManager();
        for (IParamDescriptionPO newParam: newOldParamMap.keySet()) {
            tdManager.addUniqueId(newParam.getUniqueId());
        }
        // iterate over all data sets (rows)
        for (IDataSetPO oldDataSet: oldExec
                .getDataManager().getDataSets()) {
            List<String> newRow = new ArrayList<String>(
                    newOldParamMap.size());
            // iterate over all new parameter descriptions
            for (IParamDescriptionPO newParam: newOldParamMap.keySet()) {
                IParamDescriptionPO oldParam = newOldParamMap.get(newParam);
                if (oldParam != null) {
                    // copy old test data for new Test Case
                    int column = oldExec.getDataManager()
                            .findColumnForParam(oldParam.getUniqueId());
                    // get column from old test data
                    String oldTestData = oldDataSet.getValueAt(column);
                    newRow.add(oldTestData);
                } else {
                    newRow.add(m_data.getUnmatchedValuesMap().get(newParam));
                }
            }
            // add the new row to data set for new Test Case
            tdManager.insertDataSet(
                    PoMaker.createListWrapperPO(newRow),
                    tdManager.getDataSetCount());
        }
    }

    /**
     * Looks in the<code>m_matchedCompNameGuidMap</code> if there is a
     * matching for the new ExecTestCase and add new CompNamePairs if
     * necessary
     * 
     * @param oldExec
     *            the old ExecTestCase
     * @param newExec
     *            the newly created ExecTestCase
     * @return the <code>newExec</code>
     */
    private IExecTestCasePO addNewCompNamePairs(IExecTestCasePO oldExec,
            IExecTestCasePO newExec) {
        CompNamesBP cNBP = new CompNamesBP();
        // Get all new compNamePairs to get the component names
        Collection<ICompNamesPairPO> compNamePairs = 
                cNBP.getAllCompNamesPairs(newExec);
        for (ICompNamesPairPO newPseudoPairs : compNamePairs) {
            if (m_guidMap.containsKey(newPseudoPairs
                    .getFirstName())) {
                String oldCompName = m_guidMap
                        .get(newPseudoPairs.getFirstName());
                Collection<ICompNamesPairPO> oldCompNamePairs = 
                        cNBP.getAllCompNamesPairs(oldExec);
                
                // Get the corresponding new Name of the old CompNamePair
                for (Iterator iterator = oldCompNamePairs.iterator();
                        iterator.hasNext();) {
                    ICompNamesPairPO oldPair = (ICompNamesPairPO) iterator
                            .next();
                    if (oldPair.getFirstName().equals(oldCompName)) {
                        ICompNamesPairPO newPair = PoMaker
                                .createCompNamesPairPO(
                                        newPseudoPairs.getFirstName(),
                                        oldPair.getSecondName(),
                                        oldPair.getType());
                        newPair.setPropagated(oldPair.isPropagated());
                        newExec.addCompNamesPair(newPair);
                        break;
                    }

                }
            }
        }
        return newExec;
    }

    /**
     * {@inheritDoc}
     */
    public void run(IProgressMonitor monitor) {
        m_monitor = monitor;
        if (TransactionWrapper.executeOperation(this)) {
            DataEventDispatcher.getInstance().fireProjectLoadedListener(
                    monitor);
        }
    }

}
