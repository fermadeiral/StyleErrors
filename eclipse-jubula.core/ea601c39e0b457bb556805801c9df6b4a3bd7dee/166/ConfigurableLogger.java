/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.communication.internal;

import org.eclipse.jubula.tools.internal.exception.Assert;
import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * @author BREDEX GmbH
 * 
 */
public class ConfigurableLogger implements Logger {
    /** the logger to use */
    private Logger m_logger = null;

    /** the enablement state of this logger */
    private boolean m_isEnabled = true;

    /**
     * Constructor
     * 
     * @param logger
     *            the logger to use
     */
    public ConfigurableLogger(Logger logger) {
        Assert.verify(logger != null);
        m_logger = logger;
    }

    /** {@inheritDoc} */
    public String getName() {
        return m_logger.getName();
    }

    /** {@inheritDoc} */
    public boolean isTraceEnabled() {
        return m_logger.isTraceEnabled();
    }

    /** {@inheritDoc} */
    public void trace(String msg) {
        if (isEnabled()) {
            m_logger.trace(msg);
        }
    }

    /** {@inheritDoc} */
    public void trace(String format, Object arg) {
        if (isEnabled()) {
            m_logger.trace(format, arg);
        }
    }

    /** {@inheritDoc} */
    public void trace(String format, Object arg1, Object arg2) {
        if (isEnabled()) {
            m_logger.trace(format, arg1, arg2);
        }
    }

    /** {@inheritDoc} */
    public void trace(String format, Object... arguments) {
        if (isEnabled()) {
            m_logger.trace(format, arguments);
        }
    }

    /** {@inheritDoc} */
    public void trace(String msg, Throwable t) {
        if (isEnabled()) {
            m_logger.trace(msg, t);
        }
    }

    /** {@inheritDoc} */
    public boolean isTraceEnabled(Marker marker) {
        return m_logger.isTraceEnabled(marker);
    }

    /** {@inheritDoc} */
    public void trace(Marker marker, String msg) {
        if (isEnabled()) {
            m_logger.trace(marker, msg);
        }
    }

    /** {@inheritDoc} */
    public void trace(Marker marker, String format, Object arg) {
        if (isEnabled()) {
            m_logger.trace(marker, format, arg);
        }
    }

