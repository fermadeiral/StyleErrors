/**
 * Copyright [2012] [Datasalt Systems S.L.]
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
 */
package com.datasalt.pangool.flow;

import static org.junit.Assert.*;

import org.junit.Test;

import com.datasalt.pangool.flow.LinearFlow.EXECUTION_MODE;

public class TestFlow {

	@Test
	public void test() throws Exception {
		ExampleFlow flow = new ExampleFlow();
		flow.execute("job3.output", EXECUTION_MODE.POLITE);
		assertEquals(3, flow.executedJobs.size());
		
		assertEquals("job2", flow.executedJobs.get(0));
		assertEquals("job1", flow.executedJobs.get(1));
		assertEquals("job3", flow.executedJobs.get(2));
	}
}
