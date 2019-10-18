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
package org.eclipse.jubula.communication.internal.message;

import java.awt.Point;
import java.util.Map;

import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;

/**
 * The message to send all supported and currently instantiated components of
 * the AUT. <br>
 * 
 * @author BREDEX GmbH
 * @created 05.10.2004
 */
public class ServerShowDialogMessage extends Message {
    /** opens the observation dialog */
    public static final int ACT_SHOW_OBSERVER_DIALOG = 1;

    /** closes the observation dialog */
    public static final int ACT_CLOSE_OBSERVER_DIALOG = 2;

    /** opens the checkmode dialog */
    public static final int ACT_SHOW_CHECK_DIALOG = 3;

    /** closes the checkmode dialog */
    public static final int ACT_CLOSE_CHECK_DIALOG = 4;

    /** action/dialog that should be executed */
    private int m_action = 0;

    // the data of this message BEGIN
    /** the component which should be send */
    private Component m_component;

    /** values to be checked */
    private Map m_checkValues;

    /** where to show dialog */
    private Point m_point;

    /** the component which should be send */
    private IComponentIdentifier m_compId;

    /** The LogicalName of CAP */
    private String m_logicalName;

    // the data of this message END

    /** empty constructor for serialisation */
    public ServerShowDialogMessage() {
        // do nothing
    }

    /**
     * public constructor
     * 
     * @param comp
     *            component to be observed
     * @param id
     *            IComponentIdentifier
     */
    public ServerShowDialogMessage(Component comp, IComponentIdentifier id) {
        m_component = comp;
        m_compId = id;
    }

    /**
     * public constructor
     * 
     * @param comp
     *            component to be observed
     * @param id
     *            IComponentIdentifier
     * @param checkValues
     *            Map
     */
    public ServerShowDialogMessage(Component comp, IComponentIdentifier id,
            Map checkValues) {
        m_component = comp;
        m_compId = id;
        m_checkValues = checkValues;
    }

    /**
     * the component
     * 
     * @return the component
     */
    public Component getComponent() {
        return m_component;
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.SERVER_SHOW_DIALOG_COMMAND;
    }

    /**
     * what dialog should be opened/closed
     * 
     * @return int
     */
    public int getAction() {
        return m_action;
    }

    /**
     * what dialog should be opened/closed
     * 
     * @param action
     *            int
     */
    public void setAction(int action) {
        m_action = action;
    }

    /**
     * Gets the CAP logical name
     * 
     * @return The logical name
     */
    public String getLogicalName() {
        return m_logicalName;
    }

    /**
     * Sets the CAP logical name
     * 
     * @param logicalName
     *            the logical name
     */
    public void setLogicalName(String logicalName) {
        m_logicalName = logicalName;
    }

    /**
     * set values to check
     * 
     * @param checkValues
     *            Map
     */
    public void setCheckValues(Map checkValues) {
        m_checkValues = checkValues;
    }

    /** @return values to check */
    public Map getCheckValues() {
        return m_checkValues;
    }

    /** @return IComponentIdentifier */
    public IComponentIdentifier getCompId() {
        return m_compId;
    }

    /** @return Location on Screen */
    public Point getPoint() {
        return m_point;
    }

    /**
     * @param point
     *            Location on screen
     */
    public void setPoint(Point point) {
        m_point = point;
    }
}