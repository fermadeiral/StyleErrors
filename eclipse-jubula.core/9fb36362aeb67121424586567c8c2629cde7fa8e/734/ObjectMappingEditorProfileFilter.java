/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.filter;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.ui.filter.JBPatternFilter;
import org.eclipse.jubula.client.ui.rcp.command.parameters.ProfileTypeParameter;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Profile;
import org.eclipse.jubula.tools.internal.xml.businessprocess.ProfileBuilder;
/**
 * Filters by a given profile name.
 *
 * @author BREDEX GmbH
 * @created 21.12.2015
 */
public class ObjectMappingEditorProfileFilter extends JBPatternFilter {
    
    /**
     * {@inheritDoc}
     */
    public boolean isElementVisible(Viewer viewer, Object element) {
        if (element instanceof IObjectMappingAssoziationPO) {
            IComponentIdentifier compId = 
                    ((IObjectMappingAssoziationPO) element)
                    .getTechnicalName();
            if (compId != null && wordMatches(
                    getProfileName((Profile) compId.getProfile()))) {
                return true;
            }
        }
        return super.isElementVisible(viewer, element);
    }
    
    /**
     * Get the name of a profile by comparing it to the standard profiles. If p is null then the name is Global.
     * @param p the profile 
     * @return the name of the profile
     */
    private String getProfileName(Profile p) {
        if (p == null) {
            return ProfileTypeParameter.GLOBAL;
        }
        List<Profile> stdProfiles = ProfileBuilder.getProfiles();

        for (Profile profile : stdProfiles) {
            if (matchesTemplate(profile, p)) {
                return profile.getName();
            }
        }
        return null;
    }
    
    /**
     * Checks if the settings of a given profile are equal to another given profile
     * @param template the template
     * @param p the profile
     * @return true or false
     */
    public static boolean matchesTemplate(Profile template, Profile p) {
        return (p.getContextFactor() == template.getContextFactor())
            && (p.getNameFactor() == template.getNameFactor())
            && (p.getPathFactor() == template.getPathFactor())
            && (p.getThreshold() == template.getThreshold());
    }
}
