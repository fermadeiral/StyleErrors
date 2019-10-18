/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.swt.driver;

import org.eclipse.jubula.rc.common.driver.IRobotEventConfirmer;
import org.eclipse.jubula.rc.common.driver.IRobotEventInterceptor;
import org.eclipse.jubula.rc.common.driver.InterceptorOptions;

/**
 * This class intercepts SWT events defined by the
 * <code>InterceptorOptions</code>. When <code>intercept()</code> is
 * called, a new
 * {@link org.eclipse.jubula.rc.swt.driver.RobotEventConfirmerSwtImpl} is
 * created and enabled, so that the confirmer starts collecting events at once.
 * 
 * @author BREDEX GmbH
 * @created 25.07.2006
 */
class RobotEventInterceptorSwtImpl implements
    IRobotEventInterceptor {
    /**
     * {@inheritDoc}
     */
    public IRobotEventConfirmer intercept(InterceptorOptions options) {
        RobotEventConfirmerSwtImpl confirmer = new RobotEventConfirmerSwtImpl(
            options);
        confirmer.setEnabled(true);
        return confirmer;
    }
}