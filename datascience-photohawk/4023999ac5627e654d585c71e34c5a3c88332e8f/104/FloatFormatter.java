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
package at.ac.tuwien.photohawk.taverna.model.model.util;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class FloatFormatter implements Serializable {

    private static final long serialVersionUID = 8876630779501817308L;

    private DecimalFormat dfPrec = new DecimalFormat("##############0.0##############", new DecimalFormatSymbols(
        Locale.US));
    private DecimalFormat df = new DecimalFormat(" ########.##;-########.##");
    private DecimalFormat dfScientific = new DecimalFormat(" 0.########E00;-0.########E00");

    /**
     * formats a float value in decimal notation (non scientific) if value is
     * NaN or infinite, Double.toString is used
     * 
     * @param value
     * @return
     */
    public String formatFloatPrecisly(double value) {
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            return Double.toString(value);
        }
        if (Math.abs(value) > 1000000000000000.0) {
            // ok, this is too much,
            return dfScientific.format(value);
        }
        return dfPrec.format(value);
    }

    /**
     * formats a floating point number if the number has a power > 10,
     * scientific notation is used
     * 
     * @param value
     * @return
     */
    public String formatFloat(double value) {
        double absValue = Math.abs(value);
        if ((absValue >= 10000000000.) || (absValue < 0.01)) {
            return dfScientific.format(value);
        } else {
            return df.format(value);
        }
    }

}
