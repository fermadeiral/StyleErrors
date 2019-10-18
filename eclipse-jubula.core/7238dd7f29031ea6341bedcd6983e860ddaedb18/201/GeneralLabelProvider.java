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

import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.CompNameManager;
import org.eclipse.jubula.client.core.businessprocess.IComponentNameCache;
import org.eclipse.jubula.client.core.businessprocess.db.NodeBP;
import org.eclipse.jubula.client.core.businessprocess.problems.ProblemFactory;
import org.eclipse.jubula.client.core.model.IAbstractContainerPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.ICommentPO;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.ICondStructPO;
import org.eclipse.jubula.client.core.model.IConditionalStatementPO;
import org.eclipse.jubula.client.core.model.IControllerPO;
import org.eclipse.jubula.client.core.model.IDoWhilePO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.IIteratePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.IWhileDoPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.utils.NodeNameUtil;
import org.eclipse.jubula.client.core.utils.StringHelper;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.dnd.LocalSelectionClipboardTransfer;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all label provider
 *
 * @author BREDEX GmbH
 * @created May 11, 2010
 */
public class GeneralLabelProvider extends ColumnLabelProvider 
    implements IColorProvider, ILabelProvider {
    /**
     * <code>INACTIVE_PREFIX</code>
     */
    protected static final String INACTIVE_PREFIX = "// "; //$NON-NLS-1$
    
    /** close bracked */
    protected static final String CLOSE_BRACKED = "]"; //$NON-NLS-1$

    /** open bracked */
    protected static final String OPEN_BRACKED = " ["; //$NON-NLS-1$
    
    /** <code>SEPARATOR</code> */
    protected static final String SEPARATOR = "; "; //$NON-NLS-1$
    
    /** for negated conditions <code>NEGATED_POSTFIX</code> */
    protected static final String NEGATED_POSTFIX = " (negated) "; //$NON-NLS-1$
    
    /** the logger */
    private static final Logger LOG = 
            LoggerFactory.getLogger(GeneralLabelProvider.class);
    
    /**
     * <code>UNNAMED_NODE</code>
     */
    private static final String UNNAMED_NODE = 
            Messages.GeneralLabelProviderUnnamedNode;
    /**
     * <code>COMMENT_PREFIX</code>
     */
    private static final String COMMENT_PREFIX = 
        Messages.AbstractGuiNodePropertySourceComment + StringConstants.COLON
        + StringConstants.SPACE;
    
    /** The color for disabled elements */
    private static final Color DISABLED_COLOR = LayoutUtil.GRAY_COLOR;

    /** The color for reusedProjects */
    private static Color reusedProjectsColor = null;
    
    /** The color for comments */
    private static Color commentColor = null;
    
    /** clipboard */
    private static Clipboard clipboard = null;

    /** {@inheritDoc} */
    public void dispose() {
        // empty
    }

    /** {@inheritDoc} */
    public String getText(Object element) {
        return getTextImpl(element);
    }

    /** {@inheritDoc} */
    public Image getImage(Object element) {
        Image image = getImageImpl(element);
        
        // generated elements
        if (element instanceof INodePO
                && ((INodePO)element).isGenerated()) {
            image = Plugin.getGeneratedImage(image);
        }
        
        LocalSelectionClipboardTransfer transfer =
                LocalSelectionClipboardTransfer.getInstance();
        // elements that have been "cut" to the clipboard should be grayscale
        if (transfer.getIsItCut()) {
            Object cbContents = getClipboard().getContents(transfer);
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
     * returns the clipboard after creating it if necessary
     * @return the clipboard
     */
    private Clipboard getClipboard() {
        if (clipboard == null) {
            clipboard = new Clipboard(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell().getDisplay());
        }
        return clipboard;
    }

    /**
     * {@inheritDoc}
     */
    public String getToolTipText(Object element) {
        if (element instanceof ICommentPO) {
            ICommentPO comment = (ICommentPO)element;
            return comment.getName();
        }
        if (element instanceof INodePO) {
            INodePO node = (INodePO)element;
            StringBuilder toolTip = new StringBuilder();
            String comment = node.getComment();
            if (!StringUtils.isBlank(comment)) {
                toolTip.append(COMMENT_PREFIX);
                toolTip.append(ObjectUtils.toString(comment));
                return toolTip.toString();
            }
        }
        return super.getToolTipText(element);
    }
    

    /** {@inheritDoc} */
    public Point getToolTipShift(Object object) {
        return new Point(5, 5);
    }

    /** {@inheritDoc} */
    public int getToolTipDisplayDelayTime(Object object) {
        return 50;
    }

    /** {@inheritDoc} */
    public int getToolTipTimeDisplayed(Object object) {
        if (object instanceof ICommentPO) {
            String comment = ((ICommentPO) object).getName();
            int linebreaks = comment.split(System.lineSeparator() + "+").length; //$NON-NLS-1$
            int spaces = comment.split(StringConstants.SPACE + "+").length; //$NON-NLS-1$
            // estimate 500ms to read per word
            return (linebreaks + spaces) * 500;
        }
        return 5000;
    }
    
    /** {@inheritDoc} */
    public Image getToolTipImage(Object object) {
        if (object instanceof INodePO) {
            INodePO node = (INodePO)object;
            if (ProblemFactory.hasProblem(node)) {
                switch (ProblemFactory.getWorstProblem(node.getProblems())
                        .getStatus().getSeverity()) {
                    case IStatus.INFO:
                        return IconConstants.INFO_IMAGE;
                    case IStatus.WARNING:
                        return IconConstants.WARNING_IMAGE;
                    case IStatus.ERROR:
                        return IconConstants.ERROR_IMAGE;
                    default:
                        break;
                }
            }
        } else if (object instanceof IComponentNamePO) {
            if (((IComponentNamePO) object).getTypeProblem() != null) {
                return IconConstants.ERROR_IMAGE;
            }
        }
        return super.getToolTipImage(object);
    }
    
    /**
     * @param element the element to get the text for 
     * @return a descriptive text for the given element
     */
    public static String getTextImpl (Object element) {
        if (element instanceof INodePO) {
            String prefix = StringConstants.EMPTY;
            String name = null;
            INodePO node = (INodePO)element;
            if (node.isSpecObjCont()) {
                return Messages.TreeBuilderTestCases;
            }
            if (node.isExecObjCont()) {
                IProjectPO proj = GeneralStorage.getInstance().getProject();
                if (proj != null) {
                    return proj.getName();
                }
                
                LOG.error(Messages.GeneralLabelProvier_NoActiveProject);
            }
            if (!checkActivation(node)) {
                prefix = INACTIVE_PREFIX;
                if (!node.isActive() && !isParentActive(node)) {
                    prefix += INACTIVE_PREFIX;
                }
            }
            
            if (node instanceof IRefTestSuitePO) {
                name = NodeNameUtil.getText((IRefTestSuitePO)node);
            } else if (node.getName() == null) {
                name = UNNAMED_NODE;
            } else if (node instanceof ICapPO) {
                name = getText((ICapPO)node);
            } else if (node instanceof IExecTestCasePO) {
                name = NodeNameUtil.getText((IExecTestCasePO)node, true);
            } else if (node instanceof ISpecTestCasePO) {
                name = NodeNameUtil.getText((ISpecTestCasePO)node, true);
            }  else {
                name = node.getName();
            }
            String postfix = StringConstants.EMPTY;
            if (node instanceof ICondStructPO 
                    && ((ICondStructPO) node).isNegate()) {
                postfix = NEGATED_POSTFIX;
            }
            
            return new StringBuilder(prefix).append(name).append(postfix)
                    .toString();
        }

        if (element instanceof IReusedProjectPO) {
            IReusedProjectPO reusedProject = (IReusedProjectPO)element;
            String projectName = reusedProject.getProjectName();
            if (projectName == null) {
                projectName = reusedProject.getProjectGuid();
            }
            return projectName + StringConstants.UNDERSCORE
                    + StringConstants.LEFT_BRACKET
                    + reusedProject.getVersionString()
                    + StringConstants.RIGHT_BRACKET;
        }
        if (element instanceof AbstractTestCaseEditor) {
            return getTextImpl(
                    ((AbstractTestCaseEditor) element).getEditorHelper().
                    getEditSupport().getWorkVersion());
        }
        return element == null ? StringConstants.EMPTY : element.toString();
    }
    
    /**
     * @param element the element to get the image for 
     * @return an image for the given element
     */
    public static Image getImageImpl(Object element) {
        if (element instanceof INodePO
                && ((INodePO) element).isExecObjCont()) {
            return IconConstants.PROJECT_IMAGE;
        }
        if (element instanceof ITestSuitePO) {
            return IconConstants.TS_IMAGE;
        }
        
        if (element instanceof ICapPO) {
            return IconConstants.CAP_IMAGE;
        }
        
        if (element instanceof IEventExecTestCasePO) {
            return IconConstants.EH_IMAGE;
        }
        
        if (element instanceof IExecTestCasePO) {
            return IconConstants.TC_REF_IMAGE;
        }

        if (element instanceof ISpecTestCasePO) {
            return IconConstants.TC_IMAGE;
        }

        if (element instanceof ITestJobPO) {
            return IconConstants.TJ_IMAGE;
        }

        if (element instanceof ICategoryPO
                || element instanceof IReusedProjectPO) {
            return IconConstants.CATEGORY_IMAGE;
        }

        if (element instanceof IRefTestSuitePO) {
            return IconConstants.TS_REF_IMAGE;
        }
        
        if (element instanceof ITestDataCubePO) {
            return IconConstants.TDC_IMAGE;
        }
        
        if (element instanceof ICommentPO) {
            return IconConstants.COMMENT_IMAGE;
        }
        
        if (element instanceof IConditionalStatementPO) {
            return IconConstants.CONDITION;
        }
        
        if (element instanceof IDoWhilePO) {
            return IconConstants.DO_WHILE;
        }
        
        if (element instanceof IWhileDoPO) {
            return IconConstants.WHILE_DO;
        }
        
        if (element instanceof IAbstractContainerPO) {
            return IconConstants.CONTAINER;
        }
        
        if (element instanceof IIteratePO) {
            return IconConstants.ITERATE;
        }

        if (element instanceof AbstractTestCaseEditor) {
            return ((AbstractTestCaseEditor) element).getIcon();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Color getBackground(Object element) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Color getForeground(Object element) {
        if (element instanceof IExecTestCasePO 
                || element instanceof ICapPO
                || element instanceof IRefTestSuitePO
                || element instanceof IControllerPO
                || element instanceof IAbstractContainerPO) { 
            if (!checkActivation((INodePO)element)) {
                return LayoutUtil.INACTIVE_COLOR;
            }
            return DISABLED_COLOR;
        }

        if (element instanceof ISpecTestCasePO) {
            return null;
        }
        
        if (element instanceof IReusedProjectPO
                || (element instanceof INodePO 
                && !NodeBP.isEditable((INodePO)element))) {
            return getReusedProjectsColor();
        }
        
        if (element instanceof ICommentPO) {
            return getCommentColor();
        }

        return null;
    }
    
    /**
     * @param node to be checked
     * @return <code>false</code> if the node or one of the parents is inactive.
     *          Otherwise return <code>true</code>.
     */
    private static boolean checkActivation(INodePO node) {
        if (!node.isActive()) {
            return false;
        }
        return isParentActive(node);
    }

    /**
     * @param node to be checked
     * @return <code>false</code> if one of the parents is inactive.
     *          Otherwise return <code>true</code>.
     */
    private static boolean isParentActive(INodePO node) {
        INodePO parent = node.getParentNode();
        while (parent != null && !(parent instanceof ISpecTestCasePO)) {
            if (!parent.isActive()) {
                return false;
            }
            parent = parent.getParentNode();
        }
        return true;
    }
    
    /**
     * returns the color of a reused project after creating it if necessary
     * @return the color of a reused project
     */
    private Color getReusedProjectsColor() {
        if (reusedProjectsColor == null) {
            reusedProjectsColor = Display.getDefault()
                    .getSystemColor(SWT.COLOR_BLUE);
        }
        return reusedProjectsColor;
    }
    
    /**
     * returns the color of a comment after creating it if necessary
     * @return the color of a comment
     */
    private Color getCommentColor() {
        if (commentColor == null) {
            commentColor = Display.getDefault()
                    .getSystemColor(SWT.COLOR_BLUE);
        }
        return commentColor;
    }

    /**
     * 
     * @param testStep The Test Step to examine.
     * @return label text for the given Test Step.
     */
    private static String getText(ICapPO testStep) {
        if (Plugin.getDefault().getPreferenceStore().getBoolean(
                Constants.SHOWCAPINFO_KEY)) {
            StringBuilder nameBuilder = 
                new StringBuilder(testStep.getName());
            nameBuilder.append(GeneralLabelProvider.OPEN_BRACKED);
            final Map<String, String> map = 
                StringHelper.getInstance().getMap();
            IComponentNameCache compCache = 
                Plugin.getActiveCompCache();
            nameBuilder.append(Messages.CapGUIType)
                .append(map.get(testStep.getComponentType()))
                .append(GeneralLabelProvider.SEPARATOR)
                .append(Messages.CapGUIName);
            String componentName = testStep.getComponentName();
            if (compCache != null) {
                componentName = compCache.getNameByGuid(componentName);
            } else {
                componentName = 
                    CompNameManager.getInstance().getNameByGuid(componentName);
            }
            if (componentName != null) {
                nameBuilder.append(componentName);
            }
            nameBuilder.append(GeneralLabelProvider.SEPARATOR)
                .append(Messages.CapGUIAction)
                .append(map.get(testStep.getActionName()))
                .append(GeneralLabelProvider.CLOSE_BRACKED);
            return nameBuilder.toString();
        } 
        
        return testStep.getName();
    }

    /**
     * @param paramDesc The parameter description.
     * @return The short type name of the given parameter description, e.g.
     *         the name <code>String</code>, if the parameter has the type
     *         {@link java.lang.String}.
     */
    private static String getShortTypeName(IParamDescriptionPO paramDesc) {
        String typeName = paramDesc.getType();
        int i = typeName.lastIndexOf('.');
        return typeName.substring(i + 1);
    }

    /**
     * @param paramDesc The parameter description.
     * @return The text for a parameter description surrounded with brackets.
     */
    public static String getTextWithType(IParamDescriptionPO paramDesc) {
        return paramDesc.getName()
                + OPEN_BRACKED
                + getShortTypeName(paramDesc)
                + CLOSE_BRACKED;
    }
}