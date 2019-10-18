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
package org.eclipse.jubula.tools.internal.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class reads buffered from an inputstream in a seperated thread. 
 * Used for redirecting a stream of an external process.
 *
 * @author BREDEX GmbH
 * @created 10.08.2004
 */
public class SysoRedirect extends IsAliveThread {
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(SysoRedirect.class);
    
    /** the error stream if a SUN VM does not know -javaagent */
    private static final String UNRECOGNIZED_SUN_JO = 
        "Unrecognized option: \"-javaagent"; //$NON-NLS-1$
    
    /** the input stream to read from */
    private InputStream m_inputStream;

    /** picks up the error stream if -javaagent is unknown */
    private String m_line;

    /** {@link StringBuffer} for truncated/complete log */
    private StringBuffer m_stringBuffer = new StringBuffer();
    /** a descriptive prefix used for the syso redirection */
    private final String m_sysoPrefix;
    
    /** if there are deleted lines */
    private boolean m_removedLines = false;
    
    /**
     * public constructor
     * 
     * @param inputStream
     *            the input stream to redirect to system out
     * @param sysoPrefix
     *            a descriptive prefix used for the syso redirection; mighty not
     *            be <code>null</code>
     */
    public SysoRedirect(InputStream inputStream, String sysoPrefix) {
        super("Stream Redirect"); //$NON-NLS-1$
        m_inputStream = inputStream;
        m_sysoPrefix = sysoPrefix;
    }
    
    /**
     * {@inheritDoc}
     */
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(m_inputStream);
            BufferedReader br = new BufferedReader(isr);
            String line = br.readLine();
            while (line != null) {
                System.out.println(m_sysoPrefix + line);
                writeLine(line);
                if (line.indexOf(UNRECOGNIZED_SUN_JO) > -1) {
                    m_line = line;
                }
                line = br.readLine();
            }
        } catch (IOException ioe) {
            log.debug("input stream closed", ioe); //$NON-NLS-1$
        }
    }

    /**
     * @return the 'Unrecognized option' error stream if javaagent 
     * is to be used and jdk older than 1.5, or null
     */
    public String getLine() {
        return m_line;
    }
    
    /**
     * write a line to the {@link StringBuffer}
     * @param string the line to add to the {@link StringBuffer}
     */
    private void writeLine(String string) {
        if (m_stringBuffer.length() > 10000) {
            int i = m_stringBuffer.indexOf(StringConstants.NEWLINE);
            m_stringBuffer.delete(0, i + StringConstants.NEWLINE.length());
            m_stringBuffer.trimToSize();
            m_removedLines = true;
        }
        m_stringBuffer.append(string + StringConstants.NEWLINE);
    }
    /**
     * 
     * @return the content of the {@link StringBuffer}
     */
    public String getTruncatedLog() {
        while (this.isAlive()) {
            TimeUtil.delay(10);
            // wait here
        }
        if (m_removedLines) {
            return "..." + StringConstants.NEWLINE //$NON-NLS-1$
                    + m_stringBuffer.toString();
        }
        return m_stringBuffer.toString();
    }
}