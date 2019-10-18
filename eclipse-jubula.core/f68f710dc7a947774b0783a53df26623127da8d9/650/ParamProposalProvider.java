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
package org.eclipse.jubula.client.ui.rcp.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITDManager;
import org.eclipse.jubula.client.core.utils.IParamValueToken;
import org.eclipse.jubula.client.core.utils.LiteralToken;
import org.eclipse.jubula.client.core.utils.SimpleStringConverter;
import org.eclipse.jubula.client.core.utils.SimpleValueToken;
import org.eclipse.jubula.client.core.utils.VariableToken;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.TestDataConstants;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;


/**
 * Provides proposals for parameter values based on parameter type and 
 * editing context.
 *
 * @author BREDEX GmbH
 * @created Apr 7, 2010
 */
public class ParamProposalProvider implements IContentProposalProvider {
    
    /**
     * @author BREDEX GmbH
     * @created Feb 5, 2009
     */
    public static class ParamProposal implements IContentProposal {

        /** display value */
        private String m_displayValue;

        /** the content that will be inserted if this proposal is selected */
        private String m_content;
        /** description */
        private String m_description;
        /**
         * Constructor
         * 
         * @param content The content of the proposal.
         */
        public ParamProposal(String content) {
            this(content, null);
        }

        /**
         * Constructor
         * 
         * @param content The content of the proposal.
         * @param displayValue The label for the proposal.
         */
        public ParamProposal(String content, String displayValue) {
            m_content = content;
            m_displayValue = displayValue;
        }
        
        /**
         * Constructor
         * 
         * @param content The content of the proposal.
         * @param displayValue The label for the proposal.
         * @param description the description text.
         */
        public ParamProposal(String content, String displayValue,
                String description) {
            this(content, displayValue);
            m_description = description;
        }
        
        /**
         * {@inheritDoc}
         */
        public String getContent() {
            return m_content;
        }

        /**
         * {@inheritDoc}
         */
        public int getCursorPosition() {
            return m_content.length();
        }

        /**
         * {@inheritDoc}
         */
        public String getDescription() {
            return StringUtils.defaultIfBlank(m_description, null);
        }

        /**
         * {@inheritDoc}
         */
        public String getLabel() {
            return m_displayValue;
        }

    }

    /** a map with values as key and comment as value */
    private Map<String, String> m_valueToComment;
    /** the node for which to provide proposals */
    private INodePO m_node;
    
    /** the param description for which to provide proposals */
    private IParamDescriptionPO m_paramDesc;

    /** delegate provider for Function proposals */
    private FunctionProposalProvider m_functionProposalProvider =
            new FunctionProposalProvider();
    
    /**
     * Constructor
     * 
     * @param valueSet Fixed values to propose (for example, "true" and "false"
     *                 for a boolean parameter).
     * @param node The node to use as a base for dynamically generating 
     *             proposals.
     * @param paramDesc The param description to use as a base for dynamically 
     *                  generating proposals.
     */
    public ParamProposalProvider(String[] valueSet, INodePO node, 
            IParamDescriptionPO paramDesc) {
        for (String string : valueSet) {
            m_valueToComment.put(string, StringConstants.EMPTY);
        }
        m_node = node;
        m_paramDesc = paramDesc;
    }
    
    /**
     * Constructor
     * 
     * @param valueSet Fixed values to propose (for example, "true" and "false"
     *                 for a boolean parameter).
     * @param node The node to use as a base for dynamically generating 
     *             proposals.
     * @param paramDesc The param description to use as a base for dynamically 
     *                  generating proposals.
     */
    public ParamProposalProvider(Map<String, String> valueSet, INodePO node, 
            IParamDescriptionPO paramDesc) {
        m_valueToComment = valueSet;
        m_node = node;
        m_paramDesc = paramDesc;
    }
    /**
     * {@inheritDoc}
     */
    public IContentProposal[] getProposals(String contents, int position) {

        IContentProposal[] functionProposals = 
                m_functionProposalProvider.getProposals(contents, position);
        if (!ArrayUtils.isEmpty(functionProposals)) {
            return functionProposals;
        }
        
        if (position != contents.length()) { // no proposals when in text
            return new IContentProposal[0];
        }

        List<IContentProposal> proposals = 
            new ArrayList<IContentProposal>(20);

        
        
        // if there are predefined values offer them first
        if (m_valueToComment != null) {
            proposals.addAll(getValueSetProposals(contents, position));
        }

        proposals.addAll(getParentParamProposals(contents));
        proposals.addAll(getParentVariableProposals(contents,
                TestDataConstants.VARIABLE.equals(m_paramDesc.getType())));
        
        return proposals.toArray(new IContentProposal[proposals.size()]);
    }
 
