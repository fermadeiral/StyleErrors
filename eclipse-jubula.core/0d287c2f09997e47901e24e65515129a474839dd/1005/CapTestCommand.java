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
package org.eclipse.jubula.rc.common.commands;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.CAPTestMessage;
import org.eclipse.jubula.communication.internal.message.CAPTestResponseMessage;
import org.eclipse.jubula.communication.internal.message.ChangeAUTModeMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.MessageCap;
import org.eclipse.jubula.communication.internal.message.MessageParam;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.AUTServerConfiguration;
import org.eclipse.jubula.rc.common.exception.ComponentNotFoundException;
import org.eclipse.jubula.rc.common.exception.EventSupportException;
import org.eclipse.jubula.rc.common.exception.ExecutionEvent;
import org.eclipse.jubula.rc.common.exception.MethodParamException;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.exception.StepVerifyFailedException;
import org.eclipse.jubula.rc.common.exception.UnsupportedComponentException;
import org.eclipse.jubula.rc.common.tester.AbstractUITester;
import org.eclipse.jubula.rc.common.tester.WidgetTester;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.tools.internal.constants.TimingConstantsServer;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class gets an message with ICommand action parameter triples. It invokes
 * the implementation class and executes the method. Then it creates a
 * <code>CAPTestResponseMessage</code> and sends it back to the client. The
 * <code>CAPTestResponseMessage</code> contains an error event only if the
 * test step fails, due to a problem prior to or during the execution of the
 * implementation class action method.
 * @author BREDEX GmbH
 * @created 02.01.2007
 */
public class CapTestCommand implements ICommand {
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger(
        CapTestCommand.class);
    /** The logger */
    private static final Logger CAPLOG = LoggerFactory.getLogger("CAP"); //$NON-NLS-1$
    
    
    /** The message. */
    private CAPTestMessage m_capTestMessage;

