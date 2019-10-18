/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.common.driver;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.rc.common.exception.StepVerifyFailedException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;

/**
 * this class is used to have an easy method to create a polling 
 * mechanisms with a timeout
 * @author BREDEX GmbH
 */
public class CheckWithTimeoutQueuer {
    
    /** the timout which is used can be set via environment variable */
    private static long waitTime = 100;
    
    /** ame of the environment variable that defines the polling delay*/
    private static String pollingDelayVar = "TEST_RC_POLLING_DELAY"; //$NON-NLS-1$
    
    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
            CheckWithTimeoutQueuer.class);
    /** Util class */
    private CheckWithTimeoutQueuer() {

    }
    
    /**
     * this is initializing the pollingDelay
     */
    static {
        String value = EnvironmentUtils
                .getProcessOrSystemProperty(pollingDelayVar);
        if (StringUtils.isNotBlank(value)) {
            try {
                waitTime = Long.valueOf(value).longValue();
                log.debug("Polling delay set to: " + waitTime); //$NON-NLS-1$
            } catch (Exception e) {
                log.error("Invalid value for polling delay: " + value, e); //$NON-NLS-1$
                // do nothing the waitTime is on its default
            }
        }
    }
    
    /**
     * 
     * @param name the name of the method for debugging purposes
     * @param timeout the timeout how long you want to wait
     * @param runnable the runnable which is doing the check
     */
    public static void invokeAndWait(String name, long timeout,
            Runnable runnable) {
        long startime = System.currentTimeMillis();
        while (System.currentTimeMillis() < (startime + timeout)) {
            try {
                log.debug(name);
                runnable.run();
                return;
            } catch (StepVerifyFailedException svfe) {
                if (System.currentTimeMillis() > (startime + timeout)) {
                    throw svfe;
                }
                // ignore until timeout is finished
            }
            TimeUtil.delay(waitTime);
        }
        runnable.run(); // this is the last try after timeout is over
    }
}
