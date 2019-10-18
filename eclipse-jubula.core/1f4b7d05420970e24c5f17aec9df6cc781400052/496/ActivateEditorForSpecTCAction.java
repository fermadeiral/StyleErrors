/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.handlers.open.AbstractOpenHandler;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.labelprovider.GeneralLabelProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * Tries to activate an Editor for a TC / TS which references the given SpecTC.
 * In case there is a single such Editor, it is activated. If there are
 * multiple, a dialog is shown and the user can choose which one to activate.
 * And if there are none, then all such SpecTCs / TSs are shown, and the user
 * can choose one to open in an Editor.
 * 
 * @author BREDEX GmbH
 *
 */
public class ActivateEditorForSpecTCAction {

    /** Constructor */
    private ActivateEditorForSpecTCAction() {
        // nothing
    }

    /**
     * @param spec the Spec TC
     */
    public static void activateEditor(ISpecTestCasePO spec) {
        List<IEditorReference> editors = Plugin.getAllEditors();
        List<AbstractTestCaseEditor> hits = new ArrayList<>(editors.size());
        for (IEditorReference ref : editors) {
            IEditorPart ed = ref.getEditor(true);
            if (!(ed instanceof AbstractTestCaseEditor)) {
                continue;
            }
            IPersistentObject per = ((AbstractTestCaseEditor) ed).
                    getEditorHelper().getEditSupport().getWorkVersion();
            if (!(per instanceof INodePO)) {
                continue;
            }
            boolean include = false;
            for (Iterator<INodePO> it = ((INodePO) per).getAllNodeIter();
                    it.hasNext(); ) {
                INodePO next = it.next();
                if (!(next instanceof IExecTestCasePO)) {
                    continue;
                }
                if (spec.equals(((IExecTestCasePO) next).getSpecTestCase())) {
                    include = true;
                    break;
                }
            }
            if (include) {
                hits.add((AbstractTestCaseEditor) ed);
            }
        }
        if (hits.size() == 1) {
            hits.get(0).getSite().getPage().activate(hits.get(0));
            return;
        }
        if (hits.isEmpty()) {
            showReferrersDialog(spec);
            return;
        }
        showEditorsDialog(hits);
    }

    /**
     * Shows a dialog listing Editors referencing the given SpecTC
     * @param editors the list of Editors
     */
    private static void showEditorsDialog(
            List<AbstractTestCaseEditor> editors) {
        ElementListSelectionDialog dialog = new ElementListSelectionDialog(
             Plugin.getActiveEditor().getSite().getShell(),
             new GeneralLabelProvider());
        dialog.setTitle(Messages.ChooseEditorTitle);
        dialog.setElements(editors.toArray());
        dialog.setMessage(Messages.ChooseEditorFromList);
        dialog.open();
        if (dialog.getReturnCode() != Window.OK
                || !(dialog.getFirstResult()
                        instanceof AbstractTestCaseEditor)) {
            return;
        }
        AbstractTestCaseEditor ed = (AbstractTestCaseEditor) dialog.
                getFirstResult();
        ed.getSite().getPage().activate(ed);
    }

    /**
     * Shows a dialog containing all SpecTCs / TSs referencing the given SpecTC
     * @param spec the Spec Test Case
     */
    private static void showReferrersDialog(ISpecTestCasePO spec) {
        Set<INodePO> refs = new HashSet<>();
        List<Long> ids = new ArrayList<>(1);
        Long projId = spec.getParentProjectId(); 
        ids.add(projId);
        for (IExecTestCasePO exec : NodePM.getExecTestCases(
                spec.getGuid(), ids)) {
            INodePO anc = exec.getSpecAncestor();
            if (anc != null && anc.getParentProjectId().equals(projId)) {
                refs.add(anc);
            }
        }
        if (refs.isEmpty()) {
            return;
        }
        ElementListSelectionDialog dialog = new ElementListSelectionDialog(
                Plugin.getActiveEditor().getSite().getShell(),
                new GeneralLabelProvider());
        dialog.setTitle(Messages.ChooseReferrerTitle);
        dialog.setElements(refs.toArray());
        dialog.setMessage(Messages.ChooseReferrerText);
        dialog.open();
        if (dialog.getReturnCode() != Window.OK
               || !(dialog.getFirstResult()
                       instanceof INodePO)) {
            return;
        }
        AbstractOpenHandler.openEditor((INodePO) dialog.getFirstResult());
    }
}
