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
package org.eclipse.jubula.client.core.businessprocess.compcheck;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.iterators.IteratorChain;
import org.eclipse.jubula.client.core.businessprocess.CompNameManager;
import org.eclipse.jubula.client.core.businessprocess.problems.IProblem;
import org.eclipse.jubula.client.core.businessprocess.problems.ProblemFactory;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.LogicComponentNotManagedException;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ConcreteComponent;

/**
 * Class responsible for collecting completeness information for AUTs
 * @author BREDEX GmbH
 * @created Aug 18, 2016
 */
public class CompCheck {

    /** Map Node Id => Set of propagated Component Name guids which must be mapped in order to use the node */
    private Map<Long, Set<String>> m_mustMap;

    /** Map Node Id => Set of unpropagated Component Name guids
     * which must be mapped in order to use the node.
     * These CNs are not propagated from the Node, so they
     * themselves will have to be mapped finally.
     * The only way to get here: a CN has to be the second CN in a
     *      an unpropagated persisted CNPair, transient CNPairs just
     *      let the second CN fall through like they were propagated...
     */
    private Map<Long, Set<String>> m_mustMapNoProp;
    
    /** The original Test Suites */
    private List<ITestSuitePO> m_suites;
    
    /** The current AUT for Problem Collecting */
    private IAUTMainPO m_aut;
    
    /** A unique problem for the current AUT */
    private IProblem m_autProblem;
    
    /** The ID => unique problem for the AUTs */
    private Map<Long, IProblem> m_autProblems;
    
    /** 
     * Constructor
     * @param suites the suites
     **/
    public CompCheck(List<ITestSuitePO> suites) {
        m_mustMap = new HashMap<>();
        m_mustMapNoProp = new HashMap<>();
        m_suites = suites;
    }

    /**
     * Collects the Component Name usage information for a list of Test Suites
     */
    public void traverse() {
        long start = System.currentTimeMillis();
        IAUTMainPO aut;
        IComponentIdentifier id;
        Set<String> problems;
        for (ITestSuitePO ts : m_suites) {
            aut = ts.getAut();
            if (aut != null) {
                traverseImpl(ts);
                problems = getProblematicGuids(ts); 
            }
        }
    }
    
    /**
     * The traverse implementation, bottom - up
     * @param node the current node, can be ITestSuitePO or IExecTestCasePO
     */
    private void traverseImpl(INodePO node) {
        // first we collect all used Component Names for all children of a node
        // and after this we calculate the usage for the node's SpecTC (not the node itself!)
        INodePO next;
        for (Iterator<INodePO> it = node.getAllNodeIter(); it.hasNext();) {
            next = it.next();
            // if m_mustMap contains the Id, that means we have already traversed the child
            if (getId(next) != null && !m_mustMap.containsKey(getId(next))
                    && !(next instanceof ICapPO)) {
                // if we have not yet dealt with the node (in case of an ExecTC the corresponding SpecTC!)
                // then we deal with it
                traverseImpl(next);
            }
        }
        // Finished with all children (Node and Event), next step is to fill the node's guid sets
        Set<String> nodeGuids = new HashSet<>();
        Set<String> nodeGuidsNoProp = new HashSet<>();
        Long id = getId(node);
        String guid;
        for (Iterator<INodePO> it = node.getAllNodeIter(); it.hasNext();) {
            handleNext(nodeGuids, nodeGuidsNoProp, it.next());
        }
        m_mustMap.put(id, nodeGuids);
        m_mustMapNoProp.put(id, nodeGuidsNoProp);
    }

    /**
     * Puts the used guids to the given set
     * @param nodeGuids the set of used and propagated Guids
     * @param nodeGuidsNoProp the set of used and unpropagated Guids
     * @param child the child of the SpecTC
     */
    private void handleNext(Set<String> nodeGuids, Set<String> nodeGuidsNoProp,
            INodePO child) {
        if (!child.isActive()) {
            return;
        }
        String guid;
        if (child instanceof IExecTestCasePO) {
            handleExecTestCasePO(nodeGuids, nodeGuidsNoProp,
                    (IExecTestCasePO) child);
        } else if (child instanceof ICapPO && isRelevant((ICapPO) child)) {
            guid = ((ICapPO) child).getComponentName();
            if (guid != null) {
                nodeGuids.add(CompNameManager.getInstance().resolveGuid(guid));
            }
        }
    }
    
    /**
     * Handles an ExecTestCase child: we have to alter the Comp Names by the child's Comp Names Pairs
     * @param guids the set of used and propagated guids of the parent
     * @param guidsNoProp the set of used and unpropagated guids of the parent
     * @param child the child
     */
    private void handleExecTestCasePO(Set<String> guids,
            Set<String> guidsNoProp, IExecTestCasePO child) {
        // We designed the traverse such that the corresponding SpecTC must have been traversed before
        ISpecTestCasePO childSpecTC = child.getSpecTestCase();
        if (childSpecTC != null) {
            Set<String> childSpecGuids = m_mustMap.get(childSpecTC.getId());
            ICompNamesPairPO pair;
            for (String guid : childSpecGuids) {
                pair = child.getCompNamesPair(guid);
                if (pair == null) {
                    // transient pairs are propagating as long as
                    // a non-propagating one is found
                    guids.add(guid);
                } else if (pair.isPropagated()) {
                    guids.add(CompNameManager.getInstance().
                            resolveGuid(pair.getSecondName()));
                } else {
                    guidsNoProp.add(CompNameManager.getInstance().
                            resolveGuid(pair.getSecondName()));
                }
            }
            // Non-propagated CNs from the child just fall through...
            guidsNoProp.addAll(m_mustMapNoProp.get(childSpecTC.getId()));
        }
    }
    
