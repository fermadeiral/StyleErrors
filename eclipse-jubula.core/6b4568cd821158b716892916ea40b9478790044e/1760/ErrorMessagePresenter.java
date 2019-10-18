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
package org.eclipse.jubula.client.core.errorhandling;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages a single error message presenter. If no presenter is set, a no-op
 * presenter will be used.
 *
 * @author BREDEX GmbH
 * @created Jun 8, 2010
 */
public class ErrorMessagePresenter {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(ErrorMessagePresenter.class);
    
    /** the current error message presenter for the application */
    private static IErrorMessagePresenter presenter = 
        new NoopErrorMessagePresenter();

    /**
     * Private constructor for utility class.
     */
    private ErrorMessagePresenter() {
        // Nothing to initialize
    }
    
    /**
     * @return the object responsible for presenting error messages to the user.
     */
    public static IErrorMessagePresenter getPresenter() {
        return presenter;
    }

    /**
     * @param errorMessagePresenter The new object responsible for presenting 
     *                              error messages to the user. May not be 
     *                              <code>null</code>.
     */
    public static void setPresenter(
            IErrorMessagePresenter errorMessagePresenter) {
        
        Validate.notNull(errorMessagePresenter);
        // Generally, the presenter should only be set once (on application 
        // startup), so log as a warning if the presenter is being set again.
        if (!(presenter instanceof NoopErrorMessagePresenter)) {
            StringBuilder msg = new StringBuilder();
            msg.append(Messages.ErrorMessagePresenter);
            msg.append(StringConstants.SPACE);
            msg.append(StringConstants.LEFT_BRACKET);
            msg.append(presenter);
            msg.append(StringConstants.RIGHT_BRACKET);
            msg.append(StringConstants.SPACE);
            msg.append(Messages.IsBeingReplacedWith);
            msg.append(StringConstants.SPACE);
            msg.append(StringConstants.LEFT_BRACKET);
            msg.append(errorMessagePresenter);
            msg.append(StringConstants.RIGHT_BRACKET);
            LOG.warn(msg.toString());
        }
        presenter = errorMessagePresenter;
    }
    
}
