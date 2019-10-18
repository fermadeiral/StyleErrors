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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.eclipse.jubula.examples.aut.dvdtool.DevelopmentState;
import org.eclipse.jubula.examples.aut.dvdtool.model.DvdCategory;
import org.eclipse.jubula.examples.aut.dvdtool.resources.Resources;


/**
 * This is the action class for adding a category
 * @author BREDEX GmbH
 * @created 13.04.2005
 */
public class DvdAddCategoryAction extends DvdAbstractDialogAction {

    /** simulated version state */
    private static boolean isVersion1 = DevelopmentState.instance().isV1();
    /** simulated version state */
    private static boolean isVersion2 = DevelopmentState.instance().isV2();
    /** simulated version state */
    private static boolean isVersion3 = DevelopmentState.instance().isV3();

    /** the controller of the main frame */
    private transient DvdMainFrameController m_controller; // see findBugs
    
    /**
     * public constructor
     * @param name the text to display
     * @param controller the controller of the main frame
     */
    public DvdAddCategoryAction(String name, 
            DvdMainFrameController controller) {
        
        super(name, controller, "new.category.input.message"); //$NON-NLS-1$
        m_controller = controller;
    }
    
    /**
     * {@inheritDoc}
     */
    public void handleDialogInput(String inputValue) {
        // add a category to current selection of the tree
        m_controller.addCategory(inputValue);
    }

    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e) {
        if (isVersion1 || isVersion2 || isVersion3) {
            final JTextField textField = new JTextField(30);

            final JButton okButton = 
                    new JButton(Resources.getString("new.category.input.okLabel")); //$NON-NLS-1$
            okButton.setName("new.category.input.okButton"); //$NON-NLS-1$
            JButton cancelButton = 
                    new JButton(Resources.getString("new.category.input.cancelLabel")); //$NON-NLS-1$
            cancelButton.setName("new.category.input.cancelButton"); //$NON-NLS-1$
            Object[] buttons = { okButton, cancelButton };

            addValidator(textField, okButton);
            // Create an array of the text and components to be displayed.
            Object[] array = {
                Resources.getString("new.category.input.message"), //$NON-NLS-1$
                textField };
            
            final JOptionPane pane = new JOptionPane(array,
                    JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION,
                    null, buttons);
            final JDialog dialog = pane.createDialog(
                    m_controller.getDvdMainFrame(),
                    Resources.getString("new.category.input.message")); //$NON-NLS-1$
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    pane.setValue(textField.getText());
                    dialog.setVisible(false);
                }
            });
            okButton.setEnabled(false);
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    pane.setValue(null);
                    dialog.setVisible(false);
                }
            });

            dialog.setVisible(true);
            Object selectedValue = pane.getValue();
            if (selectedValue instanceof String
                    && ((String) selectedValue).trim().length() != 0) {
                handleDialogInput((String) selectedValue);
            }
        } else {
            super.actionPerformed(e);
        }
    }

    /**
     * adds validation code to the textfield
     * @param textField checked field
     * @param okButton will be disabled if validation fails
     */
    private void addValidator(final JTextField textField, 
        final JButton okButton) {
        textField.addKeyListener(new KeyListener() {
            public void keyReleased(KeyEvent ev) {
                if (!okButton.isDefaultButton()) {
                    okButton.getRootPane().setDefaultButton(okButton);
                }
                String newCatName = textField.getText();
                final boolean empty = newCatName.trim().length() == 0;
                okButton.setEnabled(!empty);

                if (isVersion2 || isVersion3) {
                    if (!empty) {
                        DvdCategory root = DvdManager.singleton()
                                .getRootCategory();
                        okButton.setEnabled(!categoryExists(root, 
                                newCatName));
                    }
                }
            }

            private boolean categoryExists(DvdCategory cat,
                    String newCatName) {
                if (cat.getName().equals(newCatName)) {
                    return true;
                }
                Iterator it = cat.getCategories().iterator();
                while (it.hasNext()) {
                    DvdCategory child = (DvdCategory)it.next();
                    if (categoryExists(child, newCatName)) {
                        return true;
                    }
                }
                return false;
            }

            public void keyTyped(KeyEvent ev) {
                // nothing here
            }

            public void keyPressed(KeyEvent ev) {
                // nothing here
            }
        });
    }
}