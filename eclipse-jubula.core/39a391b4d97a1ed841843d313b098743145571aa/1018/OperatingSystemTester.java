/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.common.tester;

import java.io.IOException;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.exec.ExecuteException;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.interfaces.ITester;
import org.eclipse.jubula.toolkit.internal.CSConstants;
import org.eclipse.jubula.tools.exec.CommandExecutor;
import org.eclipse.jubula.tools.exec.CommandExecutor.Result;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

/**
 * Tester class for the Operating System
 * 
 * @author BREDEX GmbH
 */
public class OperatingSystemTester implements ITester {
    /** Constructor */
    public OperatingSystemTester() {
        // empty
    }
    
    /**
     * Not used because of default mapping
     * 
     * @param graphicsComponent
     *            not used
     */
    public void setComponent(Object graphicsComponent) {
        // Nothing here.
    }

    /**
     * Not used because of default mapping
     * 
     * @return null
     */
    public String[] getTextArrayFromComponent() {
        // Nothing here.
        return null;
    }

    /**
     * @param timeout
     *            the timeout
     * @param workingDir
     *            the workingDir
     * @param executable
     *            the executable
     * @param args
     *            the arguments
     * @param argSplitChar
     *            the argSplitChar
     * @param expectedExitCode
     *            the expected exit code
     * @param execEnvironment
     *            the execEnvironment
     * @param outputEncoding
     *            the outputEncoding
     * @param newEnvironment 
     *            do not use the old / parent environment
     * @return the sysout / syserr of the invoked command
     */
    public String rcExec(int timeout, 
            String workingDir, 
            String executable,
            @Nullable String args, 
            @Nullable String argSplitChar, 
            int expectedExitCode,
            String execEnvironment, 
            String outputEncoding,
            boolean newEnvironment) {
        if (CSConstants.EXEC_CONTEXT_AUT.equals(execEnvironment)) {
            return rcExec(timeout, 
                    workingDir, 
                    executable, 
                    args, 
                    argSplitChar, 
                    expectedExitCode, 
                    outputEncoding,
                    newEnvironment);
        }
        return null;
    }
    
    /**
     * @param timeout
     *            the timeout
     * @param workingDir
     *            the workingDir
     * @param executable
     *            the executable
     * @param args
     *            the arguments
     * @param argSplitChar
     *            the argSplitChar
     * @param expectedExitCode
     *            the expected exit code
     * @param outputEncoding
     *            the outputEncoding
     * @param newEnvironment 
     *            do not use the old / parent environment
     * @return the sysout / syserr of the invoked command
     */
    public String rcExec(int timeout, 
            String workingDir, 
            String executable,
            @Nullable String args, 
            @Nullable String argSplitChar, 
            int expectedExitCode,
            String outputEncoding,
            boolean newEnvironment) {
        try {
            char splitChar = argSplitChar != null 
                    ? argSplitChar.charAt(0) : null;
            Result result = CommandExecutor.exec(
                    workingDir, 
                    executable, 
                    args, 
                    splitChar, 
                    timeout, 
                    outputEncoding,
                    newEnvironment);
            
            int exitCode = result.getReturnValue();
            if (exitCode != expectedExitCode) {
                TestErrorEvent event = EventFactory.createVerifyFailed(
                        String.valueOf(expectedExitCode), 
                        String.valueOf(exitCode));
                event.addProp(
                        TestErrorEvent.Property.COMMAND_LOG_KEY, 
                        result.getCombinedOutput());
                throw new StepExecutionException(
                        "Verification of exit code failed.", //$NON-NLS-1$
                        event);
            }
            return result.getCombinedOutput();
        } catch (IllegalCharsetNameException e) {
            throw new StepExecutionException(e);
        } catch (UnsupportedCharsetException e) {
            throw new StepExecutionException(e);
        } catch (ExecuteException e) {
            throw new StepExecutionException(e);
        } catch (IOException e) {
            throw new StepExecutionException(
                "Command not found.", //$NON-NLS-1$
                EventFactory.createActionError(
                    TestErrorEvent.NO_SUCH_COMMAND));
        } catch (InterruptedException e) {
            throw new StepExecutionException(e);
        } catch (TimeoutException e) {
            TestErrorEvent event = EventFactory.createActionError(
                    TestErrorEvent.CONFIRMATION_TIMEOUT);
            event.addProp(TestErrorEvent.Property.COMMAND_LOG_KEY, 
                    e.getMessage());
            throw new StepExecutionException(
                "Timeout received before completing execution of script.", //$NON-NLS-1$
                event); 
        }
    }
}