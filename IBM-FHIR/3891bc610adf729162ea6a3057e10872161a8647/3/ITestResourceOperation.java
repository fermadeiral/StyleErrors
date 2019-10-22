/*
 * (C) Copyright IBM Corp. 2019
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.ibm.fhir.persistence.jdbc.test.spec;

import com.ibm.fhir.persistence.exception.FHIRPersistenceException;

/**
 * A test operation on a Resource
 * @author rarnold
 *
 */
@FunctionalInterface
public interface ITestResourceOperation {

	/**
	 * Process the resource and update the context as needed
	 * @param r
	 * @return
	 */
	public void process(TestContext context) throws FHIRPersistenceException;
}
