/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.wiki.ui.utils;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.mylyn.wikitext.ui.WikiText;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.jubula.client.ui.constants.Constants;
/**
 * 
 * @author BREDEX GmbH
  */
public class ProjectMarkupUtil {

    /**
     * Utils class for the markup language
     */
    private ProjectMarkupUtil() {
        // this is a Util class
    }

    /**
     * gets the MarkupLanguage for the loaded project, if not project is
     * loaded it gives back the default language
     * @return the {@link MarkupLanguage} from the project or the default
     */
    public static MarkupLanguage getProjectMarkupLanguage() {
        IProjectPO project = GeneralStorage.getInstance().getProject();
        if (project == null) {
            return getDefaultMarkupLanguage();
        }
        return getMarkupForFileName(project.getMarkupLanguage());
    }

    /**
     * 
     * @param markupLanguage null safe string name of the markup language
     * @return the {@link MarkupLanguage} for the given string 
     */
    public static MarkupLanguage getMarkupForFileName(String markupLanguage) {
        if (StringUtils.isBlank(markupLanguage)) {
            return getDefaultMarkupLanguage();
        }
        return WikiText.getMarkupLanguageForFilename(markupLanguage);
    }
    
    /** the default {@link MarkupLanguage} for the ite
     * @return the default {@link MarkupLanguage} (MediaWikiLanguage)
     */
    public static MarkupLanguage getDefaultMarkupLanguage() {
        return WikiText.getMarkupLanguageForFilename(Constants.DEFAULT_MARKUP);
    }
}
