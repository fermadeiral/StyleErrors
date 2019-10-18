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
package org.eclipse.jubula.client.ui.rcp.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jubula.client.archive.JsonStorage;
import org.eclipse.jubula.client.core.Activator;
import org.eclipse.jubula.client.core.businessprocess.INameMapper;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.businessprocess.ProjectCompNameCache;
import org.eclipse.jubula.client.core.businessprocess.ProjectNameBP;
import org.eclipse.jubula.client.core.businessprocess.db.TestSuiteBP;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMReadException;
import org.eclipse.jubula.client.core.persistence.PMSaveException;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.businessprocess.ImportFileBP;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.wizards.pages.ProjectSettingWizardPage;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.exception.JBVersionException;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.version.IVersion;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ToolkitDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 18.05.2005
 */
public class ProjectWizard extends Wizard implements INewWizard {
    
    /** the ID for the ProjectSettingWizardPage */
    private static final String PROJECT_SETTING_WP = 
        "org.eclipse.jubula.client.ui.rcp.wizards.pages.ProjectSettingWizardPage"; //$NON-NLS-1$
    
    /**
     * Prefix for unbound modules project names
     */
    private static final String LIBRARY_PREFIX = "unbound_modules_"; //$NON-NLS-1$
    
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(ProjectWizard.class);
    
    /** the new autMain of the new project */
    private IAUTMainPO m_autMain;
    /** the new autConfig of the new project */
    private IAUTConfigPO m_autConfig;
    /** dialog to get the new project name from */
    private ProjectSettingWizardPage m_projectSettingWizardPage;

    /**
     * @return the wizard page for the project settings
     */
    public ProjectSettingWizardPage getProjectSettingWizardPage() {
        return m_projectSettingWizardPage;
    }
    
    /**
     * {@inheritDoc}
     *      org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle(Messages.ProjectWizardNewProjectWizard);
        setDefaultPageImageDescriptor(IconConstants
                .PROJECT_WIZARD_IMAGE_DESCRIPTOR);
        setNeedsProgressMonitor(true);    
        setHelpAvailable(true);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean performFinish() {
        final String name = m_projectSettingWizardPage.getNewProjectName();
        if (ProjectPM.doesProjectNameExist(name)) {
            ErrorHandlingUtil.createMessageDialog(
                MessageIDs.E_PROJECTNAME_ALREADY_EXISTS, 
                new Object[]{name}, null);
            return false;
        }
        try {
            PlatformUI.getWorkbench().getProgressService().run(false, false,
                new IRunnableWithProgress() {
                    public void run(IProgressMonitor monitor) 
                        throws InterruptedException {
                        monitor.beginTask(
                            NLS.bind(Messages.ProjectWizardCreatingProject,
                                    name),
                            IProgressMonitor.UNKNOWN);
                        try {
                            boolean needTemplate = m_projectSettingWizardPage
                                    .needProjectTamplet();
                            String projectToolkit = m_projectSettingWizardPage
                                    .getProjectToolkit();
                            createNewProject(name, projectToolkit, needTemplate,
                                    monitor);
                        } catch (ToolkitPluginException e) {
                            Plugin.getDefault().writeStatus(new Status(
                                    IStatus.ERROR, Activator.PLUGIN_ID,
                                    e.getMessage()));
                        } finally {
                            monitor.done();
                        }
                    }
                });
        } catch (InvocationTargetException ite) {
            // Exception occurred during operation
            log.error(ite.getLocalizedMessage(), ite.getCause());
        } catch (InterruptedException ie) {
            // Operation was canceled.
            // Do nothing.
        }
        return true;
    }
        
    /**
     * {@inheritDoc}
     */
    public boolean performCancel() {
        Plugin.stopLongRunning();
        ProjectNameBP.getInstance().clearCache();
        return true;
    }
    /**
     * Adds the pages of this wizard.
     */
    public void addPages() {
        Plugin.startLongRunning();
        final String emptystr = StringConstants.EMPTY;
        m_autMain = PoMaker.createAUTMainPO(emptystr);
        m_autConfig = PoMaker.createAUTConfigPO();
        m_autMain.addAutConfigToSet(m_autConfig);
        m_projectSettingWizardPage = new ProjectSettingWizardPage(
                PROJECT_SETTING_WP, m_autMain, m_autConfig);
        m_projectSettingWizardPage.setTitle(Messages
                .ProjectWizardProjectSettings);
        m_projectSettingWizardPage.setDescription(Messages
                .ProjectWizardNewProject);
        addPage(m_projectSettingWizardPage); 
        
        Plugin.stopLongRunning();
    }

