/*******************************************************************************
 * Copyright 2013 Vienna University of Technology
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package at.ac.tuwien.photohawk.taverna.model.model.measurement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.photohawk.taverna.model.model.ChangeLog;
import at.ac.tuwien.photohawk.taverna.model.model.scales.Scale;
import at.ac.tuwien.photohawk.taverna.model.model.tree.CriterionCategory;
import at.ac.tuwien.photohawk.taverna.model.model.values.INumericValue;

/**
 * denotes a property that can be automatically measured. A property has a name
 * and a {@link Scale}
 * 
 * @author Christoph Becker
 * 
 */
public class MeasurableProperty implements Comparable<MeasurableProperty>, Cloneable, Serializable {
    private static final long serialVersionUID = -6675251424999307492L;

    private String propertyId;
    private String name;

    /**
     * Hibernate note: standard length for a string column is 255 validation is
     * broken because we use facelet templates (issue resolved in Seam 2.0)
     * therefore allow "long" entries
     */
    private String description;

    private CriterionCategory category;

    private Scale scale;

    /**
     * a list of all metrics that can be applied to this property TODO do we
     * want to store these in the database? (redundancy!)
     */
    List<Metric> possibleMetrics = new ArrayList<Metric>();

    private ChangeLog changeLog = new ChangeLog();

    public MeasurableProperty() {
    }

    public MeasurableProperty(Scale scale, String name) {
        this.scale = scale;
        this.name = name;
    }

    public void clear() {
        propertyId = null;
        name = null;
        description = null;
        category = null;
        scale = null;
        possibleMetrics = null;
    }

    public boolean isNumeric() {
        if (scale == null) {
            return false;
        }
        return (scale.createValue() instanceof INumericValue);
    }

    // /**
    // * returns true if o is a MeasurableProperty and has the same
    // * name as <code>this</code>
    // */
    // public boolean equals(Object o) {
    // if (this == o) {
    // return true;
    // }
    // if (o == null) {
    // return false;
    // }
    // if (o instanceof MeasurableProperty) {
    // MeasurableProperty p = (MeasurableProperty) o;
    // return (name != null && name.equals(p.getName()));
    // }
    // return false;
    // }
    //
    // @Override
    // public int hashCode() {
    // if (this.name == null){
    // return 0;
    // }
    // return this.name.hashCode();
    // }

    /**
     * returns a clone of self. Implemented for storing and inserting fragments.
     * Subclasses obtain a shallow copy by invoking this method, then modifying
     * the fields required to obtain a deep copy of this object. the id is not
     * copied
     */
    public MeasurableProperty clone() {
        try {
            MeasurableProperty clone = (MeasurableProperty) super.clone();
            if (scale != null) {
                clone.setScale(scale.clone());
            }

            if (possibleMetrics != null) {
                clone.possibleMetrics = new ArrayList<Metric>();
                for (Metric m : possibleMetrics) {
                    Metric cloneM = m.clone();
                    clone.possibleMetrics.add(cloneM);
                }
            } else {
                clone.possibleMetrics = null;
            }
            // created-timestamp is automatically set to now
            // clone.setChangeLog(new
            // ChangeLog(this.getChangeLog().getChangedBy()));
            return clone;
        } catch (CloneNotSupportedException e) {
            // never thrown
            return null;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Scale getScale() {
        return scale;
    }

    public void setScale(Scale scale) {
        this.scale = scale;
    }

    public int compareTo(MeasurableProperty p) {
        return name.compareTo(p.getName());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public List<Metric> getPossibleMetrics() {
        return possibleMetrics;
    }

    public void setPossibleMetrics(List<Metric> possibleMetrics) {
        this.possibleMetrics = possibleMetrics;
    }

    public ChangeLog getChangeLog() {
        return changeLog;
    }

    public boolean isChanged() {
        return changeLog.isAltered();
    }

    public void touch() {
        changeLog.touch();
    }

    public void setChangeLog(ChangeLog changeLog) {
        this.changeLog = changeLog;
    }

    public CriterionCategory getCategory() {
        return category;
    }

    public void setCategory(CriterionCategory category) {
        this.category = category;
    }

    /**
     * currently used by digester usage: setCategoryAsString("outcome:object")
     * 
     * @param category
     */
    public void setCategoryAsString(String category) {
        if ((category == null) || ("".equals(category))) {
            setCategory(null);
        } else {
            String cat[] = category.split(":");
            if (cat.length != 2) {
                throw new IllegalArgumentException("invalid criterion category:" + category);
            }
            setCategory(CriterionCategory.getType(cat[0], cat[1]));

        }
    }
}
