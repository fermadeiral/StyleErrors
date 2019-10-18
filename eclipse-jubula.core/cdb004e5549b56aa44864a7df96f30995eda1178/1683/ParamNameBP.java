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
package org.eclipse.jubula.client.core.businessprocess;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNamePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.ParamNamePM;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.JBFatalException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * class to manage and persist names of testcase parameters
 *
 * @author BREDEX GmbH
 * @created 27.06.2007
 */
public class ParamNameBP extends AbstractNameBP<IParamNamePO> 
    implements IParamNameMapper {
    
    /**
     * <code>log</code> logger for class
     */
    private static Logger log = LoggerFactory.getLogger(ParamNameBP.class);
    
    
    
    /**
     * <code>instance</code> single instance
     */
    private static ParamNameBP instance = null;
    
    
    /**
     * <code>m_paramDescriptions</code> registered param descriptions
     */
    private Set<IParamDescriptionPO> m_paramDescriptions = 
        new HashSet<IParamDescriptionPO>();
    
   
    
    /**
     * private constructor for singleton
     * 
     */
    private ParamNameBP() {
        super();
    }
    

    /**
     * reads all component names from database in names map
     * @throws PMException in case of any db problem
     */
    public void initMap() throws PMException {
        clearAllNamePOs();
        IProjectPO project = GeneralStorage.getInstance().getProject();
        if (project != null) {
            List<IParamNamePO> names = ParamNamePM.readAllParamNames(project
                    .getId());
            for (IParamNamePO paramNamePO : names) {
                addNamePO(paramNamePO);
            }
        }
    }
    
    /**
     * reads all param names of reused project
     * @param reusedProject reused project to read param names from
     * @throws PMException in case of any db problem
     */
    public void initParamNamesOfReusedProject(IReusedProjectPO reusedProject) 
        throws PMException {
        
        List<IParamNamePO> names;
        if (reusedProject.getId() != null) {
            names = ParamNamePM.readAllParamNames(reusedProject.getId());
            for (IParamNamePO paramNamePO : names) {
                addNamePO(paramNamePO);
            }
        }
    }

    /**
     * @return the single instance
     */
    public static ParamNameBP getInstance() {
        if (instance == null) {
            instance = new ParamNameBP();
        }
        return instance;
    }

    /**
     * @param namePO paramName object
     */
    public void addParamNamePO(IParamNamePO namePO) {
        addNamePO(namePO);
    }
    
    /**
     * @param guid guid of param name to remove
     */
    public void removeParamNamePO(String guid) {
        removeNamePO(guid);
    }

    /**
     * @param uniqueId unique id of parameter
     * @param rootProjId of project the parameter belongs to
     * @return name of parameter
     */
    public String getName(String uniqueId, Long rootProjId) {
        // fallback, show uniqueId, if no name for parameter is available
        String name = uniqueId;
        IParamNamePO namePO = getNamePO(uniqueId);
        if (namePO == null) {
            // try to get param name from db
            try {
                namePO = ParamNamePM.readParamNamePO(uniqueId, rootProjId);
            } catch (PMException e) {
                throw new JBFatalException(e, MessageIDs.E_DATABASE_GENERAL);
            }
        }
        if (namePO != null) {
            name = namePO.getName();
            addNamePO(namePO);
        } else {
            if (log.isDebugEnabled()) {
                StringBuilder msg = new StringBuilder();
                msg.append(Messages.EmptyParameterName);
                msg.append(StringConstants.DOT);
                msg.append(StringConstants.SPACE);
                msg.append(Messages.ParentProjectId);
                msg.append(StringConstants.SPACE);
                msg.append(StringConstants.EQUALS_SIGN);
                msg.append(StringConstants.SPACE);
                msg.append(rootProjId);
                msg.append(StringConstants.SPACE);
                msg.append(Messages.uniqueId);
                msg.append(StringConstants.SPACE);
                msg.append(StringConstants.EQUALS_SIGN);
                msg.append(StringConstants.SPACE);
                msg.append(uniqueId);
                
                log.debug(msg.toString());                
            }
        }
        return name;
    }
    
    /**
     * remove all entries from map
     */
    public void clearParamNames() {
        clearAllNamePOs();
    }
  

    /**
     * {@inheritDoc}
     */
    public void registerParamDescriptionr(IParamDescriptionPO desc) {
        m_paramDescriptions.add(desc);
        
    }

    /**
     * {@inheritDoc}
     */
    public void deregisterAllParamDescriptions() {
        m_paramDescriptions.clear();
        
    }
    
    /**
     * @param uniqueId uniqueId of paramName object to get
     * @return ParamNamePO object to given name, or null if it has not been found.
     */
    public IParamNamePO getParamNamePO(String uniqueId) {
        return getNamePO(uniqueId);
    }

    /**
     * @param cap
     *            the cap to search for the parameter for
     * @param uniqueId
     *            the id of the parameter
     * @return whether its optional or not
     */
    public static boolean isOptionalParameter(
            @NonNull final ICapPO cap,
            @NonNull final String uniqueId) {
        boolean isOptionalParameter = false;
        for (Param p : cap.getMetaAction().getParams()) {
            if (p.getName().equals(uniqueId)) {
                if (p.isOptional()) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }
}
