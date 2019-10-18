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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.labelprovider.DisabledLabelProvider;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;


/**
 * @author BREDEX GmbH
 * @created Mar 16, 2010
 */
public class RefTestSuiteGUIPropertySource 
        extends AbstractNodePropertySource {
    /** Constant for the String reference Name */
    private static final String P_REF_TS_DISPLAY_NAME = 
        Messages.RefTestSuiteGUIPropertySourceRefTSName;

    /** Constant for the String Specification Name */
    private static final String P_SPEC_TS_DISPLAY_NAME =
            Messages.RefTestSuiteGUIPropertySourceSpecTSName;

    /**
     * <code>P_AUT_ID_DISPLAY_NAME</code>
     */
    private static final String P_AUT_ID_DISPLAY_NAME =
        Messages.RefTestSuiteGUIPropertySourceAutIdName;

    /** List of aut names */
    private String[] m_autIdList = new String[0];

    /** cached property descriptor for AUT ID */
    private IPropertyDescriptor m_autIdPropDesc = null;

    /** cached property descriptor for name */
    private IPropertyDescriptor m_namePropDesc = null;

    /** cached property descriptor for name */
    private IPropertyDescriptor m_specNameDesc = null;

    /**
     * @param reftestsuiteGui
     *            the test job gui node
     */
    public RefTestSuiteGUIPropertySource(IRefTestSuitePO reftestsuiteGui) {
        super(reftestsuiteGui);
        fillAutIdList(reftestsuiteGui);
    }

    /**
     * @param refTestSuite
     *            the Test Suite Reference
     */
    private void fillAutIdList(IRefTestSuitePO refTestSuite) {
        Set<String> idSet = new HashSet<String>();
        ITestSuitePO ts = refTestSuite.getTestSuite();
        IAUTMainPO aut = ts.getAut();
        if (aut != null) {
            idSet.addAll(aut.getAutIds());
            for (IAUTConfigPO conf : aut.getAutConfigSet()) {
                idSet.add(conf.getValue(AutConfigConstants.AUT_ID,
                        StringUtils.EMPTY));
            }
        }
        List<String> idList = new ArrayList<String>(idSet);
        Collections.sort(idList);
        m_autIdList = idList.toArray(new String[idList.size()]);
    }

    /**
     * Inits the PropertyDescriptors and adds them into super.m_propDescriptors.
     */
    protected void initPropDescriptor() {
        if (!getPropertyDescriptorList().isEmpty()) {
            clearPropertyDescriptors();
        }
        
        if (m_namePropDesc == null) {
            m_namePropDesc = new TextPropertyDescriptor(
                    new ExecNameController(), P_REF_TS_DISPLAY_NAME);
        }
        addPropertyDescriptor(m_namePropDesc);
        
        if (m_specNameDesc == null) {
            PropertyDescriptor specNameDesc = new PropertyDescriptor(
                    new SpecNameController(), P_SPEC_TS_DISPLAY_NAME);
            specNameDesc.setLabelProvider(new DisabledLabelProvider());
            m_specNameDesc = specNameDesc;
        }
        addPropertyDescriptor(m_specNameDesc);
        super.initPropDescriptor();
        
        // Task ID
        if (getTaskIdPropDesc() == null) {
            PropertyDescriptor taskIdPropDesc = new PropertyDescriptor(
                new ReadOnlyTaskIdController(),
                org.eclipse.jubula.client.ui.i18n.Messages
                    .AbstractGuiNodePropertySourceTaskId);
            taskIdPropDesc.setLabelProvider(new DisabledLabelProvider());
            setTaskIdPropDesc(taskIdPropDesc);
        }
        addPropertyDescriptor(getTaskIdPropDesc());
        
        // AUT id list
        addPropertyDescriptor(getAutIdPropDesc());
    }

    /**
     * @return the AUT id property descriptor.
     */
    @SuppressWarnings("synthetic-access")
    private IPropertyDescriptor getAutIdPropDesc() {
        if (m_autIdPropDesc == null) {
            ComboBoxPropertyDescriptor cbpd = new ComboBoxPropertyDescriptor(
                    new AutIdController(), P_AUT_ID_DISPLAY_NAME, m_autIdList);
            cbpd.setLabelProvider(new LabelProvider() {
                public String getText(Object element) {
                    if (element instanceof Integer) {
                        if (m_autIdList.length == 0
                                || ((Integer)element).intValue() == -1) {
                            return StringConstants.EMPTY;
                        }
                        return m_autIdList[((Integer)element).intValue()];
                    }
                    Assert.notReached(Messages.WrongTypeOfElement 
                            + StringConstants.DOT);
                    return String.valueOf(element);
                }
            });
            
            m_autIdPropDesc = cbpd;
        }
        
        return m_autIdPropDesc;
    }

    /**
     * Class to control the AUT id list.
     *
     * @author BREDEX GmbH
     * @created Mar 19, 2010
     */
    private class AutIdController extends AbstractPropertyController {
        /** {@inheritDoc} */
        @SuppressWarnings("synthetic-access")
        public boolean setProperty(Object value) {
            IRefTestSuitePO refTestSuite = (IRefTestSuitePO)getPoNode();
            if (!(value instanceof String)) {
                if (value instanceof Integer) {
                    int pos = ((Integer)value).intValue();
                    if (m_autIdList.length > pos && pos >= 0) {
                        refTestSuite.setTestSuiteAutID(m_autIdList[pos]);
                        return true;
                    }
                } else {
                    return false;
                }
            }
            refTestSuite.setTestSuiteAutID((String)value);
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("synthetic-access")
        public Object getProperty() {
            IRefTestSuitePO refTestSuite = (IRefTestSuitePO)getPoNode();
            String autID = refTestSuite.getTestSuiteAutID();
            for (int i = 0; i < m_autIdList.length; i++) {
                if (m_autIdList[i].equals(autID)) {
                    return Integer.valueOf(i);
                }
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
    
    /**
     * Class to control the name of the depending SpecTestCasePO.
     * @author BREDEX GmbH
     * @created 23.02.2005
     */
    protected class SpecNameController extends AbstractPropertyController {

        /**
         * {@inheritDoc}
         */
        public boolean setProperty(Object value) {
            // do nothing, read only
            return true;
        }                
        /**
         * {@inheritDoc}
         */
        public Object getProperty() {
            IRefTestSuitePO exTc = (IRefTestSuitePO) getPoNode();
            if (exTc.getTestSuite() != null) {
                return exTc.getTestSuite().getName();
            }
            return StringConstants.EMPTY;
        }
        
        /**
         * {@inheritDoc}
         */
        public Image getImage() {
            return READONLY_IMAGE;
        }
    }
    
    /**
     * Class to control the name. 
     * @author BREDEX GmbH
     * @created 23.02.2005
     */
    protected class ExecNameController extends ElementNameController {

        /**
         * {@inheritDoc}
         */
        public Object getProperty() {
            if (getPoNode() != null) {
                IRefTestSuitePO refTs = (IRefTestSuitePO) getPoNode();
                String name = refTs.getRealName();
                if (name != null) {
                    return name;
                }
            }
            return StringConstants.EMPTY;
        }
    }
}
