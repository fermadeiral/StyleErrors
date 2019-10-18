/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.extensions.wizard.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.jubula.extensions.wizard.utils.Tools;

/**
 * An action for the tester class.
 * 
 * @author BREDEX GmbH
 */
public class Action {

    /** The action's name */
    private String m_name;
    
    /** The action's list of parameters */
    private List<Parameter> m_parameters;
    
    /**
     * The constructor
     * @param name the action's name
     */
    public Action(String name) {
        this(name, new ArrayList<Parameter>());
    }
    
    /**
     * The constructor
     * @param name the action's name
     * @param parameters the list of parameters
     */
    public Action(String name, List<Parameter> parameters) {
        m_name = name;
        m_parameters = parameters;
    }

    /**
     * @return the action's name
     */
    public String getName() {
        return m_name;
    }

    /**
     * Sets the action's name
     * @param name the name to be set
     */
    public void setName(String name) {
        m_name = name;
    }
    
    /**
     * Adds a parameter to the parameters list
     * @param parameter the parameter to be added
     */
    public void addParameter(Parameter parameter) {
        m_parameters.add(parameter);
    }
    
    /**
     * Removes the action's parameter at the given index
     * @param index the index of the parameter to be deleted
     */
    public void removeParameter(int index) {
        m_parameters.remove(index);
    }
    
    /**
     * Removes the action's parameters at the given indices
     * @param indices the indices of the parameters to be deleted
     */
    public void removeParameters(int[] indices) {
        for (int i : indices) {
            removeParameter(i);
        }
    }
    
    /**
     * @return the total count of parameters
     */
    public int getParametersCount() {
        return m_parameters.size();
    }
    

    /**
     * @return the action's parameters list
     */
    public List<Parameter> getParameters() {
        return m_parameters;
    }

    /**
     * Sets the parameters list
     * @param parameters the list of parameters to be set
     */
    public void setParameters(List<Parameter> parameters) {
        m_parameters = parameters;
    }
    
    /**
     * @return all parameters of this action as a String of the form<br>
     * <code>"(Type1 name1, Type2 name2, ...)"</code>
     * 
     */
    public String getMethodParametersAsString() {
        if (m_parameters.size() > 0) {
            final StringBuilder sb = new StringBuilder();
            sb.append("("); //$NON-NLS-1$
            m_parameters
                .stream()
                .forEach(new Consumer<Parameter>() {
                    @Override
                    public void accept(Parameter p) {
                        sb.append(p.getType() + " " //$NON-NLS-1$
                                +  Tools
                                    .getCamelCase(p.getName()) + ", "); //$NON-NLS-1$
                    }
                });
            sb.replace(sb.lastIndexOf(","), sb.lastIndexOf(" "), ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            return sb.toString();
        }
        return "()"; //$NON-NLS-1$
    }
    
    /**
     * @return all parameters of this action as a String of the form<br>
     * <code>"(Type1) Parameter Name 1, (Type2) Parameter Name 2"</code>
     */
    public String getParametersAsString() {
        if (m_parameters.size() > 0) {
            final StringBuilder sb = new StringBuilder();
            m_parameters
                .stream()
                .forEach(new Consumer<Parameter>() {
                    @Override
                    public void accept(Parameter p) {
                        sb.append("(" + p.getType() + ")" + " "  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                + p.getName() + ", "); //$NON-NLS-1$
                    }
                });
            sb.replace(sb.lastIndexOf(","), sb.lastIndexOf(" "), ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            return sb.toString();
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_name)
                .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if ((obj instanceof Action)) {
            Action action = (Action) obj;
            return new EqualsBuilder()
                .append(m_name, action.getName())                
                .isEquals();
        }
        return false;
    }
    
    
}
