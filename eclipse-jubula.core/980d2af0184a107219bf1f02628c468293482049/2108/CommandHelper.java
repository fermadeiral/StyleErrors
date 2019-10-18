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
package org.eclipse.jubula.client.ui.utils;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  Helper Class to programmatically execute commands 
 *
 * @author BREDEX GmbH
 * @created Jul 28, 2010
 */
public abstract class CommandHelper {
    /** standard logging */
    private static Logger log = LoggerFactory.getLogger(CommandHelper.class);
    
    /**
     * Constructor
     */
    private CommandHelper() {
    // hide
    }
    
    /**
     * Execute the given commmandId using the workbench handler service
     * 
     * @param commandID
     *            the command to execute
     * @return The return value from the execution; may be null.
     */
    public static Object executeCommand(String commandID) {
        return executeCommand(commandID, null);
    }
    
    /**
     * Execute the given commmandId using the given part site for handler
     * service retrievement
     * 
     * @param commandID
     *            the command to execute
     * @param site
     *            the site to get the handler service from; may be <code>null</code>
     * @return The return value from the execution; may be null.
     */
    public static Object executeCommand(String commandID, 
        IWorkbenchPartSite site) {
        IHandlerService handlerService;
        if (site != null) {
            handlerService = site.getService(IHandlerService.class);
        } else {
            handlerService = getHandlerService();
        }
        try {
            return handlerService.executeCommand(commandID, null);
        } catch (CommandException e) {
            log.warn(Messages.ErrorOccurredWhileExecutingCommand 
                + StringConstants.COLON + StringConstants.SPACE + commandID);
        }
        return null;
    }
    
    /**
     * @param pc
     *            the parameterized command
     * @return The return value from the execution; may be null.
     */
    public static Object executeParameterizedCommand(ParameterizedCommand pc) {
        return executeParameterizedCommand(getHandlerService(), pc);
    }

    /**
     * @param hs
     *            the handler service to use
     * @param pc
     *            the parameterized command
     * @return The return value from the execution; may be null.
     */
    public static Object executeParameterizedCommand(IHandlerService hs,
        ParameterizedCommand pc) {
        try {
            return hs.executeCommand(pc, null);
        } catch (CommandException e) {
            log.warn(Messages.ErrorOccurredWhileExecutingCommand
                + StringConstants.COLON + StringConstants.SPACE + pc.getId());
        }
        return null;
    }
    
    /**
     * @return the handler service
     */
    public static IHandlerService getHandlerService() {
        return PlatformUI.getWorkbench().getService(IHandlerService.class);
    }
    
    /**
     * @return the command service
     */
    public static ICommandService getCommandService() {
        return PlatformUI.getWorkbench().getService(ICommandService.class);
    }
    
    /**
     * @param menuManager
     *            the menu to add the command contribution item for
     * @param commandId
     *            the id to create the item for
     */
    public static void createContributionPushItem(IMenuManager menuManager,
            String commandId) {
        menuManager.add(createContributionItem(commandId, null, null,
                CommandContributionItem.STYLE_PUSH));
    }
    
    /**
     * @param menuManager
     *            the menu to add the command contribution item for
     * @param commandId
     *            the id to create the item for
     * @param style
     *            the style
     */
    public static void createContributionItem(IMenuManager menuManager,
            String commandId, int style) {
        menuManager.add(createContributionItem(commandId, null, null, style));
    }

    /**
     * Creates and returns a contribution item representing the command with the
     * given ID.
     * 
     * @param commandId
     *            The ID of the command for which to create a contribution item.
     * @param params a map of parameters for this command
     * @param style
     *            The style to use for the contribution item. See the
     *            CommandContributionItem STYLE_* constants.
     * @param label
     *            the label to display for this item
     * @return the created contribution item.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static IContributionItem createContributionItem(String commandId,
            Map params, String label, int style) {
        
        CommandContributionItemParameter itemParameter =
            new CommandContributionItemParameter(
                    PlatformUI.getWorkbench(), null, commandId, style);
        itemParameter.label = label;
        if (params != null) {
            if (itemParameter.parameters == null) {
                itemParameter.parameters = new HashMap(params);
            } else {
                itemParameter.parameters.putAll(params);
            }
        }
        return new CommandContributionItem(itemParameter);
    }
}
