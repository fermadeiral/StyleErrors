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

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jubula.client.core.gen.parser.parameter.lexer.LexerException;
import org.eclipse.jubula.client.core.gen.parser.parameter.node.EOF;
import org.eclipse.jubula.client.core.gen.parser.parameter.parser.Parser;
import org.eclipse.jubula.client.core.gen.parser.parameter.parser.ParserException;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.parser.parameter.JubulaParameterLexer;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * @author BREDEX GmbH
 * @created 16.08.2007
 */
public abstract class ParamValueConverter {

    /** the logger */
    private static final Logger LOG = LoggerFactory.getLogger(
            ParamValueConverter.class);
    
    /** 
     * All error codes that should be indicated to the user as "recoverable".
     * Essentially, this means that the parameter text is not currently valid,
     * but it may be made valid by appending the correct characters.
     * This is generally marked in the Jubula UI by highlighting the text field
     * in yellow.
     */
    private static final Set<Integer> RECOVERABLE_PARSE_ERROR_CODES =
        new HashSet<Integer>();
    
    static {
        RECOVERABLE_PARSE_ERROR_CODES.add(MessageIDs.E_ONE_CHAR_PARSE_ERROR);
        RECOVERABLE_PARSE_ERROR_CODES.add(MessageIDs.E_MISSING_CLOSING_BRACE);

    }
    
    /** string in gui representation */
    private String m_guiString = null;

    /**
     * <code>m_modelString</code> string in model representation
     */
    private String m_modelString = null;

    /**
     * list of tokens for current string
     * 
     */
    private List<IParamValueToken> m_tokens = 
        new ArrayList<IParamValueToken>();

    /**
     * <code>m_errors</code>errors, detected in tokens
     */
    private List<TokenError> m_errors = new ArrayList<TokenError>(1);

    /**
     * <code>m_currentNode</code>node contains the parameter with this parameter value - can be null for global context
     */
    private IParameterInterfacePO m_currentNode = null;

    /** param description associated with current gui- or model string */
    private IParamDescriptionPO m_desc = null;
    
    /** validator for special validations */
    private IParamValueValidator m_validator;
    
    /**
     * describes the state of a single token
     */
    public enum ConvValidationState {
        /**token has syntax or semantical errors */ 
        invalid,
        /** unknown state */
        notSet,
        /** currently invalid, but could be valid later */
        undecided,
        /** token is free of syntax or semantical errors */
        valid
    }

    
    /**
     * @param currentNode node with parameter for this parameterValue - can be null for global context
     * @param desc param description associated with current string (parameter value)
     * @param validator to use for special validations
     */
    public ParamValueConverter(IParameterInterfacePO currentNode,
        IParamDescriptionPO desc, IParamValueValidator validator)  {
        if (!isGUI()) {
            Validate.notNull(currentNode, 
                    Messages.NodeForGivenParameterValueMustNotBeNull);
        }
        m_currentNode = currentNode;
        m_desc = desc;
        m_validator = validator;
    }
    
    /**
     * default constructor
     */
    protected ParamValueConverter() {
        // do nothing
    }
    
    /**
     * @return list of reference names containing in s
     */
    public List<String> getNamesForReferences() {
        List<String> paramNames = new ArrayList<String>();
        for (IParamValueToken token : getAllTokens()) {
            if (token instanceof RefToken) {
                RefToken refToken = (RefToken)token;
                paramNames.add(RefToken.extractCore(refToken.getGuiString()));
            }
        }
        return paramNames;
    }
    
