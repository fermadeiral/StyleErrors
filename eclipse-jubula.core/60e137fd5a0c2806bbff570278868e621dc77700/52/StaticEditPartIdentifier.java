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

import java.util.Collections;
import java.util.Map;

import org.eclipse.draw2d.ConnectionAnchor;


/**
 * Provides a static String as an identifier. This class does not take the
 * state of the {@link org.eclipse.gef.EditPart} into account, so it is useful
 * for stateless {@link org.eclipse.gef.EditPart}s (ex. containers).
 *
 * @author BREDEX GmbH
 * @created May 19, 2009
 */
public class StaticEditPartIdentifier implements IEditPartIdentifier {

    /** the identifier that will always be returned */
    private String m_id;

    /**
     * Constructor
     *
     * @param id The static identifier String that will always be returned.
     */
    public StaticEditPartIdentifier(String id) {
        m_id = id;
    }

    /**
     * {@inheritDoc}
     */
    public String getIdentifier() {
        return m_id;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, ConnectionAnchor> getConnectionAnchors() {
        return Collections.EMPTY_MAP;
    }
}
