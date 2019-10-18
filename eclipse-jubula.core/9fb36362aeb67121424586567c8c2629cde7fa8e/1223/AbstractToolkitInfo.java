/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.toolkit.internal;

import java.util.Map;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.toolkit.ToolkitInfo;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ComponentClass;

/** @author BREDEX GmbH */
public abstract class AbstractToolkitInfo implements ToolkitInfo {
    
    /**
     * @return a map containing the mappings from a component class to the name
     *         of its tester class
     */
    public abstract Map<ComponentClass, String> getTypeMapping();
    
    /** {@inheritDoc} */
    public String registerTesterClass(String componentClassName,
            String testerClassName) {
        Validate.notNull(componentClassName);
        Validate.notNull(testerClassName);
        Map<ComponentClass, String> typeMapping = getTypeMapping();
        for (ComponentClass cc : typeMapping.keySet()) {
            if (cc.getName().equalsIgnoreCase(componentClassName)) {
                return typeMapping.put(cc, testerClassName);
            }
        }
        typeMapping.put(new ComponentClass(componentClassName),
                testerClassName);
        return null;
    }
    
    /** {@inheritDoc} */
    public String deregisterTesterClass(String componentClassName) {
        Validate.notNull(componentClassName);
        Map<ComponentClass, String> typeMapping = getTypeMapping();
        for (ComponentClass cc : typeMapping.keySet()) {
            if (cc.getName().equalsIgnoreCase(componentClassName)) {
                return typeMapping.remove(cc);
            }
        }
        return null;
    }
    
    /** {@inheritDoc} */
    public void merge(ToolkitInfo otherToolkit) {
        Validate.notNull(otherToolkit);
        Map<ComponentClass, String> otherTypeMapping =
                ((AbstractToolkitInfo) otherToolkit).getTypeMapping();
        for (ComponentClass cc : otherTypeMapping.keySet()) {
            registerTesterClass(cc.getName(), otherTypeMapping.get(cc));
        }
    }
}