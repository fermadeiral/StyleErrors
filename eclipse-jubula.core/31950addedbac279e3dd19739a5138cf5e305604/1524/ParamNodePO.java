
/*******************************************************************************
 * Copyright (c) 2005, 2010 BREDEX GmbH.
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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.businessprocess.AbstractParamInterfaceBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBP;
import org.eclipse.jubula.client.core.businessprocess.TestDataBP;
import org.eclipse.jubula.client.core.utils.ModelParamValueConverter;
import org.eclipse.jubula.client.core.utils.ParamValueConverter;
import org.eclipse.jubula.client.core.utils.RefToken;


/**
 * base class for all nodes with parameters
 * @created 06.06.2005
 */
@Entity
@DiscriminatorValue(value = "A")
abstract class ParamNodePO extends NodePO implements IParamNodePO {
    
    /** 
     * delegate for managing the parameter interface and test data for 
     * this node 
     */
    private TestDataCubePO m_parameterInterface;
    
    /**
     * @param name name of node
     * @param isGenerated indicates whether this node has been generated
     */
    public ParamNodePO(String name, boolean isGenerated) {
        super(name, isGenerated);
        setParameterInterface(new TestDataCubePO(null));
    }

    /**
     * @param name name of node
     * @param guid The GUID of the param node.
     * @param isGenerated indicates whether this node has been generated
     */
    public ParamNodePO(String name, String guid, boolean isGenerated) {
        super(name, guid, isGenerated);
        setParameterInterface(new TestDataCubePO(null));
    }

    /**
     * only for Persistence (JPA / EclipseLink)
     */
    ParamNodePO() {
       // only for Persistence (JPA / EclipseLink)
    }
    
    /**
     * 
     * @return the object responsible for maintaining the receiver's parameter
     *         interface as well as test data.
     */
    @OneToOne(cascade = CascadeType.ALL, 
               targetEntity = TestDataCubePO.class,
               orphanRemoval = true,
               fetch = FetchType.EAGER)
    @JoinColumn(name = "FK_PARAM_INTERFACE", unique = true)
    // Do not use Batch fetching here this might lead to duplicate data
    // <and> problems that are caused by that fact see e.g. http://eclip.se/489220
    private TestDataCubePO getParameterInterface() {
        return m_parameterInterface;
    }
    
    /**
     * Removes the associated TestDataCube from the DB
     * @param sess the session
     */
    private void removeParamInterface(EntityManager sess) {
        m_parameterInterface.goingToBeDeleted(sess);
        Query q = sess.createNativeQuery("update NODE set FK_PARAM_INTERFACE = null where ID = ?1"); //$NON-NLS-1$
        q.setParameter(1, getId());
        q.executeUpdate();
        q = sess.createNativeQuery("delete from PARAM_INTERFACE where ID = ?1"); //$NON-NLS-1$
        q.setParameter(1, m_parameterInterface.getId());
        q.executeUpdate();
    }

