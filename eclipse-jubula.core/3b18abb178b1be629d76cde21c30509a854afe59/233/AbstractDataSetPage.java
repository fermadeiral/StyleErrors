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
package org.eclipse.jubula.client.ui.rcp.views.dataset;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jubula.client.core.businessprocess.AbstractParamInterfaceBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.events.DataChangedEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IParamChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectLoadedListener;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IDataSetPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ITDManager;
import org.eclipse.jubula.client.core.model.ITestDataCategoryPO;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.utils.GuiParamValueConverter;
import org.eclipse.jubula.client.core.utils.IParamValueValidator;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.businessprocess.TextControlBP;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.jubula.client.ui.rcp.editors.JBEditorHelper;
import org.eclipse.jubula.client.ui.rcp.factory.TestDataControlFactory;
import org.eclipse.jubula.client.ui.rcp.filter.DataSetFilter;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.SearchResultElement;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedParamText;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedParamTextContentAssisted;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.tools.internal.constants.CharacterConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.SwtToolkitConstants;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.utils.IsAliveThread;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.progress.WorkbenchJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract base class for data set pages
 *
 * @author BREDEX GmbH
 * @created Jul 13, 2010
 */
@SuppressWarnings("synthetic-access") 
public abstract class AbstractDataSetPage extends Page 
    implements ISelectionListener, IAdaptable, IParamChangedListener,
               IProjectLoadedListener, IDataChangedListener {

    /** Constant for the width of the DataSet column in the table */
    protected static final int DATASET_NUMBER_COLUMNWIDTH = 30;
    /** Constant for the default column width */ 
    protected static final int COLUMN_WIDTH = 140;

    /** The log */
    private static final Logger LOG = LoggerFactory.getLogger(
            AbstractDataSetPage.class);

    /** Search delay in millisecond */
    private static final long SEARCH_DELAY = 200;
    
    /** Image descriptor for enabled clear button.*/
    private static final String CLEAR_ICON = "org.eclipse.ui.internal.dialogs.CLEAR_ICON"; //$NON-NLS-1$
    /** Image descriptor for disabled clear button. */
    private static final String DISABLED_CLEAR_ICON = "org.eclipse.ui.internal.dialogs.DCLEAR_ICON"; //$NON-NLS-1$
    /** Image for the disabled clear button */
    private static Image inactiveImage = null;
    /** Image for the enabled clear button */
    private static Image activeImage = null;
    /** Image for the bressed clear button */
    private static Image pressedImage = null;
    /** The preference store to hold the existing preference values. */
    private IPreferenceStore m_store = Plugin.getDefault().getPreferenceStore();
    
    static {
        ImageRegistry imgReg = JFaceResources.getImageRegistry();
        Display display = getDisplay();
        
        if (imgReg != null && display != null) {
            ImageDescriptor disabledClearIcon = imgReg
                    .getDescriptor(DISABLED_CLEAR_ICON);
            ImageDescriptor clearIcon = imgReg
                    .getDescriptor(CLEAR_ICON);
            
            if (disabledClearIcon != null) {
                inactiveImage = disabledClearIcon.createImage();
            } else {
                LOG.error("Filter: \"Disabled Clear Icon\"-Descriptor could not be found!"); //$NON-NLS-1$
            }
            
            if (clearIcon != null) {
                activeImage = clearIcon.createImage();
                
                if (activeImage != null) {
                    pressedImage = new Image(display, activeImage,
                            SWT.IMAGE_GRAY);
                } else {
                    LOG.error("Filter: \"Pressed Clear Icon\" could not be created!"); //$NON-NLS-1$
                }
            } else {
                LOG.error("Filter: \"Clear Icon\"-Descriptor could not be found!"); //$NON-NLS-1$
            }
        }
    }

    /** The data set filter */
    private DataSetFilter m_filter;
    
    /** Filter text field */
    private Text m_searchText;
    
    /** The current IParameterInterfacePO */
    private IParameterInterfacePO m_paramInterfaceObj;
    
    /** the primary control for this page */
    private Control m_control;
    /** The TableViewer for this view */
    private TableViewer m_tableViewer;
    /** the tableCursor */
    private DSVTableCursor m_tableCursor;
    
    /** The Add-Button */
    private Button m_addButton;
    /** The Insert Button */
    private Button m_insertButton;
    /** The Delete Button */
    private Button m_deleteButton;
    /** The Up Button */
    private Button m_upButton;
    /** The Down Button */
    private Button m_downButton;
    /** The Clear Filter Button */
    private Label m_clearButton;
    
    /** En-/Disabler for swt.Controls */
    private ControlEnabler m_controlEnabler;
    /** bp class */
    private AbstractParamInterfaceBP m_paramBP;
    
    /** the corresponding part */
    private IWorkbenchPart m_currentPart;
    /** The current selection */
    private IStructuredSelection m_currentSelection;
    
    /** The current param's id */
    private Long m_paramId;
    /** The column's widths */
    private int[] m_columnWidths;
    /** The current parameters count */
    private int m_columnCount;
    
    /** Constants for the button actions */
    private enum TestDataRowAction { 
        /** Add button clicked */
        ADDED, 
        /** Insert button clicked */
        INSERTED, 
        /** Delete button clicked */
        DELETED, 
        /** Up button clicked */
        MOVED_UP, 
        /** Down button clicked */
        MOVED_DOWN 
    }

    /**
     *  The constructor
     *  @param bp the business process to use for this page
     */
    public AbstractDataSetPage(AbstractParamInterfaceBP bp) {
        setParamBP(bp);
    }

    /**
     * Abstract class for ContentProviders
     * 
     * @author BREDEX GmbH
     * @created 04.04.2006
     */
    private abstract static class AbstractContentProvider implements
            IStructuredContentProvider {
        /** {@inheritDoc} */
        public Object[] getElements(Object inputElement) {
            return new Object[0];
        }

        /** {@inheritDoc} */
        public void dispose() {
        // nothing
        }

        /** {@inheritDoc} */
        public void inputChanged(Viewer viewer, Object oldInput, 
                Object newInput) {
        // nothing
        }
    }
    
    /**
     * Abstract class for ITableLabelProvider
     * @author BREDEX GmbH
     * @created 04.04.2006
     */
    private abstract class AbstractLabelProvider 
        implements ITableLabelProvider, IColorProvider {
        /** {@inheritDoc} */
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
        
        /** {@inheritDoc} */
        public String getColumnText(Object element, int columnIndex) {
            return StringConstants.EMPTY;
        }
        
        /** {@inheritDoc} */
        public void addListener(ILabelProviderListener listener) {
            // nothing
        }

        /** {@inheritDoc} */
        public void dispose() {
            // nothing
        }

        /** {@inheritDoc} */
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        /** {@inheritDoc} */
        public void removeListener(ILabelProviderListener listener) {
            // nothing
        }
        
        /**
         * {@inheritDoc}
         */
        public Color getBackground(Object element) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public Color getForeground(Object element) {
            if (!getControlEnabler().canEdit()) {
                return LayoutUtil.GRAY_COLOR;
            }
            return null;
        }
    }
    
    /**
     * @param tableViewer the tableViewer to set
     */
    private void setTableViewer(TableViewer tableViewer) {
        m_tableViewer = tableViewer;
    }

    /**
     * @return the tableViewer
     */
    private TableViewer getTableViewer() {
        return m_tableViewer;
    }
    
    /**
     * @return the tableViewers table control
     */
    private Table getTable() {
        return getTableViewer().getTable();
    }
    
    /**
     * checks the combo selection. Call after any button action!
     * @param action the action of the button
     * @param row the row on which the action was performed
     */    
    private void checkComboSelection(TestDataRowAction action, int row) {
        getTableViewer().refresh();
    }

    
    /**
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        Composite topLevelComposite = new Composite(parent, SWT.NONE);
        topLevelComposite.setData(SwtToolkitConstants.WIDGET_NAME,
                "DataSetViewPage"); //$NON-NLS-1$
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.verticalSpacing = 2;
        layout.marginWidth = LayoutUtil.MARGIN_WIDTH;
        layout.marginHeight = LayoutUtil.MARGIN_HEIGHT;
        topLevelComposite.setLayout(layout);
        GridData layoutData = new GridData(GridData.FILL_BOTH);
        layoutData.grabExcessHorizontalSpace = true;
        topLevelComposite.setLayoutData(layoutData);
        m_control = topLevelComposite;

        Composite buttonComp = new Composite(topLevelComposite, SWT.BORDER);

        // Set numColumns to 2 for the buttons
        layout = new GridLayout(2, false);
        layout.marginWidth = 1;
        layout.marginHeight = 1;
        buttonComp.setLayout(layout);

        // Create a composite to hold the children
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        buttonComp.setLayoutData(gridData);
        
        initTableViewer(buttonComp); 
        createButtons(buttonComp);
        Plugin.getHelpSystem().setHelp(getTable(),
                ContextHelpIds.JB_DATASET_VIEW);
    }

    /**
     * Add the "Add", "Delete" and "Insert" buttons
     * @param parent the parent composite
     */
    private void createButtons(Composite parent) {
        
        Composite bottomComp = new Composite(parent, SWT.NONE);

        // Set numColumns to 3 for the buttons
        GridLayout layout = new GridLayout(5, false);
        layout.marginWidth = 5;
        layout.marginHeight = 5;
        layout.verticalSpacing = 2;
        GridData gridDataBottom = new GridData(SWT.NONE, SWT.NONE, 
                true, false);
        gridDataBottom.horizontalAlignment = GridData.FILL;
        gridDataBottom.horizontalSpan = 3;
        bottomComp.setLayoutData(gridDataBottom);
        bottomComp.setLayout(layout);
        
        // Create and configure the "Add" button
        setAddButton(new Button(bottomComp, SWT.PUSH | SWT.CENTER));
        getAddButton().setData(SwtToolkitConstants.WIDGET_NAME, "DataSetView.AddButton"); //$NON-NLS-1$
        getAddButton().setText(Messages.JubulaDataSetViewAppend);
        GridData gridData = new GridData (GridData.HORIZONTAL_ALIGN_BEGINNING);
        gridData.widthHint = 80;
        getAddButton().setLayoutData(gridData);
        
        // Create and configure the "Insert" button
        setInsertButton(new Button(bottomComp, SWT.PUSH | SWT.CENTER));
        getInsertButton().setData(SwtToolkitConstants.WIDGET_NAME, "DataSetView.InsertButton"); //$NON-NLS-1$
        getInsertButton().setText(Messages.DataSetViewInsert);
        gridData = new GridData (GridData.HORIZONTAL_ALIGN_BEGINNING);
        gridData.widthHint = 80;
        getInsertButton().setLayoutData(gridData);
        
        //  Create and configure the "Delete" button
        setDeleteButton(new Button(bottomComp, SWT.PUSH | SWT.CENTER));
        getDeleteButton().setData(SwtToolkitConstants.WIDGET_NAME, "DataSetView.DeleteButton"); //$NON-NLS-1$
        getDeleteButton().setText(Messages.JubulaDataSetViewDelete);
        gridData = new GridData (GridData.HORIZONTAL_ALIGN_BEGINNING);
        gridData.grabExcessHorizontalSpace = true;
        gridData.widthHint = 80; 
        getDeleteButton().setLayoutData(gridData); 
        
        // Create and configure the "Down" button
        setDownButton(new Button(bottomComp, SWT.PUSH | SWT.CENTER));
        getDownButton().setData(SwtToolkitConstants.WIDGET_NAME, "DataSetView.DownButton"); //$NON-NLS-1$
        getDownButton().setImage(IconConstants.DOWN_ARROW_IMAGE);
        gridData = new GridData (GridData.HORIZONTAL_ALIGN_END);
        getDownButton().setLayoutData(gridData);

        // Create and configure the "Up" button
        setUpButton(new Button(bottomComp, SWT.PUSH | SWT.CENTER));
        getUpButton().setData(SwtToolkitConstants.WIDGET_NAME, "DataSetView.UpButton"); //$NON-NLS-1$
        getUpButton().setImage(IconConstants.UP_ARROW_IMAGE);
        gridData = new GridData (GridData.HORIZONTAL_ALIGN_END);
        getUpButton().setLayoutData(gridData);
        
        addListenerToButtons();
        getControlEnabler().setControlsEnabled();
    }

    /**
     * @return the Display instance
     */
    private static Display getDisplay()  {
        Display display = Display.getCurrent();
        if (display == null) {
            display = Display.getDefault();
        }
        return display;
    }
    
    /**
     * Creates and configures the "Clear Filter" button
     * @param parent the parent composite
     */
    private void createClearFilterButton(Composite parent) {
        setClearButton(new Label(parent, SWT.NONE));
        getClearButton().setData(SwtToolkitConstants.WIDGET_NAME, "DataSetView.ClearFilterButton"); //$NON-NLS-1$
        getClearButton().setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
                false, false));
        getClearButton().setImage(inactiveImage);
        getClearButton().setBackground(parent.getDisplay()
                .getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        getClearButton().setToolTipText(Messages.DataSetClearFilterButton);
        
        getClearButton().addMouseListener(new MouseAdapter() {
            private MouseMoveListener m_moveListener;
            @Override
            public void mouseDown(MouseEvent e) {
                getClearButton().setImage(pressedImage);
                m_moveListener = new MouseMoveListener() {
                    private boolean m_isMouseInButton = true;
                    @Override
                    public void mouseMove(MouseEvent e2) {
                        boolean isMouseInButton = isMouseInButton(e2);
                        if (isMouseInButton != m_isMouseInButton) {
                            m_isMouseInButton = isMouseInButton;
                            getClearButton().setImage(isMouseInButton
                                    ? pressedImage : inactiveImage);
                        }
                    }
                };
                getClearButton().addMouseMoveListener(m_moveListener);
            }
            @Override
            public void mouseUp(MouseEvent e) {
                if (m_moveListener != null) {
                    getClearButton().removeMouseMoveListener(m_moveListener);
                    m_moveListener = null;
                    boolean isMouseInButton = isMouseInButton(e);
                    getClearButton().setImage(isMouseInButton ? activeImage
                            : inactiveImage);
                    if (isMouseInButton) {
                        m_searchText.setText(StringConstants.EMPTY);
                        m_searchText.setFocus();
                    }
                }
            }
            private boolean isMouseInButton(MouseEvent e) {
                Point buttonSize = getClearButton().getSize();
                return 0 <= e.x && e.x < buttonSize.x && 0 <= e.y 
                        && e.y < buttonSize.y;
            }
        });
        getClearButton().addMouseTrackListener(new MouseTrackAdapter() {
            @Override
            public void mouseEnter(MouseEvent e) {
                getClearButton().setImage(activeImage);
            }
            @Override
            public void mouseExit(MouseEvent e) {
                getClearButton().setImage(inactiveImage);
            }
        });
    }
    
    /**
     * @param parent a parent composite
     * @return whether or not the current OS supports a native filter
     */
    private boolean useNativeFilter(Composite parent) {
        Text tempText = new Text(parent, SWT.SINGLE | SWT.BORDER
                | SWT.SEARCH | SWT.ICON_CANCEL);
        boolean usesNative = ((tempText.getStyle() & SWT.ICON_CANCEL) != 0);
        tempText.dispose();
        return usesNative;
    }
    
    /**
     * inits the m_tableViewer
     * @param parent the parent of the m_tableViewer
     */
    private void initTableViewer(Composite parent) {
        createFilter(parent);
        createTableViewer(parent);
    }

    /**
     * Creates the filter and its controls
     * @param parent the parent composite
     */
    private void createFilter(Composite parent) {
        m_filter = new DataSetFilter();
        Composite filterComposite;
        boolean usesNativeFilter = useNativeFilter(parent);
        
        if (!usesNativeFilter) {
            filterComposite = new Composite(parent, SWT.BORDER);
        } else {
            filterComposite = new Composite(parent, SWT.NONE);
        }
        
        filterComposite.setBackground(getDisplay()
                .getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        GridLayout filterLayout = new GridLayout(2, false);
        filterLayout.marginHeight = 0;
        filterLayout.marginWidth = 0;
        filterComposite.setLayout(filterLayout);
        filterComposite.setFont(parent.getFont());
        GridData searchTextGridData = new GridData(SWT.FILL, SWT.CENTER,
                true, false);
        
        if (!usesNativeFilter) {
            m_searchText = new Text(filterComposite, SWT.SINGLE);
            m_searchText.setLayoutData(searchTextGridData);
            createClearFilterButton(filterComposite);
        } else {
            m_searchText = new Text(filterComposite, SWT.SINGLE | SWT.BORDER
                    | SWT.SEARCH | SWT.ICON_CANCEL);
            searchTextGridData.horizontalSpan = 2;
            m_searchText.setLayoutData(searchTextGridData);
        }
        
        updateClearButtonVisibility(false);
        filterComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
                true, false));
    }
    
    /**
     * Creates the TableViewer
     * @param parent the parent composite
     */
    private void createTableViewer(Composite parent) {
        TableViewer viewer = new TableViewer(parent, 
                SWT.SINGLE | SWT.FULL_SELECTION);
        m_searchText.setMessage(WorkbenchMessages.FilteredTree_FilterMessage);
        final Job searchJob = createSearchJob();
        m_searchText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (m_currentPart instanceof AbstractJBEditor) {
                    if (m_searchText.getText().isEmpty()) {
                        updateClearButtonVisibility(false);
                        updateBackgroundColor(false);
                    } else {
                        updateClearButtonVisibility(true);
                        updateBackgroundColor(true);
                    }
                    getControlEnabler().setControlsEnabled();
                } else {
                    updateClearButtonVisibility(!m_searchText
                            .getText().isEmpty());
                }
                searchJob.cancel();
                searchJob.schedule(SEARCH_DELAY);
            }
        });
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                getControlEnabler().setControlsEnabled();
            }
        });
        setTableViewer(viewer);
        Table table = getTable();
        table.setData(SwtToolkitConstants.WIDGET_NAME, "DataSetView.DataTable"); //$NON-NLS-1$
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalSpan = 3;
        table.setLayoutData(gridData);
        getTableViewer().setUseHashlookup(true);
        getTableViewer().setContentProvider(new GeneralContentProvider());
        getTableViewer().setLabelProvider(new GeneralLabelProvider());
        getTableViewer().addFilter(m_filter);
        setTableCursor(new DSVTableCursor(getTable(), SWT.NONE));
    }
    
    /**
     * Updates the background color when filtering
     * @param isActive whether filtering is active or not
     */
    private void updateBackgroundColor(boolean isActive) {
        if (isActive && m_store.getBoolean(Constants.BACKGROUND_COLORING_KEY)) {
            getTableViewer().getControl().setBackground(
                new Color(Display.getCurrent(), Utils.intToRgb(
                    m_store.getInt(Constants.BACKGROUND_COLOR_KEY))));
        } else {
            getTableViewer().getControl().setBackground(
                Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        }
    }

    /**
     * @param isVisible whether the clear filter button should be visible or not
     */
    private void updateClearButtonVisibility(boolean isVisible) {
        if (getClearButton() != null) {
            getClearButton().setVisible(isVisible);
        }
    }
    
    /**
     * @return an search job
     */
    protected WorkbenchJob createSearchJob() {
        WorkbenchJob job = new WorkbenchJob("Refresh Filter") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                m_filter.setSearchText(m_searchText.getText());
                getTableViewer().refresh();
                return Status.OK_STATUS;
            }
        };
        job.setSystem(true);
        return job;
    }
    
    /**
     * Logs a button pressed event
     * @param but the button
     */
    @SuppressWarnings("nls")
    private void logButton(Button but) {
        if (!LOG.isDebugEnabled()) {
            return;
        }
        StringBuilder str = new StringBuilder();
        str.append("\nData Set View button \"");
        if (but == getDownButton()) {
            str.append("Down");
        } else if (but == getUpButton()) {
            str.append("Up");
        } else {
            str.append(but.getText());
        }
        str.append("\" pressed. The row number: ");
        str.append(getSelectedDataSet());
        str.append("\nThe data set owner is the node ");
        str.append(getParamInterfaceObj().getSpecificationUser().toString());
        str.append(".\n");
        LOG.debug(str.toString());
    }
    
    /**
     * add listener to buttons
     */
    private void addListenerToButtons() {
        getAddButton().addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                final int index = getSelectedDataSet();
                addDataSet();
                checkComboSelection(TestDataRowAction.ADDED, index);
                getControlEnabler().setControlsEnabled();
                logButton(getAddButton());
            }
        });
        getInsertButton().addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                final int index = getSelectedDataSet();
                insertDataSetAtCurrentSelection();
                checkComboSelection(TestDataRowAction.INSERTED, index);
                getControlEnabler().setControlsEnabled();
            }
        });
        getDeleteButton().addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                final int index = getSelectedDataSet();
                removeDataSet();
                checkComboSelection(TestDataRowAction.DELETED, index);
                getControlEnabler().setControlsEnabled();
                logButton(getDeleteButton());
            }
        });
        getUpButton().addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                final int index = getSelectedDataSet();
                moveDataSetUp();
                checkComboSelection(TestDataRowAction.MOVED_UP, index);
                getControlEnabler().setControlsEnabled();
                logButton(getUpButton());
            }
        });
        getDownButton().addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                final int index = getSelectedDataSet();
                moveDataSetDown();
                checkComboSelection(TestDataRowAction.MOVED_DOWN, index);
                getControlEnabler().setControlsEnabled();
                logButton(getDownButton());
            }
        });
    }
    
    
    /**
     * @return the index of the selected data set.
     */
    private int getSelectedDataSet() {
        int index = -1;
        try {
            index = Integer.parseInt(getTableViewer().getTable()
                    .getSelection()[0].getText(0)) - 1;
        } catch (Exception e) {
            // nothing
        }
        return index;
    }
    
    /**
     * Moves a data set one row down
     */
    private void moveDataSetDown() {
        final int row = getSelectedDataSet();
        moveDataSet(row, row + 1);
    }
    
    /**
     * Moves a data set one row up
     */
    private void moveDataSetUp() {
        final int row = getSelectedDataSet();
        moveDataSet(row, row - 1);
    }
    
    /**
     * Moves a data set one row down
     * @param fromIndex Position from where to move
     * @param toIndex Target position of the dataset
     */
    private void moveDataSet(int fromIndex, int toIndex) {
        final int rowCount = getParamInterfaceObj().getDataManager()
                .getDataSetCount();
        final AbstractJBEditor editor = (AbstractJBEditor)m_currentPart;
        if (editor.getEditorHelper().requestEditableState()
                == JBEditorHelper.EditableState.OK) {
            if (getParamInterfaceObj() instanceof IExecTestCasePO) {
                ITDManager man = ((IExecTestCasePO)getParamInterfaceObj())
                        .resolveTDReference();
                if (!man.equals(getTableViewer().getInput())) {
                    getTableViewer().setInput(man);
                }
            }
            ITDManager tdman = getParamInterfaceObj().getDataManager();
            if (fromIndex >= 0 && fromIndex < rowCount
                    && toIndex >= 0 && toIndex < rowCount) {
                IDataSetPO selectedDataSet = tdman.getDataSet(fromIndex);
                if (fromIndex > toIndex) {
                    tdman.insertDataSet(selectedDataSet, toIndex);
                    tdman.removeDataSet(fromIndex + 1);
                } else {
                    tdman.insertDataSet(selectedDataSet, toIndex + 1);
                    tdman.removeDataSet(fromIndex);
                }
                getTableCursor().setSelection(toIndex,
                        getTableCursor().getColumn());
                getTableViewer().refresh();
                DataEventDispatcher.getInstance().
                    fireParamChangedListener(this);
                editor.getEditorHelper().setDirty(true);
            }
        }
    }
    
    /**
     * Add a row as last element.
     */
    private void addDataSet() {
        final int rowCount = getParamInterfaceObj().getDataManager()
                .getDataSetCount();
        insertDataSet(rowCount);
    }
    
    /**
     * Inserts a new data set at the current selection in the table
     */
    private void insertDataSetAtCurrentSelection() {
        final int row = getSelectedDataSet();
        if (row >= 0) {
            insertDataSet(row);
        }
    }
    
    /**
     * Inserts a new data set at the given row
     * 
     * @param row
     *            the row to insert the new data set
     */
    private void insertDataSet(int row) {
        final AbstractJBEditor editor = (AbstractJBEditor)m_currentPart;
        if (editor.getEditorHelper().requestEditableState()
                == JBEditorHelper.EditableState.OK) {
            if (getParamInterfaceObj() instanceof IExecTestCasePO) {
                ITDManager man = ((IExecTestCasePO)getParamInterfaceObj())
                        .resolveTDReference();
                if (!man.equals(getTableViewer().getInput())) {
                    getTableViewer().setInput(man);
                }
            }
            if (row > -1) {
                getParamBP().addDataSet(getParamInterfaceObj(), row);
            } else {
                // if first data set is added
                addDataSet();
            }
            editor.getEditorHelper().setDirty(true);
            getTableViewer().refresh();
            int rowToSelect = row;
            if (rowToSelect == -1) {
                rowToSelect = getTable().getItemCount();
            } else {
                getTableCursor().setSelection(rowToSelect, 1);
                setFocus();
            }
            getTable().setSelection(rowToSelect);
            DataEventDispatcher.getInstance().fireParamChangedListener(this);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Control getControl() {
        return m_control;
    }
    
    /**
     * {@inheritDoc}
     */
    public <T> T getAdapter(Class<T> adapter) {
        return null;
    }
    
    /** 
     * {@inheritDoc}
     */
    public void setFocus() {
        getTable().setFocus();
    }

    /**
     * @return the controlEnabler
     */
    private ControlEnabler getControlEnabler() {
        if (m_controlEnabler == null) {
            m_controlEnabler = new ControlEnabler();
        }
        return m_controlEnabler;
    }

    /**
     * @param addButton the addButton to set
     */
    private void setAddButton(Button addButton) {
        m_addButton = addButton;
    }

    /**
     * @return the addButton
     */
    private Button getAddButton() {
        return m_addButton;
    }

    /**
     * @param insertButton the insertButton to set
     */
    private void setInsertButton(Button insertButton) {
        m_insertButton = insertButton;
    }

    /**
     * @return the insertButton
     */
    private Button getInsertButton() {
        return m_insertButton;
    }

    /**
     * @param deleteButton the deleteButton to set
     */
    private void setDeleteButton(Button deleteButton) {
        m_deleteButton = deleteButton;
    }

    /**
     * @return the deleteButton
     */
    private Button getDeleteButton() {
        return m_deleteButton;
    }
    
    /**
     * @param upButton the upButton to set
     */
    private void setUpButton(Button upButton) {
        m_upButton = upButton;
    }

    /**
     * @return the upButton
     */
    private Button getUpButton() {
        return m_upButton;
    }
    
    /**
     * @param downButton the downButton to set
     */
    private void setDownButton(Button downButton) {
        m_downButton = downButton;
    }

    /**
     * @return the downButton
     */
    private Button getDownButton() {
        return m_downButton;
    }
    
    /**
     * @param clearButton the clearButton to set
     */
    private void setClearButton(Label clearButton) {
        m_clearButton = clearButton;
    }
    
    /**
     * @return the clearButton
     */
    private Label getClearButton() {
        return m_clearButton;
    }

    /**
     * Clears the m_tableViewer
     */
    private void clearTableViewer() {
        getTable().removeAll();
        for (TableColumn column : getTable().getColumns()) {
            column.dispose();
        }
        DSVTableCursor tableCursor = getTableCursor();
        if (tableCursor != null && !tableCursor.isDisposed()) {
            tableCursor.dispose();
            setTableCursor(new DSVTableCursor(getTable(), SWT.NONE));
        }
    }
    
    
    /**
     * Inits and creates the column for the data set numbers
     * @return the name of the column
     */
    private String initDataSetColumn() {
        clearTableViewer();
        final Table table = getTable();
        // create column for data set numer
        TableColumn dataSetNumberCol = new TableColumn(table, SWT.NONE);
        dataSetNumberCol.setText(Messages.DataSetViewControllerDataSetNumber);
        if ((m_columnWidths != null && m_columnWidths.length > 0)
                && m_columnCount == table.getColumnCount()) {
            dataSetNumberCol.setWidth(m_columnWidths[0]);
        } else {
            dataSetNumberCol.setWidth(DATASET_NUMBER_COLUMNWIDTH);
        }
        return dataSetNumberCol.getText();
    }
    
    /**
     * Packs the table.
     */
    private void packTable() {
        final Table table = getTable();
        final TableColumn[] columns = table.getColumns();
        final int columnCount = columns.length;
        if ((m_columnWidths != null && m_columnWidths.length > 0)
                && m_columnCount == columns.length) {
            columns[0].setWidth(m_columnWidths[0]);
        } else {
            columns[0].setWidth(DATASET_NUMBER_COLUMNWIDTH);
        }
        for (int i = 1; i < columnCount; i++) {
            final TableColumn column = columns[i];
            column.pack();
            if ((m_columnWidths != null && m_columnWidths.length > i)
                    && m_columnCount == columns.length) {
                column.setWidth(m_columnWidths[i]);
            } else {
                column.setWidth(COLUMN_WIDTH);
            }
        }
    }
    
    /**
     * creates the TableColumns with Parameter
     */
    private void initTableViewerParameterColumns() {
        if (getParamInterfaceObj() == null) {
            return;
        }
        final Table table = getTable();
        
        if (m_paramId == getParamInterfaceObj().getId()) {
            TableColumn[] tableColumns = table.getColumns();
            if (tableColumns != null && tableColumns.length != 0) {
                m_columnWidths = new int[tableColumns.length];
                int i = 0;
                for (TableColumn column : tableColumns) {
                    m_columnWidths[i++] = column.getWidth();
                }
                m_columnCount = tableColumns.length;
            }
        } else {
            m_paramId = getParamInterfaceObj().getId();
            m_columnWidths = null;
        }
        
        
        String[] columnProperties = new String[getParamInterfaceObj()
                .getParameterList().size() + 1];
        columnProperties[0] = initDataSetColumn();
        // create columns for parameter
        int i = 1;
        int parameterListSize = getParamInterfaceObj().getParameterListSize();
        for (IParamDescriptionPO descr : getParamInterfaceObj()
                .getParameterList()) {
            TableColumn column = new TableColumn(table, SWT.NONE);
            String columnName = descr.getName();
            column.setText(columnName);
            columnProperties[i] = columnName;
            if (m_columnWidths == null 
                    || m_columnWidths.length <= i
                    /* This has to be parameterListSize + 1 because the "#" 
                     * column is not included within parameterList */
                    || m_columnCount != (parameterListSize + 1)) { 
                column.setWidth(COLUMN_WIDTH);
            } else {
                column.setWidth(m_columnWidths[i]);
            }
            i++;
        }
        getTableViewer().setColumnProperties(columnProperties);
    }
    
    /**
     * Updates this view. Causes the view to get and display its data.
     */
    private void updateView() {
        PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                clearTableViewer();
                IParameterInterfacePO paramObj = getParamInterfaceObj();
                if (paramObj != null && isNodeValid(paramObj)) {
                    getTableViewer().setInput(getInputForTable(paramObj));
                    createTable();
                } else {
                    getTableViewer().setInput(null);
                }
                getTableViewer().refresh();
            }
        });
    }
    
    /**
     * @param cParamInterfaceObj the param interface object to test
     * @return whether the object is valid
     */
    protected abstract boolean isNodeValid(
            IParameterInterfacePO cParamInterfaceObj);

    /**
     * Creates the table
     */
    private void createTable() {
        initTableViewerParameterColumns();
        packTable();
    }

    
    /**
     * The AbstractContentProvider of the Language-Table.
     * @author BREDEX GmbH
     * @created 03.04.2006
     */
    private static class GeneralContentProvider 
        extends AbstractContentProvider {
        /** {@inheritDoc} */
        public Object[] getElements(Object inputElement) {
            ITDManager tdMan = (ITDManager)inputElement;
            List <IDataSetPO> rows = tdMan.getDataSets();
            return rows.toArray();
        }
    }
    
    /**
     * The label provider to display the default data
     * @author BREDEX GmbH
     * @created 03.04.2006
     */
    private class GeneralLabelProvider extends AbstractLabelProvider {
        /** {@inheritDoc} */
        public String getColumnText(Object element, int columnIndex) {
            if (!(element instanceof IDataSetPO)) {
                // this happens when Content-/LabelProvider changes!
                // see ...ComboListener
                return StringConstants.EMPTY; 
            }
            ITDManager tdMan = (ITDManager)getTableViewer().getInput();
            IDataSetPO row = (IDataSetPO)element;
            int rowCount = tdMan.getDataSets().indexOf(row);
            if (columnIndex == 0) {
                for (TableItem i : Arrays.asList(getTable().getItems())) {
                    if (i instanceof IDataSetPO && ((IDataSetPO)i)
                            .equals(element)) {
                        i.setBackground(columnIndex, getTable().getDisplay()
                                .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
                        break;
                    }
                }
                return StringConstants.EMPTY + (rowCount + 1); 
            }
            List <IParamDescriptionPO>paramList = 
                getParamInterfaceObj().getParameterList();
            String value = StringConstants.EMPTY;
            if ((columnIndex - 1) < paramList.size()) {
                IParamDescriptionPO desc = paramList.get(columnIndex - 1);
                IParameterInterfacePO paramInterface = getParamInterfaceObj();
                value = getGuiStringForParamValue(paramInterface, desc,
                        rowCount);
            }
            return value;
        }
    }
    
    /**
     * @param paramObj
     *            the param interface object
     * @param desc
     *            the ParamDescriptionP
     * @param rowCount
     *            the row count
     * @return a valid string for gui presentation of the given param value
     */
    public static String getGuiStringForParamValue(
            IParameterInterfacePO paramObj, IParamDescriptionPO desc,
            int rowCount) {
        return AbstractParamInterfaceBP.getGuiStringForParamValue(paramObj,
                desc, rowCount);
    }
    
    /** {@inheritDoc} */
    public void handleParamChanged(Object caller) {
        if (caller != this) {
            initTableViewerParameterColumns();
            updateView();
        }
    }
    
    /** {@inheritDoc} */
    public void handleProjectLoaded() {
        setParamInterfaceObj(null);
        Plugin.getDisplay().syncExec(new Runnable() {
            public void run() {
                getTableViewer().setInput(null);
            }
        });
    }

    /** {@inheritDoc} */
    public void handleDataChanged(DataChangedEvent... events) {
        for (DataChangedEvent e : events) {
            handleDataChanged(e.getPo(), e.getDataState());
        }
    }
    
    /** {@inheritDoc} */
    public void handleDataChanged(IPersistentObject po, DataState dataState) {
        if (dataState == DataState.Deleted 
                && po.equals(getParamInterfaceObj())) {
            setParamInterfaceObj(null);
            updateView();
        }

        if (dataState == DataState.StructureModified
                && po instanceof ITestDataCategoryPO) {
            updateView();
        }
        
        Plugin.getDisplay().syncExec(new Runnable() {
            public void run() {
                getControlEnabler().selectionChanged(m_currentPart,
                        m_currentSelection);
            }
        });
    }
    
    /**
     * The TableCursor for keyboard support
     * @author BREDEX GmbH
     * @created 11.04.2006
     */
    public class DSVTableCursor extends TableCursor {
        /** The ControlEditor */
        private ControlEditor m_editor;
        /** the current testcase editor */
        private AbstractJBEditor m_tcEditor;
        /** The KeyListener of the editor */
        private KeyAdapter m_keyListener = new EditorKeyListener();
        /** The MouseListener of this Cursor */
        private MouseAdapter m_mouseListener = new EditorMouseListener();
        /** The SelectionListener of this Cursor */
        private CursorListener m_cursorListener = new CursorListener();
        /** The FocusListener of this Cursor */
        private EditorFocusListener m_focusListener = new EditorFocusListener();
        /** true, if editor was activated with enter key */
        private boolean m_wasActivatedWithEnterKey = false;
        /** value to reset, when pressing "ESC" */
        private String m_oldValue;
        /** The untyped Listener of this Cursor */ 
        private Listener m_listener = new Listener() {
            public void handleEvent(Event event) {
                if (event.type == SWT.Selection
                        && event.widget instanceof CCombo) {
                    writeData();
                }
            }
        };
        /** The index of the cell the editor was last activated at */
        private int m_currentEditorIndex;
        /** The current selection index of the shown table items */
        private int m_currentSelectionIndex;
        
        /**
         * @param parent parent
         * @param style style
         */
        public DSVTableCursor(Table parent, int style) {
            super(parent, style);
            addSelectionListener(m_cursorListener);
            addMouseListener(m_mouseListener);
            addKeyListener(m_keyListener);
            m_editor = new ControlEditor(this);
            m_editor.grabHorizontal = true;
            m_editor.grabVertical = true;
        }
        
        /**
         * Gets the zero based column index of the given column property
         * @param columnProperty the property to get the index of
         * @return the zero based column index of the given column property 
         * or -1 if no column with the given property was found
         */
        private int getColumnIndexOfProperty(String columnProperty) {
            Object[] props = getTableViewer().getColumnProperties();
            for (int i = 0; i < props.length; i++) {
                if (columnProperty.equals(props[i])) {
                    return i;
                }
            }
            return -1;
        }
        
        /**
         * assumes the typed data
         */
        private void writeData() {
            if (m_currentPart instanceof AbstractJBEditor) {
                m_tcEditor = (AbstractJBEditor)m_currentPart;
            }
            if (m_tcEditor == null) { // e.g. activeEditor = OMEditor
                return;
            }
            int column = getColumn();
            final Control editor = m_editor.getEditor();
            if (!TextControlBP.isTextValid(editor)) {
                TextControlBP.setText(m_oldValue, editor);
            }
            final String property = getTableViewer().getColumnProperties()
                [column].toString();
            String value = TextControlBP.getText(editor);
            if (m_oldValue != null && m_oldValue.equals(value)) {
                return;
            }
            if (value != null && value.equals(StringConstants.EMPTY)) {
                value = null;
            }
            if (LOG.isDebugEnabled()) {
                logDataChange(property, value, m_oldValue,
                        getParamInterfaceObj().getSpecificationUser());
            }
            // Not perfect, because the actual value being persisted is not exactly 'value'...
            if (value != null 
                    && value.length() > IPersistentObject.MAX_STR_LGT_CHAR) {
                value = value.substring(0, IPersistentObject.MAX_STR_LGT_CHAR);
                if (value.getBytes(StandardCharsets.UTF_8).length
                        > IPersistentObject.MAX_STRING_LENGTH) {
                    value = m_oldValue;
                }
                TextControlBP.setText(value, editor);
                ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.W_MAX_CHAR, 
                    new Object[] {IPersistentObject.MAX_STR_LGT_CHAR}, null);
                return;
            }
            writeDataSetData(property, value, m_tcEditor);
            
        }

        @Override
        public Object getData() {
            if (getRow() == null) {
                return null;
            }
            return getRow().getText(getColumn());
        }
        
        /**
         * @param col column
         * @param newV new value
         * @param oldV old value
         * @param node node
         */
        @SuppressWarnings("nls")
        private void logDataChange(String col, String newV,
                String oldV, INodePO node) {
            StringBuilder str = new StringBuilder();
            str.append("\nData Set value changed. Data owner node: ");
            str.append(node.toString());
            str.append(".\nEditor node: ");
            str.append(node.getSpecAncestor().toString());
            str.append("\nColumn: ");
            str.append(col);
            str.append(", Row: ");
            str.append(m_currentSelectionIndex);
            str.append(".\nValue change: \"");
            str.append(oldV);
            str.append("\" -> \"");
            str.append(newV);
            str.append("\".\n");
            LOG.debug(str.toString());
        }
        
        /**
         * Writes the data to the selected data set
         * @param property the column property
         * @param value the value to write
         * @param edit the editor
         */
        private void writeDataSetData(String property, Object value, 
                AbstractJBEditor edit) {
            int rowind = getSelectedDataSet();
            final int langIndex = getColumnIndexOfProperty(property);
            getTable().getItem(m_currentSelectionIndex).setText(langIndex, 
                    value == null ? StringConstants.EMPTY : (String) value);
            setValueToModel(value, edit, m_currentEditorIndex,
                    m_currentEditorIndex);
        }
        

        /**
         * @param value
         *            the value to set
         * @param editor
         *            the editor
         * @param paramIndex
         *            the index of the parameter
         * @param dsNumber
         *            the number of data set.
         */
        private void setValueToModel(Object value, AbstractJBEditor editor,
                int paramIndex, int dsNumber) {
            if (editor.getEditorHelper().requestEditableState()
                    == JBEditorHelper.EditableState.OK) {
                ParamNameBPDecorator mapper = editor.getEditorHelper()
                        .getEditSupport().getParamMapper();
                GuiParamValueConverter conv = getGuiParamValueConverter(
                        (String)value, getParamInterfaceObj(),
                        getCurrentParamDescription(),
                        ((CheckedParamText)m_editor.getEditor())
                                .getDataValidator());
                if (conv.getErrors().isEmpty()) {
                    getParamBP().startParameterUpdate(conv, dsNumber, mapper);
                    setIsEntrySetComplete(getParamInterfaceObj());
                    editor.getEditorHelper().setDirty(true);
                    new IsAliveThread() {
                        public void run() {
                            Plugin.getDisplay().syncExec(new Runnable() {
                                public void run() {
                                    DataEventDispatcher ded = 
                                            DataEventDispatcher.getInstance();
                                    ded.firePropertyChanged(false);
                                    ded.fireParamChangedListener(
                                            AbstractDataSetPage.this);
                                }
                            });
                        }
                    } .start();
                }
            }
        }
        
        
        /** {@inheritDoc} */
        public void dispose() {
            removeSelectionListener(m_cursorListener);
            removeMouseListener(m_mouseListener);
            Control editor = m_editor.getEditor();
            if (editor != null && !editor.isDisposed()) {
                editor.removeFocusListener(m_focusListener);
                editor.dispose();
            }
            super.dispose();
        }
        
        /**
         * @return if the value can be modified
         */
        private boolean canModify() {
            if (!(m_currentPart instanceof AbstractJBEditor)) {
                return false;
            }
            final AbstractJBEditor edit = (AbstractJBEditor)m_currentPart;
            // First column is not editable!
            boolean isFirstColumn = getColumn() == 0;
            boolean isEditor = (edit != null);

            return !isFirstColumn && isEditor 
                && getControlEnabler().canEdit();
        }
        
        /** {@inheritDoc} */
        protected void checkSubclass () {
            // only to subclass
        }
        
        /**
         * @return the editor to enter values
         */
        private Control createEditor() {
            Control control = TestDataControlFactory.createControl(
                    getParamInterfaceObj(), getParamName(), this, SWT.NONE);
            control.addKeyListener(m_keyListener);
            control.setFocus();
            control.addListener(SWT.Selection, m_listener);
            m_oldValue = getRow().getText(getColumn());
            TextControlBP.setText(m_oldValue, control);
            TextControlBP.selectAll(control);
            return control;
        }
        
        /**
         * @return the current param name
         */
        private String getParamName() {
            return getTableViewer().getTable().getColumn(getColumn()).getText();
        }
        
        /**
         * @return paramDescription for currently edited value
         */
        private IParamDescriptionPO getCurrentParamDescription() {
            String paramName = getParamName();
            return getParamInterfaceObj().getParameterForName(paramName);
        }

        /**
         * activate the editor
         */
        private void activateEditor() {
            if (canModify()) {
                m_editor.setEditor(createEditor());
                Control editorCtrl = m_editor.getEditor();
                if ((editorCtrl != null) && !editorCtrl.isDisposed()) {
                    editorCtrl.addFocusListener(m_focusListener);
                }
                TextControlBP.selectAll(m_editor.getEditor());
                m_currentEditorIndex = getSelectedDataSet();
                m_currentSelectionIndex = getTable().getSelectionIndex();
            }
        }

        /**
         * KeyListener for the editor
         */
        private class EditorKeyListener extends KeyAdapter {
            /** {@inheritDoc} */
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.ARROW_DOWN 
                        || e.keyCode == SWT.ARROW_UP
                        || e.keyCode == SWT.ARROW_LEFT
                        || e.keyCode == SWT.ARROW_RIGHT) {
                    
                    return;
                }
                if (!(e.character == CharacterConstants.BACKSPACE
                    || e.character == SWT.DEL // the "DEL"-Key
                    || e.character == SWT.ESC // the "ESC"-Key
                    || e.character == SWT.CR // the "ENTER"-Key
                    || e.character == SWT.KEYPAD_CR // the "ENTER"-Key
                    || (!Character.isISOControl(e.character)))) {
                    return;
                }
                if (e.getSource().equals(m_editor.getEditor())) {
                    // close the text editor when the user hits "ESC"
                    if (e.character == SWT.ESC) {
                        TextControlBP.setText(m_oldValue, m_editor.getEditor());
                        writeData();
                        TableItem rowItem = getRow();
                        final int col = getColumn();
                        rowItem.setText(col, m_oldValue);
                        m_editor.getEditor().dispose();
                        return;
                    }
                    if (e.character == SWT.CR || e.character == SWT.KEYPAD_CR) {
                        if (m_wasActivatedWithEnterKey) {
                            m_wasActivatedWithEnterKey = false;
                            return;
                        }
                        handleCR();
                    }
                }
                if (e.getSource() instanceof DSVTableCursor) {
                    if (e.character == SWT.ESC) {
                        return;
                    }
                    activateEditor();
                    if (m_editor.getEditor() != null 
                            && !m_editor.getEditor().isDisposed()
                            && e.character != SWT.CR
                            && e.character != SWT.KEYPAD_CR
                            && !(m_editor.getEditor() instanceof CCombo)) {
                        String sign = new Character(e.character).toString();
                        if (e.character == SWT.DEL // the "DEL"-Key
                            || e.character == CharacterConstants.BACKSPACE) {
                            sign = StringConstants.EMPTY;
                        }
                        TextControlBP.setText(sign, m_editor.getEditor());
                        TextControlBP.setSelection(m_editor.getEditor(), 1);
                    }
                }
            }

            /**
             * Handles the CR keys
             */
            private void handleCR() {
                final Control editorControl = m_editor.getEditor();
                if (!editorControl.isDisposed()) {
                    writeData();
                }
                // writeData() may actually dispose the control during error
                // handling, a new check is needed!
                if (!editorControl.isDisposed()) {
                    TableItem rowItem = getRow();
                    final int col = getColumn();
                    rowItem.setText(col, TextControlBP.getText(editorControl));
                    editorControl.dispose();
                    final int row = getTable().indexOf(getRow());
                    if (getTable().getColumnCount() > (col + 1)) {
                        setSelection(row, col + 1);
                        getTable().setSelection(row);
                        setFocus();
                    } else if (getTable().getItemCount() > (row + 1)) {
                        setSelection(row + 1, 1);
                        getTable().setSelection(row + 1);
                    } else {
                        getAddButton().setFocus();
                    }
                }
            }
        }
         
        /**
         * The SelectionListener
         */
        private class CursorListener extends SelectionAdapter {

            /** {@inheritDoc} */
            public void widgetDefaultSelected(SelectionEvent e) {
                activateEditor();
                m_wasActivatedWithEnterKey = true;
            }

            /** {@inheritDoc} */
            public void widgetSelected(SelectionEvent e) {
                getTable().setSelection(
                    new TableItem[] {getRow()});
            }
            
        }

        /**
         * MouseListener for the editor
         */
        private class EditorMouseListener extends MouseAdapter {
            /** {@inheritDoc} */
            public void mouseUp(MouseEvent e) {
                activateEditor();
                m_wasActivatedWithEnterKey = false;
            }
        }
        
        /**
         * @author BREDEX GmbH
         * @created 19.06.2006
         */
        private class EditorFocusListener extends FocusAdapter {
            /** {@inheritDoc} */
            public void focusLost(FocusEvent e) {
                if (m_editor.getEditor() 
                        instanceof CheckedParamTextContentAssisted) {
                    CheckedParamTextContentAssisted ed = 
                        (CheckedParamTextContentAssisted)m_editor.getEditor();
                    if (ed.isPopupOpen() && ed.isFocusControl()) {
                        super.focusLost(e);
                        return;
                    }
                }
                writeData();
                m_editor.getEditor().dispose();
                super.focusLost(e);
            }  
        }
    }
    
    /**
     * Removes a selected data set.
     */
    private void removeDataSet() {
        final AbstractJBEditor editor = (AbstractJBEditor)m_currentPart;
        if (editor == null) {
            return;
        }
        if (editor.getEditorHelper().requestEditableState()
                == JBEditorHelper.EditableState.OK) {
            if (getParamInterfaceObj() instanceof IExecTestCasePO) {
                ITDManager man = ((IExecTestCasePO)getParamInterfaceObj())
                        .resolveTDReference();
                if (!man.equals(getTableViewer().getInput())) {
                    getTableViewer().setInput(man);
                }
            }

            int row = getSelectedDataSet();
            try {
                if (row == -1 && getTableCursor().getRow() != null) {
                    row = getTable().indexOf(getTableCursor()
                        .getRow());
                }
                if (row > -1) {
                    editor.getEditorHelper().getEditSupport()
                        .lockWorkVersion();
                    getParamBP().removeDataSet(getParamInterfaceObj(),
                            row, editor.getEditorHelper().getEditSupport()
                                    .getParamMapper());
                    editor.getEditorHelper().setDirty(true);
                    getTableViewer().refresh();                    
                    setIsEntrySetComplete(getParamInterfaceObj());
                    if (getTable().getItemCount() != 0) {
                        if (getTable().getItemCount() <= row
                                && getTable().getItemCount() > 0) {
                            --row;
                            getTable().setSelection(row);
                        } else {
                            getTable().setSelection(row);
                        }
                        getTableCursor().setSelection(row, 1);
                    }
                    getControlEnabler().setControlsEnabled();
                    setFocus();
                    DataEventDispatcher.getInstance()
                            .fireParamChangedListener(this);
                }
            } catch (PMException pme) {
                PMExceptionHandler.handlePMExceptionForEditor(pme, editor);
            }
        }
    }

    /**
     * Reacts on the changes from the SelectionService of Eclipse.
     * @param part The Workbenchpart.
     * @param selection The selection.
     */
    private void reactOnChange(IWorkbenchPart part,
            IStructuredSelection selection) {
        m_currentPart = part;
        m_currentSelection = selection;
        getControlEnabler().selectionChanged(part, selection);
        
        IParameterInterfacePO paramInterfacePO = 
            getSelectedParamInterfaceObj(selection);
        
        setParamInterfaceObj(paramInterfacePO);
        updateView();
    }
    
    /**
     * @param selection
     *            the current selection
     * @return the valid param interface po or <code>null</code> if current
     *         selection does not contain a IParameterInterfacePO
     */
    private IParameterInterfacePO getSelectedParamInterfaceObj(
            IStructuredSelection selection) {
        IParameterInterfacePO paramInterfacePO = null;
        Object firstSel = selection.getFirstElement();
        if (firstSel instanceof SearchResultElement) {
            firstSel = ((SearchResultElement) firstSel).getObject();
        }
        if (firstSel instanceof IParameterInterfacePO) {
            paramInterfacePO = (IParameterInterfacePO)firstSel;
        }
        return paramInterfacePO;
    }

    /**
     * checks the given IParameterInterfacePO if all entrySets are complete for
     * the given Locale and sets the flag.
     * 
     * @param paramNode
     *            teh ParamNodePO to check.
     */
    protected abstract void setIsEntrySetComplete(
            IParameterInterfacePO paramNode);
    
    /**
     * Class for En-/Disabling swt.Controls depending of active WorkbenchPart
     * and selection
     * @author BREDEX GmbH
     * @created 06.04.2006
     */
    protected class ControlEnabler implements ISelectionListener {

        /** Whether the data cells are editable */
        private boolean m_canEdit = false;
        
        /**
         * @return whether the data cells are editable
         */
        public boolean canEdit() {
            return m_canEdit;
        }
        
        /** {@inheritDoc} */
        public void selectionChanged(IWorkbenchPart part, 
            ISelection selection) {
            if (!(selection instanceof IStructuredSelection)
                    || getTable().isDisposed()) { 
                return;
            }
            IStructuredSelection strucSelection = 
                    (IStructuredSelection)selection;
            IParameterInterfacePO paramNode = getSelectedParamInterfaceObj(
                    strucSelection);

            boolean correctPart = false;
            if (part != null) {
                correctPart = (part == AbstractDataSetPage.this || part
                        .getAdapter(AbstractJBEditor.class) != null);
            }
            if (!correctPart) {
                getTable().setForeground(LayoutUtil.GRAY_COLOR);
            } else {
                getTable().setForeground(LayoutUtil.DEFAULT_OS_COLOR);
            }
            boolean hasInput = !strucSelection.isEmpty();
            boolean isEditorOpen = isEditorOpenOrIsPageTestDataCube(paramNode);
            boolean hasParameter = false; 
            boolean hasExcelFile = false;
            boolean hasReferencedDataCube = false;
            if (paramNode != null) {
                hasParameter = !paramNode.getParameterList().isEmpty();
                final String dataFile = paramNode.getDataFile();
                hasExcelFile = !(dataFile == null || dataFile.length() == 0);
                hasReferencedDataCube = 
                    paramNode.getReferencedDataCube() != null;
            }
            // En-/disable controls
            boolean isCAP = paramNode instanceof ICapPO;
            m_canEdit = correctPart && hasInput && isEditorOpen
                && !isCAP && !hasExcelFile && !hasReferencedDataCube 
                && hasParameter;
            setControlsEnabled();
        }

        /**
         * Sets enablement of the buttons
         */
        public void setControlsEnabled() {
            m_addButton.setEnabled(false);
            m_insertButton.setEnabled(false);
            m_deleteButton.setEnabled(false);
            m_upButton.setEnabled(false);
            m_downButton.setEnabled(false);
            if (!m_canEdit || StringUtils.isNotEmpty(m_searchText.getText())) {
                return;
            }
            m_addButton.setEnabled(true);
            int rowind = getSelectedDataSet();
            if (rowind > -1) {
                m_insertButton.setEnabled(true);
                m_deleteButton.setEnabled(true);
                if (rowind > 0) {
                    m_upButton.setEnabled(true);
                }
                if (rowind < getTable().getItemCount() - 1) {
                    m_downButton.setEnabled(true);
                }
            }
        }
    }
    /**
     * Checks if the given IParameterInterfacePO is in an open editor.
     *      Returns true for TestDataCubeDataSetPage due to complications.
     *      Currently this method is only used to decide if the DataSetView
     *      is editable. If it will be used anywhere else, the TDCDataSetPage
     *      method must be corrected.
     * @param paramObj the object to check
     * @return true if the given node is in an open editor, false otherwise.
     */
    protected abstract boolean isEditorOpenOrIsPageTestDataCube(
            IParameterInterfacePO paramObj);
    
    /** {@inheritDoc} */
    public void selectionChanged(IWorkbenchPart part,
            ISelection selection) {
        if (!(selection instanceof IStructuredSelection)) { 
            // e.g. in Jubula plugin-version you can open an java editor, 
            // that reacts on org.eclipse.jface.text.TextSelection, which
            // is not a StructuredSelection
            return;
        }
                
        reactOnChange(part, (IStructuredSelection)selection);
    }

    
    
    /**
     * @param paramBP
     *            the paramBP to set
     */
    private void setParamBP(AbstractParamInterfaceBP paramBP) {
        m_paramBP = paramBP;
    }

    /**
     * @return the paramBP
     */
    private AbstractParamInterfaceBP getParamBP() {
        return m_paramBP;
    }

    /**
     * @param paramInterfaceObj
     *            the paramInterfaceObj to set
     */
    private void setParamInterfaceObj(IParameterInterfacePO paramInterfaceObj) {
        m_paramInterfaceObj = paramInterfaceObj;
    }

    /**
     * @return the paramInterfaceObj
     */
    public IParameterInterfacePO getParamInterfaceObj() {
        return m_paramInterfaceObj;
    }
    
    /**
     * hint: the string could be null.
     * 
     * @param value
     *            to convert
     * @param paramInterfaceObj
     *            obj with parameter for this parameterValue
     * @param currentParamDescription
     *            param description associated with current string (parameter
     *            value)
     * @param dataValidator
     *            to use for special validations
     * @return a valid GuiParamValueConverter
     */
    private GuiParamValueConverter getGuiParamValueConverter(String value,
            IParameterInterfacePO paramInterfaceObj,
            IParamDescriptionPO currentParamDescription,
            IParamValueValidator dataValidator) {
        return new GuiParamValueConverter(value, paramInterfaceObj,
                currentParamDescription, dataValidator);
    }
    
    /**
     * 
     * @param paramInterface The object on which the input is based.
     * @return an object suitable for use as input in a DSV table.
     */
    protected ITDManager getInputForTable(
            IParameterInterfacePO paramInterface) {
        return paramInterface.getDataManager();
    }

    /**
     * @param tableCursor the tableCursor to set
     */
    private void setTableCursor(DSVTableCursor tableCursor) {
        m_tableCursor = tableCursor;
    }

    /**
     * @return the tableCursor
     */
    public DSVTableCursor getTableCursor() {
        return m_tableCursor;
    }

    /**
     * Attempts to activate a cell determined by parameters of the getCentralTestDataSetValue function
     * @param keyCol the key column name
     * @param keyVal the key value
     * @param valCol the value column name
     */
    public void navigateToCell(String keyCol, String keyVal, String valCol) {
        getTable().setFocus();
        int keyColInd = getColumnIndexByParamName(keyCol);
        if (keyColInd == -1) {
            return;
        }
        int rowNum = 0;
        ITDManager man = getParamInterfaceObj().getDataManager();
        for (IDataSetPO row : man.getDataSets()) {
            if (StringUtils.equals(row.getColumnStringValues().get(keyColInd),
                    keyVal)) {
                break;
            }
            rowNum++;
        }
        if (rowNum >= man.getDataSetCount()) {
            // could not find the correct row
            return;
        }
        // if the value column is not found, the 0th column (row number) is chosen
        int valColNum = getColumnIndexByParamName(valCol) + 1; 
        getTable().setSelection(rowNum);
        getTableCursor().setSelection(rowNum, valColNum);
    }

    /**
     * Returns the column index by column name
     * @param name the column name
     * @return the index
     */
    public int getColumnIndexByParamName(String name) {
        int keyColInd = 0;
        for (IParamDescriptionPO desc : getParamInterfaceObj().
                getParameterList()) {
            if (StringUtils.equals(name, desc.getName())) {
                return keyColInd;
            }
            keyColInd++;
        }
        return -1;
    }

    /**
     * Returns the currently selected row's index or -1 if none is selected
     * @return the index or -1
     */
    public int getCurrentRow() {
        if (getTable().isDisposed()) {
            return -1;
        }
        return getTable().getSelectionIndex();
    }

    /**
     * Returns the currently selected column's index or -1 if none is selected
     * @return the index or -1
     */
    public int getCurrentCol() {
        if (getTable().isDisposed()) {
            return -1;
        }
        return getTableCursor().getColumn();
    }

    /**
     * Activates a cell in the underlying Table
     * @param row the row
     * @param col the data column (not counting the first column of the table)
     */
    public void navigateToCellUsingRowCol(int row, int col) {
        if (getTable().isDisposed()) {
            return;
        }
        int newRow = Math.max(0, row);
        if (newRow >= getTable().getItemCount()) {
            newRow = 0;
        }
        getTable().setFocus();
        getTable().setSelection(newRow);
        if (col < 0 || col + 1 >= getTable().getColumnCount()) {
            return;
        }
        getTableCursor().setSelection(newRow, col + 1);
    }
}
