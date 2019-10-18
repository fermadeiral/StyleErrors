/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.swt.tester;


import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IButtonComponent;


/**
 * The Toolkit specific implementation for <code>SWTButton</code> and subclasses.
 *
 * @author BREDEX GmbH
 */
public class ButtonTester extends 
    org.eclipse.jubula.rc.common.tester.ButtonTester {

    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        return new String[] { ((IButtonComponent)getComponent()).getText()};
    }
}