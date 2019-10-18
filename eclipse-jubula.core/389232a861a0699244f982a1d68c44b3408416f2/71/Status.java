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
package org.eclipse.jubula.extensions.wizard.utils;

/**
 * Contains several status information enums for input validation.
 * 
 * @author BREDEX GmbH
 */
public enum Status {
    /** Indicates that the project name is too long */
    PROJECT_NAME_TOO_LONG, 
    
    /** Indicates that the project name is invalid */
    PROJECT_NAME_INVALID,
    
    /** Indicates that the project name is legal */
    PROJECT_NAME_OK,
    
    
    
    /** Indicates that the project location is illegal */
    PROJECT_LOCATION_ILLEGAL,
    
    /** Indicates that the project location already exists */
    PROJECT_LOCATION_ALREADY_EXISTS,
    
    /** Indicates that the project location is legal */
    PROJECT_LOCATION_OK,
    
    
    
    /** Indicates that the properties version is empty */
    PROPERTIES_VERSION_EMPTY,
    
    /** Indicates that the properties version is illegal */
    PROPERTIES_VERSION_ILLEGAL,
    
    /** Indicates that the properties version is legal */
    PROPERTIES_VERSION_OK,
    
    
    
    /** Indicates that the properties ID is empty */
    PROPERTIES_ID_EMPTY,
    
    /** Indicates that the properties ID is illegal */
    PROPERTIES_ID_ILLEGAL,
    
    /** Indicates that the properties ID is legal */
    PROPERTIES_ID_OK,
    
    /** Indicates that the component type is missing */
    COMPONENT_TYPE_MISSING,
    
    /** Indicates that the component was not selected */
    COMPONENT_MISSING,
    
    /** Indicates that the component is valid */
    COMPONENT_OK,
    
    
    /** Indicates that the action name already exists */
    ACTIONS_NAME_ALREADY_EXISTS,
    
    /** Indicates that the action's parameter name already exists */
    ACTIONS_PARAMETERS_NAME_ALREADY_EXISTS,
    
    /** Indicates that the action is valid */
    ACTIONS_OK
}
