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
package org.eclipse.jubula.rc.rcp.e3.gef.factory;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.EditPart;
import org.eclipse.jubula.rc.rcp.e3.gef.identifier.ClassCountEditPartIdentifier;
import org.eclipse.jubula.rc.rcp.e3.gef.identifier.IEditPartIdentifier;


/**
 * The default adapter factory for getting adapters from EditPart to
 * IEditPartIdentifier.
 *
 * @author BREDEX GmbH
 * @created May 13, 2009
 */
public class DefaultEditPartAdapterFactory implements IAdapterFactory {

    /** the types managed by this factory */
    private static final Class [] ADAPTABLE_TYPES =
        new Class [] {EditPart.class};

    /**
     * {@inheritDoc}
     */
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adaptableObject instanceof EditPart) {
            if (adapterType == IEditPartIdentifier.class) {
                return new ClassCountEditPartIdentifier(
                        (EditPart)adaptableObject);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Class[] getAdapterList() {
        return ADAPTABLE_TYPES;
    }

    /**
     *
     * @param editPart The EditPart for which to find the IEditPartIdentifier.
     * @return the IEditPartIdentifier for the given EditPart. First, an attempt
     *         is made to load this from a registered adapter factory. If this
     *         does not provide an adapter, then this adapter factory is
     *         queried, providing a default implementation.
     */
    public static IEditPartIdentifier loadFigureIdentifier(EditPart editPart) {
        if (editPart == null) {
            // null-safe
            return null;
        }
        IEditPartIdentifier editPartIdentifier =
            (IEditPartIdentifier)Platform.getAdapterManager().loadAdapter(
                    editPart, IEditPartIdentifier.class.getName());
//        if (editPartIdentifier == null) {
//            AccessibleEditPart accessible =
//                (AccessibleEditPart)Platform.getAdapterManager().loadAdapter(
//                    editPart, AccessibleEditPart.class.getName());
//            AccessibleEvent e = new AccessibleEvent(IEditPartIdentifier.class);
//            accessible.getName(e);
//            if (e.result != null) {
//                editPartIdentifier = new StaticEditPartIdentifier(e.result);
//            }
//        }
        if (editPartIdentifier == null) {
            editPartIdentifier = new ClassCountEditPartIdentifier(editPart);
        }

        return editPartIdentifier;
    }
}
