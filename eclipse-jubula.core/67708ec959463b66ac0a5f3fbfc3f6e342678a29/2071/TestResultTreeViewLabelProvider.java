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
package org.eclipse.jubula.client.ui.provider.labelprovider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jubula.client.core.model.IAbstractContainerPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICommentPO;
import org.eclipse.jubula.client.core.model.IConditionalStatementPO;
import org.eclipse.jubula.client.core.model.IDoWhilePO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.IIteratePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ITestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.IWhileDoPO;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;


/**
 * @author BREDEX GmbH
 * @created 18.10.2004
 */
public class TestResultTreeViewLabelProvider extends CellLabelProvider
        implements ILabelProvider {
    /** ImageCache */
    private static Map<ImageDescriptor, Image> imageCache = 
           new HashMap<ImageDescriptor, Image>();

    /**
     *  
     */
    public TestResultTreeViewLabelProvider() {
        super();
    }

    /**
     * dispose images
     */
    public void dispose() {
        for (Iterator<Image> i = imageCache.values().iterator(); i.hasNext();) {
            i.next().dispose();
        }
        imageCache.clear();
    }

    /**
     * 
     * @param element
     *            Object
     * @return Image
     */
    public Image getImage(Object element) {
        TestResultNode resultTest = (TestResultNode) element;
        INodePO node = resultTest.getNode();
        Image image = null;
        if (node instanceof ITestSuitePO) {
            image = IconConstants.TS_IMAGE;
        } else if (node instanceof ITestCasePO) {
            image = IconConstants.TC_IMAGE;
            if (node instanceof IEventExecTestCasePO) {
                image = IconConstants.RESULT_EH_IMAGE;
            }
        } else if (node instanceof ICapPO) {
            TestResultNode parent = resultTest.getParent();
            if (parent.getNode() instanceof IEventExecTestCasePO) {
                image = IconConstants.EH_CAP_IMAGE;
            } else {
                image = IconConstants.CAP_IMAGE;
            }
        } else if (node instanceof ICommentPO) {
            image = IconConstants.COMMENT_IMAGE;
        } else if (node instanceof IConditionalStatementPO) {
            image = IconConstants.CONDITION;
        } else if (node instanceof IAbstractContainerPO) {
            image = IconConstants.CAP_IMAGE;
        } else if (node instanceof IDoWhilePO) {
            image = IconConstants.DO_WHILE;
        } else if (node instanceof IWhileDoPO) {
            image = IconConstants.WHILE_DO;
        } else if (node instanceof IIteratePO) {
            image = IconConstants.ITERATE;
        }
        return image;
    }
    
    /**
     * @param element
     *            Object
     * @return name String
     */
    public String getText(Object element) {
        if (element instanceof TestResultNode) {
            TestResultNode resultTest = (TestResultNode)element;
            String name = resultTest.getName();
            if (name != null) {
                return name;
            }
            INodePO node = resultTest.getNode();
            if (node instanceof ITestSuitePO) {
                return "ResultTestSuite"; //$NON-NLS-1$
            }
            if (node instanceof IExecTestCasePO) {
                return "ResultTestCase"; //$NON-NLS-1$
            } 
            if (node instanceof ICapPO) {
                return "ResultCap"; //$NON-NLS-1$
            }
        }
        throw unknownElement(element);
    }

    /**
     * 
     * @param element
     *            Object
     * @return RuntimeException Unknown type
     */
    public RuntimeException unknownElement(Object element) {
        return new RuntimeException(Messages.UnknownTypeOfElementInTreeOfType
            + element.getClass().getName());
    }

    /**
     * {@inheritDoc}
     */
    public String getToolTipText(Object element) {
        if (element instanceof TestResultNode) {
            TestResultNode resultNode = (TestResultNode) element;
            INodePO node = ((TestResultNode) element).getNode();
            if (node instanceof ICommentPO) {
                ICommentPO comment = (ICommentPO)node;
                return comment.getName();
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
    public void update(ViewerCell cell) {
        Object element = cell.getElement();
        cell.setText(getText(element));
        Image image = getImage(element);
        cell.setImage(image);
    }
}
