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
package org.eclipse.jubula.rc.common.driver;


/**
 * The interceptor intercepts a class of Graphics API specific events.
 * The class of events is definied by <code>InterceptorOptions</code>.
 * Whenever <code>intercept()</code> is called, the interceptor returns
 * a confirmer to confirm the intercepted event. The programming model
 * is as follows (this example shows how to use it with AWT/Swing): <br>
 * <pre>
 *       InterceptorOptions options = new InterceptorOptions(
 *           AWTEvent.MOUSE_EVENT_MASK);
 *       IRobotEventConfirmer confirmer = m_interceptor.intercept(options);
 *       robot.mousePress(buttonMask);
 *       robot.mouseRelease(buttonMask);
 *       confirmer.waitToConfirm(graphicsComponent, new ClickAwtEventMatcher(
 *           clickOptions));
 * </pre>
 * 
 * @author BREDEX GmbH
 * @created 17.03.2005
 */
public interface IRobotEventInterceptor {
    /**
     * Intercepts a class of events defined by the given interceptor
     * options. The returned confirmer can be used to confirm an
     * event that belongs to the event class.
     * 
     * @param options The interceptor options
     * @return The confirmer
     */
    public IRobotEventConfirmer intercept(InterceptorOptions options);
}
