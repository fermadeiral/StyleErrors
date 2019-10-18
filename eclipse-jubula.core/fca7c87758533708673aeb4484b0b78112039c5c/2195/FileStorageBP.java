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
package org.eclipse.jubula.client.archive.businessprocess;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.commons.lang.Validate;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jubula.client.archive.JsonStorage;
import org.eclipse.jubula.client.archive.errorhandling.IProjectNameConflictResolver;
import org.eclipse.jubula.client.archive.errorhandling.NullProjectNameConflictResolver;
import org.eclipse.jubula.client.archive.i18n.Messages;
import org.eclipse.jubula.client.core.Activator;
import org.eclipse.jubula.client.core.businessprocess.INameMapper;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.businessprocess.ProjectCompNameCache;
import org.eclipse.jubula.client.core.businessprocess.ProjectNameBP;
import org.eclipse.jubula.client.core.businessprocess.UsedToolkitBP;
import org.eclipse.jubula.client.core.businessprocess.db.TestSuiteBP;
import org.eclipse.jubula.client.core.businessprocess.progress.ProgressMonitorTracker;
import org.eclipse.jubula.client.core.errorhandling.ErrorMessagePresenter;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.ProjectVersion;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMReadException;
import org.eclipse.jubula.client.core.persistence.PMSaveException;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.core.progress.IProgressConsole;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.ConfigXmlException;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.exception.JBVersionException;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.version.IVersion;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created Jun 4, 2010
 */
public class FileStorageBP {
    
    /** Extension of JUB. It is an zip file which contain a project and a test result json file*/
    public static final String JUB = ".jub"; //$NON-NLS-1$

    /**
     * Reads XML files and parses them into related domain objects.
     * 
     * @author BREDEX GmbH
     * @created Jan 9, 2008
     */
    private static class ReadFilesOperation implements IRunnableWithProgress {
        /** 
         * mapping: projects to import => corresponding param name mapper 
         */
        private Map<IProjectPO, INameMapper> m_projectToMapperMap;
    
        /** 
         * mapping: projects to import => corresponding component name mapper 
         */
        private Map<IProjectPO, IWritableComponentNameCache> 
            m_projectToCompCacheMap;
    
        /** names of the files to read */
        private List<URL> m_fileURLs;
        
        /** the console to use to display progress and error messages */
        private IProgressConsole m_console;
        
        /**
         * Constructor
         * 
         * @param fileURLs
         *              URLs of the project files to read.
         * @param console
         *              The console to use to display progress and 
         *              error messages.
         */
        public ReadFilesOperation(
                List<URL> fileURLs, IProgressConsole console) {
            m_fileURLs = fileURLs;
            m_projectToMapperMap = 
                new LinkedHashMap<IProjectPO, INameMapper>();
            m_projectToCompCacheMap = 
                new LinkedHashMap<IProjectPO, 
                    IWritableComponentNameCache>();
            m_console = console;
        }
        
        /**
         * {@inheritDoc}
         */
        public void run(IProgressMonitor monitor) throws InterruptedException {
            if (m_fileURLs == null) {
                // Nothing to import. Just return.
                return;
            }
            SubMonitor subMonitor = SubMonitor.convert(monitor, Messages
                    .ImportFileBPReading, m_fileURLs.size());
            String lastFileName = StringConstants.EMPTY;
            try {
                showStartingImport(m_console);
                showStartingReadingProjects(m_console);
                for (URL fileURL : m_fileURLs) {
                    ParamNameBPDecorator paramNameMapper = 
                        new ParamNameBPDecorator(ParamNameBP.getInstance());
                    final IWritableComponentNameCache compNameCache =
                        new ProjectCompNameCache(null);
                    String fileName = fileURL.getFile();
                    lastFileName = fileName;
                    m_console.writeStatus(new Status(IStatus.INFO,
                            Activator.PLUGIN_ID, NLS.bind(Messages
                                    .ImportFileActionInfoStartingReadingProject,
                                    fileName)));
                    try {
                        IProjectPO proj = readProject(subMonitor, fileURL,
                                paramNameMapper, compNameCache, fileName);
                        if (proj == null) {
                            continue;
                        }
                        compNameCache.setContext(proj);
                        m_projectToMapperMap.put(proj, paramNameMapper);
                        m_projectToCompCacheMap.put(proj, compNameCache);
                    } catch (JBVersionException e) {
                        for (Object msg : e.getErrorMsgs()) {
                            m_console.writeStatus(new Status(IStatus.ERROR,
                                    Activator.PLUGIN_ID, (String)msg));
                        }
                        m_console.writeStatus(new Status(IStatus.ERROR,
                                Activator.PLUGIN_ID, NLS.bind(Messages.
                                        ImportFileActionErrorImportFailed,
                                        fileName)));
                    } catch (ToolkitPluginException e) {
                        m_console.writeStatus(new Status(IStatus.ERROR,
                                Activator.PLUGIN_ID, e.getMessage()));
                        handleUnsupportedToolkits(e.getMessage());
                    }
                }
                showFinishedReadingProjects(m_console);
            } catch (final PMReadException e) {
                m_console.writeStatus(new Status(IStatus.ERROR,
                        Activator.PLUGIN_ID, NLS.bind(Messages
                                .ImportFileActionErrorImportFailedProject,
                                lastFileName, StringConstants.TAB
                                        + Messages.InvalidImportFile)));
                handlePMReadException(e, m_fileURLs);
            } catch (final ConfigXmlException ce) {
                handleCapDataNotFound(ce);
            } finally {
                monitor.done();
            }
        }

