/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.toolkit.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jubula.tools.internal.constants.TestDataConstants;

/** @author BREDEX GmbH */
public final class ValueSets {

    /** @author BREDEX GmbH */
    public enum AUTActivationMethod implements LiteralProvider {
        /** AUT activation method */
        autDefault("AUT_DEFAULT"), //$NON-NLS-1$
        /** AUT activation method */
        none("NONE"), //$NON-NLS-1$
        /** AUT activation method */
        titlebar("TITLEBAR"), //$NON-NLS-1$
        /** AUT activation method */
        northwest("NW"), //$NON-NLS-1$
        /** AUT activation method */
        northeast("NE"), //$NON-NLS-1$
        /** AUT activation method */
        southwest("SW"), //$NON-NLS-1$
        /** AUT activation method */
        southeast("SE"), //$NON-NLS-1$
        /** AUT activation method */
        center("CENTER"); //$NON-NLS-1$

        /** holds the value necessary for the RC side */
        private final String m_rcValue;

        /**
         * Constructor
         * 
         * @param rcValue
         *            the remote control side value
         */
        private AUTActivationMethod(String rcValue) {
            this.m_rcValue = rcValue;
        }

        /**
         * @return the value
         */
        public String rcValue() {
            return m_rcValue;
        }
        
        /**
         * @param literal
         *            the literal
         * @return the corresponding enum
         */
        @Deprecated
        public static AUTActivationMethod literalAsEnum(String literal) {
            for (AUTActivationMethod method : values()) {
                if (method.rcValue().equals(literal)) {
                    return method;
                }
            }
            throw new IllegalArgumentException();
        }
    }
    
    /** @author BREDEX GmbH */
    public enum BinaryChoice implements LiteralProvider {
        /** binary choice option */
        yes("yes"), //$NON-NLS-1$
        /** binary choice option */
        no("no"); //$NON-NLS-1$

        /** holds the value necessary for the RC side */
        private final String m_rcValue;

        /**
         * Constructor
         * 
         * @param rcValue
         *            the remote control side value
         */
        private BinaryChoice(String rcValue) {
            this.m_rcValue = rcValue;
        }

        /**
         * @return the value
         */
        public String rcValue() {
            return m_rcValue;
        }
    }
    
    /** @author BREDEX GmbH */
    public enum Direction implements LiteralProvider {
        /** direction value */
        up("up"), //$NON-NLS-1$
        /** direction value */
        down("down"), //$NON-NLS-1$
        /** direction value */
        left("left"), //$NON-NLS-1$
        /** direction value */
        right("right"); //$NON-NLS-1$

        /** holds the value necessary for the RC side */
        private final String m_rcValue;

        /**
         * Constructor
         * 
         * @param rcValue
         *            the remote control side value
         */
        private Direction(String rcValue) {
            this.m_rcValue = rcValue;
        }

        /**
         * @return the value
         */
        public String rcValue() {
            return m_rcValue;
        }
    }
    
    /** @author BREDEX GmbH */
    public enum KeyStroke implements LiteralProvider {
        /** key stroke */
        delete("DELETE"); //$NON-NLS-1$

        /** holds the value necessary for the RC side */
        private final String m_rcValue;

        /**
         * Constructor
         * 
         * @param rcValue
         *            the remote control side value
         */
        private KeyStroke(String rcValue) {
            this.m_rcValue = rcValue;
        }

        /**
         * @return the value
         */
        public String rcValue() {
            return m_rcValue;
        }
    }
    
    /** @author BREDEX GmbH */
    public enum Modifier implements CombinableLiteralProvider {
        /** modifier value */
        none("none"), //$NON-NLS-1$
        /** modifier value */
        shift("shift"), //$NON-NLS-1$
        /** modifier value */
        control("control"), //$NON-NLS-1$
        /** modifier value */
        alt("alt"), //$NON-NLS-1$
        /** modifier value */
        meta("meta"), //$NON-NLS-1$
        /** modifier value */
        cmd("cmd"), //$NON-NLS-1$
        /** modifier value */
        mod("mod"); //$NON-NLS-1$

