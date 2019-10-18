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
package org.eclipse.jubula.client.ui.controllers.propertysources;

import org.eclipse.swt.graphics.Image;

/**
 * Interface to control the properties.
 *
 * @author BREDEX GmbH
 * @created 10.01.2005
 */
public interface IPropertyController {
    
    /**
     * Sets the given property value into the model.
     * 
     * @param value
     *            the value.
     * @return <code>true</code> if the property has been set,
     *         <code>false</code> otherwise
     */
    public boolean setProperty(Object value);
    
    /** 
     * Gets the property value from the model.
     *  @return the property.
     */
    public Object getProperty();
    
    /**
     * Gets the Image for the Property.
     * @return an <code>Image</code> value. The Image.
     */
    public Image getImage();
}
