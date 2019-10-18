/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.businessprocess;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jubula.client.core.Activator;
import org.eclipse.jubula.client.core.preferences.database.DatabaseConnection;
import org.eclipse.jubula.client.core.preferences.database.DatabaseConnectionConverter;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 * @created 03.04.2012
 */
public class SecurePreferenceBP {
    /**
     * the logger
     */
    private static final Logger LOG = LoggerFactory
            .getLogger(SecurePreferenceBP.class);
    
    /**
     * the singleton instance
     */
    private static SecurePreferenceBP instance;
    
    /**
     * private constructor
     */
    private SecurePreferenceBP() {
        
        IEclipsePreferences prefs = InstanceScope.INSTANCE
                .getNode(Activator.PLUGIN_ID);
        prefs.addPreferenceChangeListener(
                new IEclipsePreferences.IPreferenceChangeListener() {

                public void preferenceChange(PreferenceChangeEvent event) {
                    String[] oldProfileName = getProfileName(event, true);
                    String[] newProfileName = getProfileName(event, false);
                    
                    // when oldProfileName and newProfileName are empty
                    // ---> new database schema was created
                    if (oldProfileName.length != 0 
                            && newProfileName.length != 0) {
                        String oldName = StringConstants.EMPTY;
                        String newName = StringConstants.EMPTY;
                        for (int i = 0; i < oldProfileName.length; i++) {
                            oldName = oldProfileName[i];
                            newName = newProfileName[i];
                            if (oldName != null && newName != null) {
                                renameProfile(oldName, 
                                        newName);
                                IPreferenceStore store = Plugin.getDefault().
                                        getPreferenceStore();
                                if (store.getString(Constants.
                                        AUTOMATIC_DATABASE_CONNECTION_KEY).
                                        equals(oldName)) {
                                    store.setValue(Constants.
                                            AUTOMATIC_DATABASE_CONNECTION_KEY, 
                                            newName);
                                }
                            }
                        }
                    }
                }
            });
    }

    /**
     * creates the instance of this class or...
     * 
     * @return returns the instance if already exists
     */
    public static SecurePreferenceBP getInstance() {
        if (instance == null) {
            instance = new SecurePreferenceBP();
        }
        return instance;
    }

    /**
     * checks whether save password is activated
     * 
     * @param profileName the profile name
     * @return returns true if save password is activated
     */
    public boolean isSaveCredentialsActive(String profileName) {
        boolean saveProfile = false;
        Set<String> setProfiles = new HashSet<String>();
        setProfiles.clear();
        setProfiles = splitProfileString(
                Constants.SAVE_PROFILE_NAMES_KEY);

        if (!setProfiles.isEmpty()) {
            for (int i = 0; i < setProfiles.size(); i++) {
                if (setProfiles.contains(profileName)) {
                    saveProfile = true;
                }
            }
        }
        return saveProfile;
    }

    /**
     * Saves the profile names as a String separated by ";" in the preference
     * store. Is a profile listed in that String a profiled is created in
     * secure storage.
     * @see isSaveCredentialsActive
     * 
     * @param profileName
     *            the profile to save
     * @param status true sets the save profile feature as active
     *               false sets the feature as inactive
     */
    public void setSaveCredentialStatus(String profileName, boolean status) {
        IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
        Set<String> setProfiles = new HashSet<String>();
        setProfiles.clear();
        setProfiles = splitProfileString(
                Constants.SAVE_PROFILE_NAMES_KEY);
        StringBuffer bufferProfiles = new StringBuffer();
        if (status) {
            setProfiles.add(profileName);
            bufferProfiles.append(StringConstants.SEMICOLON);
            for (String profileNames : setProfiles) {
                bufferProfiles.append(profileNames).append(
                        StringConstants.SEMICOLON);
            }
            store.setValue(Constants.SAVE_PROFILE_NAMES_KEY,
                    bufferProfiles.toString());
        } else {
            if (setProfiles.contains(profileName)) {
                setProfiles.remove(profileName);
                for (String profileNames : setProfiles) {
                    bufferProfiles.append(profileNames).append(
                            StringConstants.SEMICOLON);
                }
                store.setValue(Constants.SAVE_PROFILE_NAMES_KEY,
                        bufferProfiles.toString());
            }
        }
    }

    /**
     * splits the profile preference string value into the several profiles
     * 
     * @param preferenceKey the preference key for profile names
     * @return returns the several profile names as a Set to ensure that that
     *         every profile name is individually
     */
    private Set<String> splitProfileString(String preferenceKey) {
        IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
        String profileNames = store.getString(preferenceKey);
        String[] arrayProfiles = profileNames.split(StringConstants.SEMICOLON);

        Set<String> setProfiles = new HashSet<String>();
        for (String profile : arrayProfiles) {
            setProfiles.add(profile);
        }
        return setProfiles;
    }

    /**
     * creates the profile node in secure storage and saves the passed user name
     * and password 
     * 
     * @param profileName
     *            the profile name in secure storage
     * @param userName
     *            the username
     */
    public void saveProfile(String profileName, String userName) {
        ISecurePreferences node = getNodeForProfile(profileName);
        if (node != null)  {
            try {
                node.put(Constants.SECURE_STORAGE_USERNAME_KEY, userName, 
                        false);
            } catch (StorageException e1) {
                LOG.error(e1.getLocalizedMessage(), e1);
            }
        }
    }
    
    /**
     * 
     * @param profileName the profile name in secure storage
     * @param password the password
     */
    public void saveProfilePassword(String profileName, String password) {
        ISecurePreferences node = getNodeForProfile(profileName);
        if (node != null) {
            try {
                node.put(Constants.SECURE_STORAGE_PASSWORD_KEY, password, true);
            } catch (StorageException e2) {
                LOG.error(e2.getLocalizedMessage(), e2);
            }
        }
    }

