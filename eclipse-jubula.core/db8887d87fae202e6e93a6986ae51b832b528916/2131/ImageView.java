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
package org.eclipse.jubula.client.ui.views.imageview;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.JobUtils;
import org.eclipse.jubula.client.ui.views.ContextBasedView;
import org.eclipse.jubula.client.ui.views.IJBPart;
import org.eclipse.jubula.client.ui.views.NonSortedPropertySheetPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.views.properties.IPropertySheetPage;


/**
 * @author BREDEX GmbH
 * @created Apr 14, 2010
 */
public class ImageView extends ContextBasedView 
    implements IJBPart, ISelectionProvider {
    /**
     * <code>image</code>
     */
    private Label m_imgWidget;
    
    /**
     * the image
     */
    private ImageViewData m_imgData;
   
    /**
     * <code>m_oldSelection</code>
     */
    private ISelection m_currSelection = null;
    
    /**
     * the scrolled composite
     */
    private ScrolledComposite m_scrollComposite;
    
    /**
     * the child
     */
    private Composite m_child;
    
    /**
     * constructor which sets the contextID
     */
    public ImageView() {
        super(Constants.IMAGEVIEW_DISPLAYS_IMAGE);
    }
    
    /**
     * @param selection
     *            the selection
     */
    protected void handleSelection(ISelection selection) {
        ImageProvider provider = null;
        if (selection != null && selection instanceof IStructuredSelection) {
            IStructuredSelection ss = (IStructuredSelection)selection;
            if (ss.size() > 0) {
                Object object = ss.getFirstElement();

                if (m_currSelection != null
                        && ObjectUtils.equals(object,
                                ((IStructuredSelection)m_currSelection)
                                        .getFirstElement())) {
                    return;
                }
                m_currSelection = ss;
                // First, if the object is adaptable, ask it to get an adapter.
                if (object instanceof IAdaptable) {
                    provider = ((IAdaptable)object).getAdapter(
                            ImageProvider.class);
                }

                // If we haven't found an adapter yet, try asking the
                // AdapterManager.
                if (provider == null) {
                    provider = Platform.getAdapterManager().getAdapter(
                            object, ImageProvider.class);
                }
            }
        }
        
        if (provider != null) {
            clearImage();
            handleSelection(provider);
        }
    }

    /**
     * @param provider
     *            the provider
     */
    private void handleSelection(final ImageProvider provider) {
        final String jobName = Messages.UIJobLoadingImage;
        Job job = new Job(jobName) {
            public IStatus run(IProgressMonitor monitor) {
                monitor.beginTask(jobName, IProgressMonitor.UNKNOWN);
                setImage(provider);
                monitor.done();
                return Status.OK_STATUS;
            }
        };
        JobUtils.executeJob(job, null);
    }

    /**
     * {@inheritDoc}
     */
    public void createPartControl(Composite parent) {
        parent.setLayout(new FillLayout());
        m_scrollComposite = new ScrolledComposite(parent,
                SWT.V_SCROLL | SWT.H_SCROLL);

        m_child = new Composite(m_scrollComposite, SWT.NONE);
        m_child.setLayout(new FillLayout());
        
        m_imgWidget = new Label(m_child, SWT.NONE);
        // Allow data to be copied or moved from the drag source
        int operations = DND.DROP_COPY;
        DragSource source = new DragSource(m_imgWidget, operations);

        // Create menu manager
        MenuManager contextManager = new MenuManager();
        contextManager.setRemoveAllWhenShown(true);
        // Create context menu
        Menu contextMenu = contextManager.createContextMenu(m_imgWidget);
        m_imgWidget.setMenu(contextMenu);
        // Register context menu
        getSite().registerContextMenu(contextManager, this);

        // Provide data in Text format
        Transfer[] types = new Transfer[] { ImageTransfer.getInstance() };
        source.setTransfer(types);

        source.addDragListener(new DragSourceListener() {
            public void dragStart(DragSourceEvent event) {
                // Only start the drag if there is actually text in the
                // label - this text will be what is dropped on the target.
                if (m_imgWidget.getImage() == null) {
                    event.doit = false;
                }
            }

            public void dragSetData(DragSourceEvent event) {
                // Provide the data of the requested type.
                if (ImageTransfer.getInstance().
                        isSupportedType(event.dataType)) {
                    event.data = m_imgWidget.getImage().getImageData();
                }
            }

            public void dragFinished(DragSourceEvent event) {
                // nothing needed here
            }

        });
        
        m_scrollComposite.setExpandHorizontal(true);
        m_scrollComposite.setExpandVertical(true);
        m_scrollComposite.setMinSize(m_child.computeSize(
                SWT.DEFAULT, SWT.DEFAULT));
        m_scrollComposite.setContent(m_child);
        
        super.createPartControl(parent);
        handleSelection(getSelectionService().getSelection());
        getSite().setSelectionProvider(this);
    }

    /**
     * @param provider the provider
     */
    protected void setImage(final ImageProvider provider) {
        final Display display = m_scrollComposite.getDisplay();
        display.syncExec(new Runnable() {
            public void run() {
                clearImage();
                m_imgData = provider.getImageViewData(display);
                if (m_imgData != null) {
                    Image img = m_imgData.getImage();
                    m_imgWidget.setImage(img);
                    if (img != null) {
                        Rectangle bounds = img.getBounds();
                        m_imgWidget.setSize(bounds.width, bounds.height);
                        setStatusOfContext(true);
                    }
                    m_scrollComposite.setMinSize(m_child.computeSize(
                            SWT.DEFAULT, SWT.DEFAULT));
                }
            }
        });
    }
    
    /**
     * make image invisible and dispose it
     */
    protected void clearImage() {
        Image oldImage = m_imgWidget.getImage();
        m_imgWidget.setImage(null);
        if (oldImage != null) {
            oldImage.dispose();
        }
        
        setStatusOfContext(false);
    }
    
    /**
     * {@inheritDoc}
     */
    public void dispose() {
        if (m_imgData != null) {
            m_imgData.dispose();
        }
        super.dispose();
    }

    /**
     * {@inheritDoc}
     */
    public void setFocus() {
        m_imgWidget.setFocus();
    }
    
    /**
     * {@inheritDoc}
     */
    public void addSelectionChangedListener(
        ISelectionChangedListener listener) {
        // empty
    }

    /**
     * {@inheritDoc}
     */
    public ISelection getSelection() {
        return m_currSelection;
    }

    /**
     * {@inheritDoc}
     */
    public void removeSelectionChangedListener(
            ISelectionChangedListener listener) {
        // empty        
    }

    /**
     * {@inheritDoc}
     */
    public void setSelection(ISelection selection) {
        // empty
    }
    
    /** {@inheritDoc} */
    public Object getAdapter(Class key) {
        if (key.equals(IPropertySheetPage.class)) {
            return new NonSortedPropertySheetPage();
        }
        return super.getAdapter(key);
    }
    
    /**
     * returns the data of the displayed image
     * @return the image data
     */
    public ImageViewData getImageViewData() {
        return m_imgData;
    }
}
