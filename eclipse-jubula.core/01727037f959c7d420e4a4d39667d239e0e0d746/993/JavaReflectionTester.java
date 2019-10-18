/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.common.tester;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.interfaces.ITester;
import org.eclipse.jubula.rc.common.util.ReflectionUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
/**
 * Tester class for the Reflection Component
 * 
 * @author Bredex GmbH
 * @created 27.10.2015
 */
public abstract class JavaReflectionTester implements ITester {
    
    /**
     * Queuer for executing the methods in the UI-Thread of the AUT
     */
    private IEventThreadQueuer m_threadQueuer;
    
    /**
     * Constructor
     */
    public JavaReflectionTester() {
        m_threadQueuer = getEventThreadQueuer();
    }

    /**
     * This is used to get the toolkit dependent Thread Queuer
     * @return the UI-Thread queuer
     */
    protected abstract IEventThreadQueuer getEventThreadQueuer();

    /**
     * Invokes the specified Method
     * 
     * @param fqcn Fully qualified class name
     * @param name name of the Method
     * @param signature signature of the method
     * @param args arguments for the method
     * @param argsSplit separator for the Arguments
     * @param timeout the timeout
     */
    public void rcInvokeMethod(
            final String fqcn, 
            final String name,
            @Nullable final String signature, 
            @Nullable final String args, 
            @Nullable final String argsSplit,
            int timeout) {
        invokeMethod(fqcn, name, signature, args, argsSplit, false, timeout);
    }
    
    /**
     * Invokes the specified Method
     * 
     * @param fqcn Fully qualified class name
     * @param name name of the Method
     * @param signature signature of the method
     * @param args arguments for the method
     * @param argsSplit separator for the Arguments
     * @param timeout the timeout
     */
    public void rcInvokeExternalMethod(
            final String fqcn, 
            final String name,
            @Nullable final String signature, 
            @Nullable final String args, 
            @Nullable final String argsSplit,
            int timeout) {
        invokeMethod(fqcn, name, signature, args, argsSplit, true, timeout);
    }

    /**
     * Invokes the specified Method
     * 
     * @param fqcn Fully qualified class name
     * @param name name of the Method
     * @param signature signature of the method
     * @param args arguments for the method
     * @param argsSplit separator for the Arguments
     * @param external whether the method comes
     *          from an external jar 
     * @param timeout the timeout
     */
    private void invokeMethod(
            final String fqcn, 
            final String name,
            @Nullable final String signature, 
            @Nullable final String args, 
            @Nullable final String argsSplit,
            boolean external,
            int timeout) {
        try {
            Object result = m_threadQueuer.invokeAndWait("invokeMethod", //$NON-NLS-1$
                    createCallable(fqcn, name, signature, args,
                            argsSplit, external),
                    timeout);
        } catch (TimeoutException e) {
            throw new StepExecutionException(e.toString(), EventFactory
                    .createActionError(TestErrorEvent.CONFIRMATION_TIMEOUT));
        }
    }

    /**
     * Invokes the specified Method
     * 
     * @param variableName name of the variable of the cap. This isn't used.
     * @param fqcn Fully qualified class name
     * @param name name of the Method
     * @param signature signature of the method
     * @param args arguments for the method
     * @param argsSplit separator for the Arguments
     * @param timeout the timeout
     * @return returns the string representation of the return value of the
     *         invoked method
     */
    public String rcInvokeMethodStoreReturn(
            final String variableName,
            final String fqcn, 
            final String name, 
            @Nullable final String signature,
            @Nullable final String args, 
            @Nullable final String argsSplit, 
            int timeout) {
        return invokeMethodStoreReturn(variableName, fqcn, name, signature,
                args, argsSplit, false, timeout);
    }
    
    /**
     * Invokes the specified Method
     * 
     * @param fqcn Fully qualified class name
     * @param name name of the Method
     * @param signature signature of the method
     * @param args arguments for the method
     * @param argsSplit separator for the Arguments
     * @param timeout the timeout
     * @return returns the string representation of the return value of the
     *         invoked method
     */
    public String rcInvokeMethodStoreReturnApi(
            final String fqcn, 
            final String name, 
            @Nullable final String signature,
            @Nullable final String args, 
            @Nullable final String argsSplit, 
            int timeout) {
        return invokeMethodStoreReturn("PLACEHOLDER", fqcn, name, signature, //$NON-NLS-1$
                args, argsSplit, false, timeout);
    }
    
    /**
     * Invokes the specified external Method
     * 
     * @param variableName name of the variable of the cap. This isn't used.
     * @param fqcn Fully qualified class name
     * @param name name of the Method
     * @param signature signature of the method
     * @param args arguments for the method
     * @param argsSplit separator for the Arguments
     * @param timeout the timeout
     * @return returns the string representation of the return value of the
     *         invoked method
     */
    public String rcInvokeExternalMethodStoreReturn(
            final String variableName,
            final String fqcn, 
            final String name, 
            @Nullable final String signature,
            @Nullable final String args, 
            @Nullable final String argsSplit, 
            int timeout) {
        return invokeMethodStoreReturn(variableName, fqcn, name, signature,
                args, argsSplit, true, timeout);
    }
    
