/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.internal.impl;

import java.awt.image.BufferedImage;
import java.net.ConnectException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.client.AUT;
import org.eclipse.jubula.client.Result;
import org.eclipse.jubula.client.exceptions.ActionException;
import org.eclipse.jubula.client.exceptions.CheckFailedException;
import org.eclipse.jubula.client.exceptions.CommunicationException;
import org.eclipse.jubula.client.exceptions.ComponentNotFoundException;
import org.eclipse.jubula.client.exceptions.ConfigurationException;
import org.eclipse.jubula.client.exceptions.ExecutionException;
import org.eclipse.jubula.client.exceptions.ExecutionExceptionHandler;
import org.eclipse.jubula.client.internal.AUTConnection;
import org.eclipse.jubula.client.internal.BaseConnection.NotConnectedException;
import org.eclipse.jubula.client.internal.Synchronizer;
import org.eclipse.jubula.client.internal.exceptions.ConnectionException;
import org.eclipse.jubula.communication.CAP;
import org.eclipse.jubula.communication.internal.IExceptionHandler;
import org.eclipse.jubula.communication.internal.message.CAPTestMessage;
import org.eclipse.jubula.communication.internal.message.CAPTestResponseMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.MessageCap;
import org.eclipse.jubula.communication.internal.message.MessageParam;
import org.eclipse.jubula.communication.internal.message.SetProfileMessage;
import org.eclipse.jubula.communication.internal.message.TakeScreenshotMessage;
import org.eclipse.jubula.communication.internal.message.TakeScreenshotResponseMessage;
import org.eclipse.jubula.communication.internal.message.UnknownMessageException;
import org.eclipse.jubula.toolkit.ToolkitInfo;
import org.eclipse.jubula.toolkit.internal.AbstractToolkitInfo;
import org.eclipse.jubula.tools.AUTIdentifier;
import org.eclipse.jubula.tools.Profile;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.jubula.tools.internal.serialisation.SerializedImage;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ComponentClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author BREDEX GmbH */
public class AUTImpl implements AUT {
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(AUTImpl.class);
    /** the AUT identifier */
    @NonNull private AutIdentifier m_autID;
    /** the instance */
    private AUTConnection m_instance;
    /** the toolkit specific information */
    private AbstractToolkitInfo m_information;
    /** the exception handler */
    private ExecutionExceptionHandler m_handler;
    /** */
    private boolean m_logToConsole;
    /**
     * Constructor
     * 
     * @param autID
     *            the identifier to use for connection
     * @param information
     *            the toolkit information
     */
    public AUTImpl(
        @NonNull AutIdentifier autID, 
        @NonNull ToolkitInfo information) {
        Validate.notNull(autID, "The AUT-Identifier must not be null."); //$NON-NLS-1$
        Validate.notNull(information, "The toolkit information must not be null."); //$NON-NLS-1$
        
        m_autID = autID;
        setToolkitInformation((AbstractToolkitInfo)information);
    }

    /** {@inheritDoc} */
    public void connect() throws CommunicationException {
        connect(0);
    }
    
    /** {@inheritDoc} */
    public void connect(int timeOut) throws CommunicationException {
        try {
            AUTConnection autconnection = AUTConnection.getInstance();
            if (autconnection != null) {
                autconnection.reset();
            }
        } catch (ConnectionException e) {
            // ignore
        }
        connectImpl(timeOut);
    }
    
    /** 
     * @param timeOut if 0 then the BaseAUTConnection.CONNECT_TO_AUT_TIMEOUT will be used
     * @throws CommunicationException 
     */
    private void connectImpl(int timeOut) throws CommunicationException {
        if (!isConnected()) {
            final Map<ComponentClass, String> typeMapping = 
                getInformation().getTypeMapping();
            try {
                m_instance = AUTConnection.getInstance();
                m_instance.connectToAut(m_autID, typeMapping, timeOut);
                if (!isConnected()) {
                    throw new CommunicationException(
                        new ConnectException(
                            "Could not connect to AUT: " //$NON-NLS-1$
                                + m_autID.getID() + ".")); //$NON-NLS-1$
                }
            } catch (ConnectionException e) {
                log.error(e.getLocalizedMessage(), e);
                throw new CommunicationException(e);
            }
        } else {
            throw new IllegalStateException("AUT connection is already made"); //$NON-NLS-1$
        }
    }

