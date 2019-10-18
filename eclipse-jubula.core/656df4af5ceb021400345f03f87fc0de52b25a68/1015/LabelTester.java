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
package org.eclipse.jubula.rc.common.tester;

import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITextComponent;

/**
 * 
 * @author BREDEX GmbH
 *
 */
public class LabelTester extends AbstractTextVerifiableTester {

    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        ITextComponent label = (ITextComponent) getComponent();
        return new String[] {label.getText()};
    }

}
