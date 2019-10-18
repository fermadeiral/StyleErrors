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


import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.driver.RobotConfiguration;
import org.eclipse.jubula.rc.common.driver.RobotTiming;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.toolkit.enums.ValueSets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;


/**
 * @author BREDEX GmbH
 * @created 25.07.2006
 */
public abstract class WindowActivationMethod {
    
    /**
     * no operation 
     */
    private static class NoneMethod extends WindowActivationMethod {
        /**
         * constructor
         * @param robot robot
         * @param queuer queuer
         */
        public NoneMethod(SwtRobot robot, IEventThreadQueuer queuer) {
            super(robot, queuer);
        }
        /**
         * {@inheritDoc}
         */
        protected Point getClickPoint(Shell window) {
            return null;
        }
        /**
         * {@inheritDoc}
         */
        public void activate(Shell window) {
            // do nothing
        }
    }
    /**
     * clicks into the title
     */
    private static class TitleMethod extends WindowActivationMethod {
        /**
         * constructor
         * @param robot robot
         * @param queuer queuer
         */
        public TitleMethod(SwtRobot robot, IEventThreadQueuer queuer) {
            super(robot, queuer);
        }
        /**
         * {@inheritDoc}
         */
        protected Point getClickPoint(Shell window) {
            return new Point(SwtUtils.getWidgetBounds(window).width / 2, 3);
        }
    }
    /**
     * clicks into the upper left corner
     */
    private static class NorthWestMethod extends WindowActivationMethod {
        /**
         * constructor
         * @param robot robot
         * @param queuer queuer
         */
        public NorthWestMethod(SwtRobot robot, IEventThreadQueuer queuer) {
            super(robot, queuer);
        }
        /**
         * {@inheritDoc}
         */
        protected Point getClickPoint(Shell window) {
            return new Point(0, 0);
        }
    }
    /**
     * clicks into the upper right corner
     */
    private static class NorthEastMethod extends WindowActivationMethod {
        /**
         * constructor
         * @param robot robot
         * @param queuer queuer
         */
        public NorthEastMethod(SwtRobot robot, IEventThreadQueuer queuer) {
            super(robot, queuer);
        }
        /**
         * {@inheritDoc}
         */
        protected Point getClickPoint(Shell window) {
            return new Point(SwtUtils.getWidgetBounds(window).width - 1, 0);
        }
    }
    /**
     * clicks into the bottom left corner 
     */
    private static class SouthWestMethod extends WindowActivationMethod {
        /**
         * constructor
         * @param robot robot
         * @param queuer queuer
         */
        public SouthWestMethod(SwtRobot robot, IEventThreadQueuer queuer) {
            super(robot, queuer);
        }
        /**
         * {@inheritDoc}
         */
        protected Point getClickPoint(Shell window) {
            return new Point(0, SwtUtils.getWidgetBounds(window).height - 1);
        }
    }
    /**
     * clicks into the center
     */
    private static class CenterMethod extends WindowActivationMethod {
        /**
         * constructor
         * @param robot robot
         * @param queuer queuer
         */
        public CenterMethod(SwtRobot robot, IEventThreadQueuer queuer) {
            super(robot, queuer);
        }
        /**
         * {@inheritDoc}
         */
        protected Point getClickPoint(Shell window) {
            return new Point(SwtUtils.getWidgetBounds(window).width  / 2, 
                    SwtUtils.getWidgetBounds(window).height  / 2);
        }
    }
    /**
     * clicks into the bottom right corner 
     */
    private static class SouthEastMethod extends WindowActivationMethod {
        /**
         * constructor
         * @param robot robot
         * @param queuer queuer
         */
        public SouthEastMethod(SwtRobot robot, IEventThreadQueuer queuer) {
            super(robot, queuer);
        }
        /**
         * {@inheritDoc}
         */
        protected Point getClickPoint(Shell window) {
            return new Point(SwtUtils.getWidgetBounds(window).width - 1, 
                    SwtUtils.getWidgetBounds(window).height - 1);
        }
    }

    /**
     * button used to activate the window
     */
    private static final int ACTIVATE_BTN = SWT.BUTTON1;
    
    /** robot */
    private final SwtRobot m_robot;
    
    /** queuer */
    private final IEventThreadQueuer m_queuer;
    
    /**
     * constructor
     * @param robot swt robot
     * @param queuer queuer
     */
    protected WindowActivationMethod(SwtRobot robot, 
            IEventThreadQueuer queuer) {
        
        m_robot = robot;
        m_queuer = queuer;
    }
    
    /**
     * The point that is clicked to activate the window
     * @param window the window
     * @return the point
     */
    protected abstract Point getClickPoint(Shell window);
    
    /**
     * activates the window
     * @param window window
     */
    public void activate(final Shell window) {
        m_queuer.invokeAndWait("window activate", new IRunnable<Void>() { //$NON-NLS-1$
            public Void run() throws StepExecutionException {
                Point pos = window.getLocation();
                Point cp = getClickPoint(window);
                m_robot.mouseMove(pos.x + cp.x, pos.y + cp.y);
                m_robot.mousePress(ACTIVATE_BTN);
                m_robot.mouseRelease(ACTIVATE_BTN);
                RobotTiming.sleepPostWindowActivationDelay();
                return null;
            }
        });
    }
    
    /**
     * creates an activation method
     * @param method a string representation of the method
     * @param robot the robot
     * @param queuer the queuer
     * @return the method
     */
    public static WindowActivationMethod createWindowActivationMethod(
        String method, SwtRobot robot, IEventThreadQueuer queuer) {
        
        if (ValueSets.AUTActivationMethod.autDefault.rcValue().equals(method)) {
            return createWindowActivationMethod(
                RobotConfiguration.getInstance().getDefaultActivationMethod(),
                robot, queuer);
        } else if (ValueSets.AUTActivationMethod.none.rcValue()
                .equals(method)) {
            return new NoneMethod(robot, queuer);
        } else if (ValueSets.AUTActivationMethod.titlebar.rcValue()
                .equals(method)) {
            return new TitleMethod(robot, queuer);
        } else if (ValueSets.AUTActivationMethod.northwest.rcValue()
                .equals(method)) {
            return new NorthWestMethod(robot, queuer);
        } else if (ValueSets.AUTActivationMethod.northeast.rcValue()
                .equals(method)) {
            return new NorthEastMethod(robot, queuer);
        } else if (ValueSets.AUTActivationMethod.southwest.rcValue()
                .equals(method)) {
            return new SouthWestMethod(robot, queuer);
        } else if (ValueSets.AUTActivationMethod.southeast.rcValue()
                .equals(method)) {
            return new SouthEastMethod(robot, queuer);
        } else if (ValueSets.AUTActivationMethod.center.rcValue()
                .equals(method)) {
            return new CenterMethod(robot, queuer);
        } else {
            return new NoneMethod(robot, queuer);
        }
    }
}