    /**
     * Returns the Id of the SpecTC for ExecTCs and the normal id for other nodes
     * @param node the node
     * @return the id or null if 
     */
    private Long getId(INodePO node) {
        if (node instanceof IExecTestCasePO) {
            ISpecTestCasePO specTC = ((IExecTestCasePO) node).getSpecTestCase();
            if (specTC != null) {
                return specTC.getId();
            }
            return null;
        }
        return node.getId();
    }
    
    /**
     * Adds Problem markers to Nodes: exactly those paths are marked
     * which start in a problematic TS and end at the last node where there
     * is still a chance to correct the map by changing the corresponding CNPair
     */
    public void addProblems() {
        m_autProblems = new HashMap<>();
        for (ITestSuitePO ts : m_suites) {
            m_aut = ts.getAut();
            if (m_aut != null) {
                m_autProblem = m_autProblems.get(m_aut.getId());
                if (m_autProblem == null) {
                    m_autProblem = ProblemFactory.
                            createIncompleteObjectMappingProblem(m_aut);
                    m_autProblems.put(m_aut.getId(), m_autProblem);
                }
                Set<String> problems = getProblematicGuids(ts);
                if (!problems.isEmpty()) {
                    addProblemsImpl(ts, problems);
                }
            }
        }
    }
    
    /**
     * The recursive method adding the problems to the nodes
     * @param node the starting node
     * @param problemGuids the problematic guids at this node
     *      - these are not mapped, should be, and still has a chance to be corrected
     *        by changing a CNPair
     */
    private void addProblemsImpl(INodePO node, Set<String> problemGuids) {
        if (problemGuids.size() == 0 || !node.isActive()) {
            return;
        }

        node.addProblem(m_autProblem);
        
        INodePO child;
        for (Iterator<INodePO> it = node.getAllNodeIter(); it.hasNext();) {
            // adding problematic guids to ExecTestCase children
            // other children cannot change CNs in the chain, so they are irrelevant
            child = it.next();
            if (child instanceof IExecTestCasePO) {
                addProblemsImpl(child, problemHandleExecTC(
                        (IExecTestCasePO) child, problemGuids));
            }
        }
    }

    /**
     * Collecting the problematic guids for an ExecTC
     * @param child the ExecTC
     * @param problemGuids the problematic guids of the parent
     *        (these are not mapped by the AUT at the top of the current tree path)
     * @return the problematic guids for the ExecTC
     *        these are guids that are mapped to the problemGuids Set by the CompName
     *        pairs of the ExecTC
     */
    private Set<String> problemHandleExecTC(IExecTestCasePO child,
            Set<String> problemGuids) {
        ISpecTestCasePO spec = child.getSpecTestCase();
        Set<String> result = new HashSet<>();
        if (spec == null) {
            return result;
        }
        for (String guid : m_mustMapNoProp.get(spec.getId())) {
            // Nonpropagated CNs are carried forward into the child
            if (problemGuids.contains(guid)) {
                result.add(guid);
            }
        }
        for (String guid : m_mustMap.get(spec.getId())) {
            ICompNamesPairPO pair = child.getCompNamesPair(guid);
            if (pair == null
                    && problemGuids.contains(guid)) {
                // Nonpropagated CNs are carried forward into the child
                // Since result is non-empty, child will have a Problem marker later
                result.add(guid);
            } else if (pair != null
                    && !pair.isPropagated()
                    && problemGuids.contains(pair.getSecondName())) {
                // This particular problem could be resolved here by changing the CNPair
                // So we don't add this guid instance to the result
                // Instead we mark the child - this is necessary, because if result remains
                // empty, then the child won't get its own marker.
                child.addProblem(m_autProblem);
            }
        }
        return result;
    }
    
    /**
     * Collecting those guids of a TS which should be mapped, but aren't
     * @param ts the TS
     * @return the set of unmapped guids
     */
    private Set<String> getProblematicGuids(ITestSuitePO ts) {
        Set<String> problemGuids = new HashSet<>();
        IComponentIdentifier id;
        IAUTMainPO aut = ts.getAut();
        IteratorChain it = new IteratorChain(
                m_mustMap.get(ts.getId()).iterator(),
                m_mustMapNoProp.get(ts.getId()).iterator());
        while (it.hasNext()) {
            String guid = (String) it.next();
            id = null;
            try {
                id = aut.getObjMap().getTechnicalName(guid);
            } catch (LogicComponentNotManagedException e) {
                // Nothing
            }
            if (id == null) {
                problemGuids.add(guid);
            }
        }
        return problemGuids;
    }      
    
    /**
     * Decides if the Component Name of a CAP needs mapping
     * @param cap the cap
     * @return the result
     */
    private boolean isRelevant(ICapPO cap) {
        Component metaComponentType = cap.getMetaComponentType();
        if (metaComponentType instanceof ConcreteComponent
                && ((ConcreteComponent) metaComponentType)
                        .hasDefaultMapping()) {
            return false;
        }
        return true;
    }
    
    /**
     * Returns the Suit ID => Set of guids of Component Names to map Map
     * @return the Map
     */
    public Map<Long, Set<String>> getCompNamesToMap() {
        Map<Long, Set<String>> result = new HashMap<>(m_suites.size());
        for (ITestSuitePO ts : m_suites) {
            if (ts.getAut() != null) {
                result.put(ts.getId(), m_mustMap.get(ts.getId()));
                result.put(ts.getId(), m_mustMapNoProp.get(ts.getId()));
            }
        }
        return result;
    }
}