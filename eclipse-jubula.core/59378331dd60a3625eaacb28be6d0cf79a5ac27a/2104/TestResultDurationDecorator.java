/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.provider.labelprovider.decorators;

import java.util.Date;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jubula.client.core.model.ICommentPO;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.ui.editors.TestResultViewer;

/**
 * @author BREDEX GmbH
 * @created Sep 19, 2011
 */
public class TestResultDurationDecorator 
        extends AbstractLightweightLabelDecorator {

    /** 
     * the first part of the suffix. used to visually separate the label 
     * from the suffix 
     */
    private static final String SUFFIX_SPACER = " - "; //$NON-NLS-1$
    
    /** {@inheritDoc} */
    public void decorate(Object element, IDecoration decoration) {
        if (element instanceof TestResultNode) {
            TestResultNode testResult = (TestResultNode)element;
            if (testResult.getNode() instanceof ICommentPO) {
                return;
            }
            
            Object testSuiteEndTimeValue = 
                decoration.getDecorationContext().getProperty(
                    TestResultViewer.DECORATION_CONTEXT_SUITE_END_TIME_ID);
            Date endTime = testSuiteEndTimeValue instanceof Date 
                ? (Date)testSuiteEndTimeValue : null;

            long durationMillis = testResult.getDuration(endTime);
            if (durationMillis != -1) {
                // only decorate if the duration could be determined
                decoration.addSuffix(SUFFIX_SPACER 
                        + DurationFormatUtils.formatDurationHMS(
                                durationMillis));
            }
        }
    }
}