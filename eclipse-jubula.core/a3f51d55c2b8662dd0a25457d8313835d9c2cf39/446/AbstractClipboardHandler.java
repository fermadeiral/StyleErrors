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
package org.eclipse.jubula.client.ui.rcp.handlers;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.ui.rcp.handlers.open.AbstractOpenHandler;
import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * @author BREDEX GmbH
 * @created Feb 10, 2011
 */
public abstract class AbstractClipboardHandler extends AbstractOpenHandler
        implements ClipboardOwner {
    /** <code>OBJECT_TYPE_TESTCASE</code> */
    protected static final String OBJECT_TYPE_TESTCASE = "tc"; //$NON-NLS-1$
    /** <code>OBJECT_TYPE_TESTSUITE</code> */
    protected static final String OBJECT_TYPE_TESTSUITE = "ts"; //$NON-NLS-1$
    /** <code>OBJECT_TYPE_TESTJOB</code> */
    protected static final String OBJECT_TYPE_TESTJOB = "tj"; //$NON-NLS-1$
    /** <code>SPLIT_TOKEN</code> */
    protected static final String SPLIT_TOKEN = StringConstants.EQUALS_SIGN;

    /**
     * {@inheritDoc}
     */
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // empty
    }

    /**
     * @param node
     *            the node
     */
    protected void copyIDToClipboard(INodePO node) {
        StringBuilder sb = new StringBuilder();
        if (node instanceof ISpecTestCasePO) {
            sb.append(OBJECT_TYPE_TESTCASE);
        } else if (node instanceof ITestSuitePO) {
            sb.append(OBJECT_TYPE_TESTSUITE);
        } else if (node instanceof ITestJobPO) {
            sb.append(OBJECT_TYPE_TESTJOB);
        }
        sb.append(SPLIT_TOKEN);
        sb.append(node.getGuid());
        copyStringToClipboard(sb.toString());
    }
    
    /**
     * @param s the string to copy
     */
    private void copyStringToClipboard(String s) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection ss = new StringSelection(s);
        clipboard.setContents(ss, this);
    }

    /**
     * Get the String residing on the clipboard.
     * 
     * @return any text found on the Clipboard; if none found, return an empty
     *         String.
     */
    public String getClipboardContents() {
        String result = StringConstants.EMPTY;
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText = (contents != null)
                && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if (hasTransferableText) {
            try {
                result = (String)contents
                        .getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException ex) {
                // ignore
            } catch (IOException ex) {
                // ignore
            }
        }
        return result;
    }

}