    /** {@inheritDoc} */
    public void disconnect() {
        if (isConnected()) {
            m_instance.getCommunicator().setIsServerSocketClosable(true);
            m_instance.close();
        } else {
            throw new IllegalStateException("AUT connection is already disconnected"); //$NON-NLS-1$
        }
        Thread.interrupted();
    }

    /** {@inheritDoc} */
    public boolean isConnected() {
        return m_instance != null ? m_instance.isConnected() : false;
    }

    /** {@inheritDoc} */
    @NonNull 
    public AUTIdentifier getIdentifier() {
        return m_autID;
    }
    
    /** @param information the toolkit information to set */
    private void setToolkitInformation(AbstractToolkitInfo information) {
        m_information = information;
    }

    /**
     * @return the information
     */
    public AbstractToolkitInfo getInformation() {
        return m_information;
    }
    
    /** {@inheritDoc} */
    @NonNull
    public <T> Result<T> execute(@NonNull CAP cap, @Nullable T payload)
        throws ExecutionException, CommunicationException {
        Validate.notNull(cap, "The CAP must not be null."); //$NON-NLS-1$
        AUTAgentImpl.checkConnected(this);
        
        final ResultImpl<T> result = new ResultImpl<T>(cap, payload);
        try {
            CAPTestMessage capTestMessage = new CAPTestMessage(
                    (MessageCap) cap); 
            logCAPtoConsole(capTestMessage);
            m_instance.send(capTestMessage);
            Object exchange = Synchronizer.instance().exchange(null);
            if (exchange instanceof CAPTestResponseMessage) {
                CAPTestResponseMessage response = 
                    (CAPTestResponseMessage) exchange;
                processResponse(response, result);
                result.setOK(true);
            } else {
                log.error("Unexpected response received: " //$NON-NLS-1$
                    + String.valueOf(exchange));
            }
        } catch (NotConnectedException e) {
            throw new CommunicationException(e);
        } catch (UnknownMessageException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new CommunicationException(e);
        } catch (org.eclipse.jubula.tools.internal.
                exception.CommunicationException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new CommunicationException(e);
        } catch (InterruptedException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new CommunicationException(e);
        } catch (ExecutionException exec) {
            logExecutionException(exec, false);
            throw exec;
        }
        if (m_logToConsole && result.getException() != null) {
            ExecutionException exec = result.getException();
            logExecutionException(exec, true);
        }
        return result;
    }

