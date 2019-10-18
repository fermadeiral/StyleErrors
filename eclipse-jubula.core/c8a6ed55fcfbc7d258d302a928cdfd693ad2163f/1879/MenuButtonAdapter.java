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
package org.eclipse.jubula.rc.javafx.tester.adapter;

import javafx.scene.Node;
import javafx.scene.control.MenuButton;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.AbstractMenuTester;

/**
 * Adapter class for MenuButtons.
 * 
 * @author BREDEX GmbH
 * @created 20.10.2014
 *
 */
public class MenuButtonAdapter extends ButtonBaseAdapter {
    /**
     * Creates an object with the adapted MenuButton.
     *
     * @param objectToAdapt
     *            this must be an object of the Type <code>MenuButton</code>
     */
    public MenuButtonAdapter(MenuButton objectToAdapt) {
        super(objectToAdapt);
    }

    @Override
    public AbstractMenuTester showPopup(int xPos, String xUnits, int yPos,
            String yUnits, int button) throws StepExecutionException {
        Node n = getRealComponent();
        return openContextMenu(xPos, xUnits, yPos, yUnits, button, n);
    }
}
