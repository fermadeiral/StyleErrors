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
package org.eclipse.jubula.communication.internal.message;

import java.util.List;
import java.util.Map;

import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ComponentClass;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Profile;

/**
 * This message transfers all Jubula components of type
 * {@link org.eclipse.jubula.tools.internal.xml.businessmodell.Component} and
 * subclasses. the components will be registered in the AUT server by executing
 * <code>SendAUTListOfSupportedComponentsCommand</code>.
 * 
 * @author BREDEX GmbH
 * @created 04.10.2004
 */
public final class SendAUTListOfSupportedComponentsMessage extends Message {
    // the data of this message BEGIN
    /** the "rich" list of component system components from the ITE */
    private List<Component> m_components = null;
    /** the "bare" mapping of technical type to tester class for API usage */
    private Map<ComponentClass, String> m_techTypeToTesterClassMapping = null;
    // the data of this message END

    /** fuzzy profile */
    private Profile m_profile;

    /** empty constructor for serialization */
    public SendAUTListOfSupportedComponentsMessage() {
        super();
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.SEND_COMPONENTS_COMMAND;
    }

    /** @return The list of <code>Component</code> objects. */
    public List<Component> getComponents() {
        return m_components;
    }

    /**
     * @param components
     *            The list of components to set. They are of type
     *            {@link Component} or subclasses
     */
    public void setComponents(List<Component> components) {
        m_components = components;
    }

    /** @return Returns the profile. */
    public Profile getProfile() {
        return m_profile;
    }

    /**
     * @param p
     *            The profile to set.
     */
    public void setProfile(Profile p) {
        this.m_profile = p;
    }

    /**
     * @return the techTypeToTesterClassMapping
     */
    public Map<ComponentClass, String> getTechTypeToTesterClassMapping() {
        return m_techTypeToTesterClassMapping;
    }

    /**
     * @param techTypeToTesterClassMapping
     *            the techTypeToTesterClassMapping to set
     */
    public void setTechTypeToTesterClassMapping(
        Map<ComponentClass, String> techTypeToTesterClassMapping) {
        m_techTypeToTesterClassMapping = techTypeToTesterClassMapping;
    }
}