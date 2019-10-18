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
package org.eclipse.jubula.client.ui.rcp.controllers.dnd.objectmapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jubula.client.core.businessprocess.CompNameManager;
import org.eclipse.jubula.client.core.businessprocess.CompNameTypeManager;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.osgi.util.NLS;

/**
 * Utility class containing methods for use in drag and drop as well as 
 * cut and paste operations in the Object Mapping Editor.
 *
 * @author BREDEX GmbH
 * @created 20.03.2008
 */
public class OMEditorDndSupport {

    /**
     * Private constructor
     */
    private OMEditorDndSupport() {
        // Do nothing
    }

    /**
     * Assigns the given Component Names to the given association.
     * 
     * @param compNamesToMove The Component Names to assign.
     * @param target The association to which the Component Names will be
     *               assigned.
     * @param editor Editor in which the assignment is taking place.
     * @return whether the operation is cancelled by the user
     */
    public static boolean checkTypeCompatibilityAndMove(
            List<IComponentNamePO> compNamesToMove, 
            IObjectMappingAssoziationPO target, 
            ObjectMappingMultiPageEditor editor) {

        IWritableComponentNameCache compCache = editor.getCompNameCache();
        IObjectMappingCategoryPO unmappedTechnical =
            editor.getAut().getObjMap().getUnmappedTechnicalCategory();
        if (checkProblemsAndStop(compNamesToMove, target, editor)) {
            return true;
        }
        for (IComponentNamePO compName : compNamesToMove) {
            String compNameGuid = compName.getGuid();
            if (!target.getLogicalNames().contains(compNameGuid)) {
                IObjectMappingAssoziationPO oldAssoc = 
                        editor.getOmEditorBP().getAssociation(compNameGuid);
                compCache.changeReuse(target, null, compNameGuid);
                compCache.changeReuse(oldAssoc, compNameGuid, null);
                if (getSection(target).equals(
                        unmappedTechnical)) {
                    // Change section to mapped, creating new categories 
                    // if necessary.
                    IObjectMappingCategoryPO mapped =
                        editor.getAut().getObjMap().getMappedCategory();
                    IObjectMappingCategoryPO newCategory = 
                        editor.getOmEditorBP().createCategory(
                                mapped, target.getCategory());
                    target.getCategory().removeAssociation(target);
                    newCategory.addAssociation(target);
                }
                cleanupAssociation(editor, oldAssoc);
            }
        }
        DataEventDispatcher.getInstance().fireDataChangedListener(
                editor.getAut().getObjMap(),
                DataState.StructureModified, 
                UpdateState.onlyInEditor);
        
        editor.getTreeViewer().setExpandedState(target, true);
        return false;
    }
    
    /**
     * Checks whether mapping the CNs to the Technical Name creates any problems
     *      if yes, the user is notified and can cancel the action
     * @param compNamesToMove The Component Names to assign.
     * @param target The association to which the Component Names will be
     *               assigned.
     * @param editor Editor in which the assignment is taking place.
     * @return whether the operation is cancelled by the user
     */
    private static boolean checkProblemsAndStop(
            List<IComponentNamePO> compNamesToMove, 
            IObjectMappingAssoziationPO target, 
            ObjectMappingMultiPageEditor editor) {
        List<IComponentNamePO> problems = new ArrayList<>();
        List<Component> availableComponents = ComponentBuilder.getInstance()
                .getCompSystem().getComponents(editor.getAut().getToolkit(),
                true);
        String type = null;
        if (target.getTechnicalName() != null) {
            type = CompSystem.getComponentType(target.getTechnicalName()
                    .getSupportedClassName(), availableComponents);
        }
        IComponentNamePO masterCN;
        for (IComponentNamePO cN : compNamesToMove) {
            masterCN = CompNameManager.getInstance()
                    .getResCompNamePOByGuid(cN.getGuid());
            if (masterCN == null) {
                continue;
            }
            if (!masterCN.getUsageType().equals(
                    ComponentNamesBP.UNKNOWN_COMPONENT_TYPE)
                    && !CompNameTypeManager.doesFirstTypeRealizeSecond(
                    type, masterCN.getUsageType())) {
                problems.add(masterCN);
            }
        }
        if (problems.isEmpty()) {
            return false;
        }
        StringBuilder list = new StringBuilder();
        for (IComponentNamePO cN : problems) {
            list.append(StringConstants.SPACE);
            list.append(StringConstants.SPACE);
            list.append(StringConstants.SPACE);
            list.append(cN.getName());
            list.append(StringConstants.SPACE);
            list.append(StringConstants.LEFT_BRACKET);
            list.append(CompSystemI18n.getString(cN.getUsageType()));
            list.append(StringConstants.RIGHT_BRACKET);
            list.append(StringConstants.NEWLINE);
        }
        String message = NLS.bind(Messages.IncompatibleMapDialogText,
                list.toString());
        
        MessageDialog dialog = new MessageDialog(null, 
                Messages.IncompatibleMapDialogTitle,
            null, 
            message, MessageDialog.QUESTION, new String[] {
                Messages.DialogMessageButton_YES,
                Messages.DialogMessageButton_NO }, 0);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        dialog.open();
        return dialog.getReturnCode() != 0;
    }

