/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogLabelKeys;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.exception.JBRuntimeException;
import org.eclipse.jubula.tools.internal.messagehandling.Message;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author BREDEX GmbH
 */
public class ErrorHandlingUtil {

    /** the logger */
    private static final Logger LOG = 
            LoggerFactory.getLogger(ErrorHandlingUtil.class);
    
    /** The error dialog */
    private static Dialog dlg = null;
    
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ErrorHandlingUtil() {
        // no-op
    }
    
    /**
     * Open the message dialog and logs the JBException.
     * @param ex the actual JBException
     * @return the dialog.
     */
    public static Dialog createMessageDialog(JBException ex) {
        return createMessageDialog(ex, null, null);
    }

    /**
     * Open the message dialog and logs the JBException.
     * @param ex the actual JBException
     * @param params Parameter of the message text or null, if not needed.
     * @param details use null, or overwrite in MessageIDs hardcoded details.
     * @return the dialog.
     */
    public static Dialog createMessageDialog(JBException ex, 
            Object[] params, String[] details) {
        
        Integer messageID = ex.getErrorId();
        Message m = MessageIDs.getMessageObject(messageID);
        if (m != null && m.getSeverity() == Message.ERROR) {
            LOG.error(Messages.AnErrorHasOccurred + StringConstants.DOT, ex);
        }
        return createMessageDialog(messageID, params, details);
    }

    /**
     * Open the message dialog.
     * <p><b>Use createMessageDialog(JBException ex, Object[] params, String[] details)
     * instead, if you want to get an entry in error log.</b></p>
     * @param messageID the actual messageID
     * @param params Parameter of the message text or null, if not needed.
     * @param details use null, or overwrite in MessageIDs hardcoded details.
     * @return the dialog.
     */
    public static Dialog createMessageDialog(final Integer messageID,
            final Object[] params, final String[] details) {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                if (!PlatformUI.getWorkbench().isClosing()) {
                    dlg = createMessageDialog(messageID, params, details, 
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                            .getShell());
                }
            }
        });
        return dlg;
    }

    /**
     * Open the message dialog.
     * 
     * @param messageID
     *            the actual messageID
     * @return the dialog.
     */
    public static Dialog createMessageDialog(final Integer messageID) {
        return createMessageDialog(messageID, null, null);
    }

    /**
     * Open the message dialog.
     * <p><b>Use createMessageDialog(JBException ex, Object[] params, String[] details)
     * instead, if you want to get an entry in error log.</b></p>
     * @param messageID the actual messageID
     * @param params Parameter of the message text or null, if not needed.
     * @param details use null, or overwrite in MessageIDs hardcoded details.
     * @param parent the parent shell to use for this message dialog
     * @return the dialog.
     */
    public static Dialog createMessageDialog(final Integer messageID, 
        final Object[] params, final String[] details, final Shell parent) {
        String title = StringConstants.EMPTY;
        String message = StringConstants.EMPTY;
        String[] labels = new String[] { 
                JFaceResources.getString(IDialogLabelKeys.OK_LABEL_KEY) };
        int imageID = MessageDialog.INFORMATION;
        Message msg = MessageIDs.getMessageObject(messageID);
        String[] detail = lineFeed(msg.getDetails());
        if (details != null) {
            detail = lineFeed(details);
        }
        switch (msg.getSeverity()) {
            case Message.ERROR:
                title = Messages.UtilsError;
                message = Messages.UtilsErrorOccurred;
                break;
            case Message.INFO:
                title = Messages.UtilsInfo1;
                message = Messages.UtilsInfo2;
                break;
            case Message.WARNING:
                title = Messages.UtilsWarning1;
                message = Messages.UtilsWarning2;
                break;
            case Message.QUESTION:
                title = Messages.UtilsRequest1;
                message = Messages.UtilsRequest2;
                labels = new String[] {
                    JFaceResources.getString(IDialogLabelKeys.YES_LABEL_KEY),
                    JFaceResources.getString(IDialogLabelKeys.NO_LABEL_KEY) };
                imageID = MessageDialog.QUESTION;
                break;
            default:
                break;
        }
        IStatus[] status = new Status[detail.length];
        for (int i = 0; i < detail.length; i++) {
            status[i] = new Status(msg.getSeverity(), Constants.PLUGIN_ID,
                    IStatus.OK, detail[i], null);
        }
        if ((msg.getSeverity() == Message.INFO 
                || msg.getSeverity() == Message.QUESTION)) {
            StringBuilder messageBuilder = new StringBuilder(message);
            messageBuilder.append(msg.getMessage(params));
            messageBuilder.append(StringConstants.NEWLINE);
            for (IStatus s : status) {
                if (s.getMessage() != Message.NO_DETAILS) {
                    messageBuilder.append(StringConstants.NEWLINE);
                    messageBuilder.append(s.getMessage());
                }
            }
            dlg = new MessageDialog(parent, title, null, messageBuilder
                    .toString(), imageID, labels, 0);
        } else {
            dlg = new ErrorDialog(parent, title,
                    message, new MultiStatus(Constants.PLUGIN_ID, IStatus.OK,
                            status, msg.getMessage(params), null), IStatus.OK
                            | IStatus.INFO | IStatus.WARNING | IStatus.ERROR);
        }
        dlg.create();
        DialogUtils.setWidgetNameForModalDialog(dlg);
        dlg.open();
        return dlg;
    }

    /**
     * Open the message dialog and logs the JBException.
     * @param ex the actual JBRuntimeException
     * @return the dialog.
     */
    public static Dialog createMessageDialog(JBRuntimeException ex) {
        Integer messageID = ex.getErrorId();
        Message m = MessageIDs.getMessageObject(messageID);
        if (m != null && m.getSeverity() == Message.ERROR) {
            LOG.error(Messages.AnErrorHasOccurred + StringConstants.DOT, ex);
        }
        return createMessageDialog(messageID, null,
                getStackTrace(ex.getCausedBy()));
    }
    
    /**
     * Open the message dialog and logs the JBException.
     * @param ex the actual JBRuntimeException
     * @return the dialog.
     */
    public static Dialog createMessageDialogException(Throwable ex) {
        Integer messageID = MessageIDs.E_OP_FAILED;
        Message m = MessageIDs.getMessageObject(messageID);
        if (m != null && m.getSeverity() == Message.ERROR) {
            LOG.error(Messages.AnErrorHasOccurred + StringConstants.DOT, ex);
        }
        return createMessageDialog(messageID, null,
                getStackTrace(ex));
    }
    
    /**
     * @param throwable the throwable to get the stack trace from
     * @return the stack trace as a string
     */
    public static String[] getStackTrace(Throwable throwable) {
        if (throwable != null) {
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            throwable.printStackTrace(printWriter);
            return writer.toString().split(StringConstants.NEWLINE);
        }
        return null;
    }
    
    /**
     * Line-feeds the given String array at \n
     * @param strArray the string to line feed
     * @return the line feeded strings as an array
     */
    private static String[] lineFeed(String[] strArray) {
        List<String> strList = new ArrayList<String>();
        for (String str : strArray) {
            if (str != null) {
                StringTokenizer tok = new StringTokenizer(str, 
                        StringConstants.NEWLINE);
                while (tok.hasMoreElements()) {
                    strList.add(tok.nextToken());
                }
            }
        }
        return strList.toArray(new String[strList.size()]);
    }
    
}
