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
package org.eclipse.jubula.rc.swing.tester.adapter;

import javax.swing.JTree;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
//import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeOperationContext;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITreeComponent;
import org.eclipse.jubula.rc.swing.tester.util.TreeOperationContext;
/**
 * Implementation of the Tree interface as an adapter for <code>JTree</code>.
 * 
 * @author BREDEX GmbH
 *
 */
public class JTreeAdapter extends JComponentAdapter implements ITreeComponent {
    
   
    /**
     * Creates an object with the adapted JTree.
     * @param objectToAdapt 
     */
    public JTreeAdapter(Object objectToAdapt) {
        super(objectToAdapt);
    }
    
    /**
     * @return the casted Object 
     */
    private JTree getTable() {
        return (JTree) getRealComponent();
    }
        
    /**
     * {@inheritDoc}
     */
    public AbstractTreeOperationContext getContext() {
        TreeOperationContext context = new TreeOperationContext(
                getEventThreadQueuer(), getRobot(), getTable());
        return context;
    }
    /**
     * {@inheritDoc}
     */
    public Object getRootNode() {
        
        return getTable().getModel().getRoot();
    }
    /**
     * {@inheritDoc}
     */
    public boolean isRootVisible() {
        return getTable().isRootVisible();
    }


    /**
     * {@inheritDoc}
     */
    public String getPropertyValueOfCell(String name, Object cell) {
        StepExecutionException.throwUnsupportedAction();
        return null;
    }
    
}
