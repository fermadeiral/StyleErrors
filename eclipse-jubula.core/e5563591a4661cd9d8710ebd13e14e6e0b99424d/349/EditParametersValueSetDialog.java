/*******************************************************************************
 * Copyright (c) 2018 BREDEX GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: BREDEX GmbH - initial API and implementation and/or initial
 * documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.dialogs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.dialogs.AbstractEditParametersDialog.Parameter;
import org.eclipse.jubula.client.ui.rcp.dialogs.AbstractEditParametersDialog.ValueComment;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.TestDataConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import static org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent.TRAVERSAL;
import static org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent.KEY_PRESSED;
import static org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent.PROGRAMMATIC;
import static org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION;

/**
 * @author BREDEX GmbH
 *
 */
public class EditParametersValueSetDialog extends TitleAreaDialog {

    /** the index of the value column */
    private static final int VALUE_COLUMN_INDEX = 0;
    /** the index of the comment column */
    private static final int COMMENT_COLUMN_INDEX = 1;
    /** the comment column name */
    private static final String COMMENT_COLUMN_NAME =
            Messages.EditValueSetDialogCommentTableColumnName;
    /** the value column name */
    private static final String VALUE_COLUMN_NAME =
            Messages.EditValueSetDialogValueTableColumnName;
    /** the value set */
    private List<ValueComment> m_valueSets = new ArrayList<>();
    /** default value*/
    private String m_defaultValue;
    /** the type of the parameter*/
    private String m_parameterType;
    
    /** default value combo box*/
    private Combo m_defaultValueCombo;
    /** the table viewer */
    private TableViewer m_tableViewer = null;

    /**
     * @param parentShell the parent shell
     * @param param the {@link Parameter} to change to value set
     */
    public EditParametersValueSetDialog(Shell parentShell, Parameter param) {
        super(parentShell);
        m_parameterType = param.getType();
        m_defaultValue = param.getDefaultValue();
        for (ValueComment vc : param.getValueSet()) {
            m_valueSets.add(new ValueComment(vc.getValue(), vc.getComment()));
        }
    }

    /**
     * {@inheritDoc}
     */
    protected Point getInitialSize() {
        final Point initialSize = super.getInitialSize();
        initialSize.y = 500;
        return initialSize;
    }

    /**
     * @return list of {@link ValueComment}
     */
    public List<ValueComment> getValueSets() {
        return m_valueSets;
    }
    
