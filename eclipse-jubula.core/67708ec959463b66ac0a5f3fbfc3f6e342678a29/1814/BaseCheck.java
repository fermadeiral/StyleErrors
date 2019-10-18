/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.teststyle.checks;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.model.ICheckConfPO;
import org.eclipse.jubula.client.teststyle.ExtensionHelper;
import org.eclipse.jubula.client.teststyle.checks.contexts.BaseContext;
import org.eclipse.jubula.client.teststyle.exceptions.AttributeNotFoundException;
import org.eclipse.jubula.client.teststyle.quickfix.Quickfix;
import org.eclipse.jubula.client.teststyle.quickfix.QuickfixFactory;
import org.eclipse.jubula.tools.internal.constants.StringConstants;


/**
 * Class used for implementing new checks.
 * 
 * @author marcell
 * 
 */
public abstract class BaseCheck implements Cloneable {

    /** name of the check */
    private String m_name;
    /** id of the check */
    private String m_id;
    
    /** description of this check, empty if not set */
    private String m_fulltextDescription = StringUtils.EMPTY; 
    
    /** configuration object */
    private ICheckConfPO m_conf = new CheckConfMock(); 
    
    /** map of attribute description */
    private Map<String, String> m_attrDescription = 
        new HashMap<String, String>();
    
    /**
     * @return the severity
     */
    public final Severity getSeverity() {
        return Severity.valueOf(m_conf.getSeverity());
    }
    
    /**
     * @return the description of this check
     */
    public final String getFulltextDescription() {
        return m_fulltextDescription;
    }
    
    /**
     * @param tmp copies the copy field so that i'm holding the bush right now.
     */
    public final void setFulltextDescription(String tmp) {
        m_fulltextDescription = tmp;
    }

    /**
     * @param severity
     *            the severity to set
     */
    public final void setSeverity(Severity severity) {
        m_conf.setSeverity(severity.toString());
    }
    
    /**
     * 
     * @param key name of the attribute
     * @param value description of the attribute
     */
    public final void addDescriptionForAttribute(String key, String value) {
        m_attrDescription.put(key, value);
    }
    
    /**
     * @param key the name of the attribute
     * @return the description of the attribute
     */
    public final String getDescriptionForAttribute(String key) {
        return m_attrDescription.get(key);
    }

    /**
     * 
     * @return The name of the check
     */
    public final String getName() {
        return m_name;
    }

    /**
     * 
     * @param name
     *            The new name of the chek
     */
    public final void setName(String name) {
        m_name = name;
    }

    /**
     * @return the id
     */
    public final String getId() {
        return m_id;
    }

    /**
     * @param id
     *            the id to set
     */
    public final void setId(String id) {
        this.m_id = id;
    }

    /**
     * @return the contexts
     */
    public final Map<BaseContext, Boolean> getContexts() {
        Map<BaseContext, Boolean> tmp = new HashMap<BaseContext, Boolean>();
        for (Entry<String, Boolean> e : m_conf.getContexts().entrySet()) {
                        
            BaseContext context = BaseContext.getFor(e.getKey());
            // Very strange workaround
            boolean value = true;
            if (new BigDecimal(1).equals(e.getValue())) {
                value = true;
            } else if (new BigDecimal(0).equals(e.getValue())) {
                value = false;
            } else {
                value = e.getValue();
            }
            // workaround end
            tmp.put(context, value);
        }
        return tmp;
    }

    /**
     * @param contexts
     *            the contexts to set
     */
    public final void setContexts(Map<BaseContext, Boolean> contexts) {
        Map<String, Boolean> tmp = new HashMap<String, Boolean>();
        for (Entry<BaseContext, Boolean> e : contexts.entrySet()) {
            tmp.put(e.getKey().getClass().getSimpleName(), e.getValue());
        }
        m_conf.setContexts(tmp);
    }

    /**
     * Will be used to set the check active - checkstyle will only use this
     * check if the active variable is true. The properties view handles the
     * active setting.
     * 
     * @param active
     *            the new value. When true, it sets the check active
     */
    public final void setActive(boolean active) {
        m_conf.setActive(active);
    }

    /**
     * 
     * @param active
     *            Value if it should be active.
     * @param context
     *            context where it should be triggered
     */
    public final void setActive(boolean active, BaseContext context) {
        m_conf.getContexts().put(context.getClass().getSimpleName(), active);
    }

    /**
     * Will be used to determine if the definition should be tested or not.
     * 
     * @return true, when the check is active, false when not.
     */
    public final boolean isActive() {
        return m_conf.isActive();
    }

    /**
     * 
     * @param context
     *            The context where it should be looked.
     * @return is the check active for this context?
     */
    public final boolean isActive(BaseContext context) {
        return isActive() && getContexts().get(context);
    }

    /**
     * This method should give back the reason why the check has failed.
     * 
     * @return the reason why the check has failed
     */
    public abstract String getDescription();