    /**
     * Performs any necessary "cleanup" on an Object Mapping Association. This
     * includes moving the association to the appropriate "section" 
     * (ex. Unmapped Technical Components or Mapped Components).
     * 
     * @param editor The editor in which the cleanup is to occur.
     * @param assoc The association to cleanup.
     */
    private static void cleanupAssociation(ObjectMappingMultiPageEditor editor,
            IObjectMappingAssoziationPO assoc) {

        if (assoc != null 
                && assoc.getLogicalNames().isEmpty()) {
            IObjectMappingCategoryPO fromCategory = 
                assoc.getCategory();
            if (assoc.getTechnicalName() != null) {
                // Change section to unmapped tech, creating new 
                // categories if necessary.
                IObjectMappingCategoryPO unmappedTech =
                    editor.getAut().getObjMap()
                    .getUnmappedTechnicalCategory();
                IObjectMappingCategoryPO newCategory = 
                    editor.getOmEditorBP().createCategory(
                            unmappedTech, fromCategory);
                fromCategory.removeAssociation(assoc);
                newCategory.addAssociation(assoc);
            } else {
                // Association has no logical names and no technical
                // name. It should be deleted.
                fromCategory.removeAssociation(assoc);
            }
        }
    }
    
    /**
     * Moves the given Component Names to the given category. This removes 
     * whatever mappings in which the Component Names were involved in the 
     * context of the supported editor.
     * 
     * @param compNamesToMove The Component Names to move.
     * @param target The category to which the Component Names will be
     *               moved.
     * @param editor Editor in which the move is taking place.
     */
    public static void checkTypeCompatibilityAndMove(
            List<IComponentNamePO> compNamesToMove, 
            IObjectMappingCategoryPO target, 
            ObjectMappingMultiPageEditor editor) {

        IObjectMappingCategoryPO unmappedCat =
            editor.getAut().getObjMap().getUnmappedLogicalCategory();
        IObjectMappingCategoryPO targetSection = getSection(target);
        if (!unmappedCat.equals(targetSection)) {
            return;
        }
        IWritableComponentNameCache compCache = editor.getCompNameCache();
        for (IComponentNamePO compName : compNamesToMove) {
            String compNameGuid = compName.getGuid();
            IObjectMappingAssoziationPO oldAssoc =
                editor.getOmEditorBP().getAssociation(compNameGuid);
            if (oldAssoc.getTechnicalName() == null) {
                oldAssoc.getCategory().removeAssociation(oldAssoc);
                target.addAssociation(oldAssoc);
                continue;
            }
            IObjectMappingAssoziationPO newAssoc = 
                PoMaker.createObjectMappingAssoziationPO(
                        null, new HashSet<String>());
            editor.getAut().getObjMap().addAssociationToCache(newAssoc);
            compCache.changeReuse(newAssoc, null, compNameGuid);
            compCache.changeReuse(oldAssoc, compNameGuid, null);
            target.addAssociation(newAssoc);
            if (oldAssoc != null) {
                if (oldAssoc.getLogicalNames().isEmpty()) {
                    // Change section to unmapped tech, creating new 
                    // categories if necessary.
                    IObjectMappingCategoryPO unmappedTech =
                        editor.getAut().getObjMap()
                        .getUnmappedTechnicalCategory();
                    IObjectMappingCategoryPO newCategory = 
                        editor.getOmEditorBP().createCategory(
                                unmappedTech, oldAssoc.getCategory());
                    oldAssoc.getCategory().removeAssociation(oldAssoc);
                    newCategory.addAssociation(oldAssoc);
                }
            }
        }
        DataEventDispatcher.getInstance().fireDataChangedListener(
                editor.getAut().getObjMap(), 
                DataState.StructureModified, 
                UpdateState.onlyInEditor);
        
        editor.getTreeViewer().refresh(target);
        editor.getTreeViewer().setExpandedState(target, true);
    }

