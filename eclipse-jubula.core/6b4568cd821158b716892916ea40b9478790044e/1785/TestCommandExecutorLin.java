/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.tools.exec.tests.lin;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jubula.tools.exec.CommandExecutor;
import org.eclipse.jubula.tools.exec.CommandExecutor.Result;
import org.junit.Assert;
import org.junit.Test;

/** @author BREDEX GmbH */
@SuppressWarnings("nls") 
public class TestCommandExecutorLin {
    /** the default timeout per execution */
    public static final long DEFAULT_EXECUTION_TIMEOUT = 1000;
    /** the default encoding during test execution */
    public static final String UTF_8_ENCODING = "UTF-8";

    /**
     * Test method
     * @throws Exception
     */
    @Test
    public void testWorkingDirectory() throws Exception {
        String sysOut = CommandExecutor.exec(
              ".", 
              "pwd", 
              null, 
              ' ', 
              DEFAULT_EXECUTION_TIMEOUT,
              UTF_8_ENCODING,
              false).getSysOut().trim();
        
        Assert.assertEquals(System.getProperty("user.dir"), sysOut);
    }

    /**
     * Test method
     * @throws Exception
     */
    @Test
    public void testProcessEnvironmentInheritance() throws Exception {
        String sysOut = CommandExecutor.exec(
              ".", 
              "env", 
              null, 
              ' ', 
              DEFAULT_EXECUTION_TIMEOUT, 
              UTF_8_ENCODING,
              false).getSysOut();
        
        Assert.assertTrue(sysOut.matches("((.*?)\\n)+"));
    }

    /**
     * Test method
     * @throws Exception
     */
    @Test
    public void testNewProcessEnvironment() throws Exception {
        Result exec = CommandExecutor.exec(
              ".", 
              "env", 
              null, 
              ' ', 
              DEFAULT_EXECUTION_TIMEOUT, 
              UTF_8_ENCODING,
              true);
        
        String sysOut = exec.getSysOut();
        Assert.assertTrue(sysOut.matches("((.*?)\\n){0,1}"));
    }

    /**
     * Test method
     * @throws Exception
     */
    @Test
    public void testDefaultWorkingDirectory() throws Exception {
        String sysOut = CommandExecutor.exec(
              null, 
              "pwd", 
              null, 
              ' ', 
              DEFAULT_EXECUTION_TIMEOUT, 
              UTF_8_ENCODING,
              false).getSysOut().trim();
        
        Assert.assertEquals(System.getProperty("user.dir"), sysOut);
    }

    /**
     * Test method
     * @throws Exception
     */
    @Test
    public void testSysOut() throws Exception {
        String rawArguments = "abc";
        Result r = CommandExecutor.exec(
            ".", 
            "echo", 
            rawArguments, 
            ' ', 
            DEFAULT_EXECUTION_TIMEOUT, 
            UTF_8_ENCODING,
            false);
        
        String sysOut = r.getSysOut();
        Assert.assertTrue(sysOut.contains(rawArguments));

        String combinedOutput = r.getCombinedOutput();
        Assert.assertTrue(combinedOutput.contains(rawArguments));
        
        String sysErr = r.getSysErr();
        Assert.assertTrue(
                sysErr.trim().length() == 0);
    }

    /**
     * Test method
     * @throws Exception
     */
    @Test
    public void testCorrectEncodedProcessOutput() throws Exception {
        String rawArguments = "ä ü ö";
        String sysOut = CommandExecutor.exec(
            ".", 
            "echo", 
            rawArguments, 
            ' ', 
            DEFAULT_EXECUTION_TIMEOUT, 
            UTF_8_ENCODING,
            false).getSysOut();
        
        Assert.assertTrue(sysOut.contains(rawArguments));
    }

    /**
     * Test method
     * @throws Exception
     */
    @Test
    public void testReturnValue0() throws Exception {
        Result r = CommandExecutor.exec(
                ".", 
                "true", 
                null, 
                ' ', 
                DEFAULT_EXECUTION_TIMEOUT, 
                UTF_8_ENCODING,
                false);
        Assert.assertTrue(r.getReturnValue() == 0);
    }

    /**
     * Test method
     * @throws Exception
     */
    @Test
    public void testReturnValue1() throws Exception {
        Result r = CommandExecutor.exec(
                ".", 
                "false", 
                null, 
                ' ', 
                DEFAULT_EXECUTION_TIMEOUT, 
                UTF_8_ENCODING,
                false);

        Assert.assertTrue(r.getReturnValue() == 1);
    }

    /**
     * Test method
     * @throws Exception
     */
    @Test
    public void testLongRunningProcess() throws Exception {
        int secondsToWait = 10;
        long startTime = System.currentTimeMillis();
        Result result = CommandExecutor.exec(
                ".", 
                "sleep", 
                String.valueOf(secondsToWait), 
                ' ', 
                (secondsToWait * DEFAULT_EXECUTION_TIMEOUT) * 2, 
                UTF_8_ENCODING,
                false);
        
        Assert.assertTrue(result.getReturnValue() == 0);
        Assert.assertTrue(System.currentTimeMillis() 
            > (startTime + secondsToWait * 1000));
    }

