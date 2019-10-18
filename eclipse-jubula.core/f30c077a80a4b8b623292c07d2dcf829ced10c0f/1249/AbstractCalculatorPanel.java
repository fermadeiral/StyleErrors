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
package org.eclipse.jubula.examples.aut.adder.swing.gui;

import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

/**
 * Abstract base class for the calculator panels
 * 
 * @author BREDEX GmbH
 */
public abstract class AbstractCalculatorPanel extends JPanel {
    
    /**
     * Addend1 as string
     * @return Addend1 as string
     */
    public abstract String getAddend1();
    
    /**
     * Clears addend1
     */
    public abstract void clearAddend1();

    /**
     * Addend2 as string
     * @return Addend2 as string
     */
    public abstract String getAddend2();
    
    /**
     * Clears addend2
     */
    public abstract void clearAddend2();

    /**
     * Sum as string
     * @return Sum as string
     */
    public abstract String getSum();
    
    /**
     * Set sum
     * @param sum Sum
     */
    public abstract void setSum(String sum);
    
    /**
     * Clears the sum
     */
    public abstract void clearSum();
    
    /**
     * Set the font of the sum field
     * @param font font
     */
    public abstract void setSumFont(Font font);
    
    /**
     * Sets the operation
     * @param op the string representation of the operation
     */
    public abstract void setOperation(String op);
    
    /**
     * add a listener to the evaluate button
     * @param listener the listener
     */
    public abstract void addEvaluateActionListener(ActionListener listener);

}
