/*
 * Copyright (c) 2002-2018 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 *
 * This product may include a number of subcomponents with
 * separate copyright notices and license terms. Your use of the source
 * code for these subcomponents is subject to the terms and
 *  conditions of the subcomponent's license, as noted in the LICENSE file.
 */

package org.neo4j.ogm.request;

import java.util.Map;

/**
 * @author vince
 * @author Luanne Misquitta
 */
public interface Statement {

    String getStatement();

    Map<String, Object> getParameters();

    String[] getResultDataContents();

    boolean isIncludeStats();

    /**
     * If this statement's results should be checked for number of results
     *
     * @return true if check should be performed
     */
    boolean checkResultsCount();

    /**
     * Number of expected results returned by execution of this statement
     *
     * @return number of expected results
     */
    int expectedResultsCount();
}
