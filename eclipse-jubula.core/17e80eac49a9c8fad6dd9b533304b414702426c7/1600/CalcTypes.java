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
package org.eclipse.jubula.client.core.businessprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.businessprocess.db.TestSuiteBP;
import org.eclipse.jubula.client.core.businessprocess.problems.ProblemFactory;
import org.eclipse.jubula.client.core.businessprocess.problems.ProblemType;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;

/**
 * Calculates the types for all CNs
 * Used when loading a project, changing the usage of a Component Name or saving an Editor
 * Collects a lot of information, it is up to the caller to decide what is to be done with it
 * 
 * There are two main entry points: calculateTypes() and calculateLocalTypes()
 * The formal is global and if m_writeTypes is set, it writes the calculated types
 *      to the main session's CNs and CNPairs
 * The second one only writes to the traversed CNPairs
 * 
 * @author BREDEX GmbH
 *
 */
public class CalcTypes {
    
    /** The guid of the object which is to be replaced by an in-Editor local version */
    private String m_guidToSwap;
    
    /** The local version of the edited node */
    private INodePO m_localNode;
    
    /**
     * The (NodeID = (Guid => type)) map of local propagated usage types
     */
    private Map<Long, Map<String, String>> m_localPropTypes = new HashMap<>();

    /**
     * The (NodeID = (Guid => type)) map of local final usage types
     * These are CNs which come from an unpropagated persistent CNPair
     *      so they will never agan be 'captured' up the CNPair chain
     */
    private Map<Long, Map<String, String>> m_localFinalTypes = new HashMap<>();

    /** The Guid => calculated usage type map */
    private Map<String, String> m_usageType = new HashMap<>();
    
    /**
     * The Guid => calculated global type map
     * the global type is the usage type or the last most concrete map type
     */
    private Map<String, String> m_globalType = new HashMap<>();
    
    /** Found problems */
    private Map<String, ProblemType> m_allProblems = new HashMap<>();
    
    /** Info for the problems */
    private Map<String, List<String>> m_problemInfo = new HashMap<>();
    
    /** The GUID => last type change map */
    private Map<String, String> m_lastTypeChange = new HashMap<>();
    
    /** Most abstract component type */
    private String m_mostAbstract = CompNameTypeManager.getMostAbstractType();
    
    /** The Component Name Cache used */
    private IComponentNameCache m_cache;
    
    /** Whether to write the types to CNs and CNPairs */
    private boolean m_writeTypes = false;
    
    /**
     * The constructor
     * @param cache the used component name cache
     * @param node the node to replace the master version or null
     */
    public CalcTypes(IComponentNameCache cache, INodePO node) {
        m_cache = cache;
        m_guidToSwap = node == null ? null : node.getGuid();
        m_localNode = node;
    }
    
    /**
     * Setting whether to write the types to the CNs and CNPairs
     * @param write whether to write
     */
    public void setWriteTypes(boolean write) {
        m_writeTypes = write;
    }
    
    /** Calculating the types */
    public void calculateTypes() {
        List<IProjectPO> reusedAndSelf = new ArrayList<>(
                GeneralStorage.getInstance().getReusedProjects().values());
        reusedAndSelf.add(GeneralStorage.getInstance().getProject());
        for (IProjectPO proj : reusedAndSelf) {
            // SpecTestCasePOs
            for (INodePO node : proj.getUnmodSpecList()) {
                if (!m_localPropTypes.containsKey(node.getId())) {
                    traverse(node);
                }
            }
            // TestSuitePOs
            for (ITestSuitePO ts : TestSuiteBP.getListOfTestSuites(proj)) {
                traverse(ts);
            }
        }
        collectUsageProblems();
        handleAssociations();
        if (m_writeTypes) {
            writeCompNameTypesAndProblems();
        }
    }
    