    /**
     * Creates and returns content proposals based on the Parameters available
     * from the parent node.
     * 
     * @param contents The text for which to generate content proposals.
     * @return the proposals for the given arguments.
     */
    private Collection<IContentProposal> getParentParamProposals(
            String contents) {

        List<IContentProposal> proposals = new ArrayList<IContentProposal>();
        
        // find a SpecTestCase
        INodePO node = m_node;
        while ((node != null) && !(node instanceof ISpecTestCasePO)) {
            node = node.getParentNode();
        }
        if (node == null || contents == null) {
            return proposals;
        }
        ISpecTestCasePO paramNode = (ISpecTestCasePO)node;
        
        if (!(m_node instanceof ISpecTestCasePO)) {
            // add the parameter name as a suggestion
            if (m_paramDesc != null && paramNode != null) {
                if (!paramNode.isInterfaceLocked()
                        && !paramNode.getParamNames().contains(
                                m_paramDesc.getName())) {
                    String p = SimpleValueToken.PARAMETER_START
                            + getPredefinedParamName();
                    if (p.startsWith(contents)) {
                        proposals.add(new ParamProposal(
                                p.substring(contents.length()), p));
                    }
                }
            }

            if (paramNode != null) {
                List<IParamDescriptionPO> params = 
                    paramNode.getParameterList();
                for (IParamDescriptionPO param : params) {
                    String parType = m_paramDesc.getType();
                    if ("java.lang.String".equals(parType) //$NON-NLS-1$
                            || param.getType().equals(parType)) {
                        String p = SimpleValueToken.PARAMETER_START
                                + param.getName();
                        if (p.startsWith(contents)) {
                            proposals.add(new ParamProposal(
                                    p.substring(contents.length()), p));
                        }
                    }
                }

            }
        }
        return proposals;
    }

    /**
     * @return the auto-generated param name
     */
    private String getPredefinedParamName() {
        String p = m_paramDesc.getName();
        p = StringUtils.replaceChars(p, ' ', '_');
        p = StringUtils.replaceChars(p, '-', '_');
        p = p.toUpperCase();
        return p;
    }

    /**
     * Gets proposals for variables
     * @param content the current content of the text field
     * @param varTypeParam Whether the parameter's type is 'Variable'.
     *     These are special, because can contain only the name of
     *     the variables, nothing else.
     * @return the collection of proposals
     */
    private Collection<IContentProposal> getParentVariableProposals(
            String content, boolean varTypeParam) {
        List<IContentProposal> props = new ArrayList<>();
        if (m_node == null) {
            return props;
        }
        INodePO spec = m_node.getSpecAncestor();
        if (spec == null || content == null) {
            return props;
        }
        // we determine the position where the variable name starts 
        // after the last variable start symbol
        // possible cases: '', '$', '$a', '${', '${a' if the parameter is normal
        // for 'Variable' type parameters, '$' should not appear anywhere
        int varNameStart = content.lastIndexOf(SimpleValueToken.VARIABLE_START);
        if ((varNameStart == -1 && !varTypeParam && content.length() > 0)
                || (varNameStart != -1 && varTypeParam)) {
            return props;
        }
        String proposalStart = (varNameStart == -1 && !varTypeParam)
                ? String.valueOf(SimpleValueToken.VARIABLE_START)
                        : StringUtils.EMPTY;
        varNameStart++;
        boolean needClose = false;
        if (varNameStart < content.length() && !varTypeParam) {
            if (content.charAt(varNameStart)
                    == SimpleValueToken.DELIMITER_START) {
                varNameStart++;
                needClose = true;
            }
        } else {
            if (!varTypeParam) {
                proposalStart += SimpleValueToken.DELIMITER_START;
                needClose = true;
            }
        }
        // we finally have the variable name part...
        String varNamePart = varNameStart < content.length()
                ? content.substring(varNameStart) : StringUtils.EMPTY;
        Set<String> varNames = collectVariableNames(spec);
        for (String varName : varNames) {
            if (!(varName.startsWith(varNamePart))) {
                continue;
            }
            String proposal = proposalStart;
            if (!varName.equals(varNamePart)) {
                proposal += varName.substring(varNamePart.length());
            }
            if (needClose) {
                proposal += SimpleValueToken.DELIMITER_END;
            }
            props.add(new ParamProposal(proposal, content + proposal));
        }
        return props;
    }

