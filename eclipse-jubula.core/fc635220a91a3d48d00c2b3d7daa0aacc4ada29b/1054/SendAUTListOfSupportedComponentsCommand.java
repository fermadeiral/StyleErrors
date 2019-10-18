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

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.AUTStartStateMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.SendAUTListOfSupportedComponentsMessage;
import org.eclipse.jubula.rc.common.AUTServerConfiguration;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ComponentClass;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ConcreteComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The command object for QueryAUTForComponentsMessage. <br>
 * execute() registers all components sent by the
 * <code>QueryAUTForComponentsMessage</code> in the AUT server and returns an
 * <code>AUTComponentsMessage</code> containing all components of the AUT
 * which are supported.
 * timeout() should never be called. <br>
 * @author BREDEX GmbH
 * @created 02.01.2007
 * 
 */
public final class SendAUTListOfSupportedComponentsCommand 
    implements ICommand {
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(
        SendAUTListOfSupportedComponentsCommand.class);
    /** the (empty) message */
    private SendAUTListOfSupportedComponentsMessage m_message;
    
    /**
     * {@inheritDoc}
     */
    public Message getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (SendAUTListOfSupportedComponentsMessage)message;
    }
    
    /**
     * {@inheritDoc}
     */
    public Message execute() {
        log.info("Entering method " + getClass().getName() + ".execute()."); //$NON-NLS-1$ //$NON-NLS-2$
        // Register the supported components and their implementation classes.
        final AUTServerConfiguration serverConfig = AUTServerConfiguration
            .getInstance();
        serverConfig.setProfile(m_message.getProfile());
        final List<Component> components = m_message.getComponents();
        if (components != null) {
            log.info("Processing recevied components from ITE... "); //$NON-NLS-1$
            for (Component component : components) {
                if (!component.isConcrete()) {
                    // only handle concrete components on server side
                    continue;
                }
                ConcreteComponent concrete = (ConcreteComponent)component;
                
                try {
                    String testerClass = concrete.getTesterClass();
                    String componentClass = concrete.getComponentClass()
                        .getName();
                    if (!(StringUtils.isEmpty(testerClass) 
                        && StringUtils.isEmpty(componentClass))) {
                        serverConfig.registerComponent(concrete);
                    }
                    
                } catch (IllegalArgumentException e) {
                    log.error("An error occurred while registering a component.", e); //$NON-NLS-1$
                }
            }
        } else if (m_message.getTechTypeToTesterClassMapping() != null) {
            Map<ComponentClass, String> techTypeToTesterClassMapping = m_message
                .getTechTypeToTesterClassMapping();
            
            for (ComponentClass cc : techTypeToTesterClassMapping.keySet()) {
                ConcreteComponent syntheticComponent = new ConcreteComponent();
                syntheticComponent.setComponentClass(cc);
                syntheticComponent.setTesterClass(
                    techTypeToTesterClassMapping.get(cc));
                serverConfig.registerComponent(syntheticComponent);
            }
        } else {
            log.error("Insufficient information received in: " + getClass().getName()); //$NON-NLS-1$
        }

        log.info("Exiting method " + getClass().getName() + ".execute()."); //$NON-NLS-1$ //$NON-NLS-2$
        return new AUTStartStateMessage();
    }

    /** 
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }
}