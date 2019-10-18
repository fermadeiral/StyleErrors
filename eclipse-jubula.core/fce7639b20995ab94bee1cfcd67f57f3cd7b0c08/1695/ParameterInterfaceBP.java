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
package org.eclipse.jubula.client.core.businessprocess;

import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.core.utils.GuiParamValueConverter;


/**
 * @author BREDEX GmbH
 * @created Jul 13, 2010
 */
public class ParameterInterfaceBP 
    extends AbstractParamInterfaceBP<ITestDataCubePO> {

    /** {@inheritDoc} */
    public void removeParameter(IParamDescriptionPO desc, 
            ITestDataCubePO tdc) {
        tdc.removeParameter(desc.getUniqueId());
    }

    /** {@inheritDoc} */
    protected void updateParam(GuiParamValueConverter conv,
            IParamNameMapper mapper, int row) {
        writeTestDataEntry(conv, row);
    }

    /** {@inheritDoc} */
    public void changeUsageParameter(ITestDataCubePO paramIntObj,
            IParamDescriptionPO desc, String guid,
            ParamNameBPDecorator mapper) {
        // currently not implemented
    }

}
