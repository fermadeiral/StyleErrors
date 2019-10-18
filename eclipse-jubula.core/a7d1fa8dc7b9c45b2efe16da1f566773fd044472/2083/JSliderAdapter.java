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
package org.eclipse.jubula.rc.swing.tester.adapter;

import java.util.Dictionary;

import javax.swing.JLabel;
import javax.swing.JSlider;

import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ISliderComponent;
import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.toolkit.enums.ValueSets;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
/**
 * Implementation of the Interface <code>ISliderAdapter</code> as a
 * adapter for the <code>JSlider</code> component.
 * @author BREDEX GmbH
 */
public class JSliderAdapter extends JComponentAdapter implements
        ISliderComponent {
    
    /** the actual slider */
    private JSlider m_slider;
    
    /**
     * @param objectToAdapt 
     */
    public JSliderAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_slider = (JSlider) objectToAdapt;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getPosition(final String units) {
        return getEventThreadQueuer().invokeAndWait(
                "getPosition", new IRunnable<String>() { //$NON-NLS-1$
                    public String run() {
                        double value;
                        int absValue = m_slider.getValue();
                        if (units.equalsIgnoreCase(
                                ValueSets.Measure.percent.rcValue())) {
                            value = 100 * absValue
                                    / (m_slider.getMaximum() - m_slider
                                            .getMinimum());
                        } else {
                            Dictionary labelFormatter =
                                    m_slider.getLabelTable();
                            if (labelFormatter != null) {
                                Object obj = labelFormatter.get(absValue);
                                if (obj != null && obj instanceof JLabel) {
                                    return ((JLabel) obj).getText();
                                }
                            }
                            value = absValue;
                        }
                        return String.valueOf(value);
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    public void setPosition(final String position, final String operator,
            final String units) {
        getEventThreadQueuer().invokeAndWait(
                "setPosition", new IRunnable<Void>() { //$NON-NLS-1$
                    public Void run() {
                        if (!m_slider.isEnabled()) {
                            throw new StepExecutionException(
                                    "The slider is not enabled", EventFactory //$NON-NLS-1$
                                    .createActionError("The slider is not enabled")); //$NON-NLS-1$
                        }
                        Integer value = null;
                        Dictionary<Integer, ?> labelTable =
                                m_slider.getLabelTable();
                        
                        String prefPos = getPreferredPosition(position, units);
                        
                        String possiblePos = getPossiblePosition(prefPos);
                        
                        int min = m_slider.getMinimum();
                        int max = m_slider.getMaximum();
                        int incr = getSliderIncrement();
                        MatchUtil matcher = MatchUtil.getInstance();
                        for (int val = min; val <= max; val += incr) {
                            if (labelTable != null) {
                                Object label = labelTable.get(val);
                                if (label != null && label instanceof JLabel) {
                                    if (matcher.match(
                                            ((JLabel) label).getText(),
                                            possiblePos, operator)) {
                                        value = val;
                                        break;
                                    }
                                }
                            } 
                            if (matcher.match(String.valueOf(val), possiblePos,
                                    operator)) {
                                value = val;
                                break;
                            }
                        }
                        if (value == null) {
                            throw new StepExecutionException("Value not found", //$NON-NLS-1$
                                    EventFactory.createActionError(
                                            TestErrorEvent.NOT_FOUND));
                        }
                        m_slider.setValue(value);
                        return null;
                    }
                });
    }

    /**
     * @return the possible increment of the slider
     */
    private int getSliderIncrement() {
        int incr;
        if (m_slider.getSnapToTicks()) {
            int minorTickSpacing =
                    m_slider.getMinorTickSpacing();
            incr = minorTickSpacing != 0
                    ? minorTickSpacing
                    : m_slider.getMajorTickSpacing();
        } else {
            incr = 1;
        }
        return incr;
    }

    /**
     * @param prefPos the preferred position
     * @return the possible position of the slider which can be set
     * (due to snapping to ticks, ...)
     */
    private String getPossiblePosition(String prefPos) {
        try {
            Integer pos = Integer.valueOf(prefPos);
            final int valueToSet;
            if (m_slider.getSnapToTicks()) {
                int val = m_slider.getMinimum();
                int minorTickSpacing = m_slider.getMinorTickSpacing();
                int incr = minorTickSpacing > 0
                        ? minorTickSpacing
                        : m_slider.getMajorTickSpacing();
                while (val < pos) {
                    val += incr;
                }
                valueToSet = (val - pos) <= (pos - (val - incr))
                        ? val : val - incr;
            } else {
                valueToSet = pos;
            }
            return String.valueOf(valueToSet);
        } catch (NumberFormatException nfe) {
            return prefPos;
        }
    }
    
    /** 
     * @param position the given position
     * @param units "percent" or "value"
     * @return the calculated preferred position of the slider
     */
    private String getPreferredPosition(String position, String units) {
        String returnVal = position;
        
        if (ValueSets.Measure.percent.rcValue().equals(units)) {
            try {
                Double pos = Double.valueOf(position);
                if (pos < 0 || 100 < pos) {
                    throwInvalidInputMessage();
                }
                returnVal = String.valueOf(Math.round(
                        m_slider.getMinimum() + pos * 0.01
                        * (m_slider.getMaximum() - m_slider.getMinimum())));
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return returnVal;
    }
    
    /** throws invalid input message */
    private void throwInvalidInputMessage() {
        throw new StepExecutionException("Invalid input for slider", //$NON-NLS-1$
                EventFactory.createActionError(
                        TestErrorEvent.INVALID_INPUT));
    }

}
