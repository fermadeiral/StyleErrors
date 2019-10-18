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
package org.eclipse.jubula.client.ui.rcp.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.TestDataBP;
import org.eclipse.jubula.client.core.businessprocess.TestDataCubeBP;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.propertydescriptors.ParamTextPropertyDescriptor;
import org.eclipse.jubula.client.ui.rcp.controllers.propertysources.AbstractNodePropertySource.AbstractParamValueController;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.jubula.client.ui.rcp.editors.CentralTestDataEditor;
import org.eclipse.jubula.client.ui.rcp.handlers.open.AbstractOpenHandler;
import org.eclipse.jubula.client.ui.rcp.views.JBPropertiesPage;
import org.eclipse.jubula.client.ui.rcp.views.dataset.AbstractDataSetPage;
import org.eclipse.jubula.client.ui.rcp.views.dataset.DataSetView;
import org.eclipse.swt.SWTException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.views.properties.PropertySheet;

/**
 * Jumps to a CTDS
 * @author BREDEX GmbH
 *
 */
public class JumpToCTDSHandler extends AbstractHandler {

    @Override
    public Object executeImpl(ExecutionEvent event) {
        String mined = mineString();
        if (mined == null) {
            jumpToCTDSEditorByReferenceCube();
        }
        if (mined != null) {
            jumpToCTDSEditor(mined);
        }
        return null;
    }

    /**
     * Tries to extract the parameter String from the View
     * @return the string...
     */
    private String mineString() {
        IViewPart v = Plugin.getActiveView();
        if (v instanceof PropertySheet) {
            return mineFromPropertyView((PropertySheet) v);
        }
        if (v instanceof DataSetView) {
            return mineFromDataSetView((DataSetView) v);
        }
        return null;
    }

    /**
     * Tries to extract the selection from the Property View
     * @param p the Property View
     * @return the parameter String or null
     */
    private String mineFromPropertyView(PropertySheet p) {
        if (!(p.getCurrentPage() instanceof JBPropertiesPage)) {
            return null;
        }
        ITreeSelection ssel = ((JBPropertiesPage) p.getCurrentPage()).
                getCurrentTreeSelection();
        if (ssel == null) {
            return null;
        }
        Object f = ssel.getFirstElement();
        if (!(f instanceof ParamTextPropertyDescriptor)) {
            return null;
        }
        ParamTextPropertyDescriptor pd = (ParamTextPropertyDescriptor) f;
        if (!(pd.getId() instanceof AbstractParamValueController)) {
            return null;
        }
        return ((AbstractParamValueController) pd.getId()).
                getProperty();
    }

    /**
     * Tries to extract the selected parameter value from the DataSetView
     * @param view the DataSetView
     * @return the parameter value or null
     */
    private String mineFromDataSetView(DataSetView view) {
        if (!(view.getCurrentPage() instanceof AbstractDataSetPage)) {
            return null;
        }
        AbstractDataSetPage page = (AbstractDataSetPage) view.getCurrentPage();
        Object res = null;
        try {
            res = page.getTableCursor().getData();
        } catch (SWTException e) {
            // widget is disposed or other problems
        }
        if (!(res instanceof String)) {
            return null;
        }
        return (String) res;
    }

    /**
     * @param value the Edited value
     */
    public void jumpToCTDSEditor(String value) {
        // best will contain the most accurate CTDS reference
        String[] best = null;
        int max = 0;
        int num;
        for (String[] arr : TestDataBP.getAllCTDSReferences(value)) {
            num = 0;
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] != null) {
                    num++;
                }
            }
            if (num > max) {
                best = arr;
                max = num;
            }
        }
        if (best != null) {
            openCTDS(best);
        }
    }

    /**
     * Opens an Editor to edit the CTDS
     * @param data the CTDS data: a String array of length 4,
     *      corresponding to the 4 arguments of the ?getCTDSValue function
     */
    private void openCTDS(String[] data) {
        IProjectPO proj = GeneralStorage.getInstance().getProject();
        IParameterInterfacePO po = TestDataCubeBP.
                getTestDataCubeByName(data[0], proj);
        if (po == null) {
            return;
        }
        IEditorPart part = AbstractOpenHandler.
                openEditor(proj.getTestDataCubeCont());
        if (!(part instanceof CentralTestDataEditor)
                || data[1] == null || data[2] == null) {
            // missing data[1] or data[2] makes selection of row impossible
            // so we just open the CTDS editor
            return;
        }
        CentralTestDataEditor ctdsEd = (CentralTestDataEditor) part;
        po = ctdsEd.getEntityManager().find(po.getClass(), po.getId());
        ctdsEd.getTreeViewer().setSelection(new StructuredSelection(po));
        IViewPart view = Plugin.showView(DataSetView.ID);
        if (!(view instanceof DataSetView)) {
            return;
        }
        ((DataSetView) view).navigateToCell(data[1], data[2], data[3]);
    }

    /**
     * Jumps to the CTDS editor, if there is a referenced Data Cube
     */
    private void jumpToCTDSEditorByReferenceCube() {
        Object firstElement = getSelectionFromActiveEditor();
        if (firstElement instanceof IParameterInterfacePO) {
            IParameterInterfacePO referencedDataCube =
                    ((IParameterInterfacePO) firstElement)
                            .getReferencedDataCube();
            if (referencedDataCube == null) {
                return;
            }
            IProjectPO proj = GeneralStorage.getInstance().getProject();
            IEditorPart part =
                    AbstractOpenHandler.openEditor(proj.getTestDataCubeCont());
            CentralTestDataEditor ctdsEd = (CentralTestDataEditor) part;
            IParameterInterfacePO find =
                    ctdsEd.getEditorHelper().getEditSupport().getSession().find(
                            referencedDataCube.getClass(),
                            referencedDataCube.getId());
            if (find == null) {
                return;
            }
            ctdsEd.getTreeViewer().setSelection(new StructuredSelection(find));
            IViewPart view = Plugin.showView(DataSetView.ID);
        }
    }
    
    /**
     * @return the selected Object or Null if none is selected
     */
    private Object getSelectionFromActiveEditor() {
        AbstractJBEditor activeJBEditor =
                Plugin.getDefault().getActiveJBEditor();
        if (activeJBEditor == null) {
            return null;
        }
        ISelection selection = activeJBEditor.getSelection();
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structSelection =
                    (IStructuredSelection) selection;
            return structSelection.getFirstElement();
        }
        return null;
    }
}