        /**
         * 
         * @param subMonitor
         *            The progress monitor for this potentially long-running
         *            operation.
         * @param fileURL
         *            URL of the project file to read
         * @param paramNameMapper
         *            mapper to resolve param names
         * @param compNameCache
         *            cache to resolve component names
         * @param fileName
         *             name of the project file
         * @return the persisted object
         * @throws PMReadException
         *             in case of error
         * @throws JBVersionException
         *             in case of version conflict between used toolkits of
         *             imported project and the installed Toolkit Plugins
         * @throws InterruptedException
         *             if the operation was canceled.
         * @throws ToolkitPluginException
         *             in case of the toolkit of the project is not supported
         */
        private IProjectPO readProject(SubMonitor subMonitor, URL fileURL,
                ParamNameBPDecorator paramNameMapper,
                final IWritableComponentNameCache compNameCache,
                String fileName) throws PMReadException, JBVersionException,
                        InterruptedException, ToolkitPluginException {
            String fileExt = fileName.substring(
                    fileName.lastIndexOf(StringConstants.DOT),
                    fileName.length());
            IProjectPO proj = null;
            if (fileExt.equals(JUB)) {
                proj = new JsonStorage().readProject(fileURL, paramNameMapper,
                        compNameCache, false, false, subMonitor.newChild(1),
                        m_console);
            }
            return proj;
        }
    
        /**
         * 
         * @return the projects to import, as read from the project files.
         */
        public Map<IProjectPO, INameMapper> getProjectToMapperMap() {
            return m_projectToMapperMap;
        }
    
        /**
         * 
         * @return the mapping between projects to import and their 
         *         corresponding component name cache
         */
        public Map<IProjectPO, IWritableComponentNameCache> 
            getProjectToCompCacheMap() {
        
            return m_projectToCompCacheMap;
        }
    }

