/*
 * GeoSolutions map - Digital field mapping on Android based devices
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
package it.geosolutions.android.map.model.query;

import it.geosolutions.android.map.model.Feature;
import it.geosolutions.android.map.model.Layer;

import java.util.ArrayList;

/**
 * Model for result of a query
 * @author Lorenzo Natali (www.geo-solutions.it)
 */
public class FeatureInfoQueryResult {
	private Layer layer;
	ArrayList<Feature> features;
	public Layer<?> getLayer() {
		return layer;
	}
	public void setLayer(Layer<?> layer) {
		this.layer = layer;
	}
	public ArrayList<Feature> getFeatures() {
		return features;
	}
	public void setFeatures(ArrayList<Feature> features) {
		this.features = features;
	}
}
