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

package org.neo4j.ogm.domain.entityMapping;

import org.neo4j.ogm.annotation.Relationship;

/**
 * One outgoing and one incoming relationship of the same type. Incoming field and methods annotated. Outgoing field annotated (implied outgoing), methods not annotated.
 *
 * @author Luanne Misquitta
 */
public class UserV9 extends Entity {

    @Relationship(type = "LIKES")
    private UserV9 likes;

    @Relationship(type = "LIKES", direction = "INCOMING")
    private UserV9 likedBy;

    public UserV9() {
    }

    public UserV9 getLikes() {
        return likes;
    }

    public void setLikes(UserV9 likes) {
        this.likes = likes;
    }

    public UserV9 getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(UserV9 likedBy) {
        this.likedBy = likedBy;
    }
}
