/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.utils;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * @author BREDEX GmbH
 * @created 18.06.2015
 */
public class NodeNameUtil {

    
    /** close bracked */
    protected static final String CLOSE_BRACKET = "]"; //$NON-NLS-1$

    /** open bracked */
    protected static final String OPEN_BRACKET = " ["; //$NON-NLS-1$
    
    /** <code>SEPARATOR</code> */
    protected static final String SEPARATOR = "; "; //$NON-NLS-1$
    
    /**
     * <code>UNNAMED_NODE</code>
     */
    private static final String UNNAMED_NODE = 
            Messages.UnnamedNode;

    /**
     * Construktor
     */
    private NodeNameUtil() {
        
    }
    
    /** 
     * 
     * @param testSuitRef The Reference Test Suit to examine.
     * @return label text for the given Reference Test Suit.
     */
    public static String getText(IRefTestSuitePO testSuitRef) {
        StringBuilder nameBuilder = new StringBuilder();
        
        String refRealName = testSuitRef.getRealName();

        if (!StringUtils.isBlank(refRealName)) {
            nameBuilder.append(refRealName);
            ITestSuitePO testSuite = testSuitRef.getTestSuite();
            String testSuiteName = testSuite != null
                    ? testSuite.getName()
                    : StringUtils.EMPTY;
            appendSpecName(nameBuilder, testSuiteName);
        } else {
            String testSuiteRefName = testSuitRef != null ? testSuitRef
                    .getTestSuite().getName() : UNNAMED_NODE;
            createSpecName(nameBuilder, testSuiteRefName);

        }
        return nameBuilder.toString();
    }
    
    /**
     * 
     * @param testCaseRef The Test Case Reference to examine.
     * @param params Bool if params should be added to label.
     * @return label text for the given Test Case Reference.
     */
    public static String getText(IExecTestCasePO testCaseRef, boolean params) {
        StringBuilder nameBuilder = new StringBuilder();
        
        String realName = testCaseRef.getRealName();
        if (!StringUtils.isBlank(realName)) {
            nameBuilder.append(realName);
            ISpecTestCasePO testCase = testCaseRef.getSpecTestCase();
            String testCaseName = testCase != null 
                    ? testCase.getName() 
                    : StringUtils.EMPTY;
            appendSpecName(nameBuilder, testCaseName);
            
        } else {
            ISpecTestCasePO testCase = testCaseRef.getSpecTestCase();
            String testCaseName = testCase != null 
                    ? testCase.getName() 
                    : StringUtils.EMPTY;
            createSpecName(nameBuilder, testCaseName); 
        }
        
        if (params) {
            nameBuilder.append(getParameterString(testCaseRef));
        }
        
        return nameBuilder.toString();
    }
    
    /**
     * 
     * @param testCase The Test Case to examine.
     * @param params Bool if params should be added to label.
     * @return label text for the given Test Case.
     */
    public static String getText(ISpecTestCasePO testCase, boolean params) {
        if (!params) {
            return testCase.getName();
        }
        return testCase.getName() + getParameterString(testCase);
    }
    
    /**
     * 
     * @param paramNode The Parameter Node to examine.
     * @return a label representing all Parameters used by the given node.
     */
    private static String getParameterString(IParamNodePO paramNode) {
        StringBuilder nameBuilder = new StringBuilder();
        Iterator<IParamDescriptionPO> iter = 
            paramNode.getParameterList().iterator();
        boolean parameterExist = false;
        if (iter.hasNext()) {
            parameterExist = true;
            nameBuilder.append(NodeNameUtil.OPEN_BRACKET);
        }
        if (iter.hasNext()) {
            while (iter.hasNext()) {
                IParamDescriptionPO descr = iter.next();
                nameBuilder.append(descr.getName());
                if (iter.hasNext()) {
                    nameBuilder.append(NodeNameUtil.SEPARATOR);
                }
            }
        }
        if (parameterExist) {
            nameBuilder.append(NodeNameUtil.CLOSE_BRACKET);
        }
        
        return nameBuilder.toString();
    }

    /**
     * helper method to have same appearance for TS and TC
     * @param nameBuilder {@link StringBuilder} where the name should be appended
     * @param refName ref TC or TS string
     */
    private static void createSpecName(StringBuilder nameBuilder,
            String refName) {
        nameBuilder.append(StringConstants.LEFT_INEQUALITY_SING)
                .append(refName)
                .append(StringConstants.RIGHT_INEQUALITY_SING);
    }

    /**
     * helper method to have same appearance for TS and TC
     * @param nameBuilder {@link StringBuilder} where the name should be appended
     * @param specName spec TC or TS string
     */
    private static void appendSpecName(StringBuilder nameBuilder,
            String specName) {
        nameBuilder.append(StringConstants.SPACE)
            .append(StringConstants.LEFT_PARENTHESIS)
            .append(specName)
            .append(StringConstants.RIGHT_PARENTHESIS);
    }
}
