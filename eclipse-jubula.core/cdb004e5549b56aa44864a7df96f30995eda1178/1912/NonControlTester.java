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
package org.eclipse.jubula.rc.javafx.tester;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.WidgetTester;

/**
 * Tester class for components which are not of the type control.
 * @author BREDEX GmbH
 * @created 19.3.2014
 */
public class NonControlTester extends WidgetTester {

    @Override
    public void rcPopupSelectByIndexPath(String indexPath, int button)
        throws StepExecutionException {
        StepExecutionException.throwUnsupportedAction();
    }

    @Override
    public void rcPopupSelectByTextPath(String textPath, String operator,
        int button) throws StepExecutionException {
        StepExecutionException.throwUnsupportedAction();
    }

    @Override
    public void rcPopupSelectByTextPath(int xPos, String xUnits, int yPos,
        String yUnits, String textPath, String operator, int button)
        throws StepExecutionException {
        StepExecutionException.throwUnsupportedAction();
    }

    @Override
    public void rcPopupSelectByIndexPath(int xPos, String xUnits, int yPos,
        String yUnits, String indexPath, int button)
        throws StepExecutionException {
        StepExecutionException.throwUnsupportedAction();
    }

    @Override
    public void rcPopupVerifyEnabledByIndexPath(String indexPath,
        boolean enabled, int button, int timeout) 
                throws StepExecutionException {
        StepExecutionException.throwUnsupportedAction();
    }

    @Override
    public void rcPopupVerifyEnabledByIndexPath(int xPos, String xUnits,
        int yPos, String yUnits, String indexPath, boolean enabled,
        int button, int timeout)
        throws StepExecutionException {
        StepExecutionException.throwUnsupportedAction();
    }

    @Override
    public void rcPopupVerifyEnabledByTextPath(String textPath,
        String operator, boolean enabled, int button, int timeout)
        throws StepExecutionException {
        StepExecutionException.throwUnsupportedAction();
    }

    @Override
    public void rcPopupVerifyEnabledByTextPath(int xPos, String xUnits,
        int yPos, String yUnits, String textPath, String operator,
        boolean enabled, int button, int timeout) 
                throws StepExecutionException {
        StepExecutionException.throwUnsupportedAction();
    }

    @Override
    public void rcPopupVerifySelectedByIndexPath(String indexPath,
        boolean selected, int button, int timeout) 
                throws StepExecutionException {
        StepExecutionException.throwUnsupportedAction();
    }

    @Override
    public void rcPopupVerifySelectedByIndexPath(int xPos, String xUnits,
        int yPos, String yUnits, String indexPath, boolean selected,
        int button, int timeout)
        throws StepExecutionException {
        StepExecutionException.throwUnsupportedAction();
    }

    @Override
    public void rcPopupVerifySelectedByTextPath(String textPath,
        String operator, boolean selected, int button, int timeout)
        throws StepExecutionException {
        StepExecutionException.throwUnsupportedAction();
    }

    @Override
    public void rcPopupVerifySelectedByTextPath(int xPos, String xUnits,
        int yPos, String yUnits, String textPath, String operator,
        boolean selected, int button, int timeout) 
                throws StepExecutionException {
        StepExecutionException.throwUnsupportedAction();
    }

    @Override
    public void rcPopupVerifyExistsByIndexPath(String indexPath,
        boolean exists, int button, int timeout) throws StepExecutionException {
        StepExecutionException.throwUnsupportedAction();
    }

    @Override
    public void rcPopupVerifyExistsByIndexPath(int xPos, String xUnits,
        int yPos, String yUnits, String indexPath, boolean exists, int button,
        int timeout)
        throws StepExecutionException {
        StepExecutionException.throwUnsupportedAction();
    }

    @Override
    public void rcPopupVerifyExistsByTextPath(String textPath, String operator,
        boolean exists, int button, int timeout) throws StepExecutionException {
        StepExecutionException.throwUnsupportedAction();
    }

    @Override
    public void rcPopupVerifyExistsByTextPath(int xPos, String xUnits,
        int yPos, String yUnits, String textPath, String operator,
        boolean exists, int button, int timeout) throws StepExecutionException {
        StepExecutionException.throwUnsupportedAction();
    }
}
