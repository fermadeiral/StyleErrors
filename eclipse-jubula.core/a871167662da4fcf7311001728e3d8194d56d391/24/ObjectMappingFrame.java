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
/**
 * 
 */
package org.eclipse.jubula.autagent.common.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultEditorKit;

import org.eclipse.jubula.tools.internal.om.IObjectMappingObserver;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.jubula.tools.internal.utils.SerializationUtils;
import org.eclipse.jubula.autagent.common.desktop.DesktopIntegration;
import org.eclipse.jubula.autagent.common.i18n.Messages;
import org.eclipse.jubula.tools.internal.objects.ComponentIdentifier;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a class for the ObjectMapping Window. it implements the {@link IObjectMappingObserver} 
 * to get an encoded {@link ComponentIdentifier} which can be used in the API. Also the windows 
 * is displaying information about the class and the component name as given.
 * @author BREDEX GmbH
 *
 */
public enum ObjectMappingFrame  implements IObjectMappingObserver {
    /** the instance of the singleton */
    INSTANCE;
    
    /** the logger */
    private static final Logger LOG =
            LoggerFactory.getLogger(ObjectMappingFrame.class);
    
    /** label for the currently active AUT in OMM */
    private JLabel m_componentOMAut;
    /** textfield for the component name */
    private JTextField m_componentName = null;
    /** textfield for the component class */
    private JTextField m_componentClass = null;
    /** textfield for the base64 encoded component identifier */
    private JTextField m_componentIdentfier = null;
    /** the main frame */
    private JFrame m_frame = null;
    /** first Startup of the frame? */
    private boolean m_firstStartup = false;
    
    /** the {@link DesktopIntegration} */
    private DesktopIntegration m_desktopIntegration = null;
    
    /**
     * The Listener for the windows closing
     */
    class AskBeforeCloseWindowListener extends WindowAdapter {
        /** {@inheritDoc} */
        public void windowClosing(WindowEvent e) {
            int result = JOptionPane.showConfirmDialog(m_frame,
                    Messages.CloseWindowQuestion, Messages.CloseWindowTitle,
                    JOptionPane.YES_NO_OPTION);
            if (result == 0) {
                m_frame.dispose();
                AutIdentifier identifier =
                        DesktopIntegration.getObjectMappingAUT();
                if (identifier != null) {
                    ActionListener stopListener = getDesktopIntegration()
                            .createStopListener(identifier);
                    stopListener.actionPerformed(null);
                }
            }
        }
    }

    /**
     * 
     */
    public void showObjectMappingPanel() {
        if (!m_firstStartup) {
            createFrame();
            m_firstStartup = true;
        } else {
            if (!m_frame.isVisible()) {
                m_frame.setVisible(true);
            }
            m_frame.toFront();
        }

    }
    /**
     * create the frame with its contents
     */
    private void createFrame() {
        m_frame = new JFrame();
        m_frame.setTitle(Messages.OMPaneTitle);
        m_frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        m_frame.addWindowListener(new AskBeforeCloseWindowListener());
        createMenuBar();
        createContent();
        m_frame.setSize(800, 200);
        m_frame.setVisible(true);
    }

    /**
     * creates the content for the frame
     */
    private void createContent() {
        GridBagLayout layout = new GridBagLayout();
        JPanel panel = new JPanel(layout);
        GridBagConstraints constraints = createDefaultConstraints();
        
        createAndAddLabel(panel, Messages.AutInOMM, 0);
        createAndAddLabel(panel, Messages.ComponentName, 1);
        createAndAddLabel(panel, Messages.ComponentType, 2);
        createAndAddLabel(panel, Messages.ComponentIdentifier, 3);
        
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 2;
        m_componentOMAut = new JLabel();
        AutIdentifier omAUT = DesktopIntegration.getObjectMappingAUT();
        if (omAUT == null) {
            setOMAutName(null);
        } else {
            setOMAutName(omAUT.getID());
        }
        panel.add(m_componentOMAut, constraints);
        
        constraints.gridy = 1;
        m_componentName = new JTextField();
        m_componentName.setEditable(false);
        panel.add(m_componentName, constraints);
        
        constraints.gridy = 2;
        m_componentClass = new JTextField();
        m_componentClass.setEditable(false);
        panel.add(m_componentClass, constraints);
        
        constraints.gridy = 3;
        m_componentIdentfier = new JTextField();
        m_componentIdentfier.setEditable(false);
        m_componentIdentfier.addMouseListener(new MouseAdapter() {
            /**
             * selects the complete String
             */
            public void mouseClicked(MouseEvent e) {
                m_componentIdentfier.selectAll();
            }
        });
        
        JMenuItem menuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
        menuItem.setText(Messages.CopyToClipboard);
        menuItem.setMnemonic(KeyEvent.VK_C);
        JPopupMenu popup = new JPopupMenu();
        popup.add(menuItem);
        m_componentIdentfier.add(popup);
        m_componentIdentfier.setComponentPopupMenu(popup);
        panel.add(m_componentIdentfier, constraints);
        
        m_frame.add(panel);
    }
    
