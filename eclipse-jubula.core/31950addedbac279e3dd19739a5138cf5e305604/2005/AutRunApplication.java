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
package org.eclipse.jubula.app.autrun;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jubula.app.autrun.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.EnvConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.jubula.tools.internal.utils.IsAliveThread;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;
import org.eclipse.jubula.version.Vn;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Starts an AUT and registers the AUT with an AUT Agent. In order to terminate 
 * at the right time (not too early, not too late) this application assumes 
 * that the following JVM parameters (or equivalent) are used:<ul>
 * <li>osgi.noShutdown=true</li> 
 * <li>eclipse.jobs.daemon=true</li> 
 * </ul>
 * These parameters are required because the original application was designed 
 * to run outside of an OSGi context, i.e. the application should end only 
 * when no non-daemon threads are active.
 * 
 * @author BREDEX GmbH
 * @created Dec 9, 2009
 */
public class AutRunApplication implements IApplication {
    
    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(AutRunApplication.class);
    
    /**
     * <code>LAUNCHER_NAME</code>
     */
    private static final String LAUNCHER_NAME = "autrun"; //$NON-NLS-1$

    /** <code>TOOLKIT_RCP</code> */
    private static final String TK_RCP = "rcp"; //$NON-NLS-1$

    /** <code>TK_SWT</code> */
    private static final String TK_SWT = "swt"; //$NON-NLS-1$

    /** <code>TK_SWING</code> */
    private static final String TK_SWING = "swing"; //$NON-NLS-1$

    /** <code>TK_JAVAFX</code> */
    private static final String TK_JAVAFX = "javafx"; //$NON-NLS-1$

    /** <code>DEFAULT_NAME_TECHNICAL_COMPONENTS</code> */
    private static final boolean DEFAULT_NAME_TECHNICAL_COMPONENTS = true;

    // - Command line options - Start //
    /** port number for the Aut Agent with which to register */
    private static final String OPT_AUT_AGENT_PORT = "p"; //$NON-NLS-1$

    /** port number for the Aut Agent with which to register */
    private static final String OPT_AUT_AGENT_PORT_LONG = "autagentport"; //$NON-NLS-1$
    
    /** hostname for the Aut Agent with which to register */
    private static final String OPT_AUT_AGENT_HOST = "a"; //$NON-NLS-1$
    
    /** hostname for the Aut Agent with which to register */
    private static final String OPT_AUT_AGENT_HOST_LONG = "autagenthost"; //$NON-NLS-1$
    
    /** help option */
    private static final String OPT_HELP = "h"; //$NON-NLS-1$
    
    /** hostname for the Aut Agent with which to register */
    private static final String OPT_HELP_LONG = "help"; //$NON-NLS-1$
    
    /** name of the AUT to register */
    private static final String OPT_AUT_ID = "i"; //$NON-NLS-1$
    
    /** name of the AUT to register */
    private static final String OPT_AUT_ID_LONG = "autid"; //$NON-NLS-1$
    
    /** flag for name generation for certain technical components */
    private static final String OPT_NAME_TECHNICAL_COMPONENTS = "g"; //$NON-NLS-1$

    /** flag for name generation for certain technical components */
    private static final String OPT_NAME_TECHNICAL_COMPONENTS_LONG = "generatenames"; //$NON-NLS-1$
    
    /** keyboard layout */
    private static final String OPT_KEYBOARD_LAYOUT = "k"; //$NON-NLS-1$

    /** keyboard layout */
    private static final String OPT_KEYBOARD_LAYOUT_LONG = "kblayout"; //$NON-NLS-1$

    /** AUT working directory */
    private static final String OPT_WORKING_DIR = "w"; //$NON-NLS-1$

    /** AUT working directory */
    private static final String OPT_WORKING_DIR_LONG = "workingdir"; //$NON-NLS-1$
    
    /** executable file used to start the AUT */
    private static final String OPT_EXECUTABLE = "e"; //$NON-NLS-1$
    
    /** executable file used to start the AUT */
    private static final String OPT_EXECUTABLE_LONG = "exec"; //$NON-NLS-1$
    
