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

package org.neo4j.ogm.session.request;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.neo4j.ogm.exception.OptimisticLockingException;
import org.neo4j.ogm.model.RowModel;
import org.neo4j.ogm.request.Statement;
import org.neo4j.ogm.session.Neo4jSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Frantisek Hartman
 */
public class OptimisticLockingChecker {

    private static final Logger logger = LoggerFactory.getLogger(OptimisticLockingChecker.class);

    private final Neo4jSession session;

    public OptimisticLockingChecker(Neo4jSession session) {
        this.session = session;
    }

    public void checkResultsCount(List<RowModel> models, Statement request) {
        if (request.expectedResultsCount() != models.size()) {
            Map<String, Object> parameters = request.getParameters();
            Object type = parameters.get("type");

            List<Map<String, Object>> rows = (List<Map<String, Object>>) parameters.get("rows");
            if (rows != null) {

                Set<Long> nodeIds = new HashSet<>();
                Set<Long> relIds = new HashSet<>();

                for (Map<String, Object> row : rows) {
                    if (type.equals("node")) {
                        nodeIds.add((Long) row.get("nodeId"));
                    } else if (type.equals("rel")) {
                        relIds.add((Long) row.get("relId"));
                    }
                }

                if (!models.isEmpty()) {

                    int idPosition = ArrayUtils.indexOf(models.get(0).variables(), "id");
                    for (RowModel model : models) {
                        Object id = model.getValues()[idPosition];
                        if (type.equals("node")) {
                            nodeIds.remove(id);
                        } else if (type.equals("rel")) {
                            relIds.remove(id);
                        }
                    }
                }

                for (Long nodeId : nodeIds) {
                    session.context().detachNodeEntity(nodeId);
                }

                for (Long relId : relIds) {
                    session.context().detachRelationshipEntity(relId);
                }
            } else {
                Object id = parameters.get("id");
                if (id != null && models.isEmpty()) {

                    if (type.equals("node")) {
                        session.context().detachNodeEntity((Long) id);
                    } else if (type.equals("rel")) {
                        session.context().detachRelationshipEntity((Long) id);
                    }

                }
            }


            throw new OptimisticLockingException("Optimistic locking exception");
        }
    }
}