    /**
     * 
     * @param parameterInterface The new parameter interface for the receiver.
     */
    private void setParameterInterface(TestDataCubePO parameterInterface) {
        m_parameterInterface = parameterInterface;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public String getDataFile() {
        return getParameterInterface().getDataFile();
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public ITDManager getDataManager() {
        return getParameterInterface().getDataManager();
    }

    /**
     * 
     * @return the data manager.
     */
    @Transient
    protected ITDManager getHbmDataManager() {
        return getParameterInterface().getHbmDataManager();
    }

    /**
     * Clears the parameter list.
     */
    protected final void clearParameterList() {
        getParameterInterface().clearParameterList();
    }

    /**
     * Add a parameter description to the list of descriptions
     * @param p <code>ParamDescriptionPO</code> to be added
     */
    protected void addParameter(IParamDescriptionPO p) {
        getParameterInterface().addParameter(p);
    }
    
    /**
     * 
     * @return parameters instance
     */
    @Transient
    protected List<IParamDescriptionPO> getHbmParameterList() {
        return getParameterInterface().getHbmParameterList();
    }
    
    /**
     * Remove a parameter description from the list of descriptions. This
     * is a method used by derived classes to work with the list.
     * @param p <code>ParamDescriptionPO</code> to be removed
     */
    protected void removeParameter(IParamDescriptionPO p) {
        getParameterInterface().removeParameter(p);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public IParamDescriptionPO getParameterForName(String paramName) {
        return getParameterInterface().getParameterForName(paramName);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public IParamDescriptionPO getParameterForUniqueId(String uniqueId) {
        return getParameterInterface().getParameterForUniqueId(uniqueId);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public List<IParamDescriptionPO> getParameterList() {
        return getParameterInterface().getParameterList();
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public ListIterator<IParamDescriptionPO> getParameterListIter() {
        return getParameterInterface().getParameterListIter();
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public int getParameterListSize() {
        return getParameterInterface().getParameterListSize();
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public List<String> getParamNames() {
        return getParameterInterface().getParamNames();
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public IParameterInterfacePO getReferencedDataCube() {
        return getParameterInterface().getReferencedDataCube();
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setDataFile(String pathToExternalDataFile) {
        getParameterInterface().setDataFile(pathToExternalDataFile);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setDataManager(ITDManager dataManager) {
        getParameterInterface().setDataManager(dataManager);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setReferencedDataCube(IParameterInterfacePO dataCube) {
        getParameterInterface().setReferencedDataCube(dataCube);
    }

    /** {@inheritDoc} */
    public boolean isTestDataComplete() {
        if (StringUtils.isEmpty(getDataFile())) {
            // Excel files are ignored. Other data is checked.
            final int paramListSize = getParameterListSize();
            ITDManager testDataManager = getDataManager();

            List<IParamDescriptionPO> requiredParameters = 
                new ArrayList<IParamDescriptionPO>(getParameterList());
            IParameterInterfacePO refDataCube = getReferencedDataCube();
            for (int i = 0; i < testDataManager.getDataSetCount(); i++) {
                for (IParamDescriptionPO paramDesc : requiredParameters) {
                    int column = 
                        testDataManager.findColumnForParam(
                                paramDesc.getUniqueId());
                    if (refDataCube != null) {
                        IParamDescriptionPO dataCubeParam = 
                            refDataCube.getParameterForName(
                                paramDesc.getName());
                        if (dataCubeParam != null) {
                            column = testDataManager.findColumnForParam(
                                    dataCubeParam.getUniqueId());
                        }
                    }
                   
                    
                    String value = TestDataBP.getTestData(
                            this, testDataManager, paramDesc, i);
                    if (StringUtils.isBlank(value) && column >= -1) {
                        if (this instanceof IExecTestCasePO) {
                            IExecTestCasePO exec = (IExecTestCasePO) this;
                            String result = AbstractParamInterfaceBP
                                    .getValueForSpecNodeWithParamDesc(paramDesc,
                                            exec.getSpecTestCase());
                            if (value == null  
                                    && StringUtils.isNotBlank(result)) {
                                value = result;
                            }
                            if (StringUtils.isBlank(result)) {
                                return false;
                            }
                        }
                    }
                    if (StringUtils.isNotEmpty(value)) {
                        ModelParamValueConverter mpvc = 
                                new ModelParamValueConverter(
                                        value, this, paramDesc);
                        List<RefToken> referenceTokens = mpvc.getRefTokens();
                        String uiValue = mpvc.getGuiString();
                        for (RefToken token : referenceTokens) {
                            if (uiValue.contains(token.getModelString())) {
                                return false;
                            }
                        }
                    } else {
                        if (this instanceof ICapPO) {
                            ICapPO cap = (ICapPO) this;
                            if (!ParamNameBP.isOptionalParameter(
                                    cap, paramDesc.getUniqueId())) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                }
            }
            if ((testDataManager.getDataSetCount() == 0)
                    && (paramListSize > 0)) {
                // this is needed if a Spec is the data holder
                return checkSpecForIncompleteData();
            }
        }
        return true;
    }

    /**
     * checks if the instance of a spec is having default values 
     * for all parameters
     * @return if the test data is complete
     */
    private boolean checkSpecForIncompleteData() {
        ISpecTestCasePO thisSpec = null;
        if (this instanceof ISpecTestCasePO) {
            thisSpec = (ISpecTestCasePO) this;
        } else if (this instanceof IExecTestCasePO) {
            thisSpec = ((IExecTestCasePO)this).getSpecTestCase();
        }
        if (thisSpec != null) {
            List<IParamDescriptionPO> specParameters =
                    thisSpec.getParameterList();
            boolean isComplete = true;
            for (IParamDescriptionPO paramDesc : specParameters) {
                String result = AbstractParamInterfaceBP
                        .getValueForSpecNodeWithParamDesc(
                                paramDesc,
                                thisSpec);
                if (StringUtils.isBlank(result)) {
                    isComplete = false;
                }
            }
            return isComplete;

        }
        return false;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Iterator<TDCell> getParamReferencesIterator() {
        List <TDCell> references = new ArrayList <TDCell> ();
        int row = 0;
        for (IDataSetPO dataSet : getDataManager().getDataSets()) {
            addParamReferences(references, dataSet, row);
            row++;
        }
        return references.iterator();
    }
    
    /** {@inheritDoc} */
    public Iterator<TDCell> getParamReferencesIterator(
            int dataSetRow) {
        
        IDataSetPO row = getDataManager().getDataSet(dataSetRow);
        List <TDCell> references = new ArrayList <TDCell> ();
        addParamReferences(references, row, dataSetRow);
        return references.iterator();
    }

    /**
     * @param references
     *            The references
     * @param row
     *            The row representation
     * @param dataSetRow
     *            The row index
     */
    private void addParamReferences(List <TDCell> references, 
            IDataSetPO row, int dataSetRow) {
        for (int col = 0; col < row.getColumnCount(); col++) {
            String testData = row.getValueAt(col);
            String uniqueId = getDataManager().getUniqueIds().get(col);
            IParamDescriptionPO desc = getParameterForUniqueId(uniqueId);
            ParamValueConverter conv = new ModelParamValueConverter(
                    testData, this, desc);
            if (conv.containsReferences()) {
                references.add(new TDCell(testData, dataSetRow, col));
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        super.setParentProjectId(projectId);
        getDataManager().setParentProjectId(projectId);
        if (getParameterInterface() != null) {
            getParameterInterface().setParentProjectId(projectId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public INodePO getSpecificationUser() {
        return getSpecAncestor();
    }
    
    /**
     * {@inheritDoc}
     */
    public void clearTestData() {
        if (!hasReferencedTestData()) {
            getDataManager().clear();
        }
    }
    
    /**
     * 
     * @return <code>true</code> if the receiver references Test Data (for 
     *         example, by referencing a Central Test Data instance), rather 
     *         than having Test Data of its own.
     */
    public boolean hasReferencedTestData() {
        return getReferencedDataCube() != null;
    }
    
    /** {@inheritDoc} */
    public void goingToBeDeleted(EntityManager sess) {
        super.goingToBeDeleted(sess);
        removeParamInterface(sess);
    }
}
