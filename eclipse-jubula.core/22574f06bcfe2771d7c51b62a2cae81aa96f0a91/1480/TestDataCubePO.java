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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import javax.persistence.Query;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.client.core.businessprocess.IParamNameMapper;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.persistence.PersistenceUtil;
import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.Index;


/**
 * @author BREDEX GmbH
 * @created Jul 13, 2010
 * 
 */
@Entity
@Table(name = "PARAM_INTERFACE", 
        indexes = {@javax.persistence.Index(
                        name = "FK_PARENT_IDX", 
                        columnList = "FK_PARENT",
                        unique = false),
                    @javax.persistence.Index(
                        name = "TD_MANAGER_IDX", 
                        columnList = "TD_MANAGER",
                        unique = false)})
class TestDataCubePO implements ITestDataCubePO {
    /** Persistence (JPA / EclipseLink) OID */
    private transient Long m_id = null;

    /** Persistence (JPA / EclipseLink) version id */
    private transient Integer m_version = null;

    /** The ID of the parent project */
    private Long m_parentProjectId = null;
    
    /** the name by which the cube can be referenced */
    private String m_name = null;
    
    /** parent category */
    private ITestDataCategoryPO m_parent = null;

    /**
     * <code>m_parameters</code>parameters for testcase
     */
    private List<IParamDescriptionPO> m_hbmParameterList = 
        new ArrayList<IParamDescriptionPO>();
    /**
     * <code>m_dataManager</code> dataManager for handling of testdata
     */
    private ITDManager m_dataManager = null;

    /**
     * path to externalDataFile, could be Excel or sth. else
     */
    private String m_dataFile = null;
    
    /**
     * <code>m_completeTd</code>map to manage the information about testdata 
     * completeness for each supported language <br>
     * key: supported languages, Type: string (string presentation of Locale)
     * value: flag to label the completeness of testdata
     */
    private transient boolean m_completeTd = false;
    
    /** the data cube referenced by this node */
    private IParameterInterfacePO m_referencedDataCube;

    /**
     * 0-arg constructor for Persistence (JPA / EclipseLink).
     */
    @SuppressWarnings("unused")
    private TestDataCubePO() {
        // For Persistence (JPA / EclipseLink)
        // Nothing to initialize
    }

    /**
     * Constructor
     * 
     * @param name The reference name for the newly created object.
     */
    TestDataCubePO(String name) {
        setName(name);
        setDataManager(PoMaker.createTDManagerPO(this));
    }
    
    /**
     * 
     * @return parameters instance
     */
    @OneToMany(targetEntity = ParamDescriptionPO.class, 
               fetch = FetchType.EAGER, 
               cascade = CascadeType.ALL, 
               orphanRemoval = true)
    @JoinColumn(name = "PARAM_NODE")
    @OrderColumn(name = "IDX_PARAM_NODE")
    @BatchFetch(value = BatchFetchType.JOIN)
    protected List<IParamDescriptionPO> getHbmParameterList() {
        return m_hbmParameterList;
    }
    
    /**
     * Removes the parameter descriptions
     * @param sess the session
     */
    private void removeParamDescriptions(EntityManager sess) {
        Query q = sess.createNativeQuery("delete from PARAM_DESC where PARAM_NODE = ?1"); //$NON-NLS-1$
        q.setParameter(1, getId()).executeUpdate();
        for (IParamDescriptionPO desc : getHbmParameterList()) {
            q = sess.createNativeQuery("delete from PARAM_NAMES where GUID = ?1 and PARENT_PROJ = ?2"); //$NON-NLS-1$
            q.setParameter(1, desc.getUniqueId()).
                setParameter(2, getParentProjectId()).executeUpdate();
        }
    }
    
    /**
     * Add a parameter description to the list of descriptions
     * @param p <code>ParamDescriptionPO</code> to be added
     */
    protected void addParameter(IParamDescriptionPO p) {
        getModifiableParameterList().add(p);
        getHbmDataManager().addUniqueId(p.getUniqueId());
    }
    /**
     * Get the ParameterList, create one if necessary
     * @return the normal List
     */
    @Transient
    private List<IParamDescriptionPO> getModifiableParameterList() {
        if (getHbmParameterList() == null) {
            setHbmParameterList(new ArrayList<IParamDescriptionPO>());
        }
        return getHbmParameterList();
    }

    /**
     * Remove a parameter description from the list of descriptions. This
     * is a method used by derived classes to work with the list.
     * @param p <code>ParamDescriptionPO</code> to be removed
     */
    protected void removeParameter(IParamDescriptionPO p) {
        getModifiableParameterList().remove(p);
        getHbmDataManager().removeUniqueId(p.getUniqueId());
    }
    /**
     * Gets the parameter with the given unique id
     * 
     * @param uniqueId uniqueId (GUID or I18NKey) of parameter
     * @return The parameter or <code>null</code>, if this node doesn't
     *         contain a parameter with the passed unique id
     */
    public IParamDescriptionPO getParameterForUniqueId(String uniqueId) {
        Validate.notNull(uniqueId, Messages.TheUniqueIdMustNotBeNull);
        for (IParamDescriptionPO desc : getParameterList()) {
            if (uniqueId.equals(desc.getUniqueId())) {
                return desc;
            }
        }
        return null;
    }
    
