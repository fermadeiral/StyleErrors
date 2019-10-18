/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.common.tester;

import org.eclipse.jubula.rc.common.driver.RobotTiming;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;

/**
 * General implementation for Tooltips.
 * 
 * @author BREDEX GmbH
 */
public abstract class AbstractTooltipTester extends AbstractUITester {

    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
            AbstractTooltipTester.class);

    /**
     * @return the log
     */
    public static AutServerLogger getLog() {
        return log;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        return null;
    }
    
    /**
     * 
     * @param timeout the timeout
     * @param text the tooltip text
     * @param operator the operator
     */
    public void rcCheckTooltipText(int timeout, String text, String operator) {
        String tooltipText = getTooltipText();
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeout
                && tooltipText == null) {
            RobotTiming.sleepWaitForComponentPollingDelay();
            tooltipText = getTooltipText();
        }
        if (tooltipText == null) {
            throw new StepExecutionException("No tooltip found.", //$NON-NLS-1$
                    EventFactory.createComponentNotFoundErrorEvent());
        }
        Verifier.match(tooltipText, text, operator);
    }
    
    /**
     * Abstract method for returning tooltip text.
     * Supposed to be implemented specific for each toolkit.
     * @return the text of the currently displayed tooltip,
     * or <code>null</code> if there is no tooltip
     */
    public abstract String getTooltipText();

}