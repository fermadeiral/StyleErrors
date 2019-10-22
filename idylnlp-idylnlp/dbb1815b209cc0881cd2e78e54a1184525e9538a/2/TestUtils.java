/*******************************************************************************
 * Copyright 2019 Mountain Fog, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package ai.idylnlp.testing;

import java.io.File;

public class TestUtils {

  /**
   * Gets the absolute path to the test resources.
   * @return The absolute path to the <code>src/test/resources</code> directory.
   */
  public static String getTestResourcesAbsolutePath() {

    return new File("src/test/resources/").getAbsolutePath();

  }

  /**
   * Returns <code>true</code> if running on Windows.
   * @return <code>True</code> if running on Windows.
   */
  public static boolean isWindows() {

    return System.getProperty("os.name").toLowerCase().startsWith("win");

  }

  /**
   * Returns a value based on the operating system.
   * @param linuxValue The value to return when on Linux.
   * @param windowsValue The value to return when on Windows.
   * @return The <code>linuxValue</code> when on Linux and the <code>windowsValue</code>
   * when on Windows.
   */
  public static String setOsDependentValue(String linuxValue, String windowsValue) {

    if(isWindows()) {

      return windowsValue;

    } else {

      return linuxValue;

    }

  }

}
