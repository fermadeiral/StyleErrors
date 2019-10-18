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
package org.eclipse.jubula.rc.javafx.tester.adapter;

import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITabbedComponent;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.tester.util.NodeBounds;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

/**
 * Adapter for a {@link TabPane}.
 * 
 */
public class TabPaneAdapter extends JavaFXComponentAdapter<TabPane>
        implements ITabbedComponent, IContainerAdapter {
    
    /**
     * Parameter types of the method to retrieve a Tab Header Skin.
     */
    private static final Class<?>[] GET_TAB_HEADER_SKIN_PARAMETER_TYPES = 
            new Class<?>[]{Tab.class};
    
    /**
     * Name of the method to retrieve a Tab Header Skin.
     */
    private static final String GET_TAB_HEADER_SKIN_METHOD_NAME = 
            "getTabHeaderSkin"; //$NON-NLS-1$

    /**
     * Name of the class containing the method to retrieve a Tab Header Skin.
     */
    private static final String TAB_HEADER_AREA_CLASSNAME = 
            "com.sun.javafx.scene.control.skin.TabPaneSkin$TabHeaderArea"; //$NON-NLS-1$

    /**
     * 
     * @param objectToAdapt 
     */
    public TabPaneAdapter(TabPane objectToAdapt) {
        super(objectToAdapt);
    }

    /**
     * {@inheritDoc}
     */
    public int getTabCount() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getTabCount", new Callable<Integer>() { //$NON-NLS-1$
                    public Integer call() {
                        return getRealComponent().getTabs().size();
                    }
                }); 
    }
    
    /**
     * {@inheritDoc}
     */
    public String getTitleofTab(final int index) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getTitleOfTab", new Callable<String>() { //$NON-NLS-1$
                    public String call() {
                        return getTabAt(index).getText();
                    }
                });        
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle getBoundsAt(final int index) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getBoundsAt", new Callable<Rectangle>() { //$NON-NLS-1$
                    public Rectangle call() {
                        Tab targetTab = getTabAt(index);
                        Node targetNode = getNodeFor(targetTab);
                        
                        return NodeBounds.getRelativeBounds(
                                targetNode, getRealComponent());
                    }

                }); 
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabledAt(final int index) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait(
                "isEnabledAt", new Callable<Boolean>() { //$NON-NLS-1$
                    public Boolean call() {
                        return !getTabAt(index).isDisabled();
                    }
                }); 
    }

    /**
     * {@inheritDoc}
     */
    public int getSelectedIndex() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait(
            "getSelectedIndex", new Callable<Integer>() { //$NON-NLS-1$
                @Override
                public Integer call() throws Exception {
                    return getRealComponent().getSelectionModel()
                            .getSelectedIndex();
                }
            });
    }

    /**
     * This method must be called on the FX Thread.
     * 
     * @param index zero based index.
     * @return the Tab at the zero based index in the TabPane's Tab list.
     */
    private Tab getTabAt(int index) {
        EventThreadQueuerJavaFXImpl.checkEventThread();
        
        return getRealComponent().getTabs().get(index);
    }

    /**
     * This method must be called on the FX Thread.
     * 
     * @param targetTab the Tab for which to get the bounding node.
     * 
     * @return the Node representing the bounding box of <code>targetTab</code>,
     *         or <code>null</code> if no such Node could be found.
     *         
     * @throws StepExecutionException if the Skin or structure of the real
     *                                component is not supported.
     */
    private Node getNodeFor(Tab targetTab) throws StepExecutionException {
        EventThreadQueuerJavaFXImpl.checkEventThread();

        Skin<?> tabPaneSkin = getRealComponent().getSkin();
        if (!(tabPaneSkin instanceof SkinBase)) {
            throw new StepExecutionException(
                    "Skin not supported: " + tabPaneSkin.getClass(), //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.RENDERER_NOT_SUPPORTED));
        }
        
        ClassLoader skinClassLoader = targetTab.getClass().getClassLoader();
        try {
            Class<?> tabHeaderAreaClass = 
                    skinClassLoader.loadClass(TAB_HEADER_AREA_CLASSNAME);
            for (Node tabPaneSkinChild 
                    : ((SkinBase<?>)tabPaneSkin).getChildren()) {
                
                if (tabHeaderAreaClass.isInstance(tabPaneSkinChild)) {
                    Method method = tabHeaderAreaClass.getDeclaredMethod(
                            GET_TAB_HEADER_SKIN_METHOD_NAME, 
                            GET_TAB_HEADER_SKIN_PARAMETER_TYPES);
                    method.setAccessible(true);
                    Object tabHeaderSkin = method.invoke(
                            tabPaneSkinChild, new Object[]{ targetTab });
                    if (tabHeaderSkin == null 
                            || tabHeaderSkin instanceof Node) {
                        return (Node) tabHeaderSkin;
                    }

                    throw new StepExecutionException(
                            "Skin not supported: " + tabHeaderSkin.getClass(), //$NON-NLS-1$ 
                            EventFactory.createActionError(
                                    TestErrorEvent.RENDERER_NOT_SUPPORTED));

                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            // These exceptions indicate that internal implementation details 
            // of JavaFX are not what we were expecting (either something was 
            // changed or we are dealing with an alternate implementation).
            // We *could* implement a fallback here, but, since we don't 
            // currently have any backup plan, we just fall-through instead.
        } catch (IllegalAccessException | InvocationTargetException e) {
            // These types of exceptions give us no information about whether 
            // the Skin is supported, so just wrap and rethrow. 
            throw new StepExecutionException(e);
        }
        
        throw new StepExecutionException(
                "Skin / structure not supported: " + tabPaneSkin.getClass(), //$NON-NLS-1$
                EventFactory.createActionError(
                        TestErrorEvent.RENDERER_NOT_SUPPORTED));
    }
    /**
     * {@inheritDoc}
     * In this case the Content-Node of the currently selected Tab will be returned
     */
    @Override
    public List<Node> getContent() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getContent", //$NON-NLS-1$
                new Callable<List<Node>>() {

                    @Override
                    public List<Node> call() throws Exception {
                        ArrayList<Node> list = new ArrayList<>();
                        list.add(getRealComponent().getSelectionModel()
                                .getSelectedItem().getContent());
                        return list;
                    }
                });
    }

}