    /**
     * Invokes the specified external Method
     * 
     * @param fqcn Fully qualified class name
     * @param name name of the Method
     * @param signature signature of the method
     * @param args arguments for the method
     * @param argsSplit separator for the Arguments
     * @param timeout the timeout
     * @return returns the string representation of the return value of the
     *         invoked method
     */
    public String rcInvokeExternalMethodStoreReturnApi(
            final String fqcn, 
            final String name, 
            @Nullable final String signature,
            @Nullable final String args, 
            @Nullable final String argsSplit, 
            int timeout) {
        return invokeMethodStoreReturn("PLACEHOLDER", fqcn, name, signature, //$NON-NLS-1$
                args, argsSplit, true, timeout);
    }
    
    /**
     * Invokes the specified Method
     * 
     * @param variableName name of the variable of the cap. This isn't used.
     * @param fqcn Fully qualified class name
     * @param name name of the Method
     * @param signature signature of the method
     * @param args arguments for the method
     * @param argsSplit separator for the Arguments
     * @param external whether the class is from an
     *          external jar
     * @param timeout the timeout
     * @return returns the string representation of the return value of the
     *         invoked method
     */
    private String invokeMethodStoreReturn(
            final String variableName,
            final String fqcn, 
            final String name, 
            @Nullable final String signature,
            @Nullable final String args, 
            @Nullable final String argsSplit,
            boolean external,
            int timeout) {
        try {
            Object result = m_threadQueuer.invokeAndWait("invokeMethod", //$NON-NLS-1$
                    createCallable(fqcn, name, signature, args,
                            argsSplit, external),
                    timeout);
            return result == null ? StringConstants.NULL : result.toString();
        } catch (TimeoutException e) {
            throw new StepExecutionException(e.toString(), EventFactory
                    .createActionError(TestErrorEvent.CONFIRMATION_TIMEOUT));
        }
    }
    
    /**
     * Creates the runnable object which will invoke the specified Method
     * @param fqcn fully qualified class name
     * @param name method name
     * @param signature method signature
     * @param args method arguments
     * @param argsSplit separator for the arguments
     * @param external whether the method comes
     *          from an external jar
     * @return the IRunnable object
     */
    private Callable<Object> createCallable(
            final String fqcn,
            final String name, 
            @Nullable final String signature, 
            @Nullable final String args,
            @Nullable final String argsSplit,
            boolean external) {
        
        final boolean isExternal = external;
        return new Callable<Object>() {

            public Object call() {
                try {
                    ClassLoader uiClassloader;
                    
                    if (isExternal) {
                        uiClassloader = AUTServer.getInstance().
                                getExternalLoader();
                        if (uiClassloader == null) {
                            throw new ClassNotFoundException("External jars could not be loaded."); //$NON-NLS-1$
                        }
                    } else {
                        uiClassloader = Thread.currentThread()
                            .getContextClassLoader();
                    }
                    return ReflectionUtil.invokeMethod(fqcn, name,
                            signature, args, argsSplit, uiClassloader);
                } catch (Throwable e) {
                    ReflectionUtil.handleException(e);
                }
                return null;
            }
        };
    }

    /**
     * Invokes the specified Method
     * 
     * @param fqcn Fully qualified class name
     * @param name name of the Method
     * @param timeout the timeout
     */
    public void rcInvokeMethod(
            final String fqcn, 
            final String name,
            int timeout) {
        try {
            Object result = m_threadQueuer.invokeAndWait("invokeMethod", //$NON-NLS-1$
                    createCallable(fqcn, name), timeout);
        } catch (TimeoutException e) {
            throw new StepExecutionException(e.toString(), EventFactory
                    .createActionError(TestErrorEvent.CONFIRMATION_TIMEOUT));
        }
    }

    /**
     * Invokes the specified Method
     * 
     * @param variableName name of the variable of the cap. This isn't used.
     * @param fqcn Fully qualified class name
     * @param name name of the Method
     * @param timeout the timeout
     * @return returns the string representation of the return value of the
     *         invoked method
     */
    public String rcInvokeMethodStoreReturn(
            final String variableName,
            final String fqcn, 
            final String name, 
            int timeout) {
        try {
            Object result = m_threadQueuer.invokeAndWait("invokeMethod", //$NON-NLS-1$
                    createCallable(fqcn, name), timeout);
            return result == null ? StringConstants.NULL : result.toString();
        } catch (TimeoutException e) {
            throw new StepExecutionException(e.toString(), EventFactory
                    .createActionError(TestErrorEvent.CONFIRMATION_TIMEOUT));
        }
    }

    /**
     * Creates the runnable object which will invoke the specified Method
     * @param fqcn fully qualified class name
     * @param name method name
     * @return the IRunnable object
     */
    private Callable<Object> createCallable(final String fqcn,
            final String name) {
        return new Callable<Object>() {

            public Object call() {
                ClassLoader uiClassloader = Thread.currentThread()
                        .getContextClassLoader();
                try {
                    return ReflectionUtil.invokeMethod(fqcn, name,
                            uiClassloader);
                } catch (Throwable e) {
                    ReflectionUtil.handleException(e);
                }
                return null;
            }
        };
    }

    /**
     * Not used because of default mapping
     * @param graphicsComponent not used
     */
    public void setComponent(Object graphicsComponent) {
        // Nothing here.
    }

    /**
     * Not used because of default mapping
     * @return null
     */
    public String[] getTextArrayFromComponent() {
        // Nothing here.
        return null;
    }
}
