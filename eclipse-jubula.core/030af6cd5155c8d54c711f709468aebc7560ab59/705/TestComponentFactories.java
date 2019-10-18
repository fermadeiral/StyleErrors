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
package org.eclipse.jubula.qa.api.factories;

import org.eclipse.jubula.communication.CAP;
import org.eclipse.jubula.qa.api.om.OM_factories;
import org.eclipse.jubula.toolkit.base.AbstractComponents;
import org.eclipse.jubula.toolkit.base.components.GraphicsComponent;
import org.eclipse.jubula.toolkit.concrete.ConcreteComponents;
import org.eclipse.jubula.toolkit.concrete.components.ButtonComponent;
import org.eclipse.jubula.toolkit.concrete.components.ComboComponent;
import org.eclipse.jubula.toolkit.concrete.components.ListComponent;
import org.eclipse.jubula.toolkit.concrete.components.TabComponent;
import org.eclipse.jubula.toolkit.concrete.components.TableComponent;
import org.eclipse.jubula.toolkit.concrete.components.TextComponent;
import org.eclipse.jubula.toolkit.concrete.components.TextInputComponent;
import org.eclipse.jubula.toolkit.concrete.components.TreeComponent;
import org.eclipse.jubula.toolkit.enums.ValueSets.BinaryChoice;
import org.eclipse.jubula.toolkit.enums.ValueSets.Direction;
import org.eclipse.jubula.toolkit.enums.ValueSets.InteractionMode;
import org.eclipse.jubula.toolkit.enums.ValueSets.Modifier;
import org.eclipse.jubula.toolkit.enums.ValueSets.Operator;
import org.eclipse.jubula.toolkit.enums.ValueSets.SearchType;
import org.eclipse.jubula.toolkit.enums.ValueSets.TreeDirection;
import org.eclipse.jubula.toolkit.enums.ValueSets.Unit;
import org.eclipse.jubula.tools.ComponentIdentifier;
import org.junit.Assert;
import org.junit.Test;

/**
 * Class for testing the factories
 */
public class TestComponentFactories {
    
    /**
     * used for textpath and indexpath strings
     */
    private static final String TEST = "test"; //$NON-NLS-1$
    /**
     * test text input component
     */
    @Test
    public void testTextInputComp() {
        
        ComponentIdentifier identifier = 
                OM_factories.TextField_pfr_txf;
        
        TextInputComponent textField = ConcreteComponents
                .createTextInputComponent(identifier);
        Assert.assertNotNull(textField);
        CAP cap1 = textField.checkEditability(true);
        Assert.assertNotNull(cap1);
        CAP cap2 = textField.checkText(TEST, Operator.equals);
        Assert.assertNotNull(cap2);
        CAP cap3 = textField.inputText(TEST);
        Assert.assertNotNull(cap3);
        CAP cap4 = textField.insertTextAfterIndex(TEST, 1);
        Assert.assertNotNull(cap4);
        CAP cap5 = textField.insertTextBeforeAfterPattern(
                TEST, "pattern", Operator.equals, true);  //$NON-NLS-1$
        Assert.assertNotNull(cap5);
        CAP cap6 = textField.replaceText(TEST);
        Assert.assertNotNull(cap6);
        CAP cap7 = textField.selectAll();
        Assert.assertNotNull(cap7);
        CAP cap8 = textField.selectPattern(TEST, Operator.equals);
        Assert.assertNotNull(cap8);
        Assert.assertTrue(textField 
                instanceof 
                org.eclipse.jubula.toolkit.base.components.TextComponent);
        Assert.assertTrue(textField 
                instanceof 
                org.eclipse.jubula.toolkit.base.components.GraphicsComponent);
        Assert.assertTrue(textField 
                instanceof 
                org.eclipse.jubula.toolkit.base.components.TextInputComponent);
    }
    
