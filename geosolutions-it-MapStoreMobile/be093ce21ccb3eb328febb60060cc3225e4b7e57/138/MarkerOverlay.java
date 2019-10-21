/*
 * GeoSolutions Android Map Library - Digital field mapping on Android based devices
 * Copyright (C) 2013  GeoSolutions (www.geo-solutions.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.geosolutions.android.map.overlay;

import java.util.ArrayList;
import java.util.List;

import it.geosolutions.android.map.overlay.items.DescribedMarker;
import it.geosolutions.android.map.utils.ProjectionUtils;

import org.mapsforge.android.maps.MapViewPosition;
import org.mapsforge.android.maps.Projection;
import org.mapsforge.android.maps.overlay.ListOverlay;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.GeoPoint;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.util.MercatorProjection;

import android.graphics.Canvas;

public class MarkerOverlay extends ListOverlay {
	Projection projection;
	
	public Projection getProjection() {
		return projection;
	}

	public void setProjection(Projection projection) {
		this.projection = projection;
	}

	/**
	 * Query a pixel. Returns a marker drawn in that pixel
	 * Used to select/drag markers
	 * @param mapViewPosition
	 * @param projection
	 * @param x
	 * @param y
	 * @return
	 */
	public DescribedMarker queryPixel(MapViewPosition mapViewPosition,Projection projection,float x, float y) {
		
		
		
		for(OverlayItem i : getOverlayItems()){
			//TODO use architectural instrument to get markers
			if(i instanceof DescribedMarker){
				DescribedMarker dm = (DescribedMarker)i;
				boolean match = dm.matchPoint(mapViewPosition,projection,x,y);
				if(match) return dm;
			}
			
		}
		return null;
	}
	
	public ArrayList<DescribedMarker> getMarkers(){
	        ArrayList<DescribedMarker> res = new ArrayList<DescribedMarker>();
		for(OverlayItem i : getOverlayItems()){
			if(i instanceof DescribedMarker){
				res.add((DescribedMarker)i);
			}
		}
		return res;
	}
	public DescribedMarker getHighlighted(){
		for(OverlayItem i : getOverlayItems()){
			if(i instanceof DescribedMarker){
				return ((DescribedMarker) i).isHighlight() ? (DescribedMarker)i :null ;
			}
		}
		return null;
	}
	public void resetHighlight(){
		for(OverlayItem i : getOverlayItems()){
			if(i instanceof DescribedMarker){
			 ((DescribedMarker) i).highlightOff();			
			}
		}
	}
	
	@Override
    public synchronized void draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas) {
			double canvasPixelLeft,canvasPixelTop;
			if(projection == null){
				 canvasPixelLeft = MercatorProjection.longitudeToPixelX(boundingBox.minLongitude, zoomLevel);
		         canvasPixelTop = MercatorProjection.latitudeToPixelY(boundingBox.maxLatitude, zoomLevel);
			}else{
				double n = boundingBox.maxLatitude;
	    		double w = boundingBox.minLongitude;
	    		double s = boundingBox.minLatitude;
	    		double e = boundingBox.maxLongitude;
	    		GeoPoint dp = new GeoPoint(n, w); // ULC
	    		long[] pxDp= ProjectionUtils.getDrawPoint(dp, projection, zoomLevel);
	            canvasPixelLeft = pxDp[0];
	    		canvasPixelTop= pxDp[1];
			}
		
           
            

    		// replaces the argument
    		
            Point canvasPosition = new Point(canvasPixelLeft, canvasPixelTop);

            synchronized (this.getOverlayItems()) {
                    int numberOfOverlayItems = this.getOverlayItems().size();
                    for (int i = 0; i < numberOfOverlayItems; ++i) {
                            OverlayItem overlayItem = this.getOverlayItems().get(i);
                            overlayItem.draw(boundingBox, zoomLevel, canvas, canvasPosition);
                    }
            }
    }


	
	
	
}