    /** AUT agent host name */
    private static final String HOSTNAME = "hostname"; //$NON-NLS-1$
    
    /** AUT agent port */
    private static final String PORT = "port"; //$NON-NLS-1$
    
    /** AUT id */
    private static final String ID = "id"; //$NON-NLS-1$
    
    /** technical components */
    private static final String TRUE_FALSE = "true / false"; //$NON-NLS-1$
    
    /** AUT keyboard layout */
    private static final String LOCALE = "locale"; //$NON-NLS-1$
    
    /** AUT working directory */
    private static final String DIRECTORY = "directory"; //$NON-NLS-1$
    
    /** AUT options */
    private static final String COMMAND = "command"; //$NON-NLS-1$
    
    /** swing class prefix */
    private static final String SWING_AUT_TOOLKIT_CLASS_PREFIX = "Swing"; //$NON-NLS-1$
    
    /** SWT class prefix */
    private static final String SWT_AUT_TOOLKIT_CLASS_PREFIX = "Swt"; //$NON-NLS-1$
    
    /** RCP class prefix */
    private static final String RCP_AUT_TOOLKIT_CLASS_PREFIX = "Rcp"; //$NON-NLS-1$
    
    /** JavaFX class prefix */
    private static final String JAVAFX_AUT_TOOLKIT_CLASS_PREFIX = "JavaFX"; //$NON-NLS-1$
    // - Command line options - End //

    /**
     * prints help options
     * @param pe a parse Exception - may also be null
     */
    private static void printHelp(ParseException pe) {
        System.out.println(Vn.getDefault().getVersion());
        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(new OptionComparator());
        if (pe != null) {
            formatter.printHelp(
                    LAUNCHER_NAME,
                    StringConstants.EMPTY,
                    createCmdLineOptions(), 
                    "\n" + pe.getLocalizedMessage(),  //$NON-NLS-1$
                    true);
        } else {
            formatter.printHelp(LAUNCHER_NAME,
                    createCmdLineOptions(), true);
        }
    }

    /**
     * Creates and returns settings for starting an AUT based on the given
     * command line.
     *  
     * @param cmdLine Provides the settings for the AUT configuration.
     * @return new settings for starting an AUT.
     */
    private static Map<String, String> createAutConfig(CommandLine cmdLine) {
        Map<String, String> autConfig = new HashMap<String, String>();
        if (cmdLine.hasOption(OPT_WORKING_DIR)) {
            autConfig.put(AutConfigConstants.WORKING_DIR, cmdLine
                    .getOptionValue(OPT_WORKING_DIR));
        } else {
            autConfig.put(AutConfigConstants.WORKING_DIR, System
                    .getProperty("user.dir")); //$NON-NLS-1$
        }
        
        if (cmdLine.hasOption(OPT_NAME_TECHNICAL_COMPONENTS)) {
            autConfig.put(AutConfigConstants.NAME_TECHNICAL_COMPONENTS, String
                    .valueOf(cmdLine.getOptionValue(
                        OPT_NAME_TECHNICAL_COMPONENTS)));
        } else {
            autConfig.put(AutConfigConstants.NAME_TECHNICAL_COMPONENTS,
                String.valueOf(DEFAULT_NAME_TECHNICAL_COMPONENTS));
        }
        autConfig.put(AutConfigConstants.EXECUTABLE, cmdLine
                .getOptionValue(OPT_EXECUTABLE));
        
        if (cmdLine.hasOption(OPT_KEYBOARD_LAYOUT)) {
            autConfig.put(AutConfigConstants.KEYBOARD_LAYOUT, 
                    cmdLine.getOptionValue(OPT_KEYBOARD_LAYOUT));
        }
        
        String[] autArguments = cmdLine.getOptionValues(OPT_EXECUTABLE);
        if (autArguments.length > 1) {
            autConfig.put(AutConfigConstants.AUT_RUN_AUT_ARGUMENTS, StringUtils
                .join(autArguments,
                    AutConfigConstants.AUT_RUN_AUT_ARGUMENTS_SEPARATOR_CHAR, 1,
                    autArguments.length));
        }
        
        return autConfig;
    }