    /**
     * @return the default value String
     */
    public String getDefaultValue() {
        return m_defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    protected void setShellStyle(int newShellStyle) {
        super.setShellStyle(newShellStyle | SWT.RESIZE);
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        final String dialogTitle = Messages.EditValueSetDialogTitle;
        setTitle(dialogTitle);
        Plugin.getHelpSystem().setHelp(parent, ContextHelpIds.EDIT_PARAMETERS);
        setHelpAvailable(true);
        setMessage(Messages.EditValueSetDialogMessage);

        getShell().setText(dialogTitle);

        final GridLayout gridLayoutParent = new GridLayout();
        gridLayoutParent.numColumns = 1;
        parent.setLayout(gridLayoutParent);
        LayoutUtil.createSeparator(parent);

        final Composite area = new Composite(parent, SWT.NONE);
        final GridLayout areaLayout = new GridLayout();
        area.setLayout(areaLayout);
        final GridData areaGridData = new GridData();
        areaGridData.grabExcessVerticalSpace = true;
        areaGridData.grabExcessHorizontalSpace = true;
        areaGridData.horizontalAlignment = GridData.FILL;
        areaGridData.verticalAlignment = GridData.FILL;
        area.setLayoutData(areaGridData);

        createTableComposite(area);
        createDefaultValueCombo(area);
        return parent;

    }

    /**
     * @param parent the parent control
     */
    private void createDefaultValueCombo(Composite parent) {
        final Label label = new Label(parent, SWT.NONE);
        label.setText(Messages.EditValueSetDialogComboLabel);
        m_defaultValueCombo = new Combo(parent, SWT.READ_ONLY);
        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.BEGINNING;
        gridData.minimumWidth = 150;
        gridData.widthHint = 150;
        m_defaultValueCombo.setLayoutData(gridData);
        setComboBoxValues();
        
        m_defaultValueCombo.addSelectionListener(
                SelectionListener.widgetSelectedAdapter(c -> {
                    String[] items = m_defaultValueCombo.getItems();
                    int selectionIndex =
                            m_defaultValueCombo.getSelectionIndex();
                    if (selectionIndex >= 0
                            && selectionIndex < m_valueSets.size() + 1) {
                        m_defaultValue = items[selectionIndex];
                    }
                }));
    }
    /**
     * sets the comboboxValues according to the {@link #m_valueSets} 
     */
    private void setComboBoxValues() {
        int size = m_valueSets.size();
        String[] values = (String[]) ArrayUtils.add(
                m_valueSets.stream().map(ValueComment::getValue)
                        .collect(Collectors.toList()).toArray(new String[size]),
                0, StringConstants.EMPTY);
        m_defaultValueCombo.setItems(values);
        int indexOf = ArrayUtils.indexOf(values, m_defaultValue);
        m_defaultValueCombo.select(indexOf == -1 ? 0 : indexOf);
    }

    /**
     * creates the table and the add/remove buttons
     * @param parent the parent control
     */
    private void createTableComposite(Composite parent) {
        final Label label = new Label(parent, SWT.NONE);
        label.setText(Messages.EditValueSetDialogTableLabel);

        final Composite tableArea = new Composite(parent, SWT.NONE);
        final GridLayout areaLayout = new GridLayout(2, false);
        tableArea.setLayout(areaLayout);
        final GridData tableAreaGridData = new GridData();
        tableAreaGridData.grabExcessVerticalSpace = true;
        tableAreaGridData.grabExcessHorizontalSpace = true;
        tableAreaGridData.horizontalAlignment = GridData.FILL;
        tableAreaGridData.verticalAlignment = GridData.FILL;
        tableArea.setLayoutData(tableAreaGridData);

        createValueSetTable(tableArea);

        createTableButtons(tableArea);

    }

    /**
     * @param parent the parent control
     */
    private void createTableButtons(Composite parent) {
        final Composite tableButtonArea = new Composite(parent, SWT.NONE);
        final GridLayout areaLayout = new GridLayout(1, true);
        tableButtonArea.setLayout(areaLayout);
        final GridData tableAreaGridData = new GridData();
        tableAreaGridData.grabExcessVerticalSpace = true;
        tableAreaGridData.horizontalAlignment = GridData.FILL;
        tableAreaGridData.verticalAlignment = GridData.FILL;
        tableButtonArea.setLayoutData(tableAreaGridData);

        final GridData buttonsGridData = new GridData();
        buttonsGridData.grabExcessHorizontalSpace = true;
        buttonsGridData.horizontalAlignment = GridData.FILL;
        createAddButton(tableButtonArea, buttonsGridData);
        createDeleteButton(tableButtonArea, buttonsGridData);
        LayoutUtil.createSeparator(tableButtonArea);

    }

    /**
     * @param parent the parent control
     * @param layoutData the LayoutData
     */
    private void createDeleteButton(Composite parent, GridData layoutData) {
        final Button deleteButton = new Button(parent, SWT.NONE);
        deleteButton.setText(Messages.EditParametersDialogRemove);
        deleteButton.setLayoutData(layoutData);
        deleteButton.addSelectionListener(
                SelectionListener.widgetSelectedAdapter(c -> {
                    IStructuredSelection selection =
                            m_tableViewer.getStructuredSelection();
                    for (@SuppressWarnings("unchecked")
                        Iterator<ValueComment> iterator =
                            selection.iterator(); iterator.hasNext();) {
                        ValueComment valueComment = iterator.next();
                        m_valueSets.remove(valueComment);
                        m_tableViewer.refresh();
                        checkOKButton();
                    }
                    String item = m_defaultValueCombo
                            .getItem(m_defaultValueCombo.getSelectionIndex());
                    if (!doesExist(item)) {
                        setComboBoxValues();
                        m_defaultValueCombo.select(0);
                        m_defaultValue = null;
                    }
                }));

    }

    /**
     * @param parent the parent control
     * @param layoutData the LayoutData
     */
    private void createAddButton(Composite parent, GridData layoutData) {
        final Button addButton = new Button(parent, SWT.NONE);
        addButton.setText(Messages.EditParametersDialogAdd);
        addButton.setLayoutData(layoutData);
        addButton.addSelectionListener(
                SelectionListener.widgetSelectedAdapter(c -> {
                    m_valueSets.add(new ValueComment(StringConstants.EMPTY,
                            StringConstants.EMPTY));
                    m_tableViewer.refresh();
                    checkOKButton();
                }));

    }

    /**
     * @param parent the parent control
     */
    private void createValueSetTable(Composite parent) {
        m_tableViewer = new TableViewer(parent,
                SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
        final GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        Table table = m_tableViewer.getTable();
        TableViewerColumn tvColumn1 =
                new TableViewerColumn(m_tableViewer, SWT.NONE);
        tvColumn1.getColumn().setText(VALUE_COLUMN_NAME);
        tvColumn1.getColumn().setWidth(200);
        TableViewerColumn tvColumn2 =
                new TableViewerColumn(m_tableViewer, SWT.NONE);
        tvColumn2.getColumn().setText(COMMENT_COLUMN_NAME);
        tvColumn2.getColumn().setWidth(200);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(gridData);
        m_tableViewer.setColumnProperties(
                new String[] { VALUE_COLUMN_NAME, COMMENT_COLUMN_NAME });
        m_tableViewer.setContentProvider(new IStructuredContentProvider() {
            public Object[] getElements(Object inputElement) {
                return ((List<?>) inputElement).toArray();
            }
        });
        setLabelProvider();
        m_tableViewer.setInput(m_valueSets);
        final TextCellEditor textCellEditor = new TextCellEditor(table);
        final ICellModifier cellModifier = createCellModifier();
        m_tableViewer.setCellModifier(cellModifier);
        m_tableViewer.setCellEditors(
                new CellEditor[] { textCellEditor, textCellEditor });
        ColumnViewerEditorActivationStrategy activationStrategy =
            new ColumnViewerEditorActivationStrategy(m_tableViewer) {
                protected boolean isEditorActivationEvent(
                            ColumnViewerEditorActivationEvent event) {
                    return event.eventType == TRAVERSAL
                                || event.eventType == MOUSE_CLICK_SELECTION
                                || (event.eventType == KEY_PRESSED
                                        && event.keyCode == SWT.CR)
                                || event.eventType == PROGRAMMATIC;
                }
            };
        TableViewerEditor.create(m_tableViewer,
                activationStrategy,
                ColumnViewerEditor.TABBING_HORIZONTAL
                        | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
                        | ColumnViewerEditor.KEYBOARD_ACTIVATION);

    }

    /**
     * @return {@link ICellModifier}
     */
    private ICellModifier createCellModifier() {
        final ICellModifier cellModifier = new ICellModifier() {

            @Override
            public void modify(Object element, String property, Object value) {
                TableItem ti = (TableItem) element;
                ValueComment vc = (ValueComment) ti.getData();
                if (property.equals(VALUE_COLUMN_NAME)) {

                    vc.setValue(value.toString());
                    m_tableViewer.refresh();
                    int selectionIndex =
                            m_defaultValueCombo.getSelectionIndex();
                    setComboBoxValues();
                    if (selectionIndex < 0
                            && selectionIndex > m_defaultValueCombo
                                    .getItems().length) {
                        m_defaultValueCombo.select(0);
                    } else {
                        String item =
                                m_defaultValueCombo.getItem(selectionIndex);
                        if (!doesExist(item)) {
                            m_defaultValueCombo.select(0);
                            m_defaultValue = m_defaultValueCombo.getItem(0);
                        } else {
                            m_defaultValueCombo.select(selectionIndex);
                            m_defaultValue = item;
                        }
                    }
                } else if (property.equals(COMMENT_COLUMN_NAME)) {

                    vc.setComment(value.toString());
                    m_tableViewer.refresh();
                }
                checkOKButton();
            }

            @Override
            public Object getValue(Object element, String property) {
                ValueComment vc = (ValueComment) element;
                if (property.equals(VALUE_COLUMN_NAME)) {
                    return vc.getValue();
                } else if (property.equals(COMMENT_COLUMN_NAME)) {
                    return vc.getComment();

                }
                return null;
            }

            @Override
            public boolean canModify(Object element, String property) {
                return true;
            }
        };
        return cellModifier;
    }

    /**
     * sets the label provider for the table viewer
     */
    private void setLabelProvider() {
        m_tableViewer.setLabelProvider(new ITableLabelProvider() {

            @Override
            public void addListener(ILabelProviderListener listener) {
                // nothing
            }

            @Override
            public void dispose() {
                // nothing
            }

            @Override
            public boolean isLabelProperty(Object element, String property) {
                return false;
            }

            @Override
            public void removeListener(ILabelProviderListener listener) {
                // nothing
            }

            @Override
            public Image getColumnImage(Object element, int columnIndex) {
                if (columnIndex == VALUE_COLUMN_INDEX) {
                    final ValueComment valuecomment = (ValueComment) element;
                    String value = valuecomment.getValue();
                    if (StringConstants.EMPTY.equals(value.trim())
                            || isDuplicate(valuecomment)
                            || !isValidBoolean(value)
                            || !isValidInteger(value)) {

                        return IconConstants.ERROR_IMAGE;
                    }
                }
                return null;
            }

            @Override
            public String getColumnText(Object element, int columnIndex) {
                if (element instanceof ValueComment) {
                    ValueComment entry = (ValueComment) element;
                    switch (columnIndex) {
                        case VALUE_COLUMN_INDEX:
                            return entry.getValue();
                        case COMMENT_COLUMN_INDEX:
                            return entry.getComment();
                        default:
                            break;
                    }
                }
                return null;
            }

        });
    }

    /**
     * enables or disables the OK button base ond the {@link #isDataValid()} method
     */
    private void checkOKButton() {
        getButton(IDialogConstants.OK_ID).setEnabled(isDataValid());
    }

    /**
     * checks if the data is valid
     * @return <code>true</code> if there is not empty entry and no double
     */
    private boolean isDataValid() {
        Set<String> values = new HashSet<>();
        for (ValueComment valueComment : m_valueSets) {
            String value = valueComment.getValue();
            if (StringUtils.isBlank(value)) {
                setErrorMessage(Messages.EditValueSetDialogErrorEmpty);
                return false;
            }
            boolean contains = values.contains(value);
            if (contains) {
                setErrorMessage(Messages.EditValueSetDialogErrorDuplicate);
                return false;
            }
            if (!isValidBoolean(value)) {
                setErrorMessage(Messages.EditValueSetDialogErrorNoBoolean);
                return false;
            }
            if (!isValidInteger(value)) {
                setErrorMessage(Messages.EditValueSetDialogErrorNoInt);
                return false;
            }
            values.add(value);
        }
        setErrorMessage(null);
        return true;
    }

    /**
     * checks if the type is Integer and if the value is a valid integer
     * @param value the value
     * @return <code>true</code> if the {@link #m_parameterType} is integer and the value is parsable
     */
    private boolean isValidInteger(String value) {
        if (TestDataConstants.INTEGER.equals(m_parameterType)) {
            try {
                Integer.parseInt(value);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    /**
     * checks if the type is Integer and if the value is a valid integer
     * 
     * @param value the value
     * @return <code>true</code> if the {@link #m_parameterType} is boolean and
     *         the value is <code>true</code> or <code>false</code> ignoring the
     *         case
     */
    private boolean isValidBoolean(String value) {
        if (TestDataConstants.BOOLEAN.equals(m_parameterType)) {
            boolean valid = StringUtils
                    .equalsIgnoreCase(Boolean.TRUE.toString(), value)
                    || StringUtils.equalsIgnoreCase(
                            Boolean.FALSE.toString(), value);
            if (!valid) {
                return false;
            }
        }
        return true;
    }
    /**
     * 
     * @param vc the {@link ValueComment} to check
     * @return <code>true</code> if the {@link ValueComment#getValue()} is already existing
     */
    private boolean isDuplicate(ValueComment vc) {
        for (ValueComment valueComment : m_valueSets) {
            if ((valueComment != null) && (vc != null)
                    && !vc.equals(valueComment) && StringUtils
                            .equals(vc.getValue(), valueComment.getValue())) {
                return true;
            }
        }
        return false;
    }
    /**
     * @param value the value to check
     * @return <code>true</code> if the values already exists in the {@link #m_valueSets}
     */
    private boolean doesExist(String value) {
        for (ValueComment valueComment : m_valueSets) {
            if (StringUtils.equals(value, valueComment.getValue())) {
                return true;
            }
        }
        return false;
    }
}
