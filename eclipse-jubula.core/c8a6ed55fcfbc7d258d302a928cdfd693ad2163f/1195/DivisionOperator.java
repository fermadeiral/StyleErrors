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
package org.eclipse.jubula.examples.aut.adder.swing.model;

/**
 * Performs a division operation.
 *
 * @author BREDEX GmbH
 * @created 15.04.2010
 */
public class DivisionOperator implements IOperator {
    /**
     * {@inheritDoc}
     */
    public float calculate(float lhs, float rhs) {
        return lhs / rhs;
    }
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "/"; //$NON-NLS-1$
    }
}
