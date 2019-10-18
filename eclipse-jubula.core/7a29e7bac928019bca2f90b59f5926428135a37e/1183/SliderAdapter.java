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
package org.eclipse.jubula.rc.swt.tester.adapter;

import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ISliderComponent;
import org.eclipse.jubula.toolkit.enums.ValueSets;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.swt.widgets.Slider;
/**
 * @author BREDEX GmbH
 */
public class SliderAdapter extends ControlAdapter implements ISliderComponent {
    /** the list */
    private Slider m_slider;
    
    /**
     * @param objectToAdapt -
     */
    public SliderAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_slider = (Slider) objectToAdapt;
    }

    /** {@inheritDoc} */
    public String getPosition(final String units) {
        return getEventThreadQueuer().invokeAndWait(
                "getPosition", new IRunnable<String>() { //$NON-NLS-1$
                    public String run() {
                        double value;
                        int absValue = m_slider.getSelection();
                        if (units.equalsIgnoreCase(
                                ValueSets.Measure.percent.rcValue())) {
                            value = 100 * absValue
                                    / (m_slider.getMaximum() - m_slider
                                            .getMinimum());
                        } else {
                            value = absValue;
                        }
                        return String.valueOf(value);
                    }
                });
    }

    /** {@inheritDoc} */
    public void setPosition(final String position, String operator,
            final String units) {
        getEventThreadQueuer().invokeAndWait(
                "setPosition", new IRunnable<Void>() { //$NON-NLS-1$
                    public Void run() {
                        int value = 0;
                        try {
                            value = Integer.valueOf(position);
                        } catch (NumberFormatException nfe) {
                            throwInvalidInputMessage();
                        }
                        setValueProgrammatically(units, value);
                        return null;
                    }

                });
    }
    /**
     * @param units the units
     * @param value the value
     */
    private void setValueProgrammatically(final String units,
            int value) {
        if (!m_slider.isEnabled()) {
            throw new StepExecutionException(
                    "The slider is not enabled", EventFactory //$NON-NLS-1$
                    .createActionError("The slider is not enabled")); //$NON-NLS-1$
        }
        final int valueToSet;
        if (units.equalsIgnoreCase(
                ValueSets.Measure.percent.rcValue())) {
            if (value < 0 || 100 < value) {
                throwInvalidInputMessage();
            }
            valueToSet = (int) (m_slider.getMinimum() + value
                    * ((m_slider.getMaximum() - m_slider.getMinimum()) * 0.01));
        } else {
            valueToSet = value;
        }
        m_slider.setSelection(valueToSet);
    }
    
    /** throws invalid input message */
    private void throwInvalidInputMessage() {
        throw new StepExecutionException("Invalid input for slider", //$NON-NLS-1$
                EventFactory.createActionError(
                        TestErrorEvent.INVALID_INPUT));
    }

    
}
