/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.search.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jubula.client.core.businessprocess.TestDataBP;
import org.eclipse.jubula.client.core.model.IDataSetPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.ITDManager;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.utils.ITreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;
import org.eclipse.jubula.client.core.utils.TreeTraverser;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.SearchResultElement;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.TestDataCubeExtendedAction;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

/**
 * Searches for (nearly) exact CTDS references
 * @author BREDEX GmbH
 *
 */
public class ShowWhereReferencedCTDSValueQuery
    extends AbstractShowWhereUsedQuery implements ITreeNodeOperation<INodePO> {

    /** The CTDS */
    private IParameterInterfacePO m_dataSet;

    /** The row index */
    private int m_row;

    /** The column index */
    private int m_col;

    /** The column name */
    private String m_colStr;

    /** The exact Search Results */
    private List<SearchResultElement> m_exactResults;

    /** The column-only Search Results */
    private List<SearchResultElement> m_columnResults;

    /** The progress monitor */
    private IProgressMonitor m_monitor;

    /** The node counter */
    private int m_counter;

    /**
     * @param ctds the central test data
     * @param row the selected row
     * @param col the selected column
     */
    public ShowWhereReferencedCTDSValueQuery(IParameterInterfacePO ctds,
            int row, int col) {
        super(null);
        m_dataSet = ctds;
        m_row = row;
        m_col = col;
        m_colStr = m_dataSet.getParamNames().get(m_col);
    }

    /**
     * Class containing a reference to a CTDS
     * @author BREDEX GmbH
     */
    public static class CTDSReference {
        /** The referencing node */
        private IParamNodePO m_node;

        /** The row */
        private int m_row;

        /** The column */
        private int m_col;

        /**
         * Constructor
         * @param node the node
         * @param row the row
         * @param col the column
         */
        public CTDSReference(IParamNodePO node, int row, int col) {
            m_node = node;
            m_row = row;
            m_col = col;
        }

        /** @return the node */
        public IParamNodePO getNode() {
            return m_node;
        }

        /** @return the row */
        public int getRow() {
            return m_row;
        }

        /** @return the column */
        public int getColumn() {
            return m_col;
        }
    }

    @Override
    public boolean operate(ITreeTraverserContext<INodePO> ctx,
            INodePO parent, INodePO node, boolean alreadyVisited) {
        if (m_monitor == null) {
            m_counter++; // only relevant when m_monitor is null
            return true;
        }
        m_monitor.worked(1);
        if (!(node instanceof IParamNodePO)) {
            return true;
        }
        if (m_monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        IParamNodePO pNode = (IParamNodePO) node;
        ITDManager manager = pNode.getDataManager();
        for (int i = 0; i < manager.getDataSetCount(); i++) {
            IDataSetPO row = manager.getDataSet(i);
            for (int j = 0; j < row.getColumnCount(); j++) {
                for (String[] ref : TestDataBP.getAllCTDSReferences(
                        manager.getDataSet(i).getValueAt(j))) {
                    if (!StringUtils.equals(ref[0], m_dataSet.getName())
                            || !StringUtils.equals(ref[3], m_colStr)) {
                        // DS or VALUE columns are different
                        continue;
                    }
                    addResult(pNode, ref, i, j);
                }
            }
        }
        return true;
    }

    /**
     * Adds an appropriate (exact or column-only) search result
     * @param pNode the current ParamNodePO
     * @param ref the found reference - String of length 4 corresponding to
     *          the arguments of ?getCTDSValue.
     * @param row the row number of the currently investigated cell
     * @param col the column number
     */
    private void addResult(IParamNodePO pNode, String[] ref,
            int row, int col) {
        boolean exact = false;
        IParamDescriptionPO desc = m_dataSet.getParameterForName(ref[1]);
        if (desc != null) {
            String val = m_dataSet.getDataManager().getCell(m_row, desc);
            if (StringUtils.equals(val, ref[2])) {
                // exact result: the current DS cell is referenced here
                exact = true;
            }
        }
        Image img = exact ? IconConstants.START_OM
                : IconConstants.STOP_OM;
        SearchResultElement<CTDSReference> result =
                new SearchResultElement<CTDSReference>(NLS.bind(
                    Messages.SearchResultPageElementLabel, new Object[] {
                        pNode.getParentNode().getName(), pNode.getName()}),
                    new CTDSReference(pNode, row, col),
                    img, new TestDataCubeExtendedAction());
        if (exact) {
            m_exactResults.add(result);
        } else {
            m_columnResults.add(result);
        }
    }

    @Override
    public IStatus run(IProgressMonitor monitor)
            throws OperationCanceledException {
        m_exactResults = new ArrayList<>();
        m_columnResults = new ArrayList<>();
        TreeTraverser trav = new TreeTraverser(
                GeneralStorage.getInstance().getProject(), this, true, true);
        trav.setTraverseIntoExecs(false);
        trav.setTraverseReused(false);
        long time = System.currentTimeMillis();
        // Counting the visited nodes
        m_monitor = null;
        m_counter = 0;
        trav.traverse(false);
        m_monitor = monitor;
        monitor.beginTask(Messages.SearchingForCTDSRefs, m_counter);
        trav.traverse(false);
        monitor.done();
        List<SearchResultElement> results = new ArrayList<>();
        results.addAll(m_exactResults);
        results.addAll(m_columnResults);
        setSearchResult(results);
        return Status.OK_STATUS;
    }

    @Override
    public String getLabel() {
        return "Searching for CTDS reference"; //$NON-NLS-1$
    }

    @Override
    public void postOperate(ITreeTraverserContext<INodePO> ctx, INodePO parent,
            INodePO node, boolean alreadyVisited) {
        // not used
    }
}
