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
package org.eclipse.jubula.client.ui.provider.labelprovider.decorators;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationContext;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.IDecorationContext;
import org.eclipse.jubula.client.core.model.ICommentPO;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.ui.constants.IconConstants;


/**
 * @author BREDEX GmbH
 * @created 21.10.2004
 */
public class ResultDecorator extends AbstractLightweightLabelDecorator {
    /** {@inheritDoc} */
    public void decorate(Object element, IDecoration decoration) {
        if (element instanceof TestResultNode) {
            TestResultNode resultNode = 
                ((TestResultNode)element);
            if (resultNode.getNode() instanceof ICommentPO) {
                return;
            }
            
            int status = resultNode.getStatus();
            ImageDescriptor image2use = null;
            IDecorationContext context = 
                decoration.getDecorationContext();
            if (context instanceof DecorationContext) {
                ((DecorationContext)context).putProperty(
                        IDecoration.ENABLE_REPLACE, Boolean.TRUE);
            }
            switch (status) {
                case TestResultNode.NOT_YET_TESTED:
                    break;
                case TestResultNode.NO_VERIFY:
                    image2use = IconConstants.STEP_OK_IMAGE_DESCRIPTOR;
                    break;
                case TestResultNode.TESTING:
                    image2use = IconConstants.STEP_TESTING_IMAGE_DESCRIPTOR;
                    break;
                case TestResultNode.SUCCESS:
                    image2use = IconConstants.STEP_OK_IMAGE_DESCRIPTOR;
                    break;
                case TestResultNode.ERROR:
                case TestResultNode.CONDITION_FAILED:
                case TestResultNode.INFINITE_LOOP:
                    image2use = IconConstants.STEP_NOT_OK_IMAGE_DESCRIPTOR;
                    break;
                case TestResultNode.ERROR_IN_CHILD:
                    image2use = IconConstants.STEP_NOT_OK_IMAGE_DESCRIPTOR;
                    break;
                case TestResultNode.NOT_TESTED:
                    image2use = IconConstants.STEP_FAILED_IMAGE_DESCRIPTOR;
                    break;
                case TestResultNode.RETRYING:
                    image2use = IconConstants.STEP_RETRY_IMAGE_DESCRIPTOR;
                    break;
                case TestResultNode.SUCCESS_RETRY:
                    image2use = IconConstants.STEP_RETRY_OK_IMAGE_DESCRIPTOR;
                    break;
                case TestResultNode.ABORT:
                    image2use = IconConstants.STEP_NOT_OK_IMAGE_DESCRIPTOR;
                    break;
                case TestResultNode.SKIPPED:
                    image2use = IconConstants.STEP_SKIPPED_IMAGE_DESCRIPTOR;
                    break;
                case TestResultNode.SUCCESS_ONLY_SKIPPED:
                    image2use =
                        IconConstants.STEP_SUCCESS_SKIPPED_IMAGE_DESCRIPTOR;
                    break;
                default:
                    break;
            }
            decoration.addOverlay(
                    image2use,
                    IDecoration.REPLACE);
        }
    }
}