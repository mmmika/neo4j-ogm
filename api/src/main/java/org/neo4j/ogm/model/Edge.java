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

package org.neo4j.ogm.model;

import java.util.List;

/**
 * @author vince
 */
public interface Edge {

    String getType();

    Long getStartNode();

    Long getEndNode();

    Long getId();

    List<Property<String, Object>> getPropertyList();

    /**
     * Returns name of the primary id property (property annotated with @Id)
     */
    String getPrimaryIdName();

    /**
     * Returns if the relationship entity has version property
     */
    boolean hasVersionProperty();

    /**
     * Return current version of the node, null if the relationship entity is new
     *
     * @return version property with current version
     */
    Property<String, Long> getVersion();
}