    /**
     * gets the parameter description for the given parameter
     * @param paramName name of parameter 
     * @return paramDescription for given parameter
     */
    public IParamDescriptionPO getParameterForName(String paramName) {
        Validate.notNull(paramName, "Param name must not be null."); //$NON-NLS-1$
        for (IParamDescriptionPO desc : getParameterList()) {
            if (paramName.equals(desc.getName())) {
                return desc;
            }
        }
        return null;
    }
    
    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.core.model.IParamNodePO#getParamNames()
     */
    @Transient
    public List<String> getParamNames() {
        List<String> paramNames = new ArrayList<String>();
        for (IParamDescriptionPO desc : getParameterList()) {
            paramNames.add(desc.getName());
        }
        return paramNames;
    }
    

    /**
     * Clears the parameter list.
     */
    protected final void clearParameterList() {
        getModifiableParameterList().clear();
        getDataManager().clearUniqueIds();
    }
    
    /**
     * @return an unmodifiable copy for further use
     */
    @Transient
    public List<IParamDescriptionPO> getParameterList() {
        List<IParamDescriptionPO> hbmParameterList = getHbmParameterList();
        if (hbmParameterList == null) {
            return Collections.emptyList();
        } 
        return Collections.unmodifiableList(hbmParameterList);
    }
    
    /**
     * {@inheritDoc}
     */
    @Transient
    public ListIterator<IParamDescriptionPO> getParameterListIter() {
        if (getHbmParameterList() == null) {
            List<IParamDescriptionPO>emptyList = Collections.emptyList();
            return emptyList.listIterator();
        }
        return getHbmParameterList().listIterator();
    }
    /**
     * 
     * @return Size of ParameterList to prevent calls get getParamterList()
     * just to check if there are any parameters
     */
    @Transient
    public int getParameterListSize() {
        if (getHbmParameterList() == null) {
            return 0;
        } 
        return getHbmParameterList().size();        
    }
    
