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
package org.eclipse.jubula.client.ui.rcp.widgets;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jubula.client.core.businessprocess.CalcTypes;
import org.eclipse.jubula.client.core.businessprocess.CompNameManager;
import org.eclipse.jubula.client.core.businessprocess.CompNamesBP;
import org.eclipse.jubula.client.core.businessprocess.IComponentNameCache;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.events.DataChangedEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.dialogs.CNTypeProblemDialog;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.editors.IJBEditor;
import org.eclipse.jubula.client.ui.rcp.editors.JBEditorHelper.EditableState;
import org.eclipse.jubula.client.ui.rcp.editors.TestSuiteEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPart;


/**
 * @author Markus Tiede
 * @created Jul 25, 2011
 */
public class ComponentNamesTableComposite extends Composite implements
    IDataChangedListener, DisposeListener {
    /**
     * Internal name of the second column.
     */
    private static final String COLUMN_PROPAGATE = "propagate"; //$NON-NLS-1$
    /**
     * Internal name of the third column.
     */
    private static final String COLUMN_OLD_NAME = "firstName"; //$NON-NLS-1$
    /**
     * Internal name of the fourth column.
     */
    private static final String COLUMN_NEW_NAME = "secondName"; //$NON-NLS-1$
    /**
     * Internal name of the fifth column.
     */
    private static final String COLUMN_TYPE_NAME = "type"; //$NON-NLS-1$
    
    /** Constant for the default column width */ 
    private static final int COLUMN_WIDTH = 70;
    
    /** Constant for the default new component name column width */ 
    private static final int NEW_NAME_COLUMN_WIDTH = 190;
    
    /** The table viewer */
    private CheckboxTableViewer m_tableViewer;
    
    /** the cell modifier of this the actual tableViewer */
    private CellModifier m_cellModifier;

    /** The business process that performs component name operations. */
    private CompNamesBP m_compNamesBP = new CompNamesBP();
    
    /** flag to indicate if invalid data has been entered */
    private boolean m_invalidData = false;
    
    /**
     * The currently selected test execution node, may be <code>null</code>,
     * if no test execution node is selected.
     */
    private IExecTestCasePO m_selectedExecNode;
    
    /**
     * The owner of the currently selected test execution node, may be
     * <code>null</code>, if no test execution node ist selected or if the
     * part has been closed.
     */
    private IWorkbenchPart m_selectedExecNodeOwner;
    
    /**
     * The currently selected compNamesPair, or <code>nully</code> if no 
     * compNamesPair is currently selected.
     */
    private ICompNamesPairPO m_selectedPair = null;

    /** is view editable at the moment */
    private boolean m_editable;
    
    /** the cell editor listener */
    private CellEditorListener m_cellEditorListener = new CellEditorListener();
    
    /** The selection changed listener of the tableViewer */
    private TableSelectionChangedListener m_selectionChangedListener = 
        new TableSelectionChangedListener();
    
    /** the cell editor for this table */
    private CompNamePopupTextCellEditor m_cellEdit;
    
    /** the check state listener */
    private CheckStateListener m_checkStateListener = new CheckStateListener();
    
    /** the component cache to use for finding and modifying components */
    private IComponentNameCache m_compCache;
    
    /**
     * @param parent
     *            the parent
     * @param style
     *            the style
     */
    public ComponentNamesTableComposite(Composite parent, int style) {
        super(parent, style);
        this.addDisposeListener(this);
        setLayout(this);
        Table table = new Table(this, SWT.BORDER | SWT.CHECK
                | SWT.FULL_SELECTION);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
        final TableColumn tc1 = new TableColumn(table, SWT.CENTER);
        tc1.setImage(IconConstants.PROPAGATE_IMAGE);
        final TableColumn tc2 = new TableColumn(table, SWT.LEFT);
        tc2.setText(Messages.CompNamesViewOldNameColumn);
        final TableColumn tc3 = new TableColumn(table, SWT.LEFT);
        tc3.setText(Messages.CompNamesViewNewNameColumn);
        final TableColumn tc4 = new TableColumn(table, SWT.LEFT);
        tc4.setText(Messages.CompNamesViewTypeColumn);
        final ComponentNamesTableCompositeContentProvider provider =
            new ComponentNamesTableCompositeContentProvider();
        m_tableViewer = new CheckboxTableViewer(table);
        m_tableViewer.setContentProvider(provider);
        m_tableViewer.setLabelProvider(
            new ComponentNamesTableCompositeLabelProvider());
        m_tableViewer.setColumnProperties(new String[] { COLUMN_PROPAGATE, 
            COLUMN_OLD_NAME, COLUMN_NEW_NAME, COLUMN_TYPE_NAME });

        setCompCache(Plugin.getActiveCompCache());
        setCellEdit(new CompNamePopupTextCellEditor(getCompCache(), table));
        m_tableViewer.setCellEditors(new CellEditor[] { null, null, 
            getCellEdit(), null});
        setCellModifier(new CellModifier());
        m_tableViewer.setCellModifier(getCellModifier()); 
        handleListenersAndResources(true);
        Plugin.getHelpSystem().setHelp(this, ContextHelpIds.COMP_NAME);
        setSelectedExecNode(null);
        setSelectedExecNodeOwner(null);
    }

    /**
     * @author BREDEX GmbH
     * @created Jan 22, 2007
     */
    private final class CellEditorListener implements ICellEditorListener {
        /**
         * {@inheritDoc}
         */
        public void applyEditorValue() {
            final Object value = getCellEdit().getValue();
            final String newName = (value != null) 
                ? value.toString() : StringConstants.EMPTY;
            if (getCellEdit().isDirty()) {
                updateSecondName(m_selectedPair, newName);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void cancelEditor() {
            // Do nothing.
        }

        /**
         * {@inheritDoc}
         */
        public void editorValueChanged(boolean oldValidState, 
            boolean newValidState) { 
            // Do nothing.
        }
    }
    
    /**
     * SelectionChangedListener for the tableViewer
     *
     * @author BREDEX GmbH
     * @created 13.06.2006
     */
    private class TableSelectionChangedListener 
        implements ISelectionChangedListener {
        /**
         * {@inheritDoc}
         */
        public void selectionChanged(SelectionChangedEvent event) {
            if (!(event.getSelection() instanceof IStructuredSelection)) {
                return;
            }
            Object o = ((IStructuredSelection)event.getSelection())
                .getFirstElement();
            if (!(o instanceof ICompNamesPairPO && getSelectedExecNodeOwner() 
                        instanceof AbstractTestCaseEditor)) {
                m_selectedPair = null;
                return;
            }
            IWritableComponentNameCache cache = ((AbstractTestCaseEditor)
                    getSelectedExecNodeOwner()).getCompNameCache();

            ICompNamesPairPO pair = (ICompNamesPairPO)o;
            m_selectedPair = pair;
            getCellModifier().setModifiable(m_editable 
                && !StringConstants.EMPTY.equals(pair.getType()));
            if (!getCellModifier().isModifiable()) {
                return;
            }
            CalcTypes calc = new CalcTypes(cache, null);
            String filter = calc.calculateLocalType(getSelectedExecNode()
                    .getSpecTestCase(), pair.getFirstName());
            getCellEdit().setFilter(filter);
            setInvalidData(false);
        }
    }
    
    /**
     * The cell modifier of the table. It supports the modification of the
     * second (new) component name.
     */
    private final class CellModifier implements ICellModifier {
        /**
         * Flag to indicate if the table is modifiable.
         */
        private boolean m_modifiable = true;
        
        /**
         * @param element
         *            The current table element
         * @return The component name pair
         */
        private ICompNamesPairPO getPair(Object element) {
            return (ICompNamesPairPO)(element instanceof Item ? ((Item)element)
                .getData() : element);
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean canModify(Object element, String property) {
            boolean editable = false;
            if (getSelectedExecNodeOwner() instanceof AbstractTestCaseEditor
                    && ((AbstractTestCaseEditor)getSelectedExecNodeOwner())
                            .getEditorHelper().requestEditableState() 
                            == EditableState.OK) {
                editable = true;
            }
            if (editable && element instanceof ICompNamesPairPO) {
                final ICompNamesPairPO compNamesPair = (ICompNamesPairPO)
                    element;
                return COLUMN_NEW_NAME.equals(property) && isModifiable()
                    && CompNamesBP.isValidCompNamePair(compNamesPair);
            }
            return false;
        }
        
        /**
         * {@inheritDoc}
         */
        public Object getValue(Object element, String property) {
            if (getCompCache() != null) {
                return getCompCache().getNameByGuid(
                        getPair(element).getSecondName());
            }
            return CompNameManager.getInstance().getNameByGuid(
                    getPair(element).getSecondName());
        }
        
        /**
         * {@inheritDoc}
         */
        public void modify(Object element, String property, Object value) {
            /*
             * Do nothing, since this method seemed to-do nothing except causing
             * a bug in the component names view. Everything is already handled
             * by CellEditorListener.applyEditorValue().
             */
        }
        
        /**
         * @param modifiable The modifiable property to set.
         */
        public void setModifiable(boolean modifiable) {
            m_modifiable = modifiable;
        }
        
        /**
         * @return Returns the modifiable property.
         */
        public boolean isModifiable() {
            return m_modifiable;
        }
    }
    
    /**
     * @author BREDEX GmbH
     * @created Jan 22, 2007
     */
    private final class CheckStateListener implements ICheckStateListener {
        /**
         * {@inheritDoc}
         */
        public void checkStateChanged(CheckStateChangedEvent event) {
            boolean editable = false;
            if (getSelectedExecNodeOwner() instanceof AbstractTestCaseEditor
                    && ((AbstractTestCaseEditor)getSelectedExecNodeOwner())
                            .getEditorHelper().requestEditableState() 
                            == EditableState.OK) {
                editable = true;
            }

            if (editable) {
                ICompNamesPairPO pair = (ICompNamesPairPO)event.getElement();
                getCellModifier().setModifiable(m_editable
                        && !StringConstants.EMPTY.equals(
                                pair.getType()));
                if (!getCellModifier().isModifiable() 
                        || getSelectedExecNodeOwner() 
                            instanceof TestSuiteEditor) { 
                    
                    // Reset the old value if the table is non-editable.
                    m_tableViewer.setChecked(pair, pair.isPropagated());
                    return;
                }
                updatePropagated(pair, event.getChecked());
            }
        }
    }
    
    /**
     * The content provider of the table.
     */
    private class ComponentNamesTableCompositeContentProvider implements
        IStructuredContentProvider {
        /**
         * {@inheritDoc}
         */
        public Object[] getElements(Object inputElement) {
            return ((List)inputElement).toArray();
        }
        
        /**
         * {@inheritDoc}
         */
        public void dispose() {
            // Nothing to dispose
        }
        
        /**
         * {@inheritDoc}
         */
        public void inputChanged(Viewer viewer, Object oldInput,
            Object newInput) {
            // Nothing to be done
        }
    }
    
    /**
     * The label provider of the table.
     */
    private class ComponentNamesTableCompositeLabelProvider 
        implements ITableLabelProvider {
        /**
         * {@inheritDoc}
         */
        public void addListener(ILabelProviderListener listener) {
            // No listeners supported
        }
        
        /**
         * {@inheritDoc}
         */
        public void dispose() {
            // Nothing to dispose
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }
        
        /**
         * {@inheritDoc}
         */
        public void removeListener(ILabelProviderListener listener) {
            // No listeners supported
        }
        
        /**
         * {@inheritDoc}
         */
        public Image getColumnImage(Object element, int columnIndex) {
            String type = ((ICompNamesPairPO)element).getType();
            switch (columnIndex) {
                case 0:
                    if (StringConstants.EMPTY.equals(type)) { 
                        m_tableViewer.getTable().getColumn(0).pack();
                        return IconConstants.WARNING_IMAGE;
                    }
                    return null;
                default:
                    break;
            }
            return null;
        }
        
        /**
         * {@inheritDoc}
         */
        public String getColumnText(Object element, int columnIndex) {
            ICompNamesPairPO pair = (ICompNamesPairPO)element;
            Table table = m_tableViewer.getTable();
            switch (columnIndex) {
                case 0:
                    return StringConstants.EMPTY;
                case 1:
                    if (getCompCache() != null) {
                        return getCompCache()
                            .getNameByGuid(pair.getFirstName());
                    }
                    return CompNameManager.getInstance().getNameByGuid(
                            pair.getFirstName());
                case 2:
                    if (getCompCache() != null) {
                        return getCompCache().
                            getNameByGuid(pair.getSecondName());
                    }
                    return CompNameManager.getInstance().getNameByGuid(
                            pair.getSecondName());
                case 3:
                    String type = pair.getType();
                    for (int i = 0; i < table.getItems().length; i++) {
                        if (table.getItems()[i].getData() != null
                            && table.getItems()[i].getData().equals(pair)) {
                            
                            if (StringConstants.EMPTY.equals(type)) {
                                type = "CompNamesView.errorText"; //$NON-NLS-1$
                                
                                TableItem item = table.getItem(i);
                                item.setForeground(3, 
                                        item.getDisplay().getSystemColor(
                                                SWT.COLOR_RED));
                                item.setFont(3, LayoutUtil.ITALIC_TAHOMA);
                                if (getSelectedExecNodeOwner() 
                                        instanceof AbstractTestCaseEditor) {
                                    
                                    m_tableViewer.setGrayed(pair, false);
                                    m_tableViewer.setChecked(pair, false);
                                }
                            } else {
                                TableItem item = table.getItem(i);
                                item.setForeground(3, LayoutUtil.GRAY_COLOR);
                                item.setFont(3, LayoutUtil.NORMAL_TAHOMA);
                                if (getSelectedExecNodeOwner() 
                                        instanceof AbstractTestCaseEditor) {
                                    
                                    item.setForeground(3, LayoutUtil
                                        .DEFAULT_OS_COLOR);
                                    m_tableViewer.setGrayed(pair, true);
                                    m_tableViewer.setChecked(pair, true);
                                }
                            }
                        }
                    }
                    return CompSystemI18n.getString(type);
                default:
                    break;
            }
            return null;
        }
    }
    
    /**
     * Sets the type of the ExecTC's CNPairs, some of them new 'transient' ones
     * @param cNPairs the 'transient' CNPairs
     * @param spec the SpecTC of the ExecTC
     */
    private void searchAndSetComponentType(List<ICompNamesPairPO> cNPairs,
            ISpecTestCasePO spec) {
        if (cNPairs.isEmpty()) {
            return;
        }
        IComponentNameCache cache;
        if ((getSelectedExecNodeOwner() instanceof IJBEditor)) {
            EditSupport supp = ((IJBEditor)getSelectedExecNodeOwner()).
                    getEditorHelper().getEditSupport();
            if (supp == null) {
                return;
            }
            cache = supp.getCache();
        } else {
            cache = CompNameManager.getInstance();
        }
        CalcTypes calc = new CalcTypes(cache, null);
        calc.calculateLocalType(spec, null);
        Map<String, String> locMap = calc.getLocalPropTypes(spec);
        for (ICompNamesPairPO pair : cNPairs) {
            pair.setType(locMap.get(CompNameManager.getInstance().
                    resolveGuid(pair.getFirstName())));
        }
    }
    
    /**
     * Sets the layout of the composite.
     * 
     * @param c
     *            the composite
     */
    private void setLayout(Composite c) {
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.verticalSpacing = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        c.setLayout(layout);
        GridData layoutData = new GridData(GridData.FILL_BOTH);
        layoutData.grabExcessHorizontalSpace = true;
        c.setLayoutData(layoutData);
    }
    
    /**
     * Updates the table input by calling
     * {@link CompNamesBP#getAllCompNamesPairs(ExecTestCasePO)} (passing the
     * selected node).
     */
    private void updateTableInput() {
        boolean editable = false;
        INodePO workVersion = null;
        IExecTestCasePO exec = getSelectedExecNode();
        if (getSelectedExecNodeOwner() instanceof AbstractTestCaseEditor) {
            editable = true;
            workVersion = (INodePO)
                    ((AbstractTestCaseEditor) getSelectedExecNodeOwner())
                    .getEditorHelper().getEditSupport().getWorkVersion();
        }
        List<ICompNamesPairPO> input = null;
        if (exec != null) {
            input = m_compNamesBP.getAllCompNamesPairs(exec);
            IWorkbenchPart activePart = Plugin.getActivePart();
            if (activePart instanceof IJBEditor) {
                setCompCache(((IJBEditor)activePart).getCompNameCache());
            }
            ISpecTestCasePO spec = exec.getSpecTestCase();
            if (spec != null) {
                // has to set the type of the CompNamePairPOs, some of them new 'transient' ones
                searchAndSetComponentType(input, spec);
            }
        }
        m_tableViewer.setInput(input);
        // Set the table to (non-) editable. We don't use Control.setEditable()
        // here because this crays out the table, and this doesn't fit
        // the look and feel of other views.

        m_editable = editable;
        for (TableItem item : m_tableViewer.getTable().getItems()) {
            if (editable) {
                item.setForeground(LayoutUtil.DEFAULT_OS_COLOR);
            } else {
                item.setForeground(LayoutUtil.GRAY_COLOR);
            }
            item.setGrayed(!editable);
        }
        // packs all columns (despite of the first)
        Table table = m_tableViewer.getTable();
        if (table.getItemCount() != 0) {
            final TableColumn[] columns = table.getColumns();
            final int columnCount = columns.length;
            for (int i = 1; i < columnCount; i++) {
                TableColumn column = columns[i];
                column.pack();
                if (column.getWidth() < COLUMN_WIDTH) {
                    column.setWidth(COLUMN_WIDTH);
                }
            }
            for (ICompNamesPairPO pair : input) {
                m_tableViewer.setChecked(pair, pair.isPropagated());
            }
            TableColumn propagationColumn = table.getColumn(0);
            propagationColumn.setResizable(false);
            propagationColumn.setWidth(38);

            TableColumn newNameColumn = table.getColumn(2);
            if (newNameColumn.getWidth() < NEW_NAME_COLUMN_WIDTH) {
                newNameColumn.setWidth(NEW_NAME_COLUMN_WIDTH);
            }
        }
        controlPropagation(editable);
    }
    
    /**
     * Updates the propagated property of the pair.
     * 
     * @param pair
     *            The pair
     * @param propagated
     *            The propagated property
     */
    private void updatePropagated(ICompNamesPairPO pair, boolean propagated) {
        if (getSelectedExecNodeOwner() instanceof IJBEditor) {
            IExecTestCasePO exec = getSelectedExecNode();
            if (exec == null) {
                return;
            }
            pair.setPropagated(propagated);
            if (exec.getCompNamesPair(pair.getFirstName()) == null) {
                exec.addCompNamesPair(pair);
            }
            DataEventDispatcher.getInstance().fireDataChangedListener(
                    getSelectedExecNode(), DataState.StructureModified,
                    UpdateState.onlyInEditor);
        }
    }
    
    /**
     * Updates the second name property of the pair.
     * 
     * @param pair
     *            The pair
     * @param secondName
     *            The second name
     */
    private void updateSecondName(ICompNamesPairPO pair, String secondName) {
        if (getInvalidData()
            || !(getSelectedExecNodeOwner() instanceof IJBEditor)
            || getSelectedExecNode() == null) {
            return;
        }
        if (secondName == null || secondName.length() == 0) {
            return;
        }
        final AbstractTestCaseEditor editor = 
            (AbstractTestCaseEditor)getSelectedExecNodeOwner();
        IWritableComponentNameCache cache = editor.getCompNameCache();
        String oldName = pair.getSecondName();
        IComponentNamePO cN = cache.getResCompNamePOByGuid(oldName);
        if (cN != null) {
            oldName = cN.getName();
        }
        INodePO root = (INodePO) editor.getEditorHelper().getEditSupport().
                getWorkVersion();
        
        m_compNamesBP.updateCompNamesPairNew(getSelectedExecNode(),
                pair, secondName, cache);
        setInvalidData(true);
        if (!CNTypeProblemDialog.noProblemOrIgnore(cache, root)) {
            m_compNamesBP.updateCompNamesPairNew(getSelectedExecNode(),
                    pair, oldName, cache);
            return;
        }

        setInvalidData(false);
        
        DataEventDispatcher.getInstance().fireDataChangedListener(
                getSelectedExecNode(), DataState.StructureModified,
                UpdateState.onlyInEditor);
    }
    
    /**
     * @param invalidData the flag to indicate that invalid data has been detected
     */
    private void setInvalidData(boolean invalidData) {
        m_invalidData = invalidData;
    }

    /**
     * @return the invalidData flag
     */
    private boolean getInvalidData() {
        return m_invalidData;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean setFocus() {
        return m_tableViewer.getTable().setFocus();
    }
    
    /**
     * @param selectedExecNode the selectedExecNode to set
     */
    public void setSelectedExecNode(IExecTestCasePO selectedExecNode) {
        m_selectedExecNode = selectedExecNode;
        getCellEdit().setSelectedNode(selectedExecNode);
        updateTableInput();
    }

    /**
     * @return the selectedExecNode
     */
    private IExecTestCasePO getSelectedExecNode() {
        return m_selectedExecNode;
    }

    /**
     * @param selectedExecNodeOwner the selectedExecNodeOwner to set
     */
    public void setSelectedExecNodeOwner(IWorkbenchPart selectedExecNodeOwner) {
        m_selectedExecNodeOwner = selectedExecNodeOwner;
    }

    /**
     * @return the selectedExecNodeOwner
     */
    private IWorkbenchPart getSelectedExecNodeOwner() {
        return m_selectedExecNodeOwner;
    }

    /**
     * @param cellModifier the cellModifier to set
     */
    private void setCellModifier(CellModifier cellModifier) {
        m_cellModifier = cellModifier;
    }

    /**
     * @return the cellModifier
     */
    private CellModifier getCellModifier() {
        return m_cellModifier;
    }
    
    /**
     * control the editability of the propagation column
     * 
     * @param allow
     *            to allow propagation
     */
    public void controlPropagation(boolean allow) {
        getCellModifier().setModifiable(allow);
    }

    /**
     * @param compCache the compCache to set
     */
    private void setCompCache(IComponentNameCache compCache) {
        m_compCache = compCache;
    }

    /**
     * @return the compCache
     */
    private IComponentNameCache getCompCache() {
        return m_compCache;
    }

    /**
     * @param cellEdit the cellEdit to set
     */
    private void setCellEdit(CompNamePopupTextCellEditor cellEdit) {
        m_cellEdit = cellEdit;
    }

    /**
     * @return the cellEdit
     */
    public CompNamePopupTextCellEditor getCellEdit() {
        return m_cellEdit;
    }

    /** {@inheritDoc} */
    public void handleDataChanged(final DataChangedEvent... events) {
        Plugin.getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                for (DataChangedEvent e : events) {
                    handleDataChanged(e.getPo(), e.getDataState());
                }
            }
            
        });
    }
    
    /**
     * {@inheritDoc}
     */
    public void handleDataChanged(final IPersistentObject po,
            DataState dataState) {
        IExecTestCasePO exec = getSelectedExecNode();
        if (po != null && exec != null
                && po.equals(exec.getSpecAncestor())) {
            updateTableInput();
            return;
        }
        if (po instanceof IExecTestCasePO
                || po instanceof ISpecTestCasePO
                || po instanceof IComponentNamePO
                || po instanceof ICompNamesPairPO) {
            m_tableViewer.refresh();
        }
    }

    /**
     * Adds and disposes of resources - should be called from constructors and dispose listeners
     * @param add whether adding or disposing
     */
    private void handleListenersAndResources(boolean add) {
        if (add) {
            getCellEdit().addListener(m_cellEditorListener);
            m_tableViewer.addCheckStateListener(m_checkStateListener);
            m_tableViewer.addSelectionChangedListener(
                    m_selectionChangedListener);
            DataEventDispatcher.getInstance().addDataChangedListener(
                    this, true);
            return;
        }
        getCellEdit().removeListener(m_cellEditorListener);
        m_tableViewer.removeCheckStateListener(m_checkStateListener);
        m_tableViewer.removeSelectionChangedListener(
                m_selectionChangedListener);
        m_tableViewer = null;
        DataEventDispatcher.getInstance().removeDataChangedListener(this);
    }
    
    @Override
    public void widgetDisposed(DisposeEvent e) {
        handleListenersAndResources(false);
    }
}
