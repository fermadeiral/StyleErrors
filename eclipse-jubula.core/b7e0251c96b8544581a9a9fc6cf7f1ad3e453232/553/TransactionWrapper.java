/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.actions;

import org.eclipse.jubula.client.core.persistence.PMAlreadyLockedException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMObjectDeletedException;
import org.eclipse.jubula.client.core.persistence.PMRefreshFailedException;
import org.eclipse.jubula.client.core.persistence.TransactionSupport;
import org.eclipse.jubula.client.core.persistence.TransactionSupport.ITransaction;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;

/**
 * A standard transaction wrapper class which tries to execute an operation
 *   in a new session and handles any exception that could happen in the process.
 * @author BREDEX GmbH
 *
 */
public class TransactionWrapper {
    
    /** Constructor */
    private TransactionWrapper() {
        // Empty
    }
    
    /**
     * Executes the operation
     * @param op the operation
     * @return whether it was successful
     */
    public static boolean executeOperation(ITransaction op) {
        try {
            TransactionSupport.transact(op);
            return true;
        } catch (PMRefreshFailedException e) {
            return true;
        } catch (ProjectDeletedException e) {
            PMExceptionHandler.handleProjectDeletedException();
        } catch (PMAlreadyLockedException | PMObjectDeletedException e) {
            PMExceptionHandler.handlePMExceptionForMasterSession(e);
        } catch (PMException e) {
            ErrorHandlingUtil.createMessageDialogException(e);
        } catch (Exception e) {
            ErrorHandlingUtil.createMessageDialogException(e);
        }
        return false;
    }

}
