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
package org.eclipse.jubula.client.core.model;

import java.util.Date;

/**
 * @author BREDEX GmbH
 * @created Aug 13, 2010
 */
public interface ITestResult {

    /**
     * 
     * @return the root of the test result tree.
     */
    public TestResultNode getRootResultNode();

    /**
     * 
     * @return the name of the project within which the test was executed.
     */
    public String getProjectName();

    /**
     * 
     * @return the major version number of the project within which the test 
     *         was executed.
     */
    public Integer getProjectMajorVersion();
    
    /**
     * 
     * @return the minor version number of the project within which the test 
     *         was executed.
     */
    public Integer getProjectMinorVersion();
    
    /**
    * @return the micro version number of the project within which the test
    *         was executed.
    */
    public Integer getProjectMicroVersion();
   
   /**
    * @return the version qualifier of the project within which the test
    *         was executed.
    */
    public String getProjectVersionQualifier();
    
    /**
     * 
     * @return the GUID of the project within which the test was executed.
     */
    public String getProjectGuid();

    /**
     * 
     * @return the database ID of the project within which the test was 
     *         executed.
     */
    public long getProjectId();

    /**
     * 
     * @return the number of Test Steps that would be executed during a 
     *         completely successful test. 
     */
    public int getExpectedNumberOfSteps();

    /**
     * 
     * @return the total number of Test Steps that were executed during the 
     *         test.
     */
    public int getNumberOfTestedSteps();

    /**
     * 
     * @return the number of Test Steps that were executed within an 
     *         error-handling context during the test.
     */
    public int getNumberOfEventHandlerSteps();

    /**
     * 
     * @return the number of Test Steps that encountered an error while 
     *         executing during the test. 
     */
    public int getNumberOfFailedSteps();

    /**
     * 
     * @return the time at which the test started.
     */
    public Date getStartTime();

    /**
     * 
     * @return the time at which the test ended.
     */
    public Date getEndTime();

    /**
     * 
     * @return the name of the AUT Configuration used to start the tested AUT, 
     *         or {@link TestresultSummaryBP#AUTRUN} to indicate that no 
     *         AUT Configuration was used.
     */
    public String getAutConfigName();

    /**
     * 
     * @return the hostname of the computer on which the AUT Agent was 
     *         running during the test.
     */
    public String getAutAgentHostName();

    /**
     * 
     * @return a string containing the arguments used to start the 
     *         tested AUT, or the empty string (<code>""</code>) if no
     *         arguments were used.
     */
    public String getAutArguments();
}
