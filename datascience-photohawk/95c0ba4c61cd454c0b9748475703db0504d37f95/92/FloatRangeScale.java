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
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import at.ac.tuwien.photohawk.taverna.model.model.values.FloatRangeValue;
import at.ac.tuwien.photohawk.taverna.model.model.values.Value;
import at.ac.tuwien.photohawk.taverna.model.validation.ValidationError;

/**
 * float value within a specified range, to be used in the leaves of the
 * objective tree
 * 
 * @author Michael Kraxner
 * 
 */
// We don't use the annotation NotNullField anymore, as the error message
// doesn't allow
// to specify the name of the leaf. So the error message is not very accurate.
// As we already
// have all methods available in the base class Scale we use them to check for
// restriction/unit.
// @NotNullField(fieldname="restriction",
// message="Please enter a restriction for the scale of type 'Number range'.")
public class FloatRangeScale extends RestrictedScale {

    private static final long serialVersionUID = -6413518480730261070L;

    @Override
    public String getDisplayName() {
        return "Number Range";
    }

    @Override
    public FloatRangeValue createValue() {
        FloatRangeValue v = new FloatRangeValue();
        v.setScale(this);
        return v;
    }

    /**
     * This checks the provided restriction, and iff it is valid,
     * this.restriction is set to the provided value.
     * 
     * @param restriction
     *            new restriction to set
     * @return true if restriction conforms to the {@link #pattern}, in which
     *         case the restriction is set; false otherwise, in which case the
     *         restriction is not set
     */
    public boolean validateAndSetRestriction(String restriction) {
        if (p.matcher(restriction).matches()) {
            setRestriction(restriction);
            return true;
        } else {
            return false;
        }

    }

    public static final String floatPattern = "([-+]?(\\d)+(\\.)?(\\d)*)|(([-+]?(\\.)?(\\d)+))";
    /**
     * regular expression pattern for matching input format strings
     * 
     * @see Pattern#compile(String)
     * @see #p
     */
    public static final String pattern = floatPattern + Scale.SEPARATOR + floatPattern;

    /**
     * Regexp pattern compiled from {@link #pattern}
     */
    private static Pattern p = Pattern.compile(pattern);

    private double lowerBound = -100.0;
    private double upperBound = 100.0;

    public void setLowerBound(double lower) {
        lowerBound = lower;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public void setUpperBound(double upper) {
        upperBound = upper;
    }

    public double getUpperBound() {
        return upperBound;
    }

    @Override
    public String getRestriction() {
        if ((lowerBound == Double.MIN_VALUE) && (upperBound == Double.MAX_VALUE))
            return null;
        else
            return Double.toString(lowerBound) + Scale.SEPARATOR + Double.toString(upperBound);
    }

    @Override
    public String getReadableRestriction() {
        return "between " + lowerBound + " and " + upperBound;
    }

    @Override
    public void setRestriction(String restriction) {
        if (restriction == null) {
            return;
        }
        String[] s = restriction.split(Scale.SEPARATOR);
        try {
            if (s.length == 2) {
                // keep old values if restriction is not valid
                double lower = Double.parseDouble(s[0]);
                double upper = Double.parseDouble(s[1]);
                setLowerBound(lower);
                setUpperBound(upper);
            }
        } catch (NumberFormatException e) {
            Logger.getLogger(getClass()).warn("Ignoring invalid numberformat in setRestriction:" + restriction);
        }
    }

    @Override
    protected boolean restrictionIsValid(String leafName, List<ValidationError> errors) {
        if (getRestriction() == null || "".equals(getRestriction())) {
            errors.add(new ValidationError("Please enter a restriction for the scale of type 'Number range' at leaf '"
                + leafName + "'", this));
            return false;
        }
        if (this.lowerBound >= this.upperBound) {
            errors.add(new ValidationError("The lower bound specified for leaf \"" + leafName
                + "\" is greater or equal its upper bound!", this));
            return false;
        }
        return true;
    }

    @Override
    public boolean isEvaluated(Value value) {
        boolean evaluated = false;
        if ((value != null) && (value instanceof FloatRangeValue)) {
            FloatRangeValue v = (FloatRangeValue) value;

            evaluated = value.isChanged() && (v.getValue() >= getLowerBound() && v.getValue() <= getUpperBound());
        }
        return evaluated;
    }
}
