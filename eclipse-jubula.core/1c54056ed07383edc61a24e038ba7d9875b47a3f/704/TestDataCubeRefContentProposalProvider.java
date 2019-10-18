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
package org.eclipse.jubula.client.ui.rcp.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jubula.client.core.businessprocess.TestDataCubeBP;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;


/**
 * Provides content proposals for referencing Test Data Cubes.
 *
 * @author BREDEX GmbH
 * @created Jul 20, 2010
 */
public class TestDataCubeRefContentProposalProvider implements
        IContentProposalProvider {

    /**
     * Content proposal for referencing a Test Data Cube.
     *
     * @author BREDEX GmbH
     * @created Jul 20, 2010
     */
    private static class TestDataCubeRefContentProposal 
            implements IContentProposal, 
                Comparable<TestDataCubeRefContentProposal> {

        /** the proposed Test Data Cube */
        private IParameterInterfacePO m_dataCube;
        
        /**
         * Constructor
         * 
         * @param dataCube The proposed Test Data Cube. Must not be 
         *                 <code>null</code>.
         */
        public TestDataCubeRefContentProposal(IParameterInterfacePO dataCube) {
            Validate.notNull(dataCube);
            m_dataCube = dataCube;
        }
        
        /**
         * {@inheritDoc}
         */
        public String getContent() {
            return m_dataCube.getName();
        }

        /**
         * {@inheritDoc}
         */
        public int getCursorPosition() {
            return m_dataCube.getName().length();
        }

        /**
         * {@inheritDoc}
         */
        public String getDescription() {
            StringBuilder sb = new StringBuilder();
            for (IParamDescriptionPO param : m_dataCube.getParameterList()) {
                sb.append(param.getName());
                sb.append(StringConstants.TAB);
                sb.append(StringConstants.MINUS);
                sb.append(StringConstants.TAB);
                sb.append(CompSystemI18n.getString(param.getType()));
                sb.append(StringConstants.NEWLINE);
            }

            return sb.length() > 0  ? sb.toString() : null;
        }

        /**
         * {@inheritDoc}
         */
        public String getLabel() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public int compareTo(TestDataCubeRefContentProposal otherProposal) {
            if (otherProposal == null) {
                return -1;
            }

            if (getContent() == null) {
                return otherProposal.getContent() == null ? 0 : -1;
            }
            
            return getContent().compareTo(otherProposal.getContent());
        }
        
    }

    /** the Project containing the Test Data Cubes available for reference */
    private IProjectPO m_project;

    /** the Parameter Interface wishing to reference a Test Data Cube */
    private IParameterInterfacePO m_paramInterface;
    
    /**
     * Constructor
     * 
     * @param project The Project containing the Test Data Cubes available for 
     *                reference. Must not be <code>null</code>.
     * @param paramInterface The Parameter Interface wishing to reference a 
     *                       Test Data Cube. Must not be <code>null</code>.
     */
    public TestDataCubeRefContentProposalProvider(IProjectPO project, 
            IParameterInterfacePO paramInterface) {
        Validate.notNull(project);
        Validate.notNull(paramInterface);
        m_project = project;
        m_paramInterface = paramInterface;
    }
    
    /**
     * {@inheritDoc}
     */
    public IContentProposal[] getProposals(String contents, int position) {
        List<TestDataCubeRefContentProposal> proposalList = 
            new ArrayList<TestDataCubeRefContentProposal>();
        for (IParameterInterfacePO dataCube 
                : TestDataCubeBP.getAllTestDataCubesFor(m_project)) {
            if (dataCubeFulfillsNameRequirements(
                        dataCube, contents.substring(0, position))
                    && dataCubeFulfillsParameterRequirements(
                        dataCube, m_paramInterface)) {

                proposalList.add(
                        new TestDataCubeRefContentProposal(dataCube));
            }
        }

        Collections.sort(proposalList);
        
        return proposalList.toArray(
                new TestDataCubeRefContentProposal[proposalList.size()]);
    }

    /**
     * Checks whether a Test Data Cube supports at least the Parameters 
     * defined by a Parameter Interface.
     * 
     * @param dataCube The Test Data Cube to check for fulfillment.
     * @param paramInterface The Parameter Interface that must be fulfilled.
     * @return <code>true</code> if the given <code>dataCube</code> fulfills 
     *         the Parameter requirements presented by 
     *         <code>paramInterface</code>. Otherwise <code>false</code>.
     */
    private static boolean dataCubeFulfillsParameterRequirements(
            IParameterInterfacePO dataCube, 
            IParameterInterfacePO paramInterface) {
        
        for (IParamDescriptionPO requiredParameter 
                : paramInterface.getParameterList()) {
            IParamDescriptionPO matchingParameter = 
                dataCube.getParameterForName(requiredParameter.getName());
            if (matchingParameter == null 
                    || !matchingParameter.getType().equals(
                            requiredParameter.getType())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks whether the name of a Test Data Cube starts with a given prefix.
     * This check is case <b>in</b>sensitive.
     * 
     * @param dataCube The Test Data Cube to check.
     * @param prefixToMatch The prefix with which the Test Data Cube name 
     *                      should begin.
     * @return <code>true</code> if <code>dataCube</code>'s name starts with
     *         <code>prefixToMatch</code> (<b>not</b> case sensitive). 
     *         Otherwise <code>false</code>.
     */
    private static boolean dataCubeFulfillsNameRequirements(
            IParameterInterfacePO dataCube, String prefixToMatch) {
        return StringUtils.startsWithIgnoreCase(
                dataCube.getName(), prefixToMatch);
    }
}
