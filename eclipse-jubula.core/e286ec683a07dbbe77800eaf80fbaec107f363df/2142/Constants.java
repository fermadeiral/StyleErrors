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
package org.eclipse.jubula.client.ui.constants;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.eclipse.jubula.client.ui.preferences.utils.Utils;
import org.eclipse.jubula.tools.internal.constants.InputConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.ui.IPageLayout;


/**
 * This class contains common constants for the module JubulaClientGUI
 *
 * @author BREDEX GmbH
 * @created 13.07.2004
 */
public interface Constants {
    /** plugin id */
    public static final String PLUGIN_ID = "org.eclipse.jubula.client.ui"; //$NON-NLS-1$

    /** the category id of the Jubula views */
    public static final String JB_VIEWS_CATEGORY_ID = "org.eclipse.jubula.client.views"; //$NON-NLS-1$
    //  -------------------------------------------------------------
    //  Keys for Views, Editors, Perspectives, Plugins
    //  -------------------------------------------------------------
    /** the ID of the MoveTestCaseAction (e.g. for retargeting) */
    public static final String MOVE_TESTCASE_ACTION_ID = 
        "org.eclipse.jubula.client.ui.rcp.actions.MoveTestCaseAction"; //$NON-NLS-1$
    
    /** the ID of the specification perspective */
    public static final String SPEC_PERSPECTIVE = 
        "org.eclipse.jubula.client.ui.rcp.perspectives.SpecificationPerspective"; //$NON-NLS-1$
    
    /** the ID of the execution perspective */
    public static final String EXEC_PERSPECTIVE = 
        "org.eclipse.jubula.client.ui.rcp.perspectives.ExecutionPerspective"; //$NON-NLS-1$
    
    /** The ID of the aut property page. */
    public static final String AUT_PROPERTY_ID = 
        "org.eclipse.jubula.client.ui.projectProperties.AUTPropertyPage";  //$NON-NLS-1$
    
    /** The ID of the project property page. */
    public static final String PROJECT_PROPERTY_ID = 
        "org.eclipse.jubula.client.ui.projectProperties.ProjectPropertyPage";  //$NON-NLS-1$
    
    /** The ID of the reused project property page. */
    public static final String REUSED_PROJECT_PROPERTY_ID = 
        "org.eclipse.jubula.client.ui.projectProperties.ReusedProjectPropertyPage";  //$NON-NLS-1$

    /** The ID of the ALM project property page. */
    public static final String ALM_PROJECT_PROPERTY_ID = 
        "org.eclipse.jubula.client.ui.projectProperties.ALMProjectPropertyPage";  //$NON-NLS-1$
    
    /** The ID of the Simple Component Extension property page. */
    public static final String SIMPLE_EXTENSION_PROPERTY_ID = 
        "org.eclipse.jubula.client.ui.projectProperties.ReusedProjectPropertyPage";  //$NON-NLS-1$

    /** the ID of the test suite browser */
    public static final String TS_BROWSER_ID = 
        "org.eclipse.jubula.client.ui.rcp.views.TestSuiteBrowser"; //$NON-NLS-1$
    
    /** the ID of the test result tree view */
    public static final String TESTRE_ID = 
        "org.eclipse.jubula.client.ui.rcp.views.TestResultTreeView"; //$NON-NLS-1$
    
    /** the ID of the test case browser */
    public static final String TC_BROWSER_ID = 
        "org.eclipse.jubula.client.ui.rcp.views.TestCaseBrowser"; //$NON-NLS-1$
    
    /** ID of the Jubula Data Set View */
    public static final String JB_DATASET_VIEW_ID = 
        "org.eclipse.jubula.client.ui.rcp.views.DataSetView"; //$NON-NLS-1$
    
    /** ID of the Eclipse Properties View */
    public static final String PROPVIEW_ID = IPageLayout.ID_PROP_SHEET;

    /** ID of the ImageView */
    public static final String IMAGEVIEW_ID = "org.eclipse.jubula.client.ui.views.Image"; //$NON-NLS-1$
    
    /** ID of the LogView */
    public static final String LOGVIEW_ID = "org.eclipse.jubula.client.ui.views.Log"; //$NON-NLS-1$
    
