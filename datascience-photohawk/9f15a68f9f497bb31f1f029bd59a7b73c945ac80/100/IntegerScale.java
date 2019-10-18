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

import at.ac.tuwien.photohawk.taverna.model.model.values.IntegerValue;
import at.ac.tuwien.photohawk.taverna.model.model.values.Value;
import at.ac.tuwien.photohawk.taverna.model.validation.ValidationError;

/**
 * An integer value to be used in the leaves of the objective tree
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
// @NotNullField(fieldname="unit",
// message="Please enter a unit for the scale of type 'Integer'")
public class IntegerScale extends Scale {

    private static final long serialVersionUID = 8594390834250087870L;

    /**
     * @see at.ac.tuwien.photohawk.taverna.model.model.scales.Scale#createValue()
     */
    @Override
    public Value createValue() {
        Value v = new IntegerValue();
        v.setScale(this);
        return v;
    }

    /**
     * @see at.ac.tuwien.photohawk.taverna.model.model.scales.Scale#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        return "Integer";
    }

    /**
     * @see at.ac.tuwien.photohawk.taverna.model.model.scales.Scale#getType()
     */
    @Override
    public ScaleType getType() {
        return ScaleType.value;
    }

    @Override
    public boolean isInteger() {
        return true;
    }

    /**
     * @see at.ac.tuwien.photohawk.taverna.model.model.scales.Scale#isCorrectlySpecified(java.lang.String,
     *      List)
     */
    @Override
    public boolean isCorrectlySpecified(String leafName, List<ValidationError> errors) {

        if (getUnit() == null || "".equals(getUnit())) {
            errors.add(new ValidationError("Please enter a unit for the scale of type 'Integer' at leaf '" + leafName
                + "'", this));
            return false;
        }

        return true;
    }

    /**
     * @see at.ac.tuwien.photohawk.taverna.model.model.scales.Scale#isEvaluated(at.ac.tuwien.photohawk.taverna.model.model.values.Value)
     */
    @Override
    public boolean isEvaluated(Value value) {
        boolean evaluated = false;
        if ((value != null) && (value instanceof IntegerValue)) {
            IntegerValue v = (IntegerValue) value;

            evaluated = v.isChanged();
        }
        return evaluated;
    }

    /**
     * An {@link IntegerScale} is not restricted.
     * 
     * @see at.ac.tuwien.photohawk.taverna.model.model.scales.Scale#isRestricted()
     */
    @Override
    public boolean isRestricted() {
        return false;
    }

}
