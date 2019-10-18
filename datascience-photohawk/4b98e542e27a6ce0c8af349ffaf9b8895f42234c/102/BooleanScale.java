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

import at.ac.tuwien.photohawk.taverna.model.model.values.BooleanValue;

/**
 * Boolean value to be used in the leaves of the objective tree
 * 
 * @author Christoph Becker
 * 
 */
public class BooleanScale extends OrdinalScale {

    private static final long serialVersionUID = -65662892936008713L;

    public String getDisplayName() {
        return "Boolean";
    }

    public BooleanScale() {
        super.setRestriction("Yes/No");
        // this is a Boolean-value, the restrictions above must not be changed
        immutableRestriction = true;
    }

    /*
     * this restriction cannot be changed
     */
    @Override
    public void setRestriction(String restriction) {
    }

    /**
     * 
     */
    public BooleanValue createValue() {
        BooleanValue bv = new BooleanValue();
        bv.setScale(this);
        return bv;
    }
}
