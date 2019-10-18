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
package org.eclipse.jubula.rc.common.implclasses;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.KeyTyper;
import org.eclipse.jubula.rc.common.exception.ExecutionEvent;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.tester.interfaces.ITester;
import org.eclipse.jubula.rc.common.util.Comparer;
import org.eclipse.jubula.rc.common.util.KeyStrokeUtil;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.internal.utils.ExternalCommandExecutor;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;
import org.eclipse.jubula.tools.internal.utils.ExternalCommandExecutor.MonitorTask;


/**
 * @author BREDEX GmbH
 * @created Dec 19, 2006
 */
public abstract class AbstractApplicationImplClass implements ITester {
    /**
     * String for sequential numbering for screenshots
     */
    public static final String RENAME = "rename"; //$NON-NLS-1$
    
    /**
     * String for overwriting for screenshots
     */
    public static final String OVERWRITE = "overwrite"; //$NON-NLS-1$

    /**
     * The default format to use when writing images to disk.
     */
    private static final String DEFAULT_IMAGE_FORMAT = "png"; //$NON-NLS-1$
    
    /**
     * The string used to separate filename and file extension.
     */
    private static final String EXTENSION_SEPARATOR = "."; //$NON-NLS-1$
    
    /**
     * The logging.
     */
    private static AutServerLogger log = 
        new AutServerLogger(AbstractApplicationImplClass.class);


    /**
     * Executes the given command and waits for it to finish. If the 
     * execution does not finish in good time, a timeout will occur. If the 
     * exit code for the execution is not the same as the expected code, the 
     * test step fails.
     * 
     * @param cmd The command to execute.
     * @param expectedExitCode The expected exit code of the command.
     * @param local <code>true</code> if the command should be executed on the
     *        local (client) machine. Otherwise (should run on the server side),
     *        this value should be <code>false</code>.
     * @param timeout The amount of time (in milliseconds) to wait for the 
     *        execution to finish.
     */
    public void rcExecuteExternalCommand(String cmd, int expectedExitCode, 
        boolean local, int timeout) {

        if (!local) {
            MonitorTask mt = new ExternalCommandExecutor().executeCommand(
                null, cmd, timeout);

            if (!mt.wasCmdValid()) {
                throw new StepExecutionException(
                    "Command not found.", //$NON-NLS-1$
                    EventFactory.createActionError(
                        TestErrorEvent.NO_SUCH_COMMAND));
            }
            
            if (mt.hasTimeoutOccurred()) {
                throw new StepExecutionException(
                    "Timeout received before completing execution of script.", //$NON-NLS-1$
                    EventFactory.createActionError(
                        TestErrorEvent.CONFIRMATION_TIMEOUT));
            }
            
            int actualExitValue = mt.getExitCode();
            if (actualExitValue != expectedExitCode) {
                throw new StepExecutionException(
                    "Verification of exit code failed.", //$NON-NLS-1$
                    EventFactory.createVerifyFailed(
                        String.valueOf(expectedExitCode), 
                        String.valueOf(actualExitValue)));
            }
        } 
        
    }
    
    /**
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        // Do nothing; Application has no corresponding component
    }

    /**
     * Takes a screenshot and saves the image to disk.
     * 
     * @param destination
     *          Path and filename for the created image. If the extension is not
     *          ".jpeg" (case-insensitive), ".jpeg" will be appended to the 
     *          filename.
     * @param delay
     *          Amount of time to wait (in milliseconds) before taking the
     *          screenshot.
     * @param fileAccess
     *          Determines how the file will be created if a file with the
     *          given name and path already exists:<br>
     *          <code>SwingApplicationImplClass.RENAME</code> -
     *          The screenshot will be saved with a sequential integer appended 
     *          to the filename.<br>
     *          <code>SwingApplicationImplClass.OVERWRITE</code> -
     *          The screenshot will overwrite the file.
     * @param scaling
     *          Degree to which the image should be scaled, in percent. A
     *          <code>scaling</code> value of <code>100</code> produces an
     *          unscaled image. This value must be greater than <code>0</code>
     *          and less than or equal to <code>200</code>.
     * @param createDirs
     *          Determines whether a path will be created if it does not already
     *          exist. A value of <code>true</code> means that all necessary 
     *          directories that do not exist will be created automatically.
     */
    public void rcTakeScreenshot(String destination, int delay,
            String fileAccess, int scaling, boolean createDirs) {
        // Determine current screen size
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();

        // If screen !(resolution%2==0) --> bad scaling
        int screenWidth = (int)screenSize.getWidth();
        int screenHeight = (int)screenSize.getHeight();
        if (!(screenWidth % 2 == 0)) {
            screenWidth = screenWidth - 1;
        }
        if (!(screenHeight % 2 == 0)) {
            screenHeight = screenHeight - 1;
        }

        screenSize.setSize(screenWidth, screenHeight);

        Rectangle screenRect = new Rectangle(screenSize);

        takeScreenshot(destination, delay, fileAccess, scaling, createDirs,
                screenRect);
    }

