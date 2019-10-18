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
package org.eclipse.jubula.client.ui.rcp.perspective;

import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Execution Perspective.
 * @author BREDEX GmbH
 * @created 08.08.2005
 */
public class ExecutionPerspective implements IPerspectiveFactory {

    /** The logger */
    static final Logger LOG = 
        LoggerFactory.getLogger(ExecutionPerspective.class);
    /** Ration 0.27f */
    private static final float RATIO_0_27 = 0.27f;
    /** Ration 0.5f */
    private static final float RATIO_0_5 = 0.5f;
    /** Ration 0.6f */
    private static final float RATIO_0_6 = 0.6f;
    
    /**
     * construct ExecutionPerspective
     */
    public ExecutionPerspective() { 
        super();
    }
    
    /**
     * Creates the initial layout for a page.
     * @param layout IPageLayout
     */
    public void createInitialLayout(IPageLayout layout) { 
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible(false);
        IFolderLayout left = layout.createFolder("left", //$NON-NLS-1$
                IPageLayout.LEFT, RATIO_0_27, editorArea);
        IFolderLayout topLeft = layout.createFolder("topLeft", //$NON-NLS-1$
                IPageLayout.TOP, RATIO_0_27, "left"); //$NON-NLS-1$
        IFolderLayout topRight = layout.createFolder("topRight", //$NON-NLS-1$
                IPageLayout.RIGHT, RATIO_0_6, editorArea);
        IFolderLayout bottomRight = layout.createFolder("bottomRight", //$NON-NLS-1$
                IPageLayout.BOTTOM, RATIO_0_5, "topRight"); //$NON-NLS-1$
        IFolderLayout middle = layout.createFolder("middle", //$NON-NLS-1$
                IPageLayout.LEFT, RATIO_0_27, editorArea);
        IFolderLayout middleBottom = layout.createFolder("middleBottom", //$NON-NLS-1$
                IPageLayout.BOTTOM, 0.8f, "middle"); //$NON-NLS-1$
        left.addView(Constants.TS_BROWSER_ID);
        topLeft.addView(Constants.RUNNING_AUTS_VIEW_ID);
        middle.addView(Constants.TESTRE_ID);
        middleBottom.addView(IPageLayout.ID_PROGRESS_VIEW);
        topRight.addView(Constants.PROPVIEW_ID);
        bottomRight.addView(Constants.IMAGEVIEW_ID);

        // mark test result tree view as not closeable
        layout.getViewLayout(Constants.TESTRE_ID).setCloseable(false);
    }
}