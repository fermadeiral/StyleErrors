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
package org.eclipse.jubula.logging;

import java.io.InputStream;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * @author BREDEX GmbH
 */
public class Configurator {
    /** hide constructor */
    private Configurator() {
        // empty
    }

    /**
     * @param logFileName
     *            the file name of the log which is getting created
     */
    public static void loadLogbackConfiguration(String logFileName) {
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        if (loggerFactory instanceof LoggerContext) {
            LoggerContext lc = (LoggerContext) loggerFactory;
            try {
                JoranConfigurator configurator = new JoranConfigurator();
                configurator.setContext(lc);
                // the context was probably already configured by default
                // configuration rules
                lc.reset();
                lc.setName(logFileName);
                InputStream is = Configurator.class
                    .getResourceAsStream("configuration.xml"); //$NON-NLS-1$
                configurator.doConfigure(is);
            } catch (JoranException je) {
                // StatusPrinter will handle this
            }
            StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
        }
    }
}