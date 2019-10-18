/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.swt.components;

import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.eclipse.jubula.rc.common.implclasses.table.Cell;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;

/**
 * This class represents a cell in a SWT Table or Tree Table.
 * @author BREDEX GmbH
 * @created 21.08.2015
 */
public class SWTCell extends Cell implements DynaBean {
    
    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
            SWTCell.class);
    
    /** constant for background property */
    private static final String BACKGROUND = "background"; //$NON-NLS-1$
    /** constant for bounds property */
    private static final String BOUNDS = "bounds"; //$NON-NLS-1$
    /** constant for font property */
    private static final String FONT = "font"; //$NON-NLS-1$
    /** constant for foreground property */
    private static final String FOREGROUND = "foreground"; //$NON-NLS-1$
    /** constant for grayed property */
    private static final String GRAYED = "grayed"; //$NON-NLS-1$
    /** constant for image property */
    private static final String IMAGE = "image"; //$NON-NLS-1$
    /** constant for style property */
    private static final String STYLE = "style"; //$NON-NLS-1$
    /** constant for text property */
    private static final String TEXT = "text"; //$NON-NLS-1$

    /** the dynamic bean */
    private DynaBean m_bean;
    
    /** the properties */
    private DynaProperty[] m_properties = new DynaProperty[] {
        new DynaProperty(BACKGROUND, Color.class),
        new DynaProperty(BOUNDS, Rectangle.class),
        new DynaProperty(FONT, Font.class),
        new DynaProperty(FOREGROUND, Color.class),
        new DynaProperty(GRAYED, Boolean.class),
        new DynaProperty(IMAGE, Image.class),
        new DynaProperty(STYLE, Integer.class),
        new DynaProperty(TEXT, String.class)
    };


    /**
     * Creates a new Cell instance.
     * @param row The zero based row of the cell.
     * @param col The zero based column of the cell.
     * @param item The table item containing the actual cell
     */
    public SWTCell(int row, int col, TableItem item) {
        super(row, col);
        try {
            m_bean = new BasicDynaClass("cell", null, m_properties).newInstance(); //$NON-NLS-1$
            set(BACKGROUND, item.getBackground(col));
            set(BOUNDS, item.getBounds(col));
            set(FONT, item.getFont(col));
            set(FOREGROUND, item.getForeground(col));
            set(GRAYED, item.getGrayed());
            set(IMAGE, item.getImage(col));
            set(STYLE, item.getStyle());
            set(TEXT, item.getText(col));
        } catch (IllegalAccessException e) {
            log.error("Error while creating instance of SWT table cell", e); //$NON-NLS-1$
        } catch (InstantiationException e) {
            log.error("Error while creating instance of SWT table cell", e); //$NON-NLS-1$
        } 
    }


    /**
     * Creates a new Cell instance.
     * @param row The zero based row of the cell.
     * @param col The zero based column of the cell.
     * @param item The tree table item containing the actual cell
     */
    public SWTCell(int row, int col, TreeItem item) {
        super(row, col);
        try {
            m_bean = new BasicDynaClass("cell", null, m_properties).newInstance(); //$NON-NLS-1$
            set(BACKGROUND, item.getBackground(col));
            set(BOUNDS, item.getBounds(col));
            set(FONT, item.getFont(col));
            set(FOREGROUND, item.getForeground(col));
            set(GRAYED, item.getGrayed());
            set(IMAGE, item.getImage(col));
            set(STYLE, item.getStyle());
            set(TEXT, item.getText(col));
        } catch (IllegalAccessException e) {
            log.error("Error while creating instance of SWT tree table cell", e); //$NON-NLS-1$
        } catch (InstantiationException e) {
            log.error("Error while creating instance of SWT tree table cell", e); //$NON-NLS-1$
        } 
    }

    /** {@inheritDoc} */
    public boolean contains(String name, String key) {
        return m_bean.contains(name, key);
    }

    /** {@inheritDoc} */
    public Object get(String name) {
        return m_bean.get(name);
    }

    /** {@inheritDoc} */
    public Object get(String name, int index) {
        return m_bean.get(name, index);
    }

    /** {@inheritDoc} */
    public Object get(String name, String key) {
        return m_bean.get(name, key);
    }

    /** {@inheritDoc} */
    public DynaClass getDynaClass() {
        return m_bean.getDynaClass();
    }

    /** {@inheritDoc} */
    public void remove(String name, String key) {
        m_bean.remove(name, key);
    }

    /** {@inheritDoc} */
    public void set(String name, Object value) {
        m_bean.set(name, value);
    }

    /** {@inheritDoc} */
    public void set(String name, int index, Object value) {
        m_bean.set(name, index, value);
    }

    /** {@inheritDoc} */
    public void set(String name, String key, Object value) {
        m_bean.set(name, key, value);
    }

}