    /**
     * Calculating the local type for a single Component Name
     * @param node the starting node - should be SpecTC or TestSuite
     * @param guid the guid - can be null if irrelevant
     * @return the type - can be null if the CN does not appear anywhere
     *          below the given node
     */
    public String calculateLocalType(INodePO node, String guid) {
        if (!(node instanceof ISpecTestCasePO
                || node instanceof ITestSuitePO)) {
            throw new IllegalArgumentException(
                    "Node can only be a Spec Test Case or a Test Suite"); //$NON-NLS-1$
        }
        traverse(node);
        return m_localPropTypes.get(node.getId()).get(guid);
    }
    
    /** Collects usage incompatibility problems */
    private void collectUsageProblems() {
        for (String guid : m_usageType.keySet()) {
            if (m_usageType.get(guid).equals(
                    ComponentNamesBP.UNKNOWN_COMPONENT_TYPE)) {
                m_allProblems.put(guid,
                        ProblemType.REASON_INCOMPATIBLE_USAGE_TYPE);
            }
        }
    }
    
    /**
     *  Handling the associations
     *  All mapped types should realize the most concrete usage type
     *  The most concrete of usages and the last map will be the new type
     */
    private void handleAssociations() {
        // The Guid => most concrete mapped type
        Map<String, String> maps = new HashMap<>(); 
        CompSystem compSystem = ComponentBuilder.getInstance().getCompSystem();
        String resGuid;

        for (IAUTMainPO aut : GeneralStorage.getInstance()
                .getProject().getAutMainList()) {
            for (IObjectMappingAssoziationPO assoc : aut.getObjMap()
                    .getMappings()) {
                IComponentIdentifier technicalName = assoc.getTechnicalName();
                if (technicalName == null) {
                    continue;
                }
                List<Component> availableComponents = compSystem
                        .getComponents(aut.getToolkit(), true);
                String type = CompSystem.getComponentType(technicalName
                        .getSupportedClassName(), availableComponents);
                for (String guid : assoc.getLogicalNames()) {
                    resGuid = CompNameManager.getInstance().resolveGuid(guid);
                    if (!maps.containsKey(resGuid) || CompNameTypeManager
                            .doesFirstTypeRealizeSecond(type,
                                    maps.get(resGuid))) {
                        maps.put(resGuid, type);
                    }
                    String globType = m_usageType.get(resGuid);
                    if (globType == null) {
                        m_usageType.put(resGuid, m_mostAbstract);
                        globType = m_mostAbstract;
                    }
                    if (globType != null && !globType.equals(
                            ComponentNamesBP.UNKNOWN_COMPONENT_TYPE)
                            && !CompNameTypeManager.doesFirstTypeRealizeSecond(
                            type, globType)) {
                        // incompatible usage and mapping
                        m_allProblems.put(resGuid,
                                ProblemType.REASON_INCOMPATIBLE_MAP_TYPE);
                        List<String> info = new ArrayList<>(2);
                        info.add(CompSystemI18n.getString(type));
                        info.add(CompSystemI18n.getString(globType));
                        info.add(m_lastTypeChange.get(guid));
                        info.add(aut.getGuid());
                        m_problemInfo.put(resGuid, info);
                    }
                }
            }
        }
        // determining the final type: most concrete of usages and maps
        for (String guid : m_usageType.keySet()) {
            if (!maps.containsKey(guid)
                || ProblemType.REASON_INCOMPATIBLE_USAGE_TYPE.equals(
                        m_allProblems.get(guid))
                || ProblemType.REASON_INCOMPATIBLE_MAP_TYPE.equals(
                        m_allProblems.get(guid))) {
                // if there is no map or
                // if there is a map problem then the displayed type is the usage type
                m_globalType.put(guid, m_usageType.get(guid));
            } else {
                // otherwise the type is the most concrete visible type of the most concrete mapped type
                m_globalType.put(guid, CompNameTypeManager.
                        getMostConcreteVisibleAncestorType(maps.get(guid)));
            }
        }
    }
    