    /** ID of the Jubula Component Names View (override component names) */
    public static final String COMPNAMESVIEW_ID =
        "org.eclipse.jubula.client.ui.rcp.views.CompNamesView"; //$NON-NLS-1$
    
    /** ID of the Jubula Component Name Browser */
    public static final String COMPNAMEBROWSER_ID =
        "org.eclipse.jubula.client.ui.rcp.views.ComponentNameBrowser"; //$NON-NLS-1$

    /** ID of the Eclipse help View */
    public static final String ECLIPSE_HELP_VIEW_ID =
        "org.eclipse.help.ui.HelpView"; //$NON-NLS-1$
    
    /** ID of the ObjectMappingEditor */
    public static final String OBJECTMAPPINGEDITOR_ID = 
        "org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingEditor"; //$NON-NLS-1$
    
    /** ID of the TestSuiteEditor */
    public static final String TEST_SUITE_EDITOR_ID = 
        "org.eclipse.jubula.client.ui.rcp.editors.TestSuiteEditor"; //$NON-NLS-1$
    
    /** ID of the TestJobEditor */
    public static final String TEST_JOB_EDITOR_ID = 
        "org.eclipse.jubula.client.ui.rcp.editors.TestJobEditor"; //$NON-NLS-1$

    /** ID of the central test data editor */
    public static final String CENTRAL_TESTDATA_EDITOR_ID = 
        "org.eclipse.jubula.client.ui.rcp.editors.CentralTestDataEditor"; //$NON-NLS-1$
    
    /** ID of the SpecTestCaseEditor */
    public static final String TEST_CASE_EDITOR_ID = 
        "org.eclipse.jubula.client.ui.rcp.editors.TestCaseEditor"; //$NON-NLS-1$
    
    /** ID of the TestresultSummaryView */
    public static final String TESTRESULT_SUMMARY_VIEW_ID = 
        "org.eclipse.jubula.client.ui.views.TestresultSummaryView"; //$NON-NLS-1$

    /** ID of the TestresultSummaryView */
    public static final String RUNNING_AUTS_VIEW_ID = 
        "org.eclipse.jubula.client.ui.rcp.views.RunningAuts"; //$NON-NLS-1$
    
    //  -------------------------------------------------------------
    //  Keys for Contexts
    //  -------------------------------------------------------------
    /** if there is an image displayed in the image view */
    public static final String IMAGEVIEW_DISPLAYS_IMAGE = 
        "org.eclipse.jubula.client.ui.contexts.ImageViewDisplaysImage"; //$NON-NLS-1$
    
    /** if there is a log displayed in the log view */
    public static final String LOGVIEW_DISPLAYS_LOG = 
            "org.eclipse.jubula.client.ui.contexts.LogViewDisplaysLog"; //$NON-NLS-1$
    
    //  -------------------------------------------------------------
    //  decorator ids
    //  -------------------------------------------------------------
    /** ID of the completeness check decorator */
    public static final String CC_DECORATOR_ID = 
        "org.eclipse.jubula.client.ui.rcp.decorators.completenessCheckDecorator"; //$NON-NLS-1$
    
    /** ID of the active element decorator */
    public static final String ACTIVE_ELEMENT_DECORATOR_ID = 
        "org.eclipse.jubula.client.ui.rcp.decorators.activeElementDecorator"; //$NON-NLS-1$
    
    //  -------------------------------------------------------------
    //  preference keys
    //  -------------------------------------------------------------
    
    /** Key for m_text value to be stored as a resource property */
    public static final String AUT_AGENT_SETTINGS_KEY = "AUT_AGENT_SETTINGS_KEY"; //$NON-NLS-1$

    /** Key for m_text value to be stored as a resource property */
    public static final String SHOWCHILDCOUNT_KEY = "SHOWCHILDCOUNT_PREF_KEY"; //$NON-NLS-1$
    
    /** Key indicating whether number information should be shown for CNs */
    public static final String SHOWCOUNTERS_KEY = "SHOWCOUNTERS_PREF_KEY"; //$NON-NLS-1$

    /** Key for m_text value to be stored as a resource property */
    public static final String SHOWRECORDDIALOG_KEY = "SHOWRECORDDIALOG_PREF_KEY"; //$NON-NLS-1$
    
