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
package org.eclipse.jubula.tools.internal.utils;

import java.io.File;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.TimingConstantsServer;


/**
 * Utility class for executing external commands.
 *
 * @author BREDEX GmbH
 * @created Sep 11, 2007
 */
@Deprecated
public class ExternalCommandExecutor {
    /** Constant for command for executing commands under DOS */
    private static final String [] DOS_CMD = {"command.com", "/c"}; //$NON-NLS-1$ //$NON-NLS-2$

    /** Constant for command for executing commands under Windows NT */
    private static final String [] WIN_NT_CMD = {"cmd.exe", "/c"}; //$NON-NLS-1$ //$NON-NLS-2$

    /** Constant for command for executing commands under Windows 9x */
    private static final String [] WIN_9X_CMD = {"command.com", "/c"}; //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * This class is a timer, which monitors the inner class ExecuteTask. After
     * the given time, it asks the user, if the executed task should stop. This
     * class is for JUnit tests public.
     */
    public class MonitorTask extends TimerTask {
        /** New thread to execute the given file. */
        private ExecuteTask m_task = null;
    
        /** Process state. True if inner process is finished. */
        private boolean m_finished = false;
        
        /** Inner process, started by the Runtime.exec() in the ExecuteTask. */
        private Process m_process = null;

        /** whether a timeout has occurred */
        private boolean m_isTimeout;
    
        /** whether the command is valid */
        private boolean m_isCmdValid;
        
        /** redirect object */
        private SysoRedirect m_redirect;

        /**
         * The inner thread starts a new process to execute an command. It will
         * be instantiate by the MonitorTask, which can also stop this thread.
         */
        class ExecuteTask extends IsAliveThread {
            /** The command to start by the Runtime. */
            private String m_cmd;
            /** the parameters for the command */
            private String[] m_cmdParams;
    
            /** The OS command needed in order to start the given command. */
            private String [] m_prepend;

            /** The folder, in whose the command will be started. */
            private File m_dir = null;
    
            /**
             * Instantiate a new ExecuteTask and assigns the parameters to its
             * own.
             * 
             * @param path
             *            Path in whose the command should start.
             * @param prepend
             *            The OS command required to execute the given command.
             * @param cmdLine
             *            Command to start script-file plus the parameters.
             */
            public ExecuteTask(File path, String [] prepend, String cmdLine) {
                splitCmdLine(cmdLine); // sets m_cmd and m_cmdParams
                m_prepend = prepend != null ? prepend : new String[0];
                m_dir = path;
                m_isTimeout = false;
                m_isCmdValid = true;
            }
    
            /**
             * Separates the command and the parameters from a command line
             * @param cmdLine The command line and its parameters 
             */
            private void splitCmdLine(String cmdLine) {
                try {
                    String cmdLinePar = 
                        cmdLine.replaceAll("[\\\\]", "\\\\\\\\"); //$NON-NLS-1$ //$NON-NLS-2$
                    StreamTokenizer tk = 
                        new StreamTokenizer(new StringReader(cmdLinePar));
                    tk.slashSlashComments(false);
                    tk.slashStarComments(false);
                    tk.lowerCaseMode(false);
                    tk.resetSyntax();
                    tk.wordChars(Character.MIN_VALUE, Character.MAX_VALUE);
                    tk.whitespaceChars(' ', ' ');
                    tk.whitespaceChars('\t', '\t');
                    tk.quoteChar('"');
                    tk.quoteChar('\'');
                    if (tk.nextToken() != StreamTokenizer.TT_EOF) {
                        m_cmd = tk.sval;
                    } else {
                        m_cmd = StringConstants.EMPTY;
                    }
                    List<String> params = new ArrayList<String>();
                    while (tk.nextToken() != StreamTokenizer.TT_EOF) {
                        params.add(tk.sval);                        
                    }
                    m_cmdParams = params.toArray(new String[params
                            .size()]);
                } catch (IOException e) {
                    // error handling below
                }
                if (m_cmd == null) {
                    m_cmd = StringConstants.EMPTY;
                }
                if (m_cmdParams == null) {
                    m_cmdParams = new String[0];               
                }
                
            }

