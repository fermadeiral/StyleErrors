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
package org.eclipse.jubula.examples.aut.adder.swing.businessprocess;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.eclipse.jubula.examples.aut.adder.swing.gui.AutFrame;
import org.eclipse.jubula.examples.aut.adder.swing.model.DivisionOperator;
import org.eclipse.jubula.examples.aut.adder.swing.model.IOperator;
import org.eclipse.jubula.examples.aut.adder.swing.model.MinusOperator;
import org.eclipse.jubula.examples.aut.adder.swing.model.MultiplyOperator;
import org.eclipse.jubula.examples.aut.adder.swing.model.OperatorTreeNode;
import org.eclipse.jubula.examples.aut.adder.swing.model.OptionsTableEntry;
import org.eclipse.jubula.examples.aut.adder.swing.model.OptionsTableModel;
import org.eclipse.jubula.examples.aut.adder.swing.model.PlusOperator;


/**
 * This class handles the business process for the AutFrame, concerning the
 * initialisation and the window handling.
 * 
 * @author BREDEX GmbH
 * @created 20.07.2004
 */
public class AutFrameBP {
    /**
     * The default precisions: <code>0, 1, 2</code>.
     */
    private static final Integer[] DEFAUT_PRECISIONS = new Integer[] {
        new Integer(0), new Integer(1), new Integer(2) };

    /**
     * WindowEvent class handling the event 'window closing'.
     * @author BREDEX GmbH
     * @created 20.07.2004
     */
    private class AutWindowClosing extends WindowAdapter {
        /**
         * Handles windowClosing
         * 
         * @param event
         *            a <code>WindoeEvent</code>
         */
        public void windowClosing(WindowEvent event) {
            handleClosing();
        }
    }

    /**
     * The frame of AUT
     */
    private AutFrame m_autFrame = null;

    /**
     * The current operator of the calculator.
     */
    private IOperator m_currentOperator;

    /**
     * when set to false, AUT is started in Simple Mode otherwise in Advanced
     * Mode
     */
    private boolean m_complexMode;
    
    /**
     * When set to true, the AUT uses another layout
     */
    private boolean m_alternativeLayout;
    /**
     * When set to true, the AUT waits 5 seconds before it shows the about
     * dialog
     */
    private boolean m_slow;

    /**
     * Default constructor Initialize the AutFrame
     * 
     * @param mode
     *            boolean : what mode aut is started in
     * @param alternativeLayout
     *            boolean : which layout to use
     * @param slow
     *            boolean : true to show the about dialog with a 5 second delay
     */
    public AutFrameBP(boolean mode, boolean alternativeLayout, boolean slow) {
        super();
        m_complexMode = mode;
        m_alternativeLayout = alternativeLayout;
        m_slow = slow;
        initialize();
    }

    /**
     * compute result
     */
    private void evaluate() {

        AutFrame frame = getAutFrame();

        float value1 = 17;
        float value2 = 4;

        try {
            if (frame.getFloatCheckBox().isSelected()) {
                value1 = Float.parseFloat(frame.getCalculatorPanel()
                        .getAddend1());
                value2 = Float.parseFloat(frame.getCalculatorPanel()
                        .getAddend2());
            } else {
                value1 = Integer.parseInt(frame.getCalculatorPanel()
                        .getAddend1());
                value2 = Integer.parseInt(frame.getCalculatorPanel()
                        .getAddend2());
            }
            if (frame.getCalculationMode() == AutFrame.BLACK_JACK_MODE
                    && value1 == 17 && value2 == 4) {
                frame.getCalculatorPanel().setSum("jackpot"); //$NON-NLS-1$
            } else if (frame.getCalculationMode() == AutFrame.DEEP_THOUGHT_MODE
                    && m_currentOperator.calculate(value1, value2) == 42) {
                frame.getCalculatorPanel().setSum("tricky..."); //$NON-NLS-1$
            } else {
                float result = m_currentOperator.calculate(value1, value2);
                if (frame.getFloatCheckBox().isSelected()) {
                    NumberFormat format = NumberFormat.getNumberInstance();
                    format.setMaximumFractionDigits(((Integer) frame
                            .getPrecisionComboBox().getSelectedItem())
                            .intValue());
                    frame.getCalculatorPanel().setSum(format.format(result));
                } else {
                    frame.getCalculatorPanel().setSum(
                            String.valueOf((int) result));
                }
            }
        } catch (NumberFormatException nfe) {
            frame.getCalculatorPanel().setSum("#error"); //$NON-NLS-1$
        }

    }

    /**
     * Gets the AutFrame
     * 
     * @return a <code>AutFrame</code> object
     */
    public AutFrame getAutFrame() {
        if (m_autFrame == null) {
            m_autFrame = new AutFrame(
                    "Adder", m_complexMode, m_alternativeLayout); //$NON-NLS-1$
            m_autFrame.addWindowListener(new AutWindowClosing());
        }
        return m_autFrame;
    }

    /**
     * Handle the closing of the window.
     * 
     */
    protected void handleClosing() {
        System.exit(0);
    }

