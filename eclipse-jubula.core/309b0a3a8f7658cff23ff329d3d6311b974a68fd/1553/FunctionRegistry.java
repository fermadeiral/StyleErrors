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
package org.eclipse.jubula.client.core.functions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registry for function extensions.
 */
public class FunctionRegistry {

    /** the logger */
    private static final Logger LOG = 
            LoggerFactory.getLogger(FunctionRegistry.class);
    
    /** the ID of the Functions extension point */
    private static final String EXTENSION_POINT_ID = 
            "org.eclipse.jubula.client.core.functions"; //$NON-NLS-1$
    
    /** ID for "name" attribute used for functions and parameters */
    private static final String ATTR_NAME = "name"; //$NON-NLS-1$

    /** ID for "class" attribute used for function evaluator */
    private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

    /** ID for "type" attribute used for parameters and varArgs */
    private static final String ATTR_TYPE = "type"; //$NON-NLS-1$

    /** 
     * ID for "defaultArgumentCount" attribute used for parameters and varArgs 
     */
    private static final String ATTR_DEFAULT_ARG_COUNT = "defaultArgumentCount"; //$NON-NLS-1$

    /** "parameter" sub-element of function */
    private static final String ELEMENT_PARAM = "parameter"; //$NON-NLS-1$

    /** "varArgs" sub-element of function */
    private static final String ELEMENT_VARARG = "varArg"; //$NON-NLS-1$
    
    /** single instance */
    private static FunctionRegistry instance = null;
    
    /** all registered functions: &lt;function_name&gt; =&gt; &lt;function&gt; */
    private Map<String, FunctionDefinition> m_registeredFunctions = null;

    /**
     * Private constructor for singleton class.
     */
    private FunctionRegistry() {
        registerFunctions();
    }

    /**
     * 
     * @return the single instance.
     */
    public static synchronized FunctionRegistry getInstance() {
        if (instance == null) {
            instance = new FunctionRegistry();
        }
        
        return instance;
    }
    
    /**
     * Reads and registers Functions from extensions.
     */
    private void registerFunctions() {
        Map<String, FunctionDefinition> registeredFunctions = 
                new HashMap<String, FunctionDefinition>();
        IExtensionPoint functionExtensionPoint = 
                Platform.getExtensionRegistry().getExtensionPoint(
                        EXTENSION_POINT_ID);

        for (IExtension extension : functionExtensionPoint.getExtensions()) {
            for (IConfigurationElement functionElement 
                    : extension.getConfigurationElements()) {
                
                String functionName = functionElement.getAttribute(ATTR_NAME);
                
                VarArgsDefinition varArg = null;
                List<ParameterDefinition> parameters = 
                        new ArrayList<ParameterDefinition>();
                for (IConfigurationElement parameterElement 
                        : functionElement.getChildren(ELEMENT_PARAM)) {

                    String name = parameterElement.getAttribute(ATTR_NAME);
                    String type = parameterElement.getAttribute(ATTR_TYPE);
                    parameters.add(new ParameterDefinition(name, type));
                }
                for (IConfigurationElement varArgElement 
                        : functionElement.getChildren(ELEMENT_VARARG)) {

                    varArg = new VarArgsDefinition(
                            varArgElement.getAttribute(ATTR_TYPE), 
                            Integer.parseInt(varArgElement.getAttribute(
                                    ATTR_DEFAULT_ARG_COUNT)));
                }

                ParameterDefinition[] parameterArray = 
                        new ParameterDefinition[parameters.size()];
                parameterArray = parameters.toArray(parameterArray);
                Object evaluator;
                try {
                    evaluator = functionElement.createExecutableExtension(
                            ATTR_CLASS);
                    if (evaluator instanceof IFunctionEvaluator) {
                        FunctionDefinition function = new FunctionDefinition(
                                functionName, parameterArray, varArg,
                                (IFunctionEvaluator)evaluator);
                        registeredFunctions.put(function.getName(), function);
                    } else {
                        LOG.error(NLS.bind(
                                Messages.FunctionRegistry_WrongEvaluatorType, 
                                new Object[] {evaluator.getClass(), 
                                    functionName, 
                                    IFunctionEvaluator.class.getName()}));
                    }
                } catch (CoreException e) {
                    LOG.error(NLS.bind(
                            Messages.FunctionRegistry_EvaluatorCreationError, 
                            functionName), e);
                }
            }
        }
        
        m_registeredFunctions = 
                Collections.unmodifiableMap(registeredFunctions);
        
    }

    /**
     * 
     * @return all registered Functions.
     */
    public Collection<FunctionDefinition> getAllFunctions() {
        return m_registeredFunctions.values();
    }
    
    /**
     * 
     * @param functionName The name of the Function to retrieve.
     * @return the Function registered for the given name, or <code>null</code>
     *         if no function is registered for that name.
     */
    public FunctionDefinition getFunction(String functionName) {
        return m_registeredFunctions.get(functionName);
    }
}
