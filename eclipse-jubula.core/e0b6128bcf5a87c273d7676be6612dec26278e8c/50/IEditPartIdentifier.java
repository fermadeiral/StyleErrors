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
 * Encapsulates the ability to generate unique IDs for a given
 * {@link org.eclipse.gef.EditPart}.
 *
 * @author BREDEX GmbH
 * @created May 13, 2009
 */
public interface IEditPartIdentifier {

    /**
     *
     * @return a String uniquely identifying the EditPart.
     */
    public String getIdentifier();

    /**
     *
     * @return a map that correlates identifiers (<code>String</code>s) with
     *         <code>ConnectionAnchor</code>s.
     */
    public Map<String, ConnectionAnchor> getConnectionAnchors();
}