    /**
     * Takes a screenshot and saves the image to disk.
     * 
     * @param destination
     *            Path and filename for the created image. If the extension is
     *            not ".jpeg" (case-insensitive), ".jpeg" will be appended to
     *            the filename.
     * @param delay
     *            Amount of time to wait (in milliseconds) before taking the
     *            screenshot.
     * @param fileAccess
     *            Determines how the file will be created if a file with the
     *            given name and path already exists:<br>
     *            <code>SwingApplicationImplClass.RENAME</code> - The screenshot
     *            will be saved with a sequential integer appended to the
     *            filename.<br>
     *            <code>SwingApplicationImplClass.OVERWRITE</code> - The
     *            screenshot will overwrite the file.
     * @param scaling
     *            Degree to which the image should be scaled, in percent. A
     *            <code>scaling</code> value of <code>100</code> produces an
     *            unscaled image. This value must be greater than <code>0</code>
     *            and less than or equal to <code>200</code>.
     * @param createDirs
     *            Determines whether a path will be created if it does not
     *            already exist. A value of <code>true</code> means that all
     *            necessary directories that do not exist will be created
     *            automatically.
     * @param marginTop
     *            the extra top margin
     * @param marginRight
     *            the extra right margin
     * @param marginBottom
     *            the extra bottom margin
     * @param marginLeft
     *            the extra left margin
     */
    public void rcTakeScreenshotOfActiveWindow(String destination, int delay,
            String fileAccess, int scaling, boolean createDirs, int marginTop,
            int marginRight, int marginBottom, int marginLeft) {
        Rectangle activeWindowBounds = getActiveWindowBounds();
        
        if (activeWindowBounds == null) {
            throw new StepExecutionException("No active window found", //$NON-NLS-1$
                    EventFactory
                        .createActionError(TestErrorEvent.NO_ACTIVE_WINDOW));
        }
        
        int x = activeWindowBounds.x - marginLeft;
        int y = activeWindowBounds.y - marginTop;
        int width = activeWindowBounds.width + marginLeft + marginRight;
        int height = activeWindowBounds.height + marginTop + marginBottom;
        
        if (width < 1 || height < 1) {
            throw new StepExecutionException("Margin parameter lead to negative height or width", //$NON-NLS-1$
                    EventFactory
                        .createActionError(TestErrorEvent.INVALID_INPUT));
        }
        
        Rectangle screenRect = new Rectangle(x, y, width, height);

        takeScreenshot(destination, delay, fileAccess, scaling, createDirs,
                screenRect);
    }