    /** Key for m_text value to be stored as a resource property */
    public static final String OPENRESULTVIEW_KEY = "OPENRESULTVIEW_PREF_KEY"; //$NON-NLS-1$

    /** Key for m_text value to be stored as a resource property */
    public static final String ASKSTOPAUT_KEY = "ASKSTOPAUT_PREF_KEY"; //$NON-NLS-1$

    /** Key for m_text value to be stored as a resource property */
    public static final String TRACKRESULTS_KEY = "TRACKRESULTS_PREF_KEY"; //$NON-NLS-1$

    /** Key for auto screenshot function */
    public static final String OPEN_LOGVIEW_KEY = "OPEN_LOGVIEW_PREF_KEY"; //$NON-NLS-1$
    
    /** Key for auto screenshot function */
    public static final String AUTO_SCREENSHOT_KEY = "AUTO_SCREENSHOT_PREF_KEY"; //$NON-NLS-1$

    /** Key for auto screenshot function */
    public static final boolean AUTO_SCREENSHOT_KEY_DEFAULT = true;
    
    /** Key for m_text value to be stored as a resource property */
    public static final String RESULTPATH_KEY = "RESULTPATH_PREF_KEY"; //$NON-NLS-1$

    /** Key for m_text value to be stored as a resource property */
    public static final String MAX_NUMBER_OF_DAYS_KEY = "MAX_NUMBER_OF_DAYS_KEY"; //$NON-NLS-1$
    
    /** Key for secure storage category */
    public static final String ORG_ECLIPSE_JUBULA_MOST_RECENT_PROJECTDATA_KEY = "org.eclipse.jubula.MostRecentProjectdata"; //$NON-NLS-1$
    
    /** Key for the most recent project name */
    public static final String MOST_RECENT_PROJECTDATA_GUID_KEY = "project.guid"; //$NON-NLS-1$
    
    /** Key for the most recent project version */
    public static final String MOST_RECENT_PROJECTDATA_VERSION_KEY = "project.version"; //$NON-NLS-1$
    
    /** Key for m_text value to be stored as a resource property */
    public static final String GENERATEREPORT_KEY = "GENERATEREPORT_KEY"; //$NON-NLS-1$

    /** Key for m_generateReport value to be stored as a resource property */
    public static final String GENERATE_MONITORING_REPORT_KEY = "GENERATE_MONITORING_REPORT_KEY"; //$NON-NLS-1$
    
    /** Key for report generator style to be stored as a resource property */
    public static final String REPORTGENERATORSTYLE_KEY = "REPORTGENERATORSTYLE_KEY"; //$NON-NLS-1$
    
    /** Key for the first value of the ObjectMapping Sash weight to be stored as a resource property */
    public static final String OME_SASH_WEIGHT_0 = "OME_SASH0_PREF_KEY"; //$NON-NLS-1$
    
    /** Key for the second value of the ObjectMapping Sash weight to be stored as a resource property */
    public static final String OME_SASH_WEIGHT_1 = "OME_SASH1_PREF_KEY"; //$NON-NLS-1$
    
    /** Key for m_text value to be stored as a resource property */
    public static final String MAPPING_MOD_KEY = "MAPPINGMOD1_PREF_KEY"; //$NON-NLS-1$

    /** Key for m_text value to be stored as a resource property */
    public static final String MAPPING_TRIGGER_KEY = "MAPPINGMOD2_PREF_KEY"; //$NON-NLS-1$
    
    /** Key for m_text value to be stored as a resource property */
    public static final String MAPPING_TRIGGER_TYPE_KEY = "MAPPINGMOD2_TYPE_PREF_KEY"; //$NON-NLS-1$

    /** Key for m_text value to be stored as a resource property */
    public static final String MAPPING_WITH_PARENTS_MOD_KEY = "MAPPING_WITH_PARENTS_MOD1_PREF_KEY"; //$NON-NLS-1$

    /** Key for m_text value to be stored as a resource property */
    public static final String MAPPING_WITH_PARENTS_TRIGGER_KEY = "MAPPING_WITH_PARENTS_MOD2_PREF_KEY"; //$NON-NLS-1$
    
    /** Key for m_text value to be stored as a resource property */
    public static final String MAPPING_WITH_PARENTS_TRIGGER_TYPE_KEY = "MAPPING_WITH_PARENTS_MOD2_TYPE_PREF_KEY"; //$NON-NLS-1$

