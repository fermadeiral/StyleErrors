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
package org.eclipse.jubula.autagent.common.monitoring;

import java.io.File;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.jubula.tools.internal.objects.IMonitoringValue;

/**
 * This interface contains all methods which will be called during monitoring execution.
 *  
 * @author BREDEX GmbH
 * @created 20.08.2010
 */
public interface IMonitoring {  
    
    /**
     * This method is creating the agent string for starting the AUT with
     * a monitoring agent. The AUT configuration map for the given AUT should be saved
     * in the MonitoringDataManager. Any further data read or input should
     * be done by using this manager. Note that this method will be called every
     * time a monitored application will be restarted. 
     * 
     * @return A String containing all necessary informations to launch a AUT
     * with a monitoring agent. This String is added to the _JAVA_OPTIONS 
     * environment variable 
     */ 
   
    public String createAgent();
    
    /**
     * Use this method to get data form the profiling agent. This method will
     * be called
     * 
     * @return The value of the String will be displayed in the TestResultSummaryView in the
     * "Measured Value Column"
     */
    
    public Map<String, IMonitoringValue> getMonitoringData();   

    /**
     * Writes the contents of the monitoring report to the given stream. 
     * 
     * @param out The OutputStream in which the report will be written. The 
     *            caller is responsible for initializing and closing the stream. 
     * 
     */
    public void writeMonitoringReport(OutputStream out);
    /**
     * This Method will be executed, when AUT restart is performed.
     */
    public void autRestartOccurred();
    /**
     * to provide a reset of monitoring data.
     */
    public void resetMonitoringData();
    /** sets the autId
     * @param autId the autId to set */
    public void setAutId(String autId);
    
    /**
     * Store a File referring to the directory where support files
     * are stored.
     * @param installDir File referring the installation directory. The
     * directory need not to exist.
     */
    public void setInstallDir(File installDir);
   
    
}
