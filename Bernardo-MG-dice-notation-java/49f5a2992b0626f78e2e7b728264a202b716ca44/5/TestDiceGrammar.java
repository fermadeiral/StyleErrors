/**
 * Copyright 2014-2016 the original author or authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.wandrell.tabletop.dice.test.integration.generated.grammar;

import java.util.Iterator;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.wandrell.tabletop.dice.generated.DiceNotationGrammarLexer;
import com.wandrell.tabletop.dice.generated.DiceNotationGrammarParser;
import com.wandrell.tabletop.dice.generated.DiceNotationGrammarParser.ParseContext;
import com.wandrell.tabletop.dice.parser.listener.DefaultErrorListener;
import com.wandrell.tabletop.dice.test.util.config.parameter.DiceParametersFactory;

/**
 * Unit tests for the generated grammar classes, testing that they can process
 * dice notation.
 * <p>
 * Checks the following cases:
 * <ol>
 * <li>Strings with valid notation do not generate exceptions.</li>
 * </ol>
 * 
 * @author Bernardo Martínez Garrido
 */
public final class TestDiceGrammar {

    /**
     * Marker for the notation parameters.
     */
    protected static final String NOTATION = "notation";

    /**
     * Dice notation parameters.
     * <p>
     * It returns sets of a single parameter, containing a dice notation
     * expression.
     * 
     * @return dice notation expressions
     * @throws Exception
     *             if any error occurs while preparing the parameters
     */
    @DataProvider(name = NOTATION)
    public final static Iterator<Object[]> getNotationData() throws Exception {
        return DiceParametersFactory.getInstance().getDiceText();
    }

    /**
     * Default constructor.
     */
    public TestDiceGrammar() {
        super();
    }

    /**
     * Tests that strings with valid notation do not generate exceptions.
     * 
     * @param notation
     *            the notation to parse
     */
    @Test(dataProvider = NOTATION)
    public final void
            testParse_ValidNotation_NoException(final String notation) {
        final ParseContext context;

        context = getParser(notation).parse();

        Assert.assertNull(context.exception);
    }

    /**
     * Builds a grammar parser for the specified notation.
     * 
     * @param notation
     *            notation to parse
     * @return a parse tailored the the notation
     */
    private final DiceNotationGrammarParser getParser(final String notation) {
        final CharStream in;             // Characters stream
        final DiceNotationGrammarLexer lexer;   // Lexer
        final TokenStream tokens;        // Lexical tokens
        final DiceNotationGrammarParser parser; // Parser

        in = new ANTLRInputStream(notation);
        lexer = new DiceNotationGrammarLexer(in);
        tokens = new CommonTokenStream(lexer);

        parser = new DiceNotationGrammarParser(tokens);

        parser.addErrorListener(new DefaultErrorListener());

        return parser;
    }

}
