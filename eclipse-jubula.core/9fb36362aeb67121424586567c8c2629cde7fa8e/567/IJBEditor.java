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
package org.eclipse.jubula.client.ui.rcp.editors;

import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.persistence.IEntityManagerProvider;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.rcp.events.GuiEventDispatcher.IEditorDirtyStateListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart2;


/**
 * Encapsulates the functionality expected of all Jubula editors.
 *
 * @author BREDEX GmbH
 * @created Oct 21, 2008
 */
public interface IJBEditor extends IEditorPart, IWorkbenchPart2, 
    IEditorDirtyStateListener, IDataChangedListener, IEntityManagerProvider {

    /**
     * 
     * @return an editor helper.
     */
    public JBEditorHelper getEditorHelper();
    
    /**
     * 
     * @return the name/title prefix for all editors of this type.
     */
    public String getEditorPrefix();

    /**
     * Reopens the Editor with the changed node
     * 
     * @param node the changed node of this editor.
     * @throws PMException if the node can not be loaded
     */
    public abstract void reOpenEditor(IPersistentObject node) 
        throws PMException;

    /**
     * @return The parent composite of this workbench part.
     */
    public Composite getParentComposite();

    /**
     * 
     * @return the image used to represent this type of editor when disabled.
     */
    public Image getDisabledTitleImage();
    
    /**
     * Callback method for initializing the name/title of the editor as well
     * as the editor input.
     * 
     * @param site the editor site
     * @param input the editor input
     */
    public void initTextAndInput(IEditorSite site, IEditorInput input);

    /**
     * Callback method for firing an <code>EDITOR_DIRTY</code> notification. 
     * 
     * @param isDirty whether the editor is now dirty.
     */
    public void fireDirtyProperty(boolean isDirty);
    
    /**
     * Returns the Comp Name Cache
     * @return the cache
     */
    public IWritableComponentNameCache getCompNameCache();
}