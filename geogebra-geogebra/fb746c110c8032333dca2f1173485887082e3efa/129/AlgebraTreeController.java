package org.geogebra.desktop.gui.view.algebra;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.gui.view.algebra.AlgebraController;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.main.AppD;

/**
 * Controller for tree of geos
 * 
 * @author mathieu
 *
 */
public class AlgebraTreeController extends AlgebraController
		implements MouseListener, MouseMotionListener {

	/** tree */
	private AlgebraTree tree;
	private boolean skipSelection;
	private GeoElement lastSelectedGeo = null;

	/**
	 * Creator
	 * 
	 * @param kernel
	 *            kernel
	 */
	public AlgebraTreeController(Kernel kernel) {
		super(kernel);
	}

	/**
	 * set the tree controlled
	 * 
	 * @param tree
	 *            tree
	 */
	public void setTree(AlgebraTree tree) {
		this.tree = tree;
	}

	/**
	 * check double click
	 * 
	 * @param geo
	 *            geo clicked
	 * @param e
	 *            mouse event
	 * @return true if double click
	 */
	protected boolean checkDoubleClick(GeoElement geo, MouseEvent e) {

		return false;
	}

	/**
	 * 
	 * @param mode
	 *            euclidian controller mode
	 * @return true if the mode is a mode for selection
	 */
	protected boolean isSelectionModeForClick(int mode) {
		return true;
	}

	/*
	 * MouseListener implementation for popup menus
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// use mouse released instead
	}

	@Override
	public void mouseReleased(MouseEvent e) {

		// process click if no dragging
		if (draggingOccured) {
			return;
		}

		// right click is consumed in mousePressed, but in GeoGebra 3D,
		// where heavyweight popup menus are enabled this doesn't work
		// so make sure that this is no right click as well (ticket #302)
		if (e.isConsumed() || AppD.isRightClick(e)) {
			return;
		}

		// get GeoElement at mouse location
		TreePath tp = tree.getPathForLocation(e.getX(), e.getY());
		GeoElement geo = AlgebraTree.getGeoElementForPath(tp);

		ArrayList<GeoElement> groupedGeos = null;

		// check if we clicked on the scaled show/hide icon
		if (geo != null) {
			int h = tree.getIconShownHeight();
			Rectangle rect = tree.getPathBounds(tp);
			boolean iconClicked = rect != null && e.getX() - rect.x < h; // distance
																			// from
																			// left
																			// border
			if (iconClicked) {
				// icon clicked: toggle show/hide
				geo.setEuclidianVisible(!geo.isSetEuclidianVisible());
				geo.updateVisualStyle(GProperty.VISIBLE);
				app.storeUndoInfo();
				kernel.notifyRepaint();
				return;
			}

		} else { // try group action
			groupedGeos = groupAction(e, tp, false);

		}

		// check double click
		if (checkDoubleClick(geo, e)) {
			return;
		}

		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		int mode = ev.getMode();
		if (!skipSelection && isSelectionModeForClick(mode)) {
			// update selection
			if (geo == null) {
				if (!AppD.isControlDown(e) && !e.isShiftDown()) {
					selection.clearSelectedGeos();
				}

				if (groupedGeos != null) {
					selection.addSelectedGeos(groupedGeos, true);
				}

			} else {
				// handle selecting geo
				if (AppD.isControlDown(e)) {
					selection.toggleSelectedGeo(geo);
					if (selection.getSelectedGeos().contains(geo)) {
						lastSelectedGeo = geo;
					}
				} else if (e.isShiftDown() && lastSelectedGeo != null) {
					ArrayList<GeoElement> geos = tree
							.getGeosBetween(lastSelectedGeo, geo);
					if (geos != null) {
						selection.clearSelectedGeos(false); // repaint will be
															// done next step
						selection.addSelectedGeos(geos, true);
					}

				} else {
					selection.clearSelectedGeos(false); // repaint will be done
														// next step
					selection.addSelectedGeo(geo);
					lastSelectedGeo = geo;
				}
			}
		} else if (mode != EuclidianConstants.MODE_SELECTION_LISTENER) {
			euclidianViewClick(ev, geo, e);
		} else {
			// tell selection listener about click
			app.geoElementSelected(geo, false);
		}

		// Alt click: copy definition to input field
		if (geo != null && e.isAltDown() && app.showAlgebraInput()) {
			// F3 key: copy definition to input bar
			app.getGlobalKeyDispatcher().handleFunctionKeyForAlgebraInput(3,
					geo);
		}

		ev.mouseMovedOver(null);
	}

	/**
	 * let euclidianView know about the click
	 * 
	 * @param ev
	 *            euclidian view
	 * @param geo
	 *            geo clicked
	 * @param e
	 *            mouse event
	 */
	protected void euclidianViewClick(EuclidianViewInterfaceCommon ev,
			GeoElement geo, MouseEvent e) {
		setSelectedGeo(geo);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		leftPress(e);
		setMousePressed();
	}

	private long lastMousePressedTime;

	/**
	 * set values (dragging, mouse pressed time)
	 */
	protected void setMousePressed() {

		draggingOccured = false;
		lastMousePressedTime = System.currentTimeMillis();
	}

	/**
	 * right mouse pressed
	 * 
	 * @param e
	 *            event
	 * @param mouseCoords
	 *            mouse coords
	 */
	final protected void rightPress(MouseEvent e, GPoint mouseCoords) {
		e.consume();

		// get GeoElement at mouse location
		TreePath tp = tree.getPathForLocation(e.getX(), e.getY());
		GeoElement geo = AlgebraTree.getGeoElementForPath(tp);

		// single selection: popup menu
		// if (app.selectedGeosSize() < 2) {
		if (geo == null) {

			ArrayList<GeoElement> childs = AlgebraTree.getGeoChildsForPath(tp);
			if (childs == null || childs.size() == 0) {// if click on e.g.
														// object type (like
														// "Point"), then select
														// all and popup menu
				selection.clearSelectedGeos();
				AlgebraContextMenuD contextMenu = new AlgebraContextMenuD(
						(AppD) app);
				contextMenu.show(tree, e.getPoint().x, e.getPoint().y);
			} else {// popup algebra menu
				selection.clearSelectedGeos(false);
				selection.addSelectedGeos(childs, true);
				((GuiManagerD) app.getGuiManager()).showPopupMenu(childs, tree,
						mouseCoords);
			}

		} else {
			if (selection.containsSelectedGeo(geo)) {// popup menu for current
														// selection (including
														// selected object)
				((GuiManagerD) app.getGuiManager()).showPopupMenu(
						selection.getSelectedGeos(), tree, mouseCoords);
			} else {// select only this objet and popup menu
				selection.clearSelectedGeos(false);
				selection.addSelectedGeo(geo, true, true);
				ArrayList<GeoElement> temp = new ArrayList<>();
				temp.add(geo);
				((GuiManagerD) app.getGuiManager()).showPopupMenu(temp, tree,
						mouseCoords);
			}
		}
		/*
		 * } // multiple selection: popup menu (several geos) else { if(geo !=
		 * null) {
		 * ((GuiManagerD)app.getGuiManager()).showPopupMenu(app.getSelectedGeos(
		 * ), tree, mouseCoords); } }
		 */

	}

	/**
	 * left press
	 * 
	 * @param e
	 *            event
	 */
	final protected void leftPress(MouseEvent e) {

		// When a single, new selection is made with no key modifiers
		// we need to handle selection in mousePressed, not mouseClicked.
		// By doing this selection early, a DnD drag will come afterwards
		// and grab the new selection.
		// All other selection types must be handled later in mouseClicked.
		// In this case a DnD drag starts first and grabs the previously
		// selected
		// geos (e.g. cntrl-selected or EV selected) as the user expects.

		skipSelection = false; // flag to prevent duplicate selection in
								// MouseClicked

		TreePath tp = tree.getPathForLocation(e.getX(), e.getY());
		GeoElement geo = AlgebraTree.getGeoElementForPath(tp);

		if (leftPressCanSelectGeo(e, geo)) {
			ArrayList<GeoElement> groupedGeos = groupAction(e, tp, true);
			if (groupedGeos != null
					&& !selection.containsSelectedGeos(groupedGeos)) {
				selection.clearSelectedGeos(false); // repaint will be done next
													// step
				selection.addSelectedGeos(groupedGeos, true);
				skipSelection = true;
			}
		}
	}

	/**
	 * 
	 * @param e
	 *            mouse event
	 * @param geo
	 *            geo
	 * @return true if left press can select the geo
	 */
	protected boolean leftPressCanSelectGeo(MouseEvent e, GeoElement geo) {

		if (!AppD.isControlDown(e) && !e.isShiftDown()) {
			if (!setSelectedGeo(geo)) {
				return true;
			}
		}

		return false;

	}

	/**
	 * set the geo selected
	 * 
	 * @param geo
	 *            geo
	 * @return true if geo is not null and wasn't yet selected
	 */
	protected boolean setSelectedGeo(GeoElement geo) {
		if (geo != null && !selection.containsSelectedGeo(geo)) {
			selection.clearSelectedGeos(false); // repaint will be done next
												// step
			selection.addSelectedGeo(geo);
			lastSelectedGeo = geo;
			skipSelection = true;

			return true;
		}

		return false;
	}

	private ArrayList<GeoElement> groupAction(MouseEvent e, TreePath tp,
			boolean mousePressed) {

		Rectangle rect = tree.getPathBounds(tp);
		if (rect != null) { // group action
			if (e.getX() - rect.x < tree.getOpenIconHeight()) { // collapse/expand
																// icon
				if (mousePressed) {
					if (tree.isCollapsed(tp)) {
						tree.expandPath(tp);
					} else {
						tree.collapsePath(tp);
					}
				}
			} else { // collect geos of the group
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp
						.getLastPathComponent();
				ArrayList<GeoElement> groupedGeos = new ArrayList<>();
				for (int i = 0; i < node.getChildCount(); i++) {
					groupedGeos.add((GeoElement) ((DefaultMutableTreeNode) node
							.getChildAt(i)).getUserObject());
				}
				return groupedGeos;
			}
		}

		return null;

	}

	@Override
	public void mouseEntered(MouseEvent p1) {
		//
	}

	@Override
	public void mouseExited(MouseEvent p1) {
		highlight(app.getActiveEuclidianView(), (GeoElement) null);
	}

	private boolean draggingOccured = false;

	// MOUSE MOTION LISTENER
	@Override
	public void mouseDragged(MouseEvent arg0) {
		// used for interactive boards
		if (System.currentTimeMillis() > EuclidianConstants.DRAGGING_DELAY
				+ lastMousePressedTime) {
			draggingOccured = true;
		}
	}

	/**
	 * 
	 * @return true if view is editing
	 */
	protected boolean viewIsEditing() {
		return false;
	}

	// tell EuclidianView
	@Override
	public void mouseMoved(MouseEvent e) {
		if (viewIsEditing()) {
			return;
		}

		int x = e.getX();
		int y = e.getY();

		GeoElement geo = AlgebraTree.getGeoElementForLocation(tree, x, y);

		// tell EuclidianView to handle mouse over
		// EuclidianView ev = app.getEuclidianView();
		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		// ev.mouseMovedOver(geo,true);

		highlight(ev, geo);

		if (geo != null) {
			app.getLocalization().setTooltipFlag();
			tree.setToolTipText(geo.getLongDescriptionHTML(true, true));
			app.getLocalization().clearTooltipFlag();
		} else {
			tree.setToolTipText(null);
			TreePath tp = tree.getPathForLocation(e.getX(), e.getY());
			if (!tree.isCollapsed(tp)) {
				Rectangle rect = tree.getPathBounds(tp);
				if (rect != null) { // mouse over group
					if (e.getX() - rect.x > 16) { // collect geos of the group
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp
								.getLastPathComponent();
						ArrayList<GeoElement> groupedGeos = new ArrayList<>();
						for (int i = 0; i < node.getChildCount(); i++) {
							groupedGeos
									.add((GeoElement) ((DefaultMutableTreeNode) node
											.getChildAt(i)).getUserObject());
						}
						highlight(ev, groupedGeos);
					}
				}
			}

		}
	}

	/**
	 * highlight this geo using euclidian view
	 * 
	 * @param ev
	 *            euclidian view
	 * @param geo
	 *            geo
	 */
	protected void highlight(EuclidianViewInterfaceCommon ev, GeoElement geo) {
		ev.highlight(geo);
	}

	/**
	 * highlight these geos using euclidian view
	 * 
	 * @param ev
	 *            euclidian view
	 * @param geos
	 *            geos
	 */
	protected void highlight(EuclidianViewInterfaceCommon ev,
			ArrayList<GeoElement> geos) {
		ev.highlight(geos);
	}

}
