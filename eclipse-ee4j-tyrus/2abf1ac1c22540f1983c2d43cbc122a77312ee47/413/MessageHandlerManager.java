/*
 * Copyright (c) 2013, 2017 Oracle and/or its affiliates. All rights reserved.
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

import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.websocket.Decoder;
import javax.websocket.MessageHandler;
import javax.websocket.PongMessage;

import org.glassfish.tyrus.core.coder.CoderWrapper;
import org.glassfish.tyrus.core.l10n.LocalizationMessages;

/**
 * Manages registered {@link MessageHandler}s and checks whether the new ones may be registered.
 *
 * @author Stepan Kopriva (stepan.kopriva at oracle.com)
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 * @see MessageHandler
 * @see javax.websocket.OnMessage
 */
public class MessageHandlerManager {

    private static final List<Class<?>> WHOLE_TEXT_HANDLER_TYPES = Arrays.<Class<?>>asList(String.class, Reader.class);
    private static final Class<?> PARTIAL_TEXT_HANDLER_TYPE = String.class;
    private static final List<Class<?>> WHOLE_BINARY_HANDLER_TYPES = Arrays.<Class<?>>asList(
            ByteBuffer.class, InputStream.class, byte[].class);
    private static final List<Class<?>> PARTIAL_BINARY_HANDLER_TYPES = Arrays.<Class<?>>asList(
            ByteBuffer.class, byte[].class);
    private static final Class<?> PONG_HANDLER_TYPE = PongMessage.class;

    private boolean textHandlerPresent;
    private boolean textWholeHandlerPresent;
    private boolean binaryHandlerPresent;
    private boolean binaryWholeHandlerPresent;
    private boolean pongHandlerPresent;
    private boolean readerHandlerPresent;
    private boolean inputStreamHandlerPresent;
    private final Map<Class<?>, MessageHandler> registeredHandlers = new HashMap<Class<?>, MessageHandler>();
    private final List<Class<? extends Decoder>> decoders;

    private Set<MessageHandler> messageHandlerCache;

    /**
     * Construct manager with no decoders.
     */
    public MessageHandlerManager() {
        this(Collections.<Class<? extends Decoder>>emptyList());
    }

    /**
     * Construct manager.
     *
     * @param decoders registered {@link Decoder}s.
     */
    MessageHandlerManager(List<Class<? extends Decoder>> decoders) {
        this.decoders = decoders;
    }

    /**
     * Construct manager.
     *
     * @param decoders registered {@link Decoder}s.
     */
    static MessageHandlerManager fromDecoderInstances(List<Decoder> decoders) {
        List<Class<? extends Decoder>> decoderList = new ArrayList<Class<? extends Decoder>>();
        for (Decoder decoder : decoders) {
            if (decoder instanceof CoderWrapper) {
                decoderList.add(((CoderWrapper<? extends Decoder>) decoder).getCoderClass());
            } else {
                decoderList.add(decoder.getClass());
            }
        }

        return new MessageHandlerManager(decoderList);
    }

    /**
     * Construct manager.
     *
     * @param decoderClasses registered {@link Decoder}s.
     * @return constructed message handler manager.
     */
    public static MessageHandlerManager fromDecoderClasses(List<Class<? extends Decoder>> decoderClasses) {
        return new MessageHandlerManager(decoderClasses);
    }

    /**
     * Add {@link MessageHandler} to the manager.
     *
     * @param handler {@link MessageHandler} to be added to the manager.
     */
    public void addMessageHandler(MessageHandler handler) throws IllegalStateException {

        final Class<?> handlerClass = getHandlerType(handler);

        if (handler instanceof MessageHandler.Whole) { //WHOLE MESSAGE HANDLER
            addMessageHandler(handlerClass, (MessageHandler.Whole) handler);
        } else if (handler instanceof MessageHandler.Partial) { // PARTIAL MESSAGE HANDLER
            addMessageHandler(handlerClass, (MessageHandler.Partial) handler);
        } else {
            throwException(LocalizationMessages.MESSAGE_HANDLER_WHOLE_OR_PARTIAL());
        }
    }

