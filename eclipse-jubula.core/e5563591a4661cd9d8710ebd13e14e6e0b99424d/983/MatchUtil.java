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
package org.eclipse.jubula.rc.common.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.oro.text.GlobCompiler;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.toolkit.enums.ValueSets.Operator;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;


/**
 * Utility class for string matching operations
 *
 * @author BREDEX GmbH
 * @created 02.12.2005
 */
public class MatchUtil {
    
    /**
     * 
     * Helper class to return a string and its position 
     *
     */
    public static class FindResult {
        /** Position of pattern in string  */
        private int m_pos;
        /** string matching pattern */
        private String m_str;
        
        /**
         * Store data
         * @param str string matching pattern
         * @param pos Position of pattern in string
         */
        public FindResult(String str, int pos) {
            m_str = str;
            m_pos = pos;
        }
        /**
         * @return the pos
         */
        public int getPos() {
            return m_pos;
        }
        /**
         * @return the str
         */
        public String getStr() {
            return m_str;
        }
    }
    
    /**
     * A match operation
     */
    private interface MatchOperation {
        /**
         * Checks if <code>text</code> matches <code>pattern</code>
         * 
         * @param text
         *          a text
         * @param pattern
         *          a pattern
         * @return
         *          <code>true</code> if <code>text</code> matches
         *          <code>pattern</code>.
         */
        public boolean matches(String text, String pattern);
    }

    /**
     * A match operation
     */
    private interface FindOperation {
        /**
         * Checks if <code>text</code> matches <code>pattern</code>
         * 
         * @param text
         *          a text
         * @param pattern
         *          a pattern
         * @return
         *          position and value of match
         */
        public FindResult find(String text, String pattern);
    }

    /**
     * match if text equals pattern
     */
    public static final String EQUALS = Operator.equals.rcValue();

    /**
     * match if text and pattern are not equal
     */
    public static final String NOT_EQUALS = Operator.notEquals.rcValue();
    
    /**
     * match if text matches a regexp pattern 
     */
    public static final String MATCHES_REGEXP = Operator.matches.rcValue();
    
    /**
     * match if text matches a Unix-style glob pattern 
     */
    public static final String MATCHES_GLOB = Operator.simpleMatch.rcValue();
    
    /**
     * default operator
     */
    public static final String DEFAULT_OPERATOR = EQUALS;
    
    /**
     * <code>MatchUtil</code> instance
     */
    private static MatchUtil instance = new MatchUtil();
    
    /**
     * Maps operators to operations
     */
    private Map<String, MatchOperation> m_operationMap = 
        new HashMap<String, MatchOperation>();

    /**
     * Maps operators to operations
     */
    private Map<String, FindOperation> m_findOperationMap = 
        new HashMap<String, FindOperation>();

    /**
     * forbid construction
     */
    private MatchUtil() {
        m_operationMap.put(EQUALS, new MatchOperation() {
            public boolean matches(String text, String pattern) {
                return isEqual(text, pattern);
            }
        });
        m_operationMap.put(NOT_EQUALS, new MatchOperation() {
            public boolean matches(String text, String pattern) {
                return !isEqual(text, pattern);
            }
        });
        m_operationMap.put(MATCHES_REGEXP, new MatchOperation() {
            public boolean matches(String text, String pattern) {
                return matchesRegExp(text, pattern);
            }
        });
        m_operationMap.put(MATCHES_GLOB, new MatchOperation() {
            public boolean matches(String text, String pattern) {
                return matchesGlob(text, pattern);
            }
        });
        m_findOperationMap.put(EQUALS, new FindOperation() {
            public FindResult find(String text, String pattern) {
                int index = text.indexOf(pattern);
                return new FindResult(index != -1 ? pattern : null, index);
            }
        });
        m_findOperationMap.put(NOT_EQUALS, new FindOperation() {
            public FindResult find(String text, String pattern) {
                int index = text.indexOf(pattern);
                return new FindResult(index != -1 ? pattern : null, index);
            }
        });
        m_findOperationMap.put(MATCHES_REGEXP, new FindOperation() {
            public FindResult find(String text, String pattern) {
                return findRegExp(text, pattern);
            }
        });
        m_findOperationMap.put(MATCHES_GLOB, new FindOperation() {
            public FindResult find(String text, String pattern) {
                return findGlob(text, pattern);
            }
        });
    }
    
    /**
     * Returns the <code>MatchUtil</code> instance
     * 
     * @return
     *      the <code>MatchUtil</code> instance
     */
    public static MatchUtil getInstance() {
        return instance;
    }
    
    /**
     * Checks if <code>text</code> matches <code>pattern</code>.
     *  
     * @param text
     *          a text
     * @param pattern
     *          a pattern
     * @param operator
     *          operator used for matching
     * @return
     *          position and value of match
     * @throws StepExecutionException
     *          if the operator is not known
     */
    public FindResult find(String text, String pattern, String operator)
        throws StepExecutionException {
        
        if (!m_findOperationMap.containsKey(operator)) {
            TestErrorEvent event = EventFactory.createActionError(
                    TestErrorEvent.UNKNOWN_OPERATOR, new Object[] { operator });
            throw new StepExecutionException("unknown operator", event); //$NON-NLS-1$
        }
        if (pattern == null) {
            TestErrorEvent event = EventFactory.createActionError(
                TestErrorEvent.MALFORMED_REGEXP, new Object[] { pattern });
            throw new StepExecutionException("null pattern", event); //$NON-NLS-1$
        }
        FindOperation op = m_findOperationMap.get(operator);
        
        return op.find(text == null ? StringConstants.EMPTY : text, pattern);
    }

