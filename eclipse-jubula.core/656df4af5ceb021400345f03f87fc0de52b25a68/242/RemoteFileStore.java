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
package org.eclipse.jubula.client.ui.rcp.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileInfo;
import org.eclipse.core.filesystem.provider.FileStore;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jubula.client.ui.rcp.businessprocess.RemoteFileBrowserBP;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.communication.internal.Communicator;
import org.eclipse.jubula.tools.internal.constants.StringConstants;


/**
 * @author BREDEX GmbH
 * @created May 27, 2009
 */
public class RemoteFileStore extends FileStore {
    
    /** communicator used for connections */
    private Communicator m_com;
    /** remote path */
    private String m_path;
    /** remote browsing support BP */
    private RemoteFileBrowserBP m_remBrowser;
    /** is this entry a directory */
    private boolean m_isDirectory;
    /** is the content valid or has some error occurred */
    private boolean m_isContentValid;

    /**
     * @param com is the Communicator channel to be used. This is needed as 
     * soon as multiple connections to AUT controllers are supported.
     * @param path remote file system path name. If null "." will be used
     * instead
     * @param isDirectory is this entry a directory
     * 
     */
    public RemoteFileStore(Communicator com, String path, 
            boolean isDirectory) {
        Assert.isNotNull(com, Messages.NoNullConnectionAllowed);
        m_com = com;
        m_path = (path != null) ? path : "."; //$NON-NLS-1$
        m_isDirectory = isDirectory;
        m_remBrowser = new RemoteFileBrowserBP(m_com);
        m_isContentValid = m_remBrowser.fetchRemoteDirContent(m_path);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unused")
    public String[] childNames(int options, IProgressMonitor monitor)
        throws CoreException {
        final List<String> remoteDirContent = 
            m_remBrowser.getRemoteDirContent();
        String[] res = new String[remoteDirContent.size()];
        int index = 0;
        for (String entry : remoteDirContent) {
            res[index++] = normalize(entry.substring(1));
        }
        return res;
    }

    /**
     * @param path file path
     * @return a path with leading working dir strings removed
     */
    private String normalize(String path) {
        /* currentDir string which represents the working 
        * directory (for example '.' or './' on Unix, '.' or '.\' on Windows)
        * */
        String currentDir = StringConstants.DOT + m_remBrowser.getSepChar();
        StringBuilder s = new StringBuilder(path);
        while (s.indexOf(currentDir) == 0) {
            s.delete(0, currentDir.length());
        }
        return s.toString();
    }

    /**
     * {@inheritDoc}
     */
    public IFileInfo fetchInfo(int options, IProgressMonitor monitor) {
        FileInfo result = new FileInfo(m_path);
        result.setDirectory(m_isDirectory);
        result.setExists(m_isContentValid);
        result.setLength(EFS.NONE);
        return result;

    }

    /**
     * {@inheritDoc}
     */
    public IFileStore getChild(String name) {
        boolean isDirectory = false;
        for (String candidate : m_remBrowser.getRemoteDirContent()) {
            if (normalize(candidate.substring(1)).equals(name)) {
                isDirectory = candidate.charAt(0) == 'D';
                break;
            }
        }
        return new RemoteFileStore(m_com, name, isDirectory);
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        if (!m_isContentValid) {
            return m_path;
        }
        int sepPos = m_path.lastIndexOf(m_remBrowser.getSepChar());
        if (sepPos == -1) {
            return m_path;
        }
        return m_path.substring(sepPos + 1);
    }

    /**
     * {@inheritDoc}
     */
    public IFileStore getParent() {    
        if (!m_remBrowser.isDataValid()) {
            return null;
        }
        int sepPos = m_path.lastIndexOf(m_remBrowser.getSepChar());
        if (sepPos == -1) {
            return null;
        }
        return new RemoteFileStore(m_com, m_path.substring(0, sepPos), true);
    }

    /**
     * {@inheritDoc}
     */
    public InputStream openInputStream(int options, IProgressMonitor monitor) {
        return new ByteArrayInputStream(new byte[0]);
    }

    /**
     * {@inheritDoc}
     */
    public URI toURI() {
        try {
            return new URI(m_path);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {        
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RemoteFileStore)) {
            return false;        
        }
        RemoteFileStore fs = (RemoteFileStore)obj;
        if (m_path == fs.m_path) { // covers null values
            return true;
        }
        return normalize(m_path).equals(normalize(fs.m_path));

    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        if (m_path == null) {
            return 0;
        }
        return normalize(m_path).hashCode();
    }

    /**
     * 
     * @return the Strings describing the remote file system roots
     */
    public List<String> getRootFSs() {
        return m_remBrowser.getRemoteFilesystemRoots();
    }

    /**
     * @return the Communicator used with this remote service
     */
    public Communicator getCommunicator() {
        return m_com;
    }

    /**
     * @return the isContentValid
     */
    public boolean isContentValid() {
        return m_isContentValid;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return m_path;
    }
}