    /**
     * Moves the given associations to the given category.
     * 
     * @param toMove The associations to move.
     * @param target The category into which the associations will be moved.
     * @param editor The editor in which the move is occurring.
     */
    public static void checkAndMoveAssociations(
            List<IObjectMappingAssoziationPO> toMove, 
            IObjectMappingCategoryPO target, 
            ObjectMappingMultiPageEditor editor) {
        
        IObjectMappingCategoryPO unmappedTechNames = 
            editor.getAut().getObjMap().getUnmappedTechnicalCategory();
        IObjectMappingCategoryPO newSection = getSection(target);
        for (IObjectMappingAssoziationPO assoc : toMove) {
            IObjectMappingCategoryPO oldSection = getSection(assoc);
            
            if (oldSection.equals(newSection)) {
                IObjectMappingCategoryPO fromCategory = assoc.getCategory();
                fromCategory.removeAssociation(assoc);
                target.addAssociation(assoc);
                continue;
            } else if (unmappedTechNames.equals(newSection)) {
                IObjectMappingCategoryPO unmappedCompNames = 
                    editor.getAut().getObjMap().getUnmappedLogicalCategory();
                
                IWritableComponentNameCache compCache =
                        editor.getCompNameCache();
                for (String compNameGuid 
                        : new ArrayList<String>(assoc.getLogicalNames())) {
                    compCache.changeReuse(assoc, compNameGuid, null);
                    IObjectMappingAssoziationPO compNameAssoc = 
                        PoMaker.createObjectMappingAssoziationPO(
                                null, new HashSet<String>());
                    editor.getAut().getObjMap()
                        .addAssociationToCache(compNameAssoc);
                    compCache.changeReuse(
                            compNameAssoc, null, compNameGuid);
                    unmappedCompNames.addAssociation(compNameAssoc);
                }
                
                IObjectMappingCategoryPO fromCategory = assoc.getCategory();
                fromCategory.removeAssociation(assoc);
                target.addAssociation(assoc);
            }
        }

        if (!toMove.isEmpty()) {
            DataEventDispatcher.getInstance().fireDataChangedListener(
                    editor.getAut().getObjMap(), 
                    DataState.StructureModified, 
                    UpdateState.onlyInEditor);
            editor.getTreeViewer().setExpandedState(target, true);
        }

    }

