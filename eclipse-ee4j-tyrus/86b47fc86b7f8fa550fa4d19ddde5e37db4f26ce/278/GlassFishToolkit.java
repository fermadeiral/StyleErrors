/*
 * Copyright (c) 2011, 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.glassfish.tyrus.tests.qa.tools;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.ClientEndpoint;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpoint;

import org.glassfish.tyrus.tests.qa.config.AppConfig;

import org.glassfish.embeddable.archive.ScatteredArchive;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Michal Conos (michal.conos at oracle.com)
 */
public class GlassFishToolkit implements ServerToolkit {

    private String installRoot;
    private ScatteredArchive deploy;
    private final static Logger logger = Logger.getLogger(GlassFishToolkit.class.getName());
    private static final String glassfishWeb =
            "<!DOCTYPE glassfish-web-app PUBLIC \"-//GlassFish.org//DTD GlassFish Application Server 3.1 Servlet 3" +
                    ".0//EN\" \"http://glassfish.org/dtds/glassfish-web-app_3_0-1.dtd\">"
                    + "<glassfish-web-app error-url=\"\">"
                    + "<context-root>%s</context-root>"
                    + "<class-loader delegate=\"true\"/>"
                    + "<jsp-config>"
                    + "<property name=\"keepgenerated\" value=\"true\">"
                    + "<description>Keep a copy of the generated servlet class' java code.</description>"
                    + "</property>"
                    + "</jsp-config>"
                    + "</glassfish-web-app>";
    private AppConfig config;

    public GlassFishToolkit(AppConfig config) {
        this.config = config;
        this.installRoot = config.getInstallRoot();
    }

    public class Asadmin {

        private final static String ASADMIN_CMD_UNIX = "%s/bin/asadmin %s";
        private final static String ASADMIN_CMD_WINDOWS = "%s/bin/asadmin.bat %s";
        private final static String ASADMIN_DEFAULT_DOMAIN = "domain1";
        private final static String ASADMIN_START_DOMAIN = "start-domain %s";
        private final static String ASADMIN_STOP_DOMAIN = "stop-domain %s";
        private final static String ASADMIN_DEPLOY_WAR = "deploy --force=true %s";
        private final static String ASADMIN_DEPLOY_WAR_PROPS = "deploy --force=true --properties %s=%s %s";
        private final static String ASADMIN_UNDEPLOY_APP = "undeploy --cascade=true %s";
        private final static String ASADMIN_LIST_APPS = "list-applications";

        private boolean onWindows() {
            return System.getProperty("os.name").startsWith("Windows");
        }

        private String getAsadminCommand() {
            if (onWindows()) {
                return ASADMIN_CMD_WINDOWS;
            } else {
                return ASADMIN_CMD_UNIX;
            }
        }

        private String getDomain() {
            final String domain = System.getProperty("glassfish.domain");
            if (domain != null) {
                return domain;
            } else {
                return ASADMIN_DEFAULT_DOMAIN;
            }
        }

        public String getAsadminStartDomain(String domain) {
            return String.format(getAsadminCommand(), installRoot, String.format(ASADMIN_START_DOMAIN, domain));
        }

        public String getAsadminStopDomain(String domain) {
            return String.format(getAsadminCommand(), installRoot, String.format(ASADMIN_STOP_DOMAIN, domain));
        }

        public String getAsadminStartDomain1() {
            return getAsadminStartDomain(getDomain());
        }

        public String getAsadminStopDomain1() {
            return getAsadminStopDomain(getDomain());
        }

        public String getAsadminListApplications() {
            return String.format(getAsadminCommand(), installRoot, ASADMIN_LIST_APPS);
        }

        public String getAsadminDeployCommand(String warfile) {
            return String.format(getAsadminCommand(), installRoot, String.format(ASADMIN_DEPLOY_WAR, warfile));
        }

        public String getAsadminDeployCommand(String warfile, String key, String value) {
            return String.format(getAsadminCommand(), installRoot,
                                 String.format(ASADMIN_DEPLOY_WAR_PROPS, key, value, warfile));
        }

        public String getAsadminUnDeployCommand(String appname) {
            return String.format(getAsadminCommand(), installRoot, String.format(ASADMIN_UNDEPLOY_APP, appname));
        }

        public void exec(String cmd) {
            logger.log(Level.INFO, "asadmin.exec: {0}", cmd);
            CommandLine cmdLine = CommandLine.parse(cmd);
            DefaultExecutor executor = new DefaultExecutor();
            try {
                Map<String, String> env = System.getenv();
                //env.put("WEBSOCKET_CONTAINER", "GLASSFISH");
                int exitValue = executor.execute(cmdLine, env);
                logger.log(Level.INFO, "asadmin.exec.rc: {0}", exitValue);
                if (exitValue != 0) {
                    logger.log(Level.SEVERE, "Can't execute:{0}", cmdLine.toString());
                    throw new RuntimeException("Can't start GF server:" + exitValue);
                }

            } catch (ExecuteException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex.getMessage());
            }
        }