        /** holds the value necessary for the RC side */
        private final String m_rcValue;

        /**
         * Constructor
         * 
         * @param rcValue
         *            the remote control side value
         */
        private Modifier(String rcValue) {
            this.m_rcValue = rcValue;
        }

        /**
         * @return the value
         */
        public String rcValue() {
            return m_rcValue;
        }
        
        @Override
        public String toString() {
            return rcValue();
        }
        
        /**
         * @param literal
         *            the literal
         * @return the corresponding enum
         */
        @Deprecated
        public static Modifier[] literalAsEnum(String literal) {
            ArrayList<Modifier> modifiers = new ArrayList<ValueSets.Modifier>();
            if (literal != null) {
                String[] split = literal.split(
                        TestDataConstants.COMBI_VALUE_SEPARATOR);
                for (String s : split) {
                    boolean found = false;
                    for (Modifier modifier : values()) {
                        if (modifier.rcValue().equals(s)) {
                            modifiers.add(modifier);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        throw new IllegalArgumentException("Unkown modifier: " + s); //$NON-NLS-1$
                    }
                }
            }
            return modifiers.toArray(new Modifier[modifiers.size()]);
        }
    }
    
    /** @author BREDEX GmbH */
    public enum Operator implements LiteralProvider {
        /** value comparison operator */
        equals("equals"), //$NON-NLS-1$
        /** value comparison operator */
        notEquals("not equals"), //$NON-NLS-1$
        /** value comparison operator */
        matches("matches"), //$NON-NLS-1$
        /** value comparison operator */
        simpleMatch("simple match"); //$NON-NLS-1$

        /** holds the value necessary for the RC side */
        private final String m_rcValue;

        /**
         * Constructor
         * 
         * @param rcValue
         *            the remote control side value
         */
        private Operator(String rcValue) {
            this.m_rcValue = rcValue;
        }

        /**
         * @return the value
         */
        public String rcValue() {
            return m_rcValue;
        }
        
        /**
         * @param literal
         *            the literal
         * @return the corresponding enum
         */
        @Deprecated
        public static Operator literalAsEnum(String literal) {
            for (Operator o : values()) {
                if (o.rcValue().equals(literal)) {
                    return o;
                }
            }
            throw new IllegalArgumentException();
        }
    }
    
    /** @author BREDEX GmbH */
    public enum SearchType implements LiteralProvider {
        /** search type value */
        relative("relative"), //$NON-NLS-1$
        /** search type value */
        absolute("absolute"); //$NON-NLS-1$

        /** holds the value necessary for the RC side */
        private final String m_rcValue;

        /**
         * Constructor
         * 
         * @param rcValue
         *            the remote control side value
         */
        private SearchType(String rcValue) {
            this.m_rcValue = rcValue;
        }

        /**
         * @return the value
         */
        public String rcValue() {
            return m_rcValue;
        }
    }
    
    /** @author BREDEX GmbH */
    public enum TreeDirection implements LiteralProvider {
        /** direction value */
        up("up"), //$NON-NLS-1$
        /** direction value */
        down("down"), //$NON-NLS-1$
        /** direction value */
        next("next"), //$NON-NLS-1$
        /** direction value */
        previous("previous"); //$NON-NLS-1$

        /** holds the value necessary for the RC side */
        private final String m_rcValue;

        /**
         * Constructor
         * 
         * @param rcValue
         *            the remote control side value
         */
        private TreeDirection(String rcValue) {
            this.m_rcValue = rcValue;
        }

        /**
         * @return the value
         */
        public String rcValue() {
            return m_rcValue;
        }
    }
    
    /** @author BREDEX GmbH */
    public enum Unit implements LiteralProvider {
        /** unit value */
        pixel("pixel"), //$NON-NLS-1$
        /** unit value */
        percent("percent"); //$NON-NLS-1$

        /** holds the value necessary for the RC side */
        private final String m_rcValue;

