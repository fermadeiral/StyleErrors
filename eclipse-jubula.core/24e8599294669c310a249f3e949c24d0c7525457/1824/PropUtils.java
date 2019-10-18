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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.jubula.client.teststyle.checks.Category;
import org.eclipse.jubula.client.teststyle.checks.CheckCont;
import org.eclipse.jubula.client.teststyle.properties.nodes.CategoryNode;
import org.eclipse.jubula.client.teststyle.properties.nodes.INode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


/**
 * @author marcell
 * @created Oct 21, 2010
 */
public class PropUtils {

    // --------------------------------------------------------------------------
    // Constants for the fixedSizeComposite
    // --------------------------------------------------------------------------
    /** Width of the configuration */
    public static final int WIDTH = 200;
    /** Height of the configuration */
    public static final int HEIGHT = 200;
    /** The number of columns of the layout */
    public static final int NUM_COLUMNS = 2;
    /** The spacing to the left and right side */
    public static final int MARGIN_WIDTH = 10;
    /** The spacing to the above and under the configuration */
    public static final int MARGIN_HEIGHT = 10;

    /**
     * Private constructor because its a utility class
     */
    private PropUtils() {
        // NOTHIIIIIIIIIING
    }

    /**
     * Returns a composite for the properties window. The parent must use a
     * gridlayout, so that this function can create the right griddata for
     * creating a good size.
     * 
     * @param parent
     *            The composite where the new composite should be created.
     * @return A composite which contains a griddata with a fixed size.
     */
    public static Composite createCustomComposite(Composite parent) {
        // Create the layout
        GridLayout layout = new GridLayout();
        layout.marginHeight = MARGIN_HEIGHT;
        layout.marginWidth = MARGIN_WIDTH;

        // Create the GridData for this composite
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.heightHint = WIDTH;
        data.widthHint = HEIGHT;

        // And finally create the composite
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(layout);
        composite.setLayoutData(data);
        return composite;
    }

    /**
     * @return All categories of the plugin in the right node structure.
     */
    public static INode[] getCategoriesAsNodes() {
        List<INode> nodes = new ArrayList<INode>();
        Set<Category> categories = CheckCont.getCategories();
        for (Category category : categories) {
            nodes.add(new CategoryNode(category));
        }
        Collections.sort(nodes);
        return nodes.toArray(new INode[nodes.size()]);
    }

}
