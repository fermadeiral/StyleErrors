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
package org.eclipse.jubula.client.core.functions;

import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.osgi.util.NLS;

/**
 * Abstract base class for methods useful in Evaluators
 * 
 * @author BREDEX GmbH
 * @created Feb 22, 2012
 */
public abstract class AbstractFunctionEvaluator 
    implements IFunctionEvaluator {
    /**
     * the current context this function is being called for
     */
    private FunctionContext m_context = null;

    /**
     * @param arguments
     *            evaluate() parameter
     * @param numParamsExpected
     *            how many parameters does the Evaluator expect
     * @throws InvalidDataException
     *             if the are not exactly numParamsExpected parameters in
     *             arguments
     */
    protected void validateParamCount(String[] arguments, int numParamsExpected)
        throws InvalidDataException {
        if (arguments.length != numParamsExpected) {
            throw new InvalidDataException(NLS.bind(
                    Messages.WrongNumFunctionArgs, new Integer[] {
                        numParamsExpected, arguments.length }),
                    MessageIDs.E_WRONG_NUM_FUNCTION_ARGS);
        }
    }

    /**
     * @return the context; may be <code>null</code>.
     */
    public FunctionContext getContext() {
        return m_context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(FunctionContext context) {
        m_context = context;
    }
}