    /**
     * @param parameterList The parameterList to set.
     */
    protected void setHbmParameterList(
            List<IParamDescriptionPO> parameterList) {
        m_hbmParameterList = parameterList;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public ITDManager getDataManager() {
        if (getReferencedDataCube() != null) {
            return getReferencedDataCube().getDataManager();
        }
        return getHbmDataManager();
    }
    
    /**
     * 
     * @return Returns the dataManager.
     */
    @OneToOne(cascade = CascadeType.ALL, 
              fetch = FetchType.EAGER, 
              targetEntity = TDManagerPO.class)
    @JoinColumn(name = "TD_MANAGER")
    @BatchFetch(value = BatchFetchType.JOIN)
    protected ITDManager getHbmDataManager() {
        return m_dataManager;
    }
    
    /**
     * Removes the data manager
     * @param sess the session
     */
    private void removeDataManager(EntityManager sess) {
        m_dataManager.goingToBeDeleted(sess);
        Query q = sess.createNativeQuery("update PARAM_INTERFACE set TD_MANAGER = null where ID = ?1"); //$NON-NLS-1$
        q.setParameter(1, getId()).executeUpdate();
        q = sess.createNativeQuery("delete from TD_MANAGER where ID = ?1"); //$NON-NLS-1$
        q.setParameter(1, m_dataManager.getId()).executeUpdate();
    }
    
    /**
     * Setter for internal data used by Persistence (JPA / EclipseLink)
     * @param dataManager data
     */
    protected void setHbmDataManager(ITDManager dataManager) {
        m_dataManager = dataManager;        
    }
    
    /**
     * @param dataManager The dataManager to set.
     */
    public void setDataManager(ITDManager dataManager) {
        setHbmDataManager(dataManager);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        setHbmParentProjectId(projectId);
        for (IParamDescriptionPO paramDesc : getHbmParameterList()) {
            paramDesc.setParentProjectId(projectId);
        }
        getDataManager().setParentProjectId(projectId);
    }

    /** @return Returns the completeTdMap */
    @Transient
    private boolean getCompleteTdMap() {
        return m_completeTd;
    }
    
    /**
     * method to set the completeTdFlag for a given Locale
     * @param flag the state of completeTdFlag to set
     */
    public void setCompleteTdFlag(boolean flag) {
        m_completeTd = flag;
    }

    /**
     * sets the File
     * @param pathToExternalDataFile
     *      path to file
     */
    private void setHbmDataFile(String pathToExternalDataFile) {
        m_dataFile = pathToExternalDataFile;
    }
    
    
    /**
     * gets the value of the m_dataFile property
     * 
     * @return the name of the node
     */
    @Basic
    @Column(name = "DATA_FILE", length = MAX_STRING_LENGTH)
    private String getHbmDataFile() {
        return m_dataFile;
    }
    
    
    /**
     * gets the value of the m_dataFile property
     * 
     * @return the name of the node
     */
    @Transient
    public String getDataFile() {
        return getHbmDataFile();
    }

    /**
     * sets the File
     * @param pathToExternalDataFile
     *      path to file
     */
    public void setDataFile(String pathToExternalDataFile) {
        setHbmDataFile(pathToExternalDataFile);
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public IParameterInterfacePO getReferencedDataCube() {
        return getHbmReferencedDataCube();
    }

    /**
     * {@inheritDoc}
     */
    public void setReferencedDataCube(IParameterInterfacePO dataCube) {
        setHbmReferencedDataCube(dataCube);
    }

    /**
     * 
     * @param dataCube The Data Cube to reference.
     */
    private void setHbmReferencedDataCube(IParameterInterfacePO dataCube) {
        m_referencedDataCube = dataCube;
    }
    
    
    /**
     *      
     * @return the referenced Data Cube, or <code>null</code> if no Data Cube 
     *         is referenced from this node.
     */
    @ManyToOne(targetEntity = TestDataCubePO.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "REF_DATA_CUBE")
    @BatchFetch(value = BatchFetchType.JOIN)
    private IParameterInterfacePO getHbmReferencedDataCube() {
        return m_referencedDataCube;
    }

    /**
     *  
     * @return Returns the id.
     */
    @Id
    @GeneratedValue
    public Long getId() {
        return m_id;
    }
    /**
     * @param id The id to set.
     */
    void setId(Long id) {
        m_id = id;
    }

    /**
     *          
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "NAME", length = MAX_STRING_LENGTH)
    public String getName() {
        return m_name;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String name) {
        m_name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public Long getParentProjectId() {
        return getHbmParentProjectId();
    }
    
    /**
     * JPA accessor for ID of parent Project.
     * 
     * @return the parent Project ID.
     */
    @Basic
    @Column(name = "PARENT_PROJ")
    @Index(name = "PI_TDC_PARENT_PROJ")
    Long getHbmParentProjectId() {
        return m_parentProjectId;
    }

    /**
     * JPA mutator for ID of parent Project.
     * 
     * @param projectId The parent Project ID.
     */
    void setHbmParentProjectId(Long projectId) {
        m_parentProjectId = projectId;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public INodePO getSpecificationUser() {
        return null;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Version
    public Integer getVersion() {
        return m_version;
    }
    /**
     * @param version The version to set.
     */
    @SuppressWarnings("unused")
    private void setVersion(Integer version) {
        m_version = version;
    }

    /** {@inheritDoc} */
    public IParamDescriptionPO addParameter(String type, String name,
            IParamNameMapper mapper) {
        return addParameter(type, name, PersistenceUtil.generateUUID(), mapper);
    }

    /** {@inheritDoc} */
    public IParamDescriptionPO addParameter(String type, String name,
            String guid, IParamNameMapper mapper) {
        Validate.notEmpty(type, "Missing parameter type for TestDataCube " + //$NON-NLS-1$
                this.getName());
        Validate.notEmpty(name, "Missing name for parameter in " + //$NON-NLS-1$
                "TestDataCube " + this.getName()); //$NON-NLS-1$

        IParamDescriptionPO desc = PoMaker.createTcParamDescriptionPO(type,
                name, guid, mapper);
        addParameter(desc);
        return desc;
    }

    /** {@inheritDoc} */
    public IParamDescriptionPO addParameter(String type, String userDefName,
            boolean always, IParamNameMapper mapper) {
        IParamDescriptionPO desc = null;
        if (always || getParameterForName(userDefName) == null) {
            desc = addParameter(type, userDefName, mapper);
        }
        return desc;
    }

    /** {@inheritDoc} */
    public void moveParameter(String guId, int index) {
        final IParamDescriptionPO parameter = getParameterForUniqueId(guId);
        final List<IParamDescriptionPO> paramList = getHbmParameterList();
        final int currIdx = paramList.indexOf(parameter);
        paramList.remove(currIdx);
        paramList.add(index, parameter);
    }

    /** {@inheritDoc} */
    public void removeParameter(String uniqueId) {
        IParamDescriptionPO desc = getParameterForUniqueId(uniqueId);
        if (desc != null) {
            removeParameter(desc);
            ((TcParamDescriptionPO)desc).getParamNameMapper()
                .removeParamNamePO(desc.getUniqueId());
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    @ManyToOne(targetEntity = TestDataCategoryPO.class)
    @JoinColumn(name = "FK_PARENT")
    public ITestDataCategoryPO getParent() {
        return m_parent;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public void setParent(ITestDataCategoryPO parent) {
        m_parent = parent;
    }
    
    /** {@inheritDoc} */
    public void goingToBeDeleted(EntityManager sess) {
        removeParamDescriptions(sess);
        removeDataManager(sess);
    }
}
