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
package org.eclipse.jubula.autagent.common.gui;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.eclipse.jubula.tools.internal.i18n.I18n;


/**
 * @author BREDEX GmbH
 * @created Aug 18, 2010
 */
public class ManualTestStepOptionPane extends JOptionPane {
    /**
     * <code>DIALOG_TITLE</code>
     */
    private static final String DIALOG_TITLE = I18n.getString("Cap.ManualTestStepDialogTitle"); //$NON-NLS-1$

    /**
     * <code>MAX_CHAR_PER_LINE_COUNT</code>
     */
    private static final int MAX_CHAR_PER_LINE_COUNT = 75;

    /**
     * <code>DIALOG_EXIT_OPTIONS</code>
     */
    private static final Object[] DIALOG_EXIT_OPTIONS = new Object[] {
        StepStatus.Passed, StepStatus.Failed };

    /**
     * <code>LINE_BREAK</code>
     */
    private static final String LINE_BREAK = "\n"; //$NON-NLS-1$
    
    /**
     * @author BREDEX GmbH
     * @created Aug 19, 2010
     */
    private static enum StepStatus {
        /**
         * <code>Ok</code>
         */
        Passed,
        
        /**
         * <code>Failed</code>
         */
        Failed
    }
    
    /**
     * @author BREDEX GmbH
     * @created Aug 18, 2010
     */
    public static class ManualTestStepResult {
        /**
         * <code>m_comment</code>
         */
        private String m_comment = null;
        
        /**
         * <code>m_successful</code>
         */
        private boolean m_successful = false;

        /**
         * Constructor
         * @param comment the user comment
         * @param success true if manual test step succeeded; false otherwise
         */
        public ManualTestStepResult(String comment, boolean success) {
            setComment(comment);
            setSuccessful(success);
        }
        
        /**
         * @param comment the comment to set
         */
        private void setComment(String comment) {
            m_comment = comment;
        }

        /**
         * @return the comment
         */
        public String getComment() {
            return m_comment;
        }

        /**
         * @param successful the successful to set
         */
        private void setSuccessful(boolean successful) {
            m_successful = successful;
        }

        /**
         * @return the successful
         */
        public boolean isSuccessful() {
            return m_successful;
        }
    }
    
    /**
     * @param message the message
     * @param messageType the message type
     * @param optiontype the option type
     * @param icon ht eicon
     * @param dialogExitOptions the exit options
     * @param defaultexit the default exit option
     */
    public ManualTestStepOptionPane(Object message, int messageType,
            int optiontype, Icon icon, Object[] dialogExitOptions,
            Object defaultexit) {
        super(message, messageType, optiontype, icon, dialogExitOptions,
                defaultexit);
    }

    /**
     * @param actionToPerform
     *            a string description
     * @param expectedBehavior
     *            a string description
     * @param timeout
     *            the user defined timeout for message displaying
     * @return the result
     */
    public static ManualTestStepResult showDialog(String actionToPerform,
            String expectedBehavior, int timeout) {
        if (GraphicsEnvironment.isHeadless()) {
            return new ManualTestStepResult(
                    "Test step has been skipped due to headless running mode", //$NON-NLS-1$
                    false);
        }
        StringBuilder message = new StringBuilder();
        message.append("Manual action to perform: "); //$NON-NLS-1$
        message.append(LINE_BREAK);
        message.append(actionToPerform);
        message.append(LINE_BREAK).append(LINE_BREAK);
        message.append("Expected Behavior: "); //$NON-NLS-1$
        message.append(LINE_BREAK);
        message.append(expectedBehavior);
        message.append(LINE_BREAK).append(LINE_BREAK);
        message.append("Comment: "); //$NON-NLS-1$

        return showManualTestStepDialog(message, timeout);
    }

    /**
     * @param message
     *            the message to display for the user
     * @param timeout
     *            the user defined timeout for message displaying
     * @return the result
     */
    private static ManualTestStepResult showManualTestStepDialog(
            StringBuilder message, final int timeout) {
        Component parentComponent = new JFrame();
        int messageType = JOptionPane.QUESTION_MESSAGE;

        JOptionPane pane = new ManualTestStepOptionPane(message, messageType,
                OK_CANCEL_OPTION, null, DIALOG_EXIT_OPTIONS, null);

        pane.setWantsInput(true);
        pane.setComponentOrientation(((parentComponent == null) ? getRootFrame()
                        : parentComponent).getComponentOrientation());
        final JDialog dialog = pane.createDialog(DIALOG_TITLE);

        pane.selectInitialValue();

        Timer closeDialog = new Timer();
        closeDialog.schedule(new TimerTask() {
            public void run() {
                dialog.setVisible(false);
            }
        }, timeout);
        dialog.setVisible(true);
        closeDialog.cancel();

        Object inputValue = pane.getInputValue();
        Object value = pane.getValue();
        
        dialog.dispose();

        if (inputValue instanceof String && value instanceof StepStatus) {
            return new ManualTestStepResult((String)inputValue,
                    ((StepStatus)value) == StepStatus.Passed);
        }

        return new ManualTestStepResult("timeout occurred", false); //$NON-NLS-1$
    }
    
    /**
     * {@inheritDoc}
     */
    public int getMaxCharactersPerLineCount() {
        return MAX_CHAR_PER_LINE_COUNT;
    }
}
