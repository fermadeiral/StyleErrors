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
package org.eclipse.jubula.client.teststyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jubula.client.core.model.ICheckConfContPO;
import org.eclipse.jubula.client.core.model.ICheckConfPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.teststyle.analyze.Analyzer;
import org.eclipse.jubula.client.teststyle.analyze.AnalyzerContainer;
import org.eclipse.jubula.client.teststyle.checks.BaseCheck;
import org.eclipse.jubula.client.teststyle.checks.Category;
import org.eclipse.jubula.client.teststyle.checks.CheckCont;
import org.eclipse.jubula.client.teststyle.checks.DecoratingCheck;
import org.eclipse.jubula.client.teststyle.checks.Severity;
import org.eclipse.jubula.client.teststyle.checks.contexts.BaseContext;
import org.eclipse.jubula.client.teststyle.constants.Ext;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Static methods for handling my extension point.
 * 
 * @author marcell
 * 
 */
public class ExtensionHelper {

    /** logger, logger, on the wall... */
    private static final Logger LOG = LoggerFactory
            .getLogger(ExtensionHelper.class);
    
    /** default configurations */
    private static Map<String, ICheckConfPO> defaults = 
        new HashMap<String, ICheckConfPO>();

    /** private constructor for utility class */
    private ExtensionHelper() {
    // Go along, here's nothing to see.
    }
    
    /**
     * @return the defaults of the checks, which were defined by the extension
     *         point
     */
    public static Map<String, ICheckConfPO> getDefaults() {
        return defaults;
    }

    /**
     * Handles the extensions from my defined extension point.
     * 
     * Initialize the categories by adding them and their checks to a list. Will
     * be called when the plugin starts, so it gathers all checks that are
     * relevant to the process.
     */
    public static void initChecks() {
        for (IExtension extension : getExtensions()) {
            for (IConfigurationElement category : extension
                    .getConfigurationElements()) {
                if (Ext.CAT.equals(category.getName())) {
                    handleCategory(category);
                }
            }
        }
    }
    
    /**
     * Initializes analyzers in the analyzecontainer.
     */
    public static void initAnalyzes() {
        for (IExtension extension : getExtensions()) {
            for (IConfigurationElement analyzer : extension
                    .getConfigurationElements()) {
                if (Ext.ANALYZER.equals(analyzer.getName())) {
                    handleAnalyzer(analyzer);
                }
            }
        }
    }

    /**
     * @param analyzerCfg The analyzer that should be handled.
     */
    private static void handleAnalyzer(IConfigurationElement analyzerCfg) {
        String analyzerName = analyzerCfg.getAttribute(Ext.ANALYZER_NAME);
        Analyzer analyzer = null;
        try {
            Object obj = 
                analyzerCfg.createExecutableExtension(Ext.ANALYZER_CLASS);
            analyzer = (Analyzer) obj;
        } catch (CoreException e) {
            LOG.error(Ext.CORE_EXCEPTION, e);
            return; // If theres an error, return;
        }
        
        analyzer.setName(analyzerName);
        IConfigurationElement[] contextCfgs = 
            analyzerCfg.getChildren(Ext.ANALYZER_CONTEXTS)[0].getChildren();
        for (BaseContext context : handleContexts(contextCfgs)) {
            AnalyzerContainer.add(context, analyzer);
        }
        
        
    }

    /**
     * 
     * @return All extensions of my extension points.
     */
    private static IExtension[] getExtensions() {
        IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
        IExtensionPoint extensionPoint = extensionRegistry
                .getExtensionPoint(Ext.DEFINE_ID);
        return extensionPoint.getExtensions();
    }

    /**
     * Handles the category from an extension and changed the CheckCont from the
     * parameter.
     * 
     * @param category
     *            The category that should be handled
     */
    private static void handleCategory(IConfigurationElement category) {
        BaseContext[] contextsArray;

        // First we create the category
        String catName = category.getAttribute(Ext.CAT_NAME);
        String catDesc = category.getAttribute(Ext.CAT_DESCR);
        Category cat = new Category(catName);
        cat.setDescription(catDesc);

        for (IConfigurationElement chkCfg : category.getChildren()) {
            // Create the check
            BaseCheck chk = handleCheck(chkCfg);
            if (chk == null) {
                continue; // If theres an error, continue with the next cfg
            }

            // Add it to the category
            cat.addCheck(chk);

            // We handle all attributes of this check
            handleAttributes(chkCfg.getChildren(Ext.CHK_ATTR)[0].getChildren(),
                    chk);

            // Adds the check with its relations to the container
            if (chk instanceof DecoratingCheck) {

                // Then we look for the contexts of the check
                List<BaseContext> contexts = handleContexts(chkCfg
                        .getChildren(Ext.CHK_DECCONT)[0].getChildren());
                contextsArray = contexts.toArray(new BaseContext[contexts
                        .size()]);

                // Now we set them active in the check
                for (BaseContext context : contexts) {
                    chk.setActive(true, context);
                }

                DecoratingCheck decChk = (DecoratingCheck)chk;
                CheckCont.add(decChk, cat, contextsArray);
            } else {

                // Then we look for the contexts of the check
                List<BaseContext> contexts = handleContexts(chkCfg
                        .getChildren(Ext.CHK_CONT)[0].getChildren());
                contextsArray = contexts.toArray(new BaseContext[contexts
                        .size()]);

                // Now we set them active in the check
                for (BaseContext context : contexts) {
                    chk.setActive(true, context);
                }

                CheckCont.add(chk, cat, contextsArray);
            }
        }
    }

