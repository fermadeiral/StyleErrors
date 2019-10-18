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
package org.eclipse.jubula.communication.internal.writer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * A class used to write messages. Uses '\n' as a line separator to avoid
 * platform dependencies during parsing a received message.
 * 
 * @author BREDEX GmbH
 * @created 15.07.2004
 */
public class MessageWriter extends BufferedWriter {
    /** the line separator * */
    public static final char LINE_SEPARATOR = '\n';

    /**
     * @param out -
     *            the writer which will be buffered
     * {@inheritDoc}
     */
    public MessageWriter(Writer out) {
        super(out);
    }

    /**
     * @param out -
     *            the writer which will be buffered
     * @param sz -
     *            the size of the buffer
     * {@inheritDoc}
     */
    public MessageWriter(Writer out, int sz) {
        super(out, sz);
    }

    /**
     * write a linefeed
     * 
     * @throws IOException -
     *             {@link java.io.BufferedWriter#write(char)}write
     */
    public void writeLine() throws IOException {
        super.write(LINE_SEPARATOR);
    }

    /**
     * overrides newLine() from java.io.BufferedWriter to avoid different
     * linefeed characters on different platforms
     * 
     * @throws IOException
     *             from BufferedWriter.newLine()
     */
    public void newLine() throws IOException {
        writeLine();
    }
}
