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
package org.eclipse.jubula.client.ui.rcp.extensions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.ui.rcp.handlers.project.ProjectPropertiesHandler;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.properties.AbstractProjectPropertyPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class will handle the extension point "propertyPages". Its for adding a
 * new project property page, mostly used for configure things that will be
 * persisted in the database, in relation with the project.
 * 
 * @author BREDEX GmbH
 * @created Oct 27, 2010
 */
public final class ProjectPropertyExtensionHandler {

    //--------------------------------------------------------------------------
    // Extension points ids and constants
    //--------------------------------------------------------------------------
    /** The id of the extension point for creating property pages s */
    public static final String PROPERTY_ID = 
        "org.eclipse.jubula.client.ui.rcp.propertyPages"; //$NON-NLS-1$
    /** Attribute name which represents the created class */
    public static final String EP_CLASS = "class"; //$NON-NLS-1$
    /** Attribute name which represents the name of the page */
    public static final String EP_NAME = "name"; //$NON-NLS-1$
    /** Attribute name which represents the id of the page */
    public static final String EP_ID = "id"; //$NON-NLS-1$

    /** logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(ProjectPropertiesHandler.class);
    
    /**
     * Private constructor, because the extension stuff is stateless.
     */
    private ProjectPropertyExtensionHandler() {
        // Nothing here
    }

    /**
     * This method creates all property pages which were created by using the
     * extension point of ClientGUI.
     * 
     * @param es
     *            The edit support for the pages.
     * @param mgr
     *            The manager, where the pages should be added.
     * @return list of these pages
     */
    public static List<AbstractProjectPropertyPage> createPages(
            EditSupport es, PreferenceManager mgr) {
        List<AbstractProjectPropertyPage> tmp = 
            new ArrayList<AbstractProjectPropertyPage>();
        // With all extensions of my extension point...
        for (IExtension ext : ExtensionUtils.getExtensions(PROPERTY_ID)) {
            // ...and then all children which represent the page.
            for (IConfigurationElement prop : ext.getConfigurationElements()) {
                // Institate the class dynamically
                AbstractProjectPropertyPage page = null;
                try {
                    page = (AbstractProjectPropertyPage)prop
                            .createExecutableExtension(EP_CLASS);
                } catch (CoreException e) {
                    LOG.error(Messages.CoreException, e);
                    continue; // If the creation of the class fails
                }

                // Set the edit support
                page.setEditSupport(es);
                
                // Adding the page with the proper title to the manager
                page.setTitle(prop.getAttribute(EP_NAME));
                IPreferenceNode node = new PreferenceNode(prop
                        .getAttribute(EP_ID), page);
                mgr.addToRoot(node);
                tmp.add(page);
            }
        }
        return tmp;
    }
}