    /** Key for m_text value to be stored as a resource property */
    public static final String RECORDMOD_COMP_MODS_KEY = "RECORDMOD1_PREF_KEY"; //$NON-NLS-1$

    /** Key for m_text value to be stored as a resource property */
    public static final String RECORDMOD_COMP_KEY_KEY = "RECORDMOD2_PREF_KEY"; //$NON-NLS-1$
    
    /** Key for m_text value to be stored as a resource property */
    public static final String RECORDMOD_APPL_MODS_KEY = "RECORDMOD3_PREF_KEY"; //$NON-NLS-1$

    /** Key for m_text value to be stored as a resource property */
    public static final String RECORDMOD_APPL_KEY_KEY = "RECORDMOD4_PREF_KEY"; //$NON-NLS-1$
    
    /** Key for m_text value to be stored as a resource property */
    public static final String CHECKMODE_MODS_KEY = "CHECKMODE_MODS_PREF_KEY"; //$NON-NLS-1$

    /** Key for m_text value to be stored as a resource property */
    public static final String CHECKMODE_KEY_KEY = "CHECKMODE_KEY_PREF_KEY"; //$NON-NLS-1$

    /** Key for m_text value to be stored as a resource property */
    public static final String CHECKCOMP_MODS_KEY = "CHECKCOMP_MODS_PREF_KEY"; //$NON-NLS-1$

    /** Key for m_text value to be stored as a resource property */
    public static final String CHECKCOMP_KEY_KEY = "CHECKCOMP_KEY_PREF_KEY"; //$NON-NLS-1$

    /** Key for m_text value to be stored as a resource property */
    public static final String SINGLELINETRIGGER_KEY = "SINGLELINETRIGGER_PREF_KEY"; //$NON-NLS-1$
    
    /** Key for m_text value to be stored as a resource property */
    public static final String MULTILINETRIGGER_KEY = "MULTILINETRIGGER_PREF_KEY"; //$NON-NLS-1$

    /** Key for m_text value to be stored as a resource property */
    public static final String MINIMIZEONSUITESTART_KEY = "MINIMIZEONSUITESTART_PREF_KEY"; //$NON-NLS-1$
    
    /** Key for m_text value to be stored as a resource property */
    public static final String TREEAUTOSCROLL_KEY = "TREEAUTOSCROLL_PREF_KEY"; //$NON-NLS-1$

    /** Key for m_text value to be stored as a resource property */
    public static final String LAST_USED_AUT_AGENT_KEY = "LAST_USED_AUT_AGENT_KEY"; //$NON-NLS-1$
    
    /** Key for m_text value to be stored as a resource property */
    public static final String PREFERRED_JRE_KEY = "PREFERRED_JRE_KEY"; //$NON-NLS-1$
    
    /** Key for m_text value to be stored as a resource property */
    public static final String REMEMBER_KEY = "REMEMBER_KEY"; //$NON-NLS-1$

    /** Key for m_text value to be stored as a resource property */
    public static final String TEST_EXECUTION_RELEVANT_REMEMBER_KEY = "TEST_EXECUTION_RELEVANT_REMEMBER_KEY"; //$NON-NLS-1$
    
    /** Key for m_text value to be stored as a resource property */
    public static final String DATADIR_WS_KEY = "DATADIR_WS_KEY"; //$NON-NLS-1$

    /** Key for m_text value to be stored as a resource property */
    public static final String DATADIR_PATH_KEY = "DATADIR_PATH_KEY"; //$NON-NLS-1$
    
    /** Key for automatically database connection function */
    public static final String AUTOMATIC_DATABASE_CONNECTION_KEY = "AUTOMATIC_DATABASE_CONNECTION_KEY"; //$NON-NLS-1$
    
    /** Key for the secure storage plugin id */
    public static final String SECURE_STORAGE_PLUGIN_ID = "org.eclipse.equinox.security.ui.storage"; //$NON-NLS-1$
    
    /** Key for secure storage category */
    public static final String ORG_ECLIPSE_JUBULA_SECURE_KEY = "org.eclipse.jubula.DatabaseProfile"; //$NON-NLS-1$
    
