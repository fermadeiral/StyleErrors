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

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITabbedComponent;
import org.eclipse.jubula.rc.common.util.IndexConverter;
import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

import static org.eclipse.jubula.rc.common.driver.CheckWithTimeoutQueuer.invokeAndWait;

/**
 * Implementation of the general actions for TabPanes
 * @author BREDEX GmbH
 *
 */
public class TabbedPaneTester extends WidgetTester {

    /**
     * 
     * @return the <code>ITabPane</code>
     */
    public ITabbedComponent getTabPane() {
        return (ITabbedComponent)getComponent();
    }
    
    /**
     * @param title The tab title
     * @param operator The matching operator
     * @return The tab index
     */
    private int getIndexOfTab(final String title, final String operator) {
        int index = -1;
        int tabs = getTabPane().getTabCount();
        for (int a = 0; a < tabs; a++) {
            if (MatchUtil.getInstance().match(
                    getTabPane().getTitleofTab(a),
                    title,
                    operator)) {

                index = a;
                break;
            }
        }

        if (index == -1) {
            throw new StepExecutionException(
                "Can not find tab: '" + title + "' using operator: '"  //$NON-NLS-1$ //$NON-NLS-2$
                + operator + "'", EventFactory //$NON-NLS-1$
                    .createActionError(TestErrorEvent.NOT_FOUND));
        }
        return index;

    }
    
