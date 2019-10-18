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

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.ui.constants.IconConstants;

/**
 * @author BREDEX GmbH
 * @created Oct 27, 2011
 */
public class TestResultParametersDecorator extends
        AbstractLightweightLabelDecorator {
    /** {@inheritDoc} */
    public void decorate(Object element, IDecoration decoration) {
        if (element instanceof TestResultNode) {
            TestResultNode testResult = (TestResultNode) element;
            decoration.addSuffix(testResult.getParameterDescription());
            if (StringUtils.isNotBlank(testResult.getCommandLog())) {
                decoration.addOverlay(
                        IconConstants.COMMANDLOG_IMAGE_DESCRIPTOR);
            }
        }
    }
}