    /**
     * imports an entire project
     * 
     * @author BREDEX GmbH
     * 
     */
    private static class CompleteImportOperation 
            implements IRunnableWithProgress {
    
        /** mapping: projects to import => corresponding param name mapper */
        private Map<IProjectPO, INameMapper> m_projectToMapperMap;
    
        /** mapping: projects to import => corresponding comp name cache */
        private Map<IProjectPO, IWritableComponentNameCache> 
            m_projectToCompCacheMap;
    
        /** whether a refresh is required after import */
        private boolean m_isRefreshRequired = false;
        
        /** whether the import succeeded */
        private boolean m_wasImportSuccessful = false;

        /** the console to use for reporting progress and errors */
        private IProgressConsole m_console;
        
        /**
         * constructor 
         * 
         * @param projectToMapperMap
         *            Mapping from projects to import to corresponding param
         *            name mappers.
         * @param projectToCompCacheMap
         *            Mapping from projects to import to corresponding 
         *            component name caches.
         * @param console
         *              The console to use to display progress and 
         *              error messages.
         */
        public CompleteImportOperation(
                Map<IProjectPO, INameMapper> projectToMapperMap, 
                Map<IProjectPO, IWritableComponentNameCache> 
                projectToCompCacheMap, IProgressConsole console) {
    
            m_projectToMapperMap = projectToMapperMap;
            m_projectToCompCacheMap = projectToCompCacheMap;
            m_console = console;
        }
    
        /**
         * 
         * {@inheritDoc}
         */
        public void run(IProgressMonitor monitor) throws InterruptedException {
            SubMonitor subMonitor = SubMonitor.convert(
                    monitor, Messages.ImportFileBPImporting,
                    m_projectToMapperMap.size());
    
            if (checkImportProblems()) {
                return;
                
            }
            
            for (final IProjectPO proj : m_projectToMapperMap.keySet()) {
                if (subMonitor.isCanceled()) {
                    throw new InterruptedException();
                }
                String projectName = proj.getDisplayName();
                showStartingImport(m_console, projectName);
                try {
                    m_wasImportSuccessful = 
                        importProject(proj, subMonitor.newChild(1));
                    showFinishedImport(m_console, projectName);
                } catch (PMSaveException e) {
                    LOG.warn(Messages.ErrorWhileImportingProject, e);
                    JBException gde = new JBException(
                        e + StringConstants.SPACE + StringConstants.COLON 
                        + Messages.SaveOf + proj.getName() 
                        + StringConstants.SPACE + Messages.Failed,
                        MessageIDs.E_IMPORT_PROJECT_XML_FAILED);
                    showErrorDuringImport(m_console, projectName, gde);
                    ErrorMessagePresenter.getPresenter().showErrorMessage(
                            gde, new String [] {proj.getName()}, null);
                } catch (PMException pme) {
                    LOG.warn(Messages.ErrorWhileImportingProject, pme);
                    JBException gde = new JBException(
                        pme + Messages.ImportOf + proj.getName() 
                        + StringConstants.SPACE + Messages.Failed,
                        MessageIDs.E_IMPORT_PROJECT_XML_FAILED);
                    showErrorDuringImport(m_console, projectName, gde);
                    ErrorMessagePresenter.getPresenter().showErrorMessage(
                            gde, new String [] {proj.getName()}, null);
                } catch (ProjectDeletedException e) {
                    JBException gde = new JBException(
                        e + Messages.ImportOf + proj.getName() 
                        + StringConstants.SPACE + Messages.Failed,
                        MessageIDs.E_ALREADY_DELETED_PROJECT);
                    showErrorDuringImport(m_console, projectName, gde);
                    ErrorMessagePresenter.getPresenter().showErrorMessage(
                            gde, new String [] {proj.getName()}, null);
                }
            }
            showFinishedImport(m_console);
        }
    
        /**
         * Checks the list of projects to import for problems. Handles problems
         * by displaying an error message to the user.
         * 
         * @return <code>true</code> if a problem was found, meaning that the
         *         operation cannot complete successfully. Otherwise, 
         *         <code>false</code>.
         */
        private boolean checkImportProblems() {
            Map<String, String> guidToNameMap = new HashMap<String, String>();
            if (checkImportedProjects(guidToNameMap)) {
                return true;
            }
            
            EntityManager circularDependencyCheckSess = 
                Persistor.instance().openSession();
    
            // if a name/guid conflict occurs
            // then show error message(s) and cancel
            try {
                if (checkNameGuidConflict(guidToNameMap)) {
                    return true;
                }
    
                // check for reusable project problems (circular dependencies)
                if (checkCircularDependencies(circularDependencyCheckSess)) {
                    return true;
                }
            } catch (PMException pme) {
                ErrorMessagePresenter.getPresenter().showErrorMessage(
                    new JBException(
                        pme + Messages.ImportFailed,
                        MessageIDs.E_DATABASE_GENERAL), 
                    null, null);
                return true;
            } finally {
                Persistor.instance().dropSessionWithoutLockRelease(
                    circularDependencyCheckSess);
            }
            
            return false;
        }
    
        /**
         * @param circularDependencyCheckSess The session to use for getting
         *                                    projects from the database.
         * @return <code>true</code> if any circular dependencies are found.
         *         Otherwise <code>false</code>.
         */
        private boolean checkCircularDependencies(
                EntityManager circularDependencyCheckSess) {
            for (final IProjectPO proj : m_projectToMapperMap.keySet()) {
                Set<IProjectPO> checkedProjects = new HashSet<IProjectPO>();
                Set<IProjectPO> illegalProjects = new HashSet<IProjectPO>();
                illegalProjects.add(proj);
                
                Set<IProjectPO> projectsToCheck = new HashSet<IProjectPO>();
                
                for (IReusedProjectPO reused : proj.getUsedProjects()) {
                    IProjectPO reusedProject = null;
    
                    for (IProjectPO importedProject 
                            : m_projectToMapperMap.keySet()) {
                        
                        if (reused.getProjectGuid().equals(
                                importedProject.getGuid()) 
                            && reused.getProjectVersion().equals(
                                importedProject.getProjectVersion())) {
                            
                            reusedProject = importedProject;
                            break;
                        }
    
                    }
    
                    if (reusedProject == null) {
                        try {
                            reusedProject = ProjectPM.loadReusedProject(
                                    reused, circularDependencyCheckSess);
                        } catch (JBException e) {
                            // We can't detect circular dependencies from a
                            // project if we can't load it from the db.
                            // Report to the user that the error will
                            // cause the import to abort.
                            handleCircularDependency(m_console, proj.getName());
                            return true;
                        }
                    }
                    
                    if (reusedProject != null) {
                        projectsToCheck.add(reusedProject);
                    }
                }
                for (IProjectPO projToCheck : projectsToCheck) {
                    ProjectPM.findIllegalProjects(projToCheck, 
                        checkedProjects, illegalProjects, 
                        m_projectToMapperMap.keySet());
                }
                
                illegalProjects.remove(proj);
                
                if (!illegalProjects.isEmpty()) {
                    handleCircularDependency(m_console, proj.getName());
                    return true;
                }
            }
            
            return false;
        }
    
        /**
         * 
         * @return <code>true</code> if the import succeeded. Otherwise 
         *         <code>false</code>
         */
        public boolean wasImportSuccessful() {
            return m_wasImportSuccessful;
        }
    
        /**
         * @param proj
         *            The project to import.
         * @param monitor
         *            The progress monitor for this operation.
         * @return <code>true</code> if the project was successfully imported.
         *         Returns <code>false</code> if their were conflicts that
         *         prevented the project from being successfully imported.
         * @throws PMException
         *             in case of any db error
         * @throws ProjectDeletedException
         *             if project is already deleted
         * @throws InterruptedException
         *             if the operation was canceled
         */
        private boolean importProject(IProjectPO proj, 
            IProgressMonitor monitor) 
            throws PMException, ProjectDeletedException, 
            InterruptedException {
            
            // if (import.guid exists and guid->version == import.version)
            // then show error message and cancel
            if (projectExists(proj.getGuid(), proj.getMajorProjectVersion(),
                proj.getMinorProjectVersion(), proj.getMicroProjectVersion(),
                proj.getProjectVersionQualifier())) {
                String projectNameToImport = proj.getName();
                handleProjectExists(
                    m_console,
                    ProjectNameBP.getInstance().getName(proj.getGuid(), false),
                    projectNameToImport,
                    proj.getProjectVersion());
                return false;
            }
            String selectedProjectName = 
                checkProjectAndRename(proj.getGuid(), proj.getName());
            if (selectedProjectName != null) {
                // Import project
                proj.setClientMetaDataVersion(
                    IVersion.JB_CLIENT_METADATA_VERSION);
                boolean willRequireRefresh = false;
                IProjectPO currentProject = 
                    GeneralStorage.getInstance().getProject();
                if (currentProject != null) {
                    for (IReusedProjectPO reused 
                        : currentProject.getUsedProjects()) {
    
                        if (m_isRefreshRequired || willRequireRefresh) {
                            break;
                        }
                        String guid = reused.getProjectGuid();
                        willRequireRefresh = proj.getGuid().equals(guid) 
                            && proj.getProjectVersion().equals(
                                reused.getProjectVersion());
    
                    }
                    m_isRefreshRequired = willRequireRefresh 
                        || m_isRefreshRequired;
                }
    
                monitor.beginTask(StringConstants.EMPTY, getTotalWork(proj));
                monitor.subTask(Messages.ImportFileBPSaveToDB);
                // Register Persistence (JPA / EclipseLink) progress listeners
                ProgressMonitorTracker tracker = 
                        ProgressMonitorTracker.SINGLETON;
                tracker.setProgressMonitor(monitor);
                IWritableComponentNameCache compNameCache = 
                    m_projectToCompCacheMap.get(proj);
                try {
                    ProjectPM.saveProject(proj, selectedProjectName, 
                            m_projectToMapperMap.get(proj), compNameCache);
                } finally {
                    // Remove JPA progress listeners
                    tracker.setProgressMonitor(null);
                }
                UsedToolkitBP.getInstance().refreshToolkitInfo(proj);
                return true;
            }
            
            return false;
        }
    
        /**
         * 
         * @param proj The project for which to find the required work
         *             amount.
         * @return the amount of work required to save the given project to the
         *         database.
         */
        private int getTotalWork(IProjectPO proj) {
            
            // (project_node=1)
            int totalWork = 1;
            
            // (INodePO=1)
            for (ITestSuitePO testSuite 
                    : TestSuiteBP.getListOfTestSuites(proj)) {
                
                totalWork += getWorkForNode(testSuite);
            }
            for (INodePO spec : proj.getUnmodSpecList()) {
                
                totalWork += getWorkForNode(spec);
            }
            
            // 1 for each event type
            totalWork *= NUM_HBM_PROGRESS_EVENT_TYPES;
            return totalWork;
        }
    
        /**
         * Recursively determines the amount of work involved in saving the
         * given node to the database.
         * 
         * @param node The node for which to determine the amount of work.
         * @return the amount of work required to save the given node to the 
         *         database.
         */
        private int getWorkForNode(INodePO node) {
            int work = 1;
            if (!(node instanceof IExecTestCasePO)) {
                Iterator<INodePO> childIter = node.getNodeListIterator();
                while (childIter.hasNext()) {
                    work += getWorkForNode(childIter.next());
                }
            }
            
            if (node instanceof ISpecTestCasePO) {
                work += ((ISpecTestCasePO)node).getAllEventEventExecTC().size();
            }
            
            return work;
        }
        
        /**
         * @param guidToNameMap mapping from project guids to names
         * @return <code>true</code> if any name/guid conflicts are found.
         *         Otherwise <code>false</code>.
         */
        private boolean checkImportedProjects(
            Map<String, String> guidToNameMap) {
            
            for (IProjectPO proj : m_projectToMapperMap.keySet()) {
                final String projectName = proj.getName();
                final String guid = proj.getGuid();
                Validate.notNull(projectName, Messages.ImportWithoutName);
                Validate.notEmpty(projectName, Messages.ImportEmptyName);
    
                // Check for name/guid conflicts among the imported projects as
                // we go.
                if (isSameGuidOtherName(guidToNameMap, projectName, guid)) {
                    
                    // Same guid, different name
                    handleGuidConflict(projectName, guidToNameMap.get(guid));
                    return true;
                
                } else if (isOtherGuidSameName(guidToNameMap, projectName, 
                    guid)) {
                    
                    // Different guid, same name
                    handleNameConflict(projectName);
                    return true;
                } else {
                    guidToNameMap.put(guid, projectName);
                }
            }
            return false;
        }
    
        /**
         * Checks if the mapping contains an entry that conflicts with given
         * name and guid.
         * 
         * @param guidToNameMap The mapping to check against.
         * @param projectName The name to check.
         * @param guid The guid to check.
         * @return <code>true</code> if there exists an entry in the mapping
         *         such that name<code>.equals(projectName)</code> and 
         *         not guid<code>.equals(guid)</code>. Otherwise 
         *         <code>false</code>.
         */
        private boolean isOtherGuidSameName(Map<String, String> guidToNameMap, 
            final String projectName, final String guid) {
            
            return guidToNameMap.containsValue(projectName)
                && !projectName.equals(guidToNameMap.get(guid));
        }
    
        /**
         * Checks if the mapping contains an entry that conflicts with given
         * name and guid.
         * 
         * @param guidToNameMap The mapping to check against.
         * @param projectName The name to check.
         * @param guid The guid to check.
         * @return <code>true</code> if there exists an entry in the mapping
         *         such that not name<code>.equals(projectName)</code> and 
         *         guid<code>.equals(guid)</code>. Otherwise 
         *         <code>false</code>.
         */
        private boolean isSameGuidOtherName(
            Map<String, String> guidToNameMap, final String projectName, 
            final String guid) {
            
            return guidToNameMap.containsKey(guid) 
                    && !projectName.equals(guidToNameMap.get(guid));
        }
    
        /**
         * Creates an error dialog.
         * 
         * @param name The name that is causing the conflict.
         */
        private void handleNameConflict(String name) {
            ErrorMessagePresenter.getPresenter().showErrorMessage(
                MessageIDs.E_PROJ_NAME_CONFLICT, 
                new String [] {}, new String [] {name});
        }
    
        /**
         * Creates an error dialog.
         * 
         * @param console
         *              The console to use to display progress and 
         *              error messages.
         * @param name The name of the project that is causing the problem.
         */
        private void handleCircularDependency(
                IProgressConsole console, String name) {
            
            console.writeErrorLine(
                    NLS.bind(Messages.ErrorMessagePROJ_CIRC_DEPEND, name));
    
            ErrorMessagePresenter.getPresenter().showErrorMessage(
                    MessageIDs.E_PROJ_CIRC_DEPEND, new String [] {name}, null);
        }
    
        /**
         * Checks that the import will not create any name/GUID conflicts. If
         * a conflict would be caused, this method will attempt to rename the
         * project.
         * 
         * @param guid The guid of the project to check.
         * @param projectName The name of the project to check.
         * @return a name if the import will not cause conflicts
         *         (either because no conflicts existed or because the project
         *         was successfully renamed such that no more conflicts exist).
         *         Otherwise <code>null</code>.
         */
        private String checkProjectAndRename(
            String guid, final String projectName) {
            
            String selectedName = projectName;
            // if (import.guid exists and guid->name != import.name) 
            // then show error message and offer to rename project
            //   : options are guid->name and import.name
            String existingNameForGuid = 
                ProjectNameBP.getInstance().getName(guid);
            if (existingNameForGuid != null 
                && !existingNameForGuid.equals(projectName)) {
    
                if (ProjectPM.doesProjectNameExist(projectName)) {
                    ArrayList<String> possibleNames = new ArrayList<String>(1);
                    possibleNames.add(existingNameForGuid);
                    selectedName = 
                        projectNameConflictResolver.resolveNameConflict(
                                possibleNames);
                } else {
                    String [] possibleNames = new String [] {
                        existingNameForGuid, projectName};
                    selectedName = 
                        projectNameConflictResolver.resolveNameConflict(
                                Arrays.asList(possibleNames));
                }
            } else if (ProjectPM.doesProjectNameExist(projectName)
                && !projectName.equals(existingNameForGuid)) {
                // if (import.name exists and name->guid != import.guid)
                    // then show error message and offer to rename project

                ArrayList<String> possibleNames = new ArrayList<String>(1);
                possibleNames.add(existingNameForGuid);
                selectedName = 
                    projectNameConflictResolver.resolveNameConflict(
                            possibleNames);
            }
            return selectedName;
        }
    
        /**
         * Checks whether there are any name/guid conflicts between the given
         * project information and the projects currently existing in the 
         * database.
         * 
         * @param guidToNameMap mapping of imported project guids to names
         * @return <code>true</code> if the given project information contains
         *         any name/guid conflicts. Otherwise <code>false</code>.
         */
        private boolean checkNameGuidConflict(
            Map<String, String> guidToNameMap) throws PMException {
            
            Map<String, String> dbGuidToNameMap = 
                ProjectNameBP.getInstance().readAllProjectNamesFromDB();
            for (String guid : guidToNameMap.keySet()) {
                if (isOtherGuidSameName(dbGuidToNameMap, 
                    guidToNameMap.get(guid), guid)) {
                    
                    handleNameConflict(guidToNameMap.get(guid));
                    return true;
                } else if (isSameGuidOtherName(dbGuidToNameMap, guid, 
                    guidToNameMap.get(guid))) {
                    
                    handleGuidConflict(guidToNameMap.get(guid), 
                        dbGuidToNameMap.get(guid));
                    return true;
                }
            }
            return false;
        }
    
        /**
         * Displays an error dialog.
         * 
         * @param importName name of the imported proejct causing the guid conflict
         * @param existingName name of the existing project causing the guid conflict
         */
        private void handleGuidConflict(
            String importName, String existingName) {
            
            ErrorMessagePresenter.getPresenter().showErrorMessage(
                    MessageIDs.E_PROJ_GUID_CONFLICT, 
                    new String [0], new String [] {importName, existingName});
        }
        
        /**
         * Checks whether the currently imported project already exists in the
         * database.
         * 
         * @param guid
         *            GUID to check
         * @param majorNumber
         *            Major version number to check
         * @param minorNumber
         *            Minor version number to check
         * @param microNumber The micro version number to check
         * @param versionQualifier The version qualifier to check
         * @return <code>true</code> if another project with the same GUID and
         *         version number as the currently imported project already 
         *         exists in the database. Otherwise <code>false</code>.
         */
        private boolean projectExists(String guid, Integer majorNumber, 
            Integer minorNumber, Integer microNumber,
            String versionQualifier) {
            
            return ProjectPM.doesProjectVersionExist(guid, majorNumber, 
                minorNumber, microNumber, versionQualifier);
        }
    
        /**
         * Writes an error to the console.
         * 
         * @param console
         *              The console to use to display progress and 
         *              error messages.
         * @param existingName 
         *      Name of the project that already exists in the database
         * @param importName 
         *      Name of the project that is being imported
         * @param version the project version
         */
        private void handleProjectExists(IProgressConsole console, 
                String existingName, String importName,
                ProjectVersion version) {
            
            console.writeErrorLine(
                    NLS.bind(Messages.ErrorMessageIMPORT_PROJECT_FAILED,
                            importName));
            console.writeErrorLine(NLS.bind(
                    Messages.ErrorMessageIMPORT_PROJECT_FAILED_EXISTING,
                    new String[] { existingName, version.toString() }));
        }
    
    }

