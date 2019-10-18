/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.widgets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jubula.client.core.functions.FunctionDefinition;
import org.eclipse.jubula.client.core.functions.FunctionRegistry;
import org.eclipse.jubula.client.core.functions.ParameterDefinition;
import org.eclipse.jubula.client.core.functions.VarArgsDefinition;
import org.eclipse.jubula.client.core.gen.parser.parameter.lexer.LexerException;
import org.eclipse.jubula.client.core.gen.parser.parameter.node.TBeginFunctionArgsToken;
import org.eclipse.jubula.client.core.gen.parser.parameter.node.TEndFunctionArgsToken;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.parser.parameter.FunctionLocator;
import org.eclipse.jubula.client.ui.rcp.widgets.ParamProposalProvider.ParamProposal;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides Function proposals in parameter values based on parameter type and 
 * editing context.
 */
public class FunctionProposalProvider implements IContentProposalProvider {

    /** the logger */
    private static final Logger LOG = 
            LoggerFactory.getLogger(FunctionProposalProvider.class);

    /** separator for Function arguments in content proposals */
    private static final String ARG_SEPARATOR = ","; //$NON-NLS-1$
    
    /** the base name to use for vararg content proposals */
    private static final String BASE_VARARG_NAME = "varArg"; //$NON-NLS-1$
    
    /**
     * 
     * {@inheritDoc}
     */
    public IContentProposal[] getProposals(String contents, int position) {
        String proposalSubstring = contents.substring(0, position);
        try {
            FunctionLocator locator = new FunctionLocator(proposalSubstring);
            String startingFunctionText = locator.getCurrentFunction();
            if (startingFunctionText != null) {
                // if the user is currently entering a function, only 
                // function-related content proposals are interesting, so just 
                // return from here
                return getProposalsForFunction(startingFunctionText);
            }
        } catch (LexerException e) {
            LOG.warn(NLS.bind(Messages.ParamProposal_ParsingError, 
                    proposalSubstring), e);
        } catch (IOException e) {
            LOG.warn(NLS.bind(Messages.ParamProposal_ParsingError, 
                    proposalSubstring), e);
        }
        
        
        return new IContentProposal[0];
    }

    /**
     * 
     * @param startingFunctionText The text for which to generate content 
     *                             proposals.
     * @return the proposals for the given arguments.
     */
    private IContentProposal[] getProposalsForFunction(
            String startingFunctionText) {

        List<IContentProposal> proposals = new ArrayList<IContentProposal>();
        for (FunctionDefinition function 
                : FunctionRegistry.getInstance().getAllFunctions()) {
            
            if (function.getName().startsWith(startingFunctionText)) {
                StringBuilder displayBuilder = new StringBuilder();
                
                displayBuilder.append(function.getName())
                    .append(new TBeginFunctionArgsToken().getText());

                ParameterDefinition[] parameters = function.getParameters();
                List<String> parameterNames = new ArrayList<String>();
                for (ParameterDefinition param : parameters) {
                    parameterNames.add(param.getName());
                }

                VarArgsDefinition varArgs = function.getVarArgs();
                if (varArgs != null) {
                    for (int i = 0; i < varArgs.getDefaultNumberOfArgs(); i++) {
                        StringBuilder varArgNameBuilder = 
                                new StringBuilder(BASE_VARARG_NAME);
                        varArgNameBuilder.append(i + 1);
                        parameterNames.add(varArgNameBuilder.toString());
                    }
                }
                
                displayBuilder.append(StringUtils.join(
                        parameterNames, ARG_SEPARATOR));
                
                displayBuilder.append(new TEndFunctionArgsToken().getText());
                String displayString = displayBuilder.toString();
                proposals.add(new ParamProposal(
                        displayString.substring(
                                startingFunctionText.length()),
                        displayString));
            }

        }

        return proposals.toArray(new IContentProposal[proposals.size()]);
    }

}
