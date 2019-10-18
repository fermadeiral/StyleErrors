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
package org.eclipse.jubula.client.ui.rcp.search.query;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.labelprovider.GeneralLabelProvider;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.NodeSearchResultElementAction;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.SearchResultElement;
import org.eclipse.osgi.util.NLS;


/**
 * This class provides common methods for "show where used queries".
 * Currently it is a place holder.
 *
 * @author BREDEX GmbH
 * @created Jul 27, 2010
 */
public abstract class AbstractShowWhereUsedQuery extends AbstractQuery {

    /**
     * @param viewId The default view ID to open.
     */
    public AbstractShowWhereUsedQuery(String viewId) {
        super(viewId);
    }

    /**
     * gives a search result containing the parents of an array of INodePOs
     *
     * @param monitor
     *              the monitor
     * @param reuse
     *              the array containing the INodePOs
     * @return the search result
     */
    protected List<SearchResultElement<?>> getResultElementsFromNodes(
            IProgressMonitor monitor, INodePO[] reuse) {
        final List<SearchResultElement<?>> reuseLoc =
            new ArrayList<SearchResultElement<?>>(reuse.length);

        for (INodePO node : reuse) {
            INodePO parent = node.getSpecAncestor();
            if (parent == null) {
                parent = node.getParentNode();
            }
            if (parent != null) {
                Long id = node.getId();
                String nodeName = GeneralLabelProvider.getTextImpl(node);
                reuseLoc.add(new SearchResultElement<Long>(NLS.bind(
                        Messages.SearchResultPageElementLabel, new Object[] {
                                parent.getName(), nodeName }), id,
                                GeneralLabelProvider.getImageImpl(node),
                        new NodeSearchResultElementAction(), node
                                .getComment()));
            }
            monitor.worked(1);
        }
        return reuseLoc;
    }

}