    /**
     * Test method
     * @throws Exception
     */
    @Test(expected = TimeoutException.class)
    public void negativeTestTimeoutBehavior() throws Exception {
        int secondsToDelay = 10;
        CommandExecutor.exec(
                ".", 
                "sleep", 
                String.valueOf(secondsToDelay), 
                ' ', 
                (secondsToDelay * DEFAULT_EXECUTION_TIMEOUT) / 2, 
                UTF_8_ENCODING,
                false);
    }

    /**
     * Test method
     * @throws Exception
     */
    @Test
    public void testSynchronousExecutionAndNoTimeout() throws Exception {
        int secondsToDelay = 1;
        long startTime = System.currentTimeMillis();
        CommandExecutor.exec(
                ".", 
                "sleep", 
                String.valueOf(secondsToDelay), 
                ' ', 
                CommandExecutor.NO_TIMEOUT, 
                UTF_8_ENCODING,
                false);
        
        Assert.assertTrue(System.currentTimeMillis() 
            > (startTime + secondsToDelay * 1000));
    }

    /**
     * Test method
     * @throws Exception
     */
    @Test
    public void testMultipleArgumentPassing() throws Exception {
        char[] argSeparator = { ' ', ',', ';', '/' };
        for (char c : argSeparator) {
            int currArgCount = 1;
            String rawArguments = String.valueOf(1);
            do {
                String sysOut = CommandExecutor.exec(
                        ".", 
                        "expr", 
                        rawArguments, 
                        c, 
                        DEFAULT_EXECUTION_TIMEOUT, 
                        UTF_8_ENCODING,
                        false).getSysOut();
                Assert.assertTrue(
                    sysOut.contains(
                        String.valueOf(currArgCount)));
                rawArguments += c + "+" + c + String.valueOf(1);
                currArgCount++;
            } while (currArgCount <= 9);
        }
    }

    /**
     * Test method
     * @throws Exception
     */
    @Test
    public void testNoArgumentPassing() throws Exception {
        String sysOut = CommandExecutor.exec(
                ".", 
                "echo", 
                null, 
                ',', 
                DEFAULT_EXECUTION_TIMEOUT, 
                UTF_8_ENCODING,
                false).getSysOut();
        Assert.assertTrue(sysOut.length() == 1);
    }

    /**
     * Test method
     * @throws Exception
     */
    @Test(expected = IOException.class)
    public void negativeTestInvalidWorkingDirectory() throws Exception {
        CommandExecutor.exec(
                "/a/b/c/d/e/f/g/h/i", 
                "echo", 
                null, 
                ',', 
                DEFAULT_EXECUTION_TIMEOUT, 
                UTF_8_ENCODING,
                false).getSysOut();
    }

    /**
     * Test method
     * 
     * @throws Exception
     */
    @Test(expected = IOException.class)
    public void negativeTestInvalidExecutable() throws Exception {
        CommandExecutor.exec(
                ".", 
                "nonExistingCommandOrExecutable", 
                null, 
                ',', 
                DEFAULT_EXECUTION_TIMEOUT, 
                UTF_8_ENCODING,
                false).getSysErr();
    }

    /**
     * Test method
     * @throws Exception
     */
    @Test
    public void testSysErr() throws Exception {
        final int expectedExitCode = 2;
        Result result = CommandExecutor.exec(
                ".", 
                "expr", 
                null, 
                ',', 
                DEFAULT_EXECUTION_TIMEOUT, 
                UTF_8_ENCODING,
                false);
        
        Assert.assertTrue(
            expectedExitCode == result.getReturnValue());
        
        String sysErr = result.getSysErr();
        Assert.assertTrue(
                sysErr.length() > 1);
        
        String combinedOutput = result.getCombinedOutput();
        Assert.assertTrue(
                combinedOutput.length() > 1);
        
        String sysOut = result.getSysOut();
        Assert.assertTrue(
                sysOut.trim().length() == 0);
        
    }
    
    /**
     * Test for checking the order of outputs and error
     * @throws Exception
     */
//    @Test
    public void testOutputOrder() throws Exception {
        int numberRepeats = 1000;
        char splitCharacter = ',';
        Result result = CommandExecutor.exec(
                "./resources",
                "/bin/sh",
                "concurrentSysErrOutUsage.sh" + ',' + numberRepeats,
                splitCharacter,
                DEFAULT_EXECUTION_TIMEOUT,
                UTF_8_ENCODING,
                false);
        Matcher matcher = Pattern.compile("echo\\s(\\d+)\\necho error\\s\\1")
                .matcher(result.getCombinedOutput());
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        Assert.assertEquals(numberRepeats, count);
    }
}