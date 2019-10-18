/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx;

import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.jubula.communication.internal.message.AUTServerStateMessage;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.exception.ComponentNotFoundException;
import org.eclipse.jubula.rc.common.listener.BaseAUTListener;
import org.eclipse.jubula.rc.common.listener.DisabledCheckListener;
import org.eclipse.jubula.rc.common.listener.DisabledRecordListener;
import org.eclipse.jubula.rc.common.registration.IRegisterAut;
import org.eclipse.jubula.rc.javafx.components.CurrentStages;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.driver.RobotFactoryJavaFXImpl;
import org.eclipse.jubula.rc.javafx.listener.AbstractFXAUTEventHandler;
import org.eclipse.jubula.rc.javafx.listener.ComponentHandler;
import org.eclipse.jubula.rc.javafx.listener.MappingListener;
import org.eclipse.jubula.tools.internal.constants.AUTServerExitConstants;
import org.eclipse.jubula.tools.internal.exception.JBVersionException;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;

import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * The AutServer controlling the AUT. <br>
 * A quasi singleton: the instance is created from main(). <br>
 * Expected arguments to main are, see also
 * StartAUTServerCommand.createCmdArray():
 * <ul>
 * <li>The name of host the client is running on, must be InetAddress conform.</li>
 * <li>The port the JubulaClient is listening to.</li>
 * <li>The main class of the AUT.</li>
 * <li>Any further arguments are interpreted as arguments to the AUT.</li>
 * <ul>
 * When a connection to the JubulaClient could made, any errors will send as a
 * message to the JubulaClient.
 *
 * Changing the mode to OBJECT_MAPPING results in installing an
 * JavaFXEventFilter on the assumed primary stage, which listens to the mouse-
 * and key.events relevant for OBJECT_MAPPING.
 *
 * Changing the mode removes the installed MappingListener.
 *
 * @author BREDEX GmbH
 * @created 24.09.2013
 */
public class JavaFXAUTServer extends AUTServer {
    /**
     * constructor instantiates the listeners
     */
    public JavaFXAUTServer() {
        super(new MappingListener(), 
                new DisabledRecordListener(),
                new DisabledCheckListener());
    }

    @Override
    protected void addToolkitEventListener(BaseAUTListener listener) {
        if (listener instanceof AbstractFXAUTEventHandler) {
            addToolkitEventListener((AbstractFXAUTEventHandler) listener);
        }
    }

    /**
     * Adds a handler to the stage
     *
     * @param handler
     *            the handler
     */
    private void addToolkitEventListener(AbstractFXAUTEventHandler handler) {
        List<? extends Window> windows = 
                ComponentHandler.getAssignableFrom(Window.class);
        for (final Window win : windows) {
            handler.addHandler(win);
        }
        CurrentStages.addStagesListener(handler);
    }

    @Override
    protected void addToolkitEventListeners() {
        addToolkitEventListener(new ComponentHandler());
    }

    @Override
    protected void removeToolkitEventListener(BaseAUTListener listener) {
        if (listener instanceof AbstractFXAUTEventHandler) {
            removeToolkitEventListener((AbstractFXAUTEventHandler) listener);
        }
    }

    /**
     * removes a handler from the stage
     *
     * @param handler
     *            the handler
     */
    private void removeToolkitEventListener(AbstractFXAUTEventHandler handler) {
        List<? extends Stage> stages = 
                ComponentHandler.getAssignableFrom(Stage.class);
        
        for (Stage stage : stages) {
            handler.removeHandler(stage);
        }
        
        CurrentStages.removeStagesListener(handler);
    }

    @Override
    protected void startTasks() throws ExceptionInInitializerError,
            InvocationTargetException, NoSuchMethodException {
        addToolKitEventListenerToAUT();
        invokeAUT();
    }

    @Override
    public IRobot<Rectangle> getRobot() {
        return RobotFactoryJavaFXImpl.INSTANCE.getRobot();
    }
    
    /**
     * {@inheritDoc}
     */
    public Object findComponent(IComponentIdentifier ci, int timeout)
        throws ComponentNotFoundException, IllegalArgumentException {
        return ComponentHandler.findComponent(ci, true, timeout);
    }
    
    

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isComponentDisappeared(IComponentIdentifier ci, int timeout)
            throws ComponentNotFoundException, IllegalArgumentException {
        return ComponentHandler.isComponentDisappeared(ci, timeout);
    }
    
    /**
     * Tries to schedule an empty runnable in the JavaFX-Thread
     * 
     * @return true if scheduling was successful and the toolkit is therefore
     *         initialized, false otherwise
     */
    private boolean checkInitialization() {
        try {
            return EventThreadQueuerJavaFXImpl.invokeAndWait("getStageByTitle", //$NON-NLS-1$
                    new Callable<Boolean>() {

                        @Override
                        public Boolean call() throws Exception {
                            // Do nothing, we are just checking if an Exception
                            // is thrown when the task is scheduled
                            return true;
                        }
                    });
        } catch (IllegalStateException e) {
            // Do nothing, toolkit not initialized
            return false;
        }
    }

    @Override
    protected void registerAutinAgent(IRegisterAut autReg)
            throws JBVersionException {
        new Thread(new Runnable() {

            @Override
            public void run() {
                do {
                    TimeUtil.delay(100);
                    // do nothing, wait to get the toolkit initialized
                } while (!checkInitialization());
                try {
                    JavaFXAUTServer.super.registerAutinAgent(autReg);
                } catch (JBVersionException e) {
                    sendExitReason(e,
                            AUTServerStateMessage.EXIT_AUT_WRONG_CLASS_VERSION);
                    System.exit(AUTServerExitConstants.EXIT_UNKNOWN_ITE_CLIENT);
                }
            }
        }).start();
    }

    @Override
    protected void connectToITE() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                do {
                    TimeUtil.delay(100);
                    // do nothing, wait to get the toolkit initialized
                } while (!checkInitialization());
                JavaFXAUTServer.super.connectToITE();
            }
        }).start();
    }
    
    

}