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
package org.eclipse.jubula.client.ui.rcp.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.businessprocess.UsedToolkitBP;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.widgets.DirectCombo;
import org.eclipse.jubula.mylyn.utils.MylynAccess;
import org.eclipse.jubula.toolkit.common.businessprocess.ToolkitSupportBP;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.toolkit.common.utils.ToolkitUtils;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.ToolkitConstants;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ToolkitDescriptor;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.wikitext.ui.WikiText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;


/**
 * This class creates specific Controls
 *
 * @author BREDEX GmbH
 * @created 05.06.2007
 */
public class ControlFactory {

    /**
     * Private utility constructor
     */
    private ControlFactory() {
        // nothing
    }
    
    /**
     * Creates a Combo with all independent toolkit entries.
     * @param parent the parent of the Combo
     * @return a Combo with all independent toolkit entries.
     */
    public static DirectCombo<String> createToolkitCombo(Composite parent) {
        
        final List<ToolkitDescriptor> descriptors = 
            ComponentBuilder.getInstance().getCompSystem()
                .getIndependentToolkitDescriptors(false);
        List<String> values = new ArrayList<String>();
        List<String> displayValues = new ArrayList<String>();
        for (ToolkitDescriptor desc : descriptors) {
            values.add(desc.getToolkitID());
            displayValues.add(desc.getName());
        }

        return new DirectCombo<String>(
            parent, SWT.READ_ONLY, values, displayValues, false, 
                true);
    }
    
    /**
     * Creates a Combo for AUT-Toolkit depending on the Project-Toolkit.
     * @param parent the parent of the Combo
     * @param project the depending project.
     * @param currentValue the value to select by default. If this value is not 
     *                     found within the toolkit plugins, it will be added to
     *                     the combo box anyway, but it will not be 
     *                     internationalized. If this parameter is 
     *                     <code>null</code>, it will be ignored.
     * @return a Combo with toolkits depending on the Project-Toolkit.
     * @throws ToolkitPluginException if the toolkit for the given project 
     *         cannot be found.
     */
    public static DirectCombo<String> createAutToolkitCombo(Composite parent, 
        IProjectPO project, String currentValue) throws ToolkitPluginException {
        
        return createAutToolkitCombo(parent, project, currentValue, false);
    }
    
    /**
     * Creates a Combo for AUT-Toolkit depending on the Project-Toolkit.
     * @param parent the parent of the Combo
     * @param project the depending project.
     * @param currentValue the value to select by default. If this value is not 
     *                     found within the toolkit plugins, it will be added to
     *                     the combo box anyway, but it will not be 
     *                     internationalized. If this parameter is 
     *                     <code>null</code>, it will be ignored.
     * @param needDefaultNull if <code>true</code>then the first value will be null.
     *                      otherwise will not contain null.
     * @return a Combo with toolkits depending on the Project-Toolkit.
     * @throws ToolkitPluginException if the toolkit for the given project 
     *         cannot be found.
     */
    public static DirectCombo<String> createAutToolkitCombo(Composite parent, 
        IProjectPO project, String currentValue, boolean needDefaultNull)
                throws ToolkitPluginException {
        
        final List<ToolkitDescriptor> toolkits = getAutToolkits(project);

        List<String> values = new ArrayList<String>();
        List<String> displayValues = new ArrayList<String>();
        if (needDefaultNull) {
            values.add(null);
            displayValues.add(StringConstants.EMPTY);
        }
        
        for (ToolkitDescriptor desc : toolkits) {
            values.add(desc.getToolkitID());
            displayValues.add(desc.getName());
        }

        if (currentValue != null && currentValue.trim().length() != 0
            && !values.contains(currentValue)) {
            
            values.add(currentValue);
            displayValues.add(currentValue);
        }

        return new DirectCombo<String>(parent, SWT.READ_ONLY, 
            values, displayValues, false, true);
        
    }
    
    /**
     * Creates a Combo for Project-Toolkit depending on the current 
     * Project-Toolkit and underlying AUT-Toolkits.
     * @param parent the parent of the Combo
     * @return a Combo with toolkits depending on the Project-Toolkit.
     */
    public static DirectCombo<String> createProjectToolkitCombo(
        Composite parent) {

        final IProjectPO project = GeneralStorage.getInstance().getProject();
        final List<ToolkitDescriptor> toolkits = 
            UsedToolkitBP.getInstance().getAllowedProjectToolkits(project);

        final List<String> values = new ArrayList<String>();
        final List<String> displayValues = new ArrayList<String>();
        for (ToolkitDescriptor desc : toolkits) {
            values.add(desc.getToolkitID());
            displayValues.add(desc.getName());
        }

        final String currentValue = project.getToolkit();
        if (currentValue != null && currentValue.trim().length() != 0
            && !values.contains(currentValue)) {
            
            values.add(currentValue);
            final String tkName = ToolkitUtils.getToolkitName(currentValue);
            displayValues.add(tkName);
        }
        
        return new DirectCombo<String>(parent, SWT.NONE, values,
            displayValues, false, true);
    }
    
