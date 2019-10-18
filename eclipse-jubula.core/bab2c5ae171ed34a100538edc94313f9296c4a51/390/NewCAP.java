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
package org.eclipse.jubula.client.ui.rcp.handlers.newcap;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.businessprocess.CapBP;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP.CompNameCreationContext;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.rcp.controllers.IEditorOperation;
import org.eclipse.jubula.client.ui.rcp.dialogs.CNTypeProblemDialog;
import org.eclipse.jubula.client.ui.rcp.dialogs.NewCAPDialog;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.handlers.NewTestCaseHandlerTCEditor;
import org.eclipse.jubula.client.ui.rcp.utils.NodeTargetCalculator.NodeTarget;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ConcreteComponent;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created 18.02.2009
 */
public class NewCAP extends AbstractSelectionBasedHandler {
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        if (activePart instanceof AbstractTestCaseEditor) {
            final AbstractTestCaseEditor tce = 
                    (AbstractTestCaseEditor)activePart;
            tce.getEditorHelper().doEditorOperation(new IEditorOperation() {
                
                public void run(IPersistentObject workingPo) {
                    ISpecTestCasePO workTC = (ISpecTestCasePO)workingPo;
                    IStructuredSelection selection = getSelection();
                    INodePO selected = (INodePO)selection.getFirstElement();
                    NodeTarget tar = NewTestCaseHandlerTCEditor.
                            getPositionToInsert(selected, false);
                    if (tar != null) {
                        addCap(tar.getNode(), tar.getPos(), tce);
                        tce.getTreeViewer().setExpandedState(
                                tar.getNode().getParentNode(), true);
                    }
                }
            });
        }
        return null;
    }
    
    /**
     * Adds a new CAP to the given node at the given
     * position and, if successful, sets the given editor dirty.
     * 
     * @param target
     *            the GUI SpecTestCase for the NewCapDialog
     * @param position
     *            the position to add
     * @param tce
     *            the editor.
     */
    private void addCap(INodePO target, Integer position,
            AbstractTestCaseEditor tce) {

        final NewCAPDialog dialog = 
            new NewCAPDialog(
                getActiveShell(), 
                target, 
                tce.getCompNameCache());
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        dialog.open();
        if (dialog.getReturnCode() != Window.OK) {
            return;
        }
        final String componentType = dialog.getComponentType();
        final String capName = dialog.getCapName();
        final String action = dialog.getActionName();
        final String componentName = dialog.getComponentName(); 
        final ICapPO cap = CapBP.createCapWithDefaultParams(
            capName, componentName, componentType, action);
        final IWritableComponentNameCache cache = tce
                .getCompNameCache();
        CompSystem compSystem = ComponentBuilder.getInstance()
                .getCompSystem();
        Component comp = compSystem.findComponent(componentType);
        // Set the Component Name to null so that the Component Name 
        // cache doesn't remove the instance of reuse 
        // for <code>componentName</code>.
        cap.setComponentName(null);
        boolean hasDefaultMapping = false;
        if (comp.isConcrete()) {
            hasDefaultMapping = ((ConcreteComponent) comp)
                    .hasDefaultMapping();
        }
        if (!hasDefaultMapping) {
            ComponentNamesBP.setCompName(cap, componentName,
                CompNameCreationContext.STEP, cache);
            target.addNode(position, cap);
            if (!CNTypeProblemDialog.noProblemOrIgnore(cache,
                    target.getSpecAncestor())) {
                target.removeNode(cap);
                return;
            }
        } else {
            target.addNode(position, cap);
        }
        tce.getEditorHelper().setDirty(true);
        DataEventDispatcher.getInstance().fireDataChangedListener(cap, 
                DataState.Added, UpdateState.onlyInEditor);
    }
}