    /**
     * Initializes the configurations for the checks.
     */
    static void initCheckConfiguration() {
        Persistor per = Persistor.instance();
        EntityManager s = per.openSession();
        EntityTransaction tx = per.getTransaction(s);
        IProjectPO project = GeneralStorage.getInstance().getProject();
        
        ICheckConfContPO cfg = 
            s.merge(project.getProjectProperties().getCheckConfCont());
        
        for (BaseCheck chk : new HashSet<BaseCheck>(CheckCont.getAll())) {
            ICheckConfPO chkPO = cfg.getCheckConf(chk.getId());
            ICheckConfPO defaultCfg = defaults.get(chk.getId());
            if (chkPO == null) {
                chkPO = cfg.createCheckConf();
                s.persist(chkPO);
                
                chkPO.setSeverity(defaultCfg.getSeverity());
                chkPO.setActive(defaultCfg.isActive());
                chkPO.setAttr(defaultCfg.getAttr());
                chkPO.setContexts(
                    new HashMap<String, Boolean>(defaultCfg.getContexts()));
                
                
                cfg.addCheckConf(chk.getId(), chkPO);
            } else {
                // Synchronize contexts
                Map<String, Boolean> defaultCont = defaultCfg.getContexts();
                Map<String, Boolean> chkCont = chkPO.getContexts();
                for (Entry<String, Boolean> cont : defaultCont.entrySet()) {
                    if (!chkCont.containsKey(cont.getKey())) {
                        chkCont.put(cont.getKey(), cont.getValue());
                    }
                }
                
                // Synchronize attributes
                Map<String, String> defaultAttr = defaultCfg.getAttr();
                Map<String, String> chkAttr = chkPO.getAttr();
                for (Entry<String, String> attr : defaultAttr.entrySet()) {
                    if (!chkAttr.containsKey(attr.getKey())) {
                        chkAttr.put(attr.getKey(), attr.getValue());
                    }
                }
            }
            chk.setConf(chkPO);
        }
        
        try {
            per.commitTransaction(s, tx);
        } catch (Exception e) {
            LOG.error(Ext.EXCEPTION, e);
        } finally {
            per.dropSession(s);
        }
    }

    /**
     * Handles the checks and returns them.
     * 
     * @param chkCfg
     *            The configuration which contains the check
     * @return The check from this configuration element.
     */
    public static BaseCheck handleCheck(IConfigurationElement chkCfg) {
        BaseCheck chk = null;
        try {
            chk = (BaseCheck)chkCfg.createExecutableExtension(Ext.CHK_CLASS);
        } catch (CoreException e) {
            LOG.error(Ext.CORE_EXCEPTION, e);
            return null; // If theres an error, return null;
        }

        if (Boolean.TRUE.toString().equals(
                chkCfg.getAttribute(Ext.CHK_ACTIVE))) { 
            chk.setActive(true);
        } else {
            chk.setActive(false);
        }
        
        // Add the right id to the check
        chk.setId(chkCfg.getAttribute(Ext.CHK_ID));

        // Add the right name to the check
        chk.setName(chkCfg.getAttribute(Ext.CHK_NAME));
        
        chk.setFulltextDescription(chkCfg.getAttribute(Ext.CHK_DSCR));

        // Sets the right default severity
        String sevStr = chkCfg.getAttribute(Ext.CHK_SEVERITY);
        if (sevStr != null) {
            chk.setSeverity(Severity.valueOf(sevStr));
        }

        defaults.put(chk.getId(), chk.getConf());
        
        return chk;
    }

    /**
     * Handles the contexts of one check. I'm really sorry for this code. It's a
     * lot of reflection magic.
     * 
     * @param confCont
     *            The check configuration element.
     * @return The list of the contexts.
     */
    private static List<BaseContext> handleContexts(
            IConfigurationElement[] confCont) {
        final String contextPackage = BaseContext.class.getPackage().getName()
                + StringConstants.DOT; 
        List<BaseContext> contexts = new ArrayList<BaseContext>();
        for (IConfigurationElement context : confCont) {
            try {
                if (context.getName().equals(Ext.CONT_BASE)) {
                    contexts.add((BaseContext)context
                            .createExecutableExtension(Ext.CONT_CLASS));
                } else {
                    String fullContextName = contextPackage
                            + context.getAttribute(Ext.CONT_ID);
                    contexts.add((BaseContext)Class.forName(fullContextName)
                            .newInstance());
                }
            } catch (Exception e) {
                LOG.error(Ext.EXCEPTION, e);
            }
        }
        return contexts;
    }

    /**
     * Handles the attribute that are configured for a check.
     * 
     * @param confCont
     *            The configuration container which will contain the list of
     *            attributes.
     * @param check
     *            The check which will get the attributes.
     */
    private static void handleAttributes(IConfigurationElement[] confCont,
            BaseCheck check) {
        for (IConfigurationElement attrConf : confCont) {
            String name = attrConf.getAttribute(Ext.ATTR_NAME);
            String defaultValue = attrConf.getAttribute(Ext.ATTR_VALUE);
            String descr = attrConf.getAttribute(Ext.ATTR_DESCR);
            check.setAttributeValue(name, defaultValue);
            check.addDescriptionForAttribute(name, descr);
        }
    }
}
