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
package org.eclipse.jubula.tools.exec;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.OS;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.environment.EnvironmentUtils;
import org.apache.commons.exec.launcher.CommandLauncher;
import org.apache.commons.exec.launcher.Java13CommandLauncher;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

/** @author BREDEX GmbH */
public class CommandExecutor {
    /** The marker for an infinite timeout / no timeout for executing the command */
    public static final long NO_TIMEOUT = ExecuteWatchdog.INFINITE_TIMEOUT;
    
    /**  */
    private static class WindowsCommandLauncher extends Java13CommandLauncher {
        @Override
        public Process exec(CommandLine cmd, Map environment, File workingDir)
                throws IOException {
            if (workingDir == null) {
                return exec(cmd, environment);
            }
            // Use cmd.exe from windows to have all tools available
            CommandLine windowsCommand = new CommandLine("cmd"); //$NON-NLS-1$
            windowsCommand.addArgument("/c"); //$NON-NLS-1$
            windowsCommand.addArguments(cmd.toStrings());

            return super.exec(windowsCommand, environment, workingDir);
        }
    }
    /** @author BREDEX GmbH */
    private static class ExtendedDefaultExecutor extends DefaultExecutor {
        /** launcher used for windows OS family types */
        private CommandLauncher m_windowsCommandLauncher = 
                new WindowsCommandLauncher();

        @Override
        protected Process launch(CommandLine command, 
                Map env, File dir) throws IOException {
            String cmdWorkingDirAbsolutePath = FilenameUtils
                    .getFullPathNoEndSeparator(dir.getAbsolutePath());
            String executable = command.getExecutable();
            File commandFile = new File(dir.getCanonicalFile(), executable);
            boolean doesCommandExist = commandFile.exists();
            if (OS.isFamilyWindows()) {
                return m_windowsCommandLauncher.exec(command, env, dir);
            }
            return super.launch(command, env, dir);
        }
    }
    
    /** @author BREDEX GmbH */
    public static class Result {
        /** the process return value*/
        private Integer m_returnValue = null;
        /** the encoding to use */
        private String m_encoding;
        /** the output stream */
        private ByteArrayOutputStream m_outStream;
        /** the error stream */
        private ByteArrayOutputStream m_errStream;
        /** the combined output stream */
        private ByteArrayOutputStream m_combinedStream;

        /**
         * @param encoding
         *            the encoding
         * @param outStream
         *            the output stream
         * @param errStream
         *            the error stream
         * @param combinedStream 
         *            the combined stream
         */
        public Result(String encoding, 
                ByteArrayOutputStream outStream,
                ByteArrayOutputStream errStream, 
                ByteArrayOutputStream combinedStream) {
            
            setEncoding(encoding);
            setOutStream(outStream);
            setErrStream(errStream);
            setCombinedStream(combinedStream);
        }

        /**
         * @return the returnValue
         */
        public Integer getReturnValue() {
            return m_returnValue;
        }

        /**
         * @param returnValue the returnValue to set
         */
        public void setReturnValue(Integer returnValue) {
            m_returnValue = returnValue;
        }

        /**
         * @return the sysOut
         * @throws UnsupportedEncodingException
         */
        public String getSysOut() throws UnsupportedEncodingException {
            return getOutStream().toString(getEncoding());
        }

        /**
         * @return the combined output
         * @throws UnsupportedEncodingException
         */
        public String getCombinedOutput() throws UnsupportedEncodingException {
            return getCombinedStream().toString(getEncoding());
        }

        /**
         * @return the sysErr
         * @throws UnsupportedEncodingException
         */
        public String getSysErr() throws UnsupportedEncodingException {
            return getErrStream().toString(getEncoding());
        }

        /**
         * @return the encoding
         */
        public String getEncoding() {
            return m_encoding;
        }

        /**
         * @param encoding the encoding to set
         */
        private void setEncoding(String encoding) {
            m_encoding = encoding;
        }

        /**
         * @return the outStream
         */
        public ByteArrayOutputStream getOutStream() {
            return m_outStream;
        }

        /**
         * @param outStream the outStream to set
         */
        private void setOutStream(ByteArrayOutputStream outStream) {
            m_outStream = outStream;
        }

        /**
         * @return the errStream
         */
        public ByteArrayOutputStream getErrStream() {
            return m_errStream;
        }

        /**
         * @param errStream the errStream to set
         */
        private void setErrStream(ByteArrayOutputStream errStream) {
            m_errStream = errStream;
        }