    /**
     * Creates a new project, stops a started AUT, closes all opened editors.
     * @param newProjectName the name for this project
     * @param projectToolkit project toolkit
     * @param needTemplate need of template 
     * @param monitor The progress monitor for this potentially long-running 
     *                operation.
     * @throws InterruptedException if the operation is canceled.
     * @throws ToolkitPluginException 
     */
    private void createNewProject(final String newProjectName,
            final String projectToolkit, boolean needTemplate,
            IProgressMonitor monitor) throws InterruptedException,
            ToolkitPluginException {          
        
        Plugin.closeAllOpenedJubulaEditors(false);
        ParamNameBPDecorator paramNameMapper = 
                new ParamNameBPDecorator(ParamNameBP.getInstance());
        final IWritableComponentNameCache compNameCache =
            new ProjectCompNameCache(null);
        
        IProjectPO project = null;
        if (needTemplate) {
            try {
                URL templateUrl = ImportFileBP.getInstance()
                        .getProjectTemplateUrl();
                project = new JsonStorage().readProject(templateUrl,
                        paramNameMapper, compNameCache, true, true, monitor,
                        Plugin.getDefault());
            } catch (PMReadException | JBVersionException e) {
                e.printStackTrace();
            }
        } 
        if (project == null) {
            project = NodeMaker.createProjectPO(newProjectName,
                    IVersion.JB_CLIENT_METADATA_VERSION);
        }
        project.setToolkit(projectToolkit);
        if (m_autMain.getName() != null && !m_autMain.getName().isEmpty()
                && m_autMain.getToolkit() != null) {
            project.addAUTMain(m_autMain);
            for (ITestSuitePO ts : TestSuiteBP.getListOfTestSuites(project)) {
                ts.setAut(m_autMain);
            }
        }
        if (m_autConfig.getName() == null || m_autConfig.getName().isEmpty()) {
            m_autMain.removeAutConfig(m_autConfig);
        }
        addUnboundModules(project);
        List<INameMapper> mapperList = new ArrayList<INameMapper>();
        List<IWritableComponentNameCache> compNameCacheList = 
                new ArrayList<IWritableComponentNameCache>();
        mapperList.add(paramNameMapper);
        compNameCache.setContext(project);
        compNameCacheList.add(compNameCache);
        try {
            GeneralStorage.getInstance().reset();
            ProjectPM.attachProjectToROSession(project, newProjectName, 
                    mapperList, compNameCacheList, monitor);
        } catch (PMSaveException e) {
            PMExceptionHandler.handlePMExceptionForMasterSession(
                new PMSaveException(e.getMessage(), 
                    MessageIDs.E_CREATE_NEW_PROJECT_FAILED));
        } catch (PMException e) {
            PMExceptionHandler.handlePMExceptionForMasterSession(e);
        } catch (ProjectDeletedException e) {
            PMExceptionHandler.handleProjectDeletedException();
        } catch (InterruptedException ie) {
            throw ie;
        }
    }

    /**
     * Adds appropriate testcase libraries to the given project's reused 
     * projects set.
     * 
     * @param newProject The project that will reuse the testcase libraries.
     */
    private void addUnboundModules(IProjectPO newProject) {

        // Use toolkit-specific module, and modules for all required toolkits
        ToolkitDescriptor desc = 
            ComponentBuilder.getInstance().getCompSystem()
            .getToolkitDescriptor(newProject.getToolkit());

        while (desc != null) {
            try {
                String moduleName = LIBRARY_PREFIX + StringUtils.lowerCase(
                        desc.getName());
                IProjectPO ubmProject = 
                    ProjectPM.loadLatestVersionOfProjectByName(moduleName);
                if (ubmProject != null) {
                    IReusedProjectPO[] reusedProj = new IReusedProjectPO
                            [newProject.getUsedProjects().size()];
                    newProject.getUsedProjects().toArray(reusedProj);
                    for (int i = 0; i < reusedProj.length; i++) {
                        IReusedProjectPO oldReusedProject = reusedProj[i];
                        if (oldReusedProject != null
                                && oldReusedProject.getName()
                                        .equals(ubmProject.getName())
                                && oldReusedProject.getProjectVersion()
                                        .compareTo(ubmProject
                                                .getProjectVersion()) <= 0) {
                            newProject.removeUsedProject(oldReusedProject);
                        }
                    }
                    newProject.addUsedProject(
                            PoMaker.createReusedProjectPO(ubmProject));
                } else {
                    if (log.isInfoEnabled()) {
                        log.info(Messages.Project + StringConstants.SPACE
                            + StringConstants.APOSTROPHE + moduleName
                            + StringConstants.APOSTROPHE + Messages.DoesNotExist
                            + StringConstants.DOT);
                    }
                }
            } catch (JBException e) {
                log.error(e + StringConstants.COLON + StringConstants.SPACE 
                        + e.getMessage());
            }
            desc = ComponentBuilder.getInstance().getCompSystem()
                .getToolkitDescriptor(desc.getIncludes());
        }

    }

    /**
     * {@inheritDoc}
     */
    public boolean canFinish() {
        return getContainer().getCurrentPage().isPageComplete();
    }

    /**
     * @return the IAUTMainPO
     */
    public IAUTMainPO getAutMain() {
        return m_autMain;
    }    

}