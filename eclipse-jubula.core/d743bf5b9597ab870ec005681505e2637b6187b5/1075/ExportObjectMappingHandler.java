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
package org.eclipse.jubula.client.api.ui.handlers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.api.ui.utils.OMExport;
import org.eclipse.jubula.client.core.businessprocess.IComponentNameCache;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.model.IObjectMappingPO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.LogicComponentNotManagedException;
import org.eclipse.jubula.client.core.utils.ObjectMappingUtil;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.toolkit.client.api.ui.internal.OMClassGenerator;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.objects.ComponentIdentifier;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author BREDEX GmbH
 * @created 07.10.2014
 */
public class ExportObjectMappingHandler extends AbstractHandler {
        
    /** map containing all object mappings */
    private Map<String, String> m_map = new TreeMap<String, String>();
    
    /** the component name cache to use */
    private IComponentNameCache m_compCache;
    
    /** the class generator for the OM class */
    private OMClassGenerator m_omClassGenerator = new OMClassGenerator();

    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        m_map.clear();
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        if (activePart instanceof ObjectMappingMultiPageEditor) {
            final ObjectMappingMultiPageEditor omEditor = 
                (ObjectMappingMultiPageEditor)activePart;
            IAUTMainPO aut = omEditor.getAut();
            
            int exportType = determineExportType();
            if (exportType != -1) {
                FileDialog saveDialog = createSaveDialog(aut, exportType);
                String path = saveDialog.open();
                if (path != null) {
                    Utils.storeLastDirPath(saveDialog.getFilterPath());
                    fillMap(omEditor, aut);
                    // map is filled and can be written to class or file
                    OMExport omAssociations = new OMExport(
                            m_map, saveDialog.getFileName());
                    try (BufferedWriter writer = new BufferedWriter(
                            new FileWriter(path))) {
                        switch (exportType) {
                            case 0: // Write Java Class
                                writer.append(m_omClassGenerator
                                        .generate(omAssociations));
                                break;
                            case 1: // Write Properties File
                                writer.append(omAssociations
                                        .createEncodedAssociations());
                                break;
                            default: // Nothing
                                break;
                        }
                    } catch (IOException e) {
                        ErrorHandlingUtil.createMessageDialog(
                            new JBException(e.getMessage(), e,
                                    MessageIDs.E_FILE_NO_PERMISSION));
                    }
                }
            }
        }
        return null;
    }

    /**
     * fills the map with the encoded object mapping associations
     * @param omEditor the object mapping editor
     * @param aut the AUT
     */
    private void fillMap(final ObjectMappingMultiPageEditor omEditor,
            IAUTMainPO aut) {
        m_compCache = omEditor.getCompNameCache();
        IObjectMappingPO objMap = aut.getObjMap();
        IStructuredSelection selection = (IStructuredSelection) omEditor
                .getTreeViewer().getSelection();
        try {
            if (selection.isEmpty()) {
                IObjectMappingCategoryPO rootCategory = objMap
                        .getMappedCategory();
                writeAssociationsToMap(rootCategory);
            } else {
                Iterator<IPersistentObject> selectionIterator = 
                        selection.iterator();
                while (selectionIterator.hasNext()) {
                    IPersistentObject next = selectionIterator
                            .next();
                    if (next instanceof IObjectMappingCategoryPO) {
                        writeAssociationsToMap((IObjectMappingCategoryPO) next);
                    } else if (next instanceof IObjectMappingAssoziationPO) {
                        addAssoziationToMap((IObjectMappingAssoziationPO) next);
                    }
                }
            }
        } catch (LogicComponentNotManagedException | IOException e) {
            ErrorHandlingUtil.createMessageDialog(new JBException(e
                    .getMessage(), e, MessageIDs.E_EXPORT_OM_ERROR));
        }
    }

    /**
     * Creates the save dialogue
     * @param aut the aut (needed for the name)
     * @param exportType the type of the export
     * @return the save dialogue
     */
    private FileDialog createSaveDialog(IAUTMainPO aut, int exportType) {
        String fileName = StringConstants.EMPTY;
        String fileExtension = StringConstants.EMPTY;
        switch (exportType) {
            case 0: // Write Java Class
                fileExtension = ".java"; //$NON-NLS-1$
                fileName = "OM" + fileExtension; //$NON-NLS-1$
                break;
            case 1: // Write Properties File
                fileExtension = ".properties"; //$NON-NLS-1$
                fileName = "objectMapping" + StringConstants.UNDERSCORE //$NON-NLS-1$
                        + aut.getName() + fileExtension;
                break;
            default: // Nothing
                break;
        }
        
        FileDialog saveDialog = new FileDialog(getActiveShell(), SWT.SAVE);
        saveDialog.setFileName(fileName);
        saveDialog.setFilterExtensions(
                new String[] { StringConstants.STAR + fileExtension });
        saveDialog.setOverwrite(true);
        String filterPath = Utils.getLastDirPath();
        saveDialog.setFilterPath(filterPath);
        return saveDialog;
    }

    /**
     * Opens a question dialogue to determine the desired export type
     * @return the export type
     *      <code>0</code> for a Java Class File
     *      <code>1</code> for a Properties File
     */
    private int determineExportType() {
        String dialogTitle = Messages.ExportObjectMappingDialogTitle;
        String dialogMessage = Messages.ExportObjectMappingDialogMessage;
        MessageDialog dialog = new MessageDialog(getActiveShell(), dialogTitle,
                null, dialogMessage, MessageDialog.QUESTION,
                new String[] {
                    Messages.ExportObjectMappingDialogChoiceJavaClass,
                    Messages.ExportObjectMappingDialogChoicePropertiesFile },
                0);
        return dialog.open();
    }

    /**
     * Writes all object mapping associations from a given category (and
     * recursively from all sub-categories) into the map
     * 
     * @param category
     *            the category
     * @throws LogicComponentNotManagedException when there is a problem with
     *      assigning component identifiers to their logical names
     * @throws IOException when there is a problem with encoding
     */
    private void writeAssociationsToMap(IObjectMappingCategoryPO category)
        throws LogicComponentNotManagedException, IOException {
        List<IObjectMappingCategoryPO> subcategoryList =
                category.getUnmodifiableCategoryList();
        if (!subcategoryList.isEmpty()) {
            for (IObjectMappingCategoryPO subcategory : subcategoryList) {
                writeAssociationsToMap(subcategory);
            }
        }
        for (IObjectMappingAssoziationPO assoziation
                : category.getUnmodifiableAssociationList()) {
            addAssoziationToMap(assoziation);
        }
    }

    /**
     * Adds an object mapping association to the object map for export
     * @param assoziation the object mapping association
     */
    private void addAssoziationToMap(IObjectMappingAssoziationPO assoziation)
            throws IOException {
        for (String compUUID : assoziation.getLogicalNames()) {
            String compName = m_compCache.getNameByGuid(compUUID);
            ComponentIdentifier identifier = (ComponentIdentifier) 
                    ObjectMappingUtil.createCompIdentifierFromAssoziation(
                            assoziation);
            m_map.put(compName, OMExport.getSerialization(identifier));
        }
    }
}