    /**
     * @return an awt rectangle which represents the absolute active window
     *         bounds; may return null e.g. if no active window could be found
     */
    public abstract Rectangle getActiveWindowBounds();

    
    /**
     * Takes a screenshot and saves the image to disk, in JPEG format.
     * 
     * @param destination
     *          Path and filename for the created image. If the extension is not
     *          ".jpeg" (case-insensitive), ".jpeg" will be appended to the 
     *          filename.
     * @param delay
     *          Amount of time to wait (in milliseconds) before taking the
     *          screenshot.
     * @param fileAccess
     *          Determines how the file will be created if a file with the
     *          given name and path already exists:<br>
     *          <code>SwingApplicationImplClass.RENAME</code> -
     *          The screenshot will be saved with a sequential integer appended 
     *          to the filename.<br>
     *          <code>SwingApplicationImplClass.OVERWRITE</code> -
     *          The screenshot will overwrite the file.
     * @param scaling
     *          Degree to which the image should be scaled, in percent. A
     *          <code>scaling</code> value of <code>100</code> produces an
     *          unscaled image. This value must be greater than <code>0</code>
     *          and less than or equal to <code>200</code>.
     * @param createDirs
     *          Determines whether a path will be created if it does not already
     *          exist. A value of <code>true</code> means that all necessary 
     *          directories that do not exist will be created automatically.
     * @param screenShotRect 
     *          the rectangle to take the screenshot of
     */
    private void takeScreenshot(String destination, int delay,
            String fileAccess, int scaling, boolean createDirs,
            Rectangle screenShotRect) {
        if (scaling <= 0 || scaling > 200) {
            throw new StepExecutionException(
                    "Invalid scaling factor: Must be between 1 and 200", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.INVALID_PARAM_VALUE));
        }
        
        double scaleFactor = scaling * 0.01;

        // Check if file name is valid
        String outFileName = destination;
        String imageExtension = getExtension(outFileName);
        if (imageExtension.length() == 0) {
            // If not, then we simply append the default extension
            imageExtension = DEFAULT_IMAGE_FORMAT;
            outFileName += EXTENSION_SEPARATOR + imageExtension;
        }

        // Wait for a user-specified time
        if (delay > 0) {
            TimeUtil.delay(delay);
        }

