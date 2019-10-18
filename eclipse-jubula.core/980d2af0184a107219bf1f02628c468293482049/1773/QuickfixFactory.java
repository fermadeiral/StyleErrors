/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.teststyle.quickfix;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.events.InteractionEventDispatcher;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.ICommentPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestDataCategoryPO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.teststyle.i18n.Messages;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.controllers.MultipleTCBTracker;
import org.eclipse.jubula.client.ui.rcp.handlers.open.AbstractOpenHandler;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.client.ui.views.ITreeViewerContainer;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;


/**
 * 
 * @author marcell
 */
public class QuickfixFactory {
    
    /** private constructor */
    private QuickfixFactory() {
        // factory class
    }
    
    /**
     * This is an implemented quickfix for opening a TestCase. Will be used as
     * default for testcases.
     * 
     * 
     * @author marcell
     * @created Oct 15, 2010
     */
    @SuppressWarnings("synthetic-access")
    public static class QuickfixOpenTestCase extends Quickfix {        

        /**
         * {@inheritDoc}
         */
        public String getLabel() {
            return Messages.QuickfixOpenTestCase;
        }

        /**
         * {@inheritDoc}
         */
        public void run(IMarker marker) {
            Object obj = getObject(marker);
            if (obj instanceof INodePO) {
                INodePO execTestCase = (INodePO) obj;
                AbstractOpenHandler.openEditorAndSelectNode(
                        execTestCase.getSpecAncestor(), execTestCase);
            } else {
                AbstractOpenHandler.openEditor((IPersistentObject)obj);
            }
            
        }

    }

    /**
     * This is an implemented quickfix for opening a TestSuite. Will be used as
     * default for testcases.
     * 
     * 
     * @author marcell
     * @created Oct 15, 2010
     */
    @SuppressWarnings("synthetic-access")
    public static class QuickfixOpenTestSuite extends Quickfix {

        /**
         * {@inheritDoc}
         */
        public String getLabel() {
            return Messages.QuickfixOpenTestSuite;
        } 

        /**
         * {@inheritDoc}
         */
        public void run(IMarker marker) {
            AbstractOpenHandler.openEditor(
                    (IPersistentObject)getObject(marker));
        }

    }
    
    /**
     * This is an implemented quickfix for opening the central test data
     * editor. Will be used as default for test data cubes.
     * 
     * 
     * @author marcell
     * @created Oct 15, 2010
     */
    @SuppressWarnings("synthetic-access")
    public static class QuickfixOpenTestDataCubeEditor extends Quickfix {
        
        /**
         * {@inheritDoc}
         */
        public String getLabel() {
            return Messages.QuickfixOpenCTDEditor;
        } 

        /**
         * {@inheritDoc}
         */
        public void run(IMarker marker) {
            IProjectPO project = GeneralStorage.getInstance().getProject();
            if (project != null) {
                ITestDataCategoryPO centralTestData = 
                    project.getTestDataCubeCont();
                if (centralTestData != null) {
                    IEditorPart editor = 
                        AbstractOpenHandler.openEditor(centralTestData);
                    if (editor != null) {
                        editor.getSite().getPage().activate(editor);
                    }
                }
            }
        }
    }
    
    /**
     * This is an implemented quickfix for opening a TestSuite. Will be used as
     * default for testcases.
     * 
     * 
     * @author marcell
     * @created Oct 15, 2010
     */
    @SuppressWarnings("synthetic-access")
    public static class QuickfixSelectCategory extends Quickfix {
        /**
         * {@inheritDoc}
         */
        public String getLabel() {
            return Messages.QuickfixSelectCategory;
        } 

        /**
         * {@inheritDoc}
         */
        public void run(IMarker marker) {
            INodePO node = (INodePO)getObject(marker);
            if (node != null) {
                if (!Utils.openPerspective(Constants.SPEC_PERSPECTIVE)) {
                    return;
                }
                if (!PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getActivePage().getPerspective().getId().equals(
                                Constants.SPEC_PERSPECTIVE)) {
                    // show error must be in SpecPers
                    ErrorHandlingUtil.createMessageDialog(
                            MessageIDs.I_NO_PERSPECTIVE_CHANGE);
                    return;
                }
                IViewPart view = MultipleTCBTracker.getInstance()
                        .getMainTCB();
                ITreeViewerContainer specView = (ITreeViewerContainer)view;
                InteractionEventDispatcher.getDefault().
                    fireProgammableSelectionEvent(
                            new StructuredSelection(node));
                if (specView != null) {
                    specView.getTreeViewer().refresh();
                    specView.getTreeViewer().reveal(node);
                    specView.getTreeViewer().getTree().update();
                    view.setFocus();
                    specView.getTreeViewer().expandToLevel(node, 0);
                    specView.getTreeViewer().setSelection(
                            new StructuredSelection(node), true);
                }
            }
        }

    }
    

    /**
     * This is an implemented quickfix for opening a TestSuite. Will be used as
     * default for testcases.
     * 
     * 
     * @author marcell
     * @created Oct 15, 2010
     */
    @SuppressWarnings("synthetic-access")
    public static class QuickfixOpenTestJob extends Quickfix {
        
        /**
         * {@inheritDoc}
         */
        public String getLabel() {
            return Messages.QuickfixOpenTestJob;
        } 

        /**
         * {@inheritDoc}
         */
        public void run(IMarker marker) {
            AbstractOpenHandler.openEditor(
                    (IPersistentObject)getObject(marker));
        }
    }

    /**
     * @param obj
     *            The object where a default quickfix should be searched.
     * @return The list of default quickfixes.
     */
    public static Quickfix[] getDefaultQuickfixFor(Object obj) {
        
        if (obj instanceof ISpecTestCasePO) {
            return new Quickfix[] { new QuickfixOpenTestCase() };
        } else if (obj instanceof ITestSuitePO) {
            return new Quickfix[] { new QuickfixOpenTestSuite() };
        } else if (obj instanceof Long) {
            return new Quickfix[] { new QuickfixOpenTestDataCubeEditor() };
        } else if (obj instanceof ICategoryPO) {
            return new Quickfix[] { new QuickfixSelectCategory() };
        } else if (obj instanceof ITestJobPO) {
            return new Quickfix[] { new QuickfixOpenTestJob() };
        } else if (obj instanceof IExecTestCasePO) {
            return new Quickfix[] { new QuickfixOpenTestCase() };
        } else if (obj instanceof ICommentPO) {
            return new Quickfix[] { new QuickfixOpenTestCase() };
        }
        return new Quickfix[] { }; 
    }
}