    /**
     * Dealing with a single node
     * @param nodeOld the node
     */
    public void traverse(INodePO nodeOld) {
        // the type of the CN at this node
        HashMap<String, String> localType = new HashMap<String, String>();
        HashMap<String, String> locFinType = new HashMap<String, String>();
        INodePO node = nodeOld;
        if (m_guidToSwap != null 
                && m_guidToSwap.equals(node.getGuid())) {
            node = m_localNode;
        }
        // first we handle a child...
        for (Iterator<INodePO> it = node.getAllNodeIter(); it.hasNext(); ) {
            INodePO child = it.next();
            // this part is for categories
            if (child instanceof ICategoryPO
                    || child instanceof ISpecTestCasePO) {
                traverse(child);
            } else if (child instanceof ICapPO) {
                // ...for CAPs, we simply add the usage type to the CN
                String guid = ((ICapPO) child).getComponentName();
                if (guid != null) {
                    guid = CompNameManager.getInstance().resolveGuid(guid);
                    String type = ((ICapPO) child).getComponentType();
                    updateType(guid, type, localType, child);
                }
            } else if (child instanceof IExecTestCasePO) {
                handleExecTC((IExecTestCasePO) child, localType, locFinType);
            }
        }
        m_localPropTypes.put(node.getId(), localType);
        m_localFinalTypes.put(node.getId(), locFinType);
    }

    /**
     * Processes an ExecTC child by updating the parent SpecTC's local type maps
     * @param child the ExecTC child
     * @param localType the local types map of the parent SpecTC
     * @param locFinType the local final types map of the parent SpecTC
     */
    private void handleExecTC(IExecTestCasePO child,
            Map<String, String> localType, Map<String, String> locFinType) {
     // for ExecTestCasePOs we first handle the SpecTestCasePO
        ISpecTestCasePO spec = child.getSpecTestCase();
        if (spec == null) {
            return;
        }
        if (!m_localPropTypes.containsKey(spec.getId())) {
            traverse(spec);
        }
        // ...and then we add the usages to the 'new' CNs
        // indicated by the CompNamePairPOs
        Map<String, String> mapLocalFinal = m_localFinalTypes.
                get(spec.getId());
        for (String gui : mapLocalFinal.keySet()) {
            updateType(gui, mapLocalFinal.get(gui), locFinType, child);
        }
        Map<String, String> specLocalTypes = m_localPropTypes.
                get(spec.getId());
        for (String gui : specLocalTypes.keySet()) {
            ICompNamesPairPO pair = child.getCompNamesPair(gui);
            String guid;
            if (pair == null) {
                guid = gui;
            } else {
                guid = pair.getSecondName();
                guid = CompNameManager.getInstance().resolveGuid(guid);
            }
            if (pair == null || pair.isPropagated()) {
                // nonexistent pairs keep propagating the CNs due to the bug
                //    https://bugs.eclipse.org/bugs/show_bug.cgi?id=515641
                updateType(guid, specLocalTypes.get(gui), localType, child);
            } else {
                // a nonpropagating persisted pair puts the guid in the final map
                updateType(guid, specLocalTypes.get(gui), locFinType, child);
            }
        }
        setCompNamePairTypes(child, specLocalTypes);
    }
    
    /**
     * Applies the given type to the local and global type maps
     * @param guid the guid
     * @param type the type
     * @param localTypes the local type map to be updated (propagated or final)
     * @param node the node - used only for collecting information
     */
    private void updateType(String guid, String type,
            Map<String, String> localTypes, INodePO node) {
        // Calculating the local type for the CN
        if (localTypes.containsKey(guid)) {
            localTypes.put(guid, CompNameTypeManager.calcUsageType(
                    localTypes.get(guid), type));
        } else {
            localTypes.put(guid, type);
        }
        String currentType = m_usageType.get(guid);
        String currentTypeDisp = CompSystemI18n.getString(currentType);
        String typeDisp = CompSystemI18n.getString(type);
        if (currentType == null) {
            m_usageType.put(guid, m_mostAbstract);
            currentType = m_mostAbstract;
        }
        if (currentType.equals(ComponentNamesBP.UNKNOWN_COMPONENT_TYPE)) {
            return;
        }
        // Calculating the global type for the CN
        String newType = CompNameTypeManager.calcUsageType(
                currentType, type);
        // Collecting information on the conflicting usages
        // This can be used for example in the QuickFix
        if (newType.equals(ComponentNamesBP.UNKNOWN_COMPONENT_TYPE)) {
            List<String> info = new ArrayList<String>(2);
            info.add(typeDisp);
            info.add(currentTypeDisp);
            info.add(m_lastTypeChange.get(guid));
            info.add(node.getGuid());
            m_problemInfo.put(guid, info);
        } else if (!StringUtils.equals(currentTypeDisp,
                CompSystemI18n.getString(newType))) {
            m_lastTypeChange.put(guid, node.getGuid());
        }
        m_usageType.put(guid, newType);
    }
    
