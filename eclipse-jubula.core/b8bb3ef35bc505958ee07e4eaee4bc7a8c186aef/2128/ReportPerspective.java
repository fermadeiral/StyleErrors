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
package org.eclipse.jubula.client.ui.perspective;

import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


/**
 * The Report Perspective.
 * @author BREDEX GmbH
 * @created 08.08.2005
 */
public class ReportPerspective implements IPerspectiveFactory {

    /** perspective ID */
    public static final String PERSPECTIVE_ID = 
            "org.eclipse.jubula.client.ui.perspectives.ReportPerspective";  //$NON-NLS-1$
    
    /**
     * constructor
     */
    public ReportPerspective() { 
        super();
    }
    /**
     * Creates the initial layout for a page.
     * @param layout IPageLayout
     */
    public void createInitialLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();
        String topRightId = "topRight"; //$NON-NLS-1$
        String bottomRightId = "bottomRight"; //$NON-NLS-1$
        String bottomLeftId = "bottomLeft"; //$NON-NLS-1$
        
        IFolderLayout topRight = layout.createFolder(topRightId,
                IPageLayout.RIGHT, 0.70f, editorArea);
        IFolderLayout bottomRight = layout.createFolder(bottomRightId,
                IPageLayout.BOTTOM, 0.5f, topRightId);
        IFolderLayout bottomLeft = layout.createFolder(bottomLeftId,
                IPageLayout.BOTTOM, 0.70f, editorArea);
        
        topRight.addView(Constants.PROPVIEW_ID);
        bottomRight.addView(Constants.IMAGEVIEW_ID);
        bottomLeft.addView(Constants.TESTRESULT_SUMMARY_VIEW_ID);
        
        // mark metadata view as not closeable
        layout.getViewLayout(Constants.TESTRESULT_SUMMARY_VIEW_ID)
            .setCloseable(false);
    }
}