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

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jubula.client.core.gen.parser.parameter.lexer.LexerException;
import org.eclipse.jubula.client.core.gen.parser.parameter.node.EOF;
import org.eclipse.jubula.client.core.gen.parser.parameter.node.TEndFunctionArgsToken;
import org.eclipse.jubula.client.core.gen.parser.parameter.node.TFunctionToken;
import org.eclipse.jubula.client.core.gen.parser.parameter.node.Token;

/**
 * Lexes a given parameter string in order to determine whether it ends with 
 * the beginnings of a function call (e.g. "?", "?a", "?add(", etc).
 */
public class FunctionLocator {

    /** the located function text */
    private String m_functionContents;

    /**
     * Constructor
     * 
     * Performs the location of the function text.
     * 
     * @param contents The Parameter text to process.
     * @throws LexerException if an error occurs while lexing the Parameter 
     *                        text.
     * @throws IOException if an error occurs while lexing the Parameter text.
     */
    public FunctionLocator(String contents) throws LexerException, IOException {
        JubulaParameterLexer lexer = 
                new JubulaParameterLexer(new PushbackReader(
                        new StringReader(contents)));
        Token token = lexer.next();
        List<Token> functionTokens = null;
        while (!(token instanceof EOF)) {
            if (token instanceof TFunctionToken) {
                functionTokens = new LinkedList<Token>();
            } else if (token instanceof TEndFunctionArgsToken) {
                functionTokens = null;
            } else if (functionTokens != null) {
                functionTokens.add(token);
            }
            
            token = lexer.next();
        }

        if (functionTokens != null) {
            
            // we are in a function
            StringBuilder functionContentsBuilder = new StringBuilder();
            for (Token tokenInFunction : functionTokens) {
                functionContentsBuilder.append(tokenInFunction.getText());
            }
            m_functionContents = functionContentsBuilder.toString();
        }
    }

    /**
     * 
     * @return the located function text.
     */
    public String getCurrentFunction() {
        return m_functionContents;
    }
}
