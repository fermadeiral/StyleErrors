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

import java.util.List;

import org.eclipse.jubula.client.archive.converter.utils.AbstractConverter;
import org.eclipse.jubula.client.archive.dto.CapDTO;
import org.eclipse.jubula.client.archive.dto.CategoryDTO;
import org.eclipse.jubula.client.archive.dto.DataRowDTO;
import org.eclipse.jubula.client.archive.dto.ExportInfoDTO;
import org.eclipse.jubula.client.archive.dto.NodeDTO;
import org.eclipse.jubula.client.archive.dto.ParamDescriptionDTO;
import org.eclipse.jubula.client.archive.dto.ProjectDTO;
import org.eclipse.jubula.client.archive.dto.TestCaseDTO;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Action;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Param;

/**
 * This converter adds the timeout parameter to caps which are missing it and 
 * giving it the default value
 * @author BREDEX GmbH
 */
public class AddTimeoutToCAPConverter extends AbstractConverter<ProjectDTO> {
    /** the version info to check if conversion is needed */
    private ExportInfoDTO m_exportInfo = null;
    
    /**
     * @param exportInfo the exported info to check if the project must be converted
     */
    public AddTimeoutToCAPConverter(ExportInfoDTO exportInfo) {
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
     * Adds the missing timeout value to the CAP. This method 
     * is recursive and is handling all children of categories
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
                    if (action.getParamsSize() > cap.getParameterDescription()
                            .size()) {
                        Param capParam = action.getParams()
                                .get(action.getParams().size() - 1);
                        if (capParam.getName().equals("CompSystem.Timeout")) { //$NON-NLS-1$
                            addMissingParamToCap(cap, capParam);
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
    
    /**
     * adds the parameter with its default value to the {@link CapDTO}
     * @param cap the {@link CapDTO}
     * @param capParam the {@link Param}
     */
    private void addMissingParamToCap(CapDTO cap, Param capParam) {
        ParamDescriptionDTO paramdesc =
                new ParamDescriptionDTO();
        paramdesc.setType(capParam.getType());
        paramdesc.setName(capParam.getName());
        paramdesc.setUuid(capParam.getName());
        cap.getParameterDescription().add(paramdesc);
        DataRowDTO dataRow = new DataRowDTO();
        cap.getTDManager().getDataSets().get(0).getColumns()
                .add(capParam.getDefaultValue());
        cap.getTDManager().getUniqueIds()
                .add(capParam.getName());
    }

}
