package org.neo4j.ogm.persistence.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.ogm.autoindex.AutoIndexManager;
import org.neo4j.ogm.domain.forum.Login;
import org.neo4j.ogm.domain.forum.Member;
import org.neo4j.ogm.domain.simple.User;
import org.neo4j.ogm.request.RowModelRequest;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.testutil.MultiDriverTestClass;
import org.neo4j.ogm.transaction.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Frantisek Hartman
 */
public class DefaultTransactionTest extends MultiDriverTestClass {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTransactionTest.class);

    private Session session;

    @BeforeClass
    public static void oneTimeSetUp() {
        driver = spy(driver);
        sessionFactory = new SessionFactory(driver, "org.neo4j.ogm.domain.simple");

    }

    @Before
    public void init() {

        session = sessionFactory.openSession();
        session.purgeDatabase();

        getGraphDatabaseService().execute("CREATE CONSTRAINT ON (u:User) ASSERT u.name IS UNIQUE");
    }

    @After
    public void tearDown() throws Exception {
        getGraphDatabaseService().execute("DROP CONSTRAINT ON (u:User) ASSERT u.name IS UNIQUE");
    }

    @Test
    public void shouldRollbackTransactionOnException() {

        User user1 = new User("frantisek");
        session.save(user1);

        try {
            User user2 = new User("frantisek");
            session.save(user2);

        } catch (Exception e) {
            logger.info("Fail", e);
            // do nothing
        }

        assertThat(session.getTransaction()).isNull();
    }
}
