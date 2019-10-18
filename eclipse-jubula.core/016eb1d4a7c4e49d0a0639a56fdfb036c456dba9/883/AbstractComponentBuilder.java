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
package org.eclipse.jubula.tools.internal.utils.generator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.jubula.tools.internal.exception.ConfigXmlException;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.internal.utils.ClassPathHacker;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ToolkitDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

/**
 * @author BREDEX GmbH
 * @created Jun 6, 2007
 */
public class AbstractComponentBuilder {
    
    /** The logger. */
    private static Logger log = 
        LoggerFactory.getLogger(AbstractComponentBuilder.class);
    
    /** The component system loaded from the configuration files. */
    private CompSystem m_compSystem;

    /** The XStream */
    private XStream m_xStream = XStreamGenerator.createXStream();
    
    /**
     * For use in RCP
     *
     */
    public AbstractComponentBuilder() {
        super();
    }
    
    /**
     * For use outside RCP, information about toolkits must be given,
     * rather than gathered from the installed plugins
     * @param config toolkit filename and location data
     */
    public AbstractComponentBuilder(ToolkitConfig config) {
        super();
        initCompSystem(config);
    }
    
    /**
     * Creates a CompSystem from the given InputStream.
     * @param inputStream an InputStream
     * @return a CompSystem
     */
    protected CompSystem createCompSystem(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(
                (new InputStreamReader(inputStream)));
        String string = ""; //$NON-NLS-1$
        String line = null;
        try {
            do {
                line = reader.readLine();
                if (line != null) {
                    string += line;
                }
            } while (line != null);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        string = string.replaceAll("[\r\n]+", " "); //$NON-NLS-1$ //$NON-NLS-2$
        string = string.replace("\t", " "); //$NON-NLS-1$ //$NON-NLS-2$
        string = string.replaceAll("[^\\S\\r\\n]+", " "); //$NON-NLS-1$ //$NON-NLS-2$
        string = string.replaceAll(">\\s", ">"); //$NON-NLS-1$ //$NON-NLS-2$
        string = string.replaceAll("\\s<", "<"); //$NON-NLS-1$ //$NON-NLS-2$
        
        CompSystem compSystem = (CompSystem)m_xStream.fromXML(string);
        return compSystem;
    }
    
    
    /**
     * Merges the given CompSystem in the main CompSystem (m_compSystem)
     * @param compSystem a ComponentSystem
     */
    protected void addToolkitToCompSystem(CompSystem compSystem) {
        if (m_compSystem == null) {
            m_compSystem = compSystem;
        } else {
            m_compSystem.merge(compSystem);
        }
    }
    
    
    /**
     * Finishes the ComponentSystem.<br>
     * Call this after all Toolkits are read in.
     * @throws ConfigXmlException in case of error creating CompSystem.
     */
    protected void postProcess() throws ConfigXmlException {
        m_compSystem.postProcess();
    }
    
    
    
    
    /**
     * For use outside RCP
     *
     * @param config Toolkit configuration from constructor
     */
    private void initCompSystem(ToolkitConfig config) {
        List names = config.getToolkitNames();
        for (Iterator i = names.iterator(); i.hasNext();) {
            String configName = (String)i.next();
            try {
                String xmlPath = config.getXMLPath(configName);
                URL xmlURL = new File(xmlPath).toURI().toURL();
                InputStream inputStream = getInputStream(xmlURL);
                CompSystem compSystem = createCompSystem(inputStream);
                String xmlPluginPath = config.getPluginXMLPath(configName);
                ToolkitPluginParser parser = 
                    new ToolkitPluginParser(xmlPluginPath, compSystem);
                ToolkitDescriptor descr = parser.getToolkitDescriptor();
                compSystem.addToolkitPluginDescriptor(
                    descr.getToolkitID(), descr);
                setToolkitDescriptorToComponents(compSystem, descr);
                addToolkitToCompSystem(compSystem);
            } catch (IOException e) {
                log.error(e.getLocalizedMessage(), e);
            }
            String resourceBundlePath = 
                config.getResourceBundlePath(configName);
            try {
                ClassPathHacker.addFile(
                        new File(resourceBundlePath)
                            .getCanonicalFile());
            } catch (IOException e) {
                log.error(e.getLocalizedMessage(), e);
                e.printStackTrace();
            }
            String resourceBundleFQN = config
                    .getResourceBundleFQN(configName);
            ResourceBundle bundle = ResourceBundle.getBundle(
                    resourceBundleFQN);
            CompSystemI18n.addResourceBundle(bundle);
        }
        postProcess();
    }
    

    
    /**
     * Gets an InputStream of the given file URL
     * @param absoluteFileURL the URL of the file
     * @return an InputStream
     * @throws IOException in case of file not found
     */
    protected InputStream getInputStream(URL absoluteFileURL) 
        throws IOException {
        return new BufferedInputStream(absoluteFileURL.openStream());
    }

    /**
     * @return the compSystem
     */
    protected CompSystem getCompSystem() {
        return m_compSystem;
    }
    
    /**
     * Sets the attributes given in the Map to all Components in the given
     * CompSystem.
     * @param compSystem the CompSystem
     * @param pluginDescr the {@link ToolkitDescriptor}
     */
    protected void setToolkitDescriptorToComponents(CompSystem compSystem, 
        ToolkitDescriptor pluginDescr) {
        
        List<Component> components = new ArrayList<Component>();
        List<? extends Component> tmpComponents = 
                compSystem.getAbstractComponents();
        if (tmpComponents != null) {
            components.addAll(tmpComponents);
        }
        tmpComponents = compSystem.getConcreteComponents();
        if (tmpComponents != null) {
            components.addAll(tmpComponents);
        }
        for (Iterator<Component> i = components.iterator(); i.hasNext();) {
            Component comp = i.next();
            comp.setToolkitDesriptor(pluginDescr);
        }
    }

}
