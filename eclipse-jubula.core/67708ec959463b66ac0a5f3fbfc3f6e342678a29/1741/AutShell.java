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
package org.eclipse.jubula.examples.aut.adder.swt.gui;

import org.eclipse.jubula.examples.aut.adder.swt.model.PlusOperator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * The frame of the Application Under Test.
 * 
 * @author BREDEX GmbH
 * @created 23.02.2006
 */
public class AutShell extends Shell {
    /** constant for naming the swt components */
    private static final String WIDGET_NAME = "TEST_COMP_NAME"; //$NON-NLS-1$
    /** sum field */
    private Text m_sumField;
    /** value2 field */
    private Text m_value2Field;
    /** value1 field */
    private Text m_value1Field;
    /** equals button */
    private Button m_equalsButton;
    /** reset menu item */
    private MenuItem m_resetMenuItem;
    /** exit menu item */
    private MenuItem m_exitMenuItem;
    /** about menu item */
    private MenuItem m_aboutMenuItem;
    /** operator label */
    private Label m_operator;

    /**
     * Constructor of AutShell Sets the title of the shell and initialize the gui.
     * @param title A <code>String</code> value.
     */
    public AutShell(String title) {
        super(SWT.MIN);
        setText(title);
        initMenu();
        initControls();
        pack();
    }

    /**
     * Inits the menu.
     */
    private void initMenu() {
        Menu menuBar = new Menu(this, SWT.BAR);
        setMenuBar(menuBar);
        
        MenuItem fileMenu = new MenuItem(menuBar, SWT.CASCADE);
        MenuItem helpMenu = new MenuItem(menuBar, SWT.CASCADE);
        
        Menu fileSubMenu = new Menu(this, SWT.DROP_DOWN);
        fileMenu.setMenu(fileSubMenu);
        Menu helpSubMenu = new Menu(this, SWT.DROP_DOWN);
        helpMenu.setMenu(helpSubMenu);
        
        m_resetMenuItem = new MenuItem(fileSubMenu, SWT.PUSH);
        m_exitMenuItem = new MenuItem(fileSubMenu, SWT.PUSH);
        m_aboutMenuItem = new MenuItem(helpSubMenu, SWT.PUSH);
        fileMenu.setText("File"); //$NON-NLS-1$
        helpMenu.setText("Help"); //$NON-NLS-1$
        m_resetMenuItem.setText("Reset"); //$NON-NLS-1$
        m_exitMenuItem.setText("Exit"); //$NON-NLS-1$
        m_aboutMenuItem.setText("About"); //$NON-NLS-1$   
    }

    /**
     * {@inheritDoc}
     */
    protected void checkSubclass() {
        // do nothing
    }
    
    /**
     * Inits the layout.
     */
    private void initControls() {
        GridLayout shellLayout = new GridLayout ();
        shellLayout.marginWidth = 100;
        setLayout (shellLayout);
        
        Composite composite = new Composite(this, SWT.NONE);
        composite.setData(WIDGET_NAME, "SWTAdder.Composite"); //$NON-NLS-1$
        GridLayout compositeLayout = new GridLayout (2, false);
        composite.setLayout (compositeLayout);
        GridData data = new GridData ();
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = false;
        data.grabExcessVerticalSpace = false;
        composite.setLayoutData (data);
        
        new Label(composite, SWT.NONE);
        
        m_value1Field = new Text (composite, SWT.BORDER | SWT.RIGHT);
        GridData value1FieldData = new GridData ();
        value1FieldData.horizontalAlignment = GridData.FILL;
        value1FieldData.verticalAlignment = GridData.BEGINNING;
        value1FieldData.grabExcessHorizontalSpace = true;
        m_value1Field.setLayoutData (value1FieldData);
        
        m_operator = new Label (composite, SWT.NONE);
        m_operator.setData(WIDGET_NAME, "SWTAdder.Operator"); //$NON-NLS-1$
        PlusOperator plus = new PlusOperator();
        m_operator.setText (plus.toString()); 
        m_operator.setData("op", plus); //$NON-NLS-1$
        GridData operatorData = new GridData ();
        operatorData.horizontalAlignment = GridData.END;
        m_operator.setLayoutData (operatorData);
        
        m_value2Field = new Text (composite, SWT.BORDER | SWT.RIGHT);
        GridData value2FieldData = new GridData ();
        value2FieldData.horizontalAlignment = GridData.FILL;
        value2FieldData.verticalAlignment = GridData.BEGINNING;
        value2FieldData.grabExcessHorizontalSpace = true;
        m_value2Field.setLayoutData (value2FieldData);
        
        m_equalsButton = new Button (composite, SWT.PUSH);
        m_equalsButton.setText ("="); //$NON-NLS-1$
        m_equalsButton.setData(WIDGET_NAME, "SWTAdder.EqualsButton"); //$NON-NLS-1$
        GridData equalsButtonData = new GridData ();
        equalsButtonData.horizontalAlignment = GridData.END;
        m_equalsButton.setLayoutData (equalsButtonData);
        
        m_sumField = new Text (composite, SWT.BORDER | SWT.RIGHT);
        m_sumField.setData(WIDGET_NAME, "SWTAdder.SumField"); //$NON-NLS-1$
        m_sumField.setEditable(false);
        GridData sumFieldData = new GridData ();
        sumFieldData.horizontalAlignment = GridData.FILL;
        sumFieldData.verticalAlignment = GridData.BEGINNING;
        sumFieldData.grabExcessHorizontalSpace = true;
        m_sumField.setLayoutData (sumFieldData);
    }

    /**
     * @return Returns the equalsButton.
     */
    public Button getEqualsButton() {
        return m_equalsButton;
    }

    /**
     * @return Returns the sumField.
     */
    public Text getSumField() {
        return m_sumField;
    }

    /**
     * @return Returns the value1Field.
     */
    public Text getValue1Field() {
        return m_value1Field;
    }

    /**
     * @return Returns the value2Field.
     */
    public Text getValue2Field() {
        return m_value2Field;
    }

    /**
     * @return Returns the aboutMenuItem.
     */
    public MenuItem getAboutMenuItem() {
        return m_aboutMenuItem;
    }

    /**
     * @return Returns the exitMenuItem.
     */
    public MenuItem getExitMenuItem() {
        return m_exitMenuItem;
    }

    /**
     * @return Returns the resetMenuItem.
     */
    public MenuItem getResetMenuItem() {
        return m_resetMenuItem;
    }

    /**
     * @return Returns the operator.
     */
    public Label getOperator() {
        return m_operator;
    }
}