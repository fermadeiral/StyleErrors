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
package org.eclipse.jubula.client.ui.controllers.propertysources;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.persistence.MonitoringValuePM;
import org.eclipse.jubula.tools.internal.constants.MonitoringConstants;
import org.eclipse.jubula.tools.internal.objects.IMonitoringValue;
import org.eclipse.jubula.tools.internal.objects.MonitoringValue;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * This class loads the monitoring values that will be shown by the
 * PropertiesView
 * 
 * @author BREDEX GmbH
 * @created 05.12.2010
 */
public class MonitoringValuePropertySource implements IPropertySource {

    /** the cached property descriptors */
    private IPropertyDescriptor[] m_descriptors = null;

    /** found MonitoringValues */
    private Map<String, IMonitoringValue> m_monitoringValueMap = null;

    /**
     * @param summary
     *            the Summary which was selected
     */
    public MonitoringValuePropertySource(ITestResultSummaryPO summary) {
        Long id = summary.getId();
        m_monitoringValueMap = MonitoringValuePM.loadMonitoringValues(id);
    }

    /**
     * {@inheritDoc}
     */
    public Object getEditableValue() {
        return "editableValue"; //$NON-NLS-1$
    }

    /**
     * @return Each {@link PropertyDescriptor} will be renderd as an entry of
     *         PropertiesView
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {

        List<IPropertyDescriptor> tmpList = 
            new LinkedList<IPropertyDescriptor>();
        Iterator it = m_monitoringValueMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            MonitoringValue tmp = (MonitoringValue)pairs.getValue();
            PropertyDescriptor p = new PropertyDescriptor(pairs.getKey(),
                    (String)pairs.getKey());
            if (!tmp.getCategory().equals(MonitoringConstants.NO_CATEGORY)) {
                p.setCategory(tmp.getCategory());
            }
            tmpList.add(p);

        }
        m_descriptors = tmpList
                .toArray(new IPropertyDescriptor[tmpList.size()]);
        return m_descriptors;
    }

    /**
     * @return displays the value of the given object id, default is "empty"
     * @param id
     *            the id
     */
    public Object getPropertyValue(Object id) {

        IMonitoringValue m = m_monitoringValueMap.get(id);
        if (m.getType().equals(MonitoringConstants.PERCENT_VALUE)) {
            DecimalFormat n = new DecimalFormat("0.0#%"); //$NON-NLS-1$
            Double doubleValue = Double.valueOf(m.getValue());
            return StringUtils
                    .defaultString(n.format(doubleValue.doubleValue()));
        }
        return m.getValue();

    }

    /**
     * {@inheritDoc}
     */
    public boolean isPropertySet(Object arg0) {
        // Do nothing
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void resetPropertyValue(Object arg0) {

        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void setPropertyValue(Object arg0, Object arg1) {
        // Do nothing

    }
}