    /**
     * Creates a Combo for MarkupLanguages
     * @param parent the parent of the Combo
     * @return a Combo with all available MarkupLanguages
     */
    public static DirectCombo<String> createProjectMarkupLanguageCombo(
            Composite parent) {
        final List<String> values = new ArrayList<String>();
        final List<String> displayValues = new ArrayList<String>();
        
        Set<String> markupLanguagesFileExt = WikiText.getMarkupFileExtensions();
        for (String value : markupLanguagesFileExt) {
            values.add(value);
            displayValues.add(WikiText.getMarkupLanguageNameForFilename(value));
        }
        String markupLang = GeneralStorage.getInstance()
                .getProject().getMarkupLanguage();
        if (StringUtils.isBlank(markupLang)) {
            markupLang = Constants.DEFAULT_MARKUP;
        }
        DirectCombo<String> combo = new DirectCombo<String>(parent, SWT.NONE,
                values, displayValues, false, true);
        combo.setSelectedObject(markupLang);
        return combo;
    }
    
    /**
     * Creates a Combo for available ALM repositories depending on the current
     * Mylyn ALM repositories available in the workspace
     * 
     * @param parent
     *            the parent of the Combo
     * @param preConfiguredRepositoryName
     *            the pre-configured repository name which does not necessarily
     *            has to be locally available
     * @return a Combo with available ALM repositories
     */
    public static DirectCombo<String> createALMRepositoryCombo(
        Composite parent, String preConfiguredRepositoryName) {

        final List<String> values = new ArrayList<String>();
        final List<String> displayValues = new ArrayList<String>();
        for (TaskRepository repo : MylynAccess.getAllRepositories()) {
            String repositoryLabel = repo.getRepositoryLabel();
            values.add(repositoryLabel);
            displayValues.add(repositoryLabel);
        }

        if (StringUtils.isNotBlank(preConfiguredRepositoryName)
                && !values.contains(preConfiguredRepositoryName)) {
            values.add(preConfiguredRepositoryName);
            displayValues.add(preConfiguredRepositoryName);
        }
        
        return new DirectCombo<String>(parent, SWT.NONE, values,
            displayValues, true, true);
    }

    /**
     * Creates a Combo for AUT-Toolkit depending on the current AUT-Toolkit.
     * This method should be called only as a backup, if the Project-Toolkit
     * is unavailable.
     * @param parent the parent of the Combo
     * @param aut the depending AUT.
     * @return a Combo with toolkits depending on the current AUT-Toolkit.
     */
    public static DirectCombo<String> createAutToolkitCombo(Composite parent, 
        IAUTMainPO aut) {

        final ToolkitDescriptor toolkit = 
            ComponentBuilder.getInstance().getCompSystem()
                .getToolkitDescriptor(aut.getToolkit());
        final List<String> values = new ArrayList<String>();
        final List<String> displayValues = new ArrayList<String>();
        if (toolkit != null) {
            values.add(toolkit.getToolkitID());
            displayValues.add(toolkit.getName());
            
        } else {
            String autToolkitId = aut.getToolkit();
            if (autToolkitId != null && autToolkitId.trim().length() != 0) {
                values.add(autToolkitId);
                displayValues.add(autToolkitId);
            }
        }        
        return new DirectCombo<String>(parent, SWT.READ_ONLY, 
            values, displayValues, false, true);
    }

    /**
     * Gets the List of Toolkits for a AUT depending of the Project-Toolkit.
     * @param project the depending project.
     * @return the List of Toolkits for a AUT depending of the Project-Toolkit.
     * @throws ToolkitPluginException if the toolkit for the given project 
     *         cannot be found.
     */
    public static List<ToolkitDescriptor> getAutToolkits(
        IProjectPO project) throws ToolkitPluginException {
        
        CompSystem compSys = ComponentBuilder.getInstance().getCompSystem();
        final String projToolkit = project != null
                ? project.getToolkit() : null;
        if (projToolkit == null) {
            return compSys.getIndependentToolkitDescriptors(
                    ToolkitConstants.LEVEL_TOOLKIT);
        }
        final String level = ToolkitSupportBP.getToolkitLevel(projToolkit);
        if (ToolkitConstants.LEVEL_TOOLKIT.equals(level)) {
            final List<ToolkitDescriptor> toolkitList = 
                new ArrayList<ToolkitDescriptor>(1);
            toolkitList.add(compSys.getToolkitDescriptor(projToolkit));
            
            for (Object descObj 
                    : compSys.getIndependentToolkitDescriptors(
                            ToolkitConstants.LEVEL_TOOLKIT)) {
                
                ToolkitDescriptor desc = (ToolkitDescriptor)descObj;
                
                if (ToolkitUtils.doesToolkitInclude(
                        desc.getToolkitID(), projToolkit)) {
                    toolkitList.add(desc);
                }
            }
            
            return toolkitList;
        }

        return compSys.getIndependentToolkitDescriptors(
            ToolkitConstants.LEVEL_TOOLKIT);
    }
    
    
}