    /** Key which associate the secure storage value for the database username */
    public static final String SECURE_STORAGE_USERNAME_KEY = "Username"; //$NON-NLS-1$
    
    /** Key which associate the secure storage value for the database password */
    public static final String SECURE_STORAGE_PASSWORD_KEY = "Password"; //$NON-NLS-1$
    
    /** Key for m_text value to be stored as a resource property */
    public static final String PERSP_CHANGE_KEY = "PERSP_CHANGE_KEY"; //$NON-NLS-1$
    /** Key for m_text value to be stored as a resource property */
    public static final String USER_KEY = "USER_KEY"; //$NON-NLS-1$
    /** Key for m_text value to be stored as a resource property */
    public static final String SCHEMA_KEY = "SCHEMA_KEY"; //$NON-NLS-1$
    /** Key for save profile function */
    public static final String SAVE_PROFILE_NAMES_KEY = "SAVE_PROFILE_NAMES_KEY"; //$NON-NLS-1$
    /** Key for m_text value to be stored as a resource property */
    public static final String START_BROWSE_PATH_KEY = "START_BROWSE_PATH_KEY"; //$NON-NLS-1$
    /** Key for m_text value to be stored as a resource property */
    public static final String NODE_INSERT_KEY = "NODE_INSERT_KEY"; //$NON-NLS-1$
    /** Key for m_text value to be stored as a resource property */
    public static final String SAVE_REMINDER_ENABLE_KEY = "SAVE_REMINDER_ENABLE_KEY"; //$NON-NLS-1$
    /** Key for m_text value to be stored as a resource property */
    public static final String SAVE_REMINDER_INTERVAL_KEY = "SAVE_REMINDER_INTERVAL_KEY"; //$NON-NLS-1$
    /** Key for m_text value to be stored as a resource property */
    public static final String SHOWCAPINFO_KEY = "SHOWCAPINFO_PREF_KEY"; //$NON-NLS-1$
    /** Key preference store property */
    public static final String SHOW_TRANSIENT_CHILDREN_KEY = "SHOW_TRANSIENT_CHILDREN_KEY"; //$NON-NLS-1$
    /** Key for the status od the Link-with-Editor-Key */
    public static final String LINK_WITH_EDITOR_TCVIEW_KEY = "LINK_WITH_EDITOR_TCVIEW_KEY"; //$NON-NLS-1$
    /** Key for the mode of the AUT Config dialog */
    public static final String AUT_CONFIG_DIALOG_MODE = "AUT_CONFIG_DIALOG_MODE"; //$NON-NLS-1$
    /** Key for the mode of background color filtering */
    public static final String BACKGROUND_COLORING_KEY = "BACKGROUND_COLORING_KEY"; //$NON-NLS-1$
    /** Key for the mode of background color filtering color */
    public static final String BACKGROUND_COLOR_KEY = "BACKGROUND_COLOR_KEY"; //$NON-NLS-1$
    
    /** 
     * Key for preference whether Test Results opened in the Test Result Viewer
     * should be cached in the master session (even after closing the viewer) 
     */
    public static final String PREF_KEY_CACHE_TEST_RESULTS = "CACHE_TEST_RESULTS"; //$NON-NLS-1$
    /** Key for loading the default project automatically */
    public static final String PERFORM_AUTO_PROJECT_LOAD_KEY = "PERFORM_AUTO_PROJECT_LOAD_KEY"; //$NON-NLS-1$
    /** Key for content assist of component names view to be stored as a resource property */
    public static final String MILLIS_TO_OPEN_COMP_NAMES_CONTENT_PROPOSAL = "MILLIS_TO_OPEN_COMP_NAMES_CONTENT_PROPOSAL"; //$NON-NLS-1$
    /** Key for show the update reused project dialog */
    public static final String UPDATE_REUSED_PROJECT_KEY = "UPDATE_REUSED_PROJECT_KEY"; //$NON-NLS-1$
    /** Key for the maximum number of iterations */
    public static final String MAX_ITERATION_KEY = "MAX_ITERATION_KEY"; //$NON-NLS-1$
    /** Key for show the update reused project dialog */
    public static final int MAX_ITERATION_DEFAULT = 100;
    
