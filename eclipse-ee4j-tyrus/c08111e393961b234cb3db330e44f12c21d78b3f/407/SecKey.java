/*
 * Copyright (c) 2012, 2017 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.tyrus.core;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

import org.glassfish.tyrus.core.l10n.LocalizationMessages;
import org.glassfish.tyrus.spi.UpgradeRequest;

/**
 * Class represents security key, used during the handshake phase.
 *
 * @author Alexey Stashok
 */
class SecKey {
    private static final Random random = new SecureRandom();

    private static final int KEY_SIZE = 16;

    /**
     * Security key string representation, which includes chars and spaces.
     */
    private final String secKey;

    public SecKey() {
        secKey = create();
    }

    private String create() {
        byte[] bytes = new byte[KEY_SIZE];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Constructor.
     *
     * @param base64 sec key to be represented as {@link SecKey} instance.
     * @throws HandshakeException when the provided key is {@code null}.
     */
    public SecKey(String base64) throws HandshakeException {
        if (base64 == null) {
            throw new HandshakeException(LocalizationMessages.SEC_KEY_NULL_NOT_ALLOWED());
        }
        secKey = base64;
    }

    /**
     * Generate server-side security key, which gets passed to the client during
     * the handshake phase as part of message payload.
     *
     * @param clientKey client's Sec-WebSocket-Key
     * @return server key.
     */
    public static SecKey generateServerKey(SecKey clientKey) throws HandshakeException {
        String key = clientKey.getSecKey() + UpgradeRequest.SERVER_KEY_HASH;
        final MessageDigest instance;
        try {
            instance = MessageDigest.getInstance("SHA-1");
            instance.update(key.getBytes("UTF-8"));
            final byte[] digest = instance.digest();
            if (digest.length != 20) {
                throw new HandshakeException(LocalizationMessages.SEC_KEY_INVALID_LENGTH(digest.length));
            }

            return new SecKey(Base64.getEncoder().encodeToString(digest));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new HandshakeException(e.getMessage());
        }
    }

    /**
     * Gets security key string representation, which includes chars and spaces.
     *
     * @return Security key string representation, which includes chars and spaces.
     */
    public String getSecKey() {
        return secKey;
    }

    @Override
    public String toString() {
        return secKey;
    }

    /**
     * Validate provided server key.
     *
     * @param serverKey server key to be validated.
     * @throws HandshakeException when the server key is invalid.
     */
    public void validateServerKey(String serverKey) throws HandshakeException {
        final SecKey key = generateServerKey(this);
        if (!key.getSecKey().equals(serverKey)) {
            throw new HandshakeException(LocalizationMessages.SEC_KEY_INVALID_SERVER());
        }
    }
}
