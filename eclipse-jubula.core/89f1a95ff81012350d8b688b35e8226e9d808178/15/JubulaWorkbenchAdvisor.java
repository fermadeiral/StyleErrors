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
package org.eclipse.jubula.app.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ConcurrentModificationException;

import javax.persistence.PersistenceException;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeySequenceText;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jubula.app.i18n.Messages;
import org.eclipse.jubula.client.core.businessprocess.progress.OperationCanceledUtil;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.Plugin.ClientStatus;
import org.eclipse.jubula.client.ui.rcp.businessprocess.JBNavigationHistory;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.JBFatalException;
import org.eclipse.jubula.tools.internal.exception.JBRuntimeException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.console.ConsoleView;
import org.eclipse.ui.internal.ide.IDEInternalWorkbenchImages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 *  
 */
public class JubulaWorkbenchAdvisor extends WorkbenchAdvisor {
    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(JubulaWorkbenchAdvisor.class);

    /** {@link ILogListener} for Platform to handle errors
     * see {@link Plugin#handleError(Throwable)} 
     */
    private ILogListener m_runtimeLogger = new ILogListener() {
        public void logging(IStatus status, String pluginId) {
            if (status.getException() instanceof RuntimeException) {
                Plugin.getDefault().handleError(status.getException());
            }
        }
    };

    /**
     * Constructs a new <code>JubulaWorkbenchAdvisor</code>.
     */
    public JubulaWorkbenchAdvisor() {
        // do nothing
    }

    /**
     * @return String Perspective
     */
    public String getInitialWindowPerspectiveId() {
        return Constants.SPEC_PERSPECTIVE;
    }

    /**
     * @param configurer
     *            IWorkbenchConfigurer
     */
    public void initialize(IWorkbenchConfigurer configurer) {
        super.initialize(configurer);
        configurer.setSaveAndRestore(true);
        
        // To get the icons for projects correctly registered
        final String iconsPath = "icons/full/"; //$NON-NLS-1$
        final String pathObject = iconsPath + "obj16/"; //$NON-NLS-1$
        final String problemsViewPath = iconsPath + "etool16/"; //$NON-NLS-1$
        final String elclPath = iconsPath + "elcl16/"; //$NON-NLS-1$
        final String dlclPath = iconsPath + "dlcl16/"; //$NON-NLS-1$
        final String wizbanPath = iconsPath + "wizban/"; //Wizard icons //$NON-NLS-1$
        
        Bundle ideBundle = Platform.getBundle(IDEWorkbenchPlugin.IDE_WORKBENCH);
        declareWorkbenchImage(configurer, ideBundle, 
                IDE.SharedImages.IMG_OBJ_PROJECT, 
                pathObject + "prj_obj.png", true); //$NON-NLS-1$
        declareWorkbenchImage(configurer, ideBundle, 
                IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED,
                pathObject + "cprj_obj.png", true); //$NON-NLS-1$

        declareWorkbenchImage(configurer, ideBundle,
                IDEInternalWorkbenchImages.IMG_ETOOL_PROBLEM_CATEGORY,
                problemsViewPath + "problem_category.png", //$NON-NLS-1$
                true);
        declareWorkbenchImage(configurer, ideBundle,
                IDEInternalWorkbenchImages.IMG_OBJS_ERROR_PATH,
                pathObject + "error_tsk.png", //$NON-NLS-1$
                true);
        declareWorkbenchImage(configurer, ideBundle,
                IDEInternalWorkbenchImages.IMG_OBJS_WARNING_PATH,
                pathObject + "warn_tsk.png", //$NON-NLS-1$
                true);
        declareWorkbenchImage(configurer, ideBundle,
                IDEInternalWorkbenchImages.IMG_OBJS_INFO_PATH,
                pathObject + "info_tsk.png", //$NON-NLS-1$
                true);

        declareWorkbenchImage(configurer, ideBundle,
                IDEInternalWorkbenchImages.IMG_ELCL_QUICK_FIX_ENABLED,
                elclPath + "smartmode_co.png", //$NON-NLS-1$
                true);
        
        declareWorkbenchImage(configurer, ideBundle,
                IDEInternalWorkbenchImages.IMG_DLCL_QUICK_FIX_DISABLED,
                dlclPath + "smartmode_co.png", //$NON-NLS-1$
                true);
        
        declareWorkbenchImage(configurer, ideBundle,
                IDEInternalWorkbenchImages.IMG_DLGBAN_QUICKFIX_DLG,
                wizbanPath + "quick_fix.png",  //$NON-NLS-1$
                true);
        
        declareWorkbenchImage(configurer, ideBundle,
                IDEInternalWorkbenchImages.IMG_DLGBAN_SAVEAS_DLG,
                wizbanPath + "saveas_wiz.png", true); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * @param configurer
     * @return
     */
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
        IWorkbenchWindowConfigurer configurer) {
        
        return new JubulaWorkbenchWindowAdvisor(configurer);
    }
    
