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
package org.eclipse.jubula.client.ui.rcp.propertytester;

import java.util.Collection;
import java.util.List;

import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.propertytester.AbstractBooleanPropertyTester;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.dnd.LocalSelectionClipboardTransfer;
import org.eclipse.jubula.client.ui.rcp.controllers.dnd.objectmapping.OMEditorDndSupport;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.ui.IEditorPart;

/**
 * Property tester for selections in Object Mapping Editor. 
 * 
 * @created 22.05.2012
 */
public class ObjectMappingCollectionPropertyTester extends
        AbstractBooleanPropertyTester {

    /** the id of the "isPasteAllowed" property */
    public static final String IS_PASTE_ALLOWED = "isPasteAllowed"; //$NON-NLS-1$

    /**
     * testable properties
     */
    private static final String[] PROPERTIES = new String[] { 
        IS_PASTE_ALLOWED };

    /**
     * 
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public boolean testImpl(Object receiver, String property, Object[] args) {
        Collection<? extends Object> selectionContents = 
                (Collection<? extends Object>)receiver;
        if (property.equals(IS_PASTE_ALLOWED)) {
            return testIsPasteAllowed(selectionContents);
        }

        return false;
    }

    /**
     * 
     * @param selectionContents The selection contents to test.
     * @return <code>true</code> if the paste command should be enabled for
     *         the given selection contents. Otherwise <code>false</code>.
     */
    private boolean testIsPasteAllowed(
            Collection<? extends Object> selectionContents) {

        LocalSelectionClipboardTransfer transfer = 
                LocalSelectionClipboardTransfer.getInstance();
        IEditorPart activeEditor = Plugin.getActiveEditor();
        if (!(activeEditor instanceof ObjectMappingMultiPageEditor)) {
            return false;
        }
        ObjectMappingMultiPageEditor objectMappingEditor = 
                (ObjectMappingMultiPageEditor)activeEditor;
        
        Object cbContents = objectMappingEditor.getEditorHelper()
                .getClipboard().getContents(transfer);

        if (cbContents == null) {
            return false;
        }

        if (transfer.getSource() != null 
                && !transfer.getSource().equals(
                        objectMappingEditor.getTreeViewer())) {
            return false;
        }

        boolean isEnabled = false;
        if (transfer.containsOnlyType(IObjectMappingAssoziationPO.class)) {
            // Use logic for validating associations
            isEnabled = getPasteActionEnablementForAssocs(
                    transfer.getSelection().toList(), 
                    selectionContents, objectMappingEditor);
        } else if (transfer.containsOnlyType(
                IObjectMappingCategoryPO.class)) {
            // Use logic for validating categories
            isEnabled = false;
        } else if (transfer.containsOnlyType(IComponentNamePO.class)) {
            // Use logic for validating Component Names
            isEnabled = getPasteActionEnablementForCompNames(
                    selectionContents, objectMappingEditor);
        } else {
            isEnabled = false;
        }

        
        return isEnabled;
    }

    /**
     * @param targetList The currently selected elements.
     * @param objectMappingEditor The Object Mapping Editor in which the paste
     *                            would occur.
     * @return <code>true</code> if the paste operation should be
     *         enabled for the given arguments. Otherwise, 
     *         <code>false</code>.
     */
    private boolean getPasteActionEnablementForCompNames(
            Collection<? extends Object> targetList, 
            ObjectMappingMultiPageEditor objectMappingEditor) {
        
        for (Object target : targetList) {
            if (target instanceof IObjectMappingAssoziationPO) {
                return true;
            } else if (target instanceof IObjectMappingCategoryPO) {
                if (!OMEditorDndSupport.canMoveCompNames(
                        (IObjectMappingCategoryPO)target, 
                        objectMappingEditor)) {

                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }
    
    /**
     * 
     * @param toMove The associations on the clipboard.
     * @param targetList The currently selected elements.
     * @param objectMappingEditor The Object Mapping Editor in which the paste
     *                            would occur.
     * @return <code>true</code> if the paste operation should be
     *         enabled for the given arguments. Otherwise, 
     *         <code>false</code>.
     */
    private boolean getPasteActionEnablementForAssocs(
            List<IObjectMappingAssoziationPO> toMove, 
            Collection<? extends Object> targetList, 
            ObjectMappingMultiPageEditor objectMappingEditor) {

        for (Object target : targetList) {
            if (target instanceof IObjectMappingCategoryPO) {
                if (!OMEditorDndSupport.canMoveAssociations(
                        toMove, (IObjectMappingCategoryPO)target, 
                        objectMappingEditor)) {

                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Class<? extends Object> getType() {
        return Collection.class;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String[] getProperties() {
        return PROPERTIES;
    }

}
