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
package org.eclipse.jubula.client.ui.rcp.businessprocess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.eclipse.jubula.client.core.businessprocess.CompNameManager;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.businessprocess.ObjectMappingEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.model.IObjectMappingPO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.ui.rcp.controllers.dnd.objectmapping.OMEditorDndSupport;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;


/**
 * BusinessProcess class for the OM Editor.
 *
 * @author BREDEX GmbH
 * @created 02.03.2006
 */
public class OMEditorBP {
    
    /** when mapping a technical name, it will be created in here */
    private transient IObjectMappingCategoryPO m_categoryToCreateIn;

    /** editor */
    private ObjectMappingMultiPageEditor m_editor;
    
    /**
     * Constructor.
     * @param editor ObjectMappingEditor.
     */
    public OMEditorBP(ObjectMappingMultiPageEditor editor) {
        setEditor(editor);
    }
    
    /**
     * @return Returns the categoryToCreateIn.
     */
    public IObjectMappingCategoryPO getCategoryToCreateIn() {
        return m_categoryToCreateIn;
    }
    /**
     * @param categoryToCreateIn The categoryToCreateIn to set.
     */
    public void setCategoryToCreateIn(
            IObjectMappingCategoryPO categoryToCreateIn) {
        
        m_categoryToCreateIn = categoryToCreateIn;
        ObjectMappingEventDispatcher.setCategoryToCreateIn(
                categoryToCreateIn);
    }
    
    /**
     * Deletes the given category and all children from the object map.
     * 
     * @param toDelete The category to delete.
     * @return the parent of the category before it was
     *         deleted.
     */
    public IObjectMappingCategoryPO deleteCategory(
            IObjectMappingCategoryPO toDelete) {
        
        changeReuseOfCompNames(toDelete);
        
        IObjectMappingCategoryPO parent = toDelete.getParent();
        parent.removeCategory(toDelete);
        DataEventDispatcher.getInstance().fireDataChangedListener(
                parent, DataState.StructureModified, 
                UpdateState.onlyInEditor);
        DataEventDispatcher.getInstance().fireDataChangedListener(
                toDelete, DataState.Deleted, 
                UpdateState.onlyInEditor);
        return parent;
    }
    
    /**
     * Removes the used Component Names
     * @param toDelete the category to delete
     */
    private void changeReuseOfCompNames(IObjectMappingCategoryPO toDelete) {
        for (IObjectMappingAssoziationPO assoc
                : toDelete.getUnmodifiableAssociationList()) {
            removeCompNames(assoc);
        }
        for (IObjectMappingCategoryPO cat
                : toDelete.getUnmodifiableCategoryList()) {
            changeReuseOfCompNames(cat);
        }
    }

    /**
     * Deletes the given Component Name from the object map.
     * 
     * @param toDelete The Component Name to delete.
     * @param fromEditor indicates that the deletion initiated from the Editor
     * @return the category to which the Component Name belonged before it was
     *         deleted.
     */
    public IObjectMappingCategoryPO deleteCompName(
            IComponentNamePO toDelete, boolean fromEditor) {
        IObjectMappingCategoryPO originalCategory = null;
        IWritableComponentNameCache cNCache = getEditor().getCompNameCache();
        IObjectMappingAssoziationPO parent = getAssociation(toDelete.getGuid());
        if (parent == null) {
            return null;
        }
        cNCache.changeReuse(parent, toDelete.getGuid(), null);

        IObjectMappingCategoryPO category = parent.getCategory();
        originalCategory = category;

        if (parent.getLogicalNames().isEmpty()) {
            if (originalCategory != null) {
                // can be null when the Component Name was never in the database
                originalCategory.removeAssociation(parent);
            }
            if (parent.getTechnicalName() != null) {
                // Move association to appropriate section/category
                Stack<String> catPath = new Stack<String>();
                while (category.getParent() != null) {
                    catPath.push(category.getName());
                    category = category.getParent();
                }
                IObjectMappingCategoryPO newCategory = getEditor().getAut()
                        .getObjMap().getUnmappedTechnicalCategory();
                while (!catPath.isEmpty()) {
                    String catName = catPath.pop();
                    IObjectMappingCategoryPO subcategory = 
                        findSubcategory(newCategory, catName);
                    if (subcategory == null) {
                        // Create new category
                        subcategory = 
                            PoMaker.createObjectMappingCategoryPO(catName,
                                    getEditor().getAut());
                        newCategory.addCategory(subcategory);
                    }
                    newCategory = subcategory;
                }
                newCategory.addAssociation(parent);
            } else {
                // Delete empty association from session
                getEditor().getEntityManager().remove(parent);
            }
        }
        if (fromEditor && CompNameManager.getInstance()
                .getResCompNamePOByGuid(toDelete.getGuid()) == null) {
            // Comp Name is not in the DB, so we need to remove it from the cache
            // if it is still in the DB, we cannot throw it, because its type may have changed
            // due to removal from the Editor
            cNCache.removeCompName(toDelete.getGuid());
        }
        DataEventDispatcher.getInstance().fireDataChangedListener(
                getEditor().getAut().getObjMap(), 
                DataState.StructureModified, 
                UpdateState.onlyInEditor);            
        
        return originalCategory;
    }

