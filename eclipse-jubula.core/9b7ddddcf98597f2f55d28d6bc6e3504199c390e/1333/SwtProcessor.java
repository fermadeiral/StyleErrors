/*******************************************************************************
 * Copyright (c) 2004, 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.rcp.e4.swt.starter;

import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.jubula.rc.rcp.e4.namer.E4ComponentNamer;
import org.eclipse.jubula.rc.rcp.e4.starter.AbstractProcessor;
import org.eclipse.jubula.rc.rcp.e4.swt.namer.E4SwtComponentNamer;
import org.eclipse.jubula.rc.rcp.swt.aut.SwtRemoteControlService;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * The SWT implementation of the abstract e4 processor.
 */
public class SwtProcessor extends AbstractProcessor {
    /** The implementation of the e4 component namer interface. */
    private E4SwtComponentNamer m_componentNamer = new E4SwtComponentNamer();

    @Override
    protected E4ComponentNamer getE4ComponentNamer() {
        return m_componentNamer;
    }

    @Override
    protected void onModelWindowCreated(MWindow mWindow) {
        Shell shell = (Shell) mWindow.getWidget();
        Display display = shell.getDisplay();
        ((SwtRemoteControlService) SwtRemoteControlService.getInstance())
                .checkRemoteControlService(display, m_componentNamer);
    }
}