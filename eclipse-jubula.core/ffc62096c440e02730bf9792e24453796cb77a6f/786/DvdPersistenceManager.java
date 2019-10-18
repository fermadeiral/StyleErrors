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
package org.eclipse.jubula.examples.aut.dvdtool.persistence;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.List;
import java.util.Vector;


/**
 * This class is the persistence manager. It's a singleton.
 *
 * @author BREDEX GmbH
 * @created 13.04.2005
 */
public class DvdPersistenceManager {
    
    /** the singleton instance reference */
    private static DvdPersistenceManager manager = null;
    
    /**
     * private constructor, use method singleton() for an instance of this manager
     */
    private DvdPersistenceManager() {
        // empty
    }
    
    /**
     * Implementation of the singleton pattern.
     * @return the single instance of this manager.
     */
    public static DvdPersistenceManager singleton() {
        if (manager == null) {
            manager = new DvdPersistenceManager();
        }
        return manager;
    }
    
    /**
     * Save object <code>list</code> to <code>file</code> (via serialisation).
     * @param file the file to store object <code>list</code> in
     * @param list the object <code>list</code> to save; all objects must 
     *        implement serialisable
     * @throws DvdPersistenceException if an io error occurs
     * @throws DvdInvalidObjectException if object <code>list</code> could not 
     *         made persistent.
     */
    public void save(File file, List list)
        throws DvdInvalidObjectException, DvdPersistenceException {
        try {
            FileOutputStream fs = new FileOutputStream(file);
            ObjectOutputStream output = new ObjectOutputStream(fs);
            try {
                for (int i = 0; i < list.size(); i++) {
                    Object object = list.get(i);
                    output.writeObject(object);
                }
            } catch (InvalidClassException ice) {
                output.close();
                file.delete();
                throw new DvdPersistenceException("exception.save.failure", ice); //$NON-NLS-1$
            } catch (NotSerializableException nse) {
                output.close();
                file.delete();
                throw new DvdInvalidObjectException("exception.save.failure", nse); //$NON-NLS-1$
            } catch (IOException ioe) {
                output.close();
                file.delete();
                throw new DvdPersistenceException("exception.ioerror", ioe); //$NON-NLS-1$
            } finally {
                output.close();
            }
        } catch (FileNotFoundException fnfe) {
            throw new DvdPersistenceException("exception.invalid.file", fnfe); //$NON-NLS-1$
        } catch (IOException ioe) {
            // try to delete the file
            file.delete();
            throw new DvdPersistenceException("exception.ioerror", ioe); //$NON-NLS-1$
        }
    }
    
    /**
     * Loads an object list from <code>file</code> (via deserialisation).
     * @param file the <code>file</code> to load the object list from
     * @return the read object list
     * @throws DvdPersistenceException if an io error occurs
     * @throws DvdInvalidContentException if the <code>file</code> does not 
     *         contain the expected data
     */
    public List load(File file) throws DvdInvalidContentException,
        DvdPersistenceException {
        try {
            FileInputStream fs = new FileInputStream(file);
            return load(fs);
        } catch (FileNotFoundException fnfe) {
            throw new DvdPersistenceException("exception.ioerror", fnfe); //$NON-NLS-1$
        }
    }

    /**
     * Loads an object list from <code>InsputStream</code> (via deserialisation).
     * @param is the <code>InputStream</code> to load the object list from
     * @return the read object list
     * @throws DvdPersistenceException if an io error occurs
     * @throws DvdInvalidContentException if the <code>file</code> does not 
     *         contain the expected data
     */
    public List load(InputStream is) throws DvdInvalidContentException,
            DvdPersistenceException {
        try {
            ObjectInputStream input = new ObjectInputStream(is);
            List<Object> list = new Vector<Object>();
            try {
                // this infinite loop will be exited by an EOFException
                // when the end of the stream is reached
                do {
                    Object object = input.readObject();
                    list.add(object);
                } while (true);
            } catch (EOFException eofe) {
                // EOFException is expected after all objects are read
                is.close();
                input.close();
                return list;
            } catch (ClassNotFoundException cnfe) {
                is.close();
                input.close();
                throw new DvdInvalidContentException(
                        "exception.invalid.file.content", cnfe); //$NON-NLS-1$
            } catch (InvalidClassException ice) {
                is.close();
                input.close();
                throw new DvdInvalidContentException(
                        "exception.invalid.file.content", ice); //$NON-NLS-1$
            } catch (StreamCorruptedException sce) {
                is.close();
                input.close();
                throw new DvdInvalidContentException(
                        "exception.invalid.file.content", sce); //$NON-NLS-1$
            } catch (OptionalDataException ode) {
                is.close();
                input.close();
                throw new DvdInvalidContentException(
                        "exception.invalid.file.content", ode); //$NON-NLS-1$
            } catch (IOException ioe) {
                is.close();
                input.close();
                throw new DvdPersistenceException("exception.ioerror", ioe); //$NON-NLS-1$
            }
        } catch (StreamCorruptedException sce) {
            try {
                is.close();
            } catch (IOException e) { // NOPMD by zeb on 10.04.07 14:08
                // fileInputStream could not be closed -> do nothing
            }
            throw new DvdInvalidContentException(
                    "exception.invalid.file.content", sce); //$NON-NLS-1$
        } catch (IOException ioe) {
            try {
                is.close();
            } catch (IOException e) { // NOPMD by zeb on 10.04.07 14:08
                // fileInputStream could not be closed -> do nothing
            }
            throw new DvdPersistenceException("exception.ioerror", ioe); //$NON-NLS-1$
        }
    }
}