    /**
     * Collects all variable names appearing within the edited node
     * @param spec the edited node
     * @return the variable names
     */
    private Set<String> collectVariableNames(INodePO spec) {
        Set<String> varNames = new HashSet<>();
        for (Iterator<INodePO> it = spec.getAllNodeIter(); it.hasNext(); ) {
            INodePO next = it.next();
            if (!(next instanceof IParamNodePO)) {
                continue;
            }
            IParamNodePO paramNode = (IParamNodePO) next;
            ITDManager man = paramNode.getDataManager();
            // More convenient, but slower way to iterate
            // (we need the ParamDescPO's type)
            for (IParamDescriptionPO param : paramNode.getParameterList()) {
                for (int i = 0; i < man.getDataSetCount(); i++) {
                    String data = null;
                    try {
                        data = man.getCell(i, param);
                    } catch (IndexOutOfBoundsException ioobe) {
                        // ignore we will continue with other data
                    }
                    if (data == null) {
                        continue;
                    }
                    SimpleStringConverter conv =
                            new SimpleStringConverter(data);
                    if (TestDataConstants.VARIABLE.equals(param.getType())) {
                        List<IParamValueToken> toks = conv.getTokens();
                        // we ignore more complicated cases, e.g. 'VAR''IA'BLE...
                        if (toks.size() != 1) {
                            continue;
                        }
                        IParamValueToken token = toks.get(0);
                        if (token instanceof LiteralToken
                                || token instanceof SimpleValueToken) {
                            try {
                                varNames.add(token.getExecutionString(null));
                            } catch (InvalidDataException e) {
                                // Improbable, but if happens, we just ignore it
                            }
                        }
                        continue;
                    }
                    for (IParamValueToken token : conv.getTokens()) {
                        if (!(token instanceof VariableToken)) {
                            continue;
                        }
                        String varName = ((VariableToken) token).getGuiString();
                        if (varName.length() > 1 && varName.charAt(1)
                                == SimpleValueToken.DELIMITER_START) {
                            varNames.add(varName.substring(2,
                                    varName.length() - 1));
                        } else {
                            varNames.add(varName.substring(1));
                        }
                    }
                }
            }
        }
        return varNames;
    }

    /**
     * 
     * @param contents The text for which to generate content proposals.
     * @param position The current position in the text for which to generate 
     *                 proposals.
     * @return the proposals for the given arguments.
     */
    private Collection<IContentProposal> getValueSetProposals(
            String contents, int position) {

        List<IContentProposal> proposals = new ArrayList<IContentProposal>();
        StringBuilder sb = new StringBuilder(contents);
        
        sb.delete(position, sb.length());
        sb.delete(0, 
            sb.lastIndexOf(TestDataConstants.COMBI_VALUE_SEPARATOR) + 1);
        for (Entry<String, String> entry: m_valueToComment.entrySet()) {
            String predefValue = entry.getKey();
            String comment = entry.getValue();
            if (predefValue.startsWith(sb.toString())) {
                proposals.add(new ParamProposal(
                        predefValue.substring(sb.length()), predefValue,
                        comment));
            } else if (predefValue.startsWith(contents)) {
                proposals.add(new ParamProposal(
                        predefValue.substring(
                                contents.length()), predefValue,
                        comment));
            }
        }

        return proposals;
    }

}
