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
package org.eclipse.jubula.client.ui.rcp.wizards.pages;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.ExternalTestDataBP;
import org.eclipse.jubula.client.core.businessprocess.ParameterInterfaceBP;
import org.eclipse.jubula.client.core.businessprocess.importfilter.DataTable;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ITestDataCategoryPO;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.databinding.validators.TestDataManagerNameValidator;
import org.eclipse.jubula.client.ui.rcp.dialogs.AbstractEditParametersDialog.Parameter;
import org.eclipse.jubula.client.ui.rcp.editors.CentralTestDataEditor;
import org.eclipse.jubula.client.ui.rcp.handlers.AbstractEditParametersHandler;
import org.eclipse.jubula.client.ui.rcp.handlers.AddNewTestDataManagerHandler;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.TestDataConstants;
import org.eclipse.jubula.tools.internal.exception.DuplicateColumnNameException;
import org.eclipse.jubula.tools.internal.exception.IncompleteDataException;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FileSystemElement;
import org.eclipse.ui.dialogs.WizardResourceImportPage;
import org.eclipse.ui.ide.dialogs.IElementFilter;
import org.eclipse.ui.internal.ide.filesystem.FileSystemStructureProvider;
import org.eclipse.ui.internal.progress.ProgressMonitorJobsDialog;
import org.eclipse.ui.internal.wizards.datatransfer.DataTransferMessages;
import org.eclipse.ui.internal.wizards.datatransfer.IDataTransferHelpContextIds;
import org.eclipse.ui.internal.wizards.datatransfer.MinimizedFileSystemElement;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.wizards.datatransfer.IImportStructureProvider;


/**
 * @author BREDEX GmbH
 * @created Oct 19, 2010
 */
@SuppressWarnings("restriction")
public class ImportXLSTestdataWizardPage extends WizardResourceImportPage {
    /**
     * @author BREDEX GmbH
     * @created Oct 20, 2010
     */
    private class XLSImportOperation implements IRunnableWithProgress {
        /**
         * <code>m_fileSystemObjects</code>
         */
        private final List m_fileSystemObjects;
        /**
         * <code>m_selection</code>
         */
        private final IStructuredSelection m_selection;
        
        /**
         * <code>m_ctde</code>
         */
        private CentralTestDataEditor m_ctde;

        /** All parameter names used */
        private Set<String> m_parNames;

        /**
         * @param fileSystemObjects fileSystemObjects
         * @param selection the current selection
         * @param ctde the central test data editor
         */
        public XLSImportOperation(List fileSystemObjects, 
            IStructuredSelection selection, CentralTestDataEditor ctde) {
            m_fileSystemObjects = fileSystemObjects;
            m_selection = selection;
            m_ctde = ctde;
            m_parNames = new HashSet<>();
        }

        /**
         * {@inheritDoc}
         */
        public void run(IProgressMonitor monitor) {
            int numerOfFilesToImport = m_fileSystemObjects.size();
            String operationDescription = 
                Messages.ImportXLSTestDataWizardImportOperationName;
            monitor.beginTask(operationDescription, numerOfFilesToImport);
            Plugin.getDefault().writeLine(operationDescription);
            boolean merge = m_selection != null && m_selection.size() == 1
                    && numerOfFilesToImport == 1;
            for (Object o : m_fileSystemObjects) {
                if (o instanceof File) {
                    File f = (File)o;
                    importOneFile(monitor, merge, f);
                }
            }
            Plugin.getDefault().writeLine(
                    Messages.ImportXLSTestDataWizardImportOperationFinished 
                    + StringConstants.NEWLINE);
            monitor.done();
        }

