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
package org.eclipse.jubula.communication.internal.message;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.tools.internal.constants.CommandConstants;

/**
 * @author BREDEX GmbH
 * @created May 18, 2009
 */
public class SendDirectoryResponseMessage extends Message {
    /** dir marker */
    public static final String DIR_MARKER = "D"; //$NON-NLS-1$

    /** file marker */
    public static final String FILE_MARKER = "F"; //$NON-NLS-1$

    /** state */
    public static final int OK = 0;

    /** state */
    public static final int NOT_A_DIR = 1;

    /** state */
    public static final int IO_ERROR = 2;

    /** m_base */
    private String m_base;

    /** m_dirEntries */
    private List<String> m_dirEntries;

    /** m_error */
    private int m_error = OK;

    /** m_separator */
    private char m_separator = File.separatorChar;

    /** m_roots */
    private List<String> m_roots;

    /** basic constructor */
    public SendDirectoryResponseMessage() {
        super();
        m_dirEntries = new ArrayList<String>(101);
        m_roots = new ArrayList<String>(26);
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.PROCESS_DIR_COMMAND;
    }

    /**
     * add a file entry
     * 
     * @param name
     *            of the entry
     */
    public void addFile(String name) {
        m_dirEntries.add(FILE_MARKER + name);
    }

    /**
     * add a directory entry
     * 
     * @param name
     *            of the entry;
     */
    public void addDir(String name) {
        m_dirEntries.add(DIR_MARKER + name);
    }

    /**
     * add an entry to the roots list this is set from File.listRoots()
     * 
     * @param absName
     *            the absolute path name of a root filesystem entry
     */
    public void addRoot(String absName) {
        m_roots.add(absName);
    }

    /** @return the dirEntries */
    public List getDirEntries() {
        return m_dirEntries;
    }

    /** @return the error */
    public int getError() {
        return m_error;
    }

    /**
     * @param error
     *            the error to set
     */
    public void setError(int error) {
        m_error = error;
    }

    /** @return the base */
    public String getBase() {
        return m_base;
    }

    /**
     * @param base
     *            the base to set
     */
    public void setBase(String base) {
        m_base = base;
    }

    /** @return m_roots */
    public List getRoots() {
        return m_roots;
    }

    /** @return m_separator */
    public char getSeparator() {
        return m_separator;
    }
}