    /**
     * Deletes the association from the object map. The corresponding Technical
     * Name is deleted as well.
     * 
     * @param toDelete The association to delete.
     * @return the category to which the association belonged before deletion.
     */
    public IObjectMappingCategoryPO deleteAssociation(
            IObjectMappingAssoziationPO toDelete) {

        removeCompNames(toDelete);
        IObjectMappingCategoryPO parent = toDelete.getCategory();
        parent.removeAssociation(toDelete);
        getEditor().getAut().getObjMap().removeAssociationFromCache(toDelete);
        DataEventDispatcher.getInstance().fireDataChangedListener(
                parent, DataState.StructureModified, 
                UpdateState.onlyInEditor);
        return parent;
    }
    
    /**
     * Changes the usage of the Component Names in the association
     * @param assoc the association
     */
    private void removeCompNames(IObjectMappingAssoziationPO assoc) {
        // making a copy to avoid Concurrent Modification
        List<String> guids = new ArrayList<>(assoc.getLogicalNames());
        for (String guid : guids) {
            getEditor().getCompNameCache().changeReuse(assoc, guid, null);
        }
    }

    /**
     * 
     * @param category The category in which to look for the subcategory.
     * @param subcategoryName The name of the subcategory to find.
     * @return the category that is a child of <code>category</code> with name
     *         <code>subcategoryName</code>, or <code>null</code> if no such
     *         category can be found.
     */
    private IObjectMappingCategoryPO findSubcategory(
            IObjectMappingCategoryPO category, String subcategoryName) {
        
        for (IObjectMappingCategoryPO subcategory 
                : category.getUnmodifiableCategoryList()) {
            
            if (subcategoryName.equals(subcategory.getName())) {
                return subcategory;
            }
        }
        
        return null;
    }

    /**
     * 
     * @param compNameGuid The GUID of the Component Name for which to find
     *                     the corresponding association.
     * @return The association for the Component Name with the given GUID.
     */
    public IObjectMappingAssoziationPO getAssociation(String compNameGuid) {
        IObjectMappingPO objMap = getEditor().getAut().getObjMap();
        return objMap.getLogicalNameAssoc(compNameGuid);        
    }

    /**
     * checks if a category name exists
     * 
     * !! does not work recursivly, just checks in given INodePO !!
     * 
     * @param name      String
     * @param start     GuiNode
     * @return          boolean
     */
    public boolean existCategory(IObjectMappingCategoryPO start, String name) {
        for (IObjectMappingCategoryPO child 
                : start.getUnmodifiableCategoryList()) {
            if (child.getName().equals(name)) { 
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @param compNamePo The Component Name to check.
     * @return the top-level category to which the given Component Name belongs.
     */
    public IObjectMappingCategoryPO getSection(
            IComponentNamePO compNamePo) {
        
        IObjectMappingAssoziationPO assoc = 
            getAssociation(compNamePo.getGuid());
        if (assoc != null) {
            return OMEditorDndSupport.getSection(assoc);
        }

        return null;
    }

    /**
     * 
     * @param compNamePo The Component Name for which to find the category.
     * @return the category to which <code>compNamePo</code> belongs.
     */
    public IObjectMappingCategoryPO getCategory(
            final IComponentNamePO compNamePo) {
        
        IObjectMappingAssoziationPO assoc = 
            getAssociation(compNamePo.getGuid());

        return assoc.getCategory();
    }

    /**
     * Creates and returns a category. The new category has the same path as
     * <code>subcategory</code> except for the top-level element of the path
     * (based on <code>section</code>), which may be different.
     * 
     * If the category to create already exists, then no category is created, 
     * and the existing category is returned.
     * 
     * @param section The top-level category in which to create the new 
     *                category.
     * @param subcategory The category with the path to use for the new 
     *                    category.
     * @return the created or pre-existing category.
     */
    public IObjectMappingCategoryPO createCategory(
            IObjectMappingCategoryPO section, 
            IObjectMappingCategoryPO subcategory) {
        
        IObjectMappingCategoryPO curCat = section;
        for (String pathEntry : getCatPath(subcategory)) {
            boolean childExists = false;
            for (IObjectMappingCategoryPO child 
                    : curCat.getUnmodifiableCategoryList()) {
                if (child.getName().equals(pathEntry)) {
                    curCat = child;
                    childExists = true;
                    break;
                }
            }
            if (!childExists) {
                IObjectMappingCategoryPO newCat = 
                    PoMaker.createObjectMappingCategoryPO(pathEntry,
                            getEditor().getAut());
                curCat.addCategory(newCat);
                curCat = newCat;
            }
        }
        
        return curCat;
    }

    /**
     * 
     * @param category The category for which to get the path.
     * @return the path for the given category, excluding the section.
     */
    private List<String> getCatPath(IObjectMappingCategoryPO category) {
        List<String> catPath = new ArrayList<String>();
        IObjectMappingCategoryPO curCat = category;
        while (curCat.getParent() != null) {
            catPath.add(curCat.getName());
            curCat = curCat.getParent();
        }
        Collections.reverse(catPath);
        return catPath;
    }
    
    /**
     * collect new logical component names / refresh object mapping editor
     */
    public void collectNewLogicalComponentNames() {
        if (getEditor().cleanupNames() > 0) {
            DataEventDispatcher.getInstance().fireDataChangedListener(
                    getEditor().getAut().getObjMap(), 
                    DataState.StructureModified, UpdateState.onlyInEditor);
        }
    }

    /**
     * @param editor the editor to set
     */
    private void setEditor(ObjectMappingMultiPageEditor editor) {
        m_editor = editor;
    }

    /**
     * @return the editor
     */
    private ObjectMappingMultiPageEditor getEditor() {
        return m_editor;
    }
}