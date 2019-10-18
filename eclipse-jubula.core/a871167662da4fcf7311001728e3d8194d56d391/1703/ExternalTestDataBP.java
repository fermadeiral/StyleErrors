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
package org.eclipse.jubula.client.core.businessprocess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jubula.client.core.businessprocess.importfilter.DataTable;
import org.eclipse.jubula.client.core.businessprocess.importfilter.ExcelImportFilter;
import org.eclipse.jubula.client.core.businessprocess.importfilter.IDataImportFilter;
import org.eclipse.jubula.client.core.businessprocess.importfilter.exceptions.DataReadException;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.ITDManager;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.IncompleteDataException;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Business class for handling with external data sources (e.g. Excel files)
 *
 * @author BREDEX GmbH
 * @created Nov 3, 2005
 */
public class ExternalTestDataBP {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(ExternalTestDataBP.class);

    /** this is where the datafile are stored */
    private static File globalDataDir = new File(StringConstants.DOT);

    /** import fiter*/
    private List <IDataImportFilter> m_filter;
     
    /** 
     * Cache for the read in data file
     * Key = file name
     * Value = DataTable 
     */
    private Map<File, DataTable> m_dataTableCache = 
        new HashMap<File, DataTable>();
    
    /**
     * Cache for ITDmanagerPOs
     * Key = IParamNodePO
     * Value = ITDManager
     */
    private Map<IParamNodePO, ITDManager> m_tdManagerCache = 
        new HashMap<IParamNodePO, ITDManager>();

    /**
     * Constructor
     *
     */
    public ExternalTestDataBP() {
        m_filter = new ArrayList <IDataImportFilter> ();
        m_filter.add(new ExcelImportFilter());
    }

    /**
     * Creates a new ITDManager for the given IParamNodePO filled with 
     * the data of the given file or gets the ITDManager from the cache.
     * @param dataDir
     *      directory for data files
     * @param file data source File
     * @param node ParamNode
     * @throws JBException error occurred while reading data source
     * @return filled TestDataManager
     */
    private ITDManager createFilledTDManager(File dataDir, String file, 
        IParamNodePO node) 
        throws JBException {
        
        ITDManager tdManager = m_tdManagerCache.get(node);
        if (tdManager != null) {
            return tdManager;
        }
        
        tdManager = PoMaker.createTDManagerPO(node);
        // clear TDManager first
        node.clearTestData();
        // fill it again
        DataTable dataTable = createDataTable(dataDir, file);
        tdManager = parseTable(dataTable, node);
        m_tdManagerCache.put(node, tdManager);
        return tdManager;
    }

    /**
     * Creates and returns a Data Manager populated with test data corresponding
     * to the given arguments.
     * 
     * @param referencedDataCube
     *          The cube from which test data will be retrieved.
     * @param node
     *          The node containing the parameters that will use the generated
     *          data.
     * @return the created Test Data Manager.
     */
    private ITDManager createFilledTDManager(
            IParameterInterfacePO referencedDataCube, IParamNodePO node) {
            
        ITDManager tdManager = m_tdManagerCache.get(node);
        if (tdManager != null) {
            return tdManager;
        }

        tdManager = referencedDataCube.getDataManager();
        
        m_tdManagerCache.put(node, tdManager);
        return tdManager;
    }

