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
package org.eclipse.jubula.client.cmd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jubula.client.cmd.constants.ClientStrings;
import org.eclipse.jubula.client.cmd.exceptions.PreValidateException;
import org.eclipse.jubula.client.cmd.i18n.Messages;
import org.eclipse.jubula.client.cmd.progess.HeadlessProgressProvider;
import org.eclipse.jubula.client.core.ClientTest;
import org.eclipse.jubula.client.core.businessprocess.ClientTestStrings;
import org.eclipse.jubula.client.core.constants.Constants;
import org.eclipse.jubula.client.core.errorhandling.ErrorMessagePresenter;
import org.eclipse.jubula.client.core.errorhandling.IErrorMessagePresenter;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.persistence.locking.LockManager;
import org.eclipse.jubula.client.core.preferences.database.DatabaseConnection;
import org.eclipse.jubula.client.core.preferences.database.DatabaseConnectionConverter;
import org.eclipse.jubula.client.core.preferences.database.H2ConnectionInfo;
import org.eclipse.jubula.client.core.preferences.database.MySQLConnectionInfo;
import org.eclipse.jubula.client.core.preferences.database.OracleConnectionInfo;
import org.eclipse.jubula.client.core.preferences.database.PostGreSQLConnectionInfo;
import org.eclipse.jubula.client.core.progress.IProgressConsole;
import org.eclipse.jubula.client.core.utils.StringHelper;
import org.eclipse.jubula.client.internal.AutAgentConnection;
import org.eclipse.jubula.client.internal.exceptions.ConnectionException;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.EnvConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.messagehandling.Message;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;
import org.eclipse.jubula.version.Vn;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created Mar 12, 2009
 */
public abstract class AbstractCmdlineClient implements IProgressConsole {
    /** <code>EXIT_CODE_ERROR</code> */
    protected static final int EXIT_CODE_ERROR = 1;
    /** <code>EXIT_CODE_OK</code> */
    protected static final int EXIT_CODE_OK = 0;
    
    /** error message */
    protected static final String OPT_NO_VAL = Messages.NoArgumentFor;
    /** error message */
    protected static final String OPT_UNKNOWN = Messages.UnrecognizedOption;
    /** error message */
    protected static final String JDBC_UNKNOWN = Messages.UnsupportedJDBC;
    
    /** log facility */
    private static Logger log = 
        LoggerFactory.getLogger(AbstractCmdlineClient.class);
    /** be quiet during processing */
    private static boolean quiet = false;
    /** did an error occur during processing */
    private static boolean errorOccurred = false;
    /** did a validation error occur during processing */
    private static boolean validationErrorOccurred = false;
    /** did a missing argument error occur during processing */
    private static boolean missingArgErrorOccurred = false;
    /** is this a dry run* */
    private static boolean noRunValue = false;
    /** the command line representation */
    private CommandLine m_cmd = null;

    /** external configuration file with parameters */
    private File m_configFile;
    /** JobConfiguration created from configFile */
    private JobConfiguration m_job;

    /**
     * @param name name of option
     * @param hasArg option has an argument
     * @param argname name of the argument
     * @param text Text for help
     * @param isReq option is required
     * @return Option opt 
     */
    protected static Option createOption(String name, boolean hasArg,
            String argname, String text, boolean isReq) {
        Option opt = new Option(name, hasArg, text);
        opt.setRequired(isReq);
        opt.setArgName(argname);
        return opt;
    }


