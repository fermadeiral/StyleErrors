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
package org.eclipse.jubula.client.ui.views;

import static org.eclipse.jubula.tools.internal.constants.StringConstants.EMPTY;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ITestresultChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ITestresultSummaryEventListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.TestresultState;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.model.ProjectVersion;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.core.persistence.TestResultSummaryPM;
import org.eclipse.jubula.client.core.utils.DatabaseStateDispatcher;
import org.eclipse.jubula.client.core.utils.DatabaseStateEvent;
import org.eclipse.jubula.client.core.utils.IDatabaseStateListener;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.CommandIDs;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.filter.JBPatternFilter;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.provider.labelprovider.TestresultSummaryViewColumnLabelProvider;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.client.ui.utils.JobUtils;
import org.eclipse.jubula.toolkit.common.businessprocess.ToolkitSupportBP;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.tools.internal.constants.MonitoringConstants;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * View for presenting summaries of Test Results.
 * 
 * @author BREDEX GmbH
 */
@SuppressWarnings("unchecked")
public class TestresultSummaryView extends ViewPart implements
        ITestresultSummaryEventListener, ITestresultChangedListener,
        IDatabaseStateListener {
    /**
     * <code>defaultDateTimeFormat</code> the Date Time Format used in to
     * display dates
     */
    public static final DateFormat DTF_DEFAULT = DateFormat.getDateTimeInstance(
            DateFormat.DEFAULT, DateFormat.DEFAULT);

    /**
     * <code>fullDateTimeFormat</code> the detailed Date Time Format used in to
     * display dates
     */
    public static final DateFormat DTF_LONG = DateFormat.getDateTimeInstance(
            DateFormat.DEFAULT, DateFormat.LONG);
    
    /**
     * <code>FILTER_CONTROL_INPUT_DELAY</code>
     */
    private static final int FILTER_CONTROL_INPUT_DELAY = 300;

    /**
     * <code>NO_DATA_AVAILABLE</code>
     */
    private static final String NO_DATA_AVAILABLE = 
        Messages.TestresultSummaryNoData;

    /**
     * <code>TESTRESULT_SUMMARY_ADDITIONAL_INFO</code>
     */
    private static final String TESTRESULT_SUMMARY_ADDITIONAL_INFO = 
            Messages.TestresultSummaryAdditionalInfo;
    /**
     * <code>TESTRESULT_SUMMARY_NUMBER_OF_FAILED_CAPS</code>
     */
    private static final String TESTRESULT_SUMMARY_NUMBER_OF_FAILED_CAPS = 
        Messages.TestresultSummaryNumberOfFailedCaps;

    /**
     * <code>TESTRESULT_SUMMARY_DETAILS_AVAILABLE</code>
     */
    private static final String TESTRESULT_SUMMARY_DETAILS_AVAILABLE = 
        Messages.TestresultSummaryDetailsAvailable;

    /**
     * <code>TESTRESULT_SUMMARY_TESTRUN_RELEVANT</code>
     */
    private static final String TESTRESULT_SUMMARY_TESTRUN_RELEVANT = 
        Messages.TestresultSummaryTestrunRelevant;

    /**
     * <code>TESTRESULT_SUMMARY_CMD_PARAM</code>
     */
    private static final String TESTRESULT_SUMMARY_CMD_PARAM = 
        Messages.TestresultSummaryCmdParam;

    /**
     * <code>TESTRESULT_SUMMARY_HANDLER_CAPS</code>
     */
    private static final String TESTRESULT_SUMMARY_HANDLER_CAPS = 
        Messages.TestresultSummaryHandlerCaps;

    /**
     * <code>TESTRESULT_SUMMARY_EXECUTED_CAPS</code>
     */
    private static final String TESTRESULT_SUMMARY_EXECUTED_CAPS = 
        Messages.TestresultSummaryExecCaps;

    /**
     * <code>TESTRESULT_SUMMARY_EXPECTED_CAPS</code>
     */
    private static final String TESTRESULT_SUMMARY_EXPECTED_CAPS = 
        Messages.TestresultSummaryExpecCaps;

    /**
     * <code>TESTRESULT_SUMMARY_DURATION</code>
     */
    private static final String TESTRESULT_SUMMARY_DURATION = 
        Messages.TestresultSummaryDuration;

    /**
     * <code>TESTRESULT_SUMMARY_END_TIME</code>
     */
    private static final String TESTRESULT_SUMMARY_END_TIME = 
        Messages.TestresultSummaryEndTime;

    /**
     * <code>TESTRESULT_SUMMARY_START_TIME</code>
     */
    private static final String TESTRESULT_SUMMARY_START_TIME = 
        Messages.TestresultSummaryStartTime;

    /**
     * <code>TESTRESULT_SUMMARY_TOOLKIT</code>
     */
    private static final String TESTRESULT_SUMMARY_TOOLKIT = 
        Messages.TestresultSummaryToolkit;

    /**
     * <code>TESTRESULT_SUMMARY_AUT_OS</code>
     */
    private static final String TESTRESULT_SUMMARY_AUT_OS = 
        Messages.TestresultSummaryAutOS;

    /**
     * <code>TESTRESULT_SUMMARY_AUT_AGENT_HOSTNAME</code>
     */
    private static final String TESTRESULT_SUMMARY_AUT_AGENT_HOSTNAME = 
        Messages.TestresultSummaryAutAgentHostname;

    /**
     * <code>TESTRESULT_SUMMARY_AUT_HOSTNAME</code>
     */
    private static final String TESTRESULT_SUMMARY_AUT_HOSTNAME = 
        Messages.TestresultSummaryAutHostname;

    /**
     * <code>TESTRESULT_SUMMARY_AUT_CONFIG</code>
     */
    private static final String TESTRESULT_SUMMARY_AUT_CONFIG = 
        Messages.TestresultSummaryAutConf;

    /**
     * <code>TESTRESULT_SUMMARY_AUT_ID</code>
     */
    private static final String TESTRESULT_SUMMARY_AUT_ID = 
        Messages.TestresultSummaryAutId;

    /**
     * <code>TESTRESULT_SUMMARY_AUT_NAME</code>
     */
    private static final String TESTRESULT_SUMMARY_AUT_NAME = 
        Messages.TestresultSummaryAutName;

    /**
     * <code>TESTRESULT_SUMMARY_TESTSUITE_STATUS</code>
     */
    private static final String TESTRESULT_SUMMARY_TESTSUITE_STATUS = 
        Messages.TestresultSummaryTestsuiteStatus;

    /**
     * <code>TESTRESULT_SUMMARY_TESTSUITE</code>
     */
    private static final String TESTRESULT_SUMMARY_TESTSUITE = 
        Messages.TestresultSummaryTestsuite;

    /**
     * <code>TESTRESULT_SUMMARY_PROJECT_NAME</code>
     */
    private static final String TESTRESULT_SUMMARY_PROJECT_NAME = 
        Messages.TestresultSummaryProjectName;
    
    /**
     * <code>TESTRESULT_SUMMARY_PROJECT_VERSION</code>
     */
    private static final String TESTRESULT_PROJECT_VERSION = 
        Messages.TestresultSummaryProjectVersion;

    /**
     * <code>TESTRESULT_SUMMARY_TESTRUN_STATE</code>
     */
    private static final String TESTRESULT_SUMMARY_TESTRUN_STATE = 
        Messages.TestresultSummaryTestrunState;
    
    /**
     * <code>TESTRESULT_SUMMARY_DATE</code>
     */
    private static final String TESTRESULT_SUMMARY_DATE = 
        Messages.TestresultSummaryDate;

    /**
     * <code>TESTRESULT_SUMMARY_COMMENT_TITLE</code>
     */
    private static final String TESTRESULT_SUMMARY_COMMENT_TITLE =
        Messages.TestresultSummaryCommentTitle;
    
    /**
     * <code>TESTRESULT_SUMMARY_TEST_JOB_START_TIME</code>
     */
    private static final String TESTRESULT_SUMMARY_TEST_JOB_START_TIME = 
        Messages.TestresultSummaryTestJobStartTime;

    /**
     * <code>TESTRESULT_SUMMARY_TEST_JOB</code>
     */
    private static final String TESTRESULT_SUMMARY_TEST_JOB = 
        Messages.TestresultSummaryTestJob;

    /**
     * <code>TESTRESULT_SUMMARY_TESTRUN_ID</code>
     */
    private static final String TESTRESULT_SUMMARY_TESTRUN_ID = 
        Messages.TestresultSummaryTestrunID;
    
    /**
     * <code>MONITORING_ID</code>
     */
    private static final String MONITORING_ID = 
        Messages.TestresultSummaryMonitoringId;
    
    /**
     * <code>MONITORING_VALUE</code>
     */
    private static final String MONITORING_VALUE = 
        Messages.TestresultSummaryMonitoringValue;
    
    /**
     * <code>MONITORING_DETAILS</code>
     */
    private static final String MONITORING_DETAILS = 
        Messages.TestresultSummaryMonitoringDetails;
    /** standard logging */
    private static Logger log = 
        LoggerFactory.getLogger(TestresultSummaryView.class);
    
    /** column tag for memento*/
    private static final String TAG_COLUMN = "column"; //$NON-NLS-1$

    /** number tag for memento*/
    private static final String TAG_NUMBER = "number"; //$NON-NLS-1$

    /** width tag for memento*/
    private static final String TAG_WIDTH = "width"; //$NON-NLS-1$
    
    /** column index tag for memento*/
    private static final String TAG_COL_IDX = "columnIndex"; //$NON-NLS-1$
    
    /** filter tag for memento*/
    private static final String TAG_FILTER = "filter"; //$NON-NLS-1$
    
    /** filter type tag for memento*/
    private static final String TAG_FILTER_TYPE = "filterType"; //$NON-NLS-1$

    /** search type tag for memento*/
    private static final String TAG_SEARCH_TYPE = "searchType"; //$NON-NLS-1$
    
    /** sort tag for memento*/
    private static final String TAG_SORT = "sort"; //$NON-NLS-1$
    
    /** sort column tag for memento*/
    private static final String TAG_SORT_COL = "sortColumn"; //$NON-NLS-1$

    /** sort direction tag for memento*/
    private static final String TAG_SORT_DIRECTION = "sortDirection"; //$NON-NLS-1$
    
    /** table viewer of metadata table */
    private TableViewer m_tableViewer;

    /** filter for metadata */
    private TestresultSummaryFilter m_filter;

    /** menu to show/hide columns */
    private Menu m_headerMenu;

    /** IMemento to persist view settings like filter, sort etc */
    private IMemento m_memento;
    
    /** combobox for filter type */
    private Combo m_filterCombo;

    /** search string text field for filter*/
    private Text m_searchText;

    /** The preference store to hold the existing preference values. */
    private IPreferenceStore m_store = Plugin.getDefault().getPreferenceStore();
    
    /**
     * <code>m_filterJob</code>
     */
    private TestresultFilterJob m_filterJob = new TestresultFilterJob(
            Messages.JobFilterSummaryView,
            EMPTY);

    /**
     * The constructor.
     */
    public TestresultSummaryView() {
        //default constructor
    }

    /**
     * {@inheritDoc}
     */
    public void init(IViewSite site, IMemento memento)
        throws PartInitException {
        super.init(site, memento);
        this.m_memento = memento;
    }

    /**
     * {@inheritDoc}
     */
    public void createPartControl(Composite parent) {
        m_headerMenu = new Menu(parent);
        GridLayout layout = new GridLayout(4, false);
        parent.setLayout(layout);
        m_filter = new TestresultSummaryFilter();
        createSearchFilter(parent);
        m_tableViewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL
                | SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.VIRTUAL);
        addDateColumn(m_tableViewer);
        addDetailsColumn(m_tableViewer);
        addStatusDecoratorColumn(m_tableViewer);
        addTestsuiteColumn(m_tableViewer);
        addCommentTitleColumn(m_tableViewer);
        addSummaryIdColumn(m_tableViewer);
        addTestRelevantColumn(m_tableViewer);
        addTestJobStartTimeColumn(m_tableViewer);
        addTestJobColumn(m_tableViewer);
        addTsStatusColumn(m_tableViewer);
        addProjectNameColumn(m_tableViewer);
        addProjectVersionColumn(m_tableViewer);
        addAutIdColumn(m_tableViewer);
        addAutNameColumn(m_tableViewer);
        addAutConfColumn(m_tableViewer);
        addCmdParamColumn(m_tableViewer);
        addAutOSColumn(m_tableViewer);
        addAutHostnameColumn(m_tableViewer);
        addAutAgentHostnameColumn(m_tableViewer);
        addToolkitColumn(m_tableViewer);
        addStartTimeColumn(m_tableViewer);
        addEndTimeColumn(m_tableViewer);
        addDurationColumn(m_tableViewer);
        addExpecCapsColumn(m_tableViewer);
        addExecCapsColumn(m_tableViewer);
        addEventhandlerCapsColumn(m_tableViewer);
        addFailedCapsColumn(m_tableViewer);
        addMonitoringIdColumn(m_tableViewer);
        addMonitoringValueColumn(m_tableViewer);
        addMonitoringReportColumn(m_tableViewer);
        addAdditonalInfoColumn(m_tableViewer);
        
        getSite().setSelectionProvider(m_tableViewer);
        m_tableViewer.setContentProvider(new ArrayContentProvider());
        m_tableViewer.getTable().setLinesVisible(true);
        m_tableViewer.getTable().setHeaderVisible(true);
        m_tableViewer.setUseHashlookup(true);

        addContextMenu(m_tableViewer, m_headerMenu);
        setTableViewerLayout();
        
        m_tableViewer.addFilter(m_filter);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(
                m_tableViewer.getControl(),
                ContextHelpIds.TESTRESULT_SUMMARY_VIEW);
        DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.addTestresultListener(this, true);
        ded.addTestresultSummaryEventListener(this);
        
        DatabaseStateDispatcher.addDatabaseStateListener(this);
        addDoubleClickListener(m_tableViewer);
        
        loadViewInput();
        restoreViewStatus();
    }

    /**
     * Adds a context menu to the table's header.
     * 
     * @param tableViewer The table viewer.
     * @param headerMenu The context menu to add to the table's header.
     */
    private void addContextMenu(final TableViewer tableViewer, 
            final Menu headerMenu) {
        /*
         * Add context menu to header. Similar to the snippets described in:
         * http://eclip.se/23103
         */
        final Table table = tableViewer.getTable();
        table.addListener(SWT.MenuDetect, new Listener() {
            public void handleEvent(Event event) {
                Point pt = event.display.map(
                        null, table, new Point(event.x, event.y));
                Rectangle clientArea = table.getClientArea();
                boolean isHeaderEvent = clientArea.y <= pt.y 
                    && pt.y < (clientArea.y + table.getHeaderHeight());
                if (isHeaderEvent) {
                    table.setMenu(headerMenu);
                } else {
                    // Create menu manager.
                    MenuManager menuMgr = new MenuManager();
                    menuMgr.setRemoveAllWhenShown(true);
                    menuMgr.addMenuListener(new IMenuListener() {
                        public void menuAboutToShow(IMenuManager mgr) {
                            fillContextMenu(mgr);
                        }
                    });
                    // Create menu.
                    Menu menu = menuMgr.createContextMenu(table);
                    // Register menu for extension.
                    getSite().registerContextMenu(menuMgr, tableViewer);
                    table.setMenu(menu);
                }
            }
        });
        // Comment from snippet in http://eclip.se/23103
        // IMPORTANT: Dispose the menus (only the current menu, set with 
        // setMenu(), will be automatically disposed) 
        table.addListener(SWT.Dispose, new Listener() {
            public void handleEvent(Event event) {
                headerMenu.dispose();
            }
        });
    }

    /**
     * @param mgr the menu manager
     */
    private void fillContextMenu(IMenuManager mgr) {
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.OPEN_TEST_RESULT_DETAIL_COMMAND_ID);
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.REFRESH_COMMAND_ID);
        mgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
    }
    
    /**
     * @param tableViewer
     *            the table viewer
     */
    private void addFailedCapsColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setImage(IconConstants.EH_CAP_IMAGE);
        column.getColumn().setText(TESTRESULT_SUMMARY_NUMBER_OF_FAILED_CAPS);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                int noFailed = ((ITestResultSummaryPO)element)
                        .getTestsuiteFailedTeststeps();
                if (noFailed == ITestResultSummaryPO
                        .DEFAULT_NUMBER_OF_FAILED_TEST_STEPS) {
                    return NO_DATA_AVAILABLE;
                }
                return String.valueOf(noFailed);
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1)
                            .getTestsuiteFailedTeststeps(), 
                        ((ITestResultSummaryPO)e2)
                            .getTestsuiteFailedTeststeps());
            }
        };
    }

    /**
     * @param tableViewer
     *            the table viewer
     */
    private void addDetailsColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(70);
        column.getColumn().setToolTipText(
                Messages.TestresultSummaryColumnDescriptionDetails);
        column.getColumn().setText(TESTRESULT_SUMMARY_DETAILS_AVAILABLE);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public Image getImage(Object element) {
                if (((ITestResultSummaryPO)element).hasTestResultDetails()) {
                    return IconConstants.TRSV_DETAILS;
                }
                return IconConstants.TRSV_NODETAILS;
            }
            public String getText(Object element) {
                return null;
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                    ((ITestResultSummaryPO)e1).hasTestResultDetails(), 
                    ((ITestResultSummaryPO)e2).hasTestResultDetails());
            }
        };
    }

    /**
     * Adds a double-click listener to the given viewer. This listener 
     * opens/activates a Test Result Viewer based on the current selection.
     * 
     * @param viewer The viewer to which to add the listener.
     */
    private void addDoubleClickListener(StructuredViewer viewer) {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                CommandHelper.executeCommand(
                        CommandIDs.OPEN_TEST_RESULT_DETAIL_COMMAND_ID,
                        getSite());
            }
        });
    }
    
    /**
     * save filters, sorting etc
     * @param memento IMemento
     */
    public void saveState(IMemento memento) {
        Table table = m_tableViewer.getTable();
        // save columns width
        TableColumn columns[] = table.getColumns();
        int[] colOrder = table.getColumnOrder();
        for (int i = 0; i < columns.length; i++) {
            IMemento colWidthChild = memento.createChild(TAG_COLUMN);
            colWidthChild.putInteger(TAG_NUMBER, i);
            colWidthChild.putInteger(TAG_WIDTH, columns[i].getWidth());
            colWidthChild.putInteger(TAG_COL_IDX, colOrder[i]);
        }
        // save filter
        IMemento filterChild = memento.createChild(TAG_FILTER);
        filterChild.putString(TAG_FILTER_TYPE, m_filterCombo.getItem(
                m_filterCombo.getSelectionIndex()));
        filterChild.putString(TAG_SEARCH_TYPE, m_searchText.getText());
        //save sorting
        IMemento sortChild = memento.createChild(TAG_SORT);
        TableColumn sortCol = table.getSortColumn();
        if (sortCol != null) {
            sortChild.putString(TAG_SORT_COL, sortCol.getText());
            sortChild.putInteger(TAG_SORT_DIRECTION, table.getSortDirection());
        }
    }

    /**
     * restore view settings like column order, width etc
     */
    private void restoreViewStatus() {
        Table table = m_tableViewer.getTable();
        if (m_memento != null) {
            // restore columns
            IMemento children[] = m_memento.getChildren(TAG_COLUMN);
            if (children.length == table.getColumnCount()) {
                if (children != null) {
                    int[] colOrder = new int[table.getColumnOrder().length];
                    for (int i = 0; i < children.length; i++) {
                        Integer val = children[i].getInteger(TAG_NUMBER);
                        if (val != null) {
                            int index = val.intValue();
                            Integer width = children[i].getInteger(TAG_WIDTH);
                            if (width != null) {
                                table.getColumn(index).setWidth(
                                        width.intValue());
                            }
                            Integer colIdx = children[i].getInteger(
                                    TAG_COL_IDX);
                            if (colIdx != null) {
                                colOrder[i] = colIdx.intValue();
                            }
                        }
                    }
                    if (children.length == colOrder.length) {
                        table.setColumnOrder(colOrder);
                    }
                }
                //restore filter
                IMemento filterChild = m_memento.getChild(TAG_FILTER);
                if (filterChild != null) {
                    String filterTypeString = 
                        filterChild.getString(TAG_FILTER_TYPE);
                    String searchString = 
                        filterChild.getString(TAG_SEARCH_TYPE);
                    m_filterCombo.select(m_filterCombo.indexOf(
                            filterTypeString));
                    m_filter.setFilterType(filterTypeString);
                    m_searchText.setText(searchString);
                    m_filter.setPattern(searchString);
                }
                //restore sorting
                IMemento sortChild = m_memento.getChild(TAG_SORT);
                if (sortChild != null) {
                    String sortHeader = sortChild.getString(TAG_SORT_COL);
                    final Integer direction = sortChild.getInteger(
                            TAG_SORT_DIRECTION);
                    sortTable(table, sortHeader, direction);
                }
            }
        } else {
            // apply default behavior for new workspaces
            sortTable(table, TESTRESULT_SUMMARY_DATE, SWT.DOWN);
        }
    }

    /**
     * sorts a table by a given column
     * @param table the table
     * @param sortHeader the header of the sort column
     * @param direction the direction
     * @return
     */
    public void sortTable(Table table, String sortHeader, Integer direction) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn tblCol = table.getColumn(i);
            if (tblCol.getText().equals(sortHeader)) {
                //set sort column
                table.setSortColumn(tblCol);
                //set sort direction
                table.setSortDirection(direction);
                tblCol.notifyListeners(SWT.Selection, new Event());
                if (direction == SWT.DOWN) {
                    tblCol.notifyListeners(SWT.Selection, new Event());
                }
                break;
            }
        }
    }

    /**
     * create textfield dfor search filter
     * @param parent composite
     */
    private void createSearchFilter(Composite parent) {
        // "filter by" label
        Label searchLabel = new Label(parent, SWT.NONE);
        searchLabel.setText(Messages.TestresultSummaryFilterLabel);
        // combo box to change column for filter
        m_filterCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        String[] items = new String[] { TESTRESULT_SUMMARY_TESTRUN_ID,
            TESTRESULT_SUMMARY_TEST_JOB,
            TESTRESULT_SUMMARY_TEST_JOB_START_TIME,
            TESTRESULT_SUMMARY_DATE,
            TESTRESULT_SUMMARY_COMMENT_TITLE,
            TESTRESULT_SUMMARY_TESTRUN_STATE,
            TESTRESULT_SUMMARY_PROJECT_NAME,
            TESTRESULT_PROJECT_VERSION,
            TESTRESULT_SUMMARY_TESTSUITE,
            TESTRESULT_SUMMARY_TESTSUITE_STATUS,
            TESTRESULT_SUMMARY_AUT_NAME,
            TESTRESULT_SUMMARY_AUT_ID,
            TESTRESULT_SUMMARY_AUT_CONFIG,
            TESTRESULT_SUMMARY_AUT_HOSTNAME,
            TESTRESULT_SUMMARY_AUT_AGENT_HOSTNAME,
            TESTRESULT_SUMMARY_AUT_OS,
            TESTRESULT_SUMMARY_TOOLKIT,
            TESTRESULT_SUMMARY_START_TIME,
            TESTRESULT_SUMMARY_END_TIME,
            TESTRESULT_SUMMARY_DURATION,
            TESTRESULT_SUMMARY_EXPECTED_CAPS,
            TESTRESULT_SUMMARY_EXECUTED_CAPS,
            TESTRESULT_SUMMARY_HANDLER_CAPS,
            TESTRESULT_SUMMARY_CMD_PARAM,
            TESTRESULT_SUMMARY_TESTRUN_RELEVANT,
            TESTRESULT_SUMMARY_DETAILS_AVAILABLE,
            TESTRESULT_SUMMARY_NUMBER_OF_FAILED_CAPS,
            TESTRESULT_SUMMARY_ADDITIONAL_INFO};
        Arrays.sort(items);
        m_filterCombo.setItems(items);
        m_filterCombo.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                if (e.widget instanceof Combo) {
                    Combo cbx = (Combo)e.widget;
                    m_filter.setFilterType(cbx.getItem(
                            cbx.getSelectionIndex()));
                    m_tableViewer.refresh();
                }
            }
        });
        int index = m_filterCombo.indexOf(TESTRESULT_SUMMARY_TESTRUN_STATE);
        if (index != -1) {
            m_filterCombo.select(index);
            m_filter.setFilterType(TESTRESULT_SUMMARY_TESTRUN_STATE);
        } else {
            m_filterCombo.select(0);
        }

        createLabel(parent);

        // search filter textfield
        m_searchText = new Text(parent, SWT.BORDER | SWT.SEARCH);
        m_searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL));
        m_searchText.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent ke) {
                if (m_filterJob.cancel()) {
                    m_filterJob = new TestresultFilterJob(
                            Messages.JobFilterSummaryView,
                            m_searchText.getText());
                    JobUtils.executeJob(m_filterJob, null, 
                            FILTER_CONTROL_INPUT_DELAY);
                }
            }
        });
        
        addFilterBackgroundColoring();
    }

    /**
     * Adds Filter Background Coloring functionality
     */
    private void addFilterBackgroundColoring() {
        m_searchText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (!m_searchText.getText().isEmpty() 
                    && m_store.getBoolean(Constants.BACKGROUND_COLORING_KEY)) {
                    m_tableViewer.getControl().setBackground(
                        new Color(Display.getCurrent(), intToRgb(
                            m_store.getInt(Constants.BACKGROUND_COLOR_KEY))));
                } else {
                    m_tableViewer.getControl().setBackground(
                        Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
                }
            }
        });
    }
    
    /**
     * Converts a given int color to a SWT RGB color object
     * @param intColor the int color
     * @return the RGB color object
     */
    public static RGB intToRgb(int intColor) {
        java.awt.Color color = new java.awt.Color(intColor);
        return new RGB(color.getRed(), color.getGreen(), color.getBlue());
    }
    
    /**
     * @param parent
     *            the parent to use for label creation
     */
    private void createLabel(Composite parent) {
        IPreferenceStore ps = Plugin.getDefault().getPreferenceStore();
        final Label forLabel = new Label(parent, SWT.NONE);
        forLabel.setText(NLS.bind(Messages.TestresultSummaryForLabel,
                ps.getInt(Constants.MAX_NUMBER_OF_DAYS_KEY)));
        ps.addPropertyChangeListener(new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty()
                        .equals(Constants.MAX_NUMBER_OF_DAYS_KEY)) {
                    forLabel.setText(NLS.bind(
                            Messages.TestresultSummaryForLabel,
                            event.getNewValue()));
                }
            }
        });
    }

    /**
     * get the testrun ids of selected testruns
     * @return the testrun ids of selected testruns
     */
    public Long[] getSelectedTestrunIds() {
        ISelection selection = m_tableViewer.getSelection();
        if (selection instanceof IStructuredSelection) {
            List<Long> selectedIds = new ArrayList<Long>();
            for (Object selectedItem 
                    : ((IStructuredSelection)selection).toArray()) {
                ITestResultSummaryPO summary = 
                    (ITestResultSummaryPO)selectedItem;
                selectedIds.add(summary.getId());
            }
            return selectedIds.toArray(new Long [selectedIds.size()]);
        }

        return new Long[0];
    }
    
    
    /**
     * delete selected testresults
     * @param testrunIds testruns which will be deleted
     */
    public void deleteTestresults(final Long[] testrunIds) {
        final String jobName = Messages.UIJobDeletingTestResultFromDB;
        Job job = new Job(jobName) {
            public IStatus run(IProgressMonitor monitor) {
                monitor.beginTask(jobName, IProgressMonitor.UNKNOWN);
                TestResultSummaryPM.deleteTestruns(testrunIds);
                loadViewInput();
                monitor.done();
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }
    
    /**
     * refresh view
     */
    public void loadViewInput() {
        m_tableViewer.getControl().getDisplay().asyncExec(new Runnable() {
            public void run() {
                List<ITestResultSummaryPO> metaList;
                try {
                    int maxNoOfDays = Plugin.getDefault().getPreferenceStore()
                            .getInt(Constants.MAX_NUMBER_OF_DAYS_KEY);
                    Date startTime = DateUtils.addDays(new Date(), 
                            maxNoOfDays * -1);
                    metaList = TestResultSummaryPM
                            .findAllTestResultSummaries(startTime);
                    
                    if (metaList != null) {
                        m_tableViewer.setInput(metaList.toArray());
                    }
                } catch (JBException e) {
                    String msg = Messages.CantLoadMetadataFromDatabase;
                    log.error(msg, e);
                    showErrorDialog(msg);
                }
                // re-set the selection as this could otherwise lead to cached selected
                // POs which are not up-to-date and lead to db-problems on
                // EntityManager.merge();
                ISelection s = m_tableViewer.getSelection();
                m_tableViewer.setSelection(null);
                m_tableViewer.setSelection(s);
            }
        });
    }

    /**
     * set layout for table viewer
     */
    private void setTableViewerLayout() {
        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalSpan = 4;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        m_tableViewer.getControl().setLayoutData(gridData);
        
    }

    /**
     * 
     */
    public void manageColumnWidths() {
        int availableWidth = m_tableViewer.getTable().getBounds().width;
        final ScrollBar verticalBar = m_tableViewer.getTable().getVerticalBar();
        if (verticalBar.isVisible()) {
            // The +5 because of the space between table and the vertical scrollbar
            availableWidth -= verticalBar.getSize().x + 5;            
        }
        TableColumn[] columns = m_tableViewer.getTable().getColumns();
        List<TableColumn> columnsWithVariableWidth = new ArrayList<>();
        for (TableColumn column : columns) {
            String columnName = column.getText();
            if (columnName.equals(TESTRESULT_SUMMARY_DATE)
                    || columnName.equals(TESTRESULT_SUMMARY_DETAILS_AVAILABLE)
                    || columnName.equals(TESTRESULT_PROJECT_VERSION)
                    || columnName.equals(TESTRESULT_SUMMARY_START_TIME)
                    || columnName.equals(TESTRESULT_SUMMARY_END_TIME)
                    || columnName.equals(TESTRESULT_SUMMARY_DURATION)
                    || columnName.equals(TESTRESULT_SUMMARY_EXECUTED_CAPS)
                    || columnName.equals(TESTRESULT_SUMMARY_EXPECTED_CAPS)
                    || columnName.equals(TESTRESULT_SUMMARY_HANDLER_CAPS)
                    || columnName.equals(TESTRESULT_SUMMARY_TESTRUN_RELEVANT)
                    || columnName.equals(TESTRESULT_SUMMARY_TOOLKIT)
                    || columnName.equals(TESTRESULT_SUMMARY_TESTRUN_STATE)) {
                availableWidth -= column.getWidth();
            } else if (column.getWidth() > 0) {
                columnsWithVariableWidth.add(column);
            }
        }
        int numberOfVariableColums = columnsWithVariableWidth.size();
        if (numberOfVariableColums == 0) {
            return;
        }
        int columnWidth = Math.max(150, 
                availableWidth / numberOfVariableColums);
        for (TableColumn column : columnsWithVariableWidth) {
            column.setWidth(columnWidth);
        }
    }
    
    /**
     * Adds a "AutServer" column to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addAdditonalInfoColumn(
            TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setText(TESTRESULT_SUMMARY_ADDITIONAL_INFO);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return StringUtils.defaultString(
                    ((ITestResultSummaryPO)element).getAdditionalInformation());
            }
        });
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1).getAdditionalInformation(), 
                        ((ITestResultSummaryPO)e2).getAdditionalInformation());
            }
        };
        createMenuItem(m_headerMenu, column.getColumn());
    }
    
    /**
     * Adds a "Testjob name" column to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addTestJobColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setImage(IconConstants.TJ_IMAGE);
        column.getColumn().setText(TESTRESULT_SUMMARY_TEST_JOB);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return StringUtils.defaultString(
                    ((ITestResultSummaryPO)element).getTestJobName());
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1).getTestJobName(), 
                        ((ITestResultSummaryPO)e2).getTestJobName());
            }
        };
    }
    
    /**
     * Adds a "Test job Start Time" column to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addTestJobStartTimeColumn(
            TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setImage(IconConstants.CLOCK_IMAGE);
        column.getColumn().setText(TESTRESULT_SUMMARY_TEST_JOB_START_TIME);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                Date date = ((ITestResultSummaryPO)element)
                        .getTestJobStartTime();
                if (date != null) {
                    return DTF_LONG.format(date);
                }
                return ObjectUtils.toString(date, EMPTY);
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1).getTestJobStartTime(), 
                        ((ITestResultSummaryPO)e2).getTestJobStartTime());
            }
        };
    }

    /**
     * Adds a "Status decorator" column to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addStatusDecoratorColumn(
            TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(60);
        column.getColumn().setToolTipText(
                Messages.TestresultSummaryColumnDescriptionStatus);
        column.getColumn().setText(TESTRESULT_SUMMARY_TESTRUN_STATE);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return null;
            }
            public Image getImage(Object element) {
                ITestResultSummaryPO row = (ITestResultSummaryPO)element;
                switch (row.getTestsuiteStatus()) {
                    case TestResultNode.NOT_YET_TESTED:
                        break;
                    case TestResultNode.NO_VERIFY:
                        return IconConstants.STEP_OK_IMAGE;
                    case TestResultNode.TESTING:
                        return IconConstants.STEP_TESTING_IMAGE;
                    case TestResultNode.SUCCESS:
                        return IconConstants.STEP_OK_IMAGE;
                    case TestResultNode.ERROR:
                    case TestResultNode.CONDITION_FAILED:
                    case TestResultNode.INFINITE_LOOP:
                        return IconConstants.STEP_NOT_OK_IMAGE;
                    case TestResultNode.ERROR_IN_CHILD:
                        return IconConstants.STEP_NOT_OK_IMAGE;
                    case TestResultNode.NOT_TESTED:
                        return IconConstants.STEP_FAILED_IMAGE;
                    case TestResultNode.RETRYING:
                        return IconConstants.STEP_RETRY_IMAGE;
                    case TestResultNode.SUCCESS_RETRY:
                        return IconConstants.STEP_RETRY_OK_IMAGE;
                    case TestResultNode.ABORT:
                        return IconConstants.STEP_NOT_OK_IMAGE;
                    case TestResultNode.SUCCESS_ONLY_SKIPPED:
                        return IconConstants.STEP_SUCCESS_SKIPPED_IMAGE;
                    default:
                        return null;
                }
                
                return null;
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1).getTestRunState(), 
                        ((ITestResultSummaryPO)e2).getTestRunState());
            }
        };
    }
    
    /**
     * Adds a "Project Name" column to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addProjectNameColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setImage(IconConstants.PROJECT_IMAGE);
        column.getColumn().setText(TESTRESULT_SUMMARY_PROJECT_NAME);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return StringUtils.defaultString(
                    ((ITestResultSummaryPO)element).getProjectName());
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1).getProjectName(), 
                        ((ITestResultSummaryPO)e2).getProjectName());
            }
        };
    }
    

    /** Gives the project version of a test result summary [major.minor]
     * @param element the test result summary
     * @return the project version
     */
    private String getProjectVersion(ITestResultSummaryPO element) {
        ProjectVersion version = new ProjectVersion(
                element.getProjectMajorVersion(),
                element.getProjectMinorVersion(),
                element.getProjectMicroVersion(),
                element.getProjectVersionQualifier());
        return StringUtils.defaultString(version.toString());
    }
    
    /**
     * Adds a "Project Name" column to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addProjectVersionColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setImage(IconConstants.PROJECT_IMAGE);
        column.getColumn().setText(TESTRESULT_PROJECT_VERSION);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return getProjectVersion((ITestResultSummaryPO) element);
            }

        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                ITestResultSummaryPO testResultSummary1 = 
                        (ITestResultSummaryPO)e1;
                ITestResultSummaryPO testResultSummary2 = 
                        (ITestResultSummaryPO)e2;
                int majDif = testResultSummary1.getProjectMajorVersion()
                        - testResultSummary2.getProjectMajorVersion();
                if (majDif != 0) {
                    return majDif;
                }
                return testResultSummary1.getProjectMinorVersion()
                        - testResultSummary2.getProjectMinorVersion();
            }
        };
    }

    /**
     * Adds a "Testsuite name" column to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addTestsuiteColumn(TableViewer tableViewer) {
        final TableViewerColumn column =
                new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(250);
        column.getColumn().setImage(IconConstants.TS_IMAGE);
        column.getColumn().setText(TESTRESULT_SUMMARY_TESTSUITE);
        column.getColumn().setMoveable(true);

        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return StringUtils.defaultString(
                    ((ITestResultSummaryPO)element).getTestsuiteName());
            }
        });
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1).getTestsuiteName(), 
                        ((ITestResultSummaryPO)e2).getTestsuiteName());
            }
        };
        createMenuItem(m_headerMenu, column.getColumn());
    }

    /**
     * Adds a "Testsuite status" column to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addTsStatusColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setImage(IconConstants.TS_IMAGE);
        column.getColumn().setText(TESTRESULT_SUMMARY_TESTSUITE_STATUS);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return StringUtils.defaultString(
                    ((ITestResultSummaryPO)element).getStatusString());
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1).getStatusString(), 
                        ((ITestResultSummaryPO)e2).getStatusString());
            }
        };
    }

    /**
     * Adds a "Aut Name" column to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addAutNameColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setImage(IconConstants.AUT_RUNNING_IMAGE);
        column.getColumn().setText(TESTRESULT_SUMMARY_AUT_NAME);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return StringUtils.defaultString(
                    ((ITestResultSummaryPO)element).getAutName());
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1).getAutName(), 
                        ((ITestResultSummaryPO)e2).getAutName());
            }
        };
    }
    
    /**
     * Adds a "Aut Id" column to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addAutIdColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setImage(IconConstants.AUT_RUNNING_IMAGE);
        column.getColumn().setText(TESTRESULT_SUMMARY_AUT_ID);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return StringUtils.defaultString(
                    ((ITestResultSummaryPO)element).getAutId());
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1).getAutId(), 
                        ((ITestResultSummaryPO)e2).getAutId());
            }
        };
    }

    /**
     * Adds a "Aut Config" column to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addAutConfColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setImage(IconConstants.AUT_RUNNING_IMAGE);
        column.getColumn().setText(TESTRESULT_SUMMARY_AUT_CONFIG);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return StringUtils.defaultString(
                    ((ITestResultSummaryPO)element).getAutConfigName());
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1).getAutConfigName(), 
                        ((ITestResultSummaryPO)e2).getAutConfigName());
            }
        };
    }

    /**
     * Adds a "AutServer" column to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addAutAgentHostnameColumn(
            TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setImage(IconConstants.AUT_RUNNING_IMAGE);
        column.getColumn().setText(TESTRESULT_SUMMARY_AUT_AGENT_HOSTNAME);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return StringUtils.defaultString(
                    ((ITestResultSummaryPO)element).getAutAgentName());
            }
        });
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1).getAutAgentName(), 
                        ((ITestResultSummaryPO)e2).getAutAgentName());
            }
        };
        createMenuItem(m_headerMenu, column.getColumn());
    }
    
    /**
     * Adds a "AutHostname" column to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addAutHostnameColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setImage(IconConstants.AUT_RUNNING_IMAGE);
        column.getColumn().setText(TESTRESULT_SUMMARY_AUT_HOSTNAME);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return StringUtils.defaultString(
                    ((ITestResultSummaryPO)element).getAutHostname());
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1).getAutHostname(), 
                        ((ITestResultSummaryPO)e2).getAutHostname());
            }
        };
    }
    
    /**
     * Adds a "AutOS" column to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addAutOSColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setImage(IconConstants.AUT_RUNNING_IMAGE);
        column.getColumn().setText(TESTRESULT_SUMMARY_AUT_OS);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return StringUtils.defaultString(
                    ((ITestResultSummaryPO)element).getAutOS());
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1).getAutOS(), 
                        ((ITestResultSummaryPO)e2).getAutOS());
            }
        };
    }

    /**
     * Adds a "Toolkit" column to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addToolkitColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setText(TESTRESULT_SUMMARY_TOOLKIT);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                String name = null;
                try {
                    name = ToolkitSupportBP.getToolkitDescriptor((
                                (ITestResultSummaryPO) element).getAutToolkit())
                                    .getName();
                } catch (ToolkitPluginException e) {
                    // ignore
                }
                return StringUtils.defaultString(name);
            }
        });

        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                String name1 = null;
                try {
                    name1 = ToolkitSupportBP.getToolkitDescriptor((
                                (ITestResultSummaryPO) e1).getAutToolkit())
                                    .getName();
                } catch (ToolkitPluginException e) {
                    // ignore
                }
                String name2 = null;
                try {
                    name2 = ToolkitSupportBP.getToolkitDescriptor((
                                (ITestResultSummaryPO) e2).getAutToolkit())
                                    .getName();
                } catch (ToolkitPluginException e) {
                    // ignore
                }
                return getCommonsComparator().compare(name1, name2);
            }
        };
    }

    /**
     * Adds a "Date" column to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addDateColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(200);
        column.getColumn().setText(TESTRESULT_SUMMARY_DATE);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return DTF_DEFAULT.format(
                        ((ITestResultSummaryPO)element).getTestsuiteDate());
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1).getTestsuiteDate(), 
                        ((ITestResultSummaryPO)e2).getTestsuiteDate());
            }
        };
    }
    
    /**
     * Adds a "Comment Title" column to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addCommentTitleColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setText(TESTRESULT_SUMMARY_COMMENT_TITLE);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return ObjectUtils.toString(
                    ((ITestResultSummaryPO)element).getCommentTitle());
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1).getCommentTitle(), 
                        ((ITestResultSummaryPO)e2).getCommentTitle());
            }
        };
    }
    
    /**
     * Adds a "Start Time" column to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addStartTimeColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setImage(IconConstants.CLOCK_IMAGE);
        column.getColumn().setText(TESTRESULT_SUMMARY_START_TIME);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return DTF_LONG.format(
                    ((ITestResultSummaryPO)element).getTestsuiteStartTime());
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1).getTestsuiteStartTime(), 
                        ((ITestResultSummaryPO)e2).getTestsuiteStartTime());
            }
        };
    }

    /**
     * Adds a "End Time" column to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addEndTimeColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setImage(IconConstants.CLOCK_IMAGE);
        column.getColumn().setText(TESTRESULT_SUMMARY_END_TIME);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return DTF_LONG.format(
                    ((ITestResultSummaryPO)element).getTestsuiteEndTime());
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1).getTestsuiteEndTime(), 
                        ((ITestResultSummaryPO)e2).getTestsuiteEndTime());
            }
        };
    }

    /**
     * Adds a "Duration" column to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addDurationColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setImage(IconConstants.CLOCK_IMAGE);
        column.getColumn().setText(TESTRESULT_SUMMARY_DURATION);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return StringUtils.defaultString(
                    ((ITestResultSummaryPO)element).getTestsuiteDuration());
            }
        });
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1).getTestsuiteDuration(), 
                        ((ITestResultSummaryPO)e2).getTestsuiteDuration());
            }
        };
        createMenuItem(m_headerMenu, column.getColumn());
    }

    /**
     * Adds a "Expected Caps" column to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addExpecCapsColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setImage(IconConstants.CAP_IMAGE);
        column.getColumn().setText(TESTRESULT_SUMMARY_EXPECTED_CAPS);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return String.valueOf(((ITestResultSummaryPO)element)
                        .getTestsuiteExpectedTeststeps());
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1)
                            .getTestsuiteExpectedTeststeps(), 
                        ((ITestResultSummaryPO)e2)
                            .getTestsuiteExpectedTeststeps());
            }
        };
    }

    /**
     * Adds a "Executed Caps" column to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addExecCapsColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setImage(IconConstants.CAP_IMAGE);
        column.getColumn().setText(TESTRESULT_SUMMARY_EXECUTED_CAPS);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return String.valueOf(((ITestResultSummaryPO)element)
                        .getTestsuiteExecutedTeststeps());
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1)
                            .getTestsuiteExecutedTeststeps(), 
                        ((ITestResultSummaryPO)e2)
                            .getTestsuiteExecutedTeststeps());
            }
        };
    }

    /**
     * Adds a "errorhandler caps" column to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addEventhandlerCapsColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setImage(IconConstants.EH_CAP_IMAGE);
        column.getColumn().setText(TESTRESULT_SUMMARY_HANDLER_CAPS);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return String.valueOf(((ITestResultSummaryPO)element)
                        .getTestsuiteEventHandlerTeststeps());
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1)
                            .getTestsuiteEventHandlerTeststeps(), 
                        ((ITestResultSummaryPO)e2)
                            .getTestsuiteEventHandlerTeststeps());
            }
        };
    }

    /**
     * Adds a "cmd param " column to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addCmdParamColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setText(TESTRESULT_SUMMARY_CMD_PARAM);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return StringUtils.defaultString(
                    ((ITestResultSummaryPO)element).getAutCmdParameter());
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1).getAutCmdParameter(), 
                        ((ITestResultSummaryPO)e2).getAutCmdParameter());
            }
        };
    }
    
    /**
     * Adds a "testrun id" column for birt reporting (test details) to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addSummaryIdColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setText(TESTRESULT_SUMMARY_TESTRUN_ID);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return StringUtils.defaultString(
                    ((ITestResultSummaryPO)element).getId().toString());
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1).getId(), 
                        ((ITestResultSummaryPO)e2).getId());
            }
        };
    }
    
    /**
     * Adds a "testrun relevant" column for birt reporting (test details) to the given viewer.
     * @param tableViewer The viewer to which the column will be added.
     */
    private void addTestRelevantColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setText(TESTRESULT_SUMMARY_TESTRUN_RELEVANT);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return String.valueOf(
                    ((ITestResultSummaryPO)element).isTestsuiteRelevant());
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1).isTestsuiteRelevant(), 
                        ((ITestResultSummaryPO)e2).isTestsuiteRelevant());
            }
        };
    }
    
    /**
     * @param tableViewer the table viewer
     */
    private void addMonitoringReportColumn(
            TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setImage(IconConstants.INFO_IMAGE);
        column.getColumn().setText(MONITORING_DETAILS);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {  
                if (((ITestResultSummaryPO)element).isReportWritten()) {
                    return Messages.TestresultSummaryMonitoringDetailsAvailable;
                }                
                return Messages.TestresultSummaryMonitoringDetailsNotAvailable;
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1).isReportWritten(), 
                        ((ITestResultSummaryPO)e2).isReportWritten());
            }
        };
    }
    
    /**
     * @param tableViewer the tableViewer
     */
    private void addMonitoringIdColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setImage(IconConstants.INFO_IMAGE);
        column.getColumn().setText(MONITORING_ID);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                return StringUtils.defaultIfEmpty(
                    ((ITestResultSummaryPO)element).getInternalMonitoringId(), 
                    Messages.TestresultSummaryMonitoringIdNonSelected);
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1).getInternalMonitoringId(), 
                        ((ITestResultSummaryPO)e2).getInternalMonitoringId());
            }
        };
    }
    
    /**
     * @param tableViewer the tableViewer
     */
    private void addMonitoringValueColumn(TableViewer tableViewer) {
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setWidth(0);
        column.getColumn().setImage(IconConstants.INFO_IMAGE);
        column.getColumn().setText(MONITORING_VALUE);
        column.getColumn().setMoveable(true);        
        column.setLabelProvider(new TestresultSummaryViewColumnLabelProvider() {
            public String getText(Object element) {
                String monitoringValue = 
                    ((ITestResultSummaryPO)element).getMonitoringValue();
                String monitoringId = 
                    ((ITestResultSummaryPO)element).getInternalMonitoringId();
                String monitoringValueTyp = 
                    ((ITestResultSummaryPO)element).getMonitoringValueType(); 
                if (monitoringId != null && monitoringValue != null) { 
                    if (monitoringValueTyp.equals(
                            MonitoringConstants.PERCENT_VALUE)) {  
                        DecimalFormat n = new DecimalFormat("0.0#%"); //$NON-NLS-1$
                        Double doubleValue = Double.valueOf(monitoringValue);
                        return StringUtils.defaultString(
                                n.format(doubleValue.doubleValue())); 
                    }
                    if (monitoringValueTyp.equals(
                            MonitoringConstants.DOUBLE_VALUE)) {
                        return String.format(Locale.getDefault(), 
                                "%f", monitoringValue); //$NON-NLS-1$
                    }
                    return StringUtils.defaultString(monitoringValue);
                } 
                return Messages.TestresultSummaryMonitoringValueNotAvailable;  
            }
        });
        createMenuItem(m_headerMenu, column.getColumn());
        new ColumnViewerSorter(tableViewer, column) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                return getCommonsComparator().compare(
                        ((ITestResultSummaryPO)e1).getMonitoringValue(), 
                        ((ITestResultSummaryPO)e2).getMonitoringValue());
            }
        };
    }
    
    /**
     * Opens an error dialog.
     * @param message the message to show in the dialog.
     */
    private void showErrorDialog(String message) {
        ErrorHandlingUtil.createMessageDialog(new JBException(message,
                MessageIDs.E_PERSISTENCE_LOAD_FAILED), null,
                new String[] { message });
    }

    /**
     * create menus for columns
     * @param menu Menu
     * @param column TableColumn
     */
    private void createMenuItem(Menu menu, final TableColumn column) {
        final MenuItem itemName = new MenuItem(menu, SWT.CHECK);
        itemName.setText(column.getText());
        itemName.setSelection(false);
        if (column.getWidth() > 0) {
            itemName.setSelection(true);
        }
        itemName.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (itemName.getSelection()) {
                    column.setWidth(150);
                    column.setResizable(true);
                } else {
                    column.setWidth(0);
                    column.setResizable(false);
                }
            }
        });

    }

    /**
     * {@inheritDoc}
     */
    public void setFocus() {
        m_tableViewer.getControl().setFocus();
    }
    
    /**
     * {@inheritDoc}
     */
    public void dispose() {
        DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.removeTestresultListener(this);
        ded.removeTestresultSummaryEventListener(this);
        DatabaseStateDispatcher.removeDatabaseStateListener(this);
        super.dispose();
    }
    
    /**
     * Clears the view (table).
     */
    public void clear() {
        m_tableViewer.getControl().getDisplay().syncExec(new Runnable() {
            public void run() {
                // avoid resetting selection on database change
                m_tableViewer.setSelection(StructuredSelection.EMPTY);
                m_tableViewer.setInput(ArrayUtils.EMPTY_OBJECT_ARRAY);
                m_tableViewer.refresh(true);
            }
        });
    }

    /** {@inheritDoc} */
    public void handleTestresultChanged(TestresultState state) {
        switch (state) {
            case Clear:
                clear();
                break;
            case Refresh:
                loadViewInput();
                break;
            default:
                break;
        }
    }

    /** {@inheritDoc} */
    public void handleTestresultSummaryChanged(ITestResultSummaryPO summary, 
        DataState state) {
        loadViewInput();
    }
   
    /**
     * @author BREDEX GmbH
     * @created Nov 23, 2010
     */
    private class TestresultFilterJob extends Job {
        /**
         * <code>m_filterText</code>
         */
        private String m_filterText = EMPTY;
        
        /**
         * @param name the name of the job
         * @param filterText the filter Pattern
         */
        public TestresultFilterJob(String name, String filterText) {
            super(name);
            m_filterText = filterText;
        }

        /**
         * {@inheritDoc}
         */
        protected IStatus run(IProgressMonitor monitor) {
            m_tableViewer.getTable().getDisplay().syncExec(new Runnable() {
                public void run() {
                    m_filter.setPattern(m_filterText);
                    m_tableViewer.refresh();
                }
            });
            return Status.OK_STATUS;
        }
    }
    
    /**
     * @author BREDEX GmbH
     * @created Jan 28, 2010
     */
    private class TestresultSummaryFilter extends JBPatternFilter {
        /**
         * defines, which column should be the filter value
         */
        private String m_filterType = StringUtils.EMPTY;

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("synthetic-access")
        public boolean isElementVisible(Viewer viewer, Object element) {
            ITestResultSummaryPO m = (ITestResultSummaryPO) element;
            String metaValue = EMPTY;
            if (m_filterType.equals(TESTRESULT_SUMMARY_DATE)) {
                metaValue = DTF_DEFAULT.format(m.getTestsuiteDate());
            } else if (m_filterType.equals(TESTRESULT_SUMMARY_TESTRUN_ID)) {
                metaValue = String.valueOf(m.getId());
            } else if (m_filterType
                    .equals(TESTRESULT_SUMMARY_TEST_JOB_START_TIME)) {
                Date date = m.getTestJobStartTime();
                metaValue = date != null ? DTF_LONG.format(date) : EMPTY;
            } else if (m_filterType.equals(TESTRESULT_SUMMARY_TEST_JOB)) {
                metaValue = m.getTestJobName();
            } else if (m_filterType.equals(TESTRESULT_SUMMARY_TESTRUN_STATE)) {
                metaValue = m.getTestRunState();
            } else if (m_filterType.equals(TESTRESULT_SUMMARY_PROJECT_NAME)) {
                metaValue = m.getProjectName();
            } else if (m_filterType.equals(TESTRESULT_PROJECT_VERSION)) {
                metaValue = getProjectVersion(m);
            } else if (m_filterType.equals(TESTRESULT_SUMMARY_TESTSUITE)) {
                metaValue = m.getTestsuiteName();
            } else if (m_filterType.equals(
                    TESTRESULT_SUMMARY_TESTSUITE_STATUS)) {
                metaValue = m.getStatusString();
            } else if (m_filterType.equals(TESTRESULT_SUMMARY_AUT_NAME)) {
                metaValue = m.getAutName();
            } else if (m_filterType.equals(TESTRESULT_SUMMARY_AUT_ID)) {
                metaValue = m.getAutId();
            } else if (m_filterType.equals(TESTRESULT_SUMMARY_AUT_CONFIG)) {
                metaValue = m.getAutConfigName();
            } else if (m_filterType
                    .equals(TESTRESULT_SUMMARY_AUT_AGENT_HOSTNAME)) {
                metaValue = m.getAutAgentName();
            } else if (m_filterType.equals(TESTRESULT_SUMMARY_AUT_HOSTNAME)) {
                metaValue = m.getAutHostname();
            } else if (m_filterType.equals(TESTRESULT_SUMMARY_AUT_OS)) {
                metaValue = m.getAutOS();
            } else if (m_filterType.equals(TESTRESULT_SUMMARY_TOOLKIT)) {
                metaValue = m.getAutToolkit();
            } else if (m_filterType.equals(TESTRESULT_SUMMARY_START_TIME)) {
                metaValue = DTF_LONG.format(m.getTestsuiteStartTime());
            } else if (m_filterType.equals(TESTRESULT_SUMMARY_END_TIME)) {
                metaValue = DTF_LONG.format(m.getTestsuiteEndTime());
            } else if (m_filterType.equals(TESTRESULT_SUMMARY_DURATION)) {
                metaValue = m.getTestsuiteDuration();
            } else if (m_filterType.equals(TESTRESULT_SUMMARY_EXPECTED_CAPS)) {
                metaValue = String.valueOf(m.getTestsuiteExpectedTeststeps());
            } else if (m_filterType.equals(TESTRESULT_SUMMARY_EXECUTED_CAPS)) {
                metaValue = String.valueOf(m.getTestsuiteExecutedTeststeps());
            } else if (m_filterType.equals(TESTRESULT_SUMMARY_HANDLER_CAPS)) {
                metaValue = String.valueOf(m
                        .getTestsuiteEventHandlerTeststeps());
            } else if (m_filterType.equals(TESTRESULT_SUMMARY_CMD_PARAM)) {
                metaValue = m.getAutCmdParameter();
            } else if (m_filterType.equals(
                    TESTRESULT_SUMMARY_TESTRUN_RELEVANT)) {
                metaValue = String.valueOf(m.isTestsuiteRelevant());
            } else if (m_filterType
                    .equals(TESTRESULT_SUMMARY_DETAILS_AVAILABLE)) {
                metaValue = String.valueOf(m.hasTestResultDetails());
            } else if (m_filterType
                    .equals(TESTRESULT_SUMMARY_NUMBER_OF_FAILED_CAPS)) {
                metaValue = String.valueOf(m.getTestsuiteFailedTeststeps());
            } else if (m_filterType
                    .equals(TESTRESULT_SUMMARY_COMMENT_TITLE)) {
                metaValue = StringUtils.defaultString(m.getCommentTitle());
            } else if (m_filterType
                    .equals(TESTRESULT_SUMMARY_ADDITIONAL_INFO)) {
                metaValue = StringUtils.defaultString(
                        m.getAdditionalInformation());
            }
            return wordMatches(metaValue);
        }

        /**
         * @param filterType the filterType to set
         */
        public void setFilterType(String filterType) {
            m_filterType = filterType;
        }
    }
    
    /**
     * Creates and returns a comparator for natural comparison that can also 
     * handle <code>null</code> values. 
     * 
     * @return the created comparator.
     */
    @SuppressWarnings("rawtypes")
    private static Comparator getCommonsComparator() {
        return ComparatorUtils.nullHighComparator(
                ComparatorUtils.naturalComparator());
    }

    /**
     * {@inheritDoc}
     */
    public void reactOnDatabaseEvent(DatabaseStateEvent e) {
        switch (e.getState()) {
            case DB_LOGIN_SUCCEEDED:
                loadViewInput();
                break;
            case DB_LOGOUT_SUCCEEDED:
                clear();
                break;
            default:
                break;
        }
    }
    
}