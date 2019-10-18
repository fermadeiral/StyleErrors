/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.communication;

import org.eclipse.jubula.tools.ComponentIdentifier;

/**
 * CAPs are executed remotely on the {@link org.eclipse.jubula.client.AUT AUT}<br>
 * <b>C</b>: {@link org.eclipse.jubula.tools.ComponentIdentifier Component} to address <br>
 * <b>A</b>: Action to perform<br>
 * <b>P</b>: Parameter to use <br>
 * 
 * @author BREDEX GmbH
 * @created 13.10.2014
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CAP {
    /**
     * @return the component identifier used for this CAP; might be
     *         <code>null</code> e.g. for CAPs without a component identifier
     *         mapping
     */
    ComponentIdentifier getComponentIdentifier();
}