    /**
     * test button component
     */
    @Test
    public void testButtonComp() {
        ComponentIdentifier identifier = 
                OM_factories.Button_button_btn;
        ButtonComponent button = ConcreteComponents
                .createButtonComponent(identifier);
        Assert.assertNotNull(button);
        CAP cap1 = button.checkSelection(true);
        Assert.assertNotNull(cap1);
        Assert.assertTrue(button 
                instanceof 
                org.eclipse.jubula.toolkit.base.components.TextComponent);
        Assert.assertTrue(button 
                instanceof 
                org.eclipse.jubula.toolkit.base.components.GraphicsComponent);
        Assert.assertTrue(button 
                instanceof 
                org.eclipse.jubula.toolkit.base.components.ButtonComponent);
    }
    /**
     * test combo component
     */
    @Test
    public void testComboComponent() {
        ComponentIdentifier identifier = 
                OM_factories.ComboBox_disabled_cbx;
        ComboComponent combo = ConcreteComponents
                .createComboComponent(identifier);
        Assert.assertNotNull(combo);
        CAP cap1 = combo.checkSelectionOfEntryByIndex(TEST, true);
        Assert.assertNotNull(cap1);
        CAP cap2 = combo.selectEntryByIndex(TEST);
        Assert.assertNotNull(cap2);
        CAP cap3 = combo.selectEntryByValue(TEST, 
                Operator.equals, SearchType.absolute);
        Assert.assertNotNull(cap3);
        Assert.assertTrue(combo 
                instanceof 
                org.eclipse.jubula.toolkit.base.components.TextComponent);
        Assert.assertTrue(combo 
                instanceof 
                org.eclipse.jubula.toolkit.base.components.TextInputComponent);
        Assert.assertTrue(combo 
                instanceof 
                org.eclipse.jubula.toolkit.base.components.GraphicsComponent);
    }
    /**
     * test list component
     */
    @Test
    public void testListComponent() {
        ComponentIdentifier identifier = 
                OM_factories.List_selectEntryValue_lst;
        ListComponent list = ConcreteComponents
                .createListComponent(identifier);
        Assert.assertNotNull(list);
        CAP cap1 = list.selectEntryByValueS(
                TEST, Operator.equals, SearchType.absolute,
                BinaryChoice.no, InteractionMode.primary, 2);
        Assert.assertNotNull(cap1);
        CAP cap2 = list.selectEntryByIndexIndices(
                TEST, BinaryChoice.yes, InteractionMode.primary, 1);
        Assert.assertNotNull(cap2);
        CAP cap3 = list.dropOnEntryByValue(
                TEST, Operator.equals, SearchType.absolute, 1000);
        Assert.assertNotNull(cap3);
        CAP cap4 = list.dropOnEntryByIndex(3, 1000);
        Assert.assertNotNull(cap4);
        CAP cap5 = list.dragEntryByValue(
                InteractionMode.primary, Modifier.values(), TEST,
                Operator.equals, SearchType.relative);
        Assert.assertNotNull(cap5);
        CAP cap6 = list.dragEntryByIndex(
                InteractionMode.primary, Modifier.values(), 5);
        Assert.assertNotNull(cap6);
        CAP cap7 = list.checkSelectionOfEntryByValue(
                TEST, Operator.equals, false);
        Assert.assertNotNull(cap7);
        CAP cap8 = list.checkSelectionOfEntryByIndex(
                TEST, true);
        Assert.assertNotNull(cap8);
        CAP cap9 = list.checkExistenceOfEntryByValue(
                TEST, Operator.equals, true);
        Assert.assertNotNull(cap9);
        Assert.assertTrue(list 
                instanceof 
                org.eclipse.jubula.toolkit.base.components.TextComponent);
        Assert.assertTrue(list 
                instanceof 
                org.eclipse.jubula.toolkit.base.components.GraphicsComponent);
    }
    /**
     * test tab component
     */
    @Test
    public void testTabComponent() {
        ComponentIdentifier identifier = 
                OM_factories.TabbedPane_existing_tpn;
        TabComponent tab = ConcreteComponents
                .createTabComponent(identifier);
        Assert.assertNotNull(tab);
        CAP cap1 = tab.checkEnablementOfTabByIndex(2, true);
        Assert.assertNotNull(cap1);
        CAP cap2 = tab.checkEnablementOfTabByValue(
                TEST, Operator.equals, false);
        Assert.assertNotNull(cap2);
        CAP cap3 = tab.checkExistenceOfTab(TEST, Operator.equals, false);
        Assert.assertNotNull(cap3);
        CAP cap4 = tab.checkSelectionOfTabByValue(TEST, Operator.equals, true);
        Assert.assertNotNull(cap4);
        CAP cap5 = tab.checkSelectionOfTabByIndex(2, false);
        Assert.assertNotNull(cap5);
        CAP cap6 = tab.checkTextOfTabByIndex(2, TEST, Operator.equals);
        Assert.assertNotNull(cap6);
        CAP cap7 = tab.selectTabByIndex(2);
        Assert.assertNotNull(cap7);
        CAP cap8 = tab.selectTabByValue(TEST, Operator.equals);
        Assert.assertNotNull(cap8);
        Assert.assertTrue(tab 
                instanceof 
                org.eclipse.jubula.toolkit.base.components.GraphicsComponent);
    }
    /**
     * test table checks
     */
    @Test
    public void testTableChecks() {
        ComponentIdentifier identifier = 
                OM_factories.Table_bigTable_tbl;
        TableComponent table = ConcreteComponents
                .createTableComponent(identifier);
        Assert.assertNotNull(table);
        CAP cap1 = table.checkEditabilityOfCellMousePosition(true);
        Assert.assertNotNull(cap1);
        CAP cap2 = table.checkEditabilityOfSelectedCell(true);
        Assert.assertNotNull(cap2);
        CAP cap3 = table.checkEditabilitySpecifyCell(
                true, "2", Operator.equals, "1", Operator.equals); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertNotNull(cap3);
        CAP cap4 = table.checkExistenceOfValueInColumn(
                "2", Operator.equals, "2", Operator.equals, SearchType.absolute, false);  //$NON-NLS-1$//$NON-NLS-2$
        Assert.assertNotNull(cap4);
        CAP cap5 = table.checkExistenceOfValueInRow(
                "1", Operator.equals, "3", Operator.equals, SearchType.absolute, true);    //$NON-NLS-1$//$NON-NLS-2$
        Assert.assertNotNull(cap5);
        CAP cap6 = table.checkSelectionOfCheckboxAtMousePosition(true);
        Assert.assertNotNull(cap6);
        CAP cap7 = table.checkSelectionOfCheckboxInSelectedRow(false);
        Assert.assertNotNull(cap7);
        CAP cap8 = table.checkTextMousePosition(TEST, Operator.matches);
        Assert.assertNotNull(cap8);
        Assert.assertTrue(table 
                instanceof 
                org.eclipse.jubula.toolkit.base.components.TextComponent);
        Assert.assertTrue(table 
                instanceof 
                org.eclipse.jubula.toolkit.base.components.TextInputComponent);
        Assert.assertTrue(table 
                instanceof 
                org.eclipse.jubula.toolkit.base.components.GraphicsComponent);
    }
    /**
     * test table Actions
     */
    @Test
    public void testTableActions() {
        String column = "1"; //$NON-NLS-1$
        String row = "2"; //$NON-NLS-1$
        ComponentIdentifier identifier = 
                OM_factories.Table_bigTable_tbl;
        TableComponent table = ConcreteComponents
                .createTableComponent(identifier);
        Assert.assertNotNull(table);
        CAP cap9 = table.checkTextSpecifyCell(
                TEST, Operator.equals, row, Operator.equals, column,
                Operator.equals);
        Assert.assertNotNull(cap9);
        CAP cap15 = table.dropOnCellFromRow(
                row, Operator.equals, TEST, Operator.equals,
                SearchType.relative, 1000);
        Assert.assertNotNull(cap15);
        CAP cap16 = table.inputTextSpecifyCell(
                TEST, row, Operator.equals, column, Operator.equals);
        Assert.assertNotNull(cap16);
        CAP cap17 = table.move(
                Direction.down, 2, 2, 50, Unit.percent, 
                50, Unit.percent, BinaryChoice.yes);
        Assert.assertNotNull(cap17);
        CAP cap18 = table.selectCell(
                row, Operator.equals, column, Operator.equals, 3,
                50, Unit.percent, 50, Unit.percent, 
                BinaryChoice.yes, InteractionMode.secondary);
        Assert.assertNotNull(cap18);
        CAP cap19 = table.selectValueFromColumn(
                column, Operator.equals, TEST, Operator.equals, 1,
                BinaryChoice.yes, SearchType.relative,
                InteractionMode.secondary);
        Assert.assertNotNull(cap19);
        CAP cap20 = table.selectValueFromRow(
                row, Operator.equals, TEST, Operator.equals, 4,
                BinaryChoice.yes, SearchType.relative,
                InteractionMode.secondary);
        Assert.assertNotNull(cap20);
        CAP cap21 = table.toggleCheckboxAtMousePosition();
        Assert.assertNotNull(cap21);
        CAP cap22 = table.toggleCheckboxInSelectedRow();
        Assert.assertNotNull(cap22);
    }
    /**
     * test table drag and drop
     */
    public void testTableDragAndDrop() {
        String column = "1"; //$NON-NLS-1$
        String row = "2"; //$NON-NLS-1$
        ComponentIdentifier identifier = 
                OM_factories.Table_bigTable_tbl;
        TableComponent table = ConcreteComponents
                .createTableComponent(identifier);
        Assert.assertNotNull(table);
        CAP cap10 = table.dragCell(
                InteractionMode.secondary, Modifier.values(), 
                row, Operator.equals, row, Operator.equals, 50,
                Unit.percent, 50, Unit.percent);
        Assert.assertNotNull(cap10);
        CAP cap11 = table.dragCellFromColumn(
                InteractionMode.secondary, Modifier.values(), 
                column, Operator.equals, TEST, Operator.equals,
                SearchType.relative);
        Assert.assertNotNull(cap11);
        CAP cap12 = table.dragCellFromRow(
                InteractionMode.secondary, Modifier.values(), 
                row, Operator.equals, TEST, Operator.equals,
                SearchType.relative);
        Assert.assertNotNull(cap12);
        CAP cap13 = table.dropOnCell(
                row, Operator.equals, column, Operator.equals, 50,
                Unit.percent, 50, Unit.percent, 1000);
        Assert.assertNotNull(cap13);
        CAP cap14 = table.dropOnCellFromColumn(
                column, Operator.equals, TEST, Operator.equals,
                SearchType.relative, 1000);
        Assert.assertNotNull(cap14);
    }
    /**
     * test text component
     */
    @Test
    public void testTextComponent() {
        ComponentIdentifier identifier = 
                OM_factories.aa_TextArea_Back_btc;
        TextComponent text = ConcreteComponents
                .createTextComponent(identifier);
        Assert.assertNotNull(text);
        Assert.assertTrue(text 
                instanceof 
                org.eclipse.jubula.toolkit.base.components.TextComponent);
        Assert.assertTrue(text 
                instanceof 
                org.eclipse.jubula.toolkit.base.components.GraphicsComponent);
    }
    /**
     * test tree component
     */
    @Test
    public void testTreeComponent() {
        ComponentIdentifier identifier = 
                OM_factories.Tree_existing_tre;
        TreeComponent tree = ConcreteComponents
                .createTreeComponent(identifier);
        Assert.assertNotNull(tree);
        
        CAP cap1 = tree.checkExistenceOfNodeByIndexpath(
                SearchType.absolute, 2, TEST, true);
        Assert.assertNotNull(cap1);
        CAP cap2 = tree.checkExistenceOfNodeByTextpath(
                SearchType.absolute, 2, TEST, Operator.equals, true);
        Assert.assertNotNull(cap2);
        CAP cap3 = tree.checkTextMousePosition(TEST, Operator.equals);
        Assert.assertNotNull(cap3);
        CAP cap4 = tree.checkTextOfSelectedNodeS(TEST, Operator.equals);
        Assert.assertNotNull(cap4);
        CAP cap5 = tree.collapseNodeByIndexpath(
                SearchType.absolute, 2, TEST);
        Assert.assertNotNull(cap5);
        CAP cap6 = tree.collapseNodeByTextpath(
                SearchType.absolute, 2, TEST, Operator.equals);
        Assert.assertNotNull(cap6);
        CAP cap7 = tree.dragNodeByIndexpath(
                InteractionMode.primary, Modifier.values(), 
                SearchType.absolute, 2, TEST);
        Assert.assertNotNull(cap7);
        CAP cap8 = tree.dragNodeByTextpath(
                InteractionMode.primary, Modifier.values(), 
                SearchType.absolute, 2, TEST, Operator.equals);
        Assert.assertNotNull(cap8);
        CAP cap9 = tree.dropOnNodeByIndexpath(
                SearchType.absolute, 2, TEST, 1000);
        Assert.assertNotNull(cap9);
        CAP cap10 = tree.dropOnNodeByTextpath(
                SearchType.absolute, 2, TEST, Operator.equals, 1000);
        Assert.assertNotNull(cap10);
        CAP cap11 = tree.expandNodeByIndexpath(
                SearchType.absolute, 2, TEST);
        Assert.assertNotNull(cap11);
        CAP cap12 = tree.expandNodeByTextpath(
                SearchType.absolute, 2, TEST, Operator.equals);
        Assert.assertNotNull(cap12);
        CAP cap13 = tree.move(
                TreeDirection.down, 1, 1);
        Assert.assertNotNull(cap13);
        CAP cap14 = tree.selectNodeByIndexpath(
                SearchType.absolute, 2, TEST, 
                1, InteractionMode.primary, BinaryChoice.yes);
        Assert.assertNotNull(cap14);
        CAP cap15 = tree.selectNodeByTextpath(
                SearchType.absolute, 2, TEST, Operator.equals, 
                1, InteractionMode.primary, BinaryChoice.yes);
        Assert.assertNotNull(cap15);
        
        Assert.assertTrue(tree 
                instanceof 
                org.eclipse.jubula.toolkit.base.components.GraphicsComponent);
    }
    /**
     * test base component CAPs
     */
    @Test
    public void testGraphicsCompCAP() {
        
        ComponentIdentifier identifier = 
                OM_factories.TextField_pfr_txf;
       
        GraphicsComponent textField = AbstractComponents
                .createGraphicsComponent(identifier);
        Assert.assertNotNull(textField);
        
        CAP cap1 = textField.checkEnablement(true);
        Assert.assertNotNull(cap1);
        CAP cap6 = textField.checkExistence(false, 0);
        Assert.assertNotNull(cap6);
        CAP cap11 = textField.checkFocus(true);
        Assert.assertNotNull(cap11);
        CAP cap12 = textField.checkProperty("name", "paul", Operator.equals);  //$NON-NLS-1$//$NON-NLS-2$
        Assert.assertNotNull(cap12);
        CAP cap17 = textField.click(1, InteractionMode.primary);
        Assert.assertNotNull(cap17);
        CAP cap18 = textField.clickInComponent(
                2, InteractionMode.primary, 50, Unit.percent, 50, Unit.percent);
        Assert.assertNotNull(cap18);
        CAP cap19 = textField.drag(
                InteractionMode.primary, Modifier.values(), 50, 
                Unit.percent, 50, Unit.percent);
        Assert.assertNotNull(cap19);
        CAP cap20 = textField.drop(
                50, Unit.percent, 50, Unit.percent, 1000);
        Assert.assertNotNull(cap20);      
        CAP cap25 = textField.showText(TEST, 18, 10000, 200);
        Assert.assertNotNull(cap25);
        CAP cap26 = textField.waitForComponent(1000, 1000);
        Assert.assertNotNull(cap26);
    }
    /**
     * test context menus
     */
    @Test
    public void testContextMenus() {
        ComponentIdentifier identifier = 
                OM_factories.TextField_pfr_txf;
        GraphicsComponent textField = AbstractComponents
                .createGraphicsComponent(identifier);
        Assert.assertNotNull(textField);
        
        CAP cap1 = textField.
                selectContextMenuEntryByIndexpath(
                        TEST, InteractionMode.primary);
        Assert.assertNotNull(cap1);
        CAP cap2 = textField.
                selectContextMenuEntryByIndexpathSpecifyPosition(
                        50, Unit.percent, 50, Unit.percent, 
                        TEST, InteractionMode.primary);
        Assert.assertNotNull(cap2);
        CAP cap3 = textField.
                selectContextMenuEntryByTextpath(
                        TEST, Operator.equals, InteractionMode.primary);
        Assert.assertNotNull(cap3);
        CAP cap4 = textField.
                selectContextMenuEntryByTextpathSpecifyPosition(
                        50, Unit.percent, 50, Unit.percent, 
                        TEST, Operator.equals, InteractionMode.primary);
        Assert.assertNotNull(cap4);
        CAP cap5 = textField.
                checkSelectionOfContextMenuEntryByIndexpath(
                        TEST, true, InteractionMode.primary);
        Assert.assertNotNull(cap5);
        CAP cap6 = textField.
                checkSelectionOfContextMenuEntryByIndexpathSpecifyPosition(
                        50, Unit.percent, 50, Unit.percent, TEST,
                        true, InteractionMode.primary);
        Assert.assertNotNull(cap6);
        CAP cap7 = textField.checkSelectionOfContextMenuEntryByTextpath(
                TEST, Operator.notEquals, true, InteractionMode.primary);
        Assert.assertNotNull(cap7);
        CAP cap8 = textField.
                checkSelectionOfContextMenuEntryByTextpathSpecifyPosition(
                50, Unit.percent, 50, Unit.percent, TEST, Operator.notEquals,
                true, InteractionMode.primary);
        Assert.assertNotNull(cap8);
        CAP cap9 = textField.checkExistenceOfContextMenuEntryByIndexpath(
                TEST, false, InteractionMode.secondary);
        Assert.assertNotNull(cap9);
        CAP cap10 = textField.
                checkExistenceOfContextMenuEntryByIndexpathSpecifyPosition(
                50, Unit.pixel, 50, Unit.pixel, TEST,
                false, InteractionMode.tertiary);
        Assert.assertNotNull(cap10);
        CAP cap11 = textField.checkExistenceOfContextMenuEntryByTextpath(
                TEST, Operator.notEquals, true, InteractionMode.primary);
        Assert.assertNotNull(cap11);
        CAP cap12 = textField.
                checkExistenceOfContextMenuEntryByTextpathSpecifyPosition(
                40, Unit.percent, 100, Unit.percent, TEST,
                Operator.matches, true, InteractionMode.primary);
        Assert.assertNotNull(cap12);
        CAP cap13 = textField.checkEnablementOfContextMenuEntryByIndexpath(
                TEST, true, InteractionMode.primary);
        Assert.assertNotNull(cap13);
        CAP cap14 = textField.
                checkEnablementOfContextMenuEntryByIndexpathSpecifyPosition(
                50, Unit.percent, 50, Unit.percent, TEST,
                true, InteractionMode.primary);
        Assert.assertNotNull(cap14);
        CAP cap15 = textField.checkEnablementOfContextMenuEntryByTextpath(
                TEST, Operator.equals, true, InteractionMode.primary);
        Assert.assertNotNull(cap15);
        CAP cap16 = textField.
                checkEnablementOfContextMenuEntryByTextpathSpecifyPosition(
                50, Unit.percent, 50, Unit.percent, TEST,
                Operator.equals, true, InteractionMode.primary);
        Assert.assertNotNull(cap16);
    }
}