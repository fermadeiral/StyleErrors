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

import at.ac.tuwien.photohawk.taverna.model.validation.ValidationError;

public abstract class RestrictedScale extends Scale {
    /**
     * 
     */
    private static final long serialVersionUID = 1729926168748385807L;
    /**
     * This field is only used for the annotation, to prevent hibernate from
     * trying to persist the field. The point is we need the getter and setter.
     * Handling is done differently depending on the subclass, which have to
     * take care of persisting the restriction settings themselves
     */
    private String restriction;

    public abstract void setRestriction(String restriction);

    public abstract String getRestriction();

    /**
     * This returns a String for displaying the restriction settings in a
     * readable form
     */
    public abstract String getReadableRestriction();

    @Override
    public boolean isRestricted() {
        return true;
    }

    /**
     * There are some scales with predefined restrictions - their restrictions
     * must not be changed. This field indicates that.
     */
    protected boolean immutableRestriction = false;

    public boolean isImmutableRestriction() {
        return immutableRestriction;
    }

    @Override
    public ScaleType getType() {
        return ScaleType.restricted;
    }

    @Override
    public boolean isCorrectlySpecified(String leafName, List<ValidationError> errors) {
        return this.restrictionIsValid(leafName, errors);
    }

    /**
     * Returns true if the restriction is correctly specified.
     */
    protected abstract boolean restrictionIsValid(String leafName, List<ValidationError> errors);

}