    /**
     * Performs a project import by starting either a complete import operation
     * or a partial import operation, as appropriate.
     * 
     * @author BREDEX GmbH
     * @created Jan 9, 2008
     */
    private static class ImportOperation implements IRunnableWithProgress {
        /** mapping: projects to import => corresponding name mappers */
        private Map<IProjectPO, INameMapper> m_projectToMapperMap;
    
        /** mapping: projects to import => corresponding comp name cache*/
        private Map<IProjectPO, IWritableComponentNameCache> 
            m_projectToCompCacheMap;

        /** the project to open immediately after import */
        private IProjectPO m_projectToOpen = null;

        /** the console used for reporting progress and errors */
        private IProgressConsole m_console;

        /** flag for whether to open a project immediately after import */
        private boolean m_isOpenProject;
        
        /**
         * Constructor
         * @param projectToMapperMap
         *            Mapping from projects to import to corresponding param
         *            name mappers.
         * @param projectToCompCacheMap
         *            Mapping from projects to import to corresponding 
         *            component name caches.
         * @param console
         *              The console to use to display progress and 
         *              error messages.
         * @param openProject
         *            Flag indicating whether the imported project should be 
         *            immediately opened after import.
         */
        public ImportOperation(Map<IProjectPO, 
                INameMapper> projectToMapperMap, 
                Map<IProjectPO, IWritableComponentNameCache> 
                projectToCompCacheMap, 
                IProgressConsole console, boolean openProject) {
            
            m_projectToMapperMap = projectToMapperMap;
            m_projectToCompCacheMap = projectToCompCacheMap;
            m_console = console;
            m_isOpenProject = openProject;
        }

