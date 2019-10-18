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
package org.eclipse.jubula.client.core.utils;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.jubula.client.core.businessprocess.TestCaseParamBP;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.utils.ParamValueConverter.ConvValidationState;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class to convert and validate a reference token. The conversion is carried
 * out between GUI and model representation and vice versa. The validation
 * refers to semantical correctness of the GUI representation of reference
 * string.
 * 
 * @author BREDEX GmbH
 * @created 14.08.2007
 */
public class RefToken extends AbstractParamValueToken {
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(RefToken.class);
    
    /** prefix for a reference */
    private static final String PREFIX = "={"; //$NON-NLS-1$
    
    /**
     * <code>m_guiString</code> string represents the token in the GUI
     */
    private String m_guiString = null;
    
    /**
     * <code>m_modelString</code>string represents the token in the model<br>
     * e.g. <b>"=REF"</b> or <b>"={REF}"</b>  --> GUI representation <br>
     *      <b>"=GUID"</b> or <b>={GUID}</b> --> model representation
     */
    private String m_modelString = null; 

    /** flag for differentiation between token creation based on string in GUI- or
     * model representation
     */
    private boolean m_isTokenGuiBased;

    /** node holding this reference - can be null, but then the RefToken is invalid */
    private IParameterInterfacePO m_currentNode;
    
    /**
     * use this constructor only for references coming from GUI
     * @param string represents the token
     * @param isGuiString flag for differentiation between GUI- and model representation of string
     * @param startPos index of first character of token in entire string
     * @param node holding this reference
     * @param desc param description belonging to this reference
     */
    public RefToken(String string, boolean isGuiString, int startPos,
            IParameterInterfacePO node, IParamDescriptionPO desc) {

        super(string, startPos, desc);
        if (!isValid(string, isGuiString)) {
            throw new IllegalArgumentException(NLS.bind(
                    Messages.SyntaxErrorInReference, string));
        }
        m_isTokenGuiBased = isGuiString;
        if (isGuiString) {
            m_guiString = string;
        } else {
            m_modelString = string;
        }
        m_currentNode = node;
    }
    
    /**
     * creates the model representation from the GUI representation
     * @return modelString
     */
    public String getModelString() {
        if (m_modelString == null && m_guiString != null) {
            String uuid = computeUUID();
            if (uuid != null) {
                m_modelString = replaceCore(computeUUID(), m_guiString);
            }
        }
        return m_modelString;
    }

    /**
     * hint: returned UUID is null if reference is new and the associated parameter 
     * in parent node is not yet created
     * @return GUID belonging to this reference
     */
    private String computeUUID() {
        String uuid = StringConstants.EMPTY;
        if (m_modelString != null) {
            uuid = extractCore(m_modelString);
        } else if (m_guiString != null) {
            if (m_currentNode instanceof INodePO) {
                INodePO parent = NodePM.getSpecTestCaseParent(
                        (INodePO)m_currentNode);
                String refName = extractCore(m_guiString);
                if (parent instanceof IParamNodePO) {
                    IParamNodePO parentNode = (IParamNodePO)parent;
                    IParamDescriptionPO desc = 
                        parentNode.getParameterForName(refName);
                    if (desc != null) {
                        uuid = desc.getUniqueId();
                    } else {
                        return null;
                    }
                } else {
                    StringBuilder msg = new StringBuilder();
                    msg.append(Messages.Node);
                    msg.append(StringConstants.SPACE);
                    msg.append(m_currentNode.getName());
                    msg.append(StringConstants.SPACE);
                    msg.append(Messages.WithReferenceIsNotChildOfParamNode);
                    Assert.notReached(msg.toString());
                }
            }
        }
        return uuid;
    }


    /**
     * @param repl replacement (UUID or reference name)
     * @param str base string
     * @return replaced string
     */
    public static String replaceCore(String repl, String str) {
        int start = -1;
        int end = -1;
        StringBuilder builder = new StringBuilder(str);
        if (str.startsWith(PREFIX)) {
            start = 2;
            end = str.length() - 1;
        } else {
            start = 1;
            end = str.length();
        }
        if (start < end) {
            builder.replace(start, end, repl);
            return builder.toString();
        }
        Assert.notReached(Messages.UnexpectedProblemWithStringReplacement);
        return str;
    }

    /**
     * @param s string for syntax validation
     * @param isGuiString flag to distinct GUI- and modelStrings
     * @return if the syntax of guiString is correct
     */
    private boolean isValid(String s, boolean isGuiString) {
        if (!isGuiString) {
            String string = extractCore(s);
            final String wordRegex = "[0-9a-fA-F]{32}"; //$NON-NLS-1$
            return (Pattern.matches(wordRegex, string));
        }
        return true;
    }
    
    /**
     * @param s string in GUI- or model representation
     * @return reference name respectively UUID-portion of entire string
     */
    public static String extractCore(String s) {
        StringBuilder builder = new StringBuilder(s);
        if (s != null && s.length() != 0) {
            if (s.startsWith("={") && s.endsWith("}")) { //$NON-NLS-1$ //$NON-NLS-2$
                builder.delete(0, 2);
                builder.deleteCharAt(builder.length() - 1);
            } else if (s.startsWith("=")) { //$NON-NLS-1$
                builder.deleteCharAt(0);
            }
        }
        return builder.toString();

    }



