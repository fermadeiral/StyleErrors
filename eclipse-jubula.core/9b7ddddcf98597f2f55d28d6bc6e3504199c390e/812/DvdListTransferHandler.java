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
package org.eclipse.jubula.examples.aut.dvdtool.control;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListModel;

/**
 * The instances of this class handle transfer operations of Strings in JList
 * objects that have non-empty Strings as elements.
 * The source and target element of the transfer operation swap their position.
 * 
 * (Needed for supporting 'drag and drop' feature in the language list.)
 * 
 * @author BREDEX GmbH
 * @created 06.02.2008
 */
public class DvdListTransferHandler extends StringTransferHandler {
    
    /**
     * {@inheritDoc}
     */
    protected String exportString(JComponent c) {
        JList listComp = (JList)c;
        String selectedString = (String) listComp.getSelectedValue();
        
        return selectedString;
    }
    
    /**
     * {@inheritDoc}
     */
    protected void importString(JComponent c, String draggedStr) {
        JList listComp = (JList)c;
        int selectedIndex = listComp.getSelectedIndex();
        String selectedString = (String) listComp.getSelectedValue();
        ListModel currentModel = listComp.getModel();
        
        // build new model
        String[] newModel = new String[currentModel.getSize()];
        for (int i = 0; i < newModel.length; i++) {
            String currentStr = (String) currentModel.getElementAt(i);
            
            if (currentStr.equals(draggedStr)) {
                newModel[i] = selectedString;
            } else {
                newModel[i] = currentStr;
            }
        }
        newModel[selectedIndex] = draggedStr;
        
        // update model of listComp
        listComp.setListData(newModel);
        listComp.setSelectedIndex(selectedIndex);
    }
    
}