        /**
         * Constructor
         * 
         * @param rcValue
         *            the remote control side value
         */
        private Unit(String rcValue) {
            this.m_rcValue = rcValue;
        }

        /**
         * @return the value
         */
        public String rcValue() {
            return m_rcValue;
        }
    }
    
    /**
     * @author BREDEX GmbH 
     * @since 4.0
     */
    public enum Measure implements LiteralProvider {
        /** unit value */
        value("value"), //$NON-NLS-1$
        /** unit value */
        percent("percent"); //$NON-NLS-1$

        /** holds the value necessary for the RC side */
        private final String m_rcValue;

        /**
         * Constructor
         * 
         * @param rcValue
         *            the remote control side value
         */
        private Measure(String rcValue) {
            this.m_rcValue = rcValue;
        }

        /**
         * @return the value
         */
        public String rcValue() {
            return m_rcValue;
        }
    }
    
    /** @author BREDEX GmbH 
     * The InteractionMode is e.g. used to define which mouse button is used.
     */
    public enum InteractionMode implements LiteralProvider {
        /** primary value*/
        primary(1),
        /** tertiary value*/
        tertiary(2),
        /** secondary value*/
        secondary(3);
        
        /** mapping integer values to respective enums */
        private static Map<Integer, InteractionMode> map =
                new HashMap<Integer, InteractionMode>();
        
        /** holds the value necessary for the RC side */
        private final Integer m_rcValue;
        
        static {
            for (InteractionMode mode : InteractionMode.values()) {
                map.put(mode.rcIntValue(), mode);
            }
        }
        
        /**
         * Constructor
         * 
         * @param rcValue
         *            the remote control side value
         */
        private InteractionMode(Integer rcValue) {
            m_rcValue = rcValue;
        }
        
        /**
         * @return the value
         */
        public String rcValue() {
            return m_rcValue.toString();
        }
        
        /**
         * @return the real value with correct type
         */
        public Integer rcIntValue() {
            return m_rcValue;
        }
        
        /**
         * Returns the interaction mode corresponding to a given integer
         * @param i the integer
         * @return the corresponding interaction mode
         */
        public static InteractionMode valueOf(Integer i) {
            return map.get(i);
        }
    }

    /**
     * @author BREDEX GmbH 
     * @since 4.0
     * the anchor type is used in GEF to determine between incoming 
     * and outgoing anchors
     */
    public enum AnchorType implements LiteralProvider {
        /** incoming anchors */
        incoming("incoming"), //$NON-NLS-1$
        /** outgoing anchors */
        outgoing("outgoing"), //$NON-NLS-1$
        /** incoming and outgoing anchors */
        both("both"); //$NON-NLS-1$
        /** holds the value necessary for the RC side */
        private String m_value;
        
        /**
         * Constructor
         * 
         * @param rcValue
         *            the remote control side value
         */
        private AnchorType(String rcValue) {
            m_value = rcValue;
        }
        /**
         * @return the value
         * @since 4.0
         */
        public String rcValue() {
            return m_value;
        }
        
    }

    /**
     * @since 4.0
     * NumberComparisonOperator is used for standard number comparisons
     */
    public enum NumberComparisonOperator implements LiteralProvider {
        /** number comparison operator */
        equals("equal to"), //$NON-NLS-1$
        /** number comparison operator */
        greater("greater than"), //$NON-NLS-1$
        /** number comparison operator */
        greaterorEqual("greater or equal than"), //$NON-NLS-1$
        /** number comparison operator */
        less("less than"), //$NON-NLS-1$
        /** number comparison operator */
        lessOrEqual("less or equal than"); //$NON-NLS-1$
        
        /** holds the value necessary for the RC side */
        private String m_value;
        
        /**
         * Constructor
         * 
         * @param rcValue
         *            the remote control side value
         */
        private NumberComparisonOperator(String rcValue) {
            m_value = rcValue;
        }
        /**
         * @return the value
         */
        public String rcValue() {
            return m_value;
        }
        
    }
    /** Constructor */
    private ValueSets() {
        // hide
    }
}
