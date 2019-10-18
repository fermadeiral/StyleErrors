/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.api.converter.ui.handlers;

import static org.eclipse.jubula.client.api.converter.utils.Utils.EXEC_PATH;
import static org.eclipse.jubula.client.api.converter.utils.Utils.SPEC_PATH;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.api.converter.CTDSGenerator;
import org.eclipse.jubula.client.api.converter.CTDSInfo;
import org.eclipse.jubula.client.api.converter.NodeGenerator;
import org.eclipse.jubula.client.api.converter.NodeInfo;
import org.eclipse.jubula.client.api.converter.exceptions.InvalidNodeNameException;
import org.eclipse.jubula.client.api.converter.exceptions.MinorConversionException;
import org.eclipse.jubula.client.api.converter.exceptions.StopConversionException;
import org.eclipse.jubula.client.api.converter.ui.i18n.Messages;
import org.eclipse.jubula.client.api.converter.utils.Utils;
import org.eclipse.jubula.client.core.errorhandling.ErrorMessagePresenter;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 * @created 24.10.2014
 */
public class ConvertProjectHandler extends AbstractHandler {

    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(ConvertProjectHandler.class);
    
    /** target path of conversion */
    private static String genPath;
    
    /** target package name space */
    private static String genPackage;
    
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        DirectoryDialog directoryDialog = createDirectoryDialog();
        genPath = directoryDialog.open();
        if (genPath != null) {
            org.eclipse.jubula.client.ui.rcp.utils.Utils.storeLastDirPath(
                    directoryDialog.getFilterPath());
            File directory = new File(genPath);
            if (directory.list().length == 0) {
                InputDialog inputDialog = new InputDialog(getActiveShell(),
                        Messages.InputDialogName, Messages.InputDialogMessage,
                        StringConstants.EMPTY, new PackageNameValidator());
                if (inputDialog.open() == Window.OK) {
                    genPackage = inputDialog.getValue();
                    IWorkbench workbench = PlatformUI.getWorkbench();
                    try {
                        workbench.getProgressService().run(true, true,
                                new ConvertProjectOperation());
                    } catch (InvocationTargetException
                            | InterruptedException e) {
                        LOG.error(Messages.ErrorWhileConverting, e);
                    }
                }
            } else {
                ErrorHandlingUtil.createMessageDialog(
                        MessageIDs.E_NON_EMPTY_DIRECTORY);
            }
        }
        return null;
    }

    /**
     * @return a directory dialog
     */
    private DirectoryDialog createDirectoryDialog() {
        DirectoryDialog directoryDialog = 
                new DirectoryDialog(getActiveShell(), SWT.SAVE);
        String filterPath =
                org.eclipse.jubula.client.ui.rcp.utils.Utils.getLastDirPath();
        directoryDialog.setFilterPath(filterPath);
        return directoryDialog;
    }
    
    /**
     * @created 24.10.2014
     */
    private static class ConvertProjectOperation implements
        IRunnableWithProgress {

        /** the project */
        private static IProgressMonitor progressMonitor;
        
        /** the default toolkit */
        private static String defaultToolkit;
        
        /** maps a UUID from a test case/suite/job to the name of its
         * corresponding node info for generation */
        private static Map<String, NodeInfo> uuidToNodeInfoMap;
        
        /** set of projects to convert */
        private static Set<IProjectPO> projects;

        /** counting absolute work units */
        private static int workUnits;
        
        /** {@inheritDoc} */
        public void run(IProgressMonitor monitor) {
            progressMonitor = monitor;
            IProjectPO project = GeneralStorage.getInstance().getProject();
            progressMonitor.setTaskName(
                    Messages.PreparingConvertProjectTaskName);
            String basePath = genPath + StringConstants.SLASH
                    + genPackage.replace(StringConstants.DOT, 
                            StringConstants.SLASH);
            uuidToNodeInfoMap = new HashMap<String, NodeInfo>();
            NodeInfo.setUuidToNodeInfoMap(uuidToNodeInfoMap);
            projects = new HashSet<IProjectPO>();
            workUnits = 0;
            
            if (project != null) {
                defaultToolkit = determineDefaultToolkit(project);
                addProjectsToConvert(project);
                
                try {
                    for (IProjectPO p : projects) {
                        determineClassNamesForProject(p, basePath);
                    }
                    progressMonitor.beginTask(
                            Messages.PreparingConvertProjectTaskName,
                            workUnits);
                    for (IProjectPO p : projects) {
                        progressMonitor.setTaskName(NLS.bind(
                                Messages.ConvertProjectTaskName, p.getName()));
                        handleProject(p, basePath);
                    }
                } catch (StopConversionException e) {
                    progressMonitor.setCanceled(true);
                    if (!e.wasManuallyTriggered()) {
                        ErrorHandlingUtil.createMessageDialog(
                                new JBException(e.getMessage(), e,
                                        MessageIDs.E_CONVERSION_ABORTED_ERROR));
                    }
                    return;
                }
            }
            progressMonitor.done();
        }

        /**
         * Adds a project and its reused projects to the set of projects to convert
         * @param project the project
         */
        private void addProjectsToConvert(IProjectPO project) {
            projects.add(project);
            Iterator iterator = project.getUsedProjects().iterator();
            while (iterator.hasNext()) {
                IReusedProjectPO reusedProject =
                        (IReusedProjectPO) iterator.next();
                IProjectPO usedProject;
                try {
                    usedProject = ProjectPM
                        .loadReusedProjectInMasterSession(reusedProject);
                    addProjectsToConvert(usedProject);
                } catch (JBException e) {
                    ErrorHandlingUtil.createMessageDialog(
                            new JBException(e.getMessage(), e,
                                    MessageIDs.E_LOAD_PROJECT));
                }
            }
        }

        /**
         * Maps for all nodes from a project their UUIDs to the name 
         * of its corresponding class to generate 
         * @param project the project
         * @param basePath the base path
         * @throws StopConversionException 
         */
        private void determineClassNamesForProject(IProjectPO project,
                String basePath) throws StopConversionException {
            String projectName = StringConstants.EMPTY;
            try {
                projectName = Utils.translateToPackageName(project);
            } catch (InvalidNodeNameException e) {
                displayErrorForInvalidName(project);
                throw new StopConversionException();
            }
            String projectPath = basePath + StringConstants.SLASH + projectName;
            for (INodePO node : project.getUnmodExecList()) {
                if (progressMonitor.isCanceled()) {
                    throw new StopConversionException();
                }
                String path = projectPath + StringConstants.SLASH + EXEC_PATH;
                determineClassNamesForNode(node, path);
            }
            for (INodePO node : project.getUnmodSpecList()) {
                if (progressMonitor.isCanceled()) {
                    throw new StopConversionException();
                }
                String path = projectPath + StringConstants.SLASH + SPEC_PATH;
                determineClassNamesForNode(node, path);
            }
        }

        /**
         * Maps a node's UUID to the name of its corresponding class to generate 
         * @param node the node
         * @param basePath the base path
         * @throws StopConversionException 
         */
        private void determineClassNamesForNode(INodePO node, String basePath)
                throws StopConversionException {
            workUnits++;
            if (node instanceof ICategoryPO) {
                ICategoryPO category = (ICategoryPO) node;
                String path = StringConstants.EMPTY;
                try {
                    path = basePath + StringConstants.SLASH
                            + Utils.translateToPackageName(category);
                } catch (InvalidNodeNameException e) {
                    displayErrorForInvalidName(category);
                    throw new StopConversionException();
                }
                for (NodeInfo nodeInfo : uuidToNodeInfoMap.values()) {
                    if (nodeInfo.getFqFileName().equals(path)) {
                        displayErrorForDuplicate(node);
                        throw new StopConversionException();
                    }
                }
                NodeInfo nodeInfo = new NodeInfo(path, node,
                        genPackage, defaultToolkit);
                uuidToNodeInfoMap.put(node.getGuid(), nodeInfo);
                for (INodePO child : node.getUnmodifiableNodeList()) {
                    determineClassNamesForNode(child, path);
                }
            } else {
                String className = StringConstants.EMPTY;
                try {
                    className = Utils.determineClassName(node);
                } catch (InvalidNodeNameException e) {
                    displayErrorForInvalidName(node);
                    throw new StopConversionException();
                }
                String fileName = basePath + StringConstants.SLASH
                        + className + ".java"; //$NON-NLS-1$
                for (NodeInfo nodeInfo : uuidToNodeInfoMap.values()) {
                    if (nodeInfo.getFqFileName().equals(fileName)) {
                        Plugin.getDefault().writeErrorLineToConsole(
                                "Duplicate filename error:" + fileName, true); //$NON-NLS-1$
                    }
                }
                NodeInfo nodeInfo = new NodeInfo(fileName, node,
                        genPackage, defaultToolkit);
                uuidToNodeInfoMap.put(node.getGuid(), nodeInfo);
            }
        }
        
        /**
         * Returns the default toolkit for a project
         * by inspecting its first AUT
         * @param project the project
         * @return the name of the default toolkit
         */
        private String determineDefaultToolkit(IProjectPO project) {
            String toolkit = null;
            IAUTMainPO firstAUT = null;
            try {
                firstAUT = project.getAutCont()
                        .getAutMainList().iterator().next();
            } catch (NoSuchElementException e) {
                ErrorMessagePresenter.getPresenter().showErrorMessage(
                        new JBException(
                            Messages.NoAutInProject, 
                            MessageIDs.E_NO_AUT_IN_PROJECT),
                        null, null);
                progressMonitor.setCanceled(true);
            }
            if (firstAUT != null) {
                toolkit = firstAUT.getToolkit();
            }
            if (toolkit.equals(CommandConstants.RCP_TOOLKIT)) {
                toolkit = CommandConstants.SWT_TOOLKIT;
            }
            return toolkit;
        }

        /**
         * Traverses a project and creates files and directories for its content.
         * @param project the project
         * @param basePath the base path
         * @throws StopConversionException 
         */
        private void handleProject(IProjectPO project, String basePath)
                throws StopConversionException {
            createCentralTestDataClass(project, basePath);
            for (INodePO node : project.getUnmodExecList()) {
                handleNode(node);
            }
            for (INodePO node : project.getUnmodSpecList()) {
                handleNode(node);
            }
        }
        
        /**
         * Handles the conversion for a node.
         * @param node the test case
         * @throws StopConversionException 
         */
        private void handleNode(INodePO node) throws StopConversionException {
            if (progressMonitor.isCanceled()) {
                throw new StopConversionException(true);
            }
            progressMonitor.worked(1);
            NodeInfo info = uuidToNodeInfoMap.get(node.getGuid());
            File file = new File(info.getFqFileName());
            if (node instanceof ICategoryPO) {
                file.mkdirs();
                for (INodePO child : node.getUnmodifiableNodeList()) {
                    handleNode(child);
                }
            } else {
                try {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                    NodeGenerator gen = new NodeGenerator();
                    try {
                        String content = gen.generate(info);
                        writeContentToFile(file, content);
                    } catch (MinorConversionException e) {
                        Plugin.getDefault().writeLineToConsole(
                                NLS.bind(Messages.InvalidNode,
                                        new String[] { node.getName(),
                                                       e.getMessage() }),
                                true);
                        file.delete();
                    }
                } catch (IOException e) {
                    ErrorHandlingUtil.createMessageDialog(
                            new JBException(e.getMessage(), e,
                                    MessageIDs.E_FILE_NO_PERMISSION));
                    throw new StopConversionException();
                }
            }
        }
        
        /**
         * Writes a string into a given file.
         * @param file the file
         * @param content the content
         */
        private void writeContentToFile(File file, String content)
            throws IOException {
            FileOutputStream fop = new FileOutputStream(file);
            byte[] contentInBytes = content.getBytes();
            IOUtils.write(contentInBytes, fop);
        }

        /**
         * Displays an error message in the case that a node occurs multiple times
         * @param node the duplicate node
         */
        private void displayErrorForDuplicate(INodePO node) {
            String fqNodeName = Utils.getFullyQualifiedName(node);
            ErrorMessagePresenter.getPresenter().showErrorMessage(
                    new JBException(
                        NLS.bind(Messages.DuplicateNode, 
                            new String[] {fqNodeName}), 
                        MessageIDs.E_DUPLICATE_NODE),
                    new String [] {fqNodeName},
                    null);
            progressMonitor.setCanceled(true);
        }

        /**
         * Displays an error for the case of an invalid node name
         * @param node the node
         */
        private void displayErrorForInvalidName(INodePO node) {
            String fqNodeName = Utils.getFullyQualifiedName(node);
            ErrorMessagePresenter.getPresenter().showErrorMessage(
                    new JBException(
                        NLS.bind(Messages.InvalidNodeName, 
                            new String[] {fqNodeName}), 
                        MessageIDs.E_INVALID_NODE_NAME),
                    new String [] {fqNodeName},
                    null);
            progressMonitor.setCanceled(true);
        }
        
        /**
         * Creates a central test data file for a project
         * @param project the project
         * @param basePath the base path
         */
        private void createCentralTestDataClass(IProjectPO project,
                String basePath) {
            String projectName = StringConstants.EMPTY;
            try {
                projectName = Utils.translateToPackageName(project);
            } catch (InvalidNodeNameException e) {
                displayErrorForInvalidName(project);
            }
            String className = "CTDS.java"; //$NON-NLS-1$
            String projectPath = basePath + StringConstants.SLASH + projectName;
            File projectDir = new File(projectPath);
            projectDir.mkdirs();
            String fileName = projectPath + StringConstants.SLASH + className;
            File file = new File(fileName);
            try {
                file.createNewFile();
                CTDSGenerator gen = new CTDSGenerator();
                CTDSInfo info = new CTDSInfo(className,
                        project, genPackage);
                String content = gen.generate(info);
                writeContentToFile(file, content);
            } catch (IOException e) {
                ErrorHandlingUtil.createMessageDialog(
                        new JBException(e.getMessage(), e,
                                MessageIDs.E_FILE_NO_PERMISSION));
            }
        }
    }
    
    /**
     * @created 27.10.2014
     */
    private static class PackageNameValidator implements IInputValidator {
        
        /** {@inheritDoc} */
        public String isValid(String newText) {
            if (newText.isEmpty()) {
                return Messages.NoPackageNameSpecified;
            }
            Pattern p = Pattern.compile(
                    "^[a-zA-Z_][\\w]*(\\.[a-zA-Z_][\\w]*)*$"); //$NON-NLS-1$
            if (!p.matcher(newText).matches()) {
                return Messages.InvalidPackageName;
            }
            return null;
        }
    }
    
}