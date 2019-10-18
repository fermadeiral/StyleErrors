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
package org.eclipse.jubula.rc.javafx.tester.adapter;

import java.util.concurrent.Callable;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ISliderComponent;
import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.toolkit.enums.ValueSets;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.Slider;
import javafx.util.StringConverter;

/**
 * Slider Adapter
 *
 * @author BREDEX GmbH
 */
public class SliderAdapter extends
    JavaFXComponentAdapter<Slider> implements ISliderComponent {
    
    /** logger */
    private static final Logger LOG = LoggerFactory.getLogger(
            SliderAdapter.class);
    
    /**
     * Creates an object with the adapted Slider.
     *
     * @param objectToAdapt
     *            this must be an object of the Type <code>Slider</code>
     */
    public SliderAdapter(Slider objectToAdapt) {
        super(objectToAdapt);
    }

    @Override
    public String getPosition(String units) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getPosition", //$NON-NLS-1$
                (Callable<String>) () -> {
                double value;
                double absValue = getRealComponent().getValue();
                if (units.equalsIgnoreCase(
                        ValueSets.Measure.percent.rcValue())) {
                    value = 100 * absValue / getRealComponent().getMax();
                } else {
                    StringConverter<Double> labelFormatter = getRealComponent()
                            .getLabelFormatter();
                    if (labelFormatter != null) {
                        return labelFormatter.toString(absValue);
                    }
                    value = absValue;
                }
                return String.valueOf(value);
            });
    }

    
    /** {@inheritDoc} */
    public void setPosition(String position, String operator,
            String units) {
        Slider slider = getRealComponent();
        if (slider.isDisabled()) {
            throw new StepExecutionException(
                    "The slider is not enabled", EventFactory //$NON-NLS-1$
                    .createActionError("The slider is not enabled")); //$NON-NLS-1$
        }
        Double value = null;
        StringConverter<Double> labelFormatter = null;
        try {
            value = Double.valueOf(position);
        } catch (NumberFormatException nfe) {
            labelFormatter = slider.getLabelFormatter();
        }
        if (ValueSets.Operator.equals.rcValue().equalsIgnoreCase(operator)) {
            if (labelFormatter != null) {
                value = labelFormatter.fromString(position);
            }
            if (value == null) {
                throwInvalidValueMessage();
            }
        } else {
            // If the operator is not "equals" we have to iterate over the slider's
            // values as good as possible and try to match the desired position.
            // For that we jump from minor tick to minor tick.
            double min = slider.getMin();
            double max = slider.getMax();
            double incr = slider.getMajorTickUnit()
                    / (slider.getMinorTickCount() + 1);
            MatchUtil matcher = MatchUtil.getInstance();
            for (double val = min; val <= max; val += incr) {
                String stringToMatch;
                if (labelFormatter != null) {
                    stringToMatch = labelFormatter.toString(val);
                } else {
                    stringToMatch = String.valueOf(val);
                }
                if (matcher.match(stringToMatch, position, operator)) {
                    value = val;
                    break;
                }
            }
        }
        setValueProgrammatically(units, value);
    }

    /** throws message that input was invalid */
    private void throwInvalidValueMessage() {
        throw new StepExecutionException("Invalid input for slider", //$NON-NLS-1$
                EventFactory.createActionError(
                        TestErrorEvent.INVALID_INPUT));
    }

    /**
     * @param units the units
     * @param value the value
     */
    private void setValueProgrammatically(String units, double value) {
        double absValue;
        Slider slider = getRealComponent();
        if (units.equalsIgnoreCase(
                ValueSets.Measure.percent.rcValue())) {
            if (value < 0 || 100 < value) {
                throwInvalidValueMessage();
            }
            absValue = slider.getMin() + value
                    * ((slider.getMax() - slider.getMin())) * 0.01;
        } else {
            absValue = value;
        }
        double closestPossibleValue = absValue;
        
        double incr = slider.getMajorTickUnit()
                        / (slider.getMinorTickCount() + 1);
        double val = slider.getMin();
        while (val < absValue) {
            val += incr;
        }
        final double valueToSet;
        if (!slider.snapToTicksProperty().get()) {
            valueToSet = absValue;
        } else {
            valueToSet = (val - absValue) <= (absValue - (val - incr))
                    ? val : val - incr;
        }
        
        EventThreadQueuerJavaFXImpl.invokeAndWait("setSliderValue", //$NON-NLS-1$
            (Callable<Void>) () -> {
                slider.setValue(valueToSet);
                LOG.warn("Fallback - Value of slider was set programmatically for " //$NON-NLS-1$
                        + getRealComponent());
                return null;
            });
    }

}