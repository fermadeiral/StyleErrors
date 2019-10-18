/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.autagent.internal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.eclipse.jubula.autagent.Embedded;
import org.eclipse.jubula.autagent.common.AutStarter;
import org.eclipse.jubula.autagent.common.desktop.DesktopIntegration;
import org.eclipse.jubula.autagent.common.gui.ObjectMappingFrame;
import org.eclipse.jubula.client.AUT;
import org.eclipse.jubula.client.AUTAgent;
import org.eclipse.jubula.client.internal.impl.AUTImpl;
import org.eclipse.jubula.toolkit.ToolkitInfo;
import org.eclipse.jubula.toolkit.html.HtmlComponents;
import org.eclipse.jubula.toolkit.javafx.JavafxComponents;
import org.eclipse.jubula.toolkit.rcp.RcpComponents;
import org.eclipse.jubula.toolkit.swing.SwingComponents;
import org.eclipse.jubula.toolkit.swt.SwtComponents;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;

/**
 * @author BREDEX GmbH
 *
 */
public class EmbeddedDesktopIntegration extends DesktopIntegration {

    /** the currently selected AUT" */
    private static AUT aut;

    /**
     * create the necessary environment for the embedded API autagent
     */
    public EmbeddedDesktopIntegration() {
        super(AutStarter.getInstance().getAgent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionListener createStartListener(final AutIdentifier id) {
        return new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                AUTAgent agent = Embedded.INSTANCE.agent();
                boolean isDisconnected = !agent.isConnected();
                if (isDisconnected) {
                    agent.connect();
                }
                String toolkitid = AutStarter.getInstance().getAgent()
                        .getToolkitForAutID(id);
                ToolkitInfo info = null;
                if (toolkitid.equals(CommandConstants.JAVAFX_TOOLKIT)) {
                    info = JavafxComponents.getToolkitInformation();
                }
                if (toolkitid.equals(CommandConstants.SWING_TOOLKIT)) {
                    info = SwingComponents.getToolkitInformation();
                }
                if (toolkitid.equals(CommandConstants.SWT_TOOLKIT)) {
                    info = SwtComponents.getToolkitInformation();
                }
                if (toolkitid.equals(CommandConstants.RCP_TOOLKIT)) {
                    info = RcpComponents.getToolkitInformation();
                }
                if (toolkitid.equals(CommandConstants.HTML_TOOLKIT)) {
                    info = HtmlComponents.getToolkitInformation();
                }
                if (info != null) {
                    aut = agent.getAUT(id, info);
                }
                if (aut != null) {
                    aut.connect();
                    ((AUTImpl) aut).startOMM();
                    setObjectMappingAUT(id);
                    ObjectMappingFrame.INSTANCE.showObjectMappingPanel();
                }
                if (isDisconnected) {
                    agent.disconnect();
                }
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionListener createStopListener(final AutIdentifier id) {
        return new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                AUTAgent agent = Embedded.INSTANCE.agent();
                boolean isDisconnected = !agent.isConnected();
                if (isDisconnected) {
                    agent.connect();
                }
                ((AUTImpl) aut).stopOMM();
                aut.disconnect();
                aut = null;
                if (isDisconnected) {
                    agent.disconnect();
                }
            }
        };
    }

}
