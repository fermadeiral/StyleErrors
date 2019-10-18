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
package org.eclipse.jubula.examples.aut.adder.swt.model;

/**
 * Identifies an operator.
 * 
 * @author BREDEX GmbH
 * @created 27.02.2006
 */
public interface IOperator {
    /**
     * Calculates the result by applying the operator on the passed values.
     * 
     * @param val1 The operator's left side.
     * @param val2 The operator's right side.
     * @return The result.
     */
    public float calculate(float val1, float val2);
}