        /**
         * 
         * @return the imported project to open, or <code>null</code> if no project should be opened.
         */
        public IProjectPO getProjectToOpen() {
            return m_projectToOpen;
        }
        
        /**
         * {@inheritDoc}
         */
        public void run(IProgressMonitor monitor) throws InterruptedException {
            try {
                // run() is used directly here rather than 
                // starting a new monitor. We want the operation to run 
                // within this monitor.
                NodePM.getInstance().setUseCache(true);
    
                CompleteImportOperation op = new CompleteImportOperation(
                        m_projectToMapperMap, m_projectToCompCacheMap, 
                        m_console);
                op.run(monitor);
                if (op.wasImportSuccessful() && m_isOpenProject) {
                    for (IProjectPO project 
                            : m_projectToMapperMap.keySet()) {
                        
                        m_projectToOpen = project;
                        break;
                    }
                }
            } catch (final ConfigXmlException ce) {
                handleCapDataNotFound(ce);
            } finally {
                NodePM.getInstance().setUseCache(false);
                monitor.done();
            }
        }
    }

    /** the logger */
    public static final Logger LOG = 
        LoggerFactory.getLogger(FileStorageBP.class);
    
    /** the total amount of work for an import operation */
    private static final int TOTAL_IMPORT_WORK = 100;

