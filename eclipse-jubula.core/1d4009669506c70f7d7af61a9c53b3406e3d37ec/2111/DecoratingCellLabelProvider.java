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
package org.eclipse.jubula.client.ui.provider;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DecorationContext;
import org.eclipse.jface.viewers.IColorDecorator;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IDecorationContext;
import org.eclipse.jface.viewers.IDelayedLabelDecorator;
import org.eclipse.jface.viewers.IFontDecorator;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreePathLabelProvider;
import org.eclipse.jface.viewers.IViewerLabelProvider;
import org.eclipse.jface.viewers.LabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

/**
 * A decorating label provider is a label provider which combines a nested label
 * provider and an optional decorator. The decorator decorates the label text,
 * image, font and colors provided by the nested label provider.
 * @see http://eclip.se/182216
 */
public class DecoratingCellLabelProvider extends CellLabelProvider implements
        ILabelProvider, IViewerLabelProvider, IColorProvider, IFontProvider,
        ITreePathLabelProvider {

    /**
     * <code>provider</code>
     */
    private ILabelProvider m_provider;

    /**
     * <code>decorator</code>
     */
    private ILabelDecorator m_decorator;

    /**
     * Need to keep our own list of listeners
     * <code>listeners</code>
     */
    private ListenerList m_listeners = new ListenerList();

    /**
     * <code>decorationContext</code>
     */
    private IDecorationContext m_decorationContext;

    /**
     * Creates a decorating label provider which uses the given label decorator
     * to decorate labels provided by the given label provider.
     * 
     * @param provider
     *            the nested label provider
     * @param decorator
     *            the label decorator, or <code>null</code> if no decorator is
     *            to be used initially
     */
    public DecoratingCellLabelProvider(ILabelProvider provider,
            ILabelDecorator decorator) {
        Assert.isNotNull(provider);
        this.m_provider = provider;
        this.m_decorator = decorator;
        this.m_decorationContext = createDefaultDecorationContext();
    }

    /**
     * Create a decoration context for the receiver that has a
     * LocalResourceManager.
     * 
     * @return the DefaultDecorationContext
     */
    private IDecorationContext createDefaultDecorationContext() {
        return new DecorationContext();
    }

    /**
     * The <code>DecoratingLabelProvider</code> implementation of this
     * <code>IBaseLabelProvider</code> method adds the listener to both the
     * nested label provider and the label decorator.
     * 
     * @param listener
     *            a label provider listener
     */
    public void addListener(ILabelProviderListener listener) {
        super.addListener(listener);
        m_provider.addListener(listener);
        if (m_decorator != null) {
            m_decorator.addListener(listener);
        }
        m_listeners.add(listener);
    }

    /**
     * The <code>DecoratingLabelProvider</code> implementation of this
     * <code>IBaseLabelProvider</code> method disposes both the nested label
     * provider and the label decorator.
     */
    public void dispose() {
        m_provider.dispose();
        if (m_decorator != null) {
            m_decorator.dispose();
        }
    }

    /**
     * The <code>DecoratingLabelProvider</code> implementation of this
     * <code>ILabelProvider</code> method returns the image provided by the
     * nested label provider's <code>getImage</code> method, decorated with the
     * decoration provided by the label decorator's <code>decorateImage</code>
     * method.
     * @param element the element to get the image for
     * @return the image
     */
    public Image getImage(Object element) {
        Image image = m_provider.getImage(element);
        if (m_decorator != null) {
            if (m_decorator instanceof LabelDecorator) {
                LabelDecorator ld2 = (LabelDecorator)m_decorator;
                Image decorated = ld2.decorateImage(image, element,
                        getDecorationContext());
                if (decorated != null) {
                    return decorated;
                }
            } else {
                Image decorated = m_decorator.decorateImage(image, element);
                if (decorated != null) {
                    return decorated;
                }
            }
        }
        return image;
    }

    /**
     * Returns the label decorator, or <code>null</code> if none has been set.
     * 
     * @return the label decorator, or <code>null</code> if none has been set.
     */
    public ILabelDecorator getLabelDecorator() {
        return m_decorator;
    }

    /**
     * Returns the nested label provider.
     * 
     * @return the nested label provider
     */
    public ILabelProvider getLabelProvider() {
        return m_provider;
    }

    /**
     * The <code>DecoratingLabelProvider</code> implementation of this
     * <code>ILabelProvider</code> method returns the text label provided by the
     * nested label provider's <code>getText</code> method, decorated with the
     * decoration provided by the label decorator's <code>decorateText</code>
     * method.
     * @param element the element to get the text for
     * @return the text
     */
    public String getText(Object element) {
        String text = m_provider.getText(element);
        if (m_decorator != null) {
            if (m_decorator instanceof LabelDecorator) {
                LabelDecorator ld2 = (LabelDecorator)m_decorator;
                String decorated = ld2.decorateText(text, element,
                        getDecorationContext());
                if (decorated != null) {
                    return decorated;
                }
            } else {
                String decorated = m_decorator.decorateText(text, element);
                if (decorated != null) {
                    return decorated;
                }
            }
        }
        return text;
    }

    /**
     * The <code>DecoratingLabelProvider</code> implementation of this
     * <code>IBaseLabelProvider</code> method returns <code>true</code> if the
     * corresponding method on the nested label provider returns
     * <code>true</code> or if the corresponding method on the decorator returns
     * <code>true</code>.
     * @param element the element
     * @param property the property
     * @return true or false
     */
    public boolean isLabelProperty(Object element, String property) {
        if (m_provider.isLabelProperty(element, property)) {
            return true;
        }
        return m_decorator != null
                && m_decorator.isLabelProperty(element, property);
    }

    /**
     * The <code>DecoratingLabelProvider</code> implementation of this
     * <code>IBaseLabelProvider</code> method removes the listener from both the
     * nested label provider and the label decorator.
     * 
     * @param listener
     *            a label provider listener
     */
    public void removeListener(ILabelProviderListener listener) {
        super.removeListener(listener);
        m_provider.removeListener(listener);
        if (m_decorator != null) {
            m_decorator.removeListener(listener);
        }
        m_listeners.remove(listener);
    }

    /**
     * Sets the label decorator. Removes all known listeners from the old
     * decorator, and adds all known listeners to the new decorator. The old
     * decorator is not disposed. Fires a label provider changed event
     * indicating that all labels should be updated. Has no effect if the given
     * decorator is identical to the current one.
     * 
     * @param decorator
     *            the label decorator, or <code>null</code> if no decorations
     *            are to be applied
     */
    public void setLabelDecorator(ILabelDecorator decorator) {
        ILabelDecorator oldDecorator = this.m_decorator;
        if (oldDecorator != decorator) {
            Object[] listenerList = this.m_listeners.getListeners();
            if (oldDecorator != null) {
                for (int i = 0; i < listenerList.length; i++) {
                    oldDecorator
                            .removeListener(
                                    (ILabelProviderListener)listenerList[i]);
                }
            }
            this.m_decorator = decorator;
            if (decorator != null) {
                for (int i = 0; i < listenerList.length; i++) {
                    decorator
                            .addListener(
                                    (ILabelProviderListener)listenerList[i]);
                }
            }
            fireLabelProviderChanged(new LabelProviderChangedEvent(this));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateLabel(ViewerLabel settings, Object element) {
        ILabelDecorator currentDecorator = getLabelDecorator();
        String oldText = settings.getText();
        boolean decorationReady = true;
        if (currentDecorator instanceof IDelayedLabelDecorator) {
            IDelayedLabelDecorator delayedDecorator = 
                (IDelayedLabelDecorator)currentDecorator;
            if (!delayedDecorator.prepareDecoration(element, oldText)) {
                // The decoration is not ready but has been queued for
                // processing
                decorationReady = false;
            }
        }
        // update icon and label

        if (decorationReady || oldText == null
                || settings.getText().length() == 0) {
            settings.setText(getText(element));
        }

        Image oldImage = settings.getImage();
        if (decorationReady || oldImage == null) {
            settings.setImage(getImage(element));
        }

        if (decorationReady) {
            updateForDecorationReady(settings, element);
        }

    }

    /**
     * Decoration is ready. Update anything else for the settings.
     * 
     * @param settings
     *            The object collecting the settings.
     * @param element
     *            The Object being decorated.
     * @since 3.1
     */
    protected void updateForDecorationReady(ViewerLabel settings, 
            Object element) {
        if (m_decorator instanceof IColorDecorator) {
            IColorDecorator colorDecorator = (IColorDecorator)m_decorator;
            Color color = colorDecorator.decorateBackground(element);
            if (color != null) {
                settings.setBackground(color);
            }
            color = colorDecorator.decorateForeground(element);
            if (color != null) {
                settings.setForeground(color);
            }
        }

        if (m_decorator instanceof IFontDecorator) {
            Font font = ((IFontDecorator)m_decorator).decorateFont(element);
            if (font != null) {
                settings.setFont(font);
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    public Color getBackground(Object element) {
        if (m_provider instanceof IColorProvider) {
            return ((IColorProvider)m_provider).getBackground(element);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Font getFont(Object element) {
        if (m_provider instanceof IFontProvider) {
            return ((IFontProvider)m_provider).getFont(element);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Color getForeground(Object element) {
        if (m_provider instanceof IColorProvider) {
            return ((IColorProvider)m_provider).getForeground(element);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Color getToolTipBackgroundColor(Object object) {
        if (m_provider instanceof CellLabelProvider) {
            return ((CellLabelProvider)m_provider)
                    .getToolTipBackgroundColor(object);
        }
        return super.getToolTipBackgroundColor(object);
    }

    /**
     * {@inheritDoc}
     */
    public int getToolTipDisplayDelayTime(Object object) {
        if (m_provider instanceof CellLabelProvider) {
            return ((CellLabelProvider)m_provider)
                    .getToolTipDisplayDelayTime(object);
        }
        return super.getToolTipDisplayDelayTime(object);
    }

    /**
     * {@inheritDoc}
     */
    public Font getToolTipFont(Object object) {
        if (m_provider instanceof CellLabelProvider) {
            return ((CellLabelProvider)m_provider).getToolTipFont(object);
        }
        return super.getToolTipFont(object);
    }

    /**
     * {@inheritDoc}
     */
    public Color getToolTipForegroundColor(Object object) {
        if (m_provider instanceof CellLabelProvider) {
            return ((CellLabelProvider)m_provider)
                    .getToolTipForegroundColor(object);
        }
        return super.getToolTipForegroundColor(object);
    }

    /**
     * {@inheritDoc}
     */
    public Image getToolTipImage(Object object) {
        if (m_provider instanceof CellLabelProvider) {
            return ((CellLabelProvider)m_provider).getToolTipImage(object);
        }
        return super.getToolTipImage(object);
    }

    /**
     * {@inheritDoc}
     */
    public Point getToolTipShift(Object object) {
        if (m_provider instanceof CellLabelProvider) {
            return ((CellLabelProvider)m_provider).getToolTipShift(object);
        }
        return super.getToolTipShift(object);
    }

    /**
     * {@inheritDoc}
     */
    public int getToolTipStyle(Object object) {
        if (m_provider instanceof CellLabelProvider) {
            return ((CellLabelProvider)m_provider).getToolTipStyle(object);
        }
        return super.getToolTipStyle(object);
    }

    /**
     * {@inheritDoc}
     */
    public String getToolTipText(Object object) {
        if (m_provider instanceof CellLabelProvider) {
            return ((CellLabelProvider)m_provider).getToolTipText(object);
        }
        return super.getToolTipText(object);
    }

    /**
     * {@inheritDoc}
     */
    public int getToolTipTimeDisplayed(Object object) {
        if (m_provider instanceof CellLabelProvider) {
            return ((CellLabelProvider)m_provider)
                    .getToolTipTimeDisplayed(object);
        }
        return super.getToolTipTimeDisplayed(object);
    }

    /**
     * Return the decoration context associated with this label provider. It
     * will be passed to the decorator if the decorator is an instance of
     * {@link LabelDecorator}.
     * 
     * @return the decoration context associated with this label provider
     * 
     * @since 3.2
     */
    public IDecorationContext getDecorationContext() {
        return m_decorationContext;
    }

    /**
     * Set the decoration context that will be based to the decorator for this
     * label provider if that decorator implements {@link LabelDecorator}.
     * 
     * If this decorationContext has a 
     * {@link org.eclipse.jface.resource.ResourceManager} stored for the
     * {@link DecorationContext#RESOURCE_MANAGER_KEY} property it will be
     * disposed when the label provider is disposed.
     * 
     * @param decorationContext
     *            the decoration context.
     * 
     * @since 3.2
     */
    public void setDecorationContext(IDecorationContext decorationContext) {
        org.eclipse.core.runtime.Assert.isNotNull(decorationContext);
        this.m_decorationContext = decorationContext;
    }

    /**
     * {@inheritDoc}
     */
    public void updateLabel(ViewerLabel settings, TreePath elementPath) {
        ILabelDecorator currentDecorator = getLabelDecorator();
        String oldText = settings.getText();
        Object element = elementPath.getLastSegment();
        boolean decorationReady = true;
        if (currentDecorator instanceof LabelDecorator) {
            LabelDecorator labelDecorator = (LabelDecorator)currentDecorator;
            if (!labelDecorator.prepareDecoration(element, oldText,
                    getDecorationContext())) {
                // The decoration is not ready but has been queued for
                // processing
                decorationReady = false;
            }
        } else if (currentDecorator instanceof IDelayedLabelDecorator) {
            IDelayedLabelDecorator delayedDecorator = 
                (IDelayedLabelDecorator)currentDecorator;
            if (!delayedDecorator.prepareDecoration(element, oldText)) {
                // The decoration is not ready but has been queued for
                // processing
                decorationReady = false;
            }
        }
        // settings.setHasPendingDecorations(!decorationReady);
        // update icon and label

        if (m_provider instanceof ITreePathLabelProvider) {
            ITreePathLabelProvider pprov = (ITreePathLabelProvider)m_provider;
            if (decorationReady || oldText == null
                    || settings.getText().length() == 0) {
                pprov.updateLabel(settings, elementPath);
                decorateSettings(settings, elementPath);
            }
        } else {
            if (decorationReady || oldText == null
                    || settings.getText().length() == 0) {
                settings.setText(getText(element));
            }

            Image oldImage = settings.getImage();
            if (decorationReady || oldImage == null) {
                settings.setImage(getImage(element));
            }

            if (decorationReady) {
                updateForDecorationReady(settings, element);
            }
        }

    }

    /**
     * Decorate the settings
     * 
     * @param settings
     *            the settings obtained from the label provider
     * @param elementPath
     *            the element path being decorated
     */
    private void decorateSettings(ViewerLabel settings, TreePath elementPath) {
        Object element = elementPath.getLastSegment();
        if (m_decorator != null) {
            if (m_decorator instanceof LabelDecorator) {
                LabelDecorator labelDecorator = (LabelDecorator)m_decorator;
                String text = labelDecorator.decorateText(settings.getText(),
                        element, getDecorationContext());
                if (text != null && text.length() > 0) {
                    settings.setText(text);
                }
                Image image = labelDecorator.decorateImage(settings.getImage(),
                        element, getDecorationContext());
                if (image != null) {
                    settings.setImage(image);
                }

            } else {
                String text = m_decorator.decorateText(settings.getText(),
                        element);
                if (text != null && text.length() > 0) {
                    settings.setText(text);
                }
                Image image = m_decorator.decorateImage(settings.getImage(),
                        element);
                if (image != null) {
                    settings.setImage(image);
                }
            }
            if (m_decorator instanceof IColorDecorator) {
                IColorDecorator colorDecorator = (IColorDecorator)m_decorator;
                Color background = colorDecorator.decorateBackground(element);
                if (background != null) {
                    settings.setBackground(background);
                }
                Color foreground = colorDecorator.decorateForeground(element);
                if (foreground != null) {
                    settings.setForeground(foreground);
                }
            }

            if (m_decorator instanceof IFontDecorator) {
                Font font = ((IFontDecorator)m_decorator).decorateFont(element);
                if (font != null) {
                    settings.setFont(font);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void update(ViewerCell cell) {

        ViewerLabel label = new ViewerLabel(cell.getText(), cell.getImage());

        // Set up the initial settings from the label provider
        label.setBackground(getBackground(cell.getElement()));
        label.setForeground(getForeground(cell.getElement()));
        label.setFont(getFont(cell.getElement()));

        updateLabel(label, cell.getElement());

        cell.setBackground(label.getBackground());
        cell.setForeground(label.getForeground());
        cell.setFont(label.getFont());

        if (label.hasNewText()) {
            cell.setText(label.getText());
        }

        if (label.hasNewImage()) {
            cell.setImage(label.getImage());
        }
    }
}