    // -------------------------------------------------------------
    // preference default values
    // some key, useful for ClientTestImpl has been moved to
    // org.eclipse.jubula.tools.internal.constants.TestDataConstants
    // -------------------------------------------------------------
        
    /** the default markup language for descriptions */
    public static final String DEFAULT_MARKUP = "mediawiki"; //$NON-NLS-1$
    
    /** Default value of the "Use workspace as data directory" preference */
    public static final boolean DATADIR_WS_KEY_DEFAULT = true;

    /** Default value of the Link-with-Editor-Key */
    public static final boolean LINK_WITH_EDITOR_TCVIEW_KEY_DEFAULT = false;
    
    /** Key for boolean value to be stored as a resource property */
    public static final boolean SHOWCHILDCOUNT_KEY_DEFAULT = false;
    
    /** Key for certain counters to be shown */
    public static final boolean SHOWCOUNTERS_KEY_DEFAULT = true;
    
    /** Key for boolean value to be stored as a resource property */
    public static final boolean SHOWRECORDDIALOG_KEY_DEFAULT = true;

    /** Key for boolean value to be stored as a resource property */
    public static final boolean OPENRESULTVIEW_KEY_DEFAULT = true; 

    /** Key for boolean value to be stored as a resource property */
    public static final boolean ASKSTOPAUT_KEY_DEFAULT = true; 

    /** Key for boolean value to be stored as a resource property */
    public static final boolean TRACKRESULTS_KEY_DEFAULT = true; 

    /** Default value of the report generator style key */
    public static final String REPORTGENERATORSTYLE_KEY_DEFAULT = 
        "TestResultViewPreferencePage.TypeComplete"; //$NON-NLS-1$

    /** Key for boolean value to be stored as a resource property */
    public static final boolean GENERATEREPORT_KEY_DEFAULT = false;
    
    /** Key for m_generateMonitoringReport value to be stored as a resource property */
    public static final boolean GENERATE_MONITORING_REPORT_KEY_DEFAULT = false;

    /** Key for m_text value to be stored as a resource property */
    public static final boolean TEST_EXECUTION_RELEVANT_REMEMBER_KEY_DEFAULT = 
        false;
    
    /** Key for m_text value to be stored as a resource property */
    public static final String RESULTPATH_KEY_DEFAULT = StringConstants.EMPTY;
    
    /**
     * <code>MAX_NUMBER_OF_DAYS_KEY_DEFAULT</code>
     */
    public static final int MAX_NUMBER_OF_DAYS_KEY_DEFAULT = 30;
    
    /** Key for m_text value to be stored as a resource property */
    public static final boolean CLEAN_TESTRESULTS_KEY_DEFAULT = false;
    
    /** Key for m_text value to be stored as a resource property */
    public static final int CLEAN_DAYS_KEY_DEFAULT = 5;
    
    /** Key for int value to be stored as a resource property */
    public static final int MAPPING_WP_TRIGGER_DEFAULT = KeyEvent.VK_A;
    /** Key for int value to be stored as a resource property */
    public static final int MAPPING_WP_MOD1_KEY_DEFAULT = 
        InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK;
    
    /** Default value for Object Mapping trigger type */
    public static final int MAPPING_WP_TRIGGER_TYPE_DEFAULT = 
        InputConstants.TYPE_KEY_PRESS;
    
    /** Default value for modifier key(s) for "Refresh" action in Object Mapping Mode */
    public static final int REFRESH_MOD_DEFAULT = 
        InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK;

    /** Default value for base key for "Refresh" action in Object Mapping Mode */
    public static final int REFRESH_KEY_DEFAULT = KeyEvent.VK_R;
    
    /** Key for int value to be stored as a resource property */
    public static final int RECORDMOD1_KEY_DEFAULT = InputEvent.SHIFT_DOWN_MASK 
        | InputEvent.CTRL_DOWN_MASK;
    
    /** Key for m_text value to be stored as a resource property */
    public static final int RECORDMOD2_KEY_DEFAULT = KeyEvent.VK_A;
    
    /** Key for int value to be stored as a resource property */
    public static final int RECORDMOD_APPL_MODS_DEFAULT = 
        InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK;
    /** Key for m_text value to be stored as a resource property */
    public static final int RECORDMOD_APPL_KEY_DEFAULT = KeyEvent.VK_M; 
    
