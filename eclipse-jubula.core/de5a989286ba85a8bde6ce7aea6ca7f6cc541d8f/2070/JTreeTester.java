/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.swing.tester;

import java.awt.Point;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.eclipse.jubula.rc.common.driver.DragAndDropHelper;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.AbstractTreeTester;
import org.eclipse.jubula.toolkit.enums.ValueSets;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

/**
 * Toolkit specific commands for the <code>JTree</code>
 * 
 * @author BREDEX GmbH
 */
public class JTreeTester extends AbstractTreeTester {
   
    /**
     * {@inheritDoc}
     */
    public void rcDragByTextPath(int mouseButton,
            String modifier, String pathType, int preAscend,
            String treeTextPath, String operator) {
        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        dndHelper.setModifier(modifier);
        dndHelper.setMouseButton(mouseButton);
        rcSelect(pathType, preAscend, treeTextPath, operator, 0, 1, 
                ValueSets.BinaryChoice.no.rcValue());
        pressOrReleaseModifiers(modifier, true);
        getRobot().mousePress(null, null, mouseButton);

    }

    /**
     * {@inheritDoc}
     */
    public void rcDropByTextPath(String pathType, int preAscend,
            String treeTextPath, String operator, int delayBeforeDrop) {
        try {
            getRobot().shakeMouse();
            rcSelect(pathType, preAscend, treeTextPath, operator, 0, 1,
                    ValueSets.BinaryChoice.no.rcValue());
            waitBeforeDrop(delayBeforeDrop);
        } finally {
            final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
            getRobot().mouseRelease(null, null, dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
        }

    }

    /**
     * {@inheritDoc}
     */
    public void rcDragByIndexPath(int mouseButton,
            String modifier, String pathType, int preAscend,
            String treeIndexPath) {
        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        dndHelper.setModifier(modifier);
        dndHelper.setMouseButton(mouseButton);
        rcSelectByIndices(pathType, preAscend, treeIndexPath, 0, 1,
                ValueSets.BinaryChoice.no.rcValue());
        pressOrReleaseModifiers(modifier, true);
        getRobot().mousePress(null, null, mouseButton);

    }

    /**
     * {@inheritDoc}
     */
    public void rcDropByIndexPath(String pathType, int preAscend,
            String treeIndexPath, int delayBeforeDrop) {
        try {
            getRobot().shakeMouse();
            rcSelectByIndices(pathType, preAscend, treeIndexPath, 0, 1,
                    ValueSets.BinaryChoice.no.rcValue());
            waitBeforeDrop(delayBeforeDrop);
        } finally {
            final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
            getRobot().mouseRelease(null, null, dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
        }
        
    }
    
    /**
     * 
     * @return the tree node at the current mouse position.
     * @throws StepExecutionException If no tree node can be found at the 
     *                                current mouse position.
     */
    protected Object getNodeAtMousePosition() throws StepExecutionException {
        return getEventThreadQueuer().invokeAndWait("getNodeAtMousePosition", new IRunnable<Object>() { //$NON-NLS-1$
            
            public Object run() throws StepExecutionException {
                Point mousePosition = getRobot().getCurrentMousePosition();
                Point treeLocation = ((JTree) getRealComponent()).
                        getLocationOnScreen();
                Point relativePos = new Point(
                        mousePosition.x - treeLocation.x,
                        mousePosition.y - treeLocation.y);

                int rowAtMousePosition = 
                    ((JTree) getRealComponent()).
                        getRowForLocation(relativePos.x, relativePos.y);
                
                if (rowAtMousePosition != -1) {
                    TreePath treePath = 
                        ((JTree) getRealComponent()).
                            getPathForLocation(relativePos.x, relativePos.y);
                    
                    if (treePath != null 
                            && treePath.getLastPathComponent() != null) {
                        return treePath.getLastPathComponent();
                    }
                    
                }
                
                throw new StepExecutionException("No tree node found at mouse position.", //$NON-NLS-1$
                    EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
            }
            
        });

    }

}