    /**
     * Add {@link MessageHandler.Whole} to the manager.
     *
     * @param clazz   type handled by {@link MessageHandler}.
     * @param handler {@link MessageHandler} to be added.
     * @param <T>     type of message to be handled.
     * @throws IllegalStateException when the message handler cannot be added.
     */
    public <T> void addMessageHandler(Class<T> clazz, MessageHandler.Whole<T> handler) throws IllegalStateException {
        if (WHOLE_TEXT_HANDLER_TYPES.contains(clazz)) { // text
            if (textHandlerPresent) {
                throwException(LocalizationMessages.MESSAGE_HANDLER_ALREADY_REGISTERED_TEXT());
            } else {
                if (Reader.class.isAssignableFrom(clazz)) {
                    readerHandlerPresent = true;
                }
                textHandlerPresent = true;
                textWholeHandlerPresent = true;
            }
        } else if (WHOLE_BINARY_HANDLER_TYPES.contains(clazz)) { // binary
            if (binaryHandlerPresent) {
                throwException(LocalizationMessages.MESSAGE_HANDLER_ALREADY_REGISTERED_BINARY());
            } else {
                if (InputStream.class.isAssignableFrom(clazz)) {
                    inputStreamHandlerPresent = true;
                }
                binaryHandlerPresent = true;
                binaryWholeHandlerPresent = true;
            }
        } else if (PONG_HANDLER_TYPE.equals(clazz)) { // pong
            if (pongHandlerPresent) {
                throwException(LocalizationMessages.MESSAGE_HANDLER_ALREADY_REGISTERED_PONG());
            } else {
                pongHandlerPresent = true;
            }
        } else {
            boolean viable = false;

            if (checkTextDecoders(clazz)) { //decodable text
                if (textHandlerPresent) {
                    throwException(LocalizationMessages.MESSAGE_HANDLER_ALREADY_REGISTERED_TEXT());
                } else {
                    textHandlerPresent = true;
                    textWholeHandlerPresent = true;
                    viable = true;
                }
            }

            if (checkBinaryDecoders(clazz)) { //decodable binary
                if (binaryHandlerPresent) {
                    throwException(LocalizationMessages.MESSAGE_HANDLER_ALREADY_REGISTERED_BINARY());
                } else {
                    binaryHandlerPresent = true;
                    binaryWholeHandlerPresent = true;
                    viable = true;
                }
            }

            if (!viable) {
                throwException(LocalizationMessages.MESSAGE_HANDLER_DECODER_NOT_REGISTERED(clazz));
            }
        }

        registerMessageHandler(clazz, handler);
    }

    /**
     * Add {@link MessageHandler.Partial} to the manager.
     *
     * @param clazz   type handled by {@link MessageHandler}.
     * @param handler {@link MessageHandler} to be added.
     * @param <T>     type of message to be handled.
     * @throws IllegalStateException when the message handler cannot be added.
     */
    public <T> void addMessageHandler(Class<T> clazz, MessageHandler.Partial<T> handler) throws IllegalStateException {
        boolean viable = false;

        if (PARTIAL_TEXT_HANDLER_TYPE.equals(clazz)) { // text
            if (textHandlerPresent) {
                throwException(LocalizationMessages.MESSAGE_HANDLER_ALREADY_REGISTERED_TEXT());
            } else {
                textHandlerPresent = true;
                viable = true;
            }
        }

        if (PARTIAL_BINARY_HANDLER_TYPES.contains(clazz)) { // binary
            if (binaryHandlerPresent) {
                throwException(LocalizationMessages.MESSAGE_HANDLER_ALREADY_REGISTERED_BINARY());
            } else {
                binaryHandlerPresent = true;
                viable = true;
            }
        }

        if (!viable) {
            throwException(LocalizationMessages.MESSAGE_HANDLER_PARTIAL_INVALID_TYPE(clazz.getName()));
        }

        registerMessageHandler(clazz, handler);
    }

    private <T> void registerMessageHandler(Class<T> clazz, MessageHandler handler) {
        // map of all registered handlers
        if (registeredHandlers.containsKey(clazz)) {
            throwException(LocalizationMessages.MESSAGE_HANDLER_ALREADY_REGISTERED_TYPE(clazz));
        } else {
            registeredHandlers.put(clazz, handler);
        }

        messageHandlerCache = null;
    }

    private void throwException(String text) throws IllegalStateException {
        throw new IllegalStateException(text);
    }

    /**
     * Remove {@link MessageHandler} from the manager.
     *
     * @param handler handler which will be removed.
     */
    public void removeMessageHandler(MessageHandler handler) {
        Iterator<Map.Entry<Class<?>, MessageHandler>> iterator = registeredHandlers.entrySet().iterator();
        Class<?> handlerClass = null;

        while (iterator.hasNext()) {
            final Map.Entry<Class<?>, MessageHandler> next = iterator.next();
            if (next.getValue().equals(handler)) {
                handlerClass = next.getKey();
                iterator.remove();
                messageHandlerCache = null;
                break;
            }
        }

        if (handlerClass == null) {
            return;
        }

        if (handler instanceof MessageHandler.Whole) { //WHOLE MESSAGE HANDLER
            if (WHOLE_TEXT_HANDLER_TYPES.contains(handlerClass)) { // text
                textHandlerPresent = false;
                textWholeHandlerPresent = false;

            } else if (WHOLE_BINARY_HANDLER_TYPES.contains(handlerClass)) { // binary
                binaryHandlerPresent = false;
                binaryWholeHandlerPresent = false;

            } else if (PONG_HANDLER_TYPE.equals(handlerClass)) { // pong
                pongHandlerPresent = false;
            } else {
                if (checkTextDecoders(handlerClass)) { //decodable text
                    textHandlerPresent = false;
                    textWholeHandlerPresent = false;

                } else if (checkBinaryDecoders(handlerClass)) { //decodable binary
                    binaryHandlerPresent = false;
                    binaryWholeHandlerPresent = false;
                }
            }
        } else { // PARTIAL MESSAGE HANDLER
            if (PARTIAL_TEXT_HANDLER_TYPE.equals(handlerClass)) { // text
                textHandlerPresent = false;

            } else if (PARTIAL_BINARY_HANDLER_TYPES.contains(handlerClass)) { // binary
                binaryHandlerPresent = false;
            }
        }
    }