    /** Key for int value to be stored as a resource property */
    public static final int RECORDMOD3_KEY_DEFAULT = InputEvent.SHIFT_DOWN_MASK 
        | InputEvent.CTRL_DOWN_MASK;
    /** Key for m_text value to be stored as a resource property */
    public static final int RECORDMOD4_KEY_DEFAULT = KeyEvent.VK_S;
    
    /** Key for int value to be stored as a resource property */
    public static final int CHECKMODE_MODS_KEY_DEFAULT = 
        InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK;
    /** Key for m_text value to be stored as a resource property */
    public static final int CHECKMODE_KEY_KEY_DEFAULT = KeyEvent.VK_F11;
    
    /** Key for int value to be stored as a resource property */
    public static final int CHECKCOMP_MODS_KEY_DEFAULT = 
        InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK;
    /** Key for m_text value to be stored as a resource property */
    public static final int CHECKCOMP_KEY_KEY_DEFAULT = KeyEvent.VK_F12;
    
    /** Key for int value to be stored as a resource property */
    public static final String SINGLELINETRIGGER_KEY_DEFAULT = 
        Utils.encodeStringArray(new String [] {"Tab", "Enter"}, //$NON-NLS-1$ //$NON-NLS-2$
                StringConstants.SEMICOLON); 
    /** Key for int value to be stored as a resource property */
    public static final String MULTILINETRIGGER_KEY_DEFAULT = 
        Utils.encodeStringArray(new String [] {"Tab"}, //$NON-NLS-1$
                StringConstants.SEMICOLON); 
    
    /** Key for boolean value to be stored as a resource property */
    public static final boolean REMEMBER_KEY_DEFAULT = false;
    
    /** Key for int value to be stored as a resource property */
    public static final int MILLIS_TO_OPEN_COMP_NAMES_CONTENT_PROPOSAL_DEFAULT =
            500;
    
    /** Key for boolean value to be stored as a resource property */
    public static final int PERSP_CHANGE_KEY_DEFAULT = 2;
    
    /** Key for boolean value to be stored as a resource property */
    public static final int TEST_EXECUTION_RELEVANT_DEFAULT = 2;
    
    /** Key for boolean value to be stored as a resource property */
    public static final boolean MINIMIZEONSUITESTART_KEY_DEFAULT = true;
    
    /** Key for boolean value to be stored as a resource property */
    public static final boolean TREEAUTOSCROLL_KEY_DEFAULT = true;

    /** Value for default preferred JRE to be stored as a resource property */
    public static final String PREFERRED_JRE_DEFAULT = StringConstants.EMPTY;
    /** Value for default username to be stored as a resource property */
    public static final String USER_DEFAULT = StringConstants.EMPTY;
    /** Value for default schema to be stored as a resource property */
    public static final String SCHEMA_DEFAULT = StringConstants.EMPTY;
    /** Value for default browser path */
    public static final String START_BROWSE_PATH_DEFAULT = 
        StringConstants.EMPTY;
    /** Key for boolean value to be stored as a resource property */
    public static final boolean NODE_INSERT_KEY_DEFAULT = true;
    /** Key for boolean value to be stored as a resource property */
    public static final boolean SAVE_REMINDER_ENABLE_KEY_DEFAULT = false;
    /** Key for int value to be stored as a resource property */
    public static final int SAVE_REMINDER_INTERVAL_KEY_DEFAULT = 10;
    /** Key for boolean value to be stored as a resource property */
    public static final boolean SHOWCAPINFO_KEY_DEFAULT = true;
    /** default value for showing transient childrens */
    public static final boolean SHOW_TRANSIENT_CHILDREN_KEY_DEFAULT = true;
    /** 
     * Default value for preference whether Test Results opened in the Test 
     * Result Viewer should be cached in the master session (even after 
     * closing the viewer) 
     */
    public static final boolean PREF_DEFAULT_CACHE_TEST_RESULTS = false;
    /** default value for loading the default project */
    public static final boolean LOAD_DEFAULT_PROJECT_KEY_DEFAULT = false;
    /** default value of show update reused projects dialog */
    public static final boolean UPDATE_REUSED_PROJECTS_KEY_DEFAULT = false;
    /** default value for filter background coloring */
    public static final boolean BACKGROUND_COLORING_KEY_DEFAULT = true;
    /** default value for filter background color */
    public static final int BACKGROUND_COLOR_KEY_DEFAULT = 14609407;

