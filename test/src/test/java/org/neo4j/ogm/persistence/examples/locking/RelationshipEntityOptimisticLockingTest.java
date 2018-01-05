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

package org.neo4j.ogm.persistence.examples.locking;

import static org.assertj.core.api.Assertions.*;

import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.ogm.domain.locking.FriendOf;
import org.neo4j.ogm.domain.locking.User;
import org.neo4j.ogm.exception.OptimisticLockingException;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.testutil.MultiDriverTestClass;

/**
 * @author Frantisek Hartman
 */
public class RelationshipEntityOptimisticLockingTest extends MultiDriverTestClass {

    private static SessionFactory sessionFactory;

    private Session session;

    @BeforeClass
    public static void setUpClass() {
        sessionFactory = new SessionFactory(driver, "org.neo4j.ogm.domain.locking");
    }

    @Before
    public void setUp() throws Exception {
        session = sessionFactory.openSession();
        session.purgeDatabase();
    }

    @Test
    public void whenSaveRelationshipEntityThenSetVersionToZero() {
        User michael = new User("Michael");
        User oliver = new User("Oliver");
        FriendOf friendOf = michael.addFriend(oliver);

        session.save(michael);

        assertThat(friendOf.getVersion()).isEqualTo(0L);
    }

    @Test
    public void givenRelationshipEntityWhenUpdateThenIncrementVersion() {
        User michael = new User("Michael");
        User oliver = new User("Oliver");
        FriendOf friendOf = michael.addFriend(oliver);

        session.save(michael);

        Date sinceUpdated = new Date();
        friendOf.setSince(sinceUpdated);

        session.save(michael);
        assertThat(friendOf.getVersion()).isEqualTo(1L);

        session.clear();

        FriendOf loaded = session.load(FriendOf.class, friendOf.getId());
        assertThat(loaded.getSince()).isEqualTo(sinceUpdated);
        assertThat(loaded.getVersion()).isEqualTo(1L);
    }

    @Test(expected = OptimisticLockingException.class)
    public void givenRelationshipEntityWithWrongVersionWhenSaveThenFailWithOptimisticLockingException() {
        User michael = new User("Michael");
        User oliver = new User("Oliver");
        FriendOf friendOf = michael.addFriend(oliver);

        session.save(michael);
        friendOf.setSince(new Date());
        session.save(friendOf);

        FriendOf wrongVersion = new FriendOf(michael, oliver);
        wrongVersion.setId(friendOf.getId());
        wrongVersion.setVersion(0L);
        Date updatedSince = new Date();
        wrongVersion.setSince(updatedSince);

        session.save(wrongVersion, 0);
    }

    @Test
    public void givenRelationshipEntityWhenDeleteThenDeleteRelationshipEntity() {
        User michael = new User("Michael");
        User oliver = new User("Oliver");
        FriendOf friendOf = michael.addFriend(oliver);

        session.save(michael);

        session.delete(friendOf);

        Collection<FriendOf> friendOfs = session.loadAll(FriendOf.class);
        assertThat(friendOfs).isEmpty();
    }

    @Test
    public void saveDeletedRelationshipEntityShouldFailWithOptimisticLockingException() {
        User michael = new User("Michael");
        User oliver = new User("Oliver");
        FriendOf friendOf = michael.addFriend(oliver);

        session.save(michael);

        // someone else deletes node
        session.delete(friendOf);

        friendOf.setSince(new Date());

        // save should throw exception
        assertThatThrownBy(() -> session.save(friendOf))
            .isInstanceOf(OptimisticLockingException.class);

        // save through related entity should trow exception
        assertThatThrownBy(() -> session.save(michael))
            .isInstanceOf(OptimisticLockingException.class);

        // and relationship should not exist
        Collection<FriendOf> friendOfs = session.loadAll(FriendOf.class);
        assertThat(friendOfs).isEmpty();
    }

    @Test
    public void givenRelationshipEntityWithWrongVersionWhenDeleteThenThrowOptimisticLockingException() {
        User michael = new User("Michael");
        User oliver = new User("Oliver");
        FriendOf friendOf = michael.addFriend(oliver);

        session.save(friendOf);

        friendOf.setVersion(1L);

        assertThatThrownBy(() -> session.delete(friendOf))
            .isInstanceOf(OptimisticLockingException.class);

        session.clear();
        Collection<FriendOf> friendOfs = session.loadAll(FriendOf.class);
        assertThat(friendOfs).hasSize(1);
    }

    @Test
    public void optimisticLockingExceptionShouldRollbackDefaultTransaction() {
        User michael = new User("Michael");
        User oliver = new User("Oliver");
        FriendOf friendOf = michael.addFriend(oliver);

        session.save(michael);
        friendOf.setSince(new Date());
        session.save(friendOf);

        michael.setName("Michael Updated");
        oliver.setName("Oliver Updated");
        FriendOf wrongVersion = new FriendOf(michael, oliver);
        wrongVersion.setId(friendOf.getId());
        wrongVersion.setVersion(0L);
        Date updatedSince = new Date();
        wrongVersion.setSince(updatedSince);

        try {
            session.save(wrongVersion, 0);
            fail("Expected OptimisticLockingException");
        } catch (OptimisticLockingException ex) {
            session.clear();

            Collection<User> users = session.loadAll(User.class);
            assertThat(users).extracting(User::getName).containsOnly("Michael", "Oliver");
        }
    }

}
