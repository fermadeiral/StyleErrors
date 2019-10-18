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
package org.eclipse.jubula.client.ui.rcp.contributionitems;

import java.util.Comparator;

import org.eclipse.jubula.client.core.model.INodePO;


/**
 * @author BREDEX GmbH
 * @created Mar 5, 2010
 */
public class NodePOComparator implements Comparator<INodePO> {

    /**
     * {@inheritDoc}
     */
    public int compare(INodePO o1, INodePO o2) {
        return o1.getName().compareTo(o2.getName());
    }

}
