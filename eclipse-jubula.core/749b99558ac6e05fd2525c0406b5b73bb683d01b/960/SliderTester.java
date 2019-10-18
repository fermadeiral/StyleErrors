/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.common.tester;

import org.eclipse.jubula.rc.common.driver.CheckWithTimeoutQueuer;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ISliderComponent;
import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;

/**
 * @author BREDEX GmbH
 */
public class SliderTester extends WidgetTester {

    /**
     * @return the <code>ISliderAdapter</code>
     */
    private ISliderComponent getSliderAdapter() {
        return (ISliderComponent) getComponent();
    }

    /**
     * Checks the position of the slider
     *
     * @param pos
     *            The expected position
     * @param operator
     *            The operator
     * @param units
     *            The units in which the position is defined (value/percent)
     * @param timeout the amaximum amount of time to wait for the position to be
     *          verified
     */
    public void rcVerifyPosition(final String pos, final String operator,
            final String units, int timeout) {
        if (pos == null) {
            throw new StepExecutionException("The position must not be null", //$NON-NLS-1$
                    EventFactory.createActionError());
        }
        CheckWithTimeoutQueuer.invokeAndWait("rcVerifyPosition", timeout,
                new Runnable() {
                    public void run() {
                        String actualPosition =
                                getSliderAdapter().getPosition(units);
                        if (!MatchUtil.getInstance().match(actualPosition, pos,
                                operator)) {
                            Verifier.throwVerifyFailed(pos, actualPosition);
                        }

                    }
                });
    }
    
    /**
     * Selects the position of the slider
     *
     * @param pos
     *            The position to select
     * @param operator
     *            The operator
     * @param units
     *            The units in which the position is defined (value/percent)
     */
    public void rcSelectPosition(String pos, String operator, String units) {
        getSliderAdapter().setPosition(pos, operator, units);
    }

}