    /**
     * Initialize the AutFrame
     * 
     */
    private void initialize() {
        if (m_complexMode) {
            initPrecisionComboBox();
            initOptionsTable();
        }
        initOptionsTree();

        initListeners();

        if (m_complexMode) {
            updatePrecisionComboBoxEnabled();
        }
        updateOperator(new PlusOperator());

        AutFrame frame = getAutFrame();

        frame.pack();

        frame.setLocation(200, 200);
        frame.pack();
        frame.setSize(
                Math.max(300, frame.getSize().width + 20),
                Math.max(200, frame.getSize().height));
        frame.setVisible(true);
    }

    /**
     * Initializes the listeners of the equals-button, the float-checkbox, the
     * options tree and the options table.
     */
    private void initListeners() {
        getAutFrame().getCalculatorPanel().addEvaluateActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        evaluate();
                    }
                });
        getAutFrame().getFloatCheckBox().addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        updatePrecisionComboBoxEnabled();
                    }
                });

        JTree tree = getAutFrame().getOptionsTree();
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = e.getNewLeadSelectionPath();
                if (path != null) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
                            .getLastPathComponent();
                    if (node.getUserObject() instanceof IOperator) {
                        getAutFrame().getCalculatorPanel().setOperation(
                                node.getUserObject().toString());
                        m_currentOperator = (IOperator) node.getUserObject();
                    }
                }
            }
        });

        if (m_complexMode) {
            JTable table = getAutFrame().getOptionsTable();
            table.getModel().addTableModelListener(new TableModelListener() {
                public void tableChanged(TableModelEvent e) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        updateFont();
                    }
                }
            });
        }

        // Menu items
        AutFrame frame = getAutFrame();
        frame.getQuit().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        frame.getReset().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                executeActionResetGui();
            }
        });
        frame.getReset().setAccelerator(KeyStroke.getKeyStroke("control R")); //$NON-NLS-1$
        frame.getAbout().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (m_slow) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e1) {
                        // ignore
                    }
                }
                executeActionShowAboutDialog();
            }
        });
    }

    /**
     * Resets the GUI.
     */
    private void executeActionResetGui() {
        AutFrame frame = getAutFrame();
        frame.getCalculatorPanel().clearAddend1();
        frame.getCalculatorPanel().clearAddend2();
        frame.getCalculatorPanel().clearSum();
    }

    /**
     * Shows the about dialog.
     * 
     */
    private void executeActionShowAboutDialog() {
        JOptionPane.showMessageDialog(getAutFrame(),
                "Application under Test\n" + //$NON-NLS-1$
                        "\ncopyright by " + //$NON-NLS-1$
                        "BREDEX Software GmbH", //$NON-NLS-1$
                "about", 1); //$NON-NLS-1$
    }

    /**
     * Initializes the options tree by creating nodes and a default tree model.
     */
    private void initOptionsTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        DefaultMutableTreeNode operation = new DefaultMutableTreeNode(
                "Operators"); //$NON-NLS-1$
        OperatorTreeNode plus = new OperatorTreeNode(new PlusOperator());
        OperatorTreeNode minus = new OperatorTreeNode(new MinusOperator());
        OperatorTreeNode mul = new OperatorTreeNode(new MultiplyOperator());
        OperatorTreeNode div = new OperatorTreeNode(new DivisionOperator());
        root.add(operation);
        operation.add(plus);
        operation.add(minus);
        operation.add(mul);
        operation.add(div);

        JTree tree = getAutFrame().getOptionsTree();
        tree.setModel(new DefaultTreeModel(root));
        tree.setRootVisible(false);
    }

    /**
     * Initializes the options table by creating a
     * <code>OptionsTableModel</code> and two entries.
     */
    private void initOptionsTable() {
        OptionsTableModel model = new OptionsTableModel();
        model.addOptionsEntry(new OptionsTableEntry("Font type", "Dialog")); //$NON-NLS-1$ //$NON-NLS-2$
        model.addOptionsEntry(new OptionsTableEntry("Font size", "12")); //$NON-NLS-1$ //$NON-NLS-2$
        getAutFrame().getOptionsTable().setModel(model);
    }

    /**
     * Initializes the precision-combobox.
     */
    private void initPrecisionComboBox() {
        getAutFrame().getPrecisionComboBox().setModel(
                new DefaultComboBoxModel(DEFAUT_PRECISIONS));
    }

    /**
     * Updates the enabled/disabled state of the precision-combobox.
     */
    private void updatePrecisionComboBoxEnabled() {
        getAutFrame().getPrecisionComboBox().setEnabled(
                getAutFrame().getFloatCheckBox().isSelected());
    }

    /**
     * Updates the operator of the calculator by setting the current operator
     * and the AUT's operation label.
     * 
     * @param operator
     *            The new operator.
     */
    private void updateOperator(IOperator operator) {
        m_currentOperator = operator;
        getAutFrame().getCalculatorPanel().setOperation(
                m_currentOperator.toString());
    }

    /**
     * Updates the font of the result textfield after it has been changed in the
     * options table.
     */
    private void updateFont() {
        OptionsTableModel model = (OptionsTableModel) getAutFrame()
                .getOptionsTable().getModel();
        String name =  model.getRowEntry(0).getDescription();
        String size =  model.getRowEntry(1).getDescription();
        Font font = new Font(name, Font.PLAIN, Integer.parseInt(size));
        getAutFrame().setFont(font);
    }
}