    /**
     * logs the CAP information to the console
     * @param capTestMessage the {@link CAPTestMessage}
     */
    private void logCAPtoConsole(CAPTestMessage capTestMessage) {
        if (m_logToConsole) {
            StringBuilder builder = new StringBuilder();
            MessageCap messcap = capTestMessage.getMessageCap();
            builder.append("Method: " + messcap.getMethod() //$NON-NLS-1$
                    + StringConstants.SPACE);
            List<MessageParam> list = messcap.getMessageParams();
            builder.append("Parameter: "); //$NON-NLS-1$
            for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                MessageParam messageParam = (MessageParam) iterator.next();
                builder.append(StringConstants.QUOTE);
                builder.append(messageParam.getValue());
                builder.append(StringConstants.QUOTE);
                if (iterator.hasNext()) {
                    builder.append(
                            StringConstants.COMMA + StringConstants.SPACE);
                }
            }
            log.debug(builder.toString());
            System.out.println(builder.toString());
        }
    }

    /**
     * this logs the occurred Exception to the console
     * @param exec the {@link ExecutionException}
     * @param caught if the exception was already handled via the {@link IExceptionHandler}
     */
    private void logExecutionException(ExecutionException exec,
            boolean caught) {
        StringBuilder builder = new StringBuilder();
        if (caught) {
            builder.append("Handled" + StringConstants.SPACE); //$NON-NLS-1$
        }
        builder.append("Error occurred" + StringConstants.SPACE); //$NON-NLS-1$
        builder.append(exec.getClass().getSimpleName());
        if (exec instanceof CheckFailedException) {
            CheckFailedException cfe = (CheckFailedException) exec;
            builder.append(StringConstants.NEWLINE);
            builder.append("Actual value: " + cfe.getActualValue()); //$NON-NLS-1$
        }
        if (exec.getMessage() != null) {
            builder.append(StringConstants.NEWLINE + exec.getMessage());
        }
        System.out.println(builder.toString());
        log.info(builder.toString());
    }

    /**
     * @param result
     *            the result
     * @param response
     *            the response to process
     */
    private void processResponse(CAPTestResponseMessage response,
        @NonNull final ResultImpl result)
        throws ExecutionException {
        ExecutionException exception = null;
        if (response.hasTestErrorEvent()) {
            final TestErrorEvent event = response.getTestErrorEvent();
            final String eventId = event.getId();
            Map<String, Object> eventProps = event.getProps();
            String description = null;
            if (eventProps.containsKey(
                TestErrorEvent.Property.DESCRIPTION_KEY)) {
                String key = (String) eventProps
                    .get(TestErrorEvent.Property.DESCRIPTION_KEY);
                Object[] args = (Object[]) eventProps
                    .get(TestErrorEvent.Property.PARAMETER_KEY);
                args = args != null ? args : new Object[0];
                description = I18n.getString(key, args);
            }
            if (TestErrorEvent.ID.ACTION_ERROR.equals(eventId)) {
                exception = new ActionException(result, description);
            } else if (TestErrorEvent.ID.COMPONENT_NOT_FOUND.equals(eventId)) {
                exception = new ComponentNotFoundException(result, description);
            } else if (TestErrorEvent.ID.CONFIGURATION_ERROR.equals(eventId)) {
                exception = new ConfigurationException(result, description);
            } else if (TestErrorEvent.ID.VERIFY_FAILED.equals(eventId)) {
                Object actualValueObject = event.getProps().get(
                    TestErrorEvent.Property.ACTUAL_VALUE_KEY);
                @NonNull String actualValue = "n/a"; //$NON-NLS-1$
                if (actualValueObject instanceof String) {
                    actualValue = (String)actualValueObject;
                }
                exception = new CheckFailedException(
                    result, description, actualValue);
            }
        } else {
            result.setReturnValue(response.getReturnValue());
        }
        
        result.setException(exception);
        
        if (exception != null) {
            if (m_handler != null) {
                m_handler.handle(exception);
            } else {
                throw exception;
            }
        }
    }

    /** {@inheritDoc} */
    public void setHandler(
        @Nullable ExecutionExceptionHandler handler) {
        m_handler = handler;
    }

    /** {@inheritDoc} */
    public BufferedImage getScreenshot() throws IllegalStateException {
        if (isConnected()) {
            Message message = new TakeScreenshotMessage();
            try {
                m_instance.send(message);
                
                Object exchange = Synchronizer.instance().exchange(null);
                if (exchange instanceof TakeScreenshotResponseMessage) {
                    TakeScreenshotResponseMessage response = 
                        (TakeScreenshotResponseMessage) exchange;

                    return SerializedImage.computeImage(
                            response.getScreenshot());
                }
                log.error("Unexpected response received: " //$NON-NLS-1$
                    + String.valueOf(exchange));
            } catch (NotConnectedException nce) {
                if (log.isErrorEnabled()) {
                    log.error(nce.getLocalizedMessage(), nce);
                }
            } catch (IllegalArgumentException e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getLocalizedMessage(), e);
                }
            } catch (org.eclipse.jubula.tools.internal.
                    exception.CommunicationException e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getLocalizedMessage(), e);
                }
            } catch (InterruptedException e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getLocalizedMessage(), e);
                }
            }
        } else {
            throw new IllegalStateException("No AUT connection!"); //$NON-NLS-1$
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setProfile(Profile profile) throws IllegalArgumentException,
            IllegalStateException, CommunicationException {
        if (profile == null) {
            throw new IllegalArgumentException("Profile is null"); //$NON-NLS-1$
        }
        if (isConnected()) {
            SetProfileMessage message = new SetProfileMessage();
            message.setProfile((org.eclipse.jubula.tools
                            .internal.xml.businessmodell.Profile) profile);
            try {
                m_instance.send(message);
            } catch (NotConnectedException nce) {
                if (log.isErrorEnabled()) {
                    log.error(nce.getLocalizedMessage(), nce);
                }
            } catch (IllegalArgumentException e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getLocalizedMessage(), e);
                }
            } catch (org.eclipse.jubula.tools.internal.
                    exception.CommunicationException e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getLocalizedMessage(), e);
                }
            }
        } else {
            throw new IllegalStateException("No AUT connection!"); //$NON-NLS-1$
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setCAPtoConsoleLogging(boolean logCapToConsole) {
        m_logToConsole = logCapToConsole;
    }
}