    /**
     * Get all successfully registered {@link MessageHandler}s.
     *
     * @return unmodifiable {@link Set} of registered {@link MessageHandler}s.
     */
    public Set<MessageHandler> getMessageHandlers() {
        if (messageHandlerCache == null) {
            messageHandlerCache = Collections.unmodifiableSet(new HashSet<MessageHandler>(registeredHandlers.values()));
        }

        return messageHandlerCache;
    }

    public List<Map.Entry<Class<?>, MessageHandler>> getOrderedWholeMessageHandlers() {
        List<Map.Entry<Class<?>, MessageHandler>> result = new ArrayList<Map.Entry<Class<?>, MessageHandler>>();
        for (final Map.Entry<Class<?>, MessageHandler> entry : registeredHandlers.entrySet()) {
            if (entry.getValue() instanceof MessageHandler.Whole) {
                result.add(entry);
            }
        }
        Collections.sort(result, new MessageHandlerComparator());
        return result;
    }

    static Class<?> getHandlerType(MessageHandler handler) {
        Class<?> root;
        if (handler instanceof AsyncMessageHandler) {
            return ((AsyncMessageHandler) handler).getType();
        } else if (handler instanceof BasicMessageHandler) {
            return ((BasicMessageHandler) handler).getType();
        } else if (handler instanceof MessageHandler.Partial) {
            root = MessageHandler.Partial.class;
        } else if (handler instanceof MessageHandler.Whole) {
            root = MessageHandler.Whole.class;
        } else {
            throw new IllegalArgumentException(LocalizationMessages.MESSAGE_HANDLER_ILLEGAL_ARGUMENT(handler));
        }
        Class<?> result = ReflectionHelper.getClassType(handler.getClass(), root);
        return result == null ? Object.class : result;
    }

    private boolean checkTextDecoders(Class<?> requiredType) {
        for (Class<? extends Decoder> decoderClass : decoders) {
            if (isTextDecoder(decoderClass)
                    && requiredType.isAssignableFrom(AnnotatedEndpoint.getDecoderClassType(decoderClass))) {
                return true;
            }
        }

        return false;
    }

    private boolean checkBinaryDecoders(Class<?> requiredType) {
        for (Class<? extends Decoder> decoderClass : decoders) {
            if (isBinaryDecoder(decoderClass)
                    && requiredType.isAssignableFrom(AnnotatedEndpoint.getDecoderClassType(decoderClass))) {
                return true;
            }
        }

        return false;
    }

    private boolean isTextDecoder(Class<? extends Decoder> decoderClass) {
        return Decoder.Text.class.isAssignableFrom(decoderClass)
                || Decoder.TextStream.class.isAssignableFrom(decoderClass);
    }

    private boolean isBinaryDecoder(Class<? extends Decoder> decoderClass) {
        return Decoder.Binary.class.isAssignableFrom(decoderClass)
                || Decoder.BinaryStream.class.isAssignableFrom(decoderClass);
    }

    boolean isWholeTextHandlerPresent() {
        return textWholeHandlerPresent;
    }

    boolean isWholeBinaryHandlerPresent() {
        return binaryWholeHandlerPresent;
    }

    boolean isPartialTextHandlerPresent() {
        return textHandlerPresent && !textWholeHandlerPresent;
    }

    boolean isPartialBinaryHandlerPresent() {
        return binaryHandlerPresent && !binaryWholeHandlerPresent;
    }

    public boolean isReaderHandlerPresent() {
        return readerHandlerPresent;
    }

    public boolean isInputStreamHandlerPresent() {
        return inputStreamHandlerPresent;
    }

    boolean isPongHandlerPresent() {
        return pongHandlerPresent;
    }

    private static class MessageHandlerComparator implements Comparator<Map.Entry<Class<?>, MessageHandler>>,
            Serializable {

        private static final long serialVersionUID = -5136634876439146784L;

        @Override
        public int compare(Map.Entry<Class<?>, MessageHandler> o1, Map.Entry<Class<?>, MessageHandler> o2) {
            if (o1.getKey().isAssignableFrom(o2.getKey())) {
                return 1;
            } else if (o2.getKey().isAssignableFrom(o1.getKey())) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
