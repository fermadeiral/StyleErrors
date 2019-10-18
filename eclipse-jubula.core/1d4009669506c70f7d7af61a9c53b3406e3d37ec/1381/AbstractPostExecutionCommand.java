/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.rc.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.client.core.businessprocess.ExternalTestDataBP;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.ITDManager;
import org.eclipse.jubula.client.core.utils.ExecObject;
import org.eclipse.jubula.client.core.utils.ModelParamValueConverter;
import org.eclipse.jubula.client.core.utils.ParamValueConverter;
import org.eclipse.jubula.client.core.utils.Traverser;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;

/**
 * abstract class for shared methods
 *
 * @author BREDEX GmbH
 * @created 19.08.2009
 */
public abstract class AbstractPostExecutionCommand 
    implements IPostExecutionCommand {
    /** the traverser */
    private Traverser m_traverser;
    /** the current CAP */
    private ICapPO m_currentCap;
    /** the external test data BP */
    private ExternalTestDataBP m_externalTestDataBP;

    /** Constructor */
    public AbstractPostExecutionCommand() {
    // empty
    }
    
    /**
     * Method to find the value for a parameter
     * 
     * @param modelValue
     *            test data object
     * @param cap
     *            the corresponding cap
     * @param desc
     *            param description belonging to current test data object
     * @return the value
     * @throws InvalidDataException
     *             in case of missing value for a parameter in a cap
     */
    protected String getValueForParam(String modelValue, ICapPO cap, 
        IParamDescriptionPO desc) 
        throws InvalidDataException {
        String value = StringConstants.EMPTY;
        ParamValueConverter conv = new ModelParamValueConverter(
            modelValue, cap, desc);
        try {
            List <ExecObject> stackList = 
                new ArrayList<ExecObject>(getTraverser().getExecStackAsList());
            value = conv.getExecutionString(stackList);
        } catch (InvalidDataException e) {
            throw new InvalidDataException(
                Messages.NeitherValueNorReferenceForNode
                + StringConstants.COLON + StringConstants.SPACE
                + cap.getName(), MessageIDs.E_NO_REFERENCE); 
        }
        return value;
    }
    
    /**
     * @param paramID
     *            the parameter id
     * @return the value of the current paramID parameter
     */
    protected String getValueForParam(String paramID) throws JBException {
        ICapPO currentCap = getCurrentCap();
        IParamDescriptionPO desc = currentCap.getParameterForUniqueId(paramID);
        ITDManager tdManager = getExternalTestDataBP()
                .getExternalCheckedTDManager(currentCap);
        String modelValue = tdManager.getCell(0, desc);
        return this.getValueForParam(modelValue, currentCap, desc);
    }

    /**
     * @return the traverser
     */
    protected Traverser getTraverser() {
        return m_traverser;
    }

    /**
     * @param traverser the traverser to set
     */
    public void setTraverser(Traverser traverser) {
        m_traverser = traverser;
    }

    /**
     * @return the currentCap
     */
    protected ICapPO getCurrentCap() {
        return m_currentCap;
    }

    /**
     * @param currentCap the currentCap to set
     */
    public void setCurrentCap(ICapPO currentCap) {
        m_currentCap = currentCap;
    }

    /**
     * @return the externalTestDataBP
     */
    protected ExternalTestDataBP getExternalTestDataBP() {
        return m_externalTestDataBP;
    }

    /**
     * @param externalTestDataBP
     *            the externalTestDataBP to set
     */
    public void setExternalTestDataBP(
        ExternalTestDataBP externalTestDataBP) {
        m_externalTestDataBP = externalTestDataBP;
    }
}