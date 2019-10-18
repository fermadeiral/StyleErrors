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
import java.util.List;


/**
 * @author BREDEX GmbH
 * @created Jan 22, 2010
 */
public interface ITestResultPO {

    /**
     * only for Persistence (JPA / EclipseLink)
     * @return Returns the id.
     */
    public abstract Long getId();

    /**
     * {@inheritDoc}
     */
    public abstract String toString();
    
    /** 
     * {@inheritDoc}
     */
    public abstract Integer getVersion();
    
    /**
     * @return the internal testresult summary id
     */
    public abstract Long getInternalTestResultSummaryID();
    
    /**
     * @param internalTestResultSummaryID the internal testresult summary id to set
     */
    public abstract void setInternalTestResultSummaryID(
            Long internalTestResultSummaryID);

    /**
     * @return the internalKeywordType.
     */
    public abstract int getInternalKeywordType();

    /**
     * @param internalKeywordType The internalKeywordType to set.
     */
    public abstract void setInternalKeywordType(int internalKeywordType);
    
    /**
     * @return the keywordTypeString.
     */
    public abstract String getKeywordType();

    /**
     * @param keywordType The keywordType to set.
     */
    public abstract void setKeywordType(String keywordType);
    
    /**
     * @return the keywordName.
     */
    public abstract String getKeywordName();

    /**
     * @param keywordName The keywordName to set.
     */
    public abstract void setKeywordName(String keywordName);
    
    /**
     * @return the keywordComment
     */
    public abstract String getKeywordComment();
    
    /**
     * @param keywordComment The keywordComment to set
     */
    public abstract void setKeywordComment(String keywordComment);
    
    /**
     * @return the keywordGuid.
     */
    public abstract String getInternalKeywordGuid();

    /**
     * @param keywordGuid The keywordGuid to set.
     */
    public abstract void setInternalKeywordGuid(String keywordGuid);
    
    /**
     * @return the internalKeywordStatus.
     */
    public abstract int getInternalKeywordStatus();

    /**
     * @param internalKeywordStatus The internalKeywordStatus to set.
     */
    public abstract void setInternalKeywordStatus(int internalKeywordStatus);
    
    /**
     * @return the keywordStatus.
     */
    public abstract String getKeywordStatus();

    /**
     * @param keywordStatus string representation for keyword status.
     */
    public abstract void setKeywordStatus(String keywordStatus);
    
    /**
     * @return the keywordLevel.
     */
    public abstract int getKeywordLevel();

    /**
     * @param keywordLevel The keywordLevel to set.
     */
    public abstract void setKeywordLevel(int keywordLevel);
    
    /**
     * @return the keywordSequence.
     */
    public abstract int getKeywordSequence();

    /**
     * @param keywordSequence The keywordSequence to set.
     */
    public abstract void setKeywordSequence(int keywordSequence);
    
    /**
     * @return the timestamp.
     */
    public abstract Date getTimestamp();

    /**
     * @param timestamp The timestamp to set.
     */
    public abstract void setTimestamp(Date timestamp);
    
    /**
     * @return the componentNameGuid.
     */
    public abstract String getInternalComponentNameGuid();

    /**
     * @param componentNameGuid The componentNameGuid to set.
     */
    public abstract void setInternalComponentNameGuid(String componentNameGuid);
    
    /**
     * @return the componentName.
     */
    public abstract String getComponentName();

    /**
     * @param componentName The componentName to set.
     */
    public abstract void setComponentName(String componentName);
    
    /**
     * @return the internalComponentType.
     */
    public abstract String getInternalComponentType();

    /**
     * @param internalComponentType The internalComponentType to set.
     */
    public abstract void setInternalComponentType(String internalComponentType);
    
    /**
     * @return the componentType.
     */
    public abstract String getComponentType();

    /**
     * @param componentType The componentType to set.
     */
    public abstract void setComponentType(String componentType);
    
    /**
     * @return the internalActionName.
     */
    public abstract String getInternalActionName();

    /**
     * @param internalActionName The internalActionName to set.
     */
    public abstract void setInternalActionName(String internalActionName);
    
    /**
     * @return the actionName.
     */
    public abstract String getActionName();

    /**
     * @param actionName The actionName to set.
     */
    public abstract void setActionName(String actionName);
    
    /**
     * adds a parameter to the parameter list
     * @param parameter IParameterDetailsPO
     */
    public abstract void addParameter(IParameterDetailsPO parameter);
    
    /**
     * @return the unmodifiable parameter list.
     */
    public abstract List<IParameterDetailsPO> getUnmodifiableParameterList();
    
    /**
     * @return the status type. (e.g. error type)
     */
    public abstract String getStatusType();

    /**
     * @param statusType the status type to set.
     */
    public abstract void setStatusType(String statusType);
    
    /**
     * @return the status description.
     */
    public abstract String getStatusDescription();

    /**
     * @param statusDescription the status description to set.
     */
    public abstract void setStatusDescription(String statusDescription);
    
    /**
     * @return the status operator type.
     */
    public abstract String getStatusOperator();

    /**
     * @param statusOperator the status operator to set.
     */
    public abstract void setStatusOperator(String statusOperator);
    
    /**
     * @return the expected value.
     */
    public abstract String getExpectedValue();

    /**
     * @param expectedValue the expected value to set.
     */
    public abstract void setExpectedValue(String expectedValue);
    
    /**
     * @return the actual value.
     */
    public abstract String getActualValue();

    /**
     * @param actualValue the actual value to set.
     */
    public abstract void setActualValue(String actualValue);
    
    /**
     * @return the parentKeywordID
     */
    public abstract Long getInternalParentKeywordID();
    
    /**
     * @param parentKeywordID the parentKeywordID to set
     */
    public abstract void setInternalParentKeywordID(Long parentKeywordID);
    
    /**
     * Checks the equality of the given Object with this Object.
     * @param obj the object to check
     * @return if there is a database ID it returns true if the ID is equal.
     * If there is no ID it will be compared to identity.
     */
    public abstract boolean equals(Object obj);

    /**
     * @return the hash code
     */
    public abstract int hashCode();
    
    /**
     * @param image The screenshot to set.
     */
    public void setImage(byte[] image);
    
    /**
     * @return the screenshot.
     */
    public byte[] getImage();

    /**
     * @param omHeuristicEquivalence the omHeuristicEquivalence to set
     */
    public void setOmHeuristicEquivalence(double omHeuristicEquivalence);

    /**
     * @return the omHeuristicEquivalence
     */
    public double getOmHeuristicEquivalence();

    /**
     * @param noOfSimilarComponents the noOfSimilarComponents to set
     */
    public void setNoOfSimilarComponents(int noOfSimilarComponents);

    /**
     * @return the noOfSimilarComponents
     */
    public int getNoOfSimilarComponents();
    
    /**
     * @return The taskId of this result node
     */
    public String getTaskId();

    /**
     * Sets the value of the task Id property.
     * @param taskId the taskId of this result node
     */
    public void setTaskId(String taskId);
    
    /**
     * @param addition the {@link ITestResultAdditionPO} to add
     */
    public void addAdditon(ITestResultAdditionPO addition);
    
    /**
     * @return gets the complete list of {@link ITestResultAdditionPO}
     */
    public List<ITestResultAdditionPO> getTestResultAdditions();
    
    /**
     * @param isJUnitTestSuite whether a TestCase should be handeled as a TestSuite in JUnit
     */
    public void setIsJUnitSuite(boolean isJUnitTestSuite);
    
    /**
     * @return a boolean to determine whether a TestCase should be handeled as a TestSuite in JUnit
     */
    public boolean getIsJUnitSuite();
}