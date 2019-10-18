/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.wizards.search.refactor.pages;


import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * Wizard page for matching the parameter names.
 *
 * @author BREDEX GmbH
 */
public abstract class AbstractMatchSelectionPage extends WizardPage {

    /** The scrolled composite containing the parameter names. */
    private ScrolledComposite m_scroll;

    /** GridLayout to show the new and old parameter names. */
    private Composite m_selectGrid;

    /** The group box for additional information. */
    private Group m_infoGroup;

    /** The text area for additional information. */
    private Text m_infoText;
    
    /** The help id associated to this page */
    private String m_helpId;

    /**
     * @param pageName The page name.
     * @param title The title of the page.
     * @param titleImage The image descriptor for the title of this wizard page,
     *   or <code>null</code> if none.
     * @param helpId the HelpId of the page
     */
    public AbstractMatchSelectionPage(
            String pageName,
            String title,
            ImageDescriptor titleImage,
            String helpId) {
        super(title, title, titleImage);
        m_helpId = helpId;
    }

    /**
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        // create a main composite with one column
        Composite composite = new Composite(parent, SWT.NONE);
        setGrid(composite, 1);
        setControl(composite);
        // create a group for selecting the parameters
        createGroupForSelectingParameters(composite);
        createGroupForInfoText(composite);
    }

    /**
     * Create the group including the table for matching the parameters
     * with combo boxes.
     * @param parent The parent.
     */
    private void createGroupForSelectingParameters(Composite parent) {
        Group groupSelection = new Group(parent, SWT.NONE);
        setGrid(groupSelection, 1);

        m_scroll = new ScrolledComposite(
                groupSelection, SWT.V_SCROLL | SWT.H_SCROLL);
        setGrid(m_scroll, 1);
        m_scroll.setExpandHorizontal(true);
        m_scroll.setExpandVertical(true);

        m_selectGrid = new Composite(m_scroll, SWT.NONE);
        m_scroll.setContent(m_selectGrid);
        setGrid(m_selectGrid, 2);
        // add right margin reserving space for decorators
        GridLayout gridLayout = (GridLayout) m_selectGrid.getLayout();
        gridLayout.marginRight = 10;
    }

    /**
     * Create the group including the info text box.
     * @param parent The parent.
     */
    private void createGroupForInfoText(Composite parent) {
        m_infoGroup = new Group(parent, SWT.NONE);
        m_infoGroup.setText(Messages
                .ReplaceTCRWizard_additionalInformation_title);
        setGrid(m_infoGroup, 1);
        m_infoGroup.setLayoutData(
                GridDataFactory.fillDefaults()
                    .align(SWT.FILL, SWT.END).grab(true, false)
                    .hint(SWT.DEFAULT, 80).create());
        m_infoText = new Text(m_infoGroup,
                SWT.READ_ONLY | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        m_infoText.setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    /**
     * @param messages The messages to show in the text area for additional
     *                 information. If null or empty, the text box is cleared.
     */
    protected void setAdditionalInformation(List<String> messages) {
        String allText = ""; //$NON-NLS-1$
        if (messages != null) {
            for (String text: messages) {
                if (allText.length() > 0) {
                    allText += "\n";  //$NON-NLS-1$
                }
                allText += "\u2022 " //$NON-NLS-1$
                        + text;
            }
        }
        m_infoText.setText(allText);
    }

    /**
     * Set a filled grid layout with given columns at the given composite.
     * @param composite The composite.
     * @param column The number of columns.
     */
    protected static void setGrid(Composite composite, int column) {
        composite.setLayout(new GridLayout(column, true));
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    /**
     * Create a label with the given text in a bold black font and white background
     * added to the given parent.
     * @param parent The parent.
     * @param text The text.
     */
    protected static void createHeadLabel(Composite parent, String text) {
        StyledText styledText = new StyledText(parent,
                SWT.READ_ONLY | SWT.WRAP | SWT.LEAD | SWT.LEFT);
        styledText.setLayoutData(
                new GridData(SWT.FILL, SWT.FILL, true, false));
        styledText.setEnabled(false);
        styledText.setText(text);
        styledText.setBackground(parent.getDisplay()
                .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        styledText.setStyleRange(new StyleRange(0, text.length(),
                null,
                null,
                SWT.BOLD));
    }

    @Override
    public void setVisible(boolean isVisible) {
        if (isVisible) {
            // add the selectable parameter names
            createSelectionTable(m_selectGrid);
            // set the minimum size of the selection table depending on current content
            m_scroll.setMinSize(m_selectGrid.computeSize(
                    SWT.DEFAULT, SWT.DEFAULT));
            // update the layout of the selection table
            m_selectGrid.layout(true);
        }
        super.setVisible(isVisible);
    }

    /**
     * Create the table of parameters showing the new component or parameter name
     * at the left column and corresponding combo boxes at the right column.
     * @param parent The parent composite with a grid layout of two columns.
     */
    protected abstract void createSelectionTable(Composite parent);

    /**
     * Show help contend attached to wizard after selecting the ? icon,
     * or pressing F1 on Windows / Shift+F1 on Linux / Help on MAC.
     * {@inheritDoc}
     */
    public void performHelp() {
        Plugin.getHelpSystem().displayHelp(m_helpId);
    }
    
}