    /**
     * cleanup of connection
     */
    protected void shutdown() {
        try {
            if (!AutAgentConnection.getInstance().isConnected()) {
                printlnConsoleError(Messages.ConnectionToAutUnexpectedly);
            }
        } catch (ConnectionException e) {
            log.info(Messages.ConnectionToAutUnexpectedly, e);
        }
        IAUTConfigPO startedConfig = m_job.getAutConfig();
        if (startedConfig != null) {
            try {
                AutIdentifier startedAutId = new AutIdentifier(
                        startedConfig.getConfigMap().get(
                                AutConfigConstants.AUT_ID));
                if (AutAgentConnection.getInstance().isConnected()) {
                    ClientTest.instance().stopAut(startedAutId);
                }
            } catch (ConnectionException e) {
                log.info(Messages.ErrorWhileShuttingDownStopping, e);
            }
        }
        
        try {
            while (AutAgentConnection.getInstance().isConnected()) {
                ClientTest.instance().disconnectFromAutAgent();
                TimeUtil.delay(200);
            }
        } catch (ConnectionException e) {
            log.info(Messages.ErrorWhileShuttingDownDisconnecting, e);
        }
        // cleanup after connections closed
        if (LockManager.isRunning()) {
            LockManager.instance().dispose();
        }
    }

    /**
     * 
     * @param args
     *            the command line
     * @throws FileNotFoundException
     *             if config file is missing
     * @throws ParseException
     *             if wrong options are present
     * @throws IOException
     *             if io error
     * @return true to continue processing the commandline run; false to stop
     *         processing further execution.
     */
    protected boolean parseCommandLine(String[] args)
        throws FileNotFoundException, ParseException, IOException {
        String[] cloneArgs = args.clone();
        Options options = createOptions(false);
        // Command line arguments parser
        CommandLineParser parser = new BasicParser();
        try {
            // we will parse the command line until there are no
            // (more) errors
            int maxTrys = 5;
            Boolean parseNotOK = true;
            while (parseNotOK) {
                try {
                    m_cmd = parser.parse(options, cloneArgs);
                    parseNotOK = false;
                } catch (ParseException exp) {
                    if (maxTrys-- < 0) {
                        cloneArgs = handleParseException(args, exp, true);
                        throw new ParseException(StringConstants.EMPTY);
                    }
                    cloneArgs = handleParseException(args, exp, false);
                }
            }
            if (m_cmd.hasOption(ClientStrings.HELP)) {
                printUsage();
                return false;
            }
            
            // The first thing to check is, if there is a config file
            // if there is a config file we read this first,
            if (m_cmd.hasOption(ClientStrings.CONFIG)) {
                m_configFile = new File(m_cmd
                        .getOptionValue(ClientStrings.CONFIG));
                if (m_configFile.exists() && m_configFile.canRead()) {
                    printConsoleLn(Messages.ClientConfigFile
                            + m_configFile.getAbsolutePath(), true);
                    m_job = JobConfiguration.initJob(m_configFile);

                } else {
                    throw new FileNotFoundException(StringConstants.EMPTY);
                }
            } else {
                m_job = JobConfiguration.initJob(null);
            }
            // now we should have all arguments, either from file or
            // from commandline
            if (m_cmd.hasOption(ClientStrings.QUIET)) {
                quiet = true;
            }
            if (m_cmd.hasOption(ClientStrings.NORUN)
                    || !StringUtils.isEmpty(
                            m_job.getNoRunOptMode())) {
                noRunValue = true;
            }
            m_job.parseJobOptions(m_cmd);
            handleCmdLineToProcessOptions();
            // check if all needed attributes are set
            // and if port number is valid
            preValidate(m_job);

        } catch (PreValidateException exp) {
            String message = exp.getLocalizedMessage();
            if (message != null && message.length() > 0) {
                printlnConsoleError(message);
            }
            printUsage();
            throw new ParseException(StringConstants.EMPTY);
        }
        return true;
    }

    /**
     * To unify ITE and cmd-line tools, some options / properties
     *      can be provided through both command-line and other means.
     *      These are simply stored as process properties.
     */
    private void handleCmdLineToProcessOptions() {
        if (m_cmd.hasOption(EnvConstants.CLIENTIP_KEY)) {
            System.setProperty(EnvConstants.CLIENTIP_KEY, m_cmd
                    .getOptionValue(EnvConstants.CLIENTIP_KEY));
        }
        if (m_cmd.hasOption(EnvConstants.CLIENTPORT_KEY)) {
            System.setProperty(EnvConstants.CLIENTPORT_KEY, m_cmd
                    .getOptionValue(EnvConstants.CLIENTPORT_KEY));
        }
    }