    /**
     * Checks if <code>text</code> matches <code>pattern</code>.
     *  
     * @param text
     *          a text
     * @param pattern
     *          a pattern
     * @param operator
     *          operator used for matching
     * @return
     *          <code>true</code> if <code>text</code> matches
     *          <code>pattern</code>
     * @throws StepExecutionException
     *          if the operator is not known
     */
    public boolean match(String text, String pattern, String operator)
        throws StepExecutionException {
        
        if (!m_operationMap.containsKey(operator)) {
            TestErrorEvent event = EventFactory.createActionError(
                    TestErrorEvent.UNKNOWN_OPERATOR, new Object[] { operator });
            throw new StepExecutionException("unknown operator", event); //$NON-NLS-1$
        }
        MatchOperation op = m_operationMap.get(operator);
        return op.matches(text == null ? StringConstants.EMPTY : text, pattern);
    }

    /**
     * Checks if <code>text</code> matches one of the patterns.
     * for equals and match-operator return true, if one of the given patterns
     * matches the given text (logical or)
     * for notequals-operator returns true, if all given patterns not match the
     * given text (logical and)
     * 
     * @param text
     *          a text
     * @param patterns
     *          several patterns
     * @param operator
     *          operator used for matching
     * @return
     *          <code>true</code> if <code>text</code> matches a pattern
     * @throws StepExecutionException
     *          if the operator is not known
     */
    public boolean match(String text, String[] patterns, String operator)
        throws StepExecutionException {
        boolean result = true;
        for (int i = 0; i < patterns.length; ++i) {
            if (operator.startsWith("not")) { //$NON-NLS-1$
                // logical and for each pattern and use of not equals-operator
                if (!(match(text, patterns[i], operator))) {
                    result = false;
                    break;
                }
                // logical or for each pattern and use of equals-/matches-operator
            } else {
                if (match(text, patterns[i], operator)) {
                    result = true;
                    break;
                } 
                result = false;
            }
        }
        return result;
    }
    
    /**
     * Returns <code>true</code> if both strings are equal
     * 
     * @param text
     *          a text
     * @param pattern
     *          a pattern
     * @return
     *          <code>true</code> if both strings are equal
     */
    private boolean isEqual(String text, String pattern) {
        return StringUtils.equals(text, pattern);
    }

    /**
     * Returns <code>true</code> if <code>text</code> matches the
     * regular expression <code>pattern</code>
     * 
     * @param text
     *          a text
     * @param pattern
     *          a pattern
     * @return
     *          <code>true</code> if <code>text</code> matches
     *          <code>pattern</code>
     */
    private boolean matchesRegExp(String text, String pattern) {
        PatternCompiler pc = new Perl5Compiler();
        PatternMatcher matcher = new Perl5Matcher();
        try {
            Pattern p = pc.compile(pattern, Perl5Compiler.SINGLELINE_MASK);
            return matcher.matches(text, p);
        } catch (MalformedPatternException exc) {
            TestErrorEvent event = EventFactory.createActionError(
                    TestErrorEvent.MALFORMED_REGEXP, new Object[] { pattern });
            throw new StepExecutionException("malformed regular expression", //$NON-NLS-1$
                    event);
        }
    }

    /**
     * Returns <code>true</code> if <code>text</code> matches the
     * glob <code>pattern</code>
     * 
     * @param text
     *          a text
     * @param pattern
     *          a pattern
     * @return
     *          <code>true</code> if <code>text</code> matches
     *          <code>pattern</code>
     */
    private boolean matchesGlob(String text, String pattern) {
        return matchesRegExp(text, GlobCompiler.globToPerl5(pattern
            .toCharArray(), GlobCompiler.DEFAULT_MASK));
    }

    /**
     * Returns <code>text</code> matches the
     * regular expression <code>pattern</code>
     * 
     * @param text
     *          a text
     * @param pattern
     *          a pattern
     * @return
     *          value and position of match
     */
    private FindResult findRegExp(String text, String pattern) {
        PatternCompiler pc = new Perl5Compiler();
        PatternMatcher matcher = new Perl5Matcher();
        try {
            Pattern p = pc.compile(pattern, Perl5Compiler.SINGLELINE_MASK);
            matcher.contains(text, p);
            MatchResult match = matcher.getMatch();
            if (match != null) {
                return new FindResult(match.toString(), 
                    match.beginOffset(0));
            }
            return null;
        } catch (MalformedPatternException exc) {
            TestErrorEvent event = EventFactory.createActionError(
                    TestErrorEvent.MALFORMED_REGEXP, new Object[] { pattern });
            throw new StepExecutionException("malformed regular expression", //$NON-NLS-1$
                    event);
        }
    }

    /**
     * Returns <code>text</code> matches the
     * glob <code>pattern</code>
     * 
     * @param text
     *          a text
     * @param pattern
     *          a pattern
     * @return
     *          value and position of match
     */
    private FindResult findGlob(String text, String pattern) {
        return findRegExp(text, GlobCompiler.globToPerl5(pattern.toCharArray(),
            GlobCompiler.DEFAULT_MASK));
    }

}
