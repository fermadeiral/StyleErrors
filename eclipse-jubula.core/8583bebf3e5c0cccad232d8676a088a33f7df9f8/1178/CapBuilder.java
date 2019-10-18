/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.toolkit;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.communication.CAP;
import org.eclipse.jubula.communication.internal.message.MessageCap;
import org.eclipse.jubula.communication.internal.message.MessageParam;
import org.eclipse.jubula.tools.ComponentIdentifier;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;

/**
 * Class following builder pattern to create CAP instances
 * 
 * @author BREDEX GmbH
 * @since 4.0
 */
public class CapBuilder {

    /** Identifier for Boolean parameters */
    private static final String BOOLEAN_IDENTIFIER = Boolean.class.getName();

    /** Identifier for Integer parameters */
    private static final String INTEGER_IDENTIFIER = Integer.class.getName();

    /** Identifier for String parameters */
    private static final String STRING_IDENTIFIER = String.class.getName();

    /** the name of the method */
    private String m_rcMethod;

    /** the component identifier */
    private ComponentIdentifier m_ci = null;

    /** the parameters of the CAP */
    private List<MessageParam> m_params = new ArrayList<MessageParam>();

    /** whether component has default mapping */
    private boolean m_defaultMapping = true;

    /**
     * Builder for creating CAPs
     * 
     * @param rcMethod
     *            name of the rc method of the CAP
     */
    public CapBuilder(@NonNull String rcMethod) {
        Validate.notNull(rcMethod);
        m_rcMethod = rcMethod;
    }

    /**
     * @param value
     *            the value of the string parameter
     * @return the modified builder
     */
    public CapBuilder addParameter(@NonNull String value) {
        Validate.notNull(value);
        return addOptionalParameter(value);
    }

    /**
     * @param value
     *            the value of the string parameter
     * @return the modified builder
     */
    public CapBuilder addOptionalParameter(@Nullable String value) {
        m_params.add(
                new MessageParam(String.valueOf(value), STRING_IDENTIFIER));
        return this;
    }

    /**
     * @param value
     *            the value of the integer parameter
     * @return the modified builder
     */
    public CapBuilder addOptionalParameter(@Nullable Integer value) {
        m_params.add(
                new MessageParam(String.valueOf(value), INTEGER_IDENTIFIER));
        return this;
    }

    /**
     * @param value
     *            the value of the integer parameter
     * @return the modified builder
     */
    public CapBuilder addParameter(@NonNull Integer value) {
        Validate.notNull(value);
        return addOptionalParameter(value);
    }

    /**
     * @param value
     *            the value of the boolean parameter
     * @return the modified builder
     */
    public CapBuilder addParameter(@NonNull Boolean value) {
        Validate.notNull(value);
        return addOptionalParameter(value);
    }

    /**
     * @param value
     *            the value of the boolean parameter
     * @return the modified builder
     */
    public CapBuilder addOptionalParameter(@Nullable Boolean value) {
        m_params.add(
                new MessageParam(String.valueOf(value), BOOLEAN_IDENTIFIER));
        return this;
    }

    /**
     * Sets the component identifier.
     * 
     * @param ci
     *            the component identifier
     * @return the modified builder
     */
    public CapBuilder setComponentIdentifier(
            @Nullable ComponentIdentifier ci) {
        Validate.isTrue(ci instanceof IComponentIdentifier);
        m_ci = ci;
        return this;
    }

    /**
     * Sets whether the component has a default mapping or not (Default value is
     * <code>true</code>).
     * 
     * @param defaultMapping
     *            whether the component has default mapping
     * @return the modified builder
     */
    public CapBuilder setDefaultMapping(@NonNull Boolean defaultMapping) {
        Validate.notNull(defaultMapping);
        m_defaultMapping = defaultMapping;
        return this;
    }

    /**
     * build CAP instance
     * 
     * @return CAP instance
     */
    @NonNull public CAP build() {
        MessageCap messageCap = new MessageCap();
        messageCap.setMethod(m_rcMethod);
        messageCap.sethasDefaultMapping(m_defaultMapping);
        messageCap.setCi((IComponentIdentifier) m_ci);
        for (MessageParam param : m_params) {
            messageCap.addMessageParam(param);
        }
        return messageCap;
    }

}
