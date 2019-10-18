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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.ReentryProperty;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.utils.StringHelper;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.controllers.propertydescriptors.IntegerTextPropertyDescriptor;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;


/**
 * @author BREDEX GmbH
 * @created 24.10.2005
 */
public class TestSuiteGUIPropertySource 
    extends AbstractNodePropertySource {

    /** Constant for the String Specification Name */
    private static final String P_SUITENAME_DISPLAY_NAME =
        Messages.TestSuiteGUIPropertySourceTestSuiteName;

    /** Constant for the String AUT Name */
    private static final String P_AUT_DISPLAY_NAME =
        Messages.TestSuiteGUIPropertySourceAUTName;
    
    /** Constant for the String relevant */
    private static final String P_RELEVANT =
        Messages.TestSuiteGUIPropertySourceRelevant;
    
    /** Constant for the String step delay */
    private static final String P_STEPDELAY_DISPLAY_NAME =
        Messages.TestSuiteGUIPropertySourceStepDelay;
    
    /** Constant for Category Event Handler */
    private static final String P_EVENTHANDLER_CAT =
        Messages.TestSuiteGUIPropertySourceEventHandler;

    /** List of aut names */
    private String[] m_autNameList = new String[0];

    /** List of auts */
    private IAUTMainPO[] m_autSubList = new IAUTMainPO[0];
    
    /** List of relevant states */
    private String[] m_relevantStatesList = {Boolean.FALSE.toString(),
            Boolean.TRUE.toString()};
    
    /** List of event handler names */
    private String[] m_reentryTypeList = new String[0];

    /** the current project */
    private IProjectPO m_project = GeneralStorage.getInstance().getProject();

    /** cached property descriptor for name */
    private IPropertyDescriptor m_namePropDesc = null;
    
    /** cached property descriptor step delay */
    private IPropertyDescriptor m_stepDelayPropDesc = null;
    
    /** cached property descriptor for aut */
    private IPropertyDescriptor m_autPropDesc = null;

    /** cached property descriptor for relevance */
    private IPropertyDescriptor m_relevancePropDesc = null;
    
    /** cached property descriptors for event handler reentry types */
    private List<IPropertyDescriptor> m_eventHandlerDescriptors =
        new ArrayList<IPropertyDescriptor>();
    
    /**
     * @param testSuite The dependened test suite.
     */
    public TestSuiteGUIPropertySource(ITestSuitePO testSuite) {
        super(testSuite);
        initAUTList();
        initPropDescriptor();
    }

    /**
     * Inits the PropertyDescriptors and adds them into super.m_propDescriptors.
     * 
     */
    @SuppressWarnings("synthetic-access")
    protected void initPropDescriptor() {
        if (!getPropertyDescriptorList().isEmpty()) {
            clearPropertyDescriptors();
        }

        // TestSuite Name
        if (m_namePropDesc == null) {
            m_namePropDesc = new TextPropertyDescriptor(
                    new ElementNameController(), P_SUITENAME_DISPLAY_NAME);
        }
        addPropertyDescriptor(m_namePropDesc);        

        // Comment
        super.initPropDescriptor();
        
        // Task ID
        if (getTaskIdPropDesc() == null) {
            setTaskIdPropDesc(new TextPropertyDescriptor(
                new TaskIdController(), 
                org.eclipse.jubula.client.ui.i18n.Messages
                    .AbstractGuiNodePropertySourceTaskId));
        }
        addPropertyDescriptor(getTaskIdPropDesc());
        
        // step delay
        if (m_stepDelayPropDesc == null) {
            m_stepDelayPropDesc = new IntegerTextPropertyDescriptor(
                    new StepDelayController(), 
                    P_STEPDELAY_DISPLAY_NAME, false, 0, 10000);
        }
        addPropertyDescriptor(m_stepDelayPropDesc);
        
        // AUT list
        addPropertyDescriptor(getAUTPropDesc());
        
        // Relevance
        addPropertyDescriptor(getRelevancePropDesc());
        
        // eventHandler
        addPropertyDescriptor(getEventHandlerDescriptors());

        initTrackedChangesPropDescriptor();
    }

    /**
     * @return The list of EventHandler descriptors.
     */
    @SuppressWarnings("synthetic-access")
    private List<IPropertyDescriptor> getEventHandlerDescriptors() {
        if (m_eventHandlerDescriptors.isEmpty()) {
            ComboBoxPropertyDescriptor cbpd;
            ITestSuitePO testSuitePO = (ITestSuitePO)getPoNode();
            m_reentryTypeList = 
                new String[ReentryProperty.TS_REENTRY_PROP_ARRAY.length];
            for (int i = 0; i < ReentryProperty.TS_REENTRY_PROP_ARRAY.length; 
                    i++) {
                m_reentryTypeList[i] = 
                    ReentryProperty.TS_REENTRY_PROP_ARRAY[i].toString();
            }
            for (String key : testSuitePO.getDefaultEventHandler().keySet()) {
                cbpd = new ComboBoxPropertyDescriptor(
                        new EventHandlerController(), I18n.getString(key), 
                        m_reentryTypeList);
                cbpd.setCategory(P_EVENTHANDLER_CAT);
                m_eventHandlerDescriptors.add(cbpd);
            }
        }
        return m_eventHandlerDescriptors;
    }
    
    /**
     * @return the relevance property descriptor.
     */
    @SuppressWarnings("synthetic-access")
    private IPropertyDescriptor getRelevancePropDesc() {
        if (m_relevancePropDesc == null) {
            ComboBoxPropertyDescriptor cbpd = new ComboBoxPropertyDescriptor(
                    new RelevanceController(), P_RELEVANT,
                    m_relevantStatesList);
            cbpd.setLabelProvider(new LabelProvider() {
                public String getText(Object element) {
                    if (element instanceof Integer) {
                        if (m_relevantStatesList.length == 0
                                || ((Integer)element).intValue() == -1) {
                            return StringConstants.EMPTY;
                        }
                        return m_relevantStatesList[((Integer)element)];
                    }
                    Assert.notReached(Messages.WrongAUT + StringConstants.DOT);
                    return String.valueOf(element);
                }
            });
            m_relevancePropDesc = cbpd;
        }
        return m_relevancePropDesc;
    }

    /**
     * @return the AUT property descriptor.
     */
    @SuppressWarnings("synthetic-access")
    private IPropertyDescriptor getAUTPropDesc() {
        if (m_autPropDesc == null) {
            ComboBoxPropertyDescriptor cbpd = new ComboBoxPropertyDescriptor(
                    new AUTController(), P_AUT_DISPLAY_NAME, m_autNameList);
            cbpd.setLabelProvider(new LabelProvider() {
                public String getText(Object element) {
                    if (element instanceof Integer) {
                        if (m_autNameList.length == 0 
                                || ((Integer)element).intValue() == -1) {
                            return StringConstants.EMPTY;
                        }
                        return m_autNameList[((Integer)element)];
                    }
                    Assert.notReached(Messages.WrongAUT + StringConstants.DOT);
                    return String.valueOf(element);
                }
            });
            m_autPropDesc = cbpd;
        }
        return m_autPropDesc;
    }

    /**
     * Inits the List of AUTs. 
     */
    private void initAUTList() {
        List<String> tmpList = new ArrayList<String>();
        List<IAUTMainPO> tmpSubList = new ArrayList<IAUTMainPO>();
        Map<String, IAUTMainPO> helpMap = new HashMap<String, IAUTMainPO>();
        if (m_project == null) {
            return;
        }
        Set<IAUTMainPO> autMainList = m_project.getAutMainList();
        if (autMainList == null || autMainList.size() == 0) {
            return;
        }
        for (IAUTMainPO autW : autMainList) {
            tmpList.add(autW.getName());
            helpMap.put(autW.getName(), autW);
        }
        m_autNameList = tmpList.toArray(new String[tmpList.size()]);
        Arrays.sort(m_autNameList);
        for (String autName : m_autNameList) {
            tmpSubList.add(helpMap.get(autName));
        }
        m_autSubList = tmpSubList.toArray(new IAUTMainPO[tmpSubList.size()]);
    }
    
    /**
     * @param object The original object.
     * @return The work version of the original object.
     */
    IPersistentObject getWorkVersion(IPersistentObject object) {
        AbstractJBEditor edit = Plugin.getDefault().getActiveJBEditor();
        EditSupport editSupport = edit.getEditorHelper().getEditSupport();
        IPersistentObject objectW = null;
        try {
            objectW = editSupport.createWorkVersion(object);
        } catch (PMException e) {
            PMExceptionHandler.handlePMExceptionForMasterSession(e);
        } 
        return objectW;       
    }

    /**
     * Class to control the AUT list. 
     * @author BREDEX GmbH
     * @created 24.10.2005
     */
    private class AUTController extends AbstractPropertyController {

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("synthetic-access")
        public boolean setProperty(Object value) {
            if (value == null) {
                return false;
            }
            ITestSuitePO testSuite = (ITestSuitePO)getPoNode();
            IAUTMainPO aut = m_autSubList[((Integer)value).intValue()];
            IAUTMainPO autW = (IAUTMainPO)getWorkVersion(aut);
            testSuite.setAut(autW);
            return true;
        }

        /**
         * {@inheritDoc}
         */
        public Object getProperty() {
            return getIndexOfAUTs();
        }

        /**
         * {@inheritDoc}
         */
        public Image getImage() {
            return DEFAULT_IMAGE;
        }

        /**
         * Returns the index of the type from the String-Array for the
         * ComboBoxPropertyDescriptor. 
         * @return an <code>Integer</code> value. The index.
         */
        @SuppressWarnings("synthetic-access")
        private Integer getIndexOfAUTs() {
            ITestSuitePO testSuite = (ITestSuitePO)getPoNode();
            IAUTMainPO aut = testSuite.getAut();
            int autListLength = m_autSubList.length;
            for (int i = 0; i < autListLength; i++) {
                if (m_autSubList[i].equals(aut)) {
                    return Integer.valueOf(i);
                }
            }
            return Integer.valueOf(-1);
        }
    }
    
    /**
     * Class to control the list containing valid relevance states. 
     * @author BREDEX GmbH
     * @created 24.10.2005
     */
    private class RelevanceController extends AbstractPropertyController {

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("synthetic-access")
        public boolean setProperty(Object value) {
            if (value == null) {
                return false;
            }
            ITestSuitePO testSuite = (ITestSuitePO)getPoNode();
            int relevant = ((Integer)value).intValue();
            testSuite.setRelevant(
                    Boolean.valueOf(m_relevantStatesList[relevant]));
            return true;
        }

        /**
         * {@inheritDoc}
         */
        public Object getProperty() {
            return getIndexOfState();
        }

        /**
         * {@inheritDoc}
         */
        public Image getImage() {
            return DEFAULT_IMAGE;
        }

        /**
         * Returns the index of the type from the String-Array for the
         * ComboBoxPropertyDescriptor.
         * @return an <code>Integer</code> value. The index.
         */
        @SuppressWarnings("synthetic-access")
        private Integer getIndexOfState() {
            ITestSuitePO testSuite = (ITestSuitePO)getPoNode();
            Boolean relevant = testSuite.getRelevant();
            for (int i = 0; i < m_relevantStatesList.length; i++) {
                if (Boolean.valueOf(m_relevantStatesList[i])
                        .equals(relevant)) {
                    return Integer.valueOf(i);
                }
            }
            return Integer.valueOf(-1);
        }
    }
    
    /**
     * Class to control the step delay. 
     * @author BREDEX GmbH
     * @created 24.10.2005
     */
    private class StepDelayController extends AbstractPropertyController {

        /**
         * {@inheritDoc}
         */
        public boolean setProperty(Object value) {
            ITestSuitePO testSuite = (ITestSuitePO)getPoNode();
            if (value == null || StringConstants.EMPTY.equals(value)) {
                testSuite.setStepDelay(-1); // empty step delay
            } else {
                try {
                    final int stepDelay = Integer.parseInt(
                        String.valueOf(value));
                    if (stepDelay >= 0) {
                        testSuite.setStepDelay(stepDelay);
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return true;
        }

        /**
         * {@inheritDoc}
         */
        public Object getProperty() {
            ITestSuitePO testSuite = (ITestSuitePO)getPoNode();
            if (testSuite.getStepDelay() == -1) { // empty step delay
                return StringConstants.EMPTY;
            }
            return String.valueOf(testSuite.getStepDelay());
        }
    }
    
    /**
     * Class to control the default event handler. 
     * @author BREDEX GmbH
     * @created 24.10.2005
     */
    private class EventHandlerController extends AbstractPropertyController {

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("synthetic-access")
        public boolean setProperty(Object value) {
            if (value == null) {
                return false;
            }
            ITestSuitePO testSuite = (ITestSuitePO)getPoNode();
            try {
                Integer reentryType = ReentryProperty.getProperty(
                    m_reentryTypeList[((Integer)value).intValue()]);
                final IPropertyDescriptor propertyDescriptor = 
                    getPropertyDescriptorById(this);
                if (propertyDescriptor == null) {
                    return false;
                }
                String eventType = StringHelper.getInstance().getMap().get(
                        propertyDescriptor.getDisplayName());
                testSuite.getDefaultEventHandler().put(eventType, reentryType);
            } catch (InvalidDataException e) {
                // do nothing
            }

            return true;
        }

        /**
         * {@inheritDoc}
         */
        public Object getProperty() {
            return getIndexOfEHs();
        }
        
        /**
         * Returns the index of the type from the String-Array for the
         * ComboBoxPropertyDescriptor. 
         * @return an <code>Integer</code> value. The index.
         */
        @SuppressWarnings("synthetic-access")
        private Integer getIndexOfEHs() {
            ITestSuitePO testSuite = (ITestSuitePO)getPoNode();
            final IPropertyDescriptor propertyDescriptor = 
                getPropertyDescriptorById(this);
            if (propertyDescriptor == null) {
                return Integer.valueOf(-1);
            }
            String eventType = StringHelper.getInstance().getMap().get(
                    propertyDescriptor.getDisplayName());
            Integer reentryNumber = testSuite.getDefaultEventHandler()
                .get(eventType);
            try {
                String reentryType = ReentryProperty.getProperty(reentryNumber)
                    .toString();
                int reentryTypeListLength = m_reentryTypeList.length;
                for (int i = 0; i < reentryTypeListLength; i++) {
                    if (m_reentryTypeList[i].equals(reentryType)) {
                        return Integer.valueOf(i);
                    }
                }
            } catch (InvalidDataException e) {
                // do nothing
            }
            return Integer.valueOf(-1);
        }

        /**
         * {@inheritDoc}
         */
        public Image getImage() {
            return DEFAULT_IMAGE;
        }
    }
}