    /**
     * This method implements the definition of the check. The concrete
     * implementation differs greatly on the object and on the definiton of the
     * check.
     * 
     * @param obj
     *            the data that should be checked
     * @return true if the data has errors
     */
    public abstract boolean hasError(Object obj);

    /**
     * This method will create the quickfixes for this. The default
     * implementation just opens the object in the editor, if its available for
     * editing. When overwritten it should create a good quickfix for the check
     * that works.
     * 
     * @param obj
     *            The Object in which context a quickfix should be created.
     * @return A list of possible quickfixes of this Check. Returns null when
     *         there's no good quickfix available for the check.
     */
    public Quickfix[] getQuickfix(Object obj) {
        return QuickfixFactory.getDefaultQuickfixFor(obj);
    }

    /**
     * This method represents the suffix that will be expanded to the node, that
     * violates a check. If you want to have a suffix, just overwrite this
     * method. It returns an empty string by default.
     * 
     * @param obj
     *            The object which will be decorated and eventually get an
     *            suffix.
     * @return The suffix that this object should become.
     */
    public String getSuffix(Object obj) {
        return StringConstants.EMPTY;
    }
    
    /**
     * This method represents the suffix that will be expanded to the node, that
     * violates a check. If you want to have a prefix, just overwrite this
     * method. It returns an empty string by default.
     * 
     * @param obj
     *            The object which will be decorated and eventually get an
     *            prefix.
     * @return The prefix that this object should become.
     */
    public String getPrefix(Object obj) {
        return StringConstants.EMPTY;
    }

    /**
     * hasError on a couple of objects.
     * 
     * @param objs
     *            The list of the objects which should be checked.
     * @return A list with objects that violated the check.
     */
    public final List<Object> hasError(List<Object> objs) {
        ArrayList<Object> objsWithError = new ArrayList<Object>();
        for (Object obj : objs) {
            if (hasError(obj)) {
                objsWithError.add(obj);
            }
        }
        return objsWithError;
    }

    /**
     * 
     * @param name
     *            Name of the attribute.
     * @return The value of this attribute.
     * @throws AttributeNotFoundException
     *             Will be thrown when the attribute is not found.
     */
    public final String getAttributeValue(String name)
        throws AttributeNotFoundException {
        if (!m_conf.getAttr().containsKey(name)) {
            throw new AttributeNotFoundException(name);
        }
        return m_conf.getAttr().get(name);
    }
    
    /**
     * 
     * @param name
     *            Name of the attribute.
     * @return The default value of this attribute defined in the extension 
     *          point
     * @throws AttributeNotFoundException
     *             Will be thrown when the attribute is not found.
     */
    public final String getDefaultAttributeValue(String name)
        throws AttributeNotFoundException {
        Map<String, String> attr = 
            ExtensionHelper.getDefaults().get(getId()).getAttr();
        if (!attr.containsKey(name)) {
            throw new AttributeNotFoundException(name);
        }
        return attr.get(name);
    }



    /**
     * Adds an attribute to the check.
     * 
     * @param name
     *            Name of the attribute.
     * @param value
     *            Default value of the attribute.
     */
    public final void setAttributeValue(String name, String value) {
        m_conf.getAttr().put(name, value);
    }

    /**
     * @return the attributes
     */
    public final Map<String, String> getAttributes() {
        return m_conf.getAttr();
    }

    /**
     * @param attributes
     *            the attributes to set
     */
    public final void setAttributes(Map<String, String> attributes) {
        m_conf.setAttr(attributes);
    }

    /**
     * {@inheritDoc}
     */
    public BaseCheck clone() {
        BaseCheck tmp = null;
        try {
            tmp = (BaseCheck)super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        
        tmp.setConf(new CheckConfMock());
        tmp.setAttributes(new HashMap<String, String>(this.getAttributes()));
        tmp.setActive(this.isActive());
        tmp.setContexts(new HashMap<BaseContext, Boolean>(this.getContexts()));
        tmp.setSeverity(this.getSeverity());
        tmp.setFulltextDescription(this.getFulltextDescription());
        
        return tmp;
    }
    
    /**
     * @param conf the conf to set
     */
    public void setConf(ICheckConfPO conf) {
        m_conf = conf;
    }
    
    /**
     * @return the conf
     */
    public ICheckConfPO getConf() {
        return m_conf;
    }

    /** 
     * @return the description map
     */
    public Map<String, String> getDescriptions() {
        return m_attrDescription;
    }

    /**
     * @param attributeName the name of the attribute
     * @return The quantity defined by the attribute or the default if the
     *         defined value is not a valid number.
     */
    protected int getIntegerAttributeValue(String attributeName) {
        try {
            return Integer.parseInt(getAttributeValue(attributeName));
        } catch (NumberFormatException e) {
            return Integer.parseInt(getDefaultAttributeValue(attributeName));
        }
    }
}
