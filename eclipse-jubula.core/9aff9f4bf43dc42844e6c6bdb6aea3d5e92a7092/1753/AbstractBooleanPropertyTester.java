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
package org.eclipse.jubula.client.core.propertytester;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 * @created Jul 20, 2011
 */
public abstract class AbstractBooleanPropertyTester extends PropertyTester {
    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(AbstractBooleanPropertyTester.class);

    /** {@inheritDoc} */
    public final boolean test(Object receiver, String property, Object[] args,
            Object expectedValue) {
        if (receiver != null) {
            if (getType().isAssignableFrom(receiver.getClass())) {
                if (ArrayUtils.indexOf(getProperties(), property) >= 0) {
                    boolean expectedBoolean = expectedValue instanceof Boolean 
                        ? ((Boolean)expectedValue).booleanValue() : true;
                    return testImpl(receiver, property, args) 
                        == expectedBoolean;
                }
                LOG.warn(NLS.bind(Messages.PropertyTesterPropertyNotSupported,
                        property));
                return false;
            }
        }
        String receiverClass = receiver != null ? receiver.getClass().getName()
                : "null"; //$NON-NLS-1$
        LOG.warn(NLS.bind(Messages.PropertyTesterTypeNotSupported,
                receiverClass));
        return false;
    }

    /**
     * Executes the property test determined by the parameter
     * <code>property</code>.
     * 
     * @param receiver
     *            the receiver of the property test
     * @param property
     *            the property to test
     * @param args
     *            additional arguments to evaluate the property. If no arguments
     *            are specified in the <code>test</code> expression an array of
     *            length 0 is passed
     * 
     * @return returns <code>true</code> if test was successful; otherwise
     *         <code>false</code> is returned
     */
    public abstract boolean testImpl(Object receiver, String property,
            Object[] args);

    /**
     * @return the expected element type this property tester has been
     *         registered for
     */
    public abstract Class<? extends Object> getType();

    /**
     * @return an array of supported property names
     */
    public abstract String[] getProperties();
}