    /**
     * Creates a DataTable of the given file name with the given Locale or 
     * gets it from the cache if available.
     * @param dataDir
     *      directory for data files
     * @param filePath the path of the data source
     * @return a DataTable
     * @throws JBException id data source is not supported
     */
    public DataTable createDataTable(File dataDir, String filePath)
            throws JBException {
        File dataFile = new File(filePath);
        if (!dataFile.isAbsolute()) {
            dataFile = new File(dataDir, filePath);
        }
        DataTable dataTable = m_dataTableCache.get(dataFile);
        if (dataTable != null) {
            return dataTable;
        }
        String dataFileName = String.valueOf(dataFile);
        try {
            IDataImportFilter filter = getFilterFromFileType(filePath);
            if (filter != null) {
                dataTable = filter.parse(dataDir, filePath);
                m_dataTableCache.put(dataFile, dataTable);
                return dataTable;
            } 
            LOG.error(Messages.DataSource + StringConstants.COLON 
                    + StringConstants.SPACE + StringConstants.APOSTROPHE
                    + dataFileName + StringConstants.APOSTROPHE 
                    + StringConstants.SPACE + Messages.NotSupported);
            throw new JBException(
                    NLS.bind(Messages.ErrorMessageNOT_SUPP_DATASOURCE,
                    dataFileName),
                MessageIDs.E_NOT_SUPP_DATASOURCE);
        } catch (IOException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(Messages.ErrorReadingFile + StringConstants.COLON
                        + StringConstants.SPACE + dataFileName, e);
            }
            throw new JBException(NLS.bind(
                Messages.ErrorMessageNOT_SUPP_DATASOURCE,
                dataFileName), 
                MessageIDs.E_DATASOURCE_FILE_IO);
        } catch (DataReadException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(Messages.ErrorReadingFile + StringConstants.COLON
                        + StringConstants.SPACE + dataFileName, e);
            }
            throw new JBException(NLS.bind(
                Messages.ErrorMessageDATASOURCE_READ_ERROR,
                dataFileName), 
                MessageIDs.E_DATASOURCE_READ_ERROR);
        }
    }

    /**
     * @param file file we will use as data source or null if file type is not
     * supported.
     * @return the filter type
     */
    private IDataImportFilter getFilterFromFileType(String file) {
        for (IDataImportFilter filter : m_filter) {
            String[] extensions = filter.getFileExtensions();
            for (String extension : extensions) {
                if (extension != null && extension.length() > 0) {
                    if (file.endsWith(extension)) { // NOPMD by al on 3/19/07 1:22 PM
                        return filter;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * update a given TestDataManager with data
     * 
     * @param filledTable
     *      data extracted from File
     * @param paramPo
     *      Parameter po we would like to update
     * @return
     *      filled TestDataManager with new data
     * @throws JBException
     *      error occurred while reading data source
     */
    private ITDManager parseTable(DataTable filledTable,
        IParameterInterfacePO paramPo) throws JBException {
        return parseTable(filledTable, paramPo, false);
    }

    /**
     * update a given TestDataManager with data
     * 
     * @param filledTable
     *            data extracted from File
     * @param paramPo
     *            Parameter po we would like to update
     * @param useParamInterfaceTDMan
     *            true to use the original td manager of the passed param po;
     *            falso to create a new one
     * @return filled TestDataManager with new data
     * @throws JBException
     *             error occurred while reading data source
     */
    public ITDManager parseTable(DataTable filledTable,
        IParameterInterfacePO paramPo, boolean useParamInterfaceTDMan)
        throws JBException {
        ITDManager tdMan;
        if (useParamInterfaceTDMan) {
            tdMan = paramPo.getDataManager();
        } else {
            tdMan = PoMaker.createTDManagerPO(paramPo);
        }
        // iterate over rows
        List<String> paramNamesExcel = 
            new ArrayList<String>();
        List <IParamDescriptionPO> paramNamesNode = 
            paramPo.getParameterList();
        final int rowCount = filledTable.getRowCount();
        for (int row = 0; row < rowCount; row++) {
            final int columnCount = filledTable.getColumnCount();
            for (int cellNr = 0; cellNr < columnCount; cellNr++) {
                final String cellString = getTestDataForTDManager(filledTable, 
                        row, cellNr);
                if (row == 0) {
                    paramNamesExcel.add(cellString);
                } else {
                    IParamDescriptionPO desc = paramPo
                            .getParameterForName(paramNamesExcel.get(cellNr));
                    if (desc != null) {
                        int dataSetNo = row - 1;
                        tdMan.updateCell(cellString,
                                dataSetNo, desc.getUniqueId());
                    }
                }
            }
        }
        for (IParamDescriptionPO desc : paramNamesNode) {
            if (!paramNamesExcel.contains(desc.getName())) {
                
                throw new JBException(NLS.bind(
                        Messages.ErrorMessageDATASOURCE_MISSING_PARAMETER,
                        new Object[] { desc.getName(),
                                paramPo.getName(),
                                paramPo.getDataFile()}),
                        MessageIDs.E_DATASOURCE_MISSING_PARAMETER);
            }
        }
        if (rowCount == 1) {
            throw new JBException(
                Messages.ErrorMessageDATASOURCE_MISSING_VALUES,
                MessageIDs.E_DATASOURCE_MISSING_VALUES);
        }
        return tdMan;
    }

    /**
     * Gets the value for the TestDataManager from the given DataTable.
     * Any internal Jubula-Symbols (References, Variables, etc.) will be
     * converted into the right format.
     * @param filledTable The DataTable of the Excel-file
     * @param row the row number
     * @param column the column number
     * @return the value for the TDManager
     * @throws IncompleteDataException if data are incomplete
     */
    private String getTestDataForTDManager(DataTable filledTable, 
            int row, int column) throws IncompleteDataException {
        
        String cellString = filledTable.getData(row, column);
        if (cellString == null || cellString.length() == 0) {
            
            MessageIDs.getMessageObject(
                    MessageIDs.E_DATASOURCE_CONTAIN_EMPTY_DATA).setDetails(
                            new String[] {});
            throw new IncompleteDataException(MessageIDs.getMessage(
                    MessageIDs.E_DATASOURCE_CONTAIN_EMPTY_DATA) 
                        + StringConstants.NEWLINE
                        + NLS.bind(Messages
                                .ErrorDetailDATASOURCE_CONTAIN_EMPTY_DATA,
                                new Object[] {row + 1, column + 1}), 
                MessageIDs.E_DATASOURCE_CONTAIN_EMPTY_DATA);
        }
        return cellString;
    }
    
    /**
     * Gets the ITDManager for the given IParamNodePO which has a file as
     * data source.<br>
     * If the given IParamNodePO has regular data,its ITDManager 
     * will be returned.
     * @param paramNode ParamNode
     * @return the usable TDManager
     * @throws JBException
     *      occurring Exception while creating TDManager
     */
    public ITDManager getExternalCheckedTDManager(IParamNodePO paramNode)
        throws JBException {
        
        boolean isTestRunning = 
            TestExecution.getInstance().getStartedTestSuite() != null
            && TestExecution.getInstance().getStartedTestSuite().isStarted()
            && TestExecution.getInstance().getConnectedAut() != null;
        
        return getExternalCheckedTDManager(paramNode, isTestRunning);
    }
    
    /**
     * Retrieves or generates a Test Data Manager for the given arguments. If 
     * the provided <code>paramNode</code> manages its own data, then its 
     * Test Data Manager is retrieved and returned. Otherwise, a Test Data 
     * Manager corresponding to:
     * <ul>
     *   <li>the external data referenced by <code>paramNode</code></li>
     *   <li>the provided locale</li>
     * </ul>
     * is generated and returned.
     * 
     * @param paramNode
     *              The node for which to retrieve / generate a Data Manager.
     *              Must not be <code>null</code>.
     * @param retrieveExternalData 
     *              Flag for forcing retrieval of external test data. If the 
     *              provided node requires external data and this flag is set to 
     *              <code>false</code>, then an empty Data Manager will be 
     *              returned.
     * 
     * @return the retrieved or generated Test Data Manager.
     * @throws JBException 
     *              if an error occurs while reading an external data source.
     */
    public ITDManager getExternalCheckedTDManager(
            IParamNodePO paramNode,
            boolean retrieveExternalData) throws JBException {
        
        Validate.notNull(paramNode);
        
        boolean usesExternalDataFile = 
            StringUtils.isNotEmpty(paramNode.getDataFile());
        boolean usesReferencedDataCube = 
            paramNode.getReferencedDataCube() != null;
                
        if (!usesExternalDataFile && !usesReferencedDataCube) {
            return paramNode.getDataManager();
        }

        if (!retrieveExternalData) {
            ITDManager tdManager = PoMaker.createTDManagerPO(paramNode);
            return tdManager;
        }

        if (usesExternalDataFile) {
            return createFilledTDManager(globalDataDir, paramNode.getDataFile(),
                    paramNode);
        } else if (usesReferencedDataCube) {
            return createFilledTDManager(
                    paramNode.getReferencedDataCube(), paramNode);
        }

        LOG.error(Messages.UnknownSourceType + StringConstants.COLON 
                + StringConstants.SPACE + paramNode.getName());
        return null;
    }
    
    /**
     * Clears this ExternalTestDataBP (e.g. the caches) after TestExecution 
     * has finished.
     */
    public void clearExternalData() {
        m_dataTableCache.clear();
        m_tdManagerCache.clear();
    }
    
    /**
     * @return the dataDir
     */
    public static File getDataDir() {
        return globalDataDir;
    }

    /**
     * @param dataDir the dataDir to set
     */
    public static void setDataDir(File dataDir) {
        globalDataDir = dataDir;
    }

}