            /**
             * Executes the command in the directory, which were set by the constructor. The process will
             * be start by the Runtime.exec method and runs till it will be
             * terminated. A Terminate can be caused by the user or the process,
             * if it ends proper.
             */
            public void run() {
                Runtime runtime = Runtime.getRuntime();
                try {
                    File cmdFile = new File(m_cmd); 
                    if (!cmdFile.isAbsolute()) {
                        File absCmdFile = new File(m_dir, m_cmd);
                        if (absCmdFile.exists()) {
                            // Use the script in the working directory, if it exists
                            m_cmd = absCmdFile.getCanonicalPath();
                        }
                    }
                    List<String> commandTokens = new ArrayList<String>();
                    for (int i = 0; i < m_prepend.length; ++i) {
                        commandTokens.add(m_prepend[i]);
                    }
                    if (EnvironmentUtils.isWindowsOS()) {
                        commandTokens.add(StringConstants.QUOTE 
                                + m_cmd + StringConstants.QUOTE);
                    } else {
                        commandTokens.add(m_cmd);                        
                    }
                    for (int i = 0; i < m_cmdParams.length; ++i) {
                        commandTokens.add(m_cmdParams[i]);
                    }
                    ProcessBuilder pb =
                            new ProcessBuilder(commandTokens);
                    pb.directory(m_dir);
                    pb.redirectErrorStream(true);
                    
                    m_process = pb.start();
                    
                    if (m_process != null) {

                        m_redirect = new SysoRedirect(
                                m_process.getInputStream(),
                                "Command sysout: "); //$NON-NLS-1$
                        m_redirect.start();
                        m_process.waitFor();
                    } else {
                        m_isCmdValid = false;
                    }
                } catch (IOException e) {
                    /* file or folder for command not found. */
                    m_isCmdValid = false;
                } catch (InterruptedException e) {
                    /* waitFor is interrupted. */
                }
                // switch to signalize, that process is finished
                m_finished = true;
            }
        }
    
        /**
         * Instantiates and starts the ExecuteTask.
         * 
         * @param path
         *            Path in which the command will be started.
         * @param prepend
         *            The OS command required to execute the given command.
         *            Can be <code>null</code>.
         * @param cmd
         *            Command to start script-file
         */
        public MonitorTask(File path, String [] prepend, String cmd) {
            // instantiate inner thread
            m_task = new ExecuteTask(path, prepend, cmd);
            m_task.start(); // starts inner thread
        }
    
        /**
         * Terminates the inner process and ends the corresponding task.
         */
        public void run() {
    
            if (finishTask()) {
                m_isTimeout = true;
            }

        }
    
        /**
         * Terminates the inner thread.
         * 
         * @return <code>true</code> if task was running when this method was
         *         called. Otherwise, <code>false</code>.
         */
        public boolean finishTask() {
            if (!isFinished()) {
                if (m_process != null) {
                    m_process.destroy();
                }
                m_finished = true;
                return true;
            }

            return false;

        }
    
        /**
         * @return <code>true</code> if the task is finished. Otherwise, 
         *         <code>false</code>.
         */
        public boolean isFinished() {
            return m_finished;
        }
    
        /**
         * @return the exit code for the task.
         * @throws IllegalThreadStateException if the task has not finished.
         */
        public int getExitCode() throws IllegalThreadStateException {
            return m_process.exitValue();
        }
        
        /**
         * @return the standard output and error
         * @throws IllegalThreadStateException if the task has not finished.
         */
        public String getOutput() {
            if (m_redirect != null) {
                return m_redirect.getTruncatedLog();
            }
            return StringConstants.EMPTY;
        }

        /**
         * @return <code>true</code> if a timeout occurred. Otherwise, 
         *         <code>false</code>. Note that this method will always return
         *         <code>false</code> until a timeout actually occurs.
         */
        public boolean hasTimeoutOccurred() {
            return m_isTimeout;
        }

        /**
         * @return <code>true</code> if the command was started successfully. 
         *         Otherwise, <code>false</code>. Note that a return value of 
         *         <code>true</code> does not guarantee that the command 
         *         finished execution successfully.
         */
        public boolean wasCmdValid() {
            return m_isCmdValid;
        }
    }

    /**
     * Executes the given command in the given path and waits the given amount 
     * of time for the execution to finish. If the command finishes in good
     * time, this method returns immediately after the script finishes.
     * Otherwise, it returns after the given amount of time.
     * 
     * @param path
     *            Path in which the command will be executed
     * @param cmd
     *            Command to execute
     * @param timeout Time (in milliseconds) to wait for the execution to finish.
     * @return the monitor for the task.
     */
    public MonitorTask executeCommand(File path, String cmd, int timeout) {

        String [] prepend = new String[0];
        
        if (EnvironmentUtils.isWin9xOS()) {
            prepend = WIN_9X_CMD;
        } else if (EnvironmentUtils.isDosOS()) {
            prepend = DOS_CMD;
        } else if (EnvironmentUtils.isWindowsOS()) { 
            // Windows NT; 2000; 2003; XP; ...
            prepend = WIN_NT_CMD;
        }
        // For all other OSes, we don't bother with an interpreter
        
        MonitorTask mt = new MonitorTask(path, prepend, cmd);
    
        Timer timer = new Timer();
        timer.schedule(mt, timeout);
        while (!mt.isFinished()) {
            TimeUtil.delay(TimingConstantsServer
                    .POLLING_DELAY_EXECUTE_EXTERNAL_COMMAND);
        }
    
        timer.cancel();

        return mt;

    }
}