    /**
     * Sets the Component Name Pair types
     * @param exec the exec TC
     * @param specMap the specTC's local types map
     */
    private void setCompNamePairTypes(IExecTestCasePO exec,
            Map<String, String> specMap) {
        String resGuid;
        for (ICompNamesPairPO pair : exec.getCompNamesPairs()) {
            if (m_writeTypes) {
                resGuid = CompNameManager.getInstance().resolveGuid(
                        pair.getFirstName());
                pair.setType(specMap.get(resGuid));
            }
        }
    }
    
    /**
     * Writes the usage types into the main session's Component Names
     */
    public void writeCompNameTypesAndProblems() {
        String guid;
        for (IComponentNamePO cN : CompNameManager.getInstance().
                getAllCompNamePOs()) {
            guid = CompNameManager.getInstance().resolveGuid(cN.getGuid());
            if (m_usageType.containsKey(guid)) {
                cN.setUsageType(m_usageType.get(guid));
                cN.setComponentType(m_globalType.get(guid));
            } else {
                cN.setUsageType(m_mostAbstract);
                cN.setComponentType(m_mostAbstract);
            }
            if (m_allProblems.containsKey(guid)) {
                cN.setTypeProblem(ProblemFactory.createIncompatibleTypeProblem(
                        cN, m_allProblems.get(guid)));
            }
        }
    }
    
    /**
     * Writes CN types to the local cache
     */
    public void writeLocalTypes() {
        for (IComponentNamePO cN : m_cache.getLocalChanges().values()) {
            if (m_globalType.containsKey(cN.getGuid())) {
                cN.setComponentType(m_globalType.get(cN.getGuid()));
            }
        }
    }
    
    /**
     * Calculates new problems
     * @return the (Guid => Problem type) map
     */
    public Map<String, ProblemType> getNewProblems() {
        Map<String, ProblemType> newProblems = new HashMap<>();
        for (String guid : m_allProblems.keySet()) {
            IComponentNamePO cN = m_cache.getResCompNamePOByGuid(guid);
            if (cN != null
                    && (cN.getTypeProblem() == null || cN.getTypeProblem()
                    .getProblemType().equals(m_allProblems.get(guid)))) {
                newProblems.put(guid, m_allProblems.get(guid));
            }
        }
        return newProblems;
    }
    
    /**
     * Returns all problems
     * @return the Guid => ProblemType map of all type problems
     */
    public Map<String, ProblemType> getAllProblems() {
        return m_allProblems;
    }
    
    /**
     * @param guid the CN guid
     * @return additional info on the problem of the CN Type
     */
    public List<String> getProblemInfo(String guid) {
        return m_problemInfo.get(guid);
    }
    
    /**
     * Recalculates the CompNamesPairPOs' type starting from the node spec
     * @param cache the cache to use
     * @param spec the SpecTestCasePO
     */
    public static void recalculateCompNamePairs(
            IComponentNameCache cache, INodePO spec) {
        CalcTypes calc = new CalcTypes(cache, spec);
        calc.setWriteTypes(true);
        calc.calculateLocalType(spec, null);

    }
    
    /**
     * Returns the local propagated types at a given node.
     * (That is, the types that are 'seen' by CNPairs of ExecTCs
     *      referencing this node (of course if it is a SpecTC)
     * @param node the node
     * @return the (CN Guid) => (local type) map
     */
    public Map<String, String> getLocalPropTypes(INodePO node) {
        return m_localPropTypes.get(node.getId());
    }
}