    /** number of Persistence (JPA / EclipseLink) event types with progress listeners */
    // Event types:
    // save, recreateCollection, postInsert, postUpdate
    private static final int NUM_HBM_PROGRESS_EVENT_TYPES = 4;
    
    /** the amount of work required to read and parse xml files into related domain objects */
    private static final int PARSE_FILES_WORK = 95;
    
    /** the amount of work required to save and commit domain objects to the db */
    private static final int SAVE_TO_DB_WORK = 
        TOTAL_IMPORT_WORK - PARSE_FILES_WORK;

    /** 
     * responsible for resolving project name conflicts that occur 
     * during import 
     */
    private static IProjectNameConflictResolver projectNameConflictResolver =
        new NullProjectNameConflictResolver();
    
    /**
     * Private constructor for utility class.
     */
    private FileStorageBP() {
        // Nothing to initialize
    }
    
    /**
     * @param projectList The list of projects to export
     * @param exportDirName The export directory of the projects
     * @param exportSession The session to be used for Persistence (JPA / EclipseLink)
     * @param monitor The progress monitor
     * @param writeToSystemTempDir Indicates whether the projects have to be 
     *                             written to the system temp directory
     * @param listOfProjectFiles The written project files are added to this 
     *                           list, if the temp dir was used and the list  
     *                           is not null.
     * @param console
     *              The console to use to display progress and 
     *              error messages.
     */
    public static void exportProjectList(List<IProjectPO> projectList, 
            String exportDirName, EntityManager exportSession, 
            IProgressMonitor monitor, boolean writeToSystemTempDir, 
            List<File> listOfProjectFiles, IProgressConsole console) 
        throws JBException, InterruptedException {

        SubMonitor subMonitor = SubMonitor.convert(monitor, 
                Messages.ExportAllBPExporting,
                JsonStorage.getWorkToSave(projectList));
        
        for (IProjectPO proj : projectList) {
            if (subMonitor.isCanceled()) {
                throw new InterruptedException();
            }
            IProjectPO projectToExport = 
                ProjectPM.loadProjectByIdAndPreLoad(
                    proj.getId(), exportSession);
            String projectFileName = projectToExport.getDisplayName() + JUB;
            final String exportFileName;
            
            if (writeToSystemTempDir) {
                exportFileName = projectFileName;
            } else {
                if (projectToExport.equals(
                    GeneralStorage.getInstance().getProject())) {
                    
                    // project is current project
                    projectToExport = 
                        GeneralStorage.getInstance().getProject();
                }
                
                exportFileName = exportDirName + projectFileName;
            }
         
            if (subMonitor.isCanceled()) {
                throw new InterruptedException();
            }
            console.writeStatus(new Status(IStatus.INFO, Activator.PLUGIN_ID,
                    NLS.bind(Messages.ExportAllBPInfoStartingExportProject, 
                            projectFileName)));
            try {
                if (subMonitor.isCanceled()) {
                    throw new InterruptedException();
                }

                JsonStorage.save(projectToExport, exportFileName,
                        true, subMonitor.newChild(1), console);
                
                if (subMonitor.isCanceled()) {
                    throw new InterruptedException();
                }                

                console.writeStatus(new Status(IStatus.INFO,
                        Activator.PLUGIN_ID,
                        NLS.bind(Messages.ExportAllBPInfoFinishedExportProject, 
                                projectFileName)));
                
            } catch (final PMSaveException e) {
                LOG.error(Messages.CouldNotExportProject, e);
                console.writeStatus(new Status(IStatus.INFO,
                        Activator.PLUGIN_ID,
                        NLS.bind(Messages.ExportAllBPErrorExportFailedProject,
                                new Object [] {projectFileName,
                                        e.getMessage()})));
            }
            exportSession.detach(projectToExport);
        }
        
    }

