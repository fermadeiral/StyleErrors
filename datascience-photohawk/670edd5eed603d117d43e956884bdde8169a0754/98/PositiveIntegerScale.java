/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
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
package at.ac.tuwien.photohawk.taverna.model.model.scales;

import java.util.List;

import org.apache.log4j.Logger;

import at.ac.tuwien.photohawk.taverna.model.model.values.PositiveIntegerValue;
import at.ac.tuwien.photohawk.taverna.model.model.values.Value;
import at.ac.tuwien.photohawk.taverna.model.validation.ValidationError;

/**
 * An integer value with lower and upper bounds to be used in the leaves of the
 * objective tree
 * 
 * @author Christoph Becker
 * 
 */
// We don't use the annotation NotNullField anymore, as the error message
// doesn't allow
// to specify the name of the leaf. So the error message is not very accurate.
// As we already
// have all methods available in the base class Scale we use them to check for
// restriction/unit.
// @NotNullField(fieldname="unit",
// message="Please enter a unit for the scale of type 'Positive Integer'")
public class PositiveIntegerScale extends RestrictedScale {

    private static final long serialVersionUID = 7455117412684178182L;

    public PositiveIntegerValue createValue() {
        PositiveIntegerValue v = new PositiveIntegerValue();
        v.setScale(this);
        return v;
    }

    public String getDisplayName() {
        return "Positive Integer";
    }

    private int upperBound = Integer.MAX_VALUE;

    @Override
    public boolean isInteger() {
        return true;
    }

    @Override
    public String getRestriction() {
        if (upperBound == Integer.MAX_VALUE)
            return "";
        else
            return Integer.toString(upperBound);
    }

    @Override
    public String getReadableRestriction() {
        if (this.upperBound == Integer.MAX_VALUE) {
            return "";
        } else {
            return "up to " + this.upperBound;
        }
    }

    @Override
    public void setRestriction(String restriction) {
        if (restriction != null && !"".equals(restriction)) {
            Logger.getLogger(this.getClass()).debug("setting restriction: " + restriction);
            try {
                setUpperBound(Integer.parseInt(restriction));

            } catch (NumberFormatException e) {
                Logger.getLogger(this.getClass()).warn(
                    "ignoring invalid restriction " + "setting in PositiveFloatValue: " + restriction);
            }
        } else {
            setUpperBound(Integer.MAX_VALUE);
        }
    }

    public int getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(int upper) {
        this.upperBound = upper;
    }

    @Override
    protected boolean restrictionIsValid(String leafName, List<ValidationError> errors) {
        if (this.upperBound <= 0) {
            errors.add(new ValidationError("The upper bound specified for leaf \"" + leafName
                + "\" is not greater than zero!", this));
            return false;
        }
        return true;
    }

    @Override
    public boolean isCorrectlySpecified(String leafName, List<ValidationError> errors) {

        if (false == super.isCorrectlySpecified(leafName, errors)) {
            return false;
        }

        // we additionally check for the unit
        if (getUnit() == null || "".equals(getUnit())) {
            errors.add(new ValidationError("Please enter a unit for the scale of type 'Positive Integer' at leaf '"
                + leafName + "'", this));
            return false;
        }

        return true;
    }

    @Override
    public boolean isEvaluated(Value value) {
        boolean evaluated = false;
        if ((value != null) && (value instanceof PositiveIntegerValue)) {
            PositiveIntegerValue v = (PositiveIntegerValue) value;

            evaluated = v.isChanged() && (v.getValue() <= getUpperBound() && v.getValue() >= 0);
        }
        return evaluated;
    }
}
