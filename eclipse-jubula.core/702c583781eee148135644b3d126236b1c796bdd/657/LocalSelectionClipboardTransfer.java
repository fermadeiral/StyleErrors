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
package org.eclipse.jubula.client.ui.rcp.controllers.dnd;

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * This class is based on Eclipse's LocalSelectionClipboardTransfer. From the 
 * comment for that class:
 * -------------------------------------------------------------------------
 * A LocalSelectionTransfer may be used for drag and drop operations
 * within the same instance of Eclipse.
 * The selection is made available directly for use in the DropTargetListener.
 * dropAccept method. The DropTargetEvent passed to dropAccept does not contain
 * the drop data. The selection may be used for validation purposes so that the
 * drop can be aborted if appropriate.
 *
 * This class is not intended to be subclassed.
 * -------------------------------------------------------------------------
 * 
 * This class is used for clipboard opeations. It has been created separately
 * so that the drag and drop operations do not share a temporary storage
 * area.
 * 
 * @author BREDEX GmbH
 * @created 19.03.2008
 */
public class LocalSelectionClipboardTransfer extends ByteArrayTransfer {

    // First attempt to create a UUID for the type name to make sure that
    // different Eclipse applications use different "types" of
    // <code>LocalSelectionTransfer</code>
    /**
     * type name
     */
    private static final String TYPE_NAME = "local-selection-transfer-format" + (new Long(System.currentTimeMillis())).toString(); //$NON-NLS-1$;

    /**
     * type - id
     */
    private static final int TYPEID = registerType(TYPE_NAME);
    
    /**
     * is <code>true</code> when the last action is cut.
     */
    private static boolean isCut = false;

    /**
     * instance
     */
    private static final LocalSelectionClipboardTransfer INSTANCE = 
        new LocalSelectionClipboardTransfer();

    /** source of drag and drop action */
    private StructuredViewer m_source;

    /** viewers that should be refreshed (in addition to the source viewer) */
    private StructuredViewer[] m_otherViewersToRefresh = 
        new StructuredViewer[0];
    
    /** selection */
    private IStructuredSelection m_selection;

    /**
     * Only the singleton instance of this class may be used.
     */
    private LocalSelectionClipboardTransfer() {
    // empty
    }

    /**
     * Returns the singleton.
     * 
     * @return LocalSelectionClipboardTransfer
     */
    public static LocalSelectionClipboardTransfer getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the local transfer data.
     * 
     * @return the local transfer data
     */
    public IStructuredSelection getSelection() {
        return m_selection;
    }

    /**
     * @return <code>true</code> when the last action is cut.
     */
    public boolean getIsItCut() {
        return isCut;
    }

    /**
     * Tests whether native drop data matches this transfer type.
     * 
     * @param result
     *            result of converting the native drop data to Java
     * @return true if the native drop data does not match this transfer type.
     *         false otherwise.
     */
    private boolean isInvalidNativeType(Object result) {
        return !(result instanceof byte[])
                || !TYPE_NAME.equals(new String((byte[])result));
    }

    /**
     * Returns the type id used to identify this transfer.
     * 
     * @return the type id used to identify this transfer.
     */
    protected int[] getTypeIds() {
        return new int[] { TYPEID };
    }

    /**
     * Returns the type name used to identify this transfer.
     * 
     * @return the type name used to identify this transfer.
     */
    protected String[] getTypeNames() {
        return new String[] { TYPE_NAME };
    }

    /**
     * Overrides org.eclipse.swt.dnd.ByteArrayTransfer#javaToNative(Object,
     * TransferData). Only encode the transfer type name since the selection is
     * read and written in the same process.
     * 
     * {@inheritDoc}
     */
    public void javaToNative(Object object, TransferData transferData) {
        byte[] check = TYPE_NAME.getBytes();
        super.javaToNative(check, transferData);
    }

    /**
     * Overrides
     * org.eclipse.swt.dnd.ByteArrayTransfer#nativeToJava(TransferData). Test if
     * the native drop data matches this transfer type.
     * 
     * {@inheritDoc}
     */
    public Object nativeToJava(TransferData transferData) {
        Object result = super.nativeToJava(transferData);
        if (isInvalidNativeType(result)) {
            // dummy Method
            isInvalidNativeType(result);
        }
        return m_selection;
    }

