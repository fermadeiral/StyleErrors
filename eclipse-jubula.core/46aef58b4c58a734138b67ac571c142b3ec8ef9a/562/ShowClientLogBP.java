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
package org.eclipse.jubula.client.ui.rcp.businessprocess;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;

/**
 * @author BREDEX GmbH
 * @created 09.04.2008
 */
public final class ShowClientLogBP {
    /** single instance */
    private static ShowClientLogBP instance = null;

    /**
     * private constructor
     */
    private ShowClientLogBP() {
        // Nothing to initialize
    }

    /**
     * @return single instance
     */
    public static ShowClientLogBP getInstance() {
        if (instance == null) {
            instance = new ShowClientLogBP();
        }
        return instance;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return true;
    }

    /**
     * Returns the client log file and handles occurring errors.
     * @return The client log file, if file logging is activated. Otherwise
     *         null is returned.
     */
    public File getClientLogFile() {
        final File clientLogFile;

        // Get location of log file
        Logger logger = LoggerFactory.getLogger(
                org.slf4j.Logger.ROOT_LOGGER_NAME);
        Iterator<Appender<ILoggingEvent>> appenders =
                new ArrayList<Appender<ILoggingEvent>>().iterator();
        
        if (logger instanceof ch.qos.logback.classic.Logger) {
            appenders = ((ch.qos.logback.classic.Logger)logger)
                    .iteratorForAppenders();
        }
        
        FileAppender<?> fileAppender = null;
        while (appenders.hasNext() && fileAppender == null) {
            Object enumElement = appenders.next();
            if (enumElement instanceof FileAppender) {
                fileAppender = (FileAppender<?>)enumElement;
            }
        }

        if (fileAppender != null) {
            clientLogFile = new File(fileAppender.getFile());
        } else {
            clientLogFile = null;

            // Ask user to turn on file logging
            ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.I_FILE_LOGGING_NOT_ENABLED,
                    new String[] { "Jubula" }, null); //$NON-NLS-1$
        }

        return clientLogFile;

    }

}
