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

/**
 * Constants for all used CommandIDs
 *
 * @author BREDEX GmbH
 * @created Jul 30, 2010
 */
public interface CommandIDs {
    /** the ID of the "add comment" command */
    public static final String ADD_COMMENT_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.AddComment"; //$NON-NLS-1$
    
    /** the ID of the "delete" command */
    public static final String DELETE_COMMAND_ID = "org.eclipse.ui.edit.delete"; //$NON-NLS-1$
    
    /** the ID of the "expand tree item" command */
    public static final String EXPAND_TREE_ITEM_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.ExpandBranch"; //$NON-NLS-1$
    
    /** the ID of the "collapse tree item" command */
    public static final String COLLAPSE_TREE_ITEM_COMMAND_ID  = "org.eclipse.jubula.client.ui.commands.CollapseBranch"; //$NON-NLS-1$ 
    
    /** <code>EXPORT_WIZARD_PARAM_ID</code> */
    public static final String EXPORT_WIZARD_PARAM_ID = "exportWizardId"; //$NON-NLS-1$
    
    /** the ID of the "open specification" command */
    public static final String OPEN_SPECIFICATION_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.OpenSpecification"; //$NON-NLS-1$
    
    /** the ID of the "Open test result detail" command */
    public static final String OPEN_TEST_RESULT_DETAIL_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.OpenTestResultViewer"; //$NON-NLS-1$
    
    /** the ID of the "Open test result detail" command */
    public static final String AUTOSIZE_COLUMNS_TEST_RESULT_SUMMARY_VIEW_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.AutoSizeColumnsTestResultSummaryView"; //$NON-NLS-1$
        
    /** the ID of the "Open test result detail" command parameter */
    public static final String OPEN_TEST_RESULT_DETAIL_COMMAND_PARAMETER_SUMMARY_ID = OPEN_TEST_RESULT_DETAIL_COMMAND_ID + ".parameter.summaryId"; //$NON-NLS-1$
    
    /** the ID of the "Open test result detail" command parameter */
    public static final String OPEN_TEST_RESULT_DETAIL_COMMAND_PARAMETER_NODE_ID = OPEN_TEST_RESULT_DETAIL_COMMAND_ID + ".parameter.resultNode"; //$NON-NLS-1$
    
    /** the ID of the "refresh" command */
    public static final String REFRESH_COMMAND_ID = "org.eclipse.ui.file.refresh"; //$NON-NLS-1$
    
    /** the ID of the "select database" command */
    public static final String SELECT_DATABASE_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.SelectDatabase"; //$NON-NLS-1$
    
    /** the ID of the "show specification" command */
    public static final String SHOW_SPECIFICATION_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.ShowSpecification"; //$NON-NLS-1$
    
    /** the ID of the "Toggle relevance" command */
    public static final String TOGGLE_RELEVANCE_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.ToggleRelevance"; //$NON-NLS-1$
}