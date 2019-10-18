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
package org.eclipse.jubula.rc.swt.tester.util;

import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.swt.driver.EventThreadQueuerSwtImpl;
import org.eclipse.jubula.tools.internal.utils.StringParsing;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;


/**
 * Creates a tooltip that disappears after a specified amount of time.
 *
 * @author BREDEX GmbH
 * @created Jul 24, 2007
 */
public class SimulatedTooltip extends Thread {

    /** the logger */
    private static AutServerLogger log = 
        new AutServerLogger(SimulatedTooltip.class);
    
    /** Font of the tooltip */
    private static final String TOOLTIP_FONT = "MS Mincho"; //$NON-NLS-1$

    /** Background color of the tooltip */
    private static final Color TOOLTIP_BG_COLOR = 
        new Color(null, 255, 255, 231);

    /** Background color of the progress bar */
    private static final Color PROGRESSBAR_BG_COLOR = 
        new Color(null, 255, 255, 220);
    
    /** Foreground color of the progress bar */
    private static final Color PROGRESSBAR_FG_COLOR = 
        new Color(null, 0, 0, 0);
    
    /** Height of the progress bar */
    private static final int P_HEIGHT = 4;

    /** The tooltip window */
    private final Shell m_dialog;

    /** Width of the tooltip window */
    private final int m_dialogWidth;

    /** Height of the tooltip window */
    private int m_dialogHeight;

    /** The size of the displayed text in points */
    private final int m_textSize;

    /** The amount of time the tooltip will remain open */
    private final int m_timeout;

    /** The bounds of the owning component */
    private final Rectangle m_componentBounds;

    /** The text to display in the tooltip */
    private final String m_displayText;
    
    /**  The label to display the text */
    private final Label m_label;
    
    /** The font used to display the tooltip */
    private Font m_font = null;
    
    /**
     * Constructor
     * 
     * @param timePerWord
     *            The amount of time, in milliseconds, that it will take to
     *            display a word
     * @param popupText
     *            The text to display
     * @param dialogWidth
     *            The width of the tooltip window
     * @param textSize
     *            The size of the displayed text in points
     * @param componentBounds
     *            The bounds of the component for which this tooltip is being
     *            shown
     */
    public SimulatedTooltip(int timePerWord, String popupText, int dialogWidth,
            int textSize, Rectangle componentBounds) {
        
        m_dialog = new Shell(SWT.ON_TOP);

        m_dialog.setBackground(TOOLTIP_BG_COLOR);

        m_dialog.setText("Information"); //$NON-NLS-1$
        m_textSize = textSize;
        m_timeout = StringParsing.countWords(popupText) * timePerWord;
        m_displayText = popupText;
        m_dialogWidth = dialogWidth;
        m_label = new Label(m_dialog, SWT.WRAP);
        m_dialog.addDisposeListener(new DisposeListener() {

            public void widgetDisposed(DisposeEvent e) {
                if (m_font != null) {
                    m_font.dispose();
                    m_font = null;
                }
            }
            
        });

        m_componentBounds = componentBounds;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void run() {
        log.debug(Thread.currentThread().toString()
                + ": managing started"); //$NON-NLS-1$
        final IEventThreadQueuer queuer = new EventThreadQueuerSwtImpl();
        
        queuer.invokeAndWait("openSimulatedTooltipShell", new IRunnable<Void>() { //$NON-NLS-1$
            public Void run() {
                log.debug(Thread.currentThread().toString()
                        + ": popup started"); //$NON-NLS-1$
                openShell();
                return null;
            }
        });

        TimeUtil.delay(m_timeout);

        queuer.invokeAndWait("disposeSimulatedTooltipShell", new IRunnable<Void>() { //$NON-NLS-1$
            public Void run() {
                if (m_dialog != null && !m_dialog.isDisposed()) {
                    m_dialog.dispose();
                    log.debug(Thread.currentThread().toString()
                            + ": popup stopped"); //$NON-NLS-1$
                }
                return null;
            }
        });

        log.debug(Thread.currentThread().toString()
                + ": managing stopped"); //$NON-NLS-1$
    }

    /**
     * Opens a simulated tooltip shell
     *
     */
    public void openShell() {
        GridLayout gl = new GridLayout();
        m_dialog.setLayout(gl);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        m_dialog.setLayoutData(gd);
        m_dialog.setBounds(100, 100, 200, 0);
        
        m_dialog.open();
        m_dialog.setVisible(false);        
        createText();
        createProgressBar();

        // set the label and get the size
        m_dialog.setSize(m_dialogWidth, 1000); 
        // 2 components + top + bottom
        int margins = m_label.getLocation().y * 4; 
        m_dialog.setSize(m_dialogWidth, 
                m_label.getSize().y + P_HEIGHT +  margins);
        m_dialogHeight = m_dialog.getBounds().height;
        setLocation();        
        m_dialog.setVisible(true);
        
    }

    /**
     * Creates the textarea in the current shell
     * 
     */
    public void createText() {
        m_font = new Font(m_dialog.getDisplay(), TOOLTIP_FONT,
                m_textSize, SWT.NORMAL);
        
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        m_label.setLayoutData(gd);
        m_label.setFont(m_font);
        m_label.setBackground(TOOLTIP_BG_COLOR);
        m_label.setText(m_displayText);
    }

    /**
     * Positions the shell so that it is visible on screen 
     */
    public void setLocation() {
        int cWidth = m_componentBounds.width;
        int cHeight = m_componentBounds.height;

        int x = m_componentBounds.x + cWidth;
        int y = m_componentBounds.y + cHeight;

        Rectangle bounds = Display.getDefault().getBounds();
        int screenWidth = bounds.width;
        int screenHeight = bounds.height;

        int xOffset = 1;
        int yOffset = 1;

        int xMin = 5 + xOffset;
        int yMin = 5 + yOffset;

        if (x + m_dialogWidth + xMin >= screenWidth) {
            x = x - m_dialogWidth - xOffset - cWidth;
        } else {
            x = x + xOffset;
        }

        if (y + m_dialogHeight + yMin >= screenHeight) {
            y = y - m_dialogHeight - yOffset - cHeight;
        } else {
            y = y + yOffset;
        }

        m_dialog.setLocation(x, y);
    }

    /**
     * Every tooltip has its own progress bar 
     */
    public void createProgressBar() {
        final ProgressBar bar = new ProgressBar(m_dialog, SWT.HORIZONTAL);

        GridData gd = new GridData();
        gd.heightHint = P_HEIGHT;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        bar.setLayoutData(gd);
        bar.setForeground(PROGRESSBAR_FG_COLOR);
        bar.setBackground(PROGRESSBAR_BG_COLOR);
        bar.setMaximum(m_timeout);
        final int maximum = bar.getMaximum();

        Thread progressBarThread = new Thread() {
            public void run() {
                int refreshIntervall = 25;
                // Calculate how often an update has to be done
                int times = Math.round(m_timeout / refreshIntervall) - 1;
                // Calculate how much the progress bar has to be incremented per update
                int increment = Math.round(maximum / times) + 1;
                for (final int[] i = new int[1]; 
                    i[0] <= maximum; i[0] += increment) {
                    TimeUtil.delay(refreshIntervall);
                    if (m_dialog != null 
                        && m_dialog.getDisplay().isDisposed()) {
                        
                        return;
                    }
                    m_dialog.getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            if (bar.isDisposed()) {
                                return;
                            }
                            bar.setSelection(i[0]);
                        }
                    });
                }
            }
        };

        progressBarThread.start();
    }

}