    /**
     * 
     * @param toMove The associations for which the move operation is 
     *               to be checked.
     * @param target The target category for which the move operation is to be
     *               checked.
     * @param editor The editor in which the move operation would occur.
     * @return <code>true</code> if the arguments represent a valid move 
     *         operation. Otherwise <code>false</code>.
     */
    public static boolean canMoveAssociations(
            List<IObjectMappingAssoziationPO> toMove, 
            IObjectMappingCategoryPO target, 
            ObjectMappingMultiPageEditor editor) {
        
        for (IObjectMappingAssoziationPO assoc : toMove) {
            if (!canMove(assoc, target, editor)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 
     * @param assoc The association for which the move operation is 
     *               to be checked.
     * @param target The target category for which the move operation is to be
     *               checked.
     * @param editor The editor in which the move operation would occur.
     * @return <code>true</code> if the arguments represent a valid move 
     *         operation. Otherwise <code>false</code>.
     */
    private static boolean canMove(IObjectMappingAssoziationPO assoc,
            IObjectMappingCategoryPO target, 
            ObjectMappingMultiPageEditor editor) {

        IObjectMappingCategoryPO oldSection = getSection(assoc);
        IObjectMappingCategoryPO newSection = getSection(target);
        IObjectMappingCategoryPO unmappedTechnicalNames =
            editor.getAut().getObjMap().getUnmappedTechnicalCategory();
        
        return unmappedTechnicalNames.equals(newSection) 
            || oldSection.equals(newSection);
    }

    /**
     * 
     * @param target The target category into which the Component Names would
     *               be moved.
     * @param editor The editor in which the move operation would occur.
     * @return <code>true</code> if the arguments represent a valid move 
     *         operation. Otherwise <code>false</code>.
     */
    public static boolean canMoveCompNames(
            IObjectMappingCategoryPO target, 
            ObjectMappingMultiPageEditor editor) {

        return getSection(target).equals(
                editor.getAut().getObjMap().getUnmappedLogicalCategory());
    }

    /**
     * 
     * @param startCategory The category to check.
     * @return the top-level category to which the given category belongs.
     */
    public static IObjectMappingCategoryPO getSection(
            IObjectMappingCategoryPO startCategory) {
        
        return startCategory != null ? startCategory.getSection() : null;
    }
    
    /**
     * 
     * @param assoc The association to check.
     * @return the top-level category to which the given association belongs.
     */
    public static IObjectMappingCategoryPO getSection(
            IObjectMappingAssoziationPO assoc) {

        return assoc != null ? assoc.getSection() : null;
    }
    
    /**
     * Moving categories...
     * @param cats the categories
     * @param targ the target
     * @return whether the move ws successful
     */
    public static boolean moveCategories(List<IObjectMappingCategoryPO> cats,
            IObjectMappingCategoryPO targ) {
        List<IObjectMappingCategoryPO> topCats = new ArrayList<>();
        boolean isTop;
        IObjectMappingCategoryPO par;
        for (IObjectMappingCategoryPO cat : cats) {
            isTop = true;
            par = cat;
            while (isTop && par != null) {
                par = par.getParent();
                if (cats.contains(par)) {
                    isTop = false;
                }
            }
            if (isTop) {
                topCats.add(cat);
            }
        }
        correctNames(topCats, targ);
        for (IObjectMappingCategoryPO cat : topCats) {
            cat.getParent().removeCategory(cat);
            targ.addCategory(cat);
        }
        return true;
    }
    
    /**
     * Checks and corrects any name collisions before moving categories
     * @param cats the categories
     * @param target the target category
     */
    private static void correctNames(List<IObjectMappingCategoryPO> cats,
            IObjectMappingCategoryPO target) {
        Map<String, Integer> nameMap = new HashMap<>();
        for (IObjectMappingCategoryPO cat
            : target.getUnmodifiableCategoryList()) {
            nameMap.put(cat.getName(), 0);
        }
        Integer val;
        IObjectMappingCategoryPO next;
        String name;
        for (int i = 0; i < cats.size(); i++) {
            next = cats.get(i);
            name = next.getName();
            val = nameMap.get(name);
            if (val == null) {
                continue;
            }
            val++;
            next.setName(name + "_" + val); //$NON-NLS-1$
            if (nameMap.containsKey(next.getName())) {
                // unlikely, but the new name may also be used...
                i--;
            } else {
                nameMap.put(name, val);
            }
        }
    }

}
