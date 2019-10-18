/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.preferences;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.tools.internal.constants.EnvConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * 
 * @author BREDEX GmbH
 * @created 17.10.2011
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore prefStore = Plugin.getDefault().getPreferenceStore();

        initializeDefaultPreferencesJubulaBasic(prefStore);
        initializeDefaultPreferencesObjectMapping(prefStore);
        initializeDefaultPreferencesKeyBoardShortCuts(prefStore);
        initializeDefaultPreferencesObservation(prefStore);
        initializeDefaultPreferencesTestResults(prefStore);
        initializeDefaultPreferencesEditors(prefStore);

        prefStore.setDefault(Constants.ASKSTOPAUT_KEY,
                Constants.ASKSTOPAUT_KEY_DEFAULT);
        prefStore.setDefault(Constants.USER_KEY, Constants.USER_DEFAULT);
        prefStore.setDefault(Constants.SCHEMA_KEY, Constants.SCHEMA_DEFAULT);
        prefStore.setDefault(Constants.LINK_WITH_EDITOR_TCVIEW_KEY, 
                Constants.LINK_WITH_EDITOR_TCVIEW_KEY_DEFAULT);
        prefStore.setDefault(Constants.DATADIR_WS_KEY, 
                Constants.DATADIR_WS_KEY_DEFAULT);
        prefStore.setDefault(Constants.DATADIR_PATH_KEY, 
                Platform.getLocation().toOSString());

        prefStore.setDefault(Constants.PREF_KEY_CACHE_TEST_RESULTS, 
                Constants.PREF_DEFAULT_CACHE_TEST_RESULTS);
        
        StringBuilder serverValuesBuilder = new StringBuilder();
        serverValuesBuilder.append(new String(Base64.encodeBase64(
                EnvConstants.LOCALHOST_ALIAS.getBytes())));
        serverValuesBuilder.append(StringConstants.SEMICOLON);
        serverValuesBuilder.append(new String(
            Base64.encodeBase64(String.valueOf(
                EnvConstants.AUT_AGENT_DEFAULT_PORT).getBytes())));
        prefStore.setDefault(
                Constants.AUT_AGENT_SETTINGS_KEY, 
                serverValuesBuilder.toString());
        
        prefStore.setDefault(Constants.MAX_ITERATION_KEY,
                Constants.MAX_ITERATION_DEFAULT);
    }

    /**
     * initialize the default preferences for a preference page 
     * @param prefStore Preference store
     */
    private static void initializeDefaultPreferencesJubulaBasic(
            IPreferenceStore prefStore) {
        prefStore.setDefault(Constants.TREEAUTOSCROLL_KEY,
                Constants.TREEAUTOSCROLL_KEY_DEFAULT);
        prefStore.setDefault(Constants.MINIMIZEONSUITESTART_KEY,
                Constants.MINIMIZEONSUITESTART_KEY_DEFAULT);
        prefStore.setDefault(
                Constants.MILLIS_TO_OPEN_COMP_NAMES_CONTENT_PROPOSAL,
                Constants.MILLIS_TO_OPEN_COMP_NAMES_CONTENT_PROPOSAL_DEFAULT);
        prefStore.setDefault(Constants.PERSP_CHANGE_KEY,
                Constants.PERSP_CHANGE_KEY_DEFAULT);
        prefStore.setDefault(Constants.NODE_INSERT_KEY,
                Constants.NODE_INSERT_KEY_DEFAULT);
        prefStore.setDefault(Constants.SAVE_REMINDER_ENABLE_KEY,
                Constants.SAVE_REMINDER_ENABLE_KEY_DEFAULT);
        prefStore.setDefault(Constants.SAVE_REMINDER_INTERVAL_KEY,
                Constants.SAVE_REMINDER_INTERVAL_KEY_DEFAULT);
        prefStore.setDefault(Constants.SHOWCAPINFO_KEY,
                Constants.SHOWCAPINFO_KEY_DEFAULT);
        prefStore.setDefault(Constants.SHOW_TRANSIENT_CHILDREN_KEY,
                Constants.SHOW_TRANSIENT_CHILDREN_KEY_DEFAULT);
        prefStore.setDefault(Constants.PERFORM_AUTO_PROJECT_LOAD_KEY, 
                Constants.LOAD_DEFAULT_PROJECT_KEY_DEFAULT);
        prefStore.setDefault(Constants.REMEMBER_KEY,
                Constants.REMEMBER_KEY_DEFAULT);
        prefStore.setDefault(Constants.UPDATE_REUSED_PROJECT_KEY,
                Constants.UPDATE_REUSED_PROJECTS_KEY_DEFAULT);
    }
    
    /**
     * initialize the default preferences for a preference page 
     * @param prefStore Preference store
     */
    private static void initializeDefaultPreferencesTestResults(
            IPreferenceStore prefStore) {
        prefStore.setDefault(Constants.GENERATEREPORT_KEY,
                Constants.GENERATEREPORT_KEY_DEFAULT);
        prefStore.setDefault(Constants.GENERATE_MONITORING_REPORT_KEY,
                Constants.GENERATE_MONITORING_REPORT_KEY_DEFAULT);
        prefStore.setDefault(Constants.REPORTGENERATORSTYLE_KEY,
            Constants.REPORTGENERATORSTYLE_KEY_DEFAULT);
        prefStore.setDefault(Constants.OPENRESULTVIEW_KEY,
                Constants.OPENRESULTVIEW_KEY_DEFAULT);
        prefStore.setDefault(Constants.TRACKRESULTS_KEY,
                Constants.TRACKRESULTS_KEY_DEFAULT);
        prefStore.setDefault(Constants.RESULTPATH_KEY,
                Constants.RESULTPATH_KEY_DEFAULT);
        prefStore.setDefault(Constants.MAX_NUMBER_OF_DAYS_KEY,
                Constants.MAX_NUMBER_OF_DAYS_KEY_DEFAULT);
        prefStore.setDefault(Constants.AUTO_SCREENSHOT_KEY, 
                Constants.AUTO_SCREENSHOT_KEY_DEFAULT);
        prefStore.setDefault(Constants.TEST_EXECUTION_RELEVANT_REMEMBER_KEY,
                Constants.TEST_EXECUTION_RELEVANT_REMEMBER_KEY_DEFAULT);
    }
    
    /**
     * initialize the default preferences for a preference page 
     * @param prefStore Preference store
     */
    private static void initializeDefaultPreferencesObjectMapping(
            IPreferenceStore prefStore) {
        prefStore.setDefault(Constants.SHOWCHILDCOUNT_KEY,
                Constants.SHOWCHILDCOUNT_KEY_DEFAULT);
    }
    
    /**
     * initialize the default preferences for a preference page 
     * @param prefStore Preference store
     */
    private static void initializeDefaultPreferencesKeyBoardShortCuts(
            IPreferenceStore prefStore) {
        prefStore.setDefault(Constants.MAPPING_MOD_KEY,
                org.eclipse.jubula.client.core.constants.Constants
                .MAPPINGMOD1_KEY_DEFAULT);
        prefStore.setDefault(Constants.MAPPING_TRIGGER_KEY,
                org.eclipse.jubula.client.core.constants.Constants
                .MAPPING_TRIGGER_DEFAULT);
        prefStore.setDefault(Constants.MAPPING_TRIGGER_TYPE_KEY,
                org.eclipse.jubula.client.core.constants.Constants
                .MAPPING_TRIGGER_TYPE_DEFAULT);

        prefStore.setDefault(Constants.MAPPING_WITH_PARENTS_MOD_KEY,
                Constants.MAPPING_WP_MOD1_KEY_DEFAULT);
        prefStore.setDefault(Constants.MAPPING_WITH_PARENTS_TRIGGER_KEY,
                Constants.MAPPING_WP_TRIGGER_DEFAULT);
        prefStore.setDefault(Constants.MAPPING_WITH_PARENTS_TRIGGER_TYPE_KEY,
                Constants.MAPPING_WP_TRIGGER_TYPE_DEFAULT);
    }
    
    /**
     * initialize the default preferences for a preference page 
     * @param prefStore Preference store
     */
    private static void initializeDefaultPreferencesObservation(
            IPreferenceStore prefStore) {
        prefStore.setDefault(Constants.RECORDMOD_COMP_MODS_KEY,
                Constants.RECORDMOD1_KEY_DEFAULT);
        prefStore.setDefault(Constants.RECORDMOD_COMP_KEY_KEY,
                Constants.RECORDMOD2_KEY_DEFAULT);
        prefStore.setDefault(Constants.RECORDMOD_APPL_MODS_KEY,
                Constants.RECORDMOD_APPL_MODS_DEFAULT);
        prefStore.setDefault(Constants.RECORDMOD_APPL_KEY_KEY,
                Constants.RECORDMOD_APPL_KEY_DEFAULT);        
        
        prefStore.setDefault(Constants.CHECKMODE_MODS_KEY,
                Constants.CHECKMODE_MODS_KEY_DEFAULT);
        prefStore.setDefault(Constants.CHECKMODE_KEY_KEY,
                Constants.CHECKMODE_KEY_KEY_DEFAULT);
        prefStore.setDefault(Constants.CHECKCOMP_MODS_KEY,
                Constants.CHECKCOMP_MODS_KEY_DEFAULT);
        prefStore.setDefault(Constants.CHECKCOMP_KEY_KEY,
                Constants.CHECKCOMP_KEY_KEY_DEFAULT);
        
        prefStore.setDefault(Constants.SHOWRECORDDIALOG_KEY,
                Constants.SHOWRECORDDIALOG_KEY_DEFAULT);
        
        prefStore.setDefault(Constants.SINGLELINETRIGGER_KEY,
                Constants.SINGLELINETRIGGER_KEY_DEFAULT);
        prefStore.setDefault(Constants.MULTILINETRIGGER_KEY,
                Constants.MULTILINETRIGGER_KEY_DEFAULT);
        
        prefStore.setDefault(Constants.BACKGROUND_COLORING_KEY,
                Constants.BACKGROUND_COLORING_KEY_DEFAULT);
        prefStore.setDefault(Constants.BACKGROUND_COLOR_KEY,
                Constants.BACKGROUND_COLOR_KEY_DEFAULT);
    }

    /**
     * initialize the default preferences for the Editors page
     * @param prefStore Preference store
     */
    private static void initializeDefaultPreferencesEditors(
            IPreferenceStore prefStore) {
        prefStore.setDefault(Constants.SHOWCOUNTERS_KEY,
                Constants.SHOWCOUNTERS_KEY_DEFAULT);
    }
    
}