    /**
     * @return the command line options available when invoking the main method. 
     */
    private static Options createCmdLineOptions() {
        Options options = new Options();
        Option autAgentHostOption = new Option(OPT_AUT_AGENT_HOST, true,
            NLS.bind(Messages.infoAutAgentHost, EnvConstants.LOCALHOST_ALIAS));
        autAgentHostOption.setLongOpt(OPT_AUT_AGENT_HOST_LONG);
        autAgentHostOption.setArgName(HOSTNAME);
        options.addOption(autAgentHostOption);

        Option autAgentPortOption = new Option(OPT_AUT_AGENT_PORT, true,
            NLS.bind(Messages.infoAutAgentPort,
                EnvConstants.AUT_AGENT_DEFAULT_PORT));
        autAgentPortOption.setLongOpt(OPT_AUT_AGENT_PORT_LONG);
        autAgentPortOption.setArgName(PORT);
        options.addOption(autAgentPortOption);

        OptionGroup autToolkitOptionGroup = new OptionGroup();
        autToolkitOptionGroup.addOption(new Option(TK_SWING,
                Messages.infoSwingToolkit));
        autToolkitOptionGroup.addOption(new Option(TK_SWT,
                Messages.infoSwtToolkit));
        autToolkitOptionGroup.addOption(new Option(TK_RCP,
                Messages.infoRcpToolkit));
        autToolkitOptionGroup.addOption(new Option(TK_JAVAFX,
                Messages.infoJavaFXToolkit));
        autToolkitOptionGroup.setRequired(true);
        options.addOptionGroup(autToolkitOptionGroup);

        Option autIdOption = new Option(OPT_AUT_ID, true, Messages.infoAutId);
        autIdOption.setLongOpt(OPT_AUT_ID_LONG);
        autIdOption.setArgName(ID);
        autIdOption.setRequired(true);
        options.addOption(autIdOption);

        Option nameTechnicalComponentsOption = new Option(
                OPT_NAME_TECHNICAL_COMPONENTS, true,
                Messages.infoGenerateTechnicalComponentNames);
        nameTechnicalComponentsOption
                .setLongOpt(OPT_NAME_TECHNICAL_COMPONENTS_LONG);
        nameTechnicalComponentsOption.setArgName(TRUE_FALSE);
        options.addOption(nameTechnicalComponentsOption);

        Option keyboardLayoutOption = new Option(OPT_KEYBOARD_LAYOUT, true,
                Messages.infoKbLayout);
        keyboardLayoutOption.setLongOpt(OPT_KEYBOARD_LAYOUT_LONG);
        keyboardLayoutOption.setArgName(LOCALE);
        options.addOption(keyboardLayoutOption);

        Option workingDirOption = new Option(OPT_WORKING_DIR, true,
                Messages.infoAutWorkingDirectory);
        workingDirOption.setLongOpt(OPT_WORKING_DIR_LONG);
        workingDirOption.setArgName(DIRECTORY);
        options.addOption(workingDirOption);

        Option helpOption = new Option(OPT_HELP, false, Messages.infoHelp);
        helpOption.setLongOpt(OPT_HELP_LONG);
        options.addOption(helpOption);
        
        OptionBuilder.hasArgs();
        Option autExecutableFileOption = OptionBuilder.create(OPT_EXECUTABLE);
        autExecutableFileOption.setDescription(Messages.infoExecutableFile);
        autExecutableFileOption.setLongOpt(OPT_EXECUTABLE_LONG);
        autExecutableFileOption.setRequired(true);
        autExecutableFileOption.setArgName(COMMAND);
        options.addOption(autExecutableFileOption);
        
        return options;
    }
    
    /**
     * @author BREDEX GmbH
     */
    private static final class WatchDog extends Thread {
        /**
         * the barrier to await
         */
        private CyclicBarrier m_b;

        /**
         * Constructor
         * 
         * @param name
         *            the name
         * @param b the barrier to use
         */
        private WatchDog(String name, CyclicBarrier b) {
            super(name);
            m_b = b;
        }
        
