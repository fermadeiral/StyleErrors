/*
 * Copyright (c) 2010, 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.glassfish.tyrus.core.l10n;

/**
 * @author WS Development Team
 */
public final class LocalizableMessage implements Localizable {

    private final String _bundlename;
    private final String _key;
    private final Object[] _args;

    public LocalizableMessage(String bundlename, String key, Object... args) {
        _bundlename = bundlename;
        _key = key;
        if (args == null) {
            args = new Object[0];
        }
        _args = args;
    }

    @Override
    public String getKey() {
        return _key;
    }

    @Override
    public Object[] getArguments() {
        final Object[] copy = new Object[_args.length];
        System.arraycopy(_args, 0, copy, 0, _args.length);
        return copy;
    }

    @Override
    public String getResourceBundleName() {
        return _bundlename;
    }
}
