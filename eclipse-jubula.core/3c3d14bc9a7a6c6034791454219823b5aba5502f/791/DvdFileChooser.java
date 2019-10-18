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
package org.eclipse.jubula.examples.aut.dvdtool.gui;

import java.io.File;
import java.io.Serializable;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.eclipse.jubula.examples.aut.dvdtool.resources.Resources;


/**
 * This is the file chooser for the dvd tool.
 * @author BREDEX GmbH
 * @created 13.04.2005
 */
public class DvdFileChooser extends JFileChooser {

    /** the file filter */
    private FileFilter m_fileFilter = new DvdFileFilter(); 
    
    /**
     * public constructor, initialises this file chooser
     */
    public DvdFileChooser() {
        setFileFilter(m_fileFilter);
        setCurrentDirectory(new File(System.getProperty("user.dir"))); //$NON-NLS-1$
    }

    /**
     * returns the (last) extension of <code>f</code>
     * @param f the file
     * @return the extension of <code>f</code> or "" if the name of <code>f</code> does not contain a dot
     */
    private String getExtension(File f) {
        String ext = ""; //$NON-NLS-1$
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;    
    }
    
    /**
     * private inner class for file filtering
     * @author BREDEX GmbH
     * @created 13.04.2005
     */
    private class DvdFileFilter extends FileFilter implements Serializable {
        /** the description of this filter */
        private String m_description = null;
        
        /**
         * public constructor, initialises this filter
         */
        public DvdFileFilter() {
            init();
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            String extension = getExtension(f);

            return Constants.SUFFIX.equals(extension);
        }
        
        /**
         * {@inheritDoc}
         */
        public String getDescription() {
            return m_description;
        }
        
        /**
         * private method for initialisation
         */
        private void init() {
            m_description = Resources.getString("dvdfile.description"); //$NON-NLS-1$
        }       
    }
}