    /**
     * This hooks up the adapters from the core (mainly resource) 
     * components to the navigator support
     */
    public void preStartup() {
        IDE.registerAdapters();
    }

    /**
     * To get the icons for projects correctly registered
     * @param configurerP 
     *      the workbench configurer
     * @param ideBundle 
     *      the bundle
     * @param symbolicName 
     *      the symbolic name
     * @param path 
     *      the path
     * @param shared 
     *      whether it's shared or not
     */
    private void declareWorkbenchImage(IWorkbenchConfigurer configurerP, 
            Bundle ideBundle, String symbolicName, 
            String path, boolean shared) {
        URL url = ideBundle.getEntry(path);
        ImageDescriptor desc = ImageDescriptor.createFromURL(url);
        configurerP.declareImage(symbolicName, desc, shared);
    }
    
    /**
     * Hook the root of the Common Navigator up to the workspace
     * {@inheritDoc}
     */
    public IAdaptable getDefaultPageInput()  {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        return workspace.getRoot();
    }
    
    /**
     * This method is called after application-start, but currently it does nothing.
     * The Indigo targeted version of Jubula (1.3) started the specification perspective
     * after application-start in this method, instead since the Juno targeted version of
     * Jubula (2.0.0) the previous perspective is opened automatically, which is the
     * normal behavior of Eclipse.
     */
    public void postStartup() {
        Platform.addLogListener(m_runtimeLogger);
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().
            getSelectionService().addSelectionListener(
                    JBNavigationHistory.getInstance());
    }

    /**
     * {@inheritDoc}
     */
    public void eventLoopException(Throwable exception) {
        if (exception instanceof RuntimeException) {
            if (OperationCanceledUtil.isOperationCanceled(
                    (RuntimeException)exception)) {
                // ignore exception that originates from a canceled 
                // operation
                return;
            }
            log.error(Messages.UnhandledRuntimeException, exception);
            if (exception instanceof JBRuntimeException) {
                ErrorHandlingUtil.createMessageDialog(
                        ((JBRuntimeException)exception).getErrorId());
                return;
            } else if (exception instanceof PersistenceException) {
                ErrorHandlingUtil.createMessageDialog(
                        MessageIDs.E_UNKNOWN_DB_ERROR);
                return;
            } else if (exception instanceof IllegalStateException) {
                // Check whether this error is caused by an invalid workspace
                Location workspace = Platform.getInstanceLocation();
                if (workspace.isSet()) {
                    File workspaceDir = 
                        new File(workspace.getURL().getFile());
                    while (workspaceDir != null && !workspaceDir.exists()) {
                        workspaceDir = workspaceDir.getParentFile();
                    }
                    if (workspaceDir == null || !workspaceDir.canWrite()) {
                        String displayDir = getWorkspaceLocation();
                        ErrorHandlingUtil.createMessageDialog(
                            MessageIDs.E_INVALID_WORKSPACE, 
                            new String [] {displayDir}, null);
                        
                        return;
                    }
                }
            } else {
                StackTraceElement[] stackTraceArray = exception.getStackTrace();
                if (!checkStackTrace(exception, stackTraceArray)) {
                    return;
                }
            }

            if (!Plugin.isRCPException(exception) 
                    && !Plugin.isContentAssistException(exception)) {
                ErrorHandlingUtil.createMessageDialog(
                        new JBFatalException(exception,
                        MessageIDs.E_UNEXPECTED_EXCEPTION));
            }
        } else {
            super.eventLoopException(exception);
        }
    }
    /**
     * 
     * @param exception -
     * @param stackTraceArray -
     * @return boolean
     */
    private boolean checkStackTrace(Throwable exception,
            StackTraceElement[] stackTraceArray) {
        for (int i = 0; i < stackTraceArray.length; ++i) {
            final StackTraceElement stackTraceElement = stackTraceArray[i];
            String className = stackTraceElement.getClassName();
            if ((exception instanceof IllegalArgumentException)
                && KeySequence.class.getName().equals(className)) {
                // ignore exception from eclipse framework, that occurs 
                // in key binding table when binding is selected and 
                // delete or backspace was pressed
                return false;
            } else if (stackTraceElement.toString().contains("mylyn")) { //$NON-NLS-1$
                // ignore some Mylyn exceptions
                // IllegalArgumentExceptions: needed for exporting a local tasks id or URL
                if ((exception instanceof IllegalArgumentException)
                    // needed for exporting uncategorized task categories
                    || (exception instanceof ClassCastException)
                    // http://bugzilla.bredex.de/1521
                    || (exception instanceof ConcurrentModificationException)) {
                    return false;
                }
            } else if ((exception instanceof NullPointerException)
                && KeySequenceText.class.getName().equals(className)) {
                // ignore exception from eclipse framework, that occurs 
                // in key binding table when cursor is before binding 
                // and delete was pressed
                return false;
            } else if ((exception instanceof NullPointerException)
                && className != null
                && className.startsWith(ConsoleView.class.getName())) {
                // ignore exception from ConsoleView, that occurs when 
                // clicking on an empty field in the ConsoleView toolbar
                return false;
            } else if ((exception instanceof NullPointerException)
                && Plugin.isGEFException(exception)) {
                // ignore exception from GEF
                return false;
            } else if ((exception instanceof NullPointerException)
                && Plugin.isRCPException(exception)) {
                // ignore exception from RCP
                return false;
            } else if (exception instanceof NumberFormatException
                && IPageLayout.ID_PROBLEM_VIEW.equals(getActivePartId())) {
                // ignore exception from eclipse framework that occurs 
                // in problem view when a non-integer is entered into  
                // the limits field of the view preferences
                return false;
            } 
            
        }
        return true;
    }

