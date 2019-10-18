/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.examples.api.adder;

import org.eclipse.jubula.toolkit.base.components.GraphicsComponent;
import org.eclipse.jubula.toolkit.concrete.components.TextComponent;
import org.eclipse.jubula.toolkit.concrete.components.TextInputComponent;

/** @author BREDEX GmbH */
public class SimpleAdder {
    /** first text input field */
    private TextInputComponent m_value1;
    /** second text input field */
    private TextInputComponent m_value2;
    /** calculate button */
    private GraphicsComponent m_calculate;
    /** result text field */
    private TextComponent m_result;

    /**
     * @param value1 first text input field
     * @param value2 second text input field
     * @param calculate calculate button
     * @param result result text field
     */
    public SimpleAdder(TextInputComponent value1, TextInputComponent value2,
            GraphicsComponent calculate, TextComponent result) {
        setValue1(value1);
        setValue2(value2);
        setCalculate(calculate);
        setResult(result);
    }

    /**
     * @return the value1
     */
    public TextInputComponent getValue1() {
        return m_value1;
    }

    /**
     * @param value1
     *            the value1 to set
     */
    private void setValue1(TextInputComponent value1) {
        m_value1 = value1;
    }

    /**
     * @return the value2
     */
    public TextInputComponent getValue2() {
        return m_value2;
    }

    /**
     * @param value2
     *            the value2 to set
     */
    private void setValue2(TextInputComponent value2) {
        m_value2 = value2;
    }

    /**
     * @return the calculate
     */
    public GraphicsComponent getCalculate() {
        return m_calculate;
    }

    /**
     * @param calculate
     *            the calculate to set
     */
    private void setCalculate(GraphicsComponent calculate) {
        m_calculate = calculate;
    }

    /**
     * @return the result
     */
    public TextComponent getResult() {
        return m_result;
    }

    /**
     * @param result
     *            the result to set
     */
    private void setResult(TextComponent result) {
        m_result = result;
    }
}