        /**
         * Imports a single file
         * @param monitor the progress monitor
         * @param merge whether we import into an existing DS
         * @param f the file
         */
        private void importOneFile(IProgressMonitor monitor,
                boolean merge, File f) {
            Plugin p = Plugin.getDefault();
            String absoluteFilePath = f.getAbsolutePath();
            try {
                EditSupport es = m_ctde.getEditorHelper()
                        .getEditSupport();
                ITestDataCategoryPO cont = 
                        (ITestDataCategoryPO)es.getWorkVersion();
                Set<String> usedNames = AddNewTestDataManagerHandler
                        .getSetOfUsedNames(m_ctde);
                if (merge) {
                    Object selectedObject = m_selection
                            .getFirstElement();
                    if (selectedObject instanceof ITestDataCubePO) {
                        ITestDataCubePO tdc = 
                            (ITestDataCubePO)selectedObject;
                        tdc.getDataManager().clear();
                        fillCentralTestDataSet(f, tdc);
                        fireDataChangedEvent(tdc, 
                                DataState.StructureModified,
                                UpdateState.onlyInEditor);
                        p.writeLine(NLS.bind(Messages
                                .ImportXLSTestDataSuccessfullMerge,
                                new Object[] { absoluteFilePath, 
                                    tdc.getName() }));
                    }
                } else {
                    // ensure name uniqueness
                    String name = f.getName().trim();
                    TestDataManagerNameValidator nameValidator = 
                        new TestDataManagerNameValidator(
                            name, usedNames);
                    int counter = 1;
                    while (!nameValidator.validate(name).isOK()) {
                        name += "_0"; //$NON-NLS-1$
                        name = name.replaceAll(
                                "(_[0-9]+)+$", "_" + counter++); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    ITestDataCubePO testdata = PoMaker
                            .createTestDataCubePO(name);
                    fillCentralTestDataSet(f, testdata);
                    cont.addTestData(testdata);
                    p.writeLine(NLS.bind(Messages
                            .ImportXLSTestDataWizardSuccessfullImport,
                            new Object[] { absoluteFilePath }));
                    fireDataChangedEvent(testdata, DataState.Added,
                            UpdateState.onlyInEditor);
                }
            } catch (DuplicateColumnNameException e) {
                p.writeErrorLine(NLS.bind(
                        Messages.ImportXLSTDWizDupColName,
                        new Object[] {f.getName()}));
            } catch (IncompleteDataException e) {
                p.writeErrorLine(absoluteFilePath 
                        + StringConstants.NEWLINE
                        + e.getLocalizedMessage());
            } catch (JBException e) {
                p.writeErrorLine(e.getLocalizedMessage());
            } finally {
                monitor.worked(1);
            }
        }

        /**
         * @param f
         *            the file to get the data from
         * @param testdata
         *            the test data cube to use
         * @throws JBException
         *             in case of data retrieval problems
         */
        private void fillCentralTestDataSet(File f,
                ITestDataCubePO testdata) throws JBException,
                DuplicateColumnNameException {
            ExternalTestDataBP bp = new ExternalTestDataBP();
            String absoluteFilePath = f.getAbsoluteFile()
                    .getAbsolutePath();
            
            DataTable dtParamInterface = bp.createDataTable(
                    null, absoluteFilePath);

            // get all parameters from first data table row
            List<Parameter> listOfParameters = new ArrayList<Parameter>();
            for (int i = 0; i < dtParamInterface.getColumnCount(); i++) {
                String colName = dtParamInterface.getData(0, i);
                if (m_parNames.contains(colName)) {
                    throw new DuplicateColumnNameException();
                }
                m_parNames.add(colName);
                // there is currently no way other than guessing the parameter type
                listOfParameters.add(
                        new Parameter(colName, TestDataConstants.STR));
            }

            // create interface for central test data set
            AbstractEditParametersHandler.editParameters(testdata,
                    listOfParameters, m_ctde.getEditorHelper()
                    .getEditSupport().getParamMapper(),
                    new ParameterInterfaceBP());
            // create new data for import
            // disable caching of external test data
            bp.clearExternalData();
            DataTable dt = bp.createDataTable(
                    null, absoluteFilePath);
            bp.parseTable(dt, testdata, true);
        }

        /**
         * @param po
         *            the po
         * @param datastate
         *            the data state
         * @param updatestate
         *            the update state
         */
        private void fireDataChangedEvent(final IPersistentObject po,
                final DataState datastate, final UpdateState updatestate) {
            Plugin.getDisplay().syncExec(new Runnable() {
                public void run() {
                    DataEventDispatcher.getInstance().fireDataChangedListener(
                            po, datastate, updatestate);
                }
            });
        }
    }
    