    /**
     * validates, if the reference name and the associated type is allowed and
     * the interface may be modified
     * {@inheritDoc}
     * @see IParamValueToken#validate(INodePO)
     */
    public ConvValidationState validate() {
        if (m_currentNode == null) {
            setErrorKey(MessageIDs.E_NO_REF_ALLOWED);
            return ConvValidationState.invalid;
        }
        ConvValidationState state = ConvValidationState.notSet;
        if (m_currentNode instanceof ISpecTestCasePO) {
            setErrorKey(MessageIDs.E_NO_REF_FOR_SPEC_TC);
            return ConvValidationState.invalid;
        } else if (m_currentNode instanceof INodePO 
                && ((INodePO)m_currentNode).getSpecAncestor() 
                    instanceof ITestSuitePO) {
            setErrorKey(MessageIDs.E_REF_IN_TS);
            return ConvValidationState.invalid;
        } else if (m_currentNode instanceof ITestDataCubePO) {
            setErrorKey(MessageIDs.E_REF_IN_TDC);
            return ConvValidationState.invalid;
        }
        final boolean isModifiable = TestCaseParamBP.isReferenceValueAllowed(
                m_currentNode);
        if (m_isTokenGuiBased) {
            INodePO parent = NodePM.getSpecTestCaseParent(
                    (INodePO)m_currentNode);
            String refName = extractCore(m_guiString);
            if (parent instanceof ISpecTestCasePO) {
                ISpecTestCasePO specTc = (ISpecTestCasePO)parent;
                List<IParamDescriptionPO> descs = specTc.getParameterList();
                Map<String, IParamDescriptionPO> paramNames = 
                    new HashMap<String, IParamDescriptionPO>();
                for (IParamDescriptionPO desc : descs) {
                    paramNames.put(desc.getName(), desc);
                }
                if ((paramNames.keySet()).contains(refName)) {
                    IParamDescriptionPO desc = paramNames.get(refName);
                    String pType = getParamDescription().getType();
                    // We allow conversion from any type to String
                    if ("java.lang.String".equals(pType)  //$NON-NLS-1$
                            || desc.getType().equals(pType)) {
                        state = ConvValidationState.valid;
                    } else {
                        state = ConvValidationState.invalid;
                        setErrorKey(MessageIDs.E_INVALID_REF_TYPE);
                    }
                } else {
                    if (isModifiable) {
                        state = ConvValidationState.valid;
                    } else {
                        state = ConvValidationState.invalid;
                        setErrorKey(MessageIDs.E_INVALID_REF);
                        for (String paramName : paramNames.keySet()) {
                            if (paramName.startsWith(refName)) {
                                IParamDescriptionPO desc = 
                                    paramNames.get(paramName);
                                if (desc.getType().equals(
                                        getParamDescription().getType())) {
                                    state = ConvValidationState.undecided;
                                    break;
                                }
                            }
                        }
                    }
                }

            } else {
                throw new UnsupportedOperationException(
                    Messages.NotAllowedToAddReferenceToNodeASpecTestCase);
            }
        } else {
            // assumption, that semantic of modelString is correct
            state = ConvValidationState.valid;
        } 
        return state;
    }

    /**
     * gets the real value for a reference
     * @param stack current execution stack
     * @return the real value for this reference token and given dataset number
     * @throws InvalidDataException if given reference is not resolvable
     */
    public String getExecutionString(List<ExecObject> stack) 
        throws InvalidDataException {
        String refGuid = extractCore(getModelString());
        ListIterator <ExecObject> it = stack.listIterator(stack.size());
        while (it.hasPrevious()) {
            ExecObject obj = it.previous();
            String parameterValue = obj.getParameterValue(refGuid);
            if (parameterValue != null) {
                return parameterValue;
            }
        }
        throwInvalidDataException(extractCore(getGuiString()));
        return null;
    }
    
    /**
     * throws an exception, if neither a value or a further reference is
     * available for given reference
     * 
     * @param reference
     *            reference, which isn't resolvable
     * @throws InvalidDataException
     *             in case of missing testdata for given reference
     */
    private void throwInvalidDataException(String reference) 
        throws InvalidDataException {
        throw new InvalidDataException(Messages.Reference + reference 
            + StringConstants.SPACE + Messages.NotResolvable, 
            MessageIDs.E_NO_REFERENCE);    
    }
    
    /**
     * {@inheritDoc}
     * @see org.eclipse.jubula.client.core.utils.IParamValueToken#getValue()
     */
    public String getGuiString() {
        if (m_modelString != null && m_guiString == null) {
            m_guiString = replaceCore(computeReferenceName(), m_modelString);
        }
        return m_guiString;
    }


    /**
     * compute reference name based on GUI- or model string
     * @return reference name
     */
    private String computeReferenceName() {
        String refName = StringConstants.EMPTY;
        if (m_guiString != null) {
            refName = extractCore(m_guiString);
        } else if (m_modelString != null) {
            String uuid = extractCore(m_modelString);
            INodePO parent = NodePM.getSpecTestCaseParent(
                    (INodePO)m_currentNode);
            if (parent instanceof IParamNodePO) {
                IParamNodePO parentNode = (IParamNodePO) parent;
                IParamDescriptionPO desc = parentNode
                        .getParameterForUniqueId(uuid);
                if (desc != null) {
                    refName = desc.getName();
                } else {
                    String id = (uuid != null) ? uuid : StringConstants.EMPTY;
                    refName = id;
                    log.error(NLS.bind(Messages.InvalidUuidInReference, id));
                }
            } else {
                Assert.notReached(
                    Messages.NodeWithReferenceIsNotChildOfParamNode);
            }
        }
        return refName;
    }
        
    /**
     * @param modelString The modelString to set.
     */
    void setModelString(String modelString) {
        m_modelString = modelString;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        RefToken otherObj = (RefToken)obj;

        return new EqualsBuilder()
            .append(getModelString(), otherObj.getModelString())
            .isEquals();
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public int hashCode() {
        return new HashCodeBuilder().append(getModelString()).toHashCode();
    }
}