        // Create path, if necessary
        File pic = new File(outFileName);
        if (pic.getParent() == null) {
            throw new StepExecutionException(
                    "Invalid file name: specify a file name", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.INVALID_PARAM_VALUE));
        }
        
        File path = new File(pic.getParent());
        if (createDirs && !path.exists() && !path.mkdirs()) {
            throw new StepExecutionException(
                "Directory path does not exist and could not be created", //$NON-NLS-1$
                EventFactory.createActionError(
                    TestErrorEvent.FILE_IO_ERROR));
        }

        // Rename file if file already exists
        // FIXME zeb This naming scheme can lead to sorting problems when 
        //           filenames have varying numbers of digits (ex. "pic_9" and 
        //           "pic_10")
        if (fileAccess.equals(RENAME)) {
            String completeExtension = 
                EXTENSION_SEPARATOR + imageExtension.toLowerCase();
            int extensionIndex = 
                pic.getName().toLowerCase().lastIndexOf(completeExtension);
            String fileName = pic.getName().substring(0, extensionIndex);
            for (int i = 1; pic.exists(); i++) {
                pic = new File(pic.getParent(), fileName + "_" + i + completeExtension); //$NON-NLS-1$
            }
        }
        
        takeScreenshot(screenShotRect, scaleFactor, pic);
    }

    /**
     * Takes a screenshot and saves the image to disk. This method will attempt
     * to encode the image according to the file extension of the given
     * output file. If this is not possible (because the encoding type
     * is not supported), then the default encoding type will be used. If
     * the default encoding type is used, an appropriate extension will be added
     * to the filename.
     * 
     * @param captureRect
     *          Rect to capture in screen coordinates.
     * @param scaleFactor
     *          Degree to which the image should be scaled, in percent. A
     *          <code>scaleFactor</code> of <code>100</code> produces an
     *          unscaled image. This value must be greater than <code>0</code>
     *          and less than or equal to <code>200</code>.
     * @param outputFile
     *          Path and filename for the created image.
     */
    public void takeScreenshot(
            Rectangle captureRect, double scaleFactor, File outputFile) {
        // Create screenshot
        java.awt.Robot robot;
        File out = outputFile;
        
        try {
            robot = new java.awt.Robot();
            BufferedImage image = robot.createScreenCapture(captureRect);

            int scaledWidth = (int) Math.floor(image.getWidth() * scaleFactor);
            int scaledHeight = 
                (int) Math.floor(image.getHeight() * scaleFactor);
            BufferedImage imageOut = 
                new BufferedImage(scaledWidth,
                    scaledHeight, BufferedImage.TYPE_INT_RGB);
            // Scale it to the new size on-the-fly
            Graphics2D graphics2D = imageOut.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            graphics2D.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);

            // Save captured image using given format, if supported.
            String extension = getExtension(out.getName());
            if (extension.length() == 0
                    || !ImageIO.getImageWritersBySuffix(extension).hasNext()
                    || !ImageIO.write(imageOut, extension, out)) {
                
                // Otherwise, save using default format
                out = new File(outputFile.getPath() 
                        + EXTENSION_SEPARATOR + DEFAULT_IMAGE_FORMAT);
                if (!ImageIO.write(imageOut, DEFAULT_IMAGE_FORMAT, out)) {
                    
                    // This should never happen, so log as error if it does.
                    // In this situation, the screenshot will not be saved, but
                    // the test step will still be marked as successful.
                    log.error("Screenshot could not be saved. " + //$NON-NLS-1$
                            "Default image format (" + DEFAULT_IMAGE_FORMAT  //$NON-NLS-1$
                            + ") is not supported."); //$NON-NLS-1$
                }
            }
            
        } catch (AWTException e) {
            throw new RobotException(e);
        } catch (IOException e) {
            throw new StepExecutionException(
                    "Screenshot could not be saved", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.FILE_IO_ERROR));
        }
    }
    
    /**
     * Waits a specified time. 
     * @param timeMilliSec the time to wait in MilliSec
     */
    public void rcWait(int timeMilliSec) {
        TimeUtil.delay(timeMilliSec);
    }

    /**
     * shows a ConfirmDialog and Pause the Execution of the Test until Window 
     * is closed
     */
    public void rcPause() {
        throw new ExecutionEvent(ExecutionEvent.PAUSE_EXECUTION);
    }

    /**
     * Does nothing! The restart is implemented in the client but the server
     * must have an action to execute.
     */
    public void rcRestart() {
        // nothing
    }
    
    /**
     * Types the given text without checking location or event confirmation.
     * 
     * @param text The text to type.
     */
    public void rcNativeInputText(String text) {
        try {
            KeyTyper.getInstance().nativeTypeString(text);
        } catch (AWTException e) {
            throw new RobotException(e);
        }
    }

    /**
     * Action to perform a manual test step on server side; opens a window and
     * wait's for real user interaction
     * 
     * @param actionToPerform
     *            a textual description of the action to perform in the AUT
     * @param expectedBehavior
     *            a textual description of the expected behaviour
     * @param timeout
     *            the timeout
     */
    public void rcManualTestStep(String actionToPerform, 
            String expectedBehavior, int timeout) {
    // empty implementation: implementation can be found in the corresponding
    // postExecutionCommand
    }
    
    /**
     * Perform a keystroke specified according <a
     * href=http://java.sun.com/j2se/1.4.2/docs/api/javax/swing/KeyStroke.html#getKeyStroke(java.lang.String)>
     * string representation of a keystroke </a>.
     * This method does not wait for event confirmation, as we have no way of 
     * confirming events on OS-native widgets.
     * 
     * @param modifierSpec the string representation of the modifiers
     * @param keySpec the string representation of the key
     */
    public void rcNativeKeyStroke(String modifierSpec, String keySpec) {        
        if (keySpec == null || keySpec.trim().length() == 0) {
            throw new StepExecutionException("The base key of the key stroke " //$NON-NLS-1$
                + "must not be null or empty", //$NON-NLS-1$
                EventFactory.createActionError());
        }

        try {
        
            KeyTyper typer = KeyTyper.getInstance();
            String keyStrokeSpec = keySpec.trim().toUpperCase();
            String mod = KeyStrokeUtil.getModifierString(modifierSpec);
            if (mod.length() > 0) {
                keyStrokeSpec = mod + " " + keyStrokeSpec; //$NON-NLS-1$
            }
            
            typer.type(keyStrokeSpec, null, null, null);

        } catch (AWTException e) {
            throw new RobotException(e);
        }
        
    }

    /**
     * Action to set the value of a variable in the Client.
     * 
     * @param variable The name of the variable.
     * @param value The new value for the variable.
     * @return the new value for the variable.
     */
    public String rcSetValue(String variable, String value) {
        return value;
    }
    
    /**
     * @return The Robot instance
     */
    protected abstract IRobot getRobot();

    /**
     * 
     * @param filename A filename for which to find the extension.
     * @return the file extension for the given filename. For example,
     *         "png" for "example.png". Returns an empty string if the given
     *         name has no extension. This is the case if the given name does
     *         not contain an instance of the extension separator or ends with
     *         the extension separator. For example, "example" or "example.".
     */
    private String getExtension(String filename) {
        File file = new File(filename);
        int extensionIndex = file.getName().lastIndexOf(EXTENSION_SEPARATOR);
        return extensionIndex == -1 
                    || extensionIndex == file.getName().length() - 1 
                ? StringConstants.EMPTY
                : file.getName().substring(extensionIndex + 1); 
    }
    
    /**
     * method to copy a string to the system clipboard
     * 
     * @param text The text to copy
     */
    public void rcCopyToClipboard(final String text) {
        StringSelection strSel = new StringSelection(text);
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(strSel, null);
        } catch (IllegalStateException ise) {
            throw new StepExecutionException(
                    "Clipboard not available.", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.CLIPBOARD_NOT_AVAILABLE));
        } catch (HeadlessException he) {
            throw new StepExecutionException(
                    "Clipboard not available.", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.CLIPBOARD_NOT_AVAILABLE));
        }
    }
    
    /**
     * method to compare a string to the system clipboard
     * 
     * @param operator
     *            the comparison method
     * @param text
     *            the text for comparison
     */
    public void rcCheckClipboard(final String operator, final String text) {
        String content = null;
        try {
            content = (String) Toolkit.getDefaultToolkit().getSystemClipboard()
                    .getData(DataFlavor.stringFlavor);
        } catch (IllegalStateException ise) {
            throw new StepExecutionException(
                    "Clipboard not available.", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.CLIPBOARD_NOT_AVAILABLE));
        } catch (HeadlessException he) {
            throw new StepExecutionException(
                    "Clipboard not available.", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.CLIPBOARD_NOT_AVAILABLE));
        } catch (UnsupportedFlavorException ufe) {
            throw new StepExecutionException(
                    "Unsupported Clipboard content.", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.CLIPBOARD_UNSUPPORTED_FLAVOR));
        } catch (IOException ioe) {
            throw new StepExecutionException(
                    "Clipboard could not be compared.", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.CLIPBOARD_IO_ERROR));
        }
        Verifier.match(content, text, operator);
    }
    
    /**
     * method to compare to values
     * 
     * @param value1
     *            the first value for comparison
     * @param comparisonMethod
     *            the comparison method
     * @param value2
     *            the second value for comparison
     */
    public void rcCheckValues(final String value1,
            final String comparisonMethod, final String value2) {
        Comparer.compare(value1, value2, comparisonMethod);
    }
    
    /**
     * method to compare to strings
     * 
     * @param value1
     *            the first value for comparison
     * @param operator
     *            the comparison method
     * @param value2
     *            the second value for comparison
     */
    public void rcCheckStringValues(final String value1,
            final String operator, final String value2) {
        Verifier.match(value1, value2, operator);
    }

    /**
     * Does nothing! The start timer is implemented in the client but the server
     * must have an action to execute.
     * @param timerName the name for the timer
     * @param variableName the variable name to store the current time in millisecs in
     */
    public void rcStartTimer(String timerName, String variableName) {
    // empty
    }

    /**
     * Does nothing! The read timer is implemented in the client but the server
     * must have an action to execute.
     * @param timerName the name for the timer
     * @param variableName the variable name to store the current time delta in millisecs in
     */
    public void rcReadTimer(String timerName, String variableName) {
    // empty
    }
}