        /**
         * @return the combinedStream
         */
        public ByteArrayOutputStream getCombinedStream() {
            return m_combinedStream;
        }

        /**
         * @param combinedStream the combinedStream to set
         */
        private void setCombinedStream(ByteArrayOutputStream combinedStream) {
            m_combinedStream = combinedStream;
        }
    }
    
    /** Constructor */
    private CommandExecutor() {
        // hide
    }

    /**
     * @param workingDir
     *            the working directory to use
     * @param executable
     *            the executable
     * @param rawArguments
     *            the raw, still concatenated arguments string
     * @param splitCharacter
     *            the
     *            {@link org.apache.commons.lang.StringUtils#split(String, char)
     *            split} character for the given arguments string
     * @param timeout
     *            the timeout to use
     * @param encoding
     *            the output encoding
     * @param newEnvironment 
     *            do not use the old / parent environment; defaults to false
     * @return the combined process out and error stream content
     * @throws IOException
     * @throws ExecuteException
     * @throws InterruptedException
     * @throws TimeoutException
     * @throws UnsupportedEncodingException
     */
    public static Result exec(
            @Nullable final String workingDir, 
            final String executable, 
            @Nullable final String rawArguments, 
            @Nullable final Character splitCharacter, 
            final long timeout, 
            @Nullable final String encoding,
            @Nullable final Boolean newEnvironment)
    
        throws ExecuteException, 
        IOException, 
        InterruptedException, 
        TimeoutException,
        IllegalCharsetNameException,
        UnsupportedCharsetException {

        String outputEncoding = encoding;
        if (outputEncoding == null) {
            outputEncoding = Charset.defaultCharset().name();
        } else {
            boolean supported = Charset.isSupported(encoding);
            if (!supported) {
                throw new UnsupportedCharsetException(encoding);
            }
        }
        
        CommandLine cmdLine = new CommandLine(executable);
        String[] arguments;
        if (splitCharacter != null) {
            arguments = StringUtils.split(rawArguments, splitCharacter);
        } else {
            if (StringUtils.isNotEmpty(rawArguments)) {
                arguments = new String[] { rawArguments };
            } else {
                arguments = null;
            }
        }
        
        if (arguments != null) {
            for (String argument : arguments) {
                cmdLine.addArgument(argument, true);
            }
        }

        Executor executor = new ExtendedDefaultExecutor();
        executor.setWorkingDirectory(
                new File(StringUtils.isNotBlank(workingDir) 
                        ? workingDir  : ".")); //$NON-NLS-1$
        
        ByteArrayOutputStream combinedStream = new ByteArrayOutputStream();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        TeeOutputStream outStreamTee = new TeeOutputStream(
                outStream, combinedStream);
        ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        TeeOutputStream errStreamTee = new TeeOutputStream(
                errStream, combinedStream);
        
        PumpStreamHandler pumpStreamHandler = 
                new PumpStreamHandler(
                        outStreamTee,
                        errStreamTee);
        executor.setStreamHandler(pumpStreamHandler);
        
        ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
        executor.setWatchdog(watchdog);

        DefaultExecuteResultHandler resultHandler = 
                new DefaultExecuteResultHandler();
        
        Result r = new Result(encoding, outStream, errStream, combinedStream);
        
        Map environment = EnvironmentUtils.getProcEnvironment(); 
        if (newEnvironment != null && newEnvironment) {
            environment = new HashMap(0);
        }
        removeJavaOptions(environment);
        executor.execute(cmdLine, environment, resultHandler);
        
        resultHandler.waitFor();
        if (watchdog.killedProcess()) {
            throw new TimeoutException(r.getCombinedOutput());
        }
        int exitValue = resultHandler.getExitValue();
        if (Executor.INVALID_EXITVALUE == exitValue) {
            throw new IOException();
        }
        r.setReturnValue(exitValue);
        return r;
    }

    /**
     * removes our javaagent from the environment map
     * @param environment the environment map
     */
    private static void removeJavaOptions(Map environment) {
        final String javaOptionsKey = "_JAVA_OPTIONS"; //$NON-NLS-1$
        String javaOptions = (String) environment.get(javaOptionsKey);
        if (StringUtils.isNotBlank(javaOptions)
                && StringUtils.contains(javaOptions, "-javaagent")) { //$NON-NLS-1$)
            environment.remove(javaOptionsKey);
        }
    }
}