    /**
     * Sets the name of the AUT that is currently in OMM.
     * @param name the name of the AUT or {@code null}
     */
    public void setOMAutName(String name) {
        if (m_componentOMAut != null) {
            if (name == null) {
                m_componentOMAut.setForeground(Color.LIGHT_GRAY);
                m_componentOMAut.setText(Messages.NoAutInOMM);
            } else {
                m_componentOMAut.setForeground(Color.DARK_GRAY);
                m_componentOMAut.setText(name);
            }
        }
    }
    /**
     * 
     * @return the {@link DesktopIntegration} mainly used for creating the Listener
     */
    public DesktopIntegration getDesktopIntegration() {
        return m_desktopIntegration;
    }
    
    /**
     * 
     * @param desktopIntegration {@link DesktopIntegration} for creating the listener
     */
    public void setDesktopIntegration(DesktopIntegration desktopIntegration) {
        m_desktopIntegration = desktopIntegration;
    }
    /**
     * 
     * @param panel the panel where to add the label
     * @param text the text of the label
     * @param y the y coordinate in the grid bag
     */
    public void createAndAddLabel(JPanel panel, String text, int y) {
        GridBagConstraints constraints = createDefaultConstraints();
        constraints.gridx = 0;
        constraints.gridy = y;
        JLabel componentIdLabel = new JLabel(text);
        panel.add(componentIdLabel, constraints);
    }
    
    /**
     * @return default {@link GridBagConstraints} for the frame
     */
    private GridBagConstraints createDefaultConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(2, 10, 0, 10);
        constraints.anchor = GridBagConstraints.WEST;
        return constraints;
    }
    /**
     * creates the menubar with action listeners
     */
    private void createMenuBar() {
        JMenuBar menubar = new JMenuBar();
        m_frame.setJMenuBar(menubar);
        JMenu fileMenu = new JMenu(Messages.FileMenu);
        JMenuItem exitMenu = new JMenuItem(Messages.CloseMenu);
        exitMenu.addActionListener(new ActionListener() {
            
            /**  */
            public void actionPerformed(ActionEvent e) {
                m_frame.dispose();
            }
        });
        JMenuItem settingsMenu = new JMenuItem(Messages.SettingsMenu);
        settingsMenu.addActionListener(new ActionListener() {
            private JFrame m_settings = new ObjectMappingSettingsFrame();
            @Override
            public void actionPerformed(ActionEvent e) {
                m_settings = new ObjectMappingSettingsFrame();
                m_settings.setVisible(true);
                m_settings.toFront();
            }
        });
        fileMenu.add(settingsMenu);
        fileMenu.add(exitMenu);
        menubar.add(fileMenu);
        
    }

    /**
     *  {@inheritDoc}
     */
    public void update(int event, Object obj) {
        if (event == IObjectMappingObserver.EVENT_COMPONENT_MAPPED) {
            IComponentIdentifier[] identifier = (IComponentIdentifier[]) obj;
            if (identifier.length > 0
                    && identifier[0] instanceof ComponentIdentifier) {
                ComponentIdentifier id = (ComponentIdentifier) identifier[0];
                m_componentName.setText(identifier[0].getComponentName());
                m_componentClass.setText(identifier[0].getSupportedClassName());
                try {
                    m_componentIdentfier.setText(SerializationUtils.encode(id));
                } catch (IOException e) {
                    LOG.error("error occurred during generation of Identifier", //$NON-NLS-1$
                            e);
                    m_componentIdentfier.setText(
                            "error generating Identifier please see logs"); //$NON-NLS-1$
                }
            }

            if (!m_frame.isVisible()) {
                m_frame.setVisible(true);
            }
        }
    }
}