    /**
     * <code>SOURCE_EMPTY_MESSAGE</code>
     */
    protected static final String SOURCE_EMPTY_MESSAGE = 
        DataTransferMessages.FileImport_sourceEmpty;
    
    // dialog store id constants
    /**
     * <code>STORE_SOURCE_NAMES_ID</code>
     */
    private static final String STORE_SOURCE_NAMES_ID = "WizardFileSystemResourceImportPage1.STORE_SOURCE_NAMES_ID"; //$NON-NLS-1$

    /**
     * <code>SELECT_TYPES_TITLE</code>
     */
    private static final String SELECT_TYPES_TITLE = 
        DataTransferMessages.DataTransfer_selectTypes;

    /**
     * <code>SELECT_ALL_TITLE</code>
     */
    private static final String SELECT_ALL_TITLE = 
        DataTransferMessages.DataTransfer_selectAll;

    /**
     * <code>DESELECT_ALL_TITLE</code>
     */
    private static final String DESELECT_ALL_TITLE = 
        DataTransferMessages.DataTransfer_deselectAll;

    /**
     * <code>SELECT_SOURCE_TITLE</code>
     */
    private static final String SELECT_SOURCE_TITLE = 
        DataTransferMessages.FileImport_selectSourceTitle;

    /**
     * <code>SELECT_SOURCE_MESSAGE</code>
     */
    private static final String SELECT_SOURCE_MESSAGE = 
        DataTransferMessages.FileImport_selectSource;

    /**
     * <code>m_sourceNameField</code>
     */
    private Combo m_sourceNameField;

    /**
     * <code>m_sourceBrowseButton</code>
     */
    private Button m_sourceBrowseButton;

    /**
     * <code>m_selectTypesButton</code>
     */
    private Button m_selectTypesButton;

    /**
     * <code>selectAllButton</code>
     */
    private Button m_selectAllButton;

    /**
     * <code>deselectAllButton</code>
     */
    private Button m_deselectAllButton;

    /**
     * A boolean to indicate if the user has typed anything
     * <code>entryChanged</code>
     */
    private boolean m_entryChanged = false;

    /**
     * <code>fileSystemStructureProvider</code>
     */
    private FileSystemStructureProvider m_fileSystemStructureProvider = 
        new FileSystemStructureProvider();

    /**
     * @param name
     *            the name
     */
    public ImportXLSTestdataWizardPage(String name) {
        super(name, StructuredSelection.EMPTY);
        setTitle(Messages.ImportXLSTestDataWizardTitle);
        setDescription(DataTransferMessages.FileImport_importFileSystem);
    }

    /**
     * Creates a new button with the given id.
     * <p>
     * The <code>Dialog</code> implementation of this framework method creates a
     * standard push button, registers for selection events including button
     * presses and registers default buttons with its shell. The button id is
     * stored as the buttons client data. Note that the parent's layout is
     * assumed to be a GridLayout and the number of columns in this layout is
     * incremented. Subclasses may override.
     * </p>
     * 
     * @param parent
     *            the parent composite
     * @param id
     *            the id of the button (see <code>IDialogConstants.*_ID</code>
     *            constants for standard dialog button ids)
     * @param label
     *            the label from the button
     * @param defaultButton
     *            <code>true</code> if the button is to be the default button,
     *            and <code>false</code> otherwise
     * @return the button
     */
    protected Button createButton(Composite parent, int id, String label,
            boolean defaultButton) {
        // increment the number of columns in the button bar
        ((GridLayout)parent.getLayout()).numColumns++;

        Button button = new Button(parent, SWT.PUSH);
        button.setFont(parent.getFont());

        GridData buttonData = new GridData(GridData.FILL_HORIZONTAL);
        button.setLayoutData(buttonData);

        button.setData(new Integer(id));
        button.setText(label);

        if (defaultButton) {
            Shell shell = parent.getShell();
            if (shell != null) {
                shell.setDefaultButton(button);
            }
            button.setFocus();
        }
        return button;
    }

