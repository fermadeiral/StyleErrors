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
package org.eclipse.jubula.client.teststyle.properties;

import java.util.Map;

import javax.persistence.EntityManager;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.model.ICheckConfContPO;
import org.eclipse.jubula.client.core.model.ICheckConfPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.teststyle.ExtensionHelper;
import org.eclipse.jubula.client.teststyle.TeststyleHandler;
import org.eclipse.jubula.client.teststyle.checks.BaseCheck;
import org.eclipse.jubula.client.teststyle.checks.Severity;
import org.eclipse.jubula.client.teststyle.i18n.Messages;
import org.eclipse.jubula.client.teststyle.properties.dialogs.attributes.EditAttributeDialog;
import org.eclipse.jubula.client.teststyle.properties.dialogs.contexts.EditContextDialog;
import org.eclipse.jubula.client.teststyle.properties.nodes.CategoryNode;
import org.eclipse.jubula.client.teststyle.properties.nodes.CheckNode;
import org.eclipse.jubula.client.teststyle.properties.nodes.INode;
import org.eclipse.jubula.client.teststyle.properties.nodes.INode.TreeState;
import org.eclipse.jubula.client.teststyle.properties.provider.TeststyleBoxProvider;
import org.eclipse.jubula.client.teststyle.properties.provider.TeststyleContentProvider;
import org.eclipse.jubula.client.teststyle.properties.provider.TeststyleLabelProvider;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.properties.AbstractProjectPropertyPage;
import org.eclipse.jubula.client.ui.rcp.properties.ProjectGeneralPropertyPage.IOkListener;
import org.eclipse.jubula.client.ui.widgets.UIComponentHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;


/**
 * @author marcell
 * @created Oct 18, 2010
 */
