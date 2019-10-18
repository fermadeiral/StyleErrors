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
package org.eclipse.jubula.client.ui.rcp.controllers.propertysources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jubula.client.core.businessprocess.IParamNameMapper;
import org.eclipse.jubula.client.core.businessprocess.TestDataCubeBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestCasePO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.contentassist.TestDataCubeRefContentProposalProvider;
import org.eclipse.jubula.client.ui.rcp.controllers.propertydescriptors.ContentAssistedTextPropertyDescriptor;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.jubula.client.ui.rcp.factory.TestDataControlFactory;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.labelprovider.DisabledLabelProvider;
import org.eclipse.jubula.client.ui.rcp.provider.labelprovider.ParameterValueLabelProvider;
import org.eclipse.jubula.client.ui.rcp.validator.TestDataCubeReferenceValidator;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedText.IValidator;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is the PropertySource of a SpecTestCase.
 * Its used to display and edit the properties in the Properties View.
 * @author BREDEX GmbH
 * @created 08.12.2004
 * {@inheritDoc}
 */
public class SpecTestCaseGUIPropertySource 
        extends AbstractNodePropertySource {
    /** Constant for the String Locked Parameter */
    public static final String P_ELEMENT_DISPLAY_PARAM_LOCKED = 
        Messages.SpecTestCaseGUIPropertySourceLockedParameters;

    /** Constant for the String Excel Data File */
    public static final String P_ELEMENT_DISPLAY_DATEFILE = 
        Messages.SpecTestCaseGUIPropertySourceTestCaseFileName;

    /** Constant for name of referenced Test Data Cube */
    public static final String P_ELEMENT_DISPLAY_REFDATA = 
        Messages.SpecTestCaseGUIPropertySourceTestCaseReferencedTestData;

    /** Property m_text on display */
    public static final String P_ELEMENT_DISPLAY_DATASOURCE = 
        Messages.SpecTestCaseGUIPropertySourceDataSource;
    
    /** Constant for Category Parameter */
    public static final String P_PARAMETER_CAT = 
        Messages.SpecTestCaseGUIPropertySourceParameter;
   
    /** Constant for Category Parameter */
    public static final String P_TESTDATA_CAT = 
        Messages.SpecTestCaseGUIPropertySourceTestdataCategory;
    
    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(SpecTestCaseGUIPropertySource.class);
    
    /** cached property descriptor for name */
    private IPropertyDescriptor m_namePropDesc = null;
    
    /** cached property descriptor for locking the Test Case interface */
    private IPropertyDescriptor m_lockPropDesc = null;

    /** cached property descriptor for external data file */
    private PropertyDescriptor m_extDataPropDesc = null;

    /** cached property descriptor for referenced Test Data Cube */
    private PropertyDescriptor m_referencedCubePropDesc = null;
    /** cached property descriptor for om categories */
    private IPropertyDescriptor m_oMCatPropDesc;
    
    /**
     * Constructor.
     * Use this only for ISpecTestCasePO!
     * @param testCase the dependened SpecTestCase.
     */
    public SpecTestCaseGUIPropertySource(ITestCasePO testCase) {
        super(testCase);
    }

    /**
     * {@inheritDoc}
     * Inits the PropertyDescriptors and adds them into 
     * super.m_propDescriptors.
     */
    protected void initPropDescriptor() {
        if (!getPropertyDescriptorList().isEmpty()) {
            clearPropertyDescriptors();
        }
        
        // TestCase Name
        if (m_namePropDesc == null) {
            m_namePropDesc = new TextPropertyDescriptor(
                new ElementNameController(),
                Messages.SpecTestCaseGUIPropertySourceTestCaseName);
        }
        addPropertyDescriptor(m_namePropDesc);
        
        super.initPropDescriptor();

        // Task ID
        if (getTaskIdPropDesc() == null) {
            setTaskIdPropDesc(new TextPropertyDescriptor(
                new TaskIdController(),
                org.eclipse.jubula.client.ui.i18n.Messages
                    .AbstractGuiNodePropertySourceTaskId));
        }
        addPropertyDescriptor(getTaskIdPropDesc());
        
        // Data Source
        addPropertyDescriptor(getDataSourcePropertyDescr(
                new SpecTestCaseTestDataSourceController(this)));
        
        if (m_extDataPropDesc == null) {
            m_extDataPropDesc = new TextPropertyDescriptor(
                    new ExternalDataController(this),
                    P_ELEMENT_DISPLAY_DATEFILE);
            m_extDataPropDesc.setCategory(P_TESTDATA_CAT);
        }
        addPropertyDescriptor(m_extDataPropDesc);

        if (m_referencedCubePropDesc == null) {
            IProjectPO activeProject = 
                GeneralStorage.getInstance().getProject();
            IContentProposalProvider dataCubeRefProposalProvider = null;
            IValidator dataCubeRefValidator = null;

            if (activeProject != null) {
                dataCubeRefProposalProvider = 
                    new TestDataCubeRefContentProposalProvider(activeProject, 
                            (IParameterInterfacePO)getPoNode());
                dataCubeRefValidator =
                    new TestDataCubeReferenceValidator(activeProject);
            }
            
            m_referencedCubePropDesc = 
                new ContentAssistedTextPropertyDescriptor(
                        new ReferenceTestDataController(this), 
                        P_ELEMENT_DISPLAY_REFDATA,
                        dataCubeRefProposalProvider, dataCubeRefValidator,
                        ContentProposalAdapter.PROPOSAL_REPLACE);
            m_referencedCubePropDesc.setCategory(P_TESTDATA_CAT);
        }
        addPropertyDescriptor(m_referencedCubePropDesc);

        if (m_lockPropDesc == null) {
            PropertyDescriptor propDes = new PropertyDescriptor(
                    new LockInterfaceController(), 
                    P_ELEMENT_DISPLAY_PARAM_LOCKED);
            propDes.setLabelProvider(new DisabledLabelProvider());
            m_lockPropDesc = propDes;
        }
        addPropertyDescriptor(m_lockPropDesc);
        addPropertyDescriptor(getOMCategorieDescriptor());
        initParameterPropDescriptors();

        initTrackedChangesPropDescriptor();
    }
       
    /**
     * @return the OM association {@link IPropertyDescriptor}
     */
    private IPropertyDescriptor getOMCategorieDescriptor() {
        if (m_oMCatPropDesc == null) {
            PropertyDescriptor cbpd = new PropertyDescriptor(
                    new OMCategorieController(),
                    Messages.SpecTestCaseGUIPropertySourceOMCat);
            cbpd.setLabelProvider(new DisabledLabelProvider());
            m_oMCatPropDesc = cbpd;
        }
        return m_oMCatPropDesc;
    }
    
    /**
     * Initializes the parameter property descriptors, if needed, and adds
     * them to the given list. The contents of the list are then added to
     * the overall list of property descriptors.
     */
    private void initParameterPropDescriptors() {
        List<IPropertyDescriptor> paramPropDescList = 
            new ArrayList<IPropertyDescriptor>();
        List<IParamDescriptionPO> paramList = ((IParamNodePO)getPoNode())
                .getParameterList();
        IParamNameMapper activeParamNameMapper = getActiveParamNameMapper();
        for (IParamDescriptionPO paramDescr : paramList) {
            PropertyDescriptor propDes = TestDataControlFactory
                    .createValuePropertyDescriptor(
                            new ParameterValueController(this, paramDescr,
                                    activeParamNameMapper),
                            getParameterNameDescr(paramDescr), new HashMap<>(),
                            false);
            propDes.setCategory(P_PARAMETER_CAT);
            propDes.setLabelProvider(new ParameterValueLabelProvider(
                    WARNING_IMAGE));
            paramPropDescList.add(propDes);
        }

        addPropertyDescriptor(paramPropDescList);
    }
    
    /**
     *  for the associated {@link IObjectMappingCategoryPO}
     */
    protected class OMCategorieController extends AbstractPropertyController {

        /** {@inheritDoc} */
        public boolean setProperty(Object value) {
            return false;
        }

        /** {@inheritDoc} */
        public Object getProperty() {
            ISpecTestCasePO poNode = (ISpecTestCasePO) getNode();
            List<IObjectMappingCategoryPO> omCategoryAssoc =
                    poNode.getOmCategoryAssoc();
            if (omCategoryAssoc != null) {
                return omCategoryAssoc.stream()
                        .map(IObjectMappingCategoryPO::getName)
                        .collect(Collectors.joining(",")); //$NON-NLS-1$
            }
            return StringConstants.EMPTY;
        }
    }
    
    /**
     * This controllers allows to set FileName for external datas
     * @author BREDEX GmbH
     * @created Nov 2, 2005
     */
    public class ExternalDataController extends 
        AbstractPropertyController implements IParameterPropertyController {

        /**
         * contructor
         * @param s AbstractNodePropertySource
         */
        public ExternalDataController(AbstractNodePropertySource s) {
            super(s);
        }

        /**
         * {@inheritDoc}
         */
        public boolean setProperty(Object value) {
            if (getPoNode() != null) {
                IParamNodePO node = (IParamNodePO)getPoNode();
                node.clearTestData();
                if (value != null) {
                    node.setDataFile(String.valueOf(value));
                } else {
                    node.setDataFile(null);
                }
                getPropertySource().updateParameterInputType();
                DataEventDispatcher.getInstance().fireDataChangedListener(node,
                    DataState.StructureModified, UpdateState.onlyInEditor);
                if (StringUtils.isNotEmpty(node.getDataFile())) {
                    getPropertySource().setActiveParameterInputType(
                            getInputType());
                }
            }
            return true;
        }

        
        /**
         * {@inheritDoc}
         */
        public String getProperty() {
            if (getPoNode() != null) {
                IParamNodePO node = (IParamNodePO)getPoNode();
                if (StringUtils.isNotEmpty(node.getDataFile())) {
                    getPropertySource().updateParameterInputType();
                    return node.getDataFile();
                }
            }
            getPropertySource().setReadOnly(false);
            return StringConstants.EMPTY;
        }

        /**
         * {@inheritDoc}
         */
        public ParameterInputType getInputType() {
            return ParameterInputType.EXTERNAL;
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean isPropertySet() {
            if (getPoNode() != null) {
                IParamNodePO node = (IParamNodePO)getPoNode();
                return StringUtils.isNotEmpty(node.getDataFile());
            }
            
            return false;
        }
    }
    
    /**
     * Class to control the test data source for a spec test case
     * @author BREDEX GmbH
     * @created Aug 30, 2010
     */
    public class SpecTestCaseTestDataSourceController extends
        AbstractPropertyController implements IParameterPropertyController {
        /**
         * <code>UNKOWN_DATA_SOURCE</code>
         */
        protected static final String UNKOWN_DATA_SOURCE = "TestDataSource.unkown"; //$NON-NLS-1$

        /**
         * <code>DATA_SOURCE_CTDS</code>
         */
        protected static final String DATA_SOURCE_CTDS = "TestDataSource.central"; //$NON-NLS-1$

        /**
         * <code>DATA_SOURCE_EXCEL</code>
         */
        protected static final String DATA_SOURCE_EXCEL = "TestDataSource.excel"; //$NON-NLS-1$

        /**
         * <code>DATA_SOURCE_LOCAL</code>
         */
        protected static final String DATA_SOURCE_LOCAL = "TestDataSource.local"; //$NON-NLS-1$
        
        /**
         * <code>DATA_SOURCE_LOCAL</code>
         */
        protected static final String DATA_SOURCE_NONE = "TestDataSource.none"; //$NON-NLS-1$
        
        /**
         * <code>dataSources</code>
         */
        private final List<String> m_dataSources = new LinkedList<String>();

        /**
         * <code>dataSources</code>
         */
        private final List<String> m_userChoosableValues = 
            new LinkedList<String>();
        
        /**
         * Constructor
         * @param s AbstractNodePropertySource
         */
        public SpecTestCaseTestDataSourceController(
                AbstractNodePropertySource s) {
            super(s);
            getDataSource().add(DATA_SOURCE_LOCAL);
            getDataSource().add(DATA_SOURCE_EXCEL);
            getDataSource().add(DATA_SOURCE_CTDS);
            getDataSource().add(DATA_SOURCE_NONE);
            getDataSource().add(UNKOWN_DATA_SOURCE);
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean setProperty(Object value) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        public Object getProperty() {
            if (getPoNode() instanceof IParamNodePO) {
                IParamNodePO node = (IParamNodePO)getPoNode();
                String dataSource = getDataSource(node);
                getPropertySource().setReadOnly(true);
                return getDataSource().indexOf(dataSource);
            }
            return getDataSource().indexOf(UNKOWN_DATA_SOURCE);
        }
        
        /**
         * @param node
         *            the param node
         * @return the kind of datasource
         */
        protected String getDataSource(IParamNodePO node) {
            if (!StringUtils.isBlank(node.getDataFile())) {
                return DATA_SOURCE_EXCEL;
            } else if (node.getReferencedDataCube() != null) {
                return DATA_SOURCE_CTDS;
            } else if (node.getParameterListSize() == 0) {
                return DATA_SOURCE_NONE;
            } else if (node instanceof ISpecTestCasePO
                    || node instanceof ICapPO) {
                return DATA_SOURCE_LOCAL;
            }
            return UNKOWN_DATA_SOURCE;
        }

        /**
         * {@inheritDoc}
         */
        public Image getImage() {
            return DEFAULT_IMAGE;
        }

        /**
         * @return the dataSources
         */
        public String[] getDataSourceArray() {
            return m_dataSources.toArray(new String[m_dataSources.size()]);
        }
        
        /**
         * @return the dataSources as a list
         */
        public List<String> getDataSourceList() {
            return getDataSource();
        }
        
        /**
         * @return the dataSources as a list
         */
        public List<String> getDataSource() {
            return m_dataSources;
        }
        
        /**
         * @return the dataSources human readable
         */
        public String[] getUserChoosableDataSource() {
            if (getUserChoosableValues().isEmpty()) {
                getUserChoosableValues().add(I18n.getString(DATA_SOURCE_LOCAL));
            }
            return getUserChoosableValues().toArray(
                    new String[getUserChoosableValues().size()]);
        }

        /**
         * @return the userChoosableValues
         */
        protected List<String> getUserChoosableValues() {
            return m_userChoosableValues;
        }

        /**
         * {@inheritDoc}
         */
        public ParameterInputType getInputType() {
            return ParameterInputType.LOCAL;
        }

        /**
         * {@inheritDoc}
         */
        public boolean isPropertySet() {
            return false;
        }
    }
    
    /**
     * Controller for managing a reference to a Test Data Cube.
     *
     * @author BREDEX GmbH
     * @created Jul 12, 2010
     */
    public class ReferenceTestDataController extends 
        AbstractPropertyController implements IParameterPropertyController {

        /**
         * contructor
         * @param s AbstractNodePropertySource
         */
        public ReferenceTestDataController(AbstractNodePropertySource s) {
            super(s);
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("synthetic-access")
        public boolean setProperty(Object value) {
            if (getPoNode() instanceof IParamNodePO) {
                IParamNodePO node = (IParamNodePO)getPoNode();
                IParameterInterfacePO oldRefTestData = 
                    node.getReferencedDataCube();
                node.clearTestData();
                String valueString = (String)value;
                if (!StringUtils.isEmpty(valueString)) {
                    AbstractJBEditor activeEditor = 
                        Plugin.getDefault().getActiveJBEditor();
                    if (activeEditor == null) {
                        LOG.error(Messages
                                .ActiveJubulaEditorReferenceNotNull);
                        return false;
                    }

                    IParameterInterfacePO dataCube;
                    dataCube = TestDataCubeBP.getTestDataCubeByName(
                            valueString, 
                            GeneralStorage.getInstance().getProject());
                    if (dataCube == null) {
                        if (LOG.isInfoEnabled()) {
                            StringBuilder msg = new StringBuilder();
                            msg.append(Messages.CouldNotFindTestDataNamed)
                                .append(StringConstants.SPACE)
                                .append(StringConstants.APOSTROPHE)
                                .append(valueString)
                                .append(StringConstants.APOSTROPHE)
                                .append(StringConstants.SPACE)
                                .append(Messages.InCurrentProject);
                            LOG.info(msg.toString());
                        }
                        return false;
                    }
                    // Be sure to use the Data Cube from the editor session.
                    // Otherwise, we'll be using the Data Cube from the Master 
                    // Session, which confuses EclipseLink.
                    node.setReferencedDataCube(
                        activeEditor.getEditorHelper()
                            .getEditSupport().getSession().find(
                                    PoMaker.getTestDataCubeClass(), 
                                    dataCube.getId()));
                    if (node instanceof IExecTestCasePO) {
                        ((IExecTestCasePO)node).setHasReferencedTD(false);
                    }
                } else {
                    node.setReferencedDataCube(null);
                }

                boolean wasModified = !ObjectUtils.equals(
                        oldRefTestData, node.getReferencedDataCube());
                getPropertySource().updateParameterInputType();
                if (wasModified) {
                    DataEventDispatcher.getInstance().fireDataChangedListener(
                            node, DataState.StructureModified, 
                            UpdateState.onlyInEditor);
                }
                return wasModified;
            }
            
            return false;
        }

        
        /**
         * {@inheritDoc}
         */
        public String getProperty() {
            if (getPoNode() != null) {
                IParamNodePO node = (IParamNodePO)getPoNode();
                if (node.getReferencedDataCube() != null) {
                    getPropertySource().updateParameterInputType();
                    return node.getReferencedDataCube().getName();
                }
            }

            return StringConstants.EMPTY;
        }
        
        /**
         * {@inheritDoc}
         */
        public ParameterInputType getInputType() {
            return ParameterInputType.REFERENCE;
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean isPropertySet() {
            if (getPoNode() != null) {
                IParamNodePO node = (IParamNodePO)getPoNode();
                return node.getReferencedDataCube() != null;
            }

            return false;
        }
    }

    /**
     * Class to control parameter value.
     * @author BREDEX GmbH
     * @created 07.02.2005
     */
    public class ParameterValueController extends
        AbstractParamValueController implements IParameterPropertyController {
        /**
         * Constructor
         * @param paramDescr the Parameter description.
         * @param s AbstractNodePropertySource
         * @param paramNameMapper the param name mapper
         */
        public ParameterValueController(AbstractNodePropertySource s, 
            IParamDescriptionPO paramDescr, IParamNameMapper paramNameMapper) {
            super(s, paramDescr, paramNameMapper);
        }
        
        /**
         * {@inheritDoc}
         */
        public ParameterInputType getInputType() {
            return ParameterInputType.LOCAL;
        }
    }

    /**
     * 
     * @author BREDEX GmbH
     * @created Mar 18, 2008
     */
    public final class LockInterfaceController 
        extends AbstractPropertyController {

        /**
         * {@inheritDoc}
         */
        public Object getProperty() {
            final ISpecTestCasePO specTc = (ISpecTestCasePO)getPoNode();
            final boolean isInterfaceLocked = specTc.isInterfaceLocked();
            return isInterfaceLocked ? Messages.UtilsYes
                    : Messages.UtilsNo;
        }

        /**
         * {@inheritDoc}
         */
        public boolean setProperty(Object value) {
            return false;
        }
    }
    
    /**
     * @return the data source property descriptor
     * @param tdsc the test data souce controller to use
     */
    protected IPropertyDescriptor getDataSourcePropertyDescr(
        final SpecTestCaseTestDataSourceController tdsc) {
        ComboBoxPropertyDescriptor cbpd = new ComboBoxPropertyDescriptor(tdsc,
               P_ELEMENT_DISPLAY_DATASOURCE, tdsc.getUserChoosableDataSource());
        cbpd.setLabelProvider(new LabelProvider() {
            public String getText(Object element) {
                int dataSource = ((Integer)element).intValue();
                return I18n.getString(tdsc.getDataSourceArray()[dataSource]);
            }
        });
        cbpd.setCategory(P_TESTDATA_CAT);
        return cbpd;
    }
}