    /**
     * method to create an options object, filled with all options
     * @param req
     *      boolean flag must be true for an required option
     *      this is only used for printing the correct usage
     * @return the options
     */
    private Options createOptions(boolean req) {
        Options options = new Options();
        options.addOption(createOption(ClientStrings.HELP, false, 
                StringConstants.EMPTY, 
                Messages.ClientHelpOpt, false));
        options.addOption(createOption(ClientStrings.QUIET, false, 
                StringConstants.EMPTY, 
                Messages.ClientQuietOpt, false));
        options.addOption(createOption(ClientStrings.CONFIG, true, 
                ClientStrings.CONFIGFILE,
                Messages.ClientConfigOpt, false));
        OptionGroup ogConnection = new OptionGroup();
        
        ogConnection.addOption(createOption(ClientTestStrings.DBURL, true, 
                ClientTestStrings.DATABASE,
                Messages.ClientDburlOpt, false));
        ogConnection.addOption(createOption(ClientTestStrings.DB_SCHEME, true, 
                ClientTestStrings.SCHEME, 
                Messages.ClientDbschemeOpt, false));
        options.addOptionGroup(ogConnection);
        
        options.addOption(createOption(ClientTestStrings.DB_USER, true, 
                ClientTestStrings.USER, 
                Messages.ClientDbuserOpt, false));
        options.addOption(createOption(ClientTestStrings.DB_PW, true, 
                ClientTestStrings.PASSWORD, 
                Messages.ClientDbpwOpt, false));
        extendOptions(options, req);
        return options;
    }
    
    /**
     * method to extend an options object, filled with all options
     * @param opt Predefined options. This options will be extended
     * during the method call.
     * @param req
     *      boolean flag must be true for an required option
     *      this is only used for printing the correct usage
     */

    protected abstract void extendOptions(Options opt, boolean req);
    
    /**
     * Do any final work required before actually running the client
     */
    protected void preRun() {
        // nothing in here - subclasses may override
    }

