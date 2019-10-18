/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.ext.rc.javafx.tester;

import javafx.scene.chart.PieChart;

import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.WidgetTester;
import org.eclipse.jubula.rc.common.util.Verifier;


/** @author BREDEX GmbH */
public class PieChartTester extends WidgetTester {
    /**
     * @return the casted PieChart instance
     */ 
    protected PieChart getPieChart() {
        return (PieChart) getRealComponent();
    }

    /**
     * Verifies the number of items in the pie chart.
     * 
     * @param expected
     *            The expected number of items.
     */
    public void rcVerifyNrItems(int expected) {
        final PieChart pieChart = getPieChart();
        final int actual = getEventThreadQueuer().invokeAndWait(
                "verifyNrItems", new IRunnable<Integer>() { //$NON-NLS-1$
                    public Integer run() throws StepExecutionException {
                        return pieChart.getData().size();
                    }
                });
        Verifier.equals(expected, actual);
    }
}