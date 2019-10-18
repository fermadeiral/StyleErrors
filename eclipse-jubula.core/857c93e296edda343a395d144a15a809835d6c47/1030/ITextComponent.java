/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.common.tester.adapter.interfaces;
/**
 * @author BREDEX GmbH
 */
public interface ITextComponent extends IWidgetComponent {

    /**
     * Gets the value of the component, or if there are more than the first
     * selected.
     * 
     * @return the value of the component
     */
    public String getText();
}
