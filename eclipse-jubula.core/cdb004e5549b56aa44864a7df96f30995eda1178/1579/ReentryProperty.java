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
package org.eclipse.jubula.client.core.model;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * class for enumeration of reentry properties for resuming of 
 * testexecution after execution of errorhandler
 *
 * @author BREDEX GmbH
 * @created 04.04.2005
 */
public final class ReentryProperty {
    
    
    /**
     * <code>CONTINUE</code> continue testexecution with next step
     */
    public static final ReentryProperty CONTINUE = 
        new ReentryProperty(1, "CONTINUE"); //$NON-NLS-1$

    /**
     * <code>REPEAT</code> continue testexecution with actual step (which
     * caused the error)
     */
    public static final ReentryProperty REPEAT = new ReentryProperty(2, "REPEAT"); //$NON-NLS-1$

    /**
     * <code>BREAK</code> continue testexecution with next testcase
     */
    public static final ReentryProperty BREAK = new ReentryProperty(3, "BREAK"); //$NON-NLS-1$

    /**
     * <code>GOTO</code> continue testexecution with next nested testcase
     */
    public static final ReentryProperty GOTO = new ReentryProperty(4, "GO TO"); //$NON-NLS-1$

    /**
     * <code>RETURN</code> continue testexecution with next step (Cap or testcase)
     * in testcase, which contains the actual eventhandler
     */
    public static final ReentryProperty RETURN = new ReentryProperty(5, "RETURN"); //$NON-NLS-1$

    /**
     * <code>STOP</code> stop testexecution after execution of
     * errorHandler
     */
    public static final ReentryProperty STOP = new ReentryProperty(6, "PAUSE"); //$NON-NLS-1$

    /**
     * <code>EXIT</code> cancel testexecution
     */
    public static final ReentryProperty EXIT = new ReentryProperty(7, "EXIT"); //$NON-NLS-1$
    
    /**
     * <code>RETRY</code> retry test step
     */
    public static final ReentryProperty RETRY = new ReentryProperty(8, "RETRY"); //$NON-NLS-1$
    
    /**
     * <code>CONDITION</code> placeholder to indicate a condition
     */
    public static final ReentryProperty CONDITION = new ReentryProperty(9, "CONDITION"); //$NON-NLS-1$

    /** Array of existing Reentry Properties */
    public static final ReentryProperty[] REENTRY_PROP_ARRAY = 
    {BREAK, CONTINUE, EXIT, RETURN, STOP, RETRY};

    /** Array of Reentry Properties available for test suites */
    public static final ReentryProperty[] TS_REENTRY_PROP_ARRAY = 
    {CONTINUE, EXIT, STOP};
    
    /** The logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(ReentryProperty.class);

    /**
     * <code>m_value</code> reentry property
     */
    private int m_value = 0;
    
    /**
     * <code>m_internalName</code> fixed name for property
     */
    private String m_name;

    /**
     * both values should not be changed!
     * @param value status for Reentry Property
     * @param name the internal name
     */
    private ReentryProperty(int value, String name) {
        m_value = value;
        m_name = name;
    }
    
    /**
     * empty constructor
     */
    private ReentryProperty() {
        // nothing
    }
    
    /**
     * get the Proeprty for an specific int value, used by persistence layer.
     * @param reentryPropValue int value from DB
     * @return the property for the supplied int value
     * * @throws InvalidDataException if the integer is not known
     */
    public static ReentryProperty getProperty(Integer reentryPropValue)
        throws InvalidDataException {
        int val = reentryPropValue == null ? 0 : reentryPropValue.intValue();
        validateValue(val);
        switch (val) {
            case 1:
                return CONTINUE;
            case 2:
                return REPEAT;
            case 3:
                return BREAK;
            case 4:
                return GOTO;
            case 5:
                return RETURN;
            case 6:
                return STOP;
            case 7:
                return EXIT;
            case 8:
                return RETRY;
            case 9:
                return CONDITION;
            default:
                return null; // can not happen, values are validated
        }
    }
    
