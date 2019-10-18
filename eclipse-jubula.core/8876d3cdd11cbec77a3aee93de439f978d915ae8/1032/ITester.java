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
package org.eclipse.jubula.rc.common.tester.interfaces;


/**
 * The interface for all tester classes.
 *
 * @author BREDEX GmbH
 * @created 19.04.2006
 */
public interface ITester {

    /**
     * Set the component the methods this tester class implements have
     * to work with. <br>
     * 
     * The methods are declared by the configuration file for supported
     * components.
     * 
     * @param graphicsComponent
     *            the component from the AUT <br>
     *            Notice: Every changes made to <code>control</code> affects
     *            the AUT.
     */
    public void setComponent(Object graphicsComponent);

    /**
     * Returns a descriptive text array that represents the given GUI component.
     * <br>
     * Descriptive texts can be received for example from buttons, labels and
     * table headers. If text is obviously data (for example in text input 
     * components) then it is not considered a descriptive text. <br>
     * 
     * If the component has no descriptive text then null, an empty array or
     * an array with length one and null or an empty string as content has to 
     * be returned.
     * 
     * @return array containing none, one or many texts representing the <br>
     *         GUI component or null
     */
    public String[] getTextArrayFromComponent();
}