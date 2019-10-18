/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.utils;

/**
 * TreeNodeOperation which does not support the postOperating method
 * 
 * @param <T>
 *            The class of nodes handled by the operation.
 */
public abstract class AbstractNonPostOperatingTreeNodeOperation<T> implements
        ITreeNodeOperation<T> {
    /** {@inheritDoc} */
    public final void postOperate(ITreeTraverserContext<T> ctx, T parent,
            T node, boolean alreadyVisited) {
        // no post operation necessary
    }
}
