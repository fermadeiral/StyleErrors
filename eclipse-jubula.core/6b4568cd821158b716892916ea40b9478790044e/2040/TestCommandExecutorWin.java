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
package org.eclipse.jubula.tools.exec.tests.win;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jubula.tools.exec.CommandExecutor;
import org.eclipse.jubula.tools.exec.CommandExecutor.Result;
import org.junit.Assert;
import org.junit.Test;

/** @author BREDEX GmbH */
@SuppressWarnings("nls") 
public class TestCommandExecutorWin {
    /** the default timeout per execution */
    public static final long DEFAULT_EXECUTION_TIMEOUT = 1000;
    /** the default encoding during test execution */
    public static final String UTF_8_ENCODING = "UTF-8";

    /**
     * Test method
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCommand() throws Exception {
        CommandExecutor.exec(".", 
                null, 
                null, 
                ' ', 
                DEFAULT_EXECUTION_TIMEOUT,
                UTF_8_ENCODING,
                false);
    }
    
    /**
     * Test method
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeTimeout() throws Exception {
        int secondsToDelay = 10;
        CommandExecutor.exec(
                ".", 
                "ping", 
                "127.0.0.1 -n " + String.valueOf(secondsToDelay + 1), 
                ' ', 
                -5000, 
                UTF_8_ENCODING,
                false);
    }
    
    /**
     * Test method
     * @throws Exception
     */
    @Test
    public void testWorkingDirectory() throws Exception {
        String sysOut = CommandExecutor.exec(
                ".", 
                "cd", 
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
    public void testDefaultWorkingDirectory() throws Exception {
        String sysOut = CommandExecutor.exec(
              null, 
              "echo", 
              "%cd%", 
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
            "Cp850",
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
                "ver", 
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
                "ver", 
                "/invalidArgument", 
                ' ', 
                DEFAULT_EXECUTION_TIMEOUT, 
                UTF_8_ENCODING,
                false);

        Assert.assertTrue(r.getReturnValue() > 0);
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
                "ping", 
                "127.0.0.1 -n " + String.valueOf(secondsToWait + 1), 
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
    public void testNegativeTestTimeoutBehavior() throws Exception {
        int secondsToDelay = 5;
        CommandExecutor.exec(
                ".", 
                "ping", 
                "127.0.0.1 -n " + String.valueOf(secondsToDelay + 1), 
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
        int secondsToDelay = 2;
        long startTime = System.currentTimeMillis();
        Result result = CommandExecutor.exec(
                ".", 
                "ping", 
                "127.0.0.1 -n " + String.valueOf(secondsToDelay + 1), 
                ' ', 
                CommandExecutor.NO_TIMEOUT, 
                UTF_8_ENCODING,
                false);
        
        Assert.assertTrue(result.getReturnValue() == 0);
        Assert.assertTrue(System.currentTimeMillis() 
            > (startTime + secondsToDelay * 1000));
    }

    /**
     * Test method
     * @throws Exception
     */
    @Test
    public void testMultipleArgumentPassing() throws Exception {
        char[] argSeparator = { ' ', ',', ';', '-'};
        for (char c : argSeparator) {
            int currArgCount = 1;
            String rawArguments = "/a" + c + String.valueOf(1);
            do {
                String sysOut = CommandExecutor.exec(
                        ".", 
                        "set", 
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
        Assert.assertTrue(sysOut.toLowerCase().contains("echo is on."));
    }

    /**
     * Test method
     * @throws Exception
     */
    @Test(expected = IOException.class)
    public void testNegativeTestInvalidWorkingDirectory() throws Exception {
        CommandExecutor.exec(
                "/a/b/c/d/e/f/g/h/i", 
                "cd", 
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
    public void testNegativeTestInvalidExecutable() throws Exception {
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
        Result result = CommandExecutor.exec(
                ".", 
                "set", 
                "/a", 
                ' ', 
                DEFAULT_EXECUTION_TIMEOUT, 
                UTF_8_ENCODING,
                false);
        
        Assert.assertTrue(0 != result.getReturnValue());
        
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
}