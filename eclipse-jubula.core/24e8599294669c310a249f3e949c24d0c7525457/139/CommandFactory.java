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
package org.eclipse.jubula.communication.internal;

import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;


/**
 * @author BREDEX GmbH
 * @created 22.11.2004
 */
public class CommandFactory {
    
    /**
     * Classloader
     */
    private ClassLoader m_classLoader;
    
    /**
     * @param cl the ClassLoader
     */
    public CommandFactory(ClassLoader cl) {
        m_classLoader = cl;
    }
    
    /**
     * 
     */
    public CommandFactory() {
        this(null);
    }
    /** 
     * creates the appropriate command object for this message per reflection.
     * The message is set to the command.
     * @param commandClassName name for command class
     * @throws UnknownCommandException -
     *             the exception thrown if the instantiation of command failed.
     * @return the created command
     */
    public ICommand createCommandObject(String commandClassName)
        throws UnknownCommandException {
        try {
            Class commandClass;
            if (m_classLoader != null) {
                commandClass = m_classLoader.loadClass(commandClassName);
            } else {
                commandClass = Class.forName(commandClassName);
            }

            if (!ICommand.class.isAssignableFrom(commandClass)) {
                throw new UnknownCommandException(
                    commandClass.getName()
                        + "is not assignable to " //$NON-NLS-1$
                        + ICommand.class.getName(), 
                        MessageIDs.E_COMMAND_NOT_ASSIGNABLE);
            }
    
            //  create a sharedInstance and set the message
            ICommand result = (ICommand)commandClass.newInstance();
            return result;
        } catch (ExceptionInInitializerError eiie) {
            throw new UnknownCommandException(
                    "creating an ICommand sharedInstance for " //$NON-NLS-1$ 
                            + commandClassName + " failed: " + //$NON-NLS-1$);
                            eiie.getMessage(), 
                            MessageIDs.E_COMMAND_NOT_CREATED);
        } catch (LinkageError le) {
            throw new UnknownCommandException(
                    "creating an ICommand sharedInstance for " //$NON-NLS-1$ 
                            + commandClassName + " failed: " + //$NON-NLS-1$);
                            le.getMessage(), 
                            MessageIDs.E_COMMAND_NOT_CREATED);
        } catch (ClassNotFoundException cnfe) {
            throw new UnknownCommandException(
                    "creating an ICommand sharedInstance for " //$NON-NLS-1$ 
                            + commandClassName + " failed: " + //$NON-NLS-1$);
                            cnfe.getMessage(), 
                            MessageIDs.E_COMMAND_NOT_CREATED);
        } catch (InstantiationException ie) {
            throw new UnknownCommandException(
                    "creating an ICommand sharedInstance for " //$NON-NLS-1$ 
                            + commandClassName + " failed: " + //$NON-NLS-1$);
                            ie.getMessage(), 
                            MessageIDs.E_COMMAND_NOT_CREATED);
        } catch (IllegalAccessException iae) {
            throw new UnknownCommandException(
                    "creating an ICommand sharedInstance for " //$NON-NLS-1$ 
                            + commandClassName + " failed: " + //$NON-NLS-1$);
                            iae.getMessage(), 
                            MessageIDs.E_COMMAND_NOT_CREATED);
        }
    }
}