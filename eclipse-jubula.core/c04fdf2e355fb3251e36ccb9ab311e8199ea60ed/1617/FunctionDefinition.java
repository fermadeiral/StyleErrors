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
package org.eclipse.jubula.client.core.functions;

import org.apache.commons.lang.Validate;

/**
 * Definition of a Function, as read from an extension. The Function has a list 
 * of parameters that each require a corresponding argument when the Function is
 * called/referenced. The Function may also support var args (variable 
 * arguments), 0..n additional arguments after the parameter arguments.
 */
public class FunctionDefinition {

    /** name of the Function */
    private String m_name;
    
    /** parameters for the Function */
    private ParameterDefinition[] m_parameters;
    
    /** var args for the Function, if the Function supports var args */
    private VarArgsDefinition m_varArgs;

    /** object capable of evaluating the Function */
    private IFunctionEvaluator m_evaluator;

    /**
     * Constructor
     * 
     * @param name The name of the Function. May not be <code>null</code>.
     * @param parameters The parameters for the Function. 
     *                   May not be <code>null</code>.
     * @param varArgs The type for var args for the Function, or 
     *                    <code>null</code> if the Function does not support 
     *                    var args. 
     * @param evaluator An object capabale of evaluating this Function.
     *                  May not be <code>null</code>.
     */
    public FunctionDefinition(String name, ParameterDefinition[] parameters, 
            VarArgsDefinition varArgs, IFunctionEvaluator evaluator) {

        Validate.notNull(name);
        Validate.notNull(parameters);
        Validate.notNull(evaluator);
        
        m_name = name;
        m_parameters = parameters;
        m_varArgs = varArgs;
        m_evaluator = evaluator;
    }

    /**
     * 
     * @return the name of the receiver. Never <code>null</code>.
     */
    public String getName() {
        return m_name;
    }

    /**
     * 
     * @return the parameters for the receiver. Never <code>null</code>. 
     *         Empty if receiver has no arguments.
     */
    public ParameterDefinition[] getParameters() {
        return m_parameters;
    }

    /**
     * 
     * @return the var args for the receiver, or <code>null</code> if
     *         the receiver does not accept var args.
     */
    public VarArgsDefinition getVarArgs() {
        return m_varArgs;
    }

    /**
     * 
     * @return the object capable of evaluating calls to this Function.
     *         Never <code>null</code>.
     */
    public IFunctionEvaluator getEvaluator() {
        return m_evaluator;
    }

}
