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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jubula.client.core.businessprocess.treeoperations.FindNodesForComponentNameOp;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.utils.TreeTraverser;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;


/**
 * Acts as a Component Name cache for the master session.
 *
 * @author BREDEX GmbH
 * @created Apr 9, 2008
 */
public class ComponentNamesBP {
    
    /** i18n key for the "unknown" component type */
    public static final String UNKNOWN_COMPONENT_TYPE = "guidancer.abstract.Unknown"; //$NON-NLS-1$
    
    /**
     * @author BREDEX GmbH
     * @created Apr 16, 2008
     */
    public enum CompNameCreationContext {
        /** Component Name Browser context */
        CN_BROWSER,
        /** ObjectMapping-Editor context*/
        OBJECT_MAPPING,
        /** Overriden name context */
        OVERRIDDEN_NAME,
        /** Test Step context */
        STEP;
        
        /** for toString and forName */
        private static final String CN_BROWSER_CTX = "CN_BROWSER"; //$NON-NLS-1$
        
        /** for toString and forName */
        private static final String OBJECT_MAPPING_CTX = "OBJECT_MAPPING"; //$NON-NLS-1$
        
        /** for toString and forName */
        private static final String OVERRIDDEN_NAME_CTX = "OVERRIDDEN_NAME"; //$NON-NLS-1$
        
        /** for toString and forName */
        private static final String STEP_CTX = "STEP"; //$NON-NLS-1$
        
        /** {@inheritDoc} */
        public String toString() {
            switch (this) {
                case CN_BROWSER:
                    return CN_BROWSER_CTX;
                case OBJECT_MAPPING:
                    return OBJECT_MAPPING_CTX;
                case OVERRIDDEN_NAME:
                    return OVERRIDDEN_NAME_CTX;
                case STEP:
                    return STEP_CTX;
                default:
                    // nothing
            }
            Assert.notReached("Missing toString representation for CompNameCreationContext"); //$NON-NLS-1$
            return StringConstants.EMPTY;
        }
        
        /**
         * @param name a toString representation of a CompNameCreationContext.
         * @return a CompNameCreationContext
         */
        public static CompNameCreationContext forName(String name) {
            if (CN_BROWSER_CTX.equals(name)) {
                return CN_BROWSER;
            }
            if (OBJECT_MAPPING_CTX.equalsIgnoreCase(name)) {
                return OBJECT_MAPPING;
            }
            if (OVERRIDDEN_NAME_CTX.equalsIgnoreCase(name)) {
                return OVERRIDDEN_NAME;
            }
            if (STEP_CTX.equalsIgnoreCase(name)) {
                return STEP;
            }
            Assert.notReached("No CompNameCreationContext for '" //$NON-NLS-1$
                    + String.valueOf(name) + "'"); //$NON-NLS-1$
            return null;
        }
    }
    
    /**
     * Singleton Constructor.
     */
    private ComponentNamesBP() {
        //
    }
    
    /**
     * Sets the Component Name for the given Test Step.
     * 
     * Callers should ensure that they have a rollback mechanism in case
     *      the user aborts the operation.
     * 
     * @param capPo The Test Step for which to set the Component Name.
     * @param compName The logical name of the Component Name to use.
     * @param ctx The creation context of the Component Name.
     * @param compCache The Component Name Cache responsible for the given 
     *                   Test Step.
     */
    public static void setCompName(ICapPO capPo, String compName, 
            CompNameCreationContext ctx, 
            IWritableComponentNameCache compCache) {
        
        String oldGuid = capPo.getComponentName();

        if (StringUtils.isBlank(compName)) {
            compCache.changeReuse(capPo, oldGuid, null);
            return;
        }
        String guidToSet = compCache.getGuidForName(compName, 
                GeneralStorage.getInstance().getProject().getId());
        if (guidToSet == null) {
            final IComponentNamePO newComponentNamePO = 
                compCache.createComponentNamePO(
                        compName, capPo.getComponentType(), ctx);
            if (capPo.getParentProjectId() != null) {
                newComponentNamePO.setParentProjectId(
                        capPo.getParentProjectId());
            }
            guidToSet = newComponentNamePO.getGuid();
        }
        compCache.changeReuse(capPo, oldGuid, guidToSet);
    }

    /**
     * Finds all instances of reuse of a Component Name within the given 
     * objects.
     * 
     * @param specsToSearch The Test Cases and Categories to search 
     *                      (recursively).
     * @param suitesToSearch The Test Suites to search.
     * @param compNameGuid The GUID of the Component Name for which to find the
     *                    instances of reuse.
     * @param monitor The progress monitor for this operation.
     * @return all instances of reuse of the Component Name with GUID 
     *         <code>compNameGuid</code> within <code>specsToSearch</code> and
     *         <code>autsToSearch</code>.
     */
    public static Set<INodePO> findNodesOfReuse(
            Collection<INodePO> specsToSearch, 
            Collection<ITestSuitePO> suitesToSearch, String compNameGuid, 
            IProgressMonitor monitor) {

        Set<INodePO> reuse = new HashSet<INodePO>();

        monitor.beginTask(
                Messages.ShowWhereUsedSearching,
                specsToSearch.size() + suitesToSearch.size());
        
        for (INodePO node : specsToSearch) {
            FindNodesForComponentNameOp op = 
                new FindNodesForComponentNameOp(compNameGuid);
            TreeTraverser traverser = new TreeTraverser(node, op);
            traverser.traverse(true);
            reuse.addAll(op.getNodes());
            
            monitor.worked(1);
        }

        for (ITestSuitePO ts : suitesToSearch) {
            FindNodesForComponentNameOp op = 
                new FindNodesForComponentNameOp(compNameGuid);
            TreeTraverser traverser = new TreeTraverser(ts, op);
            traverser.traverse(true);
            reuse.addAll(op.getNodes());
            
            monitor.worked(1);
        }
        
        return reuse;
    }

    /**
     * Finds all instances of reuse of a Component Name within the given 
     * objects.
     * 
     * @param autsToSearch The AUTs for which to search through the 
     *                     Object Mapping.
     * @param compNameGuid The GUID of the Component Name for which to find the
     *                    instances of reuse.
     * @return all instances of reuse of the Component Name with GUID 
     *         <code>compNameGuid</code> within <code>autsToSearch</code>.
     */
    public static Set<IObjectMappingAssoziationPO> findAssocsOfReuse(
            Collection<IAUTMainPO> autsToSearch, String compNameGuid) {
        Set<IObjectMappingAssoziationPO> reuse = 
            new HashSet<IObjectMappingAssoziationPO>();
        
        for (IAUTMainPO aut : autsToSearch) {
            for (IObjectMappingAssoziationPO assoc 
                    : aut.getObjMap().getMappings()) {
                
                if (assoc.getTechnicalName() != null 
                        && assoc.getLogicalNames().contains(compNameGuid)) {
                    reuse.add(assoc);
                }
            }
        }
        return reuse;
    }
}