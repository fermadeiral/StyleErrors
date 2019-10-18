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
package org.eclipse.jubula.client.core.parser.parameter;

import java.io.PushbackReader;

import org.eclipse.jubula.client.core.gen.parser.parameter.lexer.Lexer;
import org.eclipse.jubula.client.core.gen.parser.parameter.node.TBeginFunctionArgsToken;
import org.eclipse.jubula.client.core.gen.parser.parameter.node.TEndFunctionArgsToken;

/**
 * Customized lexer for Jubula test data parameter strings.
 * 
 * Tracks the nesting level of functions in the parameter string and 
 * modifies the lexer state as necessary based on the current nesting level.
 */
public class JubulaParameterLexer extends Lexer {

    /** current depth of function nesting */
    private int m_functionDepth = 0;
    
    /**
     * Constructor
     * 
     * @param in Reader for input stream.
     */
    public JubulaParameterLexer(PushbackReader in) {
        super(in);
    }

    @Override
    protected void filter() {
        if (token instanceof TBeginFunctionArgsToken 
                && Lexer.State.FUNCTION_ARGS.equals(state)) {
            m_functionDepth++;
        }
        if (token instanceof TEndFunctionArgsToken) {
            m_functionDepth--;
        }
        if (m_functionDepth > 0
                && Lexer.State.NORMAL.equals(state)) {
            state = Lexer.State.FUNCTION_ARGS;
        }
    }
    
}
