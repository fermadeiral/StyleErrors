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
package org.eclipse.jubula.client.ui.rcp.provider.labelprovider;

import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.swt.graphics.Image;



/**
 * @author BREDEX GmbH
 * @created 04.04.2011
 */
public class TestCaseBrowserLabelProvider extends TooltipLabelProvider {

    @Override
    public String getText(Object element) {
        if (element instanceof IProjectPO) {
            return Messages.TreeBuilderTestCases;
        }
        return super.getText(element);
    }
    
    @Override
    public Image getImage(Object element) {
        if (element instanceof INodePO
                && ((INodePO) element).isSpecObjCont()) {
            return IconConstants.CATEGORY_IMAGE;
        }
        return super.getImage(element);
    }
}