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
package org.eclipse.jubula.client.ui.rcp.provider.labelprovider;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jubula.client.core.businessprocess.IComponentNameCache;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.dnd.LocalSelectionClipboardTransfer;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;


/**
 * @author BREDEX GmbH
 * @created 15.10.2004
 */
public class OMEditorTreeLabelProvider extends LabelProvider {
    /** 
     * mapping from top-level category name to i18n key for top-level
     * category name 
     */
    private static Map<String, String> topLevelCategoryToNameKey =
        new HashMap<String, String>();
    
    static {
        topLevelCategoryToNameKey.put(
                IObjectMappingCategoryPO.MAPPEDCATEGORY,
                "ObjectMappingEditor.Assigned"); //$NON-NLS-1$
        topLevelCategoryToNameKey.put(
                IObjectMappingCategoryPO.UNMAPPEDLOGICALCATEGORY,
                "ObjectMappingEditor.UnAssignedLogic"); //$NON-NLS-1$
        topLevelCategoryToNameKey.put(
                IObjectMappingCategoryPO.UNMAPPEDTECHNICALCATEGORY,
                "ObjectMappingEditor.UnAssignedTech"); //$NON-NLS-1$
    }

    /** the component cache to use for finding and modifying components */
    private IComponentNameCache m_compCache;
    
    /** clipboard */
    private Clipboard m_clipboard = new Clipboard(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell().getDisplay());

    /**
     * Constructor
     * 
     * @param compCache The component cache to use for finding and modifying 
     *                   Component Names.
     */
    public OMEditorTreeLabelProvider(IComponentNameCache compCache) {
        m_compCache = compCache;
    }
    
    /**
     * {@inheritDoc}
     */
    public void dispose() {
        m_clipboard.dispose();
    }
    
    
    /** 
     * 
     * @param element Object the Object
     * @return Image an Image
     */
    public Image getImage(Object element) {
        Image image = null;
        if (element instanceof IComponentNamePO) {
            image = IconConstants.LOGICAL_NAME_IMAGE;
        } else if (element instanceof IObjectMappingAssoziationPO) {
            int status = getQualitySeverity(
                    ((IObjectMappingAssoziationPO)element)
                        .getCompIdentifier());
            switch (status) {
                case IStatus.OK:
                    image = IconConstants.TECH_NAME_OK_IMAGE;
                    break;
                case IStatus.WARNING:
                    image = IconConstants.TECH_NAME_WARNING_IMAGE;
                    break;
                case IStatus.ERROR:
                    image = IconConstants.TECH_NAME_ERROR_IMAGE;
                    break;
                default:
                    image = IconConstants.TECHNICAL_NAME_IMAGE;
                    break;
            }
            if (((IObjectMappingAssoziationPO) element).getTechnicalName()
                    .getProfile() != null) {
                image = IconConstants.decorateImage(image,
                        IconConstants.TRIANGLE_OVERLAY, IDecoration.TOP_LEFT);
            }
        } else if (element instanceof IObjectMappingCategoryPO) {
            image = IconConstants.CATEGORY_IMAGE;
        } else if (element instanceof String) {
            // Missing Component Name
            image = IconConstants.LOGICAL_NAME_IMAGE;
        } else {
            String elementType = element != null 
                ? element.getClass().getName() : "null"; //$NON-NLS-1$
            StringBuilder msg = new StringBuilder();
            msg.append(Messages.ElementType)
                .append(StringConstants.SPACE)
                .append(StringConstants.APOSTROPHE)
                .append(elementType)
                .append(StringConstants.APOSTROPHE)
                .append(StringConstants.SPACE)
                .append(Messages.NotSupported)
                .append(StringConstants.DOT);
            Assert.notReached(msg.toString());
            return null;
        }

        LocalSelectionClipboardTransfer transfer =
                LocalSelectionClipboardTransfer.getInstance();
        if (transfer.getIsItCut()) {
            Object cbContents = m_clipboard.getContents(transfer);
            
            if (cbContents instanceof IStructuredSelection) {
                IStructuredSelection sel = (IStructuredSelection)cbContents;
                if (sel.toList().contains(element)) {
                    image = Plugin.getCutImage(image);
                }
            }
        }
        
        return image;
    }
    
    /**
     * @param identifier
     *            the identifier to check for its quality
     * @return an IStatus severity indicating the quality
     */
    public static int getQualitySeverity(IComponentIdentifier identifier) {
        if (identifier != null) {
            int noOfMatchedComps = identifier
                    .getNumberOfOtherMatchingComponents();
            if (identifier.isEqualOriginalFound()) {
                if (noOfMatchedComps == 1) {
                    return IStatus.OK;
                }
                return IStatus.WARNING;
            }
            return IStatus.ERROR;
        }
        return IStatus.CANCEL;
    }
    
    /**
     * @param element
     *            Object
     * @return name String
     */
    public String getText(Object element) {
        if (element instanceof IObjectMappingAssoziationPO) {
            IComponentIdentifier compId = 
                ((IObjectMappingAssoziationPO)element).getTechnicalName();
            if (compId != null) {
                String res = compId.getComponentNameToDisplay();
                return StringUtils.abbreviate(res.replace(
                        StringConstants.NEWLINE, StringConstants.SPACE), 200);
            }
        } else if (element instanceof IComponentNamePO) {
            return m_compCache.getNameByGuid(
                    ((IComponentNamePO)element).getGuid());
        } else if (element instanceof IObjectMappingCategoryPO) {
            IObjectMappingCategoryPO category = 
                (IObjectMappingCategoryPO)element;
            StringBuilder nameBuilder = new StringBuilder();
            String catName = category.getName();
            if (getTopLevelCategoryName(catName) != null) {
                catName = getTopLevelCategoryName(catName);
            }
            nameBuilder.append(catName);

            if (Plugin.getDefault().getPreferenceStore()
                    .getBoolean(Constants.SHOWCHILDCOUNT_KEY)) {
                int childListSize = 0;
                childListSize += 
                    category.getUnmodifiableAssociationList().size();
                childListSize += 
                    category.getUnmodifiableCategoryList().size();
                nameBuilder.append(StringConstants.SPACE 
                        + StringConstants.LEFT_PARENTHESIS)
                            .append(childListSize).append(StringConstants
                                    .RIGHT_PARENTHESIS);
            }
            return nameBuilder.toString();
        } else if (element instanceof String) {
            // Missing Component Name
            return (String)element;
        }


        Assert.notReached(
                org.eclipse.jubula.client.ui.i18n.Messages
                    .UnknownTypeOfElementInTreeOfType
                + StringConstants.SPACE + element.getClass().getName());
        return StringConstants.EMPTY;
    }
    
    /**
     * 
     * @param key The untranslated name of a top-level category.
     * @return the translated category name, or <code>null</code> if there is
     *         no translation for the given key.
     */
    public static String getTopLevelCategoryName(String key) {
        if (topLevelCategoryToNameKey.containsKey(key)) {
            return I18n.getString(topLevelCategoryToNameKey.get(key));
        }
        
        return null;
    }
}