    /**
     * @return the location of the currently selected workspace suitable for 
     *         display to the user.
     */
    private String getWorkspaceLocation() {
        String displayDir;
        try {
            displayDir = new File(Platform.getInstanceLocation()
                .getURL().getFile()).getCanonicalPath();
        } catch (IOException ioe) {
            displayDir = new File(Platform.getInstanceLocation()
                .getURL().getFile()).getPath();
        }
        return displayDir;
    }
    
    /**
     * 
     * @return the id of the currently active part, or the empty string if 
     *         no part is currently active.
     */
    private String getActivePartId() {
        String emptyString = StringConstants.EMPTY;

        IWorkbench wb = PlatformUI.getWorkbench();
        if (wb == null) {
            return emptyString;
        }
        
        IWorkbenchWindow wbWin = wb.getActiveWorkbenchWindow();
        if (wbWin == null) {
            return emptyString;
        }
        
        IWorkbenchPage page = wbWin.getActivePage();
        if (page == null) {
            return emptyString;
        }

        IWorkbenchPart part = page.getActivePart();
        if (part == null) {
            return emptyString;
        }
        
        IWorkbenchPartSite site = part.getSite();
        if (site == null) {
            return emptyString;
        }
        
        return site.getId();
    
        
    }

    /**
     * {@inheritDoc}
     */
    public boolean preShutdown() {
        try {
            Plugin.getDefault().setClientStatus(ClientStatus.STOPPING);
            // Close all open editors
            IWorkbenchWindow[] allWW = PlatformUI.getWorkbench()
                .getWorkbenchWindows();
            
            if (allWW != null) {
                for (int i = 0; i < allWW.length; i++) {
                    IWorkbenchPage[] allWP = allWW[i].getPages();
                    if (allWP != null) {
                        for (int j = 0; j < allWP.length; j++) {
                            boolean areAllClosed = allWP[j]
                                                         .closeAllEditors(true);
                            if (!areAllClosed) {
                                return false;
                            }
                        }
                    }
                }
            }
            
            // save the full workspace before quit
            ResourcesPlugin.getWorkspace().save(true, null);
        } catch (final CoreException e) {
            if (log.isErrorEnabled()) {
                log.error(Messages.UnhandledRuntimeException, e);
            }
        }
        Platform.removeLogListener(m_runtimeLogger);
        return super.preShutdown();
    }
    
}