    /**
     * {@inheritDoc}
     */
    public Message getMessage() {
        return m_capTestMessage;
    }
    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_capTestMessage = (CAPTestMessage)message;
    }
    
    /**
     * Is called if the graphics component cannot be found. Logs the error and
     * sets the action error event into the message.
     * @param response The response message
     * @param e The exception.
     */
    private void handleComponentNotFound(CAPTestResponseMessage response,
        Throwable e) {
        if (LOG.isWarnEnabled()) {
            LOG.warn(e.getLocalizedMessage(), e);
        }
        response.setTestErrorEvent(EventFactory
                .createComponentNotFoundErrorEvent());
    }

    /**
     * Is called if one or more CAP parameters are invalid. Logs the error and
     * sets the action error event into the message.
     * @param e The error message.
     */
    private void handleInvalidInput(String e) {
        throw new StepExecutionException(e, EventFactory
                .createImplClassErrorEvent());
    }

    /**
     * Gets the implementation class. 
     * @param response The response message.
     * @return the implementation class or null if an error occurs.
     */
    protected Object getImplClass(CAPTestResponseMessage response) {
        Object implClass = null;
        final MessageCap messageCap = m_capTestMessage.getMessageCap();
        IComponentIdentifier ci = messageCap.getCi();
        if (LOG.isInfoEnabled()) {
            LOG.info("component class name: " //$NON-NLS-1$
                + (ci == null ? "(none)" : ci.getComponentClassName())); //$NON-NLS-1$
        }
        try {
            if (!messageCap.hasDefaultMapping()) {
                Validate.notNull(ci);
            }
            int timeout = 0;
            // FIXME : Extra handling for waitForComponent and verifyExists
            boolean isWaitForComponent = 
                    WidgetTester.RC_METHOD_NAME_WAIT_FOR_COMPONENT
                        .equals(messageCap.getMethod());
            boolean isCheckExistenceComponent = 
                    WidgetTester.RC_METHOD_NAME_CHECK_EXISTENCE
                        .equals(messageCap.getMethod());
            if (isWaitForComponent) { 
                timeout = getTimeoutParameter(messageCap, 0,
                        TimingConstantsServer.DEFAULT_FIND_COMPONENT_TIMEOUT);
            }
            final AUTServerConfiguration rcConfig = AUTServerConfiguration
                .getInstance();
            if (!messageCap.hasDefaultMapping()) {
                if (isCheckExistenceComponent) {
                    implClass = handleCheckComponentExistenceAction(messageCap,
                            ci, rcConfig);
                } else {
                    Object component = AUTServer.getInstance().findComponent(ci,
                            timeout);
                    implClass = rcConfig.prepareImplementationClass(component,
                            component.getClass());
                    if (implClass instanceof AbstractUITester) {
                        saveErrorComponent(((AbstractUITester) implClass)
                                .getComponent());
                    }
                }
             
            } else {
                implClass = rcConfig.getImplementationClass(ci
                    .getComponentClassName());
            }
            if (isWaitForComponent) {
                delayWaitingForComponent(messageCap);
            }
        } catch (IllegalArgumentException e) {
            handleComponentNotFound(response, e);
        } catch (ComponentNotFoundException e) {
            if (WidgetTester.RC_METHOD_NAME_CHECK_EXISTENCE
                    .equals(messageCap.getMethod())) {
                MessageParam isVisibleParam = 
                    messageCap.getMessageParams().get(0);
                handleComponentDoesNotExist(response, 
                    Boolean.valueOf(isVisibleParam.getValue())
                        .booleanValue());
            } else {
                handleComponentNotFound(response, e);
            }
        } catch (UnsupportedComponentException buce) {
            LOG.error(buce.getLocalizedMessage(), buce);
            response.setTestErrorEvent(EventFactory.createConfigErrorEvent());
        } catch (Throwable e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage(), e);
            }
            response.setTestErrorEvent(
                    EventFactory.createImplClassErrorEvent());
        } 
        return implClass;
    }
    
    /**
     * @param componentAdapter the adapter for the component at which the error occurred
     */
    private void saveErrorComponent(IComponent componentAdapter) {
        if (componentAdapter != null) {
            WeakReference<IComponent> errorCompRef =
                    new WeakReference<IComponent>(
                        componentAdapter);
            AUTServer.getInstance().
                setErrorComponent(errorCompRef);
        }
    }
    /**
     * Delay action on waiting for component
     * @param messageCap the CAP message data.
     */
    private void delayWaitingForComponent(final MessageCap messageCap) {
        MessageParam delayParam = messageCap.
                getMessageParams().get(1);
        try {
            int delay = Integer.parseInt(delayParam.getValue());
            TimeUtil.delay(delay);
        } catch (IllegalArgumentException iae) {
            handleInvalidInput("Invalid input: " //$NON-NLS-1$
                + CompSystemI18n.getString("CompSystem.DelayAfterVisibility") //$NON-NLS-1$
                + " must be a non-negative integer."); //$NON-NLS-1$
        }
    }
    
    /**
     * Handle the component exist action.
     * 
     * @param messageCap
     *            the CAP message data
     * @param ci
     *            the component identifier
     * @param rcConfig
     *            AUT server configuration
     * @return the implementation class or null if an error occurs.
     * @throws UnsupportedComponentException
     *             when an implementation class is requested, which was not configured
     * @throws ComponentNotFoundException
     *             when the component not found
     */
    private Object handleCheckComponentExistenceAction(
            final MessageCap messageCap, IComponentIdentifier ci, 
            final AUTServerConfiguration rcConfig) throws
                    UnsupportedComponentException, ComponentNotFoundException {
        Object implClass;
        // Get the expected existence
        MessageParam isVisibleParam = 
                messageCap.getMessageParams().get(0);
        boolean shouldBeVisible = Boolean.valueOf(isVisibleParam.getValue());
        // Get the timeout parameter
        int timeout = getTimeoutParameter(messageCap, 1, 0);
        
        Object component = null;
        try {
            // To check whether the component is disappeared, the timeout is not
            // used,
            // because have to know the fact immediately
            component = AUTServer.getInstance().findComponent(ci,
                    shouldBeVisible ? timeout : 0);
            if (!shouldBeVisible && component != null) {
                // This is the case, when the component is still not
                // disappeared, but it's expected
                // and to start a poll is necessary
                if (AUTServer.getInstance().isComponentDisappeared(ci,
                        timeout)) {
                    // This is the case, when the component successfully gone
                    throw new ComponentNotFoundException(
                            "Component disappeared", //$NON-NLS-1$
                            MessageIDs.E_COMPONENT_NOT_FOUND);
                }
            }
        } catch (ComponentNotFoundException e) {
            // The searched component is not found, throw exception
            // manage in common way
            throw e;
        }

        implClass = rcConfig.prepareImplementationClass(component,
            component.getClass());
        return implClass;
    }
    
    /**
     * 
     * @param messageCap
     *            the CAP message data
     * @param indexOfParameter
     *            index of the timeout parameter in the message data
     * @param defaultValue
     *            which will be set, if the message data does not contain
     *            timeout parameter
     * @return timeout parameter
     */
    private int getTimeoutParameter(final MessageCap messageCap,
            int indexOfParameter, int defaultValue) {
        MessageParam timeoutParam = messageCap.getMessageParams()
                .get(indexOfParameter);
        int timeout = defaultValue;
        try {
            timeout = Integer.parseInt(timeoutParam.getValue());
        } catch (NumberFormatException e) {
            LOG.warn("Error while parsing timeout parameter. " //$NON-NLS-1$
                    + "Using default value.", e); //$NON-NLS-1$
        }
        return timeout;
    }
    
    /**
     * Handles the scenario where a component does not exist, but may also
     * not be expected to exist.
     * Is called if the graphics component cannot be found and the current 
     * request is attempting to verify the existence/non-existence of that 
     * component.
     * Sets the status of the response to Verification Error if the component is
     * expected to exist. Otherwise continues normal operation.

     * @param response The response message
     * @param shouldExist <code>True</code> if the component is expected to
     *                    exist. Otherwise, <code>false</code>.
     */
    private void handleComponentDoesNotExist(CAPTestResponseMessage response, 
        boolean shouldExist) {
        try {
            Verifier.equals(shouldExist, false);
        } catch (StepVerifyFailedException svfe) {
            response.setTestErrorEvent(EventFactory.createVerifyFailed(
                    String.valueOf(shouldExist), String.valueOf(false)));
            
        }
    }

    /**
     * calls the method of the implementation class per reflection
     * {@inheritDoc}
     */
    public Message execute() {
        AUTServer autServer = AUTServer.getInstance();
        autServer.setErrorComponent(null);
        final int oldMode = autServer.getMode();
        TestErrorEvent event = null;
        CAPTestResponseMessage response = new CAPTestResponseMessage();
        autServer.setMode(ChangeAUTModeMessage.TESTING);
        try {
            MessageCap messageCap = m_capTestMessage.getMessageCap();
            response.setMessageCap(messageCap);
        
            // get the implementation class
            Object implClass = getImplClass(response);
            if (implClass == null) {
                return response;
            }
            MethodInvoker invoker = new MethodInvoker(messageCap);
            Object returnValue = invoker.invoke(implClass);
            if (returnValue != null) {
                response.setReturnValue(String.valueOf(returnValue));
            }
            if ("true".equals(System.getenv("LogExecutedCaps")) //$NON-NLS-1$ //$NON-NLS-2$
                    && messageCap != null
                    && messageCap.getCi() != null
                    && messageCap.getCi().getComponentClassName() != null
                    && messageCap.getAction() != null
                    && messageCap.getAction().getName() != null) {
                CAPLOG.debug(messageCap.getCi().getComponentClassName() + " - " //$NON-NLS-1$
                        + CompSystemI18n.getString(
                                messageCap.getAction().getName()));
            }
        } catch (NoSuchMethodException nsme) {
            LOG.error("implementation class method not found", nsme); //$NON-NLS-1$
            event = EventFactory.createUnsupportedActionError();
        } catch (IllegalAccessException iae) {
            LOG.error("Failed accessing implementation class method", iae); //$NON-NLS-1$
            event = EventFactory.createConfigErrorEvent();
        } catch (InvocationTargetException ite) {
            if (ite.getTargetException() instanceof EventSupportException) {
                EventSupportException e = (EventSupportException)
                    ite.getTargetException();
                event = e.getEvent();
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getLocalizedMessage(), e);
                }
            } else if (ite.getTargetException() instanceof ExecutionEvent) {
                ExecutionEvent e = (ExecutionEvent)ite.getTargetException();
                response.setState(e.getEvent());
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getLocalizedMessage(), e);
                }
            } else {
                event = EventFactory.createConfigErrorEvent();
                if (LOG.isErrorEnabled()) {
                    LOG.error("InvocationTargetException: ", ite); //$NON-NLS-1$
                    LOG.error("TargetException: ", ite.getTargetException()); //$NON-NLS-1$
                }
            }
        } catch (IllegalArgumentException e) {
            LOG.error(e.getLocalizedMessage(), e);
        } catch (MethodParamException ex) {
            LOG.error(ex.getLocalizedMessage(), ex);
        } finally {    
            autServer.setMode(oldMode);
        }
        if (event != null) {
            response.setTestErrorEvent(event);
        }
        return response;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.error(this.getClass().getName() + "timeout() called"); //$NON-NLS-1$
    }
}