    /**
     * writes an output to console
     * 
     * @param text
     *            the message
     * @param printTimestamp
     *            whether a timestamp should be printed
     */
    public static void printConsoleLn(String text, boolean printTimestamp) {
        String textToPrint = StringUtils.chomp(text);
        String consoleOutput = StringConstants.EMPTY;
        if (printTimestamp) {
            String timeStamp = DateFormat.getDateTimeInstance(
                DateFormat.SHORT,
                DateFormat.MEDIUM).format(
                    Calendar.getInstance().getTime());
            consoleOutput = NLS.bind(Messages.ClientCmdOutputWithTimeStamp,
                timeStamp, textToPrint);
        } else {
            consoleOutput = NLS.bind(Messages.ClientCmdOutputWithoutTimeStamp,
                textToPrint);
        }
        printConsole(consoleOutput);
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeErrorLine(String line) {
        printlnConsoleError(line);
    }

    /**
     * {@inheritDoc}
     */
    public void writeLine(String line) {
        printConsole(line + StringConstants.NEWLINE);
    }

    /**
     * {@inheritDoc}
     */
    public void writeStatus(IStatus status) {
        printConsoleLn("AUT " + StringHelper.getStringOf(status, false)  //$NON-NLS-1$
                + "..." + StringConstants.NEWLINE, true); //$NON-NLS-1$
        if (status.isMultiStatus()) {
            for (IStatus s : status.getChildren()) {
                printConsoleLn("AUT " + StringHelper.getStringOf(s, false) //$NON-NLS-1$
                        + "..." + StringConstants.NEWLINE, true); //$NON-NLS-1$
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeStatus(IStatus status, String id) {
        writeStatus(status);
    }
    
    /**
     * {@inheritDoc}
     */
    public void closeConsole() {
        //no op
    }


    /**
     * writes an output to console
     * @param text
     *      Message
     */
    public static void printConsole(String text) {
        if (!quiet) {
            System.out.print(text);
        }
    }

    /**
     * writes an output to console
     * @param text
     *      the message to log and println to sys.err
     */
    public static void printlnConsoleError(String text) {
        errorOccurred = true;
        log.error(Messages.AnErrorOcurred + StringConstants.COLON
                + StringConstants.SPACE + text);
        System.err.println(Messages.ClientError + StringConstants.NEWLINE
                + StringConstants.TAB
                + text); 
    }
    
    /**
     * Execute a job
     * 
     * @param args
     *            Command Line Parameter
     * @return Exit Code
     */
    public int run(String[] args) {
        Job.getJobManager().setProgressProvider(new HeadlessProgressProvider());
        
        ErrorMessagePresenter.setPresenter(new IErrorMessagePresenter() {
            public void showErrorMessage(JBException ex, Object[] params,
                    String[] details) {
                
                log.error(ex + StringConstants.COLON + StringConstants.SPACE
                        + ex.getMessage());
                Integer messageID = ex.getErrorId();
                showErrorMessage(messageID, params, details);
            }

            public void showErrorMessage(Integer messageID, Object[] params,
                    String[] details) {

                Message m = MessageIDs.getMessageObject(messageID);
                if (m == null) {
                    log.error(Messages.NoCorrespondingMessage 
                            + StringConstants.COLON + StringConstants.SPACE 
                            + messageID);
                } else {
                    String msgString = m.getMessage(params);
                    if (m.getSeverity() == Message.ERROR) {
                        printlnConsoleError(msgString);
                    } else {
                        printConsole(msgString);
                    }
                }
            }
        });
        try {
            if (!parseCommandLine(args)) {
                return EXIT_CODE_OK;
            }
        } catch (ParseException e) {
            log.error(e.getLocalizedMessage(), e);
            return EXIT_CODE_ERROR;
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
            return EXIT_CODE_ERROR;
        }
        preRun();
        try {
            int exitCode = doRun();

            if (isErrorOccurred()) {
                exitCode = EXIT_CODE_ERROR;
            }
            
            printConsoleLn(Messages.ClientExitCode + exitCode, true);

            return exitCode;
        } catch (Throwable t) {
            // Assume that, if an exception has bubbled up this far, then it is 
            // a big enough problem to warrant telling the user and returning a
            // generic error exit code.
            log.error(t.getLocalizedMessage(), t);
            printlnConsoleError(t.getLocalizedMessage());
            return EXIT_CODE_ERROR;
        }
    }


    /**
     * runs the job
     * @return int
     *      Exit Code
     */
    protected abstract int doRun();


    /**
     * checks if all job arguments are present
     * @param job
     *      contains the job configuration
     * @throws PreValidateException is arguments are missing
     */
    private void preValidate(JobConfiguration job) throws PreValidateException {
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append(Messages.ClientMissingArgs);
        StringBuilder errorInvalidArgsMsg = new StringBuilder();
        errorInvalidArgsMsg.append(Messages.ClientInvalidArgs);
        if (job.getDbConnectionName() == null && job.getDb() == null) {
            appendError(errorMsg, ClientTestStrings.DB_SCHEME, 
                    ClientTestStrings.SCHEME + " OR"); //$NON-NLS-1$
            appendError(errorMsg, ClientTestStrings.DBURL, 
                    ClientTestStrings.DATABASE);
        }
        if (job.getDb() != null 
                && !(job.getDb().startsWith(OracleConnectionInfo.JDBC_PRE)
                || job.getDb().startsWith(MySQLConnectionInfo.JDBC_PRE)
                || job.getDb().startsWith(PostGreSQLConnectionInfo.JDBC_PRE) 
                || job.getDb().startsWith(H2ConnectionInfo.JDBC_PRE))) {
            appendError(errorMsg, JDBC_UNKNOWN, job.getDb());
        }
        if (job.getDbuser() == null) {
            appendError(errorMsg, ClientTestStrings.DB_USER, 
                    ClientTestStrings.USER);
        }
        if (job.getDbpw() == null) {
            appendError(errorMsg, ClientTestStrings.DB_PW, 
                    ClientTestStrings.PASSWORD);
        }
        extendValidate(job, errorMsg, errorInvalidArgsMsg);
        if (missingArgErrorOccurred) {
            errorMsg.append(Messages.ClientReadUserManual);
            throw new PreValidateException(errorMsg.toString());
        }
        if (job.getNoRunOptMode() != null && job.getTestJobName() != null) {
            throw new PreValidateException(
                    Messages.NoRunOptionDoesNotSupportTestJobs
                            + StringConstants.SPACE
                            + Messages.ClientReadUserManual);
        }
        // If the datadir directory was not specified by user and the default value
        // cannot be used because the platform is running without an instance location
        if (job.getDataDir() == String.valueOf(
                Constants.INVALID_VALUE)) {
            throw new PreValidateException(
                    Messages.NoPlatformInstanceLocation);
        }
        if (validationErrorOccurred) {
            errorInvalidArgsMsg.append(Messages.ClientReadUserManual);
            throw new PreValidateException(errorInvalidArgsMsg.toString());
        }
        if (job.getDbscheme() == null && job.getDb() == null) {
            List<DatabaseConnection> availableConnections = 
                DatabaseConnectionConverter.computeAvailableConnections();
            List<String> connectionNames = new ArrayList<String>();
            for (DatabaseConnection conn : availableConnections) {
                connectionNames.add(conn.getName());
            }
            throw new PreValidateException(NLS.bind(
                    Messages.NoSuchDatabaseConnection, 
                    new String[] {job.getDbConnectionName(), 
                            StringUtils.join(connectionNames, ", ")})); //$NON-NLS-1$
        }
    }

    /**
     * Do validation beyond the basic parameters
     * @param job configuration to check
     * @param errorMsgs storage for error messages from validation in case required arguments are missing
     * @param errorInvalidArgsMsg storage for error messages from validation in case values of given arguments are invalid
     */
    protected abstract void extendValidate(JobConfiguration job, 
              StringBuilder errorMsgs, StringBuilder errorInvalidArgsMsg);

    /**
     * 
     * @param args
     *          command line
     * @param exp
     *          exception
     * @param printToConsole if <code>true</code> the error 
     *          message will be shown in the console
     * @return arguments modified
     */
    public String[] handleParseException(String [] args, ParseException exp,
            boolean printToConsole) {
        // if there is an error we will remove that token
        // and try it again
        String message = exp.getLocalizedMessage();
        if (message != null && message.length() > 0 && printToConsole) {
            printlnConsoleError(
                    extendMissingArgumentExceptionMessage(message, exp));
            printUsage();
        }
        if (message.startsWith(OPT_NO_VAL)) {
            message = printAndGetEndOfMessage(message, 1);
        } else if (message.startsWith(OPT_UNKNOWN)) {
            message = printAndGetEndOfMessage(message, 2);
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i].endsWith(message)) {
                args[i] = StringConstants.EMPTY;
            }
        }
        return args;
    }

    /**
     * Extend parse exception message with the missing parameters
     * 
     * @param errorMessage
     *            extendable message
     * @param exp
     *            parse exception
     * @return extended error message with missing argument if defined, else the
     *         error message given in method parameter
     */
    private String extendMissingArgumentExceptionMessage(String errorMessage,
            ParseException exp) {

        if (exp instanceof MissingArgumentException) {
            MissingArgumentException missingArgExp = 
                    (MissingArgumentException) exp;
            if (missingArgExp != null) {
                Option option = missingArgExp.getOption();
                if (option != null && option.getArgName() != null) {
                    StringBuilder errorBuilder = new StringBuilder();
                    appendError(errorBuilder, errorMessage,
                            option.getArgName());
                    errorBuilder.append(Messages.ClientReadUserManual);
                    return errorBuilder.toString();
                }
            }
        }

        return errorMessage;
    }

    /**
     * prints the message and substrings it
     * @param message the message
     * @param indexForSubstring the value which should be cut
     * @return the substringed message
     */
    private String printAndGetEndOfMessage(String message,
            int indexForSubstring) {
        printlnConsoleError(message);
        int idx = message.indexOf(StringConstants.COLON);
        String substring = message.substring(idx + indexForSubstring);
        return substring;
    }

    /**
     * 
     * @param errorMsg Stringbuilder with message
     * @param msg1 the missing option
     * @param msg2 the missing option argument
     */
    protected void appendError(StringBuilder errorMsg, String msg1, 
            String msg2) {
        missingArgErrorOccurred = true;
        errorOccurred = true;
        errorMsg.append(StringConstants.TAB);
        errorMsg.append(StringConstants.MINUS);
        errorMsg.append(msg1);
        errorMsg.append(StringConstants.SPACE);
        errorMsg.append(msg2);
        errorMsg.append(StringConstants.NEWLINE);   
    }

    /**
     * 
     * @param validationErrorMsg Stringbuilder with message
     * @param msg1 the missing option
     * @param msg2 the missing option argument
     */
    protected void appendValidationError(StringBuilder validationErrorMsg, 
            String msg1, String msg2) {
        validationErrorOccurred = true;
        errorOccurred = true;
        validationErrorMsg.append(StringConstants.TAB);
        validationErrorMsg.append(StringConstants.MINUS);
        validationErrorMsg.append(msg1);
        validationErrorMsg.append(StringConstants.SPACE);
        validationErrorMsg.append(msg2);
        validationErrorMsg.append(StringConstants.NEWLINE);   
    }
    
    /** 
     * prints the command line syntax
     */
    private void printUsage() {
        writeLine(Vn.getDefault().getVersion().toString());
        Options options = createOptions(true);
        
        // The "-data" argument is parsed and handled by the Eclipse RCP
        // before we get a chance to see it, but we want to make sure that the
        // user is aware that it's an option. In order to accomplish this, we 
        // add it to the options used in generating usage, but not the the 
        // options actually used in parsing the command line.
        options.addOption(createOption(ClientTestStrings.WORKSPACE, true, 
                ClientTestStrings.WORKSPACE_ARG, 
                Messages.ClientWorkspaceOpt, false));
        HelpFormatter formatter = new HelpFormatter();
    
        formatter.printHelp(getCmdlineClientExecName(), options, true);
    }

    /** @return the name of the executable for the commandline Client */
    public abstract String getCmdlineClientExecName();

    /**
     * @return the noRun
     */
    public static boolean isNoRun() {
        return noRunValue;
    }

    /**
     * @return the quiet
     */
    public boolean isQuiet() {
        return quiet;
    }

    /**
     * @return the errorOccurred
     */
    public static boolean isErrorOccurred() {
        return errorOccurred;
    }

    /**
     * @return the validationErrorOccurred
     */
    public static boolean isValidationErrorOccurred() {
        return validationErrorOccurred;
    }
    
    /**
     * @return the missingArgErrorOccurred
     */
    public static boolean isMissingArgErrorOccurred() {
        return missingArgErrorOccurred;
    }
    
    /**
     * @return CommandLine
     *      the command Line the client was started with
     */
    public CommandLine getCmdLine() {
        return m_cmd;
    }

    /**
     * @return the job
     */
    public JobConfiguration getJob() {
        return m_job;
    }
    
    /** {@inheritDoc} */
    public void writeWarningLine(String line) {
        writeLine(line);
    }
}