    /** allow importing some files
     * 
     * @param importProjectURLs list of file URLs. Each URL must be valid.
     * @param monitor The progress monitor for the operation.
     * @param console
     *              The console to use to display progress and 
     *              error messages.
     * @param openProject
     *            Flag indicating whether the imported project should be 
     *            immediately opened after import.
     */
    public static void importFiles(List<URL> importProjectURLs, 
            IProgressMonitor monitor, IProgressConsole console, 
            boolean openProject) {
        // import all data from projects
        try {
            doImport(importProjectURLs, 
                    SubMonitor.convert(monitor), console, openProject);
        } catch (InterruptedException e) {
            // Operation was canceled. Do nothing.
        }
    }
    
    /**
     * Imports a chosen project from a file.
     * @param fileURLs
     *            The URLs of the files to import.
     * @param monitor 
     *            The progress monitor for the operation.
     * @param console
     *              The console to use to display progress and 
     *              error messages.
     * @param openProject
     *            Flag indicating whether the imported project should be 
     *            immediately opened after import.
     * 
     * @return the project to open immediately after import, or 
     *         <code>null</code> if no project should be opened.
     * @throws InterruptedException if the operation was canceled or the thread
     *                              was interrupted.
     */
    public static IProjectPO importProject(final List<URL> fileURLs,
            IProgressMonitor monitor, IProgressConsole console,
            boolean openProject)
        throws InterruptedException {

        SubMonitor subMonitor = SubMonitor.convert(monitor,
                Messages.ImportFileBPImporting, TOTAL_IMPORT_WORK);
        return doImport(fileURLs, subMonitor, console, openProject);
    }