public class TeststyleProjectPropertyPage extends AbstractProjectPropertyPage
        implements IOkListener {
    // Constants
    /** Style of the treeviewer */
    private static final int TREE_STYLE = SWT.SINGLE | SWT.BORDER
            | SWT.FULL_SELECTION;
    /** Text of the teststyle configuration group */
    private static final String TS_GROUP = Messages.PropertyFullGroup;
    /** Text of the enablement checkbox */
    private static final String CHK_ENABLED = Messages.PropertyEnableRadio;
    /** Text of the group */
    private static final String GRP_TXT = Messages.PropertyEditGroup;
    /** Text of the button which edits attributes */
    private static final String BTN_ATTR = Messages.PropertyButtonAttribute;
    /** Text of the button which edits contexts */
    private static final String BTN_CONT = Messages.PropertyButtonContext;
    /** Text of the button which selects everything */
    private static final String BTN_SELECT_ALL = 
        Messages.PropertyButtonSelectAll;
    /** Text of the button which edits contexts */
    private static final String BTN_DESELECT_ALL = 
        Messages.PropertyButtonDeselectAll;
    /** Text that is in the description label */
    private static final String LBL_DESCRIPTION = 
        Messages.PropertyLabelDescription;
    
    /** ContextHelpId for TESTSTYLE_PROPERTY_PAGE */
    private static final String TESTSTYLE_PROPERTY_PAGE = ContextHelpIds.PRAEFIX
            + "testStylePropertyPageContextId"; //$NON-NLS-1$

    // The gui components of the property page
    /** The composite where all the elements are stored */
    private Composite m_composite;
    /** checkbox that en- or disables teststyle */
    private Button m_enabledCheckbox;
    /** The group where all the important elements are stored */
    private Group m_fullGroup;
    /** The CheckBoxTreeViewer which contains the checks and categories */
    private CheckboxTreeViewer m_treeView;
    /** The composite which contains the tree with the buttons */
    private Composite m_treeComposite;
    /** button for selecting everything */
    private Button m_selectAllBtn;
    /** button for select nothing */
    private Button m_deselectAllBtn;
    /** The button for editing the attributes */
    private Button m_editAttrBtn;
    /** The button for editing the contexts */
    private Button m_editContBtn;
    /** Combobox for editing the severity of a check */
    private Combo m_editSeverity;
    /** textfield for the description */
    private StyledText m_descriptionText;

    // The content provider
    /** The content provider for the tree */
    private TeststyleContentProvider m_treeContentProvider;
    /** The label provider for the tree */
    private TeststyleLabelProvider m_treeLabelProvider;
    /** The check provider for the tree */
    private TeststyleBoxProvider m_treeCheckProvider;

    // temporary variables that will be synchronized when accepting the project
    // properties
    /** is enabled */
    private boolean m_enabled;

    /**
     * {@inheritDoc}
     */
    protected Control createContents(Composite parent) {
        m_composite = PropUtils.createCustomComposite(parent);
        m_enabled = TeststyleHandler.getInstance().isEnabled();

        // First we create all elements
        createProvider();
        createEnabledCheckbox();
        createFullGroup();
        createTreeComposite();
        createGroup();
        createDescriptionField();

        // Then we set their events
        setTreeEvents();
        setGrpEvents();

        // set default enablement
        setTeststyleEnabled(m_enabled);

        return parent;
    }

    /**
     * 
     */
    private void createDescriptionField() {
        Label lbl = new Label(m_fullGroup, SWT.NULL);
        lbl.setText(LBL_DESCRIPTION);
        GridData lblData = new GridData();
        lblData.horizontalSpan = 2;
        lblData.grabExcessHorizontalSpace = true;
        lbl.setLayoutData(lblData);

        m_descriptionText = new StyledText(m_fullGroup, SWT.MULTI
                | SWT.READ_ONLY | SWT.BORDER | SWT.WRAP);
        m_descriptionText.setCursor(getShell().getDisplay().getSystemCursor(
                SWT.CURSOR_ARROW));
        m_descriptionText.setCaret(null);

        GridData txtData = new GridData(SWT.DEFAULT, 55);
        txtData.horizontalSpan = 2;
        txtData.grabExcessHorizontalSpace = true;
        txtData.horizontalAlignment = SWT.FILL;
        m_descriptionText.setBackground(getShell().getDisplay().getSystemColor(
                SWT.COLOR_WIDGET_BACKGROUND));
        m_descriptionText.setLayoutData(txtData);
    }

    /**
     * 
     */
    private void createEnabledCheckbox() {
        m_enabledCheckbox = new Button(m_composite, SWT.CHECK);
        m_enabledCheckbox.setText(CHK_ENABLED);
        m_enabledCheckbox.setSelection(m_enabled);
        m_enabledCheckbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                m_enabled = m_enabledCheckbox.getSelection();
                setTeststyleEnabled(m_enabled);
                getApplyButton().setEnabled(true);
            }
        });
    }

    /**
     * 
     */
    private void createFullGroup() {
        // Create the layout
        GridLayout layout = new GridLayout();
        layout.marginHeight = PropUtils.MARGIN_HEIGHT;
        layout.marginWidth = PropUtils.MARGIN_WIDTH;
        layout.numColumns = PropUtils.NUM_COLUMNS;

        // Create the GridData for this composite
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.heightHint = PropUtils.WIDTH;
        data.widthHint = PropUtils.HEIGHT;

        m_fullGroup = new Group(m_composite, SWT.NULL);
        m_fullGroup.setText(TS_GROUP);

        m_fullGroup.setLayout(layout);
        m_fullGroup.setLayoutData(data);
    }

    /**
     * Overwritten due to the standard enablement of the apply button.
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        super.createControl(parent);

        // disable the apply button by default
        getApplyButton().setEnabled(false);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(
                parent, TESTSTYLE_PROPERTY_PAGE);
    }

    /**
     * Creates all providers this property page uses.
     */
    private void createProvider() {
        /* The content provider for the tree */
        m_treeContentProvider = new TeststyleContentProvider();
        /* The label provider for the tree */
        m_treeLabelProvider = new TeststyleLabelProvider();
        /* The check provider for the tree */
        m_treeCheckProvider = new TeststyleBoxProvider();
    }

    /**
     * Creates the tree where the categories and the checks are stored
     * afterwards.
     */
    private void createTreeComposite() {
        // First create the griddata, so that the new element fits nicely
        GridData compGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        GridData treeGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        treeGridData.horizontalSpan = 2;
        GridData btn1GridData = new GridData(SWT.END, SWT.FILL, true, false);
        GridData btn2GridData = new GridData(SWT.END, SWT.FILL, false, false);

        // Then create the composite and use the new griddata
        m_treeComposite = new Composite(m_fullGroup, SWT.NONE);

        // Set the layout of the composite
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0; // no borders
        layout.marginWidth = 0; // no borders
        layout.numColumns = 2; // 2 columns so that the button are correct
        m_treeComposite.setLayout(layout);
        m_treeComposite.setLayoutData(compGridData);

        // Create the tree on the approriate place
        m_treeView = new CheckboxTreeViewer(m_treeComposite, TREE_STYLE);
        m_treeView.getTree().setLayoutData(treeGridData);

        // create the two buttons for selecting everything or nothing
        m_selectAllBtn = new Button(m_treeComposite, SWT.PUSH);
        m_selectAllBtn.setText(BTN_SELECT_ALL);
        m_selectAllBtn.setLayoutData(btn1GridData);
        m_deselectAllBtn = new Button(m_treeComposite, SWT.PUSH);
        m_deselectAllBtn.setText(BTN_DESELECT_ALL);
        m_deselectAllBtn.setLayoutData(btn2GridData);

        // The next methods fills the tree with stuff
        m_treeView.setContentProvider(m_treeContentProvider);
        m_treeView.setLabelProvider(m_treeLabelProvider);
        m_treeView.setCheckStateProvider(m_treeCheckProvider);
        m_treeView.setInput(PropUtils.getCategoriesAsNodes());

        // Set the default expansion to two, so that the checks are visible
        m_treeView.setAutoExpandLevel(2);
        m_treeView.expandToLevel(2);
    }

    /**
     * Creates the Group with the buttons for specific editing purposes.
     */
    private void createGroup() {
        // First create the griddata, so that the new group fits nicely
        Composite cmp = new Composite(m_fullGroup, SWT.NONE);
        cmp.setLayoutData(new GridData(SWT.NULL, SWT.FILL, false, false));
        cmp.setLayout(new FillLayout(SWT.VERTICAL));

        // Then create the new group
        Group grp = new Group(cmp, SWT.NULL);
        grp.setText(GRP_TXT);
        grp.setLayout(new GridLayout(1, false));
        // grp.setLayoutData();

        // Now puts the buttons and the radio thing in the group, first for
        // editing the attributes
        m_editAttrBtn = new Button(grp, SWT.PUSH);
        m_editAttrBtn.setText(BTN_ATTR);
        m_editAttrBtn.setEnabled(false);

        // Then for editing the contexts.
        m_editContBtn = new Button(grp, SWT.PUSH);
        m_editContBtn.setText(BTN_CONT);
        m_editContBtn.setEnabled(false);

        // Then for editing the severity
        m_editSeverity = new Combo(grp, SWT.DROP_DOWN | SWT.READ_ONLY);
        String[] items = new String[Severity.values().length];
        for (int i = 0; i < items.length; i++) {
            items[i] = Severity.values()[i].toString();
        }
        m_editSeverity.setItems(items);
        m_editSeverity.setEnabled(false);

    }

    /**
     * Sets the events for the tree.
     */
    public void setTreeEvents() {

        m_treeView.addCheckStateListener(new ICheckStateListener() {
            @SuppressWarnings("synthetic-access")
            public void checkStateChanged(CheckStateChangedEvent event) {
                INode node = (INode) event.getElement();
                if (event.getChecked()) {
                    node.setState(TreeState.CHECKED);
                } else {
                    node.setState(TreeState.EMPTY);
                }
                // refreshing the tree where its needed
                if (node instanceof CategoryNode) {
                    for (INode child : node.getChildren()) {
                        m_treeView.refresh(child);
                    }
                    m_treeView.refresh(node);
                } else if (node instanceof CheckNode) {
                    m_treeView.refresh(node.getParent());
                }
                m_treeView.setSelection(new StructuredSelection(node));
                getApplyButton().setEnabled(true);
            }
        });

        m_treeView.addSelectionChangedListener(new ISelectionChangedListener() {
            @SuppressWarnings("synthetic-access")
            public void selectionChanged(SelectionChangedEvent event) {

                // This events comes from a tree, so its a treeselection
                ITreeSelection treeSel = (ITreeSelection) event.getSelection();

                // Is it empty? If not, is it a node which contains a check?
                boolean empty = treeSel.isEmpty();
                INode selected = (INode) treeSel.getFirstElement();

                boolean isEditable = !empty && selected.isEditable();
                boolean hasSeverity = !empty && selected.hasSeverity();

                m_editAttrBtn.setEnabled(isEditable);
                m_editContBtn.setEnabled(isEditable);
                m_editSeverity.setEnabled(hasSeverity);

                if (hasSeverity) {
                    m_editSeverity.setText(((CheckNode) selected).getSeverity()
                            .name());
                }
                m_descriptionText.setText(selected.getTooltip());
            }
        });

        m_selectAllBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                selectAll();
            }
        });

        m_deselectAllBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                deselectAll();
            }
        });
    }

    /**
     * Selects every element in the tree.
     */
    void selectAll() {
        INode[] categories = (INode[]) m_treeContentProvider.getElements(null);
        for (INode category : categories) {
            category.setState(TreeState.CHECKED);
        }
        m_treeView.refresh();
        getApplyButton().setEnabled(true);
    }

    /**
     * Deselects every element in the tree.
     */
    void deselectAll() {
        INode[] categories = (INode[]) m_treeContentProvider.getElements(null);
        for (INode category : categories) {
            category.setState(TreeState.EMPTY);
        }
        m_treeView.refresh();
        getApplyButton().setEnabled(true);
    }

    /**
     * Sets the events for the buttons.
     */
    public void setGrpEvents() {
        m_editAttrBtn.addSelectionListener(new SelectionAdapter() {
            @SuppressWarnings("synthetic-access")
            public void widgetSelected(SelectionEvent e) {
                ITreeSelection sel = (ITreeSelection) m_treeView.getSelection();
                CheckNode node = (CheckNode) sel.getFirstElement();
                BaseCheck check = node.getCheck();
                Dialog dlg = new EditAttributeDialog(getShell(), check);
                int status = dlg.open();
                if (status == Window.OK) {
                    getApplyButton().setEnabled(true);
                }
            }
        });

        m_editContBtn.addSelectionListener(new SelectionAdapter() {
            @SuppressWarnings("synthetic-access")
            public void widgetSelected(SelectionEvent e) {
                ITreeSelection sel = (ITreeSelection) m_treeView.getSelection();
                CheckNode node = (CheckNode) sel.getFirstElement();
                BaseCheck check = node.getCheck();
                Dialog dlg = new EditContextDialog(getShell(), check);
                int status = dlg.open();
                if (status == Window.OK) {
                    getApplyButton().setEnabled(true);
                }
            }
        });

        m_editSeverity.addSelectionListener(new SelectionAdapter() {
            @SuppressWarnings("synthetic-access")
            public void widgetSelected(SelectionEvent e) {
                Severity newSev = Severity.valueOf(m_editSeverity.getText());
                ITreeSelection sel = (ITreeSelection) m_treeView.getSelection();
                CheckNode node = (CheckNode) sel.getFirstElement();
                node.setSeverity(newSev);
                getApplyButton().setEnabled(true);
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    protected void performApply() {
        okPressed();
        getApplyButton().setEnabled(false);
    }

    /**
     * {@inheritDoc}
     */
    public void okPressed() {
        if (m_treeContentProvider != null) {
            EntityManager s = getEditSupport().getSession();
            IProjectPO project = GeneralStorage.getInstance().getProject();
            ICheckConfContPO cfg = s.merge(project.getProjectProperties()
                    .getCheckConfCont());
            cfg.setEnabled(m_enabled);
            m_treeContentProvider.save(s);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void applyData(Object data) {
        super.applyData(data);
        if (data instanceof String) {
            INode nodeToSelect = null;
            String checkstyleID = (String)data;
            Object[] allExpandedElements = m_treeView.getExpandedElements();
            for (Object o : allExpandedElements) {
                if (o instanceof CategoryNode) {
                    CategoryNode catNode = (CategoryNode)o;
                    for (INode node : catNode.getChildren()) {
                        if (node instanceof CheckNode) {
                            CheckNode cn = (CheckNode)node;
                            if (cn.getCheck().getId().equals(checkstyleID)) {
                                nodeToSelect = cn;
                                break;
                            }
                        }
                    }
                    if (nodeToSelect != null) {
                        break;
                    }
                }
            }
            m_treeView
                    .setSelection(new StructuredSelection(nodeToSelect), true);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    protected void performDefaults() {
        Map<String, ICheckConfPO> defaults = ExtensionHelper.getDefaults();
        INode[] categories = (INode[]) m_treeContentProvider.getElements(null);
        for (INode category : categories) {
            for (INode child : category.getChildren()) {
                CheckNode checkNode = (CheckNode) child;
                ICheckConfPO defaultCfg = defaults.get(checkNode.getCheck()
                        .getId());
                ICheckConfPO currentCfg = checkNode.getCheck().getConf();

                currentCfg.setActive(defaultCfg.isActive());
                currentCfg.setSeverity(defaultCfg.getSeverity());
                currentCfg.setAttr(defaultCfg.getAttr());
                currentCfg.setContexts(defaultCfg.getContexts());

                if (checkNode.hasSeverity()) {
                    m_editSeverity.setText(checkNode.getSeverity().name());
                }
            }
        }
        m_treeView.refresh();
        getApplyButton().setEnabled(true);
    }

    /**
     * @param enabled
     *            If teststyle is enabled for this project or not.
     */
    private void setTeststyleEnabled(boolean enabled) {
        UIComponentHelper.setEnabledRecursive(m_fullGroup, enabled);
        m_editAttrBtn.setEnabled(false);
        m_editContBtn.setEnabled(false);
        m_editSeverity.setEnabled(false);
        m_treeView.getTree().setSelection(new TreeItem[] {});
    }

}
