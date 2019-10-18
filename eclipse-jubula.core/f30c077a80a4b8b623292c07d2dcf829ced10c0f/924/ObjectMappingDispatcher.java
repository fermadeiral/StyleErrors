/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.jubula.tools.internal.om;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;


/**
 * @author BREDEX GmbH
 *
 */
public class ObjectMappingDispatcher {
    /** found TestSuites for the given TestCase */
    private static List < IObjectMappingObserver > observer = 
        new ArrayList < IObjectMappingObserver > ();
    
    /**
     * utility class
     */
    private ObjectMappingDispatcher() {
        // utility
    }
    
    /**
     * 
     * @return and unmodifialbe list of {@link IObjectMappingObserver}
     */
    public static List<IObjectMappingObserver> getObserver() {
        return Collections.unmodifiableList(observer);
    }
    
    /**
     * adds an Observer to List
     * @param obs
     *      Observer
     */
    public static void addObserver(IObjectMappingObserver obs) {
        if (!observer.contains(obs)) {
            observer.add(obs);
        }
    }

    /**
     * adds an Observer to List
     * @param obs
     *      Observer
     */
    public static void removeObserver(IObjectMappingObserver obs) {
        if (observer.contains(obs)) {
            observer.remove(obs);
        }
    }
    
    /**
     * adds a technical name to any open editor registered here with listener
     * 
     * @param tech
     *      ComponentIdentifiers
     */
    public static void notifyObjectMappedObserver(IComponentIdentifier[] tech) {
        List <IObjectMappingObserver> obs = 
            Collections.unmodifiableList(observer);
        for (IObjectMappingObserver obsvr : obs) {
            try {
                obsvr.update(IObjectMappingObserver.EVENT_COMPONENT_MAPPED, 
                    tech);
            } catch (Throwable t) {
                // just catch possible errors in listener and continue to notify
            }
        }
    }
}
