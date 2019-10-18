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
package org.eclipse.jubula.client.archive;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.utils.AbstractNonPostOperatingTreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;
import org.eclipse.jubula.client.core.utils.TreeTraverser;
import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * @author BREDEX GmbH
 */
public class ImportExportUtil {    
    /** number of characters of a UUID */
    public static final int UUID_LENGTH = 32; 
    
    /** offset of test result selection */
    public static final int PAGE_SIZE = 1000;
    
    /** Date formatter */
    public static final SimpleDateFormat DATE_FORMATTER =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S"); //$NON-NLS-1$

    /** we do not need constructor */
    private ImportExportUtil() { }
    
    /**
     * @param proj the IProjectPO
     * @param oldToNewGUID a Map with old to new GUID.
     */
    public static void switchCompNamesGuids(IProjectPO proj, 
            final Map<String, String> oldToNewGUID) {
        /** */
        class SwitchCompNamesGuidsOp 
            extends AbstractNonPostOperatingTreeNodeOperation<INodePO> {
            /** {@inheritDoc} */
            public boolean operate(ITreeTraverserContext<INodePO> ctx, 
                    INodePO parent, INodePO node, boolean alreadyVisited) {
                if (node instanceof ICapPO) {
                    switchCapCompNameGuids((ICapPO)node);
                } else if (node instanceof IExecTestCasePO) {
                    switchExecTcCompNameGuids((IExecTestCasePO)node);
                }
                return true;
            }
            /**
             * @param execTc an IExecTestCasePO
             */
            private void switchExecTcCompNameGuids(IExecTestCasePO execTc) {
                for (ICompNamesPairPO pair : new ArrayList<ICompNamesPairPO>(
                        execTc.getCompNamesPairs())) {
                    final String oldGuid = pair.getFirstName();
                    final String newGuid = oldToNewGUID.get(oldGuid);
                    if (newGuid != null) {
                        pair.setFirstName(newGuid);
                        execTc.removeCompNamesPair(oldGuid);
                        execTc.addCompNamesPair(pair);
                    }
                    final String oldSecGuid = pair.getSecondName();
                    final String newSecGuid = oldToNewGUID.get(oldSecGuid);
                    if (newSecGuid != null) {
                        pair.setSecondName(newSecGuid);
                    }
                }
            }
            /**
             * @param cap an IcapPO
             */
            private void switchCapCompNameGuids(ICapPO cap) {
                final String oldGuid = cap.getComponentName();
                final String newGuid = oldToNewGUID.get(oldGuid);
                if (newGuid != null) {
                    cap.setComponentName(newGuid);
                }
            }
        }
        final SwitchCompNamesGuidsOp switchGuidOp = 
            new SwitchCompNamesGuidsOp();
        TreeTraverser ttv = new TreeTraverser(proj, switchGuidOp, true);
        ttv.traverse(true);
        ttv = new TreeTraverser(proj, switchGuidOp, false);
        ttv.traverse(true);
        for (IAUTMainPO autMain : proj.getAutMainList()) {
            final IObjectMappingPO objMap = autMain.getObjMap();
            for (IObjectMappingAssoziationPO oma : objMap.getMappings()) {
                List<String> namesToUpdate = new ArrayList<String>();
                for (String oldLogicName : oma.getLogicalNames()) {
                    if (oldToNewGUID.containsKey(oldLogicName)) {
                        namesToUpdate.add(oldLogicName);
                    }
                }
                for (String oldLogicName : namesToUpdate) {
                    oma.removeLogicalName(oldLogicName);
                    oma.addLogicalName(oldToNewGUID.get(oldLogicName));
                }
            }
        }
    }
    
    /**
     * Find a persistent object which has a GUID.
     * @param usedTestcaseGuid The GUID used to identify this instance
     * @param projectGuid The GUID of the spec testcase's parent project
     * @param parentProject The parent project of the exec testcase
     * @param assignNewGuid <code>true</code> if elements are being assigned new 
     *              GUIDs. Otherwise <code>false</code>.
     * @param oldToNewGuids map of old- and new Guids
     * @param tcRef Referenced TC is in same project
     * @return the object build while reading the XML element, or 
     *              <code>null</code> if the object cannot be found
     */
    public static ISpecTestCasePO findReferencedTCByGuid(
            String usedTestcaseGuid, String projectGuid,
            IProjectPO parentProject, boolean assignNewGuid,
            Map<String, String> oldToNewGuids,
            Map<String, ISpecTestCasePO> tcRef) {
        
        String actualProjectGuid = assignNewGuid 
            ? oldToNewGuids.get(projectGuid) : projectGuid;
        if (projectGuid == null
            || parentProject.getGuid().equals(actualProjectGuid)) {
            // Referenced TC is in same project
            if (assignNewGuid) {
                return tcRef.get(oldToNewGuids.get(usedTestcaseGuid));
            }
            return tcRef.get(usedTestcaseGuid);
        }
        
        // Referenced TC is in different project
        return NodePM.getSpecTestCase(parentProject.getUsedProjects(), 
            projectGuid, usedTestcaseGuid);
    }

    /**
     * @param l long number
     * @return string of l or null if l is null
     */
    public static String i2str(Long l) {
        if (l != null) {
            return l.toString();
        }
        return StringConstants.EMPTY;
    }

    /**
     * Checks whether the operation has been canceled. If the operation has been
     * canceled, an <code>OperationCanceledException</code> will be thrown.
     * 
     * @param monitor is an IProgressMonitor
     * 
     * @throws OperationCanceledException if the operation has been canceled.
     */
    public static void checkCancel(IProgressMonitor monitor)
            throws OperationCanceledException {
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
    }
}
