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
package org.eclipse.jubula.autagent.desktop;

import java.awt.event.ActionListener;

import org.eclipse.jubula.autagent.common.agent.AutAgent;
import org.eclipse.jubula.autagent.common.desktop.DesktopIntegration;
import org.eclipse.jubula.autagent.desktop.listener.StartOMActionListener;
import org.eclipse.jubula.autagent.desktop.listener.StopOMActionListener;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;


/**
 * @author BREDEX GmbH
 * @created 11.06.2010
 */
public class CoreDesktopIntegration extends DesktopIntegration {


    /**
     * create the necessary environment for the autagent
     * 
     * @param autAgent The AUT Agent monitored by the created object.
     */
    public CoreDesktopIntegration(AutAgent autAgent) {
        super(autAgent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionListener createStartListener(AutIdentifier id) {
        return new StartOMActionListener(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionListener createStopListener(AutIdentifier id) {
        return new StopOMActionListener(id);
    }
}