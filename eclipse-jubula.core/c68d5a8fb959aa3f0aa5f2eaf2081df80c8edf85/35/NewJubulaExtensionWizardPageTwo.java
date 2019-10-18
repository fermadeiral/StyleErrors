/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.extensions.wizard.view;

import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jubula.extensions.wizard.model.ToolkitProvider;
import org.eclipse.jubula.extensions.wizard.i18n.Messages;
import org.eclipse.jubula.extensions.wizard.model.Storage;
import org.eclipse.jubula.extensions.wizard.model.Toolkit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.PlatformUI;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * The second page of the New Jubula Extension Wizard in which the user
 * can select the extension's toolkit.
 * 
 * @author BREDEX GmbH
 */
public final class NewJubulaExtensionWizardPageTwo extends WizardPage {

    /** The page's ID */
    private static final String PAGE_NAME = Messages.PageTwo_PageName;
    
    /** The page's title */
    private static final String PAGE_TITLE = Messages.PageTwo_PageTitle;
    
    /** The page's description */
    private static final String PAGE_DESCRIPTION = 
            Messages.PageTwo_PageDescription;
    
    /** The page's container */
    private Composite m_container;
    
    /** The toolkit group instance */
    private final ToolkitGroup m_toolkitGroup;
    
    /** The instance of this wizard's storage */
    private final Storage m_storage;

    /**
     * The constructor that creates the page and sets
     * its title and description.
     * @param storage the storage instance this page instance should use
     */
    public NewJubulaExtensionWizardPageTwo(Storage storage) {
        super(PAGE_NAME);
        setTitle(PAGE_TITLE);
        setDescription(PAGE_DESCRIPTION);
        
        m_storage = storage;
        m_toolkitGroup = new ToolkitGroup();
    }
    
    @Override
    public void createControl(Composite parent) {
        m_container = new Composite(parent, SWT.NONE);
        
        setControl(m_container);
        m_container.setLayout(new FormLayout());
        
        m_toolkitGroup.createControl(m_container);
        
        m_container.getShell().setSize(550, 700);
        setPageComplete(true);
        
        PlatformUI.getWorkbench().getHelpSystem()
            .setHelp(getShell(), 
                    Messages.PageTwoQualifier);
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            PlatformUI.getWorkbench().getHelpSystem()
                .setHelp(getShell(), 
                        Messages.PageTwoQualifier);
        }
    }
    
    /**
     * Creates all items and controls of the toolkit group.
     */
    private final class ToolkitGroup {
        
        /** The "available toolkits" label */
        private Label m_label;
        
        /** The sash form */
        private SashForm m_sashForm;
        
        /** The list containing all displayed toolkits */
        private java.util.List<Toolkit> m_toolkits;
        
        /** The description text box */
        private StyledText m_text;
        
        /** The toolkit list */
        private List m_list;
        
        /**
         * Creates the project name text field
         * @param container
         *              The parent container
         */
        private void createControl(Composite container) {
            createLabel(container);
            createToolkitSash(container);
        }
        
        /**
         * Creates the "available toolkits" label
         * @param container
         *              The parent container
         */
        private void createLabel(Composite container) {
            m_label = new Label(m_container, SWT.NONE);
            FormData fdLabel = new FormData();
            fdLabel.top = new FormAttachment(0, 10);
            fdLabel.left = new FormAttachment(0, 10);
            m_label.setLayoutData(fdLabel);
            m_label.setText(Messages.PageTwo_AvailableToolkits);
        }
        
        /**
         * Creates the toolkit sash form
         * @param container
         *              The parent container
         */
        private void createToolkitSash(Composite container) {
            m_sashForm = new SashForm(container, SWT.NONE);
            FormData fdSashForm = new FormData();
            fdSashForm.top = new FormAttachment(m_label, 6);
            fdSashForm.left = new FormAttachment(0, 10);
            fdSashForm.bottom = new FormAttachment(100, -10);
            fdSashForm.right = new FormAttachment(100, -10);
            m_sashForm.setLayoutData(fdSashForm);
            createList(container);
            createTextBox(container);
            m_sashForm.setWeights(new int[] {2, 3});
        }
        
        /**
         * Creates the toolkit list, its contents and controls
         * @param container
         *              The parent container
         */
        private void createList(Composite container) {
            m_toolkits = ToolkitProvider.getInstance().getToolkits();
            m_list = new List(m_sashForm, SWT.BORDER);
            m_list.setItems(
                    m_toolkits.stream()
                            .map(new Function<Toolkit, Object>() {
                                @Override
                                public Object apply(Toolkit t) {
                                    return t.getName();
                                }
                            })
                            .collect(Collectors.toList())
                            .toArray(new String[0])
            );
            m_list.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    setToolkitDescription();
                    m_storage.setToolkit(getSelectedToolkit());
                    m_storage.setToolkitChanged(true);
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    widgetSelected(e);
                }
            });
            m_list.addMouseListener(new MouseListener() {
                @Override
                public void mouseDoubleClick(MouseEvent e) {
                    if (isPageComplete()) {
                        getContainer().showPage(getNextPage());
                    }
                }
                @Override
                public void mouseDown(MouseEvent e) {
                    // Not used
                }
                @Override
                public void mouseUp(MouseEvent e) {
                    // Not used
                }
            });
            m_list.select(0);
            m_storage.setToolkit(getSelectedToolkit());
            m_storage.setToolkitChanged(true);
        }
        
        /**
         * Displays the description of the selected toolkit.
         */
        private void setToolkitDescription() {
            Toolkit toolkit = getSelectedToolkit();
            m_text.setText(
                    toolkit.getName() + "\n\n" //$NON-NLS-1$
                    + toolkit.getDescription()
            );
            StyleRange textStyle = new StyleRange();
            textStyle.start = 0;
            textStyle.length = toolkit.getName().length();
            textStyle.fontStyle = SWT.BOLD;
            m_text.setStyleRange(textStyle);
        }
        
        /**
         * Creates the description text box
         * @param container
         *              The parent container
         */
        private void createTextBox(Composite container) {
            m_text = new StyledText(m_sashForm, 
                    SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
            setToolkitDescription();
        }
        
        /**
         * Returns the selected toolkit
         * @return 
         *          the selected toolkit
         */
        private Toolkit getSelectedToolkit() {
            String[] selected = m_list.getSelection();
            if (selected.length == 1) {
                String toolkitName = selected[0];
                int i = m_toolkits.indexOf(
                        new Toolkit(toolkitName));
                return m_toolkits.get(i);
            }
            return null;
        }
        
        
    }
}
