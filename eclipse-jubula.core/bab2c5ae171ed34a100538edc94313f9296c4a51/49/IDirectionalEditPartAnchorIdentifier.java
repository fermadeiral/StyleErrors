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
package org.eclipse.jubula.rc.rcp.e3.gef.identifier;

import java.util.Map;

import org.eclipse.draw2d.ConnectionAnchor;
/**
 * Extended part Identifier which can differentiate between incoming and outgoing
 * {@link ConnectionAnchor}s
 * 
 * @author BREDEX GmbH
 */
public interface IDirectionalEditPartAnchorIdentifier {

    /**
     *
     * @return a map that correlates identifiers (<code>String</code>s) with
     *         incoming <code>ConnectionAnchor</code>s.
     */
    public Map<String, ConnectionAnchor> getIncomingConnectionAnchors();

    /**
     *
     * @return a map that correlates identifiers (<code>String</code>s) with
     *         outgoing <code>ConnectionAnchor</code>s.
     */
    public Map<String, ConnectionAnchor> getOutgoingConnectionAnchors();
}