    /**
     * @return list of variables contained in current string
     */
    public List<String> getVariables() {
        List<String> variables = new ArrayList<String>();
        for (IParamValueToken token : getAllTokens()) {
            if (token instanceof VariableToken) {
                variables.add(token.getGuiString());
            }
        }
        return variables;
    }
    
    
    /**
     * @return true, if string contains at least one reference
     */
    public boolean containsReferences() {
        for (IParamValueToken token : getAllTokens()) {
            if (token instanceof RefToken) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @return true, if string contains only simple values
     */
    public boolean containsOnlySimpleValues() {
        for (IParamValueToken token : getTokens()) {
            if (!(token instanceof SimpleValueToken)) {
                return false;
            }
        }
        return true;
    }
   
    /**
     * @param stack current execution stack
     * @return string for testexecution
     * @throws InvalidDataException in case of any problem to resolve the token
     */
    public String getExecutionString(List<ExecObject> stack) 
            throws InvalidDataException {
        
        StringBuilder builder = new StringBuilder();
        for (IParamValueToken token : getTokens()) {
            builder.append(token.getExecutionString(
                    new ArrayList<ExecObject>(stack)));
        }
        return builder.toString();
    }

    /**
     * parses the provided string and separates it in single tokens
     */
    void createTokens() {
        String toParse = isGUI() ? getGuiString() : getModelString();
        Parser parser = new Parser(new JubulaParameterLexer(new PushbackReader(
                new StringReader(StringUtils.defaultString(toParse)))));
        ParsedParameter parsedParam = new ParsedParameter(isGUI(),
                getCurrentNode(), getDesc());
        try {
            parser.parse().apply(parsedParam);
            List<IParamValueToken> liste = parsedParam.getTokens();
            setTokens(liste);
        } catch (LexerException e) {
            createErrors(e, getGuiString());
        } catch (ParserException e) {
            createErrors(e, getGuiString());
        } catch (IOException e) {
            LOG.error(Messages.ParameterParsingErrorOccurred, e);
            createErrors(e, getGuiString());
        } catch (SemanticParsingException e) {
            createErrors(e, getGuiString());
        }
    }
    
    /** calls the validation for each token */
    abstract void validateSingleTokens();

    /**
     * @return Returns the tokens.
     */
    public List<IParamValueToken> getTokens() {
        return m_tokens;
    }
    
    /**
     * 
     * @return all tokens contained in this converter (including nested tokens).
     */
    protected List<IParamValueToken> getAllTokens() {
        List<IParamValueToken> tokens = 
                new ArrayList<IParamValueToken>(getTokens());
        for (IParamValueToken token : getTokens()) {
            if (token instanceof INestableParamValueToken) {
                addAllSubTokens((INestableParamValueToken)token, tokens);
            }
        }
        
        return tokens;
    }

    /**
     * Recursive method for finding all (recursively) nested tokens.
     * 
     * @param token The token from which to acquire nested tokens.
     * @param tokenList The list to which the nested tokens should be added.
     */
    private void addAllSubTokens(
            INestableParamValueToken token, List<IParamValueToken> tokenList) {
        
        IParamValueToken[] nestedTokens = token.getNestedTokens();
        tokenList.addAll(Arrays.asList(nestedTokens));
        for (IParamValueToken subToken : nestedTokens) {
            if (subToken instanceof INestableParamValueToken) {
                addAllSubTokens((INestableParamValueToken)subToken, tokenList);
            }
        }
    }
    
    /**
     * @param tokens The tokens to set.
     */
    protected void setTokens(List<IParamValueToken> tokens) {
        m_tokens = tokens;
    }

    /**
     * @return Returns the currentNode.
     */
    public IParameterInterfacePO getCurrentNode() {
        return m_currentNode;
    }

    /**
     * @return Returns the guiString.
     */
    public String getGuiString() {
        return m_guiString;
    }

    /**
     * @param guiString The guiString to set.
     */
    protected void setGuiString(String guiString) {
        m_guiString = guiString;
    }
    
    /**
     * @return Returns the modelString.
     */
    public String getModelString() {
        return m_modelString;
    }
    
    /**
     * @return an unmodifiable list of contained RefTokens
     */
    public List<RefToken> getRefTokens() {
        List <RefToken> refTokens = new ArrayList<RefToken>();
        for (IParamValueToken token : getAllTokens()) {
            if (token instanceof RefToken) {
                refTokens.add((RefToken)token);
            }
        }
        return Collections.unmodifiableList(refTokens);
    }

    /**
     * @param modelString The modelString to set.
     */
    protected void setModelString(String modelString) {
        m_modelString = modelString;
    }

    /**
     * @return Returns the desc.
     */
    public IParamDescriptionPO getDesc() {
        return m_desc;
    }
    
    /**
     * @param desc The desc to set.
     */
    protected void setDesc(IParamDescriptionPO desc) {
        m_desc = desc;
    }

    /**
     * @return list of detected errors in tokens
     */
    public List <TokenError> getErrors() {
        validateSingleTokens();
        return m_errors;
    }
    
    /**
     * @return if currently converted and validated string contains errors
     */
    public boolean containsErrors() {
        return !(m_errors.isEmpty());
    }
    
    /**
     * @param error error to add to error list
     */
    protected void addError(TokenError error) {
        m_errors.add(error);
    }

    /**
     * @param errors The errors to set.
     */
    protected void setErrors(List<TokenError> errors) {
        m_errors = errors;
    }

    /**
     * @param currentNode The currentNode to set.
     */
    void setCurrentNode(IParamNodePO currentNode) {
        m_currentNode = currentNode;
    }
    
    /**
     * Creates an error based on the provided information and appends that
     * error to the receiver.
     * 
     * @param e The exception that caused the error.
     * @param input The input string for which the exception occurred.
     */
    protected void createErrors(IOException e, String input) {
        addError(new TokenError(input, MessageIDs.E_GENERAL_PARSE_ERROR, 
                ConvValidationState.invalid));
    }

    /**
     * Creates an error based on the provided information and appends that
     * error to the receiver.
     * 
     * @param e The exception that caused the error.
     * @param input The input string for which the exception occurred.
     */
    protected void createErrors(LexerException e, String input) {
        addError(new TokenError(input, MessageIDs.E_GENERAL_PARSE_ERROR, 
                ConvValidationState.invalid));
    }

    /**
     * Creates an error based on the provided information and appends that
     * error to the receiver.
     * 
     * @param e The exception that caused the error.
     * @param input The input string for which the exception occurred.
     */
    protected void createErrors(ParserException e, String input) {
        ConvValidationState state = ConvValidationState.invalid;
        if (e.getToken() instanceof EOF) {
            // unexpected EOF token means that the error occurred at the
            // end of the parsed string, which implies that, although the
            // current string is invalid, it could be made valid by appending
            // the necessary text
            state = ConvValidationState.undecided;
        }
        addError(new TokenError(input, 
                MessageIDs.E_GENERAL_PARSE_ERROR, state));
    }

    /**
     * Creates an error based on the provided information and appends that
     * error to the receiver.
     * 
     * @param e The exception that caused the error.
     * @param input The input string for which the exception occurred.
     */
    protected void createErrors(SemanticParsingException e, String input) {
        if (RECOVERABLE_PARSE_ERROR_CODES.contains(e.getErrorId())) {
            addError(new TokenError(input,  
                    e.getErrorId(), ConvValidationState.undecided));
        } else {
            addError(new TokenError(input, 
                    e.getErrorId(), ConvValidationState.invalid));
        }
    }

    /**
     * @return Returns the validator.
     */
    IParamValueValidator getValidator() {
        return m_validator;
    }

    /**
     * creates appropriate TokenError for given ConverterValidationState
     * @param state computed validation state
     * @param token validated token
     */
    protected void createTokenError(ConvValidationState state, 
            IParamValueToken token) {
        if (state == ConvValidationState.invalid 
            || state == ConvValidationState.undecided) {
            TokenError tokenError = 
                new TokenError(getGuiString(), 
                    token.getErrorKey(), state);
            addError(tokenError);
        }
    }
    
    /**
     * @return whether we are in GUI context
     */
    abstract boolean isGUI();

}
