/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.common.tester;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.driver.CheckWithTimeoutQueuer;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComboComponent;
import org.eclipse.jubula.rc.common.util.IndexConverter;
import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.toolkit.enums.ValueSets.SearchType;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

import static org.eclipse.jubula.rc.common.driver.CheckWithTimeoutQueuer.invokeAndWait;

/**
 * General implementation for ComboBoxes and ComboBox like components.
 * @author BREDEX GmbH
 *
 */
public class ComboBoxTester extends AbstractTextInputSupportTester {
    /**
     * @return the <code>IComboBoxAdapter</code>
     */
    private IComboComponent getCBAdapter() {
        return (IComboComponent) getComponent();
    }
    
    
    /** {@inheritDoc} */
    public String[] getTextArrayFromComponent() {
        return new String [] {getText()};
    }

    /**
     * {@inheritDoc}
     */
    public void rcVerifyEditable(final boolean editable, int timeout) {
        CheckWithTimeoutQueuer.invokeAndWait("rcVerifyEditable", timeout, //$NON-NLS-1$
                new Runnable() {
                    public void run() {
                        Verifier.equals(editable, getCBAdapter().isEditable());
                    }
                });
    }
    
    /**
     * Checks if the component contains the specified text.
     * @param text check if this text is in the combobox
     * @param timeout the maximum amount to wait to check whether the component
     *          contains the specified text
     */
    public void rcVerifyContainsValue(final String text, int timeout) {
        invokeAndWait("rcVerifyContainsValue", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                Verifier.equals(true, containsValue(text, MatchUtil.EQUALS));
            }
        });
    }
    
    /**
     * Verifies if the list contains an element that renderes <code>value</code>.
     * @param value The text to verify
     * @param operator The operator used to verify
     * @param exists If the value should exist or not.
     * @param timeout the maximum amount to wait to check whether the component
     *          contains the specified text
     */
    public void rcVerifyContainsValue(final String value, final String operator,
            final boolean exists, int timeout) {
        invokeAndWait("rcVerifyContainsValue", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                final boolean contains = containsValue(value, operator);
                Verifier.equals(exists, contains);
            }
        });
    }
    
  /**
  * @param value
  *            The value to check
  * @param operator
  *            The operator used to verify
  * @return <code>true</code> if the combobox contains an element rendered
  *         with the passed value
  */
    private boolean containsValue(String value, String operator) {
        String[] comboValues = getCBAdapter().getValues();
        for (int i = 0; i < comboValues.length; i++) {
            boolean contains = MatchUtil.getInstance()
                    .match(comboValues[i], value, operator);
            if (contains) {
                return contains;
            }
        }
        return false;
    }


    /**
     * @see org.eclipse.jubula.rc.swing.swing.implclasses.AbstractSwingImplClass#getText()
     * @return value from ComboBoxHelper
     */
    protected String getText() {
        return getCBAdapter().getText();
    }
    
    /**
     * {@inheritDoc}
     */
    public void rcInputText(String text) {        
        getCBAdapter().input(text, false);
    }
   
    /**
     * {@inheritDoc}
     */
    public void rcReplaceText(String text) throws StepExecutionException {
        getCBAdapter().input(text, true);
    }
    
    /**
     * Selects <code>index</code> in the combobox.
     *
     * @param index
     *            The index to select
     */
    public void rcSelectIndex(String index) {
        int implIdx = IndexConverter.toImplementationIndex(
                IndexConverter.intValue(index)); 
        getCBAdapter().select(implIdx);

    }
    
    /**
     * Selects a value from the list of the combobox
     * 
     * @param value
     *            The value to select
     * @param operator
     *            if regular expressions are used
     * @param searchType
     *            Determines where the search begins ("relative" or "absolute")
     */
    public void rcSelectValue(String value, String operator,
            final String searchType) {
        String[] comboValues = getCBAdapter().getValues();
        Validate.notNull(value, "text must not be null"); //$NON-NLS-1$
        
        int index = -1;

        for (int i = getStartingIndex(searchType);
                i < comboValues.length; ++i) {
            String str = comboValues[i];
            if (MatchUtil.getInstance().match(str, value, operator)) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            throw new StepExecutionException("Text '" + value //$NON-NLS-1$
                + "' not found", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
        }

        getCBAdapter().select(index);
    }
    
    /**
     * Verifies if the combobox has <code>index</code> selected.
     * @param index The index to verify
     * @param isSelected If the index should be selected or not.
     * @param timeout the maximum amount of time to wait for the index's
     *          selection to be verified
     */
    public void rcVerifySelectedIndex(final String index,
            final boolean isSelected, int timeout) {
        invokeAndWait("rcVerifySelectedIndex", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                int implIdx = IndexConverter.toImplementationIndex(
                        IndexConverter.intValue(index));
                int actual = getCBAdapter().getSelectedIndex();
                Verifier.equals(implIdx, actual, isSelected);
            }
        });
    }
    
    /**
     * @param searchType Determines where the search begins ("relative" or "absolute")
     * @return The index from which to begin a search, based on the search type
     *         and (if appropriate) the currently selected item.
     */
    private int getStartingIndex(final String searchType) {
        int startingIndex = 0;
        if (searchType.equalsIgnoreCase(SearchType.relative.rcValue())) {
            startingIndex = getCBAdapter().getSelectedIndex();
        }
        return startingIndex;
    }
}