    /**
     * removes the profile from secure storage and its entries
     * 
     * @param profileName
     *            the profile name in secure storage
     */
    public void removeProfile(String profileName) {
        ISecurePreferences node = getNodeForProfile(profileName);
        if (node != null) {
            if (!isSaveCredentialsActive(profileName)) {
                node.removeNode();
            }
        }
    }

    /**
     * renames the profile in secure storage and preference store when database
     * schema was renamed in database preferences
     * 
     * @param oldProfileName the old profile name
     * @param newProfileName the new profile name
     */
    private void renameProfile(String oldProfileName, String newProfileName) {
        // rename secure storage
        ISecurePreferences root = SecurePreferencesFactory.getDefault();
        if (root == null) {
            LOG.error("No root node found"); //$NON-NLS-1$
        }
        if (oldProfileName != null) {
            ISecurePreferences node = root
                    .node(getSecureStorageProfilePath(oldProfileName));
            String userName = getUserName(oldProfileName);
            String password = getPassword(oldProfileName);
            node.removeNode();
            try {
                node = root.node(getSecureStorageProfilePath(newProfileName));
                node.put(Constants.SECURE_STORAGE_USERNAME_KEY, userName,
                        false);
            } catch (StorageException e1) {
                LOG.error(e1.getLocalizedMessage(), e1);
            }
            if (isSaveCredentialsActive(oldProfileName)) {
                try {
                    node.put(Constants.SECURE_STORAGE_PASSWORD_KEY, password,
                            true);
                } catch (StorageException e2) {
                    LOG.error(e2.getLocalizedMessage(), e2);
                }
            }
            // rename preference store
            if (isSaveCredentialsActive(oldProfileName)) {
                setSaveCredentialStatus(oldProfileName, false);
                setSaveCredentialStatus(newProfileName, true);
            }
        }
    }
    
    /**
     * Returns the new profile name when profile name was renamed.
     * Returns an empty String[] when a new profile was created
     * 
     * @param event the fired preference changed event when profile
     *        was renamed
     * @param oldName true for old profile name and false otherwise
     * @return returns the old profile name if true and the new profile name otherwise
     */
    public String[] getProfileName(PreferenceChangeEvent event, 
            boolean oldName) {
        String[] profileName = new String[0];
        if (event.getOldValue() != null) {
            List<DatabaseConnection> oldConnectionList = 
                    DatabaseConnectionConverter.convert(
                            event.getOldValue().toString());
            if (event.getNewValue() != null) {
                List<DatabaseConnection> newConnectionList = 
                        DatabaseConnectionConverter.convert(
                                event.getNewValue().toString());
                profileName = new String[oldConnectionList.size()];
                if (oldConnectionList.size() == newConnectionList.size()) {
                    for (int i = 0; i < oldConnectionList.size(); i++) {
                        String oldProfileName = oldConnectionList.
                                get(i).getName();
                        String newProfileName = newConnectionList.
                                get(i).getName();
                        if (!oldProfileName.equals(newProfileName)) {
                            if (oldName) {
                                profileName[i] = oldProfileName;
                            } else {
                                profileName[i] = newProfileName;
                            }
                        }
                    }
                }
            }
        }
        return profileName;
    }
    /**
     * readout the username from secure storage
     * 
     * @param profileName
     *            the profile name in secure storage
     * @return returns username for given profile
     */
    public String getUserName(String profileName) {
        String userName = StringConstants.EMPTY;

        ISecurePreferences secureStorage = SecurePreferencesFactory
                .getDefault();
        try {
            userName = secureStorage.node(
                    getSecureStorageProfilePath(profileName)).get(
                    Constants.SECURE_STORAGE_USERNAME_KEY, 
                    StringConstants.EMPTY);

        } catch (StorageException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
        return userName;
    }

    /**
     * readout password from secure storage
     * 
     * @param profileName
     *            the profile name in secure storage
     * @return the password for given profile
     */
    public String getPassword(String profileName) {
        // the password
        String dbPwd = StringConstants.EMPTY;
        // secure storage
        ISecurePreferences secureStorage = SecurePreferencesFactory
                .getDefault();
        try {
            dbPwd = secureStorage
                    .node(getSecureStorageProfilePath(profileName)).get(
                            Constants.SECURE_STORAGE_PASSWORD_KEY, 
                            StringConstants.EMPTY);

        } catch (StorageException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
        return dbPwd;
    }
    
    /**
     * 
     * @param profileName the profile name in secure storage
     */
    public void removePassword(String profileName) {
        ISecurePreferences node = getNodeForProfile(profileName);
        if (node != null) {
            node.remove(Constants.SECURE_STORAGE_PASSWORD_KEY);
        }
    }

    /**
     * Returns the secure storage profile path for the given profile name
     * 
     * @param dbProfileName
     *            the profile name
     * @return the secure storage profile path
     */
    public String getSecureStorageProfilePath(String dbProfileName) {
        return new StringBuffer()
                .append(Constants.ORG_ECLIPSE_JUBULA_SECURE_KEY)
                .append(StringConstants.SLASH)
                .append(dbProfileName)
                .toString();
    }
    
    /**
     * Returns the secure storage node for the given profile name
     * 
     * @param profileName the profile name
     * @return the secure storage profile node or null if application was
     *         unable to create secure preferences
     */
    private ISecurePreferences getNodeForProfile(String profileName) {
        ISecurePreferences root = SecurePreferencesFactory.getDefault();
        ISecurePreferences node = root;
        if (root == null) {
            LOG.error("No root node found"); //$NON-NLS-1$
        } else if (profileName != null) {
            node = root
                    .node(getSecureStorageProfilePath(profileName));
        }
        return node;
    }
}
