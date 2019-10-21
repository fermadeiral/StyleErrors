/*
 * GeoSolutions Android map Library - Digital field mapping on Android based devices
 * Copyright (C) 2014  GeoSolutions (www.geo-solutions.it)
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
package it.geosolutions.android.map.spatialite;

import android.util.Log;
import jsqlite.Exception;
import it.geosolutions.android.map.database.SpatialDataSourceManager;
import it.geosolutions.android.map.model.Layer;
import it.geosolutions.android.map.model.LayerGroup;
import it.geosolutions.android.map.style.AdvancedStyle;
import it.geosolutions.android.map.style.StyleManager;
import eu.geopaparazzi.spatialite.database.spatial.core.ISpatialDatabaseHandler;
import eu.geopaparazzi.spatialite.database.spatial.core.SpatialVectorTable;

/**
 * Abstraction of a Vector layer
 * The source is a Spatialite database
 * 
 * @author Lorenzo Natali (lorenzo.natali@geo-solutions.it)
 * @author Lorenzo Pini (lorenzo.pini@geo-solutions.it)
 */
public class SpatialiteLayer implements Layer<SpatialiteSource> {
	
	private String title;
	private String label;
	SpatialiteSource source;
	private String tableName;
	
	private double opacity;
	
	/**
	 * LayerGroup of this Layer, can be null
	 */
	protected LayerGroup layerGroup;

	public SpatialiteLayer(SpatialVectorTable t) {
		if(t != null){
			this.title = t.getName();
			this.tableName = t.getName();
		}
	}


	public AdvancedStyle  getStyle(){
		StyleManager styleManager =StyleManager.getInstance();
        return styleManager.getStyle(tableName);
	}
	
	boolean visibility =true;
	private int status;
	public SpatialiteSource getSource() {
		return source;
	}


	public void setSource(SpatialiteSource source) {
		this.source = source;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	@Override
	public String getTitle() {
		return title;
	}

	
	@Override
	public boolean isVisibility() {
		return visibility;
	}

	@Override
	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
		
	}


	public String getTableName() {
		return tableName;
	}


	public void setTableName(String tableName) {
		this.tableName = tableName;
	}


	/* (non-Javadoc)
	 * @see it.geosolutions.android.map.model.Layer#setStatus(int)
	 */
	@Override
	public void setStatus(int status) {
		this.status = status;
		
	}


	/* (non-Javadoc)
	 * @see it.geosolutions.android.map.model.Layer#getStatus()
	 */
	@Override
	public int getStatus() {
		return status;
	}
	
	/**
	 * Returns the renderer associated to this layer
	 * @return
	 */
	public ISpatialDatabaseHandler getSpatialDatabaseHandler(){
		SpatialDataSourceManager sdsm = SpatialDataSourceManager.getInstance();
		if(sdsm != null){
			try {
				if(sdsm.getVectorTableByName(tableName)!=null){
					return sdsm.getVectorHandler(sdsm.getVectorTableByName(tableName));
				}
			} catch (Exception e) {
				Log.e("SpatialiteLayer","Exception while getting SpatialDatabaseHandler");
				Log.e("SpatialiteLayer",Log.getStackTraceString(e));
			}
		}
		return null;
	}
	
	/**
	 * Set {@link LayerGroup}
	 */
	@Override
	public void setLayerGroup(LayerGroup layerGroup) {
		this.layerGroup = layerGroup;
	}


	/**
	 * Get {@link LayerGroup}
	 */
	@Override
	public LayerGroup getLayerGroup() {
		return this.layerGroup;
	}


	@Override
	public void setOpacity(double opacityValue) {

		this.opacity = opacityValue;
	}


	@Override
	public double getOpacity() {
		return this.opacity;
	}


	@Override
	public String getLabel() {
		return this.label;
	}


	@Override
	public void setLabel(String label) {
		this.label = label;
		
	}

}