    /**
     * Sets the transfer and source data for local use.
     * 
     * @param sel
     *            The transfer data. A value of <code>null</code> clears the
     *            transfer data.
     * @param source
     *            The source to set. A value of <code>null</code> clears the
     *            source data.
     * @param isItCut <code>true</code> when the action is cut.
     */
    public void setSelection(IStructuredSelection sel, 
            StructuredViewer source, boolean isItCut) {
        
        setSelection(sel, source, null, isItCut);
    }

    /**
     * Sets the transfer and source data for local use.
     * 
     * @param sel
     *            The transfer data. A value of <code>null</code> clears the
     *            transfer data.
     * @param source
     *            The source to set. A value of <code>null</code> clears the
     *            source data.
     * @param otherViewersToRefresh
     *            Viewers that should be updated (in addition to the source 
     *            viewer) after the selection change. May be <code>null</code>
     *            or empty, in which case no additional viewers will be updated.
     * @param isItCut <code>true</code> when the action is cut.
     */
    public void setSelection(IStructuredSelection sel, 
            StructuredViewer source, StructuredViewer[] otherViewersToRefresh,
            boolean isItCut) {
        
        IStructuredSelection oldSelection = getSelection();
        StructuredViewer oldSource = getSource();
        StructuredViewer[] oldViewersToRefresh = getOtherViewersToRefresh();
        setSelection(sel, isItCut);
        setSource(source);
        setOtherViewersToRefresh(otherViewersToRefresh);
        
        if (oldSource != null && !oldSource.getControl().isDisposed()
                && oldSelection != null) {
            // Allows the other item previously marked as "cut" to 
            // now be marked as normal.
            oldSource.update(oldSelection.toArray(), null);
            for (StructuredViewer toRefresh : oldViewersToRefresh) {
                if (toRefresh != oldSource) {
                    toRefresh.update(oldSelection.toArray(), null);
                }
            }
        }


        // Refresh the viewer so that it can show the selection as "cut".
        if (source != null) {
            source.update(sel.toArray(), null);
            for (StructuredViewer toRefresh : getOtherViewersToRefresh()) {
                if (toRefresh != source) {
                    toRefresh.update(sel.toArray(), null);
                }
            }
        }
    }

    /**
     * Sets the transfer data for local use.
     * 
     * @param sel
     *            The transfer data. A value of <code>null</code> clears the
     *            transfer data.
     * @param isItCut <code>true</code> when the action is cut.
     */
    public void setSelection(IStructuredSelection sel, boolean isItCut) {
        m_selection = sel;
        isCut = isItCut;
    }

    /**
     * checks if there are objects of different classes in selection
     * 
     * @return boolean
     */
    public boolean hasSingleClassType() {
        Class classType = null;
        Iterator iter = getSelection().iterator();

        while (iter.hasNext()) {
            Object obj = iter.next();
            // selectionitems must be same type
            if (classType == null) {
                classType = obj.getClass();
            }
            if (obj.getClass() != classType) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return the source
     */
    public StructuredViewer getSource() {
        return m_source;
    }

    /**
     * @param source
     *            The source to set. A value of <code>null</code> clears the
     *            source data.
     */
    private void setSource(StructuredViewer source) {
        m_source = source;
    }

    /**
     * @return the viewers that should be refreshed (in addition to the source
     *         viewer). This should never be <code>null</code>.
     */
    private StructuredViewer[] getOtherViewersToRefresh() {
        return m_otherViewersToRefresh;
    }
    
    /**
     * @param otherViewersToRefresh 
     *           Sets the viewers to update. Use <code>null</code> or an empty 
     *           array to avoid updating any additional viewers.
     */
    private void setOtherViewersToRefresh(
            StructuredViewer[] otherViewersToRefresh) {
        
        m_otherViewersToRefresh = otherViewersToRefresh != null 
            ? otherViewersToRefresh : new StructuredViewer[0];
    }
    
    /**
     * Checks whether all elements in the selection are instances of the given
     * class.
     * 
     * @param supportedClass The class/interface to check against. 
     * @return <code>true</code> if all elements in the selection are instances
     *         of the given class. Otherwise, <code>false</code>.
     */
    @SuppressWarnings("unchecked")
    public boolean containsOnlyType(Class supportedClass) {
        Iterator<Object> iter = getSelection().iterator();
        while (iter.hasNext()) {
            if (!supportedClass.isInstance(iter.next())) {
                return false;
            }
        }
        
        return true;
    }
}