    /**
     * actually do the import work. Separated to only batch calls
     * @param fileURLs
     *            The URLs of the files to import.
     * @param subMonitor @see #importProject(int)
     * @param console
     *              The console to use to display progress and 
     *              error messages.
     * @param openProject
     *            Flag indicating whether the imported project should be 
     *            immediately opened after import.
     * @return the project to open immediately after import, or 
     *         <code>null</code> if no project should be opened.
     * @throws InterruptedException @see #importProject(int)
     */
    private static IProjectPO doImport(List<URL> fileURLs,
            SubMonitor subMonitor, IProgressConsole console,
            boolean openProject) 
        throws InterruptedException {
        
        // Read project files
        ReadFilesOperation readFilesOp = 
            new ReadFilesOperation(fileURLs, console);
        readFilesOp.run(subMonitor.newChild(PARSE_FILES_WORK));

        // Import projects
        ImportOperation importOp = new ImportOperation(
            readFilesOp.getProjectToMapperMap(), 
            readFilesOp.getProjectToCompCacheMap(), 
            console, openProject);
        
        importOp.run(subMonitor.newChild(SAVE_TO_DB_WORK));
        return importOp.getProjectToOpen();
    }

    /**
     * Report to the user that an error occurred while importing the project.
     * 
     * @param console
     *              The console to use to display progress and 
     *              error messages.
     * @param projectFileName The filename of the project that was being 
     *                        imported.
     * @param e The error that occurred.
     */
    private static void showErrorDuringImport(IProgressConsole console, 
            String projectFileName, Exception e) {
        
        console.writeErrorLine(
                NLS.bind(Messages.ImportFileActionErrorImportFailedProject, 
                        new Object [] {projectFileName, e.getMessage()}));
    }

    /**
     * Report to the user that all projects have been imported.
     * 
     * @param console
     *              The console to use to display progress and 
     *              error messages.
     */
    private static void showFinishedImport(IProgressConsole console) {
        console.writeLine(
            Messages.ImportFileActionInfoFinishedImport);
    }

    /**
     * Report to the user that the project has been imported.
     * 
     * @param console
     *              The console to use to display progress and 
     *              error messages.
     * @param projectFileName The filename of the imported project.
     */
    private static void showFinishedImport(IProgressConsole console, 
            String projectFileName) {
        console.writeLine(
                NLS.bind(Messages.ImportFileActionInfoFinishedImportProject, 
                        projectFileName));
    }

    /**
     * Report to the user that all projects to import have been analyzed.
     * 
     * @param console
     *              The console to use to display progress and 
     *              error messages.
     */
    private static void showFinishedReadingProjects(IProgressConsole console) {
        console.writeLine(
            Messages.ImportFileActionInfoFinishedReadingProjects);
    }

    /**
     * Report to the user that the import process is beginning.
     * 
     * @param console
     *              The console to use to display progress and 
     *              error messages.
     */
    private static void showStartingImport(IProgressConsole console) {
        console.writeLine(
                Messages.ImportFileActionInfoStartingImport);
    }

    /**
     * Report to the user that the project is being imported.
     * 
     * @param console
     *              The console to use to display progress and 
     *              error messages.
     * @param projectFileName The filename of the imported project.
     */
    private static void showStartingImport(IProgressConsole console, 
            String projectFileName) {
        console.writeLine(
                NLS.bind(Messages.ImportFileActionInfoStartingImportProject,
                        projectFileName));
    }

    /**
     * Report to the user that all projects to import will be analyzed.
     * 
     * @param console
     *              The console to use to display progress and 
     *              error messages.
     */
    private static void showStartingReadingProjects(IProgressConsole console) {
        console.writeLine(
                Messages.ImportFileActionInfoStartingReadingProjects);
    }

    /**
     * 
     * @param resolver The new conflict resolver.
     */
    public static void setProjectNameConflictResolver(
            IProjectNameConflictResolver resolver) {
        
        Validate.notNull(resolver);
        projectNameConflictResolver = resolver;
    }

    /**
     * @param e PMReadException
     * @param fileURLs The URLs of the files that were being imported.
     */
    private static void handlePMReadException(final PMReadException e, 
            final List<URL> fileURLs) {
        ErrorMessagePresenter.getPresenter().showErrorMessage(
                new JBException(e + Messages.Reading + fileURLs.toArray()
                        + Messages.Failed,
                        MessageIDs.E_IMPORT_XML_FAILED), null,
                MessageIDs.getMessageObject(e.getErrorId()).getDetails());
    }

    /**
     * Create an appropriate error dialog.
     * 
     * @param ce The exception that prevented the import of the 
     *           project.
     */
    private static void handleCapDataNotFound(final ConfigXmlException ce) {

        ErrorMessagePresenter.getPresenter().showErrorMessage(
                MessageIDs.E_IMPORT_PROJECT_CONFIG_CONFLICT, 
                null, new String[] {ce.getMessage()});
    }
    
    /**
     * Create an error dialog 
     * @param errorMessage msg to show
     */
    private static void handleUnsupportedToolkits(String errorMessage) {
        ErrorMessagePresenter.getPresenter().showErrorMessage(
                MessageIDs.E_UNSUPPORTED_TOOLKIT, 
                null, new String[] {errorMessage});
    }
}