    /**
     * {@inheritDoc}
     */
    protected void verifyIndexExists(final int index) {
        boolean exists = (index >= 0) && (index < getTabPane().getTabCount()); 

        if (!exists) {
            throw new StepExecutionException(
                "The tab index doesn't exist: " + index, EventFactory //$NON-NLS-1$
                    .createActionError(TestErrorEvent.INVALID_INDEX));
        }
    }    
    
    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        final String[] componentTextArray;
        componentTextArray = new String[getTabPane().getTabCount()];
        for (int i = 0; i < componentTextArray.length; i++) {
            componentTextArray[i] = getTabPane().getTitleofTab(i);
        }
        return componentTextArray;
    }
    
    /**
     * Selects the tab with the passed index. The method doesn't care if the tab is enabled or not.
     * @param index The tab index
     */
    private void selectTabByImplIndex(int index) {
        verifyIndexExists(index);

        // FIXME zeb: We currently ignore the possibility of needing to scroll
        //            or use a pulldown menu to find the tab item. This means
        //            that the user must know when this type of action is
        //            necessary and specify their tests accordingly. We may wish
        //            to change this later so that it is "smarter" (i.e. can
        //            scroll or use a pulldown menu to find tab items in a crowded
        //            tab folder).

        // Some tab  items have a close button embedded in them.
        // In order to reduce the chance of clicking this close button, we click
        // at x-coordinate 25% rather than 50%.
        getRobot().click(getTabPane().getRealComponent(),
                getTabPane().getBoundsAt(index), 
            ClickOptions.create().left(), 25, false, 50, false);
    }
    
    /**
     * Selects the tab with the passed index.
     * The method doesn't care if the tab is enabled or not.
     *
     * @param index
     *            The tab index
     * @throws StepExecutionException
     *             If the tab index is invalid.
     */
    public void rcSelectTabByIndex(int index)
        throws StepExecutionException {
        int implIdx = IndexConverter.toImplementationIndex(index);

        selectTabByImplIndex(implIdx);
    }
    /**
     * Selects the tab with the passed title. The method doesn't care if the tab
     * is enabled or not.
     *
     * @param title
     *            The tab title
     * @param operator
     *      using regex
     * @throws StepExecutionException
     *             If the tab title is invalid.
     */
    public void rcSelectTab(final String title, String operator)
        throws StepExecutionException {

        selectTabByImplIndex(getIndexOfTab(title, operator));
    }
    
    /**
     * Verifies the text of the tab by index
     *
     * @param index index of tab
     * @param text The tab title
     * @param operator Operator to be executed
     * @param timeout the maximum amount of time to wait for the text of the tab
     *          identified by index to be verified
     * @throws StepExecutionException
     *             If the tab title is invalid.
     */
    public void rcVerifyTextOfTabByIndex(final int index, final String text,
            final String operator, int timeout)
        throws StepExecutionException {
        invokeAndWait("rcVerifyTextOfTabByIndex", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                final int tabIndex =
                        IndexConverter.toImplementationIndex(index);
                String tabTitle = getTabPane().getTitleofTab(tabIndex);
                Verifier.match(tabTitle, text, operator);
            }
        });
        
    }
    
    /**
     * Verifies existence of tab by index/value
     *
     * @param tab index/value of tab
     * @param operator Operator to be executed
     * @param exists boolean, tab exists
     * @param timeout the maximum amount of time to wait for the check whether
     *            the tab is existent in according to the exists parameter
     * @throws StepExecutionException if tab does not exist.
     */
    public void rcVerifyExistenceOfTab(final String tab, final String operator,
            final boolean exists, int timeout)
        throws StepExecutionException {
        invokeAndWait("rcVerifyExistenceOfTab", timeout, new Runnable() { //$NON-NLS-1$

            public void run() {
                final int tabIdx = getTabIndexFromString(tab, operator);
                boolean tabExists = true;
                try {
                    verifyIndexExists(tabIdx);
                } catch (StepExecutionException e) {
                    tabExists = false;
                }
                
                Verifier.equals(exists, tabExists);
            } 
        });
    }
    
    /**
     * @param tab index or title of tab
     * @param operator Operator to be executed
     * @return returns index of tab if exists, -1 otherwise
     */
    private int getTabIndexFromString(String tab, String operator) {
        int tabIndex = -1;
        try {
            tabIndex = IndexConverter.toImplementationIndex(
                    Integer.parseInt(tab));
        } catch (NumberFormatException nfe) {
            for (int i = 0; i < getTabPane().getTabCount(); i++) {
                String text = getTabPane().getTitleofTab(i);
                if (MatchUtil.getInstance().match(text, tab, operator)) {
                    return i;
                }
            }
            
        }
        return tabIndex;
    }

    /**
     * Verifies if the tab with the passed title is enabled.
     *
     * @param title The tab title
     * @param operator operation to be executed
     * @param isEnabled whether to test if the tab  is enabled or not
     * @param timeout the maximum amount of time to wait for the check whether
     *            the tab has the specified enabled status
     * @throws StepExecutionException
     *             If the tab title is invalid.
     */
    public void rcVerifyEnabled(String title, String operator,
        final boolean isEnabled, int timeout)

        throws StepExecutionException {
        final int tabIndex = getIndexOfTab(title, operator);
        invokeAndWait("rcVerifyEnabled", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                Verifier.equals(isEnabled, getTabPane().isEnabledAt(tabIndex));
            }
        });
    }
    
    /**
     * Verifies if the tab with the passed index is enabled.
     *
     * @param index
     *            The tab index
     * @param enabled
     *            Should the tab be enabled?
     * @param timeout the maximum amount of time to wait for the check whether
     *            the tab has the specified enabled status
     * @throws StepExecutionException
     *             If the tab index is invalid.
     */
    public void rcVerifyEnabledByIndex(int index, final boolean enabled,
            int timeout)
        throws StepExecutionException {
        final int implIdx = IndexConverter.toImplementationIndex(index);
        invokeAndWait("rcVerifyEnabledByIndex", timeout, new Runnable() { //$NON-NLS-1$
            
            public void run() {
                verifyIndexExists(implIdx);
                Verifier.equals(enabled, getTabPane().isEnabledAt(implIdx));
            }
        });
    }
    
    /**
     * Verifies the selection of the tab with the passed index.
     *
     * @param index
     *            The tab index
     * @param selected
     *            Should the tab be selected?
     * @param timeout the maximum amount of time to wait for the check whether
     *            the tab is selected according to the selected parameter
     *            
     * @throws StepExecutionException
     *             If the tab index is invalid.
     */
    public void rcVerifySelectedTabByIndex(int index, final boolean selected,
            int timeout) throws StepExecutionException {
        final int implIdx = IndexConverter.toImplementationIndex(index);
        invokeAndWait("rcVerifySelectedTabByIndex", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                int selIndex = getTabPane().getSelectedIndex();
                if (selIndex == -1) {
                    if (!selected) {
                        return;
                    }
                    throw new StepExecutionException(
                            I18n.getString(TestErrorEvent.NO_SELECTION),
                            EventFactory.createActionError(
                                    TestErrorEvent.NO_SELECTION));
                }

                Verifier.equals(selected, selIndex == implIdx);
            }

        });
    }
    
    /**
     * Verifies the selection of the tab with the passed title.
     *
     * @param tabTitlePattern
     *            The tab title pattern to use for checking
     * @param operator
     *            Operator to be executed
     * @param selected
     *            Should the tab be selected?
     * @param timeout
     *            the maximum amount of time to wait for the check whether
     *            the selected tab has a specific title
     * @throws StepExecutionException
     *             If the tab title is invalid.
     */
    public void rcVerifySelectedTab(final String tabTitlePattern,
            final String operator, final boolean selected, int timeout)
            throws StepExecutionException {
        invokeAndWait("rcVerifySelectedTab", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                String selectedTabTitle = null; // for no Selection
                int selectedIndex = getTabPane().getSelectedIndex();
                if (selectedIndex >= 0) {
                    selectedTabTitle =
                            getTabPane().getTitleofTab(selectedIndex);
                }

                if (selectedTabTitle == null) {
                    if (!selected) {
                        return;
                    }
                    throw new StepExecutionException(
                            I18n.getString(TestErrorEvent.NO_SELECTION),
                            EventFactory.createActionError(
                                    TestErrorEvent.NO_SELECTION));
                }
                Verifier.match(selectedTabTitle, tabTitlePattern, operator,
                        selected);

            }
        });
    }
    
}