    /**
     * Creates the buttons for selecting specific types or selecting all or none
     * of the elements.
     * 
     * @param parent
     *            the parent control
     */
    protected final void createButtonsGroup(Composite parent) {
        // top level group
        Composite buttonComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        layout.makeColumnsEqualWidth = true;
        buttonComposite.setLayout(layout);
        buttonComposite.setFont(parent.getFont());
        GridData buttonData = new GridData(SWT.FILL, SWT.FILL, true, false);
        buttonComposite.setLayoutData(buttonData);

        // types edit button
        m_selectTypesButton = createButton(buttonComposite,
                IDialogConstants.SELECT_TYPES_ID, SELECT_TYPES_TITLE, false);

        SelectionListener listener = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                handleTypesEditButtonPressed();
            }
        };
        m_selectTypesButton.addSelectionListener(listener);
        setButtonLayoutData(m_selectTypesButton);

        m_selectAllButton = createButton(buttonComposite,
                IDialogConstants.SELECT_ALL_ID, SELECT_ALL_TITLE, false);

        listener = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                setAllSelections(true);
                updateWidgetEnablements();
            }
        };
        m_selectAllButton.addSelectionListener(listener);
        setButtonLayoutData(m_selectAllButton);

        m_deselectAllButton = createButton(buttonComposite,
                IDialogConstants.DESELECT_ALL_ID, DESELECT_ALL_TITLE, false);

        listener = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                setAllSelections(false);
                updateWidgetEnablements();
            }
        };
        m_deselectAllButton.addSelectionListener(listener);
        setButtonLayoutData(m_deselectAllButton);

    }

    /**
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);

        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL));
        composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        composite.setFont(parent.getFont());

        createSourceGroup(composite);

        restoreWidgetValues();
        updateWidgetEnablements();
        setPageComplete(determinePageCompletion());
        setErrorMessage(null);  // should not initially have error message

        setControl(composite);
        
        validateSourceGroup();
        PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),
                IDataTransferHelpContextIds.FILE_SYSTEM_IMPORT_WIZARD_PAGE);
    }

    /**
     * Create the group for creating the root directory
     * @param parent the parent
     */
    protected void createRootDirectoryGroup(Composite parent) {
        Composite sourceContainerGroup = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        sourceContainerGroup.setLayout(layout);
        sourceContainerGroup.setFont(parent.getFont());
        sourceContainerGroup.setLayoutData(new GridData(
                GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        Label groupLabel = new Label(sourceContainerGroup, SWT.NONE);
        groupLabel.setText(getSourceLabel());
        groupLabel.setFont(parent.getFont());

        // source name entry field
        m_sourceNameField = new Combo(sourceContainerGroup, SWT.BORDER);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
                | GridData.GRAB_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        m_sourceNameField.setLayoutData(data);
        m_sourceNameField.setFont(parent.getFont());
        m_sourceNameField.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                updateFromSourceField();
            }
        });

        m_sourceNameField.addKeyListener(new KeyListener() {
            /* @see KeyListener.keyPressed */
            public void keyPressed(KeyEvent e) {
                // If there has been a key pressed then mark as dirty
                m_entryChanged = true;
                if (e.character == SWT.CR) {
                    m_entryChanged = false;
                    updateFromSourceField();
                }
            }

            /* @see KeyListener.keyReleased */
            public void keyReleased(KeyEvent e) {
                // currently empty 
            }
        });

        m_sourceNameField.addFocusListener(new FocusListener() {
            /* @see FocusListener.focusGained(FocusEvent) */
            public void focusGained(FocusEvent e) {
                // Do nothing when getting focus
            }

            /* @see FocusListener.focusLost(FocusEvent) */
            public void focusLost(FocusEvent e) {
                // Clear the flag to prevent constant update
                if (m_entryChanged) {
                    m_entryChanged = false;
                    updateFromSourceField();
                }

            }
        });

        // source browse button
        m_sourceBrowseButton = new Button(sourceContainerGroup, SWT.PUSH);
        m_sourceBrowseButton.setText(DataTransferMessages.DataTransfer_browse);
        m_sourceBrowseButton.addListener(SWT.Selection, this);
        m_sourceBrowseButton.setLayoutData(new GridData(
                GridData.HORIZONTAL_ALIGN_FILL));
        m_sourceBrowseButton.setFont(parent.getFont());
        setButtonLayoutData(m_sourceBrowseButton);
    }

    /**
     * Update the receiver from the source name field.
     */

    private void updateFromSourceField() {

        setSourceName(m_sourceNameField.getText());
        // Update enablements when this is selected
        updateWidgetEnablements();
        m_fileSystemStructureProvider.clearVisitedDirs();
    }

    /**
     * Creates and returns a <code>FileSystemElement</code> if the specified
     * file system object merits one. The criteria for this are: Also create the
     * children.
     * @param fileSystemObject fileSystemObject
     * @param provider provider
     * @return the MinimizedFileSystemElement
     */
    protected MinimizedFileSystemElement createRootElement(
            Object fileSystemObject, IImportStructureProvider provider) {
        boolean isContainer = provider.isFolder(fileSystemObject);
        String elementLabel = provider.getLabel(fileSystemObject);

        // Use an empty label so that display of the element's full name
        // doesn't include a confusing label
        MinimizedFileSystemElement dummyParent = new MinimizedFileSystemElement(
                StringConstants.EMPTY, null, true);
        dummyParent.setPopulated();
        MinimizedFileSystemElement result = new MinimizedFileSystemElement(
                elementLabel, dummyParent, isContainer);
        result.setFileSystemObject(fileSystemObject);

        // Get the files for the element so as to build the first level
        result.getFiles(provider);

        return dummyParent;
    }

    /**
     * {@inheritDoc}
     */
    protected void createSourceGroup(Composite parent) {
        createRootDirectoryGroup(parent);
        createFileSelectionGroup(parent);
        createButtonsGroup(parent);
    }
    
    /**
     * {@inheritDoc}
     */
    protected void enableButtonGroup(boolean enable) {
        m_selectTypesButton.setEnabled(enable);
        m_selectAllButton.setEnabled(enable);
        m_deselectAllButton.setEnabled(enable);
    }

    /**
     * Answer a boolean indicating whether the specified source currently exists
     * and is valid
     * @return whether source is valid or not
     */
    protected boolean ensureSourceIsValid() {
        if (new File(getSourceDirectoryName()).isDirectory()) {
            return true;
        }

        setErrorMessage(DataTransferMessages.FileImport_invalidSource);
        return false;
    }

    /**
     * Execute the passed import operation. Answer a boolean indicating success.
     * @param op the operation 
     * @return execute
     */
    protected boolean executeImportOperation(IRunnableWithProgress op) {
        try {
            getContainer().run(true, true, op);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            displayErrorDialog(e.getTargetException());
            return false;
        }
        return true;
    }

    /**
     * The Finish button was pressed. Try do to the required work now and answer
     * a boolean indicating success. If false is returned then the wizard will
     * not close.
     * 
     * @param selection the current selection
     * @param ctde the central test data editor
     * @return boolean
     */
    public boolean finish(IStructuredSelection selection, 
            CentralTestDataEditor ctde) {
        if (!ensureSourceIsValid()) {
            return false;
        }

        saveWidgetValues();

        Iterator resourcesEnum = getSelectedResources().iterator();
        List<Object> fileSystemObjects = new ArrayList<Object>();
        while (resourcesEnum.hasNext()) {
            fileSystemObjects.add(((FileSystemElement)resourcesEnum.next())
                    .getFileSystemObject());
        }

        if (fileSystemObjects.size() > 0) {
            return importResources(fileSystemObjects, selection, ctde);
        }

        MessageDialog.openInformation(getContainer().getShell(),
                DataTransferMessages.DataTransfer_information,
                DataTransferMessages.FileImport_noneSelected);

        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    protected ITreeContentProvider getFileProvider() {
        return new WorkbenchContentProvider() {
            public Object[] getChildren(Object o) {
                if (o instanceof MinimizedFileSystemElement) {
                    MinimizedFileSystemElement element = 
                        (MinimizedFileSystemElement)o;
                    return element.getFiles(m_fileSystemStructureProvider)
                            .getChildren(element);
                }
                return new Object[0];
            }
        };
    }

    /**
     * Answer the root FileSystemElement that represents the contents of the
     * currently-specified source. If this FileSystemElement is not currently
     * defined then create and return it.
     * @return MinimizedFileSystemElement
     */
    protected MinimizedFileSystemElement getFileSystemTree() {
        File sourceDirectory = getSourceDirectory();
        if (sourceDirectory == null) {
            return null;
        }

        return selectFiles(sourceDirectory, m_fileSystemStructureProvider);
    }

    /**
     * {@inheritDoc}
     */
    protected ITreeContentProvider getFolderProvider() {
        return new WorkbenchContentProvider() {
            public Object[] getChildren(Object o) {
                if (o instanceof MinimizedFileSystemElement) {
                    MinimizedFileSystemElement element = 
                        (MinimizedFileSystemElement)o;
                    return element.getFolders(m_fileSystemStructureProvider)
                            .getChildren(element);
                }
                return new Object[0];
            }

            public boolean hasChildren(Object o) {
                return o instanceof MinimizedFileSystemElement;
            }
        };
    }

    /**
     * Returns a File object representing the currently-named source directory
     * if it exists as a valid directory, or <code>null</code> otherwise.
     * 
     * @return the
     *            file
     */
    protected File getSourceDirectory() {
        return getSourceDirectory(this.m_sourceNameField.getText());
    }

    /**
     * Returns a File object representing the currently-named source directory
     * iff it exists as a valid directory, or <code>null</code> otherwise.
     * 
     * @param path
     *            a String not yet formatted for java.io.File compatability
     * 
     * @return the
     *            file
     *            
     */
    private File getSourceDirectory(String path) {
        File sourceDirectory = new File(getSourceDirectoryName(path));
        if (!sourceDirectory.exists() || !sourceDirectory.isDirectory()) {
            return null;
        }

        return sourceDirectory;
    }

    /**
     * Answer the directory name specified as being the import source. Note that
     * if it ends with a separator then the separator is first removed so that
     * java treats it as a proper directory
     * 
     * @return the
     *            file
     */
    private String getSourceDirectoryName() {
        return getSourceDirectoryName(this.m_sourceNameField.getText());
    }

    /**
     * Answer the directory name specified as being the import source. Note that
     * if it ends with a separator then the separator is first removed so that
     * java treats it as a proper directory
     * @param sourceName the source name
     * @return the
     *            string
     */
    private String getSourceDirectoryName(String sourceName) {
        IPath result = new Path(sourceName.trim());

        if (result.getDevice() != null && result.segmentCount() == 0) {
            result = result.addTrailingSeparator();
        } else {
            result = result.removeTrailingSeparator();
        }

        return result.toOSString();
    }

    /**
     * Answer the string to display as the label for the source specification
     * field
     * 
     * @return the string
     */
    protected String getSourceLabel() {
        return DataTransferMessages.FileImport_fromDirectory;
    }

    /**
     * Handle all events and enablements for widgets in this dialog
     * 
     * @param event
     *            Event
     */
    public void handleEvent(Event event) {
        if (event.widget == m_sourceBrowseButton) {
            handleSourceBrowseButtonPressed();
        }
        super.handleEvent(event);
    }

    /**
     * Open an appropriate source browser so that the user can specify a source
     * to import from
     */
    protected void handleSourceBrowseButtonPressed() {

        String currentSource = this.m_sourceNameField.getText();
        DirectoryDialog dialog = new DirectoryDialog(
                m_sourceNameField.getShell(), SWT.SAVE | SWT.SHEET);
        dialog.setText(SELECT_SOURCE_TITLE);
        dialog.setMessage(SELECT_SOURCE_MESSAGE);
        dialog.setFilterPath(getSourceDirectoryName(currentSource));

        String selectedDirectory = dialog.open();
        if (selectedDirectory != null) {
            // Just quit if the directory is not valid
            if ((getSourceDirectory(selectedDirectory) == null)
                    || selectedDirectory.equals(currentSource)) {
                return;
            }
            // If it is valid then proceed to populate
            setErrorMessage(null);
            setSourceName(selectedDirectory);
            selectionGroup.setFocus();
        }
    }

    /**
     * Import the resources with extensions as specified by the user
     * @param fileSystemObjects the file system objects
     * @param selection the current selection
     * @param ctde the central test data editor
     * @return executeImportOperation(operation)
     */
    protected boolean importResources(List fileSystemObjects, 
        IStructuredSelection selection, CentralTestDataEditor ctde) {
        IRunnableWithProgress operation = 
            new XLSImportOperation(fileSystemObjects, selection, ctde);
        return executeImportOperation(operation);
    }

    /**
     * Returns whether the extension provided is an extension that has been
     * specified for export by the user.
     * 
     * @param extension
     *            the resource name
     * @return <code>true</code> if the resource name is suitable for export
     *         based upon its extension
     */
    protected boolean isExportableExtension(String extension) {
        if (selectedTypes == null) {
            return true;
        }

        Iterator itr = selectedTypes.iterator();
        while (itr.hasNext()) {
            if (extension.equalsIgnoreCase((String)itr.next())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Repopulate the view based on the currently entered directory.
     */
    protected void resetSelection() {
        MinimizedFileSystemElement currentRoot = getFileSystemTree();
        this.selectionGroup.setRoot(currentRoot);
    }

    /**
     * Use the dialog store to restore widget values to the values that they
     * held last time this wizard was used to completion
     */
    protected void restoreWidgetValues() {
        IDialogSettings settings = getDialogSettings();
        if (settings != null) {
            String[] sourceNames = settings.getArray(STORE_SOURCE_NAMES_ID);
            if (sourceNames == null) {
                return; // ie.- no values stored, so stop
            }

            // set filenames history
            for (int i = 0; i < sourceNames.length; i++) {
                m_sourceNameField.add(sourceNames[i]);
            }
            updateWidgetEnablements();
        }
    }

    /**
     * Since Finish was pressed, write widget values to the dialog store so that
     * they will persist into the next invocation of this wizard page
     */
    protected void saveWidgetValues() {
        IDialogSettings settings = getDialogSettings();
        if (settings != null) {
            // update source names history
            String[] sourceNames = settings.getArray(STORE_SOURCE_NAMES_ID);
            if (sourceNames == null) {
                sourceNames = new String[0];
            }

            sourceNames = addToHistory(sourceNames, getSourceDirectoryName());
            settings.put(STORE_SOURCE_NAMES_ID, sourceNames);
        }
    }

    /**
     * Invokes a file selection operation using the specified file system and
     * structure provider. If the user specifies files to be imported then this
     * selection is cached for later retrieval and is returned.
     * @param rootFileSystemObject root
     * @param structureProvider sp
     * @return MinimizedFileSystemElement
     */
    protected MinimizedFileSystemElement selectFiles(
            final Object rootFileSystemObject,
            final IImportStructureProvider structureProvider) {
        final MinimizedFileSystemElement[] results = 
            new MinimizedFileSystemElement[1];
        BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
            public void run() {
                // Create the root element from the supplied file system object
                results[0] = createRootElement(rootFileSystemObject,
                        structureProvider);
            }
        });

        return results[0];
    }

    /**
     * Set all of the selections in the selection group to value. Implemented
     * here to provide access for inner classes.
     * 
     * @param value
     *            boolean
     */
    protected void setAllSelections(boolean value) {
        super.setAllSelections(value);
    }

    /**
     * Sets the source name of the import to be the supplied path. Adds the name
     * of the path to the list of items in the source combo and selects it.
     * 
     * @param path
     *            the path to be added
     */
    protected void setSourceName(String path) {

        if (path.length() > 0) {

            String[] currentItems = this.m_sourceNameField.getItems();
            int selectionIndex = -1;
            for (int i = 0; i < currentItems.length; i++) {
                if (currentItems[i].equals(path)) {
                    selectionIndex = i;
                }
            }
            if (selectionIndex < 0) {
                int oldLength = currentItems.length;
                String[] newItems = new String[oldLength + 1];
                System.arraycopy(currentItems, 0, newItems, 0, oldLength);
                newItems[oldLength] = path;
                this.m_sourceNameField.setItems(newItems);
                selectionIndex = oldLength;
            }
            this.m_sourceNameField.select(selectionIndex);

            resetSelection();
        }
    }

    /**
     * Update the tree to only select those elements that match the selected
     * types
     */
    protected void setupSelectionsBasedOnSelectedTypes() {
        ProgressMonitorDialog dialog = new ProgressMonitorJobsDialog(
                getContainer().getShell());
        final Map<FileSystemElement, List<FileSystemElement>> selectionMap = 
                new Hashtable<FileSystemElement, List<FileSystemElement>>();
        final IElementFilter filter = new IElementFilter() {
            public void filterElements(Collection files,
                    IProgressMonitor monitor) throws InterruptedException {
                if (files == null) {
                    throw new InterruptedException();
                }
                Iterator filesList = files.iterator();
                while (filesList.hasNext()) {
                    if (monitor.isCanceled()) {
                        throw new InterruptedException();
                    }
                    checkFile(filesList.next());
                }
            }
            public void filterElements(Object[] files, IProgressMonitor monitor)
                throws InterruptedException {
                if (files == null) {
                    throw new InterruptedException();
                }
                for (int i = 0; i < files.length; i++) {
                    if (monitor.isCanceled()) {
                        throw new InterruptedException();
                    }
                    checkFile(files[i]);
                }
            }
            private void checkFile(Object fileElement) {
                MinimizedFileSystemElement file = 
                    (MinimizedFileSystemElement)fileElement;
                if (isExportableExtension(file.getFileNameExtension())) {
                    List<FileSystemElement> elements = 
                            new ArrayList<FileSystemElement>();
                    FileSystemElement parent = file.getParent();
                    if (selectionMap.containsKey(parent)) {
                        elements = selectionMap.get(parent);
                    }
                    elements.add(file);
                    selectionMap.put(parent, elements);
                }
            }
        };
        IRunnableWithProgress runnable = new IRunnableWithProgress() {
            public void run(final IProgressMonitor monitor)
                throws InterruptedException {
                monitor.beginTask(
                        DataTransferMessages.ImportPage_filterSelections,
                        IProgressMonitor.UNKNOWN);
                getSelectedResources(filter, monitor);
            }
        };
        try {
            dialog.run(true, true, runnable);
        } catch (InvocationTargetException exception) {
            return;
        } catch (InterruptedException exception) {
            return;
        }
        // make sure that all paint operations caused by closing the progress
        // dialog get flushed, otherwise extra pixels will remain on the screen
        // until updateSelections is completed
        getShell().update();
        // The updateSelections method accesses SWT widgets so cannot be executed
        // as part of the above progress dialog operation since the operation
        // forks a new process.
        if (selectionMap != null) {
            updateSelections(selectionMap);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        resetSelection();
        if (visible) {
            this.m_sourceNameField.setFocus();
        }
    }

    /**
     * Update the selections with those in map . Implemented here to give inner
     * class visibility
     * 
     * @param map
     *            Map - key tree elements, values Lists of list elements
     */
    protected void updateSelections(Map map) {
        super.updateSelections(map);
    }

    /**
     * Answer a boolean indicating whether self's source specification widgets
     * currently all contain valid values.
     * @return valid flag
     */
    protected boolean validateSourceGroup() {
        File sourceDirectory = getSourceDirectory();
        if (sourceDirectory == null) {
            setMessage(SOURCE_EMPTY_MESSAGE);
            enableButtonGroup(false);
            return false;
        }

        if (sourceConflictsWithDestination(
                new Path(sourceDirectory.getPath()))) {
            setMessage(null);
            setErrorMessage(getSourceConflictMessage());
            enableButtonGroup(false);
            return false;
        }

        List resourcesToExport = selectionGroup.getAllWhiteCheckedItems();
        if (resourcesToExport.size() == 0) {
            setMessage(null);
            setErrorMessage(DataTransferMessages.FileImport_noneSelected);
            return false;
        }

        enableButtonGroup(true);
        setErrorMessage(null);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected boolean determinePageCompletion() {
        boolean complete = validateSourceGroup() && validateOptionsGroup();
        // Avoid draw flicker by not clearing the error
        // message unless all is valid.
        if (complete) {
            setErrorMessage(null);
        }

        return complete;
    }
    
    /**
     * {@inheritDoc}
     */
    protected boolean sourceConflictsWithDestination(IPath sourcePath) {
        // Destination is DB
        return false;
    }
}