    // --------------------------------------------------------------------
    
    // -------------------------------------------------------------
    // Keys for informations at TableItems
    // -------------------------------------------------------------
    /** Constant for a NodePO key in a Map */
    public static final String NODEPO_KEY = "NodePO"; //$NON-NLS-1$
    
    /** Constant for a DataSetCount key in a Map */
    public static final String DATASETCOUNT_KEY = "DataSetCount"; //$NON-NLS-1$
    
    /** Constant for a DataSetType column key in a Map */
    public static final String DATATYPE_COLUMN_KEY = "DataTypeColumn"; //$NON-NLS-1$
    
    /** Constant for the data type in a Map */
    public static final String DATATYPE_KEY = "DataType"; //$NON-NLS-1$
    
    // -------------------------------------------------------------
    /** Constant for a Bullet (-) */
    public static final String BULLET = "- "; //$NON-NLS-1$
    
    /** Constant for "item" */
    public static final String ITEM = "item"; //$NON-NLS-1$
    
    /** change perspective automatically = yes */
    public static final int PERSPECTIVE_CHANGE_YES = 0;
    /** change perspective automatically = no */
    public static final int PERSPECTIVE_CHANGE_NO = 1;
    /** change perspective automatically = prompt */
    public static final int PERSPECTIVE_CHANGE_PROMPT = 2;
    
    /** test execution is always relevant = yes */
    public static final int TEST_EXECUTION_RELEVANT_YES = 0;
    /** test execution is never relevant = no */
    public static final int TEST_EXECUTION_RELEVANT_NO = 1;
    /** ask before test execution = prompt */
    public static final int TEST_EXECUTION_RELEVANT_PROMPT = 2;
    
    /** value for YES */
    public static final int UTILS_YES = 1;
    /** value for NO */
    public static final int UTILS_NO = 2;
    /** value for PROMPT */
    public static final int UTILS_PROMPT = 0;
    
    // -------------------------------------------------------------
    // Marker for Problem/Task view
    //  -------------------------------------------------------------
    /** problem marker ID */
    public static String JB_PROBLEM_MARKER = "org.eclipse.jubula.client.ui.rcp.jbProblem"; //$NON-NLS-1$
    /** Object marker type. */
    public static final String JB_OBJECT_HASHCODE = "jbObjectHashCode"; //$NON-NLS-1$
    /** Object marker type. */
    public static final String JB_FQCN = "jbFQCN"; //$NON-NLS-1$
    /** Reason marker attribute. */
    public static final String JB_REASON = "jbReason"; //$NON-NLS-1$
    /** Object marker name. */
    public static final String JB_OBJECT_NAME = "jbObjectName"; //$NON-NLS-1$
    /** Object marker name. */
    public static final String JB_NODE_GUID = "jbNodeGUID"; //$NON-NLS-1$
    
    //  -------------------------------------------------------------
    
    /** AUT-Agent Preference Page */
    public static final String JB_PREF_PAGE_AUTAGENT = "org.eclipse.jubula.client.ui.rcp.preferences.AutAgentPrefPage"; //$NON-NLS-1$
    //  'status icon' constants
    /** constant for state "not connected to server" */
    public static final int NO_SERVER = 0;
    /** constant for state "not connected to service component */
    public static final int NO_SC = 1;
    /** constant for state "no aut running" */
    public static final int NO_AUT = 2;
    /** constant for state "aut ss up and running" */
    public static final int AUT_UP = 3;
    /** constant for state "aut in record mode" */
    public static final int RECORDING = 4;
    /** constant for state "aut in mapping mode" */
    public static final int MAPPING = 5;
    /** constant for state "suite paused" */
    public static final int PAUSED = 6;
    /** constant for state "aut in check mode" */
    public static final int CHECKING = 7;
    /** constant for default color */
    public static final int DEFAULT_ICON = NO_SERVER;
    
    // ---------------------------------------------------------------
    /** validation state of parameter value in combobox */
    public static final String VALID_STATE = "validationState"; //$NON-NLS-1$
}
