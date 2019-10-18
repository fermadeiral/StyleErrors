/*******************************************************************************
 * Copyright (c) 2018 BREDEX GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: BREDEX GmbH - initial API and implementation and/or initial
 * documentation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.jubula.client.core.model;

import java.util.List;
import java.util.Map;

/**
 * @author BREDEX GmbH
 *
 */
public interface IParamValueSetPO {

    /**
     * @return the id
     */
    Long getId();

    /**
     * @return the project id
     */
    Long getParentProjectId();
    /**
     * 
     * @param id the project id
     */
    void setParentProjectId(Long id);

    /**
     * @return a {@link Map} with the value as key and the comment as value
     */
    List<IValueCommentPO> getValues();

    /**
     * @return the default value
     */
    String getDefaultValue();

    /**
     * @param defaultValue the default value
     */
    void setDefaultValue(String defaultValue);

}