    /**
     * get the Proeprty for an specific int value, used by persistence layer.
     * @param reentryPropValue string value
     * @return the property(Integer) for the supplied string value
     * @throws InvalidDataException if the i18n name is not known
     */
    public static Integer getProperty(String reentryPropValue) 
        throws InvalidDataException {
        String val = StringUtils.defaultIfBlank(reentryPropValue,
                StringConstants.EMPTY);
        if (val.equals(Messages.EventExecTestCasePOCONTINUE)) { 
            return CONTINUE.getValue();
        } else if (val.equals(Messages.EventExecTestCasePOREPEAT)) { 
            return REPEAT.getValue();
        } else if (val.equals(Messages.EventExecTestCasePOBREAK)) { 
            return BREAK.getValue();
        } else if (val.equals(Messages.EventExecTestCasePOGOTO)) { 
            return GOTO.getValue();
        } else if (val.equals(Messages.EventExecTestCasePORETURN)) { 
            return RETURN.getValue();
        } else if (val.equals(Messages.EventExecTestCasePOSTOP)) { 
            return STOP.getValue();
        } else if (val.equals(Messages.EventExecTestCasePOEXIT)) { 
            return EXIT.getValue();
        } else if (val.equals(Messages.EventExecTestCasePORETRY)) { 
            return RETRY.getValue();
        } 
        LOG.error(Messages.UnsupportedReentryProperty + StringConstants.SPACE 
            + val); 
        throw new InvalidDataException(Messages.UnsupportedReentryProperty 
            + StringConstants.SPACE + val, MessageIDs.E_UNSUPPORTED_REENTRY);
    }
    
    /**
     * 
     * @param name the fixed name of a {@link ReentryProperty}
     * @return the reentryProperty based on the {@link ReentryProperty#getValue()}
     * @throws InvalidDataException if the name is not known
     */
    public static ReentryProperty getPropertyFromName(String name)
            throws InvalidDataException {
        String val = StringUtils.defaultIfBlank(name, StringConstants.EMPTY);
        if (val.equalsIgnoreCase(CONTINUE.getName())) {
            return CONTINUE;
        } else if (val.equalsIgnoreCase(REPEAT.getName())) {
            return REPEAT;
        } else if (val.equalsIgnoreCase(BREAK.getName())) {
            return BREAK;
        } else if (val.equalsIgnoreCase(GOTO.getName())) {
            return GOTO;
        } else if (val.equalsIgnoreCase(RETURN.getName())) {
            return RETURN;
        } else if (val.equalsIgnoreCase(STOP.getName()) || val.equalsIgnoreCase("STOP")) { //$NON-NLS-1$
            return STOP;
        } else if (val.equalsIgnoreCase(EXIT.getName())) {
            return EXIT;
        } else if (val.equalsIgnoreCase(RETRY.getName())) {
            return RETRY;
        } 
        LOG.error(Messages.UnsupportedReentryProperty + StringConstants.SPACE 
            + val); 
        throw new InvalidDataException(Messages.UnsupportedReentryProperty 
            + StringConstants.SPACE + val, MessageIDs.E_UNSUPPORTED_REENTRY);
    }

    /**
     * @param value
     *            value for reentry property
     * {@inheritDoc}
     *  
     */
    private static void validateValue(int value) throws InvalidDataException {
        if (value == 9) {
            // CONDITION
            return;
        }
        for (int i = 0; i < REENTRY_PROP_ARRAY.length; i++) {
            ReentryProperty prop = REENTRY_PROP_ARRAY[i];
            if (prop.getValue() == value) {
                return;
            }
        }
        LOG.error(Messages.UnsupportedReentryProperty + StringConstants.SPACE
            + value); 
        throw new InvalidDataException(Messages.UnsupportedReentryProperty
                + StringConstants.SPACE + value,
                MessageIDs.E_UNSUPPORTED_REENTRY);
    }

    /**
     * @return the hashcode
     */
    public int hashCode() {
        return new HashCodeBuilder().append(m_value).toHashCode();
    }
    
    
    /**
     * only to prevent problems in case of loading this class with different
     * classloaders
     * @param obj the object to equal
     * @return if equals or not
     */
    public boolean equals(Object obj) {
        return (obj instanceof ReentryProperty) 
            ? ((ReentryProperty)obj).getValue() == m_value : false;
    }
    
    /**
     * @return Returns the value for reentryProperty.
     */
    public int getValue() {
        return m_value;
    }
    /**
     * @return the name of the {@link ReentryProperty}
     */
    public String getName() {
        return m_name;
    }

    /**
    * @return a String representation of this object.
    */
    public String toString() {
        switch (m_value) {
            case 1:
                return Messages.EventExecTestCasePOCONTINUE; 
            case 2:
                return Messages.EventExecTestCasePOREPEAT; 
            case 3:
                return Messages.EventExecTestCasePOBREAK; 
            case 4:
                return Messages.EventExecTestCasePOGOTO; 
            case 5:
                return Messages.EventExecTestCasePORETURN; 
            case 6:
                return Messages.EventExecTestCasePOSTOP; 
            case 7:
                return Messages.EventExecTestCasePOEXIT; 
            case 8:
                return Messages.EventExecTestCasePORETRY;
            case 9: // condition - just a placeholder, so is never shown
                return StringUtils.EMPTY;
            default:
                Assert.notReached(Messages.WrongTypeOfReentryProperty 
                    + StringConstants.EXCLAMATION_MARK); 
                return StringConstants.EMPTY;
        }
    }
    
}
