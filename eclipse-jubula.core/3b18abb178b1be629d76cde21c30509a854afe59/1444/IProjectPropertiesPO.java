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

import java.util.Set;

import org.eclipse.jubula.client.core.utils.TrackingUnit;

/**
 * @author BREDEX GmbH
 * @created Jun 11, 2007
 */
public interface IProjectPropertiesPO extends IPersistentObject, 
    IALMReportingProperties {
    /**
     * @return <code>true</code> if this project is reusable. Otherwise
     *         <code>false</code>.
     */
    public abstract boolean getIsReusable();
    
    /**
     * @param isReusable Whether the project should be reusable.
     */
    public void setIsReusable(boolean isReusable);
    
    /**
     * @return <code>true</code> if this project tracks changes. Otherwise
     *         <code>false</code>.
     */
    public abstract boolean getIsTrackingActivated();
    
    /**
     * @param isTrackingActivated Whether the project should track changes.
     */
    public void setIsTrackingActivated(boolean isTrackingActivated);

    /**
     * @return <code>true</code> if this project is protected. Otherwise
     *         <code>false</code>.
     */
    public abstract boolean getIsProtected();
    
    /**
     * @param isProtected Whether the project should be protected.
     */
    public void setIsProtected(boolean isProtected);
    
    /**
     * 
     * @return the set of used projects.
     */
    public Set<IReusedProjectPO> getUsedProjects();

    /**
     * 
     * @param reusedProject The project to reuse.
     */
    public void addUsedProject(IReusedProjectPO reusedProject);
    
    /**
     * 
     * @param project The project to remove.
     */
    public void removeUsedProject(IReusedProjectPO project);

    /**
     * @return the id of the toolkit of this project
     */
    public abstract String getToolkit();
    
       
    /**
     * @param toolkit the id of the toolKit type of this project
     */
    public abstract void setToolkit(String toolkit);

    /**
     * @return the major version number of this project
     */
    public abstract Integer getMajorNumber();

    /**
     * @return the major version number of this project
     */
    public abstract Integer getMinorNumber();

    /**
     * 
     * @return Returns the micro version number.
     */
    public Integer getMicroNumber();

    /**
     * 
     * @return Returns the qualifier version number.
     */
    public String getVersionQualifier();
    
    /**
     * Clears the reused projects set.
     */
    public abstract void clearUsedProjects();

    /**
     * @return the the number of days to clean the results for
     */
    public abstract Integer getTestResultCleanupInterval();
    
    /**
     * @param noOfDays the number of days to clean the results for
     */
    public abstract void setTestResultCleanupInterval(Integer noOfDays);
    
    /**
     * @return the check conf container
     */
    public abstract ICheckConfContPO getCheckConfCont();
    
    /**
     * @return the detail of a user who made a change which is stored for identification
     */
    public abstract String getTrackChangesSignature();

    /**
     * @param signature the detail of a user who made a change which is stored for identification
     */
    public abstract void setTrackChangesSignature(String signature);

    /**
     * @return the unit in which time should be measured for storing changes
     */
    public abstract TrackingUnit getTrackChangesUnit();

    /**
     * @param unit the unit in which time should be measured for storing changes
     */
    public abstract void setTrackChangesUnit(TrackingUnit unit);

    /**
     * @return the timespan of how long changes should be stored
     */
    public abstract Integer getTrackChangesSpan();

    /**
     * @param span the timespan of how long changes should be stored
     */
    public abstract void setTrackChangesSpan(Integer span);
    
    /**
     * @param markupLanguage the markup language used for the description text
     */
    public void setMarkupLanguage(String markupLanguage);
    
    /**
     * @return the markup language used for the description text
     */
    public String getMarkupLanguage();

    /**
     * @return the GUID.
     */
    public String getGuid();

}
