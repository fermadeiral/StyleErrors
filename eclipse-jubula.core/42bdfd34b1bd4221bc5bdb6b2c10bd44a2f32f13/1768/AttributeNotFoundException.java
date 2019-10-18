/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.teststyle.exceptions;

import org.eclipse.jubula.client.teststyle.i18n.Messages;
import org.eclipse.osgi.util.NLS;



/**
 * @author marcell
 * @created Oct 11, 2010
 */
public class AttributeNotFoundException extends RuntimeException {

    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param attrName
     *            The name of the attribute that will be described.
     */
    public AttributeNotFoundException(String attrName) {
        super(NLS.bind(Messages.AtrributeNotFoundExceptionText, attrName));
    }
}