    /** {@inheritDoc} */
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        if (isEnabled()) {
            m_logger.trace(marker, format, arg1, arg2);
        }
    }

    /** {@inheritDoc} */
    public void trace(Marker marker, String format, Object... argArray) {
        if (isEnabled()) {
            m_logger.trace(marker, format, argArray);
        }
    }

    /** {@inheritDoc} */
    public void trace(Marker marker, String msg, Throwable t) {
        if (isEnabled()) {
            m_logger.trace(marker, msg, t);
        }
    }

    /** {@inheritDoc} */
    public boolean isDebugEnabled() {
        return m_logger.isDebugEnabled();
    }

    /** {@inheritDoc} */
    public void debug(String msg) {
        if (isEnabled()) {
            m_logger.debug(msg);
        }
    }

    /** {@inheritDoc} */
    public void debug(String format, Object arg) {
        if (isEnabled()) {
            m_logger.debug(format, arg);
        }
    }

    /** {@inheritDoc} */
    public void debug(String format, Object arg1, Object arg2) {
        if (isEnabled()) {
            m_logger.debug(format, arg1, arg2);
        }
    }

    /** {@inheritDoc} */
    public void debug(String format, Object... arguments) {
        if (isEnabled()) {
            m_logger.debug(format, arguments);
        }
    }

    /** {@inheritDoc} */
    public void debug(String msg, Throwable t) {
        if (isEnabled()) {
            m_logger.debug(msg, t);
        }
    }

    /** {@inheritDoc} */
    public boolean isDebugEnabled(Marker marker) {
        return m_logger.isDebugEnabled(marker);
    }

    /** {@inheritDoc} */
    public void debug(Marker marker, String msg) {
        if (isEnabled()) {
            m_logger.debug(marker, msg);
        }
    }

    /** {@inheritDoc} */
    public void debug(Marker marker, String format, Object arg) {
        if (isEnabled()) {
            m_logger.debug(marker, format, arg);
        }
    }

    /** {@inheritDoc} */
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        if (isEnabled()) {
            m_logger.debug(marker, format, arg1, arg2);
        }
    }

    /** {@inheritDoc} */
    public void debug(Marker marker, String format, Object... arguments) {
        if (isEnabled()) {
            m_logger.debug(marker, format, arguments);
        }
    }

    /** {@inheritDoc} */
    public void debug(Marker marker, String msg, Throwable t) {
        if (isEnabled()) {
            m_logger.debug(marker, msg, t);
        }
    }

    /** {@inheritDoc} */
    public boolean isInfoEnabled() {
        return m_logger.isInfoEnabled();
    }

    /** {@inheritDoc} */
    public void info(String msg) {
        if (isEnabled()) {
            m_logger.info(msg);
        }
    }

    /** {@inheritDoc} */
    public void info(String format, Object arg) {
        if (isEnabled()) {
            m_logger.info(format, arg);
        }
    }

    /** {@inheritDoc} */
    public void info(String format, Object arg1, Object arg2) {
        if (isEnabled()) {
            m_logger.info(format, arg1, arg2);
        }
    }

    /** {@inheritDoc} */
    public void info(String format, Object... arguments) {
        if (isEnabled()) {
            m_logger.info(format, arguments);
        }
    }

    /** {@inheritDoc} */
    public void info(String msg, Throwable t) {
        if (isEnabled()) {
            m_logger.info(msg, t);
        }
    }

    /** {@inheritDoc} */
    public boolean isInfoEnabled(Marker marker) {
        return m_logger.isInfoEnabled(marker);
    }

    /** {@inheritDoc} */
    public void info(Marker marker, String msg) {
        if (isEnabled()) {
            m_logger.info(marker, msg);
        }
    }

    /** {@inheritDoc} */
    public void info(Marker marker, String format, Object arg) {
        if (isEnabled()) {
            m_logger.info(marker, format, arg);
        }
    }

    /** {@inheritDoc} */
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        if (isEnabled()) {
            m_logger.info(marker, format, arg1, arg2);
        }
    }

    /** {@inheritDoc} */
    public void info(Marker marker, String format, Object... arguments) {
        if (isEnabled()) {
            m_logger.info(marker, format, arguments);
        }
    }

    /** {@inheritDoc} */
    public void info(Marker marker, String msg, Throwable t) {
        if (isEnabled()) {
            m_logger.info(marker, msg, t);
        }
    }

    /** {@inheritDoc} */
    public boolean isWarnEnabled() {
        return m_logger.isWarnEnabled();
    }

    /** {@inheritDoc} */
    public void warn(String msg) {
        if (isEnabled()) {
            m_logger.warn(msg);
        }
    }

    /** {@inheritDoc} */
    public void warn(String format, Object arg) {
        if (isEnabled()) {
            m_logger.warn(format, arg);
        }
    }

    /** {@inheritDoc} */
    public void warn(String format, Object... arguments) {
        if (isEnabled()) {
            m_logger.warn(format, arguments);
        }
    }

    /** {@inheritDoc} */
    public void warn(String format, Object arg1, Object arg2) {
        if (isEnabled()) {
            m_logger.warn(format, arg1, arg2);
        }
    }

    /** {@inheritDoc} */
    public void warn(String msg, Throwable t) {
        if (isEnabled()) {
            m_logger.warn(msg, t);
        }
    }

    /** {@inheritDoc} */
    public boolean isWarnEnabled(Marker marker) {
        return m_logger.isWarnEnabled(marker);
    }

    /** {@inheritDoc} */
    public void warn(Marker marker, String msg) {
        if (isEnabled()) {
            m_logger.warn(marker, msg);
        }
    }

    /** {@inheritDoc} */
    public void warn(Marker marker, String format, Object arg) {
        if (isEnabled()) {
            m_logger.warn(marker, format, arg);
        }
    }

    /** {@inheritDoc} */
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        if (isEnabled()) {
            m_logger.warn(marker, format, arg1, arg2);
        }
    }

    /** {@inheritDoc} */
    public void warn(Marker marker, String format, Object... arguments) {
        if (isEnabled()) {
            m_logger.warn(marker, format, arguments);
        }
    }

    /** {@inheritDoc} */
    public void warn(Marker marker, String msg, Throwable t) {
        if (isEnabled()) {
            m_logger.warn(marker, msg, t);
        }
    }

    /** {@inheritDoc} */
    public boolean isErrorEnabled() {
        return m_logger.isErrorEnabled();
    }

    /** {@inheritDoc} */
    public void error(String msg) {
        if (isEnabled()) {
            m_logger.error(msg);
        }
    }

    /** {@inheritDoc} */
    public void error(String format, Object arg) {
        if (isEnabled()) {
            m_logger.error(format, arg);
        }
    }

    /** {@inheritDoc} */
    public void error(String format, Object arg1, Object arg2) {
        if (isEnabled()) {
            m_logger.error(format, arg1, arg2);
        }
    }

    /** {@inheritDoc} */
    public void error(String format, Object... arguments) {
        if (isEnabled()) {
            m_logger.error(format, arguments);
        }
    }

    /** {@inheritDoc} */
    public void error(String msg, Throwable t) {
        if (isEnabled()) {
            m_logger.error(msg, t);
        }
    }

    /** {@inheritDoc} */
    public boolean isErrorEnabled(Marker marker) {
        return m_logger.isErrorEnabled(marker);
    }

    /** {@inheritDoc} */
    public void error(Marker marker, String msg) {
        if (isEnabled()) {
            m_logger.error(marker, msg);
        }
    }

    /** {@inheritDoc} */
    public void error(Marker marker, String format, Object arg) {
        if (isEnabled()) {
            m_logger.error(marker, format, arg);
        }
    }

    /** {@inheritDoc} */
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        if (isEnabled()) {
            m_logger.error(marker, format, arg1, arg2);
        }
    }

    /** {@inheritDoc} */
    public void error(Marker marker, String format, Object... arguments) {
        if (isEnabled()) {
            m_logger.error(marker, format, arguments);
        }
    }

    /** {@inheritDoc} */
    public void error(Marker marker, String msg, Throwable t) {
        if (isEnabled()) {
            m_logger.error(marker, msg, t);
        }
    }

    /**
     * @return the isEnabled
     */
    public boolean isEnabled() {
        return m_isEnabled;
    }

    /**
     * @param isEnabled
     *            the isEnabled to set
     */
    public void setEnabled(boolean isEnabled) {
        m_isEnabled = isEnabled;
    }
}