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
package org.eclipse.jubula.client.ui.rcp.controllers.propertysources;

/**
 * Property controller responsible for assigning value(s) to Parameter(s).
 *
 * @author BREDEX GmbH
 * @created Jul 16, 2010
 */
public interface IParameterPropertyController {

    /** types of input that can be used to assign values to Parameters */
    public enum ParameterInputType {
        /** external data source (e.g. Excel spreadsheet file) */
        EXTERNAL {
            /**
             * 
             * {@inheritDoc}
             */
            public boolean isEnabled(ParameterInputType activeInputType) {
                switch (activeInputType) {
                    case LOCAL:
                    case EXTERNAL:
                        return true;
                    default:
                        return false;
                }
            }
        }, 
        /** referenced Test Data Cube */
        REFERENCE {
            /**
             * 
             * {@inheritDoc}
             */
            public boolean isEnabled(ParameterInputType activeInputType) {
                switch (activeInputType) {
                    case LOCAL:
                    case REFERENCE:
                        return true;
                    default:
                        return false;
                }
            }
        }, 
        /** direct value assignment */
        LOCAL {
            /**
             * 
             * {@inheritDoc}
             */
            public boolean isEnabled(ParameterInputType activeInputType) {
                switch (activeInputType) {
                    case LOCAL:
                        return true;
                    default:
                        return false;
                }
            }
        };
        
        /**
         * 
         * @param activeInputType The currently used input type.
         * @return <code>true</code> if the receiving input type should be 
         *         enabled given the active input type. Otherwise 
         *         <code>false</code>.
         */
        public abstract boolean isEnabled(ParameterInputType activeInputType);
    }

    /**
     * 
     * @return the input type utilized by the receiver.
     */
    public ParameterInputType getInputType();

    /**
     * 
     * @return <code>true</code> if the property has been changed from its 
     *         default value. Otherwise <code>false</code>.
     */
    public boolean isPropertySet();
}