        /** {@inheritDoc} */
        public void run() {
            try {
                boolean shouldShutdown;
                do {
                    TimeUtil.delay(2500);
                    shouldShutdown = true;
                    Set<Thread> allThreads = Thread.getAllStackTraces()
                            .keySet();
                    for (Thread t : allThreads) {
                        if (t instanceof IsAliveThread) {
                            shouldShutdown = false;
                            break;
                        }
                    }
                } while (!shouldShutdown);
            } finally {
                try {
                    m_b.await();
                } catch (InterruptedException e) {
                    LOG.error(e.getLocalizedMessage(), e);
                } catch (BrokenBarrierException e) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
            }

            try {
                EclipseStarter.shutdown();
            } catch (Exception e) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * This class implements the <code>Comparator</code> interface for comparing
     * Options.
     */
    private static class OptionComparator implements Comparator {
        /** {@inheritDoc} */
        public int compare(Object o1, Object o2) {
            Option opt1 = (Option)o1;
            Option opt2 = (Option)o2;
            // always list -exec as last option
            if (opt1.getOpt().equals(OPT_EXECUTABLE)) {
                return 1;
            }
            if (opt2.getOpt().equals(OPT_EXECUTABLE)) {
                return -1;
            }
            return 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object start(final IApplicationContext context) throws Exception {
        String[] args = (String[])context.getArguments().get(
                IApplicationContext.APPLICATION_ARGS);
        if (args == null) {
            args = new String[0];
        }
        Options options = createCmdLineOptions();
        Parser parser = new BasicParser();
        CommandLine cmdLine = null;
        try {
            cmdLine = parser.parse(options, args, false);
            if (!cmdLine.hasOption(OPT_HELP)) {
                
                String toolkit = StringConstants.EMPTY;
                if (cmdLine.hasOption(TK_SWING)) {
                    toolkit = SWING_AUT_TOOLKIT_CLASS_PREFIX;
                } else if (cmdLine.hasOption(TK_SWT)) {
                    toolkit = SWT_AUT_TOOLKIT_CLASS_PREFIX;
                } else if (cmdLine.hasOption(TK_RCP)) {
                    toolkit = RCP_AUT_TOOLKIT_CLASS_PREFIX;
                } else if (cmdLine.hasOption(TK_JAVAFX)) {
                    toolkit = JAVAFX_AUT_TOOLKIT_CLASS_PREFIX;
                }
                
                int autAgentPort = EnvConstants.AUT_AGENT_DEFAULT_PORT;
                if (cmdLine.hasOption(OPT_AUT_AGENT_PORT)) {
                    try {
                        autAgentPort = Integer.parseInt(cmdLine
                                .getOptionValue(OPT_AUT_AGENT_PORT));
                    } catch (NumberFormatException nfe) {
                        // use default
                    }
                }
                String autAgentHost = EnvConstants.LOCALHOST_ALIAS;
                if (cmdLine.hasOption(OPT_AUT_AGENT_HOST)) {
                    autAgentHost = cmdLine.getOptionValue(OPT_AUT_AGENT_HOST);
                }
                
                InetSocketAddress agentAddr = 
                        new InetSocketAddress(autAgentHost, autAgentPort);
                AutIdentifier autId = new AutIdentifier(cmdLine
                        .getOptionValue(OPT_AUT_ID));
                
                Map<String, String> autConfiguration = createAutConfig(cmdLine);
                
                AutRunner runner = new AutRunner(
                        toolkit, autId, agentAddr, autConfiguration);
                try {
                    runner.run();
                } catch (ConnectException ce) {
                    LOG.info(Messages.infoConnectionToAutAgentFailed, ce);
                    System.err.println(Messages.infoNonAutAgentConnectionInfo);
                }
            } else {
                printHelp(null);
            }
        } catch (ParseException pe) {
            printHelp(pe);
        } finally {
            CyclicBarrier b = new CyclicBarrier(2);
            Thread watchDog = new WatchDog("http://eclip.se/457600#c8", b); //$NON-NLS-1$
            watchDog.setDaemon(true);
            watchDog.start();
            // http://eclip.se/461810: prevent application to quit too early, otherwise framework bundle lookups do not work
            b.await();
        }
        
        return IApplication.EXIT_OK;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void stop() {
        // no-op
    }
}