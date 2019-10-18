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
package org.eclipse.jubula.client.ui.rcp.widgets;

import org.eclipse.jubula.client.ui.rcp.widgets.ModifiableListObservable.IContentAddedListener;
import org.eclipse.jubula.client.ui.rcp.widgets.ModifiableListObservable.IContentChangedListener;
import org.eclipse.jubula.client.ui.rcp.widgets.ModifiableListObservable.IContentRemovedListener;
import org.eclipse.jubula.client.ui.rcp.widgets.ModifiableListObservable.IOptionalButtonSelectedListener;
import org.eclipse.jubula.client.ui.rcp.widgets.ModifiableListObservable.ISelectionChangedListener;

/**
 * @author BREDEX GmbH
 * @created 13.06.2006
 */
public interface IModifiableListObservable {
    /**
     * @param listener listener for add events in the container
     */
    public void addContentAddedListener(IContentAddedListener listener);
    /**
     * @param listener listener for modification of an item in the container
     */
    public void addContentChangedListener(IContentChangedListener listener);
    /**
     * @param listener listener for removal of an item in the container
     */
    public void addContentRemovedListener(IContentRemovedListener listener);
    /**
     * @param listener listener for change of selection in the container
     */
    public void addSelectionChangedListener(ISelectionChangedListener listener);
    
    /**
     * @param listener listener for selection of optional button
     */
    public void addOptionalButtonSelectedListener(
        IOptionalButtonSelectedListener listener);
    /**
     * @param listener listener for add events in the container
     */
    public void removeContentAddedListener(IContentAddedListener listener);
    /**
     * @param listener listener for modification of an item in the container
     */
    public void removeContentChangedListener(IContentChangedListener listener);
    /**
     * @param listener listener for removal of an item in the container
     */
    public void removeContentRemovedListener(IContentRemovedListener listener);
    /**
     * @param listener listener for change of selection in the container
     */
    public void removeSelectionChangedListener
        (ISelectionChangedListener listener);
    
    /**
     * @param listener listener for selection of optional button
     */
    public void removeOptionalButtonSelectedListener(
        IOptionalButtonSelectedListener listener);
    /**
     * @param newValue newly added content
     */
    public void fireContentAdded(String newValue);
    
    /**
     * @param oldValue this value was just changed
     * @param newValue this is the new value
     */
    public void fireContentChanged(String oldValue, String newValue);    
    /**
     * @param oldValue the value which was just removed from the list
     */
    public void fireContentRemoved(String oldValue);    
    /**
     * @param value which value is selected
     */
    public void fireSelectionChanged(String value);
    
    /**
     * signals, that optionalButton was clicked
     */
    public void fireOptionalButtonSelected();


}
