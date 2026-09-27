package standardNaast.service;

import com.standardnaast.persistence.EntityManagerFactoryHelper;
import org.junit.Assert;
import org.junit.Test;
import standardNaast.entities.Personne;
import standardNaast.utils.DbUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class DatabaseStartupTest {

    @Test
    public void testDatabaseStartupAndLiquibase() {
        EntityManagerFactory factory = EntityManagerFactoryHelper.getFactory();
        Assert.assertNotNull("EntityManagerFactory should not be null", factory);
        Assert.assertTrue("EntityManagerFactory should be open", factory.isOpen());

        DbUtils.runLiquibase();

        EntityManager em = factory.createEntityManager();
        try {
            Assert.assertNotNull("EntityManager should be created", em);
            Object result = em.createNativeQuery("SELECT count(*) FROM PERSONNES").getSingleResult();
            System.out.println("Found " + result + " personnes in database.");
            Assert.assertNotNull("Query should execute successfully", result);
        } finally {
            em.close();
        }
    }
}
