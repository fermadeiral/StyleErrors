/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.swt.tester;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.AbstractTooltipTester;


/**
 * @author BREDEX GmbH
 * @created 20.05.2015
 */
public class TooltipTester extends AbstractTooltipTester {

    @Override
    public String getTooltipText() {
        StepExecutionException.throwUnsupportedAction();
        return null;
    }
}
