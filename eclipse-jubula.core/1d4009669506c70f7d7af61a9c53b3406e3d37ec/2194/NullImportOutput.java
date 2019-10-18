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
package org.eclipse.jubula.client.archive.output;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jubula.client.core.progress.IProgressConsole;

/**
 * Default import output
 *
 * @author BREDEX GmbH
 * @created 19.03.2009
 */
public class NullImportOutput implements IProgressConsole {
    /** {@inheritDoc} */
    public void writeErrorLine(String line) {
        // no output
    }

    /** {@inheritDoc} */
    public void writeLine(String line) {
        // no output
    }

    /** {@inheritDoc} */
    public void writeWarningLine(String line) {
        // no output
    }

    /** {@inheritDoc} */
    public void writeStatus(IStatus status) {
     // no output
    }
    
    /** {@inheritDoc} */
    public void writeStatus(IStatus status, String id) {
     // no output
    }
    
    /** {@inheritDoc} */
    public void closeConsole() {
     // no output
    }
}