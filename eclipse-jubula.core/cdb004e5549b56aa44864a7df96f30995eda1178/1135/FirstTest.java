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
package org.eclipse.jubula.examples.api.adder.tests;

import org.eclipse.jubula.examples.api.adder.SimpleAdder;
import org.eclipse.jubula.toolkit.enums.ValueSets.InteractionMode;
import org.eclipse.jubula.toolkit.enums.ValueSets.Operator;
import org.junit.Test;

/**
 * @author BREDEX GmbH
 */
public class FirstTest {
    /**
     * 
     * @param sa the SimpleAdder class
     */
    @Test
    private void test1(SimpleAdder sa) {
        sa.getValue1().replaceText("1"); //$NON-NLS-1$
        sa.getValue2().replaceText("2"); //$NON-NLS-1$
        sa.getCalculate().click(1, InteractionMode.primary);
        sa.getResult().checkText("3", Operator.equals); //$NON-NLS-1$
    }
}
