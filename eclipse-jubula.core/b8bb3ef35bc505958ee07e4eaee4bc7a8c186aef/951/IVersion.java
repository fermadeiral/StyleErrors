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
package org.eclipse.jubula.tools.internal.version;

/**
 * Interface to manage all relevant versions for Jubula
 * @author BREDEX GmbH
 * @created 02.11.2005
 */
public interface IVersion {
    /**
     * major version for communication between the client, AUT-Agent and remote
     * control components please increase this version in case of modification
     * of status messages from server or creation of new messages
     */
    public final Integer JB_PROTOCOL_MAJOR_VERSION = new Integer(11);
    
    /** major version for state of metadata in XML-format or from database
     *  modifications in client code without modification of ToolkitPlugins
     *  require an increase of this version
     */
    public final Integer JB_CLIENT_METADATA_VERSION = new Integer(6);
    
    /** minimum required metadata version for project import */
    public final Integer JB_CLIENT_MIN_XML_METADATA_VERSION = new Integer(5);
    
}
