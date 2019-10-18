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
package org.eclipse.jubula.client.archive.converter.json;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.client.archive.converter.utils.AbstractConverter;
import org.eclipse.jubula.client.archive.dto.CapDTO;
import org.eclipse.jubula.client.archive.dto.CategoryDTO;
import org.eclipse.jubula.client.archive.dto.ExportInfoDTO;
import org.eclipse.jubula.client.archive.dto.NodeDTO;
import org.eclipse.jubula.client.archive.dto.ProjectDTO;
import org.eclipse.jubula.client.archive.dto.TestCaseDTO;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Action;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.InvalidAction;

/**
 * This converter is removing doubled uniqueIDs from the TDManager of CAPS
 * @author BREDEX GmbH
 */
public class RemoveDoubledUniqueIds extends AbstractConverter<ProjectDTO> {
    /** the version info to check if conversion is needed */
    private ExportInfoDTO m_exportInfo = null;
    
    /**
     * @param exportInfo the exported info to check if the project must be converted
     */
    public RemoveDoubledUniqueIds(ExportInfoDTO exportInfo) {
        m_exportInfo = exportInfo;
    }
    
    /**
     * {@inheritDoc}
     */
    protected boolean conversionIsNecessary(ProjectDTO project) {
        if (m_exportInfo != null && m_exportInfo.getMajorVersion() == 1
                && m_exportInfo.getMinorVersion() == 0
                && m_exportInfo.getMicroVersion() == 0) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    protected void convertImpl(ProjectDTO project) {
        List<NodeDTO> categories = project.getCategories();
        for (NodeDTO nodeDTO : categories) {
            convertNodes(nodeDTO);
        }
        
    }
    
    /**
     * removes uniqueIDs which have seemed to be doubled
     * @param node the node to convert
     */
    private void convertNodes(NodeDTO node) {
        if (node instanceof TestCaseDTO) {
            List<NodeDTO> testSteps = ((TestCaseDTO) node).getTestSteps();
            for (NodeDTO nodeDTO : testSteps) {
                if (nodeDTO instanceof CapDTO) {
                    CapDTO cap = (CapDTO) nodeDTO;
                    String componentname = cap.getComponentName();
                    CompSystem compSystem =
                            ComponentBuilder.getInstance().getCompSystem();
                    Component component =
                            compSystem.findComponent(cap.getComponentType());
                    Action action = component.findAction(cap.getActionName());
                    List<String> uniqueIds = cap.getTDManager().getUniqueIds();
                    int uniqueIdSize = uniqueIds.size();
                    if (!(action instanceof InvalidAction)
                            && uniqueIdSize > action.getParamsSize()) {
                        // either Action has changed or they have doubled entrys
                        List<String> end = uniqueIds.subList(uniqueIdSize / 2,
                                uniqueIdSize);
                        end = new ArrayList<String>(end);
                        boolean allTheSame = true;
                        for (int i = 0; i < (uniqueIdSize / 2); i++) {
                            if (!uniqueIds.get(i).equals(end.get(i))) {
                                allTheSame = false;
                            }
                        }
                        if (allTheSame) {
                            cap.getTDManager().getUniqueIds().clear();
                            cap.getTDManager().getUniqueIds().addAll(end);

                        }

                    }
                }
            }
        } else if (node instanceof CategoryDTO) {
            CategoryDTO cat = (CategoryDTO) node;
            List<NodeDTO> nodeList = cat.getNodes();
            for (NodeDTO nodeDTO : nodeList) {
                convertNodes(nodeDTO);
            }
        }
    }

}
