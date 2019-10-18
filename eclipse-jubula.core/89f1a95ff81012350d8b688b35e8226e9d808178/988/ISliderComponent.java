/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.common.tester.adapter.interfaces;

/**
 * @author BREDEX GmbH
 */
public interface ISliderComponent extends IWidgetComponent {

    /**
     * Gets the position of the component
     * @param units the units in which the position is defined (value/percent)
     * 
     * @return the position of the component
     */
    public String getPosition(String units);
    
    /**
     * Sets the position of the component
     * 
     * @param position the position to set
     * @param operator the operator
     * @param units the units in which the position is defined (value/percent)
     */
    public void setPosition(String position, String operator, String units);
}
