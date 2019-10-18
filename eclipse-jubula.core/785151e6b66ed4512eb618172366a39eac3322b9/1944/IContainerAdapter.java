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
package org.eclipse.jubula.rc.javafx.tester.adapter;

import java.util.List;

import javafx.scene.Node;

import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;

/**
 * This Interface should be implemented by adapter classes for container
 * components.
 * 
 * @author BREDEX GmbH
 * @created 30.05.2014
 */
public interface IContainerAdapter extends IComponent {
    /**
     * Get the Content of the container which could be a single Node or a List
     * of Nodes.
     * 
     * @return the content
     */
    public List<? extends Node> getContent();
}
