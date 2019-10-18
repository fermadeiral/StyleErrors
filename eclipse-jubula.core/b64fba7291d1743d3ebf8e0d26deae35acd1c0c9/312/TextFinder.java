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
package org.eclipse.jubula.client.ui.rcp.search.query;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;


/**
 * This class can be used to search for a given search string multiple
 * times with respect to the {@link Operation}. The search pattern
 * for a regular expression is only created once in the constructor.
 *
 * @author BREDEX GmbH
 * @created Apr 30, 2013
 */
public class TextFinder {

    /** The regular expression pattern compiler. */
    private static PatternCompiler patternCompiler = new Perl5Compiler();

    /** The regular expression evaluator.*/
    private static PatternMatcher patternMatcher = new Perl5Matcher();

    /** The string searching for. */
    private String m_searchString;

    /** The search operation. */
    private Operation m_operation;

    /** The pattern for a regular expression search. */
    private Pattern m_pattern;

    /**
     * @param searchString The search string searching for. Also a regular
     *                     expression is possible, if a corresponding search
     *                     operation has been chosen.
     * @param operation The search operation.
     */
    public TextFinder(String searchString, Operation operation) {
        m_searchString = searchString;
        m_operation = operation;
        try {
            prepareSearch();
        } catch (MalformedPatternException e) {
            try {
                m_pattern = patternCompiler.compile(""); //$NON-NLS-1$
            } catch (MalformedPatternException e2) {
                // do nothing
            }
        }
    }

    /**
     * Prepare the search depending on the search operation.
     */
    private void prepareSearch() throws MalformedPatternException {
        switch (m_operation) {
            case IGNORE_CASE:
                m_searchString = m_searchString.toLowerCase();
                break;
            case REGEX_MATCH_CASE:
                m_pattern = patternCompiler.compile(m_searchString);
                break;
            case REGEX_IGNORE_CASE:
                m_pattern = patternCompiler.compile(m_searchString,
                        Perl5Compiler.CASE_INSENSITIVE_MASK);
                break;
            default:
        }
    }

    /**
     * @param text The text searching in.
     * @return True, if the text contains the search string given to the
     *         constructor with respect to the search operation, otherwise false.
     */
    public boolean matchSearchString(String text) {
        switch (m_operation) {
            case MATCH_CASE:
                return (text.indexOf(m_searchString) >= 0);
            case IGNORE_CASE:
                return (text.toLowerCase().indexOf(m_searchString) >= 0);
            case REGEX_MATCH_CASE:
            case REGEX_IGNORE_CASE:
                return patternMatcher.matches(text, m_pattern);
            default:
                return false;
        }
    }

}