        public void exec(String[] commands) {
            for (String cmd : commands) {
                exec(cmd);
            }
        }
    }

    private File createWebXml(String path) throws IOException {
        File webXml = File.createTempFile("glassfish-web", "xml");
        FileUtils.writeStringToFile(webXml, String.format(glassfishWeb, path));
        return webXml;
    }

    private String getClazzCanonicalName(File clazz) {
        logger.log(Level.FINE, "getClazzCanonicalName:{0}", clazz.toString());
        return FilenameUtils.removeExtension(FilenameUtils.separatorsToUnix(clazz.toString()))
                            .replaceFirst("target/classes/", "").replace('/', '.');
    }

    private File getDirname(File clazz) {
        return new File(new File(getClazzCanonicalName(clazz).replace('.', '/')).getParent());
    }

    private Class<?> getClazzForFile(File clazz) throws ClassNotFoundException, MalformedURLException {
        String clazzCanonicalName = getClazzCanonicalName(clazz);
        //URLClassLoader cl = new URLClassLoader(new URL[] {new URL("file://"+clazz.getAbsoluteFile().getParent())});
        logger.log(Level.FINE, "getClazzForFile(): {0}", clazzCanonicalName);
        logger.log(Level.FINE, "getClazzForFile(): {0}", clazz.getAbsolutePath());
        //logger.log(Level.FINE, "getClazzForFile(): classloader:{0}", cl.getURLs());
        return Class.forName(clazzCanonicalName); //, true, cl);
    }

    private boolean isBlackListed(File clazz) throws ClassNotFoundException, MalformedURLException {
        //logger.log(Level.FINE, "File? {0}", clazzCanonicalName);

        Class tryMe = getClazzForFile(clazz);


        logger.log(Level.FINE, "File? {0}", tryMe.getCanonicalName());

        logger.log(Level.FINE, "Interfaces:{0}", tryMe.getInterfaces());
        if (Arrays.asList(tryMe.getInterfaces()).contains((ServerApplicationConfig.class))) {
            logger.log(Level.FINE, "ServerApplicationConfig : {0}", tryMe.getCanonicalName());
            return true;
        }
        if (tryMe.isAnnotationPresent(ServerEndpoint.class)) {
            logger.log(Level.FINE, "Annotated ServerEndpoint: {0}", tryMe.getCanonicalName());
            return true;
        }

        if (tryMe.isAnnotationPresent(ClientEndpoint.class)) {
            logger.log(Level.FINE, "Annotated ClientEndpoint: {0}", tryMe.getCanonicalName());
            return true;
        }
        //Endpoint itself is not blacklisted
        // TYRUS-150
        //if (Endpoint.class.isAssignableFrom(tryMe)) {
        //    logger.log(Level.INFO, "Programmatic Endpoint: {0}", tryMe.getCanonicalName());
        //    return true;
        //}

        return false;
    }

    private File getFileForClazz(Class clazz) {
        logger.log(Level.FINE, "Obtaining file for : {0}", clazz.getCanonicalName());

        String clazzBasename = new File(clazz.getCanonicalName().replace('.', '/')).getName();

        return new File(getDirname(new File(clazz.toString())), clazzBasename);
    }

    public ScatteredArchive makeWar(Class clazz, String path) throws IOException, ClassNotFoundException,
            InterruptedException, URISyntaxException {
        ScatteredArchive archive = new ScatteredArchive("testapp", ScatteredArchive.Type.WAR);
        String name = clazz.getName();

        //archive.addClassPath(new File("target/classes"));
        List<File> addClasses = new ArrayList<File>();
        File tempDir = FileUtils.getTempDirectory();
        Path dir = Files.createTempDirectory(Paths.get(tempDir.toURI()), null);
        File dstDirectory = new File(dir.toFile(), "lib");
        FileUtils.forceMkdir(dstDirectory);
        logger.log(Level.INFO, "BEFORE: Target WAR: {0}", FileUtils.listFiles(dstDirectory, null, true));
        File source = new File("target/classes");
        Set<File> warFiles = new HashSet<File>();
        for (File addMe : FileUtils.listFiles(source, new String[]{"class"}, true)) {
            String srcClazzCanonicalName = getClazzForFile(addMe).getCanonicalName();
            if (srcClazzCanonicalName != null && srcClazzCanonicalName.equals(clazz.getCanonicalName())) {
                warFiles.add(addMe);
                continue;
            }
            if (!isBlackListed(addMe)) {
                warFiles.add(addMe);
            }
        }
        Misc.copyFiles(warFiles, dstDirectory, "^target/classes", "");
        archive.addClassPath(dstDirectory);
        archive.addMetadata(createWebXml(path), "WEB-INF/glassfish-web.xml");
        logger.log(Level.INFO, "Target WAR: {0}", FileUtils.listFiles(dstDirectory, null, true));
        return archive;
    }

    private String getLocalFileFromURI(URI file) {
        if (file.getScheme().equals("file")) {
            return file.toString().replaceFirst("file:", "");
        }
        return null;
    }

    @Override
    public void startServer() throws DeploymentException {
        try {
            Asadmin asadmin = new Asadmin();
            asadmin.exec(
                    new String[]{
                            asadmin.getAsadminStartDomain1(),
                            asadmin.getAsadminDeployCommand(getLocalFileFromURI(deploy.toURI())),
                            asadmin.getAsadminListApplications()
                    });
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(GlassFishToolkit.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public void stopServer() {
        try {
            Asadmin asadmin = new Asadmin();
            if (deploy != null) {
                String appname = FilenameUtils.removeExtension(new File(getLocalFileFromURI(deploy.toURI())).getName());
                try {
                    asadmin.exec(asadmin.getAsadminUnDeployCommand(appname));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            asadmin.exec(
                    new String[]{
                            asadmin.getAsadminListApplications(),
                            asadmin.getAsadminStopDomain1()
                    });
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public void registerEndpoint(Class<?> endpoint) {
        try {
            this.deploy = makeWar(endpoint, config.getContextPath());
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }
}
