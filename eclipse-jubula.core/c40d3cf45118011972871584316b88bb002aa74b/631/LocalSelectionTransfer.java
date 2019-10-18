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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * A LocalSelectionTransfer may be used for drag and drop operations
 * within the same instance of Eclipse.
 * The selection is made available directly for use in the DropTargetListener.
 * dropAccept method. The DropTargetEvent passed to dropAccept does not contain
 * the drop data. The selection may be used for validation purposes so that the
 * drop can be aborted if appropriate.
 *
 * This class is not intended to be subclassed.
 * 
 * @since 2.1
 */
public class LocalSelectionTransfer extends ByteArrayTransfer {

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
     * instance
     */
    private static final LocalSelectionTransfer INSTANCE = 
        new LocalSelectionTransfer();

    /** source of drag and drop action */
    private Viewer m_source;
    
    /**
     * selection
     */
    private IStructuredSelection m_selection;

    /**
     * Only the singleton instance of this class may be used. 
     */
    private LocalSelectionTransfer() { 
        // empty
    }

    /**
     * Returns the singleton.
     * @return LocalSelectionTransfer
     */
    public static LocalSelectionTransfer getInstance() {
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
     * Tests whether native drop data matches this transfer type.
     * 
     * @param result result of converting the native drop data to Java
     * @return true if the native drop data does not match this transfer type.
     *  false otherwise.
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
     * TransferData).
     * Only encode the transfer type name since the selection is read and
     * written in the same process.
     * 
     * {@inheritDoc}
     */
    public void javaToNative(Object object, TransferData transferData) {
        byte[] check = TYPE_NAME.getBytes();
        super.javaToNative(check, transferData);
    }

    /**
     * Overrides org.eclipse.swt.dnd.ByteArrayTransfer#nativeToJava(TransferData).
     * Test if the native drop data matches this transfer type.
     * 
     * {@inheritDoc}
     */
    public Object nativeToJava(TransferData transferData) {
        Object result = super.nativeToJava(transferData);
        if (isInvalidNativeType(result)) {
            //dummy Method
            isInvalidNativeType(result);
        }
        return m_selection;
    }

    /**
     * Sets the transfer data for local use.
     * 
     * @param s the transfer data
     */
    public void setSelection(IStructuredSelection s) {
        m_selection = s;
    }

    /**
     * @return the source
     */
    public Viewer getSource() {
        return m_source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(Viewer source) {
        m_source = source;
    }

}
