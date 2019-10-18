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
package org.eclipse.jubula.client.ui.rcp.editors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.ui.IPersistableElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created Feb 7, 2007
 */
public class ClientLogInput extends PlatformObject 
                            implements ISimpleEditorInput {
    /** the logger */
    protected static final Logger LOG = LoggerFactory
            .getLogger(ClientLogInput.class);
    
    /** the log file */
    private File m_logFile;
    
    /**
     * 
     * @param logFile the log file
     */
    public ClientLogInput(File logFile) {
        m_logFile = logFile;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean exists() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor() {
        return ImageDescriptor.getMissingImageDescriptor();
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return Messages.ClientLogViewerName;
    }

    /**
     * {@inheritDoc}
     */
    public String getToolTipText() {
        return m_logFile.getAbsolutePath();
    }

    /**
     * {@inheritDoc}
     */
    public IPersistableElement getPersistable() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getContent() throws CoreException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(m_logFile));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + StringConstants.NEWLINE);
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR, Plugin.PLUGIN_ID,
                    IStatus.OK, 
                    Messages.ErrorMessageFILE_NOT_FOUND,
                    e));
        } catch (IOException ioe) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR, Plugin.PLUGIN_ID,
                    IStatus.OK,
                    Messages.ErrorMessageIO_EXCEPTION,
                    ioe));
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOG.error(e.getLocalizedMessage());
                }
            }
        }
    }

}
