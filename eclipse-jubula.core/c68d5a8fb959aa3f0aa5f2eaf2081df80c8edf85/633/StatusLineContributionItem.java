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
package org.eclipse.jubula.client.ui.rcp.widgets;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author BREDEX GmbH
 * @created 29.05.2006
 */
public class StatusLineContributionItem extends ContributionItem {
    /** default char width of the displayed text */
    public static final int DEFAULT_CHAR_WIDTH = 40;

    /** max. char width of the displayed text */
    private int m_charWidth;
    
    /** <code>m_textLabel</code> label for text */
    private CLabel m_textLabel;
    
    /** <code>m_imageLabel</code> label for image */
    private CLabel m_imageLabel;

    /** <code>m_text</code> text for item */
    private String m_text = null;
    
    /** <code>m_image</code> image for item */
    private Image m_image = null;

    /**
     * The composite into which this contribution item has been placed. This
     * will be <code>null</code> if this instance has not yet been
     * initialized.
     */
    private Composite m_statusLine = null;
    
    /** width of the displayed text */
    private int m_widthHint = -1;
    
    /** height of the displayed text */
    private int m_heightHint = -1;
    /**
     * @param id ID of the StatusLineContributionItem.
     */
    public StatusLineContributionItem(String id) {
        this(id, DEFAULT_CHAR_WIDTH);
    }

    /**
     * @param id
     *            StatusLineContributionItem
     * @param charWidth
     *            max. char width of the displayed text.
     */
    public StatusLineContributionItem(String id, int charWidth) {
        super(id);
        m_charWidth = charWidth;
        setVisible(false); // no text to start with
    }

    /**
     * {@inheritDoc}
     */
    public void fill(Composite parent) {
        m_statusLine = parent;

        Label sep = new Label(parent, SWT.SEPARATOR);
        m_textLabel = new CLabel(m_statusLine, SWT.SHADOW_NONE);

        if (m_widthHint < 0) {
            GC gc = new GC(m_statusLine);
            gc.setFont(m_statusLine.getFont());
            FontMetrics fm = gc.getFontMetrics();
            m_widthHint = fm.getAverageCharWidth() * m_charWidth;
            m_heightHint = fm.getHeight();
            gc.dispose();
        }

        StatusLineLayoutData data = new StatusLineLayoutData();
        m_textLabel.setLayoutData(data);
        m_textLabel.setText(m_text);
        
        if (m_image != null) {
            m_imageLabel = new CLabel(parent, SWT.NONE);
            m_imageLabel.setImage(m_image);
        }

        data = new StatusLineLayoutData();
        data.heightHint = m_heightHint;
        sep.setLayoutData(data);
    }
//
//    /**
//     * @return displayed text
//     */
//    public String getText() {
//        return m_text;
//    }

    /**
     * @param text displayed text to set
     */
    public void setText(String text) {
        if (text == null) {
            throw new NullPointerException();
        }
        m_text = text;
        if (m_textLabel != null && !m_textLabel.isDisposed()) {
            m_textLabel.setText(m_text);
        }
        if (m_text.length() == 0) {
            if (isVisible()) {
                updateContributionManager(false);
            }
        } else {
            if (!isVisible()) {
                updateContributionManager(true);
            }
        }
    }

    /**
     * Sets the conribution item visible and updates the contribution manager.
     * @param visible set visible.
     */
    private void updateContributionManager(boolean visible) {
        setVisible(visible);
        IContributionManager contributionManager = getParent();
        if (contributionManager != null) {
            contributionManager.update(true);
        }
    }

    /**
     * @param image The displayed image to set.
     */
    public void setImage(Image image) {
        m_image = image;
    }
    
    /**
     * @param text displayed text to set
     * @param image displayed image to set
     */
    public void setMessage(Image image, String text) {
        setImage(image);
        setText(text);
    }
}